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

    public DeliveryService(DeliveryRecordRepository recordRepo, MilkPlanRepository planRepo, WhatsAppService whatsAppService){
        this.recordRepo = recordRepo; this.planRepo = planRepo; this.whatsAppService = whatsAppService;
    }

    public void generateDeliveryRecordsForDate(LocalDate date){
        List<MilkPlan> plans = planRepo.findByStatus("ACTIVE");
        for(MilkPlan p : plans){
            LocalDate start = p.getStartDate();
            LocalDate end = start.plusDays(p.getDays()-1);
            if((!date.isBefore(start)) && (!date.isAfter(end))){
                boolean shouldGenerate = false;
                switch (p.getSubscriptionType()) {
                    case "DAILY":
                        shouldGenerate = true;
                        break;
                    case "WEEKLY":
                        shouldGenerate = (java.time.temporal.ChronoUnit.DAYS.between(start, date) % 7 == 0);
                        break;
                    case "MONTHLY":
                        shouldGenerate = (java.time.temporal.ChronoUnit.DAYS.between(start, date) % 30 == 0);
                        break;
                    case "CUSTOM":
                        // For custom, you can add logic to check a list of custom delivery dates
                        shouldGenerate = true; // Default to true for now
                        break;
                }
                if (shouldGenerate) {
                    boolean exists = recordRepo.findByDeliveryDate(date).stream()
                        .anyMatch(r-> r.getMilkPlan().getId().equals(p.getId()));
                    if(!exists){
                        DeliveryRecord r = new DeliveryRecord();
                        r.setMilkPlan(p);
                        r.setDeliveryDate(date);
                        r.setStatus("PENDING");
                        recordRepo.save(r);
                    }
                }
            }
        }
    }

    public List<DeliveryRecord> getRecordsForDate(LocalDate date){
        return recordRepo.findByDeliveryDate(date);
    }

    public void markDelivered(Long id){
        DeliveryRecord r = recordRepo.findById(id).orElseThrow();
        r.setStatus("DELIVERED");
        r.setDeliveredAt(java.time.Instant.now());
        recordRepo.save(r);
        String phone = r.getMilkPlan().getCustomer().getPhone();
        double liters = r.getMilkPlan().getLitersPerDay();
        whatsAppService.sendMessage(phone, "Your milk (" + liters + "L) has been delivered today.");
    }
}
