package aifriend.ai_backend.service;

import aifriend.ai_backend.dto.AuthResponse;
import aifriend.ai_backend.dto.LoginRequest;
import aifriend.ai_backend.model.Admin;
import aifriend.ai_backend.model.BillingRecord;
import aifriend.ai_backend.model.User;
import aifriend.ai_backend.repository.AdminRepository;
import aifriend.ai_backend.repository.BillingRecordRepository;
import aifriend.ai_backend.repository.MessageRepository;
import aifriend.ai_backend.repository.UserRepository;
import aifriend.ai_backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private BillingRecordRepository billingRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * Create a new admin user
     */
    @Transactional
    public Admin createAdmin(String email, String password, String firstName, String lastName, String role) {
        // Check if email already exists
        if (adminRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already in use");
        }
        
        Admin admin = new Admin();
        admin.setEmail(email);
        admin.setPasswordHash(passwordEncoder.encode(password));
        admin.setFirstName(firstName);
        admin.setLastName(lastName);
        admin.setRole(role);
        admin.setIsActive(true);
        
        return adminRepository.save(admin);
    }
    
    /**
     * Admin login
     */
    public AuthResponse login(LoginRequest request) {
        Optional<Admin> adminOpt = adminRepository.findByEmail(request.getEmail());
        
        if (adminOpt.isEmpty()) {
            return new AuthResponse(null, null, null, null, null, "Invalid email or password");
        }
        
        Admin admin = adminOpt.get();
        
        if (!passwordEncoder.matches(request.getPassword(), admin.getPasswordHash())) {
            return new AuthResponse(null, null, null, null, null, "Invalid email or password");
        }
        
        // Generate JWT token with admin role
        String token = jwtUtil.generateToken(admin.getId(), admin.getEmail());
        
        return new AuthResponse(
            token,
            admin.getId(),
            admin.getEmail(),
            admin.getFirstName(),
            admin.getLastName(),
            "Login successful"
        );
    }
    
    /**
     * Get dashboard statistics
     */
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // User stats
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByIsActiveTrue();
        
        // Message stats
        long totalMessages = messageRepository.count();
        LocalDateTime lastDay = LocalDateTime.now().minusDays(1);
        long messagesLastDay = messageRepository.countByCreatedAtAfter(lastDay);
        
        // Revenue stats
        BigDecimal totalRevenue = billingRepository.sumAmountDueByIsPaidTrue().orElse(BigDecimal.ZERO);
        String currentMonth = YearMonth.now().toString();
        BigDecimal currentMonthRevenue = billingRepository.sumAmountDueByBillingPeriodAndIsPaidTrue(currentMonth)
            .orElse(BigDecimal.ZERO);
        
        // Compile stats
        stats.put("totalUsers", totalUsers);
        stats.put("activeUsers", activeUsers);
        stats.put("totalMessages", totalMessages);
        stats.put("messagesLastDay", messagesLastDay);
        stats.put("totalRevenue", totalRevenue);
        stats.put("currentMonthRevenue", currentMonthRevenue);
        
        return stats;
    }
    
    /**
     * Get all users
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    /**
     * Get user by ID
     */
    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }
    
    /**
     * Update user status (active/inactive)
     */
    @Transactional
    public User updateUserStatus(Long userId, boolean isActive) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setIsActive(isActive);
        return userRepository.save(user);
    }
    
    /**
     * Get billing records for a specific period
     */
    public List<BillingRecord> getBillingRecordsByPeriod(String period) {
        return billingRepository.findByBillingPeriod(period);
    }
    
    /**
     * Get all billing records
     */
    public List<BillingRecord> getAllBillingRecords() {
        return billingRepository.findAll();
    }
}
