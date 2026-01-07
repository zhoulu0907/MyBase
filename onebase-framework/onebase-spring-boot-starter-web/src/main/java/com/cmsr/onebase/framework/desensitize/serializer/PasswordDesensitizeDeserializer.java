package com.cmsr.onebase.framework.desensitize.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;

import java.io.IOException;

public class PasswordDesensitizeDeserializer extends JsonDeserializer<String> {

    @Override
    public String deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String passwordStr = jsonParser.getValueAsString();
        int pwdLength = passwordStr.length();
        if (Strings.CI.equals(passwordStr, StringUtils.repeat("*", pwdLength))) {
            return null;
        }
        return passwordStr;
    }
}
