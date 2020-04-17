package io.treasure.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;
import io.treasure.common.utils.Result;
import io.treasure.dto.GoodDTO;
import io.treasure.dto.MerchantDTO;
import io.treasure.entity.BannerEntity;
import io.treasure.service.BannerService;
import io.treasure.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Map;


/**
 * 轮播图
 *
 * @author zhangguanglin 494535675@qq.com
 * @since 1.0.0 2019-12-13
 */
@RestController
@RequestMapping("/banner")
@Api(tags = "轮播图设置")
public class BannerController {
    @Autowired
    private BannerService bannerService;
    @Autowired
    private MerchantService merchantService;

    @GetMapping("getAllBnner")
    @ApiOperation("查询所有轮播图")
    public List<BannerEntity> precreate() {
        return bannerService.getAllBanner();
    }
    @GetMapping("martLike")
    @ApiOperation("模糊查询商家")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name="name",value="商户名称",paramType = "query",required = true, dataType="String")
    })
    public Result<PageData<MerchantDTO>> martLike(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<MerchantDTO> page = merchantService.martLike(params);
        return new Result<PageData<MerchantDTO>>().ok(page);
    }

}
