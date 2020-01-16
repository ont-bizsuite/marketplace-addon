<template>
  <div class="addBox">
    <h2>{{$t('common.ready_shelf')}}</h2>
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
          :rules="[
            { required: true, message: tableLang.nameTip }
          ]"
        >
          <el-input disabled v-model="dynamicValidateForm.data.name"></el-input>
        </el-form-item>
        <!-- 描述 -->
        <el-form-item
          prop="data.desc"
          :label="tableLang.desc"
          :rules="[
            { required: true, message: tableLang.descTip }
          ]"
        >
          <el-input disabled v-model="dynamicValidateForm.data.desc"></el-input>
        </el-form-item>
        <!-- 图片地址 -->
        <el-form-item prop="data.img" :label="tableLang.imgAddress">
          <el-input disabled v-model="dynamicValidateForm.data.img"></el-input>
        </el-form-item>
        <!-- price -->
        <el-form-item
          prop="price"
          :label="tableLang.price"
          :rules="[
            { required: true, message: tableLang.priceTip1 },
            { type: 'number', message: tableLang.priceTip2 }
          ]"
        >
          <el-input-number
            v-model="dynamicValidateForm.price"
            controls-position="right"
            :min="0"
            :max="100"
          ></el-input-number>
          <!-- <el-input type='number' min="1" max="100" v-model.number="dynamicValidateForm.price"></el-input> -->
        </el-form-item>
        <!-- 标签 -->
        <el-form-item
          v-for="(tag, index) in dynamicValidateForm.tags"
          :label="tableLang.tag + (index + 1)"
          :key="tag.key"
          :prop="'tags.' + index + '.value'"
          :rules="{
            required: true, message: tableLang.tagTip, trigger: 'blur'
        }"
        >
          <el-input v-model="tag.value" disabled></el-input>
          <el-button disabled @click.prevent="removetag(tag)">{{$t('common.delete')}}</el-button>
        </el-form-item>
        <!-- 币种 coin -->
        <el-form-item :label="tableLang.coin">
          <el-select
            style="float: left;"
            v-model="dynamicValidateForm.coin"
            :placeholder="tableLang.coinTip"
          >
            <el-option label="ONG" value="ONG"></el-option>
            <el-option label="ONT" value="ONT"></el-option>
          </el-select>
        </el-form-item>
        <!-- 认证方 -->
        <el-form-item :label="tableLang.certifier">
          <el-select
            style="float: left;"
            v-model="dynamicValidateForm.certifier"
            :placeholder="tableLang.cerTips"
            disabled
          >
            <el-option
              v-for="item in certifierArr"
              :key="item.id"
              :label="item.ontid"
              :value="item.ontid"
            ></el-option>
          </el-select>
        </el-form-item>
        <!-- 仲裁方 -->
        <el-form-item :label="tableLang.judger">
          <el-checkbox-group v-model="dynamicValidateForm.judgerType">
            <el-checkbox
              v-for="item in judgerArr"
              :key="item.id"
              :label="item.ontid"
              name="judgerType"
            ></el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            @click="submitForm('dynamicValidateForm')"
          >{{$t('common.shelf')}}</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
function uuid(len, radix) {
  var chars = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'.split('');
  var uuid = [], i;
  radix = radix || chars.length;

  if (len) {
    for (i = 0; i < len; i++) uuid[i] = chars[0 | Math.random() * radix];
  } else {
    var r;
    uuid[8] = uuid[13] = uuid[18] = uuid[23] = '-';
    uuid[14] = '4';
    for (i = 0; i < 36; i++) {
      if (!uuid[i]) {
        r = 0 | Math.random() * 16;
        uuid[i] = chars[(i == 19) ? (r & 0x3) | 0x8 : r];
      }
    }
  }

  return uuid.join('');
}
import { client } from 'ontology-dapi'
import { sha256 } from 'js-sha256'
// import { OntidContract, TransactionBuilder, TxSignature, Identity, Crypto, RestClient, utils } from 'ontology-ts-sdk';

export default {
  data() {
    return {
      tableLang: {
        name: this.$t('common.commodity_name'),
        nameTip: this.$t('common.please_enter') + this.$t('common.commodity_name'),
        desc: this.$t('common.description'),
        descTip: this.$t('common.please_enter') + this.$t('common.description'),
        imgAddress: this.$t('common.img_address'),
        dataSource: this.$t('common.data_source'),
        tag: this.$t('common.tag'),
        tagTip: this.$t('common.tag_not_empty'),
        certifier: this.$t('common.certifier'),
        cerTips: this.$t('common.cerTip'),
        price: this.$t('common.price'),
        priceTip1: this.$t('common.please_enter') + this.$t('common.price'),
        priceTip2: this.$t('common.price_tip2'),
        coin: this.$t('common.coin'),
        coinTip: this.$t('common.please_enter') + this.$t('common.coin'),
        judger: this.$t('common.judger'),
        tokenNum: this.$t('common.number'),
        tokenNumTip: this.$t('common.please_enter') + this.$t('common.number'),
      },
      dynamicValidateForm: {
        tags: [],
        price: '',
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
        identifier: '',  // uuid----dataId
        name: '',   // name----name
        description: '', //  description
        keywords: '',
        publisher: {
          type: 'Person',
          identifier: '',   // ONT ID
          dateCreated: new Date()
        },
        datePublished: new Date()
      },
      ont_id: null,
      certifierArr: [],
      judgerArr: [],
      detailList: { data: {}, nnn: 2 },
    };
  },
  methods: {
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          console.log(this.dynamicValidateForm)
          this.addNewData()
        } else {
          console.log('error submit!!');
          return false;
        }
      });
    },
    resetForm(formName) {
      this.$refs[formName].resetFields();
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
      });
    },
    toIndex() {
      this.$router.push({ path: '/' })
    },
    async addNewData() {
      if (this.dataParams.judger.length == 0) {
        this.$message({
          message: this.$t('common.judger_one_tip'),
          type: 'error',
          center: true,
          duration: 2000
        })
        return
      }
      this.dataParams.id = this.detailList.id
      console.log('this.dataParams', this.dataParams)
      // return
      let OJList = []
      this.dataParams.judger.map((item, idx) => {
        OJList.push('Address:' + item.substring(8))
      })


      let tid = sessionStorage.getItem('resale_tokenId')
      let contracParams = {
        argsList: [{
          name: "makerTokenHash",
          value: "ByteArray:3e7d3d82df5e1f951610ffa605af76846802fbae"
        }, {
          name: "makerTokenId",
          value: +tid
        }, {
          name: "makerReceiveAddress",
          value: "Address:" + this.accountid
        }, {
          name: "takerPaymentTokenAmount",
          value: this.dataParams.price * Math.pow(10, 9)
        }, {
          name: "mpReceiveAddress",
          value: "Address:AePd2vTPeb1DggiFj82mR8F4qQXM2H9YpB"
        }, {
          name: "OJList",
          value: OJList
        }],
        contractHash: '7c2b06ae3e70a470d01ac5ce63017d18b88e08b7',
        method: 'makeOrder'
      }
      console.log('contracParams', contracParams)
      console.log('this.dataParams', this.dataParams);
      // 构造交易

      try {
        let res = await this.$store.dispatch('makeTransaction', contracParams)
        console.log('makeTransaction', res)
        if (res.data.desc === 'SUCCESS') {
          let sigVo = {
            txHex: res.data.result,
            pubKeys: '',
            sigData: ''
          }
          let message = res.data.result
          message = message.slice(0, message.length - 2)
          message = Ont.utils.sha256(message)
          message = Ont.utils.sha256(message)
          message = Ont.utils.hexstr2str(message)
          try {
            let signData = await client.api.message.signMessage({ message });
            sigVo.pubKeys = signData.publicKey
            sigVo.sigData = signData.data
          } catch (error) {
            this.$message({
              message: this.$t('common.pro_fail'),
              type: 'error',
              center: true,
              duration: 2000
            })
            return
          }

          let upid = sessionStorage.getItem('resale_id')
          let orderParams = {
            dataId: this.detailList.dataId,
            tokenId: +tid,
            id: upid,
            token: "ong",
            price: this.dataParams.price * Math.pow(10, 9),
            providerOntid: this.ont_id,
            ojList: this.dataParams.judger,
            keywords: this.detailList.data.keywords,
            sigVo,
            // amount: this.dataParams.tokenNum,
            name: this.detailList.data.name,
            desc: this.detailList.data.desc,
            img: this.detailList.data.img
          }

          try {
            let res = await this.$store.dispatch('updateOrder', orderParams)
            console.log('orderParams', res)
            if (res.data.desc === 'SUCCESS' && res.data.result) {
              this.$message({
                message: this.$t('common.pro_success'),
                type: 'success',
                center: true,
                duration: 2000
              })
              this.$router.push({ path: '/' })
            } else {
              this.$message({
                message: this.$t('common.pro_fail'),
                type: 'error',
                center: true,
                duration: 2000
              })
              return false
            }
          } catch (error) {
            this.$message({
              message: this.$t('common.pro_fail'),
              type: 'error',
              center: true,
              duration: 2000
            })
            return false
          }

        } else {
          this.$message({
            message: this.$t('common.pro_fail'),
            type: 'error',
            center: true,
            duration: 2000
          })
          return false
        }
      } catch (error) {
        throw error
        this.$message({
          message: this.$t('common.pro_fail'),
          type: 'error',
          center: true,
          duration: 2000
        })
        return false
      }

    },
    toDataId() {
      this.dynamicValidateForm.data.dataId = uuid()
    },
    async getDetail(id) {
      let params = {
        id
      }
      try {
        let res = await this.$store.dispatch('getDataByDataID', params)
        if (res.status === 200 && res.data.desc === 'SUCCESS') {
          this.detailList = res.data.result
          console.log('this.detailList', this.detailList)
          this.dynamicValidateForm.certifier = this.detailList.certifier
          this.dynamicValidateForm.judger = this.detailList.judger
          this.dynamicValidateForm.tokenId = sessionStorage.getItem('resale_tokenId')
          this.dynamicValidateForm.price = +this.detailList.price * Math.pow(10, -9) || ''
          this.dynamicValidateForm.coin = this.detailList.coin || 'ONG'
          this.dynamicValidateForm.data.name = this.detailList.data.name
          this.dynamicValidateForm.data.desc = this.detailList.data.desc
          this.dynamicValidateForm.data.img = this.detailList.data.img

          this.detailList.data.keywords.map(x => {
            let a = {}
            a.value = x
            this.dynamicValidateForm.tags.push(a)
          })
          console.log(this.dynamicValidateForm)
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
    this.accountid = await client.api.asset.getAccount()
    this.ont_id = await client.api.identity.getIdentity()
    try {
      let res = await this.$store.dispatch('getCertifier')
      if (res.data && res.data.desc === 'SUCCESS') {
        this.certifierArr = res.data.result
        this.dynamicValidateForm.certifier = this.certifierArr[0].ontid
      } else {
        this.certifierArr = []
        this.dynamicValidateForm.certifier = ''
      }
    } catch (error) {
      this.certifierArr = []
      this.dynamicValidateForm.certifier = ''
    }

    try {
      let res = await this.$store.dispatch('getJudger')
      if (res.data && res.data.desc === 'SUCCESS') {
        this.judgerArr = res.data.result
      } else {
        this.judgerArr = []
      }
    } catch (error) {
      this.judgerArr = []
      this.dynamicValidateForm.judger = ''
    }

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
        coin: '',
        price: null,
        ontid: '',
        judger: [],
        challengePeriod: []
      }
      cpas.coin = this.dynamicValidateForm.coin || 'ONG'
      cpas.price = this.dynamicValidateForm.price
      cpas.ontid = this.ont_id
      cpas.certifier = this.dynamicValidateForm.certifier
      cpas.judger = this.dynamicValidateForm.judgerType
      cpas.tokenNum = this.dynamicValidateForm.tokenNum
      return cpas
    }
  },
}
</script>

<style lang='less' scoped>
.addBox {
  margin: 0 auto;
  width: 80%;
  .formBox {
    .el-input {
      width: 80%;
      float: left;
    }
  }
}
</style>
