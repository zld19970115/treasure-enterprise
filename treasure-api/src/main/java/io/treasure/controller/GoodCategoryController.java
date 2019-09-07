package io.treasure.controller;


import io.treasure.annotation.Login;
import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;
import io.treasure.common.utils.Result;
import io.treasure.common.validator.ValidatorUtils;
import io.treasure.dto.GoodCategoryDTO;
import io.treasure.enm.Common;
import io.treasure.entity.GoodCategoryEntity;
import io.treasure.service.GoodCategoryService;
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
 * 店铺类型分类表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-24
 */
@RestController
@RequestMapping("/goodcategory")
@Api(tags="菜品分类管理")
public class GoodCategoryController {
    @Autowired
    private GoodCategoryService goodCategoryService;
    @Login
    @GetMapping("pageOn")
    @ApiOperation("显示中列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = Constant.PAGE, value = "1", paramType = "query", required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.LIMIT, value = "10", paramType = "query",required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "id", paramType = "query", dataType="String") ,
        @ApiImplicitParam(name = Constant.ORDER, value = "desc", paramType = "query", dataType="String"),
            @ApiImplicitParam(name="merchantId",value="商户编号",paramType = "query",required = true,dataType = "long")
    })
    public Result<PageData<GoodCategoryDTO>> pageOn(@ApiIgnore @RequestParam Map<String, Object> params, String name, String merchantId){
        params.put("status", String.valueOf(Common.STATUS_ON.getStatus()));
        params.put("name",name);
        params.put("merchantId",merchantId);
        PageData<GoodCategoryDTO> page = goodCategoryService.page(params);
        return new Result<PageData<GoodCategoryDTO>>().ok(page);
    }
    @Login
    @GetMapping("pageOff")
    @ApiOperation("隐藏中列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "1", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "2", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "id", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "desc", paramType = "query", dataType="String"),
            @ApiImplicitParam(name="merchantId",value="商户编号",paramType = "query",required = true,dataType = "long")
    })
    public Result<PageData<GoodCategoryDTO>> pageOff(@ApiIgnore @RequestParam Map<String, Object> params, String name, String merchantId){
        params.put("status", String.valueOf(Common.STATUS_OFF.getStatus()));
        params.put("name",name);
        params.put("merchantId",merchantId);
        PageData<GoodCategoryDTO> page = goodCategoryService.page(params);
        return new Result<PageData<GoodCategoryDTO>>().ok(page);
    }
    @Login
    @GetMapping("getById")
    @ApiOperation("详细信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", paramType = "query", required = true, dataType = "long")
    })
    public Result<GoodCategoryDTO> get(@RequestParam  Long id){
        GoodCategoryDTO data = goodCategoryService.get(id);
        return new Result<GoodCategoryDTO>().ok(data);
    }
    @Login
    @PostMapping("save")
    @ApiOperation("保存")
    public Result save(@RequestBody GoodCategoryDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto);
        //同一个商户，分类不能同名
        List cate= goodCategoryService.getByNameAndMerchantId(dto.getName(),dto.getMerchantId());
        if(null!=cate && cate.size()>0){
            return new Result().error("分类名称已经存在！");
        }
        GoodCategoryEntity category=new GoodCategoryEntity();
        category.setBrief(dto.getBrief());
        category.setIcon(dto.getIcon());
        category.setName(dto.getName());
        category.setStatus(dto.getStatus());
        category.setCreateDate(new Date());
        category.setCreator(dto.getCreator());
        category.setSort(dto.getSort());
        category.setMerchantId(dto.getMerchantId());
        goodCategoryService.insert(category);
        return new Result();
    }
    @Login
    @PutMapping("update")
    @ApiOperation("修改")
    public Result update(@RequestBody GoodCategoryDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto);
        //同一个商户，分类不能同名
        GoodCategoryDTO cate= goodCategoryService.get(dto.getId());
        System.out.println(!cate.getName().equals(dto.getName()));
        System.out.println(cate.getMerchantId()==dto.getMerchantId());
        System.out.println(cate.getMerchantId()+"==="+dto.getMerchantId());
        if(!cate.getName().equals(dto.getName()) && cate.getMerchantId()==dto.getMerchantId()){
            //同一个商户，分类不能同名
             List flag= goodCategoryService.getByNameAndMerchantId(dto.getName(),dto.getMerchantId());
            if(null!=flag && flag.size()>0){
                return new Result().error("分类名称已经存在！");
            }
        }

        GoodCategoryEntity category=new GoodCategoryEntity();
        category.setBrief(dto.getBrief());
        category.setIcon(dto.getIcon());
        category.setName(dto.getName());
        category.setStatus(dto.getStatus());
        category.setUpdateDate(new Date());
        category.setUpdater(dto.getCreator());
        category.setSort(dto.getSort());
        category.setId(dto.getId());
        goodCategoryService.updateById(category);
        return new Result();
    }
    @Login
    @DeleteMapping("delete")
    @ApiOperation("删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", paramType = "query", required = true, dataType = "long")
    })
    public Result delete(@RequestParam  Long id){
        goodCategoryService.remove(id,Common.STATUS_DELETE.getStatus());
        return new Result();
    }

    /**
     * 显示数据
     * @param id
     * @return
     */
    @Login
    @PutMapping("on")
    @ApiOperation("显示数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", paramType = "query", required = true, dataType = "long")
    })
    public Result on(@RequestParam  Long id){
        goodCategoryService.on(id,Common.STATUS_ON.getStatus());
        return new Result();
    }

    /**
     * 隐藏数据
     * @param id
     * @return
     */
    @Login
    @PutMapping("off")
    @ApiOperation("隐藏数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", paramType = "query", required = true, dataType = "long")
    })
    public Result off(@RequestParam  Long id){
        goodCategoryService.off(id,Common.STATUS_OFF.getStatus());
        return new Result();
    }
    /**
     * 根据商户Id显示商户分类
     * @param merchantId
     * @return
     */
    @Login
    @GetMapping("getAllByMerchantId")
    @ApiOperation("显示商户对应的分类")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "merchantId", value = "商户编号", paramType = "query", required = true, dataType = "long")
    })
    public Result<List> getAllByMerchantId(@RequestParam  Long merchantId){
       List list= goodCategoryService.getAllByMerchantId(merchantId);
       return new Result().ok(list);
      }
}