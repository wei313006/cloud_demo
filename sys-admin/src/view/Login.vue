<template>
    <div>
        <div class="mainPage" @keyup.enter="listnerEnter">
            <main class="loginPage">
                <br />
                <h2 style="text-align: center; color: gray">xxx后台管理</h2>
                <el-input class="inputItem" :prefix-icon="UserFilled" v-model="loginInfo.username" maxlength="30"
                    show-word-limit>
                </el-input>
                <br />
                <el-input ref="test" class="inputItem" :prefix-icon="Hide" v-model="loginInfo.password" maxlength="30"
                    show-password>
                </el-input>
                <el-input class="checkCodeimp" :prefix-icon=Lock v-model="loginInfo.checkCode" maxlength="4">
                </el-input>
                <img ref="checkCodeRef" class="checkCode" v-on:click="changeCheckCode()" src="" alt=""
                    title="点击更换验证码" />
                <br />
                <el-button style="margin-top: 25px" v-on:click="login" class="inputItem" type="primary">登录</el-button>
            </main>
        </div>
    </div>
</template>

<script setup lang="ts">
import api from '@/request/api';
import { onMounted, ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import { UserFilled, Hide, Lock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus';
import { v4 as uuidv4 } from 'uuid';

let loginInfo: any = reactive({
    username: '',
    password: '',
    checkCode: '',
    checkCodeId: '',
})
const checkCodeRef = ref<HTMLImageElement | null>(null);
const router = useRouter()

function listnerEnter() {
    login()
}

function changeCheckCode() {
    if (checkCodeRef.value) {
        const uuid = uuidv4();
        localStorage.setItem('checkCode-uniqueId', uuid);
        checkCodeRef.value.src = api.checkCode(uuid);
    }
}

async function login() {
    loginInfo.checkCodeId = localStorage.getItem("checkCode-uniqueId")
    try {
        let resp: any = await api.managerLogin(loginInfo)
        if (resp.code == 10041) {
            localStorage.setItem('accessToken', resp.data)
            ElMessage({
                message: resp.msg,
                type: 'success',
            })
            setTimeout(() => {
                router.push('/admin/verified/datamanager')
            }, 1500)
        } else {
            changeCheckCode()
            loginInfo.password = ''
            ElMessage({
                message: resp.msg,
                type: 'warning',
            })
        }
    } catch (error) {
        ElMessage({
            message: '请求失败 ： ' + error,
            type: 'warning',
        })
    }
}

onMounted(() => {
    changeCheckCode()

    document.addEventListener('keypress', function (e) {
        if (e.keyCode === 13 || e.which === 13) {
            // enter键被按下的处理逻辑
            // ...
        }
    });
})


</script>

<style scoped>
.loginPage {
    position: relative;
    margin: 150px auto;
    width: 400px;
    height: 380px;
    border-radius: 5px;
    background-color: white;
    box-shadow: 5px 5px 5px 5px #ccc
}

.mainPage {
    padding-top: 1px;
    /* width: 1519px;
    height: 698px; */
}

.loginPage .inputItem {
    width: 300px;
    height: 35px;
    margin: 15px 13%;
}

.loginPage .checkCodeimp {
    width: 150px;
    height: 35px;
    margin: 15px 13%;
}

.checkCode {
    position: absolute;
    bottom: 25%;
    right: 12%;
    width: 130px;
    height: 48px;
    /* background-color: aqua; */
}

.Register {
    text-decoration: none;
    float: right;
    margin-right: 10px;
    color: #454a55;
}

.Register:hover {
    color: orange;
}
</style>