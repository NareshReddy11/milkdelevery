package com.shanthifarms.controller;

import com.shanthifarms.service.AuthService;
import com.shanthifarms.repository.CustomerRepository;
import com.shanthifarms.model.Customer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {
    private final AuthService authService;
    private final CustomerRepository customerRepo;
    public AuthController(AuthService authService, CustomerRepository customerRepo){
        this.authService = authService; this.customerRepo = customerRepo;
    }

    @GetMapping({"/","/login"})
    public String loginPage(){ return "login"; }

    @PostMapping("/auth/send-otp")
    public String sendOtp(@RequestParam String phone, Model model){
        authService.generateOtp(phone);
        model.addAttribute("phone", phone);
        model.addAttribute("info", "OTP sent (demo: 1234)");
        return "login";
    }

    @PostMapping("/auth/verify-otp")
    public String verify(@RequestParam String phone, @RequestParam String otp, HttpSession session){
        boolean ok = authService.verifyOtp(phone, otp);
        if(!ok) return "redirect:/login?error=otp";
        Customer c = customerRepo.findByPhone(phone).orElseGet(()-> customerRepo.save(new Customer(phone)));
        session.setAttribute("customerId", c.getId());
        return "redirect:/home";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session){
        session.invalidate(); return "redirect:/login";
    }
}
