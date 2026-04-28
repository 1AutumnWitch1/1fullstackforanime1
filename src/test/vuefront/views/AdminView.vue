<template>
  <div class="admin-container" style="margin-top: 60px; border: 5px solid red;">
    <aside class="admin-sidebar">
      <h2 style="color: #00ffcc;">后台中枢</h2>
      <nav>
        <button @click="activeTab = 'anime'" :class="{ active: activeTab === 'anime' }">动漫管理</button>
        <button @click="activeTab = 'comment'" :class="{ active: activeTab === 'comment' }">评论审核</button>
      </nav>
    </aside>

    <main class="admin-content">
      <h1 style="color: #333;">当前状态：{{ activeTab }}</h1>

      <section v-if="activeTab === 'anime'">
        <div class="header-actions">
          <h3>动漫库列表 (总数: {{ allAnimes.length }})</h3>
          <button @click="showAddModal = true" class="add-btn">+ 新增数据</button>
        </div>

        <table class="admin-table">
          <thead>
          <tr><th>ID</th><th>标题</th><th>状态</th><th>操作</th></tr>
          </thead>
          <tbody>
          <tr v-for="item in allAnimes" :key="item.id">
            <td>{{ item.id }}</td>
            <td>{{ item.title }}</td>
            <td><span :class="['status-tag', item.status]">{{ item.status }}</span></td>
            <td>
              <button v-if="item.status === 'PENDING'" @click="handleApprove(item.id)" class="text-btn success">通过</button>
              <button @click="handleDelete(item.id)" class="text-btn danger">删除</button>
            </td>
          </tr>
          </tbody>
        </table>
      </section>

    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, inject } from 'vue';
import { animeApi } from '../api/anime';

const user = inject('currentUser');
const allAnimes = ref([]);
const activeTab = ref('anime'); // 切换 动漫管理/评论管理

const loadData = async () => {
  const res = await animeApi.getAll('ADMIN'); // 管理员获取全量数据
  allAnimes.value = res.data;
};

const handleApprove = async (id) => {
  await animeApi.approve(id, 'ADMIN');
  loadData();
};

const handleDelete = async (id) => {
  if (confirm("确定永久删除该动漫吗？")) {
    await animeApi.delete(id, 'ADMIN');
    loadData();
  }
};

// 假设你的 API 增加了删除评论的接口
const deleteComment = async (animeId, commentId) => {
  if (confirm("确定删除该违规评论吗？")) {
    // 这里需要你在后端的接口里增加对应的删除评论逻辑
    // await animeApi.deleteComment(animeId, commentId, 'ADMIN');
    alert("已下发删除指令 (需后端接口配合)");
    loadData();
  }
};

onMounted(loadData);
</script>

<style scoped>
.admin-container { display: flex; height: 100vh; background: #f4f7f6; color: #333; }
.admin-sidebar { width: 240px; background: #2c3e50; color: white; padding: 20px; }
.admin-sidebar nav button {
  display: block; width: 100%; padding: 12px; margin-bottom: 10px;
  background: transparent; color: #bdc3c7; border: none; text-align: left; cursor: pointer;
}
.admin-sidebar nav button.active { background: #34495e; color: white; border-radius: 4px; }

.admin-content { flex: 1; padding: 30px; overflow-y: auto; }
.admin-table { width: 100%; border-collapse: collapse; background: white; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.05); }
.admin-table th, .admin-table td { padding: 15px; text-align: left; border-bottom: 1px solid #eee; }
.admin-table th { background: #fafafa; font-weight: 600; }

.status-tag { padding: 4px 8px; border-radius: 4px; font-size: 12px; }
.status-tag.APPROVED { background: #e3f9e5; color: #1f7a1f; }
.status-tag.PENDING { background: #fff3e0; color: #e65100; }

.text-btn { background: none; border: none; cursor: pointer; font-weight: bold; margin-right: 10px; }
.text-btn.success { color: #2ecc71; }
.text-btn.danger { color: #e74c3c; }
</style>