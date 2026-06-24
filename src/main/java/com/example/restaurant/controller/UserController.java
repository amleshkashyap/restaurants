package com.example.restaurant.controller;

import com.example.restaurant.controller.dto.UserDTO;
import com.example.restaurant.controller.model.LoginModel;
import com.example.restaurant.controller.model.UserModel;
import com.example.restaurant.model.User;
import com.example.restaurant.repository.UserRepository;
import com.example.restaurant.service.SecurityService;
import com.example.restaurant.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;
    private final SecurityService securityService;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public UserController(
            UserService userService,
            UserRepository userRepository,
            SecurityService securityService,
            AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.securityService = securityService;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<HashMap<String, String>> login(@Valid @RequestBody LoginModel loginModel) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginModel.getUsername(),
                loginModel.getPassword()
        );

        Authentication authentication = this.authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = this.securityService.createToken(authentication);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(jwt);
        HashMap<String, String> response = new HashMap<>();
        response.put("token", jwt);
        return new ResponseEntity<>(response, httpHeaders, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Iterable<UserDTO>> getUsers() {
        Iterable<User> u = this.userRepository.findAll();
        List<UserDTO> users = new ArrayList<>();
        for (User user: u) {
            users.add(new UserDTO(user));
        }
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@ModelAttribute("currentUser") User currentUser, @PathVariable Long id) {
        if (!Objects.equals(currentUser.getId(), id) && !currentUser.getRole().getName().equals("ROLE_ADMIN")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        User user = this.userRepository.findById(id).orElse(null);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(new UserDTO(user));
    }

    @PostMapping()
    public ResponseEntity<UserDTO> createUser(@RequestBody UserModel userModel) {
        User user = this.userService.createUser(userModel);
        return ResponseEntity.ok(new UserDTO(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserModel userModel) {
        User user = this.userService.updateUser(id, userModel);
        return ResponseEntity.ok(new UserDTO(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HashMap<String, String>> deleteUser(@PathVariable Long id) {
        User storedUser = this.userRepository.findById(id).orElse(null);
        HashMap<String, String> response = new HashMap<>();
        if (storedUser != null) {
            this.userRepository.delete(storedUser);
            response.put("message", "Successfully Deleted User.");
            return ResponseEntity.ok(response);
        }
        response.put("message", "Couldn't Find User.");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
