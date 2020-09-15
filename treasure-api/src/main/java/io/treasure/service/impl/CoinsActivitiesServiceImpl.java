package io.treasure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import io.treasure.common.utils.Result;
import io.treasure.dao.CoinsActivitiesDao;
import io.treasure.dao.MulitCouponBoundleDao;
import io.treasure.enm.ECoinsActivities;
import io.treasure.enm.ESharingRewardGoods;
import io.treasure.entity.CoinsActivitiesEntity;
import io.treasure.entity.MulitCouponBoundleEntity;
import io.treasure.service.CoinsActivitiesService;
import io.treasure.utils.SharingActivityRandomUtil;
import io.treasure.utils.TimeUtil;
import io.treasure.vo.CoinsActivityVo;
import io.treasure.vo.CounterDownVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static io.treasure.enm.ECoinsActivities.ExpireTimeUnit.*;
import static io.treasure.enm.ECoinsActivities.NewUserWinAwards.FOCRE_NEW_USER_WIN_AWARDS;
import static io.treasure.enm.ECoinsActivities.TimeMode.EVERY_DAY_OPENING;
import static java.time.LocalDate.now;

@Service
public class CoinsActivitiesServiceImpl implements CoinsActivitiesService {

    @Autowired(required = false)
    private CoinsActivitiesDao coinsActivitiesDao;
    @Autowired(required = false)
    private MulitCouponBoundleDao mulitCouponBoundleDao;

    private static List<Double> jackpotList = new ArrayList<>();

    /**
     * @param id 1系统默认参数 2本次活动(2020-0915这次)当前
     * @param defaultParams
     * @return
     */
    public CoinsActivitiesEntity getCoinsActivityById(Long id,boolean defaultParams){
        if(id == null){
            if(defaultParams == true){
                id = 1L;
            }else{
                id = 2L;
            }
        }
        return coinsActivitiesDao.selectById(id);
    }

    /**
     * 取得默认的
     * @return
     */
    public CoinsActivitiesEntity getDefaultParams(){
        return getCoinsActivityById(null,true);
    }

    /**
     * 对用户设置的实体进行校验和初始化、对数据库获取的实体进行校验和初始化
     * @param coinsActivitiesEntity
     * @return
     */
    public CoinsActivitiesEntity initatorEntityViaDefault(CoinsActivitiesEntity coinsActivitiesEntity){
        CoinsActivitiesEntity defaultEntity = getDefaultParams();

        if(coinsActivitiesEntity.getType()==null)
            coinsActivitiesEntity.setType(defaultEntity.getType());//宝币
        if(coinsActivitiesEntity.getStatus() == null)
            coinsActivitiesEntity.setStatus(defaultEntity.getStatus());
        if(coinsActivitiesEntity.getMode() == null)
            coinsActivitiesEntity.setMode(defaultEntity.getMode());
        //首段参数
        if(coinsActivitiesEntity.getHeadPersonNum()==null)
            coinsActivitiesEntity.setHeadPersonNum(defaultEntity.getHeadPersonNum());
        if(coinsActivitiesEntity.getFirstPrizeNum()==null)
            coinsActivitiesEntity.setFirstPrizeNum(defaultEntity.getFirstPrizeNum());
        if(coinsActivitiesEntity.getFirstPrizePosS()==null)
            coinsActivitiesEntity.setFirstPrizePosS(defaultEntity.getFirstPrizePosS());
        if(coinsActivitiesEntity.getFirstPrizePosE()==null)
            coinsActivitiesEntity.setFirstPrizePosE(defaultEntity.getFirstPrizePosE());

        //主体参数
        if(coinsActivitiesEntity.getBodySegmentNum() ==null)
            coinsActivitiesEntity.setBodySegmentNum(defaultEntity.getBodySegmentNum());
        if(coinsActivitiesEntity.getBodyPrizePosS() == null)
            coinsActivitiesEntity.setBodyPrizePosS(defaultEntity.getBodyPrizePosS());
        if(coinsActivitiesEntity.getBodyPrizePosE() == null)
            //============================================================================================================
        //基本参数
        if(coinsActivitiesEntity.getPrizeMaxmum() == null)//头奖高限
            ;
        if(coinsActivitiesEntity.getPrizeMinmun() == null)//头奖低限
            ;
        if(coinsActivitiesEntity.getCommonWinMaxmum()==null)//常规范围
            ;
        if(coinsActivitiesEntity.getCommonWinMinmum() == null)//常规范围
            ;
        if(coinsActivitiesEntity.getNewUserWinAwards()==null)
            coinsActivitiesEntity.setNewUserWinAwards(FOCRE_NEW_USER_WIN_AWARDS.getCode());
        if(coinsActivitiesEntity.getPersonalCoinsLimit() == null)


        if(coinsActivitiesEntity.getDrawTimes()==null){
            coinsActivitiesEntity.setDrawTimes(1);
        }else if(coinsActivitiesEntity.getDrawTimes() <=0){
            coinsActivitiesEntity.setDrawTimes(1);
        }
        if(coinsActivitiesEntity.getDrawDays() == null){
            coinsActivitiesEntity.setDrawDays(1);
        }else if(coinsActivitiesEntity.getDrawDays() <= 0){
            coinsActivitiesEntity.setDrawDays(1);
        }

        if(coinsActivitiesEntity.getRealyJackpot() == null){
            coinsActivitiesEntity.setRealyJackpot(0);
        }
        if(coinsActivitiesEntity.getVisualJackpot() == null){
            coinsActivitiesEntity.setVisualJackpot(0);
        }

        if(coinsActivitiesEntity.getExpectedPersonNum() == null){
            //此值需要大于或等于前面的两个数值的和
        }else if(coinsActivitiesEntity.getExpectedPersonNum()<2){//===================================================
        }
        if(coinsActivitiesEntity.getExpireTimeUnit() == null)
            coinsActivitiesEntity.setExpireTimeUnit(UNIT_MONTHS.getCode());

        if(coinsActivitiesEntity.getExpireTimeLong()==null){
            Integer expireTimeUnit = coinsActivitiesEntity.getExpireTimeUnit();
            ECoinsActivities.ExpireTimeUnit eTimeUnit = ECoinsActivities.ExpireTimeUnit.fromCode(expireTimeUnit);
            switch (eTimeUnit){
                case UNIT_MONTHS:
                    coinsActivitiesEntity.setExpireTimeLong(1);
                    break;
                case UNIT_WEEKS:
                    coinsActivitiesEntity.setExpireTimeLong(4);
                    break;
                default:
                    coinsActivitiesEntity.setExpireTimeLong(30);
                    break;
            }
        }
        if(coinsActivitiesEntity.getTimeMode() == null){
            coinsActivitiesEntity.setTimeMode(EVERY_DAY_OPENING.getCode());
        }

        if(coinsActivitiesEntity.getOpeningPmt()==null){
            coinsActivitiesEntity.setOpeningPmt(new Date());
        }
        if(coinsActivitiesEntity.getClosingPmt() == null){
        }

        return coinsActivitiesEntity;
    }

    /**
     * @param coinsActivitiesEntity
     * @return  过期日期，null表示参数错误
     * @throws ParseException
     */
    public Date generateExpireTimeForActivities(CoinsActivitiesEntity coinsActivitiesEntity) throws ParseException {

        Integer expireTimeLong = coinsActivitiesEntity.getExpireTimeLong();
        Integer expireTimeUnit = coinsActivitiesEntity.getExpireTimeUnit();
        ESharingRewardGoods.ActityValidityUnit eTimeUnit = ESharingRewardGoods.ActityValidityUnit.fromCode(expireTimeUnit);

        if(eTimeUnit != null)
            TimeUtil.calculateAddDate(expireTimeLong, eTimeUnit);
        return null;
    }
//======================================以上基本活动处理=======================================================
    /**
     * @param coinsActivitiesEntity
     * @return  1活动已结束，2活动进行中，3活动马上开始,4活动已过期,5活动参数错误
     */
    public int isOnTime(CoinsActivitiesEntity coinsActivitiesEntity) throws ParseException {

        Integer timeMode = coinsActivitiesEntity.getTimeMode();
        Date openingPmt = coinsActivitiesEntity.getOpeningPmt();
        Date closingPmt = coinsActivitiesEntity.getClosingPmt();

        if(timeMode == null || openingPmt == null || closingPmt == null)
            return 5;

        boolean betweenTime = TimeUtil.isBetweenTime(openingPmt, closingPmt);//是否在活动范围内
        boolean betweenDate = TimeUtil.isBetweenDate(openingPmt, closingPmt);
        if(betweenDate){
            if(betweenTime){
                return 2;
            }else{
                //当前时间小于当前的活动开始时间
                Date date = TimeUtil.contentTimeAndDate(openingPmt, true);
                long dateLong = date.getTime();
                long nowLong = new Date().getTime();
                if(nowLong < dateLong){
                    return 3;
                }else{
                    return 1;
                }

            }
        }else{
            return 4;
        }
    }

    /**
     * 统计活动送出的宝币数量
     * @return
     * @throws ParseException
     */
    public BigDecimal getSumForCoinsActivityToday(CoinsActivitiesEntity coinsActivity) throws ParseException {

        Date openingPmt = coinsActivity.getOpeningPmt();
        Date closingPmt = coinsActivity.getClosingPmt();
        Date startConvert = TimeUtil.getCurrentDateAndTime(openingPmt);
        Date endingConvert = TimeUtil.getCurrentDateAndTime(closingPmt);

        QueryWrapper<MulitCouponBoundleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type",1);
        queryWrapper.ge("get_method",3);
        queryWrapper.ge("got_pmt",startConvert);
        queryWrapper.le("got_pmt",endingConvert);
        queryWrapper.select("sum(coupon_value) as coupon_value");
        MulitCouponBoundleEntity mulitCouponBoundleEntity = mulitCouponBoundleDao.selectOne(queryWrapper);
        if(mulitCouponBoundleEntity == null)
            return new BigDecimal("0");
        BigDecimal couponValue = mulitCouponBoundleEntity.getCouponValue();
        return couponValue;
    }

    /**
     * 检查奖池是否还有钱
     * @return 0表示已经抢完了
     */
    public BigDecimal jackpotRemaining(CoinsActivitiesEntity coinsActivity) throws ParseException {

        if(coinsActivity.getRealyJackpot() == null)
            return new BigDecimal("0");//数据库的值发生错误

        Integer realyJackpot = coinsActivity.getRealyJackpot();
        BigDecimal spent = getSumForCoinsActivityToday(coinsActivity);
        BigDecimal result = new BigDecimal(coinsActivity.getRealyJackpot()+"");
        if(realyJackpot > spent.doubleValue()){
            return result.subtract(spent);
        }
        return new BigDecimal("0");
    }

    /**
     * 得到当日活动的总记录数量
     * @param coinsActivity
     * @return
     * @throws ParseException
     */
    public int getRecordsNumToday(CoinsActivitiesEntity coinsActivity) throws ParseException {

        Date openingPmt = coinsActivity.getOpeningPmt();
        //Date date = TimeUtil.calculateSubDate(null, openingPmt, 1);
        Date date = TimeUtil.simpleDateFormat.parse(TimeUtil.sdfYmd.format(new Date())+" "+"00:00:00");

        QueryWrapper<MulitCouponBoundleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type",1);
        queryWrapper.eq("get_method",3);
        queryWrapper.ge("got_pmt",date);

        Integer res = mulitCouponBoundleDao.selectCount(queryWrapper);
        if(res==null)
            res = 0;
        return res;
    }

    /**
     * 检查当前活动产生的记录数量及相关信息
     * @param coinsActivity
     * @return
     */
    public boolean shouldBeFirstPrize(CoinsActivitiesEntity coinsActivity,Long clientId) throws ParseException {
        int recordsNumToday = getRecordsNumToday(coinsActivity);
        int firstPersonNum = coinsActivity.getHeadPersonNum();
        if(recordsNumToday<firstPersonNum){
            return checkSegmentSubStatus(coinsActivity,true,recordsNumToday,firstPersonNum,clientId);
        }else{
            Integer bodySegmentNum = coinsActivity.getBodySegmentNum();
            int target = (recordsNumToday - firstPersonNum)== 0?0:(recordsNumToday - firstPersonNum)%bodySegmentNum;
            return checkSegmentSubStatus(coinsActivity,false,target,firstPersonNum,clientId);
        }
    }
    /**
     * 子程序
     * @param maxmum  最大奖励的个数是否还可以出大奖判断
     * @param records  自获取日期反向截取记录的数量
     * @return
     */
    private boolean checkFirstPrizeInSegmentSubStatus(CoinsActivitiesEntity coinsActivity, Integer maxmum, int records, Long clientId,int startPos,int stopPos) throws ParseException {
        maxmum = maxmum == null?1:maxmum;

        Integer newUserWinAwards = coinsActivity.getNewUserWinAwards();
        boolean b = firstDrawCoinsActivities(clientId);
        if(b && newUserWinAwards == FOCRE_NEW_USER_WIN_AWARDS.getCode()){
            return true;
        }
        Integer prizeMinmun = coinsActivity.getPrizeMinmun();
        QueryWrapper<MulitCouponBoundleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type",1);
        queryWrapper.eq("get_method",3);
        queryWrapper.orderByDesc("got_pmt");
        queryWrapper.last("limit 0,"+records);
        List<MulitCouponBoundleEntity> mulitCouponBoundleEntities = mulitCouponBoundleDao.selectList(queryWrapper);
        int realyFirstPrizeNum = 0;
        for(int i=0;i<mulitCouponBoundleEntities.size();i++){

            if((mulitCouponBoundleEntities.get(i).getCouponValue()).doubleValue()>prizeMinmun)
                realyFirstPrizeNum ++;
        }

        if(realyFirstPrizeNum < maxmum){
            //有可能产生大奖
            int currentPos = records+1;
           if(currentPos >= startPos && currentPos <= stopPos){
               if(currentPos == stopPos){
                   return true;
               }else{
                   if(startPos == stopPos){
                       return true;
                   }
                   //!==============================================================================
                   //
                   //
                   //

                   int tmpPos = SharingActivityRandomUtil.getRandomPos(startPos,stopPos);
                   System.out.println(tmpPos);
                   if(tmpPos==currentPos){
                       return true;
                   }else{
                       return false;
                   }

               }
           }else return false;
        }else{
           return false;
        }
    }

    /**
     * 子程序
     * @param coinsActivity
     * @param isHeaderSegment
     * @param pos
     * @param sumRecords
     * @return
     */
    private boolean checkSegmentSubStatus(CoinsActivitiesEntity coinsActivity, boolean isHeaderSegment, int pos, int sumRecords, Long clientId) throws ParseException {
        //区别:奖励的位置和数量不同

        if(!isHeaderSegment){

            Integer prizePosS = coinsActivity.getBodyPrizePosS();
            Integer prizePosE = coinsActivity.getBodyPrizePosE();

            if(checkFirstPrizeInSegmentSubStatus(coinsActivity,null,pos,clientId,prizePosS,prizePosE)){
                return true;
            }else{
               return false;
            }

        }else{//header部分
            Integer firstPrizeNum = coinsActivity.getFirstPrizeNum();
            Integer prizePosS = coinsActivity.getFirstPrizePosS();
            Integer prizePosE = coinsActivity.getFirstPrizePosE();

            if(checkFirstPrizeInSegmentSubStatus(coinsActivity,firstPrizeNum,pos,clientId,prizePosS,prizePosE)){
                return true;
            }else{
                return false;
            }
        }
    }

//==========================================用户相关====================================================

    /**
     * 检查用户--是否第一次参与本活动
     * @param clientId
     * @return
     * @throws ParseException
     */
    public boolean firstDrawCoinsActivities(Long clientId) throws ParseException {

        QueryWrapper<MulitCouponBoundleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("owner_id",clientId);
        queryWrapper.eq("type",1);
        queryWrapper.eq("get_method",3);
        Integer res = mulitCouponBoundleDao.selectCount(queryWrapper);
        if(res==null)
            res = 0;
        if(res <= 0)
            return true;
        return false;
    }
    /**
     * 总体范围检查次数是否超限
     * @param coinsActivity
     * @param clientId
     * @return true可以参加，false超限
     */
    public boolean checkTimesTimeRange(CoinsActivitiesEntity coinsActivity,Long clientId) throws ParseException {

        Integer timeMode = coinsActivity.getTimeMode();
        Date openingPmt = coinsActivity.getOpeningPmt();

        Integer drawDays = coinsActivity.getDrawDays();
        Integer drawTimes = coinsActivity.getDrawTimes();

        QueryWrapper<MulitCouponBoundleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("owner_id",clientId);
        queryWrapper.eq("type",1);
        queryWrapper.eq("get_method",3);

        Date date = TimeUtil.calculateSubDate(new Date(), openingPmt, drawDays.longValue());
        System.out.println("校验时间"+date);
        queryWrapper.ge("got_pmt",date);

        Integer res = mulitCouponBoundleDao.selectCount(queryWrapper);

        if(res==null)
            res = 0;
        if(res >= drawTimes)
            return false;
        return true;
    }

    /**
     * 检查每天抢宝币的数量是否超限
     * @param coinsActivity
     * @param clientId
     * @return
     * @throws ParseException
     */
    public Boolean checkTimesEveryDay(CoinsActivitiesEntity coinsActivity,Long clientId) throws ParseException {

        Integer timeMode = coinsActivity.getTimeMode();
        Date openingPmt = coinsActivity.getOpeningPmt();

        Integer drawDays = coinsActivity.getDrawDays();
        Integer drawTimes = coinsActivity.getDrawTimes();
        if(drawDays>drawTimes)
            return false;
        int everyDayTimes = drawTimes/drawDays;//只取整不取余

        String today = TimeUtil.sdfYmd.format(new Date())+ " 00:00:00";
        Date target = TimeUtil.simpleDateFormat.parse(today);
        QueryWrapper<MulitCouponBoundleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("owner_id",clientId);
        queryWrapper.eq("type",1);
        queryWrapper.eq("get_method",3);
        queryWrapper.ge("got_pmt",target);

        Integer res = mulitCouponBoundleDao.selectCount(queryWrapper);
        if(res==null)
            res = 0;
        if(res >= everyDayTimes)
            return false;
        return true;
    }

    /**
     * 2检查用户====仅本活动获得的宝币的总值(仅仅未过期的宝币)
     * @param clientUser_id
     * @return
     */
    public BigDecimal getCurrentActivityEnableCoinsSumByClientId(Long clientUser_id,Long  coinsActivityId){

        QueryWrapper<MulitCouponBoundleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("owner_id",clientUser_id);
        queryWrapper.eq("type",1);
        queryWrapper.eq("use_status",0);
        queryWrapper.eq("get_method",3);
        queryWrapper.ge("expire_pmt",now());
        queryWrapper.select("sum(coupon_value - consume_value) as coupon_value");
        MulitCouponBoundleEntity mulitCouponBoundleEntity = mulitCouponBoundleDao.selectOne(queryWrapper);
        if(mulitCouponBoundleEntity == null)
            return new BigDecimal("0");
        return mulitCouponBoundleEntity.getCouponValue();
    }


    /**
     *  4修改用户====增加抢宝币记录
     * @param clientId
     * @param award
     * @param method
     * @param coinsActivity
     * @throws ParseException
     */
    public Double insertCoinsActivityRecordByClientId(CoinsActivitiesEntity coinsActivity,Long clientId,BigDecimal award,Integer method) throws ParseException {
        Integer personalCoinsLimit = coinsActivity.getPersonalCoinsLimit();
        if(personalCoinsLimit == null||personalCoinsLimit ==0)
            return 0d;

        MulitCouponBoundleEntity mulitCouponBoundleEntity = new MulitCouponBoundleEntity();
        mulitCouponBoundleEntity.setOwnerId(clientId);
        mulitCouponBoundleEntity.setType(1);
        mulitCouponBoundleEntity.setGetMethod(method);
        mulitCouponBoundleEntity.setUseStatus(0);

        BigDecimal clientActivityCoinsVolume = getCurrentActivityEnableCoinsSumByClientId(clientId,coinsActivity.getId());
        if(clientActivityCoinsVolume.doubleValue()>= personalCoinsLimit){
            return 0d;
        }
        BigDecimal sum = award.add(clientActivityCoinsVolume);
        if(sum.doubleValue() > personalCoinsLimit)
            sum = new BigDecimal(personalCoinsLimit+"").subtract(clientActivityCoinsVolume);
        mulitCouponBoundleEntity.setCouponValue(sum);
        mulitCouponBoundleEntity.setConsumeValue(new BigDecimal("0"));
        mulitCouponBoundleEntity.setGotPmt(new Date());
        Date date = generateExpireTimeForActivities(coinsActivity);
        mulitCouponBoundleEntity.setExpirePmt(date);
        try{
            mulitCouponBoundleDao.insert(mulitCouponBoundleEntity);
            return sum.doubleValue();
        }catch (Exception e){
            return 0d;
        }
    }
    //==========================================层级调用--业务===========================================================
    public Result coinActivityResult(int code,String msg,Object data){
        Result result = new Result();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    public Result clientDraw(Long clientId) throws ParseException {
        CoinsActivityVo coinsActivityVo = new CoinsActivityVo();

        CoinsActivitiesEntity entity = getCoinsActivityById(2L, false);
        coinsActivityVo.setCoinsActivitiesEntity(entity);
        coinsActivityVo.setRewardValue(jackpotRemaining(entity));

        int onTime = isOnTime(entity);
        switch (onTime){
            case 1://1活动已结束
                coinsActivityVo.setComment("今日活动已结束，明天再抢吧!!");
                return coinActivityResult(501,"今日活动已结束，明天再抢吧!!",coinsActivityVo);
            case 2://2活动进行中
                //活动进行中
                Boolean timesStatusEveryDay = checkTimesEveryDay(entity, clientId);
                boolean timesStatusTimeRange = checkTimesTimeRange(entity, clientId);
                if(!timesStatusEveryDay || !timesStatusTimeRange){
                    coinsActivityVo.setComment("您已参加过本活动了!!");
                    return coinActivityResult(501,"您已参加过本活动了!!",coinsActivityVo);
                }
                boolean b = shouldBeFirstPrize(entity,clientId);
                BigDecimal rewardCoins = new BigDecimal("0");
                if(b){
                    //生成大奖,并返回
                    Integer prizeMaxmum = entity.getPrizeMaxmum();
                    Integer prizeMinmun = entity.getPrizeMinmun();
                    rewardCoins = SharingActivityRandomUtil.getRandomCoinsInRange(new BigDecimal(prizeMaxmum+""),new BigDecimal(prizeMinmun+""));
                }else{
                    //生成普通奖,并返回
                    Integer commonWinMaxmum = entity.getCommonWinMaxmum();
                    Integer commonWinMinmum = entity.getCommonWinMinmum();
                    rewardCoins = SharingActivityRandomUtil.getRandomCoinsInRange(new BigDecimal(commonWinMaxmum+""),new BigDecimal(commonWinMinmum+""));
                }
                //insertCoinsActivityRecordByClientId(CoinsActivitiesEntity coinsActivity,Long clientId,BigDecimal award,Integer method, ESharingRewardGoods.ActityValidityUnit actityValidityUnit)
                Double aDouble = insertCoinsActivityRecordByClientId(entity, clientId, rewardCoins, 3);
                if(aDouble>0){
                    coinsActivityVo.setComment("恭喜获得"+aDouble+"宝币!!");
                    coinsActivityVo.setRewardValue(jackpotRemaining(entity));
                    return coinActivityResult(200,"恭喜获得"+aDouble+"宝币!!",coinsActivityVo);
                }else{
                    coinsActivityVo.setComment("宝币仓已满，请消费后再来抢红包!!");
                    return coinActivityResult(501,"宝币仓已满，请消费后再来抢红包!!",coinsActivityVo);
                }

            case 3://3活动马上开始
                coinsActivityVo.setComment("活动马上开始!!");
                return coinActivityResult(501,"活动马上开始!!",coinsActivityVo);
            case 4://4活动已过期
                coinsActivityVo.setComment("来晚了，宝币已经抢完了!!");
                return coinActivityResult(501,"来晚了，宝币已经抢完了!!",coinsActivityVo);
            default://5活动参数错误
                coinsActivityVo.setComment("系统活动参数异常!!");
                return coinActivityResult(501,"系统活动参数异常!!",coinsActivityVo);
        }
    }

    public Result getCoinsActivityInfo() throws ParseException {
        CoinsActivityVo coinsActivityVo = new CoinsActivityVo();

        CoinsActivitiesEntity entity = getCoinsActivityById(2L, false);
        coinsActivityVo.setCoinsActivitiesEntity(entity);
        coinsActivityVo.setComment("success");
        coinsActivityVo.setRewardValue(jackpotRemaining(entity));
        //还差时间相关信息
        return coinActivityResult(200,"success",coinsActivityVo);
    }

    public Result getCountDownInfo() throws ParseException {

        CoinsActivitiesEntity entity = getCoinsActivityById(2L, false);
        int onTime = isOnTime(entity);
        switch (onTime){
            case 1://1活动已结束
                Date openingPmt = entity.getOpeningPmt();
                Date date1 = TimeUtil.contentTimeAndDate(openingPmt, false);
                return coinActivityResult(501,"今日活动已结束，明天再抢吧!!",
                        new CounterDownVo(date1.getTime(),1,"今日活动已结束，明天再抢吧!!")
                        );
            case 2://2活动进行中
                Date closingPmt = entity.getClosingPmt();
                Date date2 = TimeUtil.contentTimeAndDate(closingPmt, true);
                return coinActivityResult(501,"抢宝币活动正在进行中!!",
                        new CounterDownVo(date2.getTime(),2,"抢宝币活动正在进行中!!")
                        );
            case 3://3活动马上开始
                Date openingPmt3 = entity.getOpeningPmt();
                Date date3 = TimeUtil.contentTimeAndDate(openingPmt3, true);
                return coinActivityResult(501,"活动马上开始!!",
                        new CounterDownVo(date3.getTime(),3,"活动马上开始!!")
                        );
            case 4://4活动已过期

                return coinActivityResult(501,"来晚了，本活动已结束!!",
                        new CounterDownVo(new Date().getTime(),4,"来晚了，本活动已结束!!")
                        );
            default://5活动参数错误

                return coinActivityResult(501,"系统活动参数异常!!",
                        new CounterDownVo(new Date().getTime(),4,"系统活动参数异常!!")
                        );
        }
    }

}