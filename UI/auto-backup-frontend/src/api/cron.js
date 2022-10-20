import request from '@/utils/request';

const baseURL = 'http://192.168.31.94:8088';
export function test_cron(params) {
    return request({
        baseURL: baseURL,
        url: `/cron/validation`,
        method: 'get',
        params: params,
    });
}
