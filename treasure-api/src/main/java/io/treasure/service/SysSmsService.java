/**
 * Copyright (c) 2018 聚宝科技 All rights reserved.
 *
 * https://www.treasure.io
 *
 * 版权所有，侵权必究！
 */

package io.treasure.service;

import io.treasure.common.page.PageData;
import io.treasure.common.service.BaseService;
import io.treasure.dto.SysSmsDTO;
import io.treasure.entity.SysSmsEntity;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 短信
 *
 * @author super 63600679@qq.com
 */
public interface SysSmsService extends BaseService<SysSmsEntity> {

    PageData<SysSmsDTO> page(Map<String, Object> params);

    /**
     * 发送短信
     * @param mobile   手机号
     * @param params   短信参数
     */
    void send(String mobile, String params);

    /**
     * 保存短信发送记录
     * @param platform   平台
     * @param mobile   手机号
     * @param params   短信参数
     * @param status   发送状态
     */
    void save(Integer platform, String mobile, LinkedHashMap<String, String> params, Integer status);
}

