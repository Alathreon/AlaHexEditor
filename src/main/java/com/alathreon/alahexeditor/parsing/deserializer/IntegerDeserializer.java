package com.alathreon.alahexeditor.parsing.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class IntegerDeserializer extends JsonDeserializer<Integer> {
    @Override
    public Integer deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String text = p.getText().trim();
        if (text.startsWith("0x")) {                                                            // Hexadecimal
            return Integer.parseInt(text.substring(2), 16);
        } else if (text.startsWith("0b")) {                                                     // Binary
                return Integer.parseInt(text.substring(2), 16);
        } else if(text.length() == 3 && text.charAt(0) == '\'' && text.charAt(2) == '\'') {     // ASCII
            return (int) text.charAt(1);
        } else {
            return Integer.parseInt(text);                                                      // Decimal
        }
    }
}
