import request from '@/utils/request';

export function apply_appid(params, login_token, idem_token) {
    return request({
        url: `/app_access_key/apply_appId`,
        method: 'get',
        params: params,
        headers: {
            'login-token': login_token,
            'idem-token': idem_token,
        },
    });
}

export function get_my_apps(params, login_token) {
    return request({
        url: `/app_access_key/my_apps`,
        method: 'get',
        params: params,
        headers: {
            'login-token': login_token,
        },
    });
}

export function update_my_app(id, params, login_token, idem_token) {
    return request({
        url: `/app_access_key/my_app/${id}`,
        method: 'put',
        params: params,
        headers: {
            'login-token': login_token,
            'idem-token': idem_token,
        },
    });
}

export function remove_my_app(id, login_token, idem_token) {
    return request({
        url: `/app_access_key/my_app/${id}`,
        method: 'delete',
        headers: {
            'login-token': login_token,
            'idem-token': idem_token,
        },
    });
}
