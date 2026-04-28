<template>
  <div class="admin-container" style="margin-top: 60px;">
    <aside class="admin-sidebar">...</aside>

    <main class="admin-list">
      <h3>动漫列表 ({{ allAnimes.length }})</h3>
      <table class="admin-table">
        <tr v-for="item in allAnimes" :key="item.id"
            @click="selectedItem = item"
            :class="{ active: selectedItem?.id === item.id }">
          <td>ID: {{ item.id }}</td>
          <td>{{ item.title }}</td>
          <td><span :class="['status-tag', item.status]">{{ item.status }}</span></td>
        </tr>
      </table>
    </main>

    <section class="admin-detail-panel" v-if="selectedItem">
      <div class="panel-header">
        <h3>管理详情：{{ selectedItem.title }}</h3>
        <button @click="selectedItem = null">关闭</button>
      </div>

      <div class="edit-section">
        <h4>封面管理</h4>
        <div class="preview-box">
          <img :src="selectedItem.coverUrl" alt="预览失败" class="admin-preview-img">
        </div>
        <div class="input-group">
          <input v-model="selectedItem.coverUrl" placeholder="输入新的图片URL">
          <button @click="updateCover" class="save-btn">保存图片</button>
        </div>
      </div>

      <div class="edit-section">
        <h4>评论监管 ({{ selectedItem.comments?.length || 0 }})</h4>
        <div class="admin-comment-list">
          <div v-for="c in selectedItem.comments" :key="c.id" class="admin-comment-item">
            <div class="c-user"><strong>{{ c.author }}</strong>:</div>
            <div class="c-content">{{ c.content }}</div>
            <button @click="deleteComment(selectedItem.id, c.id)" class="del-c-btn">删除</button>
          </div>
          <div v-if="!selectedItem.comments?.length" class="empty">暂无评论</div>
        </div>
      </div>
    </section>

    <div v-else class="panel-placeholder">
      💡 请在左侧点击一个动漫进行深度管理
    </div>
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
const selectedItem = ref(null); // 当前选中的动漫

// 保存图片修改
const updateCover = async () => {
  if (!selectedItem.value) return;
  try {
    await animeApi.updateCover(selectedItem.value.id, selectedItem.value.coverUrl, user.value.role);
    alert("封面路径已更新！");
    loadData(); // 刷新数据
  } catch (e) {
    alert("保存失败，请检查网络或权限");
  }
};

// 删除评论
const deleteComment = async (animeId, commentId) => {
  if (confirm("确定要删除这条评论吗？此操作不可撤销。")) {
    // 调用你后端的删除评论接口
    // await animeApi.deleteComment(animeId, commentId, user.value.role);
    alert(`准备从动漫 ${animeId} 中删除评论 ${commentId}`);
    // 逻辑：你可以通过 loadData 重新拉取，或者直接从本地数组里 splice 掉它
    loadData();
  }
};

onMounted(loadData);
</script>

<style scoped>
.admin-container {
  display: grid;
  grid-template-columns: 200px 1fr 400px; /* 固定的侧边栏，自适应的列表，固定的详情面板 */
  height: calc(100vh - 60px);
}

.admin-list {
  border-right: 1px solid #ddd;
  overflow-y: auto;
  padding: 20px;
}

.admin-detail-panel {
  background: white;
  padding: 20px;
  box-shadow: -5px 0 15px rgba(0,0,0,0.05);
  overflow-y: auto;
}

.admin-preview-img {
  width: 100%;
  max-height: 250px;
  object-fit: contain;
  border-radius: 8px;
  background: #eee;
  margin-bottom: 10px;
}

.admin-table tr:hover { background: #eef2f7; cursor: pointer; }
.admin-table tr.active { background: #e3f2fd; border-left: 4px solid #2196f3; }
</style>