package org.example.repository;

import org.example.model.Anime;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository // 目的：标记为数据访问层组件
public interface AnimeRepository extends JpaRepository<Anime, Long> {
    List<Anime> findByStatus(String status, Sort sort);
    boolean existsByTitle(String title);
    // 继承 JpaRepository 后，你自动拥有了 save(), findAll(), deleteById() 等所有功能
}