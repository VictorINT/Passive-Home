import Vue from 'vue';
import Router from 'vue-router';
import DashboardView from '@/views/DashboardView.vue';

Vue.use(Router);

export default new Router({
    mode: 'history',
    routes: [
        {
            path: '/',
            redirect: '/login'
        },
        {
            path: '/login',
            component: {
                template: '<div>Login Page</div>'
            }
        },
        {
            path: '/dashboard',
            name: 'Dashboard',
            component: DashboardView
        }
    ]
});