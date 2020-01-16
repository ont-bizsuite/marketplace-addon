<template>
  <div>
    <el-table
      border
      :data="tableData"
      style="width: 100%"
      :empty-text="$t('common.no_data')"
    >
      <el-table-column
        type="index"
        :index="indexMethod"
        align="center"
      ></el-table-column>
      <el-table-column
        prop="provider"
        :label="tableLang.seller"
        align="center"
      ></el-table-column>
      <!-- <el-table-column label="dataId" width="360" align="center">
        <template slot-scope="scope">
          <el-button
            @click="toDataIDList(scope.row.dataId)"
            size="mini"
            type="primary"
          >{{scope.row.dataId}}</el-button>
        </template>
      </el-table-column>-->
      <el-table-column label="Token ID" width="100" align="center">
        <template slot-scope="scope">
          <!-- <el-button
            @click="toTokenId(scope.row.tokenId)"
            size="mini"
            type="primary"
          >{{scope.row.tokenId}}</el-button>-->
          <el-button size="mini" type="primary">{{
            scope.row.tokenId
          }}</el-button>
        </template>
      </el-table-column>
      <el-table-column
        prop="orderId"
        :label="tableLang.order_num"
        align="center"
      ></el-table-column>
      <!-- <el-table-column :label="tableLang.desc" prop="desc" align="center" width="180"></el-table-column> -->
      <el-table-column
        prop="createTime"
        :label="tableLang.buy_date"
        width="280"
        align="center"
      ></el-table-column>
      <el-table-column :label="tableLang.state" width="220" align="center">
        <template slot-scope="scope">
          <el-button size="mini" v-if="scope.row.state == 4" type="danger">{{
            $t('common.order_over')
          }}</el-button>
          <!-- <el-button
            size="mini"
            v-else-if="scope.row.state == 4"
            type="danger"
          >{{$t('common.appeal')}}</el-button>-->
          <!-- <el-button
            size="mini"
            v-else-if="scope.row.state == 5"
            type="success"
          >{{$t('common.apple_end')}}</el-button>-->
          <!-- <el-button
            size="mini"
            v-else-if="scope.row.state == 1"
            type="danger"
          >{{$t('common.pending_order')}}</el-button>-->
          <el-button size="mini" v-else type="success">{{
            $t('common.buy_suc')
          }}</el-button>
          <!-- <el-tag type="info" v-show="scope.row.arbitrage == '1'">{{$t('common.appeal_suc')}}</el-tag> -->
          <!-- <el-tag type="danger" v-show="scope.row.arbitrage == '0'">{{$t('common.appeal_fail')}}</el-tag> -->
        </template>
      </el-table-column>
      <el-table-column :label="tableLang.operating" width="380" align="center">
        <template slot-scope="scope">
          <el-button
            v-show="scope.row.state == 4"
            size="mini"
            @click="viewInfo(scope.row)"
            >{{ $t('common.view_info') }}</el-button
          >
          <el-button size="mini" @click="viewOther(scope.row)">{{
            $t('common.view_other')
          }}</el-button>
          <el-button
            size="mini"
            v-show="scope.row.state == 2"
            type="success"
            @click="confirmReceipt(scope.row)"
            >{{ $t('common.sure_order') }}</el-button
          >
        </template>
      </el-table-column>
    </el-table>

    <!-- <div class="paginatio">
      <el-pagination
        @current-change="handleCurrentChange"
        background
        layout="prev, pager, next"
        :total="orderCount"
        :page-size="pageSize"
      ></el-pagination>
    </div>-->
  </div>
</template>

<script>
import { client } from 'ontology-dapi'
import moment from 'moment'
import { mapState } from 'vuex'

export default {
  data() {
    return {
      tableLang: {
        seller: this.$t('center.sell'),
        order_num: this.$t('common.order_num'),
        desc: this.$t('common.description'),
        buy_date: this.$t('common.buy_date'),
        operating: this.$t('common.operating'),
        state: this.$t('common.state')
      },
      tableData: [],
      accountid: '',
      orderCount: 0,
      pageSize: 10,
      pageNum: 0,
      commonId: null,
      commonTimer: null,
      viewTimer: null
    }
  },
  computed: {
    ...mapState({
      isShow: state => state.qrcodeParams.isShow
    })
  },
  methods: {
    indexMethod(index) {
      return this.pageNum * this.pageSize + index + 1
    },
    async getBuyOrder() {
      try {
        let params = {
          ontid: this.accountid,
          type: 2
        }
        let res = await this.$store.dispatch('getBuyOrder', params)
        console.log('res buy', res)
        if (res.status === 200 && res.data.desc === 'SUCCESS') {
          this.tableData = res.data.result
          this.tableData.map((item, idx) => {
            item.createTime = moment(item.createTime * 1000).format('LLL')
          })
        } else {
          this.tableData = []
        }
      } catch (error) {
        this.tableData = []
      }
    },
    async cancelOrder(data) {
      // 取消订单
      // console.log(data)
      // 构造数据
      let operation = 'cancelExchange'

      // 构造args
      let exchange_id = data.orderId
      let args = [
        {
          type: 'ByteArray',
          value: exchange_id
        }
      ]
      let params = {
        operation,
        args
      }

      try {
        const result = await this.$store.dispatch('dapiInvoke', params)
        console.log(result)

        if (result && result.transaction) {
          this.getBuyOrder()
          this.$message({
            message: this.$t('common.cancel_suc'),
            type: 'success',
            center: true,
            duration: 2000
          })
        } else {
          this.$message({
            message: this.$t('common.cancel_fail'),
            type: 'error',
            center: true,
            duration: 2000
          })
        }
      } catch (error) {
        this.$message({
          message: this.$t('common.cancel_fail'),
          type: 'error',
          center: true,
          duration: 2000
        })
      }
    },
    async confirmReceipt(data) {
      //  确认收货
      let sureParams = { orderId: data.orderId }
      try {
        let result = await this.$store.dispatch('SureOrder', sureParams)
        if (result.data.desc === 'SUCCESS') {
          this.commonId = result.data.result.id
          let codeParams = result.data.result.qrCode
          let qrparams = {
            params: codeParams,
            isShow: true
          }
          this.$store.dispatch('changeQrcode', qrparams)
          clearInterval(this.commonTimer)
          this.commonTimer = setInterval(async () => {
            let result = await this.$store.dispatch(
              'getCheckRes',
              this.commonId
            )
            console.log('result orjs', result)
            if (result === 1) {
              clearInterval(this.commonTimer)
              this.$message({
                message: this.$t('common.delivery_suc'),
                center: true,
                type: 'success'
              })
            } else if (result === 0) {
              clearInterval(this.commonTimer)
              this.$message({
                message: this.$t('common.delivery_fail'),
                type: 'error',
                center: true,
                duration: 2000
              })
            } else if (result === 4) {
              clearInterval(this.commonTimer)
            } else {
            }
          }, 3000)
        } else {
          this.$message({
            message: this.$t('common.delivery_fail'),
            type: 'error',
            center: true,
            duration: 2000
          })
        }
      } catch (error) {
        return false
      }
    },
    handleCurrentChange(val) {
      this.pageNum = val - 1
      this.getBuyOrder()
    },
    async viewInfo(data) {
      console.log('data', data)
      try {
        let res = await this.$store.dispatch('jwtMsg', data.tokenId)
        // console.log('makeTransaction', res)
        if (res.data.desc === 'SUCCESS') {
          let commonId = res.data.result.id
          let codeParams = res.data.result.qrCode
          let qrparams = {
            params: codeParams,
            isShow: true
          }
          this.$store.dispatch('changeQrcode', qrparams)
          clearInterval(this.viewTimer)
          this.viewTimer = setInterval(() => {
            this.getdataResult(commonId)
          }, 3000)
        } else {
          return this.$message({
            message: this.$t('common.view_fail'),
            type: 'error',
            center: true,
            duration: 2000
          })
        }
      } catch (error) {
        return this.$message({
          message: this.$t('common.view_fail'),
          type: 'error',
          center: true,
          duration: 2000
        })
      }
    },
    openMsgBox(msg) {
      this.$alert(msg, 'Message', {
        confirmButtonText: this.$t('common.sure'),
        callback: action => {}
      })
    },
    async toAppeal(data) {
      let appealParams = {}
      if (data.authId) {
        appealParams = {
          argsList: [{ name: 'orderId', value: 'ByteArray:' + data.orderId }],
          contractHash: '57a078f603a6894ea4c3688251b981e543fe1cb1',
          method: 'applyArbitrage'
        }
      } else {
        appealParams = {
          argsList: [{ name: 'orderId', value: 'ByteArray:' + data.orderId }],
          contractHash: '7c2b06ae3e70a470d01ac5ce63017d18b88e08b7',
          method: 'applyArbitrage'
        }
      }

      let paramsData = {
        txHex: '',
        pubKeys: '',
        sigData: ''
      }
      try {
        let res = await this.$store.dispatch('makeTransaction', appealParams)
        console.log('makeTransaction', res)
        if (res.data.desc === 'SUCCESS') {
          paramsData.txHex = res.data.result
        } else {
          this.$message({
            message: this.$t('common.to_appeal_fail'),
            type: 'error',
            center: true,
            duration: 2000
          })
          return
        }
      } catch (error) {
        this.$message({
          message: this.$t('common.to_appeal_fail'),
          type: 'error',
          center: true,
          duration: 2000
        })
        return
      }

      try {
        let message = paramsData.txHex
        message = message.slice(0, message.length - 2)
        message = Ont.utils.sha256(message)
        message = Ont.utils.sha256(message)
        message = Ont.utils.hexstr2str(message)
        let signData = await client.api.message.signMessage({ message })
        paramsData.pubKeys = signData.publicKey
        paramsData.sigData = signData.data
      } catch (error) {
        this.$message({
          message: this.$t('common.to_appeal_fail'),
          type: 'error',
          center: true,
          duration: 2000
        })
        return
      }
      try {
        let res = await this.$store.dispatch('sendPass', paramsData)
        console.log('sendPass', res)
        if (res.data.desc === 'SUCCESS') {
          this.$message({
            message: this.$t('common.to_appeal_suc'),
            type: 'success',
            center: true,
            duration: 2000
          })
        } else {
          this.$message({
            message: this.$t('common.to_appeal_fail'),
            type: 'error',
            center: true,
            duration: 2000
          })
        }
      } catch (error) {
        this.$message({
          message: this.$t('common.to_appeal_fail'),
          type: 'error',
          center: true,
          duration: 2000
        })
        return
      }
    },
    async viewOther(data) {
      try {
        let res = await this.$store.dispatch('viewOtherInfo', data.tokenId)
        if (res.status === 200 && res.data.desc === 'SUCCESS') {
          let infoCode = res.data.result
          let timer = ''
          if (+infoCode.expireTime == 1) {
            timer = 'everlasting'
          } else {
            timer = moment(infoCode.expireTime * 1000).format('LLL')
          }
          let message = `
            accessCount: ${infoCode.accessCount}
            transferCount: ${infoCode.transferCount},
            expireTimeCount: ${timer}
          `
          console.log('message', message)
          this.openMsgBox(message)
        } else {
          this.$message({
            message: this.$t('common.view_fail'),
            type: 'error',
            center: true,
            duration: 2000
          })
        }
      } catch (error) {
        throw error
        this.$message({
          message: this.$t('common.view_fail'),
          type: 'error',
          center: true,
          duration: 2000
        })
      }
    },
    async Resale(data) {
      try {
        let res = await this.$store.dispatch('viewOtherInfo', data.tokenId)
        console.log('viewInfo', res.data.result)
        if (res.status === 200 && res.data.desc === 'SUCCESS') {
          let infoCode = res.data.result
          if (+infoCode.accessCount <= 0 || +infoCode.transferCount <= 0) {
            this.$message({
              message: this.$t('common.resale_noenough'),
              type: 'error',
              center: true,
              duration: 2000
            })
            return
          }
        } else {
          this.$message({
            message: this.$t('common.resale_fail'),
            type: 'error',
            center: true,
            duration: 2000
          })
        }
      } catch (error) {
        console.log('error', error)
        this.$message({
          message: this.$t('common.resale_fail'),
          type: 'error',
          center: true,
          duration: 2000
        })
      }
      sessionStorage.setItem('resale_tokenId', data.tokenId)
      sessionStorage.setItem('resale_id', data.id)
      this.$router.push({
        path: 'resaleDetail',
        query: { commodityId: data.dataId }
      })
    },
    toDataIDList(dataId) {
      this.$router.push({ name: 'DataIDList', query: { dataId: dataId } })
    },
    toTokenId(tokenId) {
      this.$router.push({ name: 'TokenDetail', query: { tokenId: tokenId } })
    },
    async getdataResult(tok) {
      if (this.isShow) {
        try {
          let res = await this.$store.dispatch('getdataRes', tok)
          console.log('getdataResult', res)
          if (res.data.desc === 'SUCCESS') {
            if (res.data.result.result === '2') {
              let jwt = res.data.result.jwt
              this.$message({
                message: 'Successful',
                center: true,
                type: 'success'
              })
              this.$store.commit('CHANGE_MODEL_STATE', false)
              clearInterval(this.viewTimer)
              let url =
                process.env.VUE_APP_FILE_API +
                '/api/v1/data/access?token=' +
                jwt
              window.open(url, '_self')
              return true
            } else if (res.data.result.result === '0') {
              this.$message({
                message: this.$t('common.view_fail_to'),
                center: true,
                type: 'error'
              })
              clearInterval(this.viewTimer)
              return false
            } else {
            }
          } else {
            this.$message({
              message: this.$t('common.view_fail'),
              center: true,
              type: 'error'
            })
            clearInterval(this.viewTimer)
            return false
          }
        } catch (error) {
          clearInterval(this.viewTimer)
          return false
        }
      } else {
        clearInterval(this.viewTimer)
        return
      }
    },
    downloadfiles(jwt) {
      console.log('jwt', jwt)
      try {
        let url = process.env.VUE_APP_API + '/api/v1/data/access?token=' + jwt
        console.log('url', url)
        window.open(url)
      } catch (error) {
        throw error
      }
    }
  },
  async mounted() {
    this.accountid = sessionStorage.getItem('user_ontid')
    if (!this.accountid) {
      return
    }
    this.getBuyOrder()
  },
  beforeDestroy() {
    clearInterval(this.commonTimer)
  }
}
</script>

<style lang="less" scoped>
.paginatio {
  margin: 20px auto;
}
</style>
