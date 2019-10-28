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
import io.treasure.entity.MasterOrderEntity;
import io.treasure.entity.MerchantEntity;
import io.treasure.service.MasterOrderService;
import io.treasure.service.impl.ClientUserServiceImpl;
import io.treasure.service.impl.EvaluateServiceImpl;
import io.treasure.service.impl.MerchantServiceImpl;
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
    @Autowired
    private MerchantServiceImpl merchantService;
    @PostMapping("/add")
    @ApiOperation("添加评价表")
    public Result addEvaluate(@RequestBody EvaluateDTO dto){

        EvaluateEntity evaluateEntity1 = evaluateService.selectByUserIdAndOid(dto.getUid(), dto.getMasterorderId());
        if (evaluateEntity1!=null){
            return new Result().error("已评价");
        }
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
        MerchantEntity merchantEntity = merchantService.selectById(dto.getMartId());
        Double avgAllScore = evaluateService.selectAvgAllScore2(dto.getMartId());
        merchantEntity.setScore(avgAllScore);
        merchantService.updateById(merchantEntity);
        return new Result().ok("评价成功 ");
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
    public Result<PageData<EvaluateDTO>> seeEvaluate(@ApiIgnore @RequestParam Map<String, Object> params){

        params.put("status", Common.STATUS_ON.getStatus()+"");
        PageData<EvaluateDTO> page = evaluateService.selectPage(params);
        List list = page.getList();
        Map map= new HashMap();
        Double avgSpeed = evaluateService.selectAvgSpeed(params);
        Double avgHygiene = evaluateService.selectAvgHygiene(params);
        Double avgAttitude = evaluateService.selectAvgAttitude(params);
        Double avgFlavor = evaluateService.selectAvgFlavor(params);
        Double avgAllScore = evaluateService.selectAvgAllScore(params);
        map.put("avgHygiene",Math.round(avgHygiene));//平均环境卫生
        map.put("avgAttitude",Math.round(avgAttitude));//平均服务态度
        map.put("avgFlavor",Math.round(avgFlavor));//平均菜品口味
        map.put("avgSpeed",Math.round(avgSpeed));//平均上菜速度
        map.put("avgAllScore",Math.round(avgAllScore));//平均总分
        list.add(map);
        return new Result<PageData<EvaluateDTO>>().ok(page);
    }
    @GetMapping("/seeComment")
    @ApiOperation("查看评论表")
    public Result seeComment(@RequestParam long merchantId){
        List<EvaluateEntity> evaluateEntities = evaluateService.selectByMerchantId(merchantId);
        return new Result().ok(evaluateEntities);
    }
}
