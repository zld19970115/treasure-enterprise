package io.treasure.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.annotation.Login;
import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;
import io.treasure.common.utils.Result;
import io.treasure.common.validator.ValidatorUtils;
import io.treasure.common.validator.group.AddGroup;
import io.treasure.common.validator.group.UpdateGroup;
import io.treasure.dto.BusinessManagerDTO;
import io.treasure.service.BusinessManagerService;
import io.treasure.task.item.OrderForBm;
import io.treasure.vo.BusinessManagerPageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/businessManager")
@Api(tags="业务员表")
public class BusinessManagerController {
    @Autowired
    private OrderForBm orderForBm;
    @Autowired
    BusinessManagerService businessManagerService;
    @CrossOrigin
    @Login
    @GetMapping("getById")
    @ApiOperation("详细信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", paramType = "query", required = true, dataType = "long")
    })
    public Result<BusinessManagerDTO> get(Long id){
        if(id>0){
            BusinessManagerDTO data = businessManagerService.get(id);
            return new Result<BusinessManagerDTO>().ok(data);
        }else{
            return new Result<BusinessManagerDTO>().error(null);
        }
    }
    @CrossOrigin
    @Login
    @PostMapping("save")
    @ApiOperation("保存")
    public Result save(@RequestBody BusinessManagerDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class);
        //根据菜品名称、商户查询该菜品名称是否存在
        List<BusinessManagerDTO> list=businessManagerService.getByNameAndPassWord(dto.getRealName(),dto.getPassword());
        if(null!=list && list.size()>0){
            return new Result().error("业务员已经存在！");
        }
        businessManagerService.save(dto);
        return new Result();
    }
    @CrossOrigin
    @Login
    @PutMapping("update")
    @ApiOperation("修改")
    public Result update(@RequestBody BusinessManagerDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class);
        BusinessManagerDTO data =businessManagerService.get(dto.getId());
        if(!data.getRealName().equals(dto.getRealName())){
            //根据菜品名称、商户查询该菜品名称是否存在
            List<BusinessManagerDTO> list=businessManagerService.getByNameAndPassWord(dto.getRealName(),dto.getPassword());
            if(null!=list && list.size()>0){
                return new Result().error("业务员已经存在！");
            }
        }
        ;
        businessManagerService.update(dto);
        return new Result();
    }

    @CrossOrigin
    @Login
    @DeleteMapping("delete")
    @ApiOperation("删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", paramType = "query", required = true, dataType = "long")
    })
    public Result delete(@RequestParam Long id){
        BusinessManagerDTO dto=businessManagerService.get(id);
        if (dto==null){
            return new Result().error("没有找到该业务员");
        }
        dto.setStatus(3);
        businessManagerService.update(dto);
        return new Result().ok("删除成功");
    }
    @GetMapping("getAll")
    @ApiOperation("查询业务员列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
    })
    public Result<PageData<BusinessManagerDTO>> getAll(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<BusinessManagerDTO> page = businessManagerService.listPage(params);
        return new Result<PageData<BusinessManagerDTO>>().ok(page);
    }
    @GetMapping("binding")
    @ApiOperation("绑定业务员与商家")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "bmId", value = "业务员id", paramType = "query", required = true, dataType = "int"),
            @ApiImplicitParam(name = "mchId", value = "商户id", paramType = "query", required = true, dataType = "long")
    })
    public Result binding(@ApiIgnore @RequestParam Integer bmId , Long mchId){
        businessManagerService.binding(bmId,mchId+"");
        return new Result().ok("绑定成功！");
    }
    @GetMapping("text")
    @ApiOperation("1111")

    public Result text(){
        orderForBm.getOrderByYwy();
        return new Result().ok("绑定成功！");
    }

    @Login
    @GetMapping("pagePC")
    @ApiOperation("分页查询绑定关系")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int")
    })
    public Result<PageData<BusinessManagerPageVo>> pagePC(@ApiIgnore @RequestParam Map<String, Object> params) {
        return new Result<PageData<BusinessManagerPageVo>>().ok(businessManagerService.pagePC(params));
    }

}
