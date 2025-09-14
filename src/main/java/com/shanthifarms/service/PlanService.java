package com.shanthifarms.service;

import com.shanthifarms.model.MilkPlan;
import com.shanthifarms.model.Customer;
import com.shanthifarms.repository.MilkPlanRepository;
import com.shanthifarms.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

@Service
public class PlanService {
    private final MilkPlanRepository planRepo;
    private final CustomerRepository customerRepo;

    public PlanService(MilkPlanRepository planRepo, CustomerRepository customerRepo){
        this.planRepo = planRepo; this.customerRepo = customerRepo;
    }

    public MilkPlan createPlan(String phone, LocalDate startDate, int days, double liters, String subscriptionType){
        Optional<Customer> oc = customerRepo.findByPhone(phone);
        Customer c = oc.orElseGet(()-> customerRepo.save(new Customer(phone)));
        MilkPlan p = new MilkPlan();
        p.setCustomer(c);
        p.setStartDate(startDate);
        p.setDays(days);
        p.setLitersPerDay(liters);
        p.setSubscriptionType(subscriptionType);
        return planRepo.save(p);
    }

    public List<MilkPlan> getPlansForCustomer(Long customerId){
        return planRepo.findByCustomerId(customerId);
    }

    public List<MilkPlan> getActivePlans(){
        return planRepo.findByStatus("ACTIVE");
    }
}
