package io.treasure.service.impl;

import io.treasure.dao.ClientMemberGradeAssessmentDao;
import io.treasure.entity.ClientMemberGradeAssessmentEntity;
import io.treasure.service.ClientMemberGradeAssessmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ClientMemberGradeAssessmentServiceImpl implements ClientMemberGradeAssessmentService {

    @Autowired(required = false)
    ClientMemberGradeAssessmentDao clientMemberGradeAssessmentDao;

    @Override
    public ClientMemberGradeAssessmentEntity getRule(){
        List<ClientMemberGradeAssessmentEntity> clientMemberGradeAssessmentEntities = clientMemberGradeAssessmentDao.selectList(null);
        return clientMemberGradeAssessmentEntities.size()>0?clientMemberGradeAssessmentEntities.get(0):null;

    }
    @Override
    public void setRule(ClientMemberGradeAssessmentEntity entity){

        if(entity.getId() == null){
           clientMemberGradeAssessmentDao.insert(entity);
        }else{
            clientMemberGradeAssessmentDao.updateById(entity);
        }

    }
}
