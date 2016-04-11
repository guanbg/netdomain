/*
 * 新增业务规则版本
 */
jQuery(function($) {
	var dialog = $('#ruleVersionAddDialog'), busibtn = $('#add_version_button');
	
	dialog.on('hidden.bs.modal', function (e) {
		dialog.find('.modal-body input[id], .modal-body textarea').val('');
		dialog.parent().hide();
		$('#businessrule').multiSelect('deselect_all', {});
		busibtn.prop("disabled",false);
	});
	
	busibtn.click(function(){
		if (!myValidation("#ruleVersionAddDialog .modal-body")) {
	        return false;
        }
		var versions = [], folderVersions = [], versionerr = "";
		$('#rightConfigTree  .tree-folder').each(function (){
			if($('> .tree-folder-content',$(this)).children('.tree-item').length > 1){//忽略有叶子结点的上级节点
				versionerr = "提示："+$('> .tree-folder-header>.tree-folder-name',$(this)).text()+"，分类下只能选择一个版本";
				return;
			}
			if($('> .tree-folder-content',$(this)).children('*').length){//忽略有叶子结点的上级节点
				return;
			}
			var dt = getTreeData($('> .tree-folder-header',$(this)));
			if(!dt.id){
				return;
			}
			folderVersions.push(dt.id);
		});
		$('#rightConfigTree  .tree-item').each(function (){
			var dt = getTreeData($(this));
			if(!dt.id){
				return;
			}
			versions.push(dt.id);
		});
		if(versionerr){
			xAlert(versionerr);
			return;
		}
		if((!versions || versions.length <= 0) && (!folderVersions || folderVersions.length <= 0)){
			xAlert('请选择业务规则版本信息');
			return;
		}
		
	   	busibtn.prop("disabled",true);
	   	
		var dt = {
			version_num:$('#version_num').val(),
			version_name:$('#version_name').val(),
			disp_order:$('#disp_order').val(),
			version_memo:$('#version_memo').val(),
			businessrule:$('#businessrule').val(),
			versions:versions,
			folderVersions:folderVersions
		};
		
		invockeServiceSync('busi.configrule.version.add.service', dt, function(data, isSucess){
			if(!isSucess){
				busibtn.prop("disabled",false);
				return;
			}
			//dialog.modal('hide');
			xMsg("新建业务规则版本成功");
			$("#myJqGrid").trigger("reloadGrid");
		});
	});
	
	loadPKList("pklist.fms_businessrule.service", "businessrule",function(){
		$('#businessrule').multiSelect({
			selectableHeader:"<div class='panel-default'><div class='panel-heading'><h3 class='panel-title'>请选择业务规则版本</h3></div>",
			selectionHeader:"<div class='panel-success'><div class='panel-heading'><h3 class='panel-title'>已选择业务规则版本</h3></div><div>",
			afterSelect: function(value){
				var perfix = $("#businessrule option[value='"+value+"']").text().split('-')[0];
				$($('#businessrule').val()).each(function(i,v){
					if(v == value){
						return;
					}
					var p = $("#businessrule option[value='"+v+"']").text().split('-')[0];
					if(p == perfix){
						$('#businessrule').multiSelect('deselect', v+"");
					}
				});
		  	},
		  	afterDeselect: function(value){
		    	;
		  	}
		});
		$('.ms-container .ms-selectable, .ms-container .ms-selection').css('width','49%');
		$('.ms-container div, .ms-container ul').css('border-radius','0px');
	},true);
	
	var 
	getTreeData = function(node){
		var data;
		$.each(node, function (index, value) {
			data = $(value).data();
		});
		return data;
	},
	toRemoveIcon = function(iconparent){
		if(!iconparent){
			return;
		}
		var
		icon = iconparent.find('i.folder-action');
		icon.removeData();
		icon.data('toggle','tooltip');
		icon.data('placement','left');
		icon.attr('data-toggle','tooltip');
		icon.attr('data-placement','left');
		icon.attr('data-original-title','移除全部');
		icon.removeClass('fa-hand-o-right blue green').addClass('fa-remove red');
		icon.off('click');
		icon.on('click', rightFolderActionHandle);
		
		icon = iconparent.find('i.item-action');
		icon.removeData();
		icon.data('toggle','tooltip');
		icon.data('placement','left');
		icon.attr('data-toggle','tooltip');
		icon.attr('data-placement','left');
		icon.attr('data-original-title','移除该项');
		icon.removeClass('fa-hand-o-right blue green').addClass('fa-remove blue');
		icon.off('click');
		icon.on('click', rightItemActionHandle);
	},
	toAddIcon = function(iconparent){
		if(!iconparent){
			return;
		}
		var
		icon = iconparent.find('i.folder-action');
		icon.removeData();
		icon.data('toggle','tooltip');
		icon.data('placement','left');
		icon.attr('data-toggle','tooltip');
		icon.attr('data-placement','left');
		icon.attr('data-original-title','选择全部');
		icon.removeClass('fa-remove blue red').addClass('fa-hand-o-right blue');
		icon.off('click');
		icon.on('click', leftFolderActionHandle);
		
		icon = iconparent.find('i.item-action');
		icon.removeData();
		icon.data('toggle','tooltip');
		icon.data('placement','left');
		icon.attr('data-toggle','tooltip');
		icon.attr('data-placement','left');
		icon.attr('data-original-title','选择该项');
		icon.removeClass('fa-remove red blue').addClass('fa-hand-o-right green');
		icon.off('click');
		icon.on('click', leftItemActionHandle);
	},
	copyAllFold = function(src, srcType, distTree){
		var srcdata = srcType == 'item'?getTreeData(src):getTreeData($('> .tree-folder-header',src)),
		folddata = srcdata,//getTreeData($('> .tree-folder-header',parentFold)),
		parentFold = src,//srcType == 'item'?src.parent().parent():src.parents('.tree-folder:first'),
		parents = [],
		prevParentFold = src,
		folderHeader = null;
		
		$('.tree-folder-header', distTree).each(function(){
			var dt = getTreeData($(this));
			if(!$.isEmptyObject(dt) && dt.id == folddata.parentid){
				folderHeader = $(this);
				parents.push(folddata.id);
				return true;
			}
		});
		
		if(!folderHeader){
			while(folddata.parentid){
				$('.tree-folder-header', distTree).each(function(){
					var dt = getTreeData($(this));
					if(!$.isEmptyObject(dt) && dt.id == folddata.id){
						folderHeader = $(this);
						parents.push(folddata.id);
						return true;
					}
				});
				
				if(folderHeader){
					break;
				}
				
				parents.push(folddata.id);
				prevParentFold = parentFold;
				
				parentFold = parentFold.parents('.tree-folder:first');
				folddata = getTreeData($('> .tree-folder-header',parentFold));
				if(!folddata || !parentFold || parentFold.find('.tree').length){
					break;
				}
			}
			if(folddata.id){//判断最顶层是否存在
				$('.tree-folder-header', distTree).each(function(){
					var dt = getTreeData($(this));
					if(!$.isEmptyObject(dt) && dt.id == folddata.id){
						folderHeader = $(this);
						parents.push(folddata.id);
						return true;
					}
				});
			}
		}
		
		var parentFoldClone = (folderHeader?prevParentFold:parentFold).clone(true);
		$('.tree-folder', parentFoldClone).each(function (){
			var dt = getTreeData($('> .tree-folder-header',$(this)));
			var flag = false;
			for(var i=0; i<parents.length; i++){
				if(dt.id == parents[i]){
					flag = true;
					break;
				}
			}
			if(!flag){
				$(this).remove();
			}
		});
		$('.tree-item', parentFoldClone).each(function (){
			var dt = getTreeData($(this));
			var flag = false;
			for(var i=0; i<parents.length; i++){
				if(dt.id == parents[i]){
					flag = true;
					break;
				}
			}
			if(!flag){
				$(this).remove();
			}
		});
		
		if(distTree.attr('id') == 'rightConfigTree'){
			toRemoveIcon(parentFoldClone);
		}
		else{
			toAddIcon(parentFoldClone);
		}
		
		if(folderHeader){//目标中存在
			folderHeader.parent().find('>.tree-folder-content').append(parentFoldClone);
		}
		else{
			distTree.append(parentFoldClone);
		}
		
		$('[data-toggle="tooltip"]').tooltip({container:'body'});
		return;
	},
	removeTo = function(src, srcDataId, srcDataParentId, srcType, distTree){
		var notRefresh = false, refreshTree = true;
		if(!src || !src.length || !distTree || !distTree.length){
			return notRefresh;
		}
		if(!srcDataParentId){//顶层节点
			if(srcType == 'item'){//叶子节点
				var isExist = false;
				$('> .tree-item', distTree).each(function (){
					var data = getTreeData($(this));
					if(data && data.id == srcDataId){
						isExist = true;
						return false;
					}
				});
				if(isExist){//如果已经存在，则将需要移动的节点直接移除
					src.remove();
					return notRefresh;
				}
				distTree.append(src);
				return notRefresh;
			}
			else{//目录节点
				var folder;
				$('> .tree-folder', distTree).each(function (){
					var data = getTreeData($('> .tree-folder-header',$(this)));
					if(data && data.id == srcDataId){
						folder = $(this);
						return false;
					}
				});
				if(!folder){//如果目标中不存在此节点，则将节点移动到目标中
					distTree.append(src);
					return notRefresh;
				}
				//如果目标中存在此节点，则清空该节点的所有下级节点，进行当点击展开时自动重新加载
				$('.tree-minus', folder).removeClass('tree-minus').addClass('tree-plus');//暂时写死类名
				var folderContent = folder.children('.tree-folder-content');
				folderContent.hide();
				folderContent.empty();
				
				var dt = $('> .tree-folder-header',folder).data();
				if("children" in dt && dt["children"].length > 0){
					delete dt["children"];
				}
				src.remove();
				return notRefresh;
			}
		}
		
		//非顶层节点处理
		if(srcType == 'item'){//叶子节点
			var data = getTreeData($('> .tree-folder-header',src.parent().parent()));
			var folderHeader;
			$('.tree-folder-header', distTree).each(function (){
				var dt = getTreeData($(this));
				if(dt && dt.id == data.id){
					folderHeader = $(this);
					return false;
				}
			});
			if(!folderHeader){//目标中不存在，则直接移除
				copyAllFold(src, srcType, distTree);
				src.remove();
				return refreshTree;
			}
			var isExist = false;
			$('.tree-item', folderHeader.parent()).each(function (){
				var dt = getTreeData($(this));
				if(dt && dt.id == srcDataId){
					isExist = true;
					return false;
				}
			});
			if(isExist){
				src.remove();
				return notRefresh;
			}
			if($('> .tree-folder-content',folderHeader.parent()).children('*').length){
				$('> .tree-folder-content',folderHeader.parent()).append(src);
			}
			else{//如果没有展示，则清楚缓存数据
				src.remove();
				var dt = folderHeader.data();
				if("children" in dt && dt["children"].length > 0){
					delete dt["children"];
				}
			}
			return notRefresh;
		}else{//目录节点
			var folderHeader;
			$('> .tree-folder-header', distTree).each(function(){
				var data = getTreeData($(this));
				if(data && data.id == srcDataId){
					folderHeader = $(this);
					return false;
				}
			});
			
			if(!folderHeader){//目标中不存在，则直接移除
				copyAllFold(src, srcType, distTree);
				src.remove();
				return refreshTree;
			}
			
			src.remove();
			var folder = folderHeader.parent();
			$('.tree-minus', folder).removeClass('tree-minus').addClass('tree-plus');//暂时写死类名
			var folderContent = folder.children('.tree-folder-content');
			folderContent.hide();
			folderContent.empty();
			
			var dt = folderHeader.data();
			if("children" in dt && dt["children"].length > 0){
				delete dt["children"];
			}
			src.remove();
			return notRefresh;
		}
	},
	removeEmptyFolder = function(folder, tree){
		if(!folder || !folder.length || !tree.has(folder).length){
			return;
		}
		
		if(!$('.tree-folder-content',folder).children().length){
			var parent = folder.parents('.tree-folder:first');
			folder.remove();
			removeEmptyFolder(parent, tree);
		}
	},
	leftItemActionHandle = function(event){
		var item = $(event.target).parent('div'),folder = item.parent().parent(),tdata = getTreeData(item);
		
		$(event.target).tooltip('hide');
		var isRefreshTree = removeTo(item, tdata.id, tdata.parentid, 'item', $('#rightConfigTree'));
		removeEmptyFolder(folder,  $('#leftConfigTree'));
		if(!isRefreshTree){//更新图标和重新挂载事件
			toRemoveIcon($(event.target).parent().parent());
		}
		
		return false;
	},
	leftFolderActionHandle = function(event){
		var folder = $(event.target).parent('div').parent(),parent = folder.parents('.tree-folder:first'),tdata = getTreeData($(event.target).parent('div'));
		
		$(event.target).tooltip('hide');
		var isRefreshTree = removeTo(folder, tdata.id, tdata.parentid, 'folder', $('#rightConfigTree'));
		removeEmptyFolder(parent,  $('#leftConfigTree'));
		if(!isRefreshTree){//更新图标和重新挂载事件
			toRemoveIcon($(event.target).parent().parent());
		}
		
		return false;
	},
	rightItemActionHandle = function(event){
		var item = $(event.target).parent('div'),folder = item.parent().parent(),tdata = getTreeData(item);
		
		$(event.target).tooltip('hide');
		var isRefreshTree = removeTo(item, tdata.id, tdata.parentid, 'item', $('#leftConfigTree'));
		removeEmptyFolder(folder,  $('#rightConfigTree'));
		if(!isRefreshTree){//更新图标和重新挂载事件
			toAddIcon($(event.target).parent().parent());
		}
		return false;
	},
	rightFolderActionHandle = function(event){
		var folder = $(event.target).parent('div').parent(),parent = folder.parents('.tree-folder:first'),tdata = getTreeData($(event.target).parent('div'));
		
		$(event.target).tooltip('hide');
		var isRefreshTree = removeTo(folder, tdata.id, tdata.parentid, 'folder', $('#leftConfigTree'));
		removeEmptyFolder(parent,  $('#rightConfigTree'));
		if(!isRefreshTree){//更新图标和重新挂载事件
			toAddIcon($(event.target).parent().parent());
		}
		return false;
	},
	leftCallback = function(){
		var tree = $('#leftConfigTree');
		$('.item-action', tree).off('click');
		$('.folder-action', tree).off('click');
		
		$('.item-action', tree).on('click', leftItemActionHandle);
		$('.folder-action', tree).on('click', leftFolderActionHandle);
		
		$('[data-toggle="tooltip"]').tooltip({container:'body'});
	},
	rightCallback = function(){
		var tree = $('#rightConfigTree');
		$('.item-action', tree).off('click');
		$('.folder-action', tree).off('click');
		
		$('.item-action', tree).on('click', rightItemActionHandle);
		$('.folder-action', tree).on('click', rightFolderActionHandle);
		
		$('[data-toggle="tooltip"]').tooltip({container:'body'});
	};
	
	$('#leftConfigTree').gbg_tree({//树初始化
		loadingHTML:'<div class="tree-loading"><i class="ace-icon fa fa-refresh fa-spin blue"></i></div>',
		'item-action':'<i class="ace-icon fa fa-hand-o-right green item-action" data-placement="left" data-toggle="tooltip" data-original-title="选择该项"></i>',
	    'folder-action':'<i class="ace-icon fa fa-hand-o-right blue folder-action" data-placement="left" data-toggle="tooltip" data-original-title="选择全部"></i>',
	    'unselected-icon': 'fa fa-square-o',
		multiSelect: false,
		selectable: false,
		dataSource: new TreeDataSource({
			rootService:"busi.config.version.tree.query.service",
			childService:"busi.config.version.tree.query.service", 
			callback:leftCallback
		})
	});
	
	$('#rightConfigTree').gbg_tree({//树初始化
		loadingHTML:'<div class="tree-loading"><i class="ace-icon fa fa-refresh fa-spin blue"></i></div>',
		'item-action':'<i class="ace-icon fa fa-remove blue item-action" data-placement="left" data-toggle="tooltip" data-original-title="移除该项"></i>',
	    'folder-action':'<i class="ace-icon fa fa-remove red folder-action" data-placement="left" data-toggle="tooltip" data-original-title="移除全部"></i>',
	    'unselected-icon': 'fa fa-square-o',
		multiSelect: false,
		selectable: false,
		dataSource: new TreeDataSource({
			rootService:"busi.config.version.subtree.query.service",
			childService:"busi.config.version.subtree.query.service", 
			callback:rightCallback
		})
	});
	
	$('#leftRefreshButton').on('click', function(){
		var tree = $('#leftConfigTree');
		tree.removeData('tree');
		$('.tree-folder-header', tree).off('click');
		$('.tree-item', tree).off('click');
		tree.off('click');
		tree.empty();
		$('#leftConfigTree').gbg_tree({//树初始化
			loadingHTML:'<div class="tree-loading"><i class="ace-icon fa fa-refresh fa-spin blue"></i></div>',
			'item-action':'<i class="ace-icon fa fa-hand-o-right green item-action" data-placement="left" data-toggle="tooltip" data-original-title="选择该项"></i>',
		    'folder-action':'<i class="ace-icon fa fa-hand-o-right blue folder-action" data-placement="left" data-toggle="tooltip" data-original-title="选择全部"></i>',
		    'unselected-icon': 'fa fa-square-o',
			multiSelect: false,
			selectable: false,
			dataSource: new TreeDataSource({
				rootService:"busi.config.version.tree.query.service",
				childService:"busi.config.version.tree.query.service", 
				callback:leftCallback
			})
		});
	});
	
	$('#rightRefreshButton').on('click', function(){
		var tree = $('#rightConfigTree');
		tree.removeData('tree');
		$('.tree-folder-header', tree).off('click');
		$('.tree-item', tree).off('click');
		tree.off('click');
		tree.empty();
		$('#rightConfigTree').gbg_tree({//树初始化
			loadingHTML:'<div class="tree-loading"><i class="ace-icon fa fa-refresh fa-spin blue"></i></div>',
			'item-action':'<i class="ace-icon fa fa-remove green item-action" data-placement="left" data-toggle="tooltip" data-original-title="移除该项"></i>',
		    'folder-action':'<i class="ace-icon fa fa-remove red folder-action" data-placement="left" data-toggle="tooltip" data-original-title="移除全部"></i>',
		    'unselected-icon': 'fa fa-square-o',
			multiSelect: false,
			selectable: false,
			dataSource: new TreeDataSource({
				rootService:"busi.config.version.subtree.query.service",
				childService:"busi.config.version.subtree.query.service", 
				callback:rightCallback
			})
		});
	});
	
	
	$('[data-toggle="tooltip"]').tooltip({container:'body'});
});