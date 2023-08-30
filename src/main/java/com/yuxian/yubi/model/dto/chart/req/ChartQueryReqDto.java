package com.yuxian.yubi.model.dto.chart.req;

import com.yuxian.yubi.common.PageRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yuxian&羽弦
 * date 2023/06/04 20:30
 * description:
 * @version 1.0
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChartQueryReqDto extends PageRequest {
	private Long id;
	/**
	 * 分析目标
	 */
	private String goal;
	/**
	 * 图表名称
	 */
	private String name;
	/**
	 * 图表数据
	 */
	private String chartData;

	/**
	 * 图表类型
	 */
	private String chartType;

	/**
	 * 生成的图表数据
	 */
	private String genChart;

	/**
	 * 生成的分析结论
	 */
	private String genResult;

	/**
	 * 用户Id
	 */
	private Long userId;

	private Integer status;


}
