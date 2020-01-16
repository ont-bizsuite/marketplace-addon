<template>
  <div class="addBox">
    <h2>{{ $t('common.ready_shelf') }}</h2>
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
    <div class="formBox">
      <el-form
        :model="dynamicValidateForm"
        ref="dynamicValidateForm"
        label-width="100px"
        class="demo-dynamic"
      >
        <!-- 商品名 -->
        <el-form-item
          prop="data.name"
          :label="tableLang.name"
          :rules="[{ required: true, message: tableLang.nameTip }]"
        >
          <el-input v-model="dynamicValidateForm.data.name"></el-input>
        </el-form-item>
        <!-- 描述 -->
        <el-form-item prop="data.desc" :label="tableLang.desc">
          <el-input v-model="dynamicValidateForm.data.desc"></el-input>
        </el-form-item>
        <!-- price -->
        <el-form-item
          prop="price"
          :label="tableLang.price"
          :rules="[
            { required: true, message: tableLang.priceTip1 },
            { type: 'number', message: tableLang.priceTip2 }
          ]"
          style="text-align:left;"
        >
          <el-input-number
            v-model="dynamicValidateForm.price"
            controls-position="right"
            :min="0"
            :max="100"
          ></el-input-number>
          <!-- <el-input type='number' min="1" max="100"  v-model.number="dynamicValidateForm.price"></el-input> -->
        </el-form-item>
        <!-- 标签 -->
        <el-form-item
          v-for="(tag, index) in dynamicValidateForm.tags"
          :label="tableLang.tag + (index + 1)"
          :key="tag.key"
          :prop="'tags.' + index + '.value'"
          :rules="{
            required: true,
            message: tableLang.tagTip,
            trigger: 'blur'
          }"
        >
          <el-input v-model="tag.value"></el-input>
          <el-button @click.prevent="removetag(tag)">{{
            $t('common.delete')
          }}</el-button>
        </el-form-item>

        <el-form-item class="no_left">
          <el-button @click="addtag">{{ $t('common.add_new_tag') }}</el-button>
          <el-button
            type="primary"
            @click="submitForm('dynamicValidateForm')"
            >{{ $t('common.shelf') }}</el-button
          >
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import { client } from 'ontology-dapi'
import { sha256 } from 'js-sha256'
import moment from 'moment'

export default {
  data() {
    return {
      tableLang: {
        name: this.$t('common.commodity_name'),
        nameTip:
          this.$t('common.please_enter') + this.$t('common.commodity_name'),
        desc: this.$t('common.description'),
        descTip: this.$t('common.please_enter') + this.$t('common.description'),
        imgAddress: this.$t('common.img_address'),
        dataSource: this.$t('common.data_source'),
        tag: this.$t('common.tag'),
        tagTip: this.$t('common.tag_not_empty'),
        certifier: this.$t('common.certifier'),
        cerTips: this.$t('common.cerTip'),
        price: this.$t('common.price'),
        amount: this.$t('common.amount'),
        symbol: this.$t('common.symbol'),
        tokenName: this.$t('common.tokenName'),
        transferCount: this.$t('common.transferCount'),
        accessCount: this.$t('common.accessCount'),
        expireTime: this.$t('common.expireTime'),
        priceTip1: this.$t('common.please_enter') + this.$t('common.price'),
        amountTip1: this.$t('common.please_enter') + this.$t('common.amount'),
        priceTip2: this.$t('common.price_tip2'),
        coin: this.$t('common.coin'),
        coinTip: this.$t('common.please_enter') + this.$t('common.coin'),
        judger: this.$t('common.judger'),
        tokenNum: this.$t('common.number'),
        tokenNumTip: this.$t('common.please_enter') + this.$t('common.number')
      },
      dynamicValidateForm: {
        tags: [
          {
            value: ''
          }
        ],
        price: 1,
        amount: '',
        symbol: '',
        tokenName: '',
        transferCount: '',
        accessCount: 999999999,
        expireTime: '',
        coin: 'ONG',
        data: {
          dataId: null,
          name: null,
          desc: null,
          token: null,
          keywords: [],
          img: null,
          metadata: null
        },
        certifier: '',
        judger: '',
        // juderTime: '',
        judgerType: [],
        tokenNum: null,
        tokenId: null,
        tokenTotal: null
      },
      accountid: '',
      metadata: {
        context: '',
        type: 'Dataset',
        identifier: '', //
        name: '', // name----name
        description: '', //  description
        keywords: '',
        publisher: {
          type: 'Person',
          identifier: '', // ONT ID
          dateCreated: new Date()
        },
        datePublished: new Date()
      },
      ont_id: null,
      certifierArr: [],
      judgerArr: [],
      detailList: { data: {}, nnn: 2 },
      hashId: null,
      idTimer: null,
      DId: null,
      dataIdTimer: null,
      currentDataId: ''
    }
  },
  methods: {
    submitForm(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          console.log(this.dynamicValidateForm)
          this.addNewData()
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
    resetForm(formName) {
      this.$refs[formName].resetFields()
    },
    removetag(item) {
      var index = this.dynamicValidateForm.tags.indexOf(item)
      if (index !== 0) {
        this.dynamicValidateForm.tags.splice(index, 1)
      }
    },
    addtag() {
      this.dynamicValidateForm.tags.push({
        value: '',
        key: Date.now()
      })
    },
    toIndex() {
      this.$router.push({ path: '/' })
    },
    async addNewData() {
      console.log('this.dataParams', this.dataParams)
      try {
        let result = await this.$store.dispatch(
          'ShelfAuthorization',
          this.dataParams
        )
        console.log('result', result)
        if (result.data.desc === 'SUCCESS') {
          this.DId = result.data.result.id
          let codeParams = result.data.result.qrCode
          let qrparams = {
            params: codeParams,
            isShow: true
          }
          this.$store.dispatch('changeQrcode', qrparams)
          clearInterval(this.dataIdTimer)
          this.dataIdTimer = setInterval(async () => {
            let result = await this.$store.dispatch('getCheckRes', this.DId)
            console.log('result orjs', result)
            if (result === 1) {
              clearInterval(this.dataIdTimer)
              this.$message({
                message: this.$t('common.pro_success'),
                center: true,
                type: 'success',
                offset: 400,
                duration: 2000
              })
              this.$router.push({ path: '/' })
            } else if (result === 0) {
              clearInterval(this.dataIdTimer)
              this.$message({
                message: this.$t('common.pro_fail'),
                type: 'error',
                center: true,
                duration: 2000
              })
            } else if (result === 4) {
              clearInterval(this.dataIdTimer)
            } else {
            }
          }, 3000)
        } else {
          this.$message({
            message: this.$t('common.pro_fail'),
            type: 'error',
            center: true,
            duration: 2000
          })
        }
      } catch (error) {
        this.$message({
          message: this.$t('common.pro_fail'),
          type: 'error',
          center: true,
          duration: 2000
        })
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
          this.currentDataId = res.data.result.dataId
        }
      } catch (error) {
        console.log(error)
      }
    },
    back() {
      this.$router.go(-1)
    }
  },
  async mounted() {
    this.accountid = sessionStorage.getItem('user_ontid').substring(8)
    this.ont_id = sessionStorage.getItem('user_ontid')
    let data_id = this.$route.query.commodityId
    await this.getDetail(data_id)
    let pars = {
      ontid: this.ont_id.substring(8),
      tokenId: this.detailList.tokenId
    }
  },
  computed: {
    dataParams() {
      let cpas = {
        provider: '',
        tags: []
      }
      cpas.accessCount = 10
      cpas.amount = 10
      cpas.dataId = this.currentDataId
      cpas.description = this.dynamicValidateForm.data.desc
      cpas.expireTime = 0
      cpas.id = this.$route.query.commodityId
      cpas.name = this.dynamicValidateForm.data.name
      cpas.price = this.dynamicValidateForm.price * Math.pow(10, 9)
      cpas.provider = this.ont_id
      cpas.symbol = 'ont'
      this.dynamicValidateForm.tags.map(item => {
        cpas.tags.push(item.value)
      })
      cpas.transferCount = 10
      return cpas
    }
  },
  beforeDestroy() {
    clearInterval(this.dataIdTimer)
  }
}
</script>

<style lang="less" scoped>
.addBox {
  margin: 0 auto;
  width: 80%;
  .formBox {
    .el-input {
      width: 80%;
      float: left;
    }
  }
  .no_left {
    /deep/ .el-form-item__content {
      margin-left: 0 !important;
    }
  }
}
</style>
