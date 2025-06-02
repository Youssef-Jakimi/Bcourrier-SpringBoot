package com.courrier.Bcourrier.config;


import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;


import com.courrier.Bcourrier.Entities.Confidentialité;
import com.courrier.Bcourrier.Entities.Role;
import com.courrier.Bcourrier.Entities.Urgence;
import com.courrier.Bcourrier.Enums.Confidentialite;
import com.courrier.Bcourrier.Repositories.ConfidentialitéRepository;
import com.courrier.Bcourrier.Repositories.RoleRepository;
import com.courrier.Bcourrier.Repositories.UrgenceRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;



@Component
public class EnumDatabaseValidator implements ApplicationRunner {

    private final ConfidentialitéRepository confidentialiteRepository;
    private final RoleRepository roleRepository;
    private final UrgenceRepository urgenceRepository;

    @Autowired
    public EnumDatabaseValidator(
            ConfidentialitéRepository confidentialiteRepository,
            RoleRepository roleRepository,
            UrgenceRepository urgenceRepository
    ) {
        this.confidentialiteRepository = confidentialiteRepository;
        this.roleRepository = roleRepository;
        this.urgenceRepository = urgenceRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        // 1. Vérification de Confidentialite
        Set<String> confFromDb = confidentialiteRepository.findAll().stream()
                .map(Confidentialité::getNom)
                .collect(Collectors.toSet());

        Set<String> confFromEnum = Arrays.stream(Confidentialite.values())
                .map(Enum::name)
                .collect(Collectors.toSet());

        Set<String> missingConfInEnum = confFromDb.stream()
                .filter(dbVal -> !confFromEnum.contains(dbVal))
                .collect(Collectors.toSet());

        Set<String> missingConfInDb = confFromEnum.stream()
                .filter(enumVal -> !confFromDb.contains(enumVal))
                .collect(Collectors.toSet());

        // 2. Vérification de Role
        Set<String> rolesFromDb = roleRepository.findAll().stream()
                .map(Role::getNom)
                .collect(Collectors.toSet());

        Set<String> rolesFromEnum = Arrays.stream(com.courrier.Bcourrier.Enums.Role.values())
                .map(Enum::name)
                .collect(Collectors.toSet());

        Set<String> missingRoleInEnum = rolesFromDb.stream()
                .filter(dbVal -> !rolesFromEnum.contains(dbVal))
                .collect(Collectors.toSet());

        Set<String> missingRoleInDb = rolesFromEnum.stream()
                .filter(enumVal -> !rolesFromDb.contains(enumVal))
                .collect(Collectors.toSet());

        // 3. Vérification de Urgence
        Set<String> urgFromDb = urgenceRepository.findAll().stream()
                .map(Urgence::getNom)
                .collect(Collectors.toSet());

        Set<String> urgFromEnum = Arrays.stream(com.courrier.Bcourrier.Enums.Urgence.values())
                .map(Enum::name)
                .collect(Collectors.toSet());

        Set<String> missingUrgInEnum = urgFromDb.stream()
                .filter(dbVal -> !urgFromEnum.contains(dbVal))
                .collect(Collectors.toSet());

        Set<String> missingUrgInDb = urgFromEnum.stream()
                .filter(enumVal -> !urgFromDb.contains(enumVal))
                .collect(Collectors.toSet());

        // 4. Si incohérence, lever exception et bloquer le démarrage
        if (!missingConfInEnum.isEmpty() || !missingConfInDb.isEmpty()
                || !missingRoleInEnum.isEmpty() || !missingRoleInDb.isEmpty()
                || !missingUrgInEnum.isEmpty() || !missingUrgInDb.isEmpty()) {

            StringBuilder msg = new StringBuilder("Incohérence détectée entre enums et tables en base :\n");

            if (!missingConfInEnum.isEmpty()) {
                msg.append(" - Confidentialite en base sans équivalent enum: ")
                        .append(missingConfInEnum)
                        .append("\n");
            }
            if (!missingConfInDb.isEmpty()) {
                msg.append(" - ConfidentialiteEnum manquants en base: ")
                        .append(missingConfInDb)
                        .append("\n");
            }
            if (!missingRoleInEnum.isEmpty()) {
                msg.append(" - Role en base sans équivalent enum: ")
                        .append(missingRoleInEnum)
                        .append("\n");
            }
            if (!missingRoleInDb.isEmpty()) {
                msg.append(" - RoleEnum manquants en base: ")
                        .append(missingRoleInDb)
                        .append("\n");
            }
            if (!missingUrgInEnum.isEmpty()) {
                msg.append(" - Urgence en base sans équivalent enum: ")
                        .append(missingUrgInEnum)
                        .append("\n");
            }
            if (!missingUrgInDb.isEmpty()) {
                msg.append(" - UrgenceEnum manquants en base: ")
                        .append(missingUrgInDb)
                        .append("\n");
            }

            throw new IllegalStateException(msg.toString());
        }

        // Tout est cohérent ; l’application démarre normalement.
    }
}

