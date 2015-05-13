package io.beanmapper.core;

import io.beanmapper.exceptions.*;

import java.lang.reflect.Field;
import java.util.Stack;

public class BeanField {

    private String name;

    private Field field;

    private BeanField next;

    public BeanField(String name, Field field) {
        setName(name);
        setField(field);
    }

    public String getName() {
        return getName("");
    }

    private String getName(String prefix) {
        if (hasNext()) {
            return getNext().getName(prefix + name + ".");
        }
        return prefix + name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean hasNext() {
        return next != null;
    }

    public BeanField getNext() {
        return next;
    }

    public void setNext(BeanField beanField) {
        this.next = beanField;
    }

    protected Field getCurrentField() {
        return field;
    }

    public Field getField() {
        return hasNext() ? getNext().getField() : getCurrentField();
    }

    public void setField(Field field) {
        this.field = field;
        this.field.setAccessible(true);
    }

    public Object getObject(Object object) throws BeanMappingException {
        try {
            object = getCurrentField().get(object);
        } catch (IllegalAccessException e) {
            throw new BeanGetFieldException(object.getClass(), getCurrentField(), e);
        }
        if (hasNext()) {
            if (object == null) {
                throw new BeanMissingPathException("The path could not be resolved");
            }
            return getNext().getObject(object);
        }
        return object;
    }

    public Object getOrCreate(Object parent) throws BeanMappingException {
        Object target = null;
        try {
            target = getCurrentField().get(parent);
        } catch (IllegalAccessException e) {
            throw new BeanGetFieldException(parent.getClass(), getCurrentField(), e);
        }
        if (target == null) {

            try {
                target = getCurrentField().getType().getConstructor().newInstance();
            } catch (Exception e) {
                throw new BeanInstantiationException(getCurrentField().getType(), e);
            }
            try {
                getCurrentField().set(parent, target);
            } catch (IllegalAccessException e) {
                throw new BeanSetFieldException(parent.getClass(), getCurrentField(), e);
            }
        }
        return target;
    }

    public Object writeObject(Object source, Object parent) throws BeanMappingException {
        if (hasNext()) {
            getNext().writeObject(source, getOrCreate(parent));
        }
        else {

            // If target is a String and source is not, call toString on the value
            if (    source != null &&
                    getCurrentField().getType().equals(String.class) &&
                    !source.getClass().equals(String.class)) {
                source = source.toString();
            }

            try {
                getCurrentField().set(parent, source);
            } catch (IllegalAccessException e) {
                throw new BeanSetFieldException(parent.getClass(), getCurrentField(), e);
            }
        }
        return parent;
    }

    public static BeanField determineNodesForNode(Class baseClass, String node)
            throws NoSuchFieldException {
        return determineNodes(baseClass, new Route(node), new Stack<>());
    }

    public static BeanField determineNodesForPath(Class baseClass, String path)
            throws NoSuchFieldException {
        return determineNodes(baseClass, new Route(path), new Stack<>());
    }

    public static BeanField determineNodesForPath(Class baseClass, String path, BeanField prefixingBeanField)
            throws NoSuchFieldException {
        return determineNodes(baseClass, new Route(path), copyNodes(prefixingBeanField));
    }

    private static Stack<BeanField> copyNodes(BeanField prefixingBeanField) {
        Stack<BeanField> beanFields = new Stack<>();
        while (prefixingBeanField != null) {
            beanFields.push(new BeanField(prefixingBeanField.getName(), prefixingBeanField.getCurrentField()));
            prefixingBeanField = prefixingBeanField.getNext();
        }
        return beanFields;
    }

    private static BeanField determineNodes(Class baseClass, Route route, Stack<BeanField> beanFields) throws NoSuchFieldException {

        if (!traversePath(baseClass, route, beanFields)) return null;

        return getFirstBeanField(beanFields);
    }

    private static BeanField getFirstBeanField(Stack<BeanField> beanFields) {
        BeanField previousBeanField = null;
        BeanField currentBeanField = null;
        while (!beanFields.empty()) {
            currentBeanField = beanFields.pop();
            if (previousBeanField != null) {
                currentBeanField.setNext(previousBeanField);
            }
            previousBeanField = currentBeanField;
        }

        return currentBeanField;
    }

    private static boolean traversePath(Class baseClass, Route route, Stack<BeanField> beanFields) throws NoSuchFieldException {
        for (String node : route.getRoute()) {
            final Field field = baseClass.getDeclaredField(node);
            if (field == null) {
                return false;
            }
            BeanField currentBeanField = new BeanField(node, field);
            beanFields.push(currentBeanField);
            baseClass = currentBeanField.getCurrentField().getType();
        }
        return true;
    }

}