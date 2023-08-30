package com.yuxian.yubi.enums;

/**
 * @author yuxian&羽弦
 * date 2023/06/13 22:42
 * description:
 * @version 1.0
 **/
public enum ChartStatusEnum {

	WAIT(0,"等待"),

	RUNNING(1,"执行中"),

	SUCCEED(2,"执行成功"),

	FAILED(3,"执行失败");


	private Integer code;
	private String desc;

	ChartStatusEnum(Integer code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public static ChartStatusEnum getEnumByCode(Integer code) {
		for (ChartStatusEnum chartStatusEnum : ChartStatusEnum.values()) {
			if (chartStatusEnum.getCode().equals(code)) {
				return chartStatusEnum;
			}
		}
		return null;
	}

	//根据code获取desc
	public static String getDescByCode(Integer code) {
		ChartStatusEnum chartStatusEnum = getEnumByCode(code);
		if (chartStatusEnum != null) {
			return chartStatusEnum.getDesc();
		}
		return null;
	}
}
