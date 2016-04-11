package com.platform.cubism.service.log;

import static com.platform.cubism.util.StringUtils.hasText;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.slf4j.LoggerFactory;

import com.platform.cubism.base.CArray;
import com.platform.cubism.base.Json;
import com.platform.cubism.base.JsonFactory;
import com.platform.cubism.service.ServiceFactory;
import com.platform.cubism.service.config.FldElement;
import com.platform.cubism.service.config.LogElement;
import com.platform.cubism.service.core.ConditionFactory;
import com.platform.cubism.util.CubismHelper;
import com.platform.cubism.util.HeadHelper;

public class DefaultLogWriter implements LogWriter {
	private static final Pattern constans = Pattern.compile("^\'.*\'$|^\".*\"$", Pattern.CASE_INSENSITIVE);
	private static final Pattern plussplit = Pattern.compile(".*\\+.*", Pattern.CASE_INSENSITIVE);
	private org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());
	private List<LogElement> logElem;

	public static DefaultLogWriter getLogger(List<LogElement> logElem) {
		return new DefaultLogWriter(logElem);
	}

	private DefaultLogWriter(List<LogElement> logElem) {
		this.logElem = logElem;
	}

	public Json beginLog(Json in) {
		List<LogElement> elem = getValidLogElement(in);
		if (elem == null) {
			logger.debug("该服务未配置日志选项，不记录before日志。");
			return null;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("服务执行前日志记录器启动，开始记录日志...");
		}
		Json result = JsonFactory.create();
		for (LogElement le : elem) {
			String beforeServiceName = le.getBefore();
			if (hasText(beforeServiceName)) {
				Json ret = ServiceFactory.executeService(beforeServiceName, logMapping(in, le));
				result.mergeOf(ret);
				if (!HeadHelper.isSuccess(ret)) {
					break;
				}
			}
		}
		return result;
	}

	public Json endLog(Json in) {
		List<LogElement> elem = getValidLogElement(in);
		if (elem == null) {
			logger.debug("该服务没有配置日志选项，不记录end日志.");
			return null;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("服务执行前日志记录器启动，开始记录日志...");
		}
		Json result = JsonFactory.create();
		for (LogElement le : elem) {
			String afterServiceName = le.getAfter();
			if (hasText(afterServiceName)) {
				try {
					Json ret = ServiceFactory.executeService(afterServiceName, logMapping(in, le), true);
					result.mergeOf(ret);
					if (!HeadHelper.isSuccess(ret)) {
						break;
					}
				} catch (Exception e) {
					if (logger.isErrorEnabled()) {
						logger.error("after日志服务执行错误：" + e.getMessage());
					}
				}
			}

		}
		return result;
	}

	private Json logMapping(Json in, LogElement le) {
		Json log = CubismHelper.deepCopy(in);
		if (le == null || le.isEmpty() || le.isEmpty()) {
			return log;
		}
		if (le.getFld() != null && !le.getFld().isEmpty()) {
			log.mergeOf(fldMapping(log, le.getFld()));
		}
		if (le.getRow() != null && !le.getRow().isEmpty()) {
			String name = "log";
			if (le.getRow().size() == 1) {
				log.addStruc(fldMapping(log, le.getRow().get(0)).setName(name).getObject());
			} else {
				CArray ca = new CArray(name);
				for (List<FldElement> flds : le.getRow()) {
					ca.add(fldMapping(log, flds).getObject());
				}
				log.remove(name);
				log.addArray(ca);
			}
		}
		return log;
	}

	private Json fldMapping(Json log, List<FldElement> flds) {
		Json ret = JsonFactory.create();
		if (flds == null || flds.isEmpty()) {
			return ret;
		}
		for (FldElement fld : flds) {
			if (constans.matcher(fld.getValue()).find()) {
				String value = fld.getValue().substring(1, fld.getValue().length() - 1);
				ret.addField(fld.getName(), value);
			} else if (plussplit.matcher(fld.getValue()).find()) {
				String[] values = fld.getValue().split("\\+");
				StringBuilder sb = new StringBuilder();
				for (String v : values) {
					if (constans.matcher(fld.getValue()).find()) {
						sb.append(v.substring(1, v.length() - 1));

					} else {
						if (!hasText(v)) {
							continue;
						}

						Object obj = log.get(v);
						if (obj == null) {
							continue;
						}
						sb.append(log.getStringValues(obj, ","));
					}
				}
				ret.addField(fld.getName(), sb.toString());
			} else {
				if (!hasText(fld.getName())) {
					continue;
				}

				Object obj = log.get(fld.getValue());
				if (obj == null) {
					continue;
				}
				ret.addField(fld.getName(), log.getStringValues(obj, ","));
			}
		}
		return ret;
	}

	private List<LogElement> getValidLogElement(Json in) {
		if (logElem == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("该服务没有配置日志选项");
			}
			return null;
		}
		List<LogElement> elem = new ArrayList<LogElement>();
		for (LogElement le : logElem) {
			if (hasText(le.getCondition())) {
				if (ConditionFactory.isTrue(le.getCondition(), in)) {
					elem.add(le);
					if (logger.isDebugEnabled()) {
						logger.debug("日志记录条件满足：" + le.getName() + "条件：" + le.getCondition());
					}
					continue;
				}
				if (logger.isDebugEnabled()) {
					logger.debug("日志记录条件不满足,跳过：" + le.getName() + "条件：" + le.getCondition());
				}
			} else {
				elem.add(le);
				if (logger.isDebugEnabled()) {
					logger.debug("无条件记录日志：" + le.getName());
				}
			}
		}
		if (elem == null || elem.isEmpty()) {
			if (logger.isDebugEnabled()) {
				logger.debug("该服务没有满足条件的配置日志选项");
			}
			return null;
		}
		return elem;
	}
}