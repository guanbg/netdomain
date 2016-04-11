/**
 * 规则版本管理
 */
jQuery(function($){
	checkChildWindow();
	$(window).bind('resize', function() { 	
		$("#myJqGrid").jqGrid( 'setGridWidth', $(window).width()-1);
	}); 
	
	setApply = function(chk,id){
		if(!id || !chk){
			alert("规则标识错误，请重新刷新页面后再试！");
			return;
		}
		xConfirm("你确定要立刻更改状态吗？",function(flag){
	    	if(!flag){
	    		$(chk).prop("checked", !chk.checked);
	    		return;
	    	}
	    	var dt = {param_version_id:id,version_status:chk.checked?'0':'1'}; 
			invockeService("busi.configrule.status.update.service",dt);
		});
	};
	viewRuleVersion = function(param_version_id){
		showDialog("ruleVersionView.jsp","ruleVersionViewDialog",function(dialog){
			invockeService('busi.configrule.detail.query.service',{param_version_id:param_version_id}, function(data, isSucess){
				if(!isSucess){
					return;
				}
				loadDivData(data.version,dialog.find('.config_version_info'));
				var table = $('#businessrule_view'),tmpl="<tr><td>{rulename}</td><td><span class='label label-warning arrowed arrowed-right'>{ruleversion}</span></td></tr>";
				table.empty();
				$(data.businessrule).each(function(){
					table.append(parserTemplate(tmpl,this));
				});
			});
			
			$('#configTree').removeData('tree');
			$('#configTree .tree-folder-header').off('click');
			$('#configTree .tree-item').off('click');
			$('#configTree').off('click.fu.tree');
			$('#configTree').off('click');
			$('#configTree').empty();
			$('#configTree').gbg_tree({//树初始化
				loadingHTML:'<div class="tree-loading"><i class="ace-icon fa fa-refresh fa-spin blue"></i></div>',
				'unselected-icon': 'fa fa-square-o',
				multiSelect: false,
				selectable: false,
				dataSource: new TreeDataSource({rootService:"busi.configrule.version.tree.query.service",childService:"busi.configrule.version.tree.query.service",condition:{param_version_id:param_version_id}})
			});
    	});
	};
	
	var 
	getIsApplyList = function(){
		return "0:是;1:否";
	},
	isApplyFormat = function(cellvalue, options, rowObject ){
		var id = rowObject['param_version_id'], status = rowObject['is_apply'];
		if(!status || status == '0'){//可选择
			return "<lable><input checked class='ace ace-switch ace-switch-3' type='checkbox' onclick='setApply(this,"+id+");'><span class='lbl'></span></lable>";
		}
		else if(status == '1'){//不可选择
			return "<lable><input class='ace ace-switch ace-switch-3' type='checkbox' onclick='setApply(this,"+id+");'><span class='lbl'></span></lable>";
		}
	},
	versionNameFormatter = function(cellvalue, options, rowObject){
		if(!cellvalue){
			return "";
		}
		return "<a onclick='viewRuleVersion(\""+rowObject['param_version_id']+"\")' class='hand'>"+cellvalue+"</a>";//ruleVersionView.jsp?param_version_id=
	};
	$("#myJqGrid").jqGrid({
		datatype: function(){setJqgridDataFunc('busi.configrule.version.query.service')},
		width:"100%",
		height:'auto',
		autowidth: true,
		shrinkToFit: true,
	    caption:"参数规则管理",
	    colNames:['规则序号','应用','规则编号','规则名称','备注','创建日期','创建人','最近修改日期','最近修改人','顺序'],   
	    colModel :[
		  {name:'param_version_id', index:'param_version_id', key:true, hidden:true,sortable:false},
		  {name:'is_apply', index:'is_apply',width:'50px', search:true, formatter:isApplyFormat, stype:'select', searchoptions: {searchhidden: true, sopt: ['eq'], value:getIsApplyList()}},
		  {name:'version_num', index:'version_num',width:'70px', search:true,searchoptions: {sopt: ['cn', 'eq']}},
		  {name:'version_name', index:'version_name',formatter:versionNameFormatter, search:true,searchoptions: {sopt: ['cn', 'eq']}},
		  {name:'version_memo', index:'version_memo',search:true,searchoptions: {sopt: ['cn', 'eq']}},
		  {name:'create_date', index:'create_date',width:'90px',search:true,formatter:jqGridFormatDate,searchoptions: { sopt:['ge','le'],dataInit : function (elem) {$(elem).datetimepicker({language:'zh-CN',autoclose: 1,format:'yyyy-mm-dd hh:ii'});}}},
		  {name:'create_user_name', index:'create_user_name',width:'50px',search:true,searchoptions: {sopt: ['eq', 'cn']}},
		  {name:'last_date', index:'last_date',width:'90px',search:true,formatter:jqGridFormatDate,searchoptions: { sopt:['ge','le'],dataInit : function (elem) {$(elem).datetimepicker({language:'zh-CN',autoclose: 1,format:'yyyy-mm-dd hh:ii'});}}},
		  {name:'last_user_name', index:'last_user_name',width:'50px',search:true,searchoptions: {sopt: ['eq', 'cn']}},
		  {name:'disp_order', index:'disp_order', hidden:true,sortable:false}],
	    pager: "#myJqGridPager",
	    rowNum:12,
	    rowList:[10,12,20,50,100], 
	    altRows:true,
		loadonce: true,
		cellEdit:false,
	    viewrecords: true,//定义是否在导航条上显示总的记录数
		multiselect : true,
		multiboxonly: true
  	}).navGrid('#myJqGridPager',
		{
			alertcap:"请选中您要操作的行",alerttext:"您还没有选中行，请选择后再重新操作！",
			edit: true, editicon : 'fa fa-pencil blue',edittitle:'修改规则信息',
			add: true, addicon : 'fa fa-plus-circle purple',addtitle:'新建参数规则',
			del: true, delicon : 'fa fa-trash-o red',deltitle:'删除参数规则',
			search: true, searchicon : 'fa fa-search orange',searchtitle:'多条件查找',
			refresh: true, refreshicon : 'fa fa-refresh green',refreshtitle:'重新载入数据',
			view: false, viewicon : 'fa fa-search-plus grey',viewtitle:'查看详细',
			addfunc:function(){
				showDialog("ruleVersionAdd.jsp","ruleVersionAddDialog",function(dialog){
	        		;
	        	});
			},
			editfunc: function(){
				var grid = $('#myJqGrid'),keys = grid.jqGrid("getGridParam","selarrrow"); 
			    if(!keys || keys.length <= 0){
			    	xAlert("请选择要需要操作的行");
					return;
				}else if(keys.length > 1){
					xAlert("一次只能修改一项，请重新选择");
					return;
			    }else{
					var key = grid.jqGrid("getGridParam","selrow"); //单条
					var rowData = grid.jqGrid("getRowData",key);
					var dt = {
						param_version_id_upd:rowData['param_version_id'],
						version_num_upd:rowData['version_num'],
						version_name_upd:$(rowData['version_name']).text(),
						version_memo_upd:rowData['version_memo'],
						disp_order_upd:rowData['disp_order']
					};
					showDialog("ruleVersionUpdate.jsp","ruleVersionUpdateDialog",function(dialog){
						loadDivData(dt,dialog.find('.modal-body'));
						//loadDivData(rowData,dialog.find('.modal-body'),function(name,val){return name+'_upd';});
		        	});
			    }
			},
			delfunc: function(){
				var grid = $('#myJqGrid'),keys = grid.jqGrid("getGridParam","selarrrow");
				if(!keys || keys.length <= 0){
					xAlert('请选择需要删除的规则');
					return;
			    }
				
				xConfirm("你确定要删除当前选择的规则吗？",function(flag){
			    	if(!flag){
			    		return;
			    	}
			    	invockeServiceSync('busi.configrule.version.delete.service',{param_version_id:keys}, function(data, isSucess){
						if(!isSucess){
							return;
						}
						xMsg('删除参数规则成功');
						var len = keys.length;  
						for(var i=0; i<len ;i++) {  
							grid.jqGrid("delRowData", keys[0]);  
						}
					});	
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
	);
	
	setJqgridPageIcon();
	$('[data-toggle="tooltip"]').tooltip({container:'body'});
});