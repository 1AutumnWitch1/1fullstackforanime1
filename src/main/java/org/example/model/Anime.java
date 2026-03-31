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

    public Anime() {}


    public Anime(Long id, String title, String description,int Lovepoint) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.lovepoint = Lovepoint;
    }
    public int getLovepoint() { return lovepoint; }
    public void setLovepoint(int Lo) { this.lovepoint = Lo; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}

