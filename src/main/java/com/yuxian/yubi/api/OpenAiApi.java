package com.yuxian.yubi.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yupi.yucongming.dev.client.YuCongMingClient;
import com.yupi.yucongming.dev.common.BaseResponse;
import com.yupi.yucongming.dev.model.DevChatRequest;
import com.yupi.yucongming.dev.model.DevChatResponse;
import com.yuxian.yubi.enums.ErrorCode;
import com.yuxian.yubi.exception.BusinessException;
import com.yuxian.yubi.exception.ThrowUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author yuxian&羽弦
 * date 2023/06/02 10:39
 * description:
 * @version 1.0
 **/
@Service
public class OpenAiApi {

	@Resource
	private YuCongMingClient client;


	public String[] genChartAnalyse(Long modelId, String question) {
		DevChatRequest devChatRequest = new DevChatRequest();
		devChatRequest.setModelId(modelId);
		devChatRequest.setMessage(question);
		BaseResponse<DevChatResponse> response = client.doChat(devChatRequest);
		ThrowUtils.throwIf(response == null || response.getData() == null, ErrorCode.SYSTEM_ERROR, "生成AI回答失败");

		String chartAnalyseResult = response.getData().getContent();
		ThrowUtils.throwIf(StringUtils.isBlank(chartAnalyseResult), ErrorCode.SYSTEM_ERROR, "生成AI回答失败");
		String[] results = chartAnalyseResult.split("【【【【【");
		ThrowUtils.throwIf(results.length != 3, ErrorCode.SYSTEM_ERROR, "生成AI回答失败");
		results[1] = results[1].substring(results[1].indexOf('{'), results[1].lastIndexOf('}') + 1);
		results[1] = removeTitle(results[1]);
		return results;
	}

	/**
	 * 隐藏图表的 title
	 * @param jsonString
	 * @return
	 */
	private String removeTitle(String jsonString) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode jsonNode = mapper.readTree(jsonString);
			if (jsonNode.has("title")) {
				// 如果 JSON 中有 "title" 属性，则将其移除
				ObjectNode objectNode = (ObjectNode) jsonNode;
				objectNode.remove("title");
				jsonString = mapper.writeValueAsString(objectNode);
			}
		} catch (Exception e) {
			throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生成AI回答失败");
		}
		return jsonString;
	}

}
