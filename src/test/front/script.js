const { createApp, ref, onMounted, computed } = Vue;

createApp({
    setup() {
        const apiBase = "http://localhost:8080/animes";

        // 数据状态
        const animes = ref([]);
        const selectedAnime = ref(null);
        const currentUser = ref(JSON.parse(localStorage.getItem('user') || 'null'));

        // UI 状态
        const showLoginModal = ref(false);
        const showAddModal = ref(false);
        const loginForm = ref({ username: '', password: '' });

        const roleText = computed(() => {
            if (!currentUser.value) return '游客';
            return currentUser.value.role === 'admin' ? '系统管理员' : '正式会员';
        });

        // 获取数据
        const fetchAnimes = async () => {
            try {
                const res = await axios.get(apiBase);
                animes.value = res.data;
                // 默认选中第一个
                if (animes.value.length > 0 && !selectedAnime.value) {
                    selectedAnime.value = animes.value[0];
                }
            } catch (e) { alert("后端连接失败"); }
        };

        // 登录模拟
        const handleLogin = () => {
            if (loginForm.value.username === 'admin') {
                currentUser.value = { username: 'YYW', role: 'admin' };
            } else {
                currentUser.value = { username: loginForm.value.username, role: 'user' };
            }
            localStorage.setItem('user', JSON.stringify(currentUser.value));
            showLoginModal.value = false;
        };

        const logout = () => {
            currentUser.value = null;
            localStorage.removeItem('user');
            selectedAnime.value = null;
            fetchAnimes();
        };

        const deleteAnime = async (id) => {
            if (!confirm("确定下架吗？")) return;
            await axios.delete(`${apiBase}/${id}`);
            selectedAnime.value = null;
            fetchAnimes();
        };

        onMounted(fetchAnimes);

        return {
            animes, selectedAnime, currentUser, roleText,
            showLoginModal, loginForm, handleLogin, logout,
            deleteAnime
        };
    }
}).mount('#app');