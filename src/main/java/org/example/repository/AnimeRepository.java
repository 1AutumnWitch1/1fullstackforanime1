package org.example.repository;

import org.example.model.Anime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // 目的：标记为数据访问层组件
public interface AnimeRepository extends JpaRepository<Anime, Long> {
    // 继承 JpaRepository 后，你自动拥有了 save(), findAll(), deleteById() 等所有功能
}