import request from '@/utils/request';
export function get_permissions(params, login_token) {
    return request({
        url: `/permissions`,
        method: 'get',
        params: params,
        headers: {
            'login-token': login_token,
        },
    });
}

export function create_permission(data, login_token, idem_token) {
    return request({
        url: `/permission`,
        method: 'post',
        data: data,
        headers: {
            'login-token': login_token,
            'idem-token': idem_token,
        },
    });
}
export function update_permission_by_id(
    permissionid,
    data,
    login_token,
    idem_token,
) {
    return request({
        url: `/permission/${permissionid}`,
        method: 'put',
        data: data,
        headers: {
            'login-token': login_token,
            'idem-token': idem_token,
        },
    });
}
export function remove_permission_by_id(
    permission_id,
    login_token,
    idem_token,
) {
    return request({
        url: `/permission/${permission_id}`,
        method: 'delete',
        headers: {
            'login-token': login_token,
            'idem-token': idem_token,
        },
    });
}

export function get_api_list_by_permission_id(permissionid, login_token) {
    return request({
        url: `/apis/permission_id/${permissionid}`,
        method: 'get',
        headers: {
            'login-token': login_token,
        },
    });
}

export function insert_api_to_permission(
    permissionid,
    api_id,
    login_token,
    idem_token,
) {
    return request({
        url: `/permission_api/permission_id/${permissionid}/api_id/${api_id}`,
        method: 'post',
        headers: {
            'login-token': login_token,
            'idem-token': idem_token,
        },
    });
}
export function insert_api_to_permission_by_api_url_and_method(
    permissionid,
    data,
    login_token,
    idem_token,
) {
    return request({
        url: `/permission_api/permission_id/${permissionid}`,
        method: 'post',
        data: data,
        headers: {
            'login-token': login_token,
            'idem-token': idem_token,
        },
    });
}

export function insert_permission_for_role(
    roleid,
    permissionid,
    login_token,
    idem_token,
) {
    return request({
        url: `/roleid/${roleid}/permissionid/${permissionid}`,
        method: 'post',
        headers: {
            'login-token': login_token,
            'idem-token': idem_token,
        },
    });
}
export function remove_permission_from_role(
    roleid,
    permissionid,
    login_token,
    idem_token,
) {
    return request({
        url: `/roleid/${roleid}/permissionid/${permissionid}`,
        method: 'delete',
        headers: {
            'login-token': login_token,
            'idem-token': idem_token,
        },
    });
}
