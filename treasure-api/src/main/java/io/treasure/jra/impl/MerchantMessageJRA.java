package io.treasure.jra.impl;

import io.treasure.config.MyRedisPool;
import io.treasure.dao.OrderDao;
import io.treasure.enm.EIncrType;
import io.treasure.enm.EMessageUpdateType;
import io.treasure.jra.IMerchantMessageJRA;
import io.treasure.jro.MerchantMessage;
import io.treasure.jro.MerchantSet;
import lombok.AllArgsConstructor;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import java.util.*;

//更新初始数据
@Component
public class MerchantMessageJRA implements IMerchantMessageJRA {

    @Autowired(required = false)
    OrderDao orderDao;

    @Autowired
    MerchantSetJRA merchantSetJRA;

    Jedis jedis = MyRedisPool.getJedis();


    private String getTargetItem(String merchantId){
        return new MerchantMessage().initFieldName(merchantId).getFieldName();
    }

    /**
     * @param merchantId 对象未存在则需要重新初始化
     * @return
     */
    @Override
    public boolean isMerchantMessageExist(String merchantId) {

        String targetItem = getTargetItem(merchantId);
        return jedis.hexists(targetItem,MerchantMessage.getCreateOrderCounterField());
    }

    @Override
    public boolean addRecord(MerchantMessage merchantMessage) {

        String targetItem = merchantMessage.initFieldName();

        Map<String,String> map = new HashMap<>();
        map.put(merchantMessage.getCreateOrderCounterField(),merchantMessage.getCreateOrderCounter()+"");
        map.put(merchantMessage.getAttachItemCounterField(),merchantMessage.getAttachItemCounter()+"");
        map.put(merchantMessage.getAttachRoomCounterField(),merchantMessage.getAttachRoomCounter()+"");
        map.put(merchantMessage.getRefundOrderCounterField(),merchantMessage.getRefundOrderCounter()+"");
        map.put(merchantMessage.getDetachItemCounterField(),merchantMessage.getDetachItemCounter()+"");
        map.put(merchantMessage.getInProcessCounterField(),merchantMessage.getInProcessCounter()+"");

        String res = jedis.hmset(targetItem, map).toLowerCase();

        if(res.equals("ok"))
            return true;
        return false;
    }

    @Override
    public MerchantMessage getMerchantMessageCounter(String merchantId) {

        List<String> res = jedis.hmget(getTargetItem(merchantId),
                                        MerchantMessage.getCreateOrderCounterField(),
                                        MerchantMessage.getAttachItemCounterField(),
                                        MerchantMessage.getAttachRoomCounterField(),
                                        MerchantMessage.getRefundOrderCounterField(),
                                        MerchantMessage.getDetachItemCounterField(),
                                        MerchantMessage.getInProcessCounterField());


        if(res.size()<6)
            return null;
        MerchantMessage merchantMessage = new MerchantMessage();
        merchantMessage.setMerchantId(merchantId);
        merchantMessage.initFieldName();

        merchantMessage.setCreateOrderCounter(Integer.parseInt(res.get(0)));
        merchantMessage.setAttachItemCounter(Integer.parseInt(res.get(1)));
        merchantMessage.setAttachRoomCounter(Integer.parseInt(res.get(2)));
        merchantMessage.setRefundOrderCounter(Integer.parseInt(res.get(3)));
        merchantMessage.setDetachItemCounter(Integer.parseInt(res.get(4)));
        merchantMessage.setInProcessCounter(Integer.parseInt(res.get(5)));

        //重复更新进行中的订单
        Integer inp = orderDao.selectInProcessCount(Long.parseLong(merchantId));
        if(inp != null)
            merchantMessage.setInProcessCounter(inp);

        return merchantMessage;
    }


    @Override
    public MerchantMessage updateSpecifyField(String merchantId, EMessageUpdateType eMessageUpdateType, EIncrType eIncrType) {
        int step = 1;
        String targetItem = new MerchantMessage().initFieldName(merchantId).getFieldName();

        if(eIncrType.ordinal()==EIncrType.SUB.ordinal())
            step = -1;

        if(isMerchantMessageExist(merchantId) && merchantSetJRA.isExistMember(merchantId)){
            String hincrByField = null;
            switch(eMessageUpdateType){

                case CREATE_ORDER://新单

                    hincrByField = MerchantMessage.getCreateOrderCounterField();
                    break;
                case ATTACH_ITEM://加菜

                    hincrByField = MerchantMessage.getAttachItemCounterField();
                    break;
                case ATTACH_ROOM://加房

                    hincrByField = MerchantMessage.getAttachRoomCounterField();
                    break;
                case REFUND_ORDER://整单退

                    hincrByField = MerchantMessage.getRefundOrderCounterField();
                    break;
                case DETACH_ITEM://退菜

                    hincrByField = MerchantMessage.getDetachItemCounterField();
                    break;
            }
            if(hincrByField != null)
                jedis.hincrBy(targetItem,hincrByField,step);


            return getMerchantMessageCounter(merchantId);
        }else{
            //如果redis模型不存在，则直接创建新模型
            MerchantMessage counterByOrderDao = getCounterByOrderDao(Long.parseLong(merchantId));
            addRecord(counterByOrderDao);
            merchantSetJRA.add(merchantId);
            return counterByOrderDao;
        }
    }

    @Override
    public List<String> MerchantList(){

        Set<String> keys = jedis.keys(MerchantMessage.MCH_MSG_PREFIX + "*");
        if(keys.isEmpty())
            return null;

        return new ArrayList<>(keys);
    }

    //每天凌晨五点定时清理提醒消息
    @Override
    public void deleteRecord(){

        //查询新单数量
        merchantSetJRA.removeAll();

    }

    public MerchantMessage getCounterByOrderDao(Long merchantId){
        MerchantMessage merchantMessage = new MerchantMessage();
        merchantMessage.setMerchantId(merchantId+"");

        Integer newOrders = orderDao.selectNewOrderCount(merchantId);

        //Integer attachItems = orderDao.selectActtachItemCount(merchantId);
        //Integer attachRooms = orderDao.selectAttachRoomCount(merchantId);
        Integer refundOrders = orderDao.selectRefundOrderCount(merchantId);
        Integer detachItems = orderDao.selectDetachItemCount(merchantId);
        Integer inProcess = orderDao.selectInProcessCount(merchantId);

        if(newOrders != null)
            merchantMessage.setCreateOrderCounter(newOrders);
        //if(attachItems != null)
          // merchantMessage.setAttachItemCounter(attachItems);
//
//        if(attachRooms != null)
//            merchantMessage.setAttachRoomCounter(attachRooms);

        if(refundOrders != null)
            merchantMessage.setRefundOrderCounter(refundOrders);

        if(detachItems != null)
            merchantMessage.setDetachItemCounter(detachItems);
        if(inProcess != null)
            merchantMessage.setInProcessCounter(inProcess);

        return merchantMessage;
    }


}

