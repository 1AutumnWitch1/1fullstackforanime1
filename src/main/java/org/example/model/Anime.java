package org.example.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Anime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT") // 简介可能很长，用 TEXT 类型
    private String description;

    private String status;
    private String applicant;

    // --- 核心改动 1：不再存单一的 lovepoint，而是存平均分 ---
    private double averageLovepoint;

    // --- 核心改动 2：一对多关联评论表 ---
    // mappedBy 指向 Comment 类中的 anime 字段
    // cascade = ALL 表示删除动漫时，对应的评论也一起删除
    @OneToMany(mappedBy = "anime", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();

    public Anime() {}

    // 构造函数也需要更新，去掉不合理的初始值赋值
    public Anime(String title, String description) {
        this.title = title;
        this.description = description;
        this.status = "PENDING";
        this.averageLovepoint = 0.0;
    }

    // Getter 和 Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getApplicant() { return applicant; }
    public void setApplicant(String applicant) { this.applicant = applicant; }

    public double getAverageLovepoint() { return averageLovepoint; }
    public void setAverageLovepoint(double avg) { this.averageLovepoint = avg; }

    public List<Comment> getComments() { return comments; }
    public void setComments(List<Comment> comments) { this.comments = comments; }
}