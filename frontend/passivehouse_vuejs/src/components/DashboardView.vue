<template>
    <div>
        <h1>Sensor Data</h1>
        <div v-if="sensorData">
            <p>ID: {{ sensorData.id }}</p>
            <p>Timestamp: {{ sensorData.timestamp }}</p>
            <p>Temperature: {{ sensorData.temperature }}°C</p>
            <p>Humidity: {{ sensorData.humidity }}%</p>
            <p>Light 1: {{ sensorData.light1 }}</p>
            <p>Light 2: {{ sensorData.light2 }}</p>
            <p>Light 3: {{ sensorData.light3 }}</p>
            <p>Voltage 1: {{ sensorData.voltage1 }}V</p>
            <p>Voltage 2: {{ sensorData.voltage2 }}V</p>
            <p>Current 1: {{ sensorData.current1 }}A</p>
            <p>Current 2: {{ sensorData.current2 }}A</p>
        </div>
        <div v-else>
            <p>Loading...</p>
        </div>
    </div>
</template>

<script>
import axios from 'axios';

export default {
    data() {
        return {
            sensorData: null,
        };
    },
    created() {
        this.fetchSensorData();
    },
    methods: {
        async fetchSensorData() {
            try {
                const token = localStorage.getItem('token');
                if (!token) {
                    console.error('No token found');
                    return;
                }
                console.log('Using token:', token);
                const response = await axios.get('http://localhost:8080/sensors/last', {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    },
                    withCredentials: true // Ensure cookies are sent if needed
                });
                this.sensorData = response.data;
            } catch (error) {
                console.error('Error fetching sensor data:', error);
            }
        }
    }
};
</script>

<style scoped>
h1 {
    font-size: 24px;
    margin-bottom: 20px;
}
p {
    margin: 5px 0;
}
</style>
