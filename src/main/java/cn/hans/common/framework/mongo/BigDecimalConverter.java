package cn.hans.common.framework.mongo;

import org.mongodb.morphia.converters.TypeConverter;
import org.mongodb.morphia.mapping.MappedField;

import java.math.BigDecimal;

/**
 * mongo类型转换器，用于转换BigDecimal
 * @author      hans
 */
public class BigDecimalConverter extends TypeConverter {

    public BigDecimalConverter() {
        super(BigDecimal.class);
    }

    @Override
    public Object encode(Object value, MappedField optionalExtraInfo) {
        BigDecimal val = (BigDecimal) value;
        if (val == null) {
            return null;
        }
        return val.doubleValue();
    }

    @Override
    public Object decode(Class<?> aClass, Object o, MappedField mappedField) {
        if (o == null) {
            return null;
        }
        BigDecimal dec = BigDecimal.valueOf((double)o);
        return dec;
    }
}
