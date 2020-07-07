package io.treasure.controller;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import io.treasure.annotation.Login;
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
import io.treasure.entity.MerchantActivityEntity;
import io.treasure.service.MerchantActivityService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 商户活动管理
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-01
 */
@Slf4j
@RestController
@RequestMapping("/merchantactivity")
@Api(tags="商户活动管理")
public class MerchantActivityController {
    @Autowired
    private MerchantActivityService merchantActivityService;
    @CrossOrigin
    @Login
    @GetMapping("page")
    @ApiOperation("列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
        @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
        @ApiImplicitParam(name="merchantId",value="商户编号",paramType = "query",required = true, dataType="String")
    })
    public Result<PageData<MerchantActivityDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Common.STATUS_ON.getStatus()+"");
        PageData<MerchantActivityDTO> page = merchantActivityService.listPage(params);
        return new Result<PageData<MerchantActivityDTO>>().ok(page);
    }
    @CrossOrigin
    @Login
    @GetMapping("getById")
    @ApiOperation("信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", paramType = "query", required = true, dataType = "long")
    })
    public Result<MerchantActivityDTO> get(@RequestParam  Long id){
        MerchantActivityDTO data = merchantActivityService.get(id);
        return new Result<MerchantActivityDTO>().ok(data);
    }
    @CrossOrigin
    @Login
    @PostMapping("save")
    @ApiOperation("保存")
    public Result save(@RequestBody MerchantActivityDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);
        MerchantActivityEntity entity=new MerchantActivityEntity();
        entity.setTitle(dto.getTitle());
        entity.setBrief(dto.getBrief());
        entity.setContent(dto.getContent());
        entity.setIcon(dto.getIcon());
        entity.setMerchantId(dto.getMerchantId());
        entity.setCreator(dto.getCreator());
        entity.setCreateDate(new Date());
        entity.setStatus(Common.STATUS_ON.getStatus());
        merchantActivityService.insert(entity);
        return new Result();
    }
    @CrossOrigin
    @Login
    @PutMapping("update")
    @ApiOperation("修改")
    public Result update(@RequestBody MerchantActivityDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);
        MerchantActivityEntity entity=new MerchantActivityEntity();
        entity.setTitle(dto.getTitle());
        entity.setBrief(dto.getBrief());
        entity.setContent(dto.getContent());
        entity.setIcon(dto.getIcon());
        entity.setMerchantId(dto.getMerchantId());
        entity.setUpdater(dto.getUpdater());
        entity.setUpdateDate(new Date());
        entity.setStatus(Common.STATUS_ON.getStatus());
        entity.setId(dto.getId());
        merchantActivityService.updateById(entity);
        return new Result();
    }
    @CrossOrigin
    @Login
    @DeleteMapping("delete")
    @ApiOperation("删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", paramType = "query", required = true, dataType = "long")
    })
    public Result delete(@RequestParam  Long id){
        merchantActivityService.remove(id,Common.STATUS_DELETE.getStatus());
        return new Result();
    }
}