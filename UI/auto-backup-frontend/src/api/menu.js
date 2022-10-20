import request from '@/utils/request';

export function get_menu_page(data, token) {
    return request({
        url: '/get_menu_page',
        method: 'get',
        params: data,
        headers: { 'login-token': token },
    });
}
export function insert_menu(data, token) {
    return request({
        url: '/insert_menu',
        method: 'post',
        data: data,
        headers: { 'login-token': token },
    });
}
export function get_menu_list(data, token) {
    return request({
        url: '/get_menu_list',
        method: 'get',
        data: data,
        headers: { 'login-token': token },
    });
}

export function remove_menu(params, login_token, idem_token) {
    return request({
        url: '/remove_menu',
        method: 'delete',
        params: params,
        headers: {
            'login-token': login_token,
            'idem-token': idem_token,
        },
    });
}

export function update_menu(menuid, params, login_token, idem_token) {
    return request({
        url: `/menu/${menuid}`,
        method: 'put',
        params: params,
        headers: {
            'login-token': login_token,
            'idem-token': idem_token,
        },
    });
}
