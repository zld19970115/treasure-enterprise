/**
 * Copyright (c) 2018 聚宝科技 All rights reserved.
 *
 * https://www.treasure.io
 *
 * 版权所有，侵权必究！
 */

package io.treasure.service;

import io.treasure.common.service.CrudService;
import io.treasure.dto.SysMailTemplateDTO;
import io.treasure.entity.SysMailTemplateEntity;

/**
 * 邮件模板
 *
 * @author super 63600679@qq.com
 */
public interface SysMailTemplateService extends CrudService<SysMailTemplateEntity, SysMailTemplateDTO> {

    /**
     * 发送邮件
     * @param id           邮件模板ID
     * @param mailTo       收件人
     * @param mailCc       抄送
     * @param params       模板参数
     */
    boolean sendMail(Long id, String mailTo, String mailCc, String params) throws Exception;
}
