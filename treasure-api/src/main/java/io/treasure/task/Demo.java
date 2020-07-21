package io.treasure.task;

import io.treasure.dto.SharingActivityDTO;
import io.treasure.dto.SharingActivityExtendsDTO;
import io.treasure.entity.SharingActivityExtendsEntity;
import io.treasure.utils.ObjectUtil;
import org.junit.Test;

import java.util.ArrayList;

public class Demo {

    @Test
    public void test(){
        SharingActivityExtendsEntity s = new SharingActivityExtendsEntity();
        s.setSaeId(123);
        s.setHelperRewardId(4654646l);
        s.setHelperRewardAmount(36);

        Object anotherObjVimJson = new ObjectUtil<SharingActivityDTO>().getAnotherObjVimJson(s);
        SharingActivityDTO sadto = (SharingActivityDTO)anotherObjVimJson;
        System.out.println(sadto.toString());
    }

}
