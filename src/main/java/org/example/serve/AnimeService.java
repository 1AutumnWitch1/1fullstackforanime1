package org.example.serve;
import jakarta.annotation.PostConstruct;
import org.example.model.Anime;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.User;
import org.example.repository.AnimeRepository;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.example.controller.AnimeController;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnimeService {
    @Autowired // 目的：自动注入仓库实例，不需要你 new
    private AnimeRepository animeRepository;
    @Autowired
    private UserRepository userRepository;

    @PostConstruct
    public void initAdmin() {
        // 因为 username 是主键，我们用 existsById 检查
        if (!userRepository.existsById("Autumn")) {
            User admin = new User("Autumn", "Witch", "ADMIN");
            userRepository.save(admin);
            System.out.println(">>> 管理员 Autumn 已初始化！");
        }
    }

    public List<Anime> getVisibleAnimes(String role) {
        Sort sort = Sort.by(Sort.Direction.DESC, "lovepoint");

        // 如果是管理员，看到所有（包括 PENDING 状态的申请）
        if ("ADMIN".equalsIgnoreCase(role)) {
            return animeRepository.findAll(sort);
        }

        // 游客（role为null）或普通用户，只能看到 APPROVED 状态的动漫
        // 这行代码需要 AnimeRepository 中有 findByStatus 方法支持
        return animeRepository.findByStatus("APPROVED", sort);
    }

    public Anime applyAnime(Anime anime, String username) {
        anime.setStatus("PENDING"); // 强制设为申请中
        anime.setApplicant(username); // 记录申请人
        return animeRepository.save(anime);
    }

    public void approveAnime(Long id) {
        Anime anime = animeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("找不到该申请"));
        anime.setStatus("APPROVED"); // 变更状态为已通过
        animeRepository.save(anime);
    }

    public void deleteWithAuth(Long id, String role) {
        if (!"ADMIN".equalsIgnoreCase(role)) {
            throw new RuntimeException("只有管理员可以删除或拒绝动漫！");
        }
        animeRepository.deleteById(id);
    }

    /**
     * 处理用户注册逻辑
     * @param user 包含前端传来的用户名和密码
     * @return 保存后的用户对象
     */
    public User registerUser(User user) {
        // 1. 安全校验：检查用户名是否已存在
        // 这里使用 userRepository 的 existsById (因为 username 是主键 @Id)
        if (userRepository.existsById(user.getUsername())) {
            throw new RuntimeException("注册失败：用户名「" + user.getUsername() + "」已被占用。");
        }

        // 2. 权限锁定：强制设置为普通用户角色
        // 防止黑客通过前端接口直接伪造 "ADMIN" 身份注册
        user.setRole("USER");

        // 3. 执行保存
        return userRepository.save(user);
    }

    /**
     * 处理登录验证逻辑
     * @param username 用户名
     * @param password 密码
     * @return 验证通过的用户对象
     */
    public User login(String username, String password) {
        // 1. 查找用户
        User user = userRepository.findById(username)
                .orElseThrow(() -> new RuntimeException("登录失败：用户不存在。"));

        // 2. 验证密码 (注：实际项目建议使用 BCrypt 加密，这里先用明文对比)
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("登录失败：密码错误。");
        }

        return user;
    }

}