const { createApp, ref, onMounted, computed } = Vue;

createApp({
    setup() {
        const apiBase = "http://localhost:8080/animes";
        const userApi = "http://localhost:8080/users"; // 对应你后端的 UserController

        const animes = ref([]);
        const selectedAnime = ref(null);
        const currentUser = ref(JSON.parse(localStorage.getItem('user') || 'null'));

        // UI 状态
        const showLoginModal = ref(false);
        const isLogin = ref(true); // 切换登录/注册
        const loginForm = ref({ username: '', password: '', rePassword: '' });

        const roleText = computed(() => {
            if (!currentUser.value) return '游客';
            return currentUser.value.role === 'ADMIN' ? '系统管理员' : '正式会员';
        });

        const fetchAnimes = async () => {
            try {
                // 根据身份请求不同的数据
                const roleParam = currentUser.value ? `?role=${currentUser.value.role}` : '';
                const res = await axios.get(`${apiBase}${roleParam}`);
                animes.value = res.data;
            } catch (e) { console.error("数据加载失败"); }
        };

        // 注册逻辑
        const handleRegister = async () => {
            if (loginForm.value.password !== loginForm.value.rePassword) {
                alert("两次密码输入不一致！");
                return;
            }
            try {
                await axios.post(`${userApi}/register`, {
                    username: loginForm.value.username,
                    password: loginForm.value.password
                });
                alert("注册成功！请登录");
                isLogin.value = true; // 注册成功跳回登录页
            } catch (e) {
                alert(e.response?.data || "注册失败，用户名可能已存在");
            }
        };

        // 登录逻辑
        const handleLogin = async () => {
            try {
                const res = await axios.post(`${userApi}/login`, {
                    username: loginForm.value.username,
                    password: loginForm.value.password
                });
                currentUser.value = res.data;
                localStorage.setItem('user', JSON.stringify(currentUser.value));
                showLoginModal.value = false;
                fetchAnimes(); // 登录后刷新列表（管理员能看到待审批）
            } catch (e) {
                alert("登录失败，请检查账号密码");
            }
        };

        const logout = () => {
            currentUser.value = null;
            localStorage.removeItem('user');
            fetchAnimes();
        };

        const openLoginModal = () => {
            isLogin.value = true;
            showLoginModal.value = true;
        };

        onMounted(fetchAnimes);

        return {
            animes, selectedAnime, currentUser, roleText,
            showLoginModal, isLogin, loginForm,
            handleLogin, handleRegister, logout, openLoginModal
        };
    }
}).mount('#app');