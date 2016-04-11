<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
	<head>
		<jsp:include page="../../inc/header.jsp"/>
	</head>

	<body class="no-skin">
		<div class="main-container">
			<div class="row" style='margin:0px;'>
				<div class="col-sm-7" style='padding:0px;'>
					<div class="widget-box no-border">
						<div class="widget-header">
							<h4 class="widget-title lighter smaller">选择菜单项</h4>
							<div class="widget-toolbar  no-border">
								<a href="#" data-toggle="tooltip" data-placement="bottom" data-original-title="收起全部" id='closeAllButton'><i class="fa fa-chevron-up blue"></i></a>
								<a href="#" data-toggle="tooltip" data-placement="bottom" data-original-title="重新加载菜单树" id='refreshButton'><i class="fa fa-refresh green" style="cursor:pointer; padding:4px"></i></a>
							</div>
						</div>
						<div class="widget-body">
							<div class="widget-main" style='padding:0px;'>
								<div id="menu_tree"></div>
							</div>
						</div>
					</div>
				</div>
				<div class="col-sm-5" style='padding:0px;'>
					<div class="widget-box transparent">
						<div class="widget-header widget-header-flat">
							<h4 class="smaller widget-title lighter" style="line-height: 36px; padding: 0px; margin: 0px; display:inline">
								菜单信息
								<span>
									<a href="#" data-toggle="tooltip" data-placement="bottom" data-original-title="新增菜单" id='newMenuButton'><i class="fa fa-plus green" style="cursor:pointer; padding:4px"></i></a>
									<a href="#" data-toggle="tooltip" data-placement="bottom" data-original-title="删除菜单" id='delMenuButton'><i class="fa fa-remove red" style="cursor:pointer; padding:4px"></i></a>
									<a href="#" data-toggle="tooltip" data-placement="bottom" data-original-title="保存修改" id='saveMenuButton'><i class="fa fa-save blue" style="cursor:pointer; padding:4px"></i></a>
								</span>
							</h4>
							
							<div class="widget-toolbar  no-border">
								<a href="#" data-toggle="tooltip" data-placement="bottom" data-original-title="复制到当前节点" id='copysButton'><i class="fa fa-copy blue" style="cursor:pointer; padding:4px"></i></a>
							</div>
						</div>
						<div class="widget-body">
							<div class="widget-main">
								<span class="input-group">
									<label class="input-group-addon no-padding-right input-group-addon-label" for="parentmenuname">上级菜单：</label>
									<input type="text" id="parentmenuname" class="form-control" readonly/>
									<input type="hidden" id="parentid"/><input type="hidden" id="menuid"/>
								</span>
								<div class="space-4"></div>
								<span class="input-group">
									<label class="input-group-addon no-padding-right input-group-addon-label" for="menuname">菜单名称：</label>
									<input type="text" id="menuname" class="form-control" placeholder="输入菜单名称" data-validation-required-message="菜单名称必输"/>
									<!-- span class="input-group-addon"><input type="checkbox" id="ishidden" value="1" data-toggle="tooltip" data-original-title="不在用户菜单中显示"/></span>  -->
								</span>
								<div class="space-4"></div>
								<span class="input-group">
									<label class="input-group-addon no-padding-right input-group-addon-label" for="menuurl">链接地址：</label>
									<input type="text" id="menuurl" class="form-control" placeholder="输入链接地址"/>
								</span>
								<div class="space-4"></div>
								<span class="input-group">
									<label class="input-group-addon no-padding-right input-group-addon-label" for="menuorder">显示次序：</label>
									<input type="number" id="menuorder" value="100" class="form-control" placeholder="输入显示次序"/>
								</span>
								<div class="space-4"></div>
								<span class="input-group">
									<label class="input-group-addon no-padding-right input-group-addon-label" for="menuicon">菜单图标：</label>
									<input type="text" id="menuicon" class="form-control" placeholder="输入菜单图标"/>
								</span>
								<div class="space-4"></div>
								<span class="input-group">
									<label class="input-group-addon no-padding-right input-group-addon-label" for="trancode">助记代码：</label>
									<input type="text" id="trancode" class="form-control" placeholder="输入助记码"/>
								</span>
								<div class="panel panel-default" style="border:0px;">
  									<div class="panel-body noclear nopack" style="padding-right:0px;">
										<table id='myJqGrid' style="width:100%"></table>
										<div id='myJqGridPager'></div>
  									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div><!-- /.main-container -->	
		
		<jsp:include page="../../inc/script.jsp"/>
		<script type="text/javascript">
		$(function(){
			checkChildWindow();//有效性验证
			$('#newMenuButton').on("click", function(){
				if (!myValidation(".col-sm-5")) {
			        return false;
		        }
				var data = packData(".col-sm-5 .widget-main");
			    var grid = $('#myJqGrid'),menurights=[];
				$(grid.jqGrid('getDataIDs')).each(function(){
					var $tr = $('#'+this+"[editable='1']");
					if($tr.length <= 0){
						return;
					}
					var $td = $('td',$tr);
					menurights.push({
						rightsvalue:$($td[2]).text(),
						rightsname:$($td[3]).text(),
						rightsurl:$('input',$($td[5])).val() || $($td[5]).text(),
					});
				});
				if(menurights.length > 0){
					data['menurights'] = menurights;
				}
			    invockeServiceSync('sys.menu.add.service', data, function(data, isSucess){
					if(!isSucess){
						return;
					}
					xMsg("新增成功");
					//refreshTree();
				});
			});
			$('#delMenuButton').on("click", function(){
				var menuid = $('#menuid').val();
				if (!menuid) {
			        return false;
		        }
				if(!confirm("你确定要删除当前菜单吗？")){
					return false;
				}
			    invockeServiceSync('sys.menu.delete.service', {menuid:menuid}, function(data, isSucess){
					if(!isSucess){
						return;
					}
					xMsg("删除成功");
					//refreshTree();
				});
			});
			$('#saveMenuButton').on("click", function(){
				if (!myValidation(".col-sm-5")) {
			        return false;
		        }
				var data = packData(".col-sm-5 .widget-main");
			    //data['ishidden'] = $('#ishidden').prop('checked')?'1':'';
			    
			    var grid = $('#myJqGrid'),menurightsadd=[],menurightsedit=[];
				$(grid.jqGrid('getDataIDs')).each(function(){
					/*var $tr = $('#'+this+"[editable='1']");
					if($tr.length <= 0){
						return;
					}*/
					var $tr = $('#'+this), $td = $('td',$tr);
					if(!$($td[2]).text() || $($td[2]).text().length <= 0){
						return;
					}
					var id = this * 1;
					if(id < 0){
						menurightsadd.push({
							rightsvalue:$($td[2]).text(),
							rightsname:$('input',$($td[4])).val() || $($td[4]).text(),
							rightsurl:$('input',$($td[5])).val() || $($td[5]).text(),
						});
					}
					else{
						menurightsedit.push({
							id:id,
							rightsvalue:$($td[2]).text(),
							rightsname:$('input',$($td[4])).val() || $($td[4]).text(),
							rightsurl:$('input',$($td[5])).val() || $($td[5]).text(),
						});
					}
				});
				if(menurightsadd.length > 0){
					data['menurightsadd'] = menurightsadd;
				}
				if(menurightsedit.length > 0){
					data['menurightsedit'] = menurightsedit;
				}
			    invockeServiceSync('sys.menu.update.service', data, function(data, isSucess){
					if(!isSucess){
						return;
					}
					xMsg("保存成功");
					//refreshTree();
				});
			});
			$('#refreshButton').on('click', function(){
				$('#menu_tree').jstree('refresh');
			});
			$('#closeAllButton').on('click', function(){
				if(!!menu_tree_node){
			    	$('#menu_tree').jstree(true).set_type(menu_tree_node,menu_tree_node_type);//恢复图标
			    }
			    var instance = $('#menu_tree').jstree(true);
			    instance.deselect_all();
			    instance.close_all();
			});
			
			$('#menu_tree').jstree({
		        plugins: ["wholerow","types"],
		        types: {
		        	folder : {
		              	icon : "fa fa-circle"
		            },
		            item : {
		              	icon : "fa fa-circle-o"
		            },
		            selected : {
		            	icon : "fa fa-dot-circle-o"
		            }
		        },
		        core: {
		            themes: {
		                name: 'proton',
		                icons : true,//false
		                responsive: true
		            },
		            multiple:false,
		            data:function(node,treecallback){
	            		var d = {};
	            		if(node.id === "#") {
	            			d['id'] = '';
	                    }
	                    else {
	                    	d['id'] = node.id;
	                    }
                    	invockeService('sys.menu.query.service', d, function(dt, isSucess){
        					if(!isSucess){
        						return;
        					}
        					treecallback(dt.menus);
        				});
		            }
		        }
		    });
			
			var menu_tree_node,menu_tree_node_type;
			$('#menu_tree').on("changed.jstree", function (e, data) {
			    clearField('.col-sm-5 .widget-main');
			    if(!data || !data.node || !data.node.original){
			    	return;
			    }
			    
			    if(!!menu_tree_node){
			    	$('#menu_tree').jstree(true).set_type(menu_tree_node,menu_tree_node_type);//恢复上一节点图标
			    }
			    menu_tree_node = data.node;
			    menu_tree_node_type = $('#menu_tree').jstree(true).get_type(menu_tree_node);//暂存原图标
			    $('#menu_tree').jstree(true).set_type(menu_tree_node,'selected');//设置选中图标
			    
			    var dt = data.node.original, parentMenuName = data.node.parent === "#"?"":data.instance.get_node(data.node.parent).text;
			    $('#menuid').val(dt['id']);
				$('#parentid').val(dt['id']);
				$('#menuname').val(dt['text']);
				$('#parentmenuname').val(parentMenuName);
				$('#menuurl').val(dt['url']);
				$('#menuorder').val(dt['menuorder']);
				$('#trancode').val(dt['trancode']);
				$('#menuicon').val(dt['iconclass']);
				//$('#ishidden').prop('checked', dt['ishidden']=='1');
				
				$("#myJqGrid").clearGridData();//清空数据
				if(dt.rights && dt.rights.length >0){
					$("#myJqGrid")[0].addJSONData({rows:dt.rights,total:1,page:1,records:dt.rights.length});
				}
			});
			
			var menu_rights,
			getMenuRightsList = function(){
				if(!menu_rights){
					menu_rights = getJqgridOptions({svrName:"pklist.sys_data_dictionary.extend.service",svrData:{datavalue:'menu_rights'}});
				}
				return menu_rights;
			};
			$("#myJqGrid").jqGrid({
			    datatype: "local",//datatype: "local",editurl
				height:'auto',
				autowidth: true,
				shrinkToFit: true,
			    colNames:['序号','权限标识','权限列表','权限名称','拦截地址'],   
			    colModel :[
				  {name:'id', index:'id', key:true, search:false, editable:false, hidden:true},
				  {name:'rightsvalue', index:'rightsvalue',search:false, editable:false, hidden:true},
			      {name:'predefinedrights', index:'predefinedrights',search:true, width:'20%', align:'center', formatter:'select', edittype:'select', editable:true, editrules:{required:true},editoptions: {value:getMenuRightsList(),defaultValue:''},searchoptions: {sopt: ['cn', 'eq'],value:getMenuRightsList()}},
				  {name:'rightsname', index:'rightsname',search:false, width:'20%',editable:true},
			      {name:'rightsurl', index:'rightsurl',search:true, width:'60%',editable:true}],
			    pager: "#myJqGridPager",
			    loadonce: true,
				cellEdit:false,
				pgbuttons:false,
				pginput:false,
				pgtext:false,
			    viewrecords: true,//定义是否在导航条上显示总的记录数
				multiselect : true,
				multiboxonly: true,
				ondblClickRow : function(rowid,iRow,iCol,e){
					var grid = $('#myJqGrid');
					if($('#'+rowid+"[editable='1']").length > 0){// Exists
						grid.restoreRow(rowid);
					}
					else{// Does not exist
						grid.editRow(rowid, false);
						
						$("select[name='predefinedrights']").unbind();
						$("select[name='predefinedrights']").on('change',function(){
							var value = $(this).val();
							if(value && value.length > 0){
								var v = value.split('-');
								grid.jqGrid('setRowData', rowid, {rightsvalue:v[0]});
								$(this).closest('tr').find("input[name='rightsurl']").val(v[1]);
								$(this).closest('tr').find("input[name='rightsname']").val($(this).find("option:selected").text());
								//grid.jqGrid('setRowData', rowid, {rightsname:$(this).find("option:selected").text()});
							}
						});
					}
				}
			}).navGrid('#myJqGridPager',
				{
					alertcap:"请选择您要操作的行",alerttext:"点击任意位置后关闭该窗口",
					edit: true, editicon : 'fa fa-pencil blue',edittitle:'修改/撤销修改选中行',
					add: true, addicon : 'fa fa-plus-circle purple',addtitle:'增加一行',
					del: true, delicon : 'fa fa-trash-o red',deltitle:'删除选中行',
					search: false, searchicon : 'fa fa-search orange',
					refresh: false, refreshicon : 'fa fa-refresh green',refreshtitle:'重新载入数据',
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
						
						$("select[name='predefinedrights']").unbind();
						$("select[name='predefinedrights']").on('change',function(){
							var rowid = $(this).attr('rowid'),value = $(this).val();
							if(value && value.length > 0){
								var v = value.split('-');
								grid.jqGrid('setRowData', rowid, {rightsvalue:v[0]});
								$(this).closest('tr').find("input[name='rightsurl']").val(v[1]);
								$(this).closest('tr').find("input[name='rightsname']").val($(this).find("option:selected").text());
								//grid.jqGrid('setRowData', rowid, {rightsname:$(this).find("option:selected").text()});
							}
						});
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
								
								$("select[name='predefinedrights']").unbind();
								$("select[name='predefinedrights']").on('change',function(){
									var rowid = $(this).attr('rowid'),value = $(this).val();
									if(value && value.length > 0){
										var v = value.split('-');
										grid.jqGrid('setRowData', rowid, {rightsvalue:v[0]});
										$(this).closest('tr').find("input[name='rightsurl']").val(v[1]);
										$(this).closest('tr').find("input[name='rightsname']").val($(this).find("option:selected").text());
										//grid.jqGrid('setRowData', rowid, {rightsname:$(this).find("option:selected").text()});
									}
								});
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
							invockeServiceSync('sys.menu.rights.delete.service',{rightid:keys}, function(data, isSucess){
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
			);
			setJqgridPageIcon();
			
			$('#gbox_myJqGrid').css('border','0px');	
			$('div.ui-jqgrid-hdiv').css('border','0px');
			$('[data-toggle="tooltip"]').tooltip({container:'body'});
		});
		</script>
	</body>
</html>		