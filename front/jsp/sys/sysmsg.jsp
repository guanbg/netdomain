<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="javax.servlet.jsp.*,java.io.*"%> 
<%@ page import="com.platform.cubism.front.login.Login"%> 
<%
	String ctx_c = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="en">
	<head>
		<jsp:include page="../../inc/header.jsp"/>
	</head>
    <link href="<%=ctx_c %>/js/umeditor/themes/default/css/umeditor.css" type="text/css" rel="stylesheet">
    <script type="text/javascript" src="<%=ctx_c %>/js/umeditor/third-party/jquery.min.js"></script>
    <script type="text/javascript" charset="utf-8" src="<%=ctx_c %>/js/umeditor/umeditor.config.js"></script>
    <script type="text/javascript" charset="utf-8" src="<%=ctx_c %>/js/umeditor/umeditor.min.js"></script>
    <script type="text/javascript" src="<%=ctx_c %>/js/umeditor/lang/zh-cn/zh-cn.js"></script>
    

	<body class="no-skin" oncontextmenu="if(self.event)self.event.returnValue=false;return false;">
		 <div class="main-container">
			 	
			<table id='myJqGrid' style="width:100%"></table> 
			<div id='myJqGridPager'></div>
			
			 <div class="modal fade" id="addMsgModalDialog" tabindex="-1" role="dialog" aria-labelledby="" aria-hidden="true">
			  <div class="modal-dialog" style="width: 800px;">
			    <div class="modal-content">
			      <div class="modal-header">
			        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">关闭</span></button>
			        <h4 class="modal-title">新建消息</h4>
			      </div>
			      <div class="modal-body" style="float: left;">
			        <form role="form" id="msg_form">
						<div class="space-4"></div>
						<span class="input-group">
							<label class="input-group-addon no-padding-right input-group-addon-label" for="msg_title">接收人：</label>
							<textarea rows="2" cols="80" id="receive_user_names" class="form-control" data-validation-required-message="请选择接收人！" onclick="javascript:showSelectGuest();" readonly="readonly"></textarea>
							<input type="hidden" id="receive_user_ids" />
						</span>
						<div class="space-4"></div>
						<span class="input-group">
							<label class="input-group-addon no-padding-right input-group-addon-label" for="msg_title">标　题：</label>
							<input type="text" id="msg_title" class="form-control" data-validation-required-message="请填写标题！"  style="padding-left:10px;"/>
						</span>
						<div class="space-4"></div>
						<span class="input-group">
							<label class="input-group-addon no-padding-right input-group-addon-label" for="msg_text">正　文：</label>
							<input type="hidden" />
							<script type="text/plain" id="myEditor">
							</script>

						</span>
						<input type="hidden" id="msg_id" />
			        </form>
			      </div>
			      <div class="modal-footer">
			      	<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
			        <button type="button" class="btn btn-primary" id="add_msg_button">保存</button>
			        <button type="button" class="btn btn-primary" id="send_msg_button">保存并发送</button>
			        <button type="button" class="btn btn-primary" id="update_msg_button">修改</button>
			        <button type="button" class="btn btn-primary" id="send2_msg_button">修改并发送</button>
			      </div>
			    </div>
			  </div>
			</div>
			
			<div class="modal fade" id="viewMsgModalDialog" tabindex="-1" role="dialog" aria-labelledby="" aria-hidden="true">
			  <div class="modal-dialog" style="width: 800px;">
			    <div class="modal-content">
			      <div class="modal-header">
			        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">关闭</span></button>
			        <h4 class="modal-title">查看消息</h4>
			      </div>
			      <div class="modal-body" style="float: left;">
			        <form id="msg_v_form"  id="msg_form">
						<div class="space-4"></div>
						<span class="input-group">
							标　题：<span id="msg_title" ></span>
						</span>
						<div class="space-4"></div>
						<span class="input-group">
							<label class="input-group-addon no-padding-right " style="font-size: 13px;">正　文：</label>
							<input type="hidden" id="msg_id" /><span id="msg_text_r" ></span>
						</span>
						<input type="hidden" id="msg_id" />
						<input type="hidden" id="msg_id_v" />
						<div class="space-4"></div>
						<span class="input-group">
							已读(<a href="javascript:view_read_history($('#msg_id_v').val());" ><span id="count_read" ></span></a>)：<span  id="receive_user_names_read" style="color: green"></span>
						</span>
						<div class="space-4"></div>
						<span class="input-group">
							未读(<a href="javascript:view_read_history($('#msg_id_v').val());" ><span id="count_send" ></span></a>)：<span  id="receive_user_names_send" style="color: red"></span>
						</span>
			        </form>
			      </div>
			      <div class="modal-footer">
			      </div>
			    </div>
			  </div>
			</div>
			
			<div class="modal fade" id="selectguest" tabindex="-1" role="dialog" aria-labelledby="" aria-hidden="true" >
			  <div class="modal-dialog" style="width: 300px;">
			    <div class="modal-content">
			      <div class="modal-header">
			        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">关闭</span></button>
			        <h4 class="modal-title">选择单位</h4>
			      </div>
			      <div class="modal-body" style="float: left;">
			        <form role="form" id="msg_form">
						<div style="width: 260px;height: 360px; overflow-y: auto; " id="rec_"></div>
			        </form>
			      </div>
			      <div class="modal-footer">
			        <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
			        <button type="button" class="btn btn-primary" id="select_guest_button">确定</button>
			      </div>
			    </div>
			  </div>
			</div>
			
			<div class="modal fade" id="readhistory" tabindex="-1" role="dialog" aria-labelledby="" aria-hidden="true" >
			  <div class="modal-dialog" style="width: 500px;">
			    <div class="modal-content">
			      <div class="modal-header">
			        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">关闭</span></button>
			        <h4 class="modal-title">阅读情况</h4>
			      </div>
			      <div class="modal-body" style="float: left;"><table id='my_history_JqGrid' ></table>
			      </div>
			      <div class="modal-footer">
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
			var tree_checked;
			 $("#rec_").jstree({
			    "checkbox" : {
			      "keep_selected_style" : false
			    },
			    "plugins" : [ "checkbox"],
			    core: {
		            themes: {
		            	icons: false,
		                name: 'proton',
		                responsive: true
		            },
		            data:function(node,treecallback){
		            	invockeService('crop.registeuser.getall.service', {}, function(data, isSucess){
							if(!isSucess){
								treecallback([]);
								return;
							}
							treecallback(data.registeuser || []);
						});
		            }
		        }
			  }).on("click",function(node,selected,event){
				var tree = $('#rec_').jstree(true);
				if(!tree.is_checked('0')){//没勾选节点0
					if(tree_checked){
						tree.enable_checkbox(tree_checked);//所有节点可用
					}
				}else{//勾选了节点0
					tree.check_all();
					tree_checked = tree.get_top_checked()
					tree.disable_checkbox(tree_checked);//禁用所有节点
					
					tree.uncheck_all();//去掉全部勾选节点
					
					tree.enable_checkbox('0');//节点0可用
					tree.check_node('0');//节点0勾选
				}
	        }).on('uncheck_node.jstree',function(node,selected,event){alert(8888);
	        	if(node.id != '0'){
					return;
				}
	        });
			
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
				invockeService('msg.query.service', condition, callback, page, rowNum, records);
				return false;
			};
			
			//确定选择的单位
			$('#select_guest_button').on("click", function(){
				var guest_ids = $('#rec_').jstree().get_checked();//选中的ID
				var guest = $('#rec_').jstree().get_checked(true);//选中的对象
				
				var guest_names = '';
				for(i=0;i<guest.length;i++){
					guest_names += guest[i].text+',';
				}
				guest_names = guest_names.substring(0,guest_names.length);	
				$('#receive_user_names').val(guest_names);
				$('#receive_user_ids').val(guest_ids);
				$('#selectguest').modal('hide');
			});
				
				
			//保存消息
			$('#add_msg_button').on("click", function(){
				if (!myValidation(".modal-body form")) {
			        return false;
		        }
				var data = packData(".modal-body form");
				data['msg_text'] = UM.getEditor('myEditor').getContent();
				data['msg_status'] = 1;
				//状态说明：1，保存；2已发送
				data['receive_user_ids'] = $('#receive_user_ids').val().split(',');
			    invockeServiceSync('msg.add.service', data, function(data, isSucess){
					if(!isSucess){
						return;
					}
					$('#addMsgModalDialog').modal('hide');
					xMsg("保存消息成功");
					$("#myJqGrid").trigger("reloadGrid");
				});
			});
						
			//保存并发送信息
			 $('#send_msg_button').on("click", function(){
			 	if (!myValidation(".modal-body form")) {
			        return false;
		        }
				var data = packData(".modal-body form");
				data['msg_text'] = UM.getEditor('myEditor').getContent();
				data['msg_status'] = 2;
				//状态说明：1，保存；2已发送
				data['receive_user_ids'] = $('#receive_user_ids').val().split(',');
			    invockeServiceSync('msg.add.service', data, function(data, isSucess){
					if(!isSucess){
						return;
					}
					$('#addMsgModalDialog').modal('hide');
					xMsg("保存消息成功");
					$("#myJqGrid").trigger("reloadGrid");
				});
			});
			
			//修改消息
			$('#update_msg_button').on("click", function(){
				if (!myValidation(".modal-body form")) {
			        return false;
		        }
				var data = packData(".modal-body form");
				data['msg_text'] = UM.getEditor('myEditor').getContent();
				data['msg_status'] = 1;
				//状态说明：1，保存；2已发送
				data['receive_user_ids'] = $('#receive_user_ids').val().split(',');
			    invockeServiceSync('msg.update.service', data, function(data, isSucess){
					if(!isSucess){
						return;
					}
					$('#addMsgModalDialog').modal('hide');
					xMsg("修改消息成功");
					$("#myJqGrid").trigger("reloadGrid");
				});
			});
			
			//修改并发送消息
			$('#send2_msg_button').on("click", function(){
				if (!myValidation(".modal-body form")) {
			        return false;
		        }
				var data = packData(".modal-body form");
				data['msg_text'] = UM.getEditor('myEditor').getContent();
				data['msg_status'] = 2;
				//状态说明：1，保存；2已发送
				data['receive_user_ids'] = $('#receive_user_ids').val().split(',');
			    invockeServiceSync('msg.update.service', data, function(data, isSucess){
					if(!isSucess){
						return;
					}
					$('#addMsgModalDialog').modal('hide');
					xMsg("修改消息成功");
					$("#myJqGrid").trigger("reloadGrid");
				});
			});
			
			//预览消息
			showMsgView = function(key){
				invockeServiceSync('msg.selectandlook.service',{msg_id:key}, function(data, isSucess){
					if(!isSucess){
						return;
					}
					var receive_user_names_send = data.msg_upd.receive_user_names_send;
					var receive_user_names_read = data.msg_upd.receive_user_names_read;
					if(receive_user_names_send.length>=30){
						receive_user_names_send = receive_user_names_send.substring(0,30)+'...';
					}
					if(receive_user_names_read.length>=30){
						receive_user_names_read = receive_user_names_read.substring(0,30)+'...';
					}
					
					loadDivData(data.msg_upd,$('#msg_v_form'));
					$('#msg_text_r').html(data.msg_upd.msg_text);
					
					$('#receive_user_names_send').html(receive_user_names_send);
					$('#receive_user_names_read').html(receive_user_names_read);
					$('#msg_id_v').val(data.msg_upd.msg_id);
					$('#add_msg_button').hide();
					$('#send_msg_button').hide();
					$('#update_msg_button').hide();
					$('#send2_msg_button').hide();
					$('#viewMsgModalDialog .modal-header h4').text('详细内容');
					$('#viewMsgModalDialog').modal({backdrop:false,show:true});
				});	
			};
			
			//预览阅读记录
			view_read_history = function(key){
				invockeServiceSync('msg.selectandlookhistory.service',{msg_id:key}, function(data, isSucess){
					if(!isSucess){
						return;
					}
					$("#my_history_JqGrid").clearGridData();
					$("#my_history_JqGrid")[0].addJSONData(data);
					$('#readhistory .modal-header h4').text('阅读记录');
					$('#readhistory').modal({backdrop:false,show:true});
					$("#my_history_JqGrid").jqGrid( 'setGridWidth', $('#readhistory .modal-body').width()-10);
					
				});	
			}
			//阅读记录grid
			$("#my_history_JqGrid").jqGrid({
				datatype: 'json',
				height:320,
			    colNames:['阅读id','企业名称','阅读时间'],   
			    colModel:[
				  {name:'receive_id', index:'receive_id', key:true,  hidden:true},
				  {name:'company_name', index:'company_name',  width:'330px', align:'left'},
			      {name:'read_time', index:'read_time', width:'170px', align:'left'}],
                   cellEdit:false,
                   loadonce: true//一次性加载
			});
			//主数据
			$("#myJqGrid").jqGrid({
			    datatype: setJqgridData,     //datatype: "local",
				width:($(window).width()-1),
				height:'auto',
				autowidth: true,
				shrinkToFit: true,
				styleUI : 'Bootstrap',
			    caption:"消息页面",
			    colNames:['消息编号','发送者','消息标题','消息内容','附件','接收数','已读数','发送时间','消息状态'],   
			    colModel:[
				  {name:'msg_id', index:'msg_id', key:true, search:true, editable:false, hidden:true},
			      {name:'send_user', index:'send_user',search:true, width:'10%', align:'center',editable:true, hidden:true,editrules:{required:true},},
			      {name:'msg_title', index:'msg_title',search:true, width:'70%', align:'left', editable:true, formatter:function(cellvalue, options, rowObject){
					return "<a onclick=\"showMsgView("+rowObject['msg_id']+");\" style='text-decoration:underline;color:blue;cursor:pointer;'>"+cellvalue+"</a>";
					}, editrules:{required:true},searchoptions: {sopt: ['cn', 'eq']}},
			      {name:'msg_text', index:'msg_text',search:true, width:'10%', align:'center', editable:true, hidden:true,editrules:{required:true},},
			      {name:'attechment', index:'attechment',search:true, width:'10%', align:'center', editable:true, hidden:true,editrules:{required:true},},
			      {name:'count_send', index:'count_send',search:true, width:'5%', align:'left', editable:true, editrules:{required:true},},
			      {name:'count_read', index:'count_read',search:true, width:'5%', align:'left', editable:true,
			      formatter:function(cellvalue, options, rowObject){
					return "<a onclick=\"view_read_history("+rowObject['msg_id']+");\" style='text-decoration:underline;color:blue;cursor:pointer;'>"+cellvalue+"</a>";
					}, editrules:{required:true},},
			      {name:'send_time', index:'send_time',search:true, width:'10%', align:'left', editable:true, editrules:{required:true},},
			      {name:'msg_status', index:'msg_status',search:true, width:'10%', align:'left', editable:true, editoptions:{value:'1:<span style=\'color:blue\'>已保存	</span>;2:<span style=\'color:green\'>已发送</span>'},
  					formatter:function(v){ 
                         if(v=="1"){
                             return "<span style='color:blue'>已保存</span>";
                         }else{ 
                             return "<span style='color:green'>已发送</span>" 
                         } 
                   } ,editrules:{required:true},}],
			    pager: "#myJqGridPager",
			    rowNum:12,//显示行数
			    rowList:[12,30,50], //选择显示行数
				loadonce: true,//一次性加载
				cellEdit:false,
			    viewrecords: true,//定义是否在导航条上显示总的记录数
				multiselect : true,//定义是否显示多选框
				multiboxonly: true //定义复选框多选还是单选
				 
			}).navGrid('#myJqGridPager',
				{
					alertcap:"请选择您要操作的行",alerttext:"点击任意位置后关闭该窗口",
					edit: true, editicon : 'fa fa-pencil blue',edittitle:'修改/撤销修改选中行',
					add: true, addicon : 'fa fa-plus-circle purple',addtitle:'新建消息',
					del: true, delicon : 'fa fa-trash-o red',deltitle:'删除选中行',
					search: false, searchicon : 'fa fa-search orange',
					refresh: true, refreshicon : 'fa fa-refresh green',refreshtitle:'重新载入数据',
					view: false, viewicon : 'fa fa-search-plus grey',
					addfunc: function(){
						//清空内容
						//$("#msg_form")[0].reset(); 
						//复选框数据
						//把复选框信息放到复选框中，发送时选择对应的用户，点击发送
						var grid = $('#myJqGrid');
						$('#add_msg_button').show();
						$('#send_msg_button').show();
						$('#update_msg_button').hide();
						$('#send2_msg_button').hide();
						$('#addMsgModalDialog .modal-header h4').text('新建消息');
						$('#addMsgModalDialog').modal({backdrop:false,show:true});
 					},
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
							invockeServiceSync('msg.select.service',{msg_id:key}, function(data, isSucess){
								if(!isSucess){
									return;
								}
								loadDivData(data.msg_upd,'.modal-body form');
								UM.getEditor('myEditor').setContent(data.msg_upd.msg_text);//必须手动写入UM编辑器
								if(data.msg_upd.receive_user_ids){
									var ids = data.msg_upd.receive_user_ids.split(',');
									$('#rec_').jstree().check_node(ids);
									if($.inArray('0',ids)>=0){
										$('#receive_user_names').val('全体单位,'+data.msg_upd.receive_user_names);
									}
								}
								
								$('#add_msg_button').hide();
								$('#send_msg_button').hide();
								$('#update_msg_button').show();
								$('#send2_msg_button').show();
								$('#addMsgModalDialog .modal-header h4').text('修改消息');
								$('#addMsgModalDialog').modal({backdrop:false,show:true});
								
							});	
					    }
					},
					delfunc: function(){
						var grid = $('#myJqGrid');
						var keys = grid.jqGrid("getGridParam","selarrrow"); 
					    if(!keys || keys.length <= 0){
					    	xAlert("请选择要删除的行");
							return;
					    }
					    else if(!confirm("你确定要删除吗？")){
							return false;
						}
						invockeServiceSync('msg.delete.service',{id:keys}, function(data, isSucess){
							if(!isSucess){
								return;
							}
							xMsg("删除成功");
							var len = keys.length;  
							for(var i=0; i<len ;i++) {  
								grid.jqGrid("delRowData", keys[0]);  
							}
						});
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
				$('#rec_').jstree().uncheck_all();
				
			});
			
			//弹出选择单位的框
			showSelectGuest = function(){
				$('#selectguest').modal({backdrop:false,show:true});
			};
			
			setJqgridPageIcon();
			$('[data-toggle="tooltip"]').tooltip({container:'body'});
		});
		</script>
	</body>
</html>
<script type="text/javascript">
    //实例化编辑器
    var ue = UM.getEditor('myEditor', {
    	initialFrameHeight:340,
    	initialFrameWidth:730,
    	maximumWords:8000,
    	autoHeightEnabled:false,
    	toolbar:[
            'source | bold italic underline | forecolor backcolor | ',
            'insertorderedlist insertunorderedlist | link unlink' 
        ]
    });
    </script>