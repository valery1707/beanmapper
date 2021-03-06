package io.beanmapper.core.converter;

import io.beanmapper.core.BeanFieldMatch;

/**
 * This class can be inherited if you want to add your own converter to the beanMapper.
 * You must supply the types of both the source and target class.
 * After instantiation of the beanMapper, you can add the converter module by
 * calling the addConverter() method.
 */
public interface BeanConverter {

    /**
     * Converts the source instance into the desired target type.
     * @param source the source instance
     * @param targetClass the desired target type
     * @return the converted source instance
     */
    Object convert(Object source, Class<?> targetClass, BeanFieldMatch beanFieldMatch);

    /**
     * Determines if the conversion of our source type to a 
     * target type is supported by this converter.
     * @param sourceClass the source class
     * @param targetClass the target class
     * @return {@code true} if the conversion is supported, else {@code false}
     */
    boolean match(Class<?> sourceClass, Class<?> targetClass);

}
