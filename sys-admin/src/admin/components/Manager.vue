<template>
    <div>

        <div style="text-align: center">
            <el-button type="primary" size="small" plain @click="manager = {}, addDialog = true">添加管理员
            </el-button>
        </div>

        <el-table ref="multipleTable" :data="managerData" tooltip-effect="dark" align="center" style="width: 100%">
            <el-table-column prop="id" align="center" label="标识符" width="180">
            </el-table-column>
            <el-table-column prop="username" align="center" label="管理员名字" width="120">
            </el-table-column>
            <el-table-column prop="status" align="center" label="状态" width="120">
                <template v-slot="scope">
                    {{ scope.row.status == '1' ? "启用" : "禁用" }}
                </template>
            </el-table-column>
            <el-table-column prop="registerTime" align="center" label="注册时间" width="180">
            </el-table-column>
            <el-table-column prop="ip" align="center" label="登录ip" width="200">
            </el-table-column>
            <el-table-column prop="rid" align="center" label="角色" width="200">
                <template v-slot="scope">
                    <span v-if="scope.row.rid == '1'"> {{ "管理员" }}</span>
                    <span v-if="scope.row.rid == '2'"> {{ "超级管理员" }}</span>
                </template>
            </el-table-column>
            <el-table-column align="center" label="操作" width="280" fixed="right">
                <template v-slot="scope">
                    <el-button type="info" size="small" plain @click="manager = scope.row, updateDialog = true">编辑账号密码
                    </el-button>
                    <el-button type="warning" size="small" plain @click="manager = scope.row, managerRoleDialog = true">
                        编辑权限
                    </el-button>
                    <el-button type="danger" size="small" plain @click="del(scope.row.id)">删除
                    </el-button>
                </template>
            </el-table-column>
        </el-table>

        <div style="display: flex; justify-content: center; align-items: center;margin-top: 10px;">
            <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize"
                :page-sizes="[10, 20, 30, 40]" :size="pageSize" :disabled="false"
                layout="total, sizes, prev, pager, next, jumper" :total="totalSize" @size-change="handleSizeChange"
                @current-change="handleCurrentChange" />
        </div>

        <el-dialog title="修改账号密码" v-model="updateDialog" width="25%">
            <div class="videoBox">
                <p>账号：</p>
                <el-input v-model="manager.username" size="" placeholder="账号："></el-input>
                <p>密码：</p>
                <el-input v-model="manager.password" size="" placeholder="密码："></el-input>
            </div>
            <div slot="footer" class="dialog-footer" style="text-align: right;margin-top: 10px;">
                <el-button @click="updateDialog = false, manager = {}">取 消</el-button>
                <el-button type="primary" @click="update">确 定</el-button>
            </div>
        </el-dialog>

        <el-dialog title="添加管理员" v-model="addDialog" width="25%">
            <div class="videoBox">
                <p>账号：</p>
                <el-input v-model="manager.username" size="" placeholder="账号："></el-input>
                <p>密码：</p>
                <el-input v-model="manager.password" size="" placeholder="密码："></el-input>
                <p>状态：</p>
                <el-switch v-model="manager.status" active-color="#13ce66" inactive-color="#ff4949" active-value="1"
                    active-text="开启" inactive-text="禁用" inactive-value="0">
                </el-switch>
                <p>角色：</p>
                <el-select v-model="manager.rid" placeholder="请选择角色">
                    <el-option v-for="item in roles" :key="item.value" :label="item.label" :value="item.value">
                    </el-option>
                </el-select>
            </div>
            <div slot="footer" class="dialog-footer" style="text-align: right;margin-top: 10px;">
                <el-button @click="addDialog = false, manager = {}">取 消</el-button>
                <el-button type="primary" @click="insert">确 定</el-button>
            </div>
        </el-dialog>


        <el-dialog title="权限控制" v-model="managerRoleDialog" width="30%">
            <div style="margin: 10px auto; width: 70%; 
            text-align: left; font-size: 18px; font-weight: bold;
          ">
                <div>管理员标识：{{ manager.id }}</div>
                <div>管理员名字：{{ manager.username }}</div>
                <div>管理员状态：{{ manager.status == 0 ? "禁用" : "启用" }}</div>
                <div>
                    管理员角色：{{ manager.rid == 2 ? "超级管理员" : "普通管理员" }}
                </div>
                <div style="text-align: center; margin-top: 10px">
                    <el-select size="large" v-model="manager.rid" placeholder="请选择角色">
                        <el-option v-for="item in roles" :key="item.value" :label="item.label" :value="item.value">
                        </el-option>
                    </el-select>
                </div>
                <p>状态：</p>
                <el-switch v-model="manager.status" active-color="#13ce66" inactive-color="#ff4949" active-value="1"
                    active-text="开启" inactive-text="禁用" inactive-value="0">
                </el-switch>
            </div>

            <div slot="footer" style="margin-top: 20px; text-align: right;">
                <el-button @click="managerRoleDialog = false" size="mini">取 消</el-button>
                <el-button type="primary" @click="updateRole" size="mini">确 定</el-button>
            </div>
        </el-dialog>

    </div>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import api from '@/request/api';
import router from '@/router';

let managerData = ref([])
let manager: any = ref({})
let addDialog = ref(false)
let updateDialog = ref(false)
let managerRoleDialog = ref(false)
let pageSize = ref(10)
let currentPage = ref(1)
let totalSize = ref(0)
const roles = ref([
    {
        label: "管理员（查看权限）",
        value: 1,
    },
    {
        label: "超级管理员（所有权限）",
        value: 2,
    },
])


async function del(id: number) {
    ElMessageBox.confirm(
        '确认删除吗?',
        '删除数据',
        {
            confirmButtonText: '确认',
            cancelButtonText: '取消',
            type: 'warning',
        }
    )
        .then(async () => {
            try {
                let resp: any = await api.delManager(id)
                if (resp.code === 10021) {
                    ElMessage({
                        message: resp.msg,
                        type: 'success'
                    })
                    findManagerByPage()
                    return
                }
                ElMessage({
                    message: resp.msg,
                    type: 'error'
                })
            } catch (error) {
                console.log(error);
            }
        })
        .catch(() => {
            ElMessage({
                type: 'info',
                message: '取消删除',
            })
        })
}

async function updateRole() {
    try {
        let resp: any = await api.modifyManagerRole(manager.value)
        if (resp.code === 10031) {
            ElMessage({
                message: resp.msg,
                type: 'success'
            })
            manager.value = ref({})
            managerRoleDialog.value = false
            findManagerByPage()
            return
        }
        ElMessage({
            message: resp.msg,
            type: 'warning'
        })
    } catch (error) {
        console.log(error);
    }
}

async function insert() {
    try {
        let resp: any = await api.registerManager(manager.value)
        if (resp.code === 10011) {
            ElMessage({
                message: resp.msg,
                type: 'success'
            })
            manager.value = ref({})
            addDialog.value = false
            findManagerByPage()
            return
        }
        ElMessage({
            message: resp.msg,
            type: 'warning'
        })
    } catch (error) {
        console.log(error);
    }
}

async function findManagerByPage() {
    let cond = {
        begin: currentPage.value,
        size: pageSize.value
    }
    try {
        let resp: any = await api.findManagerByPage(cond)
        if (resp.code === 10041) {
            managerData.value = resp.data.data
            totalSize.value = resp.data.count
            return
        }
        if (resp.code == 20090) {
         router.push("/admin/authentication")
            return
        }
        ElMessage({
            message: resp.msg,
            type: 'warning'
        })
    } catch (error) {
        ElMessage({
            message: '请求错误 ：' + error,
            type: 'warning'
        })
    }
}

async function update() {
    try {
        let resp: any = await api.modifyManager(manager.value)
        if (resp.code === 10031) {
            ElMessage({
                message: resp.msg,
                type: 'success'
            })
            manager.value = ref({})
            updateDialog.value = false
            findManagerByPage()
            return
        }

        ElMessage({
            message: resp.msg,
            type: 'warning'
        })
    } catch (error) {
        console.log(error);
    }
}


const handleSizeChange = (val: number) => {
    pageSize.value = val
    findManagerByPage()
}
const handleCurrentChange = (val: number) => {
    currentPage.value = val
    findManagerByPage()
}

onMounted(async () => {
    findManagerByPage()
})


</script>

<style scoped>
.videoBox {
    width: 80%;
    margin: 0 auto;
}
</style>