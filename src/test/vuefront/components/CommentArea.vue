<template>
  <section class="comments-section">
    <h3>💬 评价与讨论</h3>
    <div v-if="user && anime.status === 'APPROVED'" class="comment-form-box">
      <textarea v-model="content" class="modern-textarea" placeholder="分享你的观看体验..."></textarea>
      <div class="comment-form-footer">
        <span>评分：<input type="number" v-model.number="score" min="1" max="10"></span>
        <button @click="submit" class="approve-btn">提交评价</button>
      </div>
    </div>
    <div class="comment-list">
      <div v-for="c in anime.comments" :key="c.id" class="comment-card">
        <div class="comment-user-info">
          <span>👤 {{ c.author }}</span>
          <span class="user-score">★ {{ c.score }}分</span>
        </div>
        <p>{{ c.content }}</p>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref } from 'vue';
import { animeApi } from '../api/anime';
const props = defineProps(['anime', 'user']);
const emit = defineEmits(['refresh']);
const content = ref('');
const score = ref(10);

const submit = async () => {
  if (!content.value) return alert("写点什么吧");
  await animeApi.postComment(props.anime.id, props.user.username, { content: content.value, score: score.value });
  content.value = '';
  emit('refresh');
};
</script>