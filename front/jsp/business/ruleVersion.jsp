<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
	<head>
		<jsp:include page="../../inc/header.jsp"/>
	</head>

	<body class="no-skin" oncontextmenu="if(self.event)self.event.returnValue=false;return false;">
		<div class="main-container">
			<table id='myJqGrid' style="width:100%"></table> 
			<div id='myJqGridPager'></div>
		</div><!-- /.main-container -->	
		
		<div id="ruleVersionAddDialogContainer" class="hide2"></div>
		<div id="ruleVersionUpdateDialogContainer" class="hide2"></div>
		<div id="ruleVersionViewDialogContainer" class="hide2"></div>
		
		<jsp:include page="../../inc/script.jsp"/>
		<script src="ruleVersion.js"></script>
	</body>
</html>