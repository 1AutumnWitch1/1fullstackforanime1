<template>
  <nav class="top-nav">
    <div class="logo">🎬 AnimeHub <span class="tag">Alpha</span></div>
    <div class="user-area">
      <div v-if="user">
        <span class="welcome">欢迎, {{ user.username }}</span>
        <button @click="$emit('logout')" class="logout-btn">退出</button>
      </div>
      <button v-else @click="$emit('openLogin')" class="login-btn">登录 / 注册</button>
    </div>
  </nav>
</template>

<script setup>
defineProps(['user']);
defineEmits(['logout', 'openLogin']);
</script>