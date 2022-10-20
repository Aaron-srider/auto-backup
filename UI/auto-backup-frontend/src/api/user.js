import request from '@/utils/request';

export function login(data) {
    return request({
        url: '/auth_user',
        method: 'post',
        data,
    });
}

// export function getInfo(token) {
//   return request({
//     url: '/vue-admin-template/user/info',
//     method: 'get',
//     params: { token }
//   })
// }

// export function logout() {
//   return request({
//     url: '/vue-admin-template/user/logout',
//     method: 'post'
//   })
// }

export function fetch_user_info(token) {
    return request({
        url: '/user/get_user_info',
        method: 'get',
        headers: { 'login-token': token },
    });
}
export function get_full_sam_account_name(query_string, token) {
    return request({
        url: '/get_full_sam_account_name',
        method: 'get',
        params: {
            queryString: query_string,
        },
        headers: { 'login-token': token },
    });
}

export function get_user_page(data, token) {
    return request({
        url: '/get_user_page',
        method: 'get',
        params: data,
        headers: { 'login-token': token },
    });
}

export function add_role_to_user(data, token, idem_token) {
    return request({
        url: '/add_role_to_user',
        method: 'post',
        data: data,
        headers: { 'login-token': token, 'idem-token': idem_token },
    });
}

export function remove_user_role(data, token, idem_token) {
    return request({
        url: '/remove_user_role',
        method: 'delete',
        data: data,
        headers: { 'login-token': token, 'idem-token': idem_token },
    });
}
export function update_user_nickname(data, token) {
    return request({
        url: '/update_user_nickname',
        method: 'put',
        data: data,
        headers: { 'login-token': token },
    });
}
export function request_token_expire(token) {
    return request({
        url: '/token_expire',
        method: 'get',
        headers: { 'login-token': token },
    });
}
