import request from '@/utils/request';

export function get_log_page(data, token) {
    return request({
        url: '/get_log_page',
        method: 'get',
        params: data,
        headers: { 'login-token': token },
    });
}
