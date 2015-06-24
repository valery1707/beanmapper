/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.converter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class StringToEnumConverterTest {
    
    private StringToEnumConverter converter;
    
    @Before
    public void setUp() {
        converter = new StringToEnumConverter();
    }
    
    @Test
    public void testWithName() {
        Assert.assertEquals(TestEnum.B, converter.convert("B", TestEnum.class));
    }
    
    @Test
    public void testWithEmptyName() {
        Assert.assertNull(converter.convert("   ", TestEnum.class));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testWithUnknownName() {
        converter.convert("?", TestEnum.class);
    }
    
    public enum TestEnum {
        A, B, C;
    }

}