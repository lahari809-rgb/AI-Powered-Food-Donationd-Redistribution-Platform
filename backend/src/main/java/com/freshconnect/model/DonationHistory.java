package com.freshconnect.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "donation_history")
public class DonationHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "donation_id")
    private FoodDonation donation;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "request_id")
    private DonationRequest request;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;
    
    @NotNull
    private String action; // CREATED, REQUESTED, ACCEPTED, REJECTED, COLLECTED, COMPLETED, EXPIRED, CANCELLED
    
    private String details;
    
    @Column(updatable = false)
    private LocalDateTime timestamp;
    
    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }
    
    // Constructors
    public DonationHistory() {}
    
    public DonationHistory(FoodDonation donation, DonationRequest request, User user, String action, String details) {
        this.donation = donation;
        this.request = request;
        this.user = user;
        this.action = action;
        this.details = details;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public FoodDonation getDonation() { return donation; }
    public void setDonation(FoodDonation donation) { this.donation = donation; }
    
    public DonationRequest getRequest() { return request; }
    public void setRequest(DonationRequest request) { this.request = request; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
