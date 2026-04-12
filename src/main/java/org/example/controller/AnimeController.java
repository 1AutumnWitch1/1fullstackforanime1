package org.example.controller;

import org.example.model.Anime;
import org.example.model.User;
import org.example.serve.AnimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/animes")
@CrossOrigin // 允许前端 Vue 跨域访问
public class AnimeController {

    @Autowired
    private AnimeService animeService;

    // --- 修改点 1：获取列表支持角色过滤 ---
    // @RequestParam 表示从 URL 后面拿参数，例如 /animes?role=ADMIN
    @GetMapping
    public List<Anime> getAllAnimes(@RequestParam(required = false) String role) {
        // 将前端传来的角色（可能是 null）交给 Service
        return animeService.getVisibleAnimes(role);
    }

    // --- 修改点 2：用户提交申请接口 ---
    // 专门给普通用户用的，会自动设置为 PENDING 状态
    @PostMapping("/apply")
    public Anime applyAnime(@RequestBody Anime newAnime, @RequestParam String username) {
        return animeService.applyAnime(newAnime, username);
    }

    // --- 修改点 3：管理员审批通过接口 ---
    // 使用 @PutMapping 表示修改状态
    @PutMapping("/{id}/approve")
    public String approveAnime(@PathVariable Long id, @RequestParam String role) {
        if (!"ADMIN".equalsIgnoreCase(role)) {
            return "权限不足：只有管理员可以审批！";
        }
        animeService.approveAnime(id);
        return "审批成功！该动漫已正式上线。";
    }

    // --- 修改点 4：带权限验证的删除 ---
    @DeleteMapping("/{id}")
    public String deleteAnime(@PathVariable Long id, @RequestParam String role) {
        try {
            animeService.deleteWithAuth(id, role);
            return "操作成功：动漫已删除/拒绝。";
        } catch (RuntimeException e) {
            return "操作失败：" + e.getMessage();
        }
    }


}