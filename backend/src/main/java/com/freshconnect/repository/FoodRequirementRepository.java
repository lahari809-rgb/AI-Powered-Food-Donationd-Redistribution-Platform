package com.freshconnect.repository;

import com.freshconnect.model.FoodRequirement;
import com.freshconnect.model.FoodRequirement.RequirementStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FoodRequirementRepository extends JpaRepository<FoodRequirement, Long> {
    List<FoodRequirement> findByNgoId(Long ngoId);
    List<FoodRequirement> findByStatus(RequirementStatus status);
    List<FoodRequirement> findByNgoIdAndStatus(Long ngoId, RequirementStatus status);
    long countByNgoId(Long ngoId);
    long countByNgoIdAndStatus(Long ngoId, RequirementStatus status);
}
