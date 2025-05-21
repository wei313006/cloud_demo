<script setup lang="ts">
import { RouterView } from 'vue-router'
let language = localStorage.getItem('lang')
if (!language) {
  localStorage.setItem('lang', navigator.language)
}

</script>

<template>

  <RouterView />
</template>

<style scoped></style>
