package com.courrier.Bcourrier.Controllers;

import com.courrier.Bcourrier.Entities.Employe;
import com.courrier.Bcourrier.Repositories.EmployeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/recover")
public class RecoveryController {

    @Autowired
    private EmployeRepository employeRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;

    @PostMapping("/email")
    public ResponseEntity<?> sendRecoveryEmail(@RequestParam String email) {
        Optional<Employe> optional = employeRepository.findByEmail(email);
        if (optional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No user found with that email.");
        }
        Employe employe = optional.get();

        // Generate dummy token or UUID (store in DB if needed)
        String token = UUID.randomUUID().toString();
        String recoveryLink = "http://localhost:9090/reset-password?token=" + token;

        // Save token if you want to check later
        employe.setVerificationToken(token);
        employeRepository.save(employe);
        // Send email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Password Recovery");
        message.setText("Click here to reset your password: " + recoveryLink);
        mailSender.send(message);

        return ResponseEntity.ok("Recovery email sent.");
    }


    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @RequestParam String token,
            @RequestParam String newPassword
    ) {
        Optional<Employe> optional = employeRepository.findByVerificationToken(token);
        if (optional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token.");
        }

        Employe employe = optional.get();

        if (employe.getVerificationToken() == null ) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token expired.");
        }

        employe.setPassword(passwordEncoder.encode(newPassword));
        employe.setVerificationToken(null); // clear token after use
        employeRepository.save(employe);

        return ResponseEntity.ok("Password has been reset.");
    }

}

