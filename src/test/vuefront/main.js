import { createApp } from 'vue'
import App from './App.vue'
import './style.css'
import router from './router' // 这一行会自动去找 router/index.js

const app = createApp(App)

app.use(router) // 核心：使用你那个包含 /admin 的路由实例
app.mount('#app')