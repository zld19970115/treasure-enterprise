package io.treasure.controller;


import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;
import io.treasure.common.utils.Result;
import io.treasure.common.validator.AssertUtils;
import io.treasure.common.validator.ValidatorUtils;
import io.treasure.common.validator.group.AddGroup;
import io.treasure.common.validator.group.DefaultGroup;
import io.treasure.common.validator.group.UpdateGroup;
import io.treasure.dto.MerchantActivityDTO;
import io.treasure.enm.Common;
import io.treasure.service.MerchantActivityService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;


/**
 * 商户活动管理
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-01
 */
@RestController
@RequestMapping("/merchantactivity")
@Api(tags="商户活动管理")
public class MerchantActivityController {
    @Autowired
    private MerchantActivityService merchantActivityService;

    @GetMapping("page")
    @ApiOperation("列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
        @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
        @ApiImplicitParam(name="merchantId",value="商户编号",paramType = "query",dataType="String")
    })
    public Result<PageData<MerchantActivityDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<MerchantActivityDTO> page = merchantActivityService.page(params);
        params.put("status", Common.STATUS_ON.getStatus()+"");
        return new Result<PageData<MerchantActivityDTO>>().ok(page);
    }

    @GetMapping("{id}")
    @ApiOperation("信息")
    public Result<MerchantActivityDTO> get(@PathVariable("id") Long id){
        MerchantActivityDTO data = merchantActivityService.get(id);

        return new Result<MerchantActivityDTO>().ok(data);
    }

    @PostMapping("save")
    @ApiOperation("保存")
    public Result save(@RequestBody MerchantActivityDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);
        merchantActivityService.save(dto);
        return new Result();
    }

    @PutMapping("update")
    @ApiOperation("修改")
    public Result update(@RequestBody MerchantActivityDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);
        merchantActivityService.update(dto);
        return new Result();
    }

    @DeleteMapping("delete")
    @ApiOperation("删除")
    public Result delete(@PathVariable Long id){
        merchantActivityService.remove(id,Common.STATUS_DELETE.getStatus());
        return new Result();
    }

}