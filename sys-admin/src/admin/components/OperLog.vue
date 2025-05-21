<template>
    <div>
        <div class="operateLogInfoBox">
            <div class="operateLogInfoItem" v-for="(item, index) in logsData" :key="index">
                ({{ item.content }}) 操作时间 => ({{ item.time }})
            </div>
        </div>
        <div style="display: flex; justify-content: center; align-items: center;margin-top: 10px;">
            <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize"
                :page-sizes="[40, 80, 120, 160]" :size="pageSize" :disabled="false"
                layout="total, sizes, prev, pager, next, jumper" :total="totalSize" @size-change="handleSizeChange"
                @current-change="handleCurrentChange" />
        </div>
    </div>
</template>

<script setup lang="ts">
import api from '@/request/api';
import { ElMessage } from 'element-plus';
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';

let router = useRouter()
let pageSize = ref(40)
let currentPage = ref(1)
let totalSize = ref(0)
let logsData: any = ref([])


async function findLogsByPage() {
    let cond = {
        begin: currentPage.value,
        size: pageSize.value
    }
    try {
        let resp: any = await api.findLogsByPage(cond)
        if (resp.code === 10041) {
            logsData.value = resp.data.data
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

const handleSizeChange = async (val: number) => {
    pageSize.value = val
    await findLogsByPage()
}
const handleCurrentChange = async  (val: number) => {
    currentPage.value = val
    await findLogsByPage()
}

onMounted(async () => {
    await findLogsByPage()
})

</script>

<style scoped>
.operateLogInfoBox {
    margin: 0 auto;
    width: 95%;
    min-height: 680px;
    border-bottom: 1px solid #ccc;
    user-select: none;
}

.operateLogInfoItem {
    float: left;
    margin-left: 30px;
    min-width: 500px;
    height: 25px;
    line-height: 25px;
    font-size: 14px;
}

.paging {
    clear: both;
}
</style>