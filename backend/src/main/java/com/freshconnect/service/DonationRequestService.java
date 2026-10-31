package com.freshconnect.service;

import com.freshconnect.model.*;
import com.freshconnect.model.DonationRequest.RequestStatus;
import com.freshconnect.model.FoodDonation.DonationStatus;
import com.freshconnect.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class DonationRequestService {
    
    @Autowired
    private DonationRequestRepository requestRepository;
    
    @Autowired
    private FoodDonationRepository donationRepository;
    
    @Autowired
    private DonationHistoryRepository historyRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Transactional
    public DonationRequest createRequest(Long donationId, Long ngoId, String message, Integer requestedQuantity) {
        FoodDonation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new RuntimeException("Donation not found"));
        
        User ngo = null;
        if (ngoId != null) {
            ngo = userRepository.findById(ngoId).orElse(null);
        }
        if (ngo == null) {
            ngo = userRepository.findByRole(User.UserRole.NGO).stream().findFirst()
                    .orElseThrow(() -> new RuntimeException("No NGO found in database"));
        }
        
        // Validate
        if (donation.getStatus() != DonationStatus.AVAILABLE) {
            throw new RuntimeException("Donation is no longer available");
        }
        
        if (donation.getExpiryTime() != null && donation.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Donation has expired");
        }
        
        // Check for duplicate active requests
        boolean exists = requestRepository.existsByDonationIdAndNgoIdAndStatusIn(
                donationId, ngoId, Arrays.asList(RequestStatus.PENDING, RequestStatus.ACCEPTED));
        if (exists) {
            throw new RuntimeException("You already have an active request for this donation");
        }
        
        DonationRequest request = new DonationRequest();
        request.setDonation(donation);
        request.setNgo(ngo);
        request.setMessage(message);
        request.setRequestedQuantity(requestedQuantity != null ? requestedQuantity : donation.getQuantity());
        request.setStatus(RequestStatus.PENDING);
        
        DonationRequest saved = requestRepository.save(request);
        
        // Update donation status
        donation.setStatus(DonationStatus.REQUESTED);
        donationRepository.save(donation);
        
        // Record history
        historyRepository.save(new DonationHistory(
            donation, saved, ngo, "REQUESTED",
            ngo.getOrganizationName() + " requested " + donation.getFoodName()
        ));
        
        return saved;
    }
    
    @Transactional
    public DonationRequest acceptRequest(Long requestId) {
        DonationRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        
        request.setStatus(RequestStatus.ACCEPTED);
        request.setAcceptedAt(LocalDateTime.now());
        requestRepository.save(request);
        
        // Update donation status
        request.getDonation().setStatus(DonationStatus.ACCEPTED);
        donationRepository.save(request.getDonation());
        
        // Reject other pending requests for the same donation
        List<DonationRequest> otherRequests = requestRepository.findByDonationId(request.getDonation().getId());
        for (DonationRequest other : otherRequests) {
            if (!other.getId().equals(requestId) && other.getStatus() == RequestStatus.PENDING) {
                other.setStatus(RequestStatus.REJECTED);
                requestRepository.save(other);
            }
        }
        
        historyRepository.save(new DonationHistory(
            request.getDonation(), request, request.getDonation().getDonor(), "ACCEPTED",
            "Request accepted for " + request.getNgo().getOrganizationName()
        ));
        
        return request;
    }
    
    @Transactional
    public DonationRequest rejectRequest(Long requestId) {
        DonationRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        
        request.setStatus(RequestStatus.REJECTED);
        requestRepository.save(request);
        
        // Check if there are other pending requests
        List<DonationRequest> pendingRequests = requestRepository.findByDonationId(request.getDonation().getId())
                .stream().filter(r -> r.getStatus() == RequestStatus.PENDING).toList();
        
        if (pendingRequests.isEmpty()) {
            request.getDonation().setStatus(DonationStatus.AVAILABLE);
            donationRepository.save(request.getDonation());
        }
        
        historyRepository.save(new DonationHistory(
            request.getDonation(), request, request.getDonation().getDonor(), "REJECTED",
            "Request rejected for " + request.getNgo().getOrganizationName()
        ));
        
        return request;
    }
    
    @Transactional
    public DonationRequest markCollected(Long requestId) {
        DonationRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        
        request.setStatus(RequestStatus.COLLECTED);
        request.setCollectedAt(LocalDateTime.now());
        requestRepository.save(request);
        
        request.getDonation().setStatus(DonationStatus.COLLECTED);
        donationRepository.save(request.getDonation());
        
        historyRepository.save(new DonationHistory(
            request.getDonation(), request, request.getNgo(), "COLLECTED",
            "Food collected by " + request.getNgo().getOrganizationName()
        ));
        
        return request;
    }
    
    @Transactional
    public DonationRequest markCompleted(Long requestId) {
        DonationRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        
        request.setStatus(RequestStatus.COMPLETED);
        request.setCompletedAt(LocalDateTime.now());
        requestRepository.save(request);
        
        request.getDonation().setStatus(DonationStatus.COMPLETED);
        donationRepository.save(request.getDonation());
        
        historyRepository.save(new DonationHistory(
            request.getDonation(), request, request.getNgo(), "COMPLETED",
            "Distribution completed by " + request.getNgo().getOrganizationName()
        ));
        
        return request;
    }
    
    @Transactional
    public DonationRequest cancelRequest(Long requestId) {
        DonationRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        
        request.setStatus(RequestStatus.CANCELLED);
        requestRepository.save(request);
        
        // Check if donation should go back to available
        List<DonationRequest> activeRequests = requestRepository.findByDonationId(request.getDonation().getId())
                .stream().filter(r -> r.getStatus() == RequestStatus.PENDING || r.getStatus() == RequestStatus.ACCEPTED)
                .toList();
        
        if (activeRequests.isEmpty()) {
            request.getDonation().setStatus(DonationStatus.AVAILABLE);
            donationRepository.save(request.getDonation());
        }
        
        historyRepository.save(new DonationHistory(
            request.getDonation(), request, request.getNgo(), "CANCELLED",
            "Request cancelled"
        ));
        
        return request;
    }
    
    public List<DonationRequest> getRequestsByNgo(Long ngoId) {
        return requestRepository.findByNgoId(ngoId);
    }
    
    public List<DonationRequest> getRequestsByDonor(Long donorId) {
        return requestRepository.findByDonationDonorId(donorId);
    }
    
    public List<DonationRequest> getRequestsByDonation(Long donationId) {
        return requestRepository.findByDonationId(donationId);
    }
    
    public Optional<DonationRequest> getRequestById(Long id) {
        return requestRepository.findById(id);
    }
    
    public List<DonationRequest> findAll() {
        return requestRepository.findAll();
    }
    
    public long countByStatus(RequestStatus status) {
        return requestRepository.countByStatus(status);
    }
    
    public long countByNgoAndStatus(Long ngoId, RequestStatus status) {
        return requestRepository.countByNgoIdAndStatus(ngoId, status);
    }
}
