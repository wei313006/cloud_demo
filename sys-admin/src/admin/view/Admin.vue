<template>
    <div>
        <el-container>
            <el-aside width="auto">
                <AsideMenu ref="AsideMenu"></AsideMenu>
            </el-aside>
            <el-container>
                <el-header>
                    <Header ref="Header"></Header>
                </el-header>
                <el-main>
                    <RouterView />
                </el-main>
            </el-container>
        </el-container>
    </div>
</template>

<script setup lang="ts">
import { RouterView } from 'vue-router';
import AsideMenu from './AsideMenu.vue'
import Header from './Header.vue'

</script>

<style scoped>
.el-header {
    padding: 0;
    margin: 0;
}
</style>