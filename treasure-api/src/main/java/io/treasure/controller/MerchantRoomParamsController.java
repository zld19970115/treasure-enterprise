package io.treasure.controller;

import io.treasure.annotation.Login;
import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;
import io.treasure.common.utils.Result;
import io.treasure.common.validator.ValidatorUtils;
import io.treasure.common.validator.group.AddGroup;
import io.treasure.common.validator.group.UpdateGroup;
import io.treasure.dto.MerchantRoomParamsDTO;
import io.treasure.enm.Common;
import io.treasure.entity.MerchantRoomParamsEntity;
import io.treasure.service.MerchantRoomParamsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 商户端包房参数管理
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-12
 */
@RestController
@RequestMapping("/merchantroomparams")
@Api(tags="商户端包房参数管理")
public class MerchantRoomParamsController {
    @Autowired
    private MerchantRoomParamsService merchantRoomParamsService;
    @Login
    @GetMapping("page")
    @ApiOperation("列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
        @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name="merchantId",value="商户编号",paramType = "queury",required = true,dataType = "long")
    })
    public Result<PageData<MerchantRoomParamsDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<MerchantRoomParamsDTO> page = merchantRoomParamsService.page(params);

        return new Result<PageData<MerchantRoomParamsDTO>>().ok(page);
    }
    @Login
    @GetMapping("allPage")
    @ApiOperation("不根据商户查询列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String")
    })
    public Result<PageData<MerchantRoomParamsDTO>> allPage(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<MerchantRoomParamsDTO> page = merchantRoomParamsService.page(params);

        return new Result<PageData<MerchantRoomParamsDTO>>().ok(page);
    }
    @Login
    @GetMapping("getInfoById")
    @ApiOperation("信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id",value="编号",paramType = "queury",required = true,dataType = "Long")
    })
    public Result<MerchantRoomParamsDTO> get(Long id){
        MerchantRoomParamsDTO data = merchantRoomParamsService.get(id);

        return new Result<MerchantRoomParamsDTO>().ok(data);
    }
    @Login
    @PostMapping("save")
    @ApiOperation("保存")
    public Result save(@RequestBody MerchantRoomParamsDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class);
        //根据商户和内容判断是否增加过
        List<MerchantRoomParamsEntity> list=merchantRoomParamsService.getByMerchantIdAndContent(dto.getMerchantId(),dto.getContent(),Common.STATUS_ON.getStatus());
        if(null!=list && list.size()>0){
            return new Result().error("参数内容已经存在！");
        }
        dto.setCreateDate(new Date());
        merchantRoomParamsService.save(dto);
        return new Result();
    }
    @Login
    @PutMapping("update")
    @ApiOperation("修改")
    public Result update(@RequestBody MerchantRoomParamsDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class);
        //根据商户和内容判断是否增加过
        List<MerchantRoomParamsEntity> list=merchantRoomParamsService.getByMerchantIdAndContent(dto.getMerchantId(),dto.getContent(),Common.STATUS_ON.getStatus());
        if(null!=list && list.size()>0){
            return new Result().error("参数内容已经存在！");
        }
        dto.setUpdateDate(new Date());
        merchantRoomParamsService.update(dto);
        return new Result();
    }
    @Login
    @DeleteMapping("remove")
    @ApiOperation("删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id",value="编号",paramType = "queury",required = true,dataType = "Long")
    })
    public Result delete(Long id){
        merchantRoomParamsService.remove(id, Common.STATUS_DELETE.getStatus());
        return new Result();
    }
}