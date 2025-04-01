<script setup lang="ts">
import { defineComponent } from 'vue';
import Navbar from '../components/Navbar.vue';
import Sensors from '../components/Sensors.vue';
import ControlCenter from '../components/ControlCenter.vue';
</script>

<template>
  <div class="page-container">
    <Navbar />
    <main class="content">
      <section class="section">
        <h1 class="section-title">Your House</h1>
        <Sensors />
      </section>
      
      <section class="section">
        <h1 class="section-title">Control Center</h1>
        <ControlCenter />
      </section>
    </main>
  </div>
</template>

<style scoped>
.page-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: #121212;
  color: #ffffff;
}

.content {
  /* padding: clamp(1rem, 5vw, 2rem); */
  max-width: 90vw;
  margin: 0 auto;
  width: 100%;
}

.section {
  margin-bottom: clamp(1rem, 5vh, 2rem);
  background-color: #1e1e1e;
  border-radius: clamp(0.5rem, 2vw, 0.75rem);
  padding: clamp(1rem, 3vw, 1.5rem);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.section-title {
  color: #4caf50;
  font-size: clamp(1.2rem, 4vw, 1.8rem);
  margin-bottom: clamp(0.75rem, 3vh, 1.5rem);
  border-bottom: 2px solid #333;
  padding-bottom: clamp(0.25rem, 1vh, 0.5rem);
}

@media (min-width: 768px) {
  .content {
    max-width: 85vw;
  }
}

@media (min-width: 1200px) {
  .content {
    max-width: 1200px;
  }
}
</style>