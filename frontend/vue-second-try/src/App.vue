<script setup>
import Sensors from './components/Sensors.vue';
</script>

<template>
  <div class="app-container">
    <div class="dashboard-container">
      <h1>Smart Home Dashboard</h1>
      <Sensors />
    </div>
  </div>
</template>

<style scoped>
.app-container {
  background: #121212;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  /* padding: 20px; */
}

/* .dashboard-container {
  max-width: 900px;
  padding: 20px;
  background: #1e1e1e;
  color: #ffffff;
  border-radius: 10px;
  box-shadow: 0 0 10px rgba(255, 255, 255, 0.1);
  text-align: center;
} */

h1 {
  color: #4caf50;
  margin-bottom: 20px;
}
</style>