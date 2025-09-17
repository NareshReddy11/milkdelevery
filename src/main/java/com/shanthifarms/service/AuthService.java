package com.shanthifarms.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {

    private static class OtpEntry {
        String otp;
        LocalDateTime expiry;
        OtpEntry(String otp, LocalDateTime expiry) {
            this.otp = otp;
            this.expiry = expiry;
        }
    }

    private final Map<String, OtpEntry> otpStore = new ConcurrentHashMap<>();
    private final Random random = new Random();

    // Generate random 4-digit OTP, valid for 5 minutes
    public String generateOtp(String phone) {
        String otp = String.valueOf(1000 + random.nextInt(9000));
        otpStore.put(phone, new OtpEntry(otp, LocalDateTime.now().plusMinutes(5)));

        // For now, print to logs (remove later when using SMS API)
        System.out.println("Generated OTP for " + phone + ": " + otp);

        return otp;
    }

    // Verify OTP
    public boolean verifyOtp(String phone, String otp) {
        OtpEntry entry = otpStore.get(phone);
        if (entry == null) return false;

        if (LocalDateTime.now().isAfter(entry.expiry)) {
            otpStore.remove(phone);
            return false; // expired
        }

        boolean match = entry.otp.equals(otp);
        if (match) otpStore.remove(phone); // consume once
        return match;
    }
}
