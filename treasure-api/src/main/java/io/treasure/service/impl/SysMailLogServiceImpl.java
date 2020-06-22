/**
 * Copyright (c) 2018 聚宝科技 All rights reserved.
 *
 * https://www.treasure.io
 *
 * 版权所有，侵权必究！
 */

package io.treasure.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.treasure.common.constant.Constant;
import io.treasure.common.page.PageData;
import io.treasure.common.service.impl.BaseServiceImpl;
import io.treasure.dao.SysMailLogDao;
import io.treasure.dto.SysMailLogDTO;
import io.treasure.entity.SysMailLogEntity;
import io.treasure.service.SysMailLogService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
public class SysMailLogServiceImpl extends BaseServiceImpl<SysMailLogDao, SysMailLogEntity> implements SysMailLogService {

    @Override
    public PageData<SysMailLogDTO> page(Map<String, Object> params) {
        IPage<SysMailLogEntity> page = baseDao.selectPage(
            getPage(params, Constant.CREATE_DATE, false),
            getWrapper(params)
        );
        return getPageData(page, SysMailLogDTO.class);
    }

    private QueryWrapper<SysMailLogEntity> getWrapper(Map<String, Object> params){
        String templateId = (String)params.get("templateId");
        String mailTo = (String)params.get("mailTo");
        String status = (String)params.get("status");

        QueryWrapper<SysMailLogEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(templateId),"template_id", templateId);
        wrapper.like(StringUtils.isNotBlank(mailTo), "mail_to", mailTo);
        wrapper.eq(StringUtils.isNotBlank(status),"status", status);

        return wrapper;
    }

    @Override
    public void save(Long templateId, String from, String[] to, String[] cc, String subject, String content, Integer status) {
        SysMailLogEntity log = new SysMailLogEntity();
        log.setTemplateId(templateId);
        log.setMailFrom(from);
        log.setMailTo(JSON.toJSONString(to));
        if(cc != null){
            log.setMailCc(JSON.toJSONString(cc));
        }
        log.setSubject(subject);
        log.setContent(content);
        log.setStatus(status);
        this.insert(log);
    }

}
