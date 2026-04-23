import { createApp } from 'vue'
import App from './App.vue'
import { createRouter, createWebHistory } from 'vue-router'
import HomeView from './views/HomeView.vue'
import './style.css' // 确保你有个 style.css 文件

const router = createRouter({
    history: createWebHistory(),
    routes: [
        { path: '/', component: HomeView }
    ]
})

const app = createApp(App)
app.use(router)
app.mount('#app')