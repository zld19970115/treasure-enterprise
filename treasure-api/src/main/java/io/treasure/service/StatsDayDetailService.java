package io.treasure.service;

import io.treasure.common.service.CrudService;
import io.treasure.dto.MasterOrderDTO;
import io.treasure.dto.OrderDTO;
import io.treasure.dto.SlaveOrderDTO;
import io.treasure.dto.StatsDayDetailDTO;
import io.treasure.entity.MasterOrderEntity;
import io.treasure.entity.StatsDayDetailEntity;

import java.util.List;

/**
 * 平台日交易明细表
 *
 * @author zhangguanglin 494535675@qq.com
 * @since 1.0.0 2019-12-24
 */
public interface StatsDayDetailService extends CrudService<StatsDayDetailEntity, StatsDayDetailDTO> {
    /***
     * @Author: Zhangguanglin
     * @Description:
     * @Date: 2020/1/8
     * @param masterOrderEntity:
     * @Return: 生成订单时维护平台日交易表
     */
//    int creatreStatsDayDetail(MasterOrderEntity masterOrderEntity);

    /***
     * @Author: Zhangguanglin
     * @Description:
     * @Date: 2020/1/8
     * @param orderDTO:
     * @param slaveOrderDTO:
     * @Return: 退菜时，维护平台日交易明细表
     */
//    int insertReturnGood(OrderDTO orderDTO, SlaveOrderDTO slaveOrderDTO);

    /***
     * @Author: Zhangguanglin
     * @Description:
     * @Date: 2020/1/8
     * @param dto:
     * @Return: 同意退款后维护平台日明细表
     */
//    int insertReturnOrder(MasterOrderDTO dto);


    /***
     * @Author: Zhangguanglin
     * @Description:
     * @Date: 2020/1/8
     * @param dto:
     * @Return: 取消已支付订单后维护平台日明细表
     */
//    int insertCancelOrder(MasterOrderDTO dto);

    /***
     * @Author: Zhangguanglin
     * @Description:
     * @Date: 2020/1/8
     * @param id:  提现id
     * @Return: 同意提现维护平台日交易明细表
     */
//    int insertMerchantWithdraw(long id);

      int insertFinishUpdate(MasterOrderDTO dto);

      /***
       * @Author: Zhangguanglin
       * @Description:
       * @Date: 2020/1/10
       * @param null:
       * @Return: 查询是否存在等于 年月日 and 商户id 的数据
       *            如果不存在则创建，如果存在则更新：总金额  日销量
       */

}
