package org.example.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Entity
public class Anime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title; // 动漫名称
    private String description; // 简介
    private int lovepoint;
    private String status; // 取值范围："PENDING" (申请中), "APPROVED" (已通过), "REJECTED" (已拒绝)
    private String applicant; // 记录是谁申请的
    public Anime() {}


    public Anime(Long id, String title, String description,int Lovepoint) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.lovepoint = Lovepoint;
        this.status = "";
        this.applicant = "";
    }
    public int getLovepoint() { return lovepoint; }
    public void setLovepoint(int Lo) { this.lovepoint = Lo; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getApplicant() { return applicant; }
    public void setApplicant(String applicant) { this.applicant = applicant; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

}

