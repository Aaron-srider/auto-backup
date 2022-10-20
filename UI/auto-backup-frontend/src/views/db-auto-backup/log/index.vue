<template>
    <div>
        <page-header :title="page_title"></page-header>
        <page-content>
            <tool-bar></tool-bar>
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
                        <div v-if="scope.col.prop === 'duration'">
                            <readable-display
                                :type="'duration'"
                                :value="scope.row[scope.col.prop]"
                            ></readable-display>
                        </div>
                        <div v-else>
                            {{ scope.row[scope.col.prop] }}
                        </div>
                    </div>
                </template>
            </el-table-view>
        </page-content>
    </div>
</template>
<script>
import ElDialogView from '@/views/oop_components/ElDialogView.vue';

import pageHeader from '@/views/common/page-header.vue';
import toolBar from '@/views/common/tool-bar.vue';
import readableDisplay from '@/views/common/readable-display.vue';
import pageContent from '@/views/common/page-content.vue';
import ElTableView from '@/views/oop_components/ElTableView.vue';
import { OopElTableModel, OopElDialogModel } from '@/lib/index';
import { StringUtils } from '@/utils';
import { Message } from 'element-ui';
import { get_task_logs } from '@/api/task';
var vue;
export default {
    components: {
        ElTableView,
        pageContent,
        toolBar,
        pageHeader,
        ElDialogView,
        readableDisplay,
    },
    data() {
        return {
            table: new OopElTableModel('日志列表', {
                cols: [
                    {
                        prop: 'id',
                        label: 'logId',
                    },
                    {
                        prop: 'time',
                        label: '执行时间',
                    },
                    {
                        prop: 'msg',
                        label: '信息',
                    },
                    {
                        prop: 'duration',
                        label: '持续时间',
                    },
                    {
                        prop: 'status',
                        label: '执行结果',
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
                    get_task_logs({
                        taskId: vue.$route.query.task_id,
                    }).then((resp) => {
                        this.data = resp.data;
                        this.fetch_over();
                    });
                },
            }),
        };
    },
    created() {
        vue = this;
        this.table.fetch_data();
    },
    computed: {},
    methods: {},
};
</script>
<style lang="scss" scoped></style>
