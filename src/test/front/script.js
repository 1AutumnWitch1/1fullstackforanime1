const { createApp, ref, computed, onMounted } = Vue;

createApp({
    setup() {
        const apiBase = "http://localhost:8080/animes";

        // --- 状态数据 ---
        const animes = ref([]);
        const selectedAnime = ref(null);
        const currentUser = ref(JSON.parse(localStorage.getItem('user')) || null);

        // 弹窗控制
        const showLoginModal = ref(false);
        const isLogin = ref(true);
        const showAddModal = ref(false);

        // 表单数据
        const loginForm = ref({ username: '', password: '', rePassword: '' });
        const newAnime = ref({ title: '', description: '', lovepoint: 0 });
        const commentForm = ref({ content: '', score: 10 }); // 新增评论表单

        // 计算属性
        const roleText = computed(() => {
            if (!currentUser.value) return '游客';
            return currentUser.value.role === 'ADMIN' ? '首席管理员' : '正式会员';
        });

        // --- 核心方法 ---

        const fetchAnimes = async () => {
            const role = currentUser.value ? currentUser.value.role : 'GUEST';
            try {
                const res = await axios.get(`${apiBase}?role=${role}`);
                animes.value = res.data;
                // 如果当前选中的动漫在列表里，同步更新它的数据（比如新分数）
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
                commentForm.value = { content: '', score: 10 }; // 重置
                fetchAnimes(); // 刷新列表和详情
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
            if (!confirm("确定要删除/拒绝吗？")) return;
            try {
                const res = await axios.delete(`${apiBase}/${id}?role=${currentUser.value.role}`);
                alert(res.data);
                selectedAnime.value = null;
                fetchAnimes();
            } catch (e) { alert("操作失败"); }
        };

        // --- 登录/注册逻辑 (对接你的后端) ---
        // --- 1. 登录逻辑 ---
        const handleLogin = async () => {
            try {
                // 注意：这里要对应 UserController 的 @RequestMapping("/users") 和 @PostMapping("/login")
                // 我们发送的是整个 loginForm 对象，后端用 @RequestBody 接收
                const res = await axios.post(`http://localhost:8080/users/login`, {
                    username: loginForm.value.username,
                    password: loginForm.value.password
                });

                // 登录成功，后端返回的是 User 对象
                currentUser.value = res.data;
                localStorage.setItem('user', JSON.stringify(res.data)); // 持久化
                showLoginModal.value = false;

                // 登录后重置表单并刷新动漫列表（因为角色变了，看到的权限也变了）
                loginForm.value = { username: '', password: '' };
                fetchAnimes();
                alert("欢迎回来，" + currentUser.value.username);
            } catch (e) {
                // 如果后端 throw RuntimeException，消息通常在 e.response.data 中
                const errorMsg = e.response?.data || "登录失败：账号或密码错误";
                alert(errorMsg);
            }
        };

// --- 2. 注册逻辑 (补全) ---
        const handleRegister = async () => {
            // 1. 校验密码
            if (!loginForm.value.username || !loginForm.value.password) {
                alert("请填写完整的账号和密码！");
                return;
            }
            if (loginForm.value.password !== loginForm.value.rePassword) {
                alert("两次输入的密码不一致！");
                return;
            }

            try {
                // 2. 发送请求：只发送后端 User 类需要的字段
                const res = await axios.post(`http://localhost:8080/users/register`, {
                    username: loginForm.value.username,
                    password: loginForm.value.password
                });

                // 3. 处理结果
                // res.data 就是后端 return 的那个字符串
                alert(res.data);

                if (res.data === "注册成功！") {
                    isLogin.value = true; // 切换到登录模式
                    loginForm.value.password = ''; // 清空密码框
                    loginForm.value.rePassword = '';
                }
            } catch (e) {
                // 捕获后端的 throw RuntimeException
                alert("注册失败：" + (e.response?.data || "服务器繁忙"));
            }
        };

// --- 3. 退出逻辑 ---
        const logout = () => {
            currentUser.value = null;
            localStorage.removeItem('user'); // 清除缓存
            selectedAnime.value = null;      // 清空当前选中的动漫，防止权限残留
            fetchAnimes();                   // 刷新为游客视角
            alert("已安全退出");
        };

        onMounted(fetchAnimes);

        return {
            animes, selectedAnime, currentUser, showLoginModal, isLogin, showAddModal,
            loginForm, newAnime, commentForm, roleText,handleRegister,
            fetchAnimes, postComment, submitAnime, approveAnime, deleteAnime, handleLogin, logout,
            openLoginModal: () => { isLogin.value = true; showLoginModal.value = true; }
        };
    }
}).mount('#app');