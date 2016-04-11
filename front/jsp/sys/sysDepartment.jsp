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
		
		<jsp:include page="../../inc/script.jsp">
			<jsp:param name="jqgrid" value="4"/>
		</jsp:include>
		<script type="text/javascript">
		jQuery(function($){
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
				var callback = function(data,isSuccess){
					if(!isSuccess){
						return;
					}

					$.each(data['rows'],function(idx,row){
						row['isLeaf'] = row['isLeaf'] == 'true' ? true : false;
					});
					loadJqgridData(data, grid);
					$('.fa.fa-save').addClass('hidden');
					$("#myJqGrid").jqGrid( 'setGridWidth', $(window).width()-1);
				}
				grid.clearGridData();//清空数据
				invockeService('sys.department.query.service',{},callback,page,rowNum,records);
				return false;
			};
			$("#myJqGrid").jqGrid({
			    datatype: setJqgridData,     //datatype: "local",
				width:($(window).width()-1),
				height:'auto',
				autowidth: true,
				shrinkToFit: true,
			    caption:"部门管理",
			    colNames:['部门代码','上级部门','部门名称','部门说明','显示顺序','创建日期'],   
			    colModel :[
					{name:'id', index:'id', key:true, hidden:true},
					{name:'parentid', index:'parentid', hidden:true},
					{name:'departmentname', index:'departmentname',search:false, width:'45%', editable:true,edittype:'text'},
					{name:'departmentdesc', index:'departmentdesc',search:false, width:'30%', editable:true,edittype:'text'},
					{name:'disporder', index:'disporder',search:false, width:'10%', editable:true,edittype:'text'},
					{name:'createdate', index:'createdate',search:false, width:'15%', editable:false}],
		        treeGrid: true,
		        treeGridModel: "adjacency",
		        ExpandColumn: "departmentname",
		        //ExpandColClick: true,
		        treeIcons: {plus:'fa fa-plus',minus:'fa fa-minus',leaf:'fa fa-file-o'},
		        //pager: "false",
		        //scroll: "true",
			    pager: "#myJqGridPager",
			    rowNum:10000,
			    rowList:[1000,5000,10000], 
				loadonce: true,
				cellEdit:false,
				//inlineEdit:true,
			    viewrecords: true,//定义是否在导航条上显示总的记录数
				multiselect : true,
				multiboxonly: true
			}).navGrid('#myJqGridPager',
				{
					alertcap:"请选中您要操作的行",alerttext:"您还没有选中行，请选择后再重新操作！",
					edit: true, editicon : 'fa fa-pencil blue',edittitle:'修改/撤销修改选中行',
					add: true, addicon : 'fa fa-plus-circle purple',addtitle:'增加子节点',
					del: true, delicon : 'fa fa-trash-o red',deltitle:'删除选中行',
					search: false, searchicon : 'fa fa-search orange',
					refresh: true, refreshicon : 'fa fa-refresh green',refreshtitle:'重新载入数据',
					view: false, viewicon : 'fa fa-search-plus grey',
					addfunc: function(){
						var grid = $('#myJqGrid');
						var isAddedRow = false;
						$(grid.jqGrid('getDataIDs')).each(function(){
							isAddedRow = true;
							if($('#'+this+"[editable='1']").length <= 0){
								isAddedRow= false;
								return false;
							}
						});
						if(isAddedRow){
							xAlert("首次添加部门时只能添加一个节点，请先保存后再继续添加");
							return;
						}
						var key = grid.jqGrid("getGridParam","selrow"); //单条
						if(!key || key.length <= 0){
							if(!confirm("没有选择上级部门，是否增加顶层部门？")){
								return ;
							}
						}
						else if($('#'+key+"[editable='1']").length > 0){// Exists
							xAlert("未保存，不能继续增加部门，请先保存后再增加");
							return;
						}
						
						var maxkey = 0;
						$(grid.jqGrid('getDataIDs')).each(function(){
							if(Math.abs(this) > maxkey){
								maxkey = Math.abs(this);
							}
						});
						var newkey = (0 - ++maxkey),
						rowData =grid.getRowData(key),
						initData = {
							id:'',
							parentid:key || null, 
							departmentname:'',
							departmentdesc:'',
							disporder:'100',
							createdate:'',
							parent:key || null,
							level:rowData?rowData['level']++ || 0:0,
							isLeaf:true,
							expanded:true
						};
						grid.setGridParam({cellEdit:false});
						grid.addChildNode(newkey, key || null, initData);
						grid.editRow(newkey, false);
						
						$('.fa.fa-save').removeClass('hidden');
					},
					editfunc: function(){
						var grid = $('#myJqGrid');
						var key = grid.jqGrid("getGridParam","selrow"); //单条
						if(!key || key.length <= 0){
							return;
						}
						
						if($('#'+key+"[editable='1']").length > 0){// Exists
							grid.restoreRow(key);
						}
						else{// Does not exist
							grid.editRow(key, false);
						}
						
						$('.fa.fa-save').addClass('hidden');
						$(grid.jqGrid('getDataIDs')).each(function(){
							if($('#'+this+"[editable='1']").length > 0){
								$('.fa.fa-save').removeClass('hidden');
								return true;
							}
						});
					},
					delfunc: function(){
						var grid = $('#myJqGrid');
						var key = grid.jqGrid("getGridParam","selrow"); //单条
						if(!key || key.length <= 0){
							return;
						}
						if(!confirm("你确定要删除该部门及其下级部门吗？")){
							return false;
						}
						if($('#'+key+"[editable='1']").length > 0){// Exists
							if(key < 0){
								grid.jqGrid("delTreeNode", key);
								return;
							}
							grid.restoreRow(this);
						}
						var rowData =grid.getRowData(key);
						var ids = [];
						$(grid.getNodeChildren(grid.jqGrid('getLocalRow', rowData['id']))).each(function(){
							ids.push(this['id']);
						});
						ids.push(rowData['id']);
						invockeServiceSync('sys.department.delete.service',{id:ids}, function(data, isSucess){
							if(!isSucess){
								return;
							}
							xMsg("删除成功");
							grid.trigger('reloadGrid');
						});
						
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
				{},// search options
				{} // view parameters
			).navButtonAdd("#myJqGridPager",{
				position:"last",
				title:"保存所有更改",
				caption:"",
				buttonicon:"fa fa-save blue hidden",
				onClickButton: function(){
					var grid = $('#myJqGrid'),
					addData = [],
					editData = [];
							
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
					invockeServiceSync('sys.department.save.service',data, function(data, isSucess){
						if(!isSucess){
							return;
						}
						xMsg("保存成功");
						$('.fa.fa-save').addClass('hidden');
						grid.trigger('reloadGrid');
					});
				}
			});
			setJqgridPageIcon();
			
			$('[data-toggle="tooltip"]').tooltip({container:'body'});
		});
		</script>
	</body>
</html>