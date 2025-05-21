import http from "./http";

const api = {

    managerLogin(params: object) {
        return http.request.post('/auth/login', params);
    },

    checkCode(params: string) {
        return `${http.host}/auth/check_code?uid=${params}`
    },

};

export default api;


