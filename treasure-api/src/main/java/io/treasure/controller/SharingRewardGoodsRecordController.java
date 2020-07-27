package io.treasure.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.common.utils.Result;
import io.treasure.entity.SharingRewardGoodsRecordEntity;
import io.treasure.service.SharingRewardGoodsRecordService;
import io.treasure.vo.SharingRewardGoodsRecordComboVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/reward_goods")
public class SharingRewardGoodsRecordController {

    @Autowired
    SharingRewardGoodsRecordService sharingRewardGoodsRecordService;


    @PutMapping("/item")
    @ApiOperation("插入菜品奖励记录")
    public Result insertItem(SharingRewardGoodsRecordEntity expectantEntity) {
        sharingRewardGoodsRecordService.insertItem(expectantEntity);
        return new Result().ok("insert complete");
    }

    @PostMapping("/item")
    @ApiOperation("修改菜品奖励记录")
    public Result updateItemById(SharingRewardGoodsRecordEntity updateEntity) {

        sharingRewardGoodsRecordService.updateItemById(updateEntity);
        return new Result().ok("the record has been updated");
    }
/*
    @GetMapping("/list")
    @ApiOperation(value = "奖励菜品记录列表",tags = "奖励菜品记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name ="sa_id",value = "id（非空表示查一项）",dataType = "Integer",paramType = "query",required = false),
            @ApiImplicitParam(name = "subject",value="活动名称模糊",dataType = "String",paramType = "query",required = false),
            @ApiImplicitParam(name="reward_type",value = "奖励类型",dataType = "Integer",paramType = "query",required = false),
            @ApiImplicitParam(name="reward_amount",value = "奖励数量",dataType = "Integer",paramType = "query",required = false),
            @ApiImplicitParam(name="compare_method",value = "奖励数量1:大于，2小于,0等于",dataType = "Integer",paramType = "query",required = false),
            @ApiImplicitParam(name="creator",value = "发起者",dataType = "Long",paramType = "query",required = false),
            @ApiImplicitParam(name="startTime",value = "时间范围开始",dataType = "Date",paramType = "query",required = false),
            @ApiImplicitParam(name="stopTime",value = "时间范围结束",dataType = "Date",paramType = "query",required=false),
            @ApiImplicitParam(name="index",value = "页码",dataType = "int",defaultValue = "1",paramType = "query",required = false),
            @ApiImplicitParam(name="itemNum",value = "页数",dataType = "int",defaultValue = "10",paramType = "query",required = false)
    })
*/
    public Result getRecords(SharingRewardGoodsRecordComboVo conditionEntity) {

        List<SharingRewardGoodsRecordEntity> records = sharingRewardGoodsRecordService.getRecords(conditionEntity);
        return new Result().ok(records);
    }

}
