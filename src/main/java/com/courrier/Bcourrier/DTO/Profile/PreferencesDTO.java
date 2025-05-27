// src/main/java/com/courrier/Bcourrier/DTO/Profile/PreferencesDTO.java
package com.courrier.Bcourrier.DTO.Profile;
import lombok.Data;

@Data
public class PreferencesDTO {
    private boolean emailNotifications;
    private boolean smsNotifications;
    private boolean pushNotifications;
}
