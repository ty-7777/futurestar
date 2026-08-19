<script setup>
import { ref } from 'vue'
import { showToast } from 'vant'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { updateProfile } from '@/api/profile'

const userStore = useUserStore()
const router = useRouter()

const POSITIONS = [
  { label: '前锋', value: 'FORWARD' },
  { label: '中场', value: 'MIDFIELDER' },
  { label: '后卫', value: 'DEFENDER' },
  { label: '门将', value: 'GOALKEEPER' }
]
const FOOTS = [
  { label: '左脚', value: 'LEFT' },
  { label: '右脚', value: 'RIGHT' },
  { label: '双脚', value: 'BOTH' }
]

const u = userStore.user || {}
const form = ref({
  realName: u.realName || '',
  gender: u.gender || '',
  birthDate: u.birthDate || '',
  height: u.height ?? '',
  weight: u.weight ?? '',
  position: u.position || '',
  preferredFoot: u.preferredFoot || '',
  experienceYears: u.experienceYears ?? '',
  avatar: u.avatar || '',
  emergencyContact: u.emergencyContact || ''
})

// ---- 出生日期选择器 ----
const showBirth = ref(false)
const minDate = new Date(1950, 0, 1)
const maxDate = new Date()

function parseBirth(str) {
  const m = /^(\d{4})-(\d{2})-(\d{2})$/.exec(str || '')
  return m ? [+m[1], +m[2], +m[3]] : [2000, 1, 1]
}
const birthDateValue = ref(parseBirth(form.value.birthDate))

const onBirthConfirm = ({ selectedValues }) => {
  const [y, m, d] = selectedValues
  form.value.birthDate = `${y}-${String(m).padStart(2, '0')}-${String(d).padStart(2, '0')}`
  showBirth.value = false
}

// ---- 保存 ----
const saving = ref(false)
const NUMERIC_FIELDS = ['height', 'weight', 'experienceYears']

const onSubmit = async () => {
  if (form.value.realName && form.value.realName.length > 50) return showToast('姓名过长')
  if (form.value.emergencyContact && !/^1[3-9]\d{9}$/.test(form.value.emergencyContact)) {
    return showToast('紧急联系电话格式不正确')
  }
  // 部分更新：只提交有值的字段
  const payload = {}
  for (const [k, v] of Object.entries(form.value)) {
    if (v === '' || v === null || v === undefined) continue
    payload[k] = NUMERIC_FIELDS.includes(k) ? Number(v) : v
  }
  saving.value = true
  try {
    await updateProfile(payload)
    userStore.setUser({ ...userStore.user, ...payload })
    showToast('保存成功')
    router.back()
  } catch {
    // 错误提示已由请求层统一处理
  } finally {
    saving.value = false
  }
}
</script>

<template>
  <div class="edit-profile">
    <van-nav-bar title="编辑资料" left-arrow @click-left="router.back()" />
    <van-form @submit="onSubmit">
      <van-cell-group inset>
        <van-field v-model="form.realName" label="姓名" placeholder="请输入真实姓名" maxlength="50" />
        <van-field name="gender" label="性别">
          <template #input>
            <van-radio-group v-model="form.gender" direction="horizontal">
              <van-radio name="男">男</van-radio>
              <van-radio name="女">女</van-radio>
            </van-radio-group>
          </template>
        </van-field>
        <van-field
          v-model="form.birthDate"
          label="出生日期"
          placeholder="请选择"
          readonly
          is-link
          @click="showBirth = true"
        />
        <van-field v-model="form.height" label="身高(cm)" type="number" placeholder="请输入身高" />
        <van-field v-model="form.weight" label="体重(kg)" type="number" placeholder="请输入体重" />
        <van-field name="position" label="场上位置">
          <template #input>
            <van-radio-group v-model="form.position" direction="horizontal">
              <van-radio v-for="p in POSITIONS" :key="p.value" :name="p.value">
                {{ p.label }}
              </van-radio>
            </van-radio-group>
          </template>
        </van-field>
        <van-field name="preferredFoot" label="惯用脚">
          <template #input>
            <van-radio-group v-model="form.preferredFoot" direction="horizontal">
              <van-radio v-for="f in FOOTS" :key="f.value" :name="f.value">
                {{ f.label }}
              </van-radio>
            </van-radio-group>
          </template>
        </van-field>
        <van-field v-model="form.experienceYears" label="球龄(年)" type="number" placeholder="请输入球龄" />
        <van-field v-model="form.emergencyContact" label="紧急联系人" type="tel" maxlength="11" placeholder="紧急联系电话" />
        <van-field v-model="form.avatar" label="头像URL" placeholder="头像图片地址" />
      </van-cell-group>
      <div class="edit-profile__btn">
        <van-button type="primary" block round native-type="submit" :loading="saving">
          保存
        </van-button>
      </div>
    </van-form>

    <van-popup v-model:show="showBirth" position="bottom" round>
      <van-date-picker
        v-model="birthDateValue"
        title="选择出生日期"
        :min-date="minDate"
        :max-date="maxDate"
        @confirm="onBirthConfirm"
        @cancel="showBirth = false"
      />
    </van-popup>
  </div>
</template>

<style scoped>
.edit-profile__btn {
  padding: 24px 16px;
}
</style>
