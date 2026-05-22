package ua.edu.university.lab10.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ua.edu.university.lab10.dto.*;
import ua.edu.university.lab10.model.*;
import ua.edu.university.lab10.repository.UserRepository;
import ua.edu.university.lab10.security.JwtTokenProvider;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email уже зарегистрирован!"));
        }

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword())); // Хешируем
        user.setRole(Role.valueOf(dto.getRole().toUpperCase()));

        userRepository.save(user);
        return new ResponseEntity<>(Map.of("message", "Пользователь успешно создан"), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Неверный email или пароль"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Неверный email или пароль"));
        }

        String token = jwtTokenProvider.createToken(user.getEmail(), user.getRole().name());
        return ResponseEntity.ok(new AuthResponseDto(token));
    }
}