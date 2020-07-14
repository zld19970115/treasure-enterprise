/**
 * Copyright (c) 2018 聚宝科技 All rights reserved.
 * <p>
 * https://www.treasure.io
 * <p>
 * 版权所有，侵权必究！
 */

package io.treasure.controller;

//import com.google.gson.Gson;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.treasure.annotation.Login;
import io.treasure.common.constant.Constant;
import io.treasure.common.exception.ErrorCode;
import io.treasure.common.page.PageData;
import io.treasure.common.utils.Result;
import io.treasure.dto.SysOssDto;
import io.treasure.entity.SysOssEntity;
import io.treasure.oss.cloud.OSSFactory;
import io.treasure.service.SysOssService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
    //@Login
    @PostMapping("upload")
    @ApiOperation(value = "上传文件")
    public Result<Map<String, Object>> upload(@RequestParam("file") MultipartFile file) throws Exception {
        if (file.isEmpty()) {
            return new Result<Map<String, Object>>().error(ErrorCode.UPLOAD_FILE_EMPTY);
        }
//        File folder = new File("D:/images/");
//        if (!folder.exists() && !folder.isDirectory()) {
//            folder.mkdirs();
//        }
//        String originalFilename = file.getOriginalFilename();
//        File dest = new File(folder + originalFilename);
//        Thumbnails.of(file.getInputStream()).scale(1f).outputQuality(0.25f).toFile(dest);
//        InputStream in = new FileInputStream(dest);
//        byte[] data1 = toByteArray(in);
//        in.close();
        //上传文件
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        String url = OSSFactory.build().uploadSuffix(file.getBytes(), extension);

        //保存文件信息
        SysOssEntity ossEntity = new SysOssEntity();
        ossEntity.setUrl(url);
        ossEntity.setCreateDate(new Date());
        sysOssService.insert(ossEntity);

        Map<String, Object> data = new HashMap<>(1);
        data.put("src", url);
        return new Result<Map<String, Object>>().ok(data);
    }

    @CrossOrigin
    //@Login
    @PostMapping("upload2")
    @ApiOperation(value = "上传文件富文本专用")
    public Result<Map<String, Object>> upload2(HttpServletRequest request) throws IOException {
        int length = request.getContentLength();
        byte[] bytes = new byte[length];
        DataInputStream dis = new DataInputStream(request.getInputStream());
        int readcount = 0;
        while(readcount < length){
            int count = dis.read(bytes,readcount,length);
            readcount = count + readcount;
        }
        String url = null;
        String extension = FilenameUtils.getExtension(UUID.randomUUID().toString());
        url = OSSFactory.build().uploadSuffix(bytes, extension);
        SysOssEntity ossEntity = new SysOssEntity();
        ossEntity.setUrl(url);
        ossEntity.setCreateDate(new Date());
        sysOssService.insert(ossEntity);

        Map<String, Object> data = new HashMap<>(1);
        data.put("error", 0);
        data.put("url", url);
        return new Result<Map<String, Object>>().ok(data);
    }

    @GetMapping("pageList")
    @ApiOperation("分页查询PC")
    @ApiImplicitParams({
            @ApiImplicitParam(name = Constant.PAGE, value = "当前页码，从1开始", paramType = "query", required = true, dataType="int") ,
            @ApiImplicitParam(name = Constant.LIMIT, value = "每页显示记录数", paramType = "query",required = true, dataType="int") ,
            @ApiImplicitParam(name = "startDate", value = "开始time", paramType = "query",dataType="String") ,
            @ApiImplicitParam(name = "endDate", value = "结束time", paramType = "query",dataType="String")
    })
    public Result<PageData<SysOssDto>> pageList(@ApiIgnore @RequestParam Map<String, Object> params) {
        return new Result<PageData<SysOssDto>>().ok(sysOssService.pageList(params));
    }

    @GetMapping("del")
    @ApiOperation("删除")
    public Result delete(@RequestParam String ids){
        sysOssService.deleteBatchIds(JSON.parseArray(ids).toJavaList(Long.class));
        return new Result().ok("ok");
    }

}
