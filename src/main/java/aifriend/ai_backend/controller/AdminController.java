package aifriend.ai_backend.controller;

import aifriend.ai_backend.dto.AuthResponse;
import aifriend.ai_backend.dto.LoginRequest;
import aifriend.ai_backend.model.Admin;
import aifriend.ai_backend.model.BillingRecord;
import aifriend.ai_backend.model.User;
import aifriend.ai_backend.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * Admin login
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse response = adminService.login(request);
        
        if (response.getToken() == null) {
            return ResponseEntity.badRequest().body(response);
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get dashboard statistics
     */
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    /**
     * Get all users
     */
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    /**
     * Get user by ID
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Long userId) {
        User user = adminService.getUserById(userId);
        
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(user);
    }

    /**
     * Update user status (active/inactive)
     */
    @PutMapping("/users/{userId}/status")
    public ResponseEntity<User> updateUserStatus(
            @PathVariable Long userId,
            @RequestParam boolean isActive) {
        try {
            User user = adminService.updateUserStatus(userId, isActive);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get billing records for a specific period
     */
    @GetMapping("/billing/{period}")
    public ResponseEntity<List<BillingRecord>> getBillingRecordsByPeriod(@PathVariable String period) {
        return ResponseEntity.ok(adminService.getBillingRecordsByPeriod(period));
    }

    /**
     * Get all billing records
     */
    @GetMapping("/billing")
    public ResponseEntity<List<BillingRecord>> getAllBillingRecords() {
        return ResponseEntity.ok(adminService.getAllBillingRecords());
    }

    /**
     * Create a new admin user (only accessible by super admins)
     */
    @PostMapping("/create")
    public ResponseEntity<?> createAdmin(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String role) {
        try {
            Admin admin = adminService.createAdmin(email, password, firstName, lastName, role);
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", admin.getId());
            response.put("email", admin.getEmail());
            response.put("firstName", admin.getFirstName());
            response.put("lastName", admin.getLastName());
            response.put("role", admin.getRole());
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
