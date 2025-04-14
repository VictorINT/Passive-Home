import { createRouter, createWebHistory } from 'vue-router';
import Home from '../views/Home.vue';
import Stats from '../views/StatsPage.vue';
import LightControl from '../views/LightControl.vue';
import RFIDControl from '../views/RFIDControl.vue';

const routes = [
    { 
        path: '/', 
        name: 'home',
        component: Home 
    },
    { 
        path: '/stats', 
        name: 'stats',
        component: Stats 
    },
    {
        path: '/light',
        name: 'light',
        component: LightControl
    },
    {
        path: '/rfid',
        name: 'rfid',
        component: RFIDControl
    },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
