package com.example.Kirana_Register.services;

import com.example.Kirana_Register.configs.JwtTokenProvider;
import com.example.Kirana_Register.dto.LoginRequestDTO;
import com.example.Kirana_Register.dto.LoginResponseDTO;
import com.example.Kirana_Register.security.CustomUserDetails;
import org.apache.coyote.BadRequestException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public LoginResponseDTO login(LoginRequestDTO requestDTO) {
        if (requestDTO == null) {
            throw new IllegalArgumentException("Login request cannot be null");
        }
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        requestDTO.getEmail(),
                        requestDTO.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String jwt = jwtTokenProvider.generateToken(userDetails);

        LoginResponseDTO responseDTO = new LoginResponseDTO();
        responseDTO.setMessage("Login successful");
        responseDTO.setToken(jwt);
        return responseDTO;

    }

    public String logout() {
        SecurityContextHolder.clearContext();
        return "Logout successful";
    }
}
