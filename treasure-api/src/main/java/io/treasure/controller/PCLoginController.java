package io.treasure.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;
import io.treasure.common.utils.Result;
import io.treasure.dto.MerchantResourceDto;
import io.treasure.dto.MerchantResourceSaveDto;
import io.treasure.dto.PlatformDto;
import io.treasure.service.MerchantResourceService;
import io.treasure.service.PlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Map;

@RestController
@RequestMapping("/pclogin")
@Api(tags="pc权限相关")
public class PCLoginController {

    @Autowired
    private PlatformService platformService;

    @Autowired
    private MerchantResourceService merchantResourceService;

    @GetMapping("platformPageList")
    @ApiOperation("平台页分页查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int")
    })
    public Result<PageData> platformPageList(@ApiIgnore @RequestParam Map<String, Object> params) {
        return new Result<PageData>().ok(platformService.pageList(params));
    }

    @PostMapping("platformSave")
    @ApiOperation("平台保存")
    public Result platformSave(@RequestBody PlatformDto dto) {
        return platformService.savePC(dto);
    }

    @PostMapping("platformUpdate")
    @ApiOperation("平台更新")
    public Result<PageData> platformUpdate(@RequestBody PlatformDto dto) {
        return platformService.updatePC(dto);
    }

    @GetMapping("platformDel")
    @ApiOperation("平台删除")
    public Result<PageData> platformDel(@RequestParam Long id) {
        return platformService.delPC(id);
    }

    @GetMapping("menuList")
    @ApiOperation("菜单列表")
    public Result menuList(@ApiIgnore @RequestParam Map<String, Object> params) {
        return new Result<>().ok(merchantResourceService.menuList(params));
    }

    @PostMapping("menuSave")
    @ApiOperation("菜单保存")
    public Result menuSave(@RequestBody MerchantResourceSaveDto dto) {
        return merchantResourceService.menuSave(dto);
    }

    @PostMapping("menuUpdate")
    @ApiOperation("菜单更新")
    public Result<PageData> menuUpdate(@RequestBody MerchantResourceSaveDto dto) {
        return merchantResourceService.menuUpdate(dto);
    }

    @GetMapping("menuDel")
    @ApiOperation("菜单删除")
    public Result<PageData> menuDel(@RequestParam Long id) {
        return merchantResourceService.menuDel(id);
    }

}
