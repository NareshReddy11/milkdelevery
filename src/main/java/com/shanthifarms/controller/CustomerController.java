package com.shanthifarms.controller;

import com.shanthifarms.model.*;
import com.shanthifarms.repository.DeliveryRecordRepository;
import com.shanthifarms.repository.MilkPlanRepository;
import com.shanthifarms.repository.OrderRepository;
import com.shanthifarms.service.PlanService;
import com.shanthifarms.service.DeliveryService;
import com.shanthifarms.service.WhatsAppService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;

import java.time.LocalDate;
import java.util.List;

@Controller
public class CustomerController {

    private final PlanService planService;
    private final MilkPlanRepository planRepo;
    private final DeliveryRecordRepository recordRepo;
    private final DeliveryService deliveryService;
    private final WhatsAppService whatsAppService;
    private final OrderRepository orderRepo;

    public CustomerController(PlanService planService,
                              MilkPlanRepository planRepo,
                              DeliveryRecordRepository recordRepo,
                              DeliveryService deliveryService,
                              WhatsAppService whatsAppService,
                              OrderRepository orderRepo) {
        this.planService = planService;
        this.planRepo = planRepo;
        this.recordRepo = recordRepo;
        this.deliveryService = deliveryService;
        this.whatsAppService = whatsAppService;
        this.orderRepo = orderRepo;
    }

    @GetMapping("/home")
    public String home(HttpSession session, Model model){
        Long cid = (Long) session.getAttribute("customerId");
        if(cid==null) return "redirect:/login";
        List<MilkPlan> plans = planService.getPlansForCustomer(cid);
        model.addAttribute("plans", plans);
        return "home";
    }

    @GetMapping("/plans/new")
    public String newPlanForm(HttpSession session){
        Long cid = (Long) session.getAttribute("customerId");
        if(cid==null) return "redirect:/login";
        return "plan-form";
    }

    @PostMapping("/plans")
    public String createPlan(HttpSession session,
                             @RequestParam String startDate,
                             @RequestParam int days,
                             @RequestParam double liters,
                             Model model) {
        Long cid = (Long) session.getAttribute("customerId");
        if (cid == null) return "redirect:/login";

        MilkPlan saved = planService.createPlan(
                cid,
                LocalDate.parse(startDate),
                days,
                liters
        );

        whatsAppService.sendMessage("+91-00000", "Your plan is created");
        model.addAttribute("message", "Plan created");

        // Redirect to payment page for the newly created plan
        return "redirect:/plans/" + saved.getId() + "/pay";
    }


    @GetMapping("/plans/{id}/pay")
    public String payPlan(@PathVariable Long id, Model model) {
        // find plan
        MilkPlan plan = planRepo.findById(id).orElseThrow(() -> new RuntimeException("Plan not found"));

        // Create a demo ordre for this plan (if you want persistent orders)
        OrderEntity order = new OrderEntity();
        order.setCustomer(plan.getCustomer());
        order.setLiters(plan.getLitersPerDay());
        order.setDeliveryDate(plan.getStartDate()); // keep this for first delivery
        order.setOrderDate(plan.getStartDate());    // ✅ use plan’s start date, not LocalDate.now()
        order.setStatus("CONFIRMED");
        order.setPaymentStatus("PENDING");
        orderRepo.save(order);

        model.addAttribute("plan", plan);
        model.addAttribute("orderId", order.getId()); // pass to template
        return "payment";  // matches payment.html
    }

    @PatchMapping("/orders/{id}/paid")
    @ResponseBody
    public String markOrderPaid(@PathVariable Long id) {
        // fetch order
        OrderEntity order = orderRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // mark paid
        order.setPaymentStatus("PAID");
        order.setStatus("CONFIRMED");
        orderRepo.save(order);

        // try to find a milk plan for this customer (first active one)
        Long customerId = order.getCustomer() != null ? order.getCustomer().getId() : null;
        MilkPlan plan = null;
        if (customerId != null) {
            List<MilkPlan> plans = planRepo.findByCustomerId(customerId);
            if (plans != null && !plans.isEmpty()) {
                plan = plans.stream().filter(p -> "ACTIVE".equals(p.getStatus()))
                        .findFirst()
                        .orElse(plans.get(0));
            }
        }

        // ✅ Create delivery record ONLY now (after payment)
        if (plan != null) {
            DeliveryRecord rec = new DeliveryRecord();
            rec.setMilkPlan(plan);
            rec.setDeliveryDate(order.getDeliveryDate());  // usually plan.getStartDate()
            rec.setStatus("PENDING");
            recordRepo.save(rec);
        }

        return "OK";
    }
    @GetMapping("/orders")
    public String orders(HttpSession session, Model model) {
        Long cid = (Long) session.getAttribute("customerId");
        if (cid == null) {
            return "redirect:/login";
        }

        List<DeliveryRecord> records = deliveryService.findByCustomer(cid);
        model.addAttribute("records", records);

        return "orders";
    }

    @GetMapping("/account")
    public String account(HttpSession session, Model model) {
        Long cid = (Long) session.getAttribute("customerId");
        if (cid == null) return "redirect:/Login";

        // fetch the Customer entity (not Long!)
        Customer customer = planRepo.findByCustomerId(cid)
                .stream()
                .findFirst()
                .map(MilkPlan::getCustomer)
                .orElse(null);

        MilkPlan plan = planRepo.findByCustomerId(cid)
                .stream()
                .findFirst()
                .orElse(null);

        model.addAttribute("customer", customer);
        model.addAttribute("plan", plan);
        return "account";
    }
}
