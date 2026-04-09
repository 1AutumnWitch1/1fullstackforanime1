package org.example.serve;
import jakarta.annotation.PostConstruct;
import org.example.model.Anime;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.repository.AnimeRepository;
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

    public List<Anime> getAll() {
        // 目的：调用仓库的 findAll，并传入排序参数（按 lovepoint 倒序）
        // 这一行代替了你原来的 Stream 排序
        return animeRepository.findAll(Sort.by(Sort.Direction.DESC, "lovepoint"));
    }
    public void update(Anime updatedAnime) {
        // 在实际项目中，通常建议在更新前先检查是否存在
        if (updatedAnime.getId() != null && animeRepository.existsById(updatedAnime.getId())) {
            // save 方法会自动执行更新操作
            animeRepository.save(updatedAnime);
        } else {
            // 这里可以抛出异常，告知找不到该动漫
            throw new RuntimeException("更新失败：ID 为 " + updatedAnime.getId() + " 的动漫不存在。");
        }
    }
    public void add(Anime anime) {
        // 1. 先去数据库查一下有没有同名的
        if (animeRepository.existsByTitle(anime.getTitle())) {
            throw new RuntimeException("添加失败：动漫《" + anime.getTitle() + "》已在库中。");
        }
        // 2. 确定没有同名的，再执行保存
        animeRepository.save(anime);
    }
    public void delete(Long id) {
        // 目的：根据主键 ID 从数据库中移除记录
        animeRepository.deleteById(id);
    }

}