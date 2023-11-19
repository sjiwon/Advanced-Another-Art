import {createApp} from 'vue'
import App from '@/App.vue'
import router from '@/router'
import store from '@/store'

import {BootstrapVue3} from 'bootstrap-vue-3'
import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'

import 'mdb-vue-ui-kit/css/mdb.min.css'
import {library} from '@fortawesome/fontawesome-svg-core'
import {FontAwesomeIcon} from '@fortawesome/vue-fontawesome'

import {fas} from '@fortawesome/free-solid-svg-icons'
import {far, faTrashAlt} from '@fortawesome/free-regular-svg-icons'
import {fab} from '@fortawesome/free-brands-svg-icons'

import externalAxios from 'axios'
import {axios, axiosWithAccessToken, axiosWithRefreshToken} from '@/apis/axios'

library.add(faTrashAlt)
library.add(fas, far, fab)

const app = createApp(App)
app.config.globalProperties.window = window
app.config.globalProperties.externalAxios = externalAxios
app.config.globalProperties.axios = axios
app.config.globalProperties.axiosWithAccessToken = axiosWithAccessToken
app.config.globalProperties.axiosWithRefreshToken = axiosWithRefreshToken

app
  .use(store)
  .use(router)
  .use(BootstrapVue3)
  .component('font-awesome-icon', FontAwesomeIcon)
  .mount('#app')
