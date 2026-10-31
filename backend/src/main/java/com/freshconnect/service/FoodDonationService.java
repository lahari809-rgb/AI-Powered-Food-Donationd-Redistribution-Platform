package com.freshconnect.service;

import com.freshconnect.model.*;
import com.freshconnect.model.FoodDonation.DonationStatus;
import com.freshconnect.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class FoodDonationService {
    
    @Autowired
    private FoodDonationRepository donationRepository;
    
    @Autowired
    private DonationHistoryRepository historyRepository;
    
    @Autowired
    private MatchingService matchingService;
    
    public FoodDonation createDonation(FoodDonation donation) {
        donation.setStatus(DonationStatus.AVAILABLE);
        donation.setUrgencyScore(matchingService.calculateUrgencyScore(donation));
        FoodDonation saved = donationRepository.save(donation);
        
        // Record history
        historyRepository.save(new DonationHistory(
            saved, null, saved.getDonor(), "CREATED",
            "New donation created: " + saved.getFoodName() + " (" + saved.getQuantity() + " " + saved.getUnit() + ")"
        ));
        
        return saved;
    }
    
    public List<FoodDonation> getAvailableDonations() {
        List<FoodDonation> donations = donationRepository.findAvailableDonations(LocalDateTime.now());
        // Recalculate urgency scores
        for (FoodDonation d : donations) {
            d.setUrgencyScore(matchingService.calculateUrgencyScore(d));
            donationRepository.save(d);
        }
        return donations;
    }
    
    public List<FoodDonation> getDonationsByDonor(Long donorId) {
        return donationRepository.findByDonorId(donorId);
    }
    
    public Optional<FoodDonation> getDonationById(Long id) {
        return donationRepository.findById(id);
    }
    
    public FoodDonation updateDonation(Long id, FoodDonation updated) {
        FoodDonation donation = donationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Donation not found"));
        
        if (updated.getFoodName() != null) donation.setFoodName(updated.getFoodName());
        if (updated.getCategory() != null) donation.setCategory(updated.getCategory());
        if (updated.getQuantity() != null) donation.setQuantity(updated.getQuantity());
        if (updated.getUnit() != null) donation.setUnit(updated.getUnit());
        if (updated.getFoodType() != null) donation.setFoodType(updated.getFoodType());
        if (updated.getExpiryTime() != null) donation.setExpiryTime(updated.getExpiryTime());
        if (updated.getPickupAddress() != null) donation.setPickupAddress(updated.getPickupAddress());
        if (updated.getInstructions() != null) donation.setInstructions(updated.getInstructions());
        
        donation.setUrgencyScore(matchingService.calculateUrgencyScore(donation));
        return donationRepository.save(donation);
    }
    
    public FoodDonation updateStatus(Long id, DonationStatus status) {
        FoodDonation donation = donationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Donation not found"));
        donation.setStatus(status);
        return donationRepository.save(donation);
    }
    
    public void cancelDonation(Long id) {
        FoodDonation donation = donationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Donation not found"));
        donation.setStatus(DonationStatus.CANCELLED);
        donationRepository.save(donation);
        
        historyRepository.save(new DonationHistory(
            donation, null, donation.getDonor(), "CANCELLED",
            "Donation cancelled: " + donation.getFoodName()
        ));
    }
    
    // Expire stale donations
    public void expireOldDonations() {
        List<FoodDonation> available = donationRepository.findByStatus(DonationStatus.AVAILABLE);
        for (FoodDonation d : available) {
            if (d.getExpiryTime() != null && d.getExpiryTime().isBefore(LocalDateTime.now())) {
                d.setStatus(DonationStatus.EXPIRED);
                donationRepository.save(d);
                historyRepository.save(new DonationHistory(
                    d, null, d.getDonor(), "EXPIRED",
                    "Donation expired: " + d.getFoodName()
                ));
            }
        }
    }
    
    public List<FoodDonation> getExpiringSoon(int hoursThreshold) {
        return donationRepository.findExpiringSoon(
            LocalDateTime.now(),
            LocalDateTime.now().plusHours(hoursThreshold)
        );
    }
    
    public long countByDonor(Long donorId) {
        return donationRepository.countByDonorId(donorId);
    }
    
    public long countByDonorAndStatus(Long donorId, DonationStatus status) {
        return donationRepository.countByDonorIdAndStatus(donorId, status);
    }
    
    public long countByStatus(DonationStatus status) {
        return donationRepository.countByStatus(status);
    }
    
    public long countAll() {
        return donationRepository.count();
    }
    
    public List<FoodDonation> findAll() {
        return donationRepository.findAll();
    }
    
    public Map<String, Long> getCategoryDistribution() {
        Map<String, Long> dist = new LinkedHashMap<>();
        List<Object[]> results = donationRepository.countByCategory();
        for (Object[] row : results) {
            dist.put((String) row[0], (Long) row[1]);
        }
        return dist;
    }
}
