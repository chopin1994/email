package com.nexus.website.impl.dto.request;

import com.nexus.website.impl.utils.ApiParamCheckUtil;

import java.io.Serializable;

/**
 * Request基类
 */
public class BaseRequest implements Serializable {
	private static final long serialVersionUID = -6185241702568036350L;

	public String check() {
		return ApiParamCheckUtil.checkParam(this);
	}
}
