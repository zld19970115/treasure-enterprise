package io.treasure.controller;

import io.treasure.annotation.Login;
import io.treasure.dto.GoodDTO;
import io.treasure.dto.MerchantRoomDTO;
import io.treasure.dto.RefundOrderDTO;
import io.treasure.dto.SlaveOrderDTO;
import io.treasure.entity.ClientUserEntity;
import io.treasure.entity.MasterOrderEntity;
import io.treasure.entity.RefundOrderEntity;
import io.treasure.entity.SlaveOrderEntity;
import io.treasure.service.*;


import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;

import io.treasure.common.utils.Result;
import io.treasure.common.validator.AssertUtils;
import io.treasure.common.validator.ValidatorUtils;
import io.treasure.common.validator.group.AddGroup;
import io.treasure.common.validator.group.DefaultGroup;
import io.treasure.common.validator.group.UpdateGroup;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 订单菜品表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-23
 */
@RestController
@RequestMapping("/api/slaveOrder")
@Api(tags = "订单菜品表")
public class ApiSlaveOrderController {
    @Autowired
    private SlaveOrderService slaveOrderService;
    @Autowired
    private RefundOrderService refundOrderService;
    @Autowired
    private MasterOrderService masterOrderService;
    @Autowired
    private MerchantRoomService merchantRoomService;
    @Autowired
    private GoodService goodService;
    @Autowired
    private ClientUserService clientUserService;

    @Login
    @GetMapping("page")
    @ApiOperation("分页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType = "int"),
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query", required = true, dataType = "int"),
            @ApiImplicitParam(name = Constant.ORDER_FIELD, value = "排序字段", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = Constant.ORDER, value = "排序方式，可选值(asc、desc)", paramType = "query", dataType = "String")
    })
    public Result<PageData<SlaveOrderDTO>> page(@ApiIgnore @RequestParam Map<String, Object> params) {
        PageData<SlaveOrderDTO> page = slaveOrderService.page(params);

        return new Result<PageData<SlaveOrderDTO>>().ok(page);
    }

    @Login
    @GetMapping("{id}")
    @ApiOperation("信息")
    public Result<SlaveOrderDTO> get(@PathVariable("id") Long id) {
        SlaveOrderDTO data = slaveOrderService.get(id);

        return new Result<SlaveOrderDTO>().ok(data);
    }

    @Login
    @PostMapping
    @ApiOperation("保存")
    public Result save(@RequestBody SlaveOrderDTO dto) {
        //效验数据
        ValidatorUtils.validateEntity(dto, AddGroup.class, DefaultGroup.class);

        slaveOrderService.save(dto);

        return new Result();
    }

    @Login
    @PutMapping
    @ApiOperation("修改")
    public Result update(@RequestBody SlaveOrderDTO dto) {
        //效验数据
        ValidatorUtils.validateEntity(dto, UpdateGroup.class, DefaultGroup.class);

        slaveOrderService.update(dto);

        return new Result();
    }

    @Login
    @DeleteMapping
    @ApiOperation("删除")
    public Result delete(@RequestBody Long[] ids) {
        //效验数据
        AssertUtils.isArrayEmpty(ids, "id");

        slaveOrderService.delete(ids);

        return new Result();
    }

    @Login
    @PutMapping("refundGood")
    @ApiOperation("用户退单个菜品")
    public Result refundGood(@RequestBody SlaveOrderDTO slaveOrderDTO) {

            Long goodId = slaveOrderDTO.getGoodId();
            String orderId = slaveOrderDTO.getOrderId();
            //用户申请退的数量
            BigDecimal quantity = slaveOrderDTO.getQuantity();
            SlaveOrderDTO allGoods = slaveOrderService.getAllGoods(orderId, goodId);
        if (allGoods.getStatus() == 2) {
            //此订单菜品总数量
            BigDecimal quantity1 = allGoods.getQuantity();
            if (quantity1.compareTo(quantity) == 0) {
                slaveOrderService.updateSlaveOrderStatus(6, orderId, goodId);
            }
            if (quantity1.compareTo(quantity) == 1) {
                slaveOrderService.updateSlaveOrderStatus(10, orderId, goodId);
            }
            BigDecimal price = slaveOrderDTO.getPrice();
            BigDecimal totalMoney = price.multiply(quantity);
            RefundOrderEntity ro = new RefundOrderEntity();
            long id = System.currentTimeMillis();
            Random random = new Random();
            String refundID = "";
            for (int i = 0; i < 8; i++) {
                //首字母不能为0
                refundID += (random.nextInt(9) + 1);
            }
            //组装退款ID
            refundID = refundID + id;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //获取当前时间作为退款申请时间
            String date = sdf.format(new Date());
            //查询出订单对应商户信息
            MasterOrderEntity masterOrderEntity = masterOrderService.selectByOrderId(slaveOrderDTO.getOrderId());
            //包房ID
            Long roomId = masterOrderEntity.getRoomId();
            //获取包房信息
            MerchantRoomDTO merchantRoomDTO = merchantRoomService.get(roomId);
            //获取商品信息
            GoodDTO goodDTO = goodService.get(goodId);
            //获取用户信息通过电话
            ClientUserEntity userByPhone = clientUserService.getUserByPhone(masterOrderEntity.getContactNumber());
            String s = slaveOrderDTO.getMerchantId();
            long merchantID = Long.parseLong(s);
            ro.setRefundId(refundID.trim());
            ro.setGoodId(slaveOrderDTO.getGoodId());
            ro.setOrderId(slaveOrderDTO.getOrderId());
            ro.setPrice(slaveOrderDTO.getPrice());
            ro.setRefundDate(date);
            ro.setRefundQuantity(quantity);
            ro.setRefundReason(slaveOrderDTO.getRefundReason());
            ro.setTotalMoney(totalMoney);
            ro.setMerchantId(merchantID);
            ro.setContactNumber(masterOrderEntity.getContactNumber());
            ro.setRoomName(merchantRoomDTO.getName());
            ro.setGoodName(goodDTO.getName());
            ro.setIcon(goodDTO.getIcon());
            ro.setTotalFee(masterOrderEntity.getPayMoney().toString());
            ro.setUserId(userByPhone.getId());
            refundOrderService.insertRefundOrder(ro);

        }
        return new Result();
    }
}