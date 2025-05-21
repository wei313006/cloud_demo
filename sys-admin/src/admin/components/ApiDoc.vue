<template>
    <div>
        <iframe :src="http.host + '/swagger-ui.html'" style="width: 100%;height: 800px;" frameborder="0"></iframe>
    </div>
</template>

<script setup lang="ts">
import http from '@/request/http';

</script>

<style scoped></style>