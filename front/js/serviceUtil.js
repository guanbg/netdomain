/*
if (!Array.prototype.indexOf) {
    Array.prototype.indexOf = function(obj, start) {
         for (var i = (start || 0), j = this.length; i < j; i++) {
             if (this[i] === obj) { return i; }
         }
         return -1;
    }
}*/
(function() {
    /**Array*/
    // Production steps of ECMA-262, Edition 5, 15.4.4.14
    // Reference: http://es5.github.io/#x15.4.4.14
    if (!Array.prototype.indexOf) {
        Array.prototype.indexOf = function(searchElement, fromIndex) {
            var k;
            // 1. Let O be the result of calling ToObject passing
            //    the this value as the argument.
            if (null === this || undefined === this) {
                throw new TypeError('"this" is null or not defined');
            }
            var O = Object(this);
            // 2. Let lenValue be the result of calling the Get
            //    internal method of O with the argument "length".
            // 3. Let len be ToUint32(lenValue).
            var len = O.length >>> 0;
            // 4. If len is 0, return -1.
            if (len === 0) {
                return -1;
            }
            // 5. If argument fromIndex was passed let n be
            //    ToInteger(fromIndex); else let n be 0.
            var n = +fromIndex || 0;
            if (Math.abs(n) === Infinity) {
                n = 0;
            }
            // 6. If n >= len, return -1.
            if (n >= len) {
                return -1;
            }
            // 7. If n >= 0, then Let k be n.
            // 8. Else, n<0, Let k be len - abs(n).
            //    If k is less than 0, then let k be 0.
            k = Math.max(n >= 0 ? n : len - Math.abs(n), 0);
            // 9. Repeat, while k < len
            while (k < len) {
                // a. Let Pk be ToString(k).
                //   This is implicit for LHS operands of the in operator
                // b. Let kPresent be the result of calling the
                //    HasProperty internal method of O with argument Pk.
                //   This step can be combined with c
                // c. If kPresent is true, then
                //    i.  Let elementK be the result of calling the Get
                //        internal method of O with the argument ToString(k).
                //   ii.  Let same be the result of applying the
                //        Strict Equality Comparison Algorithm to
                //        searchElement and elementK.
                //  iii.  If same is true, return k.
                if (k in O && O[k] === searchElement) {
                    return k;
                }
                k++;
            }
            return -1;
        };
    }
})();

document.oncontextmenu = function(event){
	if (event) {
		event.returnValue = false;
	}
	if(event && event.event){
		event.event.returnValue=false;
	}
	
	return false;
};

var createtab = parent.createtab?parent.createtab:$.noop;

function setParentIframeHeight(){
	try{parent.setIframeHeight(document.body.scrollHeight||document.documentElement.scrollHeight);}catch(e){;}
}

function getLocationParam(name){//获取html页面传递的参数
	var s = $(location).attr('search');
	if(!s){
		return '';
	}
	s = decodeURIComponent(s);
	var i = s.indexOf('?');
	if(i >= 0){
		s = s.substr(i+1);
	}
	var param = $.parseJSON('{"'+s.replace(/&/img ,'","').replace(/=/img ,'":"')+'"}');
	return !name?param:param[name] || '';
}

function checkChildWindow(){//页面有效性检查
	if(window.self == window.top){
		document.write( "错误访问，<a href='/login'>重新登录</a>");
		document.close();
	}
	//disableRightMouseKey();
}

function getLoginUserId(){//返回登录用户ID
	var userIdObj = $('#login_user_id', parent.document);
	if(userIdObj.length <= 0){
		userIdObj = $('#login_user_id');
	}
	
	return userIdObj.val() || '';
}

var _server_date = '';
function getServerDate(){
	return dateUtils.strToDate(_server_date);
}
function callIfFun(fun){
	var fn,isFun = !!fun;	
	if(!isFun){
		return false;
	}
	
	if(typeof(fun) === "string"){
		try{
			fn = eval(fun);
		}catch(e){
			;
		}
		
		if(!$.isFunction(fn)){
			try{
				fn = eval("$."+fun);
			}catch(e){
				;
			}
		}
		if($.isFunction(fn)){
			fun = fn;
		}
		else{
			return false;
		}
	}
		
	if($.isFunction(fun)){
		return fun.apply(this, [].slice.call(arguments, 1));
	}
	else{
		return false;
	}
}

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
/**
 * 装载下拉列表
 * @param serviceName 下拉列表数据的服务名称
 * @param pklistId  下拉列表的ID
 */
function loadPKList(service, pklistId, callback, nonEmpty, showValue){
	var $pklist = $("#"+pklistId);
	$pklist.empty();
	if(!!nonEmpty){//默认增加一项空值
		$pklist.append("<option value=''>"+(nonEmpty===true?"":nonEmpty)+"</option>");
	}
	else if(nonEmpty === undefined){
		$pklist.append("<option value=''></option>");
	}
	var svrName = typeof service === "string" ? service:service['svrName'];
	var svrData = typeof service === "string" ? {}:service['svrData'] || {};
	var optionValue = typeof service === "string" ? '':service['optionValue'] || '';
	invockeService(svrName,svrData,function(data,isSuccess){
		if(!isSuccess){
			if(!!callback){
	     		callback(data,isSuccess);
	    	}
			return;
		}
		var selected = '';
		$(data.pklist).each(function() {
			if(optionValue == this.value){
				selected = 'selected';
			}
			else{
				selected = '';
			}
			if(showValue){
				$pklist.append("<option value='"+this.value+"' "+selected+">"+this.value+"-"+this.text+"</option>"); 
			}
			else{
				$pklist.append("<option value='"+this.value+"' "+selected+">"+this.text+"</option>"); 
			}
		});
		
		if(!!callback){
     		callback(data,isSuccess);
    	}
	});
}
function loadPKListSync(service, pklistId, callback, nonEmpty, showValue){
	var $pklist = $("#"+pklistId);
	$pklist.empty();
	if(!!nonEmpty){//默认增加一项空值
		$pklist.append("<option value=''>"+(nonEmpty===true?"":nonEmpty)+"</option>");
	}
	else if(nonEmpty === undefined){
		$pklist.append("<option value=''></option>");
	}
	var svrName = typeof service === "string" ? service:service['svrName'];
	var svrData = typeof service === "string" ? {}:service['svrData'] || {};
	var optionValue = typeof service === "string" ? '':service['optionValue'] || '';
	invockeServiceSync(svrName,svrData,function(data,isSuccess){
		if(!isSuccess){
			if(!!callback){
	     		callback(data,isSuccess);
	    	}
			return;
		}
		var selected = '';
		$(data.pklist).each(function() {
			if(optionValue == this.value){
				selected = 'selected';
			}
			else{
				selected = '';
			}
			if(showValue){
				$pklist.append("<option value='"+this.value+"' "+selected+">"+this.value+"-"+this.text+"</option>"); 
			}
			else{
				$pklist.append("<option value='"+this.value+"' "+selected+">"+this.text+"</option>"); 
			}
		});
		
		if(!!callback){
     		callback(data,isSuccess);
    	}
	});
}
/**
 * 返回jqGrid中的动态列表的选项
 * @param serviceName 下拉列表数据的服务名称
 * @param pklistId  下拉列表的ID
 */
function getJqgridOptions(service){
	if(!service){
		return "";
	}
	var ret = "";
	var svrName = typeof service === "string" ? service:service['svrName'];
	var svrData = typeof service === "string" ? {}:service['svrData'];
	invockeServiceSync(svrName,svrData,function(data,isSuccess){
		if(!isSuccess || !data || !data.pklist){
			return;
		}
		$(data.pklist).each(function() {
			if(ret){
				ret += ";"+this.value+":"+this.text; 
			}
			else{
				ret = this.value+":"+this.text; 
			}
		});
	});
	return ret;
}

/**
 * replace icons with FontAwesome icons like above
 * */
function setJqgridPageIcon(grd){
	var replacement = {
			'ui-icon-seek-first' : 'fa fa-angle-double-left fa-lg',
			'ui-icon-seek-prev' : 'fa fa-angle-left fa-lg',
			'ui-icon-seek-next' : 'fa fa-angle-right fa-lg',
			'ui-icon-seek-end' : 'fa fa-angle-double-right fa-lg'
	};
	
	$('.ui-pg-table:not(.navtable) > tbody > tr > .ui-pg-button > .ui-icon').each(function(){
		var icon = $(this);
		var $class = $.trim(icon.attr('class').replace('ui-icon', ''));
		if($class in replacement) icon.attr('class', 'ui-icon '+replacement[$class]);
	});
	
	$('div.ui-jqgrid').css('border','0px');	
	$('div.ui-jqgrid-hdiv').css('border','0px');
	
	$('.navtable .ui-pg-button').tooltip({container:'body'});
}

/**
 * 将服务返回的数据加载到JqGrid组件中
 * @param jqGrid jqGrid的id
 * @param service 调用的服务名称
 * @param condition 需要上送的数据
 */
function setJqgridDataFunc(service,jqGrid,condition, callback){
	var grid = $(jqGrid || "#myJqGrid");
	rowNum = grid.getGridParam('rowNum') || 10000,//每页记录数
	page = grid.getGridParam('page') || 1, // 当前页
	total = grid.getGridParam('total') || 0,//总页数
	records = grid.getGridParam('records') || 0,//总记录数
	postData = grid.getGridParam('postData');
	if(page <= 1){
		records = 0;
	}
	condition = condition || {};
	if(postData && postData._search){
		if(!!postData.filters){//多条件查询
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
			var fld,filters = $.parseJSON(postData.filters);
			for(var i in filters.rules){
				fld = filters.rules[i].field;
				if(filters.rules[i].op == 'ge'){
					if(condition[fld] == undefined){
						condition[fld] = {start:filters.rules[i].data};
					}
					else{
						condition[fld].start = filters.rules[i].data;
					}
				}
				else if(filters.rules[i].op == 'le'){
					if(condition[fld] == undefined){
						condition[fld] = {end:filters.rules[i].data};
					}
					else{
						condition[fld].end = filters.rules[i].data;
					}
				}
				else{
					condition[fld] = filters.rules[i].data;
				}
			}
		}
		if(!!postData.searchField){
			condition[postData.searchField] = postData.searchString;
		}
	}
	var loadDataCallback = function(data,isSuccess){
		if(!isSuccess){
			return;
		}
		var datatype = grid.getGridParam("datatype");
		
		grid.jqGrid('setGridParam', {datatype:'local'});
		grid[0].addJSONData(data);
		grid.jqGrid('setGridParam', {datatype:datatype});
		
		if(callback){
			callback();
		}
	}
	grid.clearGridData();//清空数据
	invockeService(service,condition,loadDataCallback,page,rowNum,records);
	return false;
}

/**
 * 将数据回填到指定jqGrid组件
 * @param data 需要回填的数据
 * @param id jqGrid的id
 */
function loadJqgridData(data, id) {
	if(!data || !data.rows || data.rows.length <= 0){
		return;
	}
	
	if($.type(id) === "string" && id.substr(0, 1) != "#"){
		id = "#"+id;
	}
	var grid = $(id),
	datatype = grid.getGridParam("datatype");
	
	grid.jqGrid('setGridParam', {datatype:'local'});
	grid[0].addJSONData(data);
	
	grid.jqGrid('setGridParam', {datatype:datatype});
}

/**
 * 返回editable中的动态列表的选项
 * @param serviceName 下拉列表数据的服务名称
 * @param pklistId  下拉列表的ID
 */
function getEditableOptions(service){//此方法为同步调用，性能不好，后面逐渐淘汰，请使用setEditableOptions代替
	if(!service){
		return [];
	}
	var ret=[];
	var svrName = typeof service === "string" ? service:service['svrName'];
	var svrData = typeof service === "string" ? {}:service['svrData'];
	
	invockeServiceSync(svrName,svrData,function(data,isSuccess){
		if(!isSuccess || !data || !data.pklist){
			return [];
		}
		ret = data.pklist; 
	});
	
	return ret;
}
/**
 * 通过回调函数设置editable中的动态列表的选项
 * @param serviceName 下拉列表数据的服务名称
 * @param pklistId  下拉列表的ID
 */
function setEditableOptions(service, callback){
	if(!service){
		return [];
	}
	var ret=[];
	var svrName = typeof service === "string" ? service:service['svrName'];
	var svrData = typeof service === "string" ? {}:service['svrData'];
	
	if(!!callback){
		invockeService(svrName,svrData,function(data,isSuccess){
			if(!isSuccess || !data || !data.pklist){
				callback(ret,false);
				return [];
			}
			ret = data.pklist; 
			callback(ret,true);
		});
	}
	else{
		invockeServiceSync(svrName,svrData,function(data,isSuccess){
			if(!isSuccess || !data || !data.pklist){
				return [];
			}
			ret = data.pklist; 
		});		
	}
	return ret;
}
function loadSelectOption(data, id, nonEmpty){
	var $select = $("#"+id);
	$select.empty();
	if(!nonEmpty){//默认增加一项空值
		$select.append("<option value=''></option>");
	}
	$(data).each(function(){
		$select.append("<option value='"+this.value+"'>"+this.text+"</option>"); 
	});
	return $select;
}

function myValidation(div){
	var target = !!div ? $.type(div) === "string" ? $(div) : div :$("body");
	var msg,val, b = true;
	$("input,select,textarea", target).not("[type=submit]").filter(function(){
		return $(this).attr('data-validation-required-message') !== undefined || $(this).attr('data-validation-length-message') !== undefined;
	}).each(function(){
		if(!b){
			return;
		}
		
		val = $(this).val();
		
		msg = $(this).attr('data-validation-required-message');
		if(msg){
			if(val == null || val == undefined || val == "" || val.length <= 0){
				b = false;
				xAlert(msg,$(this));
				return;
			}
		}
		
		msg = $(this).attr('data-validation-length-message');
		if(msg){
			var min, max, len = val.length,splt = msg.split(/\D/);
			for(var i=0; i<splt.length; i++){
				if(splt[i]){
					if(!min){
						min = splt[i];
					}
					else{
						max = splt[i];
					}
				}
			}
			if((min && len < min * 1) || (max && len > max * 1)){
				b = false;
				xAlert(msg,$(this));
				return;
			}
		}
	});
	return b;
}
/**
 * 将指定范围的数据打包成JSON对象
 * @param div 打包数据的范围，如果没有指定则将整个页面的数据打包
 */
function packData(div,fun){
	var target = !!div ? $.type(div) === "string" ? $(div) : div :$("body"),
	isFun = fun && $.isFunction(fun),
	rCRLF = /\r?\n/g,
	rselectTextarea = /^(?:select|textarea)/i,
	rinput = /^(?:color|date|datetime|datetime-local|email|hidden|month|number|password|range|search|tel|text|time|url|week)$/i;
	
	var data = {};
	$("*:not(.nopack)", target).map(function(){
		return this.elements ? $.makeArray( this.elements ) : this;
	})
	.filter(function(){
		return (this.name || this.id) && ($(this).hasClass('pack-data') || this.checked || rselectTextarea.test(this.nodeName) || rinput.test(this.type));
	})
	.map(function(i, elem){
		var val = $(this).val() || $(this).data('packdata') || $(this).text() || null,
		id = $(this).is(":checked")?(elem.name || elem.id):(elem.id || elem.name),//:radio:checked
		vl = val == null ? null: $.isArray(val) ? $.map( val, function( v, i ){return typeof v == 'string' ? v.replace(rCRLF, "\r\n") : v;}) : typeof val == 'string' ? val.replace( rCRLF, "\r\n") : val;
		if(isFun){
			$.extend(data,fun(id,vl));
		}
		else{
			if($(this).is("input[type='checkbox']") && data[id] != undefined && data[id] != null){//checkbox;
				if($.isArray(data[id])){
					data[id].push(vl);
				}
				else{
					data[id] = [data[id],vl];
				}
			}
			else{
				data[id] = vl;
			}
		}
	});
	return data;
}
function packSelectAll(id, hasText){
	var select = !!id ? $.type(id) === "string" ? $(id) : id :$("body");
	var data = [];
	$('option', select).each(function(){
		if(!hasText){
			data.push(this.value);
		}
		else{
			data.push({value:this.value, text:this.text});
		}
	});
	return data;
}

/**
 * 将数据回填到指定的块
 * @param data 需要回填的数据
 * @param div 回填范围，如果没有指定则是整个页面
 */
function loadDivData(data, div, fun){
	var target = !!div ? $.type(div) === "string" ? $(div) : div :$("body"),
		isFun = fun && $.isFunction(fun);
	
	var _validate =function (){
		if ($.fn.validatebox){
			target.find('.validatebox-text:not(:disabled)').validatebox('validate');
			var invalidbox = target.find('.validatebox-invalid');
			invalidbox.filter(':not(:disabled):first').focus();
			return invalidbox.length == 0;
		}
		return true;
	},
	_checkField = function (name, val){
		var fld = $("input[name='"+name+"'][type='radio'],input[id='"+name+"'][type='radio'],input[name='"+name+"'][type='checkbox'],input[id='"+name+"'][type='checkbox']", target);
		if(!fld.length){
			return false;
		}
		fld.each(function(){
			var f = $(this);
			if($.isArray(val)){
				$(val).each(function(){
					if (f.val() == String(this)){
						$.fn.prop ? f.prop('checked',true) : f.attr('checked',true);
						return false;
					}
					else{
						$.fn.prop ? f.prop('checked',false) : f.removeAttr('checked');
					}
				});
			}
			else{
				if (f.val() == String(val)){
					$.fn.prop ? f.prop('checked',true) : f.attr('checked',true);
				}
				else{
					$.fn.prop ? f.prop('checked',false) : f.removeAttr('checked');
				}
			}
		});
		return true;
	},
	_numberBox = function(name, val){
		var fld = target.find('input[numberboxName="'+name+'"]');
		if (fld.length){
			fld.numberbox('setValue', val);	// set numberbox value
			return true;
		}
		else{
			return false;
		}
	},
	_inputField = function(name, val){
		var fld = $('input[name="'+name+'"], input[id="'+name+'"]', target);
		if(fld.length) {fld.val(val);return true;}
		
		fld = $('textarea[name="'+name+'"], textarea[id="'+name+'"]', target);
		if(fld.length) {fld.val(val);return true;}
		
		fld = $('select[name="'+name+'"], select[id="'+name+'"]', target);
		if(fld.length) {fld.val(val);return true;}
		
		return false;
	},
	_comboField = function(name, val){
		var cc = ['combobox','combotree','combogrid','datetimebox','datebox','combo'];
		var c = target.find('[comboName="' + name + '"]');
		if (c.length){
			for(var i=0; i<cc.length; i++){
				var type = cc[i];
				if (c.hasClass(type+'-f')){
					if (c[type]('options').multiple){
						c[type]('setValues', val);
					} else {
						c[type]('setValue', val);
					}
					return true;
				}
			}
		}
		return false;
	};
	
	if($.isArray(data)){
		data = data[0];
	}
	//var hasOwn = $({}).hasOwnProperty;
	for(var nm in data){
		var name = nm;
		var val = data[name] || '';
		
		if(isFun){
			var r = fun(name,val);
			if(r === true){//如果函数返回true,则不再执行后面的自动回填功能，如果返回字符，则表示新的id或name,则继续走自动回填功能
				continue;
			}
			else{
				name = r;
			}
		}
		
		if($("#"+name, target).hasClass('nofill') || $("#"+name, target).attr("nofill") != undefined || $("#"+name, target).parents('[nofill]').length > 0){
			continue;
		}
		
		if(!_validate(name, val)){//有效性验证
			continue;
		}
		
		if(_checkField(name, val)){
			continue;
		}
		else if(_numberBox(name, val)){
			continue;
		}
		else if(_inputField(name, val)){
			continue;
		}
		else if(_comboField(name, val)){
			continue;
		}
		else{
			var obj = $("#"+name, target);
			if(!obj || obj.length <= 0){
				var $result = target.find("[name='"+name+"']");
				if ($result.length <= 0) {
					continue;
				}
				else{
					obj = $result[0];
				}
			}
			try{
				obj.text(val);//有待改进，暂时如此
			}catch(e){
				;
			}
		}
	}
}
function clearField(div, fun){
	var target = !!div ? $.type(div) === "string" ? $(div) : div :$("body"),
	isFun = fun && $.isFunction(fun),
	rselectTextarea = /^(?:select|textarea)/i,
	rinput = /^(?:color|date|datetime|datetime-local|email|hidden|month|number|password|range|search|tel|text|time|url|week)$/i;

	$("*", target).map(function(){
		return this.elements ? $.makeArray( this.elements ) : this;
	})
	.filter(function(){
		return (this.name || this.id) && (!$(this).hasClass('noclear') && $(this).parents('.noclear').length <= 0 && $(this).attr("noclear") == undefined && $(this).parents('[noclear]').length <= 0);
	})
	.map(function( i, elem ){
		if(isFun && fun($(this),elem,i) !== false){//如果函数返回false,则不再执行后面的自动回填功能，如果返回true,则继续走自动回填功能
			return;
		}
		
		if(this.type == "button"){
			return;
		}
		if(this.checked){
			$(this).prop('checked',false);
		}
		else if(rselectTextarea.test( this.nodeName ) || rinput.test( this.type )){
			$(this).val("");
		}
		else{
			$(this).text("");
		}
		if($(this).hasClass('pack-data')){
			$(this).data('packdata','');
		}
	});
}
function getPageHead(currentPage, pageCount, totalRecord){
	return {
		currentpage: currentPage || 1,// 当前页
		pagecount: pageCount || 0,// 每页总条数
		totalpage: 0,// 总页数
		totalrecord: totalRecord || 0// 总条数
	};
}
function getImgDownloadUrl(filePathName, filetype){
	if(!filePathName){
		return;
	}
	var idx = filePathName.lastIndexOf('.');
	if(!filetype && idx > 0){
		filetype = filePathName.substr(idx+1);
		filePathName = filePathName.substr(0,idx);
	}
	return 'sys.image.files?isdecrypt=false&filetype='+filetype+'&filepathname='+filePathName+'&_t='+(new Date().getTime());
}
function getDownloadUrl(filePathName, fileSaveName){
	if(!filePathName){
		return;
	}
	return 'sys.download.files?isdecrypt=false&isopen=false&filepathname='+filePathName+'&filesavename='+fileSaveName+'&_t='+(new Date().getTime());
}
function downloadFile(filePathName, fileSaveName) {
	if(!filePathName){
		return;
	}
	var newWin = window.open('../sys.download.files?isdecrypt=false&isopen=true&filepathname='+filePathName+'&filesavename='+fileSaveName+'&_t='+(new Date().getTime()));
	if (!newWin || !newWin.top) {
    	alert("你的浏览器阻止了本次下载，请进入[工具\Internet选项\隐私\阻止弹出窗口]进行设置，然后重新进入系统");
	}
}

var dateUtils = function() {

	function getDate(frt, y, m, d) {// 按照指定的日期格式返回日期字符串
		var date = new Date();
		var yy = date.getFullYear() + (y || 0);
		var mm = date.getMonth() + 1 + (m || 0);
		var dd = date.getDate() + (d || 0);

		if (!frt) {
			return yy + "年" + (mm > 9 ? mm : '0' + mm) + "月"
					+ (dd > 9 ? dd : '0' + dd) + "日";
		} else {
			var dt = frt.replace(/y+/img, yy);
			dt = dt.replace(/m+/img, (mm > 9 ? mm : '0' + mm));
			dt = dt.replace(/d+/img, (dd > 9 ? dd : '0' + dd));
			return dt;
		}
	}
	/**
	* dt:需要格式化的日期
	* frt:格式化后的格式
	* y:年份加y年后按给定的格式返回
	* m:月份加m月后按给定的格式返回
	* d:天数加d天后按给定的格式返回
	*/
	function formatDate(dt, frt, y, m, d) {// 日期格式化
		var odt;

		if (!dt) {
			return '';
		} else if (isDate(dt)) {
			odt = dt;
		}

		var yy, mm, dd, hh, ii, ss;
		if (odt) {
			yy = odt.getFullYear();
			mm = odt.getMonth() + 1;
			dd = odt.getDate();

			hh = odt.getHours();
			ii = odt.getMinutes();
			ss = odt.getSeconds();
		} else {
			var s = "", part = [];
			if(isNumeric(dt)){
				var ss = dt + "";
				part.push(ss.substr(0,4));
				part.push(ss.substr(4,2));
				part.push(ss.substr(6,2));
				part.push(ss.substr(8,2) || 0);
				part.push(ss.substr(10,2) || 0);
				part.push(ss.substr(12,2) || 0);
			}
			else{
				for ( var k = 0; k < dt.length; k++) {
					if (isNumeric(dt.charAt(k))) {
						s += dt.charAt(k);
					} else if (s && s.length > 0) {
						part.push(s);
						s = "";
					}
				}
				if (s && s.length > 0) {
					part.push(s);
					s = "";
				}
			}

			yy = parseInt(part[0] || 0, 10);
			mm = parseInt(part[1] || 0, 10);
			dd = parseInt(part[2] || 0, 10);

			hh = parseInt(part[3] || 0, 10);
			ii = parseInt(part[4] || 0, 10);
			ss = parseInt(part[5] || 0, 10);
		}

		yy += (y || 0);
		mm += (m || 0);
		dd += (d || 0);

		var ret;
		if (!frt) {
			ret = yy + "年" + (mm > 9 ? mm : '0' + mm) + "月" + (dd > 9 ? dd : '0' + dd) + "日 ";
			if(hh > 0){
				ret += (hh > 9 ? hh : '0' + hh)+'点 ';
			}
			if(ii > 0){
				ret += (ii > 9 ? ii : '0' + ii)+'分 ';
			}
			if(ss > 0){
				ret += (ss > 9 ? ss : '0' + ss)+'秒';
			}
		} else {
			ret = frt.replace(/y+/img, yy);
			ret = ret.replace(/m+/img, (mm > 9 ? mm : '0' + mm));
			ret = ret.replace(/d+/img, (dd > 9 ? dd : '0' + dd));
			ret = ret.replace(/h+/img, (hh > 9 ? hh : '0' + hh));
			ret = ret.replace(/i+/img, (ii > 9 ? ii : '0' + ii));
			ret = ret.replace(/s+/img, (ss > 9 ? ss : '0' + ss));
		}
		return ret;
	}
	function isDate(value) {
		if (value === null) {
			return false;
		}
		var type = typeof value;
		if (type === 'undefined' || type === 'string' || type === 'number'
				|| type === 'boolean') {
			return false;
		}
		if (value instanceof Date) {
			return true;
		}

		return toString.call(value) === '[object Date]';
	}
	function isNumeric(value) {
		return !isNaN(parseFloat(value)) && isFinite(value);
	}
	function subDate(endDate,startDate){//返回endDate-startDate后相差的天数
		var s = '',
		asecond = 1000,//毫秒
		aminute = asecond * 60,
		ahour = aminute * 60,
		aday = ahour * 24,
		n = $.isNumeric(endDate)? endDate: strToDate(endDate).valueOf() - strToDate(startDate).valueOf();//相差毫秒
		
		if(n >= aday){
			s += Math.floor(n / aday)+'天';
			n = n % aday;
		}
		
		if(n >= ahour){
			s += Math.floor(n / ahour)+'小时';
			n = n % ahour;
		}
		
		if(n >= aminute){
			s += Math.floor(n / aminute)+'分钟';
			n = n % aminute;
		}
		
		if(n >= asecond){
			s += Math.floor(n / asecond)+'秒';
			n = n % asecond;
		}
		
		return s;
	}
	function strToDate(dt) {// 将给定的日期字符串转换为日期对象
		if (!dt) {
			return new Date();
		}
		if (isDate(dt)) {
			return dt;
		}
		var s = "", part = [];
		for ( var k = 0; k < dt.length; k++) {
			if (isNumeric(dt.charAt(k))) {
				s += dt.charAt(k);
			} else if (s && s.length > 0) {
				part.push(s);
				s = "";
			}
		}
		if (s && s.length > 0) {
			part.push(s);
			s = "";
		}
		
		for(;;){
			if(part.length > 6){
				break;
			}
			part.push(0);
		}
		return new Date(parseInt(part[0] || 0, 10),
				parseInt(part[1] || 0, 10) - 1, parseInt(part[2] || 0, 10),
				parseInt(part[3] || 0, 10), parseInt(part[4] || 0, 10),
				parseInt(part[5] || 0, 10));
	}
	function formatNumber(s, n){//数字格式化，将数字s格式化后保留小数点后n位
		if(!s){
			return s;
		}
		var ss = (""+s).split(".");
		if(ss.length > 1 && !n){
			n = ss[1].length;
		}
   		n = n > 0 && n <= 20 ? n : 2;
   		
   		s = parseFloat((s + "").replace(/[^\d\.-]/g, "")).toFixed(n) + "";  
   		var l = s.split(".")[0].split("").reverse(),  
   		r = s.split(".")[1];  
   		t = "";  
   		for(i = 0; i < l.length; i ++ ){  
      		t += l[i] + ((i + 1) % 3 == 0 && (i + 1) != l.length ? "," : "");  
   		}  
   		return t.split("").reverse().join("") + "." + r;  
	}
	function formatTime(tm, frt){//时间格式化
		if(!tm){
			return "";
		}
		if(!frt){
			frt = "h点m分s秒";
		}
		var h = tm.substr(0,2);
		var m = tm.substr(2,2);
		var s = tm.substr(4);
		var ret = frt.replace(/h+/img, h);
		ret = ret.replace(/m+/img, m);
		ret = ret.replace(/s+/img, s);
		return ret;
	}

    function formatMoney(n) {  
		var fraction = ['角', '分'];  
		var digit = ['零', '壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖'];  
		var unit = [['元', '万', '亿'],['', '拾', '佰', '仟']];  
		var head = n < 0? '欠': '';  
		var s = '';  

		n = Math.abs(n);  
		for (var i = 0; i < fraction.length; i++) {  
			s += (digit[Math.floor(n * 10 * Math.pow(10, i)) % 10] + fraction[i]).replace(/零./, '');  
		}  
		s = s || '整';  
		n = Math.floor(n);  
		for (var i = 0; i < unit[0].length && n > 0; i++) {  
			var p = '';  
			for (var j = 0; j < unit[1].length && n > 0; j++) {  
                p = digit[n % 10] + unit[1][j] + p;  
                n = Math.floor(n / 10);  
			}  
			s = p.replace(/(零.)*零$/, '').replace(/^$/, '零') + unit[0][i] + s;  
		}  
		return head + s.replace(/(零.)*零元/, '元').replace(/(零.)+/g, '零').replace(/^整$/, '零元整');  
    }

	return {
		getDate: getDate,
		subDate:subDate,
		strToDate : strToDate,
		formatTime : formatTime,
		formatDate : formatDate,
		formatNumber: formatNumber,
		formatMoney: formatMoney
	};
}();

function jqGridFormatDate(cellvalue, options, rowObject){
	var ret = "";
	if(!!cellvalue){
		ret = dateUtils.formatDate(cellvalue);
	}
	return !!ret?ret:"&nbsp;"
}

var fileUploadFuncList = function(){
	var rheader = new RegExp(/^<h\d>.*http.*status.*(\d{3}.*)<\/h\d>/img);
	var parent = this;
	this.counter = 10; 
	this.idx = 1;//当前序号
	this.uploadFlag = false;
	this.prefix = "fileUpload";//元素前缀
	this.fileUploadFrame = this.prefix+"Frame";
	this.fileUploadForm = this.prefix+"Form";
	this.fileUploadButton = this.prefix+"Button";
	this.fileUploadTable = this.prefix+"Table";
	this.onSuccessCallback;
	
	this.callback = function(frame){
		if(!parent.uploadFlag){
			return;
		}
		frame = $("#"+parent.fileUploadFrame).get(0);
		var responseText;
		try {
			var doc = frame.contentWindow.document || frame.contentDocument || window.frames[parent.fileUploadFrame].document;
			if (doc) {
				if (doc.body) {
					if (/textarea/i.test((firstChild = doc.body.firstChild || {}).tagName)) { // json response wrapped in textarea
						responseText = firstChild.value;
					} else {
						responseText = doc.body.innerHTML;
					}
				}
			}
		} catch (e) {
			parent.updateStatus("上传失败");
			if(parent.onSuccessCallback){
				parent.onSuccessCallback(false);
			}
			return ;
		}
		if(parent.counter <= 0){
			parent.updateStatus("上传失败");
			if(parent.onSuccessCallback){
				parent.onSuccessCallback(false);
			}
			return;
		}
		else if (!parent.isCompleted(responseText)) {
			if(parent.counter <= 0){
				parent.updateStatus(RegExp?RegExp.$1:'上传错误');
				if(parent.onSuccessCallback){
					parent.onSuccessCallback(false);
				}
			}
			else{
				parent.updateStatus("正在返回...");
				window.setTimeout(parent.callback, 200);
			}
			
			return;
		}
		
		var data = $.parseJSON(responseText);
		var ret = data.rethead || data.retHead || data;
		if(ret.status == "S"){
			var flag = true;
			if(parent.onSuccessCallback){
				flag = parent.onSuccessCallback(data);
			}
			if(flag){
				parent.updateStatus("成功", parent.getFileSize(data.files[0].filesize), data.fileids.id || data.fileids[0].id);
			}
			else{
				parent.updateStatus("上传后处理出错");
			}
		}
		else{
			var msg = ret.msgarr || ret.msgArr,
            s='';
        	for(var i in msg){
        		s += msg[i].code+" "+msg[i].desc+"\n";//+"<br/>";
        	}
        	parent.updateStatus(s);
        	
        	if(parent.onSuccessCallback){
        		parent.onSuccessCallback(false);
			}
		}
		parent.uploadFlag = false;
	};
	
	this.callback2 = function(frame){//此函数内的this指针指向的是UploadFrame
		if(!parent.uploadFlag){
			return;
		}
		frame = $("#"+parent.fileUploadFrame).get(0);
		var responseText;
		try {
			var doc = frame.contentWindow.document || frame.contentDocument || window.frames[parent.fileUploadFrame].document;
			if (doc) {
				if (doc.body) {
					if (/textarea/i.test((firstChild = doc.body.firstChild || {}).tagName)) { // json response wrapped in textarea
						responseText = firstChild.value;
					} else {
						responseText = doc.body.innerHTML;
					}
				}
			}
		} catch (e) {
			alert("上传失败");
			if(parent.onSuccessCallback){
				parent.onSuccessCallback(false);
			}
			return ;
		}
		if(parent.counter <= 0){
			alert("上传失败");
			if(parent.onSuccessCallback){
				parent.onSuccessCallback(false);
			}
			return;
		}
		else if (!parent.isCompleted(responseText)) {
			if(parent.counter <= 0){
				alert(RegExp?RegExp.$1:'上传错误');
				if(parent.onSuccessCallback){
					parent.onSuccessCallback(false);
				}
			}
			else{
				window.setTimeout(parent.callback2, 200);
			}
			
			return;
		}
		
		var data = $.parseJSON(responseText);
		var ret = data.rethead || data.retHead || data;
		if(ret.status == "S"){
			if(parent.onSuccessCallback){
				parent.onSuccessCallback(data);
			}
			else{
				alert("上传成功:" + parent.getFileSize(data.files[0].filesize));
			}
		}
		else{
			var msg = ret.msgarr || ret.msgArr,
            s='';
        	for(var i in msg){
        		s += msg[i].code+" "+msg[i].desc+"\n";//+"<br/>";
        	}
        	alert(s);
        	if(parent.onSuccessCallback){
        		parent.onSuccessCallback(false);
			}
		}
		parent.uploadFlag = false;
	};
	this.reset = function(){
		this.counter = 10;
		this.idx = 1;
		this.uploadFlag = false;
		
		$("#"+this.fileUploadTable+" > tbody tr").each(function(i, obj){
			if($(this).find("td:eq(0)").text()){//不能删除上传按钮所在行,暂不删除后台文件
				$(this).remove();
			}
		});
		var file = $("#"+this.fileUploadButton+" > input[type='file']");
		var v = file.val();
		if(v && v.length > 0){
			file.after(file.clone().val(""));     
			file.remove();
		}
	};
	this.reset2 = function(){
		this.counter = 10;
		this.idx = 1;
		this.uploadFlag = false;
		
		var file = $("#"+this.fileUploadButton+" > input[type='file']");
		var v = file.val();
		if(v && v.length > 0){
			file.after(file.clone().val(""));     
			file.remove();
		}
	};
	this.upload = function(self, successCallback){
		this.counter = 10;		
		this.uploadFlag = true;
		var ie8 = "C:\\fakepath\\";
		var fileName = $(self).val();
		
		this.prefix = $(self).data("prefix") || 'fileUpload'; 
		this.fileUploadFrame = this.prefix+"Frame";
		this.fileUploadForm = this.prefix+"Form";
		this.fileUploadTable = this.prefix+"Table";
		this.fileUploadButton = this.prefix+"Button";
		
		this.addRow(fileName.replace(ie8,""), "", "开始传输...");
		
		$("#"+this.fileUploadFrame).load(this.callback);
		$("#"+this.fileUploadForm).attr('target', this.fileUploadFrame);
		$("#"+this.fileUploadForm).submit();
		this.onSuccessCallback = successCallback;
	};
	this.upload2 = function(self, successCallback){
		this.counter = 10;		
		this.uploadFlag = true;
		var ie8 = "C:\\fakepath\\";
		var fileName = $(self).val();
		
		this.prefix = $(self).data("prefix") || 'fileUpload'; 
		this.fileUploadFrame = this.prefix+"Frame";
		this.fileUploadForm = this.prefix+"Form";
		this.fileUploadTable = this.prefix+"Table";
		this.fileUploadButton = this.prefix+"Button";
		
		$("#"+this.fileUploadFrame).load(this.callback2);
		$("#"+this.fileUploadForm).attr('target', this.fileUploadFrame);
		$("#"+this.fileUploadForm).submit();
		this.onSuccessCallback = successCallback;
	};
	this.addRow = function(name, size, status){
		var newRow = "<tr  class='tr0'><td align='center'>"+this.idx+"</td><td>"+name+"</td><td  align='right'>"+size+"</td><td align='center'></td><td>"+status+"</td><td align='center'><a href='###' onclick='javascript:fileUpload.delRow(this);'>删除</a></td></tr>";
		$("#"+this.fileUploadTable+" tr:last").before(newRow);
		this.idx++;
	};
	this.delRow = function(self){
		var id = $("td:eq(3)",$(self).parents("tr")).text();
		if(!id){
			$(self).parents("tr").remove();
			parent.idx = 1;
			$("#"+this.fileUploadTable+" > tbody tr").each(function(i, obj){
				if($(this).find("td:eq(0)").text()){
					$(this).find("td:eq(0)").text(parent.idx++);
				}
			});
			
			return true;
		}
		if(!confirm("确定要删除该文件吗？")){
			return false;
		}
		invockeServiceSync("sys.file.del.service", { id:id}, function(data ,isSuccess){
			if(!isSuccess){
				return;
			}
			$(self).parents("tr").remove();
			parent.idx = 1;
			$("#"+this.fileUploadTable+" > tbody tr").each(function(i, obj){
				if($(this).find("td:eq(0)").text()){
					$(this).find("td:eq(0)").text(parent.idx++);
				}
			});
  		});
	};
	this.updateStatus = function(status, size,identify){
		if(!!status) $("#"+this.fileUploadTable+" tr:gt(0):eq("+(this.idx-2)+") td:eq(4)").text(status);
		if(!!identify) $("#"+this.fileUploadTable+" tr:gt(0):eq("+(this.idx-2)+") td:eq(3)").text(identify);
		if(!!size) $("#"+this.fileUploadTable+" tr:gt(0):eq("+(this.idx-2)+") td:eq(2)").text(size);
	};
	this.isCompleted = function(responseText) {
		if (!responseText) {
			this.counter--;
			return false;
		}
		try {
			$.parseJSON(responseText);
		} catch (e) {
			if(rheader.test(responseText)){
				this.counter = 0;
			}
			else{
				this.counter--;
			}
			return false;
		}
		return true;
	};
	this.getFiles = function(){
		var fileName,fileIdentify,files=[];
		$("#"+this.fileUploadTable+" > tbody tr").each(function(i, obj){
			if($(this).find("td:eq(3)").text()){//文件标识
				fileName = $(this).find("td:eq(1)").text();
				fileIdentify = $(this).find("td:eq(3)").text();
				files.push({fileName:fileName,id:fileIdentify});
			}
		});
		if(files.length <= 0){
			return [];
		}
		
		return files;
	};
	this.getFilesIdentify = function(){
		var filesIdentify=[];
		$("#"+this.fileUploadTable+" > tbody tr").each(function(i, obj){
			if($(this).find("td:eq(3)").text()){//文件标识
				filesIdentify.push($(this).find("td:eq(3)").text());
			}
		});
		if(filesIdentify.length <= 0){
			return [];
		}
		
		return filesIdentify;
	};
	this.getFileSize = function(size) {
        var string;
        if (size >= 1024 * 1024 * 1024 * 1024) {
          size = size / (1024 * 1024 * 1024 * 1024);
          string = "TB";
        } else if (size >= 1024 * 1024 * 1024) {
          size = size / (1024 * 1024 * 1024);
          string = "GB";
        } else if (size >= 1024 * 1024) {
          size = size / (1024 * 1024);
          string = "MB";
        } else if (size >= 1024) {
          size = size / 1024;
          string = "KB";
        } else {
          string = "b";
        }
        return (Math.round(size) / 10) + string;
      };
      this.showLoading = function (img){
    	  var fullPath = window.location.href;
    	  var relaPath = window.location.pathname
    	  var imgPath = fullPath.replace(relaPath,'/img/loading.gif');
    	  
    	  $(img).attr('src',imgPath);
    	  $(img).show();
      };
	//return {upload : upload, upload2 : upload2, delRow : delRow, reset : reset, reset2 : reset2, getFiles:getFiles, getFileSize:getFileSize};
};
var fileUploadList = new fileUploadFuncList();

/**
 * 文件上传
 */
var fileUploadFunc = function(uploadedCallback){
	var rheader = new RegExp(/^<h\d>.*http.*status.*(\d{3}.*)<\/h\d>/img);
	var parent = this;
	this.counter = 10; 
	this.uploadFlag = false;
	this.fileUploadFrame = null;
	this.fileUploadForm = null;
	this.uploadBtn = null;
	this.onCallback = uploadedCallback || null;
	this.data = null;
	
	this.upload = function(self, uploadedCallback){
		this.counter = 10;		
		this.uploadFlag = true;
		this.onCallback = uploadedCallback || this.onCallback;
		
		this.fileUploadForm = $(self).closest("form");
		this.fileUploadFrame = this.fileUploadForm.find("iframe");
		this.uploadBtn = $(self).closest(".file-upload").find("i");
		
		this.showLoading(loadingurl);
		
		var filename = $(self).attr("name");//必须要有name属性才能上送
		if(!filename){
			for(i=0;i<10000;i++){
				if($("#filename_"+i).length <= 0){
					filename = "filename_"+i;
					$(self).attr("name",filename);
					break;
				}
			}
		}
		if(!filename){
			xAlert('提交错误，不能上传文件');
			return;
		}
		var frameid = null;
		if(!this.fileUploadFrame || this.fileUploadFrame.length <= 0){
			if(!frameid){
				for(i=0;i<10000;i++){
					if($("#frame_"+i).length <= 0){
						frameid = "frame_"+i;
						break;
					}
				}
			}
			this.fileUploadForm.append("<iframe id='"+frameid+"' name='"+frameid+"' style='width:0px;height:0px;margin:0px;padding:0px;display:none;' src='javascript:false'></iframe>");
			this.fileUploadFrame = $("#"+frameid);
		}
		else{
			frameid = this.fileUploadFrame.attr('id');
		}
		if(!frameid){
			xAlert('提交错误，不能上传文件');
			return;
		}
		this.fileUploadFrame.load(this._callback);
		this.fileUploadForm.attr('target', frameid);
		this.fileUploadForm.submit();
	};
	
	this.showLoading = function (imgurl){
  	  	if(!parent.uploadBtn || parent.uploadBtn.length <= 0){
  	  		return;
  	  	}
  	  
  	  	parent.uploadBtn.addClass(imgurl||'icon-spinner icon-spin icon-2x');
  	  	parent.uploadBtn.show();
    };
    
	this.reset = function(){
		parent.counter = 10;
		parent.uploadFlag = false;
		
		var file = parent.fileUploadForm.find("input[type='file']");
		var v = file.val();
		if(v && v.length > 0){
			file.after(file.clone().val(""));     
			file.remove();
		}
		
		parent.uploadBtn.removeClass('icon-spinner icon-spin icon-2x');
		
		var firstChild, frame = parent.fileUploadFrame.get(0);
		try {
			var doc = frame.contentWindow.document || frame.contentDocument || window.frames[parent.fileUploadFrame].document;
			if (doc) {
				if (doc.body) {
					if (/textarea/i.test((firstChild = doc.body.firstChild || {}).tagName)) { // json response wrapped in textarea
						firstChild.value = '';
					} else {
						doc.body.innerHTML = '';
					}
				}
			}
		} catch (e) {
			return ;
		}
	};
	this.getFileIds = function(){
		var ids = [];
		$('ol li',parent.fileUploadForm).each(function(){
			ids.push($(this).data('id'));
		});
		
		return ids;
	};
	
	this._addRow = function(data, id,name){
		var $ol = this.fileUploadForm.find("ol");
		if(!$ol || $ol.length <= 0){
			this.fileUploadForm.append("<ol class='noclear'></ol>");
			$ol = this.fileUploadForm.find("ol");
		}
		var dt = data.files[0];
		id = data.fileids.id || data.fileids[0].id;
		name = dt.filename;
		$ol.append("<li data-id='"+id+"'><span>"+name+"</span><i class='ace-icon fa fa-remove red'  style='cursor:pointer; padding:4px'></i></li>");
		$('li i',$ol).click(function(){
			if(!confirm("确定要删除该文件吗？")){
				return false;
			}
			var $li = $(this).parent();
			invockeServiceSync("sys.file.del.service", { id:$li.data('id')}, function(data ,isSuccess){
				if(!isSuccess){
					return;
				}
				$li.remove();
	  		});
			return false;//取消冒泡
		});
	};
	this._isCompleted = function(responseText) {
		if (!responseText) {
			this.counter--;
			return false;
		}
		try {
			$.parseJSON(responseText);
		} catch (e) {
			if(rheader.test(responseText)){
				this.counter = 0;
			}
			else{
				this.counter--;
			}
			return false;
		}
		return true;
	};
	this._callback = function(frame){//此函数内的this指针指向的是UploadFrame
		parent.data = null;
		if(!parent.uploadFlag){
			return;
		}
		frame = parent.fileUploadFrame.get(0);
		var firstChild, responseText;
		try {
			var doc = frame.contentWindow.document || frame.contentDocument || window.frames[parent.fileUploadFrame].document;
			if (doc) {
				if (doc.body) {
					if (/textarea/i.test((firstChild = doc.body.firstChild || {}).tagName)) { // json response wrapped in textarea
						responseText = firstChild.value;
					} else {
						responseText = doc.body.innerHTML;
					}
				}
			}
		} catch (e) {
			xAlert("上传失败");
			if(parent.onCallback){
				parent.onCallback(false);
			}
			return ;
		}
		if(parent.counter <= 0){
			xAlert("上传失败");
			if(parent.onCallback){
				parent.onCallback(false);
			}
			return;
		}
		else if (!parent._isCompleted(responseText)) {
			if(parent.counter <= 0){
				xAlert(RegExp?RegExp.$1:'上传错误');
				if(parent.onCallback){
					parent.onCallback(false);
				}
			}
			else{
				window.setTimeout(parent.callback, 200);
			}
			return;
		}
		else if(responseText === "false"){
			xAlert("浏览器不允许上传文件，请检查文件上传框的相关信息设置是否正确!");
			parent.showLoading(false);
			if(parent.onCallback){
				parent.onCallback(false);
			}
			return;
		}
		
		var data = $.parseJSON(responseText);
		var ret = data.rethead || data.retHead || data;
		parent.data = data;
		if(ret.status == "S"){
			parent._addRow(data);
			if(parent.onCallback){
				parent.onCallback(data);
			}
			else{
				xMsg("上传成功:" + parent.getFileSize(data.files[0].filesize));
			}
		}
		else{
			var msg = ret.msgarr || ret.msgArr,
            s='';
        	for(var i in msg){
        		s += msg[i].code+" "+msg[i].desc+"\n";//+"<br/>";
        	}
        	xAlert(s);
        	if(parent.onCallback){
        		parent.onCallback(false);
			}
		}
		parent.reset();
		parent.uploadFlag = false;
	};
    
	this.getFileSize = function(size) {
        var string;
        if (size >= 1024 * 1024 * 1024 * 1024) {
          size = size / (1024 * 1024 * 1024 * 1024);
          string = "TB";
        } else if (size >= 1024 * 1024 * 1024) {
          size = size / (1024 * 1024 * 1024);
          string = "GB";
        } else if (size >= 1024 * 1024) {
          size = size / (1024 * 1024);
          string = "MB";
        } else if (size >= 1024) {
          size = size / 1024;
          string = "KB";
        } else {
          string = "b";
        }
        return (Math.round(size) / 10) + string;
      };
};
//var fileUpload = new fileUploadFunc();

/**
 * 单个文件上传
 */
var singleFileUpload = function(uploadedCallback,loadingFunc){
	var rheader = new RegExp(/^<h\d>.*http.*status.*(\d{3}.*)<\/h\d>/img);
	var parent = this;
	this.counter = 10; 
	this.uploadFlag = false;
	this.fileUploadFrame = null;
	this.fileUploadForm = null;
	this.onLoading = loadingFunc || null;
	this.onCallback = uploadedCallback || null;
	this.data = null;
	
	this.upload = function(self, uploadedCallback,loadingFunc){
		this.counter = 10;		
		this.uploadFlag = true;
		this.onCallback = uploadedCallback || this.onCallback;
		this.onLoading = loadingFunc || this.onLoading;
		
		this.fileUploadForm = $(self).closest("form");
		this.fileUploadFrame = this.fileUploadForm.find("iframe");
		
		this.showLoading(true);
		
		var filename = $(self).attr("name");//必须要有name属性才能上送
		if(!filename){
			for(i=0;i<10000;i++){
				if($("#filename_"+i).length <= 0){
					filename = "filename_"+i;
					$(self).attr("name",filename);
					break;
				}
			}
		}
		if(!filename){
			xAlert('提交错误，不能上传文件');
			this.showLoading(false);
			return;
		}
		var frameid = null;
		if(!this.fileUploadFrame || this.fileUploadFrame.length <= 0){
			if(!frameid){
				for(i=0;i<10000;i++){
					if($("#frame_"+i).length <= 0){
						frameid = "frame_"+i;
						break;
					}
				}
			}
			this.fileUploadForm.append("<iframe id='"+frameid+"' name='"+frameid+"' style='width:0px;height:0px;margin:0px;padding:0px;display:none;' src='javascript:false'></iframe>");
			this.fileUploadFrame = $("#"+frameid);
		}
		else{
			frameid = this.fileUploadFrame.attr('id');
		}
		if(!frameid){
			xAlert('提交错误，不能上传文件');
			this.showLoading(false);
			return;
		}
		this.fileUploadFrame.load(this._callback);
		this.fileUploadForm.attr('target', frameid);
		this.fileUploadForm.submit();
	};
	this.showLoading = function (isShow){
  	  	if(parent.onLoading){
  	  		parent.onLoading(isShow);
  	  	}
    };
	this.reset = function(){
		parent.counter = 10;
		parent.uploadFlag = false;
		
		var file = $(parent.imgElement).closest(".file-upload").find("input[type='file']");
		var v = file.val();
		if(v && v.length > 0){
			file.after(file.clone().val(""));     
			file.remove();
		}
		
		var firstChild, frame = parent.fileUploadFrame.get(0);
		try {
			var doc = frame.contentWindow.document || frame.contentDocument || window.frames[parent.fileUploadFrame].document;
			if (doc) {
				if (doc.body) {
					if (/textarea/i.test((firstChild = doc.body.firstChild || {}).tagName)) { // json response wrapped in textarea
						firstChild.value = '';
					} else {
						doc.body.innerHTML = '';
					}
				}
			}
		} catch (e) {
			return ;
		}
	};
	this._isCompleted = function(responseText) {
		if (!responseText) {
			this.counter--;
			return false;
		}
		try {
			$.parseJSON(responseText);
		} catch (e) {
			if(rheader.test(responseText)){
				this.counter = 0;
			}
			else{
				this.counter--;
			}
			return false;
		}
		return true;
	};
	this._callback = function(frame){//此函数内的this指针指向的是UploadFrame
		parent.data = null;
		if(!parent.uploadFlag){
			//this.showLoading(false);
			return;
		}
		frame = parent.fileUploadFrame.get(0);
		var firstChild, responseText;
		try {
			var doc = frame.contentWindow.document || frame.contentDocument || window.frames[parent.fileUploadFrame].document;
			if (doc) {
				if (doc.body) {
					if (/textarea/i.test((firstChild = doc.body.firstChild || {}).tagName)) { // json response wrapped in textarea
						responseText = firstChild.value;
					} else {
						responseText = doc.body.innerHTML;
					}
				}
			}
		} catch (e) {
			xAlert("上传失败");
			parent.showLoading(false);
			if(parent.onCallback){
				parent.onCallback(false);
			}
			return ;
		}
		if(parent.counter <= 0){
			xAlert("上传失败");
			parent.showLoading(false);
			if(parent.onCallback){
				parent.onCallback(false);
			}
			return;
		}
		else if (!parent._isCompleted(responseText)) {
			if(parent.counter <= 0){
				xAlert(RegExp?RegExp.$1:'上传错误');
				parent.showLoading(false);
				if(parent.onCallback){
					parent.onCallback(false);
				}
			}
			else{
				window.setTimeout(parent._callback, 200);
			}
			return;
		}
		else if(responseText === "false"){
			xAlert("浏览器不允许上传文件，请检查文件上传框的相关信息设置是否正确!");
			parent.showLoading(false);
			if(parent.onCallback){
				parent.onCallback(false);
			}
			return;
		}
		parent.showLoading(false);
		
		var data = $.parseJSON(responseText);
		var ret = data.rethead || data.retHead || data;
		parent.data = data;
		if(ret.status == "S"){
			if(parent.onCallback){
				parent.onCallback(data);
			}
			else{
				xMsg("文件上传成功");
			}
		}
		else{
			var msg = ret.msgarr || ret.msgArr,
            s='';
        	for(var i in msg){
        		s += msg[i].code+" "+msg[i].desc+"\n";//+"<br/>";
        	}
        	xAlert(s);
        	if(parent.onCallback){
        		parent.onCallback(false);
			}
		}
		parent.uploadFlag = false;
	};
	
	this.getFileSize = function(size) {
        var string;
        if (size >= 1024 * 1024 * 1024 * 1024) {
          size = size / (1024 * 1024 * 1024 * 1024);
          string = "TB";
        } else if (size >= 1024 * 1024 * 1024) {
          size = size / (1024 * 1024 * 1024);
          string = "GB";
        } else if (size >= 1024 * 1024) {
          size = size / (1024 * 1024);
          string = "MB";
        } else if (size >= 1024) {
          size = size / 1024;
          string = "KB";
        } else {
          string = "b";
        }
        return (Math.round(size) / 10) + string;
      };
};


/**
 * 图片文件上传
 */
var imgFileUpload = function(uploadedCallback){
	var rheader = new RegExp(/^<h\d>.*http.*status.*(\d{3}.*)<\/h\d>/img);
	var parent = this;
	this.counter = 10; 
	this.uploadFlag = false;
	this.fileUploadFrame = null;
	this.fileUploadForm = null;
	this.loadingElement = null;
	this.imgElement = null;
	this.onCallback = uploadedCallback || null;
	this.data = null;
	
	this.upload = function(self, uploadedCallback){
		this.counter = 10;		
		this.uploadFlag = true;
		this.onCallback = uploadedCallback || this.onCallback;
		
		this.loadingElement = $(self).parent().find("i");
		this.fileUploadForm = $(self).closest("form");
		this.fileUploadFrame = this.fileUploadForm.find("iframe");
		
		this.imgElement = this.fileUploadForm.find("img");
		if(this.imgElement && this.imgElement.length > 0){
			this.imgElement.removeData("packdata");
		}
		
		this.showLoading(true);
		
		var filename = $(self).attr("name");//必须要有name属性才能上送
		if(!filename){
			for(i=0;i<10000;i++){
				if($("#filename_"+i).length <= 0){
					filename = "filename_"+i;
					$(self).attr("name",filename);
					break;
				}
			}
		}
		if(!filename){
			xAlert('未选择文件，取消上传');
			this.showLoading(false);
			return;
		}
		var frameid = null;
		if(!this.fileUploadFrame || this.fileUploadFrame.length <= 0){
			if(!frameid){
				for(i=0;i<10000;i++){
					if($("#frame_"+i).length <= 0){
						frameid = "frame_"+i;
						break;
					}
				}
			}
			this.fileUploadForm.append("<iframe id='"+frameid+"' name='"+frameid+"' style='width:0px;height:0px;margin:0px;padding:0px;display:none;' src='javascript:false'></iframe>");
			this.fileUploadFrame = $("#"+frameid);
		}
		else{
			frameid = this.fileUploadFrame.attr('id');
		}
		if(!frameid){
			xAlert('未选择文件，取消上传');
			this.showLoading(false);
			return;
		}
		this.fileUploadFrame.load(this._callback);
		this.fileUploadForm.attr('target', frameid);
		this.fileUploadForm.submit();
	};
	this.showLoading = function (isShow){
		if(!parent.loadingElement || !parent.loadingElement.length){
			return;
		}
  	  	if(isShow){
  	  		parent.loadingElement.removeClass("glyphicon glyphicon-open blue").addClass("glyphicon glyphicon-repeat blue");//后面增加动态
  	  	}
  	  	else{
  	  		parent.loadingElement.removeClass().addClass("glyphicon glyphicon-open blue");
  	  	}
    };
    this.getImgElem = function(){
    	return parent.imgElement;
    };
    this.setImgSrc = function(fileid,isHideOnFail){
    	if(!fileid){
    		parent.imgElement.attr('src','');
    		if(isHideOnFail){
    			parent.imgElement.hide();
    		}
      	  	return;
    	}
    	parent.imgElement.attr('src',parent.getImgUrl(fileid));
    	parent.imgElement.show();
    };
	this.reset = function(){
		parent.counter = 10;
		parent.uploadFlag = false;
		
		var file = $(parent.imgElement).closest(".file-upload").find("input[type='file']");
		var v = file.val();
		if(v && v.length > 0){
			file.after(file.clone().val(""));     
			file.remove();
		}
		
		var firstChild, frame = parent.fileUploadFrame.get(0);
		try {
			var doc = frame.contentWindow.document || frame.contentDocument || window.frames[parent.fileUploadFrame].document;
			if (doc) {
				if (doc.body) {
					if (/textarea/i.test((firstChild = doc.body.firstChild || {}).tagName)) { // json response wrapped in textarea
						firstChild.value = '';
					} else {
						doc.body.innerHTML = '';
					}
				}
			}
		} catch (e) {
			return ;
		}
	};
	this._isCompleted = function(responseText) {
		if (!responseText) {
			this.counter--;
			return false;
		}
		try {
			$.parseJSON(responseText);
		} catch (e) {
			if(rheader.test(responseText)){
				this.counter = 0;
			}
			else{
				this.counter--;
			}
			return false;
		}
		return true;
	};
	this._callback = function(frame){//此函数内的this指针指向的是UploadFrame
		parent.data = null;
		if(!parent.uploadFlag){
			//this.showLoading(false);
			return;
		}
		frame = parent.fileUploadFrame.get(0);
		var firstChild, responseText;
		try {
			var doc = frame.contentWindow.document || frame.contentDocument || window.frames[parent.fileUploadFrame].document;
			if (doc) {
				if (doc.body) {
					if (/textarea/i.test((firstChild = doc.body.firstChild || {}).tagName)) { // json response wrapped in textarea
						responseText = firstChild.value;
					} else {
						responseText = doc.body.innerHTML;
					}
				}
			}
		} catch (e) {
			xAlert("上传失败");
			parent.showLoading(false);
			if(parent.onCallback){
				parent.onCallback(false);
			}
			return ;
		}
		if(parent.counter <= 0){
			xAlert("上传失败");
			parent.showLoading(false);
			if(parent.onCallback){
				parent.onCallback(false);
			}
			return;
		}
		else if (!parent._isCompleted(responseText)) {
			if(parent.counter <= 0){
				xAlert(RegExp?RegExp.$1:'上传错误');
				parent.showLoading(false);
				if(parent.onCallback){
					parent.onCallback(false);
				}
			}
			else{
				window.setTimeout(parent._callback, 200);
			}
			return;
		}
		else if(responseText === "false"){
			xAlert("浏览器不允许上传文件，请检查文件上传框的相关信息设置是否正确!");
			parent.showLoading(false);
			if(parent.onCallback){
				parent.onCallback(false);
			}
			return;
		}
		parent.showLoading(false);
		
		var data = $.parseJSON(responseText);
		var ret = data.rethead || data.retHead || data;
		parent.data = data;
		if(ret.status == "S"){
			var dt = data.files[0];
			parent.imgElement.data("packdata", dt.fileidentify); 
			parent.imgElement.attr('src',parent.getImgUrl(dt.fileidentify));
			
			if(parent.onCallback){
				parent.onCallback(data);
			}
			else{
				xMsg("["+dt.filename+"]文件上传成功，文件大小:" + parent.getFileSize(dt.filesize));
			}
		}
		else{
			var msg = ret.msgarr || ret.msgArr,
            s='';
        	for(var i in msg){
        		s += msg[i].code+" "+msg[i].desc+"\n";//+"<br/>";
        	}
        	xAlert(s);
        	if(parent.onCallback){
        		parent.onCallback(false);
			}
		}
		parent.uploadFlag = false;
	};
	
    this.getImgUrl = function(fileidentify){
    	return 'netdomain.sys_diskfile_download?downtype=image&fileid='+fileidentify+'&_t='+(new Date().getTime());
    };
    
	this.getFileSize = function(size) {
        var string;
        if (size >= 1024 * 1024 * 1024 * 1024) {
          size = size / (1024 * 1024 * 1024 * 1024);
          string = "TB";
        } else if (size >= 1024 * 1024 * 1024) {
          size = size / (1024 * 1024 * 1024);
          string = "GB";
        } else if (size >= 1024 * 1024) {
          size = size / (1024 * 1024);
          string = "MB";
        } else if (size >= 1024) {
          size = size / 1024;
          string = "KB";
        } else {
          string = "b";
        }
        return (Math.round(size) / 10) + string;
      };
};
var imgUpload = new imgFileUpload();

/**
 * 树视图插件，可以在树的每行的后面增加操作按钮
 * 该插件依赖fuelux.tree
 */
!function ($) {
	  var defaults = {
		loadingHTML:'<div class="tree-loading"><i class="ace-icon fa fa-refresh fa-spin blue"></i></div>',
	    'open-icon': 'tree-minus',
	    'close-icon': 'tree-plus',
	    selectable: true,
	    'selected-icon': 'ace-icon fa fa-check',
	    'unselected-icon': 'ace-icon fa fa-times',
	    'item-action':'',
	    'folder-action':''
	  };
	  
	  $.fn.gbg_tree = function (option) {
	    return opt = $.extend({}, defaults, option),
	    this.each(function () {
	      var gbgTree = $(this);
	      gbgTree.html('<div class="tree-folder" style="display:none;">'+
	    		  			'<div class="tree-folder-header">'+
	    		  				'<i class="ace-icon ' + opt['close-icon'] + '"></i>'+
	    		  				'<div class="tree-folder-name"></div>'+
    		  					(!opt['folder-action'] ? '' : opt['folder-action']) +
	    		  			'</div>'+
	    		  			'<div class="tree-folder-content"></div>'+
	    		  			'<div class="tree-loader" style="display:none"></div>'+
	    		  		'</div>'+
	    		  		'<div class="tree-item" style="display:none;">' + 
		    		  		(!opt['unselected-icon'] ? '' : '<i class="' + opt['unselected-icon'] + '"></i>') +
	    		  			'<div class="tree-item-name"></div>'+
	    		  			(!opt['item-action'] ? '' :opt['item-action']) +
	    		  		'</div>'),
	      gbgTree.addClass(opt.selectable ? 'tree-selectable' : 'tree-unselectable'),
	      gbgTree.tree(opt),
	      gbgTree.removeClass('tree-selectable tree-unselectable').addClass(opt.selectable ? 'tree-selectable' : 'tree-unselectable')
	    }),
	    this
	  }
}(window.jQuery);

var TreeDataSource = function(options) {//定义文件树数据源类
	this._data 	= options.data || null;
	this._delay = options.delay || 0;
	this._callback = options.callback || $.noop;
	this._rootService = options.rootService || '';
	this._childService = options.childService || options.rootService || '';
	this._condition = options.condition || {};//服务查询时的参数
};
TreeDataSource.prototype.data = function(options, callback) {
	var self = this;
	var $data = null;
	
	if(!self._data || self._data == null || ("root" in options && options["root"].length)){//请求根目录
		invockeServiceSync(self._rootService, $.extend({parentid:''}, self._condition), function(data ,isSuccess){
			if(!isSuccess){
				$data = null;
				return;
			}
			var treeData = data.tree || [];
			if(!treeData || treeData.length <= 0){
				for(var dt in data){
					if($.isArray(data[dt])){
						treeData = data[dt];
						break;
					}
				};
			}
			options = treeData || [];
			self._data = options;
			$data = treeData || [];
			
			if($data != null) 
				callback({data: $data});
			else 
				callback({data:{}});
			
			self._callback($data);
  		});
		return;
	}
	else if(!("name" in options) && !("type" in options)){//用初始化时传入的数据显示树
		$data = self._data;//回填根目录
	}
	else if("type" in options && options.type == "folder") {
		if("children" in options && options["children"].length > 0)
			$data = options.children;
		else if("haschild" in options && options["haschild"]>0){
			invockeServiceSync(self._childService, $.extend({ parentid:options.id || ''}, self._condition), function(data ,isSuccess){
				if(!isSuccess){
					$data = null;
					return;
				}
				var treeData = data.tree || [];
				if(!treeData || treeData.length <= 0){
					for(var dt in data){
						if($.isArray(data[dt])){
							treeData = data[dt];
							break;
						}
					};
				}
				options["children"] = treeData || [];
				$data = treeData || [];
				
				if($data != null) 
					callback({data: $data});
				else 
					callback({data:{}});
				
				self._callback($data);
	  		});
			return;
		}
		else $data = null//no data
	}
	
	if($data != null) 
		callback({data: $data});
	else 
		callback({data:{}});
	
	self._callback($data);
};

function splitStrCN(str, len){//按指定长度分割字符串，一个中文字符为两个长度，一个英文字符为一个长度
	if(!str || !len || len <= 0){
		return [str || ''];
	}
	var j = 0, l = 0;
	var s = [];
	for(var i=0; i<str.length; i++){
		if(str.charCodeAt(i) > 255){//中文
			l += 2;
		}
		else{
			l++;
		}
		if(l >= len){
			s.push(str.substring(j,i));
			l = 0;
			j=i;
		}
	}
	if(l > 0){
		s.push(str.substring(j));
	}
	return s;
};

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
		$msg.text(msg||"no");
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

function xTip(msg){
	var dialog = new BootstrapDialog({
		type: BootstrapDialog.TYPE_SUCCESS,
        message: msg+"<b class='auto-close'></b>",
        draggable: true,
        size: BootstrapDialog.SIZE_SMALL,
        closable: true,
        closeByBackdrop: true,
        closeByKeyboard: false,
        onshown: function(dialogRef){
    		var dd = dialogRef.getModalDialog();
    		$(dd).css({
    	        'margin-top': function () {
    	            var modal_height = $(dd).first().height();
    	            var window_height = $(window).height();
    	            return (Math.ceil((window_height - modal_height)/3));//三分之一的位置
    	        }
    	    });
    	}
    });
    dialog.realize();
    dialog.getModalHeader().hide();
    dialog.getModalFooter().hide();
    dialog.getModalBody().css('background-color', 'green');
    dialog.getModalBody().css('color', '#fff');
    dialog.getModalBody().css('font-weight', '700');
    dialog.open();
    if($('.modal-backdrop').length>0){
		$('.modal-backdrop').hide();
		//$('.modal-backdrop').remove();
	}
    var total=3, isHover=false;
    setTimeout(function(){
    	if(total <= 0){
    		dialog.close();
    		return;
    	}
    	$('.auto-close',$(dialog.getModalBody())).html("<font color='red'>"+total+"秒</font>");
    	if(!isHover){
    		total--;
    	}
    	
    	setTimeout(arguments.callee, 1000);
    }, 1000);
    $(dialog.getModalBody()).hover(
    	function () {
    		total = 1;
    		isHover = true;
    	},
    	function () {
    		total = 1;
    		isHover = false;
    	}
	);
}
/****
	xConfirm("你确定要...吗？",function(flag){
    	if(!flag){
    		return;
    	}
    	...
	});
 * 
 * */
function xConfirm(msg,callback){
	var isNotBootstrapDialog = false;
	try{
		new BootstrapDialog();
	}
	catch(e){
		isNotBootstrapDialog = true;
	}
	
	if(isNotBootstrapDialog){
		var flag = confirm(msg);
		if(callback){
			callback(flag);
		}
		return;
	}
	
	var dialog = new BootstrapDialog({
        title:  '信息确认',
        message: msg,
        draggable: true,//可拖拽
        backdrop:false,//背景关闭
        animate: false,//动画关闭
        closable:false,//关闭按钮
        size: BootstrapDialog.SIZE_SMALL,//大小
        type: BootstrapDialog.TYPE_INFO,//按钮样式 提示
        buttons: [{
            label: '取消',//关闭按钮
            hotkey: 27, // Esc
            cssClass: 'btn-default',
            action: function(dialogRef){
                dialogRef.close();
                if(callback){
        			callback(false);
        		}
            }
        },{
            label: '确定',//打开按钮
            hotkey: 13, // Enter
            cssClass: 'btn-info',
            action: function(dialogRef){
                dialogRef.close();
                if(callback){
        			callback(true);
        		}
            }
        }],
        onhide:function(){
        	//;
        }
    });
	dialog.realize();//  实现 
	var dd = dialog.getModalDialog();
	$(dd).css({
        'margin-top': function () {
            var modal_height = $(dd).first().height();
            var window_height = $(window).height();
            return (Math.ceil((window_height - modal_height)*0.3));//位置
        }
    });
	dialog.open();
	
	if($('.modal-backdrop').length>0){
		$('.modal-backdrop').hide();
	}
}
function xAlert(msg, title, level){
	var dialog, nextFocus = typeof title === "string" ? null:title;
	if(nextFocus){
		title = '';
	}
	var isNotBootstrapDialog = false;
	try{
		new BootstrapDialog();
	}
	catch(e){
		isNotBootstrapDialog = true;
	}
	/*
	 BootstrapDialog.TYPE_DEFAULT, 
     BootstrapDialog.TYPE_INFO, 
     BootstrapDialog.TYPE_PRIMARY, 
     BootstrapDialog.TYPE_SUCCESS, 
     BootstrapDialog.TYPE_WARNING, 
     BootstrapDialog.TYPE_DANGER
    */
	
	/* level
	 * 默认：基本alert提示方式，以TYPE_PRIMARY风格显示
	 * A：该类信息不显示提示
	 * B：该类信息冒泡式提示，以TYPE_SUCCESS风格显示
	 * C：该类信息alert式提示，以TYPE_WARNING风格显示
	 * D：该类信息alert式提示，以TYPE_DANGER风格显示
	 */
	var onshownHandle = function(dialogRef){
		var dd = dialogRef.getModalDialog();
		$(dd).css({
	        'margin-top': function () {
	            var modal_height = $(dd).first().height();
	            var window_height = $(window).height();
	            return (Math.ceil((window_height - modal_height)/3));//三分之一的位置
	        }
	    });
	}
	if(!level){
		if(isNotBootstrapDialog){
			alert(msg);
			return;
		}
		dialog = new BootstrapDialog({
            title: title || '提示信息',
            message: msg,
            draggable: true,
            backdrop:false,
            animate: false,
            size: BootstrapDialog.SIZE_SMALL,
            type: BootstrapDialog.TYPE_PRIMARY,
            buttons: [{
                label: '确定',
                hotkey: 13, // Enter.
                cssClass: 'btn-primary',
	            action: function(dialogRef){
                    dialogRef.close();
                    if(nextFocus){
                    	nextFocus.focus();
                    }
                }
            }]
        });
		dialog.realize();
		var dd = dialog.getModalDialog();
		$(dd).css({
	        'margin-top': function () {
	            var modal_height = $(dd).first().height();
	            var window_height = $(window).height();
	            return (Math.ceil((window_height - modal_height)/3));//三分之一的位置
	        }
	    });
		dialog.open();
		
		if($('.modal-backdrop').length>0){
			$('.modal-backdrop').hide();
			//$('.modal-backdrop').remove();
		}
	}
	else if(level == 'A'){//该类信息不显示提示
		return;
	}
	else if(level == 'B'){
		if(isNotBootstrapDialog){
			alert(msg);
			return;
		}
		dialog = new BootstrapDialog({
			type: BootstrapDialog.TYPE_SUCCESS,
	        message: msg+"<b class='auto-close'></b>",
	        draggable: true,
	        size: BootstrapDialog.SIZE_SMALL,
	        closable: true,
            closeByBackdrop: true,
            closeByKeyboard: false,
            onshown: onshownHandle
        });
        dialog.realize();
        dialog.getModalHeader().hide();
        //dialog.getModalFooter().hide();
        dialog.getModalBody().css('background-color', 'green');
        dialog.getModalBody().css('color', '#fff');
        dialog.getModalBody().css('font-weight', '700');
        dialog.open();
        if($('.modal-backdrop').length>0){
			$('.modal-backdrop').hide();
			//$('.modal-backdrop').remove();
		}
        var total=3, isHover=false;
        setTimeout(function(){
        	if(total <= 0){
        		dialog.close();
        		return;
        	}
        	$('.auto-close',$(dialog.getModalBody())).html("<font color='red'>"+total+"秒</font>");
        	if(!isHover){
        		total--;
        	}
        	
        	setTimeout(arguments.callee, 1000);
        }, 1000);
        $(dialog.getModalBody()).hover(
        	function () {
        		total = 1;
        		isHover = true;
        	},
        	function () {
        		total = 1;
        		isHover = false;
        	}
		);
	}
	else if(level == 'C'){
		if(isNotBootstrapDialog){
			alert((title || '警告信息:')+msg);
			return;
		}
		dialog = BootstrapDialog.show({
	        type: BootstrapDialog.TYPE_WARNING,
	        title: title || '警告信息',
	        message: msg,
	        draggable: true,
	        size: BootstrapDialog.SIZE_SMALL,
	        buttons: [{
	            label: '关闭',
	            action: function(dialogRef){
                    dialogRef.close();
                    if(nextFocus){
                    	nextFocus.focus();
                    }
                }
	        }],
            onshown: onshownHandle
	    });
	}
	else if(level == 'D'){
		if(isNotBootstrapDialog){
			alert((title || '操作错误:')+msg);
			return;
		}
		dialog = BootstrapDialog.show({
	        type: BootstrapDialog.TYPE_DANGER,
	        title: title || '操作错误',
	        message: msg,
	        draggable: true,
	        closable: true,
            closeByBackdrop: false,
            closeByKeyboard: false,
	        size: BootstrapDialog.SIZE_NORMAL,
	        buttons: [{
	            label: '确定',
	            action: function(dialogRef){
                    dialogRef.close();
                    if(nextFocus){
                    	nextFocus.focus();
                    }
                }
	        }],
            onshown: onshownHandle
	    });
	}
	else{
		if(isNotBootstrapDialog){
			alert(msg);
			return;
		}
		
		dialog = BootstrapDialog.show({
	        type: BootstrapDialog.TYPE_INFO,
	        title: title || '提示信息',
	        message: msg,
	        draggable: true,
	        buttons: [{
	            label: '确定',
	            action: function(dialogRef){
                    dialogRef.close();
                    if(nextFocus){
                    	nextFocus.focus();
                    }
                }
	        }],
            onshown: onshownHandle
	    });
	}
}

/**
 * 替换字符串中的字段.

 * @param {String} tmpl 模版字符串
 * @param {Object} data json
 * @param {RegExp} [regexp] 匹配字符串的正则表达式
 * 
 * 例如：
 * data : {value : '123',text:'abc'}
 * template : '<label>{text}</label><input type="text" value="{value}"/>'
 * str = parserTemplate(template,data);
 *   
 */
function parserTemplate(tmpl,data,regexp){
  return  tmpl.replace(regexp || /\\?\{([^{}]+)\}/g, function (match, name) {
    return (data[name] === undefined) ? '' : data[name];
  });
}

function showProgress(n,container,top,left,posi){
	var progress = $("#_sys_progress_box_");
	if(!n){
		n = 1;
	}
	if(!container || container.length <= 0){
		container = $("body");
		var 
		w = window.screen.clientWidth || document.documentElement.clientWidth, //屏幕宽
		h = window.screen.clientHeight || document.documentElement.clientHeight, //屏幕高
		top = h / 3,
		left = w / 3,
		posi = 'absolute';
	}
	if(!progress || progress.length <= 0){
		container.append("<div id='_sys_progress_box_' style='position:"+posi+";top:"+top+"px;left:"+left+"px;width:200px;zIndex:99997;'><div class='progress' style='margin-bottom: 0px;'><div class='progress-bar progress-bar-success progress-bar-striped active' role='progressbar' aria-valuenow='"+n+"' aria-valuemin='0' aria-valuemax='100' style='width: "+n+"%'><span class='sr-only'>"+n+"%</span></div></div></div>");
		progress = $("#_sys_progress_box_");
	}
	else{
		progress.css({
			top: top+'px',
			left: left+'px'
		});
		container.append(progress);
	}
	
	if(n > 100){
		progress.hide();
	}
	else{
		progress.show();
		progress.find('.progress-bar').css('width',n+'%');
		progress.find('.progress-bar span').text(n+'%');
	}
}

function showLoading(waiting){
	var loading = $("#_sys_loading_box_");
	if(!loading || loading.length <= 0){
		if(waiting *1 <= 0){
			$("#_sys_loading_box_ span").text(1);
			loading.hide();
			return;
		}
		
		$("body").append("<div id='_sys_loading_box_'><span style='position:absolute;top:6px;left:30px;font-size:3em;color:orangered;font-weight:700;'>1</span><i class='fa fa-spinner fa-pulse float-left' style='font-size:6em;'></i></div>");
		loading = $("#_sys_loading_box_");
		loading.css({					
			zIndex: '99998',
			position: 'fixed',
			backgroundColor:'white'
		});
	}
	else{
		if(waiting * 1 <= 0){
			$("#_sys_loading_box_ span").data('waiting','0');
			return;
		}

		loading.show();
	}
	
	var 
	w = window.screen.clientWidth || document.documentElement.clientWidth, //屏幕宽
	h = window.screen.clientHeight || document.documentElement.clientHeight; //屏幕高
	loading.css({
		top: h / 3 + "px",
		left: w / 3 + "px"
	});
	
	if(waiting && waiting *1 > 0){
		var span = $("#_sys_loading_box_ span"), cnt = waiting *1, idx = 1;
		span.data('waiting',waiting);
		var intervalId = window.setInterval(function(){
			cnt = span.data('waiting') * 1;
			if(idx >= cnt){
				span.text(1);
				window.clearInterval(intervalId);
				loading.hide();
			}
			else{
				if(idx<10){
					span.css({					
						'font-size': '3em',
						'font-weight': '700',
						top:'6px',
						left:'30px'
					});
				}
				else{
					span.css({					
						'font-size': '2em',
						'font-weight': '400',
						top:'20px',
						left:'20px'
					});
				}
				span.text(idx++);
			}
		},1000);
	}
}

function imageView(src){//图片查看
	var container = $('#imageViewDialogContainer');
	container.empty();
	container.append("<img src="+src+"/>");
	container.css({
		width:$(window.top).width(),
		height:$(window.top).height()-60,
		'z-index':999,
		'overflow':'scroll'
	});
	container.show();
	container.unbind();
	container.click(function(){
		container.hide();
	});
}
/**
 * 显示模式对话框，对话框内容为page制定的地址

 * @param {String} page 页面地址
 * @param {String} dialog 页面对话框ID
 * @param {String | function} [container] 页面对话框容器ID，非必输，缺省为：对话框ID+Container字符串
 * @param {function} callback 页面显示后的回调函数
 * 
 * 例如：
 * showDialog("formsfilesAdd.jsp",'addFormsfilesDialog');
 *   
 */
function showDialog(page,dialog,container,callback,initfunc){
	if(!page || !dialog){
		return;
	}
	if(jQuery.isFunction(container)){
		callback = container;
		container = dialog+'Container';
	}
	var jsfile = page.replace(/\.[^\.]*$/img,'.js');
	
	container = $('#'+(container || dialog+'Container'));
	if(container.html() && container.html().trim().length > 0){
		container.show();
		$('#'+dialog).modal({backdrop:false,show:true});
		
		if(jQuery.isFunction(initfunc)){
			initfunc($('#'+dialog));
		}
		if(jQuery.isFunction(callback)){
			callback($('#'+dialog));
		}
	}
	else{
		container.load(page+"?"+(new Date()).valueOf(), null, function(){
			$.getScript(jsfile);

			container.show();
			$('#'+dialog).modal({backdrop:false,show:true});
			
			if(jQuery.isFunction(callback)){
				callback($('#'+dialog));
			}
		});
	}
}

function getFileName(fileid) {
	if (!fileid) {
		return "";
	}
	var fileName = fileid.replace(/.{2}/g, function(hex) {
        return String.fromCharCode(parseInt(hex, 16));
    });
	var i=fileName.lastIndexOf('/') || 0,j=fileName.lastIndexOf('_');
	return fileName.substring(i+1,j);
}