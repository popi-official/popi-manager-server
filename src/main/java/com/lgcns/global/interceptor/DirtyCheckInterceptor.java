package com.lgcns.global.interceptor;

import java.io.Serializable;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

@Slf4j
public class DirtyCheckInterceptor extends EmptyInterceptor {

    @Override
    public boolean onFlushDirty(
            Object entity,
            Serializable id,
            Object[] currentState,
            Object[] previousState,
            String[] propertyNames,
            Type[] types) {

        log.info("[Dirty Checking] Entity modified: {}", entity.getClass().getSimpleName());

        for (int i = 0; i < propertyNames.length; i++) {
            Object current = currentState[i];
            Object previous = previousState[i];

            if (current != null && !current.equals(previous)) {
                log.info("   ↪ {}: {} -> {}", propertyNames[i], previous, current);
            }
        }
        return false;
    }
}
