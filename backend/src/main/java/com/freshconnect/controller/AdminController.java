package com.freshconnect.controller;

import com.freshconnect.model.DonationHistory;
import com.freshconnect.model.DonationRequest.RequestStatus;
import com.freshconnect.model.FoodDonation.DonationStatus;
import com.freshconnect.model.User.UserRole;
import com.freshconnect.repository.DonationHistoryRepository;
import com.freshconnect.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private FoodDonationService donationService;
    
    @Autowired
    private DonationRequestService requestService;
    
    @Autowired
    private FoodRequirementService requirementService;
    
    @Autowired
    private DonationHistoryRepository historyRepository;
    
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalDonors", userService.countByRole(UserRole.DONOR));
        stats.put("totalNgos", userService.countByRole(UserRole.NGO));
        stats.put("totalDonations", donationService.countAll());
        stats.put("availableDonations", donationService.countByStatus(DonationStatus.AVAILABLE));
        stats.put("completedDonations", donationService.countByStatus(DonationStatus.COMPLETED));
        stats.put("pendingRequests", requestService.countByStatus(RequestStatus.PENDING));
        stats.put("expiredDonations", donationService.countByStatus(DonationStatus.EXPIRED));
        
        long total = donationService.countAll();
        long completed = donationService.countByStatus(DonationStatus.COMPLETED);
        double successRate = total > 0 ? Math.round((double) completed / total * 100.0) : 0;
        stats.put("successRate", successRate);
        
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }
    
    @GetMapping("/users/{role}")
    public ResponseEntity<?> getUsersByRole(@PathVariable String role) {
        try {
            UserRole userRole = UserRole.valueOf(role.toUpperCase());
            return ResponseEntity.ok(userService.findByRole(userRole));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Invalid role"));
        }
    }
    
    @PatchMapping("/users/{id}/toggle")
    public ResponseEntity<?> toggleUserActive(@PathVariable Long id) {
        try {
            userService.toggleActive(id);
            return ResponseEntity.ok(Map.of("success", true, "message", "User status toggled"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    @GetMapping("/donations")
    public ResponseEntity<?> getAllDonations() {
        return ResponseEntity.ok(donationService.findAll());
    }
    
    @GetMapping("/requests")
    public ResponseEntity<?> getAllRequests() {
        return ResponseEntity.ok(requestService.findAll());
    }
    
    @GetMapping("/history")
    public ResponseEntity<List<DonationHistory>> getRecentHistory() {
        return ResponseEntity.ok(historyRepository.findTop20ByOrderByTimestampDesc());
    }
    
    @GetMapping("/analytics/categories")
    public ResponseEntity<Map<String, Long>> getCategoryDistribution() {
        return ResponseEntity.ok(donationService.getCategoryDistribution());
    }
    
    @GetMapping("/analytics/waste")
    public ResponseEntity<Map<String, Object>> getWasteAnalytics() {
        Map<String, Object> analytics = new HashMap<>();
        long total = donationService.countAll();
        long completed = donationService.countByStatus(DonationStatus.COMPLETED);
        long expired = donationService.countByStatus(DonationStatus.EXPIRED);
        long available = donationService.countByStatus(DonationStatus.AVAILABLE);
        
        analytics.put("totalDonated", total);
        analytics.put("successfullyRedistributed", completed);
        analytics.put("expired", expired);
        analytics.put("currentlyAvailable", available);
        analytics.put("wastePreventionRate", total > 0 ? Math.round((double) completed / total * 100.0) : 0);
        
        // Estimate beneficiaries (average ~3 meals per donation unit)
        analytics.put("estimatedBeneficiaries", completed * 3);
        
        return ResponseEntity.ok(analytics);
    }
}

