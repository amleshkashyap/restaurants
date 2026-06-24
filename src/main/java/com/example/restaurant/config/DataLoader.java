package com.example.restaurant.config;

import com.example.restaurant.model.Role;
import com.example.restaurant.model.User;
import com.example.restaurant.repository.RoleRepository;
import com.example.restaurant.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

@Component
public class DataLoader implements CommandLineRunner {
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (roleRepository.countRolesByName("ROLE_ADMIN") == 0) {
            Role role = new Role("ROLE_ADMIN");
            role = roleRepository.save(role);
            User user = new User();
            user.setEmail("admin@gmail.com");
            user.setFirstName("Admin");
            user.setLastName("User");
            user.setPassword(this.passwordEncoder.encode("admin123"));
            user.setRole(role);
            userRepository.save(user);
            System.out.println("Added role admin along with an admin user.");
        }
        if (roleRepository.countRolesByName("ROLE_CUSTOMER") == 0) {
            roleRepository.save(new Role("ROLE_CUSTOMER"));
            System.out.println("Added role customer");
        }
        if (roleRepository.countRolesByName("ROLE_OWNER") == 0) {
            roleRepository.save(new Role("ROLE_OWNER"));
            System.out.println("Added role owner");
        }
    }
}