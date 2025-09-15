package com.shanthifarms.service;

import com.shanthifarms.repository.CustomerRepository;
import com.shanthifarms.repository.MilkPlanRepository;
import com.shanthifarms.repository.DeliveryRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
public class AdminService {
    private final CustomerRepository customerRepo;
    private final MilkPlanRepository planRepo;
    private final DeliveryRepository deliveryRepo;

    public AdminService(CustomerRepository customerRepo,
                        MilkPlanRepository planRepo,
                        DeliveryRepository deliveryRepo) {
        this.customerRepo = customerRepo;
        this.planRepo = planRepo;
        this.deliveryRepo = deliveryRepo;
    }

    public long getTotalCustomers() {
        return customerRepo.count();
    }

    public long getActivePlans() {
        return planRepo.findByStatus("ACTIVE").size();
    }

    public long getTodayDeliveries() {
        return deliveryRepo.countByDeliveryDate(LocalDate.now());
    }
}
