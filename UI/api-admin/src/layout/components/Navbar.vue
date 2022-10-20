<template>
    <div class="navbar">
        <hamburger
            :is-active="sidebar.opened"
            class="hamburger-container"
            @toggleClick="toggleSideBar"
        ></hamburger>

        <breadcrumb class="breadcrumb-container"></breadcrumb>

        <div class="right-menu">
            <el-dropdown class="avatar-container" trigger="click">
                <!-- 头像 -->
                <div class="avatar-wrapper" style="">
                    <img :src="default_user_avator" class="user-avatar" />
                    <i class="el-icon-caret-bottom"></i>
                </div>
                <!-- 下拉菜单 -->
                <el-dropdown-menu slot="dropdown" class="user-dropdown">
                    <router-link to="/my_profile">
                        <el-dropdown-item>我的信息</el-dropdown-item>
                    </router-link>

                    <el-dropdown-item divided @click.native="logout">
                        <span style="display: block">Log Out</span>
                    </el-dropdown-item>
                </el-dropdown-menu>
            </el-dropdown>
        </div>
    </div>
</template>

<script>
import { mapGetters } from 'vuex';
import Breadcrumb from '@/components/Breadcrumb';
import Hamburger from '@/components/Hamburger';
import default_user_avator from '@/assets/default_user_avator.jpg';
import { set_login_info } from '@/utils/auth';
import store from '@/store';
import { USER_RESET_USER_STATE } from '@/store/modules/user';
import {
    copy_routes_for_sidebar,
    routes,
    srcConstantRoutes,
    reset_sidebar_routes,
} from '@/router';
import { cloneDeep } from '@/utils/route';
export default {
    components: {
        Breadcrumb,
        Hamburger,
    },
    computed: {
        ...mapGetters(['sidebar', 'avatar']),
    },
    data() {
        return {
        default_user_avator: default_user_avator,
        };
    },
    methods: {
        toggleSideBar() {
            this.$store.dispatch('app/toggleSideBar');
        },
        logout() {
            reset_sidebar_routes();
            store.commit(USER_RESET_USER_STATE);
            this.$router.push(`/login?redirect=${this.$route.fullPath}`);
        },
    },
};
</script>

<style lang="scss" scoped>
@import '~@/styles/common-style.scss';
.navbar {
    height: 50px;
    overflow: hidden;
    position: relative;
    background: $google-gray-200;
    box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);

    .hamburger-container {
        line-height: 46px;
        height: 100%;
        float: left;
        cursor: pointer;
        transition: background 0.3s;
        -webkit-tap-highlight-color: transparent;

        &:hover {
            background: rgba(0, 0, 0, 0.025);
        }
    }

    .breadcrumb-container {
        float: left;
    }

    .right-menu {
        position: relative;
        right: 45px;
        float: right;
        height: 100%;
        line-height: 50px;

        &:focus {
            outline: none;
        }

        .right-menu-item {
            display: inline-block;
            padding: 0 8px;
            height: 100%;
            font-size: 18px;
            color: #5a5e66;
            vertical-align: text-bottom;

            &.hover-effect {
                cursor: pointer;
                transition: background 0.3s;

                &:hover {
                    background: rgba(0, 0, 0, 0.025);
                }
            }
        }

        .avatar-container {
            .avatar-wrapper {
                margin-top: 5px;
                position: relative;

                .user-avatar {
                    cursor: pointer;
                    width: 40px;
                    height: 40px;
                    border-radius: 20px;
                }

                .el-icon-caret-bottom {
                    cursor: pointer;
                    position: absolute;
                    right: -20px;
                    top: 25px;
                    font-size: 12px;
                }
            }
        }
    }
}
</style>

<style>
.user-dropdown {
}
</style>
