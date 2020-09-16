package com.thirtyai.nezha.db.tenant;

/**
 * @author kyleju
 */
@FunctionalInterface
public interface ITenantId {
	/**
	 * @param where boolean , whether is in where condition.
	 * @return tenant id
	 */
	String getTenantId(boolean where);
}
