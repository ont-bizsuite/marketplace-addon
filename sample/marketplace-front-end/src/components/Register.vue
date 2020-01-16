<template>
  <div class="registered">
    <div class="logo"></div>
    <i class="el-icon-s-home tohome" @click="toHome()"></i>
    <div class="form">
      <el-form
        :model="ruleForm"
        :rules="rules"
        ref="ruleForm"
        label-width="100px"
        class="demo-ruleForm"
      >
        <el-form-item label="Account" prop="domain">
          <el-input v-model="ruleForm.domain"></el-input>
        </el-form-item>
        <el-button type="primary" @click="submitForm('ruleForm')">{{
          $t('sign.sign_up')
        }}</el-button>
      </el-form>
    </div>
  </div>
</template>

<script>
import { client } from 'ontology-dapi'
import { mapState } from 'vuex'
export default {
  data() {
    return {
      ruleForm: {
        domain: '',
        checkTimer: null,
        dataId: ''
      },
      rules: {
        ontid: [
          {
            required: true,
            message: this.$t('common.please_enter') + ' ONT ID',
            trigger: 'blur'
          }
        ],
        ons: [
          {
            required: true,
            message: this.$t('common.please_enter') + ' Enter ONS',
            trigger: 'blur'
          }
        ]
      }
    }
  },
  methods: {
    submitForm(formName) {
      this.$refs[formName].validate(async valid => {
        if (valid) {
          try {
            let res = await this.$store.dispatch('sendONS', {
              userName: this.ruleForm.domain
            })
            console.log('res', res)
            if (res.data.desc === 'SUCCESS') {
              let qrcodeParams = res.data.result.qrCode
              this.dataId = res.data.result.id
              let dataParams = {
                params: qrcodeParams,
                isShow: true
              }
              this.$store.dispatch('changeQrcode', dataParams)
              clearInterval(this.checkTimer)
              this.checkTimer = setInterval(() => {
                this.checkResult()
              }, 3000)
            } else {
              this.$message({
                message: 'Sign Up Fail!',
                center: true,
                type: 'error'
              })
              return
            }
          } catch (error) {
            return false
          }
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
    toHome() {
      this.$router.push({ path: '/' })
    },
    async checkResult() {
      if (this.isShow) {
        try {
          let res = await this.$store.dispatch('checkSignUp', this.dataId)
          if (res.data.desc === 'SUCCESS') {
            if (res.data.result.result === '1') {
              this.$message({
                message: 'Sign Up Successfuly!',
                center: true,
                type: 'success'
              })
              clearInterval(this.checkTimer)
              this.$store.commit('CHANGE_MODEL_STATE', false)
              // this.$router.push({ path: '/login' })
              return true
            } else if (res.data.result.result === '0') {
              clearInterval(this.checkTimer)
              this.$message({
                message: 'Sign Up Fail!',
                center: true,
                type: 'error'
              })
              return false
            } else if (res.data.result.result === '2') {
              clearInterval(this.checkTimer)
              this.$message({
                message: 'ONT ID Already registered!',
                center: true,
                type: 'error'
              })
              return false
            } else {
            }
          } else {
            clearInterval(this.checkTimer)
            this.$message({
              message: 'Sign Up Fail!',
              center: true,
              type: 'error'
            })
            return false
          }
        } catch (error) {
          clearInterval(this.checkTimer)
          return false
        }
      } else {
        clearInterval(this.checkTimer)
      }
    }
  },
  computed: {
    ...mapState({
      isShow: state => state.qrcodeParams.isShow
    })
  },
  beforeDestroy() {
    clearInterval(this.checkTimer)
  }
}
</script>

<style lang="less" scoped>
.registered {
  width: 100%;
  max-width: 500px;
  margin: 0 auto;
  margin-top: 280px;
  padding: 50px 50px;
  box-sizing: border-box;
  border-radius: 10px;
  box-shadow: 0px 0 10px #ccc;
  position: relative;
  .logo {
    width: 150px;
    height: 58px;
    background: url(../assets/images/logo.png) no-repeat;
    background-size: contain;
    margin: 0 auto;
    padding-bottom: 80px;
  }
  i.tohome {
    font-size: 36px;
    position: absolute;
    left: 30px;
    top: 20px;
    cursor: pointer;
  }
}
</style>
