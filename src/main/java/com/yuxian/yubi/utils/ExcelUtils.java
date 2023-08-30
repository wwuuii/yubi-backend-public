package com.yuxian.yubi.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.yuxian.yubi.enums.ErrorCode;
import com.yuxian.yubi.exception.ThrowUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yuxian&羽弦
 * date 2023/05/29 22:41
 * description:
 * @version 1.0
 **/
@Slf4j
public class ExcelUtils {

	private final static List<String> validSuffix = Arrays.asList("xlsx","xls");

	public static String excelToCsv(MultipartFile multipartFile) {
		ThrowUtils.throwIf(Objects.isNull(multipartFile), ErrorCode.PARAMS_ERROR, "上传文件不能为空");
		String suffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
		ThrowUtils.throwIf(!validSuffix.contains(suffix), ErrorCode.PARAMS_ERROR, "文件格式错误");
		List<Map<Integer, String>> excelData = readExcel(multipartFile);
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < excelData.size(); i++) {
			LinkedHashMap<Integer, String> rowData = (LinkedHashMap<Integer, String>) excelData.get(i);

			result.append(StringUtils.join(rowData.values().stream().filter(Objects::nonNull).collect(Collectors.toList()),",")).append("\n");
		}

		Assert.notBlank(result, "excel转csv转换失败");
		return result.toString();
	}

	/**
	 * 读取Excel数据
	 *
	 * @param multipartFile
	 */
	private static List<Map<Integer, String>> readExcel(MultipartFile multipartFile) {
		Assert.notNull(multipartFile, "参数multipartFile 不能为空");
		List<Map<Integer, String>> result = null;
		try {
			result = EasyExcel.read(multipartFile.getInputStream())
					.excelType(ExcelTypeEnum.XLSX)
					.sheet()
					.headRowNumber(0)
					.doReadSync();

		} catch (IOException e) {
			log.error("表格处理错误", e);
		}
		return result;
	}

}