// src/main/java/com/courrier/Bcourrier/DTO/Profile/ChangePasswordDTO.java
package com.courrier.Bcourrier.DTO.Profile;
import lombok.Data;

@Data
public class ChangePasswordDTO {
    private Long userId;
    private String currentPassword;
    private String newPassword;
}
