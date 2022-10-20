import request from '@/utils/request';

const baseURL = 'http://192.168.31.94:8088';
export function create_db_conn(params) {
    return request({
        baseURL: baseURL,
        url: `/db_conn`,
        method: 'post',
        params: params,
    });
}

export function get_db_conns() {
    return request({
        baseURL: baseURL,
        url: `/db_conns`,
        method: 'get',
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

export function test_dbconn(params) {
    return request({
        baseURL,
        url: `/db-conn/valid`,
        method: 'get',
        params,
    });
}
