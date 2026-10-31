package com.freshconnect.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "food_requirements")
public class FoodRequirement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ngo_id", nullable = false)
    private User ngo;
    
    private String foodCategory; // meals, fruits, vegetables, bakery, dairy, any
    
    private String foodType; // vegetarian, non-vegetarian, any
    
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
    
    private String unit; // kg, packets, meals
    
    @NotNull(message = "Number of beneficiaries is required")
    @Min(value = 1)
    private Integer beneficiaries;
    
    private String description;
    private String urgency; // low, medium, high, critical
    
    @Enumerated(EnumType.STRING)
    private RequirementStatus status = RequirementStatus.ACTIVE;
    
    private LocalDateTime neededBy;
    
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) status = RequirementStatus.ACTIVE;
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum RequirementStatus {
        ACTIVE, PARTIALLY_FULFILLED, FULFILLED, CANCELLED
    }
    
    // Constructors
    public FoodRequirement() {}
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getNgo() { return ngo; }
    public void setNgo(User ngo) { this.ngo = ngo; }
    
    public String getFoodCategory() { return foodCategory; }
    public void setFoodCategory(String foodCategory) { this.foodCategory = foodCategory; }
    
    public String getFoodType() { return foodType; }
    public void setFoodType(String foodType) { this.foodType = foodType; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    
    public Integer getBeneficiaries() { return beneficiaries; }
    public void setBeneficiaries(Integer beneficiaries) { this.beneficiaries = beneficiaries; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getUrgency() { return urgency; }
    public void setUrgency(String urgency) { this.urgency = urgency; }
    
    public RequirementStatus getStatus() { return status; }
    public void setStatus(RequirementStatus status) { this.status = status; }
    
    public LocalDateTime getNeededBy() { return neededBy; }
    public void setNeededBy(LocalDateTime neededBy) { this.neededBy = neededBy; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
