<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
	String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="en">
	<head>
		<jsp:include page="../../inc/header.jsp"/>
	</head>

	<body class="no-skin" oncontextmenu="if(self.event)self.event.returnValue=false;return false;">
			<table id='myJqGrid' style="width:100%"></table> 
			<div id='myJqGridPager'></div>
			<div id="viewdocumentorParticularDialogContainer" class="hide2"></div>
		<jsp:include page="../../inc/script.jsp"/>
		<script src="<%=request.getContextPath()%>/js/md5.js"></script>
		<script type="text/javascript">
		jQuery(function($){
			checkChildWindow();
			$(window).bind('resize', function() {
				$("#myJqGrid").jqGrid( 'setGridWidth', $(window).width()-1);
			});
			
			var statusForumat = function(cellvalue, options, rowObject ){
				switch(cellvalue){
					case '1': return "<font color='gray'>待审核</font>";
				}
				return "";
			},
			fileTitleFormatter = function(cellvalue, options, rowObject){
				if(!cellvalue){
					return "";
				}
				return "<span class='file_title' role='button' style='border-bottom:1px solid blue;color:blue;'>"+cellvalue+"</span>";
			};
			$("#myJqGrid").jqGrid({
			    datatype: function(){setJqgridDataFunc('crop.documentor.approval.service');},//数据类型
			    width:($(window).width()-1),//有7px的border
				height:'auto',
				autowidth: false,//自动宽
				shrinkToFit: true,//自动收缩属性
				forceFit:false,//压入配合
				hidegrid:false,//隐藏方格点
				caption:"待审资料员",//标题//
			    colNames:['资料员序号','资料员姓名','手机','邮箱','专业分类id','专业分类','所属单位','单位联系电话','审核状态','提交日期'],   //formatter 格式化//editoptions 校订选择 //stype 式样//searchoptions 搜索选择// 
			    colModel :[
					{name:'documentor_id', index:'documentor_id', key:true, hidden:true},
				    {name:'documentor_name', index:'documentor_name',width:'40px',search:true,formatter:fileTitleFormatter},
				    {name:'mobile_no', index:'mobile_no',width:'60px',search:true},
				    {name:'email', index:'email',width:'80px',search:true},
				    {name:'professional_list', index:'professional_list',hidden:true},
				    {name:'professional_list_name', index:'professional_list_name',width:'70px',search:true},
				    {name:'company_name', index:'company_name',width:'100px',search:true},
				    {name:'contact_phone', index:'contact_phone',width:'80px',search:true},                                                                                                       //日期时间控件//                  
				    {name:'approval_status', index:'approval_status',search:false,width:"40px",formatter:statusForumat, stype:'select', searchoptions: {searchhidden: true, sopt: ['eq','ne']}},
				    {name:'submit_documentor_audit', index:'submit_documentor_audit',width:'90px',search:false,formatter:jqGridFormatDate}],			    
				    pager: "#myJqGridPager",
			    rowNum:12,
			    rowList:[12,18,50,100], 
				loadonce: true,
				cellEdit:false,
			    viewrecords: true,//定义是否在导航条上显示总的记录数
				multiselect : true,
				multiboxonly: true,
				gridComplete: function () {
					var grid=$("#myJqGrid")
					grid.find("span.file_title").unbind();
					grid.find("span.file_title").click(function(){
						setTimeout(function(){//防阻塞
							var key = grid.jqGrid("getGridParam","selrow"); //单条
							var rowData = grid.jqGrid("getRowData",key);//获取数据
							var val= rowData.professional_list_name;
							var userId=getLoginUserId();//审核人ID
							invockeServiceSync('crop.documentor.get.service',{documentor_id:key}, function(data, isSucess){
								if(!isSucess){
									return;
								}
								showDialog("documentorParticular.jsp","viewdocumentorParticularDialog",function(dialog){
									loadDivData(data.documentor,"#viewdocumentorParticularDialog .modal-body",function(n,v){return n});
									var sex = $("#documentor_sex").text();
									if(sex==1){
										$("#documentor_sex").text("男");
									}else{
										$("#documentor_sex").text("女");
									}
									var project_name = data.documentor['project_name'].split(",");
									if(project_name!=null && project_name!=''){
										$("#project_name_add").empty("");
										var a=0;
										for(var i=0;i<project_name.length;i++){
											a++;
											jQuery("#project_name_add").append(a+":"+project_name[i]+"<br>");
										}
									}
								    if(val!=null && val!=''){
								    	$("#professional_list").text("");
								    	$("#professional_list").text(val);
								    }
									var datetime = data.documentor['register_date'].split(" ");
									$("#register_date").text(jqGridFormatDate(datetime[0]));
									$("#username").val(userId);
									$("#photo").attr('src','netdomain.sys_diskfile_download?percentage=40&downtype=image&fileid='+data.documentor['photo']);
									$('#photo').click(function(){
										var src = 'netdomain.sys_diskfile_download?downtype=image&fileid='+data.documentor['photo'];
										parent.imageView(src);
									});
									$('#scan_accessory_pic').attr('src','netdomain.sys_diskfile_download?percentage=40&downtype=image&fileid='+data.documentor['scan_accessory']);
									$('#scan_accessory_pic').click(function(){
										var src = 'netdomain.sys_diskfile_download?downtype=image&fileid='+data.documentor['scan_accessory'];
										parent.imageView(src);
									});
									//$("#photo").attr("src",'netdomain.sys_diskfile_download?downtype=image&fileid='+data.documentor['photo']);
									//$("#photo_gallery").attr('href','netdomain.sys_diskfile_download?downtype=image&fileid='+data.documentor['photo']);
									//$("#scan_accessory_pic").attr("src",'netdomain.sys_diskfile_download?downtype=image&fileid='+data.documentor['scan_accessory']);
									//$("#scan_accessory_gallery").attr('href','netdomain.sys_diskfile_download?downtype=image&fileid='+data.documentor['scan_accessory']);
								});
							});	
						},0);
					});
				}
			}).navGrid('#myJqGridPager',
				{
					alertcap:"请选中您要操作的行",alerttext:"您还没有选中行，请选择后再重新操作！",
					edit: false, editicon : 'fa fa-pencil blue',edittitle:'修改用户信息',
					add: false, addicon : 'fa fa-plus-circle purple',addtitle:'新建用户',
					del: false, delicon : 'fa fa-trash-o red',deltitle:'删除用户',
					search: true, searchicon : 'fa fa-search orange',searchtitle:'多条件查找',
					refresh: true, refreshicon : 'fa fa-refresh green',refreshtitle:'重新载入数据',
					view: false, viewicon : 'fa fa-search-plus grey',viewtitle:'查看详细'
				},
				{}, // default settings for edit
				{}, // default settings for add
				{}, // delete instead that del:false we need this
				{
					closeOnEscape: true,
					closeAfterSearch: true,
					sopt :['cn','eq','lt','le','gt','ge'],
					multipleSearch : true
				},// search options
				{} // view parameters
			);
			setJqgridPageIcon();
			$('.ui-jqgrid-titlebar>.ui-jqgrid-title').before(
				'<div class="ui-pg-div pull-right grid-tool-bar">'+
				'<span data-toggle="tooltip" data-placement="bottom" data-original-title="多条件查找" id="title_btn_search"><i class="ui-icon fa fa-search orange"></i></span>'+
				'<span data-toggle="tooltip" data-placement="bottom" data-original-title="重新载入数据" id="title_btn_refresh"><i class="ui-icon fa fa-refresh green"></i></span>'+
				'</div>'
			);
			$('#title_btn_refresh').on('click', function(){
				$("#myJqGrid").trigger("reloadGrid");
			});
			$('#title_btn_search').on('click', function(){
				$("#myJqGrid").jqGrid("searchGrid");
			});
		   
			$('[data-toggle="tooltip"]').tooltip({container:'body'});
		});
		</script>
	</body>
</html>		