
package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;
import io.treasure.common.utils.Result;
import io.treasure.dao.SlaveOrderDao;
import io.treasure.dto.*;
import io.treasure.enm.Constants;
import io.treasure.entity.*;
import io.treasure.push.AppPushUtil;
import io.treasure.service.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.common.service.impl.CrudServiceImpl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 订单菜品表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-23
 */
@Service
public class SlaveOrderServiceImpl extends CrudServiceImpl<SlaveOrderDao, SlaveOrderEntity, SlaveOrderDTO> implements SlaveOrderService {

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
    @Autowired
    private PayService payService;
    @Autowired
    private MerchantUserService merchantUserService;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private SlaveOrderService slaveOrderService;

    @Override
    public QueryWrapper<SlaveOrderEntity> getWrapper(Map<String, Object> params) {
        String id = (String) params.get("id");

        QueryWrapper<SlaveOrderEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }

    @Override
    public PageData<SlaveOrderDTO> getOandPoGood(Map<String, Object> params) {

        IPage<SlaveOrderEntity> pages=getPage(params, Constant.CREATE_DATE,false);

        List<SlaveOrderDTO> list=baseDao.getOandPoGood(params);



        return getPageData(list,pages.getTotal(), SlaveOrderDTO.class);

    }

    @Override
    public List<SlaveOrderEntity> selectslaveOrderByOrderId(String orderId) {
        return baseDao.selectslaveOrderByOrderId(orderId);
    }

    @Override
    public List<SlaveOrderEntity> selectByOrderId(String orderId) {
        List<SlaveOrderEntity> slaveOrderEntityList = baseDao.selectList(queryWrapper(orderId));
        return slaveOrderEntityList;
    }

    @Override
    public List<SlaveOrderEntity> selectByOrderIdAndStatus(String orderId) {
        List<SlaveOrderEntity> slaveOrderEntityList = baseDao.selectList(queryWrapper2(orderId));
        return slaveOrderEntityList;
    }

    @Override
    public SlaveOrderDTO getAllGoods(String orderId, long goodId) {
        return baseDao.getAllGoods(orderId, goodId);
    }

    /**
     * 更新订单菜品表中退款id
     *
     * @param refundId
     * @param orderId
     * @param goodId
     */
    @Override
    public void updateRefundId(String refundId, String orderId, Long goodId) {
        baseDao.updateRefundId(refundId, orderId, goodId);
    }

    /**
     * 根据订单ID和商品ID更改订单菜品状态
     *
     * @param status
     * @param orderId
     * @param goodId
     */
    @Override
    public void updateSlaveOrderStatus(int status, String orderId, Long goodId) {
        baseDao.updateSlaveOrderStatus(status, orderId, goodId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result refundGood(SlaveOrderDTO slaveOrderDTO) {
        Result result = new Result();
        Long goodId = slaveOrderDTO.getGoodId();
        String orderId = slaveOrderDTO.getOrderId();

        //用户申请退的数量
        BigDecimal quantity = slaveOrderDTO.getQuantity();
        SlaveOrderDTO allGoods = this.getAllGoods(orderId, goodId);
        if (allGoods.getStatus() != Constants.OrderStatus.MERCHANTRECEIPTORDER.getValue() && allGoods.getStatus() != Constants.OrderStatus.PAYORDER.getValue()) {
            result.error("此菜品无法退菜！");
        }
        if (allGoods.getStatus() == 2) {
            //此订单菜品总数量
            BigDecimal quantity1 = allGoods.getQuantity();
            if (quantity1.compareTo(quantity) >= 0) {
                if (allGoods.getStatus() == Constants.OrderStatus.MERCHANTRECEIPTORDER.getValue()) {
                    this.updateSlaveOrderStatus(Constants.OrderStatus.USERAPPLYREFUNDORDER.getValue(), orderId, goodId);
                } else if (allGoods.getStatus() == Constants.OrderStatus.PAYORDER.getValue()) {
                    this.updateSlaveOrderStatus(Constants.OrderStatus.MERCHANTREFUSALORDER.getValue(), orderId, goodId);
                    if (allGoods.getCreator() == null) {
                        result.error("此菜品无法退菜！无创建用户信息！");
                    }
                    if (allGoods.getPayMoney().compareTo(new BigDecimal(0)) == 0) {
                        result.error("此菜品价格为0元，无法退菜！");
                    }
                } else {
                    result.error("此菜品无法退菜！【菜品状态错误】");
                }
            }
//            if (quantity1.compareTo(quantity) == 1) {
//                slaveOrderService.updateSlaveOrderStatus(10, orderId, goodId);
//            }
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
            if (roomId != null) {
                MerchantRoomDTO merchantRoomDTO = merchantRoomService.get(roomId);
                ro.setRoomName(merchantRoomDTO.getName());
            }


            //获取商品信息
            GoodDTO goodDTO = goodService.get(goodId);
            //获取用户信息通过电话
            //      ClientUserEntity userByPhone = clientUserService.getUserByPhone(masterOrderEntity.getContactNumber());
            String s = slaveOrderDTO.getMerchantId();
            long merchantID = Long.parseLong(s);
            ro.setRefundId(refundID.trim());
            ro.setGoodId(slaveOrderDTO.getGoodId());
            ro.setOrderId(slaveOrderDTO.getOrderId());
            ro.setPrice(slaveOrderDTO.getPayMoney());
            ro.setRefundDate(date);
            ro.setRefundQuantity(quantity);
            ro.setRefundReason(slaveOrderDTO.getRefundReason());
            ro.setTotalMoney(slaveOrderDTO.getPayMoney());
            ro.setMerchantId(merchantID);
            ro.setContactNumber(masterOrderEntity.getContactNumber());

            ro.setGoodName(goodDTO.getName());
            ro.setIcon(goodDTO.getIcon());
            ro.setTotalFee(masterOrderEntity.getPayMoney().toString());
            ro.setUserId(masterOrderEntity.getCreator());

            refundOrderService.insertRefundOrder(ro);
            OrderDTO order = masterOrderService.getOrder(orderId);
            MerchantDTO merchantDTO = merchantService.get(order.getMerchantId());
            MerchantUserDTO merchantUserDTO = merchantUserService.get(merchantDTO.getCreator());
            String clientId = merchantUserDTO.getClientId();
            slaveOrderService.updateRefundReason(slaveOrderDTO.getRefundReason(),slaveOrderDTO.getOrderId(),slaveOrderDTO.getGoodId());
            if (StringUtils.isNotBlank(clientId)) {
                AppPushUtil.pushToSingleMerchant("订单管理", "您有退款信息，请及时处理退款！", "", clientId);
            }
        }
        return result;
    }

    @Override
    public List<SlaveOrderEntity> getOrderGoods(String orderId) {
        return baseDao.getOrderGoods(orderId);
    }
    @Override
    public List<SlaveOrderEntity> getOrderGoods1(String orderId) {
        return baseDao.getOrderGoods1(orderId);
    }
    @Override
    public void updateSlaveOrderPointDeduction(BigDecimal mp, BigDecimal pb, String orderId, Long goodId) {
        baseDao.updateSlaveOrderPointDeduction(mp,pb,orderId,goodId);
    }

    @Override
    public int updateRefundReason(String refundReason, String orderId, Long goodId) {
        return baseDao.updateRefundReason(refundReason,orderId,goodId);
    }


    private QueryWrapper<SlaveOrderEntity> queryWrapper(String orderId) {
        QueryWrapper<SlaveOrderEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(orderId), "order_id", orderId);

        return wrapper;
    }

    private QueryWrapper<SlaveOrderEntity> queryWrapper2(String orderId) {
        QueryWrapper<SlaveOrderEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(orderId), "order_id", orderId);
        wrapper.ne("status", 1);
        wrapper.ne("status", 9);
        wrapper.ne("status", 8);
        return wrapper;
    }
}