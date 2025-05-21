package com.courrier.Bcourrier.Controllers;

import com.courrier.Bcourrier.DTO.LoginRequest;
import com.courrier.Bcourrier.DTO.RegisterRequest;
import com.courrier.Bcourrier.DTO.SecurityQuestionDTO;
import com.courrier.Bcourrier.Services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/")
@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

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
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        return authService.verifyEmail(token);
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
