package com.freshconnect.controller;

import com.freshconnect.model.DonationRequest;
import com.freshconnect.service.DonationRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/requests")
public class RequestController {
    
    @Autowired
    private DonationRequestService requestService;
    
    @PostMapping
    public ResponseEntity<?> createRequest(@RequestBody Map<String, Object> body) {
        try {
            Long donationId = Long.valueOf(body.get("donationId").toString());
            Long ngoId = Long.valueOf(body.get("ngoId").toString());
            String message = body.containsKey("message") ? body.get("message").toString() : null;
            Integer quantity = body.containsKey("requestedQuantity") 
                    ? Integer.valueOf(body.get("requestedQuantity").toString()) : null;
            
            DonationRequest request = requestService.createRequest(donationId, ngoId, message, quantity);
            return ResponseEntity.ok(Map.of("success", true, "message", "Request submitted successfully", "request", request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    @PatchMapping("/{id}/accept")
    public ResponseEntity<?> acceptRequest(@PathVariable Long id) {
        try {
            DonationRequest request = requestService.acceptRequest(id);
            return ResponseEntity.ok(Map.of("success", true, "message", "Request accepted", "request", request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    @PatchMapping("/{id}/reject")
    public ResponseEntity<?> rejectRequest(@PathVariable Long id) {
        try {
            DonationRequest request = requestService.rejectRequest(id);
            return ResponseEntity.ok(Map.of("success", true, "message", "Request rejected", "request", request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    @PatchMapping("/{id}/collect")
    public ResponseEntity<?> markCollected(@PathVariable Long id) {
        try {
            DonationRequest request = requestService.markCollected(id);
            return ResponseEntity.ok(Map.of("success", true, "message", "Marked as collected", "request", request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    @PatchMapping("/{id}/complete")
    public ResponseEntity<?> markCompleted(@PathVariable Long id) {
        try {
            DonationRequest request = requestService.markCompleted(id);
            return ResponseEntity.ok(Map.of("success", true, "message", "Distribution completed", "request", request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<?> cancelRequest(@PathVariable Long id) {
        try {
            DonationRequest request = requestService.cancelRequest(id);
            return ResponseEntity.ok(Map.of("success", true, "message", "Request cancelled", "request", request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return requestService.getRequestById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/ngo/{ngoId}")
    public ResponseEntity<List<DonationRequest>> getByNgo(@PathVariable Long ngoId) {
        return ResponseEntity.ok(requestService.getRequestsByNgo(ngoId));
    }
    
    @GetMapping("/donor/{donorId}")
    public ResponseEntity<List<DonationRequest>> getByDonor(@PathVariable Long donorId) {
        return ResponseEntity.ok(requestService.getRequestsByDonor(donorId));
    }
    
    @GetMapping("/donation/{donationId}")
    public ResponseEntity<List<DonationRequest>> getByDonation(@PathVariable Long donationId) {
        return ResponseEntity.ok(requestService.getRequestsByDonation(donationId));
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<DonationRequest>> getAll() {
        return ResponseEntity.ok(requestService.findAll());
    }
}

