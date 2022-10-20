import request from '@/utils/request';

export function get_api_list(params, login_token) {
    return request({
        url: `/apis`,
        method: 'get',
        params: params,
        headers: {
            'login-token': login_token,
        },
    });
}

export function update_api_resource_by_id(
    api_id,
    params,
    login_token,
    idem_token,
) {
    return request({
        url: `/api/${api_id}`,
        method: 'put',
        params: params,
        headers: {
            'login-token': login_token,
            'idem-token': idem_token,
        },
    });
}

export function insert_new_api(data, login_token, idem_token) {
    return request({
        url: `/api`,
        method: 'post',
        data: data,
        headers: {
            'login-token': login_token,
            'idem-token': idem_token,
        },
    });
}

export function refresh_api_resource(login_token) {
    return request({
        url: `/refresh_api_resource`,
        method: 'get',
        headers: {
            'login-token': login_token,
        },
    });
}

export function remove_api_resource_by_id(api_id, login_token, idem_token) {
    return request({
        url: `/api/${api_id}`,
        method: 'delete',
        headers: {
            'login-token': login_token,
            'idem-token': idem_token,
        },
    });
}

export function get_api_list_by_path(params, login_token) {
    return request({
        url: `/apis_suggestion`,
        method: 'get',
        params: params,
        headers: {
            'login-token': login_token,
        },
    });
}

export function remove_api_from_permission(
    permissionid,
    api_id,
    login_token,
    idem_token,
) {
    return request({
        url: `/permission_api/permission_id/${permissionid}/api_id/${api_id}`,
        method: 'delete',
        headers: {
            'login-token': login_token,
            'idem-token': idem_token,
        },
    });
}
