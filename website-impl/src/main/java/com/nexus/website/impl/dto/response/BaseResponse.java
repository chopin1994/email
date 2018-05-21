package com.nexus.website.impl.dto.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class BaseResponse implements Serializable {

	private static final long serialVersionUID = 844524418947789561L;

	/**
	 * 200表示成功，其他自定义编码
	 */
	private String code;

	/**
	 * 信息
	 */
	private String message;

	/**
	 * 详细信息
	 */
	private String detailMessage;

	/**
	 * 响应其他数据
	 */
	private Object data;
}
