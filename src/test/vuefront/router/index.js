import { createRouter, createWebHashHistory } from 'vue-router'
// 1. 引入你的页面组件
import HomeView from '../views/HomeView.vue'

// 2. 定义路径和组件的对应关系
const routes = [
    {
        path: '/',
        name: 'home',
        component: HomeView
    },
    {
        path: '/login',
        name: 'login',
        component: LoginView
    },
    {
        path: '/admin',
        name: 'admin',
        component: AdminView
    }
]

// 3. 创建路由实例
const router = createRouter({
    // 使用 Hash 模式（URL里带#），这样你直接双击HTML也能跑，不需要配置复杂的后端路由
    history: createWebHashHistory(),
    routes
})

export default router