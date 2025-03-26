<script setup>
import { ref, onMounted, onUnmounted } from 'vue';

const sensorData = ref(null);

const fetchSensorData = async () => {
  try {
    const response = await fetch('http://localhost:8080/sensors/last', {
      method: 'GET',
      mode: 'cors',
      credentials: 'include',
    });

    if (!response.ok) throw new Error('Eroare la fetch');
    sensorData.value = await response.json();
  } catch (error) {
    console.error('Fetch error:', error);
    sensorData.value = null;
  }
};

let interval;
onMounted(() => {
  fetchSensorData();
  interval = setInterval(fetchSensorData, 5000);
});

onUnmounted(() => {
  clearInterval(interval);
});
</script>

<template>
  <div class="container">
    <h2>Sensor Data</h2>

    <div class="table-container" v-if="sensorData">
      <table>
        <thead>
          <tr>
            <th>Temperature (°C)</th>
            <th>Humidity (%)</th>
            <th>Avg Light (Lux)</th>
            <th>Generated Current (A)</th>
            <th>Consumed Current (A)</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>{{ sensorData.temperature.toFixed(2) }}</td>
            <td>{{ sensorData.humidity.toFixed(2) }}</td>
            <td>{{ ((sensorData.light1 + sensorData.light2 + sensorData.light3) / 3).toFixed(2) }}</td>
            <td>{{ sensorData.current1.toFixed(2) }}</td>
            <td>{{ sensorData.current2.toFixed(2) }}</td>
          </tr>
        </tbody>
      </table>
    </div>

    <p v-else>Loading data...</p>
  </div>
</template>

<style scoped>
.container {
  max-width: 800px;
  margin: 50px auto;
  padding: 20px;
  background: #1e1e1e;
  color: #ffffff;
  border-radius: 10px;
  box-shadow: 0 0 10px rgba(255, 255, 255, 0.1);
  text-align: center;
}

h2 {
  color: #4caf50;
  margin-bottom: 20px;
}

.table-container {
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
  background: #2a2a2a;
  border-radius: 10px;
  overflow: hidden;
}

th, td {
  padding: 15px;
  text-align: center;
}

th {
  background-color: #333;
  color: #4caf50;
  font-weight: bold;
}

td {
  background-color: #2a2a2a;
  color: #ffffff;
  font-size: 1.1rem;
}

tbody tr:hover {
  background-color: #3a3a3a;
}

p {
  font-size: 1.2rem;
  color: #bbb;
}
</style>
