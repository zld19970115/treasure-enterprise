package io.treasure.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.annotation.Login;
import io.treasure.common.constant.Constant;
import io.treasure.common.exception.ErrorCode;
import io.treasure.common.exception.RenException;
import io.treasure.common.page.PageData;
import io.treasure.common.utils.Result;
import io.treasure.common.validator.ValidatorUtils;
import io.treasure.common.validator.group.AddGroup;
import io.treasure.common.validator.group.UpdateGroup;
import io.treasure.dto.GoodDTO;
import io.treasure.dto.GoodPagePCDTO;
import io.treasure.enm.Common;
import io.treasure.entity.GoodCategoryEntity;
import io.treasure.entity.TokenEntity;
import io.treasure.service.ApiGoodService;
import io.treasure.service.TokenService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
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
@RequestMapping("/Apigood")
@Api(tags = "商家商品表")
public class ApiGoodController {
    @Autowired
    private ApiGoodService apigoodService;

    @Autowired
    private TokenService tokenService;

    @Login
    @GetMapping("onPage")
    @ApiOperation("销售中商品列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType = "int"),
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query", required = true, dataType = "int"),
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "merchantId", value = "商户编号", paramType = "query", required = true, dataType = "long")
    })
    public Result<PageData<GoodDTO>> onPage(@ApiIgnore @RequestParam Map<String, Object> params) {
        params.put("status", Common.STATUS_ON.getStatus() + "");
        PageData<GoodDTO> page = apigoodService.page(params);
        return new Result<PageData<GoodDTO>>().ok(page);
    }

    @Login
    @GetMapping("offPage")
    @ApiOperation("已下架品列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType = "int"),
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query", required = true, dataType = "int"),
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "merchantId", value = "商户编号", paramType = "query", required = true, dataType = "long")
    })
    public Result<PageData<GoodDTO>> offPage(@ApiIgnore @RequestParam Map<String, Object> params) {
        params.put("status", Common.STATUS_OFF.getStatus() + "");
        PageData<GoodDTO> page = apigoodService.page(params);
        return new Result<PageData<GoodDTO>>().ok(page);
    }

    @Login
    @GetMapping("getById")
    @ApiOperation("详细信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", paramType = "query", required = true, dataType = "long")
    })
    public Result<GoodDTO> get(Long id) {
        GoodDTO data = apigoodService.get(id);
        return new Result<GoodDTO>().ok(data);
    }

    @Login
    @PostMapping
    @ApiOperation("保存")
    public Result save(@RequestBody GoodDTO dto) {
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class);
        //根据菜品名称、商户查询该菜品名称是否存在
        List list = apigoodService.getByNameAndMerchantId(dto.getName(), dto.getMartId());
        if (null != list && list.size() > 0) {
            return new Result().error("菜品名称已经存在！");
        }
        if (dto.getStatus() == Common.STATUS_ON.getStatus()) {//启用
            dto.setShelveTime(new Date());
            dto.setShelveBy(dto.getCreator());
        } else if (dto.getStatus() == Common.STATUS_OFF.getStatus()) {//禁用
            dto.setOffShelveBy(dto.getCreator());
            dto.setOffShelveTime(new Date());
        }
        dto.setCreateDate(new Date());
        apigoodService.save(dto);
        return new Result();
    }

    @Login
    @PutMapping
    @ApiOperation("修改")
    public Result update(@RequestBody GoodDTO dto) {
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class);
        GoodDTO data = apigoodService.get(dto.getId());
        if (!data.getName().equals(dto.getName())) {
            //根据菜品名称、商户查询该菜品名称是否存在
            List list = apigoodService.getByNameAndMerchantId(dto.getName(), dto.getMartId());
            if (null != list && list.size() > 0) {
                return new Result().error("菜品名称已经存在！");
            }
        }
        if (data.getStatus() != dto.getStatus()) {
            if (dto.getStatus() == Common.STATUS_ON.getStatus()) {//启用
                dto.setShelveTime(new Date());
                dto.setShelveBy(dto.getCreator());
            } else if (dto.getStatus() == Common.STATUS_OFF.getStatus()) {//禁用
                dto.setOffShelveBy(dto.getCreator());
                dto.setOffShelveTime(new Date());
            }
        }
        dto.setUpdateDate(new Date());
        apigoodService.update(dto);
        return new Result();
    }

    @Login
    @DeleteMapping("delete")
    @ApiOperation("删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", paramType = "query", required = true, dataType = "long")
    })
    public Result delete(Long id) {
        apigoodService.remove(id, Common.STATUS_DELETE.getStatus());
        return new Result();
    }

    @Login
    /**
     * 上架商品
     * @param id
     * @return
     */
    @PutMapping("on")
    @ApiOperation("上架商品")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", paramType = "query", required = true, dataType = "long")
    })
    public Result on(Long id) {
        apigoodService.on(id, Common.STATUS_ON.getStatus());
        return new Result();
    }

    /**
     * 下架商品
     *
     * @param id
     * @return
     */
    @Login
    @PutMapping("off")
    @ApiOperation("下架商品")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", paramType = "query", required = true, dataType = "long")
    })
    public Result off(Long id) {
        apigoodService.off(id, Common.STATUS_OFF.getStatus());
        return new Result();
    }

    /**
     * 菜品类别目录
     * @param martId
     * @return
     */
    @Login
    @GetMapping("getGoodCategoryByMartId")
    @ApiOperation("根据商户ID显示此商户菜品分类")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "martId", value = "商户ID", paramType = "query", required = true, dataType = "long")
    })
    public Result getGoodCategoryByMartId(Long martId) {

        List<GoodCategoryEntity> goodCategoryByMartId = apigoodService.getGoodCategoryByMartId(martId);


        return new Result().ok(goodCategoryByMartId);
    }

    /**
     * 商家全部菜品
     * @param
     * @return
     */
    @Login
    @GetMapping("getGoodsByMartId")
    @ApiOperation("根据商户ID显示此商户菜品")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "martId", value = "商户ID", paramType = "query", required = true, dataType = "String")
    })
    public Result<List> getGoodsByMartId(@ApiIgnore @RequestParam Map<String, Object> params) {
        return new Result<List>().ok(apigoodService.getGoodsByMartId(params));
    }
    /**
     * 商家全部外卖菜品
     * @param
     * @return
     */
    //@Login
    @GetMapping("getoutsideGoodsByMartId")
    @ApiOperation("根据商户ID显示此商户外卖菜品")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "martId", value = "商户ID", paramType = "query", required = true, dataType = "String")
    })
    public Result<List> getoutsideGoodsByMartId(@ApiIgnore @RequestParam Map<String, Object> params) {
        return new Result<List>().ok(apigoodService.getoutsideGoodsByMartId(params));
    }
    /**
     * 显示指定商户菜品分类中的菜品
     * @param martId,goodCategoryId
     * @return
     */
    //@Login
    @GetMapping("getGoodsByGoodCategoryId")
    @ApiOperation("根据商户ID显示此商户菜品")
    public Result<List> getGoodsByGoodCategoryId(long martId,long goodCategoryId ) {
        return new Result<List>().ok(apigoodService.getGoodsByGoodCategoryId(martId,goodCategoryId));
    }
    //@Login
    @GetMapping("getoutsideGoodsByGoodCategoryId")
    @ApiOperation("根据商户ID显示此商户外卖菜品")
    public Result<List> getoutsideGoodsByGoodCategoryId(long martId,long goodCategoryId ) {
        return new Result<List>().ok(apigoodService.getoutsideGoodsByGoodCategoryId(martId,goodCategoryId));
    }


    //@Login
    @GetMapping("getShowInHotbyMartId")
    @ApiOperation("根据商户ID查询此商户热销菜品")
    public Result<List<GoodDTO>> getShowInHotbyMartId(long martId ) {
        return new Result<List<GoodDTO>>().ok(apigoodService.getShowInHotbyMartId(martId));
    }

    @GetMapping("goodPageListPC")
    @ApiOperation("菜品列表PC")
    public Result goodPageListPC(@ApiIgnore @RequestParam Map<String, Object> params) {
        return new Result<>().ok(apigoodService.goodPageListPC(params));
    }

    @Login
    @GetMapping("check_token")
    @ApiOperation("令牌检测")
    public int goodPageListPC(HttpServletRequest request) {
        int res_ok = 100;
        int res_invalid=0;

        String token = request.getHeader("token");
        //如果header中不存在token，则从参数中获取token
        if(StringUtils.isBlank(token)){
            token = request.getParameter("token");
        }
        //token为空
        if(StringUtils.isBlank(token)){
            return res_invalid;
        }

        //查询token信息
        TokenEntity tokenEntity = tokenService.getByToken(token);
        if(tokenEntity == null || tokenEntity.getExpireDate().getTime() < System.currentTimeMillis()){
            return res_invalid;
        }
        return res_ok;
    }
}