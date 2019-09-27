package io.treasure.controller;
import io.treasure.annotation.Login;
import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;
import io.treasure.common.utils.Result;
import io.treasure.common.validator.AssertUtils;
import io.treasure.common.validator.ValidatorUtils;
import io.treasure.common.validator.group.AddGroup;
import io.treasure.common.validator.group.DefaultGroup;
import io.treasure.common.validator.group.UpdateGroup;
import io.treasure.dto.GoodDTO;
import io.treasure.dto.MerchantDTO;
import io.treasure.dto.MerchantRoomDTO;
import io.treasure.dto.MerchantRoomParamsSetDTO;
import io.treasure.enm.Common;
import io.treasure.enm.MerchantRoomEnm;
import io.treasure.service.MerchantRoomService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.util.*;


/**
 * 包房或者桌表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-30
 */
@RestController
@RequestMapping("/merchantroom")
@Api(tags="包房或者桌管理")
public class MerchantRoomController {
    @Autowired
    private MerchantRoomService merchantRoomService;
    @Autowired
    private MerchantService merchantService;//商户
    @Login
    @GetMapping("roomPage")
    @ApiOperation("包房列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
        @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name="name" ,value="名称", paramType="query",dataType = "String"),
            @ApiImplicitParam(name="merchantId",value="商户编号",paramType ="query",required = true,dataType = "String")
    })
    public Result<PageData<MerchantRoomDTO>> roomPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Common.STATUS_ON.getStatus()+"");
        params.put("type",MerchantRoomEnm.TYPE_ROOM.getType()+"");
        PageData<MerchantRoomDTO> page = merchantRoomService.page(params);
        return new Result<PageData<MerchantRoomDTO>>().ok(page);
    }
    @Login
    @GetMapping("roomDate")
    @ApiOperation("查询日期")
    public Result roomDate(@RequestParam long merchantId){

        List<String> list = merchantRoomService.selectRoomDate(merchantId);
        LinkedHashSet<String> set = new LinkedHashSet<>(list.size());
        set.addAll(list);
        list.clear();
        list.addAll(set);
//        HashSet h = new HashSet(list);
//        list.clear();
//        list.addAll(h);
        return new Result().ok(list);
    }
    @Login
    @GetMapping("roomAlreadyPage")
    @ApiOperation("包房预约列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name="merchantId",value="商户编号",paramType ="query",required = true,dataType = "String"),
            @ApiImplicitParam(name="date",value="日期",paramType ="query",required = true,dataType = "String"),
            @ApiImplicitParam(name="state",value="使用状态1-已使用0-未使用",paramType ="query",required = true,dataType = "int")
    })
    public Result<PageData<MerchantRoomParamsSetDTO>> roomAlreadyPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Common.STATUS_ON.getStatus()+"");
        params.put("type",MerchantRoomEnm.TYPE_ROOM.getType()+"");
        PageData<MerchantRoomParamsSetDTO> page = merchantRoomService.selectRoomAlreadyPage(params);
        return new Result<PageData<MerchantRoomParamsSetDTO>>().ok(page);
    }

    @Login
    @GetMapping("deskPage")
    @ApiOperation("桌列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name="name" ,value="名称", paramType="query",dataType = "String"),
            @ApiImplicitParam(name="merchantId",value="商户编号",paramType ="query",required = true,dataType = "String")
    })
    public Result<PageData<MerchantRoomDTO>> deskPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status",Common.STATUS_ON.getStatus()+"");
        params.put("type",MerchantRoomEnm.TYPE_DESK.getType()+"");
        PageData<MerchantRoomDTO> page = merchantRoomService.page(params);
        return new Result<PageData<MerchantRoomDTO>>().ok(page);
    }
    @Login
    @GetMapping("getById")
    @ApiOperation("详细信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", paramType = "query", required = true, dataType = "long")
    })
    public Result<MerchantRoomDTO> get(Long id){
        MerchantRoomDTO data = merchantRoomService.get(id);
        return new Result<MerchantRoomDTO>().ok(data);
    }
    @Login
    @PostMapping
    @ApiOperation("保存")
    public Result save(@RequestBody MerchantRoomDTO dto){
        //根据商户和名称判断是否存在
        List roomList=merchantRoomService.getByNameAndMerchantId(dto.getName(),dto.getMerchantId(),dto.getType());
        if(null!=roomList && roomList.size()>0){
            return new Result().error("名称已经存在！");
        }
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class);
        dto.setStatus(Common.STATUS_ON.getStatus());
        dto.setCreateDate(new Date());
        merchantRoomService.save(dto);
        return new Result();
    }
    @Login
    @PutMapping
    @ApiOperation("修改")
    public Result update(@RequestBody MerchantRoomDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class);
        MerchantRoomDTO data=merchantRoomService.get(dto.getId());
        long merchantIdOld=data.getMerchantId();
        long merchantId=dto.getMerchantId();
        if(!data.getName().equals(dto.getName())){
            //根据商户和名称判断是否存在
            List roomList=merchantRoomService.getByNameAndMerchantId(dto.getName(),dto.getMerchantId(),dto.getType());
            if(null!=roomList && roomList.size()>0){
                return new Result().error("名称已经存在！");
            }
        }else if(merchantIdOld!=merchantId){
            //根据商户和名称判断是否存在
            List roomList=merchantRoomService.getByNameAndMerchantId(dto.getName(),dto.getMerchantId(),dto.getType());
            if(null!=roomList && roomList.size()>0){
                return new Result().error("名称已经存在！");
            }
        }
        dto.setUpdateDate(new Date());
        merchantRoomService.update(dto);
        return new Result();
    }
    @Login
    @DeleteMapping("remove")
    @ApiOperation("删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", paramType = "query", required = true, dataType = "long")
    })
    public Result delete(@RequestParam long id){
        //判断商户是否关闭店铺
        MerchantRoomDTO roomDto=merchantRoomService.get(id);
        long merchantId=roomDto.getMerchantId();
        if(merchantId>0){
            MerchantDTO merchantDto= merchantService.get(merchantId);
            if(merchantDto!=null){
                int status=merchantDto.getStatus();//状态
                if(status==Common.STATUS_CLOSE.getStatus()){
                    merchantRoomService.remove(id, Common.STATUS_DELETE.getStatus());
                }else{
                    return new Result().error("请关闭店铺后，在进行删除操作！");
                }
            }
        }else {
            return new Result().error("没有菜品到分类的商户!");
        }
        return new Result();
    }
}