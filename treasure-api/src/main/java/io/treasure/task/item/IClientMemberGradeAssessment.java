package io.treasure.task.item;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.treasure.entity.ChargeCashEntity;
import io.treasure.entity.ClientMemberGradeAssessmentEntity;
import io.treasure.entity.ClientUserEntity;
import io.treasure.entity.MasterOrderEntity;
import io.treasure.utils.TimeUtil;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public interface IClientMemberGradeAssessment {

    void updateGrade(int pageNum);
    int growUpGrade(Long clientId);
    int adjustGradeById(Long clientId);
    int getClientPoints(Long userId) throws ParseException;
    int getChargeCashSum(Long userId, Date assessmentTime,int timeRang) throws ParseException;
    int getChargePayMoneySum(Long userId, Date assessmentTime,int timeRang) throws ParseException;
    boolean isOnTime();
}
