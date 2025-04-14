<template>
    <div class="control-container">
      <Navbar />
      <h2>RFID Control</h2>
  
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
  