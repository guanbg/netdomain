<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
	String ctx = request.getContextPath();
%>
	<!--[if !IE]> -->
		<script src="<%=ctx%>/js/jquery-2.1.4.min.js"></script>
	<!-- <![endif]-->
	
	<!--[if IE]>
		<script src="<%=ctx%>/js/jquery-1.11.3.min.js"></script>
	<![endif]-->
	
	<!--[if !IE]> -->
	<script type="text/javascript">
		window.jQuery || document.write("<script src='<%=ctx%>/js/jquery-2.1.4.min.js'>"+"<"+"/script>");
	</script>
	<!-- <![endif]-->
	
	<!--[if IE]>
	<script type="text/javascript">
			window.jQuery || document.write("<script src='<%=ctx%>/js/jquery-1.11.3.min.js'>"+"<"+"/script>");
	</script>
	<![endif]-->
	
	<script type="text/javascript">
		if("ontouchend" in document) document.write("<script src='<%=ctx%>/js/jquery.mobile-1.4.5.min.js'>"+"<"+"/script>");
	</script>
	<script src="<%=ctx%>/js/prettify.js"></script>
	<script src="<%=ctx%>/js/json3.min.js"></script>
	<script src="<%=ctx%>/js/jquery-ui.min.js"></script>
	<script src="<%=ctx%>/js/bootstrap.min.js"></script>
	
	<% 
	String jqgrid = request.getParameter("jqgrid");
	if("4".equals(jqgrid)){//该版本为改造过的版本，修改过树视图的bug， 以后再合并到新版本上去%>
		<script src="<%=ctx%>/js/jquery.jqGrid4.min.js"></script>
		<script src="<%=ctx%>/js/i18n/grid4.locale-cn.js"></script><% 
	}
	else{//该版本为官方新版本%>
		<script src="<%=ctx%>/js/i18n/grid5.locale-cn.js"></script>
		<script src="<%=ctx%>/js/jquery.jqGrid5.min.js"></script><% 
	}%>	
	
	<script src="<%=ctx%>/js/bootstrap-datetimepicker.min.js"></script>
	<script src="<%=ctx%>/js/i18n/bootstrap-datetimepicker.zh-CN.js"></script>
	
	<script src="<%=ctx%>/js/jstree.min.js"></script>
	<script src="<%=ctx%>/js/jstree-actions-customer.js"></script>
	<script src="<%=ctx%>/js/jquery.multi-select.js"></script>
	<script src="<%=ctx%>/js/bootstrap-editable.min.js"></script>
	<script src="<%=ctx%>/js/bootstrap-modal-popover.js"></script>
	<script src="<%=ctx%>/js/fuelux.tree.min.js"></script>
	
	<script src="<%=ctx%>/js/bootstrap-dialog.min.js"></script>
	<script src="<%=ctx%>/js/serviceUtil.js"></script>
	
	<script src="<%=ctx%>/js/bootstrap-table.min.js"></script>
	<script src="<%=ctx%>/js/i18n/bootstrap-table-zh-CN.min.js"></script>