import request from '@/utils/request';

export function get_role_list(data, token) {
    return request({
        url: '/get_role_list',
        method: 'get',
        params: data,
        headers: { 'login-token': token },
    });
}
export function get_full_role_name(data, token) {
    return request({
        url: '/get_full_role_name',
        method: 'get',
        params: data,
        headers: { 'login-token': token },
    });
}
export function insert_role(data, token, idem_token) {
    return request({
        url: '/insert_role',
        method: 'post',
        data: data,
        headers: { 'login-token': token, 'idem-token': idem_token },
    });
}
export function remove_role(data, token, idem_token) {
    return request({
        url: '/remove_role',
        method: 'delete',
        data: data,
        headers: { 'login-token': token, 'idem-token': idem_token },
    });
}

export function update_role(data, token, idem_token) {
    return request({
        url: '/update_role',
        method: 'put',
        data: data,
        headers: { 'login-token': token, 'idem-token': idem_token },
    });
}

export function add_menu_to_role(data, token, idem_token) {
    return request({
        url: '/add_menu_to_role',
        method: 'post',
        data: data,
        headers: { 'login-token': token, 'idem-token': idem_token },
    });
}

export function get_role_menus(data, token) {
    return request({
        url: '/get_role_menus',
        method: 'get',
        params: data,
        headers: { 'login-token': token },
    });
}

export function remove_menu_from_role(roleid, menuid, login_token, idem_token) {
    return request({
        url: `/role_menu/roleid/${roleid}/menuid/${menuid}`,
        method: 'delete',
        headers: {
            'login-token': login_token,
            'idem-token': idem_token,
        },
    });
}
