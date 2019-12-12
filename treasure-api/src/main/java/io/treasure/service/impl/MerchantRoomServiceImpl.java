package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;
import io.treasure.common.service.impl.CrudServiceImpl;
import io.treasure.common.utils.DateUtils;
import io.treasure.dao.MerchantRoomDao;
import io.treasure.dto.*;
import io.treasure.entity.MasterOrderEntity;
import io.treasure.entity.MerchantRoomEntity;
import io.treasure.service.ClientUserService;
import io.treasure.service.MasterOrderService;
import io.treasure.service.MerchantRoomService;
import io.treasure.service.MerchantService;
import io.treasure.utils.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 包房或者桌表
 *
 * @author Super 63600679@qq.com
 * @since 1.0.0 2019-07-30
 */
@Service
public class MerchantRoomServiceImpl extends CrudServiceImpl<MerchantRoomDao, MerchantRoomEntity, MerchantRoomDTO> implements MerchantRoomService {
    @Autowired
    private ClientUserService clientUserService;

    @Autowired
    private MasterOrderService masterOrderService;

    @Autowired
    private MerchantRoomService merchantRoomService;
    //商户
    @Autowired
    private MerchantService merchantService;

    @Override
    public QueryWrapper<MerchantRoomEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");
        //状态
        String status=(String)params.get("status");
        //名称
        String name=(String)params.get("name");
        //商户
        String merchantId=(String)params.get("merchantId");
        //日期state
        String date=(String)params.get("date");
        //使用状态
        String state=(String)params.get("state");
        //类型
        String type=(String)params.get("type");
        QueryWrapper<MerchantRoomEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);
        wrapper.eq(StringUtils.isNotBlank(status),"status",status);
        wrapper.like(StringUtils.isNotBlank(name),"name",name);
        wrapper.eq(StringUtils.isNotBlank(merchantId),"merchant_id",merchantId);
        wrapper.eq(StringUtils.isNotBlank(type),"type",type);
        wrapper.eq(StringUtils.isNotBlank(date),"use_date",date);
        wrapper.eq(StringUtils.isNotBlank(state),"state",state);
        return wrapper;
    }

    /**
     * 删除
     * @param id
     * @param status
     */
    @Override
    public void remove(long id, int status) {
        baseDao.updateStatusById(id,status);
    }

    /**
     * 根据名称和商户id查询
     * @param name
     * @param merchantId
     * @return
     */
    @Override
    public List getByNameAndMerchantId(String name, long merchantId,int type) {
        return baseDao.getByNameAndMerchantId(name,merchantId,type);
    }

    /**
     * 根据商户查询包房信息
     * @param merchantId
     * @return
     */
    @Override
    public List getByMerchantId(long merchantId,int status) {
        return baseDao.getByMerchantId(merchantId,status);
    }

    @Override
    public PageData<MerchantRoomDTO> listPage(Map<String, Object> params) {
        IPage<MerchantRoomEntity> pages=getPage(params,Constant.CREATE_DATE,false);
        String merchantId=(String)params.get("merchantId");
        if (StringUtils.isNotBlank(merchantId) && StringUtils.isNotEmpty(merchantId)) {
            String[] str = merchantId.split(",");
            params.put("merchantIdStr", str);
        }else{
            params.put("merchantId",null);
        }
        List<MerchantRoomDTO> list=baseDao.listPage(params);
        return getPageData(list,pages.getTotal(), MerchantRoomDTO.class);
    }

    @Override
    public PageData<MerchantRoomParamsSetDTO> selectRoomAlreadyPage(Map<String, Object> params) {
        IPage<MerchantRoomEntity> pages=getPage(params,Constant.CREATE_DATE,false);
        String merchantId=(String)params.get("merchantId");
        if (StringUtils.isNotBlank(merchantId) && StringUtils.isNotEmpty(merchantId)) {
            String[] str = merchantId.split(",");
            params.put("merchantIdStr", str);
        }else{
            params.put("merchantId",null);
        }
        String useDate=(String)params.get("date");
        if(useDate!=null){
            Date date= DateUtils.parse(useDate,"yyyy-MM-dd");
            params.put("date",DateUtils.format(date,"yyyy-MM-dd"));
        }

        List<MerchantRoomParamsSetDTO> list=baseDao.selectRoomAlreadyPage(params);
        for (MerchantRoomParamsSetDTO s:list) {
            MasterOrderEntity orderByReservationId = masterOrderService.getOrderByReservationId(s.getId());
            if(orderByReservationId!=null){
                ClientUserDTO clientUserDTO = clientUserService.get(orderByReservationId.getCreator());
                s.setClientUserDTO(clientUserDTO);
            }
        }
        return getPageData(list,pages.getTotal(), MerchantRoomParamsSetDTO.class);
    }

    @Override
    public PageData<MerchantRoomParamsSetDTO> selectRoomByTime(Map<String, Object> params) {
        IPage<MerchantRoomEntity> pages=getPage(params,Constant.CREATE_DATE,false);
        String merchantId=(String)params.get("merchantId");
        if (StringUtils.isNotBlank(merchantId) && StringUtils.isNotEmpty(merchantId)) {
            String[] str = merchantId.split(",");
            params.put("merchantIdStr", str);
        }else{
            params.put("merchantId",null);
        }
        String Date=(String)params.get("date");

        Date date= DateUtils.parse(Date,"yyyy-MM-dd");
        params.put("date",DateUtils.format(date,"yyyy-MM-dd"));
        // 截取当前时间时分秒
        int strDateH = Integer.parseInt(Date.substring(11, 13));
        int strDateM = Integer.parseInt(Date.substring(14, 16));
        int strDateS = Integer.parseInt(Date.substring(17, 19));
        String  strDateBegin = "05:00:00";
        String  strDateEnd = "09:59:00";
        // 截取开始时间时分秒
        int strDateBeginH = Integer.parseInt(strDateBegin.substring(0, 2));
        int strDateBeginM = Integer.parseInt(strDateBegin.substring(3, 5));
        int strDateBeginS = Integer.parseInt(strDateBegin.substring(6, 8));
        // 截取结束时间时分秒
        int strDateEndH = Integer.parseInt(strDateEnd.substring(0, 2));
        int strDateEndM = Integer.parseInt(strDateEnd.substring(3, 5));
        int strDateEndS = Integer.parseInt(strDateEnd.substring(6, 8));

        long roomParmesId = 0;
        // 当前时间小时数在开始时间和结束时间小时数之间
        if (strDateH > strDateBeginH && strDateH < strDateEndH) {
            roomParmesId = 1;
            // 当前时间小时数等于开始时间小时数，分钟数在开始和结束之间
        } else if (strDateH == strDateBeginH && strDateM >= strDateBeginM
                && strDateM <= strDateEndM) {
            roomParmesId = 1;
            // 当前时间小时数等于开始时间小时数，分钟数等于开始时间分钟数，秒数在开始和结束之间
        } else if (strDateH == strDateBeginH && strDateM == strDateBeginM
                && strDateS >= strDateBeginS && strDateS <= strDateEndS) {
            roomParmesId = 1;
        }
        // 当前时间小时数大等于开始时间小时数，等于结束时间小时数，分钟数小等于结束时间分钟数
        else if (strDateH >= strDateBeginH && strDateH == strDateEndH
                && strDateM <= strDateEndM) {
            roomParmesId = 1;
            // 当前时间小时数大等于开始时间小时数，等于结束时间小时数，分钟数等于结束时间分钟数，秒数小等于结束时间秒数
        } else if (strDateH >= strDateBeginH && strDateH == strDateEndH
                && strDateM == strDateEndM && strDateS <= strDateEndS) {
            roomParmesId = 1;
        }

        String  strDateBegin2 = "10:00:00";
        String  strDateEnd2 = "15:59:00";
        // 截取开始时间时分秒
        int strDateBeginH2 = Integer.parseInt(strDateBegin2.substring(0, 2));
        int strDateBeginM2 = Integer.parseInt(strDateBegin2.substring(3, 5));
        int strDateBeginS2 = Integer.parseInt(strDateBegin2.substring(6, 8));
        // 截取结束时间时分秒
        int strDateEndH2 = Integer.parseInt(strDateEnd2.substring(0, 2));
        int strDateEndM2 = Integer.parseInt(strDateEnd2.substring(3, 5));
        int strDateEndS2 = Integer.parseInt(strDateEnd2.substring(6, 8));
        // 当前时间小时数在开始时间和结束时间小时数之间
        if (strDateH > strDateBeginH2 && strDateH < strDateEndH2) {
            roomParmesId = 2;
            // 当前时间小时数等于开始时间小时数，分钟数在开始和结束之间
        } else if (strDateH == strDateBeginH2 && strDateM >= strDateBeginM2
                && strDateM <= strDateEndM2) {
            roomParmesId = 2;
            // 当前时间小时数等于开始时间小时数，分钟数等于开始时间分钟数，秒数在开始和结束之间
        } else if (strDateH == strDateBeginH2 && strDateM == strDateBeginM2
                && strDateS >= strDateBeginS2 && strDateS <= strDateEndS2) {
            roomParmesId = 2;
        }
        // 当前时间小时数大等于开始时间小时数，等于结束时间小时数，分钟数小等于结束时间分钟数
        else if (strDateH >= strDateBeginH2 && strDateH == strDateEndH2
                && strDateM <= strDateEndM2) {
            roomParmesId = 2;
            // 当前时间小时数大等于开始时间小时数，等于结束时间小时数，分钟数等于结束时间分钟数，秒数小等于结束时间秒数
        } else if (strDateH >= strDateBeginH2 && strDateH == strDateEndH2
                && strDateM == strDateEndM2 && strDateS <= strDateEndS2) {
            roomParmesId = 2;
        }

        String  strDateBegin3 = "15:00:00";
        String  strDateEnd3 = "21:59:00";
        // 截取开始时间时分秒
        int strDateBeginH3 = Integer.parseInt(strDateBegin3.substring(0, 2));
        int strDateBeginM3 = Integer.parseInt(strDateBegin3.substring(3, 5));
        int strDateBeginS3 = Integer.parseInt(strDateBegin3.substring(6, 8));
        // 截取结束时间时分秒
        int strDateEndH3 = Integer.parseInt(strDateEnd3.substring(0, 2));
        int strDateEndM3 = Integer.parseInt(strDateEnd3.substring(3, 5));
        int strDateEndS3 = Integer.parseInt(strDateEnd3.substring(6, 8));
        // 当前时间小时数在开始时间和结束时间小时数之间
        if (strDateH > strDateBeginH3 && strDateH < strDateEndH3) {
            roomParmesId = 3;
            // 当前时间小时数等于开始时间小时数，分钟数在开始和结束之间
        } else if (strDateH == strDateBeginH3 && strDateM >= strDateBeginM3
                && strDateM <= strDateEndM3) {
            roomParmesId = 3;
            // 当前时间小时数等于开始时间小时数，分钟数等于开始时间分钟数，秒数在开始和结束之间
        } else if (strDateH == strDateBeginH3 && strDateM == strDateBeginM3
                && strDateS >= strDateBeginS3 && strDateS <= strDateEndS3) {
            roomParmesId = 3;
        }
        // 当前时间小时数大等于开始时间小时数，等于结束时间小时数，分钟数小等于结束时间分钟数
        else if (strDateH >= strDateBeginH3 && strDateH == strDateEndH3
                && strDateM <= strDateEndM3) {
            roomParmesId = 3;
            // 当前时间小时数大等于开始时间小时数，等于结束时间小时数，分钟数等于结束时间分钟数，秒数小等于结束时间秒数
        } else if (strDateH >= strDateBeginH3 && strDateH == strDateEndH3
                && strDateM == strDateEndM3 && strDateS <= strDateEndS3) {
            roomParmesId = 3;
        }
        String  strDateBegin4 = "22:00:00";
        String  strDateEnd4 = "23:59:00";
        // 截取开始时间时分秒
        int strDateBeginH4 = Integer.parseInt(strDateBegin4.substring(0, 2));
        int strDateBeginM4 = Integer.parseInt(strDateBegin4.substring(3, 5));
        int strDateBeginS4 = Integer.parseInt(strDateBegin4.substring(6, 8));
        // 截取结束时间时分秒
        int strDateEndH4 = Integer.parseInt(strDateEnd4.substring(0, 2));
        int strDateEndM4 = Integer.parseInt(strDateEnd4.substring(3, 5));
        int strDateEndS4 = Integer.parseInt(strDateEnd4.substring(6, 8));
        // 当前时间小时数在开始时间和结束时间小时数之间
        if (strDateH > strDateBeginH4 && strDateH < strDateEndH4) {
            roomParmesId = 4;
            // 当前时间小时数等于开始时间小时数，分钟数在开始和结束之间
        } else if (strDateH == strDateBeginH4 && strDateM >= strDateBeginM4
                && strDateM <= strDateEndM4) {
            roomParmesId = 4;
            // 当前时间小时数等于开始时间小时数，分钟数等于开始时间分钟数，秒数在开始和结束之间
        } else if (strDateH == strDateBeginH4 && strDateM == strDateBeginM4
                && strDateS >= strDateBeginS4 && strDateS <= strDateEndS4) {
            roomParmesId = 4;
        }
        // 当前时间小时数大等于开始时间小时数，等于结束时间小时数，分钟数小等于结束时间分钟数
        else if (strDateH >= strDateBeginH4 && strDateH == strDateEndH4
                && strDateM <= strDateEndM4) {
            roomParmesId = 4;
            // 当前时间小时数大等于开始时间小时数，等于结束时间小时数，分钟数等于结束时间分钟数，秒数小等于结束时间秒数
        } else if (strDateH >= strDateBeginH4 && strDateH == strDateEndH4
                && strDateM == strDateEndM4 && strDateS <= strDateEndS4) {
            roomParmesId = 4;
        }
        String  strDateBegin4s = "00:00:00";
        String  strDateEnd4s = "04:59:00";
        // 截取开始时间时分秒
        int strDateBeginH4s = Integer.parseInt(strDateBegin4s.substring(0, 2));
        int strDateBeginM4s = Integer.parseInt(strDateBegin4s.substring(3, 5));
        int strDateBeginS4s = Integer.parseInt(strDateBegin4s.substring(6, 8));
        // 截取结束时间时分秒
        int strDateEndH4s = Integer.parseInt(strDateEnd4s.substring(0, 2));
        int strDateEndM4s = Integer.parseInt(strDateEnd4s.substring(3, 5));
        int strDateEndS4s = Integer.parseInt(strDateEnd4s.substring(6, 8));
        // 当前时间小时数在开始时间和结束时间小时数之间
        if (strDateH > strDateBeginH4s && strDateH < strDateEndH4s) {
            roomParmesId = 4;
            // 当前时间小时数等于开始时间小时数，分钟数在开始和结束之间
        } else if (strDateH == strDateBeginH4s && strDateM >= strDateBeginM4s
                && strDateM <= strDateEndM4) {
            roomParmesId = 4;
            // 当前时间小时数等于开始时间小时数，分钟数等于开始时间分钟数，秒数在开始和结束之间
        } else if (strDateH == strDateBeginH4s && strDateM == strDateBeginM4s
                && strDateS >= strDateBeginS4s && strDateS <= strDateEndS4s) {
            roomParmesId = 4;
        }
        // 当前时间小时数大等于开始时间小时数，等于结束时间小时数，分钟数小等于结束时间分钟数
        else if (strDateH >= strDateBeginH4s && strDateH == strDateEndH4s
                && strDateM <= strDateEndM4s) {
            roomParmesId = 4;
            // 当前时间小时数大等于开始时间小时数，等于结束时间小时数，分钟数等于结束时间分钟数，秒数小等于结束时间秒数
        } else if (strDateH >= strDateBeginH4s && strDateH == strDateEndH4s
                && strDateM == strDateEndM4s && strDateS <= strDateEndS4s) {
            roomParmesId = 4;
        }
         params.put("roomParmesId",roomParmesId);
        List<MerchantRoomParamsSetDTO> list= baseDao.selectRoomByTime(params);
        for (MerchantRoomParamsSetDTO s:list) {
            MerchantRoomDTO merchantRoomDTO = merchantRoomService.get(s.getRoomId());
            MerchantDTO merchantDTO=merchantService.get(merchantRoomDTO.getMerchantId());
            s.setIcon(merchantRoomDTO.getIcon());
            s.setName(merchantRoomDTO.getName());
            s.setNumlow(merchantRoomDTO.getNumLow());
            s.setNumhigh(merchantRoomDTO.getNumHigh());
            if(null!=merchantDTO){
                s.setMerchantName(merchantDTO.getName());
            }

        }
        return getPageData(list,pages.getTotal(), MerchantRoomParamsSetDTO.class);
    }

    @Override
    public List selectRoomByTimeVis(Map<String, Object> params) {
        String merchantId=(String)params.get("merchantId");
        if (StringUtils.isNotBlank(merchantId) && StringUtils.isNotEmpty(merchantId)) {
            String[] str = merchantId.split(",");
            params.put("merchantIdStr", str);
        }else{
            params.put("merchantId",null);
        }
        String useDate=(String)params.get("date");
        if(useDate!=null){
            Date date= DateUtils.parse(useDate,"yyyy-MM-dd");
            params.put("date",DateUtils.format(date,"yyyy-MM-dd"));
        }
        //查询所有店铺信息
        List<MerchantDTO> merchantList=baseDao.selectMerchantAll();
        //包房参数
        List<MerchantRoomParamsDTO> roomList=baseDao.selectRoomParam();
        //预约包房
        List<MerchantRoomParamsSetDTO> list= baseDao.selectRoomByTimeVis(params);
        List result=new ArrayList();
        for (MerchantDTO s:merchantList) {
            long mId=s.getId();
            List map=new ArrayList();
            List data=new ArrayList();
            for(MerchantRoomParamsSetDTO rooms:list){
                long mIds=rooms.getMerchantId();
                if(mId==mIds){
                    data.add(rooms);
                }
            }
            map.add(s.getName());
            map.add(data);
            map.add(roomList);
            //data.put(s.getName(),data);
            result.add(map);
        }
        return result;
    }

    @Override
    public PageData<MerchantRoomParamsSetDTO> selectRoomDate(Map<String,Object> params) {
        IPage<MerchantRoomEntity> pages=getPage(params,Constant.CREATE_DATE,false);
        String merchantId=(String)params.get("merchantId");
        if (StringUtils.isNotBlank(merchantId) && StringUtils.isNotEmpty(merchantId)) {
            String[] str = merchantId.split(",");
            params.put("merchantIdStr", str);
        }else{
            params.put("merchantId",null);
        }
        List<MerchantRoomParamsSetDTO> list=baseDao.selectRoomDate(params);
        for (MerchantRoomParamsSetDTO merchantRoomParamsSetDTO : list) {
            Date useDate = merchantRoomParamsSetDTO.getUseDate();
            if(useDate!=null){
                params.put("date",DateUtils.format(useDate,"yyyy-MM-dd"));
                params.remove("limit");
                params.remove("page");
            }
            List<MerchantRoomParamsSetDTO> merchantRoomParamsSetDTOS = baseDao.selectByDateAndMartId(params);//已使用总数
            List<MerchantRoomParamsSetDTO> merchantRoomParamsSetDTOS1 = baseDao.selectByDateAndMartId2(params);//未使用总数
            merchantRoomParamsSetDTO.setAllaleady(merchantRoomParamsSetDTOS.size());
            merchantRoomParamsSetDTO.setAllNOT(merchantRoomParamsSetDTOS1.size());
        }
        return getPageData(list,pages.getTotal(), MerchantRoomParamsSetDTO.class);
    }

    @Override
    public MerchantRoomEntity getmerchantroom(long merchantId) {
        return baseDao.getmerchantroom(merchantId);
    }

    @Override
    public Integer selectCountRoom(long merhcnatId) {
        return baseDao.selectCountRoom(merhcnatId);
    }

    @Override
    public Integer selectCountDesk(long merhcnatId) {
        return baseDao.selectCountDesk(merhcnatId);
    }
}