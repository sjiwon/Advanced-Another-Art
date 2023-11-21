import {createRouter, createWebHistory} from 'vue-router'
import MainView from '@/views/MainView.vue'
import LoginView from '@/views/user/LoginView.vue'
import SignUpView from '@/views/user/SignUpView.vue'
import ArtSearchView from '@/views/art/ArtSearchView.vue'

import store from '@/store'

const routes = [
  {path: '/', name: 'MainView', component: MainView, meta: {requiresAuth: false}},
  {path: '/login', name: 'LoginView', component: LoginView, meta: {requiresAuth: false}},
  {
    path: '/find-id',
    name: 'FindIdView',
    component: () => import(/* webpackChunkName: "findId", webpackPrefetch: true */ '@/views/user/FindIdView.vue'),
    meta: {requiresAuth: false}
  },
  {
    path: '/change-password',
    name: 'ChangePasswordView',
    component: () => import(/* webpackChunkName: "changePassword", webpackPrefetch: true */ '@/views/user/ChangePasswordView.vue'),
    meta: {requiresAuth: false}
  },
  {
    path: '/signup',
    name: 'SignUpView',
    component: SignUpView,
    meta: {requiresAuth: false}
  },
  {
    path: '/search',
    name: 'ArtSearchView',
    component: ArtSearchView,
    meta: {requiresAuth: false}
  },
  {
    path: '/mypage',
    name: 'MyPageView',
    component: () => import(/* webpackChunkName: "userInfo", webpackPrefetch: true */ '@/views/user/MyPageView.vue'),
    meta: {requiresAuth: true},
    children: [
      {
        path: '',
        name: 'UserInformationComponent',
        component: () => import(/* webpackChunkName: "userInfo", webpackPrefetch: true */ '@/components/user/UserInformationComponent.vue')
      },
      {
        path: 'winning-auction',
        name: 'UserWinningAuctionComponent',
        component: () => import(/* webpackChunkName: "userInfo", webpackPrefetch: true */ '@/components/user/UserWinningAuctionComponent.vue')
      },
      {
        path: 'art/purchase',
        name: 'UserPurchaseHistoryComponent',
        component: () => import(/* webpackChunkName: "userInfo", webpackPrefetch: true */ '@/components/user/UserPurchaseHistoryComponent.vue')
      },
      {
        path: 'art/sold',
        name: 'UserSaleHistoryComponent',
        component: () => import(/* webpackChunkName: "userInfo", webpackPrefetch: true */ '@/components/user/UserSaleHistoryComponent.vue')
      },
      {
        path: 'point/charge',
        name: 'UserPointChargeComponent',
        component: () => import(/* webpackChunkName: "userInfo", webpackPrefetch: true */ '@/components/user/UserPointChargeComponent.vue')
      },
      {
        path: 'point/history',
        name: 'UserPointHistoryComponent',
        component: () => import(/* webpackChunkName: "userInfo", webpackPrefetch: true */ '@/components/user/UserPointHistoryComponent.vue')
      }
    ]
  },
  {
    path: '/art/register',
    name: 'ArtRegistrationView',
    component: () => import(/* webpackChunkName: "artRegistration", webpackPrefetch: true */ '@/views/art/ArtRegistrationView.vue'),
    meta: {requiresAuth: true}
  },
  {
    path: '/art',
    name: 'ArtDetailView',
    component: () => import('@/views/art/ArtDetailView.vue'),
    meta: {requiresAuth: false}
  },
  {
    path: '/:catchAll(.*)',
    redirect: '/',
    meta: {requiresAuth: false}
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

router.beforeEach((to, from, next) => {
  if (to.matched.some(record => record.meta.requiresAuth)) { // 인증 필요
    if (store.getters['memberStore/isAuthenticated'] === false) {
      alert('로그인이 필요합니다')
      next('/login')
    } else {
      next()
    }
  } else { // 인증 필요 X
    next()
  }
})

export default router
