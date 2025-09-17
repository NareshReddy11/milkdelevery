package com.shanthifarms.repository;

import com.shanthifarms.model.DeliveryRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DeliveryRecordRepository extends JpaRepository<DeliveryRecord, Long> {

    // ✅ Correct method
    List<DeliveryRecord> findByMilkPlan_Customer_IdOrderByDeliveryDateDesc(Long customerId);
    List<DeliveryRecord> findByMilkPlan_Id(Long planId);
    // Fetch all records for a specific delivery date
    List<DeliveryRecord> findByDeliveryDate(LocalDate deliveryDate);
    boolean existsByMilkPlan_IdAndDeliveryDate(Long planId, LocalDate deliveryDate);
    // Fetch all records for a customer (through MilkPlan → Customer)
    List<DeliveryRecord> findByMilkPlan_Customer_Id(Long customerId);

}
