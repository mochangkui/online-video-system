<template>
  <div>
    <p>
      <button v-on:click="add()" class="btn btn-white btn-default btn-round">
        <i class="ace-icon fa fa-edit"></i>
        新增
      </button>
      &nbsp;
      <button v-on:click="list(1)" class="btn btn-white btn-default btn-round">
        <i class="ace-icon fa fa-refresh"></i>
        刷新
      </button>
    </p>

    <div class="row">
      <div v-for="course in courses" class="col-md-4">
        <div class="thumbnail search-thumbnail">
          <img v-show="!course.image" class="media-object" src="/static/image/demo-course.jpg" />
          <img v-show="course.image" class="media-object" v-bind:src="course.image" />
          <div class="caption">
            <div class="clearfix">
              <span class="pull-right label label-primary info-label">
                {{COURSE_LEVEL | optionKV(course.level)}}
              </span>
              <span class="pull-right label label-primary info-label">
                {{COURSE_CHARGE | optionKV(course.charge)}}
              </span>
              <span class="pull-right label label-primary info-label">
                {{COURSE_STATUS | optionKV(course.status)}}
              </span>
            </div>

            <h3 class="search-title">
              <a href="#" class="blue">{{course.name}}</a>
            </h3>

            <div v-for="teacher in teachers.filter(t=>{return t.id===course.teacherId})" class="profile-activity clearfix">
              <div>
                <img v-show="!teacher.image" class="pull-left" src="/ace/assets/images/avatars/avatar5.png">
                <img v-show="teacher.image" class="pull-left" v-bind:src="teacher.image">
                <a class="user" href="#"> {{teacher.name}} </a>
                <br>
                {{teacher.position}}
              </div>
            </div>

            <p>
              <span class="blue bolder bigger-150">{{course.price}}&nbsp;<i class="fa fa-rmb"></i></span>&nbsp;
            </p>
            <p>{{course.summary}}</p>
            <p>
              <span class="badge badge-info">{{course.id}}</span>
              <span class="badge badge-info">排序：{{course.sort}}</span>
              <span class="badge badge-info">{{course.time | formatSecond}}</span>
            </p>
            <p>
              <button v-on:click="toChapter(course)" class="btn btn-white btn-xs btn-info btn-round">
                大章
              </button>&nbsp;
              <button v-on:click="toContent(course)" class="btn btn-white btn-xs btn-info btn-round">
                内容
              </button>&nbsp;
              <button v-on:click="openSortModal(course)" class="btn btn-white btn-xs btn-info btn-round">
                排序
              </button>&nbsp;
              <button v-on:click="edit(course)" class="btn btn-white btn-xs btn-info btn-round">
                编辑
              </button>&nbsp;
              <button v-on:click="del(course.id)" class="btn btn-white btn-xs btn-warning btn-round">
                删除
              </button>
            </p>
          </div>
        </div>
      </div>
    </div>

    <pagination ref="pagination" v-bind:list="list" v-bind:itemCount="8"></pagination>

    <div id="form-modal" class="modal fade" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="document">
      <div class="modal-content">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
          <h4 class="modal-title">表单</h4>
        </div>
        <div class="modal-body">
          <form class="form-horizontal">
            <div class="form-group">
              <label class="col-sm-2 control-label">
                分类
              </label>
              <div class="col-sm-10">
                <ul id="tree" class="ztree"></ul>
              </div>
            </div>
            <div class="form-group">
              <label class="col-sm-2 control-label">封面</label>
              <div class="col-sm-10">
                  <big-file v-bind:input-id="'image-upload'"
                        v-bind:text="'上传封面'"
                        v-bind:suffixs="['jpg', 'jpeg', 'png']"
                        v-bind:use="FILE_USE.COURSE.key"
                        v-bind:url="'upload'"
                        v-bind:after-upload="afterUpload"></big-file>
                <div v-show="course.image" class="row">
                  <div class="col-md-6">
                    <img v-bind:src="course.image" class="img-responsive" />
                  </div>
                </div>
              </div>
            </div>
            <div class="form-group">
              <label class="col-sm-2 control-label">名称</label>
              <div class="col-sm-10">
                <input v-model="course.name" class="form-control">
              </div>
            </div>
            <div class="form-group">
              <label class="col-sm-2 control-label">讲师</label>
              <div class="col-sm-10">
                <select v-model="course.teacherId" class="form-control">
                  <option v-for="o in teachers" v-bind:value="o.id">{{o.name}}</option>
                </select>
              </div>
            </div>
            <div class="form-group">
              <label class="col-sm-2 control-label">概述</label>
              <div class="col-sm-10">
                <textarea v-model="course.summary" class="form-control"></textarea>
              </div>
            </div>
            <div class="form-group">
              <label class="col-sm-2 control-label">时长</label>
              <div class="col-sm-10">
                <input v-model="course.time" class="form-control">
              </div>
            </div>
            <div class="form-group">
              <label class="col-sm-2 control-label">价格（元）</label>
              <div class="col-sm-10">
                <input v-model="course.price" class="form-control">
              </div>
            </div>
            <div class="form-group">
              <label class="col-sm-2 control-label">级别</label>
              <div class="col-sm-10">
                <select v-model="course.level" class="form-control">
                  <option v-for="o in COURSE_LEVEL" v-bind:value="o.key">{{o.value}}</option>
                </select>
              </div>
            </div>
            <div class="form-group">
              <label class="col-sm-2 control-label">收费</label>
              <div class="col-sm-10">
                <select v-model="course.charge" class="form-control">
                  <option v-for="o in COURSE_CHARGE" v-bind:value="o.key">{{o.value}}</option>
                </select>
              </div>
            </div>
            <div class="form-group">
              <label class="col-sm-2 control-label">状态</label>
              <div class="col-sm-10">
                <select v-model="course.status" class="form-control">
                  <option v-for="o in COURSE_STATUS" v-bind:value="o.key">{{o.value}}</option>
                </select>
              </div>
            </div>
            <div class="form-group">
              <label class="col-sm-2 control-label">报名数</label>
              <div class="col-sm-10">
                <input v-model="course.enroll" class="form-control">
              </div>
            </div>
            <div class="form-group">
              <label class="col-sm-2 control-label">顺序</label>
              <div class="col-sm-10">
                <input v-model="course.sort" class="form-control" disabled>
              </div>
            </div>
          </form>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
          <button v-on:click="save()" type="button" class="btn btn-primary">保存</button>
        </div>
      </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
  </div><!-- /.modal -->

    <div id="course-sort-modal" class="modal fade" tabindex="-1" role="dialog">
      <div class="modal-dialog" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
            <h4 class="modal-title">排序</h4>
          </div>
          <div class="modal-body">
            <form class="form-horizontal">
              <div class="form-group">
                <label class="control-label col-lg-3">
                  当前排序
                </label>
                <div class="col-lg-9">
                  <input class="form-control" v-model="sort.oldSort" name="oldSort" disabled>
                </div>
              </div>
              <div class="form-group">
                <label class="control-label col-lg-3">
                  新排序
                </label>
                <div class="col-lg-9">
                  <input class="form-control" v-model="sort.newSort" name="newSort">
                </div>
              </div>
            </form>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-white btn-default btn-round" data-dismiss="modal">
              <i class="ace-icon fa fa-times"></i>
              取消
            </button>
            <button type="button" class="btn btn-white btn-info btn-round" v-on:click="updateSort()">
              <i class="ace-icon fa fa-plus blue"></i>
              更新排序
            </button>
          </div>
        </div><!-- /.modal-content -->
      </div><!-- /.modal-dialog -->
    </div><!-- /.modal -->
  </div>
</template>

<script>
  import Pagination from "../../components/pagination";
  import BigFile from "../../components/big-file";
  export default {
    components: {
        Pagination,
        BigFile
    },
    name: "business-course",
    data: function() {
      return {
        course: {},
        courses: [],
        COURSE_LEVEL: COURSE_LEVEL,
        COURSE_CHARGE: COURSE_CHARGE,
        COURSE_STATUS: COURSE_STATUS,
        FILE_USE: FILE_USE,
        categorys: [],
        tree: {},
        saveContentLabel: "",
        sort: {
          id: "",
          oldSort: 0,
          newSort: 0
        },
        teachers: [],
      }
    },
    mounted: function() {
      this.allCategory();
      this.allTeacher();
      this.list(1);
      // sidebar激活样式方法一
      // this.$parent.activeSidebar("business-course-sidebar");

    },
    methods: {
      /**
       * 点击【新增】
       */
      add() {
        this.course = {
          sort: this.$refs.pagination.total + 1
        };
        this.tree.checkAllNodes(false);
        $("#form-modal").modal("show");
      },

      /**
       * 点击【编辑】
       */
      edit(course) {
        this.course = $.extend({}, course);
        this.listCategory(course.id);
        $("#form-modal").modal("show");
      },

      /**
       * 列表查询
       */
      list(page) {
        Loading.show();
        this.$ajax.post(process.env.VUE_APP_SERVER + '/business/admin/course/list', {
          page: page,
          size: this.$refs.pagination.size,
        }).then((response) => {
          Loading.hide();
          let resp = response.data;
          this.courses = resp.content.list;
          this.$refs.pagination.render(page, resp.content.total);
        })
      },

      /**
       * 点击【保存】
       */
      save(page) {
        // 保存校验
        if (
          !Validator.require(this.course.name, "名称")
          || !Validator.length(this.course.name, "名称", 1, 50)
          || !Validator.length(this.course.summary, "概述", 1, 2000)
          || !Validator.length(this.course.image, "封面", 1, 100)
        ) {
          return;
        }

        let categorys = this.tree.getCheckedNodes();
        if (Tool.isEmpty(categorys)) {
          Toast.warning("请选择分类！");
          return;
        }
        this.course.categorys = categorys;

        Loading.show();
        this.$ajax.post(process.env.VUE_APP_SERVER + '/business/admin/course/save', this.course).then((response)=>{
          Loading.hide();
          let resp = response.data;
          if (resp.success) {
            $("#form-modal").modal("hide");
            this.list(1);
            Toast.success("保存成功！");
          } else {
            Toast.warning(resp.message)
          }
        })
      },

      /**
       * 点击【删除】
       */
      del(id) {
        const _this = this
        Confirm.show("删除课程后不可恢复，确认删除？", function () {
          Loading.show();
          _this.$ajax.delete(process.env.VUE_APP_SERVER + '/business/admin/course/delete/' + id).then((response)=>{
            Loading.hide();
            let resp = response.data;
            if (resp.success) {
              _this.list(1);
              Toast.success("删除成功！");
            }
          })
        });
      },

      /**
       * 点击【大章】
       */
      toChapter(course) {
        SessionStorage.set(SESSION_KEY_COURSE, course);
        this.$router.push("/business/chapter");
      },

      /**
       * 点击【内容】
       */
      toContent(course) {
        SessionStorage.set(SESSION_KEY_COURSE, course);
        this.$router.push("/business/content");
      },

      allCategory() {
        Loading.show();
        this.$ajax.post(process.env.VUE_APP_SERVER + '/business/admin/category/all').then((response)=>{
          Loading.hide();
          let resp = response.data;
          this.categorys = resp.content;
          this.initTree();
        })
      },

      initTree() {
        let setting = {
          check: {
            enable: true
          },
          data: {
            simpleData: {
              idKey: "id",
              pIdKey: "parent",
              rootPId: "00000000",
              enable: true
            }
          }
        };

        let zNodes = this.categorys;

        this.tree = $.fn.zTree.init($("#tree"), setting, zNodes);

        // 展开所有的节点
        // this.tree.expandAll(true);
      },

      /**
       * 查找课程下所有分类
       * @param courseId
       */
      listCategory(courseId) {
        Loading.show();
        this.$ajax.post(process.env.VUE_APP_SERVER + '/business/admin/course/list-category/' + courseId).then((res)=>{
          Loading.hide();
          console.log("查找课程下所有分类结果：", res);
          let response = res.data;
          let categorys = response.content;

          // 勾选查询到的分类
          this.tree.checkAllNodes(false);
          for (let i = 0; i < categorys.length; i++) {
            let node = this.tree.getNodeByParam("id", categorys[i].categoryId);
            this.tree.checkNode(node, true);
          }
        })
      },

      openSortModal(course) {
        this.sort = {
          id: course.id,
          oldSort: course.sort,
          newSort: course.sort
        };
        $("#course-sort-modal").modal("show");
      },

      /**
       * 排序
       */
      updateSort() {
        if (this.sort.newSort === this.sort.oldSort) {
          Toast.warning("排序没有变化");
          return;
        }
        Loading.show();
        this.$ajax.post(process.env.VUE_APP_SERVER + "/business/admin/course/sort", this.sort).then((res) => {
          let response = res.data;

          if (response.success) {
            Toast.success("更新排序成功");
            $("#course-sort-modal").modal("hide");
            this.list(1);
          } else {
            Toast.error("更新排序失败");
          }
        });
      },

      allTeacher() {
        Loading.show();
        this.$ajax.post(process.env.VUE_APP_SERVER + '/business/admin/teacher/all').then((response)=>{
          Loading.hide();
          let resp = response.data;
          this.teachers = resp.content;
        })
      },

      afterUpload(resp) {
          debugger
        let image = resp.content.path;
        this.course.image = image;
      },

    }
  }
</script>

<style scoped>
  .caption h3 {
    font-size: 20px;
  }

  @media (max-width: 1199px) {
    .caption h3 {
      font-size: 16px;
    }
  }
</style>