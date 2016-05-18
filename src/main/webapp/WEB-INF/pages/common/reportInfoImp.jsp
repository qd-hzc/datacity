<%--
  Created by IntelliJ IDEA.
  User: wgx
  Date: 2016/2/16
  Time: 13:47
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="com.city.common.pojo.Constant" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<script>
    //报表报送状态
    var RPT_STATUS = {
        ALL:<%= Constant.RPT_STATUS.ALL %>,//待填报
        WAITING_FILL:<%= Constant.RPT_STATUS.WAITING_FILL %>,//待填报
        WAITING_FILL_CH: '<%= Constant.RPT_STATUS.WAITING_FILL_CH %>',//待填报
        DRAFT:<%= Constant.RPT_STATUS.DRAFT %>,//草稿
        DRAFT_CH: '<%= Constant.RPT_STATUS.DRAFT_CH %>',//草稿
        WAITING_PASS:<%= Constant.RPT_STATUS.WAITING_PASS %>,//待审
        WAITING_PASS_CH: '<%= Constant.RPT_STATUS.WAITING_PASS_CH %>',//待审
        PASS:<%= Constant.RPT_STATUS.PASS %>,//已审
        PASS_CH: '<%= Constant.RPT_STATUS.PASS_CH %>',//已审
        REGECT:<%= Constant.RPT_STATUS.REJECT %>,//已驳回
        REGECT_CH: '<%= Constant.RPT_STATUS.REJECT_CH %>',//已驳回
        REVIEW_ALL:<%= Constant.RPT_STATUS.REVIEW_ALL %>,//全部（报表审核）
        getString: function (status) {
            var str = "";
            switch (status) {
                case this.WAITING_FILL:
                    str = this.WAITING_FILL_CH;
                    break;
                case this.DRAFT:
                    str = this.DRAFT_CH;
                    break;
                case this.WAITING_PASS:
                    str = this.WAITING_PASS_CH;
                    break;
                case this.PASS:
                    str = this.PASS_CH;
                    break;
                case this.REGECT:
                    str = this.REGECT_CH;
                    break;
            }
            return str;
        }
    }
    //采集类型
    var COLLECTION_TYPE = {
        CALCULATE:<%= Constant.COLLECTION_TYPE.CALCULATE %>,//计算
        FILL:<%= Constant.COLLECTION_TYPE.FILL %>,//填报
        EXTRACT:<%= Constant.COLLECTION_TYPE.EXTRACT %>,//抽取
        LEADIN:<%= Constant.COLLECTION_TYPE.LEADIN  %>,//导入
    }
</script>
