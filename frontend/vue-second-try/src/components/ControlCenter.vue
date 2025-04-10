<template>
  <div class="control-container">
    <!-- LED & alarm controls -->
    <button :class="['control-button', { 'on': led1 }]" @click="toggleLed(1)">LED 1</button>
    <button :class="['control-button', { 'on': led2 }]" @click="toggleLed(2)">LED 2</button>
    <button :class="['control-button', { 'on': alarmActive }]" @click="toggleAlarm">Alarm</button>

    <!-- Color Picker -->
    <div class="color-picker-container">
      <input id="colorPicker" type="color" v-model="hexColor" />
      <button class="control-button" @click="sendLedColor">Send Color</button>
    </div>

    <!-- RFID Add -->
    <div class="rfid-container">
      <input
        type="text"
        placeholder="Enter RFID tag"
        v-model="rfidInput"
        class="rfid-input"
      />
      <button class="control-button" @click="postRFID">Add RFID</button>
    </div>

    <!-- RFID List -->
    <div class="rfid-list" v-if="rfidList.length">
      <h3>Registered RFID Tags</h3>
      <ul>
        <li v-for="rfid in rfidList" :key="rfid.id">
          <strong>ID:</strong> {{ rfid.id }} — <strong>Tag:</strong> {{ rfid.rfid_tag }}
          <button class="delete-button" @click="deleteRFID(rfid.id)">Delete</button>
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';

const led1 = ref(0);
const led2 = ref(0);
const hexColor = ref('#00ff00');
const alarmActive = ref(false);
const rfidInput = ref('');
const rfidList = ref<{ id: number; rfid_tag: string }[]>([]);

// 🔁 Fetch all RFID tags
const fetchRFIDList = async () => {
  try {
    const res = await fetch('http://localhost:8080/rfid/get', {
      method: 'GET',
      mode: 'cors',
      credentials: 'include',
    });
    rfidList.value = await res.json();
  } catch (error) {
    console.error('Error fetching RFID list:', error);
  }
};

// ➕ Add RFID tag
const postRFID = async () => {
  if (!rfidInput.value.trim()) return;

  const payload = {
    id: null,
    rfid_tag: rfidInput.value.trim(),
  };

  try {
    await fetch('http://localhost:8080/rfid/add', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
      mode: 'cors',
      credentials: 'include',
    });
    rfidInput.value = '';
    await fetchRFIDList();
  } catch (error) {
    console.error('Error posting RFID:', error);
  }
};

// ❌ Delete RFID tag by ID
const deleteRFID = async (id: number) => {
  try {
    await fetch(`http://localhost:8080/rfid/delete/${id}`, {
      method: 'DELETE',
      mode: 'cors',
      credentials: 'include',
    });
    await fetchRFIDList();
  } catch (error) {
    console.error(`Error deleting RFID with id ${id}:`, error);
  }
};

// 🔁 Toggle LEDs
const toggleLed = async (led: number) => {
  if (led === 1) led1.value = led1.value ? 0 : 1;
  if (led === 2) led2.value = led2.value ? 0 : 1;

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

// 🎨 Send LED strip color
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

// 🔔 Alarm control & SSE
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

onMounted(async () => {
  await fetchRFIDList();

  try {
    const res = await fetch('http://localhost:8080/alarm/state', {
      method: 'GET',
      mode: 'cors',
      credentials: 'include',
    });
    alarmActive.value = await res.json();
  } catch (err) {
    console.error("Error fetching alarm state:", err);
  }

  if ("Notification" in window && Notification.permission !== "granted") {
    Notification.requestPermission();
  }

  const eventSource = new EventSource("http://localhost:8080/sse");

  eventSource.addEventListener("ALARM", (event: MessageEvent) => {
    showPopupNotification("ALARM TRIGGERED", "Motion detected while alarm is active!");
  });

  eventSource.onerror = () => {
    console.error("SSE connection error");
    eventSource.close();
  };
});

function showPopupNotification(title: string, body: string) {
  if ("Notification" in window && Notification.permission === "granted") {
    new Notification(title, { body });
  } else {
    alert(`${title}: ${body}`);
  }
}
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

.color-picker-container,
.rfid-container {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.rfid-input {
  padding: 0.5rem;
  font-size: 1rem;
  border-radius: 6px;
  border: 1px solid #ccc;
  flex-grow: 1;
}

.rfid-list {
  margin-top: 1rem;
  background-color: #1e1e1e;
  padding: 1rem;
  border-radius: 6px;
}

.rfid-list h3 {
  margin-bottom: 0.5rem;
  font-size: 1.2rem;
}

.rfid-list ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.rfid-list li {
  padding: 0.3rem 0;
  border-bottom: 1px solid #444;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.delete-button {
  padding: 0.25rem 0.75rem;
  margin-left: 1rem;
  background-color: #c0392b;
  border: none;
  border-radius: 4px;
  color: white;
  cursor: pointer;
  font-size: 0.9rem;
}

.delete-button:hover {
  background-color: #e74c3c;
}
</style>