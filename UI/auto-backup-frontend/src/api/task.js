import request from '@/utils/request';

const baseURL = 'http://192.168.31.94:8088';
export function create_task(params) {
    return request({
        baseURL: baseURL,
        url: `/task`,
        method: 'post',
        params: params,
    });
}

export function get_tasks() {
    return request({
        baseURL: baseURL,
        url: `/tasks`,
        method: 'get',
    });
}
export function get_task_logs(params) {
    return request({
        baseURL: baseURL,
        url: `/logs`,
        method: 'get',
        params
    });
}

export function delete_task(task_id) {
    return request({
        baseURL: baseURL,
        url: `/task/${task_id}`,
        method: 'delete'
    });
}
