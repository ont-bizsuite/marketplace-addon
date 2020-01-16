import Vue from 'vue'
import Vuex from 'vuex'
import { client } from 'ontology-dapi'
import axios from 'axios'
import LangStorage from './helpers/lang'
import QRCode from 'qrcode'
import server from './api/request'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    scriptHash: '6abeee94255356500bccf4d65017a26413f417ac',
    gasPrice: 500,
    gasLimit: 30000,
    requireIdentity: true,
    token_address: '0000000000000000000000000000000000000002',
    dataId: {
      scriptHash: '0300000000000000000000000000000000000000',
      gasPrice: 0,
      gasLimit: 30000,
      requireIdentity: true
    },
    lang: LangStorage.getLang('en'),
    qrcodeParams: {
      isShow: false,
      qrcodeUrl: ''
    }
  },
  mutations: {
    UPDATE_HOME_LANG(state, payload) {
      state.lang = payload.lang
    },
    CREATE_QRCODE(state, payload) {
      QRCode.toDataURL(payload.url)
        .then(url => {
          state.qrcodeParams.qrcodeUrl = url
          state.qrcodeParams.isShow = payload.isShow
        })
        .catch(err => {})
    },
    CHANGE_MODEL_STATE(state, payload) {
      state.qrcodeParams.isShow = payload
    }
  },
  actions: {
    changeQrcode({ dispatch, commit }, params) {
      let p = {
        url: '',
        isShow: false
      }
      p.url = JSON.stringify(params.params)
      p.isShow = params.isShow
      console.log('p', p)
      commit('CREATE_QRCODE', p)
    },
    async dapiInvoke({ dispatch, commit }, params) {
      console.log('params', params)
      const { operation, args } = params
      const { scriptHash, gasPrice, gasLimit, requireIdentity } = this.state
      try {
        console.log('scriptHash', scriptHash)

        const result = await client.api.smartContract.invoke({
          scriptHash,
          operation,
          args,
          gasPrice,
          gasLimit,
          requireIdentity
        })
        console.log(result)
        return result
      } catch (error) {
        return error
      }
    },
    async getCommodityList({ dispatch, commit }, params) {
      console.log(params)
      try {
        return await axios.post(
          `${process.env.VUE_APP_API}/api/v1/data/query`,
          params
        )
      } catch (error) {
        return error
      }
    },
    async getSecondHandCommodityList({ dispatch, commit }, params) {
      console.log(params)
      try {
        return await axios.post(
          `${process.env.VUE_APP_DDXF_API}/api/v1/order/all/second`,
          params
        )
      } catch (error) {
        return error
      }
    },
    async addCommodity({ dispatch, commit }, params) {
      try {
        console.log(params)

        return await axios.put(
          process.env.VUE_APP_DDXF_API + '/api/v1/dataset',
          params
        )
      } catch (error) {
        return error
      }
    },
    async getCommodityDetail({ dispatch, commit }, params) {
      let { id } = params
      try {
        return await axios.get(
          `${process.env.VUE_APP_API}/api/v1/data/query/${id}`
        )
      } catch (error) {
        return error
      }
    },
    async getBuyOrder({ dispatch, commit }, params) {
      let { type, ontid } = params
      try {
        return await axios.get(
          `${process.env.VUE_APP_API}/api/v1/order/query/self/${type}/${ontid}`
        )
      } catch (error) {
        return error
      }
    },
    async getViewInfo({ dispatch, commit }, params) {
      let { orderId, accountid } = params
      try {
        return await axios.get(
          `${process.env.VUE_APP_ORFDER}/api/v1/data-dealer/tools/data?orderId=${orderId}&ontid=did:ont:${accountid}`
        )
      } catch (error) {
        return error
      }
    },
    async getSellOrderData({ dispatch, commit }, params) {
      let { accountid, pageNum, pageSize } = params
      try {
        return await axios.get(
          `${process.env.VUE_APP_ORFDER}/api/v1/data-dealer/tools/orders/1?ontid=did:ont:${accountid}&pageNum=${pageNum}&pageSize=${pageSize}`
        )
      } catch (error) {
        return error
      }
    },
    async getCertifier() {
      try {
        return await axios.get(
          process.env.VUE_APP_DDXF_API + '/api/v1/certifier'
        )
      } catch (error) {
        return error
      }
    },
    async getJudger() {
      try {
        return await axios.get(process.env.VUE_APP_DDXF_API + '/api/v1/judger')
      } catch (error) {
        return error
      }
    },
    async getCertData({ dispatch, commit }, params) {
      try {
        const { ontid, pageNum, pageSize } = params
        return await axios.get(
          `${process.env.VUE_APP_DDXF_API}/api/v1/certifier/${ontid}?pageNum=${pageNum}&pageSize=${pageSize}`
        )
      } catch (error) {
        return error
      }
    },
    async toCert({ dispatch, commit }, params) {
      try {
        return await axios.post(
          `${process.env.VUE_APP_DDXF_API}/api/v1/certifier`,
          params
        )
      } catch (error) {
        return error
      }
    },
    async getSellData({ dispatch, commit }, params) {
      try {
        console.log('params', params)
        const { ontid, pageNum, pageSize } = params
        console.log('ontid', ontid)
        return await axios.get(
          `${process.env.VUE_APP_DDXF_API}/api/v1/dataset/provider/${ontid}?pageNum=${pageNum}&pageSize=${pageSize}`
        )
      } catch (error) {
        return error
      }
    },
    async prodOperat({ dispatch, commit }, params) {
      try {
        return await axios.post(
          `${process.env.VUE_APP_SELECT}/api/v1/dataset/${params}`
        )
      } catch (error) {
        return error
      }
    },
    async regiter({ dispatch, commit }, params) {
      try {
        console.log(params)
        return await axios.post(
          `${process.env.VUE_APP_ORFDER}/api/v1/data-dealer/ons/register`,
          params
        )
      } catch (error) {
        return error
      }
    },
    async login({ dispatch, commit }, params) {
      try {
        return await axios.get(
          `${process.env.VUE_APP_ORFDER}/api/v1/data-dealer/ons/login/${params}`
        )
      } catch (error) {
        return error
      }
    },
    async shelf({ dispatch, commit }, params) {
      try {
        return await axios.post(
          `${process.env.VUE_APP_SELECT}/api/v1/dataset/provide`,
          params
        )
      } catch (error) {
        return error
      }
    },
    async setInvoke({ dispatch, commit }, params) {
      try {
        console.log(params)
        // return;
        return await axios.post(
          `${process.env.VUE_APP_SELECT}/api/v1/data-dealer/contract/dataid`,
          params
        )
      } catch (error) {
        return error
      }
    },
    async dataIdInvoke({ dispatch, commit }, params) {
      console.log('params', params)
      const { operation, args } = params
      const {
        scriptHash,
        gasPrice,
        gasLimit,
        requireIdentity
      } = this.state.dataId
      console.log('args', args)
      console.log('operation', operation)
      console.log('scriptHash', scriptHash)
      console.log('gasPrice', gasPrice)
      console.log('gasLimit', gasLimit)
      console.log('requireIdentity', requireIdentity)
      try {
        console.log('scriptHash', scriptHash)
        const result = await client.api.smartContract.invoke({
          scriptHash,
          operation,
          args,
          gasPrice,
          gasLimit,
          requireIdentity
        })
        console.log(result)
        return result
      } catch (error) {
        return error
      }
    },
    async sendPayer({ dispatch, commit }, params) {
      try {
        console.log(params)
        // return;
        return await axios.post(
          `${process.env.VUE_APP_SELECT}/api/v1/data-dealer/contract/send`,
          params
        )
      } catch (error) {
        return error
      }
    },
    async addProd({ dispatch, commit }, params) {
      try {
        return await axios.put(
          process.env.VUE_APP_DDXF_API + '/api/v1/dataset',
          params
        )
      } catch (error) {
        return error
      }
    },
    async getDID({ dispatch, commit }, params) {
      try {
        return await axios.post(
          process.env.VUE_APP_DDXF_API + '/api/v1/contract/dataid',
          params
        )
      } catch (error) {
        return error
      }
    },
    async getTID({ dispatch, commit }, params) {
      try {
        return await axios.post(
          process.env.VUE_APP_DDXF_API + '/api/v1/dataset/dataId',
          params
        )
      } catch (error) {
        return error
      }
    },
    async queryTokenNum({ dispatch, commit }, params) {
      try {
        return await axios.get(
          process.env.VUE_APP_DDXF_API +
            `/api/v1/dataset/token/balance/${params.ontid}/${params.tokenId}`
        )
      } catch (error) {
        return error
      }
    },
    async queryjuderData({ dispatch, commit }, params) {
      try {
        return await axios.get(
          `${process.env.VUE_APP_DDXF_API}/api/v1/judger/${params.ontid}?pageNum=${params.pageNum}&pageSize=${params.pageSize}`
        )
      } catch (error) {
        return error
      }
    },
    async makeTransaction({ dispatch, commit }, params) {
      console.log(params)
      try {
        return await axios.post(
          process.env.VUE_APP_DDXF_API + '/api/v1/contract/transaction',
          params
        )
      } catch (error) {
        return error
      }
    },
    async createOrderID({ dispatch, commit }, params) {
      try {
        return await axios.post(
          process.env.VUE_APP_DDXF_API + '/api/v1/order',
          params
        )
      } catch (error) {
        return error
      }
    },
    async buyData({ dispatch, commit }, params) {
      try {
        return axios.post(
          process.env.VUE_APP_DDXF_API + '/api/v1/order/purchase',
          params
        )
      } catch (error) {
        return error
      }
    },
    async buySecondHandData({ dispatch, commit }, params) {
      try {
        return axios.post(
          process.env.VUE_APP_DDXF_API + '/api/v1/order/purchase/second',
          params
        )
      } catch (error) {
        return error
      }
    },
    async checkData({ dispatch, commit }, params) {
      try {
        return axios.post(
          process.env.VUE_APP_DDXF_API + '/api/v1/order/data',
          params
        )
      } catch (error) {
        return error
      }
    },
    async sendPass({ dispatch, commit }, params) {
      try {
        return axios.post(
          process.env.VUE_APP_DDXF_API + '/api/v1/contract/send',
          params
        )
      } catch (error) {
        return error
      }
    },
    async sendOJData({ dispatch, commit }, params) {
      try {
        return axios.post(
          process.env.VUE_APP_DDXF_API + '/api/v1/judger/result',
          params
        )
      } catch (error) {
        return error
      }
    },
    async getTokenId({ dispatch, commit }, params) {
      try {
        return axios.get(
          process.env.VUE_APP_DDXF_API + '/api/v1/order/token/' + params
        )
      } catch (error) {
        return error
      }
    },
    // 查询token剩余流转次数和访问次数
    async viewOtherInfo({ dispatch, commit }, params) {
      try {
        return axios.get(
          process.env.VUE_APP_API + '/api/v1/order/token/balance/' + params
        )
      } catch (error) {
        return error
      }
    },
    async getDataByDataID({ dispatch, commit }, params) {
      try {
        return axios.get(
          process.env.VUE_APP_DDXF_API + '/api/v1/dataset/data/' + params.id
        )
      } catch (error) {
        return error
      }
    },
    async updateOrder({ dispatch, commit }, params) {
      try {
        return axios.post(
          process.env.VUE_APP_DDXF_API + '/api/v1/order/second',
          params
        )
      } catch (error) {
        return error
      }
    },
    async getDataIdDetail({ dispatch, commit }, params) {
      try {
        return axios.get(
          process.env.VUE_APP_DDXF_ID + '/api/v1/dataId/' + params
        )
      } catch (error) {
        return error
      }
    },
    async getTokenIdDetail({ dispatch, commit }, params) {
      try {
        return axios.get(
          process.env.VUE_APP_DDXF_ID + '/api/v1/tokenId/' + params
        )
      } catch (error) {
        return error
      }
    },
    // 登录app
    async getLoginMsg({ dispatch, commit }, params) {
      try {
        return axios.post(process.env.VUE_APP_API + '/api/v1/ontid/login')
      } catch (error) {
        return error
      }
    },
    // 查询登录结果
    async getLoginRes({ dispatch, commit }, params) {
      try {
        return axios.get(
          process.env.VUE_APP_API + '/api/v1/ontid/login/result/' + params
        )
      } catch (error) {
        return error
      }
    },
    // 校验jwt并访问数据
    async downloadfile({ dispatch, commit }, params) {
      try {
        return await axios.get(
          process.env.VUE_APP_API + '/api/v1/data/access?token=' + params
        )
      } catch (error) {
        return error
      }
    },
    // 下载数据结果
    async getdataRes({ dispatch, commit }, params) {
      try {
        return await axios.get(
          process.env.VUE_APP_API + '/api/v1/back/result/' + params
        )
      } catch (error) {
        return error
      }
    },
    // 注册用户名
    async sendONS({ dispatch, commit }, params) {
      try {
        return axios.post(
          process.env.VUE_APP_API + '/api/v1/ontid/register',
          params
        )
      } catch (error) {
        return error
      }
    },
    // 查询注册结果
    async checkSignUp({ dispatch, commit }, params) {
      try {
        return axios.get(
          process.env.VUE_APP_API + '/api/v1/ontid/register/result/' + params
        )
      } catch (error) {
        return error
      }
    },
    async getCerMsg({ dispatch, commit }, params) {
      try {
        return axios.get(
          process.env.VUE_APP_DDXF_API + '/api/v1/certifier/message/' + params
        )
      } catch (error) {
        return error
      }
    },
    async getCerQrRes({ dispatch, commit }, params) {
      try {
        return axios.get(
          process.env.VUE_APP_DDXF_API + '/api/v1/certifier/result/' + params
        )
      } catch (error) {
        return error
      }
    },
    /**
     *
     * @param {1} 请求成功，成功同步结果
     * @param {2} 请求成功，尚未扫码同步结果
     * @param {3} 请求失败
     */
    async getCheckRes({ dispatch, commit, state }, params) {
      try {
        console.log('state.qrcodeParams.isShow', state.qrcodeParams.isShow)
        if (state.qrcodeParams.isShow) {
          let result = await axios.get(
            process.env.VUE_APP_API + '/api/v1/back/result/' + params
          )
          console.log('checkout result', result)
          if (result.data.desc === 'SUCCESS') {
            if (
              result.data.result.result === '1' ||
              result.data.result.result === '2'
            ) {
              commit('CHANGE_MODEL_STATE', false)
              return 1
            } else if (result.data.result.result === '0') {
              commit('CHANGE_MODEL_STATE', false)
              return 0
            } else {
              return 2
            }
          } else {
            commit('CHANGE_MODEL_STATE', false)
            return 0
          }
        } else {
          commit('CHANGE_MODEL_STATE', false)
          return 4
        }
      } catch (error) {
        commit('CHANGE_MODEL_STATE', false)
        return 0
      }
    },
    /**
     *
     * Make panding order contract
     *
     * @param {param} param
     */
    async makePandingCont({ dispatch, commit }, params) {
      try {
        return await axios.post(
          process.env.VUE_APP_DDXF_API + '/api/v1/order',
          params
        )
      } catch (error) {
        return error
      }
    },
    /**
     *
     * make order
     */
    async makeOrder({ dispatch, commit }, params) {
      try {
        return await axios.post(
          process.env.VUE_APP_API + '/api/v1/order/take',
          params
        )
      } catch (error) {
        return error
      }
    },
    /**
     *  common make hash
     */
    async MakeCommonHash({ dispatch, commit }, params) {
      try {
        return await axios.post(
          process.env.VUE_APP_DDXF_API + '/api/v1/contract',
          params
        )
      } catch (error) {
        return error
      }
    },
    /**
     *
     * get claim
     */
    async getClaim({ dispatch, commit }, params) {
      try {
        return await axios.get(
          process.env.VUE_APP_DDXF_API + '/api/v1/claim/getClaim'
        )
      } catch (error) {
        return error
      }
    },
    /**
     *
     * post claim
     */
    async postClaim({ dispatch, commit }, params) {
      try {
        return await axios.get(
          process.env.VUE_APP_DDXF_API + '/api/v1/claim/postClaim'
        )
      } catch (error) {
        return error
      }
    },
    uploadFile({ dispatch, commit }, params) {
      let data = params.file
      try {
        return server.post(
          process.env.VUE_APP_API +
            '/api/v1/data/dataId/register?ontid=' +
            params.ontid,
          data,
          {
            headers: {
              'Content-Type': 'multipart/form-data'
            }
          }
        )
      } catch (error) {
        return error
      }
    },
    async getComList({ dispatch, commit }, params) {
      try {
        return axios.get(
          process.env.VUE_APP_API + '/api/v1/data/query/self/' + params
        )
      } catch (error) {
        return error
      }
    },
    // 上架授权
    async ShelfAuthorization({ dispatch, commit }, params) {
      try {
        return axios.post(
          process.env.VUE_APP_API + '/api/v1/order/auth',
          params
        )
      } catch (error) {
        return error
      }
    },
    // 确认订单
    async SureOrder({ dispatch, commit }, params) {
      try {
        return axios.post(
          process.env.VUE_APP_API + '/api/v1/order/confirm',
          params
        )
      } catch (error) {
        return error
      }
    },
    // 校验DataToken并凭借DataToken生成jwt
    async jwtMsg({ dispatch, commit }, params) {
      try {
        return axios.post(
          process.env.VUE_APP_API + '/api/v1/data/token/' + params
        )
      } catch (error) {
        return error
      }
    }
  }
})
