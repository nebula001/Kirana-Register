package com.example.Kirana_Register.dto;

import com.example.Kirana_Register.entities.Role;
import lombok.Data;

@Data
public class RegisterResponseDTO {
    private Long id;
    private String email;
    private String username;
    private Role role;
}
