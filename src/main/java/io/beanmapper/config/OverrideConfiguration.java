package io.beanmapper.config;

import io.beanmapper.core.BeanMatchStore;
import io.beanmapper.core.constructor.BeanInitializer;
import io.beanmapper.core.constructor.DefaultBeanInitializer;
import io.beanmapper.core.converter.BeanConverter;
import io.beanmapper.core.rule.MappableFields;
import io.beanmapper.core.unproxy.BeanUnproxy;
import io.beanmapper.core.unproxy.DefaultBeanUnproxy;
import io.beanmapper.core.unproxy.SkippingBeanUnproxy;

import java.util.ArrayList;
import java.util.List;

public class OverrideConfiguration extends AbstractConfiguration {

    private final Configuration parentConfiguration;

    private BeanInitializer beanInitializer;

    private SkippingBeanUnproxy beanUnproxy;

    private List<String> packagePrefixes;

    private List<BeanConverter> beanConverters;

    private NullableField<Class> targetClass = new NullableField<Class>(null);

    private NullableField<Object> target = new NullableField<Object>(null);

    private NullableField<Class> collectionClass = new NullableField<Class>(null);

    private MappableFields mappableFields;

    private boolean converterChoosable = true;

    public OverrideConfiguration(Configuration configuration) {
        if (configuration == null) {
            throw new RuntimeException("Developer error: the parent configuration may not be null");
        }
        this.parentConfiguration = configuration;
    }

    @Override
    public Class getTargetClass() {
        return targetClass == null ? parentConfiguration.getTargetClass() : targetClass.get();
    }

    @Override
    public Object getTarget() {
        return target == null ? parentConfiguration.getTarget() : target.get();
    }

    @Override
    public Class getCollectionClass() {
        return collectionClass == null ? parentConfiguration.getCollectionClass() : collectionClass.get();
    }

    @Override
    public MappableFields getMappableFields() {
        return mappableFields == null ? parentConfiguration.getMappableFields() : mappableFields;
    }

    @Override
    public BeanInitializer getBeanInitializer() {
        return beanInitializer == null ? parentConfiguration.getBeanInitializer() : beanInitializer;
    }

    @Override
    public SkippingBeanUnproxy getBeanUnproxy() {
        return beanUnproxy == null ? parentConfiguration.getBeanUnproxy() : beanUnproxy;
    }

    @Override
    public BeanMatchStore getBeanMatchStore() {
        return parentConfiguration.getBeanMatchStore();
    }

    @Override
    public List<String> getPackagePrefixes() {
        return packagePrefixes == null ? parentConfiguration.getPackagePrefixes() : packagePrefixes;
    }

    @Override
    public List<BeanConverter> getBeanConverters() {
        return beanConverters == null ? parentConfiguration.getBeanConverters() : beanConverters;
    }

    @Override
    public boolean isConverterChoosable() {
        return converterChoosable;
    }

    @Override
    public void withoutDefaultConverters() {
        // not supported for override options
    }

    // @todo make sure addConverter works for override
    @Override
    public void addConverter(BeanConverter converter) {
//        if (beanConverters == null) {
//            beanConverters = new ArrayList<BeanConverter>();
//        }
//        beanConverters.add(converter);
        // not supported for override options
    }

    @Override
    public void addProxySkipClass(Class<?> clazz) {
        // not supported for override options
    }

    @Override
    public void addPackagePrefix(Class<?> clazz) {
        // not supported for override options
    }

    @Override
    public void addPackagePrefix(String packagePrefix) {
        // not supported for override options
    }

    @Override
    public void setBeanInitializer(BeanInitializer beanInitializer) {
        this.beanInitializer = beanInitializer;
    }

    @Override
    public void setBeanUnproxy(BeanUnproxy beanUnproxy) {
        this.beanUnproxy = new SkippingBeanUnproxy(beanUnproxy);
    }

    @Override
    public boolean isAddDefaultConverters() {
        return false;
    }

    @Override
    public void setCollectionClass(Class collectionClass, boolean fallbackToParent) {
        this.collectionClass = fallbackToParent ? null : new NullableField<Class>(collectionClass);
    }

    @Override
    public void setTargetClass(Class targetClass, boolean fallbackToParent) {
        this.targetClass = fallbackToParent ? null : new NullableField<Class>(targetClass);
    }

    @Override
    public void setTarget(Object target, boolean fallbackToParent) {
        this.target = fallbackToParent ? null : new NullableField<Object>(target);
    }

    @Override
    public void setMappableFields(MappableFields mappableFields) {
        this.mappableFields = mappableFields;
    }

    @Override
    public void setConverterChoosable(boolean converterChoosable) {
        this.converterChoosable = converterChoosable;
    }

    @Override
    public boolean canReuse() {
        return true;
    }

}
