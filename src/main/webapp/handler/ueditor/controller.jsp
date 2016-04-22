<%@ page language="java" contentType="text/html; charset=UTF-8"
         import="com.baidu.ueditor.ActionEnter"
         pageEncoding="UTF-8" %>
<%@ page import="com.city.common.util.ue.FormatUEResultUtil" %>
<%
    request.setCharacterEncoding("utf-8");
    response.setHeader("Content-Type", "text/html");

    String rootPath = application.getRealPath("/");

    String result = new ActionEnter(request, rootPath).exec();
    String action = request.getParameter("action");
    if (action.startsWith("list")) {//列出文件或图片
        String preAdding = request.getContextPath();
        String fromSub = "/upload/ue";
        out.write(FormatUEResultUtil.formatObj(result, fromSub, preAdding));
    } else {
        out.write(result);
    }

%>
