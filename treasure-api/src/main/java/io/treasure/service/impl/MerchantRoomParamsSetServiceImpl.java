package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.common.utils.DateUtils;
import io.treasure.dao.MerchantRoomParamsSetDao;
import io.treasure.dto.MerchantDTO;
import io.treasure.dto.MerchantRoomParamsSetDTO;
import io.treasure.enm.Common;
import io.treasure.enm.MerchantRoomEnm;
import io.treasure.entity.MerchantRoomParamsEntity;
import io.treasure.entity.MerchantRoomParamsSetEntity;
import io.treasure.service.MerchantRoomParamsService;
import io.treasure.service.MerchantRoomParamsSetService;
import io.treasure.service.MerchantRoomService;
import io.treasure.service.MerchantService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import io.treasure.common.utils.Result;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.crypto.Data;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 商户端包房设置管理
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-08-14
 */
@Service
public class MerchantRoomParamsSetServiceImpl extends CrudServiceImpl<MerchantRoomParamsSetDao, MerchantRoomParamsSetEntity, MerchantRoomParamsSetDTO> implements MerchantRoomParamsSetService {
    //商户
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private MerchantRoomService merchantRoomService;
    //预约参数
    @Autowired
    private MerchantRoomParamsService merchantRoomParamsService;
    @Override
    public QueryWrapper<MerchantRoomParamsSetEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");
        //状态
        String status=(String)params.get("status");
        //商户
        String merchantId=(String)params.get("merchantId");
        QueryWrapper<MerchantRoomParamsSetEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);
        wrapper.eq(StringUtils.isNotBlank(merchantId),"merchant_id",merchantId);
        wrapper.eq(StringUtils.isNotBlank(status),"status",status);
        return wrapper;
    }

    /**
     * 删除
     * @param id
     */
    @Override
    public void remove(long id,int status) {
        baseDao.updateStatus(id,status);
    }

    @Override
    public void updateStatus(long id, int status) {
        baseDao.updateStatus(id,status);
    }

    /**
     * 设置包房
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result setRoom(long merchantId,long creator) {
        int days= MerchantRoomEnm.DAYS.getType();
        if(merchantId<=0){
            return new Result().error("商户编号必须大于0！");
        }
        //获取商户得信息
        MerchantDTO merchantDTO = merchantService.get(merchantId);
        //开店时间
        String businessShours=merchantDTO.getBusinesshours();
        Date openHourse= DateUtils.stringToDate(businessShours,"HH:mm");
        //闭店时间
        String colseShopHourses=merchantDTO.getCloseshophours();
        Date closeHorse=DateUtils.stringToDate(colseShopHourses,"HH:mm");
        //根据编号查询该商户对应的包房信息
        List list=merchantRoomService.getByMerchantId(merchantId, Common.STATUS_ON.getStatus());
        if(null!=list && list.size()>0){
            for(int i=1;i<=days;i++){
                //设置时间
                Date date= DateUtils.addDateDays(new Date(),i-1);
                String setdate=DateUtils.format(date,"yyyy-MM-dd");
                //预约参数
                List<MerchantRoomParamsEntity> paramsList=merchantRoomParamsService.getAllByStatus(Common.STATUS_ON.getStatus());
                for(int h=0;h<paramsList.size();h++){
                    MerchantRoomParamsEntity params=paramsList.get(h);
                    for(int room=0;room<list.size();room++){
                        Map map= (Map) list.get(room);
                        String roomId=String.valueOf(map.get("id"));
                        String roomName=String.valueOf(map.get("name"));
                        MerchantRoomParamsSetDTO dto=new MerchantRoomParamsSetDTO();
                        dto.setCreateDate(new Date());
                        dto.setCreator(creator);
                        dto.setMerchantId(merchantId);
                        dto.setRoomId(Long.parseLong(roomId));
                        dto.setType(params.getType());
                        dto.setRoomName(roomName);
                        dto.setState(MerchantRoomEnm.STATE_USE_NO.getType());
                        dto.setUseDate(DateUtils.stringToDate(setdate,"yyyy-MM-dd"));
                        dto.setStatus(Common.STATUS_ON.getStatus());
                        dto.setRoomParamsId(params.getId());
                        save(dto);
                    }
                }
            }
        }else{
            return new Result().error("该商户没有包房信息！");
        }
        return new Result().ok("包房预约成功！");
    }

    /**
     * 查询指定日期、时间段内可用包房
     * @param useDate
     * @param roomParamsId
     * @return
     */
    @Override
    public List getAvailableRoomsByData(Date useDate, long roomParamsId,Integer type,long merchantId){
        return baseDao.getAvailableRoomsByData(useDate,roomParamsId,type,merchantId);
    }

    @Override
    public int getAvailableRooms(long bigTime, long merchantId) {
        return baseDao.getAvailableRooms(bigTime,merchantId);
    }

    @Override
    public int getAvailableRoomsDesk(long bigTime, long merchantId) {
        return baseDao.getAvailableRoomsDesk(bigTime,merchantId);
    }

    @Override
    public MerchantRoomParamsSetEntity selectByMartIdAndRoomIdAndRoomId(Long merchantId, Long roomId, long roomSetId, String format) {
        return baseDao.selectByMartIdAndRoomIdAndRoomId(merchantId,roomId,roomSetId,format);
    }


}