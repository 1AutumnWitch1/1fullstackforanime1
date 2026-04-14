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
    private String coverUrl;
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
    public Anime(String title, String description, String coverUrl) {
        this.title = title;
        this.description = description;
        this.coverUrl = coverUrl; // 赋值封面
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

    public String getCoverUrl() {
        if (this.coverUrl == null || this.coverUrl.isEmpty()) {
            // 返回上面的 Base64 字符串
            return "data:image/svg+xml;charset=UTF-8,%3Csvg width='800' height='450' viewBox='0 0 800 450' xmlns='http://www.w3.org/2000/svg'%3E%3Cdefs%3E%3ClinearGradient id='g' x1='0%25' y1='0%25' x2='100%25' y2='100%25'%3E%3Cstop offset='0%25' style='stop-color:%231a1a1a;stop-opacity:1' /%3E%3Cstop offset='100%25' style='stop-color:%230f0f0f;stop-opacity:1' /%3E%3C/linearGradient%3E%3C/defs%3E%3Crect width='800' height='450' fill='url(%23g)' /%3E%3Ctext x='50%25' y='50%25' dominant-baseline='middle' text-anchor='middle' font-family='sans-serif' font-size='80' fill='%23333'%3E\uD83D\uDCFA%3C/text%3E%3Ctext x='50%25' y='65%25' dominant-baseline='middle' text-anchor='middle' font-family='sans-serif' font-size='20' fill='%23444'%3EAnimeHub Alpha%3C/text%3E%3C/svg%3E";
        }
        return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }
}