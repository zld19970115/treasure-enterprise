package io.treasure.task.item;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.dao.MerchantRoomDao;
import io.treasure.dao.MerchantRoomParamsDao;
import io.treasure.dao.MerchantRoomParamsSetDao;
import io.treasure.entity.MerchantRoomEntity;
import io.treasure.entity.MerchantRoomParamsEntity;
import io.treasure.entity.MerchantRoomParamsSetEntity;
import io.treasure.task.TaskCommon;
import io.treasure.utils.TimeUtil;
import io.treasure.vo.BookRoomVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.*;

@Service
public class ReseverRoomRecord extends TaskCommon implements IReseverRoomRecord {

  private String startTime = "2020-01-01 00:00:00";
  private Integer reserverDaysLong = 7;//最多可预订7天的，0~6
    private String creator = "15303690053";

    @Autowired(required = false)
    private MerchantRoomDao merchantRoomDao;
    @Autowired(required = false)
    private MerchantRoomParamsDao merchantRoomParamsDao;
    @Autowired(required = false)
    private MerchantRoomParamsSetDao merchantRoomParamsSetDao;

  public boolean isOntime() throws ParseException {
      boolean onTime = TimeUtil.isOnTime(TimeUtil.simpleDateFormat.parse(startTime), 30);
      return onTime;
  }

  public void checkReserverRoomRecord() throws ParseException {
      lockedProcessLock();
      updateTaskCounter();
      //===============================
      //取得包房参数
      List<MerchantRoomParamsEntity> merchantRoomParams = getMerchantRoomParams();
      Date date = new Date();

      //取得包房列表
      List<MerchantRoomEntity> merchantRoomEntities = merchantRoomDao.selectEnableList();

      for(int i=0;i<merchantRoomEntities.size();i++){
          generateReserverRoomEmptyRecord(merchantRoomParams,date,merchantRoomEntities.get(i));
      }
      //======================================
      freeProcessLock();
  }


  public List<MerchantRoomParamsEntity> getMerchantRoomParams(){

      List<MerchantRoomParamsEntity> merchantRoomParamsEntities = merchantRoomParamsDao.selectList(null);
      return merchantRoomParamsEntities;
  }

  public List<BookRoomVo> merchantRoomParamsToList(List<MerchantRoomParamsEntity> entity, Integer dLong) throws ParseException {
      List<BookRoomVo> res = new ArrayList<>();

      Date date = new Date();

      for(int i=0;i<entity.size();i++){
          String content = entity.get(i).getContent();
          if(content.contains("-")){
              String[] tmp = content.split("-");

              String startTime = tmp[0];
              String stopTime = tmp[1];
              Date sDate = TimeUtil.formatHmToYmdHms(startTime,date,dLong);
              Date eDate = TimeUtil.formatHmToYmdHms(stopTime,date,dLong);

              res.add(new BookRoomVo().setSTime(sDate).setETime(eDate).setId(entity.get(i).getId()));
          }
      }
      return res;
  }

  //检查并生成7天的记录
  public void generateReserverRoomEmptyRecord(List<MerchantRoomParamsEntity> mrpEntities,Date date,MerchantRoomEntity merchantRoomEntity) throws ParseException {


      for(int i= 0;i<=reserverDaysLong;i++){
          List<BookRoomVo> bookRoomVos = merchantRoomParamsToList(mrpEntities, i);
          for(int j=0;j<bookRoomVos.size();j++){
            //检查是否存在，不存在则插入
            //998	1	1217000165390823426	1	1216602535724773377	0	1		2020-01-15 05:00:00	2020-01-15 06:20:36	2020-01-15 06:20:36	13911223366		手镯套餐
              MerchantRoomParamsSetEntity entity = new MerchantRoomParamsSetEntity();
              entity.setRoomParamsId(bookRoomVos.get(j).getId());//1,2,3,4
              entity.setRoomId(merchantRoomEntity.getId());
              entity.setRoomName(merchantRoomEntity.getName());
              entity.setMerchantId(merchantRoomEntity.getMerchantId());
              entity.setType(merchantRoomEntity.getType());
              entity.setState(0);       //未使用
              entity.setStatus(1);      //显示
              entity.setUseDate(bookRoomVos.get(j).getSTime());//开始时间
              entity.setCreator(Long.parseLong(creator));
              if(!isExist(entity,bookRoomVos.get(j),merchantRoomEntity.getId())){
                  merchantRoomParamsSetDao.insert(entity);
                  System.out.println("插入房间记录(i,j)"+i+","+j);
              }
          }
      }
  }

  public boolean isExist(MerchantRoomParamsSetEntity entity,BookRoomVo bookRoomVo,Long room_id){
      QueryWrapper<MerchantRoomParamsSetEntity> queryWrapper = new QueryWrapper<>();

      queryWrapper.eq("room_params_id",bookRoomVo.getId());
      queryWrapper.eq("room_id",room_id);
      queryWrapper.eq("use_date",bookRoomVo.getSTime());
      System.out.println("check(room_params_id,room_id,use_date)"+bookRoomVo.getId()+","+room_id+","+bookRoomVo.getSTime());
      List<MerchantRoomParamsSetEntity> merchantRoomParamsSetEntities = merchantRoomParamsSetDao.selectList(queryWrapper);
      System.out.println();
      if(merchantRoomParamsSetEntities.size()>0)
          return true;
      return false;
  }

}
