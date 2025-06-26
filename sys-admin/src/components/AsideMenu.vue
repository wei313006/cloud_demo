<template>
    <div>
        <el-menu class="el-menu-frame" background-color="#545c64" text-color="#fff" active-text-color="#2590f1"
            @open="handleOpenMenu" @close="handleCloseMenu" :collapse="isCollapse">
            <p class="toolList">
                <b><i class="fold" @click="changeMenuSize">{{ !isCollapse ? "折叠" : "展开" }}</i></b>
            </p>
            <el-menu-item v-on:click="MenuClick(item)" v-for="(item, index) in noChildren" v-bind:key="index"
                :index="item.label">
                <el-icon>
                    <component :is="resolveIcon(item.icon)"></component>
                </el-icon>
                <span slot="title">{{ item.label }}</span>
            </el-menu-item>
            <el-sub-menu v-for="(item, index) in hasChildren" v-bind:key="index" :index="item.label">
                <template #title>
                    <el-icon>
                        <component :is="resolveIcon(item.icon)"></component>
                    </el-icon>
                    <span>{{ item.label }}</span>
                </template>
                <el-menu-item v-on:click="MenuClick(subItem)" v-for="(subItem, subMenuItemIndex) in item.children"
                    v-bind:key="subMenuItemIndex" :index="subItem.label">
                    <el-icon>
                        <component :is="resolveIcon(subItem .icon)"></component>
                    </el-icon>
                    {{ subItem.label }}
                </el-menu-item>
            </el-sub-menu>
        </el-menu>
    </div>
</template>

<script setup lang="ts">
import { reactive, ref, computed ,onMounted} from "vue";
import menudata from "@/menuData/menudata";
import { useRoute, useRouter } from "vue-router";
import * as Icons from '@element-plus/icons-vue'

const dialogVisible = ref(false);
const route = useRoute()
const router = useRouter()
let isCollapse = ref(false);

const handleOpenMenu = (key: string, keyPath: string[]) => { };
const handleCloseMenu = (key: string, keyPath: string[]) => { };

function changeMenuSize() {
    if (isCollapse.value == true) {
        isCollapse.value = false;
    } else {
        isCollapse.value = true;
    }
}

function resolveIcon(iconName: string) {
    return (Icons as Record<string, any>)[iconName] || null
}

function MenuClick(item: any) {
    if (route.path !== item.path && !(route.path === "/home" && item.path === "/")) {
        router.push(item.path);
    }
}

// 计算父菜单
const noChildren: any = computed(() => {
    return menudata.menuData.filter((menu: any) => !menu.children);
});

// 计算有父菜单的子菜单
const hasChildren: any = computed(() => {
    return menudata.menuData.filter((menu: any) => menu.children);
    // .flatMap(menu => menu.children); // 提取所有子菜单项
});

onMounted(() => {
    console.log(menudata);
    
})

</script>

<style scoped>
body .el-menu {
    border-right: none;
    padding-top: 1px;
}

.el-menu-frame:not(.el-menu--collapse) {
    width: 240px;
    min-height: 1000px;
}

.toolList {
    text-align: right;
    margin-right: 10px;
    font-size: 15px;
    color: rgb(224, 224, 224);
}

.menuAside {
    padding: 0;
    margin: 0;
    height: 100%;
}

.fold {
    cursor: pointer;
}
</style>