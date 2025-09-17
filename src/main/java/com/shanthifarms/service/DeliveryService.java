package com.shanthifarms.service;

import com.shanthifarms.model.DeliveryRecord;
import com.shanthifarms.model.MilkPlan;
import com.shanthifarms.repository.DeliveryRecordRepository;
import com.shanthifarms.repository.MilkPlanRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class DeliveryService {
    private final DeliveryRecordRepository recordRepo;
    private final MilkPlanRepository planRepo;
    private final WhatsAppService whatsAppService;

    public DeliveryService(DeliveryRecordRepository recordRepo,
                           MilkPlanRepository planRepo,
                           WhatsAppService whatsAppService) {
        this.recordRepo = recordRepo;
        this.planRepo = planRepo;
        this.whatsAppService = whatsAppService;
    }
    // Fetch deliveries by customerId
    public List<DeliveryRecord> findByCustomer(Long customerId) {
        return recordRepo.findByMilkPlan_Customer_Id(customerId);
    }

    // ✅ Generate delivery records for a given date
    public void generateDeliveryRecordsForDate(LocalDate date) {
        List<MilkPlan> plans = planRepo.findByStatus("ACTIVE");
        for (MilkPlan p : plans) {
            LocalDate start = p.getStartDate();
            LocalDate end = start.plusDays(p.getDays() - 1);

            if (!date.isBefore(start) && !date.isAfter(end)) {
                boolean exists = recordRepo.findByDeliveryDate(date).stream()
                        .anyMatch(r -> r.getMilkPlan().getId().equals(p.getId()));

                if (!exists) {
                    DeliveryRecord r = new DeliveryRecord();
                    r.setMilkPlan(p);
                    r.setDeliveryDate(date);
                    r.setStatus("PENDING");
                    recordRepo.save(r);
                }
            }
        }
    }
    public void generateDeliveryRecordForPlan(MilkPlan plan) {
        LocalDate startDate = plan.getStartDate();

        // check uniqueness before insert
        boolean exists = recordRepo.existsByMilkPlan_IdAndDeliveryDate(plan.getId(), startDate);

        if (!exists) {
            DeliveryRecord record = new DeliveryRecord();
            record.setMilkPlan(plan);
            record.setDeliveryDate(startDate);
            record.setStatus("PENDING");
            recordRepo.save(record);
        }
    }

    // Mark delivery as delivered
    public void markDelivered(Long id) {
        DeliveryRecord r = recordRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("DeliveryRecord not found"));

        r.setStatus("DELIVERED");
        r.setDeliveredAt(java.time.LocalDateTime.now());

        recordRepo.save(r); // ✅ Now works, because repo matches entity
    }
}
