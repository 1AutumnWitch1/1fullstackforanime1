const { createApp, ref, computed, onMounted } = Vue;

createApp({
    setup() {
        const apiBase = "http://localhost:8080/animes";
        const defaultPlaceholder = "data:image/svg+xml;charset=UTF-8,%3Csvg width='800' height='450' viewBox='0 0 800 450' xmlns='http://www.w3.org/2000/svg'%3E%3Cdefs%3E%3ClinearGradient id='g' x1='0%25' y1='0%25' x2='100%25' y2='100%25'%3E%3Cstop offset='0%25' style='stop-color:%231a1a1a;stop-opacity:1' /%3E%3Cstop offset='100%25' style='stop-color:%230f0f0f;stop-opacity:1' /%3E%3C/linearGradient%3E%3C/defs%3E%3Crect width='800' height='450' fill='url(%23g)' /%3E%3Ctext x='50%25' y='50%25' dominant-baseline='middle' text-anchor='middle' font-family='sans-serif' font-size='80' fill='%23333'%3E\uD83D\uDCFA%3C/text%3E%3Ctext x='50%25' y='65%25' dominant-baseline='middle' text-anchor='middle' font-family='sans-serif' font-size='20' fill='%23444'%3EAnimeHub Alpha%3C/text%3E%3C/svg%3E";

        // --- 状态数据 ---
        const animes = ref([]);
        const selectedAnime = ref(null);
        const currentUser = ref(JSON.parse(localStorage.getItem('user')) || null);

        const showLoginModal = ref(false);
        const isLogin = ref(true);
        const showAddModal = ref(false);

        const loginForm = ref({ username: '', password: '', rePassword: '' });
        const newAnime = ref({ title: '', description: '', lovepoint: 0 });
        const commentForm = ref({ content: '', score: 10 });

        // 计算属性
        const roleText = computed(() => {
            if (!currentUser.value) return '游客';
            return currentUser.value.role === 'ADMIN' ? '首席管理员' : '正式会员';
        });

        // --- 核心方法 ---

        // 修改封面方法 (新)
        const updateCoverDirectly = async () => {
            if (!selectedAnime.value || !selectedAnime.value.coverUrl) {
                return alert("请输入有效的图片链接");
            }
            try {
                const res = await axios.put(`${apiBase}/${selectedAnime.value.id}/cover`, null, {
                    params: {
                        coverUrl: selectedAnime.value.coverUrl,
                        role: currentUser.value.role
                    }
                });
                alert(typeof res.data === 'string' ? res.data : "封面已更新");
                fetchAnimes();
            } catch (e) {
                alert("修改失败：" + (e.response?.data || "请求异常"));
            }
        };

        const fetchAnimes = async () => {
            const role = currentUser.value ? currentUser.value.role : 'GUEST';
            try {
                const res = await axios.get(`${apiBase}?role=${role}`);
                animes.value = res.data;
                if (selectedAnime.value) {
                    selectedAnime.value = animes.value.find(a => a.id === selectedAnime.value.id);
                }
            } catch (e) { console.error("获取失败"); }
        };

        const postComment = async () => {
            if (!commentForm.value.content) return alert("请输入评价内容");
            try {
                const res = await axios.post(
                    `${apiBase}/${selectedAnime.value.id}/comment?username=${currentUser.value.username}`,
                    commentForm.value
                );
                alert(res.data);
                commentForm.value = { content: '', score: 10 };
                fetchAnimes();
            } catch (e) { alert("评价失败"); }
        };

        const submitAnime = async () => {
            try {
                const res = await axios.post(`${apiBase}/apply?username=${currentUser.value.username}`, newAnime.value);
                if (typeof res.data === 'string' && res.data.includes("失败")) {
                    alert(res.data);
                } else {
                    alert("提交成功！");
                    showAddModal.value = false;
                    newAnime.value = { title: '', description: '', lovepoint: 0 };
                    fetchAnimes();
                }
            } catch (e) { alert("提交失败"); }
        };

        const approveAnime = async (id) => {
            try {
                const res = await axios.put(`${apiBase}/${id}/approve?role=${currentUser.value.role}`);
                alert(res.data);
                fetchAnimes();
            } catch (e) { alert("审批失败"); }
        };

        const deleteAnime = async (id) => {
            if (!confirm("确定要下架该内容吗？")) return;
            try {
                const res = await axios.delete(`${apiBase}/${id}?role=${currentUser.value.role}`);
                alert(res.data);
                selectedAnime.value = null;
                fetchAnimes();
            } catch (e) { alert("操作失败"); }
        };

        const handleLogin = async () => {
            try {
                const res = await axios.post(`http://localhost:8080/users/login`, {
                    username: loginForm.value.username,
                    password: loginForm.value.password
                });
                currentUser.value = res.data;
                localStorage.setItem('user', JSON.stringify(res.data));
                showLoginModal.value = false;
                loginForm.value = { username: '', password: '' };
                fetchAnimes();
                alert("欢迎回来，" + currentUser.value.username);
            } catch (e) {
                alert(e.response?.data || "登录失败");
            }
        };

        const handleRegister = async () => {
            if (!loginForm.value.username || !loginForm.value.password) return alert("请填写完整信息");
            if (loginForm.value.password !== loginForm.value.rePassword) return alert("两次密码不一致");
            try {
                const res = await axios.post(`http://localhost:8080/users/register`, {
                    username: loginForm.value.username,
                    password: loginForm.value.password
                });
                alert(res.data);
                if (res.data === "注册成功！") {
                    isLogin.value = true;
                    loginForm.value.password = '';
                }
            } catch (e) {
                alert("注册失败：" + (e.response?.data || "服务器繁忙"));
            }
        };

        const logout = () => {
            currentUser.value = null;
            localStorage.removeItem('user');
            selectedAnime.value = null;
            fetchAnimes();
            alert("已退出");
        };

        onMounted(fetchAnimes);

        return {
            animes, selectedAnime, currentUser, showLoginModal, isLogin, showAddModal,
            loginForm, newAnime, commentForm, roleText, handleRegister, defaultPlaceholder,
            fetchAnimes, postComment, submitAnime, approveAnime, deleteAnime, handleLogin, logout,
            updateCoverDirectly,
            openLoginModal: () => { isLogin.value = true; showLoginModal.value = true; }
        };
    }
}).mount('#app');