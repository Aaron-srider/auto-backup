import router, {
    copy_routes_for_sidebar,
    reset_sidebar_routes,
    routes,
} from './router';
import store from './store';
import { Message } from 'element-ui';
import NProgress from 'nprogress'; // progress bar
import 'nprogress/nprogress.css'; // progress bar style
import { get_token, get_login_info } from '@/utils/auth'; // get token from cookie
import getPageTitle from '@/utils/get-page-title';
import { fetch_user_info, request_token_expire } from '@/api/user';
import { traverse_tree } from './utils/route';
import { str_empty, when_unknow_err } from './utils';
import { USER_SET_LOGIN_INFO } from './store/modules/user';
import { parse_user_info_response, process_user_menu } from './utils/user';
import { clean_sidebar_routes } from './router/index';
NProgress.configure({ showSpinner: false }); // NProgress Configuration

const whiteList = ['/login', '/']; // no redirect whitelist

var next_func;
function to_somewhere(somewhere) {
    next_func(somewhere);
    NProgress.done();
}

function pass() {
    next_func();
    NProgress.done();
}

function if_pass_user(login_info, to) {
    let if_auth = true;
    let op1 = function (node_index, node, node_path, parent_node, flag) {
        if (
            to.path === node_path &&
            node.auth != undefined &&
            node.auth === false
        ) {
            if_auth = false;
        }
    };

    traverse_tree(copy_routes_for_sidebar, '', op1);

    //  检查当前页面是否需要鉴权
    if (if_auth === false) {
        return true;
    }

    // 鉴别用户是否是超级管理员
    var is_super_admin = false;
    var roles = login_info.roles;
    if (roles != undefined && roles.length != null) {
        var super_admin_idx = roles.findIndex(
            (elem) => elem.name === '超级管理员',
        );
        if (super_admin_idx != -1) {
            is_super_admin = true;
        }
    }

    // 超级管理员直接放过
    if (is_super_admin != undefined && is_super_admin === true) {
        console.log('用户是超级管理员，放过');
        return true;
    }

    // 用户不是超级管理员，判断用户是否能过去
    var user_allow_menus = login_info.menus;
    if (user_allow_menus == undefined || user_allow_menus.length === 0) {
        console.log(`用户`, login_info, `无法访问该页面`);
        return false;
    }

    // 用户信息存在，过滤一遍菜单
    console.log(`用户`, login_info, `有权访问的菜单：`, user_allow_menus);
    let if_pass = false;
    let op2 = function (node_index, node, node_path, parent_node, flag) {
        if (to.path === node_path) {
            if_pass = true;
        }
    };

    traverse_tree(copy_routes_for_sidebar, '', op2);
    return if_pass;
}

router.beforeEach(async (to, from, next) => {
    next_func = next;
    // start progress bar
    NProgress.start();

    // set page title
    document.title = getPageTitle(to.meta.title);
    next();

    // if (hasToken) {
    //   if (to.path === '/login') {
    //     // if is logged in, redirect to the home page
    //     next({ path: '/' })
    //     NProgress.done()
    //     return;
    //   }

    //     // const hasGetUserInfo = store.getters.name
    //     // if (hasGetUserInfo) {
    //     //   next()
    //     // } else {
    //     //   try {
    //     //     // get user info
    //     //     await store.dispatch('user/getInfo')
    //     //     next()
    //     //   } catch (error) {
    //     //     // remove token and go to login page to re-login
    //     //     await store.dispatch('user/resetToken')
    //     //     Message.error(error || 'Has Error')
    //     //     next(`/login?redirect=${to.path}`)
    //     //     NProgress.done()
    //     //   }
    //     // }
    // } else {
    //   /* has no token*/

    //   if (whiteList.indexOf(to.path) !== -1) {
    //     // in the free login whitelist, go directly
    //     next()
    //   } else {
    //     // other pages that do not have permission to access are redirected to the login page.
    //     next(`/login?redirect=${to.path}`)
    //     NProgress.done()
    //   }
    // }
});

router.afterEach(() => {
    // finish progress bar
    NProgress.done();
});
