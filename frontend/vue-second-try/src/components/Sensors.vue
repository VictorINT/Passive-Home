<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue';

interface SensorData {
  temperature: number;
  humidity: number;
  light1: number;
  light2: number;
  light3: number;
  current1: number;
  current2: number;
}

const sensorData = ref<SensorData | null>(null);

const fetchSensorData = async (): Promise<void> => {
  try {
    const response = await fetch('http://localhost:8080/sensors/last', {
      method: 'GET',
      mode: 'cors',
      credentials: 'include',
    });

    if (!response.ok) throw new Error('Error fetching data');
    sensorData.value = await response.json();
  } catch (error) {
    console.error('Fetch error:', error);
    sensorData.value = null;
  }
};

let interval: number;
onMounted(() => {
  fetchSensorData();
  interval = setInterval(fetchSensorData, 5000);
});

onUnmounted(() => {
  clearInterval(interval);
});
</script>

<template>
  <div class="sensors-container">
    <h2 class="sensors-title">Sensor Data</h2>

    <div class="sensors-content" v-if="sensorData">
      <div class="sensors-summary">
        <div class="sensor-card">
          <div class="sensor-icon">🌡️</div>
          <div class="sensor-value">{{ sensorData.temperature.toFixed(1) }}°C</div>
          <div class="sensor-label">Temperature</div>
        </div>
        <div class="sensor-card">
          <div class="sensor-icon">💧</div>
          <div class="sensor-value">{{ sensorData.humidity.toFixed(1) }}%</div>
          <div class="sensor-label">Humidity</div>
        </div>
        <div class="sensor-card">
          <div class="sensor-icon">💡</div>
          <div class="sensor-value">{{ ((sensorData.light1 + sensorData.light2 + sensorData.light3) / 3).toFixed(1) }}</div>
          <div class="sensor-label">Avg Light</div>
        </div>
        <div class="sensor-card">
          <div class="sensor-icon">⚡</div>
          <div class="sensor-value">{{ sensorData.current1.toFixed(2) }}</div>
          <div class="sensor-label">Consumed Power</div>
        </div>
        <div class="sensor-card">
          <div class="sensor-icon">⚡</div>
          <div class="sensor-value">{{ sensorData.current2.toFixed(2) }}</div>
          <div class="sensor-label">Produced power</div>
        </div>
      </div>
    </div>

    <div class="loading" v-else>
      <p>Loading sensor data...</p>
    </div>
  </div>
</template>

<style scoped>

.sensors-container {
  width: 90%;
  max-width: 1200px;
  margin: auto;
}

.sensors-title {
  color: #4caf50;
  font-size: clamp(1.1rem, 3.5vw, 1.4rem);
  margin-bottom: clamp(0.5rem, 2vh, 1rem);
}

.sensors-content {
  display: flex;
  flex-direction: column;
  gap: clamp(1rem, 4vh, 1.5rem);
}

.sensors-summary {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(min(100%, 150px), 1fr));
  gap: clamp(0.5rem, 2vw, 1rem);
  margin-top: clamp(0.5rem, 2vh, 1rem);
}

.sensor-card {
  background-color: #333;
  border-radius: clamp(0.5rem, 2vw, 0.75rem);
  padding: clamp(0.75rem, 3vh, 1rem);
  text-align: center;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.sensor-icon {
  font-size: clamp(1.2rem, 4vw, 1.5rem);
  margin-bottom: clamp(0.25rem, 1vh, 0.5rem);
}

.sensor-value {
  font-size: clamp(1.1rem, 3.5vw, 1.4rem);
  font-weight: bold;
  color: #fff;
}

.sensor-label {
  font-size: clamp(0.75rem, 2.5vw, 0.85rem);
  color: #aaa;
  margin-top: clamp(0.25rem, 1vh, 0.5rem);
}

.sensors-table-container {
  overflow-x: auto;
  background-color: #262626;
  border-radius: clamp(0.5rem, 2vw, 0.75rem);
  -webkit-overflow-scrolling: touch; /* Smooth scrolling on iOS */
}

.sensors-table {
  width: 100%;
  border-collapse: collapse;
}

.sensors-table th, 
.sensors-table td {
  padding: clamp(0.5rem, 2vh, 0.75rem);
  text-align: center;
  border-bottom: 1px solid #333;
}

.sensors-table th {
  background-color: #333;
  color: #4caf50;
  font-weight: bold;
  white-space: nowrap;
  font-size: clamp(0.75rem, 2.5vw, 0.875rem);
}

.sensors-table td {
  color: #ffffff;
  font-size: clamp(0.75rem, 2.5vw, 1rem);
}

.loading {
  display: flex;
  justify-content: center;
  align-items: center;
  height: clamp(100px, 30vh, 150px);
  background-color: #262626;
  border-radius: clamp(0.5rem, 2vw, 0.75rem);
  font-size: clamp(0.875rem, 2.5vw, 1rem);
  color: #aaa;
}

@media (max-width: 640px) {
  .sensors-table th, 
  .sensors-table td {
    padding: 0.5rem 0.25rem;
    font-size: 0.75rem;
  }
  
  .sensors-table th {
    font-size: 0.7rem;
  }
}
</style>