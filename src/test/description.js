const { createApp, ref, onMounted } = Vue;

createApp({
    setup() {
        // 后端 API 地址
        const apiBase = "http://localhost:8080/animes";

        // 响应式数据
        const animes = ref([]);
        const newAnime = ref({ title: '', description: '', lovepoint: 0 });

        // 1. 从后端获取数据
        const fetchAnimes = async () => {
            try {
                const res = await axios.get(apiBase);
                // 这里的 data 已经是 Java 返回并经 Jackson 转好的 JSON 数组了
                animes.value = res.data;
            } catch (e) {
                console.error(e);
                alert("获取数据失败，请检查 Spring Boot 后端是否启动！");
            }
        };

        // 2. 添加动漫到后端
        const addAnime = async () => {
            if (!newAnime.value.title) {
                alert("动漫名称是必填的哦！");
                return;
            }
            try {
                await axios.post(apiBase, newAnime.value);
                // 重置输入框
                newAnime.value = { title: '', description: '', lovepoint: 0 };
                // 刷新列表以显示最新数据
                fetchAnimes();
            } catch (e) {
                alert("添加失败，请查看控制台错误。");
            }
        };

        // 3. 删除动漫
        const deleteAnime = async (id) => {
            if (confirm("确定要舍弃这部动漫吗？")) {
                try {
                    await axios.delete(`${apiBase}/${id}`);
                    fetchAnimes();
                } catch (e) {
                    alert("删除失败");
                }
            }
        };

        // 钩子函数：页面挂载完毕后立刻请求数据
        onMounted(fetchAnimes);

        return {
            animes,
            newAnime,
            addAnime,
            deleteAnime
        };
    }
}).mount('#app');