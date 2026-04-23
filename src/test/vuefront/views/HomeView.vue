<template>
  <div class="main-layout">
    <Sidebar :list="animes" v-model="selectedAnime" :user="user" @add="showAdd = true" />

    <main class="content-area">
      <div v-if="selectedAnime" class="anime-detail">
        <div class="detail-header">
          <h2>{{ selectedAnime.title }}</h2>
          <span class="love-badge">评分：{{ selectedAnime.averageLovepoint.toFixed(1) }}</span>
        </div>

        <div class="detail-body">
          <div style="display: flex; justify-content: space-between; gap: 20px;">
            <div class="meta-info">
              <p>状态：<span :class="selectedAnime.status">{{ selectedAnime.status }}</span></p>
              <div v-if="user?.role === 'ADMIN'" class="admin-edit">
                <input v-model="selectedAnime.coverUrl">
                <button @click="updateCover">保存封面</button>
              </div>
            </div>
            <img :src="selectedAnime.coverUrl" style="width:200px; border-radius:10px;">
          </div>
          <p class="desc">{{ selectedAnime.description }}</p>
        </div>

        <div v-if="user?.role === 'ADMIN'" class="admin-actions">
          <button v-if="selectedAnime.status === 'PENDING'" @click="doApprove" class="approve-btn">通过审批</button>
          <button @click="doDelete" class="delete-btn">下架/拒绝</button>
        </div>

        <CommentArea :anime="selectedAnime" :user="user" @refresh="fetchData" />
      </div>
      <div v-else class="empty-state">📺 请选择动漫</div>
    </main>

    <div v-if="showAdd" class="modal-overlay">
      <div class="modal">
        <h3>{{ user?.role === 'ADMIN' ? '直接入库' : '申请加入' }}</h3>
        <input v-model="newAnime.title" placeholder="名称">
        <textarea v-model="newAnime.description" placeholder="简介"></textarea>
        <div class="modal-btns">
          <button @click="submitApply" class="primary-btn">提交</button>
          <button @click="showAdd = false" class="cancel-btn">取消</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, inject } from 'vue';
import { animeApi } from '../api/anime';
import Sidebar from '../components/Sidebar.vue';
import CommentArea from '../components/CommentArea.vue';

const user = inject('currentUser');
const animes = ref([]);
const selectedAnime = ref(null);
const showAdd = ref(false);
const newAnime = ref({ title: '', description: '' });

const fetchData = async () => {
  const res = await animeApi.getAll(user.value?.role || 'GUEST');
  animes.value = res.data;
  if (selectedAnime.value) selectedAnime.value = animes.value.find(a => a.id === selectedAnime.value.id);
};

const submitApply = async () => {
  await animeApi.apply(user.value.username, newAnime.value);
  showAdd.value = false;
  fetchData();
};

const doApprove = async () => {
  const res = await animeApi.approve(selectedAnime.value.id, user.value.role);
  alert(res.data);
  fetchData();
};

const doDelete = async () => {
  if (!confirm("确定删除？")) return;
  await animeApi.delete(selectedAnime.value.id, user.value.role);
  selectedAnime.value = null;
  fetchData();
};

const updateCover = async () => {
  await animeApi.updateCover(selectedAnime.value.id, selectedAnime.value.coverUrl, user.value.role);
  alert("更新成功");
};

onMounted(fetchData);
</script>