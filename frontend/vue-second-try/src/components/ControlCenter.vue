<template>
  <div class="control-container">
    <h2>Main Control</h2>
    <button :class="['control-button', { on: led }]" @click="toggleLed">LED</button>
    <button :class="['control-button', { on: alarmActive }]" @click="toggleAlarm">Alarm</button>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'

const led = ref(0)
const alarmActive = ref(false)

const toggleLed = async () => {
  led.value = led.value ? 0 : 1

  const payload = { led1: led.value } // backend expects 'led1'

  try {
    await fetch('http://localhost:8080/instructions', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
      credentials: 'include',
    })
  } catch (error) {
    console.error('Error sending LED request:', error)
  }
}

const toggleAlarm = async () => {
  alarmActive.value = !alarmActive.value

  try {
    await fetch('http://localhost:8080/alarm/state', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ active: alarmActive.value }),
      credentials: 'include',
    })
  } catch (error) {
    console.error('Error toggling alarm:', error)
  }
}

function showPopupNotification(title: string, body: string) {
  if ("Notification" in window && Notification.permission === "granted") {
    new Notification(title, { body })
  } else {
    alert(`${title}: ${body}`)
  }
}

onMounted(async () => {
  try {
    const res = await fetch('http://localhost:8080/alarm/state', {
      method: 'GET',
      credentials: 'include',
    })
    alarmActive.value = await res.json()
  } catch (err) {
    console.error("Error fetching alarm state:", err)
  }

  if ("Notification" in window && Notification.permission !== "granted") {
    Notification.requestPermission()
  }

  const eventSource = new EventSource("http://localhost:8080/sse")

  eventSource.addEventListener("ALARM", (event: MessageEvent) => {
    showPopupNotification("ALARM TRIGGERED", "Motion detected while alarm is active!")
  })

  eventSource.onerror = () => {
    console.error("SSE connection error")
    eventSource.close()
  }
})
</script>

<style scoped>
.control-container {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  padding: 1rem;
  background-color: #2a2a2a;
  border-radius: 8px;
  color: white;
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
</style>
