package com.thirtyai.nezha.common.wrap;

import com.thirtyai.nezha.common.NezhaConstant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * response wrapper
 *
 * @author kyleju
 */
@Data
@Accessors(chain = true)
@ApiModel(description = "response wrapper")
public class Resp<T> implements Serializable {
	private static final long serialVersionUID = -4691106035819561618L;
	@ApiModelProperty(value = "http status", name = "success", example = "200")
	private int status;
	@ApiModelProperty(value = "operate code", name = "message code", example = "200")
	private String code = "";
	@ApiModelProperty(value = "desc", name = "desc", example = "description")
	private String desc = "";
	@ApiModelProperty(value = "total count", name = "total", example = "10")
	private Integer total = NezhaConstant.DEFAULT_VALUE;
	@ApiModelProperty(value = "data")
	private T data;
}
