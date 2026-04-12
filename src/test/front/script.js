const { createApp, ref, onMounted, computed } = Vue;

createApp({
    setup() {
        const apiBase = "http://localhost:8080/animes";
        const userApi = "http://localhost:8080/users";

        // 数据相关
        const animes = ref([]);
        const selectedAnime = ref(null);
        const currentUser = ref(JSON.parse(localStorage.getItem('user') || 'null'));

        // 弹窗状态
        const showLoginModal = ref(false);
        const showAddModal = ref(false);
        const isLogin = ref(true);

        // 表单数据
        const loginForm = ref({ username: '', password: '', rePassword: '' });
        const newAnime = ref({ title: '', lovepoint: 0, description: '' });

        const roleText = computed(() => {
            if (!currentUser.value) return '游客';
            return currentUser.value.role === 'ADMIN' ? '系统管理员' : '正式会员';
        });

        // 1. 获取列表
        const fetchAnimes = async () => {
            try {
                const roleParam = currentUser.value ? `?role=${currentUser.value.role}` : '';
                const res = await axios.get(`${apiBase}${roleParam}`);
                animes.value = res.data;
            } catch (e) { console.error("数据加载失败"); }
        };

        // 2. 注册 & 登录
        const handleRegister = async () => {
            if (loginForm.value.password !== loginForm.value.rePassword) {
                alert("两次密码不一致"); return;
            }
            try {
                await axios.post(`${userApi}/register`, loginForm.value);
                alert("注册成功，请登录！");
                isLogin.value = true;
            } catch (e) { alert(e.response?.data || "注册失败"); }
        };

        const handleLogin = async () => {
            try {
                const res = await axios.post(`${userApi}/login`, loginForm.value);
                currentUser.value = res.data;
                localStorage.setItem('user', JSON.stringify(res.data));
                showLoginModal.value = false;
                fetchAnimes();
            } catch (e) { alert("登录失败，请检查账号密码"); }
        };

        const logout = () => {
            currentUser.value = null;
            localStorage.removeItem('user');
            selectedAnime.value = null;
            fetchAnimes();
        };

        // 3. 提交动漫（申请或直接添加）
        const submitAnime = async () => {
            try {
                if (currentUser.value.role === 'ADMIN') {
                    await axios.post(apiBase, newAnime.value);
                } else {
                    await axios.post(`${apiBase}/apply?username=${currentUser.value.username}`, newAnime.value);
                }
                alert("提交成功！");
                showAddModal.value = false;
                newAnime.value = { title: '', lovepoint: 0, description: '' };
                fetchAnimes();
            } catch (e) { alert("提交失败"); }
        };

        // 4. 管理员操作：审批 & 删除
        const approveAnime = async (id) => {
            try {
                await axios.put(`${apiBase}/${id}/approve?role=ADMIN`);
                alert("审批已通过！");
                fetchAnimes();
                selectedAnime.value.status = 'APPROVED';
            } catch (e) { alert("审批失败"); }
        };

        const deleteAnime = async (id) => {
            if(!confirm("确定要删除/下架吗？")) return;
            try {
                await axios.delete(`${apiBase}/${id}?role=ADMIN`);
                alert("操作成功");
                selectedAnime.value = null;
                fetchAnimes();
            } catch (e) { alert("操作失败"); }
        };

        const openLoginModal = () => {
            isLogin.value = true;
            showLoginModal.value = true;
        };

        onMounted(fetchAnimes);

        return {
            animes, selectedAnime, currentUser, roleText,
            showLoginModal, showAddModal, isLogin, loginForm, newAnime,
            handleLogin, handleRegister, logout, openLoginModal,
            submitAnime, approveAnime, deleteAnime
        };
    }
}).mount('#app');