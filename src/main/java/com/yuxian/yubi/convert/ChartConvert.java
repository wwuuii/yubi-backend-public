package com.yuxian.yubi.convert;

import com.yuxian.yubi.model.dto.chart.req.ChartReqDto;
import com.yuxian.yubi.model.entity.Chart;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author yuxian&羽弦
 * date 2023/06/04 19:52
 * description:
 * @version 1.0
 **/
@Mapper
public interface ChartConvert {
	ChartConvert INSTANCE = Mappers.getMapper(ChartConvert.class);

	Chart AddReqToEo(ChartReqDto addReqDto);
}
