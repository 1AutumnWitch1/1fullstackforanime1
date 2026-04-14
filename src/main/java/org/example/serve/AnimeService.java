package org.example.serve;

import jakarta.annotation.PostConstruct;
import org.example.model.Anime;
import org.example.model.Comment;
import org.example.model.User;
import org.example.repository.AnimeRepository;
import org.example.repository.CommentRepository;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AnimeService {

    @Autowired
    private AnimeRepository animeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;


    @PostConstruct
    public void initAdmin() {
        if (!userRepository.existsById("Autumn")) {
            User admin = new User("Autumn", "Witch", "ADMIN");
            userRepository.save(admin);
            System.out.println(">>> 管理员 Autumn 已初始化！");
        }
    }


    public List<Anime> getVisibleAnimes(String role) {
        // 按照新的平均分字段降序排列
        Sort sort = Sort.by(Sort.Direction.DESC, "averageLovepoint");

        if ("ADMIN".equalsIgnoreCase(role)) {
            return animeRepository.findAll(sort);
        }

        return animeRepository.findByStatus("APPROVED", sort);
    }
    public void updateCover(Long id, String newCoverUrl, String role) {
        // 1. 权限校验
        if (!"ADMIN".equalsIgnoreCase(role)) {
            throw new RuntimeException("操作失败：只有管理员可以修改封面！");
        }

        // 2. 获取并更新
        Anime anime = animeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("找不到该动漫"));

        anime.setCoverUrl(newCoverUrl);
        animeRepository.save(anime);
    }

    public Anime applyAnime(Anime anime, String username) {
        // 1. 权限校验：如果 username 为空，说明是游客，直接拒绝
        if (username == null || username.isEmpty() || "GUEST".equalsIgnoreCase(username)) {
            throw new RuntimeException("申请失败：请先登录后再提交申请！");
        }

        anime.setStatus("PENDING");
        anime.setApplicant(username);
        anime.setAverageLovepoint(0.0);
        return animeRepository.save(anime);
    }


    @Transactional // 保证评论保存和分数更新在同一个事务中
    public void addComment(Long animeId, Comment comment, String currentUsername) {
        // 1. 权限校验：拦截游客
        if (currentUsername == null || currentUsername.isEmpty()) {
            throw new RuntimeException("操作失败：只有登录用户才能参与评论打分！");
        }

        // 2. 获取动漫
        Anime anime = animeRepository.findById(animeId)
                .orElseThrow(() -> new RuntimeException("找不到该动漫"));

        // 3. 检查状态
        if (!"APPROVED".equalsIgnoreCase(anime.getStatus())) {
            throw new RuntimeException("该动漫未通过审核，无法评论");
        }

        // 4. 设置评论信息
        comment.setAnime(anime);
        comment.setAuthor(currentUsername); // 强制使用当前登录的用户名，防止伪造
        commentRepository.save(comment);

        // 5. 重新计算平均分
        List<Comment> allComments = commentRepository.findByAnimeId(animeId);
        double avg = allComments.stream()
                .mapToInt(Comment::getScore)
                .average()
                .orElse(0.0);

        anime.setAverageLovepoint(avg);
        animeRepository.save(anime);
    }

    /**
     * 管理员：审核通过动漫
     */
    public void approveAnime(Long id) {
        Anime anime = animeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("找不到该申请"));
        anime.setStatus("APPROVED");
        animeRepository.save(anime);
    }

    /**
     * 管理员：删除动漫或拒绝申请
     */
    public void deleteWithAuth(Long id, String role) {
        if (!"ADMIN".equalsIgnoreCase(role)) {
            throw new RuntimeException("只有管理员可以执行此操作！");
        }
        animeRepository.deleteById(id);
    }

    /**
     * 用户注册
     */
    public User registerUser(User user) {
        // 改用 existsByUsername，这是你在 Repository 里明确定义的方法
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("注册失败：用户名「" + user.getUsername() + "」已被占用。");
        }
        user.setRole("USER");
        return userRepository.save(user);
    }

    /**
     * 用户登录
     */
    public User login(String username, String password) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new RuntimeException("登录失败：用户不存在。"));
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("登录失败：密码错误。");
        }
        return user;
    }
}