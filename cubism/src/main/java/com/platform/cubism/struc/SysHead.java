package com.platform.cubism.struc;

/**
   syshead : { 
   		useragent:"",
 		ip : "",
 		datetime : "",
  		userid : "",
  		usertype : "",
  		loginname : "",
  		username : "",
  		departid : "",
  		employeeid : "",
  		servicename : "" 
   }
 */
public enum SysHead {
	SYSHEAD("syshead"), USERAGENT("useragent"), USERID("userid"),USERTYPE("usertype"), LOGINNAME("loginname"), USERNAME("username"), IP("ip"),DATETIME("datetime"),SEQNO("seqno"), DEPARTID("departid"),EMPLOYEEID("employeeid"), SERVICENAME(
			"servicename");
	private final String value;

	public String value() {
		return this.value;
	}

	SysHead(String value) {
		this.value = value;
	}
}