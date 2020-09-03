package io.treasure.controller;

import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;
import io.treasure.common.utils.Result;
import io.treasure.entity.AdvertisementEntity;
import io.treasure.service.AdvertisementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/advertisement")
@Api(tags="广告")
public class AdvController {

    @Autowired
    private AdvertisementService advertisementService;

    @GetMapping("startupPageUser")
    @ApiOperation("启动页广告")
    public Result<String> selectById(Integer type) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("page", 1);
        params.put("limit", 100);
        params.put("type", type);
        List<AdvertisementEntity> list = advertisementService.pageList(params).getList();
        if(list != null && list.size() > 0) {
            return new Result<String>().ok(list.get(0).getImage());
        }
        return new Result<String>().ok("");
    }

    @GetMapping("page")
    @ApiOperation("分页查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int")
    })
    public Result<PageData<AdvertisementEntity>> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        return new Result<PageData<AdvertisementEntity>>().ok(advertisementService.pageList(params));
    }


}
