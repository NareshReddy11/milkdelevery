package com.shanthifarms.repository;

import com.shanthifarms.model.DeliveryRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface DeliveryRecordRepository extends JpaRepository<DeliveryRecord, Long> {
    List<DeliveryRecord> findByDeliveryDate(LocalDate date);
    List<DeliveryRecord> findByMilkPlanCustomerIdAndDeliveryDateGreaterThanEqual(Long customerId, LocalDate date);
    List<DeliveryRecord> findByMilkPlanCustomerId(Long customerId);
}
