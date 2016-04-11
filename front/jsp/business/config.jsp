<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
	<head>
		<jsp:include page="../../inc/header.jsp"/>
		<link rel="stylesheet" href="../../js/jqwidgets/styles/jqx.base.css" type="text/css" />
		<style type="text/css">
			.jqx-widget-header-fixed{
				position:fixed !important;
				top:0px !important;
				z-index:9999 !important;
			}
			.jqx-tree-grid-title{
				display:inline-block;
				word-break:break-all;
				word-wrap:break-word;
			}
			.jqx-tree-grid-indent{
				float:left;
				display:block;
			}
		</style>
	</head>

	<body class="no-skin">
		<div class="main-container" >
			<div id="myTreeGrid" style="border-left: 0px none; border-right: 0px none;"></div>
			<div id='myMenu' style="display:none;">
		        <ul>
		            <li data-xtype="add">新增下级</li>
		            <li data-xtype="add_top">新增同级</li>
		            <li data-xtype="update">修改该行</li>
		            <li data-xtype="update_batch">批量修改</li>
		            <li data-xtype="delete">删除该行</li>
		            <li data-xtype="version">创建版本</li>
		            <li data-xtype="copy">复制</li>
		            <li data-xtype="paste">粘贴</li>
		            <li data-xtype="import_top">导入同级</li>
		            <li data-xtype="import">导入下级</li>
		        </ul>
		    </div>
		</div><!-- /.main-container -->
		<div id="configAddDialogContainer" class="hide2"></div>
		<div id="configUpdateDialogContainer" class="hide2"></div>
		<div id="configVersionDialogContainer" class="hide2"></div>
		<div id="configImportDialogContainer" class="hide2"></div>
		
		<jsp:include page="../../inc/script.jsp"/>
		<script type="text/javascript" src="../../js/jqwidgets/jqxcore.js"></script>
    	<script type="text/javascript" src="../../js/jqwidgets/jqxdata.js"></script>
    	<script type="text/javascript" src="../../js/jqwidgets/jqxbuttons.js"></script>
    	<script type="text/javascript" src="../../js/jqwidgets/jqxscrollbar.js"></script>
    	<script type="text/javascript" src="../../js/jqwidgets/jqxcheckbox.js"></script>
    	<script type="text/javascript" src="../../js/jqwidgets/jqxlistbox.js"></script>
    	<script type="text/javascript" src="../../js/jqwidgets/jqxdropdownlist.js"></script>
    	<script type="text/javascript" src="../../js/jqwidgets/jqxtooltip.js"></script> 
    	<script type="text/javascript" src="../../js/jqwidgets/jqxinput.js"></script>
    	<script type="text/javascript" src="../../js/jqwidgets/jqxmenu.js"></script>
    	<script type="text/javascript" src="../../js/jqwidgets/jqxdatatable.js"></script>
    	<script type="text/javascript" src="../../js/jqwidgets/jqxtreegrid.js"></script>
    	<script type="text/javascript" src="../../js/jqwidgets/jqxtooltip.js"></script>
		<script src="config.js"></script>
		<script type="text/javascript">
			var select_node_name,select_node_id;
		</script>
	</body>
</html>