package com.freshconnect.controller;

import com.freshconnect.model.FoodDonation;
import com.freshconnect.model.User;
import com.freshconnect.service.FoodDonationService;
import com.freshconnect.service.MatchingService;
import com.freshconnect.service.UserService;
import com.freshconnect.service.DonationRequestService;
import com.freshconnect.model.DonationRequest.RequestStatus;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/donations")
public class DonationController {
    
    @Autowired
    private FoodDonationService donationService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private MatchingService matchingService;
    
    @Autowired
    private DonationRequestService requestService;
    
    @PostMapping
    public ResponseEntity<?> createDonation(@RequestBody FoodDonation donation, @RequestParam(required = false) Long donorId) {
        try {
            User donor = null;
            if (donorId != null) {
                donor = userService.findById(donorId).orElse(null);
            }
            if (donor == null) {
                donor = userService.findByRole(User.UserRole.DONOR).stream().findFirst()
                        .orElseThrow(() -> new RuntimeException("No donor found in database"));
            }
            donation.setDonor(donor);
            
            // Set safe defaults for any missing fields
            if (donation.getCategory() == null || donation.getCategory().isBlank()) {
                donation.setCategory("meals");
            }
            if (donation.getFoodType() == null || donation.getFoodType().isBlank()) {
                donation.setFoodType("vegetarian");
            }
            if (donation.getUnit() == null || donation.getUnit().isBlank()) {
                donation.setUnit("packets");
            }
            if (donation.getQuantity() == null || donation.getQuantity() < 1) {
                donation.setQuantity(50);
            }
            if (donation.getPreparationTime() == null) {
                donation.setPreparationTime(java.time.LocalDateTime.now());
            }
            if (donation.getExpiryTime() == null) {
                donation.setExpiryTime(java.time.LocalDateTime.now().plusHours(6));
            }
            if (donation.getPickupAddress() == null || donation.getPickupAddress().isBlank()) {
                donation.setPickupAddress(donor.getAddress() != null ? donor.getAddress() : "Donor Address");
            }
            if (donation.getPickupCity() == null || donation.getPickupCity().isBlank()) {
                donation.setPickupCity(donor.getCity() != null ? donor.getCity() : "Bengaluru");
            }
            
            FoodDonation saved = donationService.createDonation(donation);
            return ResponseEntity.ok(Map.of("success", true, "message", "Donation created successfully", "donation", saved));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    @GetMapping("/available")
    public ResponseEntity<List<FoodDonation>> getAvailable() {
        return ResponseEntity.ok(donationService.getAvailableDonations());
    }
    
    @GetMapping("/donor/{donorId}")
    public ResponseEntity<List<FoodDonation>> getByDonor(@PathVariable Long donorId) {
        return ResponseEntity.ok(donationService.getDonationsByDonor(donorId));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return donationService.getDonationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDonation(@PathVariable Long id, @RequestBody FoodDonation updated) {
        try {
            return ResponseEntity.ok(donationService.updateDonation(id, updated));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<?> cancelDonation(@PathVariable Long id) {
        try {
            donationService.cancelDonation(id);
            return ResponseEntity.ok(Map.of("success", true, "message", "Donation cancelled"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    @GetMapping("/expiring")
    public ResponseEntity<List<FoodDonation>> getExpiringSoon(@RequestParam(defaultValue = "6") int hours) {
        return ResponseEntity.ok(donationService.getExpiringSoon(hours));
    }
    
    @GetMapping("/{id}/matching-ngos")
    public ResponseEntity<List<MatchingService.NgoMatchResult>> getMatchingNgos(@PathVariable Long id) {
        return ResponseEntity.ok(matchingService.findMatchingNgos(id));
    }
    
    @GetMapping("/donor/{donorId}/stats")
    public ResponseEntity<Map<String, Object>> getDonorStats(@PathVariable Long donorId) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalDonations", donationService.countByDonor(donorId));
        stats.put("available", donationService.countByDonorAndStatus(donorId, FoodDonation.DonationStatus.AVAILABLE));
        stats.put("pending", requestService.countByStatus(RequestStatus.PENDING));
        stats.put("completed", donationService.countByDonorAndStatus(donorId, FoodDonation.DonationStatus.COMPLETED));
        stats.put("expiringSoon", donationService.getExpiringSoon(6).stream()
                .filter(d -> d.getDonor().getId().equals(donorId)).count());
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<FoodDonation>> getAll() {
        return ResponseEntity.ok(donationService.findAll());
    }
    
    @GetMapping("/categories")
    public ResponseEntity<Map<String, Long>> getCategoryDistribution() {
        return ResponseEntity.ok(donationService.getCategoryDistribution());
    }
}

