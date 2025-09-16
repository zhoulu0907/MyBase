package com.cmsr.onebase.framework.common.tools.core.convert.impl;

import com.cmsr.onebase.framework.common.tools.core.convert.AbstractConverter;
import com.cmsr.onebase.framework.common.tools.core.map.MapUtil;
import com.cmsr.onebase.framework.common.tools.core.util.ObjUtil;

import java.util.Map;


/**
 * {@link StackTraceElement} 转换器<br>
 * 只支持Map方式转换
 *
 * @author Looly
 * @since 3.0.8
 */
public class StackTraceElementConverter extends AbstractConverter<StackTraceElement> {
	private static final long serialVersionUID = 1L;

	@Override
	protected StackTraceElement convertInternal(Object value) {
		if (value instanceof Map) {
			final Map<?, ?> map = (Map<?, ?>) value;

			final String declaringClass = MapUtil.getStr(map, "className");
			final String methodName = MapUtil.getStr(map, "methodName");
			final String fileName = MapUtil.getStr(map, "fileName");
			final Integer lineNumber = MapUtil.getInt(map, "lineNumber");

			return new StackTraceElement(declaringClass, methodName, fileName, ObjUtil.defaultIfNull(lineNumber, 0));
		}
		return null;
	}

}
