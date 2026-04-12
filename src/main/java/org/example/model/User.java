package org.example.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table; // 记得导入这个

@Entity
@Table(name = "app_users") // 关键：把数据库表名改为 app_users，避开关键字 USER
public class User {

    @Id
    private String username;
    private String password;
    private String role;

    public User(){}

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // 务必检查 Setter 是否有赋值语句！
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}