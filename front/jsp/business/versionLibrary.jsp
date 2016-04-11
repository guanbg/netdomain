<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
	<head>
		<jsp:include page="../../inc/header.jsp"/>
		<style type="text/css">
		.modal-open{overflow:auto;}
		body{overflow-x:auto;}
		</style>
	</head>

	<body class="no-skin">
		<div class="main-container" >
			<div class='row-fluid margin-lr-0'>
	  				<div id="leftContentMain">
	  					<div class="widget-box no-border">
							<div class="widget-header">
								<h4 class="widget-title lighter smaller">版本库</h4>
								<div class="widget-toolbar  no-border no-float">
									<a href="#" tabindex="-1" data-toggle="tooltip" data-placement="bottom" data-original-title="收起全部" id='closeAllButton'><i tabindex="-1" class="fa fa-chevron-up blue"></i></a>
									<a href="#" tabindex="-1" data-toggle="tooltip" data-placement="bottom" data-original-title="刷新版本库" id='refreshLibraryButton'><i tabindex="-1" class="fa fa-refresh green hand padding-4"></i></a>
								</div>
							</div>
							<div class="widget-body">
								<div class="widget-main" style='padding:0px;'>
									<div id="library_tree"></div>
								</div>
							</div>
						</div>
	  				</div>
			</div>
			
 			<div class="achive_file_container hide2">
  				<div id="rightContentMain_detail" class="nooverflowx">
  				</div>
 			</div>
 			
		</div><!-- /.main-container -->
		
		<script>
			getVersionId=function(){return "<%=request.getParameter("version_id")%>";};
			getFSId=function(){return "<%=request.getParameter("fs_id")%>";};
		</script>
		<jsp:include page="../../inc/script.jsp"/>
		<script src="versionLibrary.js"></script>
	</body>
</html>