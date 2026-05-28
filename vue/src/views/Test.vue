<template>
  <div>

    主要
    <el-transfer
        :titles="['可选设备', '已选设备']"
        :data="leftList1"
        v-model="rightList1"
    >
    </el-transfer>

    <el-button type="primary" @click="selectedDevice" class="ml-5">点击显示已选设备</el-button>
    测试
    <!-- 添加或修改学生信息对话框 -->
    <el-drawer  size="50%">
      <el-form ref="form" label-width="100px" style="margin: 0 32px;">
        <el-form-item label="选择课程" prop="name">
          <el-transfer
              :titles="['可选课程', '已选课程']"
              :data="leftList"
              v-model="rightList"
          >
          </el-transfer>
        </el-form-item>
      </el-form>
    </el-drawer>



  </div>


</template>

<script>

export default {
  name:"Test",
  data() {
    return {
      // 可选课程列表（左边）
      leftList: [],
      // 已选课程列表（右边，只需要key）
      rightList: [],

      leftList1: [],
      // 已选课程列表（右边，只需要key）
      rightList1: [],

    };
  },
  created() {
    this.getDeviceList();
    console.log(this.rightList1)
  },
  methods: {
    getDeviceList() {
      // 调用查询设备列表接口
      this.request.get("/deviceLibrary").then( res  => {
        for(let i in [0,1,2,3]){
          // 将返回的设备名称列表赋值于穿梭框左边列表
          this.leftList1.push({
            key: res[i].id,
            label: res[i].name
          })
        }
        console.log(this.leftList1)
        this.loading = false;
      });
    },
    selectedDevice(){
      console.log(this.rightList1[0])
      console.log(this.leftList1[this.rightList1[0]].label)
      // console.log(this.leftList1[2].label)
      // console.log(this.leftList1[3].label)
    },
    // 两个数据ID转换的方法
    changeRefListToCourseIdList(data,fun) {
      let idList = [];
      for(let i in data){
        idList.push(data[i].id);
      }
      fun(idList);
    },
    changeCourseIdListToRefList(data,fun) {
      let refList = [];
      for(let i in data){
        let studentCourseRef = {
          courseId: data[i]
        }
        refList.push(studentCourseRef);
      }
      fun(refList);
    },
    /** 选课操作 */
    handleUpdate(row) {
      this.reset();
      let that = this;
      const id = row.id || this.ids
      getStudent(id).then(response => {
        this.form = response.data;
        // 把返回的关系表数据转换成右边已选课程列表，用于数据回显
        this.$options.methods.changeRefListToCourseIdList(response.data.studentCourseRefList, function(data){
          that.rightList = data;
        });
        this.open = true;
        this.title = "选课操作";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          let that = this;
          // 将已选课程列表的数据添加到关系表中，提交
          this.$options.methods.changeCourseIdListToRefList(this.rightList, function(data){
            that.form.studentCourseRefList = data;
            if (that.form.id != null) {
              updateStudent(that.form).then(response => {
                that.msgSuccess("课程选择成功");
                that.open = false;
                that.getList();
              });
            }
          });
        }
      });
    },
  }
}

</script>

<style scoped>

</style>