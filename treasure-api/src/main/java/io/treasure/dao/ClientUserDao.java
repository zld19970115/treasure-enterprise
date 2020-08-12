package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.dto.ClientUserDTO;
import io.treasure.entity.ClientUserEntity;
import io.treasure.entity.LevelStatusEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 用户信息
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-28
 */
@Mapper
public interface ClientUserDao extends BaseDao<ClientUserEntity> {

    ClientUserEntity getUserByMobile(String mobile);
    ClientUserEntity getUserByPhone(String mobile);
    ClientUserEntity getUserByOpenId(String openId);
    void updateOpenid(@Param("openId") String openId, @Param("mobile") String mobile);
    void updateUnionid(@Param("openId") String openId, @Param("mobile") String mobile);
    void updateCID(@Param("clientId") String clientId, @Param("mobile") String mobile);
    void insertLevelStatus(@Param("userId")Long userId, @Param("level")Integer level, @Param("balance")BigDecimal balance);
    ClientUserEntity getClientUser(Long id);
    ClientUserEntity  selectByMobile(String mobile);
    void updateWX(String userId);
    //添加一项
    void subtractGiftByMasterOrderCreate(@Param("creator") Long creator,
                                         @Param("gift") String gift);
    String selectZSCoinTotx();
    String selectCoinToBalance();
    List<LevelStatusEntity> selectLevelStatus(long userId);
    BigDecimal selectBlanceForLevel(int level);
   // List<ClientUserEntity> selectListByCondition(QueryClientUserDto queryClientUserDto);
    List<ClientUserEntity> selectZhuXiao(String mobile);
    List<ClientUserDTO> getRecordUserAll(Map<String, Object> params);
    void addRecordGiftByUserid(@Param("userId")String userId,@Param("useGift") String useGift);
    void addCoinsByUserid(@Param("userId")String userId, @Param("coins")String coins);
    void addBalanceByUserid(@Param("userId")String userId, @Param("baobi")String baobi);
    void updateBynewCoin(long userId,BigDecimal newCoin);
    List<ClientUserDTO> pagePC(Map<String,Object> params);

    ClientUserDTO pagePCTotalRow(Map<String,Object> params);

    Integer selectLogOffCount(String mobile);

}