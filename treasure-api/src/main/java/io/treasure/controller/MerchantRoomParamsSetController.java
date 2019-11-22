package io.treasure.controller;

import io.treasure.annotation.Login;
import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;
import io.treasure.common.utils.DateUtils;
import io.treasure.common.utils.Result;
import io.treasure.common.validator.AssertUtils;
import io.treasure.dto.MerchantDTO;
import io.treasure.dto.MerchantRoomParamsDTO;
import io.treasure.dto.MerchantRoomParamsSetDTO;
import io.treasure.enm.Common;
import io.treasure.enm.MerchantRoomEnm;
import io.treasure.entity.MerchantRoomParamsEntity;
import io.treasure.service.MerchantRoomParamsService;
import io.treasure.service.MerchantRoomParamsSetService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.service.MerchantRoomService;
import io.treasure.service.MerchantService;
import net.sf.jsqlparser.expression.DoubleValue;
import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.xml.crypto.Data;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 商户端包房设置管理
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-14
 */
@RestController
@RequestMapping("/merchantroomparamsset")
@Api(tags="商户端包房设置管理")
public class MerchantRoomParamsSetController {
    @Autowired
    private MerchantRoomParamsSetService merchantRoomParamsSetService;
    //预约参数
    @Autowired
    private MerchantRoomParamsService merchantRoomParamsService;
    //包房
    @Autowired
    private MerchantRoomService merchantRoomService;
    //商户
    @Autowired
    private MerchantService merchantService;
    @Login
    @GetMapping("page")
    @ApiOperation("分页")
    @ApiImplicitParams({
        @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
        @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "merchantId", value = "商户编号", paramType = "query", required = true, dataType="long")
    })
    public Result<PageData<MerchantRoomParamsSetDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status",Common.STATUS_ON.getStatus()+"");
        PageData<MerchantRoomParamsSetDTO> page = merchantRoomParamsSetService.page(params);
        return new Result<PageData<MerchantRoomParamsSetDTO>>().ok(page);
    }

//    @GetMapping("{id}")
//    @ApiOperation("信息")
//    public Result<MerchantRoomParamsSetDTO> get(@PathVariable("id") Long id){
//        MerchantRoomParamsSetDTO data = merchantRoomParamsSetService.get(id);
//
//        return new Result<MerchantRoomParamsSetDTO>().ok(data);
//    }
    @Login
    @PostMapping("save")
    @ApiOperation("预约包房设置")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "merchantId", value = "商户编号", paramType = "query", required = true, dataType="long") ,
            @ApiImplicitParam(name="creator",value="创建者",paramType = "query",required = true,dataType = "long")
    })
    public Result save(@RequestParam long merchantId,@RequestParam long creator){
       return merchantRoomParamsSetService.setRoom(merchantId,creator);
    }

//    @PutMapping
//    @ApiOperation("修改")
//    public Result update(@RequestBody MerchantRoomParamsSetDTO dto){
//        //效验数据
//        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);
//
//        merchantRoomParamsSetService.update(dto);
//
//        return new Result();
//    }
    @Login
    @DeleteMapping("remove")
    @ApiOperation("删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", paramType = "query", required = true, dataType="long")
    })
    public Result delete(@RequestParam long id){
        merchantRoomParamsSetService.remove(id,Common.STATUS_DELETE.getStatus());
        return new Result();
    }

    @Login
    @GetMapping("getAvailableRoomsByData")
    @ApiOperation("查询指定日期、时间段内可用包房")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "useDate", value = "年月日", paramType = "query", required = true, dataType="Date"),
            @ApiImplicitParam(name = "roomParamsId", value = "时间段", paramType = "query", required = true, dataType="long")
    })
    public Result<List<MerchantRoomParamsSetDTO>> getAvailableRoomsByData(Date useDate, long roomParamsId,long merchantId) throws ParseException {
        return new Result<List<MerchantRoomParamsSetDTO>>().ok(merchantRoomParamsSetService.getAvailableRoomsByData(useDate, roomParamsId,merchantId));
    }

    @Login
    @GetMapping("getAvailableRooms")
    @ApiOperation("查询当日可用包房桌数量")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "merchantId", value = "商户ID", paramType = "query", required = true, dataType="long")
    })
    public Map getAvailableRooms(long merchantId) throws ParseException {
        String format = DateFormatUtils.format(new Date(), "yyyy-MM-dd 23:59:59");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = simpleDateFormat.parse(format);
        long ts = date.getTime();
        long c=1000;
        double progress = ts/(double)c;
        long bigTime = new Double(progress).longValue();
        int availableRooms = merchantRoomParamsSetService.getAvailableRooms(bigTime, merchantId);
        int getAvailableRoomsDesk = merchantRoomParamsSetService.getAvailableRoomsDesk(bigTime, merchantId);
        Map<String,Integer> map=new HashMap<String,Integer>();
        map.put("roomNum",availableRooms);
        map.put("DeskNum",getAvailableRoomsDesk);
        return map;
    }
}