package io.treasure.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.annotation.Login;
import io.treasure.annotation.LoginUser;
import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;
import io.treasure.common.utils.Result;
import io.treasure.common.validator.AssertUtils;
import io.treasure.common.validator.ValidatorUtils;
import io.treasure.common.validator.group.AddGroup;
import io.treasure.common.validator.group.DefaultGroup;
import io.treasure.common.validator.group.UpdateGroup;
import io.treasure.dao.MasterOrderDao;
import io.treasure.dto.*;
import io.treasure.enm.Constants;
import io.treasure.entity.ClientUserEntity;
import io.treasure.entity.MasterOrderEntity;
import io.treasure.entity.OrderSimpleEntity;
import io.treasure.entity.SlaveOrderEntity;
import io.treasure.service.ClientUserService;
import io.treasure.service.MasterOrderService;
import io.treasure.service.MasterOrderSimpleService;
import io.treasure.service.MerchantRoomParamsSetService;
import io.treasure.utils.TimeUtil;
import io.treasure.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.beans.Transient;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 订单表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-23
 */
@RestController
@RequestMapping("/api/masterOrder")
@Api(tags="订单表")
public class ApiMasterOrderController {
    @Autowired
    private MasterOrderService masterOrderService;
    @Autowired
    private MerchantRoomParamsSetService merchantRoomParamsSetService;
    //会员
    @Autowired
    private ClientUserService clientUserService;

    @Autowired
    private MasterOrderSimpleService masterOrderSimpleService;

    @Autowired(required = false)
    private MasterOrderDao masterOrderDao;

    @Login
    @GetMapping("page")
    @ApiOperation("分页")
    @ApiImplicitParams({
        @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
        @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
        @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String")
    })
    public Result<PageData<MasterOrderDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<MasterOrderDTO> page = masterOrderService.page(params);

        return new Result<PageData<MasterOrderDTO>>().ok(page);
    }
    @CrossOrigin
    @Login
    @GetMapping("appointmentPage")
    @ApiOperation("商户端-预约列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "merchantId", value = "商户编号", paramType = "query",required=true, dataType="String"),
            @ApiImplicitParam(name = "orderId", value = "订单编号", paramType = "query", dataType="String")
    })
    public Result<PageData<MerchantOrderDTO>> appointmentPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Constants.OrderStatus.PAYORDER.getValue()+"");
        PageData<MerchantOrderDTO> page = masterOrderService.listMerchantPage2(params);
        return new Result<PageData<MerchantOrderDTO>>().ok(page);
    }
    @CrossOrigin
    @Login
    @GetMapping("chargePage")
    @ApiOperation("商户端-已退单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "merchantId", value = "商户编号", paramType = "query",required=true, dataType="String"),
            @ApiImplicitParam(name = "orderId", value = "订单编号", paramType = "query", dataType="String")
    })
    public Result<PageData<MerchantOrderDTO>> chargePage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Constants.OrderStatus.MERCHANTAGREEREFUNDORDER.getValue()+"");
        PageData<MerchantOrderDTO> page = masterOrderService.listMerchantPage(params);
        return new Result<PageData<MerchantOrderDTO>>().ok(page);
    }

    @CrossOrigin
    @Login
    @GetMapping("ongPageCopy")
    @ApiOperation("商户端-进行中列表(已接受订单)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "merchantId", value = "商户编号", paramType = "query",required=true, dataType="String"),
            @ApiImplicitParam(name = "orderId", value = "订单编号", paramType = "query", dataType="String")
    })
    public Result<PageData<MerchantOrderDTO>> ongPageCopy(@ApiIgnore @RequestParam Map<String, Object> params){

        Long merchantId = Long.parseLong(params.get("merchantId")+"");
        String tmp = params.get("page")+"";
        if(tmp == null)
            tmp ="0";
        Integer page = Integer.parseInt(tmp);
        if(page == null){
            page = 0;
        }else{
            if(page>0)
                page--;
        }
        String tmp1 = params.get(Constant.LIMIT)+"";
        if(tmp1 == null)
            tmp1 = "10";
        Integer limit = Integer.parseInt(tmp1);
        if(limit == null){
            limit = 10;
        }
        String orderId = params.get("orderId")+"";
        String orderField = params.get(Constant.ORDER_FIELD)+"";
        String sortMethod = params.get(Constant.ORDER)+"";

        PageData<MerchantOrderDTO> merchantOrderDTOPageData = masterOrderService.selectInProcessListByMerchantId(merchantId, page, limit, orderId, orderField, sortMethod);
        //params.put("status", Constants.OrderStatus.MERCHANTRECEIPTORDER.getValue()+","+Constants.OrderStatus.MERCHANTAGREEREFUNDORDER.getValue()+","+Constants.OrderStatus.MERCHANTREFUSESREFUNDORDER.getValue());
        return new Result<PageData<MerchantOrderDTO>>().ok(merchantOrderDTOPageData);
    }

    @CrossOrigin
    @Login
    @GetMapping("ongPage")
    @ApiOperation("商户端-进行中列表(已接受订单)备份")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "merchantId", value = "商户编号", paramType = "query",required=true, dataType="String"),
            @ApiImplicitParam(name = "orderId", value = "订单编号", paramType = "query", dataType="String")
    })
    public Result<PageData<MerchantOrderDTO>> ongPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Constants.OrderStatus.MERCHANTRECEIPTORDER.getValue()+","+Constants.OrderStatus.MERCHANTAGREEREFUNDORDER.getValue()+","+Constants.OrderStatus.MERCHANTREFUSESREFUNDORDER.getValue());
        PageData<MerchantOrderDTO> page = masterOrderService.listMerchantPages(params);
        return new Result<PageData<MerchantOrderDTO>>().ok(page);
    }

    @CrossOrigin
    @Login
    @GetMapping("ongPagePc")
    @ApiOperation("商户端-进行中列表(已接受订单)PC")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "merchantId", value = "商户编号", paramType = "query",required=true, dataType="String"),
            @ApiImplicitParam(name = "orderId", value = "订单编号", paramType = "query", dataType="String")
    })
    public Result<PageData<MerchantOrderDTO>> ongPagePc(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", "2");
        params.put("ispOrderId", "1");
        PageData<MerchantOrderDTO> page = masterOrderService.listMerchantPagesPC(params);
        return new Result<PageData<MerchantOrderDTO>>().ok(page);
    }

    @CrossOrigin
    @Login
    @GetMapping("allListPC")
    @ApiOperation("商户端-全部订单PC")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "merchantId", value = "商户编号", paramType = "query",required=true, dataType="String"),
            @ApiImplicitParam(name = "orderId", value = "订单编号", paramType = "query", dataType="String")
    })
    public Result<PageData<MerchantOrderDTO>> allListPC(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status",Constants.OrderStatus.MERCHANTRECEIPTORDER.getValue()+","+Constants.OrderStatus.MERCHANTREFUSALORDER.getValue()+","+
                Constants.OrderStatus.PAYORDER.getValue()+","+Constants.OrderStatus.USERAPPLYREFUNDORDER.getValue()+","+Constants.OrderStatus.MERCHANTREFUSESREFUNDORDER.getValue()+","
                +Constants.OrderStatus.MERCHANTAGREEREFUNDORDER.getValue()+","+Constants.OrderStatus.MERCHANTAGFINISHORDER.getValue());
        params.put("ispOrderId", "1");
        PageData<MerchantOrderDTO> page = masterOrderService.allListPC(params);
        return new Result<PageData<MerchantOrderDTO>>().ok(page);
    }

    @CrossOrigin
    @Login
    @GetMapping("finishPage")
    @ApiOperation("商户端-已完成列表(翻台)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "merchantId", value = "商户编号", paramType = "query",required=true, dataType="String"),
            @ApiImplicitParam(name = "orderId", value = "订单编号", paramType = "query", dataType="String")
    })
    public Result<PageData<MerchantOrderDTO>> finishPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Constants.OrderStatus.MERCHANTAGFINISHORDER.getValue()+"");
        PageData<MerchantOrderDTO> page = masterOrderService.listMerchantPage(params);
        return new Result<PageData<MerchantOrderDTO>>().ok(page);
    }

    @CrossOrigin
    @Login
    @GetMapping("finishPagePC")
    @ApiOperation("商户端-已完成列表(翻台)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "merchantId", value = "商户编号", paramType = "query",required=true, dataType="String"),
            @ApiImplicitParam(name = "orderId", value = "订单编号", paramType = "query", dataType="String")
    })
    public Result<PageData<MerchantOrderDTO>> finishPagePC(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Constants.OrderStatus.MERCHANTAGFINISHORDER.getValue()+"");
        params.put("ispOrderId", "1");
        PageData<MerchantOrderDTO> page = masterOrderService.finishPagePC(params);
        return new Result<PageData<MerchantOrderDTO>>().ok(page);
    }

    @CrossOrigin
    @Login
    @GetMapping("calcelPagePC")
    @ApiOperation("商户端-拒绝订单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "merchantId", value = "商户编号", paramType = "query",required=true, dataType="String"),
            @ApiImplicitParam(name = "orderId", value = "订单编号", paramType = "query", dataType="String")
    })
    public Result<PageData<MerchantOrderDTO>> calcelPagePC(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Constants.OrderStatus.MERCHANTREFUSALORDER.getValue()+"");
        params.put("ispOrderId", "1");
        PageData<MerchantOrderDTO> page = masterOrderService.calcelPagePC(params);
        return new Result<PageData<MerchantOrderDTO>>().ok(page);
    }

    @CrossOrigin
    @Login
    @GetMapping("income_list")
    @ApiOperation("商户端-订单营收明细")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "merchantId", value = "商户编号", paramType = "query",required=true, dataType="Long"),
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = false, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = false, dataType="int") ,
            @ApiImplicitParam(name = "creator",value="client_user_id",paramType = "query",required = false,dataType = "Long"),
            @ApiImplicitParam(name = "startTime",value="就餐开始时间",paramType = "query",required = false,dataType = "String"),
            @ApiImplicitParam(name = "stopTime",value="就餐结束时间",paramType = "query",required = false,dataType = "String")
    })
    public Result finishPageCopy(Long merchantId,Integer page,Integer limit,Long creator,String startTime,String stopTime) throws ParseException {

        int pageTmp = page==null?0:page;
        if(pageTmp >0)
            pageTmp --;
        int limitTmp = limit==null?10:limit;
        QueryWrapper<MasterOrderEntity> queryWrapper = new QueryWrapper();
        queryWrapper.eq("check_status",1);
        queryWrapper.in("status",10);

        if(merchantId != null)
            queryWrapper.eq("merchant_id",merchantId);
        if(creator != null)
            queryWrapper.eq("creator",creator);

        startTime = startTime+" 00:00:00";
        stopTime = stopTime+" 23:59:59";
        Long start = TimeUtil.simpleDateFormat.parse(startTime).getTime()-1;
        Long stop = TimeUtil.simpleDateFormat.parse(stopTime).getTime()+1;
        Date startTimeDate = new Date(start);
        Date stopTimeDate = new Date(stop);

        //时间处理
        queryWrapper.gt("eat_time",startTimeDate);
        queryWrapper.lt("eat_time",stopTimeDate);

        Page<MasterOrderEntity> record = new Page<MasterOrderEntity>(pageTmp,limitTmp);
        IPage<MasterOrderEntity> orders = masterOrderDao.selectPage(record, queryWrapper);

        if(orders == null)
            return new Result().error("nothing");

       // Double finishedTotal = masterOrderDao.getFinishedTotal(merchantId,creator,startTimeTmp,stopTime);
       // System.out.println("finishedTotal:"+finishedTotal);

        return new Result().ok(orders);
    }


    @CrossOrigin
    @Login
    @GetMapping("calcelPage")
    @ApiOperation("商户端-拒绝订单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "merchantId", value = "商户编号", paramType = "query",required=true, dataType="String"),
            @ApiImplicitParam(name = "orderId", value = "订单编号", paramType = "query", dataType="String")
    })
    public Result<PageData<MerchantOrderDTO>> calcelPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Constants.OrderStatus.MERCHANTREFUSALORDER.getValue()+"");
        PageData<MerchantOrderDTO> page = masterOrderService.listMerchantPage(params);
        return new Result<PageData<MerchantOrderDTO>>().ok(page);
    }
    @CrossOrigin
    @Login
    @GetMapping("applRefundPage")
    @ApiOperation("商户端-申请退款列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "merchantId", value = "商户编号", paramType = "query",required=true, dataType="String"),
            @ApiImplicitParam(name = "orderId", value = "订单编号", paramType = "query", dataType="String")
    })
    public Result<PageData<MerchantOrderDTO>> applRefundPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Constants.OrderStatus.USERAPPLYREFUNDORDER.getValue()+"");
        PageData<MerchantOrderDTO> page = masterOrderService.listMerchantPage(params);
        return new Result<PageData<MerchantOrderDTO>>().ok(page);
    }

    @CrossOrigin
    @Login
    @GetMapping("applRefundPagePC")
    @ApiOperation("商户端-申请退款列表PC")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "merchantId", value = "商户编号", paramType = "query",required=true, dataType="String"),
            @ApiImplicitParam(name = "orderId", value = "订单编号", paramType = "query", dataType="String")
    })
    public Result<PageData<MerchantOrderDTO>> applRefundPagePC(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Constants.OrderStatus.USERAPPLYREFUNDORDER.getValue()+"");
        params.put("ispOrderId",null);
        PageData<MerchantOrderDTO> page = masterOrderService.listMerchantPage(params);
        return new Result<PageData<MerchantOrderDTO>>().ok(page);
    }

    @CrossOrigin
    @Login
    @GetMapping("allPage")
    @ApiOperation("商户端-全部列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "merchantId", value = "商户编号", paramType = "query",required=true, dataType="String"),
            @ApiImplicitParam(name = "orderId", value = "订单编号", paramType = "query", dataType="String")
    })
    public Result<PageData<MerchantOrderDTO>> allPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status",Constants.OrderStatus.MERCHANTRECEIPTORDER.getValue()+","+Constants.OrderStatus.MERCHANTREFUSALORDER.getValue()+","+
                Constants.OrderStatus.PAYORDER.getValue()+","+Constants.OrderStatus.USERAPPLYREFUNDORDER.getValue()+","+Constants.OrderStatus.MERCHANTREFUSESREFUNDORDER.getValue()+","
                +Constants.OrderStatus.MERCHANTAGREEREFUNDORDER.getValue()+","+Constants.OrderStatus.MERCHANTAGFINISHORDER.getValue());
        PageData page = masterOrderService.listMerchantPage(params);
        return new Result<PageData<MerchantOrderDTO>>().ok(page);
    }

    @Login
    @GetMapping("order/{orderId}")
    @ApiOperation("用户端订单详情")
    public Result<OrderDTO> getOrderInfo(@PathVariable("orderId") String orderId){

        OrderDTO data = masterOrderService.orderParticulars(orderId);
            return new Result<OrderDTO>().ok(data);
    }

    @Login
    @GetMapping("order1/{orderId}")
    @ApiOperation("商户端订单详情")
    public Result<List<OrderDTO>> getOrderInfo1(@PathVariable("orderId") String orderId){

      List<OrderDTO> data = masterOrderService.orderParticulars1(orderId);
        return new Result<List<OrderDTO>>().ok(data);
    }
    @Login
    @GetMapping("MartOrder/{orderId}")
    @ApiOperation("商户端预约列表订单详情")
    public Result<List<OrderDTO>> getMartOrderInfo(@PathVariable("orderId") String orderId){
        List<OrderDTO> data = masterOrderService.getMartOrderInfo(orderId);
        return new Result<List<OrderDTO>>().ok(data);
    }
    @Login
    @PostMapping("generateOrder")
    @ApiOperation("生成订单")
    public Result generateOrder(@RequestBody OrderDTO dto, @LoginUser ClientUserEntity user) throws ParseException {
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);
        List<SlaveOrderEntity> dtoList=dto.getSlaveOrder();
        return  masterOrderService.orderSave(dto,dtoList,user);
    }
    @Login
    @PostMapping("generatePorder")
    @ApiOperation("加菜")
    public Result generatePorder(@RequestBody OrderDTO dto, @LoginUser ClientUserEntity user){
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);
        List<SlaveOrderEntity> dtoList=dto.getSlaveOrder();
         return  masterOrderService.saveOrder(dto,dtoList,user);
    }
    @Login
    @GetMapping("allOrderPage")
    @ApiOperation("全部订单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "userId", value = "用户编码", paramType = "query",required=true, dataType="Long")
    })
    public Result<PageData<OrderDTO>> allOrderPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("pOrderId","0");
        PageData<OrderDTO> page = masterOrderService.getAllMainOrder(params);
        return new Result<PageData<OrderDTO>>().ok(page);
    }

    //@Login
    @GetMapping("can_use_coins")
    @ApiOperation("可用宝币数量")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderId", value = "订单号", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = "userId", value = "用户编码", paramType = "query",required=true, dataType="Long")
    })
    public Result canUseCoins(@ApiIgnore @RequestParam Map<String, Object> params){
        String orderId = params.get("orderId")+"";
        Long userId = Long.parseLong(params.get("userId")+"");

        BigDecimal canUseCoins = new BigDecimal("50");
        MasterOrderEntity masterOrderEntity = masterOrderDao.payCoinsSumByOrderId(orderId);
        if(masterOrderEntity == null)
            return new Result().ok("0");
        BigDecimal paidCoins = masterOrderEntity.getPayCoins();
        if(paidCoins.compareTo(canUseCoins)>=0)
            return new Result().ok("0");
        BigDecimal subtract = canUseCoins.subtract(paidCoins);

        ClientUserEntity clientUserEntity = clientUserService.selectById(userId);
        BigDecimal balance = clientUserEntity.getBalance();
        if(balance.compareTo(subtract)>=0)
            return new Result().ok(subtract);
        return new Result().ok(balance);
    }

    @Login
    @GetMapping("noPayOrderPage")
    @ApiOperation("未支付订单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "userId", value = "用户编码", paramType = "query",required=true, dataType="Long")
    })
    public Result<PageData<OrderDTO>> noPayOrderPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Constants.OrderStatus.NOPAYORDER.getValue()+"");
        params.put("pOrderId","0");
        PageData<OrderDTO> page = masterOrderService.listPage(params);
        return new Result<PageData<OrderDTO>>().ok(page);
    }

    @Login
    @GetMapping("receiptOrderPage")
    @ApiOperation("商家已接单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "userId", value = "用户编码", paramType = "query",required=true, dataType="Long")
    })
    public Result<PageData<OrderDTO>> receiptOrderPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Constants.OrderStatus.MERCHANTRECEIPTORDER.getValue()+"");
        params.put("pOrderId","0");
        PageData<OrderDTO> page = masterOrderService.listPage(params);
        return new Result<PageData<OrderDTO>>().ok(page);
    }

    @Login
    @GetMapping("refusalOrderPage")
    @ApiOperation("商户拒接单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "userId", value = "用户编码", paramType = "query",required=true, dataType="Long")
    })
    public Result<PageData<OrderDTO>> refusalOrderPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Constants.OrderStatus.MERCHANTREFUSALORDER.getValue()+"");
        params.put("pOrderId","0");
        PageData<OrderDTO> page = masterOrderService.listPage(params);
        return new Result<PageData<OrderDTO>>().ok(page);
    }

    @Login
    @GetMapping("payFinishOrderPage")
    @ApiOperation("支付完成订单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "userId", value = "用户编码", paramType = "query",required=true, dataType="Long")
    })
    public Result<PageData<OrderDTO>> payFinishOrderPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Constants.OrderStatus.PAYORDER.getValue()+"");
        params.put("pOrderId","0");
        PageData<OrderDTO> page = masterOrderService.selectPOrderIdHavePaids(params);
        return new Result<PageData<OrderDTO>>().ok(page);
    }

    @Login
    @GetMapping("payFinishOrderPageCopy")
    @ApiOperation("支付完成订单列表新")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "userId", value = "用户编码", paramType = "query",required=true, dataType="Long")
    })
    public Result<PageData<OrderDTO>> payFinishOrderPageCopy(@ApiIgnore @RequestParam Map<String, Object> params){

        String sPage = params.get("page")+"";
        if(sPage == null)
            sPage="0";
        int page = Integer.parseInt(sPage);
        if(page > 0){
            page --;
        }
        String sLimit = params.get("limit")+"";
        if(sLimit == null)
            sLimit = "0";
        Integer limit = Integer.parseInt(sLimit);
        String orderField = params.get(Constant.ORDER_FIELD)+"";
        String sortMethod =params.get(Constant.ORDER)+"";
        Long userId = Long.parseLong(params.get("userId")+"");
        System.out.println("userId:"+userId);
        PageData<OrderDTO> pages = masterOrderService.selectPOrderIdHavePaidsCopy(page,limit,orderField,sortMethod,userId);
        return new Result<PageData<OrderDTO>>().ok(pages);
    }
////////////////////////////////////////////////////////////////////////////////////////////////

    @Login
    @GetMapping("cancelNopayOrderPage")
    @ApiOperation("取消未支付订单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "userId", value = "用户编码", paramType = "query",required=true, dataType="Long")
    })
    public Result<PageData<OrderDTO>> cancelNopayOrderPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Constants.OrderStatus.CANCELNOPAYORDER.getValue()+"");
        params.put("pOrderId","0");
        PageData<OrderDTO> page = masterOrderService.listPage(params);
        return new Result<PageData<OrderDTO>>().ok(page);
    }

    @Login
    @GetMapping("userApplyRefundOrderPage")
    @ApiOperation("消费者申请退款订单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "userId", value = "用户编码", paramType = "query",required=true, dataType="Long")
    })
    public Result<PageData<OrderDTO>> userApplyRefundOrderPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Constants.OrderStatus.USERAPPLYREFUNDORDER.getValue()+"");
        params.put("pOrderId","0");
        PageData<OrderDTO> page = masterOrderService.listPage(params);

        return new Result<PageData<OrderDTO>>().ok(page);
    }

    @Login
    @GetMapping("refusesRefundOrderPage")
    @ApiOperation("商户拒绝退款订单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "userId", value = "用户编码", paramType = "query",required=true, dataType="Long")
    })
    public Result<PageData<OrderDTO>> refusesRefundOrderPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Constants.OrderStatus.MERCHANTREFUSESREFUNDORDER.getValue()+"");
        params.put("pOrderId","0");
        PageData<OrderDTO> page = masterOrderService.listPage(params);
        return new Result<PageData<OrderDTO>>().ok(page);
    }

    @Login
    @GetMapping("agreeRefundOrderPage")
    @ApiOperation("商家同意退款订单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "userId", value = "用户编码", paramType = "query",required=true, dataType="Long")
    })
    public Result<PageData<OrderDTO>> agreeRefundOrderPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Constants.OrderStatus.MERCHANTAGREEREFUNDORDER.getValue()+"");
        params.put("pOrderId","0");
        PageData<OrderDTO> page = masterOrderService.selectAgreeRefundOrders(params);
        return new Result<PageData<OrderDTO>>().ok(page);
    }

    @Login
    @GetMapping("deleteOrderPage")
    @ApiOperation("删除订单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "userId", value = "用户编码", paramType = "query",required=true, dataType="Long")
    })
    public Result<PageData<OrderDTO>> deleteOrderPage(@ApiIgnore @RequestParam Map<String, Object> params){
        params.put("status", Constants.OrderStatus.DELETEORDER.getValue()+"");
        params.put("pOrderId","0");
        PageData<OrderDTO> page = masterOrderService.listPage(params);
        return new Result<PageData<OrderDTO>>().ok(page);
    }

    @Login
    @PutMapping("orderCancel")
    @ApiOperation("未支付订单取消")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "refundReason", value = "取消订单原因", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "id", value = "主订单ID", paramType = "query",required=true, dataType="Long")
    })
    public Result orderCancel(@ApiIgnore @RequestParam Map<String, Object> params) throws Exception {
        Long id = Long.valueOf(params.get("id").toString());
        return masterOrderService.cancelOrder(id);
    }

    @Login
    @PutMapping("userCheck")
    @ApiOperation("用户结账")
    public Result userCheck(Long id){
        return  masterOrderService.updateByCheck(id);
    }

    @Login
    @PutMapping("userApplyRefund")
    @ApiOperation("消费者申请退款")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "refundReason", value = "取消订单原因", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "id", value = "主订单ID", paramType = "query",required=true, dataType="Long")
    })
    public Result userApplyRefund(@ApiIgnore @RequestParam Map<String, Object> params){
        return  masterOrderService.updateByApplyRefund(params);
    }

    @Login
    @GetMapping("{id}")
    @ApiOperation("信息")
    public Result<MasterOrderDTO> get(@PathVariable("id") Long id){
        MasterOrderDTO data = masterOrderService.get(id);

        return new Result<MasterOrderDTO>().ok(data);
    }

    @Login
    @PostMapping
    @ApiOperation("保存")
    public Result save(@RequestBody MasterOrderDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);
        masterOrderService.save(dto);
        return new Result();
    }
    @Login
    @PutMapping
    @ApiOperation("修改")
    public Result update(@RequestBody MasterOrderDTO dto){
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);
        masterOrderService.update(dto);
        return new Result();
    }
    @Login
    @DeleteMapping
    @ApiOperation("删除")
    public Result delete(@RequestBody Long[] ids){
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");
        masterOrderService.delete(ids);
        return new Result();
    }

    @CrossOrigin
    @Login
    @PutMapping("refuseUpdate")
    @ApiOperation("商户端-取消/拒绝订单(删除)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", paramType = "query", required = true, dataType="long"),
            @ApiImplicitParam(name = "verify", value = "取消人", paramType = "query", required = true, dataType="long"),
            @ApiImplicitParam(name="verify_reason",value="取消原因",paramType = "query",required = true,dataType = "String")
    })
    public Result refuseUpdate(@RequestParam long id, @RequestParam long verify, @RequestParam String verify_reason) throws Exception {
        return masterOrderService.caleclUpdate(id,Constants.OrderStatus.MERCHANTREFUSALORDER.getValue(),verify,new Date(),verify_reason);
    }
    @CrossOrigin
    @Login
    @PutMapping("cancelUpdate")
    @ApiOperation("商户端-取消/拒绝订单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", paramType = "query", required = true, dataType="long"),
            @ApiImplicitParam(name = "verify", value = "取消人", paramType = "query", required = true, dataType="long"),
            @ApiImplicitParam(name="verify_reason",value="取消原因",paramType = "query",required = true,dataType = "String")
    })
    public Result calcelUpdate(@RequestParam long id, @RequestParam(value = "verify",defaultValue = "")  long verify, @RequestParam(value = "verify_reason",defaultValue = "")  String verify_reason) throws Exception {
       return masterOrderService.caleclUpdate(id,verify,new Date(),verify_reason,false);
    }

    @CrossOrigin
    @Login
    @PutMapping("acceptUpdate")
    @ApiOperation("商户端-接受订单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", paramType = "query", required = true, dataType="long"),
            @ApiImplicitParam(name = "verify", value = "接受人", paramType = "query", required = true, dataType="long")
    })
    public Result acceptUpdate(@RequestParam long id, @RequestParam long verify) throws Exception {
       return  masterOrderService.acceptUpdate(id,Constants.OrderStatus.MERCHANTRECEIPTORDER.getValue(),verify,new Date(),"接受订单");
    }
    @CrossOrigin
    @Login
    @Transient
    @PutMapping("finishUpdate")
    @ApiOperation("商户端-完成订单(翻台)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", paramType = "query", required = true, dataType="long"),
            @ApiImplicitParam(name = "verify", value = "操作人", paramType = "query", required = true, dataType="long")
    })
    public Result finishUpdate(@RequestParam long id, @RequestParam long verify) throws Exception {
        return masterOrderService.finishUpdate(id,Constants.OrderStatus.MERCHANTAGFINISHORDER.getValue(),verify,new Date(),"完成订单");
    }
    @CrossOrigin
    @Login
    @Transient
    @PutMapping("refundYesUpdate")
    @ApiOperation("商户端-同意退款")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", paramType = "query", required = true, dataType="long"),
            @ApiImplicitParam(name = "verify", value = "审核人", paramType = "query", required = true, dataType="long")
    })
    public Result refundYesUpdate(@RequestParam long id, @RequestParam long verify) throws Exception {
        return masterOrderService.refundYesUpdate(id, Constants.OrderStatus.MERCHANTAGREEREFUNDORDER.getValue(), verify, new Date(), "同意退款");

    }
    @CrossOrigin
    @Login
    @PutMapping("refundNoUpdate")
    @ApiOperation("商户端-拒绝退款")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编号", paramType = "query", required = true, dataType="long"),
            @ApiImplicitParam(name = "verify", value = "拒绝人", paramType = "query", required = true, dataType="long"),
            @ApiImplicitParam(name="verify_reason",value="拒绝原因",paramType = "query",required = true,dataType = "String")
    })
    public Result refundNoUpdate(@RequestParam long id, @RequestParam long verify, @RequestParam String verify_reason) throws Exception {
       return masterOrderService.refundNoUpdate(id,Constants.OrderStatus.MERCHANTREFUSESREFUNDORDER.getValue(),verify,new Date(),verify_reason);
    }

    @Login
    @PostMapping("calculateGift")
    @ApiOperation("客户端-使用赠送金")
    public Result<DesignConditionsDTO> calculateGift(@RequestBody DesignConditionsDTO dct){
        return new Result<DesignConditionsDTO>().ok(masterOrderService.calculateGift(dct));
    }

    @Login
    @PostMapping("calculateCoupon")
    @ApiOperation("客户端-使用优惠卷")
    public Result<DesignConditionsDTO> calculateCoupon(@RequestBody DesignConditionsDTO dct){
        return new Result<DesignConditionsDTO>().ok(masterOrderService.calculateCoupon(dct));
    }

    @Login
    @PostMapping("notDiscounts")
    @ApiOperation("客户端-无任何优惠")
    public Result<DesignConditionsDTO> notDiscounts(@RequestBody DesignConditionsDTO dct){
        return new Result<DesignConditionsDTO>().ok(masterOrderService.notDiscounts(dct));
    }

    @Login
    @PostMapping("calculateGiftCoupon")
    @ApiOperation("客户端-使用优惠卷与赠送金")
    public Result<DesignConditionsDTO> calculateGiftCoupon(@RequestBody DesignConditionsDTO dct){
        return new Result<DesignConditionsDTO>().ok(masterOrderService.calculateGiftCoupon(dct));
    }
    @CrossOrigin
    @Login
    @PutMapping("setRoom")
    @ApiOperation("商户端-设置包房")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "订单编号", paramType = "query", required = true, dataType="long"),
            @ApiImplicitParam(name = "roomSetId", value = "预约包房编号", paramType = "query", required = true, dataType="long")
    })
    public Result setRoom(@RequestParam long id, @RequestParam long roomSetId) throws Exception {
        return masterOrderService.setRoom(id,roomSetId);
    }
    @Login
    @PostMapping("reserveRoom")
    @ApiOperation("客户端-下单支付成功后，预定包房")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mainOrderId", value = "编号", paramType = "query", required = true, dataType="String")
    })
    public Result reserveRoom(@RequestBody OrderDTO dto, @LoginUser ClientUserEntity user, String mainOrderId) throws ParseException {
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);
        return  masterOrderService.reserveRoom(dto,user,mainOrderId);
    }

    @Login
    @PostMapping("orderFoodByRoom")
    @ApiOperation("预订包房后，再订餐")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mainOrderId", value = "预订包房订单号", paramType = "query", required = true, dataType="String")
    })
    public Result orderFoodByRoom(@RequestBody OrderDTO dto, @LoginUser ClientUserEntity user, String mainOrderId){
        List<SlaveOrderEntity> slaveOrder = dto.getSlaveOrder();
        BigDecimal platformBrokerage=new BigDecimal("0");
        BigDecimal merchantProceeds=new BigDecimal("0");
        for (SlaveOrderEntity s:slaveOrder ){
            platformBrokerage=platformBrokerage.add(s.getPlatformBrokerage());
            merchantProceeds=merchantProceeds.add(s.getMerchantProceeds());
        }
        dto.setPlatformBrokerage(platformBrokerage);
        dto.setMerchantProceeds(merchantProceeds);
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);
        List<SlaveOrderEntity> dtoList=dto.getSlaveOrder();
        return  masterOrderService.orderFoodByRoom(dto,dtoList,user,mainOrderId);
    }
    @Login
    @GetMapping("getAuxiliaryOrder")
    @ApiOperation("获取关联订单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "orderId", value = "订单ID", paramType = "query",required=true, dataType="String")
    })
    public Result<PageData<OrderDTO>> getAuxiliaryOrder(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<OrderDTO> page = masterOrderService.pageGetAuxiliaryOrder(params);
        return new Result<PageData<OrderDTO>>().ok(page);
    }
    @CrossOrigin
    @Login
    @GetMapping("getStatus4Order")
    @ApiOperation("语音推送接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "merchantId", value = "编号", paramType = "query", required = true, dataType="String")
    })
    public Result getStatus4Order(@ApiIgnore @RequestParam Map<String, Object> params){
        List<MasterOrderEntity> list = masterOrderService.getStatus4Order(params);
        return new Result().ok(list.size());
    }


    @Login
    @GetMapping("getRefundGoods")
    @ApiOperation("商户端退款订单详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderId", value = "主订单编号", paramType = "query", required = true, dataType="string"),
            @ApiImplicitParam(name = "goodId", value = "退菜菜品id", paramType = "query", required = true, dataType="Long")
    })
    public Result<List<OrderDTO>> getRefundGoods (@ApiIgnore @RequestParam Map<String, Object> params){
        List<OrderDTO> data = masterOrderService.refundOrder(params);
        return new Result<List<OrderDTO>>().ok(data);
    }
    @Login
    @GetMapping("deleteOrder")
    @ApiOperation("用户端删除订单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderId", value = "主订单编号", paramType = "query", required = true, dataType="string")
    })
    public Result deleteOrder (@RequestParam String orderId){
        Result result =  masterOrderService.deleteOrder(orderId);
        return new Result().ok(result);
    }
    @GetMapping("shareOrder")
    @ApiOperation("用户端分享订单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderId", value = "主订单编号", paramType = "query", required = true, dataType="string")
    })
    public Result shareOrder (@RequestParam String orderId){
        ShareOrderDTO shareOrder =  masterOrderService.shareOrder(orderId);
        return new Result().ok(shareOrder);
    }

    @GetMapping("backDishesPage")
    @ApiOperation("退菜列表PC")
    public Result<PageData<BackDishesVo>> backDishesPage(@RequestParam Map map) {
        return new Result<PageData<BackDishesVo>>().ok(masterOrderService.backDishesPage(map));
    }

    @GetMapping("pagePC")
    @ApiOperation("分页查询PC")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "startDate",value="开始时间",dataType = "date",paramType = "query",required = false),
            @ApiImplicitParam(name ="endDate",value = "结束时间",dataType = "date",paramType = "query",required = false),
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType = "int"),
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query", required = true, dataType = "int"),
    })
    public Result<PageTotalRowData<OrderVo>> pagePC(@ApiIgnore @RequestParam Map<String, Object> params) {
        return new Result<PageTotalRowData<OrderVo>>().ok(masterOrderService.pagePC(params));
    }

    @Login
    @GetMapping("simplePage")
    @ApiOperation("分页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "merchantId", value = "商户id号", paramType = "query", required = true, dataType="long") ,
            @ApiImplicitParam(name = "index", value = "页码", paramType = "query",required = false, dataType="int"),
            @ApiImplicitParam(name = "pageNumber", value = "页数", paramType = "query", required = false,dataType="int")
    })
    //public Result getSimpleOrders(@ApiIgnore @RequestParam Map<String, Object> params){
    public Result getSimpleOrders(@RequestParam  Long merchantId, Integer index, Integer pageNumber){


        System.out.println("xx"+merchantId+","+","+index+","+pageNumber);
        if(index == null){
            index = 0;
        }else{
            if(index >0)
                index--;
        }

        if(pageNumber == null)
            pageNumber = 10;

        Integer itemNum = masterOrderSimpleService.getCount(merchantId);
        if(itemNum == null)
            itemNum = 0;
        int rpages = (itemNum+pageNumber-1)/pageNumber;


        Result orderList = new Result();
        List<OrderSimpleEntity> orderList1 = masterOrderSimpleService.getOrderList(merchantId, index, pageNumber);
        System.out.println("数值:"+orderList1);
        for(int i=0;i<orderList1.size();i++){
            System.out.println("qurey result:"+orderList1.get(i).toString());
        }
        orderList.setData(orderList1);

        orderList.setMsg(rpages+"");

        return orderList;
    }

    @GetMapping("getOrderByYwy")
    @ApiOperation("查询该业务员下是否有商户订单2分钟未接单的订单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType="String") ,
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "mobile", value = "手机号", paramType = "query", required = true, dataType="String")
    })
    public  Result<PageData<OrderDTO>> getOrderByYwy(@ApiIgnore @RequestParam Map<String, Object> params){
        PageData<OrderDTO> orderByYwy = masterOrderService.getOrderByYwy(params);
        return new Result().ok(orderByYwy);
    }
    @GetMapping("bmGet")
    @ApiOperation("业务员已经查看")
    @ApiImplicitParams({
    })
    public  Result bmGet( @RequestParam String orderId){
        MasterOrderEntity masterOrderEntity = masterOrderService.selectByOrderId(orderId);
        if (masterOrderEntity==null){
            return new Result().error("该订单没有找到");
        }else {
            masterOrderService.bmGet(orderId);
            return new Result().ok("成功");
        }

    }
    @GetMapping("roomOrderPrinter")
    @ApiOperation("房订单打印PC")
    public Result roomOrderPrinter(@RequestParam String orderId) {
        return new Result().ok(masterOrderService.roomOrderPrinter(orderId));
    }

    @GetMapping("inProcessList")
    @ApiOperation("进行中的订单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "merchantId", value = "商户id号", paramType = "query", required = false, dataType="Long"),
            @ApiImplicitParam(name = "index", value = "页码", paramType = "query",required = false, dataType="Integer"),
            @ApiImplicitParam(name = "pageNumber", value = "页数", paramType = "query", required = false,dataType="Integer")

        })
    public Result inProcessList(@ApiIgnore @RequestParam Map<String, String> params){

        Long mchId = params.get("merchantId")==null?0:Long.parseLong(params.get("merchantId"));
        Integer index = params.get("index")==null?0:Integer.parseInt(params.get("index"));
        Integer pageNumber = params.get("pageNumber")==null?0:Integer.parseInt(params.get("pageNumber"));
        Result result = new Result();

        if(mchId == 0){
            result.setMsg("请输入商户id");
            return result.ok(null);
        }
        QueryWrapper<MasterOrderEntity> wrapper = new QueryWrapper<>();

        if(index == null){
            index = 0;
        }else{
            if(index >0)
                index--;
        }
        if(pageNumber == null)
            pageNumber = 10;

        wrapper.eq("merchant_id",mchId);
        wrapper.in("status",2,7);
        wrapper.eq("check_status",0);

        IPage<MasterOrderEntity> map = new Page<MasterOrderEntity>(index,pageNumber);
        IPage<MasterOrderEntity> orderList = masterOrderDao.selectPage(map, wrapper);


        List<MasterOrderEntity> records = orderList.getRecords();
        List<ClientUserEntity> clientUserList = new ArrayList<>();

        List<MasterOrderCombo> masterOrderCombos = new ArrayList<>();
        for(int i=0;i<records.size();i++){
            MasterOrderCombo masterOrderCombo = new MasterOrderCombo();

            MasterOrderEntity currentEntity = records.get(i);
            String mobile = currentEntity.getContactNumber();
            masterOrderCombo.setMasterOrderEntity(currentEntity);
            ClientUserEntity byMobile = null;
            if(mobile != null){
                byMobile = clientUserService.getByMobile(mobile);
                if(byMobile != null){
                    masterOrderCombo.setClientUserEntity(byMobile);
                }
            }
            masterOrderCombos.add(masterOrderCombo);
        }
        MasterOrderVo masterOrderVo = new MasterOrderVo();
        if(records.size()>0){
            masterOrderVo.setMasterOrderCombos(masterOrderCombos);
        }
        masterOrderVo.setPages(orderList.getPages());

            return result.ok(masterOrderVo);
    }

    @GetMapping("/update_sales")
    @ApiOperation("进行中的订单")
    @ApiImplicitParam(name = "merchantId", value = "商户id号", paramType = "query", required = true, dataType="Long")
    public String updateSales(@RequestParam Long merchantId){
        try{
            masterOrderService.updateSalesVolume(merchantId);
        }catch(Exception e){
            e.printStackTrace();
            return "error";
        }
        return "success";
    }



}
