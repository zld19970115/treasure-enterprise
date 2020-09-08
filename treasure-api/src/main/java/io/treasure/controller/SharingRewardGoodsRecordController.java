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

    public Result getRecords(SharingRewardGoodsRecordComboVo conditionEntity) {

        List<SharingRewardGoodsRecordEntity> records = sharingRewardGoodsRecordService.getRecords(conditionEntity);
        return new Result().ok(records);
    }

}
