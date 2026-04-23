import axios from 'axios';

const api = axios.create({ baseURL: 'http://localhost:8080' });

export const animeApi = {
    // 动漫相关
    getAll: (role) => api.get('/animes', { params: { role } }),
    apply: (username, data) => api.post(`/animes/apply`, data, { params: { username } }),
    approve: (id, role) => api.put(`/animes/${id}/approve`, null, { params: { role } }),
    delete: (id, role) => api.delete(`/animes/${id}`, { params: { role } }),
    updateCover: (id, url, role) => api.put(`/animes/${id}/cover`, null, { params: { coverUrl: url, role } }),
    postComment: (id, username, data) => api.post(`/animes/${id}/comment`, data, { params: { username } }),

    // 用户相关
    login: (data) => api.post('/users/login', data),
    register: (data) => api.post('/users/register', data)
};