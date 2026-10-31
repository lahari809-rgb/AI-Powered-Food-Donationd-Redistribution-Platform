package com.freshconnect.repository;

import com.freshconnect.model.DonationRequest;
import com.freshconnect.model.DonationRequest.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DonationRequestRepository extends JpaRepository<DonationRequest, Long> {
    List<DonationRequest> findByDonationId(Long donationId);
    List<DonationRequest> findByNgoId(Long ngoId);
    List<DonationRequest> findByNgoIdAndStatus(Long ngoId, RequestStatus status);
    List<DonationRequest> findByDonationDonorId(Long donorId);
    List<DonationRequest> findByDonationDonorIdAndStatus(Long donorId, RequestStatus status);
    List<DonationRequest> findByStatus(RequestStatus status);
    long countByNgoId(Long ngoId);
    long countByNgoIdAndStatus(Long ngoId, RequestStatus status);
    long countByStatus(RequestStatus status);
    long countByDonationDonorId(Long donorId);
    long countByDonationDonorIdAndStatus(Long donorId, RequestStatus status);
    boolean existsByDonationIdAndNgoIdAndStatusIn(Long donationId, Long ngoId, List<RequestStatus> statuses);
    
    @Query("SELECT r FROM DonationRequest r WHERE r.donation.donor.id = :donorId ORDER BY r.createdAt DESC")
    List<DonationRequest> findRecentByDonor(@Param("donorId") Long donorId);
}
