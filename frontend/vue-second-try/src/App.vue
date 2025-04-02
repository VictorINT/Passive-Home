<template>
  <div class="app">
    <!-- This is what renders your current route component -->
    <router-view />
  </div>
</template>

<script setup lang="ts">
// Any app-wide setup can go here
</script>

<style>
/* Global styles */
body {
  margin: 0;
  padding: 0;
  font-family: Arial, sans-serif;
  background-color: #121212;
  color: #ffffff;
}

.app {
  min-height: 100vh;
}
</style>