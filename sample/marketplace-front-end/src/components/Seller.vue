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
        prop="demander"
        :label="tableLang.buyer"
        align="center"
      ></el-table-column>
      <el-table-column
        prop="orderId"
        :label="tableLang.order_num"
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
      <el-table-column label="Token ID" width="200" align="center">
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
        prop="createTime"
        :label="tableLang.buy_date"
        width="280"
        align="center"
      ></el-table-column>
      <el-table-column :label="tableLang.state" width="180" align="center">
        <template slot-scope="scope">
          <el-tag size="medium" type="info" v-if="scope.row.state == 1">{{
            $t('common.sold_out')
          }}</el-tag>
          <el-tag size="medium" type="info" v-else-if="scope.row.state == 4">{{
            $t('common.order_over')
          }}</el-tag>
          <!-- <el-tag
            size="medium"
            type="info"
            v-else-if="scope.row.state == 2"
          >{{$t('common.sold_out')}}</el-tag>
          <el-tag size="medium" type="info" v-else-if="scope.row.state == 4">{{$t('common.appeal')}}</el-tag>
          <el-tag size="medium" type="info" v-else>{{$t('common.apple_end')}}</el-tag>-->
        </template>
      </el-table-column>
      <el-table-column :label="tableLang.operating" width="180" align="center">
        <template slot-scope="scope">
          <el-button
            v-show="scope.row.state == 2 && scope.row.isExpire"
            @click="collectMoney(scope.row)"
            type="primary"
            >{{ $t('common.receipt') }}</el-button
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
import { Base64 } from 'js-base64'
import moment from 'moment'

export default {
  data() {
    return {
      tableLang: {
        buyer: this.$t('center.buy'),
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
      commonId: '',
      commonTimer: null
    }
  },
  methods: {
    async toShip(data, value) {
      console.log(data)
      // 构造数据
      let operation = 'sendEncMessage'
      // 构造args
      let exchange_id = data.orderId //   ByteArray
      console.log(exchange_id)

      let str = value // 发货的数据
      str = client.api.utils.strToHex(str)
      let message_list = [
        {
          type: 'ByteArray',
          value: str
        }
      ]

      let args = [
        {
          type: 'ByteArray',
          value: exchange_id
        },
        {
          type: 'Array',
          value: message_list
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
          this.getSellOrder()
          this.$message({
            message: '发货成功！',
            type: 'success',
            center: true,
            duration: 2000
          })
        } else {
          this.$message({
            message: '发货失败，请重试！',
            type: 'error',
            center: true,
            duration: 2000
          })
        }
      } catch (error) {
        this.$message({
          message: '发货失败，请重试！',
          type: 'error',
          center: true,
          duration: 2000
        })
      }
    },
    indexMethod(index) {
      return this.pageNum * this.pageSize + index + 1
    },
    async getSellOrder() {
      try {
        let params = {
          ontid: this.accountid,
          type: 1
        }
        let res = await this.$store.dispatch('getBuyOrder', params)
        if (res.status === 200 && res.data.desc === 'SUCCESS') {
          this.tableData = res.data.result
          this.tableData.map((item, idx) => {
            item.createTime = moment(item.createTime * 1000).format('LLL')
          })
        } else {
          this.tableData = []
        }
      } catch (error) {
        console.error(error)
        this.tableData = []
      }
    },
    async collectMoney(data) {
      let sureParams = { orderId: data.orderId }
      try {
        let result = await this.$store.dispatch('SureOrder', sureParams)
        if (result.data.desc === 'SUCCESS') {
          this.commonId = result.data.result.appId
          let codeParams = result.data.result
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
                message: this.$t('common.receipt_suc'),
                center: true,
                type: 'success'
              })
              this.getSellOrder()
            } else if (result === 0) {
              clearInterval(this.commonTimer)
              this.$message({
                message: this.$t('common.receipt_fail'),
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
            message: this.$t('common.receipt_fail'),
            type: 'error',
            center: true,
            duration: 2000
          })
        }
      } catch (error) {
        throw error
        return false
      }
    },
    handleCurrentChange(val) {
      this.pageNum = val - 1
      this.getSellOrder()
    },
    openMsgBox(data) {
      this.$prompt('请输入内容', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPattern: /\S/,
        inputErrorMessage: '内容不能为空'
      })
        .then(({ value }) => {
          value = Base64.encode(value)
          console.log('Base64.encode(value)', value)
          this.toShip(data, value)
        })
        .catch(() => {})
    },
    toDataIDList(dataId) {
      this.$router.push({ name: 'DataIDList', query: { dataId: dataId } })
    },
    toTokenId(tokenId) {
      this.$router.push({ name: 'TokenDetail', query: { tokenId: tokenId } })
    }
  },
  async mounted() {
    this.accountid = sessionStorage.getItem('user_ontid')
    // this.accountid = await client.api.asset.getAccount()
    console.log(this.accountid)
    if (!this.accountid) {
      return
    }
    this.getSellOrder()
  }
}
</script>

<style lang="less" scoped>
.paginatio {
  margin: 20px auto;
}
</style>
