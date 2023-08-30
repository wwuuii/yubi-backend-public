package com.yuxian.yubi.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yuxian.yubi.annotation.AuthCheck;
import com.yuxian.yubi.annotation.RequestLimit;
import com.yuxian.yubi.common.BaseResponse;
import com.yuxian.yubi.constant.CommonConstant;
import com.yuxian.yubi.constant.UserConstant;
import com.yuxian.yubi.convert.ChartConvert;
import com.yuxian.yubi.enums.ErrorCode;
import com.yuxian.yubi.common.ResultUtils;
import com.yuxian.yubi.enums.ChartTypeEnum;
import com.yuxian.yubi.exception.ThrowUtils;
import com.yuxian.yubi.model.dto.chart.req.ChartQueryReqDto;
import com.yuxian.yubi.model.dto.chart.req.ChartReqDto;
import com.yuxian.yubi.model.dto.chart.req.GenChartAnalyseReqDto;
import com.yuxian.yubi.model.entity.Chart;
import com.yuxian.yubi.model.entity.User;
import com.yuxian.yubi.service.ChartService;
import com.yuxian.yubi.service.UserService;
import com.yuxian.yubi.utils.SqlUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @author yuxian&羽弦
 * date 2023/05/29 22:31
 * description:
 * @version 1.0
 **/
@RestController
@RequestMapping("/char")
public class ChartController {

	@Resource
	private ChartService chartService;
	@Resource
	private UserService userService;

	@PostMapping("/genChartAnalyse")
	@RequestLimit()
	public BaseResponse<String> genChartAnalyse(@RequestPart("file") MultipartFile multipartFile,
																GenChartAnalyseReqDto genChartAnalyseReqDto, HttpServletRequest request) {
		//参数校验
		checkGenChartParam(genChartAnalyseReqDto, multipartFile);
		//权限校验
		User loginUser = userService.getLoginUser(request);
		ThrowUtils.throwIf(Objects.isNull(loginUser), ErrorCode.NO_AUTH_ERROR);

		genChartAnalyseReqDto.setUserId(loginUser.getId());
		chartService.genChartAnalyse(multipartFile, genChartAnalyseReqDto);
		return ResultUtils.success("提交成功");
	}

	private void checkGenChartParam(GenChartAnalyseReqDto genChartAnalyseReqDto, MultipartFile multipartFile) {
		String name = genChartAnalyseReqDto.getName();
		String goal = genChartAnalyseReqDto.getGoal();
		String chartType = genChartAnalyseReqDto.getChartType();
		ThrowUtils.throwIf(!ChartTypeEnum.chartTypeMap.containsKey(chartType),  ErrorCode.PARAMS_ERROR, "图表类型错误");
		ThrowUtils.throwIf(StringUtils.isBlank(goal), ErrorCode.PARAMS_ERROR, "分析目标不能为空");
		ThrowUtils.throwIf(StringUtils.isBlank(goal) || name.length() > 100, ErrorCode.PARAMS_ERROR, "图表名字长度不能超过100");
		genChartAnalyseReqDto.setChartType(ChartTypeEnum.chartTypeMap.get(chartType));
		ThrowUtils.throwIf(Objects.isNull(multipartFile), ErrorCode.PARAMS_ERROR, "文件不能为空");
		final long ONE_MB = 1024 * 1024;
		ThrowUtils.throwIf(multipartFile.getSize() > ONE_MB, ErrorCode.PARAMS_ERROR, "文件大小不能超过1M");
	}

	// region 增删改查

	/**
	 * 创建
	 *
	 * @param chartReqDto
	 * @param request
	 * @return
	 */
	@PostMapping("/add")
	public BaseResponse<Long> addChart(@RequestBody ChartReqDto chartReqDto, HttpServletRequest request) {
		ThrowUtils.throwIf(Objects.isNull(chartReqDto), ErrorCode.PARAMS_ERROR, "请求参数不能为空");
		Chart chart = ChartConvert.INSTANCE.AddReqToEo(chartReqDto);
		User loginUser = userService.getLoginUser(request);
		chart.setUserId(loginUser.getId());
		boolean result = chartService.save(chart);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		long newChartId = chart.getId();
		return ResultUtils.success(newChartId);
	}

	/**
	 * 删除
	 *
	 * @param id
	 * @param request
	 * @return
	 */
	@PostMapping("/delete/{id}")
	public BaseResponse<Boolean> deleteChart(@PathVariable("id") Long id, HttpServletRequest request) {
		ThrowUtils.throwIf(Objects.isNull(id), ErrorCode.PARAMS_ERROR, "id不能为空");
		User user = userService.getLoginUser(request);
		// 判断是否存在
		Chart oldChart = chartService.getById(id);
		ThrowUtils.throwIf(Objects.isNull(oldChart), ErrorCode.NOT_FOUND_ERROR, "要删除的图表不存在");
		ThrowUtils.throwIf(!oldChart.getUserId().equals(user.getId()) && !userService.isAdmin(request), ErrorCode.NO_AUTH_ERROR, "没有权限");
		boolean result = chartService.removeById(id);
		return ResultUtils.success(result);
	}

	/**
	 * 更新（仅管理员）
	 *
	 * @param chartReqDto
	 * @return
	 */
	@PostMapping("/update")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Boolean> updateChart(@RequestBody ChartReqDto chartReqDto) {
		ThrowUtils.throwIf(Objects.isNull(chartReqDto), ErrorCode.PARAMS_ERROR, "请求参数不能为空");
		Chart chart = ChartConvert.INSTANCE.AddReqToEo(chartReqDto);
		long id = chartReqDto.getId();
		// 判断是否存在
		Chart oldChart = chartService.getById(id);
		ThrowUtils.throwIf(Objects.isNull(oldChart), ErrorCode.NOT_FOUND_ERROR,"要修改的图表不存在");
		boolean result = chartService.updateById(chart);
		return ResultUtils.success(result);
	}

	/**
	 * 根据 id 获取
	 *
	 * @param id
	 * @return
	 */
	@GetMapping("/get/{id}")
	public BaseResponse<Chart> getChartById(@PathVariable("id")Long id, HttpServletRequest request) {
		ThrowUtils.throwIf(Objects.isNull(id) || id <= 0, ErrorCode.PARAMS_ERROR, "id不能为空并且必须大于0");
		Chart result = chartService.getById(id);
		ThrowUtils.throwIf(Objects.isNull(result), ErrorCode.NOT_FOUND_ERROR, "图表不存在");
		return ResultUtils.success(result);
	}


	/**
	 * 获取查询包装类
	 *
	 * @param chartQueryRequest
	 * @return
	 */
	private QueryWrapper<Chart> getQueryWrapper(ChartQueryReqDto chartQueryRequest) {
		ThrowUtils.throwIf(Objects.isNull(chartQueryRequest), ErrorCode.PARAMS_ERROR, "请求参数不能为空");
		QueryWrapper<Chart> queryWrapper = new QueryWrapper<>();
		Long id = chartQueryRequest.getId();
		String name = chartQueryRequest.getName();
		String goal = chartQueryRequest.getGoal();
		String chartType = chartQueryRequest.getChartType();
		Long userId = chartQueryRequest.getUserId();
		String sortField = chartQueryRequest.getSortField();
		String sortOrder = chartQueryRequest.getSortOrder();

		queryWrapper.eq(id != null && id > 0, "id", id);
		queryWrapper.like(StringUtils.isNotBlank(name), "name", name);
		queryWrapper.eq(StringUtils.isNotBlank(goal), "goal", goal);
		queryWrapper.eq(StringUtils.isNotBlank(chartType), "chartType", chartType);
		queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
		queryWrapper.eq("isDelete", false);
		queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
				sortField);
		return queryWrapper;
	}


	/**
	 * 分页获取当前用户创建的资源列表
	 *
	 * @param chartQueryRequest
	 * @param request
	 * @return
	 */
	@PostMapping("/my/list/page")
	public BaseResponse<Page<Chart>> listMyChartByPage(@RequestBody ChartQueryReqDto chartQueryRequest,
													   HttpServletRequest request) {
		ThrowUtils.throwIf(Objects.isNull(chartQueryRequest), ErrorCode.PARAMS_ERROR, "请求参数不能为空");
		User loginUser = userService.getLoginUser(request);
		chartQueryRequest.setUserId(loginUser.getId());
		long current = chartQueryRequest.getCurrent();
		long size = chartQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		Page<Chart> chartPage = chartService.page(new Page<>(current, size),
				getQueryWrapper(chartQueryRequest));
		return ResultUtils.success(chartPage);
	}

	// endregion

}
