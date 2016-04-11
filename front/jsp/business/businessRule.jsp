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
			 <div class="modal fade" id="addMsgModalDialog" tabindex="-1" role="dialog" aria-labelledby="" aria-hidden="true">
			  <div class="modal-lg modal-dialog">
			    <div class="modal-content" >
			      <div class="modal-header">
			        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">关闭</span></button>
			        <h4 class="modal-title">修改业务规则</h4>
			      </div>
			      <div class="modal-body">
			        <form role="form" id="msg_form">
						<div class="space-4"></div>
						<span class="input-group">
							规　则：&nbsp;<span id="rulename"   ></span>&nbsp;&nbsp;(<span id="rulecode" class="pack-data"></span>)
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							<!-- 版本：&nbsp;<input type="text" id="ruleversion"  data-validation-required-message="请填写版本！"  style="padding-left:10px;"/> -->
						</span>
						<div class="space-4"></div>
						<div class="input-group noclear">
							<label class="input-group-addon no-padding-right input-group-addon-label" for="msg_title">明　细：</label>
							<table id='my_clause_JqGrid' width="530"></table> <div id='my_clause_JqGridPager'></div>
						</div>
						<div class="space-4"></div>
						<span class="input-group">
							<label class="input-group-addon no-padding-right input-group-addon-label" for="msg_title">表达式：</label>
							<textarea rows="2" cols="80" id="ruleexpress" class="form-control" data-validation-required-message="请填写表达式！" readonly="readonly"></textarea>
						</span>
						<input type="hidden" id="ruleid" />
			        </form>
			      </div>
			      <div class="modal-footer">
			      	<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
			        <button type="button" class="btn btn-primary" id="update_msg_button">修改</button>
			        <!-- <button type="button" class="btn btn-primary" id="new_msg_button">创建为新版本</button>-->
			        <button type="button" class="btn btn-primary" id="new1_msg_button" onclick="showInputVersion();">另存为新版本</button>
			      </div>
			    </div>
			  </div>
			</div>
			
			<div class="modal fade" id="input_version" tabindex="-1" role="dialog" aria-labelledby="" aria-hidden="true" >
			  <div class="modal-dialog" style="width: 400px;">
			    <div class="modal-content">
			      <div class="modal-header">
			        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">关闭</span></button>
			        <h4 class="modal-title">新版本名称</h4>
			      </div>
			      <div class="modal-body" style="float: left;">
						<div style="width: 460px;height: 60px; overflow-y: auto; " id="rec_">
						<input type="text" id="input_version_v"  data-validation-required-message="请填写版本！" class="form-control" style="width:380px;;padding-left:10px;"/>
						</div>
			      </div>
			      <div class="modal-footer">
			        <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
			        <button type="button" class="btn btn-primary" id="ok_version">确定</button>
			      </div>
			    </div>
			  </div>
			</div>
			
		</div>
		
		<jsp:include page="../../inc/script.jsp">
			<jsp:param name="jqgrid" value="4"/>
		</jsp:include>
		<script type="text/javascript">
		jQuery(function($){
			checkChildWindow();
			$(window).bind('resize', function() { 	
				$("#myJqGrid").jqGrid( 'setGridWidth', $(window).width()-1);
			}); 
			
			//弹出选择单位的框
			showInputVersion = function(){
				$('#input_version').modal({backdrop:false,show:true});
			};
			
			//获取所有信息并查询
			var setJqgridData = function(){
				var grid = $("#myJqGrid");
				var rowNum = grid.getGridParam('rowNum');//每页记录数
				var page = grid.getGridParam('page'); // 当前页
				var total = grid.getGridParam('total');//总页数
				var records = grid.getGridParam('records');//总记录数
				if(page <= 1){
					records = 0;
				}
				var condition = {};
				var postData = grid.getGridParam('postData');
				 if(postData._search){
					if(!!postData.filters){//多条件查询
						var filters = $.parseJSON(postData.filters);
						for(var i in filters.rules){
							condition[filters.rules[i].field] = filters.rules[i].data;
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
					$('.fa.fa-save').addClass('hidden');
				} 
				grid.clearGridData();//清空数据
				invockeService('busrule.query.service', condition, callback, page, rowNum, records);
				return false;
			};
			isCurrentFormat = function(cellvalue, options, rowObject ){
				var id = rowObject['ruleid'], status = rowObject['status'];
				if(cellvalue == '1'){
					return "<lable><input checked class='ace ace-switch ace-switch-3' type='checkbox' onclick='setCurrentVersion(this,"+id+");'><span class='lbl'></span></lable>";
				}else{
					return "<lable><input class='ace ace-switch ace-switch-3' type='checkbox' onclick='setCurrentVersion(this,"+id+");'><span class='lbl'></span></lable>";
				}
			};
			setCurrentVersion = function(chk,id){
				if(!id || !chk){
					alert("请重新刷新页面后再试！");
					return;
				}
				if(!confirm('是否改变状态？')){
					$(chk).prop("checked", !chk.checked);
					return;
				}
				var dt = {ruleid:id,status:chk.checked?'1':'2'}; 
				invockeService("busrule.updstatus.service",dt);
			};
			//保存规则  只进入数据库存储（不保存状态）
			$('#update_msg_button').on("click", function(){
				if (!myValidation(".modal-body form")) {
				        return false;
			        }
					var data = packData(".modal-body form");
				    invockeServiceSync('busrule.upd.service', data, function(data, isSucess){
						if(!isSucess){
							return;
						}
						$('#addMsgModalDialog').modal('hide');
						xMsg("修改成功");
						$("#myJqGrid").trigger("reloadGrid");
					});
			});
			
			//确定版本
			$('#ok_version').on("click", function(){
				var input_version_v = $('#input_version_v').val();
				if(!input_version_v.length || input_version_v.length<=0){
					xAlert("请输入版本名称！");
					return;
				}
				var ruleid = $('#ruleid').val();
				var dt = {ruleid:ruleid,ruleversion:input_version_v}; 
				invockeServiceSync('busrule.newversion.service', dt, function(data, isSucess){
					if(!isSucess){
						return;
					}
					//修改配置文件中
					$('#addMsgModalDialog').modal('hide');
					$('#input_version').modal('hide');
					xMsg("版本创建成功");
					$("#myJqGrid").trigger("reloadGrid");
				});
			});
			
			//停用：只修改状态为2
			$('#stop_msg_button').on("click", function(){
				if (!myValidation(".modal-body form")) {
				        return false;
			        }
					var data = packData(".modal-body form");
					data['status'] = 2;
					//状态说明：1，保存；2已发送
				    invockeServiceSync('busrule.updstatus.service', data, function(data, isSucess){
						if(!isSucess){
							return;
						}
						$('#addMsgModalDialog').modal('hide');
						xMsg("修改成功");
						$("#myJqGrid").trigger("reloadGrid");
					});
			});
			
			//启用：只修改状态为1
			$('#start_msg_button').on("click", function(){
				if (!myValidation(".modal-body form")) {
				        return false;
			        }
					var data = packData(".modal-body form");
					data['status'] = 1;
				    invockeServiceSync('busrule.updstatus.service', data, function(data, isSucess){
						if(!isSucess){
							return;
						}
						$('#addMsgModalDialog').modal('hide');
						xMsg("修改成功");
						$("#myJqGrid").trigger("reloadGrid");
					});
			});
			
			//应用到配置文件中  数据库存储  修改配置文件
			$('#new_msg_button').on("click", function(){
				if (!myValidation(".modal-body form")) {
				        return false;
			        }
					var data = packData(".modal-body form");
				    invockeServiceSync('busrule.newversion.service', data, function(data, isSucess){
						if(!isSucess){
							return;
						}
						//修改配置文件中
						$('#addMsgModalDialog').modal('hide');
						xMsg("版本创建成功");
						$("#myJqGrid").trigger("reloadGrid");
					});
			});
			
			//修改规则明细，参数：明细ID，明细代码，实例(值)，排序，状态，操作：input/select
			updRule_clause = function(id,clausecode,example,sort,sta,op){
				invockeServiceSync('busrule_clause.upd.service',{clauseid:id,clauseexample:example,status:sta}, function(data, isSucess){
					if(!isSucess){
						return;
					}
					$('#my_clause_JqGrid').jqGrid('editRow', id, false);
					var grid_clause = $("#my_clause_JqGrid");
					caluse_jqgrid_fun($("#myJqGrid").jqGrid("getGridParam","selrow"));//重新加载grid
					if (!myValidation(".modal-body form")) {
				        return false;
			        }
					var data = packData(".modal-body form");
					data['status'] = 1;//状态说明：1，启用；2禁用
				    invockeServiceSync('busrule.upd.service', data, function(data, isSucess){
						if(!isSucess){
							return;
						}
						//以下是修改总表达式值
						var res_ruleexpress = '';
						for(i=0;i<data.rows.length;i++){
							if(data.rows[i].status == 1){
								res_ruleexpress += data.rows[i].clausecode+':'+data.rows[i].clauseexample+',';
							}
						}
						res_ruleexpress = '$'+res_ruleexpress.substring(0,res_ruleexpress.length-1)+'#';
						$('#ruleexpress').val(res_ruleexpress);//表达式赋值
					});
				});	
			};
			
			caluse_jqgrid_fun = function(ruleid){
				if(!ruleid){
					return;
				}
				invockeService('busrule.select.detail.query.service',{ruleid:ruleid}, function(data, isSucess){
					if(!isSucess){
						return;
					}
					$("#my_clause_JqGrid").clearGridData();
					$("#my_clause_JqGrid")[0].addJSONData(data);
				});
			};
			
			$("#my_clause_JqGrid").jqGrid({
				datatype: 'json',
				height:'auto',
			    colNames:['明细ID','顺序','规则明细','代码','值','启用','状态'],   
			    colModel:[
				  {name:'clauseid', index:'clauseid', key:true,  hidden:true},
				  {name:'sort', index:'sort',  width:'40px', align:'center'},
			      {name:'clausename', index:'clausename', width:'120px', align:'left'},
			      {name:'clausecode', index:'clausecode', width:'120px', align:'left'},
			      {name:'clauseexample', index:'clauseexample', editable:true,edittype:'text',align:'left'},
                  {name:'status', index:'status', width:'100px', align:'center', editable:true,edittype:'select',editoptions:{value:{0:'请选择',1:'启用',2:'禁用'}},
  					formatter:function(v){ 
                         if(v=="1"){
                             return "<span style='color:green'>已启用</span>";
                         }else{ 
                             return "<span style='color:red'>已禁用</span>" 
                         } 
                   }},
			      {name:'isedit', index:'isused', width:'50px', align:'center',
			      formatter:function(v){ 
                         if(v=="1"){
                             return "<span style='color:gray'>只读</span>";
                         }else{ 
                             return "可修改"; 
                         } 
                   }
			      }],
                   cellEdit:false,
                   loadonce: true,//一次性加载
                   onSelectRow : function(id) {
			            var rowData = $("#my_clause_JqGrid").jqGrid("getRowData",id);
			            var clausecode= rowData.clausecode;
			            var isedit= rowData.isedit;
			            var example = rowData.clauseexample;
			            var st = rowData.sort;
			            var sta = rowData.status;
			            if(isedit=="可修改"){
			            	$('#my_clause_JqGrid').jqGrid('editRow', id, true);
			            	var obj_input = $("input",$(this));
			            	obj_input.unbind();
							obj_input.on('change',function(){
								example = $(this).val();
								if(sta == "<span style=\"color:green\">已启用</span>"){
									sta = 1;
								}else{
									sta = 2;
								}
								updRule_clause(id,clausecode,example,st,sta,'input');
							});
							var obj_select = $("select",$(this));
							obj_select.unbind();
							
							obj_select.on('change',function(){
								sta = $(this).val();
								updRule_clause(id,clausecode,example,st,sta,'select');
							});
							
			            }
			       }
			});
			
			$("#myJqGrid").jqGrid({
			    datatype: setJqgridData,     //datatype: "local",
				width:($(window).width()-1),
				height:'auto',
				autowidth: true,
				shrinkToFit: true,
				styleUI : 'Bootstrap',
			    caption:"业务规则",
			    colNames:['规则ID','规则名称','版本','标志符','表达式','是否启用'],   
			    colModel:[
				  {name:'ruleid', index:'ruleid', key:true, search:true, editable:false, hidden:true},
				  {name:'rulename', index:'rulename',search:true, width:'10%', align:'left'},
				  {name:'ruleversion', index:'ruleversion',search:true, width:'5%', align:'left'},
			      {name:'rulecode', index:'rulename',search:true, width:'10%', align:'left'},
			      {name:'ruleexpress', index:'ruleexpress', width:'75%', align:'left',},
			      {name:'status', index:'status', width:'10%', align:'left',formatter:isCurrentFormat}],
			    pager: "#myJqGridPager",
			    rowNum:100,//显示行数
			    rowList:[100,200,300], //选择显示行数
				loadonce: true,//一次性加载
				cellEdit:false,
			    viewrecords: true,//定义是否在导航条上显示总的记录数
				multiselect : true,//定义是否显示多选框
				multiboxonly: true //定义复选框多选还是单选
				 
			}).navGrid('#myJqGridPager',
				{
					alertcap:"请选择您要操作的行",alerttext:"点击任意位置后关闭该窗口",
					edit: true, editicon : 'fa fa-pencil blue',edittitle:'修改/撤销修改选中行',
					add: false, addicon : 'fa fa-plus-circle purple',addtitle:'新建消息',
					del: false, delicon : 'fa fa-trash-o red',deltitle:'删除选中行',
					search: false, searchicon : 'fa fa-search orange',
					refresh: true, refreshicon : 'fa fa-refresh green',refreshtitle:'重新载入数据',
					view: false, viewicon : 'fa fa-search-plus grey',
					editfunc: function(){
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
							var rowData = grid.jqGrid('getRowData',key);//单挑对象
							var rowStatus = rowData.msg_status;
							if(rowStatus == "<span style=\"color:green\">已发送</span>"){
								xAlert("已发送的消息不可修改，请重新选择");
								return;
							}
							invockeServiceSync('busrule.select.service',{ruleid:key}, function(data, isSucess){
								if(!isSucess){
									return;
								}
								loadDivData(data.busrule_upd,'.modal-body form');
								caluse_jqgrid_fun(key);
								$('#update_msg_button').show();
								$('#send2_msg_button').show();
								$('#addMsgModalDialog .modal-header h4').text('修改规则');
								$('#addMsgModalDialog').modal({backdrop:false,show:true});
								$("#my_clause_JqGrid").jqGrid( 'setGridWidth', $('#addMsgModalDialog .modal-body').width()-70);
							});	
					    }
					}
				},
				{}, // default settings for edit
				{}, // default settings for add
				{}, // delete instead that del:false we need this
				{
					closeOnEscape: true,//esc关闭窗口
					closeAfterSearch: true,
					sopt :['cn','eq'],//条件
					multipleSearch : true//高级查询
				},// search options
				{} // view parameters
			);
			
			//弹出添加对话框的时候，情况form的内容
			$('#addMsgModalDialog').on('hidden.bs.modal', function (e) {
				clearField('#addMsgModalDialog form');
			});
			
			setJqgridPageIcon();
			$('[data-toggle="tooltip"]').tooltip({container:'body'});
		});
		</script>
	</body>
</html>