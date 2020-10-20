package io.treasure.dao;

import io.treasure.common.dao.BaseDao;
import io.treasure.dto.BusinessManagerDTO;
import io.treasure.entity.BusinessManagerEntity;
import io.treasure.entity.BusinessManagerTrackRecordEntity;
import io.treasure.vo.BusinessManagerPageVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface BusinessManagerDao  extends BaseDao<BusinessManagerEntity> {
    List<BusinessManagerDTO> getByNameAndPassWord(String realName, String passWord);
    List<BusinessManagerDTO> listPage(Map<String, Object> params);
   void binding(int bmId, String mchId);
    BusinessManagerDTO selectByMobile(String mobile);
  List<BusinessManagerTrackRecordEntity> selectlogById(long id);
    List<BusinessManagerTrackRecordEntity>  selectByMartId(long martId);
    BusinessManagerTrackRecordEntity  selectByMartIdAndBmid(@Param("martId") long martId, @Param("bmId")int bmId);
    List<BusinessManagerPageVo> pagePC(Map<String, Object> params);
    void delLogById(Integer pid);
}
