package com.example.Kirana_Register.services;

import com.example.Kirana_Register.Exceptions.DuplicateResourceException;
import com.example.Kirana_Register.dto.RegisterRequestDTO;
import com.example.Kirana_Register.dto.RegisterResponseDTO;
import com.example.Kirana_Register.entities.Users;
import com.example.Kirana_Register.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public RegisterResponseDTO registerUser(RegisterRequestDTO requestDTO) {
        if (requestDTO == null) {
            log.info("Empty request body provided");
            throw new IllegalArgumentException("Registration request cannot be null");
        }

        if (userRepository.existsByEmail(requestDTO.getEmail())) {
            log.info("Duplicate email provided");
            throw new DuplicateResourceException("Email already exists");
        }
        if (userRepository.existsByUsername(requestDTO.getUsername())) {
            log.info("Duplicate user name provided");
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
            log.warn("Invalid user data provided");
            throw new DuplicateResourceException("Database constraint violation: email already exists");
        }
    }
}
