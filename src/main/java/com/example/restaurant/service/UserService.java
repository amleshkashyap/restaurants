package com.example.restaurant.service;

import com.example.restaurant.controller.model.UserModel;
import com.example.restaurant.model.Role;
import com.example.restaurant.model.User;
import com.example.restaurant.repository.RoleRepository;
import com.example.restaurant.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(UserModel userModel) {
        Role role = this.roleRepository.findByName(userModel.getRoleName()).orElse(null);
        if (role == null) {
            return null;
        }
        User user = new User(userModel, role, this.passwordEncoder);
        return userRepository.save(user);
    }

    public User updateUser(Long id, UserModel userModel) {
        User user = this.userRepository.findById(id).orElse(null);
        if (user == null) {
            return null;
        }
        // only first name, last name and mobile number change is allowed
        user.setFirstName(userModel.getFirstName());
        user.setLastName(userModel.getLastName());
        user.setMobile(userModel.getMobileNumber());
        return userRepository.save(user);
    }
}
