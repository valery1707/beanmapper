/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.core.converter;

import io.beanmapper.core.converter.impl.LocalDateTimeToLocalDate;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 
 *
 * @author Jeroen van Schagen
 * @since Jun 18, 2015
 */
public class AbstractBeanConverterTest {
    
    private LocalDateTimeToLocalDate converter = new LocalDateTimeToLocalDate();
    
    @Test
    public void testConvert() {
        LocalDateTime time = LocalDateTime.now();
        Assert.assertEquals(time.toLocalDate(), converter.convert(time, LocalDate.class, null));
    }
    
    @Test
    public void testNullSource() {
        Assert.assertNull(converter.convert(null, LocalDate.class, null));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidSource() {
        converter.convert("Test", LocalDate.class, null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidTarget() {
        converter.convert(LocalDateTime.now(), String.class, null);
    }

}
