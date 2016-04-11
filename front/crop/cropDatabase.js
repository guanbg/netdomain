/*
 * 资料库
 */
jQuery(function($) { cropDatabaseInit = function(){//初始化函数
	$("#softdownload_collapse").on('hidden.bs.collapse', function () {
		$(this).parent().find('.panel-heading>a>i').removeClass('glyphicon-chevron-up').addClass('glyphicon-chevron-down');
	});
	$('#softdownload_collapse').on('shown.bs.collapse', function () {
		$(this).parent().find('.panel-heading>a>i').removeClass('glyphicon-chevron-down').addClass('glyphicon-chevron-up');
	});
	$("#busydb_collapse").on('hidden.bs.collapse', function () {
		$(this).parent().find('.panel-heading>a>i').removeClass('glyphicon-chevron-up').addClass('glyphicon-chevron-down');
	});
	$('#busydb_collapse').on('shown.bs.collapse', function () {
		$(this).parent().find('.panel-heading>a>i').removeClass('glyphicon-chevron-down').addClass('glyphicon-chevron-up');
	});
	$("#createdb_collapse").on('hidden.bs.collapse', function () {
		$(this).parent().find('.panel-heading>a>i').removeClass('glyphicon-chevron-up').addClass('glyphicon-chevron-down');
	});
	$('#createdb_collapse').on('shown.bs.collapse', function () {
		$(this).parent().find('.panel-heading>a>i').removeClass('glyphicon-chevron-down').addClass('glyphicon-chevron-up');
	});
	
	$.ajax({
		url: 'versiondownload.jsonp?url=http://update1.ndsmart.cn/cs/update.xml',
		dataType: 'xml',
		success: function(data){
			var version,exezip,url;
			$(data).find("version").each(function(idx, ele) {
				version = $(ele).find("versionnumber").text();
				exezip = $(ele).find("zip").text();
				
				if(exezip && exezip.length > 0 && exezip=="true"){
					url = "http://update1.ndsmart.cn/cs/"+version+"/"+version+".zip";
					$('#cs_main_zip').attr('href',url);
					$('#cs_version').text(version);
					return false;//跳出循环，因为最新的是第一条
				}
			});
		},
		error: function(){
            xAlert('客户端下载服务器连接不上，暂不能下载文件');
        }
	});
	
	var $table = $('#contractor_database');
	
	getTableData = function(flag) {
		var tableOptions = $table.bootstrapTable('getOptions'),
		rowNum = tableOptions.pageSize || 10,//每页记录数
		page = flag == 'refresh'? 1:tableOptions.pageNumber || 1, // 当前页
		records = flag == 'refresh'? 0:tableOptions.totalRows || 0,//总记录数
		condition = {},//查询条件
		callback = function(data,isSuccess){
			if(!isSuccess){
				return;
			}
			$table.bootstrapTable('load', data);//回填数据
		}
		invockeService('crop.library.query.service', condition, callback, page, rowNum, records);
	};
	actionFormatter  = function(value, row, index) {
		var fmt,isShowProgress,libid=row['lib_id'], ndfile=row['ndfile'], lib_aliase=row['lib_aliase'], fs_names=row['fs_names'];
		if(ndfile && ndfile.length > 0){
			return "<a href='netdomain.sqlite_diskfile_download?fileid="+ndfile+"' class='hand' target='_blank'>[下载]</a><a onclick='libDelete("+libid+",\""+lib_aliase+"\",\""+fs_names+"\");' class='margin-left-10 hand'>[删除]</a>";
		}
		else{
			isShowProgress = false;
			invockeServiceSync('com.platform.cubism.sqlite.ExportStatus.class', {lib_id:libid}, function(data,isSuccess){
    			if(!isSuccess){
    				fmt = "<a onclick='libDelete("+libid+",\""+lib_aliase+"\",\""+fs_names+"\");' class='margin-left-10 hand'>[删除]</a>";
    				return;
    			}
    			if(data.completed == '1'){
    				fmt = "<a onclick='libDownload("+libid+",this);' class='hand' target='_blank'>[下载]</a><a onclick='libDelete("+libid+",\""+lib_aliase+"\",\""+fs_names+"\");' class='margin-left-10 hand'>[删除]</a>";
    				return;
    			}
    			fmt = "<a onclick='libDownload("+libid+",this);' class='hand creating'>[生成中]</a><a onclick='libDelete("+libid+",\""+lib_aliase+"\",\""+fs_names+"\");' class='margin-left-10 hand'>[删除]</a>";
    			isShowProgress = true;
    		});
			
			if(isShowProgress){
				setTimeout(function(){$('.table .creating').trigger('click');},3);
			}
			return fmt;
		}
	};
	
	$table.bootstrapTable();//初始化表格
	getTableData();//加载初始数据
    $table.on('page-change.bs.table',getTableData);//翻页
    
	var btn,progress;
    libDownload = function(libid, self){
    	//xAlert("下载包正在生成中，请稍后再下载");
    	if(!btn || !btn.is($(self))){
    		progress = 100;
    	}
    	btn = $(self);
    	btn.text("[生成中]");
    	
    	getDownloadStatus = function(){
    		invockeService('com.platform.cubism.sqlite.ExportStatus.class', {lib_id:libid}, function(data,isSuccess){
    			if(!isSuccess){
    				return;
    			}
    			if(data.completed == '1'){
    				showProgress(1000, btn.parent(),6,100);
    				xMsg("离线数据包生成完毕，请点击下载按钮进行下载！");
    				getTableData('refresh');
    				return;
    			}
    			showProgress(progress, btn.parent(),6,100);
    	    	progress += 5;
    	    	if(progress > 100){
    	    		progress = 100;
    	    	}
    	    	setTimeout("getDownloadStatus()", 1000*6);//6秒
    		});
    	}
    	getDownloadStatus();
    };
    
    libDelete = function(libid,libaliase,fsnames){
    	xConfirm("你确定要删除资料库吗？",function(flag){
        	if(!flag){
        		return;
        	}
        	invockeServiceSync('crop.library.delete.service', {lib_id:libid,lib_aliase:libaliase,fs_names:fsnames}, function(data,isSuccess){
    			if(!isSuccess){
    				return;
    			}
    			getTableData('refresh');
    			xMsg("删除业务资料库成功");
    		});
    	});
    };
    
    if(!getContractorId() || getContractorId().lenght <= 0){
    	xAlert("参建单位信息已变更，请重新登录后再操作");
    	return;
    }
	loadPKList({svrName:"pklist.contractor_basicinfo.service",svrData:{contractor_id:getContractorId()}},"crop_confirm", null, false);
	invockeServiceSync('crop.library.select.query.service', {}, function(data,isSuccess){
		if(!isSuccess || !data.version){
			return;
		}
		
		var $pklist = $('#library_selected'),$optgroup,vid,vname;
		$(data.version).each(function(){
			vid = this.version_id;
			vname = this.version_name;
			$optgroup = $("<optgroup label='"+this.version_name+"'></optgroup>");
			$(this.library).each(function(){
				$optgroup.append("<option value='"+this.value+"' data-version='"+vid+"' data-versionname='"+vname+this.text+"'>"+this.text+"</option>"); 
			});
			$pklist.append($optgroup);
		});
	});
	
	var global_version_id = function(){return $('#library_selected').find("option:selected").data('version')},
	global_version_name = function(){return $('#library_selected').find("option:selected").data('versionname')},
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
		var rootId = $('#library_selected').val(),
		srcdata = srcType == 'item'?getTreeData(src):getTreeData($('> .tree-folder-header',src)),
		folddata = srcdata,//getTreeData($('> .tree-folder-header',parentFold)),
		parentFold = src,//srcType == 'item'?src.parent().parent():src.parents('.tree-folder:first'),
		parents = [rootId],
		prevParentFold = src,
		folderHeader = null;
		
		$('.tree-folder-header', distTree).each(function(){
			var dt = getTreeData($(this));
			if(!$.isEmptyObject(dt) && dt.id == folddata.parentid){
				folderHeader = $(this);
				rootId = dt.id;
				return true;
			}
		});
		
		if(!folderHeader){
			while(rootId != folddata.parentid){
				$('.tree-folder-header', distTree).each(function(){
					var dt = getTreeData($(this));
					if(!$.isEmptyObject(dt) && dt.id == folddata.id){
						folderHeader = $(this);
						rootId = dt.id;
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
		
		if(distTree.attr('id') == 'rightLibraryTree'){
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
		if(!srcDataParentId || srcDataParentId==$('#library_selected').val()){//顶层节点
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
		var isRefreshTree = removeTo(item, tdata.id, tdata.parentid, 'item', $('#rightLibraryTree'));
		removeEmptyFolder(folder,  $('#leftLibraryTree'));
		if(!isRefreshTree){//更新图标和重新挂载事件
			toRemoveIcon($(event.target).parent().parent());
		}
		
		return false;
	},
	leftFolderActionHandle = function(event){
		var folder = $(event.target).parent('div').parent(),parent = folder.parents('.tree-folder:first'),tdata = getTreeData($(event.target).parent('div'));
		
		$(event.target).tooltip('hide');
		var isRefreshTree = removeTo(folder, tdata.id, tdata.parentid, 'folder', $('#rightLibraryTree'));
		removeEmptyFolder(parent,  $('#leftLibraryTree'));
		if(!isRefreshTree){//更新图标和重新挂载事件
			toRemoveIcon($(event.target).parent().parent());
		}
		
		return false;
	},
	rightItemActionHandle = function(event){
		var item = $(event.target).parent('div'),folder = item.parent().parent(),tdata = getTreeData(item);
		
		$(event.target).tooltip('hide');
		var isRefreshTree = removeTo(item, tdata.id, tdata.parentid, 'item', $('#leftLibraryTree'));
		removeEmptyFolder(folder,  $('#rightLibraryTree'));
		if(!isRefreshTree){//更新图标和重新挂载事件
			toAddIcon($(event.target).parent().parent());
		}
		return false;
	},
	rightFolderActionHandle = function(event){
		var folder = $(event.target).parent('div').parent(),parent = folder.parents('.tree-folder:first'),tdata = getTreeData($(event.target).parent('div'));
		
		$(event.target).tooltip('hide');
		var isRefreshTree = removeTo(folder, tdata.id, tdata.parentid, 'folder', $('#leftLibraryTree'));
		removeEmptyFolder(parent,  $('#rightLibraryTree'));
		if(!isRefreshTree){//更新图标和重新挂载事件
			toAddIcon($(event.target).parent().parent());
		}
		return false;
	},
	leftCallback = function(){
		var tree = $('#leftLibraryTree');
		$('.item-action', tree).off('click');
		$('.folder-action', tree).off('click');
		
		$('.item-action', tree).on('click', leftItemActionHandle);
		$('.folder-action', tree).on('click', leftFolderActionHandle);
		
		//$('div.tree-item', tree).addClass('nowrap');
		$('div.tree-folder-name,div.tree-item-name', tree).each(function(){
			$(this).attr("title",$(this).text());
		});
		$('[data-toggle="tooltip"]').tooltip({container:'body'});
	},
	rightCallback = function(){
		var tree = $('#rightLibraryTree');
		$('.item-action', tree).off('click');
		$('.folder-action', tree).off('click');
		
		$('.item-action', tree).on('click', rightItemActionHandle);
		$('.folder-action', tree).on('click', rightFolderActionHandle);
		
		$('[data-toggle="tooltip"]').tooltip({container:'body'});
	};
	
	$('#leftLibraryTree').gbg_tree({//树初始化
		loadingHTML:'<div class="tree-loading"><i class="ace-icon fa fa-refresh fa-spin blue"></i></div>',
		'item-action':'<i class="ace-icon fa fa-hand-o-right green item-action" data-placement="left" data-toggle="tooltip" data-original-title="选择该项"></i>',
	    'folder-action':'<i class="ace-icon fa fa-hand-o-right blue folder-action" data-placement="left" data-toggle="tooltip" data-original-title="选择全部"></i>',
	    'unselected-icon': 'fa fa-square-o',
		multiSelect: false,
		selectable: false,
		dataSource: new TreeDataSource({
			rootService:"crop.library.profession.query.service",
			childService:"crop.library.profession.query.service", 
			condition:{version_id:global_version_id(),id:$('#library_selected').val()}, 
			callback:leftCallback
		})
	});
	
	$('#rightLibraryTree').gbg_tree({//树初始化
		loadingHTML:'<div class="tree-loading"><i class="ace-icon fa fa-refresh fa-spin blue"></i></div>',
		'item-action':'<i class="ace-icon fa fa-remove blue item-action" data-placement="left" data-toggle="tooltip" data-original-title="移除该项"></i>',
	    'folder-action':'<i class="ace-icon fa fa-remove red folder-action" data-placement="left" data-toggle="tooltip" data-original-title="移除全部"></i>',
	    'unselected-icon': 'fa fa-square-o',
		multiSelect: false,
		selectable: false,
		dataSource: new TreeDataSource({
			rootService:"crop.library.profession2.query.service",
			childService:"crop.library.profession2.query.service", 
			condition:{version_id:global_version_id(),id:$('#library_selected').val()}, 
			callback:rightCallback
		})
	});
	
	$('#leftRefreshButton').on('click', function(){
		var tree = $('#leftLibraryTree');
		tree.removeData('tree');
		$('.tree-folder-header', tree).off('click');
		$('.tree-item', tree).off('click');
		tree.off('click');
		tree.empty();
		$('#leftLibraryTree').gbg_tree({//树初始化
			loadingHTML:'<div class="tree-loading"><i class="ace-icon fa fa-refresh fa-spin blue"></i></div>',
			'item-action':'<i class="ace-icon fa fa-hand-o-right green item-action" data-placement="left" data-toggle="tooltip" data-original-title="选择该项"></i>',
		    'folder-action':'<i class="ace-icon fa fa-hand-o-right blue folder-action" data-placement="left" data-toggle="tooltip" data-original-title="选择全部"></i>',
		    'unselected-icon': 'fa fa-square-o',
			multiSelect: false,
			selectable: false,
			dataSource: new TreeDataSource({
				rootService:"crop.library.profession.query.service",
				childService:"crop.library.profession.query.service", 
				condition:{version_id:global_version_id(),id:$('#library_selected').val()}, 
				callback:leftCallback
			})
		});
	});
	
	$('#rightRefreshButton').on('click', function(){
		var tree = $('#rightLibraryTree');
		tree.removeData('tree');
		$('.tree-folder-header', tree).off('click');
		$('.tree-item', tree).off('click');
		tree.off('click');
		tree.empty();
		$('#rightLibraryTree').gbg_tree({//树初始化
			loadingHTML:'<div class="tree-loading"><i class="ace-icon fa fa-refresh fa-spin blue"></i></div>',
			'item-action':'<i class="ace-icon fa fa-remove green item-action" data-placement="left" data-toggle="tooltip" data-original-title="移除该项"></i>',
		    'folder-action':'<i class="ace-icon fa fa-remove red folder-action" data-placement="left" data-toggle="tooltip" data-original-title="移除全部"></i>',
		    'unselected-icon': 'fa fa-square-o',
			multiSelect: false,
			selectable: false,
			dataSource: new TreeDataSource({
				rootService:"crop.library.profession2.query.service",
				childService:"crop.library.profession2.query.service", 
				condition:{version_id:global_version_id(),id:$('#library_selected').val()}, 
				callback:rightCallback
			})
		});
	});
	
	$('#library_selected').change(function(){
		$('#leftRefreshButton').trigger('click');
		$('#rightRefreshButton').trigger('click');
	});
	
	var getNodeNames = function(folders){
		var names = [];
		if(!folders || folders.length <= 0){
			return names;
		}
		var dt = getTreeData($('> .tree-folder-header',folders));
		if(!dt.id){
			return;
		}
		names.push(dt.name);
		
		if($(folders).find('> .tree-folder-content').children('*').length <= 0){
			return names;
		}
		
		$(folders).find('> .tree-folder-content > .tree-item').each(function (){
			var dt = getTreeData($(this));
			if(!dt.id){
				return;
			}
			names.push(dt.name);
		});
		
		$(folders).find('> .tree-folder-content > .tree-folder').each(function (){
			names = names.concat(getNodeNames($(this)));
		});
		return names;
	};
	
	$('#create_lib_btn').click(function(){
		var library_alias = $('#library_alias').val();
		
		if(!library_alias || library_alias.length <= 0){
			xAlert('请输入资料库名称',$('#library_alias'));
			return;
		}
		if(!global_version_id() || global_version_id().length <= 0){
			xAlert('请选择资料库',$('#library_selected'));
			return;
		}
		
		var libids = [], fsnames = [];
		$('#rightLibraryTree  .tree-folder').each(function (){
			/*if($('> .tree-folder-content',$(this)).children('*').length > 0){
				return;
			}*/
			var dt = getTreeData($('> .tree-folder-header',$(this)));
			if(!dt.id){
				return;
			}
			libids.push(dt.id);
			//fsnames.push(dt.name);
		});
		$('#rightLibraryTree  .tree-item').each(function (){
			var dt = getTreeData($(this));
			if(!dt.id){
				return;
			}
			libids.push(dt.id);
			//fsnames.push(dt.name);
		});
		if(!libids || libids.length <= 0){
			xAlert('请选择专业信息');
			return;
		}
		
		$('#rightLibraryTree >.tree-folder').each(function (){
			var dt = getTreeData($('> .tree-folder-header',$(this)));
			if(!dt.id){
				return;
			}
			if(fsnames && fsnames.length > 0){
				fsnames.push(',');
			}
			fsnames = fsnames.concat(getNodeNames($(this)));
		});
		$('#rightLibraryTree  >.tree-item').each(function (){
			var dt = getTreeData($(this));
			if(!dt.id){
				return;
			}
			fsnames.push(dt.name);
		});
		
		$(this).prop("disabled",true);
		var dt = {
			version_id:global_version_id(),
			lib_aliase:library_alias,
			fs_ids:"'"+libids.join("','")+"'",
			fs_names:fsnames.join(' '),
			crop_confirm:$('#crop_confirm').find("option:selected").text(),
			version_name:global_version_name()
		};
		invockeServiceSync('crop.library.add.service', dt, function(data,isSuccess){
			if(!isSuccess || !data.lib_id){
				$('#create_lib_btn').prop("disabled",false);
				return;
			}
			var libid = data.lib_id;
			invockeServiceSync('com.platform.cubism.sqlite.ExportToSqlite.class',{lib_id:libid},function(data,isSuccess){
				if(!isSuccess){
					invockeServiceSync('crop.library.delete.service', {lib_id:libid});
					return;
				}
				$('#create_lib_btn').prop("disabled",true);
				getTableData('refresh');
				xMsg("创建业务资料库成功");
			});
			//refresh
		});
	});
	
	$('[data-toggle="tooltip"]').tooltip({container:'body'});
}});