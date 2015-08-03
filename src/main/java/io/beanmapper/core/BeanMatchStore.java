package io.beanmapper.core;

import io.beanmapper.annotations.BeanCollection;
import io.beanmapper.annotations.BeanIgnore;
import io.beanmapper.annotations.BeanProperty;
import io.beanmapper.annotations.BeanUnwrap;
import io.beanmapper.core.collections.BeanCollectionInstructions;
import io.beanmapper.core.inspector.PropertyAccessor;
import io.beanmapper.core.inspector.PropertyAccessors;
import io.beanmapper.exceptions.BeanMissingPathException;
import io.beanmapper.exceptions.NoSuchPropertyException;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class BeanMatchStore {

    private Map<String, Map<String, BeanMatch>> store = new TreeMap<String, Map<String, BeanMatch>>();

    public BeanMatch getBeanMatch(Class<?> sourceClass, Class<?> targetClass) {
        Map<String, BeanMatch> targetsForSource = getTargetsForSource(sourceClass);
        if (targetsForSource == null || !targetsForSource.containsKey(targetClass.getCanonicalName())) {
            return addBeanMatch(determineBeanMatch(sourceClass, targetClass));
        }
        return getTarget(targetsForSource, targetClass);
    }

    public BeanMatch addBeanMatch(BeanMatch beanMatch) {
        Map<String, BeanMatch> targetsForSource = getTargetsForSource(beanMatch.getSourceClass());
        if (targetsForSource == null) {
            targetsForSource = new TreeMap<String, BeanMatch>();
            storeTargetsForSource(beanMatch.getSourceClass(), targetsForSource);
        }
        storeTarget(targetsForSource, beanMatch.getTargetClass(), beanMatch);
        return beanMatch;
    }

    private void storeTargetsForSource(Class<?> source, Map<String, BeanMatch> targetsForSource) {
        store.put(source.getCanonicalName(), targetsForSource);
    }

    private Map<String, BeanMatch> getTargetsForSource(Class<?> source) {
        return store.get(source.getCanonicalName());
    }

    private BeanMatch getTarget(Map<String, BeanMatch> targetsForSource, Class<?> target) {
        return targetsForSource.get(target.getCanonicalName());
    }

    private void storeTarget(Map<String, BeanMatch> targetsForSource, Class<?> target, BeanMatch beanMatch) {
        targetsForSource.put(target.getCanonicalName(), beanMatch);
    }

    private BeanMatch determineBeanMatch(Class<?> source, Class<?> target) {
        return determineBeanMatch(source, target, new TreeMap<String, BeanField>(), new TreeMap<String, BeanField>());
    }

    private BeanMatch determineBeanMatch(Class<?> source, Class<?> target,
                                         Map<String, BeanField> sourceNode, Map<String, BeanField> targetNode) {
        return new BeanMatch(
                source,
                target,
                getAllFields(sourceNode, targetNode, source, target, null),
                getAllFields(targetNode, sourceNode, target, source, null));
    }

    private Map<String, BeanField> getAllFields(Map<String, BeanField> ourNodes, Map<String, BeanField> otherNodes, Class<?> ourType, Class<?> otherType, BeanField prefixingBeanField) {
        List<PropertyAccessor> accessors = PropertyAccessors.getAll(ourType);
        for (PropertyAccessor accessor : accessors) {

            // Ignore fields
            if (accessor.findAnnotation(BeanIgnore.class) != null) {
                continue;
            }

            // BeanProperty allows the field to match with a field from the other side with a different name
            // and even a different nesting level.
            String name = dealWithBeanProperty(otherNodes, otherType, accessor);

            // Unwrap the fields which exist in the unwrap class
            BeanField currentBeanField = null;
            try {
                currentBeanField = BeanField.determineNodesForPath(ourType, accessor.getName(), prefixingBeanField);
            } catch (NoSuchPropertyException e) {
                throw new BeanMissingPathException(ourType, accessor.getName(), e);
            }

            handleBeanCollectionAnnotation(accessor.findAnnotation(BeanCollection.class), currentBeanField);

            if (accessor.findAnnotation(BeanUnwrap.class) != null) {
                ourNodes = getAllFields(ourNodes, otherNodes, accessor.getType(), otherType, currentBeanField);
            } else {
                ourNodes.put(name, currentBeanField);
            }
        }
        return ourNodes;
    }

    private void handleBeanCollectionAnnotation(BeanCollection beanCollection, BeanField beanField) {
        if (beanCollection == null) {
            return;
        }
        BeanCollectionInstructions collectionInstructions = new BeanCollectionInstructions();
        collectionInstructions.setCollectionMapsTo(beanCollection.elementType());
        collectionInstructions.setBeanCollectionUsage(beanCollection.beanCollectionUsage());
        beanField.setCollectionInstructions(collectionInstructions);
    }

    private String dealWithBeanProperty(Map<String, BeanField> otherNodes, Class<?> otherType, PropertyAccessor accessor) {
        String name = accessor.getName();
        if (accessor.findAnnotation(BeanProperty.class) != null) {
            name = accessor.findAnnotation(BeanProperty.class).name();
            // Get the other field from the location that is specified in the beanProperty annotation.
            // If the field is referred to by a path, store the custom field in the other map
            try {
                otherNodes.put(name, BeanField.determineNodesForPath(otherType, name, null));
            } catch (NoSuchPropertyException err) {
                // Acceptable, might have been tagged as @BeanProperty as well
            }
        }
        return name;
    }

}