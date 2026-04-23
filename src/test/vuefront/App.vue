<template>
  <div id="app">
    <nav class="top-nav">
      <div class="logo">🎬 AnimeHub <span class="tag">Alpha</span></div>
      <div class="user-area">
        <template v-if="currentUser">
          <span class="welcome">欢迎, {{ currentUser.username }}
            <b :class="currentUser.role">[{{ currentUser.role === 'ADMIN' ? '首席管理员' : '正式会员' }}]</b>
          </span>
          <button @click="logout" class="logout-btn">退出</button>
        </template>
        <button v-else @click="showLogin = true" class="login-btn">登录 / 注册</button>
      </div>
    </nav>

    <router-view @require-login="showLogin = true"></router-view>

    <div v-if="showLogin" class="modal-overlay">
      <div class="modal">
        <h3>{{ isLoginMode ? '系统登录' : '新用户注册' }}</h3>
        <input v-model="authForm.username" placeholder="请输入账号">
        <input type="password" v-model="authForm.password" placeholder="请输入密码">
        <input v-if="!isLoginMode" type="password" v-model="authForm.rePassword" placeholder="请再次输入密码">
        <div class="modal-btns">
          <button @click="handleAuth" class="primary-btn">{{ isLoginMode ? '确认登录' : '立即注册' }}</button>
          <button @click="showLogin = false" class="cancel-btn">取消</button>
        </div>
        <div class="switch-mode">
          <a @click="isLoginMode = !isLoginMode">{{ isLoginMode ? '还没有账号？现在注册' : '已有账号？返回登录' }}</a>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, provide } from 'vue';
import { animeApi } from './api/anime';

const currentUser = ref(JSON.parse(localStorage.getItem('user')) || null);
const showLogin = ref(false);
const isLoginMode = ref(true);
const authForm = ref({ username: '', password: '', rePassword: '' });

// 向上提供用户信息，方便所有页面调用
provide('currentUser', currentUser);

const handleAuth = async () => {
  try {
    if (isLoginMode.value) {
      const res = await animeApi.login(authForm.value);
      currentUser.value = res.data;
      localStorage.setItem('user', JSON.stringify(res.data));
      showLogin.value = false;
      location.reload(); // 刷新以同步状态
    } else {
      const res = await animeApi.register(authForm.value);
      alert(res.data);
      if (res.data === "注册成功！") isLoginMode.value = true;
    }
  } catch (e) { alert(e.response?.data || "操作失败"); }
};

const logout = () => {
  currentUser.value = null;
  localStorage.removeItem('user');
  location.reload();
};
</script>