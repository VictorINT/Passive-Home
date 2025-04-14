<template>
  <Navbar />
  <div class="section">
    <h1 class="section-title">RFID Control</h1>
    <div class="rfid-container">
      <input
        type="text"
        placeholder="Enter RFID tag"
        v-model="rfidInput"
        class="rfid-input"
      />
      <button class="control-button" @click="postRFID">Add RFID</button>
    </div>

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
import { ref, onMounted } from 'vue'
import Navbar from '../components/Navbar.vue';

const rfidInput = ref('')
const rfidList = ref<{ id: number; rfid_tag: string }[]>([])

const fetchRFIDList = async () => {
  const res = await fetch('http://localhost:8080/rfid/get', {
    method: 'GET',
    credentials: 'include',
  })
  rfidList.value = await res.json()
}

const postRFID = async () => {
  if (!rfidInput.value.trim()) return

  await fetch('http://localhost:8080/rfid/add', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ id: null, rfid_tag: rfidInput.value }),
    credentials: 'include',
  })

  rfidInput.value = ''
  await fetchRFIDList()
}

const deleteRFID = async (id: number) => {
  await fetch(`http://localhost:8080/rfid/delete/${id}`, {
    method: 'DELETE',
    credentials: 'include',
  })
  await fetchRFIDList()
}

onMounted(fetchRFIDList)
</script>

<style scoped>
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

.rfid-container {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  flex-wrap: wrap;
  margin-bottom: 1rem;
}

.rfid-input {
  padding: 0.5rem;
  font-size: 1rem;
  border-radius: 6px;
  border: 1px solid #ccc;
  flex-grow: 1;
  background-color: #333;
  color: white;
}

.control-button {
  padding: 0.5rem 1rem;
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

.rfid-list {
  background-color: #2a2a2a;
  padding: 1rem;
  border-radius: 6px;
}

.rfid-list ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.rfid-list li {
  padding: 0.5rem 0;
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