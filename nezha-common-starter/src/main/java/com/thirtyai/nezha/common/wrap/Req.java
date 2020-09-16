package com.thirtyai.nezha.common.wrap;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import java.io.Serializable;

/**
 * request body wrapper
 *
 * @author kyleju
 */
@Data
@ApiModel(description = "request wrapper")
public class Req<T> implements Serializable {
	private static final long serialVersionUID = -3201705171057777245L;
	@ApiModelProperty(value = "token", example = "PCYTefgQ-7jWHNuO39egqgTRbpEpnTghh6ifQcd3GfBXhzabK6uSDtKORsPQK_jL4g8Nzbs-XjI8Giek5utd_3QHjzbt2GJkWZV4i1hFnwWpWcmHtf4a-GugBboDhl2WvtNotYVR-izyrsRz1_rW-vLrh0jgTXwX1XC6NUUyeIu33PJnzgXMos-lYkYEad6jmTb3DKYjqkScqmBB8kvdsNuFkPHHaTvxVG0-sz6Twgm2ZXDKGU_YhsYy2tW7hRPqXQ6TjY12YzcuJbIKlUm76FZdEE_Z4h1fPDVerhicNrA")
	private String token = "";
	@ApiModelProperty(value = "current page", example = "1")
	private Integer pageNum = -1;
	@ApiModelProperty(value = "page size", example = "20")
	private Integer pageSize = -20;
	@ApiModelProperty(value = "data")
	@Valid
	private T data;

}
