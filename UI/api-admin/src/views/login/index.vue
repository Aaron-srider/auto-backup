<template>
    <div class="login-pannel">
        <el-form
            ref="loginForm"
            :model="loginForm"
            :rules="loginRules"
            class=""
            auto-complete="on"
            label-position="left"
        >
            <div class="title-container">
                <h3 class="title" style="color: #4285f4">TOTP ADMIN</h3>
            </div>

            <!-- 用户名输入框 -->
            <el-form-item prop="username" class="login-row">
                <div class="flex">
                    <span class="svg-container mgr8">
                        <svg-icon icon-class="user"></svg-icon>
                    </span>
                    <el-input
                        ref="username"
                        v-model="loginForm.username"
                        placeholder="Username"
                        name="uid"
                        type="text"
                        tabindex="1"
                        auto-complete="on"
                    ></el-input>
                </div>
            </el-form-item>

            <!-- 用户名错误提示 -->
            <div
                ref="login-prompt-uid"
                class="login-row login-error-prompt hide"
                style="margin: 8px auto 8px auto"
            >
                <i class="el-icon-warning-outline fs12"></i>

                <span
                    ref="login-prompt-text"
                    class="fs12"
                    style="margin-left: 4px"
                >
                    账号错误
                </span>
            </div>

            <!-- 密码输入框 -->
            <el-form-item prop="password" class="login-row mgb20">
                <div class="flex">
                    <span class="svg-container mgr8">
                        <svg-icon icon-class="password"></svg-icon>
                    </span>
                    <el-input
                        :key="passwordType"
                        ref="password"
                        v-model="loginForm.password"
                        :type="passwordType"
                        placeholder="Password"
                        name="password"
                        tabindex="2"
                        auto-complete="on"
                        @keyup.enter.native="handleLogin"
                    ></el-input>
                    <span class="show-pwd" @click="showPwd">
                        <svg-icon
                            :icon-class="
                                passwordType === 'password' ? 'eye' : 'eye-open'
                            "
                        ></svg-icon>
                    </span>
                </div>
            </el-form-item>

            <!-- 密码错误提示 -->
            <div
                ref="login-prompt-password"
                class="login-row login-error-prompt hide"
                style="margin: 8px auto 8px auto"
            >
                <i class="el-icon-warning-outline fs12"></i>

                <span
                    ref="login-prompt-text"
                    class="fs12"
                    style="margin-left: 4px"
                >
                    登录失败
                </span>
                <!-- <span> password: any</span> -->
            </div>

            <!-- login prompt -->
            <div
                ref="login-prompt"
                class="login-row login-error-prompt hide"
                style="margin: 8px auto 8px auto"
            >
                <i class="el-icon-warning-outline fs12"></i>

                <span
                    ref="login-prompt-text"
                    class="fs12"
                    style="margin-left: 4px"
                >
                    登录失败
                </span>
                <!-- <span> password: any</span> -->
            </div>

            <div class="mg-center login-row">
                <el-button
                    :loading="loading"
                    type="primary"
                    style="width: 100%"
                    @click.native.prevent="handleLogin"
                >
                    Login
                </el-button>
            </div>
        </el-form>
    </div>
</template>

<script>
import { login, fetch_user_info } from '@/api/user';
import { validUsername } from '@/utils/validate';
import { auth_user_menu, set_login_info, set_token } from '@/utils/auth';
import router, { routes } from '@/router';
import { traverse_tree } from '@/utils/route';
import user, {
    USER_RESET_USER_STATE,
    USER_SET_LOGIN_INFO,
    USER_SET_TOKEN,
} from '@/store/modules/user';
import { parse_user_info_response, process_user_menu } from '@/utils/user';
import store from '@/store';
import { when_unknow_err } from '@/utils';
import { Message } from 'element-ui';
export default {
    name: 'Login',
    beforeRouteEnter(to, from, next) {
        store.commit(USER_RESET_USER_STATE);
        next();
    },
    data() {
        const validateUsername = (rule, value, callback) => {
            if (!validUsername(value)) {
                callback(new Error('Please enter the correct user name'));
            } else {
                callback();
            }
        };
        const validatePassword = (rule, value, callback) => {
            if (value.length < 6) {
                callback(
                    new Error('The password can not be less than 6 digits'),
                );
            } else {
                callback();
            }
        };
        return {
            loginForm: {
                password: '123456',
                uid: 'admin',
                username: 'admin',
            },
            loginRules: {
                username: [
                    {
                        required: true,
                        trigger: 'blur',
                        message: '请输入用户名',
                    },
                ],
                password: [
                    {
                        required: true,
                        trigger: 'blur',
                        message: '请输入密码',
                    },
                ],
            },
            loading: false,
            passwordType: 'password',
            redirect: undefined,
        };
    },
    watch: {
        $route: {
            handler: function (route) {
                console.log(route.query);
                this.redirect = route.query && route.query.redirect;
            },
            immediate: true,
        },
    },
    methods: {
        showPwd() {
            if (this.passwordType === 'password') {
                this.passwordType = '';
            } else {
                this.passwordType = 'password';
            }
            this.$nextTick(() => {
                this.$refs.password.focus();
            });
        },
        // handleLogin() {
        //     this.$refs.loginForm.validate((valid) => {
        //         if (valid) {
        //             this.loading = true;
        //             this.$store
        //                 .dispatch("user/login", this.loginForm)
        //                 .then(() => {
        //                     this.$router.push({ path: this.redirect || "/" });
        //                     this.loading = false;
        //                 })
        //                 .catch(() => {
        //                     this.loading = false;
        //                 });
        //         } else {
        //             console.log("error submit!!");
        //             return false;
        //         }
        //     });
        // },
        prompt_uid() {
            $(this.$refs['login-prompt-uid'])
                .removeClass('hide')
                .addClass('login-error-prompt')
                .text('账号为2-20位，只包含字母、数字和下划线');
        },
        reset_uid_prompt() {
            $(this.$refs['login-prompt-uid'])
                .addClass('hide')
                .removeClass('login-error-prompt');
        },

        prompt_password() {
            $(this.$refs['login-prompt-password'])
                .removeClass('hide')
                .addClass('login-error-prompt')
                .text('密码不能为空');
        },
        reset_password_prompt() {
            $(this.$refs['login-prompt-password'])
                .addClass('hide')
                .removeClass('login-error-prompt');
        },
        validate_login() {
            // 账号：字母数字下划线，2-20
            var pattern = /^([a-zA-Z\d]|_){2,20}$/;

            if (!pattern.test(this.loginForm.uid)) {
                this.prompt_uid();
            } else {
                this.reset_uid_prompt();
                return 'success';
            }

            // 密码 不为空
            if (this.loginForm.password == '') {
                this.prompt_password();
            } else {
                this.reset_password_prompt();
                return 'success';
            }
        },
        handleLogin() {
            this.$refs['loginForm'].validate((valid) => {
                if (valid) {
                    this.do_login();
                } else {
                    return false;
                }
            });
        },
        do_login() {
            router.push('/api-admin/index');
        },
    },
};
</script>

<style lang="scss">
@import '~@/styles/common-style.scss';

.login-row {
    width: 33%;
    margin: 20px auto;
}

.title {
    font-size: 26px;
    margin: 0px auto 20px auto;
    text-align: center;
    font-weight: bold;
}

.show-pwd {
    position: absolute;
    right: -20px;
    top: 5 px;
    font-size: 16px;
    /* color: $dark_gray; */
    cursor: pointer;
    user-select: none;
}
.login-pannel {
    padding: 160px 0 0 0;
}

.login-error-prompt {
    font-size: 12px;
    color: #d93025;
}
</style>

<style lang="scss">
/* 修复input 背景不协调 和光标变色 */
/* Detail see https://github.com/PanJiaChen/vue-element-admin/pull/927 */

$bg: #283443;
$light_gray: #fff;
$cursor: #fff;

@supports (-webkit-mask: none) and (not (cater-color: $cursor)) {
    .login-container .el-input input {
        color: $cursor;
    }
}

/* reset element-ui css */
.login-container {
    .el-input {
        display: inline-block;
        height: 47px;
        width: 85%;

        input {
            background: transparent;
            border: 0px;
            -webkit-appearance: none;
            border-radius: 0px;
            padding: 12px 5px 12px 15px;
            color: $light_gray;
            height: 47px;
            caret-color: $cursor;

            &:-webkit-autofill {
                box-shadow: 0 0 0px 1000px $bg inset !important;
                -webkit-text-fill-color: $cursor !important;
            }
        }
    }

    .el-form-item {
        border: 1px solid rgba(255, 255, 255, 0.1);
        background: rgba(0, 0, 0, 0.1);
        border-radius: 5px;
        color: #454545;
    }
}
</style>

<style lang="scss" scoped>
// login background color
// $bg: #2d3a4b;
$bg: #ffffff;
$dark_gray: #889aa4;
$light_gray: #eee;

.login-container {
    min-height: 100%;
    width: 100%;
    background-color: $bg;
    overflow: hidden;

    .login-form {
        position: relative;
        width: 520px;
        max-width: 100%;
        padding: 160px 35px 0;
        margin: 0 auto;
        overflow: hidden;
    }

    .tips {
        font-size: 14px;
        color: #fff;
        margin-bottom: 10px;

        span {
            &:first-of-type {
                margin-right: 16px;
            }
        }
    }

    .svg-container {
        padding: 6px 5px 6px 15px;
        color: $dark_gray;
        vertical-align: middle;
        width: 30px;
        display: inline-block;
    }

    .title-container {
        position: relative;

        .title {
            font-size: 26px;
            color: $light_gray;
            margin: 0px auto 40px auto;
            text-align: center;
            font-weight: bold;
        }
    }

    .show-pwd {
        position: absolute;
        right: 10px;
        top: 7px;
        font-size: 16px;
        color: $dark_gray;
        cursor: pointer;
        user-select: none;
    }
}
</style>
