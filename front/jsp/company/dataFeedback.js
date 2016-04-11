/**
 * 
 *///数据反馈
jQuery(function($){
			checkChildWindow();
			$(window).bind('resize', function() {
				$("#myJqGrid").jqGrid( 'setGridWidth', $(window).width()-1);
			});
			
			caluse_jqgrid_fun = function(lib_id){
				if(!lib_id){
					return;
				}
				invockeService('datafeedback.select.desc.service',{lib_id:lib_id}, function(data, isSucess){
					if(!isSucess){
						return;
					}
					$("#my_clause_JqGrid").clearGridData();
					$("#my_clause_JqGrid")[0].addJSONData(data);
				});
			};
			var dataFeed =function(){
				var grid = $('#myJqGrid');
				var keys = grid.jqGrid("getGridParam","selarrrow"); //selarrrowduoti
				 if(!keys || keys.length <= 0){
				    	xAlert("请选择要需要操作的行");
						return;
					}else if(keys.length > 1){
						xAlert("一次只能选择一项，请重新选择");
						return;
				    }else{
				    	var key = grid.jqGrid("getGridParam","selrow"); //单条
				    	var rowData = $("#myJqGrid").jqGrid("getRowData",key);
				    	var lib_id= rowData.lib_id;
				    	caluse_jqgrid_fun(lib_id);
						//loadDivData(data.rows,'.modal-body form');
						$("#documentor_name").text(rowData.documentor_name);
						$("#company_name").text(rowData.company_name);
						$("#lib_aliase").text(rowData.lib_aliase);
						$('#dataFeedDialog .modal-header h4').text('采集进度');
						$('#dataFeedDialog').modal({backdrop:false,show:true});
						$("#my_clause_JqGrid").jqGrid( 'setGridWidth', $('#dataFeedDialog .modal-body').width()-40);
				    }
			};
			$("#my_clause_JqGrid").jqGrid({
				datatype: 'json',
				height:'auto',
			    colNames:['序号','案卷数量','条目数量','系统表格数量','电子文件数量','总体进度(%)','网卡MAC','上报日期'],   
			    colModel:[
				  {name:'xuhao', index:'xuhao',width:'20px', align:'center'},
				  {name:'foldertemplate_num', index:'foldertemplate_num',width:'20px',search:false},
				  {name:'entryfile_num', index:'entryfile_num',width:'20px',search:false},
				  {name:'formsfiles_num', index:'formsfiles_num',width:'20px',search:false},
				  {name:'files_num', index:'files_num',width:'20px',search:false},
				  {name:'progress', index:'progress',width:'20px',search:false},
				  {name:'mac', index:'mac',width:'30px',search:false},
				  {name:'create_date', index:'create_date',width:'40px',search:false,formatter:jqGridFormatDate,dataInit : function (elem) {$(elem).datetimepicker({format:'yyyy-mm-dd hh:ii'});}},
  				],
	               cellEdit:false,
	               loadonce: true, 
	               rowNum:12,
				   rowList:[12,18,50,100], 
				   loadonce: true,
				   cellEdit:false,
				   viewrecords: true,//定义是否在导航条上显示总的记录数
				   multiselect : true,
				   multiboxonly: true//一次性加载	
			});
			
			$("#myJqGrid").jqGrid({
			    datatype:function(){setJqgridDataFunc('datafeedback.select.list.service');},
			    width:($(window).width()-1),//有7px的border
				height:'auto',
				autowidth: false,
				shrinkToFit: true,
				forceFit:false,
				hidegrid:false,
			    caption:"采集进度",
			    colNames:['进展序号','资料库ID','资料员名称','企业名称','资料库名称','案卷数量','条目数量','系统表格数量','电子文件数量','总体进度(%)','网卡MAC','上报日期','IP地址'],   
			    colModel :[
					{name:'report_id', index:'report_id', key:true, hidden:true},
					{name:'lib_id', index:'lib_id', hidden:true},
					{name:'documentor_name', index:'documentor_name', width:'70px', search:true},
					{name:'company_name', index:'company_name',width:'90px',search:true},
					{name:'lib_aliase', index:'lib_aliase',width:'80px',search:true},
				    {name:'foldertemplate_num', index:'foldertemplate_num',width:'60px',search:false},
				    {name:'entryfile_num', index:'entryfile_num',width:'60px', align:'center',search:false},
				    {name:'formsfiles_num', index:'formsfiles_num',width:'60px',search:false},
				    {name:'files_num', index:'files_num',width:'60px',search:false},
				    {name:'progress', index:'progress',width:'60px',search:false},
				    {name:'mac', index:'mac',width:'60px',search:false},
				    {name:'create_date', index:'create_date',width:'60px',align:'center',search:false,formatter:jqGridFormatDate,dataInit : function (elem) {$(elem).datetimepicker({format:'yyyy-mm-dd hh:ii'});}},
				    {name:'ip', index:'ip',width:'90px',search:false}
				  ],
			    pager: "#myJqGridPager",
			    rowNum:12,
			    rowList:[12,18,50,100], 
				loadonce: true,
				cellEdit:false,
			    viewrecords: true,//定义是否在导航条上显示总的记录数
				multiselect : true,
				multiboxonly: true
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
			).navButtonAdd("#myJqGridPager",{
				position:"last",
				title:"数据对比",
				caption:"",
				buttonicon:"fa fa-table purple",
				onClickButton: dataFeed
			});
			setJqgridPageIcon();
			$('.ui-jqgrid-titlebar>.ui-jqgrid-title').before(
				'<div class="ui-pg-div pull-right grid-tool-bar">'+
				'<span data-toggle="tooltip" data-placement="bottom" data-original-title="多条件查找" id="title_btn_search"><i class="ui-icon fa fa-search orange"></i></span>'+
				'<span data-toggle="tooltip" data-placement="bottom" data-original-title="重新载入数据" id="title_btn_refresh"><i class="ui-icon fa fa-refresh green"></i></span>'+
				'<span data-toggle="tooltip" data-placement="bottom" data-original-title="数据对比" id="title_btn_dataFeed"><i class="ui-icon fa fa-table purple"></i></span>'+
				'</div>'
			);
			$('#title_btn_refresh').on('click', function(){
				$("#myJqGrid").trigger("reloadGrid");
			});
			$('#title_btn_search').on('click', function(){
				$("#myJqGrid").jqGrid("searchGrid");
			});
			$('#title_btn_dataFeed').on('click', dataFeed);
			
			$('[data-toggle="tooltip"]').tooltip({container:'body'});
		});