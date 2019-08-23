package io.treasure.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;
import io.treasure.common.utils.Result;
import io.treasure.dto.EvaluateDTO;
import io.treasure.dto.GoodDTO;
import io.treasure.enm.Common;
import io.treasure.service.impl.EvaluateServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Map;

/**
 * 商户评价表
 * 2019.8.22
 */
@RestController
@RequestMapping("/MerchanEvaluate")
@Api(tags="商户评价表")
public class MerchanEvaluateController {

    @Autowired
    private EvaluateServiceImpl evaluateService;



    @RequestMapping("/MerchanSee")
    @ApiOperation("查看评价表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name="merchantId",value="商户编号",paramType = "query",required = true, dataType="long")
    })
    public Result<PageData<EvaluateDTO>> seeMarchanEvaluate(@ApiIgnore @RequestParam Map<String, Object> params){

        PageData<EvaluateDTO> page = evaluateService.page(params);
        return new Result<PageData<EvaluateDTO>>().ok(page);
    }
}

