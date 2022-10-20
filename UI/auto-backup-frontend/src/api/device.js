import request from '@/utils/request';

// export function get_devices(serialnumbers, token) {
//     return request({
//         url: '/get_devices',
//         method: 'get',
//         params: {
//             deviceSerialNumbers: serialnumbers,
//         },
//         headers: { 'login-token': token },
//     });
// }

export function get_full_device_serial_number(query_string, token) {
    return request({
        url: '/get_full_device_serial_number',
        method: 'get',
        params: {
            queryString: query_string,
        },
        headers: { 'login-token': token },
    });
}

export function get_device_totp_timeout() {
    return request({
        url: '/get_device_totp_timeout',
        method: 'get',
    });
}

export function get_devices(params, login_token) {
    return request({
        url: `/devices`,
        method: 'get',
        params: params,
        headers: {
            'login-token': login_token,
        },
    });
}

export function get_device_template_data_type_list(login_token) {
    return request({
        url: `/device_template_data_type_list`,
        method: 'get',
        headers: {
            'login-token': login_token,
        },
    });
}
export function create_device_template(data, login_token, idem_token) {
    return request({
        url: `/device_template`,
        method: 'post',
        data: data,
        headers: {
            'login-token': login_token,
            'idem-token': idem_token,
        },
    });
}

export function get_device_template_list(params, login_token) {
    return request({
        url: `/device_template/template_id_list`,
        method: 'get',
        params: params,
        headers: {
            'login-token': login_token,
        },
    });
}
export function remove_device_template(template_id, login_token, idem_token) {
    return request({
        url: `/device_template/${template_id}`,
        method: 'delete',
        headers: {
            'login-token': login_token,
            'idem-token': idem_token,
        },
    });
}
export function update_device_template(
    template_id,
    data,
    login_token,
    idem_token,
) {
    return request({
        url: `/device_template/${template_id}`,
        method: 'put',
        data: data,
        headers: {
            'login-token': login_token,
            'idem-token': idem_token,
        },
    });
}

export function get_device_template_required_field_list(login_token) {
    return request({
        url: `/device_template_required_fields`,
        method: 'get',
        headers: {
            'login-token': login_token,
        },
    });
}
