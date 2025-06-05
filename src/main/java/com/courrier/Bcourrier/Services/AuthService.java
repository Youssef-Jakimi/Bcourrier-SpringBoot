package com.courrier.Bcourrier.Services;

import com.courrier.Bcourrier.DTO.LoginRequest;
import com.courrier.Bcourrier.DTO.RegisterRequest;
import com.courrier.Bcourrier.DTO.SecurityQuestionDTO;
import com.courrier.Bcourrier.Entities.Employe;
import com.courrier.Bcourrier.Repositories.EmployeRepository;
import com.courrier.Bcourrier.config.JwtUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
@Getter
@Setter
@Service
public class AuthService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private EmployeRepository employeRepository;

    public void registerUser(RegisterRequest request) {
        Employe employe = new Employe();
        employe.setEmail(request.getEmail());
        employe.setLogin(request.getLogin());
        employe.setCin(request.getCIN());
        employe.setDateNaissance(request.getDateNaissance());
        employe.setMatricule(request.getMatricule());
        employe.setNom(request.getNom());
        employe.setPrenom(request.getPrenom());
        employe.setTelephone(request.getTelephone());
        employe.setCheckEmail(false);
        employe.setActive(false);

        String token = UUID.randomUUID().toString();
        employe.setVerificationToken(token);
        employeRepository.save(employe);

        String verificationLink = "http://localhost:4200/verify?token=" + token;
        emailService.sendVerificationEmail(employe.getEmail(), verificationLink);
    }

    public ResponseEntity<?> verifyEmail(String token) {
        Optional<Employe> userOpt = employeRepository.findByVerificationToken(token);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid token");
        }

        Employe employe = userOpt.get();
        employe.setCheckEmail(true);
        employeRepository.save(employe);

        return ResponseEntity.ok("Email verified successfully! Please fill out the security questions form.");
    }

    public ResponseEntity<?> saveSecurityQuestions(String token, SecurityQuestionDTO dto) {
        Optional<Employe> userOpt = employeRepository.findByVerificationToken(token);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid token");
        }

        Employe employe = userOpt.get();
        employe.setPassword(passwordEncoder.encode(dto.getPassword()));
        employe.setQ1(dto.getQuestion1());
        employe.setR1(dto.getAnswer1());
        employe.setQ2(dto.getQuestion2());
        employe.setR2(dto.getAnswer2());
        employe.setQ3(dto.getQuestion3());
        employe.setR3(dto.getAnswer3());
        employe.setVerificationToken(null);
        employeRepository.save(employe);

        return ResponseEntity.ok("Security questions saved successfully!");
    }


    public ResponseEntity<?> authenticateUser(LoginRequest request) {
        Optional<Employe> userOpt = employeRepository.findByLogin(request.getLogin());

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid login or password");
        }

        Employe employe = userOpt.get();

        if (!employe.isCheckEmail()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Account not active. Please verify your email.");
        }

        if (!passwordEncoder.matches(request.getPassword(), employe.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid login or password");
        }

        String token = JwtUtil.generateToken(employe.getLogin());

        return ResponseEntity.ok(Map.of(
                "token", token,
                "role", employe.getRole()
        ));
    }

}
