package io.treasure.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.annotation.Login;
import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;
import io.treasure.common.utils.Result;
import io.treasure.dto.ClientUserCollectDTO;
import io.treasure.service.ClientUserCollectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Map;


/**
 * 用户收藏
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-02
 */
@RestController
@RequestMapping("/api/collect")
@Api(tags="用户收藏")
public class ApiClientUserCollectController {
    @Autowired
    private ClientUserCollectService clientUserCollectService;

    @GetMapping("page")
    @ApiOperation("分页")
    @ApiImplicitParams({
        @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
        @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String")
    })
    public Result<PageData<ClientUserCollectDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<ClientUserCollectDTO> page = clientUserCollectService.page(params);

        return new Result<PageData<ClientUserCollectDTO>>().ok(page);
    }

    @GetMapping("{id}")
    @ApiOperation("信息")
    public Result<ClientUserCollectDTO> get(@PathVariable("id") Long id){
        ClientUserCollectDTO data = clientUserCollectService.get(id);

        return new Result<ClientUserCollectDTO>().ok(data);
    }
    @Login
    @PostMapping
    @ApiOperation("收藏")
    public Result save(@RequestBody ClientUserCollectDTO dto){
        //效验数据
       // ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);

        clientUserCollectService.save(dto);


        return new Result();
    }
    @Login
    @GetMapping("/delete")
    @ApiOperation("取消收藏")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "会员id", paramType = "query", required = true, dataType = "long"),
            @ApiImplicitParam(name = "martId", value = "商家id", paramType = "query", required = true, dataType = "long"),

    })
    public Result delete(long userId,long martId){
        //效验数据
       // AssertUtils.isNull(id,"id");
        ClientUserCollectDTO clientUserCollectDTO = clientUserCollectService.selectByUidAndMid(userId, martId);
        if (clientUserCollectDTO!=null){
        clientUserCollectService.changeStatus(userId, martId);
        return new Result().ok("已取消");
    }
        return new Result().ok("未收藏");

    }
//    @Login
    @GetMapping("/yesOrNo")
    @ApiOperation("是否收藏")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "会员id", paramType = "query", required = false, dataType = "long"),
            @ApiImplicitParam(name = "martId", value = "商家id", paramType = "query", required = true, dataType = "long"),
    })
    public Result yesOrNo(Long userId,Long martId){
        if(userId==null){
            return new Result().ok("false");
        }
        ClientUserCollectDTO clientUserCollectDTO = clientUserCollectService.selectByUidAndMid(userId, martId);
        if (clientUserCollectDTO!=null){
            Integer status = clientUserCollectDTO.getStatus();
            Integer a = 1;
            if (a.equals(status)){
                return new Result().ok("true");
            }else {
                return new Result().ok("false");
            }

        }
        return new Result().ok("false");
        /*
=======
        if(userId==null){
            return new Result().ok("false");
        }
>>>>>>> origin/master
        ClientUserCollectDTO clientUserCollectDTO = clientUserCollectService.selectByUidAndMid(userId, martId);
        if (clientUserCollectDTO!=null){
        Integer status = clientUserCollectDTO.getStatus();
        Integer a = 1;
        if (a.equals(status)){
            return new Result().ok("true");
        }else {
            return new Result().ok("false");
        }

        }
        return new Result().ok("false");
        */

    }
    @Login
    @GetMapping("getCollectMerchantByUserId")
    @ApiOperation("客户端-用户收藏列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "clientUserId", value = "用户ID", paramType = "query",required=true, dataType="Long")
    })
    public Result<PageData<ClientUserCollectDTO>> getCollectMerchantByUserId(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<ClientUserCollectDTO> page = clientUserCollectService.getCollectMerchantByUserId(params);
        return new Result<PageData<ClientUserCollectDTO>>().ok(page);
    }

}