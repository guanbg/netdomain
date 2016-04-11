/**
 * 后端服务调用函数
 */
function invockeService(serviceName, data, callback, currentPage, pageCount,totalRecord){//异步调用查询类服务
	if(arguments.length > 3){//如果为查询服务，则提供翻页头
		data["pagehead"] = getPageHead(currentPage,pageCount,totalRecord);
	}
	invockeServiceSync(serviceName, data, callback, true);
}
function invockeServiceSync(serviceName, data, callback, async){//同步调用更新类服务
	var retData;
    $.ajax({
    	type : "post", 
        url : serviceName+"?serviceType=post&serviceName="+escape(serviceName),
        data : JSON.stringify(data),
        dataType : 'json',
        contentType:'application/json',
        processData : false,
        async : async || false,//如果想同步 async设置为false就可以（默认是true）
        success : function(data, textStatus, jqXHR){
        	var ret = data.rethead || data.retHead || data;
        	retData = data;
        	
        	if(data && data.syshead){
        		_server_date = data.syshead.datetime;
        	}
        	
            if(ret.status == "S"){
            	//$.messager.alert('操作成功', s, msg[0].level || 'show');
            	//alert('操作成功:'+s);
            	var msg = ret.msgarr || ret.msgArr;
	        	for(var idx in msg){
	        		if(msg[idx].level == 'C' || msg[idx].level == 'D'){
	        			xAlert((msg[idx].code+" "+msg[idx].desc),null, msg[0].level);
	        		}
	        	}
            	if(!!callback && $.isFunction(callback)){
            		retData = callback(data,true);
            	}
            }
            else{
	        	var msg = ret.msgarr || ret.msgArr,
	            s='';
	        	for(var idx in msg){
	        		s += msg[idx].code+" "+msg[idx].desc+"\n";//+"<br/>";
	        	}
             	//$.messager.alert('操作失败', s, msg[0].level || 'error');
	        	xAlert((s || jqXHR.responseText),'操作失败', msg[0].level || 'D');
             	if(!!callback && $.isFunction(callback)){
             		retData = callback(data,false);
            	}
            }
        },
        error:function(data, errtype){
     		retData = data;
     		if(errtype == "parsererror"){
     			xAlert(data.responseText,"返回数据无法解析","D");
     		}
     		else{
     			xAlert(data.responseText,"网络错误","D");
     		}
        	
         	if(!!callback && $.isFunction(callback)){
         		retData = callback(data,false);
        	}
        }
	});
	return retData;
}

function getPageHead(currentPage, pageCount, totalRecord){
	return {
		currentpage: currentPage || 1,// 当前页
		pagecount: pageCount || 0,// 每页总条数
		totalpage: 0,// 总页数
		totalrecord: totalRecord || 0// 总条数
	};
}
function xAlert(msg, title, level){//暂时使用，以后进行消息规范
	alert(title+" "+msg);
}
function xMsg(msg,sec){				
	var $msg = $("#auto_close_msgbox");
	if(!$msg || $msg.length <= 0){
		$("body").append("<div id='auto_close_msgbox'>"+msg+"</div>");
		$msg = $("#auto_close_msgbox");
		$msg.css({					
			zIndex: '99999',
			position: 'fixed',
			backgroundColor:'green'
		});
	}
	else{
		$msg.text(msg||"arrrrrrr");
		$msg.show();
	}
	
	var 
	w = window.screen.clientWidth || document.documentElement.clientWidth, //屏幕宽
	h = window.screen.clientHeight || document.documentElement.clientHeight, //屏幕高
	bw = $msg.width(),
	bh = $msg.height() + 100;
	$msg.css({
		top: (h - bh) / 2 + "px",
		left: (w - bw) / 2 + "px"
	});
	window.setTimeout(function(){
		$msg.hide();
		//$msg.remove();
	}, (sec || 3)*1000);
}