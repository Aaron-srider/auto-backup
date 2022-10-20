import { login, logout, getInfo } from '@/api/user';
import {
    getToken,
    get_login_info,
    setToken,
    removeToken,
    get_token,
    set_token,
    set_login_info,
} from '@/utils/auth';
import { resetRouter } from '@/router';

const getDefaultState = () => {
    return {
        token: get_token(),
        login_info: get_login_info(),
    };
};

const state = getDefaultState();

export const USER_RESET_USER_STATE = 'user/reset_user_state';
export const USER_SET_TOKEN = 'user/set_token';
export const USER_SET_LOGIN_INFO = 'user/set_login_info';
export const USER_LOG_OUT = 'user/log_out';

const mutations = {
    set_login_info: (state, login_info) => {
        state.login_info = login_info;
        set_login_info(login_info);
    },

    set_token: (state, token) => {
        state.token = token;
        set_token(token);
    },

    reset_user_state: (state) => {
        state.token = undefined;
        state.login_info = undefined;
        set_token(undefined);
        set_login_info(undefined);
    },
};

const actions = {
    // user login
    login({ commit }, userInfo) {
        const { username, password } = userInfo;
        return new Promise((resolve, reject) => {
            login({ username: username.trim(), password: password })
                .then((response) => {
                    const { data } = response;
                    commit('SET_TOKEN', data.token);
                    setToken(data.token);
                    resolve();
                })
                .catch((error) => {
                    reject(error);
                });
        });
    },

    // get user info
    getInfo({ commit, state }) {
        return new Promise((resolve, reject) => {
            getInfo(state.token)
                .then((response) => {
                    const { data } = response;

                    if (!data) {
                        return reject(
                            'Verification failed, please Login again.',
                        );
                    }

                    const { name, avatar } = data;

                    commit('SET_NAME', name);
                    commit('SET_AVATAR', avatar);
                    resolve(data);
                })
                .catch((error) => {
                    reject(error);
                });
        });
    },

    // user logout
    logout({ commit, state }) {
        return new Promise((resolve, reject) => {
            logout(state.token)
                .then(() => {
                    removeToken(); // must remove  token  first
                    resetRouter();
                    commit('RESET_STATE');
                    resolve();
                })
                .catch((error) => {
                    reject(error);
                });
        });
    },

    // remove token
    resetToken({ commit }) {
        return new Promise((resolve) => {
            removeToken(); // must remove  token  first
            commit('RESET_STATE');
            resolve();
        });
    },
};

export default {
    namespaced: true,
    state,
    mutations,
    actions,
};
