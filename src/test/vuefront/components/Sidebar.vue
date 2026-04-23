<template>
  <aside class="sidebar">
    <div class="sidebar-header">
      <h3>动漫目录</h3>
    </div>
    <div class="list-wrapper">
      <div v-for="item in list" :key="item.id"
           :class="['sidebar-item', { active: modelValue?.id === item.id }]"
           @click="$emit('update:modelValue', item)">
        <span class="title">{{ item.title }}</span>
        <span class="score">⭐ {{ item.averageLovepoint.toFixed(1) }}</span>
      </div>
    </div>
  </aside>
</template>

<script setup>
defineProps(['list', 'modelValue']);
defineEmits(['update:modelValue']);
</script>