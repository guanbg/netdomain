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
			var dispTypeFormat = function(cellvalue, options, rowObject ){
				if(cellvalue == '1') {
					return "数值和名称";
				}
				else{
					return "仅名称";
				}
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
				var postData = grid.getGridParam('postData');
				var condition = {};
				if(postData._search){
					if(!!postData.filters){//多条件查询
						var filters = $.parseJSON(postData.filters);
						for(var rule in filters.rules){
							condition[filters.rules[rule].field] = filters.rules[rule].data;
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

					$.each(data['rows'],function(idx,row){
						row['isLeaf'] = row['isLeaf'] == 'true' ? true : false;
					});
					loadJqgridData(data, grid);
					$('.fa.fa-save').addClass('hidden');
					$("#myJqGrid").jqGrid( 'setGridWidth', $(window).width()-1);
				}
				grid.clearGridData();//清空数据
				/*
				var data ={pageSize:10,page:2,total:2,records:17,rows:[
				    {id:'1', data_name:'a',data_value:'a',data_desc:'a', parent:null,level:0, isLeaf:false, expanded:true},
					{id:'11', data_name:'a1',data_value:'a1',data_desc:'a1',parent:'1',level:1,isLeaf:true,expanded:false},
					{id:'12', data_name:'a2',data_value:'a2',data_desc:'a2',parent:'1',level:1,isLeaf:true,expanded:false},
					{id:'2', data_name:'b',data_value:'b',data_desc:'b',parent:null,level:0,isLeaf:false,expanded:false},
					{id:'21', data_name:'b1',data_value:'b1',data_desc:'b1',parent:'2',level:1,isLeaf:false,expanded:false},
					{id:'211', data_name:'b11',data_value:'b11',data_desc:'b11',parent:'21',level:2,isLeaf:true,expanded:false},
					{id:'212', data_name:'b12',data_value:'b12',data_desc:'b12',parent:'21',level:2,isLeaf:true,expanded:false}]};
				loadJqgridData(data, grid);*/
				invockeService('sys.datadictionary.query.service',condition,callback,page,rowNum,records);
				return false;
			};
			$("#myJqGrid").jqGrid({
			    datatype: setJqgridData,     //datatype: "local",
				width:($(window).width()-1),
				height:'auto',
				autowidth: true,
				shrinkToFit: true,
			    caption:"数据字典",
			    colNames:['标识','上级节点','显示名称','取值','显示次序','显示方式','扩展信息','说明'],   
			    colModel :[
					{name:'id', index:'id', key:true, hidden:true},
					{name:'parentid', index:'parentid', hidden:true},
				    {name:'dataname', index:'dataname',search:true, width:'40%', editable:true, editrules:{required:true}},
				    {name:'datavalue', index:'datavalue',search:true, width:'20%', editable:true},
				    {name:'disporder', index:'disporder',search:false, width:'7%', editable:true},
				    {name:'disptype', index:'disptype',search:false, width:'8%',formatter:dispTypeFormat, editable:true, edittype:"select", editoptions:{value:"0:仅名称;1:数值和名称"}},
				    {name:'extenddata', index:'extenddata',search:true, width:'15%', editable:true},
				    {name:'description', index:'description',search:true, width:'10%', editable:true}],
		        treeGrid: true,
		        treeGridModel: "adjacency",
		        ExpandColumn: "dataname",
		        treeIcons: {plus:'fa fa-plus',minus:'fa fa-minus',leaf:'fa fa-file-o'},
		        //scroll: "true",
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
							xAlert("首次添加根节点时只能添加一个节点，请先保存后再继续添加");
							return;
						}
						var key = grid.jqGrid("getGridParam","selrow"); //单条
						if(!key || key.length <= 0){
							if(!confirm("没有选择要增加的上级节点，是否增加顶层节点？")){
								return ;
							}
						}
						else if($('#'+key+"[editable='1']").length > 0){// Exists
							xAlert("未保存，不能继续增加子节点，请先保存后再增加");
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
						grid.addChildNode(newkey,key || null, {id:'',parentid:key || null, dataname:'',datavalue:'',disptype:0, extenddata:'',description:'',parent:key || null,level:rowData?rowData['level']++ || 0:0,isLeaf:true,expanded:true});
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
						if(!confirm("你确定要删除该节点及其下级节点吗？")){
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
						invockeServiceSync('sys.datadictionary.delete.service',{id:ids}, function(data, isSucess){
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
					invockeServiceSync('sys.datadictionary.save.service',data, function(data, isSucess){
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