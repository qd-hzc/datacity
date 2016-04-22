package com.city.common.util;


import com.city.common.pojo.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日志工具类
 */
public class EsiLogUtil {
	private static Logger logger = LoggerFactory.getLogger(EsiLogUtil.class);

	/**
	 * 输出调试信息
	 * @param log 日志对象
	 * @param msg 调试信息
	 */
	public static void debug(Logger log, String msg) {
		if (log != null) {
			if (Constant.systemConfigPojo.isDebug()) {
				log.debug(msg);
			}
		} else {
			if (Constant.systemConfigPojo.isDebug())
				logger.error("No Log Instance");
		}
	}

	/**
	 * 输出信息
	 * @param log 日志对象
	 * @param msg 信息
	 */
	public static void info(Logger log, String msg) {
		if (log != null) {
			if (Constant.systemConfigPojo.isShowSysLog()) {
				log.info(msg);
			}
		} else {
			if (Constant.systemConfigPojo.isShowSysLog())
				logger.error("No Log Instance");
		}
	}

	/**
	 * 输出错误日志
	 * @param log 日志对象
	 * @param msg 错误信息
	 */
	public static void error(Logger log, String msg) {
		if (log != null) {
			if (Constant.systemConfigPojo.isShowSysLog()) {
				log.error(msg);
			}
		} else {
			if (Constant.systemConfigPojo.isShowSysLog())
				logger.error("No Log Instance");
		}
	}

	/**
	 *
	 * @param clazz 要获取日志对象的类
	 * @return 日志对象
	 */
	public static Logger getLogInstance(Class clazz) {
		Logger result = null;
		if(clazz!=null){
			result = LoggerFactory.getLogger(clazz);
		}
		return result;
	}

	/**
	 *
	 * @param logName 日志名称
	 * @return 日志对象
	 */
	public static Logger getLogInstance(String logName) {
		Logger result = null;
		if(logName!=null){
			result = LoggerFactory.getLogger(logName);
		}
		return result;
	}
}
