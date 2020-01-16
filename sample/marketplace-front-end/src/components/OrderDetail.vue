<template>
  <div class="detail_box">
    <div style="overflow: hidden; margin-bottom: 20px;">
      <el-button
        @click="$router.go(-1)"
        type="primary"
        plain
        style="float: left"
        >{{ $t('common.back_to_prev') }}</el-button
      >
      <el-button @click="toIndex()" type="primary" plain style="float: right">{{
        $t('common.to_home')
      }}</el-button>
    </div>
    <div class="item_box">
      <!-- 商品名 -->
      <div class="item">
        <p>{{ $t('common.commodity_name') }}:</p>
        {{ orderData.name }}
      </div>
      <div class="item">
        <p>{{ $t('common.tag') }}:</p>
        <el-tag v-for="(item, idx) in orderData.tags" :key="idx">{{
          item
        }}</el-tag>
      </div>
      <div class="item">
        <p>{{ $t('common.coin') }}:</p>
        ONG
      </div>
      <div class="item" v-show="orderData.price">
        <p>{{ $t('common.price') }}:</p>
        {{ orderData.price * Math.pow(10, -9) }}
      </div>
      <!-- 描述 -->
      <div class="item" v-if="orderData.desc">
        <p>{{ $t('common.description') }}:</p>
        {{ orderData.desc }}
      </div>
      <div class="item">
        <p>{{ $t('common.number') }}:</p>
        {{ orderData.amount }}
      </div>
      <div class="item" v-show="orderData.judger">
        <p>{{ $t('common.judger') }}:</p>
        <el-radio
          v-for="(item, idx) in orderData.judger"
          :key="idx"
          v-model="OJlist"
          :label="item"
          >{{ item }}</el-radio
        >
      </div>
      <div class="item">
        <p>{{ $t('common.create_time') }}:</p>
        {{ orderData.createTime | formatTime }}
      </div>
    </div>
    <el-button type="success" :disabled="signing" @click="toBuy()" round>{{
      $t('common.buy')
    }}</el-button>
  </div>
</template>

<script>
import { client } from 'ontology-dapi'
import moment from 'moment'

export default {
  data() {
    return {
      signing: false,
      OJlist: null,
      orderData: {
        name: ''
      },
      address: null,
      ontid: null,
      orderHashId: null,
      hashTimer: null
    }
  },
  created() {
    let data_id = this.$route.query.commodityId
    this.getDetail(data_id)
    if (sessionStorage.getItem('user_ontid')) {
      this.ontid = sessionStorage.getItem('user_ontid')
    }
  },
  methods: {
    toIndex() {
      this.$router.push({ path: '/' })
    },
    async toBuy() {
      let ontid = sessionStorage.getItem('user_ontid')
      if (!ontid) {
        this.$message({
          message: this.$t('common.lg_by'),
          type: 'error',
          center: true,
          duration: 2000
        })
        return
      }
      let dataParams = {
        authId: this.orderData.authId,
        demander: sessionStorage.getItem('user_ontid'),
        price: this.orderData.price,
        provider: this.orderData.ontid,
        tokenAmount: 1
      }

      try {
        let result = await this.$store.dispatch('makeOrder', dataParams)
        console.log('result', result)
        if (result.data.desc === 'SUCCESS') {
          this.orderHashId = result.data.result.id
          let codeParams = result.data.result.qrCode
          console.log('codeParams', codeParams)
          let qrparams = {
            params: codeParams,
            isShow: true
          }
          this.$store.dispatch('changeQrcode', qrparams)
          clearInterval(this.hashTimer)
          this.hashTimer = setInterval(async () => {
            let result = await this.$store.dispatch(
              'getCheckRes',
              this.orderHashId
            )
            console.log('result orjs', result)
            if (result === 1) {
              clearInterval(this.hashTimer)
              this.$message({
                message: this.$t('common.buy_suc'),
                center: true,
                type: 'success'
              })
              this.$router.push({ path: '/' })
            } else if (result === 0) {
              clearInterval(this.hashTimer)
              this.$message({
                message: this.$t('common.buy_fail'),
                type: 'error',
                center: true,
                duration: 2000
              })
            } else if (result === 4) {
              clearInterval(this.hashTimer)
            } else {
            }
          }, 3000)
        } else {
          this.$message({
            message: this.$t('common.buy_fail'),
            type: 'error',
            center: true,
            duration: 2000
          })
          return false
        }
      } catch (error) {
        return error
      }
    },
    async getDetail(id) {
      let params = {
        id
      }
      try {
        let res = await this.$store.dispatch('getCommodityDetail', params)
        console.log(res)
        if (res.status === 200 && res.data.desc === 'SUCCESS') {
          this.orderData = res.data.result
        }
      } catch (error) {
        console.log(error)
      }
    }
  },
  filters: {
    formatTime(val) {
      if (!val) {
        return ''
      } else {
        return moment(val * 1000).format('LLL')
      }
    }
  },
  beforeDestroy() {
    clearInterval(this.hashTimer)
  }
}
</script>

<style lang="less" scoped>
.detail_box {
  width: 80%;
  margin: 40px auto;
  .item_box {
    width: 50%;
    margin: 0 auto;
    text-align: left;
    margin-bottom: 20px;
    .item {
      margin-bottom: 30px;
      p {
        font-size: 22px;
        color: #000;
        line-height: 34px;
      }
      img {
        width: 160px;
      }
      span.el-tag {
        margin-right: 20px;
      }
    }
  }
}
</style>
