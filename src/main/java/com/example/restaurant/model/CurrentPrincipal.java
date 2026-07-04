package com.example.restaurant.model;

import lombok.Data;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@Data
public class CurrentPrincipal implements UserDetails {
    private Long id;
    private String name;
    private String email;
    private String password;
    private List<SimpleGrantedAuthority> roles;

    public CurrentPrincipal(Long id, String firstName, String lastName, String email, String password, String roleName) {
        this.id = id;
        this.name = firstName + " " + lastName;
        this.email = email;
        this.password = password;
        this.roles = List.of(new SimpleGrantedAuthority(roleName));
    }

    public String getRole() {
        return this.roles.getFirst().toString();
    }

    @Override
    public List<SimpleGrantedAuthority> getAuthorities() {
        return this.roles;
    }

    @Override
    public String getUsername() {
        return this.email;
    }
}