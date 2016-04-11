/**
 * 
 */
jQuery(function($){
	checkChildWindow();

	$('#library_tree').jstree({
        plugins: ["wholerow","types"],
        types: {
        	libtype : {//档案库类别
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
            data:function(node,treecallback){
        		var d = {version_id:getVersionId(),fs_id:getFSId(),id_type:node.type || ''};
        		if(node.id === "#") {
        			d['id'] = '';
                }
                else {
                	d['id'] = node.id;
                }
            	invockeService('busi.version.library.query.service', d, function(data, isSucess){
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
    }).on('select_node.jstree',function(node,selected,event){//loaded.jstree ready.jstree 档案库加载完毕后加载表格
		showFileDetail(selected.node.type == 'files2');//已关联卷内文件
	});
	var getSelectedLibNodeID = function(){
		 var ref = $('#library_tree').jstree(true),
		 sel = ref.get_selected();
		 if(!sel.length) { 
			 return ""; 
		 }
		 else{
			 return sel.id || sel[0];
		 }
	},
	getSelectedLibNodeType = function(){
		 var ref = $('#library_tree').jstree(true),
		 sel = ref.get_selected();
		 if(!sel.length) { 
			 return ""; 
		 }
		 else{
			 return ref.get_node(sel).type
		 }
	},
	showFileDetail = function(isshow){
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
			invockeService('busi.version.formsfiles.get.service',{version_id:getVersionId(),fs_id:getSelectedLibNodeID()}, function(data, isSucess){
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
			invockeService('busi.version.formsfiles.get.service',{version_id:getVersionId(),fs_id:getSelectedLibNodeID()}, function(data, isSucess){
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
		
	};
	
	$('#refreshLibraryButton').on('click', function(){
		$('#library_tree').jstree('refresh');
	});
	$('#closeAllButton').on('click', function(){
		$('#library_tree').jstree('close_all');
	});
});