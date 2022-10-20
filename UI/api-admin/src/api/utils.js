import request from '@/utils/request';

export function get_js_request_function_list(data) {
    return request({
        url: '/utils/get_js_request_function_list',
        method: 'get',
        params: data,
    });
}
