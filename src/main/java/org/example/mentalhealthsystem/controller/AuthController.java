package org.example.mentalhealthsystem.controller;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.example.mentalhealthsystem.dto.AuthResponse;
import org.example.mentalhealthsystem.dto.LoginRequest;
import org.example.mentalhealthsystem.dto.RegisterRequest;
import org.example.mentalhealthsystem.entity.User;
import org.example.mentalhealthsystem.repository.UserRepository;
import org.example.mentalhealthsystem.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "http://localhost:5174")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body("用户名已存在");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("邮箱已被注册");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname() != null ? request.getNickname() : request.getUsername());

        userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String token = jwtUtil.generateToken(userDetails);

        AuthResponse response = new AuthResponse(
                token,
                user.getUsername(),
                user.getRole().name(),
                user.getNickname(),
                user.getAvatar()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body("用户名或密码错误");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow();

        String token = jwtUtil.generateToken(userDetails);

        AuthResponse response = new AuthResponse(
                token,
                user.getUsername(),
                user.getRole().name(),
                user.getNickname(),
                user.getAvatar()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/info")
    public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        AuthResponse response = new AuthResponse(
                null,
                user.getUsername(),
                user.getRole().name(),
                user.getNickname(),
                user.getAvatar()
        );
        return ResponseEntity.ok(response);
    }
}