package io.treasure.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.treasure.common.utils.Result;
import io.treasure.entity.AgreementsEntity;
import io.treasure.service.AgreementsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 协议管理
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-24
 */
@RestController
@RequestMapping("/agreements")
@Api(tags="协议管理")
public class AgreementsController {
    @Autowired
    private AgreementsService agreementsService;

    /**
     * 查询协议信息
     * @return
     */
    @GetMapping("getAgreementsByStatusOn")
    @ApiOperation("查询协议信息")
    public Result<AgreementsEntity> getAgreementsByStatusOn(){
        List<AgreementsEntity> data = agreementsService.getAgreementsByStatusOn();
        if(null!=data && data.size()>0){
            return new Result<AgreementsEntity>().ok(data.get(0));
        }
        return new Result<AgreementsEntity>().ok(null);
    }
}