<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
	<head>
		<jsp:include page="../../inc/header.jsp"/>
	</head>

	<body class="no-skin">
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
			var positionIdentifyData;
			invockeServiceSync("pklist.sys_data_dictionary.service",{datavalue:'position_identify'},function(data,isSuccess){
				if(!isSuccess){
					return;
				}
				
				positionIdentifyData = data.pklist;
			});
			var positionIdentifyBuildSelect = function(data){
				var txt = '';
				if(!positionIdentifyData || positionIdentifyData.length <= 0){
					return txt;
				}
				$(positionIdentifyData).each(function() {
					if(txt && txt.length > 0){
						txt += ";";
					}
					txt += this.value+":"+this.text; 
				});
				return txt;
			},
			positionIdentifyFormat = function(cellvalue, options, rowObject ){
				var ret;
				if(!cellvalue || !positionIdentifyData || positionIdentifyData.length <= 0){
					return "&nbsp;";
				}
				$(positionIdentifyData).each(function() {
					if(cellvalue == this.value) {ret = this.text; return false;}
				});
				return !!ret?ret:"&nbsp;";
			},
			setJqgridData = function(){
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
				invockeService('sys.position.query.service',{},callback,page,rowNum,records);
				return false;
			};
			$("#myJqGrid").jqGrid({
				datatype: setJqgridData,     //datatype: "local",
				width:($(window).width()-1),
				height:'auto',
				autowidth: true,
				shrinkToFit: true,
			    caption:"岗位管理",
			    colNames:['岗位代码','上级岗位','岗位名称','岗位标识','显示次序','岗位描述'],   
			    colModel :[
					{name:'id', index:'id', key:true, hidden:true},
				  	{name:'parentid', index:'parentid', hidden:true},
			      	{name:'positionname', index:'positionname',search:false, width:'30%', editable:true, editrules:{required:true}},
			      	{name:'positionidentify', index:'positionidentify',search:false, width:'30%',editable:true, formatter:positionIdentifyFormat, edittype:'select', editoptions: {value: positionIdentifyBuildSelect()}},
			      	{name:'disporder', index:'disporder',search:false, width:'10%', editable:true},
			      	{name:'positiondesc', index:'positiondesc',search:false, width:'30%', editable:true}],
			    treeGrid: true,
		        treeGridModel: "adjacency",
		        ExpandColumn: "positionname",
		        treeIcons: {plus:'fa fa-plus',minus:'fa fa-minus',leaf:'fa fa-file-o'},
			    pager: "#myJqGridPager",
			    rowNum:10000,
			    rowList:[1000,5000,10000], 
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
					search: false, searchicon : 'fa fa-search orange',
					refresh: true, refreshicon : 'fa fa-refresh green',refreshtitle:'重新载入数据',
					view: false, viewicon : 'fa fa-search-plus grey',
					addfunc: function(){
						var grid = $('#myJqGrid'),isAddedRow = false;
						$(grid.jqGrid('getDataIDs')).each(function(){
							isAddedRow = true;
							if($('#'+this+"[editable='1']").length <= 0){
								isAddedRow= false;
								return false;
							}
						});
						if(isAddedRow){
							xAlert("首次添加时只能添加一个节点，请先保存后再继续添加");
							return;
						}
						var key = grid.jqGrid("getGridParam","selrow"); //单条
						if(!key || key.length <= 0){
							if(!confirm("没有选择上级岗位，是否增加顶层岗位？")){
								return ;
							}
						}
						else if($('#'+key+"[editable='1']").length > 0){// Exists
							xAlert("未保存，不能继续增加，请先保存后再增加");
							return;
						}
						
						var maxkey = 0;
						$(grid.jqGrid('getDataIDs')).each(function(){
							if(Math.abs(this) > maxkey){
								maxkey = Math.abs(this);
							}
						});
						var newkey = (0 - ++maxkey);
						var rowData =grid.getRowData(key);
						grid.setGridParam({cellEdit:false});
						grid.addChildNode(newkey,key || null, {id:'',parentid:key || null, positionidentify:'',positionname:'',disporder:100, positiondesc:'',parent:key || null,level:rowData?rowData['level']++ || 0:0,isLeaf:true,expanded:true});
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
						if(!confirm("你确定要删除吗？")){
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
						$(grid.getNodeChildren(grid.jqGrid('getLocalRow', rowData['department_id']))).each(function(){
							ids.push(this['position_id']);
						});
						ids.push(rowData['position_id']);
						invockeServiceSync('sys.position.delete.service',{position_id:ids}, function(data, isSucess){
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
					invockeServiceSync('sys.position.save.service',data, function(data, isSucess){
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