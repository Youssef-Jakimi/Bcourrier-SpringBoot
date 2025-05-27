package com.courrier.Bcourrier.Services;

import com.courrier.Bcourrier.DTO.Profile.ChangePasswordDTO;
import com.courrier.Bcourrier.DTO.Profile.PersonalInfoDTO;
import com.courrier.Bcourrier.DTO.Profile.PreferencesDTO;
import com.courrier.Bcourrier.Entities.Employe;
import com.courrier.Bcourrier.Repositories.EmployeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfilService {
    @Autowired
    private final EmployeRepository employeRepository;
    @Autowired
    private final PasswordEncoder passwordEncoder;
    public boolean updatePersonalInfo(String login, PersonalInfoDTO dto) {
        Optional<Employe> opt = employeRepository.findByLogin(login);
        if (opt.isEmpty()) return false;
        Employe e = opt.get();
        String[] parts = dto.getFullName().split(" ", 2);
        if (parts.length == 2) { e.setPrenom(parts[0]); e.setNom(parts[1]); }
        e.setEmail(dto.getEmail());
        e.setTelephone(dto.getPhone());
        // Optionally update service if you have logic for it
        employeRepository.save(e);
        return true;
    }

    public boolean changePassword(String login, ChangePasswordDTO dto) {
        Optional<Employe> opt = employeRepository.findByLogin(login);
        if (opt.isEmpty()) return false;
        Employe e = opt.get();
        if (!passwordEncoder.matches(dto.getCurrentPassword(), e.getPassword())) return false;
        e.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        employeRepository.save(e);
        return true;
    }

    public boolean updatePreferences(String login, PreferencesDTO dto) {
        Optional<Employe> opt = employeRepository.findByLogin(login);
        if (opt.isEmpty()) return false;
        Employe e = opt.get();
        e.setEmailNotifications(dto.isEmailNotifications());
        e.setSmsNotifications(dto.isSmsNotifications());
        e.setPushNotifications(dto.isPushNotifications());
        employeRepository.save(e);
        return true;
    }

}
