package io.treasure.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;
import io.treasure.common.utils.Result;
import io.treasure.dto.EvaluateDTO;
import io.treasure.enm.Common;
import io.treasure.service.impl.EvaluateServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.HashMap;
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



    @GetMapping("/MerchanSee")
    @ApiOperation("查看评价表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name="merchantId",value="商户编号",paramType = "query",required = true, dataType="long")
    })
    public Result<PageData<EvaluateDTO>> seeMarchanEvaluate(@ApiIgnore @RequestParam Map<String, Object> params, long merchantId){

        params.put("status", Common.STATUS_ON.getStatus()+"");
        PageData<EvaluateDTO> page = evaluateService.page(params);
        Map map= new HashMap();
        Double avgSpeed = evaluateService.selectAvgSpeed(merchantId);
        Double avgHygiene = evaluateService.selectAvgHygiene(merchantId);
        Double avgAttitude = evaluateService.selectAvgAttitude(merchantId);
        Double avgFlavor = evaluateService.selectAvgFlavor(merchantId);
        Double avgAllScore = evaluateService.selectAvgAllScore(merchantId);
        map.put("avgHygiene",Math.round(avgHygiene));//平均环境卫生
        map.put("avgAttitude",Math.round(avgAttitude));//平均服务态度
        map.put("avgFlavor",Math.round(avgFlavor));//平均菜品口味
        map.put("avgSpeed",Math.round(avgSpeed));//平均上菜速度
        map.put("avgAllScore",Math.round(avgAllScore));//平均上菜速度
        map.put("page",page);
        return new Result().ok(map);
    }
}

