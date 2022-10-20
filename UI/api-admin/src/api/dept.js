import request from '@/utils/request';

export function get_sub_ou(dept_name) {
    return request({
        url: '/dept/get_sub_dept',
        method: 'get',
        params: {
            deptName: dept_name,
        },
        // headers: {'login-token': token},
    });
}
