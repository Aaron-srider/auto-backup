import request from '@/utils/request';

export function get_idempotent_token(token) {
    return request({
        url: '/get_idempotent_token',
        method: 'get',
        headers: { 'login-token': token },
    });
}
