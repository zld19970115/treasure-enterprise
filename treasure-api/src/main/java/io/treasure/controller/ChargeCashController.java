package io.treasure.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.treasure.common.utils.Result;
import io.treasure.dto.EvaluateDTO;
import io.treasure.entity.CardInfoEntity;
import io.treasure.entity.ChargeCashEntity;
import io.treasure.entity.ChargeCashSetEntity;
import io.treasure.service.BannerService;
import io.treasure.service.ChargeCashService;
import io.treasure.service.ChargeCashSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 现金充值表
 * 2019.8.21
 */
@RestController
@RequestMapping("/chargrCash")
@Api(tags="现金充值表")
public class ChargeCashController {
    @Autowired
    private ChargeCashSetService chargeCashSetService;
    @Autowired
    private ChargeCashService chargeCashService;
    @PostMapping("/add")
    @ApiOperation("现金充值")
    public Result chargrCash(@RequestParam Long userId ,@RequestParam BigDecimal cash){

        ChargeCashSetEntity chargeCashSetEntity = chargeCashSetService.selectByCash(cash);
        BigDecimal changeGift = chargeCashSetEntity.getChangeGift();
        ChargeCashEntity chargeCashEntity = new ChargeCashEntity();
        chargeCashEntity.setCash(cash);
        chargeCashEntity.setChangeGift(changeGift);
        Date date = new Date();
        chargeCashEntity.setSaveTime(date);
        chargeCashEntity.setUserId(userId);
        chargeCashService.insert(chargeCashEntity);
        return new Result().ok("现金充值成功");
    }
}
