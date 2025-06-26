import { createRouter, createWebHistory } from 'vue-router'
import Login from '@/view/Login.vue'
import Main from '@/view/Main.vue'
import Home from '@/view/Home.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'main page',
      component: Main, 
      redirect:'/home',
      children: [
        {
          path: '/home',
          component: Home,
        },
        {
          path: '/home',
          component: Home,
        },
      ]
    },
    {
      path: '/login',
      component: Login,
    },
  ],
})

export default router
