package com.alathreon.alahexeditor.parsing.template.type;

import com.alathreon.alahexeditor.parsing.ParseException;
import com.alathreon.alahexeditor.parsing.object.BitsetData;
import com.alathreon.alahexeditor.parsing.template.ParseObjects;
import com.alathreon.alahexeditor.parsing.template.SchemaType;
import com.alathreon.alahexeditor.parsing.template.Template;
import com.alathreon.alahexeditor.util.ByteView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.alathreon.alahexeditor.parsing.template.TemplateUtil.*;

public record BitsetType(int size, List<String> names) implements SchemaType<BitsetData> {

    public BitsetType {
        ensureNonNegative(size);
        ensureMaxByteSize(size, 4);
        Objects.requireNonNull(names);
        if(names.isEmpty()) throw new IllegalArgumentException("names is empty");
        if(names.size() > 1 << size * 8) throw new IllegalArgumentException("names is too long, expected < %d, but actual is %d".formatted(1 << size * 8, names.size()));
    }

    @Override
    public ParseTypeResult<BitsetData> parseData(String thisName, ByteView data, Template template, ParseObjects objects) throws ParseException {
        ByteView view = safeSubView(data, size());
        String bitset = view.toBinaryString();
        List<String> selected = new ArrayList<>();
        for(int i = 0; i < names().size(); i++) {
            String name = names().get(i);
            if(bitset.charAt(bitset.length() - i - 1) == '1'){
                if(name == null) throw new ParseException(view, "Error in Bitset %s: invalid bitset flag: bit %d".formatted(thisName, i));
                selected.add(name);
            }
        }
        return new ParseTypeResult<>(data, view, new BitsetData(bitset, selected));
    }
}
