/**
 * Copyright (c) 2019 聚宝科技 All rights reserved.
 *
 * https://www.treasure.io
 *
 * 版权所有，侵权必究！
 */

package io.treasure.exception;

import io.treasure.common.exception.ErrorCode;
import io.treasure.common.exception.RenException;
import io.treasure.common.utils.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 异常处理器
 * @author Super 63600679@qq.com
 * @since 1.0.0
 */
@RestControllerAdvice
public class RenExceptionHandler {
	private static final Logger logger = LoggerFactory.getLogger(RenExceptionHandler.class);

	/**
	 * 处理自定义异常
	 */
	@ExceptionHandler(RenException.class)
	public Result handleRenException(RenException ex){
		Result result = new Result();
		result.error(ex.getCode(), ex.getMsg());

		ex.printStackTrace();//打印错误信息
		logger.error("handleRenException======(p1):"+ex.getCode(),ex.getMsg());//新加
		//System.out.println("(p1xxxxxxxxxxxxxxxxxxxxxxxx)"+ex.getMsg());
		return result;
	}

	@ExceptionHandler(DuplicateKeyException.class)
	public Result handleDuplicateKeyException(DuplicateKeyException ex){
		Result result = new Result();
		result.error(ErrorCode.DB_RECORD_EXISTS);
		logger.error("handleDuplicateKeyException======(p2):"+ex.getStackTrace(),ex.getMessage());//新加
		return result;
	}

	@ExceptionHandler(Exception.class)
	public Result handleException(Exception ex){
		logger.error("handleException======(p3):"+ex.getMessage(), ex);//修改
		//System.out.println("(p2xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx)");
		ex.printStackTrace();
		return new Result().error();
	}

	//@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	//public void notSupportRequestMethod(HttpRequestMethodNotSupportedException ex){
        //System.out.println("HTTP调用异常，不支持使用："+ex.getMethod()+"方法，请使用"+ex.getSupportedHttpMethods()+"重新发送请求");
        //System.out.println("当前支持的方法："+ex.getSupportedMethods());
    //}
	//新增：json数据格式异常
//	@ExceptionHandler({HttpMessageNotReadableException.class})
//	@ResponseBody
//	public Result requestNotReadable(HttpMessageNotReadableException ex){
//		logger.error("json_data_format_error======(p3):",ex.getMessage());
//		ex.printStackTrace();//打印错误信息
//
//		//json 数据读取失败
//		Result result = new Result();
//		result.error(400,"json data is error");
//
//		return result;
//	}

}