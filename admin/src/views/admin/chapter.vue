<template>
    <div>
        <h4 class="lighter">
            <i class="ace-icon fa fa-hand-o-right icon-animated-hand-pointer blue"></i>
            <router-link to="/business/course" class="pink"> {{course.name}} </router-link>
        </h4>
        <hr>
        <router-link to="/business/course" class="btn btn-white btn-default btn-round">
            <i class="ace-icon fa fa-arrow-left"></i>
            返回课程
        </router-link>
        <button v-on:click="add" class="btn btn-white btn-default btn-round">
            <i class="ace-icon fa fa-edit"></i>
            新增
        </button>
        <button v-on:click="list(1)" class="btn btn-white btn-default btn-round">
            <i class="ace-icon fa fa-refresh"></i>
            刷新
        </button>
        <table id="simple-table" class="table  table-bordered table-hover">
            <thead>
                <tr>
                    <th class="detail-col">ID</th>
                    <th>名称</th>
                    <th>课程ID</th>
                    <th class="hidden-480">操作</th>
                </tr>
            </thead>

            <tbody>
                <tr v-for="chapter in chapters">
                    <td>{{ chapter.id }}</td>
                    <td class="hidden-480">{{ chapter.name }}</td>
                    <td>{{ chapter.courseId }}</td>
                    <td>
                        <div class="hidden-sm hidden-xs btn-group">
                            <button v-on:click="toSection(chapter)" class="btn btn-white btn-xs btn-info btn-round">
                                小节
                            </button>&nbsp;
                            <button v-on:click="edit(chapter)" class="btn btn-white btn-xs btn-info btn-round">
                                编辑
                            </button>&nbsp;
                            <button v-on:click="del(chapter.id)" class="btn btn-white btn-xs btn-warning btn-round">
                                删除
                            </button>
                        </div>
                    </td>
                </tr>
            </tbody>
        </table>
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
                                <label class="col-sm-2 control-label">名称</label>
                                <div class="col-sm-10">
                                    <input v-model="chapter.name" class="form-control" placeholder="名称">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-sm-2 control-label">课程ID</label>
                                <div class="col-sm-10">
                                    <input v-model="chapter.id" class="form-control" placeholder="课程ID">
                                </div>
                            </div>
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                        <button v-on:click="save" type="button" class="btn btn-primary">保存</button>
                    </div>
                </div><!-- /.modal-content -->
            </div><!-- /.modal-dialog -->
        </div><!-- /.modal -->
    </div>
</template>

<script>
  import Pagination from "../../components/pagination";

  export default {
    name: "chapter",
    components: {
      Pagination
    },
    data () {
      return {
        chapter: {},
        chapters: [],
        course: {}
      }
    },
    mounted: function() {
      let course = SessionStorage.get(SESSION_KEY_COURSE) || {};
      if (Tool.isEmpty(course)) {
        this.$router.push("/welcome");
      }
      this.course = course;
      this.list(1)
    },
    methods: {
      // 查询列表
      list (page) {
        this.$ajax.post(process.env.VUE_APP_SERVER + '/business/admin/chapter/list', {
          page: page,
          size: this.$refs.pagination.size,
          courseId: this.course.id
        }).then((response) => {
          this.chapters = response.data.content.list
          this.$refs.pagination.render(page, response.data.content.total);
        })
      },

      // 增加
      add () {
        this.chapter = {}
        $("#form-modal").modal("show")
      },

      // 编辑
      edit (chapter) {
        this.chapter = { ...chapter }
        $("#form-modal").modal("show")
      },

      // 保存
      save () {
        // 保存校验
        if (
          !Validator.require(this.chapter.name, "名称")
          || !Validator.length(this.chapter.courseId, "课程ID", 1, 8)
        ) {
            return;
        }

        this.chapter.courseId = this.course.id

        Loading.show();
        this.$ajax.post(process.env.VUE_APP_SERVER + '/business/admin/chapter/save', this.chapter).then((response)=>{
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

      // 删除
      del (id) {
        const _this = this
        Confirm.show("删除大章后不可恢复，确认删除？", function () {
          Loading.show();
            this.$ajax.delete(process.env.VUE_APP_SERVER + '/business/admin/chapter/delete/' + id).then((response)=>{
            Loading.hide();
            let resp = response.data;
            if (resp.success) {
                _this.list(_this.$refs.pagination.page);
              Toast.success("删除成功！");
            }
          })
        })
      },

      /**
       * 点击【小节】
       */
      toSection(chapter) {
        SessionStorage.set(SESSION_KEY_CHAPTER, chapter);
        this.$router.push("/business/section");
      }
    }
  }
</script>