import request from '@/utils/request';

export function update_totp_whitelist(data) {
    return request({
        url: '/update_totp_whitelist',
        method: 'post',
        data,
    });
}

export function hello() {
    console.log('hello');
}
