package com.chubock.chatservice.model;

import com.chubock.chatservice.entity.AbstractEntity;
import org.springframework.beans.BeanUtils;

public class ModelFactory {

    public static <R extends AbstractModel<T>, T extends AbstractEntity> R of (T entity, Class<R> clazz) {
        R model = BeanUtils.instantiateClass(clazz);
        model.fill(entity);
        return model;
    }

}
