package io.treasure.controller;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.annotation.Login;
import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;
import io.treasure.common.utils.Result;
import io.treasure.dto.*;
import io.treasure.entity.GoodCategoryEntity;
import io.treasure.service.JahresabschlussService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/Jahresabschluss")
@Api(tags="商户端财务报表")
public class JahresabschlussController {
    @Autowired
    private io.treasure.service.JahresabschlussService JahresabschlussService;

    @Login
    @GetMapping("getJahresabschluss")
    @ApiOperation("获取财务报表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name="merchantId",value="商户编号",paramType ="query",required = false,dataType = "String"),
            @ApiImplicitParam(name = "startTime1", value = "开始日期", paramType = "query", required = false, dataType="String"),
            @ApiImplicitParam(name = "endTime1", value = "截止日期", paramType = "query", required = false, dataType="String")
    })
    public  Result<PageData<GoodCategoryDTO>> getJahresabschluss(@ApiIgnore @RequestParam Map<String, Object> params) {
       PageData<GoodCategoryDTO> list = JahresabschlussService.selectByParams(params);
        return new Result().ok(list);
 }

}
