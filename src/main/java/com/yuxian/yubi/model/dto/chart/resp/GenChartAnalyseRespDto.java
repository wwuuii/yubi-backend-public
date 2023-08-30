package com.yuxian.yubi.model.dto.chart.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yuxian&羽弦
 * date 2023/06/02 09:21
 * description:
 * @version 1.0
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenChartAnalyseRespDto {

	private String genChart;

	private String genResult;

	private Long chartId;

	private String execMessage;

	private Integer status;

}
