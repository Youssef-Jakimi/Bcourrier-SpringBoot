package com.courrier.Bcourrier.Controllers;

import com.courrier.Bcourrier.DTO.PasswordResetRequest;
import com.courrier.Bcourrier.Entities.Employe;
import com.courrier.Bcourrier.Entities.Question;
import com.courrier.Bcourrier.Repositories.EmployeRepository;
import com.courrier.Bcourrier.Repositories.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/recover")
@RequiredArgsConstructor
public class RecoveryController {

    @Autowired
    private EmployeRepository employeRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private final QuestionRepository questionRepository;


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
        String recoveryLink = "http://localhost:4200/change-password?token=" + token;

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
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetRequest request) {
        String token = request.getToken();
        String newPassword = request.getNewPassword();

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


    @PostMapping("/questions")
    public ResponseEntity<?> verifySecurityQuestions(
            @RequestParam String email,
            @RequestParam String question1,
            @RequestParam String answer1,
            @RequestParam String question2,
            @RequestParam String answer2,
            @RequestParam String question3,
            @RequestParam String answer3
    ) {
        Optional<Employe> optional = employeRepository.findByEmail(email);
        if (optional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        Employe employe = optional.get();

        // Check all questions and answers match
        if (employe.getQ1().equalsIgnoreCase(question1) && employe.getR1().equalsIgnoreCase(answer1) &&
                employe.getQ2().equalsIgnoreCase(question2) && employe.getR2().equalsIgnoreCase(answer2) &&
                employe.getQ3().equalsIgnoreCase(question3) && employe.getR3().equalsIgnoreCase(answer3)) {

            // You can return a token or status to allow access to password reset form
            return ResponseEntity.ok("Security questions verified. You may reset your password.");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Security answers do not match.");
        }
    }


    @PostMapping("/reset-password-question")
    public ResponseEntity<?> resetPasswordQuestion(
            @RequestParam String email,
            @RequestParam String newPassword
    ) {
        Optional<Employe> optional = employeRepository.findByEmail(email);

        if (optional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        Employe employe = optional.get();

        employe.setPassword(passwordEncoder.encode(newPassword));
        employeRepository.save(employe);

        return ResponseEntity.ok("Password has been reset.");
    }

    @GetMapping("/questions")
    public ResponseEntity<List<String>> getAllSecurityQuestions() {
        List<String> questions = questionRepository.findAll()
                .stream()
                .map(Question::getNom) // or use `.map(q -> q.getText())`
                .collect(Collectors.toList());
        return ResponseEntity.ok(questions);
    }

}

