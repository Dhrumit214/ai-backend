package aifriend.ai_backend.service;

import aifriend.ai_backend.model.Admin;
import aifriend.ai_backend.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class AdminInitializationService {

    @Autowired
    private AdminRepository adminRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Value("${admin.default.email:admin@aifriend.com}")
    private String defaultAdminEmail;
    
    @Value("${admin.default.password:admin123}")
    private String defaultAdminPassword;
    
    @Value("${admin.default.firstName:Admin}")
    private String defaultAdminFirstName;
    
    @Value("${admin.default.lastName:User}")
    private String defaultAdminLastName;
    
    /**
     * Initialize the default admin user if no admin exists
     */
    @PostConstruct
    public void initializeDefaultAdmin() {
        if (adminRepository.count() == 0) {
            Admin admin = new Admin();
            admin.setEmail(defaultAdminEmail);
            admin.setPasswordHash(passwordEncoder.encode(defaultAdminPassword));
            admin.setFirstName(defaultAdminFirstName);
            admin.setLastName(defaultAdminLastName);
            admin.setRole("SUPER_ADMIN");
            admin.setIsActive(true);
            
            adminRepository.save(admin);
            
            System.out.println("Default admin user created with email: " + defaultAdminEmail);
        }
    }
}
