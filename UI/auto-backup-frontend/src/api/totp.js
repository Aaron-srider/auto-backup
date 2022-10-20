import request from '@/utils/request';

export function get_totp(device_serial_number, token) {
    return request({
        url: '/get_totp',
        method: 'get',
        headers: { 'login-token': token },
        params: {
            deviceSerialNumber: device_serial_number,
        },
    });
}

export function get_totp_whitelist(employees) {
    return request({
        url: '/get_totp_whitelist',
        method: 'get',
        params: {
            userSamAccountNameList: employees,
        },
    });
}

export function poll_totp(params, token) {
    return request({
        url: `/totp_polling`,
        method: 'get',
        headers: { 'login-token': token },
        params: params,
    });
}

export function hello() {
    console.log('world');
}
