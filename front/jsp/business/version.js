/**
 * 版本管理
 */
jQuery(function($){
	checkChildWindow();
	$(window).bind('resize', function() { 	
		$("#myJqGrid").jqGrid( 'setGridWidth', $(window).width()-1);
	}); 
	
	setCurrentVersion = function(chk,id){
		if(!id || !chk){
			alert("版本标识错误，请重新刷新页面后再试！");
			return;
		}
		xConfirm("你确定要立刻更改版本状态吗？",function(flag){
	    	if(!flag){
	    		$(chk).prop("checked", !chk.checked);
	    		return;
	    	}
	    	var dt = {version_id:id,is_current:chk.checked?'1':'0'}; 
			invockeService("busi.version.current.update.service",dt);
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
	viewPackVersion = function(pack_id){
		showDialog("packVersionView.jsp","packVersionViewDialog",function(dialog){
			invockeService('busi.version.pack.get.service',{pack_id:pack_id}, function(data, isSucess){
				if(!isSucess){
					return;
				}
				loadDivData(data.pack,dialog.find('.modal-body'));
				var fileid = data.pack.version_file_id || null;
				if(fileid){
					dialog.find('#version_file_name').attr('href','netdomain.sys_diskfile_download?dirtype=/&fileid=pack/'+fileid);
				}
			});	
    	});
	};
	
	var 
	getIsCurrentList = function(){
		return " :全部;0:否;1:是";
	},
	isCurrentFormat = function(cellvalue, options, rowObject ){
		var id = rowObject['version_id'], status = rowObject['version_status'];
		if(status * 1 < 0){//非正常版本
			return "否";
		}
		if(!cellvalue || cellvalue == '0'){
			return "<lable><input class='ace ace-switch ace-switch-3' type='checkbox' onclick='setCurrentVersion(this,"+id+");'><span class='lbl'></span></lable>";
		}
		else if(cellvalue == '1'){
			return "<lable><input checked class='ace ace-switch ace-switch-3' type='checkbox' onclick='setCurrentVersion(this,"+id+");'><span class='lbl'></span></lable>";
		}
	},
	getVersionStatusList = function(){
		return "0:正常;-1:错误:库不存在;-2:错误:版本已存在;-3:错误:写入版本信息错误;-99:版本数据错误";
	},
	versionNameFormatter = function(cellvalue, options, rowObject){
		var id = rowObject['version_id'], fs_id = rowObject['fs_id'] || '', status = rowObject['version_status'];
		if(status * 1 < 0){//非正常版本
			return cellvalue;
		}
		
		if(!cellvalue){
			return "";
		}
		return "<a href='versionLibrary.jsp?version_id="+id+"&fs_id="+fs_id+"' class='hand'>"+cellvalue+"</a>";
	},
	configVersionFormatter = function(cellvalue, options, rowObject){
		if(!cellvalue){
			return "";
		}
		return "<a onclick='viewRuleVersion(\""+rowObject['param_version_id']+"\")' class='hand'>"+cellvalue+"</a>";
	},
	packVersionFormatter = function(cellvalue, options, rowObject){
		if(!cellvalue){
			return "";
		}
		var buff = [];
		$(cellvalue).each(function(){
			buff.push("<a onclick='viewPackVersion(\""+this['pack_id']+"\")' class='hand'>"+this.version_num+"</a>");
		});
		return buff.join(" ");
	};
	$("#myJqGrid").jqGrid({
		datatype: function(){setJqgridDataFunc('busi.version.query.service')},
		width:"100%",
		height:'auto',
		autowidth: true,
		shrinkToFit: true,
	    caption:"档案库版本管理",
	    colNames:['版本序号','当前版本','版本号','版本名称','版本说明','状态','参数规则','增量版本','创建日期','创建人','最近修改日期','最近修改人'],   
	    colModel :[
		  {name:'version_id', index:'version_id', key:true, hidden:true,sortable:false},
		  {name:'is_current', index:'is_current',width:'50px', search:true, formatter:isCurrentFormat, stype:'select', searchoptions: {searchhidden: true, sopt: ['eq'], value:getIsCurrentList()}},
		  {name:'version_num', index:'version_num',width:'70px', search:true,searchoptions: {sopt: ['cn', 'eq']}},
		  {name:'version_name', index:'version_name',formatter:versionNameFormatter, search:true,searchoptions: {sopt: ['cn', 'eq']}},
		  {name:'version_memo', index:'version_memo',search:true,searchoptions: {sopt: ['cn', 'eq']}},
		  {name:'version_status', index:'version_status',width:'50px', search:true, formatter:'select',editoptions:{value:getVersionStatusList()}, stype:"select", searchoptions: {searchhidden: true, sopt: ['eq'],value:getVersionStatusList()}},
		  {name:'config_version', index:'config_version',search:false,formatter:configVersionFormatter, searchoptions: {sopt: ['cn', 'eq']}},
		  {name:'pack_version', index:'pack_version',search:false,formatter:packVersionFormatter, searchoptions: {sopt: ['cn', 'eq']}},
		  {name:'create_date', index:'create_date',hidden:true,width:'70px',search:true,formatter:jqGridFormatDate,searchoptions: { sopt:['ge','le'],dataInit : function (elem) {$(elem).datetimepicker({language:'zh-CN',autoclose: 1,format:'yyyy-mm-dd hh:ii'});}}},
		  {name:'create_user_name', index:'create_user_name',hidden:true,width:'40px',search:true,searchoptions: {sopt: ['eq', 'cn']}},
		  {name:'last_date', index:'last_date',width:'70px',search:true,formatter:jqGridFormatDate,searchoptions: { sopt:['ge','le'],dataInit : function (elem) {$(elem).datetimepicker({language:'zh-CN',autoclose: 1,format:'yyyy-mm-dd hh:ii'});}}},
		  {name:'last_user_name', index:'last_user_name',width:'40px',search:true,searchoptions: {sopt: ['eq', 'cn']}}],
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
			edit: true, editicon : 'fa fa-pencil blue',edittitle:'修改版本信息',
			add: false, addicon : 'fa fa-plus-circle purple',addtitle:'新建版本',
			del: true, delicon : 'fa fa-trash-o red',deltitle:'删除版本',
			search: true, searchicon : 'fa fa-search orange',searchtitle:'多条件查找',
			refresh: true, refreshicon : 'fa fa-refresh green',refreshtitle:'重新载入数据',
			view: false, viewicon : 'fa fa-search-plus grey',viewtitle:'查看详细',
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
					invockeServiceSync('busi.version.get.service',{version_id:key}, function(data, isSucess){
						if(!isSucess){
							return;
						}
						loadDivData(data.version,'#updateVersionModalDialog .modal-body form');
						$('#updateVersionModalDialog').modal({backdrop:false,show:true});
						if($('#param_version_id option').length <= 0){
							loadPKList("pklist.fms_config_version.service","param_version_id",function(){
								$('#param_version_id').val(data.version['param_version_id']);
							});
						}
					});	
			    }
			},
			delfunc: function(){
				var grid = $('#myJqGrid'),keys = grid.jqGrid("getGridParam","selarrrow");
				if(!keys || keys.length <= 0){
					xAlert('请选择需要删除的版本');
					return;
			    }
				xConfirm("你确定要删除当前选择的版本吗？",function(flag){
			    	if(!flag){
			    		return;
			    	}
			    	var len = keys.length;  
					for(var i=0; i<len ;i++) {
						invockeServiceSync('busi.version.delete.service',{version_id:keys[0]}, function(data, isSucess){
							if(!isSucess){
								return;
							}
							xMsg('删除版本成功');
							
						});	
						grid.jqGrid("delRowData", keys[0]);  
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
	);
	
	$('#update_version_button').on("click", function(){
		if (!myValidation("#updateVersionModalDialog .modal-body form")) {
	        return false;
        }
		var dt = packData("#updateVersionModalDialog .modal-body form");
	    invockeServiceSync('busi.version.update.service', dt, function(data, isSucess){
			if(!isSucess){
				return;
			}
			
			$('#updateVersionModalDialog').modal('hide');
			xMsg("修改版本信息成功");
		});
	});
	
	setJqgridPageIcon();
	$('[data-toggle="tooltip"]').tooltip({container:'body'});
});