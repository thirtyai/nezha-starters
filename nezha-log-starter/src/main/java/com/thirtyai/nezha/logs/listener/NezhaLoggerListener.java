/*
 * Copyright (c) kyle ju(Email: nezha@thirtyai.com  QQ: 17062743) All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thirtyai.nezha.logs.listener;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.LoggerContextListener;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.LifeCycle;
import com.thirtyai.nezha.common.util.SpringUtil;
import com.thirtyai.nezha.logs.props.LogProperties;

/**
 * @author kyleju
 */
public class NezhaLoggerListener extends ContextAwareBase implements LoggerContextListener, LifeCycle {

	@Override
	public boolean isResetResistant() {
		return false;
	}

	@Override
	public void onStart(LoggerContext context) {

	}

	@Override
	public void onReset(LoggerContext context) {

	}

	@Override
	public void onStop(LoggerContext context) {

	}

	@Override
	public void onLevelChange(Logger logger, Level level) {

	}

	@Override
	public void start() {
		LogProperties logProperties = SpringUtil.getBean(LogProperties.class);
		Context context = getContext();
		if (logProperties != null && logProperties.isElk()) {
			context.putProperty("ELK", "TRUE");
			context.putProperty("INFO_APPENDER", "INFO_LOGSTASH");
			context.putProperty("ERROR_APPENDER", "ERROR_LOGSTASH");
			context.putProperty("ELK_ADDRESS", logProperties.getElkAddress());
		} else {
			context.putProperty("ELK", "FALSE");
			context.putProperty("INFO_APPENDER", "INFO");
			context.putProperty("ERROR_APPENDER", "ERROR");
			context.putProperty("ELKADDRESS", "");
		}
	}

	@Override
	public void stop() {

	}

	@Override
	public boolean isStarted() {
		return false;
	}
}
