package com.platform.cubism.service.config;

import static com.platform.cubism.util.CubismHelper.copyOf;
import static com.platform.cubism.util.StringUtils.hasText;

import java.util.ArrayList;
import java.util.List;

public class ServiceConfig {
	private int maxseq;
	private String id;
	private String type;
	private String processor;
	private String desc;
	private String datasource;
	private String extend;
	private String auth;
	private String scope;
	private String cache;
	private String updatecache;
	private String defaultlogbefore;
	private String defaultlogafter;
	private InElement in;
	private OutElement out;
	private List<RefElement> ref;
	private List<SqlElement> sql;
	private List<LogElement> log;
	private List<ExpElement> exp;
	private List<ImpElement> imp;
	private List<JsonElement> json;
	private List<QuitElement> quit;

	public String getId() {
		return id;
	}

	public String getProcessor() {
		return processor;
	}

	public void setProcessor(String processor) {
		this.processor = processor;
	}

	public String getDatasource() {
		return datasource;
	}

	public void setDatasource(String datasource) {
		this.datasource = datasource;
	}

	public String getType() {
		return type;
	}

	public String getDesc() {
		return desc;
	}

	public String getExtend() {
		return extend;
	}

	public String getAuth() {
		return auth;
	}

	public String getScope() {
		return scope;
	}

	public String getDefaultlogbefore() {
		return defaultlogbefore;
	}

	public void setDefaultlogbefore(String defaultlogbefore) {
		this.defaultlogbefore = defaultlogbefore;
	}

	public String getDefaultlogafter() {
		return defaultlogafter;
	}

	public void setDefaultlogafter(String defaultlogafter) {
		this.defaultlogafter = defaultlogafter;
	}

	public InElement getIn() {
		return in;
	}

	public OutElement getOut() {
		return out;
	}

	public RefElement getRefAt(int seq) {
		for (RefElement ref : getRef()) {
			if (ref.getSequence() == seq) {
				return ref;
			}
		}
		return null;
	}

	public QuitElement getQuitAt(int seq) {
		for (QuitElement quit : getQuit()) {
			if (quit.getSequence() == seq) {
				return quit;
			}
		}
		return null;
	}

	public SqlElement getSqlAt(int seq) {
		for (SqlElement sql : getSql()) {
			if (sql.getSequence() == seq) {
				return sql;
			}
		}
		return null;
	}
	public ExpElement getExpAt(int seq) {
		for (ExpElement exp : getExp()) {
			if (exp.getSequence() == seq) {
				return exp;
			}
		}
		return null;
	}
	public ImpElement getImpAt(int seq) {
		for (ImpElement imp : getImp()) {
			if (imp.getSequence() == seq) {
				return imp;
			}
		}
		return null;
	}

	public List<RefElement> getRef() {
		if (ref == null) {
			return new ArrayList<RefElement>(0);
		}

		return ref;
	}

	public List<QuitElement> getQuit() {
		if (quit == null) {
			return new ArrayList<QuitElement>(0);
		}

		return quit;
	}

	public List<SqlElement> getSql() {
		if (sql == null) {
			return new ArrayList<SqlElement>(0);
		}

		return sql;
	}
	public List<ExpElement> getExp() {
		if (exp == null) {
			return new ArrayList<ExpElement>(0);
		}

		return exp;
	}

	public List<ImpElement> getImp() {
		if (imp == null) {
			return new ArrayList<ImpElement>(0);
		}

		return imp;
	}
	public List<LogElement> getLog() {
		if (log == null) {
			return new ArrayList<LogElement>(0);
		}

		return log;
	}

	public List<JsonElement> getJson() {
		if (json == null) {
			return new ArrayList<JsonElement>(0);
		}

		return json;
	}

	public JsonElement getJson(String id) {
		for (JsonElement je : json) {
			if (je.getId() != null && je.getId().equalsIgnoreCase(id) && je.getValue() != null && "".equals(je.getValue())) {
				return je;
			}
		}
		return null;
	}

	public int getMaxSeq() {
		return maxseq;
	}

	public String getCache() {
		return cache;
	}

	public void setCache(String cache) {
		this.cache = cache;
	}

	public String getUpdatecache() {
		return updatecache;
	}

	public void setUpdatecache(String updatecache) {
		this.updatecache = updatecache;
	}

	public void setMaxSeq(int seq) {
		this.maxseq = seq;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public void setExtend(String extend) {
		this.extend = extend;
	}

	public void setAuth(String auth) {
		this.auth = auth;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public void setIn(InElement in) {
		this.in = in;
	}

	public void setOut(OutElement out) {
		this.out = out;
	}

	public ServiceConfig addRef(RefElement ref) {
		if (this.ref == null) {
			this.ref = new ArrayList<RefElement>();
		}
		this.ref.add(ref);
		return this;
	}

	public ServiceConfig addQuit(QuitElement quit) {
		if (this.quit == null) {
			this.quit = new ArrayList<QuitElement>();
		}
		this.quit.add(quit);
		return this;
	}

	public ServiceConfig addSql(SqlElement sql) {
		if (this.sql == null) {
			this.sql = new ArrayList<SqlElement>();
		}
		this.sql.add(sql);
		return this;
	}
	public ServiceConfig addExp(ExpElement exp) {
		if (this.exp == null) {
			this.exp = new ArrayList<ExpElement>();
		}
		this.exp.add(exp);
		return this;
	}

	public ServiceConfig addImp(ImpElement imp) {
		if (this.imp == null) {
			this.imp = new ArrayList<ImpElement>();
		}
		this.imp.add(imp);
		return this;
	}
	public ServiceConfig addLog(LogElement log) {
		if (this.log == null) {
			this.log = new ArrayList<LogElement>();
		}
		this.log.add(log);
		return this;
	}

	public ServiceConfig addJson(JsonElement json) {
		if (this.json == null) {
			this.json = new ArrayList<JsonElement>();
		}
		this.json.add(json);
		return this;
	}

	public void overrideFrom(ServiceConfig other) {
		if (other == null) {
			return;
		}
		if (hasText(other.getId()))
			this.setId(other.getId());
		if (hasText(other.getType()))
			this.setType(other.getType());
		if (hasText(other.getScope()))
			this.setScope(other.getScope());
		if (hasText(other.getProcessor()))
			this.setProcessor(other.getProcessor());
		if (hasText(other.getDesc()))
			this.setDesc(other.getDesc());
		if (hasText(other.getDatasource()))
			this.setDatasource(other.getDatasource());
		if (hasText(other.getCache()))
			this.setCache(other.getCache());
		if (hasText(other.getUpdatecache()))
			this.setUpdatecache(other.getUpdatecache());
		if (hasText(other.getExtend()))
			this.setExtend(other.getExtend());
		if (hasText(other.getDefaultlogbefore()))
			this.setDefaultlogbefore(other.getDefaultlogbefore());
		if (hasText(other.getDefaultlogafter()))
			this.setDefaultlogafter(other.getDefaultlogafter());

		if (this.in == null)
			this.in = new InElement();
		if (this.out == null)
			this.out = new OutElement();

		this.in.addOverrides(other.getIn());
		this.out.addOverrides(other.getOut());

		int oMaxSeq = other.getMaxSeq();
		if (other.getRef() != null && !other.getRef().isEmpty()) {
			if (this.ref == null)
				this.ref = new ArrayList<RefElement>();

			for (RefElement r : this.ref) {
				r.setSequence(oMaxSeq + r.getSequence());
			}

			this.ref.addAll(copyOf(other.getRef()));
		}

		if (other.getQuit() != null && !other.getQuit().isEmpty()) {
			if (this.quit == null)
				this.quit = new ArrayList<QuitElement>();

			for (QuitElement q : this.quit) {
				q.setSequence(oMaxSeq + q.getSequence());
			}

			this.quit.addAll(copyOf(other.getQuit()));
		}

		if (other.getSql() != null && !other.getSql().isEmpty()) {
			if (this.sql == null)
				this.sql = new ArrayList<SqlElement>();

			for (SqlElement r : this.sql) {
				r.setSequence(oMaxSeq + r.getSequence());
			}

			this.sql.addAll(copyOf(other.getSql()));
		}
		if (other.getExp() != null && !other.getExp().isEmpty()) {
			if (this.exp == null)
				this.exp = new ArrayList<ExpElement>();

			for (ExpElement r : this.exp) {
				r.setSequence(oMaxSeq + r.getSequence());
			}

			this.exp.addAll(copyOf(other.getExp()));
		}
		if (other.getImp() != null && !other.getImp().isEmpty()) {
			if (this.imp == null)
				this.imp = new ArrayList<ImpElement>();

			for (ImpElement r : this.imp) {
				r.setSequence(oMaxSeq + r.getSequence());
			}

			this.imp.addAll(copyOf(other.getImp()));
		}

		if (other.getLog() != null && !other.getLog().isEmpty()) {
			if (this.log == null)
				this.log = new ArrayList<LogElement>();

			this.log.addAll(copyOf(other.getLog()));
		}
		if (other.getJson() != null && !other.getJson().isEmpty()) {
			if (this.json == null)
				this.json = new ArrayList<JsonElement>();

			this.json.addAll(copyOf(other.getJson()));
		}
		setMaxSeq(this.maxseq + oMaxSeq);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("<srv ");
		if (hasText(id)) {
			sb.append("id=\"").append(id).append("\" ");
		}
		if (hasText(type)) {
			sb.append("type=\"").append(type).append("\" ");
		}
		if (hasText(extend)) {
			sb.append("extend=\"").append(extend).append("\" ");
		}
		if (hasText(auth)) {
			sb.append("auth=\"").append(auth).append("\" ");
		}
		if (hasText(scope)) {
			sb.append("scope=\"").append(scope).append("\" ");
		}
		if (hasText(processor)) {
			sb.append("processor=\"").append(processor).append("\" ");
		}
		if (hasText(datasource)) {
			sb.append("datasource=\"").append(datasource).append("\" ");
		}
		if (hasText(defaultlogbefore)) {
			sb.append("defaultlogbefore=\"").append(defaultlogbefore).append("\" ");
		}
		if (hasText(defaultlogafter)) {
			sb.append("defaultlogafter=\"").append(defaultlogafter).append("\" ");
		}
		if (hasText(desc)) {
			sb.append("desc=\"").append(desc).append("\" ");
		}
		if (hasText(cache)) {
			sb.append("cache=\"").append(cache).append("\" ");
		}
		if (hasText(updatecache)) {
			sb.append("updatecache=\"").append(updatecache).append("\" ");
		}
		sb.append(">");

		int level = 1;
		if (in != null) {
			sb.append(in.toString(level));
		}
		if (out != null) {
			sb.append(out.toString(level));
		}
		if (ref != null) {
			for (RefElement r : ref) {
				sb.append(r.toString(level));
			}
		}
		if (quit != null) {
			for (QuitElement q : quit) {
				sb.append(q.toString(level));
			}
		}
		if (sql != null) {
			for (SqlElement s : sql) {
				sb.append(s.toString(level));
			}
		}
		if (exp != null) {
			for (ExpElement s : exp) {
				sb.append(s.toString(level));
			}
		}
		if (imp != null) {
			for (ImpElement s : imp) {
				sb.append(s.toString(level));
			}
		}
		if (log != null) {
			for (LogElement l : log) {
				sb.append(l.toString(level));
			}
		}
		if (json != null) {
			for (JsonElement j : json) {
				sb.append(j.toString(level));
			}
		}
		sb.append("\n</srv>");
		return sb.toString();
	}
}