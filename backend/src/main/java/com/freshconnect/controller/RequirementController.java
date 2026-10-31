package com.freshconnect.controller;

import com.freshconnect.model.FoodRequirement;
import com.freshconnect.model.User;
import com.freshconnect.service.FoodRequirementService;
import com.freshconnect.service.MatchingService;
import com.freshconnect.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/requirements")
public class RequirementController {
    
    @Autowired
    private FoodRequirementService requirementService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private MatchingService matchingService;
    
    @PostMapping
    public ResponseEntity<?> createRequirement(@Valid @RequestBody FoodRequirement requirement, @RequestParam(required = false) Long ngoId) {
        try {
            User ngo = null;
            if (ngoId != null) {
                ngo = userService.findById(ngoId).orElse(null);
            }
            if (ngo == null) {
                ngo = userService.findByRole(User.UserRole.NGO).stream().findFirst()
                        .orElseThrow(() -> new RuntimeException("No NGO found"));
            }
            requirement.setNgo(ngo);
            FoodRequirement saved = requirementService.createRequirement(requirement);
            return ResponseEntity.ok(Map.of("success", true, "message", "Requirement created successfully", "requirement", saved));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    @GetMapping("/ngo/{ngoId}")
    public ResponseEntity<List<FoodRequirement>> getByNgo(@PathVariable Long ngoId) {
        return ResponseEntity.ok(requirementService.getRequirementsByNgo(ngoId));
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<FoodRequirement>> getActive() {
        return ResponseEntity.ok(requirementService.getActiveRequirements());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return requirementService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRequirement(@PathVariable Long id, @RequestBody FoodRequirement updated) {
        try {
            return ResponseEntity.ok(requirementService.updateRequirement(id, updated));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<?> cancelRequirement(@PathVariable Long id) {
        try {
            requirementService.cancelRequirement(id);
            return ResponseEntity.ok(Map.of("success", true, "message", "Requirement cancelled"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    
    @GetMapping("/ngo/{ngoId}/matches")
    public ResponseEntity<List<MatchingService.MatchResult>> getMatches(@PathVariable Long ngoId) {
        return ResponseEntity.ok(matchingService.findMatchesForNgo(ngoId));
    }
    
    @GetMapping("/ngo/{ngoId}/predict")
    public ResponseEntity<Map<String, Object>> predictDemand(@PathVariable Long ngoId) {
        return ResponseEntity.ok(matchingService.predictDemand(ngoId));
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<FoodRequirement>> getAll() {
        return ResponseEntity.ok(requirementService.findAll());
    }
}

