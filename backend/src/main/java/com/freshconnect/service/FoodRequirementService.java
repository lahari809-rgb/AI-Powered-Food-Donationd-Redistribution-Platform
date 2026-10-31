package com.freshconnect.service;

import com.freshconnect.model.FoodRequirement;
import com.freshconnect.model.FoodRequirement.RequirementStatus;
import com.freshconnect.repository.FoodRequirementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class FoodRequirementService {
    
    @Autowired
    private FoodRequirementRepository requirementRepository;
    
    public FoodRequirement createRequirement(FoodRequirement requirement) {
        requirement.setStatus(RequirementStatus.ACTIVE);
        return requirementRepository.save(requirement);
    }
    
    public List<FoodRequirement> getRequirementsByNgo(Long ngoId) {
        return requirementRepository.findByNgoId(ngoId);
    }
    
    public List<FoodRequirement> getActiveRequirements() {
        return requirementRepository.findByStatus(RequirementStatus.ACTIVE);
    }
    
    public Optional<FoodRequirement> getById(Long id) {
        return requirementRepository.findById(id);
    }
    
    public FoodRequirement updateRequirement(Long id, FoodRequirement updated) {
        FoodRequirement req = requirementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Requirement not found"));
        if (updated.getFoodCategory() != null) req.setFoodCategory(updated.getFoodCategory());
        if (updated.getFoodType() != null) req.setFoodType(updated.getFoodType());
        if (updated.getQuantity() != null) req.setQuantity(updated.getQuantity());
        if (updated.getUnit() != null) req.setUnit(updated.getUnit());
        if (updated.getBeneficiaries() != null) req.setBeneficiaries(updated.getBeneficiaries());
        if (updated.getDescription() != null) req.setDescription(updated.getDescription());
        if (updated.getUrgency() != null) req.setUrgency(updated.getUrgency());
        if (updated.getNeededBy() != null) req.setNeededBy(updated.getNeededBy());
        return requirementRepository.save(req);
    }
    
    public void updateStatus(Long id, RequirementStatus status) {
        FoodRequirement req = requirementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Requirement not found"));
        req.setStatus(status);
        requirementRepository.save(req);
    }
    
    public void cancelRequirement(Long id) {
        updateStatus(id, RequirementStatus.CANCELLED);
    }
    
    public long countByNgo(Long ngoId) {
        return requirementRepository.countByNgoId(ngoId);
    }
    
    public long countActiveByNgo(Long ngoId) {
        return requirementRepository.countByNgoIdAndStatus(ngoId, RequirementStatus.ACTIVE);
    }
    
    public List<FoodRequirement> findAll() {
        return requirementRepository.findAll();
    }
}
