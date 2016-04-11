<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
	<head>
		<jsp:include page="../../inc/header.jsp"/>
	</head>

	<body class="no-skin" oncontextmenu="if(self.event)self.event.returnValue=false;return false;">
		<div class="main-container">		    
			<div class="modal fade" id="addUserModalDialog" tabindex="-1" role="dialog" aria-labelledby="" aria-hidden="true">
			  <div class="modal-dialog"><!--  modal-lg -->
			    <div class="modal-content">
			      <div class="modal-header">
			        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">关闭</span></button>
			        <h4 class="modal-title">新建用户</h4>
			      </div>
			      <div class="modal-body">
			        <form role="form">
						<span class="input-group">
							<label class="input-group-addon no-padding-right input-group-addon-label" for="departid">　　部门：</label>
							<select id="departid" class="form-control selectpicker" data-placeholder="请选择..."></select>
						</span>
						<div class="space-4"></div>
		          		<span class="input-group">
							<label class="input-group-addon no-padding-right input-group-addon-label" for="username">　　姓名：</label>
							<input type="text" id="username" class="form-control" data-validation-required-message="姓名必须输入"/>
						</span>
						<div class="space-4"></div>
		          		<span class="input-group">
							<label class="input-group-addon no-padding-right input-group-addon-label" for="loginname">登录名称：</label>
							<input type="text" id="loginname" class="form-control" data-validation-required-message="登录名称必须输入"/>
							<input type="hidden" id="userid"/>
						</span>
						<div class="space-4"></div>
						<span class="input-group">
							<label class="input-group-addon no-padding-right input-group-addon-label" for="disporder">显示顺序：</label>
							<input type="number" id="disporder" value="100" class="form-control input-mini" data-validation-required-message="显示顺序必须输入整数"/><p class="help-block"></p>
						</span>
						<div class="space-4"></div>
						<span class="input-group">
							<label class="input-group-addon no-padding-right input-group-addon-label" for="memo">备注说明：</label>
							<input type="text" id="memo" class="form-control"/>
						</span>
			        </form>
			      </div>
			      <div class="modal-footer">
			        <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
			        <button type="button" class="btn btn-primary" id="add_user_button">新增</button>
			        <button type="button" class="btn btn-primary" id="update_user_button">修改</button>
			      </div>
			    </div>
			  </div>
			</div>
			<div class="modal fade" id="userGroupModalDialog" tabindex="-1" role="dialog" aria-labelledby="" aria-hidden="true">
			  <div class="modal-dialog"><!--  modal-lg -->
			    <div class="modal-content">
			      <div class="modal-header">
			        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">关闭</span></button>
			        <h4 class="modal-title">用户权限</h4>
			      </div>
			      <div class="modal-body">
			        <select id="userrights" multiple="multiple"></select>
			      </div>
			      <div class="modal-footer">
			        <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
			        <button type="button" class="btn btn-primary" id="save_rights_button">保存</button>
			      </div>
			    </div>
			  </div>
			</div>
			<table id='myJqGrid' style="width:100%"></table> 
			<div id='myJqGridPager'></div>
		</div><!-- /.main-container -->	
		
		<jsp:include page="../../inc/script.jsp"/>
		<script src="<%=request.getContextPath()%>/js/md5.js"></script>

		<script type="text/javascript">
		jQuery(function($){
			checkChildWindow();
			$(window).bind('resize', function() { 	
				$("#myJqGrid").jqGrid( 'setGridWidth', $(window).width()-1);
			});
			
			var 
			status_str="A:正常;B:登录;C:停用",
			getStatusList = function(){
				return status_str;
			},
			departid_str,
			getDepartmentList = function(){
				if(!departid_str){
					departid_str = getJqgridOptions("pklist.sys_departments.service");
				}
				return departid_str;
			},
			setJqgridData = function(){
				var grid = $("#myJqGrid");
				rowNum = grid.getGridParam('rowNum'),//每页记录数
				page = grid.getGridParam('page'), // 当前页
				total = grid.getGridParam('total'),//总页数
				records = grid.getGridParam('records'),//总记录数
				postData = grid.getGridParam('postData'),
				condition = {};
				if(page <= 1){
					records = 0;
				}
				if(postData._search){
					if(!!postData.filters){//多条件查询
						var filters = $.parseJSON(postData.filters);
						for(var i in filters.rules){
							if(filters.rules[i].field == 'lastlogindate'){
								if(condition['lastlogindate'] == undefined){
									condition['lastlogindate'] = {};
								}
								
								if(filters.rules[i].op == 'ge'){
									condition.lastlogindate['start'] = filters.rules[i].data;
								}else{
									condition.lastlogindate['end'] = filters.rules[i].data;
								}
							}
							else{
								condition[filters.rules[i].field] = filters.rules[i].data;
							}
						}
					}
					if(!!postData.searchField){
						condition[postData.searchField] = postData.searchString;
					}
				}
				var callback = function(data,isSuccess){
					if(!isSuccess){
						return;
					}
					loadJqgridData(data, grid);
					$("#myJqGrid").jqGrid( 'setGridWidth', $(window).width()-1);
				}
				grid.clearGridData();//清空数据
				invockeService('sys.user.query.service',condition,callback,page,rowNum,records);
				return false;
			},
			addfunc = function(){
				var grid = $('#myJqGrid');
				$('#add_user_button').show();
				$('#update_user_button').hide();
				$('#addUserModalDialog .modal-header h4').text('新建用户');
				$('#addUserModalDialog').modal({backdrop:false,show:true});
			},
			editfunc = function(){
				var grid = $('#myJqGrid');
				var keys = grid.jqGrid("getGridParam","selarrrow"); 
			    if(!keys || keys.length <= 0){
			    	xAlert("请选择要需要操作的行");
					return;
				}else if(keys.length > 1){
					xAlert("一次只能修改一项，请重新选择");
					return;
			    }else{
					var key = grid.jqGrid("getGridParam","selrow"); //单条
					invockeServiceSync('sys.user.getuser.service',{id:key}, function(data, isSucess){
						if(!isSucess){
							return;
						}
						loadDivData(data.user,'.modal-body form');
						$('#add_user_button').hide();
						$('#update_user_button').show();
						$('#addUserModalDialog .modal-header h4').text('修改用户');
						$('#addUserModalDialog').modal({backdrop:false,show:true});
					});	
			    }
			},
			delfunc = function(){
				var grid = $('#myJqGrid');
				var keys = grid.jqGrid("getGridParam","selarrrow"); 
			    if(!keys || keys.length <= 0){
			    	xAlert("请选择要删除的行");
					return;
			    }
			    xConfirm("你确定要删除吗？",function(flag){
			    	if(!flag){
			    		return;
			    	}
			    	invockeServiceSync('sys.user.del.service',{id:keys}, function(data, isSucess){
						if(!isSucess){
							return;
						}
						xMsg("删除成功");
						var len = keys.length;  
						for(var i=0; i<len ;i++) {  
							grid.jqGrid("delRowData", keys[0]);  
						}
					});
			    });
			},
			resetfunc = function(){
				var grid = $('#myJqGrid');
				var keys = grid.jqGrid("getGridParam","selarrrow"); 
			    if(!keys || keys.length <= 0){
			    	xAlert("请选择要重置密码的用户");
					return;
			    }
			    xConfirm("你确定要重置密码吗？",function(flag){
			    	if(!flag){
			    		return;
			    	}
			    	invockeServiceSync('sys.user.password.reset.service',{id:keys,pswd:hex_md5(hex_md5('123456'))}, function(data, isSucess){
						if(!isSucess){
							return;
						}
						xMsg("重置密码成功");
					});
				});
			},
			groupfunc = function(){
				var grid = $('#myJqGrid');
				var keys = grid.jqGrid("getGridParam","selarrrow"); 
			    if(!keys || keys.length <= 0){
			    	xAlert("请选择要需要操作的行");
					return;
				}else if(keys.length > 1){
					xAlert("一次只能选择一项，请重新选择");
					return;
			    }else{
			    	$('#userGroupModalDialog').modal({backdrop:false,show:true});
			    	$('#userrights').multiSelect('deselect_all', {});
			    	invockeService("sys.user.group.query.service", {userid:keys}, function(data ,isSuccess){
						if(!isSuccess){
							return;
						}
						$('#userrights').multiSelect('select',data.groups || []);
			  		});
				}
			};
			$("#myJqGrid").jqGrid({
				datatype: setJqgridData,     //datatype: "local",
				width:($(window).width()-1),
				height:'auto',
				autowidth: true,
				shrinkToFit: true,
			    caption:"用户管理",
			    colNames:['用户序号','部门','姓名','登录名','状态','上次登录时间','显示顺序','备注'],   
			    colModel :[
					{name:'id', index:'id', key:true, hidden:true},
				    {name:'departid', index:'departid',width:'70px',search:true, formatter:'select',editoptions:{value:getDepartmentList()},stype:'select', searchoptions: {searchhidden: true, sopt: ['eq'], value:getDepartmentList()}},
				    {name:'username', index:'username',width:'90px',search:true},
				    {name:'loginname', index:'loginname',width:'70px',search:true},
				    {name:'status', index:'status',width:'40px',search:true, formatter:'select',editoptions:{value:getStatusList()}, stype:"select", searchoptions: {searchhidden: true, sopt: ['eq'],value:getStatusList()}},
				    {name:'lastlogindate', index:'lastlogindate',width:'90px',search:true,formatter:jqGridFormatDate,searchoptions: { sopt:['ge','le'],dataInit : function (elem) {$(elem).datetimepicker({format:'yyyy-mm-dd hh:ii'});}}},
				    {name:'disporder', index:'disporder',width:'50px',search:false},
				    {name:'memo', index:'memo',search:true}],
			    pager: "#myJqGridPager",
			    rowNum:12,
			    rowList:[12,18,30,50], 
				loadonce: true,
				cellEdit:false,
			    viewrecords: true,//定义是否在导航条上显示总的记录数
				multiselect : true,
				multiboxonly: true
		  	}).navGrid('#myJqGridPager',
				{
					alertcap:"请选中您要操作的行",alerttext:"您还没有选中行，请选择后再重新操作！",
					edit: true, editicon : 'fa fa-pencil blue',edittitle:'修改用户信息',
					add: true, addicon : 'fa fa-plus-circle purple',addtitle:'新建用户',
					del: true, delicon : 'fa fa-trash-o red',deltitle:'删除用户',
					search: true, searchicon : 'fa fa-search orange',searchtitle:'多条件查找',
					refresh: true, refreshicon : 'fa fa-refresh green',refreshtitle:'重新载入数据',
					view: false, viewicon : 'fa fa-search-plus grey',viewtitle:'查看详细',
					addfunc: addfunc,
					editfunc: editfunc,
					delfunc: delfunc
				},
				{}, // default settings for edit
				{}, // default settings for add
				{}, // delete instead that del:false we need this
				{
					closeOnEscape: true,
					closeAfterSearch: true,
					sopt :['cn','eq'],
					multipleSearch : true
				},// search options
				{} // view parameters
			).navSeparatorAdd("#myJqGridPager",{
				sepclass: 'ui-separator', 
				sepcontent: ''
			}).navButtonAdd("#myJqGridPager",{
				position:"last",
				title:"密码重置",
				caption:"",
				buttonicon:"fa fa-unlock blue",
				onClickButton: resetfunc
			}).navButtonAdd("#myJqGridPager",{
				position:"last",
				title:"权限设置",
				caption:"",
				buttonicon:"fa fa-street-view purple",
				onClickButton: groupfunc
			});
			setJqgridPageIcon();//上列
			$('.ui-jqgrid-titlebar>.ui-jqgrid-title').before(
				'<div class="ui-pg-div pull-right grid-tool-bar">'+
				'<span data-toggle="tooltip" data-placement="bottom" data-original-title="新建用户" id="title_btn_add"><i class="ui-icon fa fa-plus-circle purple"></i></span>'+
				'<span data-toggle="tooltip" data-placement="bottom" data-original-title="修改用户信息" id="title_btn_upd"><i class="ui-icon fa fa-pencil blue"></i></span>'+
				'<span data-toggle="tooltip" data-placement="bottom" data-original-title="删除用户" id="title_btn_del"><i class="ui-icon fa fa-trash-o red"></i></span>'+
				'<span data-toggle="tooltip" data-placement="bottom" data-original-title="密码重置" id="title_btn_reset"><i class="ui-icon fa fa-unlock blue"></i></span>'+
				'<span data-toggle="tooltip" data-placement="bottom" data-original-title="权限设置" id="title_btn_group"><i class="ui-icon fa fa-street-view purple"></i></span>'+
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
			
			$('#title_btn_add').on('click', addfunc);
			$('#title_btn_upd').on('click', editfunc);
			$('#title_btn_del').on('click', delfunc);
			$('#title_btn_reset').on('click', resetfunc);
			$('#title_btn_group').on('click', groupfunc);
			
			/*************************************************************************************/
			$('#add_user_button').on("click", function(){
				if (!myValidation(".modal-body form")) {
			        return false;
		        }
				var data = packData(".modal-body form");
				data['password'] = hex_md5(hex_md5('123456'));
			    invockeServiceSync('sys.user.add.service', data, function(data, isSucess){
					if(!isSucess){
						return;
					}
					
					//$('#add_user_button').hide();
					xMsg("新增用户成功");
				});
			});
			$('#update_user_button').on("click", function(){
				if (!myValidation(".modal-body form")) {
			        return false;
		        }
				var data = packData(".modal-body form");
			    invockeServiceSync('sys.user.update.service', data, function(data, isSucess){
					if(!isSucess){
						return;
					}
					
					//$('#update_user_button').hide();
					$('#addUserModalDialog').modal('hide');
					xMsg("修改用户成功");
				});
			});
			
			$('#addUserModalDialog').on('hidden.bs.modal', function (e) {
				clearField('#addUserModalDialog form');
			});
			
			$('#save_rights_button').on('click', function () {
				var grid = $('#myJqGrid');
				var userid = grid.jqGrid("getGridParam","selrow");
				if(!userid){
					xAlert('请选择用户')
					return;
				}
			    invockeServiceSync('sys.user.group.save.service', {userid:userid, groupid:$('#userrights').val()}, function(data, isSucess){
					if(!isSucess){
						return;
					}
					xMsg("保存成功");
				});
			});
			
			loadPKList("pklist.sys_departments.service","departid");
			loadPKList("pklist.sys_groups.service", "userrights",function(){
				$('#userrights').multiSelect({
					selectableHeader:"<div><h6>系统可用角色</h6></div>",
					selectionHeader:"<div><h6>当前用户已有角色</h6></div>"
				});
			},true);
			$('[data-toggle="tooltip"]').tooltip({container:'body'});
		});
		</script>
	</body>
</html>