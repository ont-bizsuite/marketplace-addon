import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'

// import ElementUI from 'element-ui'
import {
  Pagination,
  Table,
  TableColumn,
  Container,
  Header,
  Main,
  Footer,
  Select,
  Option,
  Button,
  Dropdown,
  DropdownMenu,
  DropdownItem,
  Dialog,
  Input,
  InputNumber,
  Tag,
  Form,
  FormItem,
  Message,
  Icon,
  Tabs,
  TabPane,
  Link,
  DatePicker,
  Checkbox,
  CheckboxGroup,
  MessageBox,
  Radio,
  Upload,
  Loading
} from 'element-ui'
Vue.use(Pagination)
Vue.use(Table)
Vue.use(TableColumn)
Vue.use(Container)
Vue.use(Header)
Vue.use(Main)
Vue.use(Footer)
Vue.use(Select)
Vue.use(Option)
Vue.use(Button)
Vue.use(Dropdown)
Vue.use(DropdownMenu)
Vue.use(DropdownItem)
Vue.use(Dialog)
Vue.use(Input)
Vue.use(Tag)
Vue.use(Form)
Vue.use(FormItem)
Vue.use(Tabs)
Vue.use(Icon)
Vue.use(TabPane)
Vue.use(Link)
Vue.use(InputNumber)
Vue.use(DatePicker)
Vue.use(Checkbox)
Vue.use(CheckboxGroup)
Vue.use(Radio)
Vue.use(Upload)
Vue.use(Loading)
Vue.prototype.$message = Message
Vue.prototype.$confirm = MessageBox.confirm
Vue.prototype.$prompt = MessageBox.prompt
Vue.prototype.$alert = MessageBox.alert
// import 'element-ui/lib/theme-chalk/index.css'
import axios from 'axios'
import { client } from 'ontology-dapi'
import zh from './common/lang/zh'
import en from './common/lang/en'
import VueI18n from 'vue-i18n'
// import { Message } from 'element-ui'
Vue.use(VueI18n)
const i18n = new VueI18n({
  locale: localStorage.getItem('user_lang') || 'en',
  messages: {
    zh: zh,
    en: en
  }
})
// axios.defaults.baseURL = 'http://192.168.50.96:8182'
axios.defaults.timeout = 10000
//http request 拦截器
axios.interceptors.request.use(
  config => {
    config.data = JSON.stringify(config.data)
    config.headers = {
      'Content-Type': 'application/json'
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)
// response interceptor
axios.interceptors.response.use(
  response => {
    // console.log('erere', response)
    return response
  },
  error => {
    const rest = error.response
    // console.log(rest)
    if (rest) {
      if (rest.status === 400) {
        // 提示错误
        Message({ message: rest.data.desc, type: 'error' })
      }
    } else {
      Message({ message: error, type: 'error' })
    }
    console.error('err' + error) // for debug
    return Promise.reject(error)
  }
)
client.registerClient({})
// Vue.use(ElementUI)
Vue.prototype.$http = axios

Vue.config.productionTip = false

new Vue({
  router,
  store,
  i18n,
  render: h => h(App)
}).$mount('#app')

localStorage.setItem('net', 'TEST_NET')
