<template>
    <div>
      <button :class="{ 'on': led1 }" @click="toggleLed(1)">LED 1</button>
      <button :class="{ 'on': led2 }" @click="toggleLed(2)">LED 2</button>
    </div>
  </template>
  
  <script>
  export default {
    data() {
      return {
        led1: false,
        led2: false
      };
    },
    methods: {
      async toggleLed(led) {
        if (led === 1) this.led1 = !this.led1;
        if (led === 2) this.led2 = !this.led2;
        
        try {
          await fetch('http://localhost:8080/instructions', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ led, state: led === 1 ? this.led1 : this.led2 }),
            mode: 'cors',
            credentials: 'include',
          });
        } catch (error) {
          console.error('Error sending request:', error);
        }
      }
    }
  };
  </script>
  
  <style scoped>
  button {
    margin: 10px;
    padding: 10px 20px;
    font-size: 16px;
    border: none;
    cursor: pointer;
    background-color: gray;
    color: white;
    border-radius: 5px;
  }
  
  button.on {
    background-color: green;
  }
  </style>
  