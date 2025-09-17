package com.shanthifarms.service;

import com.shanthifarms.model.Customer;
import com.shanthifarms.model.MilkPlan;
import com.shanthifarms.repository.CustomerRepository;
import com.shanthifarms.repository.MilkPlanRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PlanService {
    private final MilkPlanRepository planRepo;
    private final CustomerRepository customerRepo;

    public PlanService(MilkPlanRepository planRepo, CustomerRepository customerRepo) {
        this.planRepo = planRepo;
        this.customerRepo = customerRepo;
    }

    public MilkPlan createPlan(Long customerId, LocalDate startDate, int days, double litersPerDay) {
        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        MilkPlan plan = new MilkPlan();
        plan.setCustomer(customer);
        plan.setStartDate(startDate);
        plan.setDays(days);
        plan.setLitersPerDay(litersPerDay);
        plan.setStatus("ACTIVE");

        return planRepo.save(plan);  // ✅ Only save plan, no delivery records here
    }

    public List<MilkPlan> getPlansForCustomer(Long customerId) {
        return planRepo.findByCustomerId(customerId);
    }
}
