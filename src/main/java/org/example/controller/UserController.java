package org.example.controller;

import org.example.model.User;
import org.example.serve.AnimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users") // 对应前端的 const userApi = ".../users"
@CrossOrigin
public class UserController {

    @Autowired
    private AnimeService animeService;

    @PostMapping("/register") // 对应 handleRegister 里的 axios.post
    public String register(@RequestBody User user) {
        try {
            animeService.registerUser(user);
            return "注册成功！";
        } catch (RuntimeException e) {
            return e.getMessage();
        }
    }

    @PostMapping("/login") // 对应 handleLogin 里的 axios.post
    public User login(@RequestBody User loginReq) {
        return animeService.login(loginReq.getUsername(), loginReq.getPassword());
    }
}