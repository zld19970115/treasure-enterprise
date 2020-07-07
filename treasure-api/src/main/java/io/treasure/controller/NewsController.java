package io.treasure.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.annotation.Login;
import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;
import io.treasure.common.utils.Result;
import io.treasure.dto.BannerDto;
import io.treasure.dto.NewsDto;
import io.treasure.entity.NewsEntity;
import io.treasure.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Arrays;
import java.util.Map;

@RestController
@RequestMapping("/news")
@Api(tags="公告消息")
public class NewsController {

    @Autowired
    private NewsService newsService;

    //@Login
    @GetMapping("page")
    @ApiOperation("分页查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = "startDate", value = "开始time", paramType = "query",dataType="String") ,
            @ApiImplicitParam(name = "endDate", value = "结束time", paramType = "query",dataType="String")
    })
    public Result<PageData<NewsDto>> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        return new Result<PageData<NewsDto>>().ok(newsService.page(params));
    }

    //@Login
    @GetMapping("agreePage")
    @ApiOperation("分页查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int")
    })
    public Result<PageData<NewsDto>> agreePage(@ApiIgnore @RequestParam Map<String, Object> params) {
        return new Result<PageData<NewsDto>>().ok(newsService.agreePage(params));
    }

    //@Login
    @CrossOrigin
    @GetMapping("newsById")
    @ApiOperation("根据id查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", paramType = "query",dataType="String") ,
    })
    public Result<NewsDto> bannerById(@ApiIgnore @RequestParam Long id) {
        return new Result<NewsDto>().ok(newsService.get(id));
    }

    @Login
    @GetMapping("del")
    @ApiOperation("删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", paramType = "query",dataType="String") ,
    })
    public Result<Boolean> del(@ApiIgnore @RequestParam Long id) {
        return new Result<Boolean>().ok(newsService.deleteBatchIds(Arrays.asList(new Long[]{id})));
    }

    @Login
    @PostMapping("update")
    @ApiOperation("更新")
    public Result<String> update(@RequestBody NewsDto dto) {
        newsService.update(dto);
        return new Result<String>().ok("ok");
    }

    @Login
    @PostMapping("insert")
    @ApiOperation("新增")
    public Result<String> insert(@RequestBody NewsEntity dto) {
        newsService.insert(dto);
        return new Result<String>().ok("ok");
    }

    //@Login
    @GetMapping("privacyAgrre")
    @ApiOperation("隐私协议")
    public Result<NewsDto> privacyAgrre() {
        return newsService.privacyAgrre();
    }

    //@Login
    @GetMapping("userAgrre")
    @ApiOperation("用户协议")
    public Result<NewsDto> userAgrre() {
        return newsService.userAgrre();
    }



}
