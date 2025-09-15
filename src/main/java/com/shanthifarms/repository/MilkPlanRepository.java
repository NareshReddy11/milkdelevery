package com.shanthifarms.repository;

import com.shanthifarms.model.MilkPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MilkPlanRepository extends JpaRepository<MilkPlan, Long> {
    List<MilkPlan> findByStatus(String status);
    List<MilkPlan> findByCustomerId(Long customerId);
}
