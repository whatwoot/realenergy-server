package com.cs.copy.system.api.aspect;

import com.cs.copy.global.constants.CacheKey;
import com.cs.copy.system.api.annotation.MaintainCheck;
import com.cs.copy.system.api.enums.SystemStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

import static com.cs.sp.common.WebAssert.expect;

/**
 * @author fiona
 * @date 2020/8/12 04:58
 */
@Slf4j
@Component
@Aspect
@Order(100)
public class MaintainAspect {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 切到注解上
     */
    @Pointcut("@annotation(com.cs.copy.system.api.annotation.MaintainCheck)")
    private void anyMethod() {
    }

    @Around("anyMethod() && @annotation(maintainCheck)")
    public Object maintainCheck(ProceedingJoinPoint pjp, MaintainCheck maintainCheck) throws Throwable {
        String s = stringRedisTemplate.opsForValue().get(CacheKey.GAME_STAUS);
        expect(SystemStatusEnum.OK.eq(s), SystemStatusEnum.MAINTENANCE.eq(s) ? "chk.game.stop" : "chk.game.load");
        return pjp.proceed();
    }

    /**
     * 获取当前方法
     *
     * @param pjp
     * @return
     */
    public Method getCurrentMethod(ProceedingJoinPoint pjp) {
        Signature s = pjp.getSignature();
        MethodSignature ms = (MethodSignature) s;
        Method m = ms.getMethod();
        return m;
    }

    /**
     * 获取被拦截方法对象
     * <p>
     * MethodSignature.getMethod() 获取的是顶层接口或者父类的方法对象
     * 而缓存的注解在实现类的方法上
     * 所以应该使用反射获取当前对象的方法对象
     */
    public Method getImplMethod(ProceedingJoinPoint pjp) {
        //获取参数的类型
        Object[] args = pjp.getArgs();
        Class[] argTypes = new Class[pjp.getArgs().length];
        for (int i = 0; i < args.length; i++) {
            argTypes[i] = args[i].getClass();
        }
        Method method = null;
        try {
            method = pjp.getTarget().getClass().getMethod(pjp.getSignature().getName(), argTypes);
        } catch (NoSuchMethodException | SecurityException e) {
            log.error(e.getMessage(), e);
        }
        return method;

    }

    /**
     * 获取缓存的key
     * key 定义在注解上，支持SPEL表达式
     *
     * @return
     */
    private String parseKey(String key, Method method, Object[] args) {

        //获取被拦截方法参数名列表(使用Spring支持类库)
        LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
        String[] paraNameArr = u.getParameterNames(method);

        //使用SPEL进行key的解析
        ExpressionParser parser = new SpelExpressionParser();
        //SPEL上下文
        StandardEvaluationContext context = new StandardEvaluationContext();
        //把方法参数放入SPEL上下文中
        for (int i = 0; i < paraNameArr.length; i++) {
            context.setVariable(paraNameArr[i], args[i]);
        }
        return parser.parseExpression(key).getValue(context, String.class);
    }
}
