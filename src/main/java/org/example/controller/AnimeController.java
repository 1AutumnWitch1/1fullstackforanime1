package org.example.controller;

import org.example.model.Anime;
import org.example.model.Comment;
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

    /**
     * 获取可见列表
     * GET /animes?role=ADMIN
     */
    @GetMapping
    public List<Anime> getAllAnimes(@RequestParam(required = false) String role) {
        return animeService.getVisibleAnimes(role);
    }

    /**
     * 用户提交申请（新增异常捕获，拦截游客）
     * POST /animes/apply?username=Autumn
     */
    @PostMapping("/apply")
    public Object applyAnime(@RequestBody Anime newAnime, @RequestParam(required = false) String username) {
        try {
            return animeService.applyAnime(newAnime, username);
        } catch (RuntimeException e) {
            // 返回错误信息给前端，而不是直接崩溃
            return e.getMessage();
        }
    }

    /**
     * 【核心新增】发表评论并打分接口
     * POST /animes/{id}/comment?username=Autumn
     */
    @PostMapping("/{id}/comment")
    public String addComment(
            @PathVariable Long id,
            @RequestBody Comment comment,
            @RequestParam(required = false) String username) {
        try {
            animeService.addComment(id, comment, username);
            return "评论成功！平均分已更新。";
        } catch (RuntimeException e) {
            return "评价失败：" + e.getMessage();
        }
    }

    /**
     * 管理员审批通过
     * PUT /animes/{id}/approve?role=ADMIN
     */
    @PutMapping("/{id}/approve")
    public String approveAnime(@PathVariable Long id, @RequestParam String role) {
        if (!"ADMIN".equalsIgnoreCase(role)) {
            return "权限不足：只有管理员可以审批！";
        }
        animeService.approveAnime(id);
        return "审批成功！该动漫已正式上线。";
    }

    /**
     * 带权限验证的删除/拒绝
     * DELETE /animes/{id}?role=ADMIN
     */
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