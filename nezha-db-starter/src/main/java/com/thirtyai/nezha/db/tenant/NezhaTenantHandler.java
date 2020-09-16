/*
 * Copyright (c) 2019-2020 kyle ju(Email: nezha@thirtyai.com  QQ: 17062743) All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thirtyai.nezha.db.tenant;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.tenant.TenantHandler;
import lombok.RequiredArgsConstructor;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;

/**
 * nezha tenant handler
 *
 * @author kyleju
 */
@RequiredArgsConstructor
public class NezhaTenantHandler implements TenantHandler {
	private final NezhaTenantProperties tenantProperties;
	private final ITenantId iTenantId;

	@Override
	public Expression getTenantId(boolean where) {
		if (iTenantId != null) {
			String tenantId = iTenantId.getTenantId(where);
			if (StrUtil.isBlank(tenantId)) {
				return null;
			}
			return new StringValue(tenantId);
		}
		return null;
	}

	@Override
	public String getTenantIdColumn() {
		return tenantProperties.getField();
	}

	@Override
	public boolean doTableFilter(String tableName) {
		if (StrUtil.isBlank(tenantProperties.getField())) {
			return false;
		}

		if (tenantProperties.getTables() != null) {
			return tenantProperties.getTables().contains(tableName);
		} else if (tenantProperties.getExclusionTables() != null) {
			return !tenantProperties.getExclusionTables().contains(tableName);
		} else {
			return false;
		}
	}
}
