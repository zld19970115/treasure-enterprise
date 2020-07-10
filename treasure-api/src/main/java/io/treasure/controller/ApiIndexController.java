package io.treasure.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.sun.org.apache.bcel.internal.generic.NEW;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;
import io.treasure.common.utils.Result;
import io.treasure.dto.GoodCategoryDTO;
import io.treasure.dto.MerchantDTO;
import io.treasure.entity.MerchantEntity;
import io.treasure.jra.impl.UserSearchJRA;
import io.treasure.service.GoodCategoryService;
import io.treasure.service.MerchantService;
import io.treasure.service.impl.EvaluateServiceImpl;
import io.treasure.vo.SearchKeysVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private EvaluateServiceImpl evaluateService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private UserSearchJRA userSearchJRA;

    @Autowired
    private GoodCategoryService goodCategoryService;

    @GetMapping("queryMerchant")
    @ApiOperation("全文检索店铺")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "name", value = "店铺名称支持模糊查找", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "longitude", value = "顾客的经度", paramType = "query",required=true, dataType="String"),
            @ApiImplicitParam(name = "latitude", value = "顾客的纬度", paramType = "query",required=true, dataType="String")
    })
    public Result<PageData<MerchantDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<MerchantDTO> page = merchantService.getLikeMerchant(params);

        return new Result<PageData<MerchantDTO>>().ok(page);
    }

    @GetMapping("queryRecommendMerchant")
    @ApiOperation("推荐信息店铺")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "longitude", value = "顾客的经度", paramType = "query",required=true, dataType="String"),
            @ApiImplicitParam(name = "latitude", value = "顾客的纬度", paramType = "query",required=true, dataType="String")
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
            @ApiImplicitParam(name ="orderByField", value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "longitude", value = "顾客的经度", paramType = "query",required=true, dataType="String"),
            @ApiImplicitParam(name = "name", value = "店铺名称支持模糊查找", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "latitude", value = "顾客的纬度", paramType = "query",required=true, dataType="String"),
            @ApiImplicitParam(name = "categoryId", value = "经营类别", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "distanced", value = "距离", paramType = "query", dataType="int")
    })
    public Result<PageData<MerchantDTO>> queryHotMerchant(@ApiIgnore @RequestParam Map<String, Object> params){
//        params.put("recommend","1");
        PageData<MerchantDTO> page = merchantService.queryRoundPage(params);

        return new Result<PageData<MerchantDTO>>().ok(page);
    }
//    public  void s() {
//
//        QueryWrapper<MerchantEntity> mweqw = new QueryWrapper<>();
//
//        if (not_cash != null)
//            mweqw.orderByDesc()
//    }
//
//        Page<MerchantEntity> map = new Page<MerchantEntity>(index,itemNum);
//        map.setCurrent(index);
//        map.setSize(itemNum);
//        IPage<MerchantEntity> merchantEntityIPage = merchantDao.selectPage(map, mweqw);
//        return new Result().ok(merchantEntityIPage);
//return new Result().ok(merchantWithdrawEntityIPage.getRecords());

    @GetMapping("queryALLMerchant")
    @ApiOperation("附近销量店铺")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = "orderByField", value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "longitude", value = "顾客的经度", paramType = "query",required=true, dataType="String"),
            @ApiImplicitParam(name = "name", value = "店铺名称支持模糊查找", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "latitude", value = "顾客的纬度", paramType = "query",required=true, dataType="String"),
            @ApiImplicitParam(name = "distanced", value = "距离", paramType = "query", dataType="int")
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
            @ApiImplicitParam(name = "orderByField" ,value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "longitude", value = "顾客的经度", paramType = "query",required=true, dataType="String"),
            @ApiImplicitParam(name = "name", value = "店铺名称支持模糊查找", paramType = "query", dataType="String"),
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
    @GetMapping("SearchKeysVo")
    @ApiOperation("查询历史记录与系统设置记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "userId", paramType = "query", dataType="String")
    })
    public Result<SearchKeysVo> SearchKeysVo(@ApiIgnore @RequestParam Map<String, Object> params){
        SearchKeysVo searchKeysVo = new SearchKeysVo();
//        if (params.get("userId")!=null){
//            Set<String> userId = userSearchJRA.getSetMembers((String) params.get("userId"));
////            if(userId!=null){
////                List<String> result = new ArrayList<>(userId);
////                searchKeysVo.setUserProdValue(result);
////            }
//        }
        Set<String> SysProd = userSearchJRA.getSetMembers("SysProd");
        List<String> result = new ArrayList<>(SysProd);
        searchKeysVo.setSysProdValues(result);

        return new  Result<SearchKeysVo>().ok(searchKeysVo);
    }
    @GetMapping("delUserSearchKeysVo")
    @ApiOperation("删除历史记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "userId", paramType = "query", required = true, dataType="String")
    })
    public Result delUserSearchKeysVo(@ApiIgnore @RequestParam Map<String, Object> params){
        if (params.get("userId")!=null){
            Long aLong = userSearchJRA.removeAll((String) params.get("userId"));
            return new  Result().ok(aLong);
        }else {
            return new  Result().ok("0");
        }
    }
    @GetMapping("insertSysSearchKeysVo")
    @ApiOperation("添加系统设置记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "value", value = "value", paramType = "query", required = true, dataType="String")
    })
    public Result insertSysSearchKeysVo(@ApiIgnore @RequestParam Map<String, Object> params){
        if (params.get("value")!=null){
           userSearchJRA.add("SysProd",(String) params.get("value"));
            return new  Result().ok("添加成功");
        }else {
            return new  Result().ok("0");
        }
    }

    @GetMapping("delSysSearchKeysVo")
    @ApiOperation("删除系统设置记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "value", value = "value", paramType = "query", required = true, dataType="String")
    })
    public Result delSysSearchKeysVo(@ApiIgnore @RequestParam Map<String, Object> params){
        if (params.get("value")!=null){
            userSearchJRA.delItem("SysProd",(String) params.get("value"));
            return new  Result().ok("删除成功");
        }else {
            return new  Result().ok("0");
        }
    }
    @GetMapping("getSysAndUserSearchKeysVo")
    @ApiOperation("保存历史记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "userId", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "name", value = "查询", paramType = "query", dataType="String")
    })
    public Result getSysAndUserSearchKeysVo(@ApiIgnore @RequestParam Map<String, Object> params){
        if (params.get("userId")!=null&&params.get("name")!=null){
            userSearchJRA.add((String) params.get("userId"),(String) params.get("name"));
            return new Result().ok((String) params.get("name"));
        }else {
            return new Result().ok((String) params.get("name"));
        }

}
}
