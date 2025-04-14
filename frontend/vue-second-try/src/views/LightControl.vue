<template>
  <Navbar />
  <div class="section">
    <h1 class="section-title">LED Strip Control</h1>

    <div class="color-picker-container">
      <input id="colorPicker" type="color" v-model="hexColor" />

      <!-- Manual Input Section -->
      <div class="manual-range-container">
        <input
          type="number"
          v-model.number="manualStart"
          placeholder="First LED"
          min="0"
          class="range-input"
        />
        <input
          type="number"
          v-model.number="manualEnd"
          placeholder="Last LED"
          :min="manualStart"
          :max="LED_COUNT - 1"
          class="range-input"
        />
        <button class="control-button" @click="sendManualRange">Send Manual</button>
      </div>

      <!-- Drawing Strip -->
      <div
        class="led-strip"
        @mousedown="startDrawing"
        @mouseup="stopDrawing"
        @mouseleave="stopDrawing"
      >
        <div
          v-for="(color, index) in ledColors"
          :key="index"
          class="led-box"
          :style="{ backgroundColor: color }"
          @mouseover="drawLed(index)"
        ></div>
      </div>

      <button class="control-button" @click="sendDrawnLeds">
        Send Drawn
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import Navbar from '../components/Navbar.vue'

const LED_COUNT = 72
const hexColor = ref('#00ff00')
const ledColors = ref<string[]>(Array(LED_COUNT).fill('#000000'))
const isDrawing = ref(false)
const manualStart = ref(0)
const manualEnd = ref(0)

const startDrawing = () => (isDrawing.value = true)
const stopDrawing = () => (isDrawing.value = false)

const drawLed = (index: number) => {
  if (isDrawing.value) {
    ledColors.value[index] = hexColor.value
  }
}

const sendManualRange = async () => {
  const rgb = hexToRgb(hexColor.value)
  if (!rgb) return

  const payload = {
    ledband: [manualStart.value, manualEnd.value, rgb.r, rgb.g, rgb.b],
  }

  try {
    await fetch('http://localhost:8080/instructions', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
      credentials: 'include',
    })
  } catch (error) {
    console.error('Failed to send manual LED range:', error)
  }
}

const sendDrawnLeds = async () => {
  const segments: { start: number; end: number; color: string }[] = []
  let start = null

  for (let i = 0; i <= ledColors.value.length; i++) {
    const curr = ledColors.value[i]
    const prev = ledColors.value[i - 1]

    if (i === 0 || curr !== prev) {
      if (start !== null && prev !== '#000000') {
        segments.push({ start, end: i - 1, color: prev })
      }
      start = curr !== '#000000' ? i : null
    }
  }

  for (const segment of segments) {
    const rgb = hexToRgb(segment.color)
    if (!rgb) continue

    const payload = {
      ledband: [segment.start, segment.end, rgb.r, rgb.g, rgb.b],
    }

    try {
      await fetch('http://localhost:8080/instructions', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
        credentials: 'include',
      })
    } catch (error) {
      console.error('Failed to send LED segment:', error)
    }

    await new Promise(resolve => setTimeout(resolve, 200))
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

.color-picker-container {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  align-items: flex-start;
}

.manual-range-container {
  display: flex;
  gap: 0.5rem;
  align-items: center;
  flex-wrap: wrap;
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

.led-strip {
  display: grid;
  grid-template-columns: repeat(auto-fill, 20px);
  gap: 2px;
  max-width: 100%;
  user-select: none;
  cursor: crosshair;
}

.led-box {
  width: 20px;
  height: 20px;
  border-radius: 4px;
  border: 1px solid #555;
  transition: background-color 0.2s;
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
