package chat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import chat.config.JwtUtil;
import chat.enitity.Login;
import chat.repository.LoginRepository;

@RestController
@RequestMapping("/rooms")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class LoginController {
    @Autowired
    private LoginRepository loginRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                 @RequestBody(required = false) Login login) {
        try {
            // First check if token is provided
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                String username = jwtUtil.extractUsername(token);
                
                if (username != null && jwtUtil.validateToken(token)) {
                    Login user = loginRepository.findByUsername(username);
                    if (user != null) {
                        return ResponseEntity.ok().body(new AuthResponse(token, user));
                    }
                }
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
            }

            // If no token or invalid token, check for username/password login
            if (login == null || login.getUsername() == null || login.getPassword() == null) {
                return ResponseEntity.badRequest().body("Username and password are required");
            }

            Login foundUser = loginRepository.findByUsername(login.getUsername());
            
            if (foundUser != null && passwordEncoder.matches(login.getPassword(), foundUser.getPassword())) {
                String newToken = jwtUtil.generateToken(foundUser.getUsername());
                return ResponseEntity.ok().body(new AuthResponse(newToken, foundUser));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during login");
        }
    }

    @PostMapping("/createUser")
    public ResponseEntity<String> createUser(@RequestBody Login newUser) {
        try {
            if (newUser == null || newUser.getUsername() == null || newUser.getPassword() == null) {
                return ResponseEntity.badRequest().body("Username and password are required");
            }

            if (loginRepository.findByUsername(newUser.getUsername()) != null) {
                return ResponseEntity.badRequest().body("Username already exists");
            }
            
            // Encode the password before saving
            newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
            loginRepository.save(newUser);
            return ResponseEntity.status(HttpStatus.CREATED).body("User created successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating user");
        }
    }
}

class AuthResponse {
    private String token;
    private Login user;

    public AuthResponse(String token, Login user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public Login getUser() { return user; }
    public void setUser(Login user) { this.user = user; }
}
