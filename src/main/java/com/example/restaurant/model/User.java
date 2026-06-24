package com.example.restaurant.model;

import com.example.restaurant.controller.model.UserModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.Serializable;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column(name = "email", unique = true)
    private String email;

    @Column
    @NotNull
    private String password;

    @Column
    private String mobile;

    @JoinColumn(name = "role_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Role role;

    public User(UserModel userModel, Role role, PasswordEncoder passwordEncoder) {
        this.email = userModel.getEmail();
        this.password = passwordEncoder.encode(userModel.getPassword());
        this.firstName = userModel.getFirstName();
        this.lastName = userModel.getLastName();
        this.mobile = userModel.getMobileNumber();
        this.role = role;
    }
}
