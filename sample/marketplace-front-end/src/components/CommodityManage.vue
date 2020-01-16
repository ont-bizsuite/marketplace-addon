<template>
  <div class="commoditymanage_box">
    <div class="msg">
      <div style="overflow: hidden; margin-bottom: 50px;">
        <el-button
          @click="$router.go(-1)"
          type="primary"
          plain
          style="float: left"
          >{{ $t('common.back_to_prev') }}</el-button
        >
        <el-button
          @click="toIndex()"
          type="primary"
          plain
          style="float: right"
          >{{ $t('common.to_home') }}</el-button
        >
      </div>
      <!-- <div class="msg_item">
        <p>{{$t('common.kyc')}}</p>
        <p>KYC: https://ont.io/</p>
        <p>
          {{$t('common.not_certified')}}
          <el-link type="danger" @click="certificatKYC()">{{$t('common.to_cer')}}</el-link>
        </p>
      </div> -->
      <el-upload
        ref="upload"
        class="upload-demo"
        action
        :on-preview="handlePreview"
        :on-remove="handleRemove"
        :before-upload="beforeAvatarUpload"
        :http-request="fileupload"
        :on-exceed="handleExceed"
        :limit="limit"
        :file-list="fileList"
      >
        <el-button size="small" type="primary">{{
          $t('common.add_data')
        }}</el-button>
        <div slot="tip" class="el-upload__tip">Only Txt file</div>
      </el-upload>
    </div>
    <div class="table_box" style="margin-top: 60px;">
      <el-table
        :data="tableData"
        border
        style="width: 100%"
        :empty-text="$t('common.no_data')"
      >
        <el-table-column
          type="index"
          align="center"
          width="50"
          :index="indexMethod"
        ></el-table-column>
        <el-table-column
          prop="name"
          align="center"
          :label="tableLang.name"
          width="180"
        ></el-table-column>
        <el-table-column align="center" :label="tableLang.tags">
          <template slot-scope="scope">
            <el-tag
              style="margin-right: 10px;"
              v-for="(item, idx) in scope.row.tags"
              :key="idx"
              >{{ item }}</el-tag
            >
          </template>
        </el-table-column>
        <el-table-column align="center" label="Data ID" width="380">
          <template slot-scope="scope">
            <!-- <el-button
              v-if="scope.row.dataId"
              @click="toDataIDList(scope.row.dataId)"
              size="mini"
              type="primary"
            >{{scope.row.dataId}}</el-button>-->
            <el-button v-if="scope.row.dataId" size="mini" type="primary">{{
              scope.row.dataId
            }}</el-button>
          </template>
        </el-table-column>
        <el-table-column
          prop="createTime"
          align="center"
          :label="tableLang.date"
          width="260"
        ></el-table-column>
        <el-table-column
          :label="tableLang.operating"
          align="center"
          width="400"
        >
          <template slot-scope="scope">
            <el-button
              v-if="scope.row.state === 3"
              @click="handleClick(scope.row)"
              type="primary"
              round
              size="small"
              >{{ $t('common.detail') }}</el-button
            >
            <el-button
              v-else
              @click="prodOperat(scope.row)"
              type="warning"
              round
              size="small"
              >{{ $t('common.pending_order') }}</el-button
            >
            <!-- <el-button
              v-if="scope.row.state === '2'"
              round
              size="small"
              @click="withdrawal(scope.row)"
              type="danger"
            >{{$t('common.withdrawal')}}</el-button>-->
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!--  -->
    <el-dialog
      :title="$t('common.shipping_address')"
      :visible.sync="dialogFormVisible"
      center
      width="30%"
    >
      <el-form :model="form" :rules="rules" ref="ruleForm">
        <el-form-item label="symbol" prop="symbol">
          <el-input v-model="form.symbol" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="name" prop="name">
          <el-input v-model="form.name" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="totalAmount" prop="totalAmount">
          <el-input
            type="age"
            v-model.number="form.totalAmount"
            autocomplete="off"
          ></el-input>
        </el-form-item>
        <el-form-item label="transferCount" prop="transferCount">
          <el-input
            type="age"
            v-model.number="form.transferCount"
            autocomplete="off"
          ></el-input>
        </el-form-item>
        <el-form-item label="accessCount" prop="accessCount">
          <el-input
            type="age"
            v-model.number="form.accessCount"
            autocomplete="off"
          ></el-input>
        </el-form-item>
        <el-form-item label="expireTime" prop="expireTime">
          <!-- <el-input type="age" v-model.number="form.expireTime" autocomplete="off"></el-input> -->
          <el-date-picker
            v-model="form.expireTime"
            type="datetime"
            placeholder="选择日期时间"
          ></el-date-picker>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">{{
          $t('common.cancel')
        }}</el-button>
        <el-button type="primary" @click="proDataId('ruleForm')">{{
          $t('common.sure')
        }}</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { client } from 'ontology-dapi'

import { TEST_NET } from '../assets/control'
import { sha256 } from 'js-sha256'
import { throws } from 'assert'
import moment from 'moment'
import { encode } from 'punycode'

import { mapState } from 'vuex'
export default {
  data() {
    return {
      address: null,
      tableData: [],
      ontid: '',
      url: '',
      dialogFormVisible: false,
      form: {
        symbol: '',
        name: '',
        totalAmount: null,
        transferCount: null,
        accessCount: null,
        expireTime: null
      },
      rules: {
        name: [
          {
            required: true,
            message: this.$t('common.please_enter') + 'name',
            trigger: 'blur'
          },
          { min: 3, message: this.$t('common.character3'), trigger: 'blur' }
        ],
        symbol: [
          {
            required: true,
            message: this.$t('common.please_enter') + 'symbol',
            trigger: 'blur'
          },
          { min: 3, message: this.$t('common.character3'), trigger: 'blur' }
        ],
        totalAmount: [
          {
            required: true,
            message: this.$t('common.please_enter') + 'totalAmount'
          },
          { type: 'number', message: 'totalAmount' + this.$t('common.mbnum') }
        ],
        transferCount: [
          {
            required: true,
            message: this.$t('common.please_enter') + 'transferCount'
          },
          { type: 'number', message: 'transferCount' + this.$t('common.mbnum') }
        ],
        accessCount: [
          {
            required: true,
            message: this.$t('common.please_enter') + 'accessCount'
          },
          { type: 'number', message: 'accessCount' + this.$t('common.mbnum') }
        ],
        expireTime: [
          {
            required: true,
            message: this.$t('common.please_enter') + 'expireTime'
          }
          // { type: 'number', message: 'expireTime' + this.$t('common.mbnum') },
        ]
      },
      cortData: null,
      tableLang: {
        name: this.$t('common.commodity_name'),
        tags: this.$t('common.tags'),
        date: this.$t('common.date'),
        operating: this.$t('common.operating'),
        state: this.$t('common.certification_status')
      },
      dataIdTimer: null,
      DId: '',
      getClaimId: '',
      getClaimTimer: '',
      postClaimId: '',
      postClaimTimer: '',
      isClaim: '',
      uploadfileTimer: null,
      fileId: '',
      limit: 1,
      fileList: []
    }
  },
  methods: {
    certificatKYC() {
      let callback_url = window.location.origin + '/#/commoditymanage'
      let access_token =
        'eyJ0eXAiOiJKV1QiLCJhbGciOiJFUzI1NiJ9.eyJhdWQiOiJkaWQ6b250OkFQZUUxcGlKcmhzRVRncG5nSHNUM2htdXZyakFpRFF0NE4iLCJpc3MiOiJkaWQ6b250OkFkajdXNVoyaFRlS0g3WXdKc2ZNekx1d2lENjcxbXZKNlgiLCJleHAiOjE1NTc5OTM3MDEsImlhdCI6MTU1NzkwNzMwMSwianRpIjoiZmY2MWIyNDdmZTZjNGFlNjg3MDNlNjJlZDMxYTM0ODgiLCJjb250ZW50Ijp7InR5cGUiOiJhY2Nlc3NfdG9rZW4iLCJvbnRpZCI6ImRpZDpvbnQ6QWF0YmdWMThhOExRMkhhZWJ6cVEzZmtqUnFURzV0V0FHcSJ9fQ.MDEwYWY0OWI4ZmFhZDRhZjM1MDhmMjg2MGFlYTI3MjZhZDE2NGVhNDIyYjgzNTU0N2U1ODVlODA3ZTRlOWE2MGY1MDE0ZjVjNmZmNWJlODIxNTc5NmZlODVjY2UzMDcxNzI5ZTczNzU4MDA2MGIyN2NhZTlmMzlmMWVmYjFkNDA2NQ'
      let ontid = 'did:ont:AatbgV18a8LQ2HaebzqQ3fkjRqTG5tWAGq'
      console.log(ontid + '&' + access_token + '&' + callback_url + '&' + 'zh')
      let value = window.encodeURIComponent(
        ontid + '&' + access_token + '&' + callback_url + '&' + 'zh'
      )
      console.log(value)
      window.location.href = 'https://kyc.ont.io/#/mgmtHome?params=' + value
    },
    async walletAddress() {
      this.address = await client.api.asset.getAccount()
    },
    toIndex() {
      this.$router.push({ path: '/' })
    },
    toAddData() {
      // this.isClaim = sessionStorage.getItem('isclaim')
      // if (!this.isClaim) {
      //   this.$message({
      //     message: 'Please Get Claim',
      //     type: 'error',
      //     center: true,
      //     duration: 2000
      //   });
      //   return
      // } else {
      //   this.postClaim()
      // }
      // this.postClaim()
    },
    indexMethod(idx) {
      return idx + 1
    },
    async getSellData() {
      try {
        // let params = {
        //   ontid: this.ontid,
        //   pageNum: 0,
        //   pageSize: 10
        // }
        // console.log(params)
        let res = await this.$store.dispatch('getComList', this.ontid)
        console.log(res)
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
    handleClick(row) {
      this.$router.push({ path: 'orderdetail', query: { commodityId: row.id } })
    },
    toEditData(row) {
      if (row.state == 1) {
        this.$message({
          message: this.$t('common.remove'),
          type: 'success',
          center: true,
          duration: 2000
        })
        return
      }
    },
    async prodOperat(row) {
      this.$router.push({ path: 'editdata', query: { commodityId: row.id } })
      console.log(row)
    },
    async proDataId(formName) {
      const privateKey = Ont.Crypto.PrivateKey.random()
      var identity = Ont.Identity.create(privateKey, '', '')
      let params0 = {
        dataId: identity.ontid,
        id: this.cortData.id,
        ontid: this.ontid,
        pubKey: 1
      }
      console.log('params0', params0)
      let result = await this.$store.dispatch('getTID', params0)
      console.log('result', result)
      if (result.data.desc === 'SUCCESS') {
        // DId .data.result.id
        this.DId = result.data.result.id
        let message = result.data.result.message
        message = message.slice(0, message.length - 2)
        message = Ont.utils.sha256(message)
        message = Ont.utils.sha256(message)
        let codeParams = {
          action: 'signMessage',
          version: 'v1.0.0',
          id: result.data.result.id,
          params: {
            type: 'ontid',
            message: message,
            ishex: true,
            callback: result.data.result.callback
          }
        }
        console.log('codeParams', codeParams)
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
              message: this.$t('common.data_id_suc'),
              center: true,
              type: 'success'
            })
          } else if (result === 3) {
            clearInterval(this.dataIdTimer)
            this.$message({
              message: this.$t('common.data_id_fail'),
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
          message: this.$t('common.data_id_fail'),
          type: 'error',
          center: true,
          duration: 2000
        })
        this.dialogFormVisible = false
        this.$refs[formName].resetFields()
      }
    },
    setUrl() {
      const net = localStorage.getItem('net')
      let url = ''
      if (net === 'TEST_NET') {
        url = TEST_NET + ':20334'
      } else {
        url = MAIN_NET + ':20334'
      }
      return url
    },
    dialog(data) {
      console.log('data', data)
      // if (data.isCertificated == 0) {
      //   this.$message({
      //     message: this.$t('common.b_dataid'),
      //     type: 'error',
      //     center: true,
      //     duration: 2000
      //   });
      //   return
      // }
      this.cortData = data
      this.proDataId()
    },
    async withdrawal(data) {
      console.log('withdrawaldata', data)
      let withdrawalParams = {
        argsList: [
          {
            name: 'authId',
            value: 'ByteArray:' + data.authId
          }
        ],
        contractHash: '57a078f603a6894ea4c3688251b981e543fe1cb1',
        method: 'cancelAuth'
      }
      let paramsData = {
        txHex: '',
        pubKeys: '',
        sigData: ''
      }
      try {
        let res = await this.$store.dispatch(
          'makeTransaction',
          withdrawalParams
        )
        console.log('makeTransaction', res)
        // return
        if (res.data.desc === 'SUCCESS') {
          paramsData.txHex = res.data.result
          console.log('paramsData', paramsData)
        } else {
          this.$message({
            message: 'error',
            type: 'error',
            center: true,
            duration: 2000
          })
          return
        }
      } catch (error) {
        console.log(error)
        this.$message({
          message: error,
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
          message: this.$t('common.delivery_fail'),
          type: 'error',
          center: true,
          duration: 2000
        })
        return
      }
      console.log('paramsData', paramsData)
      try {
        let res = await this.$store.dispatch('sendPass', paramsData)
        console.log('sendPass', res)
        if (res.data.desc === 'SUCCESS') {
          this.$message({
            message: 'SUCCESS ' + this.$t('common.refresh'),
            type: 'success',
            center: true,
            duration: 2000
          })
        } else {
          this.$message({
            message: 'error',
            type: 'error',
            center: true,
            duration: 2000
          })
          return
        }
      } catch (error) {
        this.$message({
          message: error,
          type: 'error',
          center: true,
          duration: 2000
        })
        return
      }
    },
    toDataIDList(dataId) {
      this.$router.push({ name: 'DataIDList', query: { dataId: dataId } })
    },
    async getClaim() {
      try {
        let result = await this.$store.dispatch('getClaim')
        console.log('result', result)
        this.getClaimId = result.data.result.id
        let params = {
          action: 'getClaim',
          id: result.data.result.id,
          version: 'v1.0.0',
          params: {
            dappName: 'dapp Name',
            dappIcon: 'dapp Icon',
            message: result.data.result.message,
            expire: result.data.result.expire,
            callback: result.data.result.callback
          }
        }
        let qrparams = {
          params,
          isShow: true
        }
        this.$store.dispatch('changeQrcode', qrparams)
        window.clearInterval(this.getClaimTimer)
        this.getClaimTimer = setInterval(async () => {
          let result = await this.$store.dispatch(
            'getCheckRes',
            this.getClaimId
          )
          console.log('result orjs', result)
          if (result === 1) {
            window.clearInterval(this.getClaimTimer)
            sessionStorage.setItem('isclaim', true)
            this.$message({
              message: 'SUCCESS',
              center: true,
              type: 'success'
            })
          } else if (result === 3) {
            window.clearInterval(this.getClaimTimer)
            this.$message({
              message: 'Fail',
              type: 'error',
              center: true,
              duration: 2000
            })
          } else if (result === 4) {
            window.clearInterval(this.getClaimTimer)
          } else {
          }
        }, 3000)
      } catch (error) {
        this.$message({
          message: error,
          type: 'error',
          center: true,
          duration: 2000
        })
      }
    },
    async postClaim() {
      try {
        let result = await this.$store.dispatch('postClaim')
        console.log('result', result)
        this.postClaimId = result.data.result.id
        let params = {
          action: 'postClaim',
          version: 'v1.0.0',
          params: {
            dappName: 'dapp Name',
            dappIcon: 'dapp Icon',
            claimTemplate: 'claims:yus_chinese_id_authentication',
            // message: result.data.result.message,
            expire: result.data.result.expire,
            callback: result.data.result.callback
          }
        }
        let qrparams = {
          params,
          isShow: true
        }
        this.$store.dispatch('changeQrcode', qrparams)
        window.clearInterval(this.postClaimTimer)
        this.postClaimTimer = setInterval(async () => {
          let result = await this.$store.dispatch(
            'getCheckRes',
            this.postClaimId
          )
          console.log('result orjs', result)
          if (result === 1) {
            window.clearInterval(this.postClaimTimer)
            this.$router.push({ path: 'addnewdata' })
            this.$message({
              message: 'SUCCESS',
              center: true,
              type: 'success'
            })
          } else if (result === 3) {
            window.clearInterval(this.postClaimTimer)
            this.$message({
              message: 'Fail',
              type: 'error',
              center: true,
              duration: 2000
            })
          } else if (result === 4) {
            window.clearInterval(this.postClaimTimer)
          } else {
          }
        }, 3000)
      } catch (error) {
        this.$message({
          message: error,
          type: 'error',
          center: true,
          duration: 2000
        })
      }
    },
    handlePreview(file) {
      console.log(file)
    },
    handleRemove(file, fileList) {
      console.log(file, fileList)
    },
    async fileupload(param) {
      let fileObject = param.file
      let formData = new FormData()
      formData.append('file', fileObject)
      const loading = this.$loading({
        lock: true,
        text: 'Loading',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.2)'
      })
      try {
        let result = await this.$store.dispatch('uploadFile', {
          file: formData,
          ontid: this.ontid
        })
        console.log(result)
        if (result.desc === 'SUCCESS') {
          loading.close()
          this.fileId = result.result.id
          let codeParams = result.result.qrCode
          console.log('codeParams', codeParams)
          let qrparams = {
            params: codeParams,
            isShow: true
          }
          this.$store.dispatch('changeQrcode', qrparams)
          clearInterval(this.uploadfileTimer)
          this.uploadfileTimer = setInterval(async () => {
            let result = await this.$store.dispatch('getCheckRes', this.fileId)
            console.log('result orjs', result)
            if (result === 1) {
              clearInterval(this.uploadfileTimer)
              this.$message({
                message: this.$t('common.data_id_suc'),
                center: true,
                type: 'success',
                offset: 400,
                duration: 2000
              })
              this.$refs.upload.clearFiles()
              // window.location.reload()
            } else if (result === 0) {
              clearInterval(this.uploadfileTimer)
              this.$message({
                message: this.$t('common.data_id_fail'),
                type: 'error',
                center: true,
                duration: 2000
              })
            } else if (result === 4) {
              clearInterval(this.uploadfileTimer)
            } else {
            }
          }, 3000)
        } else {
          loading.close()
          this.$refs.upload.clearFiles()
          return this.$message({
            message: 'Get Message Fail!',
            center: true,
            type: 'error'
          })
        }
      } catch (error) {
        this.$refs.upload.clearFiles()
        return loading.close()
      }
    },
    beforeAvatarUpload(file) {
      const isTXT = file.type === 'text/plain'
      if (!isTXT) {
        this.$message.error('Only upload txt format files!')
      }
      return isTXT
    },
    handleExceed(files, fileList) {
      this.$message.warning(`Please clear the current file list first.!`)
    },
    async getfileResult() {
      if (this.isShow) {
        try {
          let res = await this.$store.dispatch('getLoginRes', this.fileId)
          console.log('getfileResult', res)
          // if (res.data.desc === 'SUCCESS') {
          //   if (res.data.result.result === '1') {
          //     this.$message({
          //       message: 'Sign In Successful',
          //       center: true,
          //       type: 'success'
          //     });
          //     this.$store.commit('CHANGE_MODEL_STATE', false)
          //     clearInterval(this.uploadfileTimer)
          //     sessionStorage.setItem("ons", res.data.result.userName)
          //     sessionStorage.setItem("user_ontid", res.data.result.ontid)
          //     this.userAccount = res.data.result.userName
          //     this.$router.push({ path: '/' });
          //     return true
          //   } else if (res.data.result.result === '0') {
          //     this.$message({
          //       message: 'Get Sign In Result Fail!',
          //       center: true,
          //       type: 'error'
          //     });
          //     clearInterval(this.uploadfileTimer)
          //     return false
          //   } else if (res.data.result.result === '2') {
          //     this.$message({
          //       message: 'Please Sign Up ONS',
          //       center: true,
          //       type: 'error'
          //     });
          //     clearInterval(this.uploadfileTimer)
          //     return false
          //   } else { }
          // } else {
          //   this.$message({
          //     message: 'Get Sign In Result Fail!',
          //     center: true,
          //     type: 'error'
          //   });
          //   clearInterval(this.uploadfileTimer)
          //   return false
          // }
        } catch (error) {
          clearInterval(this.uploadfileTimer)
          return false
        }
      } else {
        clearInterval(this.uploadfileTimer)
        return
      }
    }
  },
  async mounted() {
    this.url = this.setUrl()
    this.ontid = sessionStorage.getItem('user_ontid')
    this.isClaim = sessionStorage.getItem('isclaim')
    this.getSellData()
  },
  computed: {
    ...mapState({
      isShow: state => state.qrcodeParams.isShow
    })
  },
  beforeDestroy() {
    window.clearInterval(this.dataIdTimer)
    window.clearInterval(this.getClaimTimer)
    window.clearInterval(this.postClaimTimer)
  }
}
</script>

<style lang="less" scoped>
.commoditymanage_box {
  margin: 20px auto;
  text-align: left;
  overflow: hidden;
  .msg {
    overflow: hidden;
    width: 50%;
    margin: 0 auto;
    .msg_item {
      margin: 30px 0;
      p {
        line-height: 30px;
      }
    }
  }
}
</style>
