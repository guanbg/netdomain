<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
	<head>
		<jsp:include page="../../inc/header.jsp"/>
	</head>

	<body class="no-skin" oncontextmenu="if(self.event)self.event.returnValue=false;return false;">
			<table id='myJqGrid' style="width:100%"></table> 
			<div id='myJqGridPager'></div>
			<div id="viewCompanyParticularDialogContainer" class="hide2"></div>
		<jsp:include page="../../inc/script.jsp"/>
		<script type="text/javascript">
		
		jQuery(function($){
			checkChildWindow();
			$(window).bind('resize', function() {
				$("#myJqGrid").jqGrid( 'setGridWidth', $(window).width()-1);
			});
			
			var statusForumat = function(cellvalue, options, rowObject ){
				switch(cellvalue){
					case '1': return "<font color='gray'>待审核</font>";
					case '2': return "<font color='green'>审核通过</font>";
					case '4': return "<font color='red'>业务冻结</font>";
				}
				return "";
			},			
			fileTitleFormatter = function(cellvalue, options, rowObject){
				if(!cellvalue){
					return "";
				}
				return "<span class='file_title' role=button style='border-bottom:1px solid blue;color:blue;'>"+cellvalue+"</span>";
			},
			freezefunc = function(){
				var grid = $('#myJqGrid');
				var keys = grid.jqGrid("getGridParam","selarrrow"); //selarrrowduoti
				var rowData = grid.jqGrid("getRowData",keys);//获取数据
				var status= $(rowData.approval_status).text();
				var userId=getLoginUserId();//审核人ID
				var record_name;
				var object_name;
				if(!keys || keys.length <= 0){
			    	xAlert("请选择要冻结的企业");
					return;
			    }else if(keys.length >=2){
					xAlert("一次只能冻结一个企业，请重新选择");
					return;
			    }else if(status=='业务冻结'){
				    	xAlert("该企业已被冻结");
				    	return;
				    }
			      else
				xConfirm("你确定要冻结该企业么？",function(flag){
			    	if(!flag){
			    		return false;
			    	}
				invockeServiceSync('sys.user.getuser.service',{id:userId},function(data,isSuccess){
					if(!isSuccess){
						return;
					}
					record_name=data.user['username'];
				});
				invockeServiceSync('crop.registeuser.get.service',{contractor_id:keys}, function(data, isSucess){
					if(!isSucess){
						return;
					}
					object_name = data.crop['company_name'];
				});
				invockeServiceSync('crop.freeze.account.service',{contractor_id:keys,record_name:record_name,object_name:object_name}, function(data, isSucess){
					if(!isSucess){
						return;
					}
					window.location.reload();
				});
			  });
			},
			companydelfunc = function(){
				var grid = $('#myJqGrid');
				var keys = grid.jqGrid("getGridParam","selarrrow"); //selarrrowduoti
			    if(!keys || keys.length <= 0){
			    	xAlert("请选择要删除的企业");
					return;
			    }
			    else xConfirm("你确定要删除该企业么？",function(flag){
		    	if(!flag){
		    		return ;
		    	}
			    var companys_name="";
			    for(var i=0;i<keys.length;i++){
			    	var rowData = grid.jqGrid("getRowData",keys[i]);
			    	companys_name +=$(rowData.company_name).text();
			    }
				invockeServiceSync('crop.registeuser.delete.service',{contractor_id:keys,company_name:companys_name}, function(data, isSucess){
					if(!isSucess){
						return;
					}
					var len = keys.length;  
					for(var i=0; i<len ;i++) {  
						grid.jqGrid("delRowData", keys[0]);  
					}
					window.location.reload();
				});	
			  });
			};
			$("#myJqGrid").jqGrid({
			    datatype: function(){setJqgridDataFunc('crop.approval.pass.query.service');},//数据类型
			    width:($(window).width()-1),//有7px的border
				height:'auto',
				autowidth: false,//自动宽
				shrinkToFit: true,//自动收缩属性
				forceFit:false,//压入配合
				hidegrid:false,//隐藏方格点
				caption:"企业管理",//标题//
			    colNames:['企业序号','企业名称','企业类型','专业分类id','专业分类','法定代表人','联系人手机','电子邮箱','审核状态','审核日期'],   //formatter 格式化//editoptions 校订选择 //stype 式样//searchoptions 搜索选择// 
			    colModel :[
					{name:'contractor_id', index:'contractor_id', key:true, hidden:true},
				    {name:'company_name', index:'company_name',width:'110px',search:true,formatter:fileTitleFormatter},
				    {name:'enterprise_type', index:'enterprise_type',width:'130px',search:true},
				    {name:'professional_list', index:'professional_list',hidden:true},
				    {name:'professional_list_name', index:'professional_list_name',width:'70px',search:true},
				    {name:'legal_representative', index:'legal_representative',width:'70px',search:true},
				    {name:'contact_phone', index:'contact_phone',width:'100px',search:true},
				    {name:'email', index:'email',width:'100px',search:true},                                                                                                       //日期时间控件//                  
				    {name:'approval_status', index:'approval_status',search:false,width:"40px",formatter:statusForumat, stype:'select', searchoptions: {searchhidden: true, sopt: ['eq','ne']}},
				    {name:'approval_date', index:'approval_date',width:'90px',search:false,formatter:jqGridFormatDate}],			    
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
							if(userId=="" || userId==null){
								return;
							}
							invockeServiceSync('crop.registeuser.get.service',{contractor_id:key}, function(data, isSucess){
								if(!isSucess){
									return;
								}
								showDialog("companyParticular.jsp","viewCompanyParticularDialog",function(dialog){
									loadDivData(data.crop,"#viewCompanyParticularDialog .modal-body",function(n,v){return n});
									//专业分类
									var list = val.split(",").join("<br>");
									    if(list!=null && list!=''){
									    	$("#professional_list").text("");
									    	$("#professional_list").append(list);
									}
									//图片查看
									$('#business_license_pic').attr('src','netdomain.sys_diskfile_download?percentage=40&downtype=image&fileid='+data.crop['business_license_pic']);
									$('#business_license_pic').click(function(){
										var src = 'netdomain.sys_diskfile_download?downtype=image&fileid='+data.crop['business_license_pic'];
										parent.imageView(src);
									});
									//图片查看
									$('#account_code_pic').attr('src','netdomain.sys_diskfile_download?percentage=40&downtype=image&fileid='+data.crop['account_code_pic']);
									$('#account_code_pic').click(function(){
										var src = 'netdomain.sys_diskfile_download?downtype=image&fileid='+data.crop['account_code_pic'];
										parent.imageView(src);
									});
									//$("#business_license_gallery").attr('href','netdomain.sys_diskfile_download?downtype=image&fileid='+data.crop['business_license_pic']);
									//$("#account_code_gallery").attr('href','netdomain.sys_diskfile_download?downtype=image&fileid='+data.crop['account_code_pic']);
									$("#username").val(userId);//保存操作人
									$("#refusal").hide();//通过按钮屏蔽
									$("#approval_pass").hide();//未通过按钮屏蔽
									//附件（重新查询）
									invockeServiceSync('crop.registeuser.get.service', {contractor_id:key}, function(data, isSucess){
										if(!isSucess){
											return;
										}
										if(data.crop['contractor_accessory']==null || data.crop['contractor_accessory']==''){
											return;
										}
										var acc=data.crop['contractor_accessory'],accname=data.crop['contractor_accessory_filename'];
										var accessory = acc.split(","), accessoryname = accname.split(",");
										var ol =$("#accessory_ol");//获取ol
										//判断是否为图片
										for(var i=0;i<accessory.length;i++){
											fileId=accessory[i];
											var name =accessoryname[i].split(".");
											var nameValue = name[1].toUpperCase();
											if(nameValue==='PNG' || nameValue==='JPG' || nameValue==="BMP" || nameValue==="JPEG"){
												ol.append("<li ><span><a id="+fileId+" href='#'>"+accessoryname[i]+"<input type='hidden' value="+fileId+" /></a></span></li>");
												ol.find('span').click(function(){
												        var fileId =$(this).find('input').val();
														var src = "netdomain.sys_diskfile_download?fileid="+fileId;
													    parent.imageView(src);
												});
											}else{
												ol.append("<li><a target='_blank' href='netdomain.sys_diskfile_download?fileid="+fileId+"'>"+accessoryname[i]+"</a></li>");
											}
										}
									});
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
					del: false, delicon : 'fa fa-trash-o red',deltitle:'删除企业',
					search: true, searchicon : 'fa fa-search orange',searchtitle:'多条件查找',
					refresh: true, refreshicon : 'fa fa-refresh green',refreshtitle:'重新载入数据',
					view: false, viewicon : 'fa fa-search-plus grey',viewtitle:'查看详细',
				},
				{}, // default settings for edit
				{}, // default settings for add
				{}, // delete instead that del:false we need this
				{
					closeOnEscape: true,//按ESC关闭对话框
					closeAfterSearch: true,
					sopt :['cn','eq','lt','le','gt','ge'],//系统选项寄存器
					multipleSearch : true//多项查询//
				},// search options
				{} // view parameters
			).navButtonAdd("#myJqGridPager",{
				position:"last",
				title:"冻结企业",
				caption:"",
				buttonicon:"fa fa-lock purple",
				onClickButton: freezefunc
			}).navButtonAdd("#myJqGridPager",{
				position:"last",
				title:"删除企业",
				caption:"",
				buttonicon:"fa fa-trash-o red",
				onClickButton: companydelfunc
			});
			setJqgridPageIcon();
			$('.ui-jqgrid-titlebar>.ui-jqgrid-title').before(
				'<div class="ui-pg-div pull-right grid-tool-bar">'+
				'<span data-toggle="tooltip" data-placement="bottom" data-original-title="多条件查找" id="title_btn_search"><i class="ui-icon fa fa-search orange"></i></span>'+
				'<span data-toggle="tooltip" data-placement="bottom" data-original-title="重新载入数据" id="title_btn_refresh"><i class="ui-icon fa fa-refresh green"></i></span>'+
				'<span data-toggle="tooltip" data-placement="bottom" data-original-title="冻结企业" id="title_btn_freeze"><i class="ui-icon fa fa-lock purple"></i></span>'+
				'<span data-toggle="tooltip" data-placement="bottom" data-original-title="删除企业" id="title_btn_del"><i class="ui-icon fa fa-trash-o red"></i></span>'+
				'</div>'
			);
			$('#title_btn_refresh').on('click', function(){
				$("#myJqGrid").trigger("reloadGrid");
			});
			$('#title_btn_search').on('click', function(){
				$("#myJqGrid").jqGrid("searchGrid");
			});
			$('#title_btn_freeze').on('click', freezefunc);
			$('#title_btn_del').on('click', companydelfunc);
			$('[data-toggle="tooltip"]').tooltip({container:'body'});
		});
		</script>
	</body>
</html>		