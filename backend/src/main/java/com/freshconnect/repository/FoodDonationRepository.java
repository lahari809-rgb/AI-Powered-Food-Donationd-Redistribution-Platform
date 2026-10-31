package com.freshconnect.repository;

import com.freshconnect.model.FoodDonation;
import com.freshconnect.model.FoodDonation.DonationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FoodDonationRepository extends JpaRepository<FoodDonation, Long> {
    List<FoodDonation> findByDonorId(Long donorId);
    List<FoodDonation> findByStatus(DonationStatus status);
    List<FoodDonation> findByDonorIdAndStatus(Long donorId, DonationStatus status);
    
    @Query("SELECT d FROM FoodDonation d WHERE d.status = 'AVAILABLE' AND d.expiryTime > :now ORDER BY d.urgencyScore DESC NULLS LAST, d.expiryTime ASC")
    List<FoodDonation> findAvailableDonations(@Param("now") LocalDateTime now);
    
    @Query("SELECT d FROM FoodDonation d WHERE d.status = 'AVAILABLE' AND d.expiryTime > :now AND d.category = :category")
    List<FoodDonation> findAvailableByCategory(@Param("now") LocalDateTime now, @Param("category") String category);
    
    long countByDonorId(Long donorId);
    long countByDonorIdAndStatus(Long donorId, DonationStatus status);
    long countByStatus(DonationStatus status);
    
    @Query("SELECT d FROM FoodDonation d WHERE d.status = 'AVAILABLE' AND d.expiryTime <= :threshold AND d.expiryTime > :now")
    List<FoodDonation> findExpiringSoon(@Param("now") LocalDateTime now, @Param("threshold") LocalDateTime threshold);
    
    @Query("SELECT SUM(d.quantity) FROM FoodDonation d WHERE d.status = 'COMPLETED'")
    Long totalRedistributedQuantity();
    
    @Query("SELECT SUM(d.quantity) FROM FoodDonation d WHERE d.donor.id = :donorId AND d.status = 'COMPLETED'")
    Long totalRedistributedByDonor(@Param("donorId") Long donorId);
    
    @Query("SELECT d.category, COUNT(d) FROM FoodDonation d GROUP BY d.category")
    List<Object[]> countByCategory();
    
    @Query("SELECT FUNCTION('MONTH', d.createdAt) as month, COUNT(d) FROM FoodDonation d WHERE d.createdAt >= :since GROUP BY FUNCTION('MONTH', d.createdAt) ORDER BY month")
    List<Object[]> donationTrends(@Param("since") LocalDateTime since);
}
