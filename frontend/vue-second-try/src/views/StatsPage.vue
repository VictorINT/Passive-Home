<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import Navbar from '../components/Navbar.vue';
import { Line } from 'vue-chartjs';
import { Chart as ChartJS, CategoryScale, LinearScale, PointElement, LineElement, Title, Tooltip, Legend } from 'chart.js';

// Register Chart.js components
ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, Title, Tooltip, Legend);

interface SensorEntry {
  timestamp: string;
  temperature: number;
  humidity: number;
  light1: number;
  light2: number;
  light3: number;
  current1: number;
  current2: number;
}

const sensorData = ref<SensorEntry[]>([]);

// Prepare chart data for temperature
const temperatureChartData = computed(() => ({
  labels: sensorData.value.map(entry => {
    // Format timestamp or use as is
    return new Date(entry.timestamp).toLocaleTimeString();
  }),
  datasets: [
    {
      label: 'Temperature',
      backgroundColor: 'rgba(255, 115, 0, 0.2)',
      borderColor: '#ff7300',
      data: sensorData.value.map(entry => entry.temperature),
      tension: 0.4
    }
  ]
}));

// Humidity chart data
const humidityChartData = computed(() => ({
  labels: sensorData.value.map(entry => new Date(entry.timestamp).toLocaleTimeString()),
  datasets: [
    {
      label: 'Humidity',
      backgroundColor: 'rgba(0, 196, 159, 0.2)',
      borderColor: '#00c49f',
      data: sensorData.value.map(entry => entry.humidity),
      tension: 0.4
    }
  ]
}));

// Light levels chart data
const lightChartData = computed(() => ({
  labels: sensorData.value.map(entry => new Date(entry.timestamp).toLocaleTimeString()),
  datasets: [
    {
      label: 'Light 1',
      backgroundColor: 'rgba(136, 132, 216, 0.2)',
      borderColor: '#8884d8',
      data: sensorData.value.map(entry => entry.light1),
      tension: 0.4
    },
    {
      label: 'Light 2',
      backgroundColor: 'rgba(130, 202, 157, 0.2)',
      borderColor: '#82ca9d',
      data: sensorData.value.map(entry => entry.light2),
      tension: 0.4
    },
    {
      label: 'Light 3',
      backgroundColor: 'rgba(255, 198, 88, 0.2)',
      borderColor: '#ffc658',
      data: sensorData.value.map(entry => entry.light3),
      tension: 0.4
    }
  ]
}));

// Power consumption chart data
const powerChartData = computed(() => ({
  labels: sensorData.value.map(entry => new Date(entry.timestamp).toLocaleTimeString()),
  datasets: [
    {
      label: 'Current 1',
      backgroundColor: 'rgba(255, 0, 0, 0.2)',
      borderColor: '#ff0000',
      data: sensorData.value.map(entry => entry.current1),
      tension: 0.4
    },
    {
      label: 'Current 2',
      backgroundColor: 'rgba(0, 0, 255, 0.2)',
      borderColor: '#0000ff',
      data: sensorData.value.map(entry => entry.current2),
      tension: 0.4
    }
  ]
}));

// Chart options
const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  scales: {
    y: {
      ticks: { color: '#aaaaaa' },
      grid: { color: '#333333' }
    },
    x: {
      ticks: { color: '#aaaaaa', maxRotation: 45, minRotation: 45 },
      grid: { color: '#333333' }
    }
  },
  plugins: {
    legend: {
      labels: { color: '#ffffff' }
    }
  }
};

const fetchSensorData = async (): Promise<void> => {
  try {
    const response = await fetch('http://localhost:8080/sensors/all', {
      method: 'GET',
      mode: 'cors',
      credentials: 'include',
    });

    if (!response.ok) throw new Error('Error fetching data');
    sensorData.value = await response.json();
  } catch (error) {
    console.error('Fetch error:', error);
    sensorData.value = [];
  }
};

onMounted(fetchSensorData);
</script>

<template>
  <div class="page-container">
    <Navbar />
    <main class="content">
      <section class="section">
        <h1 class="section-title">Sensor Statistics</h1>
        <div v-if="sensorData.length">
          <div class="chart-container">
            <h2>Temperature</h2>
            <div class="chart-wrapper">
              <Line :data="temperatureChartData" :options="chartOptions" />
            </div>
          </div>

          <div class="chart-container">
            <h2>Humidity</h2>
            <div class="chart-wrapper">
              <Line :data="humidityChartData" :options="chartOptions" />
            </div>
          </div>

          <div class="chart-container">
            <h2>Light Levels</h2>
            <div class="chart-wrapper">
              <Line :data="lightChartData" :options="chartOptions" />
            </div>
          </div>

          <div class="chart-container">
            <h2>Power(Produced and Consumed)</h2>
            <div class="chart-wrapper">
              <Line :data="powerChartData" :options="chartOptions" />
            </div>
          </div>
        </div>
        <div v-else class="loading">
          <p>Loading sensor data...</p>
        </div>
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
  max-width: 90vw;
  margin: 0 auto;
  width: 100%;
}

.section {
  margin-bottom: 2rem;
  background-color: #1e1e1e;
  border-radius: 0.75rem;
  padding: 1.5rem;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.section-title {
  color: #4caf50;
  font-size: 1.8rem;
  margin-bottom: 1rem;
  border-bottom: 2px solid #333;
  padding-bottom: 0.5rem;
}

.chart-container {
  margin-bottom: 2rem;
  background-color: #2a2a2a;
  padding: 1rem;
  border-radius: 0.75rem;
}

.chart-wrapper {
  height: 300px;
  position: relative;
}

.loading {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 150px;
  background-color: #262626;
  border-radius: 0.75rem;
  font-size: 1rem;
  color: #aaa;
}
</style>