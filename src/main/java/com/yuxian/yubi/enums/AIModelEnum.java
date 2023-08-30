package com.yuxian.yubi.enums;

import lombok.AllArgsConstructor;

/**
 * @author yuxian&羽弦
 * date 2023/06/02 11:02
 * description:
 * @version 1.0
 **/
@AllArgsConstructor
public enum AIModelEnum {
	CHART_MODEL(1664471333850853377L, "图表分析模型");

	private Long id;
	private String name;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
