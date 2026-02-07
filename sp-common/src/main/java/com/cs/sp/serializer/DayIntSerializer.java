package com.cs.sp.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author sb
 * @date 2023/7/24 04:41
 */
public class DayIntSerializer extends StdSerializer<Integer> {

    private static final Pattern SIMPLE_DATE_PATTERN = Pattern.compile("^(\\d{4})(\\d{2})(\\d{2})$");


    public DayIntSerializer() {
        this(Integer.class);
    }

    protected DayIntSerializer(Class<Integer> t) {
        super(t);
    }


    @Override
    public void serialize(Integer integer, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        if(integer == null){
            gen.writeNull();
        }else{
            Matcher matcher = SIMPLE_DATE_PATTERN.matcher(String.valueOf(integer));
            if(matcher.matches()){
                gen.writeString(String.format("%s-%s-%s", matcher.group(1), matcher.group(2), matcher.group(3)));
            }else{
                gen.writeString(String.valueOf(integer));
            }
        }
    }
}
