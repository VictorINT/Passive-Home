<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue';

const isMenuOpen = ref(false);
const isMobile = ref(window.innerWidth < 768);

const toggleMenu = () => {
  isMenuOpen.value = !isMenuOpen.value;
};

const closeMenu = () => {
  if (isMobile.value) {
    isMenuOpen.value = false;
  }
};

const handleResize = () => {
  isMobile.value = window.innerWidth < 768;
  if (!isMobile.value) {
    isMenuOpen.value = false;
  }
};

onMounted(() => {
  window.addEventListener('resize', handleResize);
});

onUnmounted(() => {
  window.removeEventListener('resize', handleResize);
});
</script>

<template>
  <nav class="navbar">
    <div class="navbar-container">
      <div class="navbar-logo">
        <span class="logo-text">Smart Home</span>
      </div>
      
      <div class="navbar-mobile-toggle" @click="toggleMenu" aria-label="Toggle menu">
        <span class="toggle-icon"></span>
        <span class="toggle-icon"></span>
        <span class="toggle-icon"></span>
      </div>
      
      <ul class="navbar-links" :class="{ 'active': isMenuOpen }">
        <li class="navbar-item">
          <router-link to="/" class="navbar-link" @click="closeMenu">Home</router-link>
        </li>
        <li class="navbar-item">
          <router-link to="/stats" class="navbar-link" @click="closeMenu">Statistics</router-link>
        </li>
        <!-- <li class="navbar-item">
          <router-link to="/settings" class="navbar-link" @click="closeMenu">Settings</router-link>
        </li> -->
      </ul>
    </div>
  </nav>
</template>

<style scoped>
.navbar {
  background-color: #1a1a1a;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
  position: sticky;
  top: 0;
  z-index: 1000;
  width: 100%;
}

.navbar-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 clamp(0.75rem, 4vw, 1.5rem);
  height: clamp(3rem, 8vh, 4rem);
  max-width: 90vw;
  margin: 0 auto;
}

.navbar-logo {
  display: flex;
  align-items: center;
}

.logo-text {
  font-size: clamp(1.1rem, 4vw, 1.5rem);
  font-weight: bold;
  color: #4caf50;
  letter-spacing: 0.5px;
}

.navbar-links {
  display: flex;
  list-style: none;
  margin: 0;
  padding: 0;
  gap: clamp(0.5rem, 2vw, 1rem);
}

.navbar-link {
  color: #e0e0e0;
  text-decoration: none;
  font-size: clamp(0.875rem, 2.5vw, 1rem);
  padding: clamp(0.25rem, 1vh, 0.5rem) clamp(0.5rem, 2vw, 1rem);
  border-radius: clamp(0.25rem, 1vw, 0.5rem);
  transition: all 0.3s ease;
}

.navbar-link:hover,
.router-link-active {
  color: #4caf50;
  background-color: rgba(76, 175, 80, 0.1);
}

.navbar-mobile-toggle {
  display: none;
  flex-direction: column;
  justify-content: space-between;
  width: clamp(20px, 6vw, 24px);
  height: clamp(16px, 5vw, 20px);
  cursor: pointer;
}

.toggle-icon {
  height: 2px;
  width: 100%;
  background-color: #e0e0e0;
  transition: all 0.3s ease;
}

@media (max-width: 767px) {
  .navbar-mobile-toggle {
    display: flex;
  }
  
  .navbar-links {
    position: absolute;
    top: clamp(3rem, 8vh, 4rem);
    left: 0;
    flex-direction: column;
    width: 100%;
    background-color: #1a1a1a;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    gap: 0;
    height: 0;
    overflow: hidden;
    transition: all 0.3s ease;
    max-height: 0;
    opacity: 0;
  }
  
  .navbar-links.active {
    height: auto;
    max-height: 100vh;
    opacity: 1;
  }
  
  .navbar-item {
    width: 100%;
  }
  
  .navbar-link {
    display: block;
    padding: clamp(0.75rem, 3vh, 1rem) clamp(1rem, 4vw, 1.5rem);
    border-radius: 0;
    border-bottom: 1px solid #333;
  }
}

@media (min-width: 768px) {
  .navbar-container {
    max-width: 85vw;
  }
}

@media (min-width: 1200px) {
  .navbar-container {
    max-width: 1200px;
  }
}
</style>