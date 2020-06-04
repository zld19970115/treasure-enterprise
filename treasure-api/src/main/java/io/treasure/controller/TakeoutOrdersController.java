package io.treasure.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.common.utils.Result;
import io.treasure.dao.TakeoutOrdersDao;
import io.treasure.dao.TakeoutOrdersDetailDao;
import io.treasure.dto.*;
import io.treasure.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/takeout_orders")
@Api(tags="外卖订单")
public class TakeoutOrdersController {


    @Autowired(required = false)
    private TakeoutOrdersDao takeoutOrdersDao;

    @Autowired(required = false)
    private TakeoutOrdersDetailDao takeoutOrdersDetailDao;


    //================================================================================

    @GetMapping("/order")
    @ApiOperation("查询指定订单信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "order_id", value = "订单id", paramType = "query",required = true,dataType="String"),
            @ApiImplicitParam(name = "mch_id", value = "商家id", paramType = "query", required = false,dataType="long"),
            @ApiImplicitParam(name = "consumer_mobile", value = "客户电话", paramType = "query", required = false,dataType="String"),
            @ApiImplicitParam(name = "courier_id", value = "骑手id", paramType = "query",required=false, dataType="Long")
    })
    public Result requireOrder(@RequestParam(name = "order_id",required = true)String  orderId,
                               @RequestParam(name = "mch_id", required = false)Long mchId,
                               @RequestParam(name = "consumer_mobile", required = false)String consumerMobile,
                               @RequestParam(name = "courier_id",required=false)Long courierId
                               ){
        if(orderId == null)
            return new Result().error();

        if(mchId == null && consumerMobile == null && courierId == null)
            return new Result().error();

        QueryWrapper<TakeoutOrdersEntity> toeWrapper = new QueryWrapper<>();

        toeWrapper.eq("order_id",orderId);

        if(mchId != null){
            toeWrapper.eq("mch_id",mchId);
        }else if(consumerMobile != null){
            toeWrapper.eq("consumer_mobile",consumerMobile);
        }else if(courierId != null){
            toeWrapper.eq("courier_id",courierId);
        }

        TakeoutOrdersEntity toe = takeoutOrdersDao.selectOne(toeWrapper);

        if(toe != null){
            return new Result().ok(toe);
        }else{
            return new Result().ok("");
        }
    }


    public Date paseYMD(Date date) throws ParseException {
        SimpleDateFormat ymdt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");

        return ymd.parse(ymd.format(date)+" 00:00:00");
    }

    @GetMapping("/list")
    @ApiOperation("查询外卖订单列表")
    @ApiImplicitParams({

            @ApiImplicitParam(name ="mch_id",value = "商户id",dataType = "long",paramType = "query",required = false),
            @ApiImplicitParam(name ="consumer_mobile",value = "商户id",dataType = "long",paramType = "query",required = false),
            @ApiImplicitParam(name ="courier_id",value = "商户id",dataType = "long",paramType = "query",required = false),
            @ApiImplicitParam(name = "startTime",value="开始时间",dataType = "date",paramType = "query",required = false),
            @ApiImplicitParam(name ="stopTime",value = "结束时间",dataType = "date",paramType = "query",required = false),
            @ApiImplicitParam(name="timeMode",value = "默认精确;1精度(天)",dataType = "int",paramType = "query",required=false),
            @ApiImplicitParam(name ="preferenceTimeNo",value="1payment，2enjoy，3confirm",dataType = "int",defaultValue = "1",paramType = "query",required = false),
            @ApiImplicitParam(name="index",value = "页码",dataType = "int",defaultValue = "1",paramType = "query",required = false),
            @ApiImplicitParam(name="itemNum",value = "页数",dataType = "int",defaultValue = "10",paramType = "query",required = false)
    })

    public Result requireOrders(@RequestParam(name = "mch_id", required = false)Long mchId,
                                                   @RequestParam(name = "consumer_mobile", required = false)String cMobile,
                                                   @RequestParam(name = "courier_id",required=false)Long courierId,
                                                   @RequestParam(name = "startTime",required = false)Date startTime,
                                                   @RequestParam(name ="stopTime",required = false)Date stopTime,
                                                   @RequestParam(name="timeMode",defaultValue = "1",required=false)Integer timeMode,
                                                   @RequestParam(name ="preferenceTimeNo",required = false)int timeNo,
                                                   @RequestParam(name="index",required = false)Integer index,
                                                   @RequestParam(name="itemNum",required = false)Integer itemNum

                                                   ) throws ParseException {

        if(mchId == null && cMobile == null && courierId == null)
            return new Result().error();

        QueryWrapper<TakeoutOrdersEntity> toeWrapper = new QueryWrapper<>();

        //查单对象条件
        if(mchId != null)
            toeWrapper.eq("mch_id",mchId);
        if(cMobile != null)
            toeWrapper.eq("consumer_mobile",cMobile);
        if(courierId != null)
            toeWrapper.eq("courier_id",courierId);

        if(timeMode == 1){
            if(startTime != null)
                startTime = paseYMD(startTime);//ymdt转ymd

            if(stopTime != null)
                stopTime = paseYMD(stopTime);//ymdt转ymd

        }

        String timeColumn = "payment_time";
        if(timeNo == 2){
            timeColumn = "enjoy_time";
        }else if(timeNo == 3){
            timeColumn = "confirm_time";
        }

        if(stopTime == null){
            if(startTime != null){
                toeWrapper.lt(timeColumn,startTime);
            }else{
                toeWrapper.lt(timeColumn,new Date());
            }
        }else{
            if(startTime != null){
                toeWrapper.between(timeColumn,startTime,stopTime);
            }else{
                toeWrapper.ge(timeColumn,stopTime);
            }
        }




        return null;
    }

 /*
        @GetMapping("/order")
    @ApiOperation("查询指定订单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "order_id", value = "订单id", paramType = "query",required = true,dataType="String"),
            @ApiImplicitParam(name = "mch_id", value = "商家id", paramType = "query", required = false,dataType="long"),
            @ApiImplicitParam(name = "consumer_mobile", value = "客户电话", paramType = "query", required = false,dataType="String"),
            @ApiImplicitParam(name = "courier_id", value = "骑手id", paramType = "query",required=false, dataType="Long")
    })
    public Result requireOrder(@RequestParam(name = "order_id",required = true)String  orderId,
                               @RequestParam(name = "mch_id", required = false)Long mchId,
                               @RequestParam(name = "consumer_mobile", required = false)String consumerMobile,
                               @RequestParam(name = "courier_id",required=false)Long courierId
                               ){
     */

 /*


 {
  "takeoutOrdersDao":
  {
    "actualPayment": 0,
      "addressId": 0,
      "confirmTime": "string",
      "consumerMobile": "string",
      "courierId": "string",
      "deleted": 0,
      "discountAmount": 0,
      "discountId": 0,
      "enjoyTime": "string",
      "giftAmount": 0,
      "mchId": 0,
      "modifyTime": "string",
      "orderId": "string",
      "orderStatus": 0,
      "originPrice": 0,
      "paymentMethod": 0,
      "paymentTime": 0,
      "shippingMethod": 0,
      "state": 0,
      "status": 0,
      "version": 0
  },
  "takeoutOrdersDetailDTO":
  {
    "goodsIcon": "werwqr",
    "goodsName": "wqerwqe",
    "goodsPrice": 1,
    "odCataLogId": 10,
    "odGoodsId": "string",
    "odGoodsQuantity": 0,
    "odId": 0,
    "odItemSubTotal": 0,
    "odModifyTime": "string",
    "odOrderId": "string",
    "odOrderTime": "string",
    "odState": 0
  },
  "takeoutOrdersDetailDaos": [
    {}
  ]
}

  */


    @PostMapping("new")//take orders
    @ApiOperation("下单")
    public Result takeOrder(PlaceTOrderDTO tOrderDTO){


        return new Result().ok(tOrderDTO);
    }

    @PostMapping("atach")
    @ApiOperation("加单")
    public Result attachOrder(@RequestBody TakeoutOrdersDTO dto){
        System.out.println("got: "+dto.toString());
        return new Result().ok(dto);
    }

    @PutMapping("cancel")
    @ApiOperation("取消订单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "refundReason", value = "取消订单原因", paramType = "query", dataType="String"),
            @ApiImplicitParam(name = "id", value = "主订单ID", paramType = "query",required=true, dataType="Long")
    })
    public Result cancelOrder(){

        return null;
    }

    @PutMapping("item")
    @ApiOperation("修改订单")//用户申请退款，同意退款，拒绝退款清台
    public Result userCheck(Long id){
        return  null;
    }

    @PostMapping("discount")
    @ApiOperation("客户端-代金券")
    public Result<DesignConditionsDTO> calculateGift(@RequestBody DesignConditionsDTO dct){
        return null;//new Result<DesignConditionsDTO>().ok(masterOrderService.calculateGift(dct));
    }

    @DeleteMapping("item")
    @ApiOperation("用户端删除订单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderId", value = "主订单编号", paramType = "query", required = true, dataType="string")
    })
    public Result deleteOrder (@RequestParam String orderId){
        //Result result =  "000";//masterOrderService.deleteOrder(orderId);
        return null;//new Result().ok(result);
    }

    @GetMapping("getStatus4Order")
    @ApiOperation("语音推送接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "merchantId", value = "编号", paramType = "query", required = true, dataType="String")
    })
    public Result getStatus4Order(@ApiIgnore @RequestParam Map<String, Object> params){
        //List<MasterOrderEntity> list = masterOrderService.getStatus4Order(params);
        return null;//new Result().ok(list.size());
    }





    @PostMapping("/item")
    public Result placeOrder(@RequestBody PlaceTOrderDTO placeTakeoutOrderDTO){

        //订单详情表
        TakeoutOrdersDao takeoutOrdersDao = placeTakeoutOrderDTO.getTakeoutOrdersDao();

        //订单细项列表
        List<TakeoutOrdersDetailDao> takeoutOrdersDetailDaos = placeTakeoutOrderDTO.getTakeoutOrdersDetailDaos();


        TakeoutOrdersComboEntity resEntity = new TakeoutOrdersComboEntity();


        return new Result().ok(resEntity);
    }


    public void sum(Long odOrderId){

        QueryWrapper<TakeoutOrdersDetailEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("od_order_id",odOrderId);
        Integer integer = takeoutOrdersDetailDao.selectCount(queryWrapper);
        System.out.println("当前记录数:"+integer);
    }

}


/*

@CrossOrigin
@Login
@GetMapping("/list_sum")
@ApiOperation(value = "分类提现列表与汇总",tags = "按不同方式显示提现记录和汇总信息")
@ApiImplicitParams({
        @ApiImplicitParam(name ="merchantId",value = "商户id",dataType = "long",defaultValue = "0",paramType = "query",required = false),
        @ApiImplicitParam(name = "startTime",value="开始时间",dataType = "date",paramType = "query",required = false),
        @ApiImplicitParam(name ="stopTime",value = "结束时间",dataType = "date",paramType = "query",required = false),
        @ApiImplicitParam(name="type",value = "1微信;2支付宝;3银行卡",dataType = "int",paramType = "query",required = false),
        @ApiImplicitParam(name="timeMode",value = "默认精确;1精度(天)",dataType = "int",paramType = "query",required=false),
        @ApiImplicitParam(name="index",value = "页码",dataType = "int",defaultValue = "1",paramType = "query",required = false),
        @ApiImplicitParam(name="itemNum",value = "页数",dataType = "int",defaultValue = "10",paramType = "query",required = false)
})
public Result requireItems(Long merchantId, Date startTime, Date stopTime,
                           Integer type, Integer timeMode, Integer index, Integer itemNum) throws ParseException {

    QueryWrapper<MerchantWithdrawEntity> mweqw = new QueryWrapper<>();
    if(timeMode != null){
        if(timeMode == 1){
            if(startTime != null)
                startTime = paseYMD(startTime);//ymdt转ymd

            if(stopTime != null)
                stopTime = paseYMD(stopTime);//ymdt转ymd
        }
    }

    if(merchantId != null)
        mweqw.eq("merchant_id",merchantId);
    if(startTime != null && stopTime != null){
        mweqw.between("create_date",startTime,stopTime);
    }else if(startTime != null){
        mweqw.gt("create_date",startTime);//大于
    }else if(stopTime != null){

        mweqw.lt("create_date",stopTime);
    }
    if(type != null)
        mweqw.eq("type",type);

    PagePlus<MerchantWithdrawEntity> map = new PagePlus<MerchantWithdrawEntity>(index,itemNum);
    map.setCurrent(index);
    map.setSize(itemNum);
    IPage<MerchantWithdrawEntity> merchantWithdrawEntityIPage = merchantWithdrawDao.selectPage(map, mweqw);
    //merchantWithdrawEntityIPage.getRecords().forEach(System.out::println);

    //汇总
    if(merchantWithdrawEntityIPage == null)
        return null;

    //更新附加内容
    List<MerchantWithdrawEntity> records = merchantWithdrawEntityIPage.getRecords();
    Double wxSumMoney = 0d;
    Double  aliSumMoney = 0d;
    Double cardSumMoney = 0d;

    for(int i=0;i<records.size();i++){
        MerchantWithdrawEntity item = records.get(i);
        if(item.getType() != null){
            if(item.getType()== 1){
                wxSumMoney = wxSumMoney+item.getMoney();
            }else if(item.getType()== 2){
                aliSumMoney = aliSumMoney+item.getMoney();
            }else if(item.getType() == 3){
                cardSumMoney = cardSumMoney+item.getMoney();
            }
        }
    }
    //System.out.println("总页数"+map.getTotal());
    map.setAliMoney(aliSumMoney);
    map.setCardMoney(cardSumMoney);
    map.setWxMoney(wxSumMoney);

    return new Result().ok(merchantWithdrawEntityIPage);
    //return new Result().ok(merchantWithdrawEntityIPage.getRecords());
}

 */