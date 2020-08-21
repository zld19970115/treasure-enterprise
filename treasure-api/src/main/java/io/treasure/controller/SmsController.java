/**
 * Copyright (c) 2018 聚宝科技 All rights reserved.
 *
 * https://www.treasure.io
 *
 * 版权所有，侵权必究！
 */

package io.treasure.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;
import io.treasure.common.sms.SMSConfig;
import io.treasure.common.sms.SMSSend;
import io.treasure.common.utils.Result;
import io.treasure.dto.SysSmsDTO;
import io.treasure.service.SysParamsService;
import io.treasure.service.SysSmsService;
import io.treasure.utils.SendSMSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 短信服务
 *
 * @author super 63600679@qq.com
 */
@RestController
@RequestMapping("sys/sms")
@Api(tags="短信服务")
public class SmsController {
	@Autowired
	private SysSmsService sysSmsService;

	@Autowired
	private SMSConfig smsConfig;

	@GetMapping("page")
	@ApiOperation("分页")
	@ApiImplicitParams({
		@ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
		@ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
		@ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
		@ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String") ,
		@ApiImplicitParam(name = "mobile", value = "mobile", paramType = "query", dataType="String"),
		@ApiImplicitParam(name = "status", value = "status", paramType = "query", dataType="String")
	})
	public Result<PageData<SysSmsDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params){
		return new Result<PageData<SysSmsDTO>>().ok(sysSmsService.page(params));
	}

	@GetMapping("del")
	@ApiOperation("删除")
	public Result delete(@RequestParam String ids){
		sysSmsService.deleteBatchIds(JSON.parseArray(ids).toJavaList(Long.class));
		return new Result().ok("ok");
	}

	@GetMapping("sendPC")
	@ApiOperation("发送短信PC")
	public Result sendPC(@RequestParam String ids,@RequestParam String code){
		SMSSend send=new SMSSend(smsConfig);
		List<String> listTel = JSON.parseArray(ids).toJavaList(String.class);
		String [] signName = new String[listTel.size()];
		String [] tel = new String[listTel.size()];
		for(int i=0;i<listTel.size();i++) {
			signName[i] = "聚宝科技";
			tel[i] = listTel.get(i);
			String data = send.send(tel[i],signName[i],code,null);
		}
		return new Result().ok("ok");
	}

}
