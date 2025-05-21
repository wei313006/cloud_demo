import { createRouter, createWebHistory } from 'vue-router'
import Login from '@/admin/view/Login.vue'
import Admin from '@/admin/view/Admin.vue'
import OperLog from '@/admin/components/OperLog.vue'
import Manager from '@/admin/components/Manager.vue'
import ApiDoc from '@/admin/components/ApiDoc.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      component: Login
    },
    {
      path: '/verified/datamanager',
      component: Admin,
      children: [
        {
          path: '/verified/datamanager/operate/log',
          component: OperLog
        },
        {
          path: '/verified/datamanager/managers',
          component: Manager
        },
        {
          path: '/verified/datamanager/api/doc',
          component: ApiDoc
        },
      ]
    }
  ],
})

export default router
