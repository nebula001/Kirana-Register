package com.example.Kirana_Register.services;

import com.example.Kirana_Register.dto.RegisterRequestDTO;
import com.example.Kirana_Register.dto.RegisterResponseDTO;
import com.example.Kirana_Register.entities.Users;
import com.example.Kirana_Register.repositories.UserRepository;
import org.apache.coyote.BadRequestException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public RegisterResponseDTO registerUser(RegisterRequestDTO requestDTO) {
        if (userRepository.existsByEmail(requestDTO.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        if (userRepository.existsByUsername(requestDTO.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        Users user = new Users();
        user.setEmail(requestDTO.getEmail());
        user.setUsername(requestDTO.getUsername());
        user.setPassword(passwordEncoder.encode(requestDTO.getPassword()));

        Users savedUser = userRepository.save(user);

        RegisterResponseDTO responseDTO = new RegisterResponseDTO();
        responseDTO.setId(savedUser.getId());
        responseDTO.setEmail(savedUser.getEmail());
        responseDTO.setUsername(savedUser.getUsername());
        responseDTO.setRole(savedUser.getRole());

        return responseDTO;
    }
}
