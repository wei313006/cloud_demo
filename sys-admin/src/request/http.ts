import axios from 'axios';

// const host = 'http://192.168.0.35:9090'
const host = 'http://localhost:9090'

const instance = axios.create({
    baseURL: host,
    timeout: 15000,
});

// 请求拦截器
instance.interceptors.request.use(
    config => {
        const accessToken = localStorage.getItem('accessToken');
        if (accessToken) {
            config.headers['ACCESS_TOKEN'] = accessToken;
        }
        return config;
    },
    error => {
        return Promise.reject(error);
    }
);
// 响应拦截器
instance.interceptors.response.use(
    response => {
        return response.data;
    },
    error => {
        return Promise.reject(error);
    }
);
// 封装HTTP请求方法
const request = {
    get(url: string, params?: string) {
        return instance.get(url, { params });
    },
    post(url: string, data: object | string) {
        return instance.post(url, data);
    },
    put(url: string, data: object) {
        return instance.put(url, data);
    },
    delete(url: string) {
        return instance.delete(url);
    },
};
export default { request, host };

