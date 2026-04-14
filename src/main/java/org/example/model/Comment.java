package org.example.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
@Entity
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;  // 评论内容

    private String author;   // 评论者用户名

    private int score;       // 该用户打的好感度分数 (例如 1-10 分)

   // 评论时间

    // --- 核心关联 ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "anime_id") // 在数据库中创建名为 anime_id 的外键列
    @JsonIgnore // 防止转 JSON 时出现无限递归死循环
    private Anime anime;

    // JPA 必须的无参构造函数
    public Comment() {

    }

    // 方便使用的有参构造函数
    public Comment(String content, String author, int score, Anime anime) {
        this.content = content;
        this.author = author;
        this.score = score;
        this.anime = anime;

    }

    // --- Getter 和 Setter ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }


    public Anime getAnime() { return anime; }
    public void setAnime(Anime anime) { this.anime = anime; }
}