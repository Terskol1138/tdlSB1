package org.example.tdlsb.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String token;      // JWT токен
    private String type;       // "Bearer"
    private String username;
    private String userId;     // опционально
}