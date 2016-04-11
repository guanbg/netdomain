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
			<div id='logItems' class='fade in' style="position:absolute;background-color: #ffe;"></div>
		</div>
		
		<jsp:include page="../../inc/script.jsp"/>
		<script type="text/javascript">
		jQuery(function($){
			checkChildWindow();
			$(window).bind('resize', function() {
				$("#myJqGrid").jqGrid( 'setGridWidth', $(window).width()-1);
			});
			
			var statusForumat = function(cellvalue, options, rowObject ){
				switch(cellvalue){
					case 'S': return "<span class='label label-sm label-success'>S-成功</span>";
					case 'F': return "<span class='label label-sm label-inverse arrowed-in'>F-失败</span>";
					case 'T': return "<span class='label label-sm label-info arrowed arrowed-righ'>T-超时</span>";
					case 'U': return "<span class='label label-sm label-warning'>U-未知</span>";
				}
				return "";
			},
			logitemsForumat = function(cellvalue, options, rowObject ){
				if(!cellvalue){
					return "";
				}
				var tr = '';
				$(cellvalue).each(function(){
					tr += "<tr><td>"+this.description+"</td><td>"+this.data+"</td></tr>"
				});
				return "<table class='table table-bordered table-striped'><thead class='thin-border-bottom'><th>字段名</th><th>修改值</th></thead><tbody>"+tr+"</tbody></table>";
			},			
			setJqgridData = function(){
				var grid = $("#myJqGrid");
				rowNum = grid.getGridParam('rowNum'),//每页记录数
				page = grid.getGridParam('page'),// 当前页
				total = grid.getGridParam('total'),//总页数
				records = grid.getGridParam('records'),//总记录数
				postData = grid.getGridParam('postData'),
				condition = {};
				
				if(page <= 1){
					records = 0;
				}
				
				/**  报文样例
				filters:						
				{
					"groupOp":"AND",
					"rules":[
						{"field":"full_title","op":"cn","data":"666"},
						{"field":"content_status","op":"eq","data":"0"},
						{"field":"checknum","op":"ge","data":"8"},
						{"field":"checknum","op":"le","data":"55"}
					]
				}
				*/
				if(postData._search){
					if(!!postData.filters){//多条件查询
						var filters = $.parseJSON(postData.filters);
						for(var i in filters.rules){
							if(filters.rules[i].field == 'status'){
								if(filters.rules[i].op == 'eq'){
									condition[filters.rules[i].field] = filters.rules[i].data;
								}else{
									condition[filters.rules[i].field+'2'] = filters.rules[i].data;
								}
							}
							else if(filters.rules[i].field == 'servicetime'){
								if(condition['servicetime'] == undefined){
									condition['servicetime'] = {};
								}
								
								if(filters.rules[i].op == 'ge'){
									condition.servicetime['start'] = filters.rules[i].data;
								}else{
									condition.servicetime['end'] = filters.rules[i].data;
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
				invockeService('sys.registerLog.query.service',condition,callback,page,rowNum,records);
				return false;
			};
			$("#myJqGrid").jqGrid({
			    datatype: setJqgridData,
			    width:($(window).width()-1),//有7px的border
				height:'auto',
				autowidth: false,
				shrinkToFit: true,
				forceFit:false,
				hidegrid:false,
			    caption:"系统登录日志",
			    colNames:['日志编号','日志明细','服务名称','服务说明','执行状态','返回码','消息描述','操作人','操作时间','登录名称','IP地址','浏览器'],   
			    colModel :[
					{name:'id', index:'id', key:true, hidden:true},
					{name:'logitems', index:'logitems', hidden:true, formatter:logitemsForumat, search:true, searchoptions: {searchhidden: true, sopt: ['cn', 'eq']}},
					{name:'servicename', index:'servicename', title:false, width:'120px', search:true,searchoptions: {sopt: ['cn', 'eq']}},
				    {name:'servicedesc', index:'servicedesc',search:true,searchoptions: {sopt: ['cn', 'eq']}},
				    {name:'status', index:'status',width:'60px', align:'center',search:true, formatter:statusForumat, stype:'select', searchoptions: {searchhidden: true, sopt: ['eq','ne'], value:"S:S-成功;F:F-失败;T:T-超时;U:U-未知"}},
				    {name:'msgcode', index:'msgcode',width:'45px',search:true,searchoptions: {sopt: ['cn', 'eq']}},
				    {name:'msgdesc', index:'msgdesc',search:true,searchoptions: {sopt: ['cn', 'eq']}},
				    {name:'username', index:'username',width:'60px',align:'center',search:true,searchoptions: {sopt: ['cn', 'eq']}},
				    {name:'servicetime', index:'servicetime',width:'90px',search:true,formatter:jqGridFormatDate,searchoptions: { sopt:['ge','le'],dataInit : function (elem) {$(elem).datetimepicker({format:'yyyy-mm-dd hh:ii'});}}},
				    {name:'loginname', index:'loginname',width:'60px', align:'center',search:true,searchoptions: {sopt: ['cn', 'eq']}},
				    {name:'ip', index:'ip',width:'60px',width:'80px',search:true,searchoptions: { sopt:['cn', 'eq']}},
				    {name:'agent', index:'agent',width:'90px',search:true,searchoptions: {sopt: ['cn', 'eq']}}],
			    pager: "#myJqGridPager",
			    rowNum:12,
			    rowList:[12,18,50,100], 
				loadonce: true,
				cellEdit:false,
			    viewrecords: true,//定义是否在导航条上显示总的记录数
				multiselect : true,
				multiboxonly: true,
				gridComplete: function () {
					$(">td:eq(3)",$("#myJqGrid .jqgrow")).mouseover(function (event) {
					   	var rowId = $(event.target).parents("tr:first").attr('id'),
					    rowdata = $('#myJqGrid').getRowData(rowId),
					    items = rowdata['logitems'];
					    $('#logItems').empty();
					    if(!items){
					    	return;
					    }
					    $(items).appendTo($('#logItems'));
					    
					    var border_top = $(window).height(),
					    border_right = $(window).width(),
					    left_pos,top_pos;
					    if(event.pageX + $('#logItems').width() >= border_right){
					        left_pos = event.pageX - $('#logItems').width();
					     } else{
					        left_pos = event.pageX;
					     }

					     if(event.pageY + $('#logItems').height() >= border_top){
					        top_pos = event.pageY - $('#logItems').height();
					     } else{
					        top_pos = event.pageY;
					     }
					    $('#logItems').css({left:left_pos, top:top_pos});
					    $('#logItems').show();
					});
					$(">td:eq(3)",$("#myJqGrid .jqgrow")).mouseout(function (event) {
					    $('#logItems').empty().hide();
					});
					
					$(">td:eq(3)",$("#myJqGrid .jqgrow")).click(function (event) {
						if ($._data(event.target,'events') != undefined && $._data(event.target,'events').mouseout != undefined) {
							$(">td:eq(3)",$("#myJqGrid .jqgrow")).off('mouseout');
						}
						else{
							$(">td:eq(3)",$("#myJqGrid .jqgrow")).mouseout(function (event) {
							    $('#logItems').empty().hide();
							});
						}
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