package io.treasure.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;
import io.treasure.common.utils.Result;
import io.treasure.dto.GoodCategoryDTO;
import io.treasure.dto.MerchantDTO;
import io.treasure.service.GoodCategoryService;
import io.treasure.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Map;

/**
 * 用户信息
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-28
 */
@RestController
@RequestMapping("/api/index")
@Api(tags="首页模块")
public class
ApiIndexController {

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private GoodCategoryService goodCategoryService;

    @GetMapping("queryMerchant")
    @ApiOperation("全文检索店铺")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "name", value = "店铺名称支持模糊查找", paramType = "query", dataType="String")
    })
    public Result<PageData<MerchantDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<MerchantDTO> page = merchantService.queryAllPage(params);

        return new Result<PageData<MerchantDTO>>().ok(page);
    }

    @GetMapping("queryRecommendMerchant")
    @ApiOperation("推荐信息店铺")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String")
    })
    public Result<PageData<MerchantDTO>> queryRecommendMerchant(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("recommend","1");
        PageData<MerchantDTO> page = merchantService.queryPage(params);

        return new Result<PageData<MerchantDTO>>().ok(page);
    }

    @GetMapping("queryHotMerchant")
    @ApiOperation("热门附近店铺")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "longitude", value = "顾客的经度", paramType = "query",required=true, dataType="String"),
            @ApiImplicitParam(name = "latitude", value = "顾客的纬度", paramType = "query",required=true, dataType="String")
    })
    public Result<PageData<MerchantDTO>> queryHotMerchant(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("recommend","1");
        PageData<MerchantDTO> page = merchantService.queryRoundPage(params);

        return new Result<PageData<MerchantDTO>>().ok(page);
    }

    @GetMapping("queryALLMerchant")
    @ApiOperation("附近全部店铺")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "longitude", value = "顾客的经度", paramType = "query",required=true, dataType="String"),
            @ApiImplicitParam(name = "latitude", value = "顾客的纬度", paramType = "query",required=true, dataType="String")
    })
    public Result<PageData<MerchantDTO>> queryALLMerchant(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<MerchantDTO> page = merchantService.queryRoundPage(params);
        return new Result<PageData<MerchantDTO>>().ok(page);
    }

    @GetMapping("queryClassifyMerchant")
    @ApiOperation("附近分类店铺")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "longitude", value = "顾客的经度", paramType = "query",required=true, dataType="String"),
            @ApiImplicitParam(name = "latitude", value = "顾客的纬度", paramType = "query",required=true, dataType="String"),
            @ApiImplicitParam(name = "categoryId", value = "商户编号", paramType = "query",required=true, dataType="Long")
    })
    public Result<PageData<MerchantDTO>> queryClassifyMerchant(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<MerchantDTO> page = merchantService.queryRoundPage(params);
        return new Result<PageData<MerchantDTO>>().ok(page);
    }

    @GetMapping("queryClassify")
    @ApiOperation("店铺分类信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
    })
    public Result<PageData<GoodCategoryDTO>> queryClassify(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<GoodCategoryDTO> page = goodCategoryService.page(params);
        return new Result<PageData<GoodCategoryDTO>>().ok(page);
    }
}
