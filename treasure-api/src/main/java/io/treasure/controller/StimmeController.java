package io.treasure.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.common.utils.Result;
import io.treasure.entity.StimmeEntity;
import io.treasure.service.impl.StimmeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/stimme")
@Api(tags="语音推送")
public class StimmeController {
    @Autowired
    private StimmeServiceImpl stimmeService;

    @GetMapping("/sti")
    @ApiOperation("获取是否有新订单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "merchantId", value = "编号", paramType = "query", required = true, dataType="String")
    })
    public Result sti(@ApiIgnore @RequestParam Map<String, Object> params) {
     List<StimmeEntity> stimmeEntities = stimmeService.selectBymerchantId(params);

            return new Result().ok(stimmeEntities.size());//存在新订单
    }


}
