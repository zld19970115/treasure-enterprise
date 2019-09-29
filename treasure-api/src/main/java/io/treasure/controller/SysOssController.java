/**
 * Copyright (c) 2018 聚宝科技 All rights reserved.
 * <p>
 * https://www.treasure.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.treasure.controller;

import com.google.gson.Gson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.treasure.annotation.Login;

import io.treasure.common.constant.Constant;
import io.treasure.common.exception.ErrorCode;
import io.treasure.common.utils.Result;
import io.treasure.entity.SysOssEntity;
import io.treasure.oss.cloud.OSSFactory;
import io.treasure.service.SysOssService;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.io.IOUtils.toByteArray;

/**
 * 文件上传
 *
 * @author super 63600679@qq.com
 */
@RestController
@RequestMapping("sys/oss")
@Api(tags = "文件上传")
public class SysOssController {
    @Autowired
    private SysOssService sysOssService;

    private final static String KEY = Constant.CLOUD_STORAGE_CONFIG_KEY;

    @CrossOrigin
    @Login
    @PostMapping("upload")
    @ApiOperation(value = "上传文件")
    public Result<Map<String, Object>> upload(@RequestParam("file") MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            return new Result<Map<String, Object>>().error(ErrorCode.UPLOAD_FILE_EMPTY);
        }
        File folder = new File("D:/images/");
        if (!folder.exists() && !folder.isDirectory()) {
            folder.mkdirs();
        }
        String originalFilename = file.getOriginalFilename();
        File dest = new File(folder + originalFilename);
        Thumbnails.of(file.getInputStream()).scale(1f).outputQuality(0.25f).toFile(dest);
        InputStream in = new FileInputStream(dest);
        byte[] data1 = toByteArray(in);
        in.close();
        //上传文件
        String extension = FilenameUtils.getExtension(originalFilename);
        String url = OSSFactory.build().uploadSuffix(data1, extension);

        //保存文件信息
        SysOssEntity ossEntity = new SysOssEntity();
        ossEntity.setUrl(url);
        ossEntity.setCreateDate(new Date());
        sysOssService.insert(ossEntity);

        Map<String, Object> data = new HashMap<>(1);
        data.put("src", url);

        return new Result<Map<String, Object>>().ok(data);
    }

}