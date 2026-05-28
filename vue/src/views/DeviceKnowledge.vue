<template>

  <el-container style="min-height: 100vh">

    <el-aside :width="sideWidth + 'px' " style="background-color: rgb(238, 241, 246);box-shadow: 2px 0 6px rgb(0 21 41 / 35%);">
      <Aside :isCollapse="isCollapse" :logoTextShow="logoTextShow" />

    </el-aside>

    <el-container>

      <el-header style="border-bottom: 1px solid #ccc;">
        <Header :collapseBtnClass="collapseBtnClass" :collapse="collapse" />
      </el-header>

      <el-main>


        <div>
          <el-card shadow="never">
            <div style="margin-bottom: 10px;">
              <el-select v-model="deviceType" placeholder="请选择设备类型" style="width: 200px">
                <el-option v-for="item in deviceTypeOptions" :key="item" :label="item" :value="item" />
              </el-select>
              <el-button type="primary" @click="handleAdd">添加设备</el-button>
            </div>

            <el-table :data="filteredDevices" border style="width: 100%">
              <el-table-column prop="name" label="设备名称" />
              <el-table-column prop="type" label="类型" />
              <el-table-column prop="description" label="说明" />
              <el-table-column label="操作">
                <template slot-scope="scope">
                  <el-button type="text" size="small" @click="editDevice(scope.row)">编辑</el-button>
                  <el-button type="text" size="small" @click="deleteDevice(scope.row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </div>



      </el-main>
    </el-container>
  </el-container>

</template>


<script>
import Header from "@/components/Header.vue";
import Aside from "@/components/Aside.vue";
import VueOfficeDocx from "@vue-office/docx";

export default {
  name: "DeviceKnowledge",
  components: {VueOfficeDocx, Aside, Header},
  data(){
    return{
      collapseBtnClass:'el-icon-s-fold',
      isCollapse:false,
      sideWidth:200,
      logoTextShow:true,

      deviceType: '',
      deviceTypeOptions: ['传感器', '执行器', '控制器'],
      devices: [
        { name: '温度传感器', type: '传感器', description: '监测温度变化' },
        { name: '摄像头', type: '传感器', description: '图像采集设备' },
        { name: '家务机器人', type: '执行器', description: '执行家庭任务' }
      ]




    }
  },
  computed: {
    filteredDevices() {
      if (!this.deviceType) return this.devices;
      return this.devices.filter(d => d.type === this.deviceType);
    }
  },
  methods: {
    collapse() { //点击收缩按钮触发
      this.isCollapse = !this.isCollapse
      if (this.isCollapse) {  //收缩
        this.sideWidth = 64
        this.collapseBtnClass = 'el-icon-s-unfold'
        this.logoTextShow = false
      } else {   //展开
        this.sideWidth = 200
        this.collapseBtnClass = 'el-icon-s-fold'
        this.logoTextShow = true
      }
    },
  }
}
</script>



<style scoped>

</style>