package com.platform.cubism.base;

import static com.platform.cubism.base.Json.DOT_SIGN;
import static com.platform.cubism.util.StringUtils.hasText;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.platform.cubism.base.format.FormatManager;
import com.platform.cubism.util.Assert;
import com.platform.cubism.util.StringUtils;

public class CField implements Comparable<Object>, Serializable {
	private static final long serialVersionUID = 2704939018296366717L;
	private static final Pattern hasSingleComma = Pattern.compile("([^\\\\])(\")", Pattern.CASE_INSENSITIVE);
	private String name;
	private String value;
	private String type;
	private String format;
	private Object objValue;

	public CField() {
		;
	}

	public CField(String name) {
		this(name, null);
	}

	public CField(String name, String value) {
		this(name, value, null);
	}

	public CField(String name, Object value) {
		this(name, value, null);
	}

	public CField(String name, String value, String format) {
		this(name, value, format, null);
	}

	public CField(String name, Object value, String format) {
		this(name, value, format, null);
	}

	public CField(String name, String value, String format, String type) {
		setName(name).setFormat(format).setType(type).setValue(value);
	}

	public CField(String name, Object value, String format, String type) {
		setName(name).setFormat(format).setType(type).setValue(value);
	}

	public int getIntValue() {
		String v = getValue();
		if (!hasText(v)) {
			return 0;
		}
		return Integer.parseInt(v);
	}

	public long getLongValue() {
		String v = getValue();
		if (!hasText(v)) {
			return 0l;
		}
		return Long.parseLong(v);
	}

	public float getFloatValue() {
		String v = getValue();
		if (!hasText(v)) {
			return 0.00f;
		}
		return Float.parseFloat(v);
	}

	public double getDoubleValue() {
		String v = getValue();
		if (!hasText(v)) {
			return 0.00d;
		}
		return Double.parseDouble(v);
	}

	public Date getDate(String format) throws ParseException {
		Assert.hasText(format);
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		Date dt = simpleDateFormat.parse(getValue());
		return dt;
	}
	public String getDateValue(String format){
		String v = getValue();
		if(!hasText(v)){
			return v;
		}
		String[] dt = v.split("\\D+");
		int len = dt.length;
		if(dt == null || len<3){
			return v;
		}
		if(!hasText(format)){
			return dt[0]+(dt[1].length()>1?dt[1]:"0"+dt[1])+(dt[2].length()>1?dt[2]:"0"+dt[2]);
		}
		String s = format;
		if(s.toLowerCase().indexOf("y")>=0 && len>=1)
			s = s.replaceAll("y+|Y+", dt[0]);
		else if(s.toLowerCase().indexOf("y")>=0)
			s = s.replaceAll("y+|Y+", "");
		if(s.toLowerCase().indexOf("m")>=0 && len>=2)
			s = s.replaceAll("m+|M+", (dt[1].length()>1?dt[1]:"0"+dt[1]));
		else if(s.toLowerCase().indexOf("m")>=0)
			s = s.replaceAll("m+|M+", "");
		if(s.toLowerCase().indexOf("d")>=0 && len>=3)
			s = s.replaceAll("d+|D+", (dt[2].length()>1?dt[2]:"0"+dt[2]));
		else if(s.toLowerCase().indexOf("d")>=0)
			s = s.replaceAll("d+|D+", "");
			
		if(s.toLowerCase().indexOf("h")>=0 && len>=4)
			s = s.replaceAll("h+|H+", (dt[3].length()>1?dt[3]:"0"+dt[3]));
		else if(s.toLowerCase().indexOf("h")>=0)
			s = s.replaceAll("h+|H+", "00");
		if(s.toLowerCase().indexOf("i")>=0 && len>=5)
			s = s.replaceAll("i+|I+", (dt[4].length()>1?dt[4]:"0"+dt[4]));
		else if(s.toLowerCase().indexOf("i")>=0)
			s = s.replaceAll("i+|I+", "00");
		if(s.toLowerCase().indexOf("s")>=0 && len>=6)
			s = s.replaceAll("s+|S+", (dt[5].length()>1?dt[5]:"0"+dt[5]));
		else if(s.toLowerCase().indexOf("s")>=0)
			s = s.replaceAll("s+|S+", "00");
		
		return s;
	}
	public String getName() {
		return name;
	}

	public int size() {
		String v = getValue();
		if(v != null){
			return v.length();
		}
		else{
			return 0;
		}
	}
	public String getLValue(int len){
		String v = getValue();
		if(len <= 0 || !hasText(v) || len >= v.length()){
			return getValue();
		}
		return v.substring(0, len-1);
	}
	public String getRValue(int len){
		String v = getValue();
		if(len <= 0 || !hasText(v) || len >= v.length()){
			return getValue();
		}
		return v.substring(v.length()-len);
	}
	public String getValue() {
		if (value != null) {
			return value;
		}
		Object obj = getObjectValue();
		if (obj == null) {
			return "";
		}/*
		if(obj.getClass().isArray()){
			StringBuilder sb = new StringBuilder();
			Object[] arr = (Object[])obj;
			for(Object o : arr){
				sb.append(o.toString());
			}
			return sb.toString();
		}*/
		return obj.toString();
	}

	public Object getObjectValue() {
		if (objValue != null) {
			/*
			 * if ("datetime".equals(type)) { return SimpleDateFormat
			 * .getDateTimeInstance().format(objValue); } else { return
			 * objValue; }
			 */
			return objValue;
		}
		if (!hasText(value)) {
			return null;
		}
		if (!hasText(type)) {
			return value;
		}

		return StringUtils.convert(value, type);
	}

	public String getFormatValue() {
		if (!hasText(format)) {
			return getValue();
		}

		String vl = FormatManager.convert(this);
		if (vl == null) {
			return getValue();
		} else {
			return vl;
		}
	}

	public String getType() {
		return type;
	}

	public String getFormat() {
		return format;
	}

	public boolean isEmpty() {
		if (!hasText(value) && objValue == null) {
			return true;
		}
		return false;
	}

	public CField setName(String name) {
		if (name != null && name.indexOf(DOT_SIGN) >= 0) {
			throw new IllegalArgumentException("[Assertion failed] - the name can't include a dot.");
		}
		this.name = name;
		return this;
	}

	public CField setValue(String value) {
		this.value = value;
		return this;
	}

	public CField setValue(Object value) {
		this.objValue = value;
		return this;
	}

	public CField setType(String type) {
		this.type = type;
		return this;
	}

	public CField setFormat(String format) {
		this.format = format;
		return this;
	}

	public void clear() {
		objValue = null;
		value = null;
		type = null;
		format = null;
	}

	public CField reset() {
		value = null;
		objValue = null;
		return this;
	}

	public CField copyOf(CField fld) {
		if (fld == null) {
			return this;
		}

		name = fld.getName();
		value = fld.getValue();
		objValue = fld.getObjectValue();
		type = fld.getType();
		format = fld.getFormat();
		return this;
	}

	public CField mergeOf(CField fld) {
		if (fld == null) {
			return this;
		}

		if (!hasText(name) || (name.equalsIgnoreCase(fld.getName()) && !hasText(value))) {
			name = fld.getName();
			value = fld.getValue();
			objValue = fld.getObjectValue();
			type = fld.getType();
			format = fld.getFormat();
		}
		return this;
	}

	public CField setValue(CField fld) {
		value = fld.getValue();
		objValue = fld.getObjectValue();
		type = fld.getType();
		format = fld.getFormat();
		return this;
	}

	public CField toJson(JsonTokener tokener) {
		if (tokener == null || tokener.isNull()) {
			return this;
		}
		char c = tokener.nextClean();
		switch (c) {
		case '"':
		case '\'':
			name = tokener.nextString(c);
			break;
		default:
			tokener.back();
			name = tokener.nextValue();
		}
		// ====================================================================//
		c = tokener.nextClean();
		if (c == '=') {
			if (tokener.next() != '>') {
				tokener.back();
			}
		} else if (c != ':') {
			throw tokener.syntaxError("Expected a ':' after a key");
		}
		// ====================================================================//
		c = tokener.nextClean();
		switch (c) {
		case '"':
		case '\'':
			value = tokener.nextString(c);
			break;
		default:
			tokener.back();
			value = tokener.nextValue();
		}

		return this;
	}
	
	public String toUrl() {
		return name+"="+getFormatValue();
	}
	
	// name:value or "name":"value" or 'name':'value'
	public CField toJson(String json) {
		return toJson(new JsonTokener(json));
	}

	public String toJson() {
		StringBuilder sb = new StringBuilder();
		String value = getFormatValue();
		
		if (value != null && value.indexOf('"') >= 0) {
			Matcher matcher = hasSingleComma.matcher(value);
			if(matcher.matches()){
				value = matcher.replaceAll("$1\\\\$2");
			}
			//value = value.replaceAll("\"", "\\\\$0");
		}
		sb.append("\"").append(name).append("\"").append(":\"").append(value == null ? "" : StringUtils.escapeJson(value)).append("\"");
		return sb.toString();
	}
	
	public String toJsonValue(){
		String value = getFormatValue();
		
		if (value != null && value.indexOf('"') >= 0) {
			Matcher matcher = hasSingleComma.matcher(value);
			if(matcher.matches()){
				value = matcher.replaceAll("$1\\\\$2");
			}
			//value = value.replaceAll("\"", "\\\\$0");
		}
		return "\""+(value == null ? "" : StringUtils.escapeJson(value))+"\"";
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		String value = getFormatValue();
		if (value != null && value.indexOf('"') >= 0) {
			Matcher matcher = hasSingleComma.matcher(value);
			if(matcher.matches()){
				value = matcher.replaceAll("$1\\\\$2");
			}
			//value = value.replaceAll("\"", "\\\\$0");
		}
		sb.append(name).append(":\"").append(value == null ? "" : value).append("\"");
		return sb.toString();
	}

	public int compareTo(Object o) {
		if (o == null) {
			if (value == null || !hasText(value)) {
				return 0;
			} else {
				return 1;
			}
		} else if (o instanceof CField) {
			return compareTo((CField) o);
		}
		else if(o instanceof Number){
			Number leftNumber = (Number) o;
			if (leftNumber instanceof Double) {
				double d1 = leftNumber.doubleValue();
				double d2 = getDoubleValue();
				return Double.compare(d1, d2);
			} else if (leftNumber instanceof Float) {
				float f1 = leftNumber.floatValue();
				float f2 = getFloatValue();
				return Float.compare(f1, f2);
			} else if (leftNumber instanceof Long) {
				Long l1 = leftNumber.longValue();
				Long l2 = getLongValue();
				return l1.compareTo(l2);
			} else {
				Integer i1 = leftNumber.intValue();
				Integer i2 = getIntValue();
				return i1.compareTo(i2);
			}
		}
		else {
			String s = o.toString();
			if (s == null) {
				s = "";
			}
			if (s.equalsIgnoreCase(getValue())) {
				return 0;
			}
			return s.compareToIgnoreCase(getValue());
		}
	}

	public int compareTo(CField field) {
		if (field == this) {
			return 0;
		}
		if (field == null) {
			return -1;
		}
		String s = field.getValue();

		if (s == getValue()) {
			return 0;
		}
		if (s == null) {
			return -1;
		}
		if (s.equalsIgnoreCase(getValue())) {
			return 0;
		}
		return s.compareToIgnoreCase(getValue());
	}
}