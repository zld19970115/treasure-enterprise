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
import io.treasure.entity.ClientUserEntity;
import io.treasure.entity.EvaluateEntity;
import io.treasure.service.impl.ClientUserServiceImpl;
import io.treasure.service.impl.EvaluateServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.*;


/**
 * 用户评价表
 * 2019.8.21
 */
@RestController
@RequestMapping("/evaluate")
@Api(tags="用户评价表")
public class EvaluateController {
    @Autowired
    private EvaluateServiceImpl evaluateService;
    @Autowired
    private ClientUserServiceImpl clientUserService;
    @RequestMapping("/add")
    @ApiOperation("添加评价表")
    public Result addEvaluate(@RequestBody EvaluateDTO dto){
        EvaluateEntity evaluateEntity = new EvaluateEntity();
        evaluateEntity.setHygiene(dto.getHygiene());
        evaluateEntity.setAttitude(dto.getAttitude());
        evaluateEntity.setFlavor(dto.getFlavor());
        evaluateEntity.setPrice(dto.getPrice());
        evaluateEntity.setMartId(dto.getMartId());
        evaluateEntity.setProposal(dto.getProposal());
        evaluateEntity.setSpeed(dto.getSpeed());
        evaluateEntity.setUid(dto.getUid());
        evaluateEntity.setTotalScore(dto.getHygiene()+dto.getFlavor()+dto.getPrice()+dto.getSpeed()+dto.getAttitude());
        evaluateEntity.setMasterorderId(dto.getMasterorderId());
        evaluateEntity.setCreator(dto.getCreator());
        evaluateEntity.setUpdater(dto.getUpdater());
        evaluateEntity.setUpdateDate(dto.getUpdateDate());
        evaluateEntity.setCreateDate(new Date());
        evaluateEntity.setStatus(Common.STATUS_ON.getStatus());
        ClientUserEntity clientUserEntity = clientUserService.selectById(dto.getUid());
        evaluateEntity.setUsername(clientUserEntity.getUsername());
        evaluateEntity.setHeadImg(clientUserEntity.getHeadImg());
        evaluateEntity.setAvgUser((dto.getHygiene()+dto.getFlavor()+dto.getPrice()+dto.getSpeed()+dto.getAttitude())/5);
        evaluateService.insert(evaluateEntity);
        return new Result();
    }
    @RequestMapping("/del")
    @ApiOperation("删除评价表")
    public Result  delEvaluate(@ApiIgnore int id){

        evaluateService.delEvaluate(id);
        return new Result();
    }
    @GetMapping("/see")
    @ApiOperation("查看评价表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name="merchantId",value="商户编号",paramType = "query",required = true, dataType="long")
    })
    public Result<PageData<EvaluateDTO>> seeEvaluate(@ApiIgnore @RequestParam Map<String, Object> params, long merchantId){

        params.put("status", Common.STATUS_OFF.getStatus()+"");
        PageData<EvaluateDTO> page = evaluateService.page(params);
        List list = page.getList();
        Map map= new HashMap();
        Double avgSpeed = evaluateService.selectAvgSpeed(merchantId);
        Double avgHygiene = evaluateService.selectAvgHygiene(merchantId);
        Double avgAttitude = evaluateService.selectAvgAttitude(merchantId);
        Double avgFlavor = evaluateService.selectAvgFlavor(merchantId);
        Double avgAllScore = evaluateService.selectAvgAllScore(merchantId);
        map.put("avgHygiene",avgHygiene);//平均环境卫生
        map.put("avgAttitude",avgAttitude);//平均服务态度
        map.put("avgFlavor",avgFlavor);//平均菜品口味
        map.put("avgSpeed",avgSpeed);//平均上菜速度
        map.put("avgAllScore",avgAllScore);//平均上菜速度
        list.add(map);
        return new Result<PageData<EvaluateDTO>>().ok(page);
    }
}
