<template>
  <div class="control-container">
    <button 
      :class="['control-button', { 'on': led1 }]" 
      @click="toggleLed(1)"
    >
      LED 1
    </button>
    <button 
      :class="['control-button', { 'on': led2 }]" 
      @click="toggleLed(2)"
    >
      LED 2
    </button>
    <button 
      :class="['control-button', { 'on': alarmActive }]" 
      @click="toggleAlarm"
    >
      Alarm
    </button>

    <div class="color-picker-container">
      <input id="colorPicker" type="color" v-model="hexColor" />
      <button class="control-button" @click="sendLedColor">
        Send Color
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';

const led1 = ref<number>(0);
const led2 = ref<number>(0);
const hexColor = ref<string>('#00ff00');
const alarmActive = ref<boolean>(false);

const toggleLed = async (led: number) => {
  if (led === 1) {
    led1.value = led1.value ? 0 : 1;
  }
  if (led === 2) {
    led2.value = led2.value ? 0 : 1;
  }

  const payload = led === 1 ? { led1: led1.value } : { led2: led2.value };

  try {
    await fetch('http://localhost:8080/instructions', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
      mode: 'cors',
      credentials: 'include',
    });
  } catch (error) {
    console.error('Error sending LED request:', error);
  }
};

const sendLedColor = async () => {
  const rgb = hexToRgb(hexColor.value);
  if (!rgb) return;

  const payload = { ledband: [rgb.r, rgb.g, rgb.b] };

  try {
    await fetch('http://localhost:8080/instructions', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
      mode: 'cors',
      credentials: 'include',
    });
  } catch (error) {
    console.error('Error sending LED color:', error);
  }
};

function hexToRgb(hex: string): { r: number; g: number; b: number } | null {
  const result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
  return result
    ? {
        r: parseInt(result[1], 16),
        g: parseInt(result[2], 16),
        b: parseInt(result[3], 16),
      }
    : null;
}

const toggleAlarm = async () => {
  alarmActive.value = !alarmActive.value;

  try {
    await fetch('http://localhost:8080/alarm/state', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ active: alarmActive.value }),
      mode: 'cors',
      credentials: 'include',
    });
  } catch (error) {
    console.error('Error toggling alarm:', error);
  }
};

// 🔔 SSE + notificări
onMounted(async () => {
  // Inițializare stare alarmă
  try {
    const res = await fetch('http://localhost:8080/alarm/state', {
      method: 'GET',
      mode: 'cors',
      credentials: 'include',
    });
    const state = await res.json();
    alarmActive.value = state;
  } catch (err) {
    console.error("Error fetching alarm state:", err);
  }

  // Permisiune notificări
  if ("Notification" in window && Notification.permission !== "granted") {
    Notification.requestPermission().then(permission => {
    console.log("Notification permission:", permission);
  });
}

  // Conectare SSE
  const eventSource = new EventSource("http://localhost:8080/sse");

  eventSource.addEventListener("ALARM", (event: MessageEvent) => {
    console.log("SSE ALARM event:", event.data);
    showPopupNotification("ALARM TRIGGERED", "Motion detected while alarm is active!");
  });

  eventSource.onerror = (err) => {
    console.error("SSE error:", err);
    eventSource.close();
  };
});

function showPopupNotification(title: string, body: string) {
  if ("Notification" in window && Notification.permission === "granted") {
    console.log("Creating notification:", title, body);
    new Notification(title, {
      body
    });
  } else {
    alert(`${title}: ${body}`);
  }
}
</script>

<style scoped>
.color-picker-container {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-top: 1rem;
  color: white;
}

.control-container {
  display: flex;
  gap: 1rem;
  padding: 1rem;
  background-color: #2a2a2a;
  border-radius: 8px;
  flex-wrap: wrap;
}

.control-button {
  padding: 0.75rem 1.5rem;
  font-size: 1rem;
  font-weight: bold;
  border: none;
  cursor: pointer;
  background-color: #555;
  color: white;
  border-radius: 6px;
  transition: all 0.3s ease;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
}

.control-button:hover {
  background-color: #666;
  transform: translateY(-2px);
}

.control-button.on {
  background-color: #4caf50;
  box-shadow: 0 0 10px rgba(76, 175, 80, 0.6);
}

.control-button.on:hover {
  background-color: #5dbd60;
}
</style>
