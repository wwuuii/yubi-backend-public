package com.yuxian.yubi.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yuxian&羽弦
 * date 2023/06/02 08:55
 * description:
 * @version 1.0
 **/
@AllArgsConstructor
@NoArgsConstructor
public enum ChartTypeEnum {

	LINE_CHART("line_chart", "折线图"),
	histogram("histogram", "柱状图"),
	STACK_CHART("stack_chart", "堆叠图"),
	PIE_CHART("pie_chart", "饼图"),
	RADAR_CHART("radar_chart", "雷达图");

	private String typeCode;
	private String typeName;

	public String getTypeCode() {
		return typeCode;
	}

	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public static final Map<String, String> chartTypeMap =
			Arrays.stream(ChartTypeEnum.values()).collect(Collectors.toMap(ChartTypeEnum::getTypeCode, ChartTypeEnum::getTypeName));
}
