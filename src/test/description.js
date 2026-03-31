const API_URL = 'http://localhost:8080/animes';

// 1. 获取列表并渲染
async function fetchAnimes() {
    try {
        const response = await fetch(API_URL);
        const data = await response.json();
        const listDiv = document.getElementById('animeList');
        listDiv.innerHTML = '';

        data.forEach(anime => {
            const item = document.createElement('div');
            item.className = 'anime-item';
            // 渲染时展示简介
            item.innerHTML = `
                <div>
                    <strong>${anime.title}</strong>
                    <span class="score-tag">❤️ ${anime.lovepoint}</span>
                </div>
                <span class="desc-text">${anime.description || '这家伙很懒，什么简介也没写~'}</span>
            `;
            listDiv.appendChild(item);
        });
    } catch (error) {
        document.getElementById('animeList').innerText = '后端连接失败，请检查服务。';
    }
}

// 2. 添加动漫
async function addAnime() {
    const title = document.getElementById('title').value;
    const lovepoint = document.getElementById('lovepoint').value;
    const description = document.getElementById('description').value; // 抓取简介内容

    if (!title) return alert('动漫名称不能为空！');

    const newAnime = {
        title: title,
        lovepoint: parseInt(lovepoint) || 0,
        description: description // 传给后端
    };

    try {
        const response = await fetch(API_URL, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(newAnime)
        });

        if (response.ok) {
            // 清空输入区域
            document.getElementById('title').value = '';
            document.getElementById('lovepoint').value = '90';
            document.getElementById('description').value = '';
            fetchAnimes(); // 刷新列表
        }
    } catch (error) {
        alert('添加失败，请检查网络。');
    }
}

fetchAnimes();