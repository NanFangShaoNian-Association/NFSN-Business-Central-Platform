package cn.nfsn.common.security.handler;

import cn.dev33.satoken.exception.*;
import cn.nfsn.common.core.domain.R;
import cn.nfsn.common.core.enums.ResultCode;
import cn.nfsn.common.core.exception.SystemServiceException;
import cn.nfsn.common.core.exception.UserOperateException;
import cn.nfsn.common.core.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @description: 自定义异常处理
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理自定义的业务异常
     * @param e
     * @return
     */
    @ExceptionHandler(value = SystemServiceException.class)
    @ResponseBody
    public R serviceExceptionHandler(SystemServiceException e){
//        log.error("抛出业务错误!：{}",e.getMessage());
        e.printStackTrace();
        return R.fail(e.getResultCode());
    }

    /**
     * 处理用户操作异常
     * @param e
     * @return
     */
    @ExceptionHandler(value = UserOperateException.class)
    @ResponseBody
    public R userOperateExceptionHandler(UserOperateException e){
//        log.error("用户操作错误!：{}",e.getMessage());
        e.printStackTrace();
        return R.fail(e.getResultCode());
    }

    /**
     * 捕获校验注解异常
    * */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseBody
    public R argValidExceptionHandler(MethodArgumentNotValidException e) {
        List<ObjectError> allErrors = e.getBindingResult().getAllErrors();
        String message = allErrors.stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(";"));
        return R.fail(message);
    }

    /**
     * 空参异常处理
     * @param ex
     * @return
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R bindException(BindException ex) {
        log.warn("BindException:", ex);
        try {
            // 拿到@NotNull,@NotBlank和 @NotEmpty等注解上的message值
            String msg = Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage();
            if (StringUtils.hasText(msg)) {
                // 自定义状态返回
                return R.fail(msg);
            }
        } catch (Exception ignored) {
        }
        // 参数类型不匹配检验
        StringBuilder msg = new StringBuilder();
        List<FieldError> fieldErrors = ex.getFieldErrors();
        fieldErrors.forEach((oe) ->
                msg.append("参数:[").append(oe.getObjectName())
                        .append(".").append(oe.getField())
                        .append("]的传入值:[").append(oe.getRejectedValue()).append("]与预期的字段类型不匹配.")
        );
        return R.fail(msg.toString());
    }

    /**
     * jsr 规范中的验证异常，嵌套检验问题
     * @param ex
     * @return
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R constraintViolationException(ConstraintViolationException ex) {
        log.warn("ConstraintViolationException:", ex);
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        String message = violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(";"));
        return R.fail(message);
    }

    /**
     * 处理空指针的异常
     * @param e
     * @return
     */
    @ExceptionHandler(value =NullPointerException.class)
    @ResponseBody
    public R exceptionHandler(NullPointerException e){
//        log.error("抛出空指针错误!:{}",e.getMessage());
        e.printStackTrace();
        return R.fail(ResultCode.INTERNAL_ERROR);
    }

    // 拦截：未登录异常
    @ExceptionHandler(NotLoginException.class)
    @ResponseBody
    public R handlerException(NotLoginException e) {
        // 返回给前端
        return R.ok(e.getMessage());
    }

    // 拦截：缺少权限异常
    @ExceptionHandler(NotPermissionException.class)
    @ResponseBody
    public R handlerException(NotPermissionException e) {
        e.printStackTrace();
        return R.ok("缺少权限：" + e.getPermission());
    }

    // 拦截：缺少角色异常
    @ExceptionHandler(NotRoleException.class)
    @ResponseBody
    public R handlerException(NotRoleException e) {
        e.printStackTrace();
        return R.ok("缺少角色：" + e.getRole());
    }

    // 拦截：二级认证校验失败异常
    @ExceptionHandler(NotSafeException.class)
    @ResponseBody
    public R handlerException(NotSafeException e) {
        e.printStackTrace();
        return R.ok("二级认证校验失败：" + e.getService());
    }

    // 拦截：服务封禁异常
    @ExceptionHandler(DisableServiceException.class)
    @ResponseBody
    public R handlerException(DisableServiceException e) {
        e.printStackTrace();
        return R.ok("当前账号 " + e.getService() + " 服务已被封禁 (level=" + e.getLevel() + ")：" + e.getDisableTime() + "秒后解封");
    }

    // 拦截：Http Basic 校验失败异常
    @ExceptionHandler(NotBasicAuthException.class)
    @ResponseBody
    public R handlerException(NotBasicAuthException e) {
        e.printStackTrace();
        return R.ok(e.getMessage());
    }
    /**
     * 处理其他异常
     * @param e
     * @return
     */
    @ExceptionHandler(value =Exception.class)
    @ResponseBody
    public R exceptionHandler(Exception e){
        //log
        e.printStackTrace();
        return R.fail(ResultCode.INTERNAL_ERROR);
    }
}
