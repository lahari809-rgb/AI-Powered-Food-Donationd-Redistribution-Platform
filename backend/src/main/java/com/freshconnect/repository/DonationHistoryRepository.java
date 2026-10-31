package com.freshconnect.repository;

import com.freshconnect.model.DonationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DonationHistoryRepository extends JpaRepository<DonationHistory, Long> {
    List<DonationHistory> findByDonationIdOrderByTimestampDesc(Long donationId);
    List<DonationHistory> findByUserIdOrderByTimestampDesc(Long userId);
    List<DonationHistory> findTop20ByOrderByTimestampDesc();
    List<DonationHistory> findByAction(String action);
}
