<template>
  <div class="detail_box">
    <div style="overflow: hidden; margin-bottom: 20px;">
      <el-button
        @click="$router.go(-1)"
        type="primary"
        plain
        style="float: left"
      >{{$t('common.back_to_prev')}}</el-button>
      <el-button
        @click="toIndex()"
        type="primary"
        plain
        style="float: right"
      >{{$t('common.to_home')}}</el-button>
    </div>
    <div class="item_box">
      <!-- 商品名 -->
      <div class="item">
        <p>{{$t('common.commodity_name')}}:</p>
        {{detailList.data.name}}
      </div>
      <!-- 图片 -->
      <div class="item">
        <p>{{$t('common.thumbnail')}}:</p>
        <img v-if="detailList.data.img" :src="detailList.data.img" alt />
        <img v-else src="https://ont.io/upload_img/20190420001238_417.png" alt />
      </div>
      <div class="item">
        <p>{{$t('common.tag')}}:</p>
        <el-tag v-for="(item, idx) in detailList.data.keywords" :key="idx">{{item}}</el-tag>
      </div>
      <div class="item" v-show="detailList.coin">
        <p>{{$t('common.coin')}}:</p>
        {{detailList.coin}}
      </div>
      <div class="item" v-show="detailList.price">
        <p>{{$t('common.price')}}:</p>
        {{detailList.price}}
      </div>
      <!-- 描述 -->
      <div class="item">
        <p>{{$t('common.description')}}:</p>
        {{detailList.data.desc}}
      </div>
      <div class="item">
        <p>{{$t('common.certifier')}}:</p>
        {{detailList.certifier}}
        <!-- 认证状态 -->
        <br />
        <el-tag type="info" v-if="detailList.isCertificated === 0">{{$t('common.no_cert')}}</el-tag>
        <el-tag type="success" v-else>{{$t('common.verified')}}</el-tag>
      </div>

      <div class="item" v-show="detailList.data.dToken">
        <p>dToken:</p>
        {{detailList.data.dToken}}
      </div>
      <div class="item" v-show="detailList.data.dataId">
        <p>dataId:</p>
        {{detailList.data.dataId}}
      </div>
      <div class="item">
        <p>{{$t('common.create_time')}}:</p>
        {{detailList.createTime}}
      </div>
    </div>
    <el-button
      type="success"
      :disabled="signing"
      round
      v-if="isCert === 0"
      @click="toCert()"
    >{{$t('common.toCer')}}</el-button>
    <el-button
      type="success"
      :disabled="signing"
      round
      v-else-if="isCert === 1"
    >{{$t('common.verified')}}</el-button>
    <el-button type="success" :disabled="signing" v-else round>{{$t('common.detail')}}</el-button>
  </div>
</template>

<script>
import { client } from 'ontology-dapi'
import { mapState } from 'vuex'

export default {
  data() {
    return {
      signing: false,
      detailList: { data: {} },
      isCert: null,
      OJlist: null,
      cerId: '',
      cerTimer: null
    }
  },
  created() {
    let id = this.$route.query.commodityId
    this.getDetail(id)
    this.isCert = +sessionStorage.getItem('isCert')
    console.log(this.isCert)
    // console.log(this.query())
  },
  methods: {
    toIndex() {
      this.$router.push({ path: '/' })
    },
    async getDetail(id) {
      let params = {
        id
      }
      try {
        let res = await this.$store.dispatch('getCommodityDetail', params)
        console.log(res)
        if (res.status === 200 && res.data.desc === 'SUCCESS') {
          this.detailList = res.data.result
        }
      } catch (error) {
        console.log(error)
      }
    },
    async toCert() {
      this.$confirm(this.$t('common.sure_cert'), this.$t('common.prompt'), {
        confirmButtonText: this.$t('common.sure'),
        cancelButtonText: this.$t('common.cancel'),
        type: 'success'
      }).then(async () => {//确定

        try {
          let result = await this.$store.dispatch('getCerMsg', this.$route.query.commodityId)
          console.log('this.$route.query.commodityId', result)
          if (result.data.desc === 'SUCCESS') {
            this.cerId = result.data.result.id
            let codeParams = {
              action: 'signMessage',
              version: 'v1.0.0',
              id: result.data.result.id,
              params: {
                // type: 'ontid',
                type: 'ontid',
                // domain: 'on.ont',
                dappName: 'dapp Name',
                dappIcon: 'dapp Icon',
                message: result.data.result.message,
                callback: result.data.result.callback,
              }
            }
            console.log('codeParams', codeParams)
            let qrparams = {
              params: codeParams,
              isShow: true
            }
            this.$store.dispatch('changeQrcode', qrparams)
            clearInterval(this.cerTimer)
            this.cerTimer = setInterval(async () => {
              let result = await this.$store.dispatch('getCheckRes', this.cerId)
              console.log('result orjs', result)
              if (result === 1) {
                clearInterval(this.cerTimer)
                sessionStorage.setItem('isCert', 1)
                this.isCert = 1
                window.location.reload();
              } else if (result === 3) {
                clearInterval(this.cerTimer)
                this.$message({
                  message: this.$t('common.buy_fail'),
                  type: 'error',
                  center: true,
                  duration: 2000
                });
              } else if (result === 4) { clearInterval(this.cerTimer) } else {}
            }, 3000)
          } else {
            this.$message({
              message: 'Get Message Fail!',
              center: true,
              type: 'error'
            });
            return
          }
        } catch (error) {
          this.$message({
            message: error,
            type: 'error',
            center: true,
            duration: 2000
          });
          return
        }
      }).catch((error) => {
        this.$message({
          message: error,
          center: true,
          type: 'error'
        });
        this.$store.commit('CHANGE_MODEL_STATE', false)
      });
    },
    query() {
      var urlSearch = window.location.hash;
      var searchParamObj = {};

      if (urlSearch.indexOf('?') > -1) {
        let idx = urlSearch.indexOf('?')
        var searchArr = urlSearch.substr(idx + 1).split('&');

        for (var m = 0; m < searchArr.length; m++) {
          searchParamObj[searchArr[m].split('=')[0]] = searchArr[m].split('=')[1];
        }
      }

      return searchParamObj;
    }
  },
  computed: {
    ...mapState({
      isShow: state => state.qrcodeParams.isShow,
    })
  },
  beforeDestroy() {
   clearInterval(this.cerTimer) 
  }
}
</script>

<style lang='less' scoped>
.detail_box {
  width: 80%;
  margin: 40px auto;
  .item_box {
    width: 50%;
    margin: 0 auto;
    text-align: left;
    margin-bottom: 20px;
    .item {
      p {
        font-size: 22px;
        color: #000;
        line-height: 28px;
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
