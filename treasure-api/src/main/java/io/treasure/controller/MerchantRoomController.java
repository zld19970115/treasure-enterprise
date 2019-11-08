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
import io.treasure.dto.*;
import io.treasure.enm.CategoryEnm;
import io.treasure.enm.Common;
import io.treasure.enm.MerchantRoomEnm;
import io.treasure.entity.GoodCategoryEntity;
import io.treasure.entity.MerchantEntity;
import io.treasure.entity.MerchantRoomEntity;
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
    @CrossOrigin
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
    @CrossOrigin
    @Login
    @GetMapping("roomListPage")
    @ApiOperation("包房列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name="name" ,value="名称", paramType="query",dataType = "String"),
            @ApiImplicitParam(name="merchantId",value="当前登陆者id",paramType ="query",required = true,dataType = "String")
    })
    public Result<PageData<MerchantRoomDTO>> roomListPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Common.STATUS_ON.getStatus()+"");
        params.put("type",MerchantRoomEnm.TYPE_ROOM.getType()+"");
        PageData<MerchantRoomDTO> page = merchantRoomService.listPage(params);
        return new Result<PageData<MerchantRoomDTO>>().ok(page);
    }
    @CrossOrigin
    @Login
    @GetMapping("roomDate")
    @ApiOperation("查询日期")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name="merchantId",value="商户编号",paramType ="query",required = true,dataType = "String")
    })
    public Result<PageData<MerchantRoomParamsSetDTO>>  roomDate(@ApiIgnore @RequestParam Map<String,Object> params){
        PageData<MerchantRoomParamsSetDTO> page = merchantRoomService.selectRoomDate(params);
        return new Result<PageData<MerchantRoomParamsSetDTO>>().ok(page);
    }
    @CrossOrigin
    @Login
    @GetMapping("roomAlreadyPage")
    @ApiOperation("包房预约列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name="merchantId",value="商户编号",paramType ="query",required = true,dataType = "String"),
            @ApiImplicitParam(name="date",value="日期",paramType ="query",dataType = "String"),
            @ApiImplicitParam(name="state",value="使用状态1-已使用0-未使用",paramType ="query",dataType = "int")
    })
    public Result<PageData<MerchantRoomParamsSetDTO>> roomAlreadyPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Common.STATUS_ON.getStatus()+"");
        params.put("type",MerchantRoomEnm.TYPE_ROOM.getType()+"");
        PageData<MerchantRoomParamsSetDTO> page = merchantRoomService.selectRoomAlreadyPage(params);
        return new Result<PageData<MerchantRoomParamsSetDTO>>().ok(page);
    }
    @CrossOrigin
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
    @CrossOrigin
    @Login
    @GetMapping("deskListPage")
    @ApiOperation("包房列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name="name" ,value="名称", paramType="query",dataType = "String"),
            @ApiImplicitParam(name="merchantId",value="商户",paramType ="query",required = true,dataType = "String")
    })
    public Result<PageData<MerchantRoomDTO>> deskListPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Common.STATUS_ON.getStatus()+"");
        params.put("type",MerchantRoomEnm.TYPE_DESK.getType()+"");
        PageData<MerchantRoomDTO> page = merchantRoomService.listPage(params);
        return new Result<PageData<MerchantRoomDTO>>().ok(page);
    }
    @CrossOrigin
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
    @CrossOrigin
    @Login
    @PostMapping
    @ApiOperation("保存")
    public Result insert(@RequestBody MerchantRoomDTO dto){
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
    @CrossOrigin
    @Login
    @PostMapping("save")
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
    @CrossOrigin
    @Login
    @PutMapping
    @ApiOperation("修改")
    public Result updateSave(@RequestBody MerchantRoomDTO dto){
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
    @CrossOrigin
    @Login
    @PutMapping("update")
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
    @CrossOrigin
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
    @CrossOrigin
    @PostMapping("exportGoods")
    @ApiOperation("导入")
    public Result exportGoods(@RequestBody ExportMerchantRoomDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class);
        String name=dto.getName();
        //商户名称
        String martId=dto.getMerchantId();
        //创建者
        long creator=dto.getCreator();
        //根据商户名称查询商户编号
        MerchantEntity merchantEntity=merchantService.getByName(martId,Common.STATUS_DELETE.getStatus());
        if(null==merchantEntity){
            return new Result().error("商户不存在，请先注册商户！");
        }
        long merchantId=merchantEntity.getId();
        MerchantRoomEntity roomEntity=new MerchantRoomEntity();
        roomEntity.setMerchantId(merchantId);
        //类型
        String type=dto.getType();
        if("桌".equals(type)){
            roomEntity.setType(MerchantRoomEnm.TYPE_DESK.getType());
        }else{
            roomEntity.setType(MerchantRoomEnm.TYPE_ROOM.getType());
        }
        //根据包房或者桌名称查询
        List goodCategoryList=merchantRoomService.getByNameAndMerchantId(name,merchantId,roomEntity.getType());
        if(null!=goodCategoryList && goodCategoryList.size()>0){
            return new Result().error(name+"该"+type+"已经存在");
        }else{
            roomEntity.setName(name);
            roomEntity.setCreateDate(new Date());
            roomEntity.setCreator(creator);
            roomEntity.setStatus(Common.STATUS_ON.getStatus());
            roomEntity.setBrief(dto.getBrief());
            roomEntity.setDescription(dto.getDescription());
            String numHigh=dto.getNumHigh();
            roomEntity.setNumHigh(Integer.parseInt(numHigh));
            String numLow=dto.getNumLow();
            roomEntity.setNumLow(Integer.parseInt(numLow));
            merchantRoomService.insert(roomEntity);
        }
        return new Result();
    }
}