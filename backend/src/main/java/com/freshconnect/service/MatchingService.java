package com.freshconnect.service;

import com.freshconnect.model.*;
import com.freshconnect.model.FoodDonation.DonationStatus;
import com.freshconnect.model.FoodRequirement.RequirementStatus;
import com.freshconnect.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * AI-Powered Matching Engine
 * Uses a rule-based scoring algorithm to intelligently match food donations with NGO requirements.
 * Architecture is ready for future ML model integration.
 */
@Service
public class MatchingService {
    
    @Autowired
    private FoodDonationRepository donationRepository;
    
    @Autowired
    private FoodRequirementRepository requirementRepository;
    
    @Autowired
    private DonationRequestRepository requestRepository;
    
    /**
     * Calculate urgency score for a donation (0-100)
     * Higher score = more urgent (needs to be collected soon)
     */
    public double calculateUrgencyScore(FoodDonation donation) {
        double score = 0;
        LocalDateTime now = LocalDateTime.now();
        
        if (donation.getExpiryTime() == null) return 30; // Default moderate urgency
        
        // 1. Time urgency (0-40 points) - closer to expiry = higher score
        Duration timeLeft = Duration.between(now, donation.getExpiryTime());
        long hoursLeft = timeLeft.toHours();
        
        if (hoursLeft <= 0) return 0; // Already expired
        if (hoursLeft <= 2) score += 40;
        else if (hoursLeft <= 6) score += 35;
        else if (hoursLeft <= 12) score += 28;
        else if (hoursLeft <= 24) score += 20;
        else if (hoursLeft <= 48) score += 12;
        else score += 5;
        
        // 2. Quantity factor (0-25 points) - larger quantities are more urgent to distribute
        int qty = donation.getQuantity() != null ? donation.getQuantity() : 0;
        if (qty >= 200) score += 25;
        else if (qty >= 100) score += 20;
        else if (qty >= 50) score += 15;
        else if (qty >= 20) score += 10;
        else score += 5;
        
        // 3. Category urgency (0-20 points) - perishable items are more urgent
        String category = donation.getCategory() != null ? donation.getCategory().toLowerCase() : "";
        switch (category) {
            case "meals": score += 20; break;
            case "dairy": score += 18; break;
            case "fruits": score += 16; break;
            case "vegetables": score += 16; break;
            case "bakery": score += 14; break;
            case "beverages": score += 8; break;
            case "packaged": score += 5; break;
            default: score += 10;
        }
        
        // 4. Demand matching (0-15 points) - donations that match active requirements
        List<FoodRequirement> activeReqs = requirementRepository.findByStatus(RequirementStatus.ACTIVE);
        boolean hasMatch = activeReqs.stream().anyMatch(req -> {
            if (req.getFoodCategory() == null || req.getFoodCategory().equals("any")) return true;
            return req.getFoodCategory().equalsIgnoreCase(donation.getCategory());
        });
        if (hasMatch) score += 15;
        
        return Math.min(100, Math.round(score * 10.0) / 10.0);
    }
    
    /**
     * Find the best matching donations for an NGO's requirements.
     * Returns a list of match results with scores and explanations.
     */
    public List<MatchResult> findMatchesForNgo(Long ngoId) {
        List<FoodRequirement> requirements = requirementRepository.findByNgoIdAndStatus(ngoId, RequirementStatus.ACTIVE);
        List<FoodDonation> available = donationRepository.findAvailableDonations(LocalDateTime.now());
        
        List<MatchResult> matches = new ArrayList<>();
        
        for (FoodDonation donation : available) {
            double bestScore = 0;
            String bestReason = "";
            FoodRequirement bestReq = null;
            
            if (requirements.isEmpty()) {
                // No specific requirements - still show available donations with base score
                double score = calculateGeneralMatchScore(donation);
                matches.add(new MatchResult(donation, null, score, "Available donation matching general needs"));
                continue;
            }
            
            for (FoodRequirement req : requirements) {
                double score = calculateMatchScore(donation, req);
                String reason = generateMatchReason(donation, req, score);
                
                if (score > bestScore) {
                    bestScore = score;
                    bestReason = reason;
                    bestReq = req;
                }
            }
            
            if (bestScore > 20) { // Minimum threshold
                matches.add(new MatchResult(donation, bestReq, bestScore, bestReason));
            }
        }
        
        // Sort by match score descending
        matches.sort((a, b) -> Double.compare(b.getMatchScore(), a.getMatchScore()));
        return matches;
    }
    
    /**
     * Find the best matching NGOs for a specific donation.
     * Used by donors to see which NGOs would benefit most.
     */
    public List<NgoMatchResult> findMatchingNgos(Long donationId) {
        FoodDonation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new RuntimeException("Donation not found"));
        
        List<FoodRequirement> allActive = requirementRepository.findByStatus(RequirementStatus.ACTIVE);
        List<NgoMatchResult> results = new ArrayList<>();
        
        // Group requirements by NGO
        Map<Long, List<FoodRequirement>> ngoReqs = allActive.stream()
                .collect(Collectors.groupingBy(r -> r.getNgo().getId()));
        
        for (Map.Entry<Long, List<FoodRequirement>> entry : ngoReqs.entrySet()) {
            User ngo = entry.getValue().get(0).getNgo();
            double bestScore = 0;
            String bestReason = "";
            FoodRequirement bestReq = null;
            
            for (FoodRequirement req : entry.getValue()) {
                double score = calculateMatchScore(donation, req);
                if (score > bestScore) {
                    bestScore = score;
                    bestReason = generateMatchReason(donation, req, score);
                    bestReq = req;
                }
            }
            
            if (bestScore > 20) {
                results.add(new NgoMatchResult(ngo, bestReq, bestScore, bestReason));
            }
        }
        
        results.sort((a, b) -> Double.compare(b.getMatchScore(), a.getMatchScore()));
        return results;
    }
    
    /**
     * Core matching algorithm - calculates how well a donation matches a requirement.
     * Score range: 0-100
     */
    private double calculateMatchScore(FoodDonation donation, FoodRequirement requirement) {
        double score = 0;
        
        // 1. Category match (0-30 points)
        if (requirement.getFoodCategory() == null || requirement.getFoodCategory().equals("any")) {
            score += 20; // Partial score for "any" category
        } else if (requirement.getFoodCategory().equalsIgnoreCase(donation.getCategory())) {
            score += 30; // Full score for exact match
        } else {
            score += 5; // Minimal score for mismatch
        }
        
        // 2. Food type compatibility (0-15 points)
        if (requirement.getFoodType() == null || requirement.getFoodType().equals("any")) {
            score += 12;
        } else if (requirement.getFoodType().equalsIgnoreCase(donation.getFoodType())) {
            score += 15;
        } else if (donation.getFoodType() != null && donation.getFoodType().equalsIgnoreCase("vegan")) {
            score += 10; // Vegan is compatible with vegetarian
        }
        
        // 3. Quantity fit (0-25 points)
        if (donation.getQuantity() != null && requirement.getQuantity() != null) {
            double ratio = (double) donation.getQuantity() / requirement.getQuantity();
            if (ratio >= 0.8 && ratio <= 1.5) {
                score += 25; // Perfect fit
            } else if (ratio >= 0.5 && ratio <= 2.0) {
                score += 18; // Acceptable fit
            } else if (ratio >= 0.3) {
                score += 10; // Partial fit
            } else {
                score += 5;
            }
        } else {
            score += 10; // Unknown quantity
        }
        
        // 4. Time urgency bonus (0-15 points) - prioritize items expiring soon
        if (donation.getExpiryTime() != null) {
            long hoursLeft = Duration.between(LocalDateTime.now(), donation.getExpiryTime()).toHours();
            if (hoursLeft > 0 && hoursLeft <= 6) score += 15;
            else if (hoursLeft <= 12) score += 12;
            else if (hoursLeft <= 24) score += 8;
            else score += 5;
        }
        
        // 5. Freshness bonus (0-15 points)
        if (donation.getPreparationTime() != null) {
            long hoursSincePrep = Duration.between(donation.getPreparationTime(), LocalDateTime.now()).toHours();
            if (hoursSincePrep <= 2) score += 15;
            else if (hoursSincePrep <= 6) score += 12;
            else if (hoursSincePrep <= 12) score += 8;
            else score += 3;
        } else {
            score += 8;
        }
        
        return Math.min(100, Math.round(score * 10.0) / 10.0);
    }
    
    private double calculateGeneralMatchScore(FoodDonation donation) {
        double score = 40; // Base score for any available donation
        
        // Time urgency bonus
        if (donation.getExpiryTime() != null) {
            long hoursLeft = Duration.between(LocalDateTime.now(), donation.getExpiryTime()).toHours();
            if (hoursLeft > 0 && hoursLeft <= 6) score += 20;
            else if (hoursLeft <= 12) score += 15;
            else if (hoursLeft <= 24) score += 10;
        }
        
        // Quantity bonus
        if (donation.getQuantity() != null && donation.getQuantity() >= 50) score += 10;
        
        return Math.min(100, score);
    }
    
    private String generateMatchReason(FoodDonation donation, FoodRequirement requirement, double score) {
        StringBuilder reason = new StringBuilder();
        
        if (requirement.getFoodCategory() != null && 
            requirement.getFoodCategory().equalsIgnoreCase(donation.getCategory())) {
            reason.append("Category match (" + donation.getCategory() + "). ");
        }
        
        if (donation.getQuantity() != null && requirement.getQuantity() != null) {
            double ratio = (double) donation.getQuantity() / requirement.getQuantity();
            if (ratio >= 0.8 && ratio <= 1.5) {
                reason.append("Quantity fits well (" + donation.getQuantity() + "/" + requirement.getQuantity() + " " + donation.getUnit() + "). ");
            }
        }
        
        if (donation.getExpiryTime() != null) {
            long hoursLeft = Duration.between(LocalDateTime.now(), donation.getExpiryTime()).toHours();
            if (hoursLeft <= 6) {
                reason.append("Urgent - expires in " + hoursLeft + "h. ");
            }
        }
        
        if (score >= 80) reason.append("Excellent match!");
        else if (score >= 60) reason.append("Good match.");
        else if (score >= 40) reason.append("Moderate match.");
        
        return reason.toString().trim();
    }
    
    /**
     * Simple demand prediction based on historical patterns.
     * Analyzes past donation requests to estimate future needs.
     */
    public Map<String, Object> predictDemand(Long ngoId) {
        Map<String, Object> prediction = new HashMap<>();
        
        List<FoodRequirement> pastReqs = requirementRepository.findByNgoId(ngoId);
        
        if (pastReqs.size() < 2) {
            prediction.put("hasSufficientData", false);
            prediction.put("message", "Insufficient historical data for reliable predictions. At least 2 past requirements are needed.");
            return prediction;
        }
        
        // Calculate average quantity needed
        double avgQuantity = pastReqs.stream()
                .mapToInt(FoodRequirement::getQuantity)
                .average()
                .orElse(0);
        
        // Calculate average beneficiaries
        double avgBeneficiaries = pastReqs.stream()
                .mapToInt(FoodRequirement::getBeneficiaries)
                .average()
                .orElse(0);
        
        // Most requested categories
        Map<String, Long> categoryCount = pastReqs.stream()
                .filter(r -> r.getFoodCategory() != null)
                .collect(Collectors.groupingBy(FoodRequirement::getFoodCategory, Collectors.counting()));
        
        String topCategory = categoryCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("meals");
        
        prediction.put("hasSufficientData", true);
        prediction.put("estimatedQuantity", Math.round(avgQuantity));
        prediction.put("estimatedBeneficiaries", Math.round(avgBeneficiaries));
        prediction.put("topCategory", topCategory);
        prediction.put("dataPoints", pastReqs.size());
        prediction.put("message", "Based on " + pastReqs.size() + " historical requirements. Predictions improve with more data.");
        
        return prediction;
    }
    
    // DTO classes for match results
    public static class MatchResult {
        private FoodDonation donation;
        private FoodRequirement requirement;
        private double matchScore;
        private String matchReason;
        
        public MatchResult(FoodDonation donation, FoodRequirement requirement, double matchScore, String matchReason) {
            this.donation = donation;
            this.requirement = requirement;
            this.matchScore = matchScore;
            this.matchReason = matchReason;
        }
        
        public FoodDonation getDonation() { return donation; }
        public FoodRequirement getRequirement() { return requirement; }
        public double getMatchScore() { return matchScore; }
        public String getMatchReason() { return matchReason; }
    }
    
    public static class NgoMatchResult {
        private User ngo;
        private FoodRequirement requirement;
        private double matchScore;
        private String matchReason;
        
        public NgoMatchResult(User ngo, FoodRequirement requirement, double matchScore, String matchReason) {
            this.ngo = ngo;
            this.requirement = requirement;
            this.matchScore = matchScore;
            this.matchReason = matchReason;
        }
        
        public User getNgo() { return ngo; }
        public FoodRequirement getRequirement() { return requirement; }
        public double getMatchScore() { return matchScore; }
        public String getMatchReason() { return matchReason; }
    }
}
