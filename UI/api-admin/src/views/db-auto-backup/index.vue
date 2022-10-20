<template>
    <div>
        <page-header :title="page_title"></page-header>
        <page-content>
            <tool-bar>
                <el-button @click="new_task_dialog.open()">新建任务</el-button>
            </tool-bar>
            <el-table-view :model="table">
                <template slot-scope="scope">
                    <div v-if="scope.col.prop == undefined">
                        <div>
                            <el-button
                                circle
                                icon="el-icon-delete"
                                @click="delete_task(scope.row.id)"
                            ></el-button>
                            <el-button
                                circle
                                icon="el-icon-view"
                                @click="to_task_log(scope.row.id)"
                            ></el-button>
                        </div>
                    </div>

                    <div v-else>
                        {{ scope.row[scope.col.prop] }}
                    </div>
                </template>
            </el-table-view>
        </page-content>

        <div name="dialogs">
            <el-dialog-view :model="new_task_dialog" :width="'50'">
                <template slot-scope="scope">
                    <div class="flex column justify-between">
                        <div class="mgt10">
                            <div class="mgt10"><span>任务名称</span></div>
                            <div class="mgt10">
                                <el-input v-model="scope.data.name"></el-input>
                            </div>
                        </div>
                        <div class="mgt10">
                            <div class="mgt10"><span>任务类型</span></div>
                            <div class="mgt10">
                                <el-select
                                    v-model="scope.data.type"
                                    placeholder="请选择"
                                >
                                    <el-option
                                        v-for="item in task_types"
                                        :key="item"
                                        :label="item"
                                        :value="item"
                                    ></el-option>
                                </el-select>
                            </div>
                        </div>
                        <div
                            v-if="
                                scope.data.type != undefined &&
                                scope.data.type === 'MySQL'
                            "
                            class="mgt10"
                        >
                            <div class="mgt10"><span>MySQL连接信息</span></div>
                            <div class="mgt10 flex">
                                <div class="mgr10">
                                    <el-select
                                        v-loading="
                                            scope.data.db_conn_list_loading
                                        "
                                        v-model="scope.data.db_conn_id"
                                        placeholder="请选择"
                                    >
                                        <el-option
                                            v-for="item in scope.data
                                                .db_conn_list"
                                            :key="item.id"
                                            :label="item.name"
                                            :value="item.id"
                                        ></el-option>
                                    </el-select>
                                </div>
                                <div v-if="!scope.data.new_db_conn_show">
                                    <el-button
                                        @click="scope.model.show_new_conn()"
                                    >
                                        新增连接
                                    </el-button>
                                </div>
                                <div class="flex" v-else>
                                    <div class="mgl10">
                                        <el-button
                                            @click="scope.model.new_db_conn()"
                                        >
                                            确定添加
                                        </el-button>
                                    </div>
                                    <div class="mgl10">
                                        <el-button
                                            @click="scope.model.hide_new_conn()"
                                        >
                                            取消
                                        </el-button>
                                    </div>
                                </div>
                            </div>
                            <div v-show="scope.data.new_db_conn_show">
                                <div class="mgt10 db-conn-form">
                                    <div class="mgt10">
                                        <div class="mgt10">
                                            <span>连接名</span>
                                        </div>
                                        <div class="mgt10">
                                            <el-input
                                                v-model="
                                                    scope.data.new_db_conn.name
                                                "
                                            ></el-input>
                                        </div>
                                    </div>
                                    <div class="mgt10">
                                        <div class="mgt10">
                                            <span>主机</span>
                                        </div>
                                        <div class="mgt10">
                                            <el-input
                                                v-model="
                                                    scope.data.new_db_conn.host
                                                "
                                            ></el-input>
                                        </div>
                                    </div>
                                    <div class="mgt10">
                                        <div class="mgt10">
                                            <span>端口</span>
                                        </div>
                                        <div class="mgt10">
                                            <el-input
                                                v-model="
                                                    scope.data.new_db_conn.port
                                                "
                                            ></el-input>
                                        </div>
                                    </div>
                                    <div class="mgt10">
                                        <div class="mgt10">
                                            <span>用户名</span>
                                        </div>
                                        <div class="mgt10">
                                            <el-input
                                                v-model="
                                                    scope.data.new_db_conn
                                                        .username
                                                "
                                            ></el-input>
                                        </div>
                                    </div>
                                    <div class="mgt10">
                                        <div class="mgt10">
                                            <span>密码</span>
                                        </div>
                                        <div class="mgt10">
                                            <el-input
                                                v-model="
                                                    scope.data.new_db_conn
                                                        .passwd
                                                "
                                            ></el-input>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div
                            v-if="
                                scope.data.type != undefined &&
                                scope.data.type === 'MySQL'
                            "
                            class="mgt10"
                        >
                            <div class="mgt10"><span>数据库名</span></div>
                            <div class="mgt10">
                                <el-input
                                    v-model="scope.data.db_name"
                                ></el-input>
                            </div>
                        </div>

                        <div class="mgt10">
                            <el-button
                                @click="
                                    test_connection(
                                        scope.data.db_conn_id,
                                        scope.data.db_name,
                                    )
                                "
                            >
                                Test connection
                            </el-button>
                        </div>
                        <div class="mgt10">
                            <div class="mgt10"><span>cron表达式</span></div>
                            <div class="mgt10">
                                <input
                                    @input="test_cron(scope.data.cron)"
                                    v-model="scope.data.cron"
                                    :class="cron_input.cur_class"
                                ></input>
                            </div>
                        </div>
                    </div>
                </template>
            </el-dialog-view>

            <el-dialog-view :model="test_conn_result_dialog">
                <template slot-scope="scope">
                    <div>结果</div>
                    <div>{{ scope.data.code }}</div>
                    <div>详情</div>
                    <div>{{ scope.data.msg }}</div>
                </template>
            </el-dialog-view>
        </div>
    </div>
</template>
<script>
import ElDialogView from '@/views/oop_components/ElDialogView.vue';

import pageHeader from '@/views/common/page-header.vue';
import toolBar from '@/views/common/tool-bar.vue';
import pageContent from '@/views/common/page-content.vue';
import ElTableView from '@/views/oop_components/ElTableView.vue';
import { OopElTableModel, OopElDialogModel } from '@/lib/index';
import { StringUtils } from '@/utils';
import { Message } from 'element-ui';

import { create_db_conn, get_db_conns, test_dbconn } from '@/api/db_conn';
import { create_task, delete_task, get_tasks } from '@/api/task';
import { test_cron } from '@/api/cron';
let vue;
export default {
    components: { ElTableView, pageContent, toolBar, pageHeader, ElDialogView },
    data() {
        return {
            new_db_conn_show: false,
            task_types: ['MySQL'],
            page_title: '数据库自动备份',
            new_task_dialog: new OopElDialogModel('新增任务', {
                reset_data() {
                    this.data = {
                        new_db_conn_show: false,
                        name: '',
                        cron: '',
                        type: 'MySQL',
                        db_conn_list_loading: false,
                        db_name: '',
                        db_conn_id: '',
                        new_db_conn: {
                            host: '',
                            port: '',
                            username: '',
                            passwd: '',
                            name: '',
                        },
                        db_conn_list: [
                            {
                                name: 'bj_tc',
                                host: '111.111.111.111',
                                port: '33060',
                                username: 'root',
                                passwd: 'wc123456',
                            },
                        ],
                    };
                },
                before_open() {
                    this.fetch_db_conns();
                    this.do_open();
                },
                enable_commit() {
                    if (
                        this.data.name != undefined &&
                        this.data.name !== '' &&
                        this.data.cron != undefined &&
                        this.data.cron !== '' &&
                        this.data.type != undefined &&
                        this.data.type !== ''
                    ) {
                        if (this.data.type === 'MySQL') {
                            if (
                                !StringUtils.empty(this.data.db_name) &&
                                !StringUtils.empty(this.data.db_conn_id)
                            ) {
                                return true;
                            } else {
                                return false;
                            }
                        }
                        return true;
                    }
                    return false;
                },
                do_commit() {
                    var params = {
                        name: this.data.name,
                        cron: this.data.cron,
                        type: this.data.type,
                        dbConnId: this.data.db_conn_id,
                        dbName: this.data.db_name,
                    };
                    create_task(params)
                        .then(resp => {
                            vue.table.fetch_data();
                        })
                        .catch((resp) => {
                            if (resp.code === 'INVALID_CRON') {
                                Message.error('cron表达式格式不正确');
                            } else if (resp.code === 'TASK_NAME_DUPLICATED') {
                                Message.error('任务名重复');
                            } else {
                                Message.error('创建失败');
                            }
                        });
                    this.close();
                },
                fetch_db_conns() {
                    this.data.db_conn_list_loading = true;
                    get_db_conns().then((resp) => {
                        this.data.db_conn_list = resp.data;
                        this.data.db_conn_list_loading = false;
                    });
                },
                show_new_conn() {
                    this.data.new_db_conn_show = true;
                },
                hide_new_conn() {
                    this.data.new_db_conn_show = false;
                },
                new_db_conn() {
                    let new_db_conn = this.data.new_db_conn;
                    if (
                        StringUtils.empty(new_db_conn.host) ||
                        StringUtils.empty(new_db_conn.port) ||
                        StringUtils.empty(new_db_conn.name) ||
                        StringUtils.empty(new_db_conn.username) ||
                        StringUtils.empty(new_db_conn.passwd)
                    ) {
                        Message.warning('请将连接信息填写完整');
                        return;
                    }

                    if (!StringUtils.isInt(new_db_conn.port)) {
                        Message.warning('端口号只能是整数');
                        return;
                    }

                    this.data.db_conn_list_loading = true;
                    create_db_conn(new_db_conn)
                        .then((resp) => {
                            this.fetch_db_conns();
                            this.data.db_conn_list_loading = false;
                        })
                        .catch((resp) => {
                            Message.error('添加失败，错误码：' + resp.code);
                        });

                    this.hide_new_conn();
                    this.clear_new_conn_form();
                },
            }),
            table: new OopElTableModel('自动备份任务列表', {
                cols: [
                    {
                        prop: 'id',
                        label: '任务id',
                    },
                    {
                        prop: 'name',
                        label: '任务名',
                    },
                    {
                        prop: 'start_time',
                        label: '启动时间',
                    },
                    {
                        prop: 'status',
                        label: '任务状态',
                    },
                    {
                        prop: 'exe_times',
                        label: '执行次数',
                    },
                    {
                        prop: 'cron',
                        label: 'cron表达式',
                    },
                    {
                        prop: undefined,
                        label: '操作',
                    },
                ],
                do_fetch_data() {
                    this.data = [
                        {
                            id: 1,
                            name: '测试任务',
                            start_time: '2022-22-22 22:22:22',
                            status: '进行中',
                            exe_times: 3,
                        },
                    ];

                    get_tasks().then((resp) => {
                        this.data = resp.data;
                        this.fetch_over();
                    });
                },
            }),

            test_conn_result_dialog: new OopElDialogModel('测试结果', {
                reset_data() {
                    this.data = { code: 'unknown', msg: '' };
                },
                before_open(data) {
                    this.data = data;
                    this.do_open();
                },
                enable_commit() {
                    return true;
                },
                do_commit() {
                    this.close();
                },
            }),

            cron_input: {
                green_class: 'green-input',
                red_class: 'red-input',
                cur_class: 'inactive-input',
            },
        };
    },
    computed: {},
    created() {
        vue = this;
        this.table.fetch_data();
    },

    methods: {
        test_cron(cron) {
            if (cron == undefined || cron === '') {
                return;
            }
            cron = window.btoa(cron);
            test_cron({
                cron,
            }).then((resp) => {
                if (resp.data === 'SUCCESS') {
                    this.input_green();
                } else if (resp.data === 'INVALID_CRON') {
                    this.input_red();
                } else {
                    Message.error('未知错误');
                }
            });
        },
        input_green() {
            this.cron_input.cur_class = this.cron_input.green_class;
        },
        input_red() {
            this.cron_input.cur_class = this.cron_input.red_class;
        },
        test_connection(conn_id, dbname) {
            test_dbconn({
                connId: conn_id,
                dbname,
            }).then((resp) => {
                let validation = resp.data;
                this.test_conn_result_dialog.open({
                    code: validation.code,
                    msg: validation.result,
                });
            });
        },
        to_task_log(task_id) {
            this.$router.push({
                path: '/db-auto-backup/log',
                query: {
                    task_id,
                },
            });
        },
        delete_task(task_id) {
            delete_task(task_id).then((resp) => {
                this.table.fetch_data();
            });
        },
        show_new_conn() {
            this.new_db_conn_show = true;
        },
        clear_new_conn_form() {
            this.data.new_db_conn = {
                host: '',
                port: '',
                username: '',
                passwd: '',
                name: '',
            };
        },
        hide_new_conn() {
            this.new_db_conn_show = false;
        },
    },
};
</script>
<style lang="scss" scoped>
@import '~@/styles/common-style.scss';

.db-conn-form {
    border: 1px solid $google-gray-400;
    padding: 10px;
    border-radius: 5px;
    font-size: 12px;
}

input{
    border-radius: 4px;
    line-height: 40px;
    height: 40px;
    padding: 0 15px;
    width: 100%;

    &.red-input {
         border: 1px solid red;
    }

    &.green-input {
        border: 1px solid green;
    }

    &.inactive-input {
        border: 1px solid #DCDFE6;
    }

    &:focus{
        outline: none;
    }
}
</style>

<style lang="css" scoped>
 .red-input .el-input_inner {
    border: 0px;
}

.green-input .el-input_inner {
    border: 0px;
}
</style>
