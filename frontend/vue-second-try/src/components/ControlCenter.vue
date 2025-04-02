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
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';

// Define data types
const led1 = ref<number>(0);
const led2 = ref<number>(0);

// Toggle LED state
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
    console.error('Error sending request:', error);
  }
};
</script>

<style scoped>
.control-container {
  display: flex;
  gap: 1rem;
  padding: 1rem;
  background-color: #2a2a2a;
  border-radius: 8px;
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