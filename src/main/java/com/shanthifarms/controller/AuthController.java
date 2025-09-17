package com.shanthifarms.controller;

import com.shanthifarms.service.AuthService;
import com.shanthifarms.repository.CustomerRepository;
import com.shanthifarms.model.Customer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final AuthService authService;
    private final CustomerRepository customerRepo;

    public AuthController(AuthService authService, CustomerRepository customerRepo) {
        this.authService = authService;
        this.customerRepo = customerRepo;
    }

    @GetMapping({"/", "/login"})
    public String loginPage() {
        return "login";
    }

    @PostMapping("/auth/send-otp")
    public String sendOtp(@RequestParam String phone,
                          RedirectAttributes redirectAttributes) {
        String otp = authService.generateOtp(phone);

        redirectAttributes.addAttribute("phone", phone); // adds ?phone=xxxx in URL
        redirectAttributes.addFlashAttribute("debugOtp", otp); // flash OTP for UI

        return "redirect:/login";
    }

    @PostMapping("/auth/verify-otp")
    public String verify(@RequestParam String phone,
                         @RequestParam String otp,
                         HttpSession session) {
        boolean ok = authService.verifyOtp(phone, otp);
        if (!ok) return "redirect:/login?error=otp";

        // save customer if not exists
        Customer c = customerRepo.findByPhone(phone)
                .orElseGet(() -> customerRepo.save(new Customer(phone)));

        // store in session
        session.setAttribute("customerId", c.getId());
        session.setAttribute("phone", phone);

        return "redirect:/home";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
