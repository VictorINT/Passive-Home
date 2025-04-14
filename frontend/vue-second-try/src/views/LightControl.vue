<template>
  <div class="control-container">
    <Navbar />
    <h2>LED Strip Control</h2>

    <div class="color-picker-container">
      <input id="colorPicker" type="color" v-model="hexColor" />

      <div class="led-range-container">
        <input
          type="number"
          v-model.number="firstLed"
          placeholder="First LED"
          min="0"
          class="range-input"
        />
        <input
          type="number"
          v-model.number="lastLed"
          placeholder="Last LED"
          :min="firstLed"
          class="range-input"
        />
      </div>

      <button class="control-button" @click="sendLedColor">
        Send Color
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import Navbar from '../components/Navbar.vue'

const hexColor = ref('#00ff00')
const firstLed = ref(0)
const lastLed = ref(71)

const sendLedColor = async () => {
  const rgb = hexToRgb(hexColor.value)
  if (!rgb) return

  const payload = {
    ledband: [firstLed.value, lastLed.value, rgb.r, rgb.g, rgb.b],
  }

  try {
    await fetch('http://localhost:8080/instructions', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
      credentials: 'include',
    })
  } catch (error) {
    console.error('Failed to send LED color:', error)
  }
}

function hexToRgb(hex: string): { r: number; g: number; b: number } | null {
  const result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex)
  return result
    ? {
        r: parseInt(result[1], 16),
        g: parseInt(result[2], 16),
        b: parseInt(result[3], 16),
      }
    : null
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

.color-picker-container {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  align-items: flex-start;
}

.led-range-container {
  display: flex;
  gap: 0.5rem;
  align-items: center;
}

.range-input {
  width: 6rem;
  padding: 0.5rem;
  font-size: 1rem;
  border-radius: 6px;
  border: 1px solid #ccc;
  background-color: #333;
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
</style>
