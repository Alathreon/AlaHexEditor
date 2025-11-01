package com.alathreon.alahexeditor.parsing.template.type;

import com.alathreon.alahexeditor.parsing.ParseException;
import com.alathreon.alahexeditor.parsing.object.BitsetData;
import com.alathreon.alahexeditor.parsing.template.ParseObjects;
import com.alathreon.alahexeditor.parsing.template.SchemaType;
import com.alathreon.alahexeditor.parsing.template.Template;
import com.alathreon.alahexeditor.util.ByteView;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import static com.alathreon.alahexeditor.parsing.template.TemplateUtil.safeSubView;

public record BitsetType(int size, List<String> names) implements SchemaType<BitsetData> {
    @Override
    public ParseTypeResult<BitsetData> parseData(String thisName, ByteView data, Template template, ParseObjects objects) throws ParseException {
        ByteView view = safeSubView(data, size());
        BitSet bitset = view.toBitset();
        List<String> names = new ArrayList<>();
        for(int i = 0; i < names().size(); i++) {
            String name = names().get(i);
            if(bitset.get(i)){
                if(name == null) throw new ParseException(view, "Error in Bitset %s: invalid bitset flag: bit %d".formatted(thisName, i));
                names.add(name);
            }
        }
        return new ParseTypeResult<>(data, view, new BitsetData(bitset, names));
    }
}
