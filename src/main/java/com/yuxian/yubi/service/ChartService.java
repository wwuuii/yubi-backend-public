package com.yuxian.yubi.service;

import com.yuxian.yubi.model.dto.chart.req.GenChartAnalyseReqDto;
import com.yuxian.yubi.model.dto.chart.resp.GenChartAnalyseRespDto;
import com.yuxian.yubi.model.entity.Chart;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author admin
 * @description 针对表【chart(图表信息表)】的数据库操作Service
 * @createDate 2023-05-27 16:32:46
 */
public interface ChartService extends IService<Chart> {

	/**
	 * 生成用户请求字符串
	 *
	 * @param multipartFile
	 * @param genChartAnalyseReqDto
	 * @return
	 */
	void genChartAnalyse(MultipartFile multipartFile, GenChartAnalyseReqDto genChartAnalyseReqDto);

	/**
	 * 修改图表状态
	 *
	 * @param id
	 * @param status
	 */
	void updateChartStatus(Long id, Integer status);


}
