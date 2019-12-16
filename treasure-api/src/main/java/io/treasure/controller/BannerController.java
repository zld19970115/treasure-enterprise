package io.treasure.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.treasure.entity.BannerEntity;
import io.treasure.service.BannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


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

    @GetMapping("getAllBnner")
    @ApiOperation("查询所有轮播图")
    public List<BannerEntity> precreate() {
        return bannerService.getAllBanner();
    }
}
