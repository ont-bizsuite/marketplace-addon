<template>
  <div>
    <div class="list_box">
      <el-table :data="tableData" border style="width: 100%" :empty-text="$t('common.no_data')">
        <el-table-column type="index" align="center" width="50" :index="indexMethod"></el-table-column>
        <el-table-column prop="name" align="center" :label="tableLang.name" width="180"></el-table-column>
        <el-table-column align="center" :label="tableLang.tags">
          <template slot-scope="scope">
            <el-tag
              style="margin-right: 10px;"
              v-for="(item, idx) in scope.row.keywords"
              :key="idx"
            >{{item}}</el-tag>
          </template>
        </el-table-column>
        <el-table-column align="center" :label="tableLang.coin" width="80">ONG</el-table-column>
        <el-table-column prop="price" align="center" :label="tableLang.price" width="80"></el-table-column>
        <el-table-column prop="boughtTime" align="center" :label="tableLang.date" width="200"></el-table-column>
        <el-table-column :label="tableLang.operating" align="center" width="200">
          <template slot-scope="scope">
            <el-button
              @click="showDivide(scope.row, true)"
              type="text"
              size="small"
            >{{$t('common.win')}}</el-button>
            <el-button
              @click="showDivide(scope.row, false)"
              type="text"
              size="small"
            >{{$t('common.lose')}}</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog
      :title="$t('common.arbitrage_result')"
      :visible.sync="dialogFormVisible"
      center
      width="30%"
    >
      <el-form :model="form" :rules="rules" ref="ruleForm">
        <el-form-item :label="$t('common.maker_receive_amount')" prop="makerReceiveAmount">
          <el-input v-model="form.makerReceiveAmount" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item :label="$t('common.taker_receive_amount')" prop="takerReceiveAmount">
          <el-input v-model="form.takerReceiveAmount" autocomplete="off"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">{{$t('common.cancel')}}</el-button>
        <el-button type="primary" @click="arbitrage()">{{$t('common.sure')}}</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { client } from 'ontology-dapi'
// import { OntidContract, TransactionBuilder, TxSignature, Identity, Crypto, RestClient, utils } from 'ontology-ts-sdk';

export default {
  data() {
    return {
      tableData: [],
      ontid: '',
      tableLang: {
        name: this.$t('common.commodity_name'),
        tags: this.$t('common.tags'),
        coin: this.$t('common.coin'),
        price: this.$t('common.price'),
        date: this.$t('common.date'),
        operating: this.$t('common.operating'),
      },
      dialogFormVisible: false,
      judgeData: {},
      winOrLose: '',
      form: {
        makerReceiveAmount: '',
        takerReceiveAmount: '',
      },
      rules: {
        makerReceiveAmount: [
          { required: true, message: this.$t('common.please_enter') + this.$t('common.maker_receive_amount') },
          // { type: 'number', message: this.$t('common.maker_receive_amount') + this.$t('common.mbnum') }
        ],
        takerReceiveAmount: [
          { required: true, message: this.$t('common.please_enter') + this.$t('common.taker_receive_amount') },
          // { type: 'number', message: this.$t('common.maker_receive_amount') + this.$t('common.mbnum') }
        ]
      },
    }
  },
  methods: {
    async handleClick(data, isWin) {
      let ojparams = {}
      if (data.authId) {
        ojparams = {
          "argsList": [
            { "name": "orderId", "value": "ByteArray:" + data.orderId },
            { "name": "isWin", "value": isWin },
            { "name": "makerReceiveAmount", "value": this.form.makerReceiveAmount * Math.pow(10, 9) },
            { "name": "takerReceiveAmount", "value": this.form.takerReceiveAmount * Math.pow(10, 9) }
          ],
          "contractHash": "57a078f603a6894ea4c3688251b981e543fe1cb1",
          "method": "arbitrage"
        }
      } else {
        ojparams = {
          "argsList": [
            { "name": "orderId", "value": "ByteArray:" + data.orderId },
            { "name": "isWin", "value": isWin },
            { "name": "makerReceiveAmount", "value": this.form.makerReceiveAmount * Math.pow(10, 9) },
            { "name": "takerReceiveAmount", "value": this.form.takerReceiveAmount * Math.pow(10, 9) }
          ],
          "contractHash": "7c2b06ae3e70a470d01ac5ce63017d18b88e08b7",
          "method": "arbitrage"
        }
      }

      console.log('isWin', isWin)
      let paramsData = {
        txHex: '',
        pubKeys: '',
        sigData: ''
      }
      try {
        // console.log('appealParams', appealParams)
        let res = await this.$store.dispatch('makeTransaction', ojparams)
        console.log('makeTransaction', res)
        if (res.data.desc === 'SUCCESS') {
          paramsData.txHex = res.data.result
        } else {
          this.$message({
            message: this.$t('common.oj_fail'),
            type: 'error',
            center: true,
            duration: 2000
          })
          return
        }
      } catch (error) {
        this.$message({
          message: this.$t('common.oj_fail'),
          type: 'error',
          center: true,
          duration: 2000
        })
        return
      }
      // return

      try {
        let message = paramsData.txHex
        message = message.slice(0, message.length - 2)
        message = Ont.utils.sha256(message)
        message = Ont.utils.sha256(message)
        message = Ont.utils.hexstr2str(message)
        let signData = await client.api.message.signMessage({ message });
        paramsData.pubKeys = signData.publicKey
        paramsData.sigData = signData.data
      } catch (error) {
        this.$message({
          message: this.$t('common.oj_fail'),
          type: 'error',
          center: true,
          duration: 2000
        })
        return
      }


      try {
        let params = {
          id: data.id,
          winOrLose: isWin,
          sigVo: paramsData
        }
        let res = await this.$store.dispatch('sendOJData', params)
        console.log('sendOJData', res)
        if (res.data.desc === 'SUCCESS') {
          this.$message({
            message: this.$t('common.oj_suc'),
            type: 'success',
            center: true,
            duration: 2000
          })
        } else {
          this.$message({
            message: this.$t('common.oj_fail'),
            type: 'error',
            center: true,
            duration: 2000
          })
        }
      } catch (error) {
        this.$message({
          message: this.$t('common.oj_fail'),
          type: 'error',
          center: true,
          duration: 2000
        })
        return
      }

    },
    indexMethod(idx) {
      return idx + 1
    },
    showDivide(data, isWin) {
      this.dialogFormVisible = true
      this.judgeData = data
      this.winOrLose = isWin
    },
    async arbitrage() {
      console.log(this.form.makerReceiveAmount)
      console.log(this.form.takerReceiveAmount)
      this.dialogFormVisible = false
      this.handleClick(this.judgeData, this.winOrLose)
    }
  },
  async mounted() {
    // this.ontid = await client.api.identity.getIdentity()
    this.ontid = sessionStorage.getItem("user_ontid")
    let params = {
      ontid: this.ontid,
      pageSize: 10,
      pageNum: 0
    }
    try {
      let res = await this.$store.dispatch('queryjuderData', params)
      if (res.data.desc === 'SUCCESS') {
        this.tableData = res.data.result.recordList
        this.tableData.map((item, idx) => {
          item.price = item.price * Math.pow(10, -9)
        })
      }
    } catch (error) {
      this.tableData = []
      return error
    }
  },
}
</script>

<style>
</style>
