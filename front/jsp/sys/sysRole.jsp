<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
	<head>
		<jsp:include page="../../inc/header.jsp"/>
	</head>

	<body class="no-skin">
		<div class="main-container">
			<div class="modal fade" id="groupRightsModalDialog" tabindex="-1" role="dialog" aria-labelledby="" aria-hidden="true">
			  <div class="modal-dialog"><!--  modal-lg -->
			    <div class="modal-content">
			      <div class="modal-header">
			        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">关闭</span></button>
			        <h4 class="modal-title">设置角色权限</h4>
			      </div>
			      <div class="modal-body">
			      	<div class="row" style='margin:0px;'>
						<div class="col-sm-6" style='padding:0px;'>
							<div class="widget-box no-border">
								<div class="widget-header">
									<h4 class="widget-title lighter smaller">角色可选权限</h4>
									<div class="widget-toolbar  no-border">
										<a href="#" id='sysRefreshButton'><i class="fa fa-refresh green" style="cursor:pointer;"></i></a>
									</div>
								</div>
								<div class="widget-body">
									<div class="widget-main" style='padding:0px;'>
										<div id="sys_rights_tree"></div>
									</div>
								</div>
							</div>
						</div>
						<div class="col-sm-6" style='padding:0px 0px 0px 10px;'>
							<div class="widget-box no-border">
								<div class="widget-header">
									<h4 class="widget-title lighter smaller">角色已有权限</h4>
									<div class="widget-toolbar  no-border">
										<a href="#" id='groupRefreshButton'><i class="fa fa-refresh green" style="cursor:pointer"></i></a>
									</div>
								</div>
								<div class="widget-body">
									<div class="widget-main" style='padding:0px;'>
										<div id="group_rights_tree"></div>
									</div>
								</div>
							</div>
						</div>
					</div>
			      </div>
			    </div>
			  </div>
			</div>

			<table id='myJqGrid' style="width:100%"></table> 
			<div id='myJqGridPager'></div>
		</div><!-- /.main-container -->	
		
		<jsp:include page="../../inc/script.jsp">
			<jsp:param name="jqgrid" value="4"/>
		</jsp:include>
		<script type="text/javascript">
		jQuery(function($){
			//jQuery.jstree.plugins.nohover = function () { this.hover_node = jQuery.noop; };
			//jQuery.jstree.plugins.noselect = function () { this.select_node = jQuery.noop; };
			
			checkChildWindow();
			$(window).bind('resize', function() { 	
				$("#myJqGrid").jqGrid( 'setGridWidth', $(window).width()-1);
			}); 
			
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
				invockeService('sys.group.query.service', condition, callback, page, rowNum, records);
				return false;
			};
			$("#myJqGrid").jqGrid({
			    datatype: setJqgridData,     //datatype: "local",
				width:($(window).width()-1),
				height:'auto',
				autowidth: true,
				shrinkToFit: true,
			    caption:"角色设置",
			    colNames:['角色代码','角色名称','说明'],   
			    colModel :[
				  {name:'id', index:'id', key:true, search:false, editable:false, hidden:true},
			      {name:'groupname', index:'groupname',search:true, width:'40%', align:'center', editable:true, editrules:{required:true},searchoptions: {sopt: ['cn', 'eq']}},
			      {name:'memo', index:'memo',search:true, width:'60%', align:'center', editable:true,searchoptions: {sopt: ['cn', 'eq']}}],
			    pager: "#myJqGridPager",
			    rowNum:12,
			    rowList:[12,30,50], 
				loadonce: true,
				cellEdit:false,
			    viewrecords: true,//定义是否在导航条上显示总的记录数
				multiselect : true,
				multiboxonly: true
			}).navGrid('#myJqGridPager',
				{
					alertcap:"请选择您要操作的行",alerttext:"点击任意位置后关闭该窗口",
					edit: true, editicon : 'fa fa-pencil blue',edittitle:'修改/撤销修改选中行',
					add: true, addicon : 'fa fa-plus-circle purple',addtitle:'增加一行',
					del: true, delicon : 'fa fa-trash-o red',deltitle:'删除选中行',
					search: true, searchicon : 'fa fa-search orange',
					refresh: true, refreshicon : 'fa fa-refresh green',refreshtitle:'重新载入数据',
					view: false, viewicon : 'fa fa-search-plus grey',
					addfunc: function(){
						var grid = $('#myJqGrid');
						var colModel = grid.jqGrid().getGridParam("colModel") ;
						var newRow = JSON.stringify(colModel); 
						
						var ids = grid.jqGrid('getDataIDs');
						var rowid = 0;
						$.each(ids,function(idx,id){
							if(Math.abs(id) > rowid){
								rowid = Math.abs(id);
							}
						});
						var newrowid = (0 - ++rowid);
						
						grid.setGridParam({cellEdit:false});								
						grid.addRowData(newrowid, newRow);
						grid.editRow(newrowid, false);
						
						$('.fa.fa-save').removeClass('hidden');
					},
					editfunc: function(){
						var grid = $('#myJqGrid');
						var ids = grid.jqGrid("getGridParam","selarrrow"); 
						$(ids).each(function(){
							if($('#'+this+"[editable='1']").length > 0){// Exists
								grid.restoreRow(this);
							}
							else{// Does not exist
								grid.editRow(this, false);
							}
						});
						
						$('.fa.fa-save').addClass('hidden');
						$(grid.jqGrid('getDataIDs')).each(function(){
							if($('#'+this+"[editable='1']").length > 0){
								$('.fa.fa-save').removeClass('hidden');
								return true;
							}
						});
					},
					delfunc: function(){
						if(!confirm("你确定要删除吗？")){
							return false;
						}
						var grid = $('#myJqGrid');
						var keys = [], delids = [];
						
						var selids = grid.jqGrid("getGridParam","selarrrow"); 
						$(selids).each(function(){
							if($('#'+this+"[editable='1']").length > 0){// Exists
								if(this < 0){
									grid.jqGrid("delRowData", this);
									return;
								}
								grid.restoreRow(this);
							}
							else{// Does not exist
								delids.push(this);
								var rowData = grid.jqGrid("getRowData",this);
								keys.push(rowData['id']);
							}
						});
						if(keys.length > 0){
							invockeServiceSync('sys.group.delete.service',{id:keys}, function(data, isSucess){
								if(!isSucess){
									return;
								}
								xMsg("删除成功");
								var len = delids.length;
								for(var i=0; i<len ;i++) {  
									grid.jqGrid("delRowData", delids[i]);
								}

								keys = [];
								delids = [];
							});
						}
						
						$('.fa.fa-save').addClass('hidden');
						$(grid.jqGrid('getDataIDs')).each(function(){
							if($('#'+this+"[editable='1']").length > 0){
								$('.fa.fa-save').removeClass('hidden');
								return true;
							}
						});
					}
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
			).navButtonAdd("#myJqGridPager",{
				position:"first",
				title:"保存所有更改",
				caption:"",
				buttonicon:"fa fa-save blue hidden",
				onClickButton: function(){
					var grid = $('#myJqGrid');
					var addData = [];
					var editData = [];
					/*
					for(var i=0; i<grid.jqGrid('getDataIDs').length; i++){
						for(var j=0; j<grid.jqGrid().getGridParam("colModel").length; j++){
							grid.jqGrid("saveCell",i,j);
						}
					}*/
					$(grid.jqGrid('getDataIDs')).each(function(){
						if($('#'+this+"[editable='1']").length <= 0){
							return;
						}
						var rowData = grid.jqGrid("getRowData",this);
						if(this < 0){
							addData.push(rowData);
						}
						else{
							editData.push(rowData);
						}
					});
					
					var data = {};
					if(addData.length > 0){
						data['add'] = addData;
					}
					if(editData.length > 0){
						data['edit'] = editData;
					}
					invockeServiceSync('sys.group.save.service',data, function(data, isSucess){
						if(!isSucess){
							return;
						}
						xMsg("保存成功");
						$('.fa.fa-save').addClass('hidden');
					});
				}
			}).navSeparatorAdd("#myJqGridPager",{
				sepclass: 'ui-separator', 
				sepcontent: ''
			}).navButtonAdd("#myJqGridPager",{
				position:"last",
				title:"设置权限",
				caption:"",
				buttonicon:"fa fa-cogs blue",
				onClickButton: function(){
					var grid = $('#myJqGrid');
					var keys = grid.jqGrid("getGridParam","selarrrow"); 
					if(!keys || keys.length <= 0){
						xAlert("请选择要需要操作的行");
						return;
					}else if(keys.length > 1){
						xAlert("一次只能选择一项，请重新选择");
						return;
				    }else{
						var key = grid.jqGrid("getGridParam","selrow"); //单条
						var rowData = grid.getRowData(key);
						
						$('#sys_rights_tree').jstree('destroy',true);
						$('#group_rights_tree').jstree('destroy',true);
						
						$('#sys_rights_tree').bind('ready.jstree', function(e, data) {
					        $('#sys_rights_tree').jstree(true).add_action("all", {
    							id: "action_add_"+data.instance._id,
    							'class': "",
    							text: "",
    							after: false,
    							append:true,//true-内部插入,false-外部插入
    							selector: "div",
    							event: "click",
    							callback: function(node_id, node, action_id, action_el){
        							var id = node.id;
        							if(id < 0){//增删改等功能
        								invockeServiceSync('sys.group.menuright.add.service', {groupid:key,menurightid:Math.abs(id*-1)}, function(data, isSucess){
        									if(!isSucess){
        										return;
        									}
        									//$("#sys_rights_tree").jstree("remove",node);  
        									$('#sys_rights_tree').jstree('refresh');
        									$('#group_rights_tree').jstree('refresh');
        								});
        							}
        							else{//菜单功能
        								invockeServiceSync('sys.group.menu.add.service', {groupid:key,menuid:id}, function(data, isSucess){
        									if(!isSucess){
        										return;
        									}
        									//$("#sys_rights_tree").jstree("remove",node);  
        									$('#sys_rights_tree').jstree('refresh');
        									$('#group_rights_tree').jstree('refresh');
        								});
        							}
    							},
    							types: {
    					        	folder : {
    					              	icon : "fa fa-hand-o-right fa-2 orange margin-top-2 pull-right"
    					            },
    					            item : {
    					              	icon : "fa fa-arrow-circle-right fa-2 orange2 margin-top-2 pull-right"
    					            },
    					            action : {
    					              	icon : "fa fa-chevron-circle-right fa-2 light-orange margin-top-2 pull-right"
    					            }
    					        }
							});
					    }).jstree({
					        plugins: ["wholerow","types","actions"],
					        types: {
					        	folder : {
					              	icon : "fa fa-star orange"
					            },
					            item : {
					              	icon : "fa fa-star-half-o orange2"
					            },
					            action : {
					              	icon : "fa fa-star-o light-orange"
					            }
					        },
					        core: {
					            themes: {
					                name: 'proton',
					                responsive: true
					            },
					            data:function(node,treecallback){
				            		var d = {groupid:key};
				            		if(node.id === "#") {
				            			d['parentid'] = '';
				                    }
				                    else {
				                    	d['parentid'] = node.id;
				                    }
			                    	invockeService('sys.group.menu.unselect.query.service', d, function(dt, isSucess){
			        					if(!isSucess){
			        						return;
			        					}
			        					treecallback(dt.tree);
			        				});
					            }//end callback
					        }
					    }).bind('dblclick.jstree', function(e, data) {
					        // invoked before jstree starts loading
					    	//alert("确定要增加权限吗？");
					    });
						
						/** 节点数据结构
							{
								id			: "string" 
								parent		: "string" 
								text		: "string" 
								icon		: "string" 
								state		: {
									opened		: boolean  
									disabled	: boolean  
									selected	: boolean  
								},
								children	: []/true/false/异步加载使用 
								li_attr		: {}  
								a_attr		: {}  
							}
						**/
						
						$('#group_rights_tree').bind('ready.jstree', function(e, data) {
					        $('#group_rights_tree').jstree(true).add_action("all", {
    							id: "action_del_"+data.instance._id,
    							'class': "fa fa-trash-o bigger-130 margin-top-2 pull-right",
    							text: "",
    							after: false,
    							append:true,//true-内部插入,false-外部插入
    							selector: "div",
    							event: "click",
    							callback: function(node_id, node, action_id, action_el){
        							var id = node.id;
        							if(id < 0){//增删改等功能
        								invockeServiceSync('sys.group.menuright.delete.service', {groupid:key,menurightid:Math.abs(id*-1)}, function(data, isSucess){
        									if(!isSucess){
        										return;
        									}
        									//$("#sys_rights_tree").jstree("remove",node);  
        									$('#sys_rights_tree').jstree('refresh');
        									$('#group_rights_tree').jstree('refresh');
        								});
        							}
        							else{//菜单功能
        								invockeServiceSync('sys.group.menu.delete.service', {groupid:key,menuid:id}, function(data, isSucess){
        									if(!isSucess){
        										return;
        									}
        									//$("#sys_rights_tree").jstree("remove",node);  
        									$('#sys_rights_tree').jstree('refresh');
        									$('#group_rights_tree').jstree('refresh');
        								});
        							}
    							},
    							types: {
    					        	folder : {
    					              	icon : "fa fa-times bigger-130 red margin-top-2 pull-right"
    					            },
    					            item : {
    					              	icon : "fa fa-trash-o bigger-130 red2 margin-top-2 pull-right"
    					            },
    					            action : {
    					              	icon : "fa fa-times-circle bigger-130 light-red margin-top-2 pull-right"
    					            }
    					        }
							});
					    }).jstree({
					        plugins: ["wholerow","types","actions"],
					        types: {
					        	folder : {
					              	icon : "fa fa-star green"
					            },
					            item : {
					              	icon : "fa fa-star-half-o light-green"
					            },
					            action : {
					              	icon : "fa fa-star-o"
					            }
					        },
					        core: {
					            themes: {
					                name: 'proton',
					                responsive: true
					            },
					            data:function(node,treecallback){
				            		var d = {groupid:key};
				            		if(node.id === "#") {
				            			d['parentid'] = '';
				                    }
				                    else {
				                    	d['parentid'] = node.id;
				                    }
			                    	invockeService('sys.group.menu.selected.query.service', d, function(dt, isSucess){
			        					if(!isSucess){
			        						return;
			        					}
			        					treecallback(dt.tree);
			        				});
					            }
					        }
					    }).bind('dblclick.jstree', function(e, data) {
					        // invoked before jstree starts loading
					    	//alert("确定要删除权限吗？");
					    });
						
						$('#sysRefreshButton').on('click', function(){
							$('#sys_rights_tree').jstree('refresh');
						});
						$('#groupRefreshButton').on('click', function(){
							$('#group_rights_tree').jstree('refresh');
						});
						
						$('#groupRightsModalDialog').modal({backdrop:false,show:true});
					}
				}
			});
			setJqgridPageIcon();
			
			$('[data-toggle="tooltip"]').tooltip({container:'body'});
		});
		</script>
	</body>
</html>