package com.platform.cubism.query;

import static com.platform.cubism.util.StringUtils.hasText;
import com.platform.cubism.base.CArray;
import com.platform.cubism.base.CStruc;
import com.platform.cubism.base.Json;
import com.platform.cubism.base.JsonFactory;

public class ExtQuery implements Query {

	public Json getQuery(String queryId) {
		QueryConfig qc = QueryManager.getQuery(queryId);
		Json ext = JsonFactory.create();
		ext.addField("serviceId", qc.getSrvid() + ".service");
		ext.addField("serviceParam", qc.getId());

		return ext;
	}

	private String getStringRelation() {
		return 
		"{                                                                                       "+
        "    name: 'status',                                                                     "+
        "    xtype:'combo',                                                                      "+
        "    mode:'local',                                                                       "+
        "    forceSelection: true,                                                               "+
        "    valueField:'id',                                                                    "+
        "    displayField:'text',                                                                "+
        "    store: new Ext.data.SimpleStore({                                                   "+
        "    	fields:['id','text'],                                                            "+
        "    	data:[['like','包含'],['unlike','不包含'],['equal','等于'],['unequal','不等于']]  "+
        "    })                                                                                  "+
        "}                                                                                       ";
	}

	private String getNumberRelation() {
		return "";
	}

	private String getDateRelation() {
		return "";
	}
}