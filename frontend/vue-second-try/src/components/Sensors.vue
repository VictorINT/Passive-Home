<script setup>
import { ref, onMounted, onUnmounted } from 'vue';

// Definim variabilele pentru datele senzorilor
const sensorData = ref(null);

// Funcția care face fetch la API
const fetchSensorData = async () => {
  try {
    const response = await fetch('http://localhost:8080/sensors/last', {
      method: 'GET',
      mode: 'cors', // Permite cross-origin
      credentials: 'include', // Trimite cookie-uri dacă API-ul are autentificare
    });

    if (!response.ok) throw new Error('Eroare la fetch');
    sensorData.value = await response.json(); // Dacă serverul returnează JSON, folosim .json()
  } catch (error) {
    console.error('Fetch error:', error);
    sensorData.value = null;
  }
};

// Actualizează datele la fiecare 5 secunde
let interval;
onMounted(() => {
  fetchSensorData();  // Inițializează datele la încărcarea componentei
  interval = setInterval(fetchSensorData, 5000);  // Actualizează la fiecare 5 secunde
});

onUnmounted(() => {
  clearInterval(interval);  // Curăță intervalul când componenta este distrusă
});
</script>

<template>
  <div>
    <h2>Sensor Data</h2>

    <!-- Tabelul care va conține datele senzorilor -->
    <table v-if="sensorData" border="1" cellspacing="0" cellpadding="10">
      <thead>
        <tr>
          <th>ID</th>
          <th>Timestamp</th>
          <th>Temperature (°C)</th>
          <th>Humidity (%)</th>
          <th>Light 1 (Lux)</th>
          <th>Light 2 (Lux)</th>
          <th>Light 3 (Lux)</th>
          <th>Voltage 1 (V)</th>
          <th>Voltage 2 (V)</th>
          <th>Current 1 (A)</th>
          <th>Current 2 (A)</th>
        </tr>
      </thead>
      <tbody>
        <tr>
          <td>{{ sensorData.id }}</td>
          <td>{{ sensorData.timestamp }}</td>
          <td>{{ sensorData.temperature.toFixed(2) }}</td>
          <td>{{ sensorData.humidity.toFixed(2) }}</td>
          <td>{{ sensorData.light1.toFixed(2) }}</td>
          <td>{{ sensorData.light2.toFixed(2) }}</td>
          <td>{{ sensorData.light3.toFixed(2) }}</td>
          <td>{{ sensorData.voltage1.toFixed(2) }}</td>
          <td>{{ sensorData.voltage2.toFixed(2) }}</td>
          <td>{{ sensorData.current1.toFixed(2) }}</td>
          <td>{{ sensorData.current2.toFixed(2) }}</td>
        </tr>
      </tbody>
    </table>

    <!-- Mesaj de eroare dacă nu sunt date -->
    <p v-else>Se încarcă datele...</p>
  </div>
</template>

<style scoped>
h2 {
  color: #007bff;
  text-align: center;
}

table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 20px;
}

th, td {
  text-align: center;
}

th {
  background-color: #f2f2f2;
}

td {
  font-size: 1.1rem;
}

p {
  text-align: center;
  font-size: 1.2rem;
  color: gray;
}
</style>
