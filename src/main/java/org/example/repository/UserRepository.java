package org.example.repository;

import org.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    // 1. 根据用户名查找用户（登录时使用）
    // 返回 Optional 是为了防止用户不存在时报空指针异常，这很 Java 风格
    Optional<User> findByUsername(String username);

    // 2. 判断用户名是否已存在（注册时使用）
    boolean existsByUsername(String username);
}