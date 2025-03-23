<script setup>
import { ref, onMounted } from 'vue';

const sensorData = ref(null);

const fetchSensorData = async () => {
  try {
    const response = await fetch('http://172.18.0.1:8080/sensors/last');
    if (!response.ok) throw new Error('Fetch error!');
    sensorData.value = await response.json();
  } catch (error) {
    console.error('Fetch error:', error);
  }
};

onMounted(fetchSensorData);
</script>

<template>
  <div class="sensor-container">
    <h2>Latest data</h2>
    <pre v-if="sensorData">{{ sensorData.value }}</pre>
    <p v-else>Loading...</p>
  </div>
</template>

<style scoped>
.sensor-container {
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 10px;
  max-width: 400px;
  margin: auto;
  text-align: center;
}
</style>
