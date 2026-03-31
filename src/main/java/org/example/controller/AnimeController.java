package org.example.controller;
import org.example.model.Anime;
import org.example.serve.AnimeService;
import org.example.model.Anime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
@RestController
@RequestMapping("/animes")
@CrossOrigin
public class AnimeController {

    @Autowired // 核心：让 Spring 把你写好的 Service 实例传过来
    private AnimeService animeService;

    @GetMapping
    public List<Anime> getAllAnimes() {

        return animeService.getAll();
    }
    @PostMapping
    public String addAnime(@RequestBody Anime newAnime) {
        // 调用 Service 层的 add 方法
        animeService.add(newAnime);
        return "添加成功！动漫名称：" + newAnime.getTitle();
    }
    @PutMapping
    public String updateAnime(@RequestBody Anime animeToUpdate) {
        if (animeToUpdate.getId() == null) {
            return "更新失败：必须提供需要更新的动漫 ID。";
        }

        try {
            animeService.update(animeToUpdate);
            return "修改成功！ID 为：" + animeToUpdate.getId() + "，新名称：" + animeToUpdate.getTitle();
        } catch (RuntimeException e) {
            return e.getMessage(); // 返回 Service 层抛出的错误信息
        }
    }
    @DeleteMapping("/{id}")
    public String deleteAnime(@PathVariable Long id) {
        animeService.delete(id);
        return "删除成功！ID 为：" + id;
    }
}