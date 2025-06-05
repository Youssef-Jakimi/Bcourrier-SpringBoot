package com.courrier.Bcourrier.Controllers;

import com.courrier.Bcourrier.DTO.LoginRequest;
import com.courrier.Bcourrier.DTO.RegisterRequest;
import com.courrier.Bcourrier.DTO.SecurityQuestionDTO;
import com.courrier.Bcourrier.Entities.Employe;
import com.courrier.Bcourrier.Enums.Questions;
import com.courrier.Bcourrier.Repositories.EmployeRepository;
import com.courrier.Bcourrier.Services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RequestMapping("/")
@RestController
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private AuthService authService;
    private final EmployeRepository employeRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        authService.registerUser(request);
        return ResponseEntity.ok("Check your email for verification link");
    }

    @GetMapping("/register")
    public String showRegistrationPage() {
        return "redirect:/signup.html";
    }



    @PostMapping("/verify")
    public ResponseEntity<?> setSecurityQuestions(
            @RequestParam String token,
            @RequestBody SecurityQuestionDTO dto
    ) {
        return authService.saveSecurityQuestions(token, dto);
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyEmailAndGetQuestions(@RequestParam("token") String token) {
        Optional<Employe> userOpt = employeRepository.findByVerificationToken(token);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid token");
        }

        Employe employe = userOpt.get();
        employe.setCheckEmail(true);
        employeRepository.save(employe);

        List<String> questions = Arrays.stream(Questions.values())
                .map(Enum::name)
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Email verified successfully! Please fill out the security questions form.");
        response.put("questions", questions);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/login")
    public String showLoginPage() {
        return "redirect:/login.html"; // Or return a template name if using Thymeleaf
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return authService.authenticateUser(request);
    }


}
