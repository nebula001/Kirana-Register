package com.example.Kirana_Register.services;

import com.example.Kirana_Register.Exceptions.DuplicateResourceException;
import com.example.Kirana_Register.dto.RegisterRequestDTO;
import com.example.Kirana_Register.dto.RegisterResponseDTO;
import com.example.Kirana_Register.entities.Users;
import com.example.Kirana_Register.repositories.UserRepository;
import org.apache.coyote.BadRequestException;
import org.springframework.dao.DataIntegrityViolationException;
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
        if (requestDTO == null) {
            throw new IllegalArgumentException("Registration request cannot be null");
        }

        if (userRepository.existsByEmail(requestDTO.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }
        if (userRepository.existsByUsername(requestDTO.getUsername())) {
            throw new DuplicateResourceException("Username already exists");
        }

        Users user = new Users();
        user.setEmail(requestDTO.getEmail());
        user.setUsername(requestDTO.getUsername());
        user.setPassword(passwordEncoder.encode(requestDTO.getPassword()));

        try {
            Users savedUser = userRepository.save(user);

            RegisterResponseDTO responseDTO = new RegisterResponseDTO();
            responseDTO.setId(savedUser.getId());
            responseDTO.setEmail(savedUser.getEmail());
            responseDTO.setUsername(savedUser.getUsername());
            responseDTO.setRole(savedUser.getRole());

            return responseDTO;
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateResourceException("Database constraint violation: email already exists");
        }
    }
}
