<script setup>
// 根组件：登录页/布局页切换带 fade-slide 过渡
</script>

<template>
  <router-view v-slot="{ Component }">
    <transition name="fade-slide" mode="out-in">
      <component :is="Component" />
    </transition>
  </router-view>
</template>
