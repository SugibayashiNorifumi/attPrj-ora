package application.interceptor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import application.entity.AbstractEntity;
import application.exception.ApplicationErrors;
import application.exception.ApplicationException;
import lombok.extern.slf4j.Slf4j;

/**
 * DAOインターセプター
 * 共通項目の設定と更新日時による楽観的ロック制御を行う。
 */
@Slf4j
@Aspect
@Component
public class DaoInterceptor {

    @Around("target(application.dao.AbstractDao) && execution(public int insert(application.entity.AbstractEntity))")
    public Object setCommonFieldsForRegister(ProceedingJoinPoint jp) throws Throwable {

        final User loginUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        AbstractEntity entity = (AbstractEntity) jp.getArgs()[0];
        entity.registUserId = Integer.valueOf(loginUser.getUsername());
        // TODO:リクエストURLパスからregistFuncCdをセット
        entity.registFuncCd = "0";

        return jp.proceed(new Object[] {entity});
    }

    @Around("target(application.dao.AbstractDao) && execution(public int update(application.entity.AbstractEntity))")
    public Object setCommonFieldsForUpdate(ProceedingJoinPoint jp) throws Throwable {

        log.debug("exec update dao interceptor");

        final User loginUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        AbstractEntity entity = (AbstractEntity) jp.getArgs()[0];
        entity.updateUserId = Integer.valueOf(loginUser.getUsername());
        // TODO:リクエストURLパスからupdateFuncCdをセット
        entity.updateFuncCd = "0";
        Integer upCount = (Integer ) jp.proceed(new Object[] {entity});

        if(upCount == 0) {
            throw new ApplicationException(ApplicationErrors.ALREADY_UPDATED);
        }

        return upCount;
    }

}
