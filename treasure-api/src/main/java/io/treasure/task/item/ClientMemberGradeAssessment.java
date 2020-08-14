package io.treasure.task.item;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.treasure.dao.ChargeCashDao;
import io.treasure.dao.ClientUserDao;
import io.treasure.dao.MasterOrderDao;
import io.treasure.entity.*;
import io.treasure.service.ClientMemberGradeAssessmentService;
import io.treasure.task.TaskCommon;
import io.treasure.utils.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 客户评级定时任务
 */
@Component
public class ClientMemberGradeAssessment extends TaskCommon implements IClientMemberGradeAssessment{

        @Autowired
        private ClientMemberGradeAssessmentService clientMemberGradeAssessmentService;
        @Autowired(required = false)
        private ChargeCashDao chargeCashDao;
        @Autowired(required = false)
        private ClientUserDao clientUserDao;
        @Autowired(required = false)
        private MasterOrderDao masterOrderDao;

        //用户级别评定
        private int gradeAssessment(Long clientId,ClientMemberGradeAssessmentEntity ruleEntity){

                Integer baseLevel = ruleEntity.getBaseLevel();
                Integer twoStar = ruleEntity.getTwoStar();
                Integer threeStar = ruleEntity.getThreeStar();
                Integer fourStar = ruleEntity.getFourStar();
                Integer fiveStar = ruleEntity.getFiveStar();
                List<Integer> starLevel = new ArrayList<>();
                starLevel.add(fiveStar);
                starLevel.add(fourStar);
                starLevel.add(threeStar);
                starLevel.add(twoStar);
                starLevel.add(baseLevel);

                for(int i=0;i<starLevel.size();i++){
                        try {
                                int clientPoints = getClientPoints(clientId);
                                if (clientPoints>= starLevel.get(i)){
                                        return (5-i);
                                }
                        } catch (ParseException e) {
                                e.printStackTrace();
                        }
                }
                return 1;//最低一星
        }
        //每个月执行一次,当用户点数不足现有级别时会自动下降一级
        @Override
        public int adjustGradeById(Long clientId){

                ClientMemberGradeAssessmentEntity ruleEntity = clientMemberGradeAssessmentService.getRule();
                ClientUserEntity clientUserEntity = clientUserDao.selectById(clientId);
                if(ruleEntity == null || clientUserEntity == null)
                        return -1;
                Integer clientLevel = clientUserEntity.getLevel();
                if(clientLevel == null){
                        clientUserEntity.setLevel(1);
                        clientUserDao.updateById(clientUserEntity);
                        clientLevel = clientUserEntity.getLevel();
                }

                int level = gradeAssessment(clientId,ruleEntity);
                System.out.println(level);
                if(clientLevel>level){

                        //点数不足则会降级
                        if(clientLevel>1){
                                clientLevel--;
                                clientUserEntity.setLevel(clientLevel);
                                clientUserDao.updateById(clientUserEntity);
                        }

                }else if(clientLevel<level){

                        //点数达级，则升级
                        clientLevel = level;
                        clientUserEntity.setLevel(clientLevel);
                        clientUserDao.updateById(clientUserEntity);
                }

                return clientLevel;
        }

        /**
         * 用户评级时如果不能进行升级，则会进行降级一级处理
         */

        @Override
        public void updateGrade(int pageNum){
                lockedProcessLock();
                updateTaskCounter();  //更新执行程序计数器
                QueryWrapper<ClientUserEntity> queryWrapper0 = new QueryWrapper<>();
                queryWrapper0.eq("status",1);
                int uCount = clientUserDao.selectCount(null);
                int pages = (uCount + pageNum - 1)/pageNum;

                for(int i=0;i<pages;i++){
                        QueryWrapper<ClientUserEntity> queryWrapper = new QueryWrapper<>();
                        queryWrapper.orderByAsc("create_date");
                        queryWrapper.eq("status",1);

                        Page<ClientUserEntity> record = new Page<>(i,pageNum);
                        IPage<ClientUserEntity> sharingRecords = clientUserDao.selectPage(record, queryWrapper);
                        List<ClientUserEntity> records = sharingRecords.getRecords();

                        for(int j=0;j<records.size();j++){
                                System.out.println("current client id:"+records.get(j).getId());
                                adjustGradeById(records.get(j).getId());
                        }
                }

                freeProcessLock();
        }
        //消费或充值时升级
        @Override
        public int growUpGrade(Long clientId){

                lockedProcessLock();
                updateTaskCounter();  //更新执行程序计数器

                ClientMemberGradeAssessmentEntity ruleEntity = clientMemberGradeAssessmentService.getRule();
                ClientUserEntity clientUserEntity = clientUserDao.selectById(clientId);
                if(clientUserEntity == null || ruleEntity == null)
                        return -1;

                int clientLevel = clientUserEntity.getLevel()==null?1:clientUserEntity.getLevel();
                int startLevel = gradeAssessment(clientId,ruleEntity);
                if(clientLevel<startLevel){
                        clientUserEntity.setLevel(startLevel);
                        clientUserDao.updateById(clientUserEntity);
                        return startLevel;
                }

                freeProcessLock();
                return clientLevel;
        }
        //取得用户在时间段内应得的点数值
        @Override
        public int getClientPoints(Long userId) throws ParseException {
                ClientMemberGradeAssessmentEntity ruleEntity = clientMemberGradeAssessmentService.getRule();

                return getChargeCashSum(userId,ruleEntity.getAssessmentTime(),ruleEntity.getTimeRange())
                        + getChargePayMoneySum(userId,ruleEntity.getAssessmentTime(),ruleEntity.getTimeRange());
        }

        @Override
        public int getChargeCashSum(Long userId, Date assessmentTime,int timeRang) throws ParseException {
                List<Date> dates = TimeUtil.generatorTimeRangeParams(assessmentTime, timeRang);

                QueryWrapper<ChargeCashEntity> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("user_id",userId);
                queryWrapper.eq("status",2);
                queryWrapper.between("save_time",dates.get(0),dates.get(1));
                queryWrapper.select("sum(cash) as cash");
                queryWrapper.groupBy("user_id");

                ChargeCashEntity chargeCashEntity = chargeCashDao.selectOne(queryWrapper);
                int res = 0;
                if(chargeCashEntity != null){
                        BigDecimal bigDecimal = chargeCashEntity.getCash();
                        res = bigDecimal.intValue();
                }
                return res;
        }



        @Override
        public int getChargePayMoneySum(Long userId, Date assessmentTime,int timeRang) throws ParseException {
                List<Date> dates = TimeUtil.generatorTimeRangeParams(assessmentTime, timeRang);

                QueryWrapper<MasterOrderEntity> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("creator",userId);
                queryWrapper.eq("status",10);
                queryWrapper.between("eat_time",dates.get(0),dates.get(1));
                queryWrapper.gt("pay_money",0);
                queryWrapper.gt("merchant_proceeds",0);
                queryWrapper.select("sum(pay_money-pay_coins) as pay_money");
                queryWrapper.groupBy("creator");

                MasterOrderEntity masterOrderEntity = masterOrderDao.selectOne(queryWrapper);
                int res = 0;
                if(masterOrderEntity != null){
                        BigDecimal bigDecimal = masterOrderEntity.getPayMoney();
                        res = bigDecimal.intValue();
                }
                return res;
        }

        @Override
        public boolean isOnTime(){
                ClientMemberGradeAssessmentEntity ruleEntity = clientMemberGradeAssessmentService.getRule();
                if(ruleEntity== null)
                        return false;
                if(TimeUtil.isOnMothDay(ruleEntity.getAssessmentTime())){
                     if(TimeUtil.isOnTime(ruleEntity.getAssessmentTime(),30)){
                             return true;
                     }
                }
                return false;
        }


}
