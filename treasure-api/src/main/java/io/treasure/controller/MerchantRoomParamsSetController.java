package io.treasure.controller;

import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;
import io.treasure.common.utils.DateUtils;
import io.treasure.common.utils.Result;
import io.treasure.common.validator.AssertUtils;
import io.treasure.dto.MerchantDTO;
import io.treasure.dto.MerchantRoomDTO;
import io.treasure.dto.MerchantRoomParamsSetDTO;
import io.treasure.enm.Common;
import io.treasure.enm.MerchantRoomEnm;
import io.treasure.entity.MerchantRoomEntity;
import io.treasure.service.MerchantRoomParamsSetService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.service.MerchantRoomService;
import io.treasure.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Date;
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
    //包房
    @Autowired
    private MerchantRoomService merchantRoomService;
    //商户
    @Autowired
    private MerchantService merchantService;
    @GetMapping("page")
    @ApiOperation("分页")
    @ApiImplicitParams({
        @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
        @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String")
    })
    public Result<PageData<MerchantRoomParamsSetDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params){
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

    @PostMapping
    @ApiOperation("预约包房设置")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "merchantId", value = "商户编号", paramType = "query", required = true, dataType="long") ,
            @ApiImplicitParam(name = "days", value = "预约天数", paramType = "query",required = true, dataType="int"),
            @ApiImplicitParam(name="creator",value="创建者",paramType = "query",required = true,dataType = "long")
    })
    public Result save(long merchantId,int days,long creator){

        if(days<=0){
            return new Result().error("预约天数必须大于0!");
        }
        if(merchantId<=0){
            return new Result().error("商户编号必须大于0！");
        }
        //获取商户得信息
        MerchantDTO merchantDTO = merchantService.get(merchantId);
        //开店时间
        String businessShours=merchantDTO.getBusinesshours();
        Date openHourse=DateUtils.stringToDate(businessShours,"HH:mm");
        //闭店时间
        String colseShopHourses=merchantDTO.getCloseshophours();
        Date closeHorse=DateUtils.stringToDate(colseShopHourses,"HH:mm");
        //根据编号查询该商户对应的包房信息
        List list=merchantRoomService.getByMerchantId(merchantId, Common.STATUS_ON.getStatus());
        if(null!=list && list.size()>0){
            for(int i=1;i<=days;i++){
                //设置时间
               Date date= DateUtils.addDateDays(new Date(),i);
               String setdate=DateUtils.format(date,"yyyy-MM-dd");
                for(int h=1;h<24;h++){
                    //预约时间段
                    Date setHourse= DateUtils.addDateHours(openHourse,h);
                    if(setHourse.after(openHourse) && setHourse.before(closeHorse)){
                        String sysTime=setdate+" "+DateUtils.format(setHourse,"HH:mm");
                        for(int room=0;room<list.size();room++){
                            Map map= (Map) list.get(room);
                            String id=String.valueOf(map.get("id"));
                            MerchantRoomParamsSetDTO dto=new MerchantRoomParamsSetDTO();
                            dto.setCreateDate(new Date());
                            dto.setCreator(creator);
                            dto.setMerchantId(merchantId);
                            dto.setRoomId(Long.parseLong(id));
                            dto.setState(MerchantRoomEnm.STATE_USE_NO.getType());
                            dto.setUseDate(DateUtils.stringToDate(sysTime,"yyyy-MM-dd HH:mm"));
                            dto.setStatus(Common.STATUS_ON.getStatus());
                            merchantRoomParamsSetService.save(dto);
                        }
                    }
                }
            }
        }else{
            return new Result().error("该商户没有包房信息！");
        }
        //merchantRoomParamsSetService.save(dto);
        return new Result();
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

    @DeleteMapping
    @ApiOperation("删除")
    public Result delete(@RequestBody Long[] ids){
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");

        merchantRoomParamsSetService.delete(ids);

        return new Result();
    }
}