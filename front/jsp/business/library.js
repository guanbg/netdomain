/**
 * 档案库
 */
jQuery(function($){
	checkChildWindow();
	
	var versionMenu = function(node){//版本发布
		return {
			label:'发布版本',
			icon:'fa fa-code-fork',
			separator_before:true, 
			submenu:{
				allVersion:{
					label:'发布全量版本',
					icon:'fa fa-object-group',
					action:function(data){
						var inst = $.jstree.reference(data.reference),
						obj = inst.get_node(data.reference);
						
						$('#libraryDialog').modalPopover({placement:'right',target:$(data.reference)}).modalPopover('show');
						$('#libraryDialog>h3').text('发布全量版本');
						$('#library_button').text('发布版本');
						$('#library_button').show();
						
						var topDialog = $('#libraryDialog').position().top;
						if(topDialog <= 0){
							$('#libraryDialog').css('top','10px');
							$('#libraryDialog>div.arrow').css('top',($(data.reference).position().top+40)+'px');
						}
						else{
							$('#libraryDialog>div.arrow').css('top','50%');
						}
						var leftDialog = $('#libraryDialog').position().left;
						if(leftDialog+$('#libraryDialog').width() >= $(window).width()){
							$('#libraryDialog').css('left',$(window).width()-$('#libraryDialog').width()-15);
						}
						
						$('#library_button').unbind();
						$('#library_button').click(function(){
							if (!myValidation("#libraryDialog .popover-content")) {
						        return false;
					        }
							showLoading(180);
							$('#library_button').hide();
							var dt = packData("#libraryDialog .popover-content");
							dt['lib_id'] = obj.id || '';
							//invockeServiceSync
							invockeService('busi.library.version.add.service', dt, function(retdata, isSucess){
								showLoading(0);
								if(!isSucess){
									return;
								}
								if(retdata.ret * 1 < 0){
									xAlert("发布全量版本失败："+retdata.ret+"，请进入版本管理查看并清除后重试！");
								}
								else{
									xMsg('发布全量版本成功');
									$('#close_library_button').trigger('click');
								}
							});
						});
					}
				},
				addVersion:{
					label:'发布增量版本',
					icon:'fa fa-object-ungroup',
					action:function(data){
						showDialog("versionPackAdd.jsp","versionPackDialog",function(dialog){
							$('[data-toggle="tooltip"],[data-original-title]').tooltip({container:'body'});
						});
					}
				}
			}
		};
	},
	libTypeMenu = function(node){
		var menuItems = $.jstree.defaults.contextmenu.items();
		
		var createMenu = menuItems.create;
		delete createMenu.action;
		createMenu.label = "新建";
		createMenu.icon = "fa fa-plus purple";
		
		createMenu['submenu'] = {};
		createMenu.submenu['createLibType'] = {
			label:"新建档案门类",
			icon:"fa fa-sitemap purple",
			action:function(data) {
				var inst = $.jstree.reference(data.reference),
				obj = inst.get_node(data.reference);
			
				$('#libraryDialog').modalPopover({placement:'right',target:$(data.reference)}).modalPopover('show');
				$('#libraryDialog>h3').text('新建档案门类');
				$('#library_button').text('新增');
				
				var topDialog = $('#libraryDialog').position().top;
				if(topDialog <= 0){
					$('#libraryDialog').css('top','10px');
					$('#libraryDialog>div.arrow').css('top',($(data.reference).position().top+40)+'px');
				}
				else{
					$('#libraryDialog>div.arrow').css('top','50%');
				}
				var leftDialog = $('#libraryDialog').position().left;
				if(leftDialog+$('#libraryDialog').width() >= $(window).width()){
					$('#libraryDialog').css('left',$(window).width()-$('#libraryDialog').width()-15);
				}
				
				$('#library_button').unbind();
				$('#library_button').click(function(){
					if (!myValidation("#libraryDialog .popover-content")) {
				        return false;
			        }
					var dt = packData("#libraryDialog .popover-content");
					dt['id_type'] = '0';
					dt['parent_id'] = obj.original['parent_id'] || obj.parent || '';
					invockeService('busi.library.add.service', dt, function(retdata, isSucess){
						if(!isSucess || !retdata.id){
							return;
						}
						inst.create_node(obj, {id:retdata.id,text:dt['fs_code']+dt['fs_name'], type:"libtype"}, "after",$.noop);
						xMsg('新建档案门类成功');
					});
				});
			}
		};
		createMenu.submenu['createSubLibType'] = {
			label:"新建下级门类",
			icon:"fa fa-sitemap blue",
			action:function(data) {
				var inst = $.jstree.reference(data.reference),
				obj = inst.get_node(data.reference);
			
				$('#libraryDialog').modalPopover({placement:'right',target:$(data.reference)}).modalPopover('show');
				$('#libraryDialog>h3').text('新建下级门类');
				$('#library_button').text('新增');
				
				var topDialog = $('#libraryDialog').position().top;
				if(topDialog <= 0){
					$('#libraryDialog').css('top','10px');
					$('#libraryDialog>div.arrow').css('top',($(data.reference).position().top+40)+'px');
				}
				else{
					$('#libraryDialog>div.arrow').css('top','50%');
				}
				var leftDialog = $('#libraryDialog').position().left;
				if(leftDialog+$('#libraryDialog').width() >= $(window).width()){
					$('#libraryDialog').css('left',$(window).width()-$('#libraryDialog').width()-15);
				}
				
				$('#library_button').unbind();
				$('#library_button').click(function(){
					if (!myValidation("#libraryDialog .popover-content")) {
				        return false;
			        }
					var dt = packData("#libraryDialog .popover-content");
					dt['id_type'] = '0';
					dt['parent_id'] = obj.id || '';
					invockeService('busi.library.add.service', dt, function(retdata, isSucess){
						if(!isSucess || !retdata.id){
							return;
						}
						inst.create_node(obj, {id:retdata.id,text:dt['fs_code']+dt['fs_name'], type:"libtype"}, "first",$.noop);
						xMsg('新建下级门类成功');
					});
				});
			}
		};
		createMenu.submenu['createLibrary'] = {
			label:"新建档案库",
			icon:"fa fa-university green",
			separator_before:true,
			action:function(data) {
				var inst = $.jstree.reference(data.reference),
				obj = inst.get_node(data.reference);
			
				$('#libraryDialog').modalPopover({placement:'right',target:$(data.reference)}).modalPopover('show');
				$('#libraryDialog>h3').text('新建档案库');
				$('#library_button').text('新增');
				
				var topDialog = $('#libraryDialog').position().top;
				if(topDialog <= 0){
					$('#libraryDialog').css('top','10px');
					$('#libraryDialog>div.arrow').css('top',($(data.reference).position().top+40)+'px');
				}
				else{
					$('#libraryDialog>div.arrow').css('top','50%');
				}
				var leftDialog = $('#libraryDialog').position().left;
				if(leftDialog+$('#libraryDialog').width() >= $(window).width()){
					$('#libraryDialog').css('left',$(window).width()-$('#libraryDialog').width()-15);
				}
				
				$('#library_button').unbind();
				$('#library_button').click(function(){
					if (!myValidation("#libraryDialog .popover-content")) {
				        return false;
			        }
					var dt = packData("#libraryDialog .popover-content");
					dt['id_type'] = '1';
					dt['parent_id'] = obj.id || '';
					invockeService('busi.library.add.service', dt, function(retdata, isSucess){
						if(!isSucess || !retdata.id){
							return;
						}
						inst.create_node(obj, {id:retdata.id,parentid:dt['parent_id'],text:dt['fs_code']+dt['fs_name'], type:"library"}, "first",$.noop);
						xMsg('新建档案库成功');
					});
				});
			}
		};
		createMenu.submenu['createCommon'] = {
				label:"新建目录",
				icon:"fa fa-folder-o grey",
				separator_before:true,
				action:function(data) {
					var inst = $.jstree.reference(data.reference),
					obj = inst.get_node(data.reference);
				
					$('#libraryDialog').modalPopover({placement:'right',target:$(data.reference)}).modalPopover('show');
					$('#libraryDialog>h3').text('新建');
					$('#library_button').text('新增');
					
					var topDialog = $('#libraryDialog').position().top;
					if(topDialog <= 0){
						$('#libraryDialog').css('top','10px');
						$('#libraryDialog>div.arrow').css('top',($(data.reference).position().top+40)+'px');
					}
					else{
						$('#libraryDialog>div.arrow').css('top','50%');
					}
					var leftDialog = $('#libraryDialog').position().left;
					if(leftDialog+$('#libraryDialog').width() >= $(window).width()){
						$('#libraryDialog').css('left',$(window).width()-$('#libraryDialog').width()-15);
					}
					
					$('#library_button').unbind();
					$('#library_button').click(function(){
						if (!myValidation("#libraryDialog .popover-content")) {
					        return false;
				        }
						var dt = packData("#libraryDialog .popover-content");
						dt['id_type'] = '9';
						dt['parent_id'] = obj.id || '';
						invockeService('busi.library.add.service', dt, function(retdata, isSucess){
							if(!isSucess || !retdata.id){
								return;
							}
							inst.create_node(obj, {id:retdata.id,parentid:dt['parent_id'],text:dt['fs_code']+dt['fs_name'], type:"common"}, "first",$.noop);
							xMsg('新建成功');
						});
					});
				}
			};
		
		var renameMenu = menuItems.rename;
		delete renameMenu.action;
		renameMenu.label = "修改档案库门类信息";
		renameMenu.icon = "fa fa-pencil-square-o";
		renameMenu.action = function(data) {
			var inst = $.jstree.reference(data.reference),
			obj = inst.get_node(data.reference);
		
			$('#libraryDialog').modalPopover({placement:'right',target:$(data.reference)}).modalPopover('show');
			$('#libraryDialog>h3').text('修改档案库门类信息');
			$('#library_button').text('保存');
			
			loadDivData(obj.original,'#libraryDialog .popover-content');
			
			var topDialog = $('#libraryDialog').position().top;
			if(topDialog <= 0){
				$('#libraryDialog').css('top','10px');
				$('#libraryDialog>div.arrow').css('top',($(data.reference).position().top+40)+'px');
			}
			else{
				$('#libraryDialog>div.arrow').css('top','50%');
			}
			var leftDialog = $('#libraryDialog').position().left;
			if(leftDialog+$('#libraryDialog').width() >= $(window).width()){
				$('#libraryDialog').css('left',$(window).width()-$('#libraryDialog').width()-15);
			}
			
			$('#library_button').unbind();
			$('#library_button').click(function(){
				if (!myValidation("#libraryDialog .popover-content")) {
			        return false;
		        }
				var dt = packData("#libraryDialog .popover-content");
				dt['fs_id'] = obj.id || '';
				invockeService('busi.library.update.service', dt, function(data, isSucess){
					if(!isSucess){
						return;
					}
					xMsg('修改档案库门类信息成功');
					inst.refresh();
				});
			});
		};
		
		var removeMenu = menuItems.remove;
		delete removeMenu.action;
		removeMenu.label = "删除档案库门类信息";
		removeMenu.icon = "fa fa-remove red";
		removeMenu.action = function(data) {
			var inst = $.jstree.reference(data.reference),
			obj = inst.get_node(data.reference);
        	
			xConfirm("确定要删除此档案库门类吗？删除后不可恢复",function(flag){
		    	if(!flag){
		    		return;
		    	}
		    	invockeService('busi.library.delete.service', {fs_id:obj.id}, function(data, isSucess){
            		if(!isSucess){
						return;
					}
            		if(inst.is_selected(obj)) {
						inst.delete_node(inst.get_selected());
					}
					else {
						inst.delete_node(obj);
					}
            	});
			});
		};
		
		var editMenu = menuItems.ccp,
		copyMenu = editMenu.submenu.copy,
		cutMenu = editMenu.submenu.cut,
		pasteMenu = editMenu.submenu.paste;
		
		editMenu.label = "复制粘贴";
		editMenu.icon = "fa fa-clone orange";
		
		copyMenu.label = "复制档案库门类";
		copyMenu.icon = "fa fa-files-o blue";
		
		cutMenu.label = "剪切档案库门类";
		cutMenu.icon = "fa fa-scissors red";
		
		pasteMenu.label = "粘贴";
		pasteMenu.icon = "fa fa-clipboard orange";
		
		return menuItems;
	},
	libraryMenu = function(node){
		var menuItems = $.jstree.defaults.contextmenu.items();
		
		var createMenu = menuItems.create;
		delete createMenu.action;
		createMenu.label = "新建";
		createMenu.icon = "fa fa-plus purple";
		
		createMenu['submenu'] = {};
		createMenu.submenu['createLibrary'] = {
			label:"新建档案库",
			icon:"fa fa-university green",
			action:function(data) {
				var inst = $.jstree.reference(data.reference),
				obj = inst.get_node(data.reference);
			
				$('#libraryDialog').modalPopover({placement:'right',target:$(data.reference)}).modalPopover('show');
				$('#libraryDialog>h3').text('新建档案库信息');
				$('#library_button').text('新增');
				
				var topDialog = $('#libraryDialog').position().top;
				if(topDialog <= 0){
					$('#libraryDialog').css('top','10px');
					$('#libraryDialog>div.arrow').css('top',($(data.reference).position().top+40)+'px');
				}
				else{
					$('#libraryDialog>div.arrow').css('top','50%');
				}
				var leftDialog = $('#libraryDialog').position().left;
				if(leftDialog+$('#libraryDialog').width() >= $(window).width()){
					$('#libraryDialog').css('left',$(window).width()-$('#libraryDialog').width()-15);
				}
				
				$('#library_button').unbind();
				$('#library_button').click(function(){
					if (!myValidation("#libraryDialog .popover-content")) {
				        return false;
			        }
					var dt = packData("#libraryDialog .popover-content");
					dt['id_type'] = '1';
					dt['parent_id'] = obj.original['parent_id'] || obj.parent || '';
					invockeService('busi.library.add.service', dt, function(retdata, isSucess){
						if(!isSucess || !retdata.id){
							return;
						}
						inst.create_node(obj, {id:retdata.id,text:dt['fs_code']+dt['fs_name'], type:"library"}, "after",$.noop);
						xMsg('新建档案库成功');
					});
				});
			}
		};
		createMenu.submenu['createSubLibrary'] = {
			label:"新建子档案库",
			icon:"fa fa-university green",
			action:function(data) {
				var inst = $.jstree.reference(data.reference),
				obj = inst.get_node(data.reference);
			
				$('#libraryDialog').modalPopover({placement:'right',target:$(data.reference)}).modalPopover('show');
				$('#libraryDialog>h3').text('新建子档案库信息');
				$('#library_button').text('新增');
				
				var topDialog = $('#libraryDialog').position().top;
				if(topDialog <= 0){
					$('#libraryDialog').css('top','10px');
					$('#libraryDialog>div.arrow').css('top',($(data.reference).position().top+40)+'px');
				}
				else{
					$('#libraryDialog>div.arrow').css('top','50%');
				}
				var leftDialog = $('#libraryDialog').position().left;
				if(leftDialog+$('#libraryDialog').width() >= $(window).width()){
					$('#libraryDialog').css('left',$(window).width()-$('#libraryDialog').width()-15);
				}
				
				$('#library_button').unbind();
				$('#library_button').click(function(){
					if (!myValidation("#libraryDialog .popover-content")) {
				        return false;
			        }
					var dt = packData("#libraryDialog .popover-content");
					dt['id_type'] = '1';
					dt['parent_id'] = obj.id || '';
					invockeService('busi.library.add.service', dt, function(retdata, isSucess){
						if(!isSucess || !retdata.id){
							return;
						}
						inst.create_node(obj, {id:retdata.id,text:dt['fs_code']+dt['fs_name'], type:"library"}, "first",$.noop);
						xMsg('新建档案库成功');
					});
				});
			}
		};
		createMenu.submenu['createProfession'] = {
			label:"新建专业",
			icon:"fa fa-shield blue",
			separator_before:true,
			action:function(data) {
				var inst = $.jstree.reference(data.reference),
				obj = inst.get_node(data.reference);
			
				$('#libraryDialog').modalPopover({placement:'right',target:$(data.reference)}).modalPopover('show');
				$('#libraryDialog>h3').text('新建专业');
				$('#library_button').text('新增');
				
				var topDialog = $('#libraryDialog').position().top;
				if(topDialog <= 0){
					$('#libraryDialog').css('top','10px');
					$('#libraryDialog>div.arrow').css('top',($(data.reference).position().top+40)+'px');
				}
				else{
					$('#libraryDialog>div.arrow').css('top','50%');
				}
				var leftDialog = $('#libraryDialog').position().left;
				if(leftDialog+$('#libraryDialog').width() >= $(window).width()){
					$('#libraryDialog').css('left',$(window).width()-$('#libraryDialog').width()-15);
				}
				
				$('#library_button').unbind();
				$('#library_button').click(function(){
					if (!myValidation("#libraryDialog .popover-content")) {
				        return false;
			        }
					var dt = packData("#libraryDialog .popover-content");
					dt['id_type'] = '2';
					dt['parent_id'] = obj.id || '';
					invockeService('busi.library.add.service', dt, function(retdata, isSucess){
						if(!isSucess || !retdata.id){
							return;
						}
						inst.create_node(obj, {id:retdata.id,text:dt['fs_code']+dt['fs_name'], type:"profession"}, "first",$.noop);
						xMsg('新建专业成功');
					});
				});
			}
		};
		
		var renameMenu = menuItems.rename;
		delete renameMenu.action;
		renameMenu.label = "修改档案库信息";
		renameMenu.icon = "fa fa-pencil-square-o";
		renameMenu.action = function(data) {
			var inst = $.jstree.reference(data.reference),
			obj = inst.get_node(data.reference);
		
			$('#libraryDialog').modalPopover({placement:'right',target:$(data.reference)}).modalPopover('show');
			$('#libraryDialog>h3').text('修改档案库信息');
			$('#library_button').text('保存');
			
			loadDivData(obj.original,'#libraryDialog .popover-content');
			
			var topDialog = $('#libraryDialog').position().top;
			if(topDialog <= 0){
				$('#libraryDialog').css('top','10px');
				$('#libraryDialog>div.arrow').css('top',($(data.reference).position().top+40)+'px');
			}
			else{
				$('#libraryDialog>div.arrow').css('top','50%');
			}
			var leftDialog = $('#libraryDialog').position().left;
			if(leftDialog+$('#libraryDialog').width() >= $(window).width()){
				$('#libraryDialog').css('left',$(window).width()-$('#libraryDialog').width()-15);
			}
			
			$('#library_button').unbind();
			$('#library_button').click(function(){
				if (!myValidation("#libraryDialog .popover-content")) {
			        return false;
		        }
				var dt = packData("#libraryDialog .popover-content");
				dt['fs_id'] = obj.id || '';
				invockeService('busi.library.update.service', dt, function(data, isSucess){
					if(!isSucess){
						return;
					}
					xMsg('修改档案库信息成功');
					inst.refresh();
				});
			});
		};
		
		menuItems['batchUpdate'] = {
			label:"批量修改下级",
			icon:"fa fa-pencil-square",
			action: function (data) {
				var inst = $.jstree.reference(data.reference),
				obj = inst.get_node(data.reference);
				
				select_node_id = obj.id;
	        	select_node_name = obj.text;
			} 
		};
		
		var removeMenu = menuItems.remove;
		delete removeMenu.action;
		removeMenu.label = "删除档案库信息";
		removeMenu.icon = "fa fa-remove red";
		removeMenu.action = function(data) {
			var inst = $.jstree.reference(data.reference),
			obj = inst.get_node(data.reference);
			ref = $('#library_tree').jstree(true),
			sel = ref.get_selected();
        	if(!sel.length) { 
        		return false; 
        	}
        	
			xConfirm("你确定要删除此档案库吗？删除后不可恢复",function(flag){
		    	if(!flag){
		    		return;
		    	}
		    	invockeService('busi.library.delete.service', {fs_id:obj.id}, function(data, isSucess){
            		if(!isSucess){
						return;
					}
            		
                	ref.delete_node(sel);
            	});
			});
		};
		
		var editMenu = menuItems.ccp,
		copyMenu = editMenu.submenu.copy,
		cutMenu = editMenu.submenu.cut,
		pasteMenu = editMenu.submenu.paste;
		
		editMenu.label = "复制粘贴";
		editMenu.icon = "fa fa-clone orange";
		
		copyMenu.label = "复制档案库";
		copyMenu.icon = "fa fa-files-o blue";
		
		cutMenu.label = "剪切档案库";
		cutMenu.icon = "fa fa-scissors red";
		
		pasteMenu.label = "粘贴";
		pasteMenu.icon = "fa fa-clipboard orange";
		
		return menuItems;
		
	},
	professionMenu = function(node){
		var menuItems = $.jstree.defaults.contextmenu.items();
		
		var createMenu = menuItems.create;
		delete createMenu.action;
		createMenu.label = "新建";
		createMenu.icon = "fa fa-plus purple";
		
		createMenu['submenu'] = {};
		createMenu.submenu['createProfession'] = {
			label:"新建专业",
			icon:"fa fa-shield blue",
			action:function(data) {
				var inst = $.jstree.reference(data.reference),
				obj = inst.get_node(data.reference);
			
				$('#libraryDialog').modalPopover({placement:'right',target:$(data.reference)}).modalPopover('show');
				$('#libraryDialog>h3').text('新建专业信息');
				$('#library_button').text('新增');
				
				var topDialog = $('#libraryDialog').position().top;
				if(topDialog <= 0){
					$('#libraryDialog').css('top','10px');
					$('#libraryDialog>div.arrow').css('top',($(data.reference).position().top+40)+'px');
				}
				else{
					$('#libraryDialog>div.arrow').css('top','50%');
				}
				var leftDialog = $('#libraryDialog').position().left;
				if(leftDialog+$('#libraryDialog').width() >= $(window).width()){
					$('#libraryDialog').css('left',$(window).width()-$('#libraryDialog').width()-15);
				}
				
				$('#library_button').unbind();
				$('#library_button').click(function(){
					if (!myValidation("#libraryDialog .popover-content")) {
				        return false;
			        }
					var dt = packData("#libraryDialog .popover-content");
					dt['id_type'] = '2';
					dt['parent_id'] = obj.original['parent_id'] || obj.parent || '';
					invockeService('busi.library.add.service', dt, function(retdata, isSucess){
						if(!isSucess || !retdata.id){
							return;
						}
						inst.create_node(obj, {id:retdata.id,text:dt['fs_code']+dt['fs_name'], type:"profession"}, "after",$.noop);
						xMsg('新建专业成功');
					});
				});
			}
		};
		createMenu.submenu['createSubProfession'] = {
			label:"新建子专业",
			icon:"fa fa-shield blue",
			action:function(data) {
				var inst = $.jstree.reference(data.reference),
				obj = inst.get_node(data.reference);
			
				$('#libraryDialog').modalPopover({placement:'right',target:$(data.reference)}).modalPopover('show');
				$('#libraryDialog>h3').text('新建子专业信息');
				$('#library_button').text('新增');
				
				var topDialog = $('#libraryDialog').position().top;
				if(topDialog <= 0){
					$('#libraryDialog').css('top','10px');
					$('#libraryDialog>div.arrow').css('top',($(data.reference).position().top+40)+'px');
				}
				else{
					$('#libraryDialog>div.arrow').css('top','50%');
				}
				var leftDialog = $('#libraryDialog').position().left;
				if(leftDialog+$('#libraryDialog').width() >= $(window).width()){
					$('#libraryDialog').css('left',$(window).width()-$('#libraryDialog').width()-15);
				}
				
				$('#library_button').unbind();
				$('#library_button').click(function(){
					if (!myValidation("#libraryDialog .popover-content")) {
				        return false;
			        }
					var dt = packData("#libraryDialog .popover-content");
					dt['id_type'] = '2';
					dt['parent_id'] = obj.id || '';
					invockeService('busi.library.add.service', dt, function(retdata, isSucess){
						if(!isSucess || !retdata.id){
							return;
						}
						inst.create_node(obj, {id:retdata.id,text:dt['fs_code']+dt['fs_name'], type:"profession"}, "first",$.noop);
						xMsg('新建专业成功');
					});
				});
			}
		};
		createMenu.submenu['createClassify'] = {
			label:"新建案卷分类",
			icon:"fa fa-th-large pink",
			separator_before:true,
			action:function(data) {
				var inst = $.jstree.reference(data.reference),
				obj = inst.get_node(data.reference);
			
				$('#libraryDialog').modalPopover({placement:'right',target:$(data.reference)}).modalPopover('show');
				$('#libraryDialog>h3').text('新建案卷分类');
				$('#library_button').text('新增');
				
				var topDialog = $('#libraryDialog').position().top;
				if(topDialog <= 0){
					$('#libraryDialog').css('top','10px');
					$('#libraryDialog>div.arrow').css('top',($(data.reference).position().top+40)+'px');
				}
				else{
					$('#libraryDialog>div.arrow').css('top','50%');
				}
				var leftDialog = $('#libraryDialog').position().left;
				if(leftDialog+$('#libraryDialog').width() >= $(window).width()){
					$('#libraryDialog').css('left',$(window).width()-$('#libraryDialog').width()-15);
				}
				
				$('#library_button').unbind();
				$('#library_button').click(function(){
					if (!myValidation("#libraryDialog .popover-content")) {
				        return false;
			        }
					var dt = packData("#libraryDialog .popover-content");
					dt['id_type'] = '3';
					dt['parent_id'] = obj.id || '';
					invockeService('busi.library.add.service', dt, function(retdata, isSucess){
						if(!isSucess || !retdata.id){
							return;
						}
						inst.create_node(obj, {id:retdata.id,text:dt['fs_code']+dt['fs_name'], type:"classify"}, "first",$.noop);
						xMsg('新建案卷分类成功');
					});
				});
			}
		};
		createMenu.submenu['createArchives'] = {
			label:"新建案卷",
			icon:"fa fa-cube orange",
			separator_before:true,
			action:function(data) {
				var inst = $.jstree.reference(data.reference),
				obj = inst.get_node(data.reference);
			
				$('#libraryDialog').modalPopover({placement:'right',target:$(data.reference)}).modalPopover('show');
				$('#libraryDialog>h3').text('新建案卷');
				$('#library_button').text('新增');
				
				var topDialog = $('#libraryDialog').position().top;
				if(topDialog <= 0){
					$('#libraryDialog').css('top','10px');
					$('#libraryDialog>div.arrow').css('top',($(data.reference).position().top+40)+'px');
				}
				else{
					$('#libraryDialog>div.arrow').css('top','50%');
				}
				var leftDialog = $('#libraryDialog').position().left;
				if(leftDialog+$('#libraryDialog').width() >= $(window).width()){
					$('#libraryDialog').css('left',$(window).width()-$('#libraryDialog').width()-15);
				}
				
				$('#library_button').unbind();
				$('#library_button').click(function(){
					if (!myValidation("#libraryDialog .popover-content")) {
				        return false;
			        }
					var dt = packData("#libraryDialog .popover-content");
					dt['id_type'] = '4';
					dt['parent_id'] = obj.id || '';
					invockeService('busi.library.add.service', dt, function(retdata, isSucess){
						if(!isSucess || !retdata.id){
							return;
						}
						inst.create_node(obj, {id:retdata.id,text:dt['fs_code']+dt['fs_name'], type:"archives"}, "first",$.noop);
						xMsg('新建案卷成功');
					});
				});
			}
		};
		
		var renameMenu = menuItems.rename;
		delete renameMenu.action;
		renameMenu.label = "修改专业信息";
		renameMenu.icon = "fa fa-pencil-square-o";
		renameMenu.action = function(data) {
			var inst = $.jstree.reference(data.reference),
			obj = inst.get_node(data.reference);
		
			$('#libraryDialog').modalPopover({placement:'right',target:$(data.reference)}).modalPopover('show');
			$('#libraryDialog>h3').text('修改专业信息');
			$('#library_button').text('保存');
			
			loadDivData(obj.original,'#libraryDialog .popover-content');
			
			var topDialog = $('#libraryDialog').position().top;
			if(topDialog <= 0){
				$('#libraryDialog').css('top','10px');
				$('#libraryDialog>div.arrow').css('top',($(data.reference).position().top+40)+'px');
			}
			else{
				$('#libraryDialog>div.arrow').css('top','50%');
			}
			var leftDialog = $('#libraryDialog').position().left;
			if(leftDialog+$('#libraryDialog').width() >= $(window).width()){
				$('#libraryDialog').css('left',$(window).width()-$('#libraryDialog').width()-15);
			}
			
			$('#library_button').unbind();
			$('#library_button').click(function(){
				if (!myValidation("#libraryDialog .popover-content")) {
			        return false;
		        }
				var dt = packData("#libraryDialog .popover-content");
				dt['fs_id'] = obj.id || '';
				invockeService('busi.library.update.service', dt, function(data, isSucess){
					if(!isSucess){
						return;
					}
					xMsg('修改专业信息成功');
					inst.refresh();
				});
			});
		};
		
		menuItems['batchUpdate'] = {
			label:"批量修改下级",
			icon:"fa fa-pencil-square",
			action: function (data) {
				var inst = $.jstree.reference(data.reference),
				obj = inst.get_node(data.reference);
				
				select_node_id = obj.id;
	        	select_node_name = obj.text;
			} 
		};
		
		var removeMenu = menuItems.remove;
		delete removeMenu.action;
		removeMenu.label = "删除专业信息";
		removeMenu.icon = "fa fa-remove red";
		removeMenu.action = function(data) {
			var inst = $.jstree.reference(data.reference),
			obj = inst.get_node(data.reference);
			ref = $('#library_tree').jstree(true),
			sel = ref.get_selected();
        	if(!sel.length) { 
        		return false; 
        	}
        	
			xConfirm("你确定要删除此专业吗？删除后不可恢复",function(flag){
		    	if(!flag){
		    		return;
		    	}
		    	invockeService('busi.library.delete.service', {fs_id:obj.id}, function(data, isSucess){
            		if(!isSucess){
						return;
					}
            		
                	ref.delete_node(sel);
            	});
			});
		};
		
		var editMenu = menuItems.ccp,
		copyMenu = editMenu.submenu.copy,
		cutMenu = editMenu.submenu.cut,
		pasteMenu = editMenu.submenu.paste;
		
		editMenu.label = "复制粘贴";
		editMenu.icon = "fa fa-clone orange";
		
		copyMenu.label = "复制专业";
		copyMenu.icon = "fa fa-files-o blue";
		
		cutMenu.label = "剪切专业";
		cutMenu.icon = "fa fa-scissors red";
		
		pasteMenu.label = "粘贴";
		pasteMenu.icon = "fa fa-clipboard orange";
		
		return menuItems;
		
	},
	classifyMenu = function(node){
		var menuItems = $.jstree.defaults.contextmenu.items();
		
		var createMenu = menuItems.create;
		delete createMenu.action;
		createMenu.label = "新建";
		createMenu.icon = "fa fa-plus purple";
		
		createMenu['submenu'] = {};
		createMenu.submenu['createClassify'] = {
			label:"新建案卷分类",
			icon:"fa fa-th-large pink",
			action:function(data) {
				var inst = $.jstree.reference(data.reference),
				obj = inst.get_node(data.reference);
			
				$('#libraryDialog').modalPopover({placement:'right',target:$(data.reference)}).modalPopover('show');
				$('#libraryDialog>h3').text('新建案卷分类信息');
				$('#library_button').text('新增');
				
				var topDialog = $('#libraryDialog').position().top;
				if(topDialog <= 0){
					$('#libraryDialog').css('top','10px');
					$('#libraryDialog>div.arrow').css('top',($(data.reference).position().top+40)+'px');
				}
				else{
					$('#libraryDialog>div.arrow').css('top','50%');
				}
				var leftDialog = $('#libraryDialog').position().left;
				if(leftDialog+$('#libraryDialog').width() >= $(window).width()){
					$('#libraryDialog').css('left',$(window).width()-$('#libraryDialog').width()-15);
				}
				
				$('#library_button').unbind();
				$('#library_button').click(function(){
					if (!myValidation("#libraryDialog .popover-content")) {
				        return false;
			        }
					var dt = packData("#libraryDialog .popover-content");
					dt['id_type'] = '3';
					dt['parent_id'] = obj.original['parent_id'] || obj.parent || '';
					invockeService('busi.library.add.service', dt, function(retdata, isSucess){
						if(!isSucess || !retdata.id){
							return;
						}
						inst.create_node(obj, {id:retdata.id,text:dt['fs_code']+dt['fs_name'], type:"classify"}, "after",$.noop);
						xMsg('新建案卷分类成功');
					});
				});
			}
		};
		createMenu.submenu['createSubClassify'] = {
			label:"新建子案卷分类",
			icon:"fa fa-th-large pink",
			action:function(data) {
				var inst = $.jstree.reference(data.reference),
				obj = inst.get_node(data.reference);
			
				$('#libraryDialog').modalPopover({placement:'right',target:$(data.reference)}).modalPopover('show');
				$('#libraryDialog>h3').text('新建子案卷分类信息');
				$('#library_button').text('新增');
				
				var topDialog = $('#libraryDialog').position().top;
				if(topDialog <= 0){
					$('#libraryDialog').css('top','10px');
					$('#libraryDialog>div.arrow').css('top',($(data.reference).position().top+40)+'px');
				}
				else{
					$('#libraryDialog>div.arrow').css('top','50%');
				}
				var leftDialog = $('#libraryDialog').position().left;
				if(leftDialog+$('#libraryDialog').width() >= $(window).width()){
					$('#libraryDialog').css('left',$(window).width()-$('#libraryDialog').width()-15);
				}
				
				$('#library_button').unbind();
				$('#library_button').click(function(){
					if (!myValidation("#libraryDialog .popover-content")) {
				        return false;
			        }
					var dt = packData("#libraryDialog .popover-content");
					dt['id_type'] = '3';
					dt['parent_id'] = obj.id || '';
					invockeService('busi.library.add.service', dt, function(retdata, isSucess){
						if(!isSucess || !retdata.id){
							return;
						}
						inst.create_node(obj, {id:retdata.id,text:dt['fs_code']+dt['fs_name'], type:"classify"}, "first",$.noop);
						xMsg('新建案卷分类成功');
					});
				});
			}
		};
		createMenu.submenu['createArchives'] = {
			label:"新建案卷",
			icon:"fa fa-cube orange",
			separator_before:true,
			action:function(data) {
				var inst = $.jstree.reference(data.reference),
				obj = inst.get_node(data.reference);
			
				$('#libraryDialog').modalPopover({placement:'right',target:$(data.reference)}).modalPopover('show');
				$('#libraryDialog>h3').text('新建案卷');
				$('#library_button').text('新增');
				
				var topDialog = $('#libraryDialog').position().top;
				if(topDialog <= 0){
					$('#libraryDialog').css('top','10px');
					$('#libraryDialog>div.arrow').css('top',($(data.reference).position().top+40)+'px');
				}
				else{
					$('#libraryDialog>div.arrow').css('top','50%');
				}
				var leftDialog = $('#libraryDialog').position().left;
				if(leftDialog+$('#libraryDialog').width() >= $(window).width()){
					$('#libraryDialog').css('left',$(window).width()-$('#libraryDialog').width()-15);
				}
				
				$('#library_button').unbind();
				$('#library_button').click(function(){
					if (!myValidation("#libraryDialog .popover-content")) {
				        return false;
			        }
					var dt = packData("#libraryDialog .popover-content");
					dt['id_type'] = '4';
					dt['parent_id'] = obj.id || '';
					invockeService('busi.library.add.service', dt, function(retdata, isSucess){
						if(!isSucess || !retdata.id){
							return;
						}
						inst.create_node(obj, {id:retdata.id,text:dt['fs_code']+dt['fs_name'], type:"archives"}, "first",$.noop);
						xMsg('新建案卷成功');
					});
				});
			}
		};
		
		var renameMenu = menuItems.rename;
		delete renameMenu.action;
		renameMenu.label = "修改案卷分类信息";
		renameMenu.icon = "fa fa-pencil-square-o";
		renameMenu.action = function(data) {
			var inst = $.jstree.reference(data.reference),
			obj = inst.get_node(data.reference);
		
			$('#libraryDialog').modalPopover({placement:'right',target:$(data.reference)}).modalPopover('show');
			$('#libraryDialog>h3').text('修改案卷分类信息');
			$('#library_button').text('保存');
			
			loadDivData(obj.original,'#libraryDialog .popover-content');
			
			var topDialog = $('#libraryDialog').position().top;
			if(topDialog <= 0){
				$('#libraryDialog').css('top','10px');
				$('#libraryDialog>div.arrow').css('top',($(data.reference).position().top+40)+'px');
			}
			else{
				$('#libraryDialog>div.arrow').css('top','50%');
			}
			var leftDialog = $('#libraryDialog').position().left;
			if(leftDialog+$('#libraryDialog').width() >= $(window).width()){
				$('#libraryDialog').css('left',$(window).width()-$('#libraryDialog').width()-15);
			}
			
			$('#library_button').unbind();
			$('#library_button').click(function(){
				if (!myValidation("#libraryDialog .popover-content")) {
			        return false;
		        }
				var dt = packData("#libraryDialog .popover-content");
				dt['fs_id'] = obj.id || '';
				invockeService('busi.library.update.service', dt, function(data, isSucess){
					if(!isSucess){
						return;
					}
					xMsg('修改案卷分类信息成功');
					inst.refresh();
				});
			});
		};
		
		menuItems['batchUpdate'] = {
			label:"批量修改下级",
			icon:"fa fa-pencil-square",
			action: function (data) {
				var inst = $.jstree.reference(data.reference),
				obj = inst.get_node(data.reference);
				
				select_node_id = obj.id;
	        	select_node_name = obj.text;
			} 
		};
		
		var removeMenu = menuItems.remove;
		delete removeMenu.action;
		removeMenu.label = "删除案卷分类信息";
		removeMenu.icon = "fa fa-remove red";
		removeMenu.action = function(data) {
			var inst = $.jstree.reference(data.reference),
			obj = inst.get_node(data.reference);
			ref = $('#library_tree').jstree(true),
			sel = ref.get_selected();
        	if(!sel.length) { 
        		return false; 
        	}
        	
			xConfirm("你确定要删除此案卷分类吗？删除后不可恢复",function(flag){
		    	if(!flag){
		    		return;
		    	}
		    	invockeService('busi.library.delete.service', {fs_id:obj.id}, function(data, isSucess){
            		if(!isSucess){
						return;
					}
            		
                	ref.delete_node(sel);
            	});
			});
		};
		menuItems['importFiles'] = {
			label:"导入条目",
			icon:"fa fa-sign-in blue",
			action:function(data) {
				var inst = $.jstree.reference(data.reference),
				obj = inst.get_node(data.reference);
				ref = $('#library_tree').jstree(true),
				sel = ref.get_selected();
	        	if(!sel.length) { 
	        		return false; 
	        	}
	        	
	        	$('#importFilesDialog').modalPopover({placement:'right',target:$(data.reference)}).modalPopover('show');
				
				var topDialog = $('#importFilesDialog').position().top;
				if(topDialog <= 0){
					$('#importFilesDialog').css('top','10px');
					$('#importFilesDialog>div.arrow').css('top',($(data.reference).position().top+40)+'px');
				}
				else{
					$('#importFilesDialog>div.arrow').css('top','50%');
				}
				var leftDialog = $('#libraryDialog').position().left;
				if(leftDialog+$('#libraryDialog').width() >= $(window).width()){
					$('#libraryDialog').css('left',$(window).width()-$('#libraryDialog').width()-15);
				}
				
				$('#importFiles_button').unbind();
				$('#importFiles_button').click(function(){
					if (!$("#import_file_id").val()) {
						xAlert("请上传文件");
				        return false;
			        }
					showLoading(180);//180秒后关闭
					var dt = {
						fileid:$("#import_file_id").val(),
						fs_id:obj.id || '',
						servicename:"busi.archivefiles.import"
					};
					invockeService('com.platform.cubism.excel.ImportExcel.class', dt, function(data, isSucess){
						if(!isSucess || !data.batch_number){
							showLoading(0);
							$('#close_importFiles_button').trigger('click');
							return;
						}
						xMsg('正在导入条目信息...',6);
						import_file_upload.reset();
						
						var total = 0, batchNumber = data.batch_number;
						getImportStatus = function(){
							total++;
							
							invockeService("sys.import.status.get.service",{batch_number:batchNumber},function(data, isSucess){
								if(!isSucess){
									showLoading(0);
									$('#close_importFiles_button').trigger('click');
									xAlert("无法查询导入状态，请手动查询导入信息是否成功。");
									return;
								}
								if(!!data && !!data.status && !!data.status.state_code){
									if(data.status.state_code == '00000'){
										showLoading(0);
										$('#close_importFiles_button').trigger('click');
										xMsg('条目信息导入成功！！！');
										inst.refresh();
										return;
									}
									else{
										showLoading(0);
										xMsg(data.status.state_code+" "+data.status.state_desc);
										return;
									}
								}
								else{
									xMsg("正在导入条目信息，请稍后...",6);
									if(total > 10){
										showLoading(0);
										$('#close_importFiles_button').trigger('click');
										xAlert("查询不到导入状态，请手动查询导入信息是否成功。");
										return;
									}
								}
								
								setTimeout("getImportStatus()", 1000*6);//6秒
							});
						}
						setTimeout(getImportStatus,1000*6);//每6秒查询一次导入状态
					});
				});
			}
		};
		
		var editMenu = menuItems.ccp,
		copyMenu = editMenu.submenu.copy,
		cutMenu = editMenu.submenu.cut,
		pasteMenu = editMenu.submenu.paste;
		
		editMenu.label = "复制粘贴";
		editMenu.icon = "fa fa-clone orange";
		
		copyMenu.label = "复制案卷分类";
		copyMenu.icon = "fa fa-files-o blue";
		
		cutMenu.label = "剪切案卷分类";
		cutMenu.icon = "fa fa-scissors red";
		
		pasteMenu.label = "粘贴";
		pasteMenu.icon = "fa fa-clipboard orange";
		
		return menuItems;
		
	},
	archivesMenu = function(node){
		var menuItems = $.jstree.defaults.contextmenu.items();
		
		var createMenu = menuItems.create;
		delete createMenu.action;
		createMenu.label = "新建";
		createMenu.icon = "fa fa-plus purple";
		
		createMenu['submenu'] = {};
		createMenu.submenu['createArchives'] = {
			label:"新建案卷",
			icon:"fa fa-cube orange",
			action:function(data) {
				var inst = $.jstree.reference(data.reference),
				obj = inst.get_node(data.reference);
			
				$('#libraryDialog').modalPopover({placement:'right',target:$(data.reference)}).modalPopover('show');
				$('#libraryDialog>h3').text('新建案卷信息');
				$('#library_button').text('新增');
				
				var topDialog = $('#libraryDialog').position().top;
				if(topDialog <= 0){
					$('#libraryDialog').css('top','10px');
					$('#libraryDialog>div.arrow').css('top',($(data.reference).position().top+40)+'px');
				}
				else{
					$('#libraryDialog>div.arrow').css('top','50%');
				}
				var leftDialog = $('#libraryDialog').position().left;
				if(leftDialog+$('#libraryDialog').width() >= $(window).width()){
					$('#libraryDialog').css('left',$(window).width()-$('#libraryDialog').width()-15);
				}
				
				$('#library_button').unbind();
				$('#library_button').click(function(){
					if (!myValidation("#libraryDialog .popover-content")) {
				        return false;
			        }
					var dt = packData("#libraryDialog .popover-content");
					dt['id_type'] = '4';
					dt['parent_id'] = obj.original['parent_id'] || obj.parent || '';
					invockeService('busi.library.add.service', dt, function(retdata, isSucess){
						if(!isSucess || !retdata.id){
							return;
						}
						inst.create_node(obj, {id:retdata.id,text:dt['fs_code']+dt['fs_name'], type:"archives"}, "after",$.noop);
						xMsg('新建案卷成功');
					});
				});
			}
		};
		createMenu.submenu['createFiles'] = {
			label:"新建卷内文件",
			icon:"fa fa-file-text-o grey",
			action:function(data) {
				var inst = $.jstree.reference(data.reference),
				obj = inst.get_node(data.reference);
			
				$('#libraryDialog').modalPopover({placement:'right',target:$(data.reference)}).modalPopover('show');
				$('#libraryDialog>h3').text('新建卷内文件信息');
				$('#library_button').text('新增');
				
				var topDialog = $('#libraryDialog').position().top;
				if(topDialog <= 0){
					$('#libraryDialog').css('top','10px');
					$('#libraryDialog>div.arrow').css('top',($(data.reference).position().top+40)+'px');
				}
				else{
					$('#libraryDialog>div.arrow').css('top','50%');
				}
				var leftDialog = $('#libraryDialog').position().left;
				if(leftDialog+$('#libraryDialog').width() >= $(window).width()){
					$('#libraryDialog').css('left',$(window).width()-$('#libraryDialog').width()-15);
				}
				
				$('#library_button').unbind();
				$('#library_button').click(function(){
					if (!myValidation("#libraryDialog .popover-content")) {
				        return false;
			        }
					var dt = packData("#libraryDialog .popover-content");
					dt['id_type'] = '5';
					dt['parent_id'] = obj.id || '';
					invockeService('busi.library.add.service', dt, function(retdata, isSucess){
						if(!isSucess || !retdata.id){
							return;
						}
						inst.create_node(obj, {id:retdata.id,text:dt['fs_code']+dt['fs_name'], type:"files"}, "first",$.noop);
						xMsg('新建卷内文件成功');
					});
				});
			}
		};
		
		var renameMenu = menuItems.rename;
		delete renameMenu.action;
		renameMenu.label = "修改案卷信息";
		renameMenu.icon = "fa fa-pencil-square-o";
		renameMenu.action = function(data) {
			var inst = $.jstree.reference(data.reference),
			obj = inst.get_node(data.reference);
		
			$('#libraryDialog').modalPopover({placement:'right',target:$(data.reference)}).modalPopover('show');
			$('#libraryDialog>h3').text('修改案卷信息');
			$('#library_button').text('保存');
			
			loadDivData(obj.original,'#libraryDialog .popover-content');
			
			var topDialog = $('#libraryDialog').position().top;
			if(topDialog <= 0){
				$('#libraryDialog').css('top','10px');
				$('#libraryDialog>div.arrow').css('top',($(data.reference).position().top+40)+'px');
			}
			else{
				$('#libraryDialog>div.arrow').css('top','50%');
			}
			var leftDialog = $('#libraryDialog').position().left;
			if(leftDialog+$('#libraryDialog').width() >= $(window).width()){
				$('#libraryDialog').css('left',$(window).width()-$('#libraryDialog').width()-15);
			}
			
			$('#library_button').unbind();
			$('#library_button').click(function(){
				if (!myValidation("#libraryDialog .popover-content")) {
			        return false;
		        }
				var dt = packData("#libraryDialog .popover-content");
				dt['fs_id'] = obj.id || '';
				invockeService('busi.library.update.service', dt, function(data, isSucess){
					if(!isSucess){
						return;
					}
					xMsg('修改案卷信息成功');
					inst.refresh();
				});
			});
		};
		
		menuItems['batchUpdate'] = {
			label:"批量修改下级",
			icon:"fa fa-pencil-square",
			action: function (data) {
				var inst = $.jstree.reference(data.reference),
				obj = inst.get_node(data.reference);
				
				select_node_id = obj.id;
	        	select_node_name = obj.text;
			} 
		};
		
		var removeMenu = menuItems.remove;
		delete removeMenu.action;
		removeMenu.label = "删除案卷信息";
		removeMenu.icon = "fa fa-remove red";
		removeMenu.action = function(data) {
			var inst = $.jstree.reference(data.reference),
			obj = inst.get_node(data.reference);
			ref = $('#library_tree').jstree(true),
			sel = ref.get_selected();
        	if(!sel.length) { 
        		return false; 
        	}
        	
			xConfirm("你确定要删除此案卷吗？删除后不可恢复",function(flag){
		    	if(!flag){
		    		return;
		    	}
		    	invockeService('busi.library.delete.service', {fs_id:obj.id}, function(data, isSucess){
            		if(!isSucess){
						return;
					}
            		
                	ref.delete_node(sel);
            	});
			});
		};
		menuItems['importFiles'] = {
			label:"导入条目",
			icon:"fa fa-sign-in blue",
			action:function(data) {
				var inst = $.jstree.reference(data.reference),
				obj = inst.get_node(data.reference);
				ref = $('#library_tree').jstree(true),
				sel = ref.get_selected();
	        	if(!sel.length) { 
	        		return false; 
	        	}
	        	
	        	$('#importFilesDialog').modalPopover({placement:'right',target:$(data.reference)}).modalPopover('show');
				
				var topDialog = $('#importFilesDialog').position().top;
				if(topDialog <= 0){
					$('#importFilesDialog').css('top','10px');
					$('#importFilesDialog>div.arrow').css('top',($(data.reference).position().top+40)+'px');
				}
				else{
					$('#importFilesDialog>div.arrow').css('top','50%');
				}
				var leftDialog = $('#libraryDialog').position().left;
				if(leftDialog+$('#libraryDialog').width() >= $(window).width()){
					$('#libraryDialog').css('left',$(window).width()-$('#libraryDialog').width()-15);
				}
				
				$('#importFiles_button').unbind();
				$('#importFiles_button').click(function(){
					if (!$("#import_file_id").val()) {
						xAlert("请上传文件");
				        return false;
			        }
					showLoading(180);//180秒后关闭
					var dt = {
						fileid:$("#import_file_id").val(),
						fs_id:obj.id || '',
						servicename:"busi.archive.files.import"
					};
					invockeService('com.platform.cubism.excel.ImportExcel.class', dt, function(data, isSucess){
						if(!isSucess || !data.batch_number){
							showLoading(0);
							$('#close_importFiles_button').trigger('click');
							return;
						}
						xMsg('正在导入条目信息...',6);
						import_file_upload.reset();
						
						var total = 0, batchNumber = data.batch_number;
						getImportStatus = function(){
							total++;
							
							invockeService("sys.import.status.get.service",{batch_number:batchNumber},function(data, isSucess){
								if(!isSucess){
									showLoading(0);
									$('#close_importFiles_button').trigger('click');
									xAlert("无法查询导入状态，请手动查询导入信息是否成功。");
									return;
								}
								if(!!data && !!data.status && !!data.status.state_code){
									if(data.status.state_code == '00000'){
										showLoading(0);
										$('#close_importFiles_button').trigger('click');
										xMsg('条目信息导入成功！！！');
										inst.refresh();
										return;
									}
									else{
										showLoading(0);
										xMsg(data.status.state_code+" "+data.status.state_desc);
										return;
									}
								}
								else{
									xMsg("正在导入条目信息，请稍后...",6);
									if(total > 10){
										showLoading(0);
										$('#close_importFiles_button').trigger('click');
										xAlert("查询不到导入状态，请手动查询导入信息是否成功。");
										return;
									}
								}
								
								setTimeout("getImportStatus()", 1000*6);//6秒
							});
						}
						setTimeout(getImportStatus,1000*6);//每6秒查询一次导入状态
					});
				});
			}
		};
		
		var editMenu = menuItems.ccp,
		copyMenu = editMenu.submenu.copy,
		cutMenu = editMenu.submenu.cut,
		pasteMenu = editMenu.submenu.paste;
		
		editMenu.label = "复制粘贴";
		editMenu.icon = "fa fa-clone orange";
		
		copyMenu.label = "复制案卷";
		copyMenu.icon = "fa fa-files-o blue";
		
		cutMenu.label = "剪切案卷";
		cutMenu.icon = "fa fa-scissors red";
		
		pasteMenu.label = "粘贴";
		pasteMenu.icon = "fa fa-clipboard orange";
		
		return menuItems;
	},
	filesMenu = function(node){
		var menuItems = $.jstree.defaults.contextmenu.items();
		
		var createMenu = menuItems.create;
		delete createMenu.action;
		createMenu.label = "新建卷内文件";
		createMenu.icon = "fa fa-plus purple";
		createMenu.action=function(data) {
				var inst = $.jstree.reference(data.reference),
				obj = inst.get_node(data.reference);
			
				$('#libraryDialog').modalPopover({placement:'right',target:$(data.reference)}).modalPopover('show');
				$('#libraryDialog>h3').text('新建卷内文件信息');
				$('#library_button').text('新增');
				
				var topDialog = $('#libraryDialog').position().top;
				if(topDialog <= 0){
					$('#libraryDialog').css('top','10px');
					$('#libraryDialog>div.arrow').css('top',($(data.reference).position().top+40)+'px');
				}
				else{
					$('#libraryDialog>div.arrow').css('top','50%');
				}
				var leftDialog = $('#libraryDialog').position().left;
				if(leftDialog+$('#libraryDialog').width() >= $(window).width()){
					$('#libraryDialog').css('left',$(window).width()-$('#libraryDialog').width()-15);
				}
				
				$('#library_button').unbind();
				$('#library_button').click(function(){
					if (!myValidation("#libraryDialog .popover-content")) {
				        return false;
			        }
					var dt = packData("#libraryDialog .popover-content");
					dt['id_type'] = '5';
					dt['parent_id'] = obj.original['parent_id'] || obj.parent || '';
					invockeService('busi.library.add.service', dt, function(retdata, isSucess){
						if(!isSucess || !retdata.id){
							return;
						}
						inst.create_node(obj, {id:retdata.id,text:dt['fs_code']+dt['fs_name'], type:"files"}, "after",$.noop);
						xMsg('新建卷内文件成功');
					});
				});
		};
		
		var renameMenu = menuItems.rename;
		delete renameMenu.action;
		renameMenu.label = "修改卷内文件信息";
		renameMenu.icon = "fa fa-pencil-square-o";
		renameMenu.action = function(data) {
			var inst = $.jstree.reference(data.reference),
			obj = inst.get_node(data.reference);
		
			$('#libraryDialog').modalPopover({placement:'right',target:$(data.reference)}).modalPopover('show');
			$('#libraryDialog>h3').text('修改卷内文件信息');
			$('#library_button').text('保存');
			
			loadDivData(obj.original,'#libraryDialog .popover-content');
			
			var topDialog = $('#libraryDialog').position().top;
			if(topDialog <= 0){
				$('#libraryDialog').css('top','10px');
				$('#libraryDialog>div.arrow').css('top',($(data.reference).position().top+40)+'px');
			}
			else{
				$('#libraryDialog>div.arrow').css('top','50%');
			}
			var leftDialog = $('#libraryDialog').position().left;
			if(leftDialog+$('#libraryDialog').width() >= $(window).width()){
				$('#libraryDialog').css('left',$(window).width()-$('#libraryDialog').width()-15);
			}
			
			$('#library_button').unbind();
			$('#library_button').click(function(){
				if (!myValidation("#libraryDialog .popover-content")) {
			        return false;
		        }
				var dt = packData("#libraryDialog .popover-content");
				dt['fs_id'] = obj.id || '';
				invockeService('busi.library.update.service', dt, function(data, isSucess){
					if(!isSucess){
						return;
					}
					xMsg('修改卷内文件信息成功');
					inst.refresh();
				});
			});
		};
		
		
		var removeMenu = menuItems.remove;
		delete removeMenu.action;
		removeMenu.label = "删除卷内文件信息";
		removeMenu.icon = "fa fa-remove red";
		removeMenu.action = function(data) {
			var inst = $.jstree.reference(data.reference),
			obj = inst.get_node(data.reference);
			ref = $('#library_tree').jstree(true),
			sel = ref.get_selected();
        	if(!sel.length) { 
        		return false; 
        	}
        	
			xConfirm("你确定要删除此卷内文件吗？删除后不可恢复",function(flag){
		    	if(!flag){
		    		return;
		    	}
		    	invockeService('busi.library.delete.service', {fs_id:obj.id}, function(data, isSucess){
            		if(!isSucess){
						return;
					}
            		
                	ref.delete_node(sel);
            	});
			});
		};
		menuItems['unRelatedVersion'] = {
			label:"解除关联",
			icon:"fa fa-plug blue",
			action:function(data) {
				var inst = $.jstree.reference(data.reference),
				obj = inst.get_node(data.reference);
				ref = $('#library_tree').jstree(true),
				sel = ref.get_selected();
	        	if(!sel.length) { 
	        		return false; 
	        	}
	        	
				xConfirm("你确定要解除此文件的关联吗？",function(flag){
			    	if(!flag){
			    		return;
			    	}
			    	invockeService('busi.library.related.delete.service', {fs_id:obj.id}, function(data, isSucess){
	            		if(!isSucess){
							return;
						}
	            		xMsg('解除文件关联成功');
	            		inst.refresh();
	            	});
				});
			}
		};
		
		var editMenu = menuItems.ccp,
		copyMenu = editMenu.submenu.copy,
		cutMenu = editMenu.submenu.cut,
		pasteMenu = editMenu.submenu.paste;
		
		editMenu.label = "复制粘贴";
		editMenu.icon = "fa fa-clone orange";
		
		copyMenu.label = "复制文件";
		copyMenu.icon = "fa fa-files-o blue";
		
		cutMenu.label = "剪切文件";
		cutMenu.icon = "fa fa-scissors red";
		
		pasteMenu.label = "粘贴";
		pasteMenu.icon = "fa fa-clipboard orange";
		
		return menuItems;
	},
	commonMenu = function(node){
		var menuItems = $.jstree.defaults.contextmenu.items();
		
		var createMenu = menuItems.create;
		delete createMenu.action;
		createMenu.label = "新建";
		createMenu.icon = "fa fa-plus purple";
		
		createMenu['submenu'] = {};
		createMenu.submenu['createLibType'] = {
			label:"新建同级",
			icon:"fa fa-folder-o purple",
			action:function(data) {
				var inst = $.jstree.reference(data.reference),
				obj = inst.get_node(data.reference);
			
				$('#libraryDialog').modalPopover({placement:'right',target:$(data.reference)}).modalPopover('show');
				$('#libraryDialog>h3').text('新建同级');
				$('#library_button').text('新增');
				
				var topDialog = $('#libraryDialog').position().top;
				if(topDialog <= 0){
					$('#libraryDialog').css('top','10px');
					$('#libraryDialog>div.arrow').css('top',($(data.reference).position().top+40)+'px');
				}
				else{
					$('#libraryDialog>div.arrow').css('top','50%');
				}
				var leftDialog = $('#libraryDialog').position().left;
				if(leftDialog+$('#libraryDialog').width() >= $(window).width()){
					$('#libraryDialog').css('left',$(window).width()-$('#libraryDialog').width()-15);
				}
				
				$('#library_button').unbind();
				$('#library_button').click(function(){
					if (!myValidation("#libraryDialog .popover-content")) {
				        return false;
			        }
					var dt = packData("#libraryDialog .popover-content");
					dt['id_type'] = '9';
					dt['parent_id'] = obj.original['parent_id'] || obj.parent || '';
					invockeService('busi.library.add.service', dt, function(retdata, isSucess){
						if(!isSucess || !retdata.id){
							return;
						}
						inst.create_node(obj, {id:retdata.id,text:dt['fs_code']+dt['fs_name'], type:"common"}, "after",$.noop);
						xMsg('新建成功');
					});
				});
			}
		};
		createMenu.submenu['createSubLibType'] = {
			label:"新建下级",
			icon:"fa fa-file-text-o blue",
			action:function(data) {
				var inst = $.jstree.reference(data.reference),
				obj = inst.get_node(data.reference);
			
				$('#libraryDialog').modalPopover({placement:'right',target:$(data.reference)}).modalPopover('show');
				$('#libraryDialog>h3').text('新建下级');
				$('#library_button').text('新增');
				
				var topDialog = $('#libraryDialog').position().top;
				if(topDialog <= 0){
					$('#libraryDialog').css('top','10px');
					$('#libraryDialog>div.arrow').css('top',($(data.reference).position().top+40)+'px');
				}
				else{
					$('#libraryDialog>div.arrow').css('top','50%');
				}
				var leftDialog = $('#libraryDialog').position().left;
				if(leftDialog+$('#libraryDialog').width() >= $(window).width()){
					$('#libraryDialog').css('left',$(window).width()-$('#libraryDialog').width()-15);
				}
				
				$('#library_button').unbind();
				$('#library_button').click(function(){
					if (!myValidation("#libraryDialog .popover-content")) {
				        return false;
			        }
					var dt = packData("#libraryDialog .popover-content");
					dt['id_type'] = '9';
					dt['parent_id'] = obj.id || '';
					invockeService('busi.library.add.service', dt, function(retdata, isSucess){
						if(!isSucess || !retdata.id){
							return;
						}
						inst.create_node(obj, {id:retdata.id,text:dt['fs_code']+dt['fs_name'], type:"common"}, "first",$.noop);
						xMsg('新建成功');
					});
				});
			}
		};
		
		var renameMenu = menuItems.rename;
		delete renameMenu.action;
		renameMenu.label = "修改信息";
		renameMenu.icon = "fa fa-pencil-square-o";
		renameMenu.action = function(data) {
			var inst = $.jstree.reference(data.reference),
			obj = inst.get_node(data.reference);
		
			$('#libraryDialog').modalPopover({placement:'right',target:$(data.reference)}).modalPopover('show');
			$('#libraryDialog>h3').text('修改信息');
			$('#library_button').text('保存');
			
			loadDivData(obj.original,'#libraryDialog .popover-content');
			
			var topDialog = $('#libraryDialog').position().top;
			if(topDialog <= 0){
				$('#libraryDialog').css('top','10px');
				$('#libraryDialog>div.arrow').css('top',($(data.reference).position().top+40)+'px');
			}
			else{
				$('#libraryDialog>div.arrow').css('top','50%');
			}
			var leftDialog = $('#libraryDialog').position().left;
			if(leftDialog+$('#libraryDialog').width() >= $(window).width()){
				$('#libraryDialog').css('left',$(window).width()-$('#libraryDialog').width()-15);
			}
			
			$('#library_button').unbind();
			$('#library_button').click(function(){
				if (!myValidation("#libraryDialog .popover-content")) {
			        return false;
		        }
				var dt = packData("#libraryDialog .popover-content");
				dt['fs_id'] = obj.id || '';
				invockeService('busi.library.update.service', dt, function(data, isSucess){
					if(!isSucess){
						return;
					}
					xMsg('修改成功');
					inst.refresh();
				});
			});
		};
		
		
		var removeMenu = menuItems.remove;
		delete removeMenu.action;
		removeMenu.label = "删除信息";
		removeMenu.icon = "fa fa-remove red";
		removeMenu.action = function(data) {
			var inst = $.jstree.reference(data.reference),
			obj = inst.get_node(data.reference);
        	
			xConfirm("你确定要删除吗？删除后不可恢复",function(flag){
		    	if(!flag){
		    		return;
		    	}
		    	invockeService('busi.library.delete.service', {fs_id:obj.id}, function(data, isSucess){
            		if(!isSucess){
						return;
					}
            		if(inst.is_selected(obj)) {
						inst.delete_node(inst.get_selected());
					}
					else {
						inst.delete_node(obj);
					}
            	});
			});
		};
		
		var editMenu = menuItems.ccp,
		copyMenu = editMenu.submenu.copy,
		cutMenu = editMenu.submenu.cut,
		pasteMenu = editMenu.submenu.paste;
		
		editMenu.label = "复制粘贴";
		editMenu.icon = "fa fa-clone orange";
		
		copyMenu.label = "复制";
		copyMenu.icon = "fa fa-files-o blue";
		
		cutMenu.label = "剪切";
		cutMenu.icon = "fa fa-scissors red";
		
		pasteMenu.label = "粘贴";
		pasteMenu.icon = "fa fa-clipboard orange";
		
		return menuItems;
	},
	contextMenuFun = function(node) {
		var menu,menuItems = $.jstree.defaults.contextmenu.items(),
		nodeType = this.get_type(node);
		
		if(nodeType === "libtype"){//档案库门类
			menu = libTypeMenu(node);
			menu['version'] = versionMenu(node);//版本发布
		}
		else if(nodeType === "library"){//档案库
			menu = libraryMenu(node);
			menu['version'] = versionMenu(node);//版本发布
		}
		else if(nodeType === "profession"){//专业
			menu = professionMenu(node);
		}
		else if(nodeType === "classify"){//分类
			menu = classifyMenu(node);
		}
		else if(nodeType === "archives"){//案卷
			menu = archivesMenu(node);
		}
		else if(nodeType === "files" || nodeType === "files2"){//卷内文件
			menu = filesMenu(node);
		}
		else if(nodeType === "common"){//通用
			menu = commonMenu(node);
		}
		else{
			menu = commonMenu(node);//通用
		}
		
		//menu['version'] = versionMenu(node);//版本发布
		return menu;
	};
	
	$('#addLibraryButton').click(function(){//增加档案库门类
	
		$('#libraryDialog').modalPopover({placement:'right',target:$('#addLibraryButton')}).modalPopover('show');
		$('#libraryDialog>h3').text('新建档案库门类信息');
		$('#library_button').text('新增');
		
		var topDialog = $('#libraryDialog').position().top;
		if(topDialog <= 0){
			$('#libraryDialog').css('top','10px');
			$('#libraryDialog>div.arrow').css('top','20px');
		}
		
		$('#library_button').unbind();
		$('#library_button').click(function(){
			if (!myValidation("#libraryDialog .popover-content")) {
		        return false;
	        }
			var dt = packData("#libraryDialog .popover-content");
			dt['id_type'] = '0';
			invockeService('busi.library.add.service', dt, function(retdata, isSucess){
				if(!isSucess || !retdata.id){
					return;
				}
				xMsg('新建档案库门类成功');
				$('#library_tree').jstree('refresh');
			});
		});
	});
	
	import_file_upload = new singleFileUpload(function(data){
		if(!data || !data.files){
			import_file_upload.reset();
			$('#import_file_id').val("");
			$('#import_file_name').text("");
			return false;
		}
		$('#import_file_id').val("");
		$('#import_file_name').text("");
		
		var file = data.files[0];
		$('#import_file_id').val(file.fileidentify);
		$('#import_file_name').text(file.filename);
		xMsg("卷内文件上传成功！");
	});
	
	$('#library_tree').jstree({
        plugins: ['state',"dnd","contextmenu","wholerow","types"],
        contextmenu: {items:contextMenuFun, select_node: true},
        types: {
        	libtype : {//档案库门类
              	icon : "fa fa-sitemap purple"
            },
            library : {//档案库
              	icon : "fa fa-university green"
            },
            profession : {//专业
              	icon : "fa fa-shield blue"
            },
            classify : {//案卷分类
              	icon : "fa fa-th-large pink"
            },
            archives : {//案卷
              	icon : "fa fa-cube orange"
            },
            files : {//未关联表格文件
            	icon : "fa fa-file-o grey"
            },
            files2 : {//已关联表格文件
            	icon : "fa fa-file-word-o green"
            },
            common : {//
            	icon : "fa fa-folder-o grey"
            },
            unknow : {//未知
            	icon : "fa fa-exclamation-triangle brown"
            }
        },
        core: {
            themes: {
                name: 'proton',
                icons : true,//false
                responsive: true
            },
            multiple:false,
            //check_callback:true,
            check_callback: function (operation, node, node_parent, node_position, more) {
            	//determines what happens when a user tries to modify the structure of the tree
            	//If left as false all operations like create, rename, delete, move or copy are prevented.
            	//You can set this to true to allow all interactions or use a function to have better control.
            	
	            // operation can be 'create_node', 'rename_node', 'delete_node', 'move_node' or 'copy_node'
            	
	            // in case of 'rename_node' node_position is filled with the new node name
	            //return operation === 'rename_node' ? true : false;
            	
            	var isLike = function(srcType,distType){
            		var flag = true;
            		if(distType === 'libtype') {
						if(srcType === 'libtype' || srcType === 'library' || srcType === 'common'){
							flag = true;
						}
						else{
							flag = false;
						}
					}
					else if(distType === 'library') {
						if(srcType === 'library' || srcType === 'profession'){
							flag = true;
						}
						else{
							flag = false;
						}
					}
					else if(distType === 'profession') {
						if(srcType === 'profession' || srcType === 'classify' || srcType === 'archives'){
							flag = true;
						}
						else{
							flag = false;
						}
					}
					else if(distType === 'classify') {
						if(srcType === 'classify' || srcType === 'archives'){
							flag = true;
						}
						else{
							flag = false;
						}
					}
					else if(distType === 'archives') {
						if(srcType === 'files' || srcType === 'files2'){
							flag = true;
						}
						else{
							flag = false;
						}
					}
					else if(distType === 'common'){
						if(srcType === 'common'){
							flag = true;
						}
						else{
							flag = false;
						}
					}
					else if(distType !== srcType) {
						flag = false;
					}
					
					return flag;
            	};
            	
            	if(more && more.dnd && more.ref) {//拖动控制
            		if(!isLike(this.get_node(node).type,this.get_node(more.ref).type)) {
						return false; 
					} 
            	}
            	
				var distType = this.get_node(node_parent).type, srcType = this.get_node(node).type;
            	
				if(operation === "move_node") {
					if(!isLike(srcType,distType)) {
						return false; 
					} 
				}
				
				if(operation === "move_node" || operation === "copy_node") {
					if(!isLike(srcType,distType)) {
						xAlert("目标类型["+distType+"]与原类型["+srcType+"]不匹配，不允许此操作");
						return false; 
					} 
				}
				return true;
	        },
            data:function(node,treecallback){
        		var d = {id_type:node.type || ''};
        		if(node.id === "#") {
        			d['id'] = '';
                }
                else {
                	d['id'] = node.id;
                }
            	invockeService('busi.library.query.service', d, function(data, isSucess){
					if(!isSucess){
						treecallback([]);
						return;
					}
					var cnt = 0;
					for(var i=0; i<data.library.length; i++){
						cnt = (data.library[i].children || 0)*1;
						if(cnt > 0){
							data.library[i].children = true;
						}
						else{
							data.library[i].children = false;
						}
					}
					treecallback(data.library || []);
				});
            }
        }
    }).on('move_node.jstree', function (e, data) {
    	/**
		 * triggered when a node is moved
		 * @event
		 * @name move_node.jstree
		 * 
		 * @param {Object} node
		 * @param {String} parent the parent's ID
		 * @param {Number} position the position of the node among the parent's children
		 * @param {String} old_parent the old parent of the node
		 * @param {Number} old_position the old position of the node
		 * @param {Boolean} is_multi do the node and new parent belong to different instances
		 * @param {jsTree} old_instance the instance the node came from
		 * @param {jsTree} new_instance the instance of the new parent
		 */
    	xConfirm("你确定要移动吗？",function(flag){
        	if(!flag){
        		data.instance.refresh();
        		return;
        	}
        	var dt = {
    			fs_id:data.node.id,
    			parent_id:data.parent
        	};
        	invockeService('busi.library.update.service', dt, function(data, isSucess){
    			if(!isSucess){
    				return;
    			}
    			xMsg('移动成功');
    			//data.instance.refresh();
    		});
    	});
	}).on('copy_node.jstree', function (e, data) {
		/**
		 * triggered when a node is copied
		 * @event
		 * @name copy_node.jstree
		 * 
		 * @param {Object} node the copied node
		 * @param {Object} original the original node
		 * @param {String} parent the parent's ID
		 * @param {Number} position the position of the node among the parent's children
		 * @param {String} old_parent the old parent of the node
		 * @param {Number} old_position the position of the original node
		 * @param {Boolean} is_multi do the node and new parent belong to different instances
		 * @param {jsTree} old_instance the instance the node came from
		 * @param {jsTree} new_instance the instance of the new parent
		 */
		xConfirm("你确定要复制吗？",function(flag){
	    	if(!flag){
	    		data.instance.refresh();
	    		return;
	    	}
	    	showLoading(100);
			invockeServiceSync('busi.library.copy.service', {fs_id:data.original.id,parent_id:data.parent}, function(retdata, isSucess){
				showLoading(0);
				if(!isSucess){
					data.instance.refresh();
					return;
				}
				xMsg('复制成功');
			});
		});
	}).on('select_node.jstree',function(node,selected,event){//loaded.jstree ready.jstree 档案库加载完毕后加载表格
		showRelatedGrid(selected.node.type == 'archives');//案卷
		showWaitingGrid(selected.node.type == 'files');//未关联卷内文件
		showFileDetail(selected.node.type == 'files2');//已关联卷内文件
	});
	
	$(document).on('context_show.vakata',function(e,data){
		$(data.element).find('i.fa-pencil-square').parent('a').attr({'href':'libraryEditor.jsp?id='+getSelectedLibNodeID(),'target':'_blank'});
	});
	
	$('#refreshLibraryButton').on('click', function(){
		$('#library_tree').jstree('refresh');
	});
	$('#closeAllButton').on('click', function(){
		$('#library_tree').jstree('close_all');
	});
	
	$('#close_library_button').click(function(){
		$('#libraryDialog').modalPopover('hide');
		$('body').css('padding-right', '0px');
		clearField('#libraryDialog .popover-content');
		//$('#library_tree').jstree('refresh');
	});
	$('#close_importFiles_button').click(function(){
		$('#importFilesDialog').modalPopover('hide');
		$('body').css('padding-right', '0px');
		clearField('#importFilesDialog .popover-content');
		//$('#library_tree').jstree('refresh');
	});
	
	getSelectedLibNodeID = function(){//获取当前选中节点的ID
		 var ref = $('#library_tree').jstree(true),
		 sel = ref.get_selected();
		 if(!sel.length) { 
			 return ""; 
		 }
		 else{
			 return sel.id || sel[0];
		 }
	};
	getSelectedLibNodeType = function(typ){//获取当前选中节点的类别
		 var ref = $('#library_tree').jstree(true),
		 sel = ref.get_selected();
		 if(!sel.length) { 
			 return ""; 
		 }
		 else{
			 if(typ){
				 return ref.get_node(sel).original['id_type'];
			 }
			 else{
				 return ref.get_node(sel).type;
			 }
		 }
	};
	
	var showFileDetail = function(isshow){
		var detail=$('#rightContentMain_detail'),container=detail.parent(),isInit = detail.html() && detail.html().trim().length > 0;
		
		if(!isshow){
			if(isInit){
				container.hide();
				container.find('.modal-body .form-control').text('');//清空数据
			}
			return;
		}
		
		container.show();
		
		if(isInit){
			invockeService('busi.formsfiles.get.service',{fs_id:getSelectedLibNodeID()}, function(data, isSucess){
				if(!isSucess || !data.formsfiles){
					return;
				}
				
				detail.find('.body .form-control').text('');
				$('#tmpl_file_name').attr('href','#');
				$('#example_file_name').attr('href','#');

				loadDivData(data.formsfiles,detail.find('.body'));
				var fileid = data.formsfiles.tmpl_file_identify || null;
				if(fileid){
					detail.find('#tmpl_file_name').attr('href','netdomain.sys_diskfile_download?fileid='+fileid);
				}
				fileid = data.formsfiles.example_file_identify || null;
				if(fileid){
					detail.find('#example_file_name').attr('href','netdomain.sys_diskfile_download?fileid='+fileid);
				}
			});	
		}
		else{
			invockeService('busi.formsfiles.get.service',{fs_id:getSelectedLibNodeID()}, function(data, isSucess){
				if(!isSucess || !data.formsfiles){
					return;
				}
				detail.load("formsfilesView2.jsp?"+(new Date()).valueOf(), null, function(){
					detail.find('.body .form-control').text('');
					$('#tmpl_file_name').attr('href','#');
					$('#example_file_name').attr('href','#');

					loadDivData(data.formsfiles,detail.find('.body'));
					var fileid = data.formsfiles.tmpl_file_identify || null;
					if(fileid){
						detail.find('#tmpl_file_name').attr('href','netdomain.sys_diskfile_download?fileid='+fileid);
					}
					fileid = data.formsfiles.example_file_identify || null;
					if(fileid){
						detail.find('#example_file_name').attr('href','netdomain.sys_diskfile_download?fileid='+fileid);
					}
				});
			});	
		}
		
	}
	
	/**已关联表格************************************************************************************************************************/
	var showRelatedGrid = function(isshow){
		var container=$('#rightContentMain_related').parent(),grid = $("#relatedJqGrid"),gridbox = $("#gbox_relatedJqGrid"), isInit = !(!grid || !grid.getGridParam('rowNum') || grid.getGridParam('rowNum')<=0);
		
		if(!isshow){
			if(isInit){
				container.hide();
				grid.clearGridData();//清空数据
			}
			return;
		}
		
		container.show();
		
		if(isInit){
			grid.jqGrid( 'setGridWidth', container.width());
			grid.trigger("reloadGrid");
		}
		else{
			var relatedFileTitleFormatter = function(cellvalue, options, rowObject){
				if(!cellvalue){
					return "";
				}
				return "<span class='file_title' role=button>"+cellvalue+"</span>";
			};
			$("#relatedJqGrid").jqGrid({
				datatype: function(){
					setJqgridDataFunc('busi.formsfiles.related.query.service','#relatedJqGrid',{fs_id:getSelectedLibNodeID()},function(){
						gridbox = $("#gbox_relatedJqGrid");
						if(gridbox.height()<$(window).height()){
							$('#rightContentMain_related').height(gridbox.height());
						}
						else{
							$('#rightContentMain_related').height($(window).height()-10);
						}
					});
				},     //datatype: "local",
				width:"100%",
				height:'auto',
				autowidth: true,
				shrinkToFit: true,
			    colNames:['案卷表格序号','关联条目','版本','文件编号','文件名称','总页数','分类目录'],   
			    colModel :[
					{name:'tb_id', index:'tb_id', key:true, hidden:true,sortable:true},
				    {name:'fs_code', index:'fs_code',width:'40px',sortable:false},
				    {name:'tb_version', index:'tb_version',width:'40px',sortable:false},
				    {name:'document_number', index:'document_number',width:'70px',align:'right',search:true,sortable:false},
				    {name:'file_title', index:'file_title',search:true,formatter:relatedFileTitleFormatter,sortable:false},
				    {name:'total_pages', index:'total_pages',width:'40px',search:true,sortable:false},
				    {name:'dir_name', index:'dir_name',width:'50px',search:true,sortable:false}],
			    pager: "#relatedJqGridPager",
			    rowNum:10,
			    rowList:[10,20,50,100,200], 
			    altRows:true,
				loadonce: true,
				cellEdit:false,
			    viewrecords: true,//定义是否在导航条上显示总的记录数
				multiselect : true,
				multiboxonly: true,
				gridComplete : function(){
					var grid=$("#relatedJqGrid"); 
					grid.find("span.file_title").unbind();
					grid.find("span.file_title").click(function(){
						setTimeout(function(){//防阻塞
							var key = grid.jqGrid("getGridParam","selrow"); //单条
							invockeServiceSync('busi.formsfiles.get.service',{tb_id:key}, function(data, isSucess){
								if(!isSucess || !data.formsfiles){
									return;
								}
								
								showDialog("formsfilesView.jsp","viewFormsfilesDialog",function(dialog){
									dialog.css('left','auto');
									
									dialog.find('.modal-body .form-control').text('');
									$('#tmpl_file_name').attr('href','#');
									$('#example_file_name').attr('href','#');

									loadDivData(data.formsfiles,dialog.find('.modal-body .tab-content'));
									var fileid = data.formsfiles.tmpl_file_identify || null;
									if(fileid){
										dialog.find('#tmpl_file_name').attr('href','netdomain.sys_diskfile_download?fileid='+fileid);
									}
									fileid = data.formsfiles.example_file_identify || null;
									if(fileid){
										dialog.find('#example_file_name').attr('href','netdomain.sys_diskfile_download?fileid='+fileid);
									}
								});
							});	
						},0);
					});
				}
		  	}).navGrid('#relatedJqGridPager',
				{
					alertcap:"请选中您要操作的行",alerttext:"您还没有选中行，请选择后再重新操作！",
					edit: false, editicon : 'fa fa-pencil blue',edittitle:'修改文件信息',
					add: false, addicon : 'fa fa-plus-circle purple',addtitle:'新建文件',
					del: true, delicon : 'fa fa-trash-o red',deltitle:'解除关联',
					search: true, searchicon : 'fa fa-search orange',searchtitle:'多条件查找',
					refresh: true, refreshicon : 'fa fa-refresh green',refreshtitle:'重新载入数据',
					view: false, viewicon : 'fa fa-search-plus grey',viewtitle:'查看详细',
					delfunc: function(){
						var keys = grid.jqGrid("getGridParam","selarrrow");
						var typ = getSelectedLibNodeType();
						if(!typ || typ != 'archives'){//必须选择案卷
							xAlert('请选择需要解除关联的案卷');
							return;
						}
						if(!keys || keys.length <= 0){
					    	xAlert("请选中需要解除关联的表格");
							return;
					    }
						
						var tabids=[],fsids=[getSelectedLibNodeID()];
						for(var i=0; i<keys.length; i++){
							tabids.push(keys[i]);
							fsids.push(fsids[0]);
						}
						fsids.shift();
						
						xConfirm("你确定要解除当前表格与选中的案卷的关联吗？",function(flag){
					    	if(!flag){
					    		return;
					    	}
					    	invockeServiceSync('busi.foldertemplate_formsfiles.delete.service',{tb_id:tabids,fs_id:fsids}, function(data, isSucess){
								if(!isSucess){
									return;
								}
								xMsg('解除关联成功');
								var len = keys.length;  
								for(var i=0; i<len ;i++) {  
									grid.jqGrid("delRowData", keys[0]);  
								}
								$('#library_tree').jstree('refresh');
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
//			$("#relatedJqGrid").jqGrid('gridDnD',{
//				connectWith:'#relatedJqGrid',
//				drag_opts:{ 
//					helper: function(event){
//						var grid=$("#relatedJqGrid"),rowData =grid.getRowData($(this).attr('id'));
//						return $('<div>' + rowData['document_number'] + ' - ' + rowData['file_title'] + '</div>');
//					}
//				}
//			});
		}// end if isInit
	};
	
	
	/**关联表格************************************************************************************************************************/
	var showWaitingGrid = function(isshow){
		var container=$('#rightContentMain_waiting').parent(),grid = $("#waitingJqGrid"),gridbox = $("#gbox_waitingJqGrid"), isInit = !(!grid || !grid.getGridParam('rowNum') || grid.getGridParam('rowNum')<=0);
		
		if(!isshow){
			if(isInit){
				container.hide();
				grid.clearGridData();//清空数据
			}
			return;
		}
		
		container.show();
		
		if(isInit){
			grid.jqGrid( 'setGridWidth', container.width());
			grid.trigger("reloadGrid");
		}
		else{
			var versionFormatter = function (cellvalue, options, rowObject) {
				if(!cellvalue){
					return "";
				}
				
				var tb_id=rowObject['tb_id'],haschild=rowObject['haschild'],parent_id=rowObject['parent_id'];
				
				if(haschild && haschild * 1 > 0){
					return "<span class='hand subversion' role=button><i class='fa fa-plus-square-o blue'></i> "+cellvalue+"</span>";
				}
				else{
					if(!parent_id || parent_id.length <= 0){
						return "<i class='fa fa-square-o'></i> "+cellvalue;
					}
					var grid = $("#myJqGrid"),margin = 0;
					for(;;){
						if(!parent_id || parent_id.length <= 0){
							break;
						}
						var rowData = grid.getRowData(parent_id);
						parent_id = rowData['parent_id'];
						margin += 10;
					}
					return "<i class='fa fa-circle-thin grey'style='margin-left:"+margin+"px;'></i> "+cellvalue;
				}
			},
			fileTitleFormatter = function(cellvalue, options, rowObject){
				if(!cellvalue){
					return "";
				}
				return "<span class='file_title' role=button>"+cellvalue+"</span>";
			};
			$("#waitingJqGrid").jqGrid({
				datatype: function(){
					setJqgridDataFunc('busi.formsfiles.waiting.query.service','#waitingJqGrid',{fs_id:getSelectedLibNodeID()},function(){
						gridbox = $("#gbox_waitingJqGrid");
						if(gridbox.height() < $(window).height()){
							$('#rightContentMain_waiting').height(gridbox.height());
						}
						else{
							$('#rightContentMain_waiting').height($(window).height()-10);
						}
					});
				},     //datatype: "local",
				width:"100%",
				height:'auto',
				autowidth: true,
				shrinkToFit: true,
			    colNames:['表格序号','上级序号','是否关联','子版本是否关联','是否子版本','关联','版本','文件编号','文件名称','总页数','分类目录'],   
			    colModel :[
					{name:'tb_id', index:'tb_id', key:true, hidden:true},
					{name:'parent_id', index:'parent_id', hidden:true},
					{name:'isrelated', index:'isrelated', hidden:true},
					{name:'ischildrelated', index:'ischildrelated', hidden:true},
					{name:'haschild', index:'haschild', hidden:true},
					{name:'action', index:'action',width:'22px'},
					{name:'tb_version', index:'tb_version',width:'50px',formatter:versionFormatter},
				    {name:'document_number', index:'document_number',width:'70px',align:'right',search:true},
				    {name:'file_title', index:'file_title',search:true,formatter:fileTitleFormatter},
				    {name:'total_pages', index:'total_pages',width:'40px',search:true},
				    {name:'dir_name', index:'paper_size',width:'50px',search:true}],
			    pager: "#waitingJqGridPager",
			    rowNum:10,
			    rowList:[10,20,50,100,200], 
			    sortname: 'tb_id',
			    altRows:true,
				loadonce: true,
				cellEdit:false,
			    viewrecords: true,//定义是否在导航条上显示总的记录数
				multiselect : false,
				multiboxonly: true,
				gridComplete : function(){
					var act,rowData,ischildrelated,grid=$("#waitingJqGrid"), ids = grid.jqGrid('getDataIDs'); 
					for (var i=0; i<ids.length; i++) { 
						rowData =grid.getRowData(ids[i]);
						if(rowData['isrelated'] * 1 > 0){//已关联
							act = "<i class='fa fa-long-arrow-right red bigger-150' data-value='"+ids[i]+"' role=button></i>";
						}
						else{//未关联
							ischildrelated=rowData['ischildrelated'];
							if(ischildrelated && ischildrelated * 1 > 0){//子版本有关联
								act = "<i class='fa fa-long-arrow-left grey bigger-150' data-value='"+ids[i]+"' role=button></i>";
							}else{//未做任何关联
								act = "<i class='fa fa-long-arrow-left purple bigger-150' data-value='"+ids[i]+"' role=button></i>";
							}
						}
						 
						grid.jqGrid('setRowData', ids[i], { action : act }); 
					}
					grid.find("i.fa-long-arrow-left").unbind();
					grid.find("i.fa-long-arrow-left").click(function(){
						var self = this;
						setTimeout(function(){//防阻塞
							var key = grid.jqGrid("getGridParam","selrow"); //单条
							var rowData =grid.getRowData(key);
							var typ = getSelectedLibNodeType();
							if(!typ || (typ != 'files' && typ != 'files2')){//必须选择卷内文件
								xAlert('请选择需要关联的卷内文件');
								return;
							}
							var ischildrelated=rowData['ischildrelated'];
							if(ischildrelated && ischildrelated * 1 > 0){//子版本有关联
								xConfirm("提示：该表格的上一版本已经关联到当前案卷，\n\n你确定要继续关联此表格吗？",function(flag){
							    	if(!flag){
							    		return;
							    	}
							    	invockeServiceSync('busi.foldertemplate_formsfiles.add.service',{tb_id:key,fs_id:getSelectedLibNodeID()}, function(data, isSucess){
										if(!isSucess){
											return;
										}
										$(self).remove();
										xMsg('关联成功');
										$('#library_tree').jstree('refresh');
									});	
								});
							}
							else{
								xConfirm("你确定要将当前表格关联到选中的案卷吗？",function(flag){
							    	if(!flag){
							    		return;
							    	}
							    	invockeServiceSync('busi.foldertemplate_formsfiles.add.service',{tb_id:key,fs_id:getSelectedLibNodeID()}, function(data, isSucess){
										if(!isSucess){
											return;
										}
										$(self).remove();
										xMsg('关联成功');
										$('#library_tree').jstree('refresh');
									});	
								});
							}
						},0);
					});
					grid.find("i.fa-long-arrow-right").unbind();
					grid.find("i.fa-long-arrow-right").click(function(){
						var self = this;
						setTimeout(function(){//防阻塞
							var key = grid.jqGrid("getGridParam","selrow"); //单条
							var typ = getSelectedLibNodeType();
							if(!typ || (typ != 'files' && typ != 'files2')){//必须选择卷内文件
								xAlert('请选择需要解除关联的卷内文件');
								return;
							}
							xConfirm("你确定要解除当前表格与选中的案卷的关联吗？",function(flag){
						    	if(!flag){
						    		return;
						    	}
						    	invockeServiceSync('busi.foldertemplate_formsfiles.delete.service',{tb_id:key,fs_id:getSelectedLibNodeID()}, function(data, isSucess){
									if(!isSucess){
										return;
									}
									$(self).removeClass('fa-long-arrow-right red').addClass('fa-long-arrow-left purple');//需挂载关联事件
									xMsg('解除关联成功');
									$(self).unbind();
									$('#library_tree').jstree('refresh');
								});	
							});
						},0);
					});
					
					grid.find("span.file_title").unbind();
					grid.find("span.file_title").click(function(){
						setTimeout(function(){//防阻塞
							var key = grid.jqGrid("getGridParam","selrow"); //单条
							invockeServiceSync('busi.formsfiles.get.service',{tb_id:key}, function(data, isSucess){
								if(!isSucess || !data.formsfiles){
									return;
								}
								
								showDialog("formsfilesView.jsp","viewFormsfilesDialog",function(dialog){
									dialog.css('left','auto');
									
									dialog.find('.modal-body .form-control').text('');
									$('#tmpl_file_name').attr('href','#');
									$('#example_file_name').attr('href','#');

									loadDivData(data.formsfiles,dialog.find('.modal-body .tab-content'));
									var fileid = data.formsfiles.tmpl_file_identify || null;
									if(fileid){
										dialog.find('#tmpl_file_name').attr('href','netdomain.sys_diskfile_download?fileid='+fileid);
									}
									fileid = data.formsfiles.example_file_identify || null;
									if(fileid){
										dialog.find('#example_file_name').attr('href','netdomain.sys_diskfile_download?fileid='+fileid);
									}
								});
							});	
						},0);
					});
					
					grid.find("span.subversion").unbind();
					grid.find("span.subversion").click(function(){
						setTimeout(function(){//防阻塞
							var key = grid.jqGrid("getGridParam","selrow"),
							currrow = grid.jqGrid('getGridRowById', key),
							minus  = $(currrow).find('span.subversion > i.fa-minus-square-o'),
							plus = $(currrow).find('span.subversion > i.fa-plus-square-o'),
							sub,newRow;
							
							if(!currrow){
								return;
							}
							
							if(minus.length > 0){
								var rowData,parent_id=key;
								$(grid.jqGrid('getDataIDs')).each(function(){
									rowData = grid.jqGrid("getRowData",this);
									if(parent_id == rowData['parent_id']){
										parent_id = rowData['tb_id'];
										grid.jqGrid("delRowData", parent_id);
									}
								});
								$(currrow).find('span.subversion > i').removeClass('fa-minus-square-o').addClass('fa-plus-square-o');
							}
							
							if(plus.length > 0){
								invockeServiceSync('busi.formsfiles.subversion.query.service',{tb_id:key,fs_id:getSelectedLibNodeID()}, function(data, isSucess){
									if(!isSucess || !data.subversion){
										return;
									}
											
									for(var i=0; i<data.subversion.length; i++){
										sub = data.subversion[i];
//										newRow = {
//											tb_id:sub.tb_id,
//											parent_id:sub.parent_id,
//											tb_version:sub.tb_version,
//											file_title:sub.file_title,
//											document_number:sub.document_number,
//											fnsort_table:sub.fnsort_table,
//											retention_period:sub.retention_period,
//											ssecrecy_level:sub.ssecrecy_level,
//											archived_copies:sub.archived_copies,
//											total_pages:sub.total_pages
//										};
										grid.jqGrid("delRowData", sub.tb_id);
										grid.addRowData(sub.tb_id, sub,"after",key);
									}
									$(currrow).find('span.subversion > i').removeClass('fa-plus-square-o').addClass('fa-minus-square-o');
								});
							}
						},0);
					});
				}
		  	}).navGrid('#waitingJqGridPager',
				{
					alertcap:"请选中您要操作的行",alerttext:"您还没有选中行，请选择后再重新操作！",
					edit: false, editicon : 'fa fa-pencil blue',edittitle:'修改文件信息',
					add: false, addicon : 'fa fa-plus-circle purple',addtitle:'新建文件',
					del: false, delicon : 'fa fa-trash-o red',deltitle:'删除文件',
					search: true, searchicon : 'fa fa-search orange',searchtitle:'多条件查找',
					refresh: true, refreshicon : 'fa fa-refresh green',refreshtitle:'重新载入数据',
					view: false, viewicon : 'fa fa-search-plus grey',viewtitle:'查看详细',
					addfunc: function(){},
					editfunc: function(){},
					delfunc: function(){}
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
		}
	};
	
	$('[data-toggle="tooltip"]').tooltip({container:'body'});
});