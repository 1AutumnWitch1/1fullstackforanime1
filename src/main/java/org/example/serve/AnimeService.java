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

    public void add(Anime anime) {
        // 目的：直接存入数据库
        // 这一行代替了你原来的 ObjectMapper 写入文件
        animeRepository.save(anime);
    }
    public void delete(Long id) {
        // 目的：根据主键 ID 从数据库中移除记录
        animeRepository.deleteById(id);
    }
}