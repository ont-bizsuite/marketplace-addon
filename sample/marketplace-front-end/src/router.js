import Vue from 'vue'
import Router from 'vue-router'
const Layout = () => import('@/views/Layout')
const CommodityDetail = () => import('@/components/CommodityDetail')
const OrderCenter = () => import('@/components/OrderCenter')
const AddNewData = () => import('@/components/AddNewData')
const CommodityManage = () => import('@/components/CommodityManage')
const EditData = () => import('@/components/EditData')
const ResaleDetail = () => import('@/components/ResaleDetail')
const Register = () => import('@/components/Register')
const OrderDetail = () => import('@/components/OrderDetail')
const DataIDList = () => import('@/components/DataIDList')
const TokenDetail = () => import('@/components/TokenDetail')
const SecondHandCommoditylist = () => import('@/components/SecondHandCommoditylist')
Vue.use(Router)
const router = new Router({
  // mode: 'history',
  routes: [
    {
      path: '/',
      name: 'Layout',
      component: Layout,
      children: [
        {
          path: 'commoditydetail',
          name: 'commoditydetail',
          component: CommodityDetail
        },
        {
          path: 'ordercenter',
          name: 'ordercenter',
          component: OrderCenter
        },
        {
          path: 'addnewdata',
          name: 'addnewdata',
          component: AddNewData
        },
        {
          path: 'commoditymanage',
          name: 'commoditymanage',
          component: CommodityManage
        },
        {
          path: 'editdata',
          name: 'editdata',
          component: EditData
        },{
          path: 'resaleDetail',
          name: 'resaleDetail',
          component: ResaleDetail
        },
        {
          path: 'register',
          name: 'register',
          component: Register
        },
        {
          path: 'orderdetail',
          name: 'orderdetail',
          component: OrderDetail
        },
        {
          path: 'secondHandCommoditylist',
          name: 'secondHandCommoditylist',
          component: SecondHandCommoditylist
        },
        {
          path: 'dataidlist',
          name: 'DataIDList',
          component: DataIDList
        },
        {
          path: 'tokendetail',
          name: 'TokenDetail',
          component: TokenDetail
        }
      ]
    },
    {
      path: '*',
      redirect: '/'
    }
  ]
})

function jumpTo(target, origin, next) {
  if (target.name !== origin.name) {
    return next(target);
  }
  return next();
}

router.beforeEach(async (to, from, next) => {

  let toPath = to.name
  // console.log(to)
  if (toPath === 'orderdetail' || toPath === 'register') {
    next()
    return
  }
  let user_ontid = sessionStorage.getItem("user_ontid");
  // if no user_ontid then go web_home
  if (!user_ontid) {
    sessionStorage.clear()
    jumpTo({ name: 'Layout' }, to, next)
    return
  }
  next()
})

export default router
