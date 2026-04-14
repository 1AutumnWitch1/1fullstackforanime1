package org.example.repository;

import org.example.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 自动按动漫 ID 查找所有评论，方便你在打开动漫详情页时调用
    List<Comment> findByAnimeId(Long animeId);

    // 自动按作者名查找评论
    List<Comment> findByAuthor(String author);
}