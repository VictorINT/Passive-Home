import { createRouter, createWebHistory } from 'vue-router';
import Home from '../views/Home.vue';
import Stats from '../views/StatsPage.vue';

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
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

export default router;
