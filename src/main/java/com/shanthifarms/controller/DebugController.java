package com.shanthifarms.controller;

import com.shanthifarms.service.DeliveryService;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@RestController
@RequestMapping("/debug")
public class DebugController {
    private final DeliveryService deliveryService;
    public DebugController(DeliveryService deliveryService){ this.deliveryService = deliveryService; }

    @PostMapping("/generate")
    public String gen(@RequestParam(required=false) String date){
        LocalDate d = date==null? LocalDate.now().plusDays(1) : LocalDate.parse(date);
        deliveryService.generateDeliveryRecordsForDate(d);
        return "generated:"+d.toString();
    }
}
