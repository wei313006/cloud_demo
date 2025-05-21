import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import { createI18n } from 'vue-i18n'

import App from './App.vue'
import router from './router'

import en from './languages/en'
import zh from './languages/zh'

// import { Button, ActionSheet, Sticky, Icon, Loading, Popup, Dialog, RadioGroup, Radio } from 'vant'
// import 'vant/lib/index.css';

import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'


const messages = {
    en: {
        ...en
    },
    zh: {
        ...zh
    }
}

const language: string = localStorage.getItem('lang') || 'en';
const i18n = createI18n({
    locale: language, // 默认语言
    fallbackLocale: 'en', // 后备语言
    messages,
});


const app = createApp(App)

app.use(createPinia())
app.use(i18n)
app.use(router)
// app.use(Button)
// app.use(ActionSheet)
// app.use(Sticky)
// app.use(Loading)
// app.use(Icon)
// app.use(Popup)
// app.use(Dialog)
// app.use(RadioGroup)
// app.use(Radio)
app.use(ElementPlus);

app.mount('#app')
