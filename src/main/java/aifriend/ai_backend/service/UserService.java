package aifriend.ai_backend.service;

import aifriend.ai_backend.dto.AuthResponse;
import aifriend.ai_backend.dto.LoginRequest;
import aifriend.ai_backend.dto.RegisterRequest;
import aifriend.ai_backend.model.User;
import aifriend.ai_backend.model.PlanType;
import aifriend.ai_backend.model.UserPreferences;
import aifriend.ai_backend.model.MessageFrequency;
import aifriend.ai_backend.repository.UserPreferencesRepository;
import aifriend.ai_backend.repository.UserRepository;
import aifriend.ai_backend.util.JwtUtil;
import aifriend.ai_backend.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserPreferencesRepository userPreferencesRepository;
    
    @Autowired
    private SecurityUtils securityUtils;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * Create a new user with secure phone number handling
     */
    @Transactional
    public User createUser(String email, String phoneNumber, String firstName, String lastName) {
        User user = new User();
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPlanType(PlanType.FREE); // Default to free plan
        
        // Set phone number securely
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            user.setPhoneNumberSecure(phoneNumber, securityUtils);
        }
        
        return userRepository.save(user);
    }
    
    /**
     * Find a user by their phone number
     */
    public Optional<User> findByPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return Optional.empty();
        }
        
        // Generate the hash of the phone number to look up
        String phoneHash = securityUtils.secureHash(phoneNumber);
        User user = userRepository.findByPhoneNumberHash(phoneHash);
        
        if (user != null) {
            // Set the transient plain phone number for use in the service
            user.setPhoneNumber(phoneNumber);
        }
        
        return Optional.ofNullable(user);
    }
    
    /**
     * Change a user's phone number
     */
    @Transactional
    public User updatePhoneNumber(Long userId, String newPhoneNumber) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        
        user.setPhoneNumberSecure(newPhoneNumber, securityUtils);
        return userRepository.save(user);
    }
    
    /**
     * Update a user's plan type
     */
    @Transactional
    public User updatePlanType(Long userId, PlanType planType) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        
        user.setPlanType(planType);
        return userRepository.save(user);
    }
    
    /**
     * Register a new user
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return new AuthResponse(null, null, null, null, null, "Email already in use");
        }
        
        // Create new user
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPlanType(PlanType.FREE);
        user.setIsActive(true);
        
        // Set phone number securely
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isEmpty()) {
            user.setPhoneNumberSecure(request.getPhoneNumber(), securityUtils);
        }
        
        // Save user
        user = userRepository.save(user);
        
        // Create default user preferences
        UserPreferences preferences = new UserPreferences();
        preferences.setUserId(user.getId());
        preferences.setMessageFrequency(MessageFrequency.DAILY);
        preferences.setDoNotDisturb(false);
        userPreferencesRepository.save(preferences);
        
        // Generate JWT token
        String token = jwtUtil.generateToken(user.getId(), user.getEmail());
        
        return new AuthResponse(
            token,
            user.getId(),
            user.getEmail(),
            user.getFirstName(),
            user.getLastName(),
            "Registration successful"
        );
    }
    
    /**
     * Login a user
     */
    public AuthResponse login(LoginRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        
        if (userOpt.isEmpty()) {
            return new AuthResponse(null, null, null, null, null, "Invalid email or password");
        }
        
        User user = userOpt.get();
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            return new AuthResponse(null, null, null, null, null, "Invalid email or password");
        }
        
        // Generate JWT token
        String token = jwtUtil.generateToken(user.getId(), user.getEmail());
        
        return new AuthResponse(
            token,
            user.getId(),
            user.getEmail(),
            user.getFirstName(),
            user.getLastName(),
            "Login successful"
        );
    }
    
    /**
     * Get user by ID
     */
    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }
    
    /**
     * Get user by email
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
}
