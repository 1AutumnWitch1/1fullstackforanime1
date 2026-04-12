package org.example.model;

import jakarta.persistence.Entity;
import org.springframework.data.annotation.Id;

@Entity
public class User {


    @jakarta.persistence.Id
    private Long id;
    @Id
    private String username; // 用户名作为主键
    private String password;
    private String role; // "USER" 或 "ADMIN"
    public User(){}

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {}

    public String getRole() {
        return role;
    }
    public void setRole(String role) {}

}
