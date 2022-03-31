package org.zrclass.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.zrclass.dto.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class RRExceptionHandler {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private MessageSource messageSource;

	@ExceptionHandler(RRException.class)
	public Result handleRRException(RRException e){
		Result r = new Result();
		r.setCode(Integer.parseInt(e.getCode()));
		e.setMsg(e.getMessage());
		return r;
	}

	@ExceptionHandler(NoHandlerFoundException.class)
	public Result handlerNoFoundException(Exception e) {
		logger.error(e.getMessage(), e);
		return Result.fail(404, "路径不存在，请检查路径是否正确");
	}

	@ExceptionHandler(DuplicateKeyException.class)
	public Result handleDuplicateKeyException(DuplicateKeyException e){
		logger.error(e.getMessage(), e);
		return Result.fail("数据库中已存在该记录");
	}
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	@ExceptionHandler(RR401Exception.class)
	public Map<String,Object> handle401(HttpServletRequest request, HttpServletResponse response, RR401Exception e) throws IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json; charset=utf-8");
		response.setStatus(401);
		PrintWriter writer = response.getWriter();
		writer.flush();
		writer.close();
		return null;
	}
	@ResponseStatus(HttpStatus.OK)
	@ExceptionHandler(BindException.class)
	public Result handleBindException(BindException e) {
		logger.error("参数验证失败:{}", e.getMessage());
		BindingResult result = e.getBindingResult();
		List<FieldError> errorList = e.getBindingResult().getFieldErrors();
		List<String> errorMessages = errorList.stream().map(x->{
			String itemMessage= messageSource.getMessage(x.getDefaultMessage(), null, x.getDefaultMessage(), LocaleContextHolder.getLocale());
			return String.format("%s", itemMessage);
		}).collect(Collectors.toList());
		return Result.fail(errorMessages.toString());
	}
	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	public Result httpMediaTypeNotSupportedException(Exception e){
		logger.error(e.getMessage(), e);
		return Result.fail("请求类型错误！");
	}
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public Result httpRequestMethodNotSupportedException(Exception e){
		logger.error(e.getMessage(), e);
		return Result.fail("请求方式错误类型错误！");
	}
	@ExceptionHandler(SocketTimeoutException.class)
	public Result SocketTimeoutException(Exception e){
		logger.error(e.getMessage(), e);
		return Result.fail("请求超时！");
	}
	@ExceptionHandler(Exception.class)
	public Result handleException(Exception e){
		logger.error(e.getMessage(), e);
		return Result.fail("业务处理异常");
	}

}
