package io.treasure.controller;

import io.treasure.annotation.Login;
import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;
import io.treasure.common.utils.Result;
import io.treasure.common.validator.ValidatorUtils;
import io.treasure.common.validator.group.AddGroup;
import io.treasure.common.validator.group.UpdateGroup;
import io.treasure.dto.ExportGoodDTO;
import io.treasure.dto.GoodCategoryDTO;
import io.treasure.dto.GoodDTO;
import io.treasure.dto.MerchantDTO;
import io.treasure.enm.CategoryEnm;
import io.treasure.enm.Common;
import io.treasure.entity.GoodCategoryEntity;
import io.treasure.entity.MerchantEntity;
import io.treasure.service.GoodCategoryService;
import io.treasure.service.GoodService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.service.MerchantService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 商品表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-29
 */
@RestController
@RequestMapping("/good")
@Api(tags="商品表")
public class GoodController {
    @Autowired
    private GoodService goodService;
    @Autowired
    private MerchantService merchantService;//商户
    @Autowired
    private GoodCategoryService goodCategoryService;//分类
    @CrossOrigin
    @Login
    @GetMapping("onPage")
    @ApiOperation("销售中商品列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
        @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name="merchantId",value="商户编号",paramType = "query",required = true,dataType = "String"),
            @ApiImplicitParam(name="name",value="商品名称",paramType = "query",dataType = "String")
    })
    public Result<PageData<GoodDTO>> onPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status",Common.STATUS_ON.getStatus()+"");
        PageData<GoodDTO> page = goodService.listPage(params);
        return new Result<PageData<GoodDTO>>().ok(page);
    }
    @CrossOrigin
    @Login
    @GetMapping("offPage")
    @ApiOperation("已下架品列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name="merchantId",value="商户编号",paramType = "query",required = true,dataType = "String"),
            @ApiImplicitParam(name="name",value="商品名称",paramType = "query",dataType = "String")
    })
    public Result<PageData<GoodDTO>> offPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status",Common.STATUS_OFF.getStatus()+"");
        PageData<GoodDTO> page = goodService.listPage(params);
        return new Result<PageData<GoodDTO>>().ok(page);
    }
    @CrossOrigin
    @Login
    @GetMapping("getById")
    @ApiOperation("详细信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", paramType = "query", required = true, dataType = "long")
    })
    public Result<GoodDTO> get(Long id){
        if(id>0){
            GoodDTO data = goodService.getByInfo(id);
            return new Result<GoodDTO>().ok(data);
        }else{
            return new Result<GoodDTO>().error(null);
        }
    }
    @CrossOrigin
    @Login
    @PostMapping("save")
    @ApiOperation("保存")
    public Result save(@RequestBody GoodDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class);
        //根据菜品名称、商户查询该菜品名称是否存在
        List list=goodService.getByNameAndMerchantId(dto.getName(),dto.getMartId());
        if(null!=list && list.size()>0){
            return new Result().error("菜品名称已经存在！");
        }
        if(dto.getStatus()==Common.STATUS_ON.getStatus()){//启用
            dto.setShelveTime(new Date());
            dto.setShelveBy(dto.getCreator());
        }else if(dto.getStatus()==Common.STATUS_OFF.getStatus()){//禁用
            dto.setOffShelveBy(dto.getCreator());
            dto.setOffShelveTime(new Date());
        }
        dto.setCreateDate(new Date());
        goodService.save(dto);
        return new Result();
    }
    @CrossOrigin
    @Login
    @PutMapping("update")
    @ApiOperation("修改")
    public Result update(@RequestBody GoodDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class);
        GoodDTO data =goodService.get(dto.getId());
        if(!data.getName().equals(dto.getName())){
            //根据菜品名称、商户查询该菜品名称是否存在
            List list=goodService.getByNameAndMerchantId(dto.getName(),dto.getMartId());
            if(null!=list && list.size()>0){
                return new Result().error("菜品名称已经存在！");
            }
        }
        if(data.getStatus()!=dto.getStatus()){
            if(dto.getStatus()==Common.STATUS_ON.getStatus()){//启用
                dto.setShelveTime(new Date());
                dto.setShelveBy(dto.getCreator());
            }else if(dto.getStatus()==Common.STATUS_OFF.getStatus()){//禁用
                dto.setOffShelveBy(dto.getCreator());
                dto.setOffShelveTime(new Date());
            }
        }
        dto.setUpdateDate(new Date());
        goodService.update(dto);
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
        //判断商户是否关闭店铺
        GoodDTO goodDto=goodService.get(id);
        long merchantId=goodDto.getMartId();
        if(merchantId>0){
            MerchantDTO merchantDto= merchantService.get(merchantId);
            if(merchantDto!=null){
                int status=merchantDto.getStatus();//状态
                if(status==Common.STATUS_CLOSE.getStatus()){
                    goodService.remove(id,Common.STATUS_DELETE.getStatus());
                }else{
                    return new Result().error("请关闭店铺后，在进行删除操作！");
                }
            }else {
                return new Result().error("没有找到菜品的店铺!");
            }
        }else {
            return new Result().error("没有找到菜品的店铺!");
        }

        return new Result();
    }

    /**
     * 上架商品
     * @param id
     * @return
     */
    @CrossOrigin
    @Login
    @PutMapping("on")
    @ApiOperation("上架商品")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", paramType = "query", required = true, dataType = "long")
    })
    public Result on( @RequestParam Long id){
        //判断商户是否关闭店铺
        GoodDTO goodDto=goodService.get(id);
        long merchantId=goodDto.getMartId();
        if(merchantId>0){
            MerchantDTO merchantDto= merchantService.get(merchantId);
            if(merchantDto!=null){
                int status=merchantDto.getStatus();//状态
                if(status==Common.STATUS_CLOSE.getStatus()){
                    goodService.on(id,Common.STATUS_ON.getStatus());
                }else{
                    return new Result().error("请关闭店铺后，在进行上架操作！");
                }
            }else {
                return new Result().error("没有找到菜品的店铺!");
            }
        }else {
            return new Result().error("没有找到菜品的店铺!");
        }
        return new Result();
    }

    /**
     * 下架商品
     * @param id
     * @return
     */
    @CrossOrigin
    @Login
    @PutMapping("off")
    @ApiOperation("下架商品")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", paramType = "query", required = true, dataType = "long")
    })
    public Result off(@RequestParam  Long id){
        //判断商户是否关闭店铺
        GoodDTO goodDto=goodService.get(id);
        long merchantId=goodDto.getMartId();
        if(merchantId>0){
            MerchantDTO merchantDto= merchantService.get(merchantId);
            if(merchantDto!=null){
                int status=merchantDto.getStatus();//状态
                if(status==Common.STATUS_CLOSE.getStatus()){
                    goodService.off(id,Common.STATUS_OFF.getStatus());
                }else{
                    return new Result().error("请关闭店铺后，在进行下架操作！");
                }
            }else{
                return new Result().error("没有获取到菜品的店铺!");
            }
        }else {
            return new Result().error("没有获取到菜品的店铺!");
        }
        return new Result();
    }

    @CrossOrigin
    @PostMapping("exportGoods")
    @ApiOperation("导入")
    public Result exportGoods(@RequestBody ExportGoodDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class);
        String name=dto.getName();
        //商户名称
        String martId=dto.getMartId();
        //分类名称
        String goodCategoryId=dto.getGoodCategoryId();
        //创建者
        long creator=dto.getCreator();
        //根据商户名称查询商户编号
        MerchantEntity merchantEntity=merchantService.getByName(martId,Common.STATUS_DELETE.getStatus());
        if(null==merchantEntity){
            return new Result().error("商户不存在，请先注册商户！");
        }
        long merchantId=merchantEntity.getId();
        //根据分类名称查询分类编号
        List goodCategoryList=goodCategoryService.getByNameAndMerchantId(goodCategoryId,merchantId);
        if(null==goodCategoryList || goodCategoryList.size()==0){
            GoodCategoryEntity categoryEntity=new GoodCategoryEntity();
            categoryEntity.setName(goodCategoryId);
            categoryEntity.setCreateDate(new Date());
            categoryEntity.setCreator(creator);
            categoryEntity.setShowInCommend(CategoryEnm.SHOW_IN_COMMEND_NO.getStatus());
            categoryEntity.setStatus(Common.STATUS_OFF.getStatus());
            categoryEntity.setMerchantId(merchantId);
            goodCategoryService.insert(categoryEntity);
            goodCategoryList=goodCategoryService.getByNameAndMerchantId(goodCategoryId,merchantId);
        }
        GoodCategoryDTO categoryMap=(GoodCategoryDTO)goodCategoryList.get(0);
        long categoryId=categoryMap.getId();
        //根据菜品名称、商户查询该菜品名称是否存在
        List goodsList=goodService.getByNameAndMerchantId(name,merchantId);
        if(null!=goodsList && goodsList.size()>0){
            return new Result().error("菜品名称已经存在！");
        }
        GoodDTO goodDTO=new GoodDTO();
        goodDTO.setUnits(dto.getUnits());
        goodDTO.setName(dto.getName());
        goodDTO.setMartId(merchantId);
        goodDTO.setGoodCategoryId(categoryId);
        goodDTO.setPrice(dto.getPrice());
        goodDTO.setFavorablePrice(dto.getFavorablePrice());
        goodDTO.setShowInHot(dto.getShowInHot());
        goodDTO.setIntroduce(dto.getIntroduce());
        goodDTO.setBarcode(dto.getBarcode());
        goodDTO.setStatus(Common.STATUS_ON.getStatus());
        goodDTO.setShelveTime(new Date());
        goodDTO.setShelveBy(dto.getCreator());
        goodDTO.setCreateDate(new Date());
        goodDTO.setCreator(creator);
        goodService.save(goodDTO);
        return new Result();
    }

}