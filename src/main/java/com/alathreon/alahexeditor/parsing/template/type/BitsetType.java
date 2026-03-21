package com.alathreon.alahexeditor.parsing.template.type;

import com.alathreon.alahexeditor.parsing.Endianness;
import com.alathreon.alahexeditor.parsing.ParseException;
import com.alathreon.alahexeditor.parsing.object.BitsetData;
import com.alathreon.alahexeditor.parsing.object.BlobData;
import com.alathreon.alahexeditor.parsing.object.ParseObject;
import com.alathreon.alahexeditor.parsing.template.ParseObjects;
import com.alathreon.alahexeditor.parsing.template.SchemaType;
import com.alathreon.alahexeditor.parsing.template.Template;
import com.alathreon.alahexeditor.parsing.template.element.sequencemaker.LengthPolicy;
import com.alathreon.alahexeditor.parsing.template.element.sequencemaker.PreparedLengthPolicy;
import com.alathreon.alahexeditor.parsing.template.element.sequencemaker.impl.CounterLengthPolicyCondition;
import com.alathreon.alahexeditor.util.ByteView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.alathreon.alahexeditor.parsing.template.TemplateUtil.*;

public record BitsetType(LengthPolicy lengthPolicy, boolean isLengthBits, List<String> names) implements SchemaType<BitsetData> {

    public BitsetType {
        Objects.requireNonNull(lengthPolicy);
        if(names == null) {
            names = new ArrayList<>();
        }
    }

    @Override
    public ParseTypeResult<BitsetData> parseData(String thisName, ByteView data, Template template, ParseObjects objects) throws ParseException {
        PreparedLengthPolicy preparedLengthPolicy = lengthPolicy.prepare(thisName, data, template, objects);
        var condition = preparedLengthPolicy.condition();
        int offset = 0;
        if(isLengthBits && condition instanceof CounterLengthPolicyCondition cc) {
            offset = 8 - cc.getLimitExclusive() % 8;
            condition = new CounterLengthPolicyCondition(cc.getLimitExclusive() / 8 + 1);
        }
        ByteView view = preparedLengthPolicy.leftover();
        ParseObject self = null;
        int i = 0;
        while(condition.hasNext(self, thisName, data, template, objects)) {
            ByteView currentView = view.subView(i, 1);
            self = new ParseObject(currentView,
                    new BlobData(currentView.toString()));
            i++;
        }
        view = view.subView(i);
        String bitset = view.toBinaryString();
        if(offset != 0) {
            bitset = bitset.substring(0, bitset.length() - offset);
        }
        List<String> selected = new ArrayList<>();
        for(i = 0; i < names().size(); i++) {
            String name = names().get(i);
            if(bitset.charAt(bitset.length() - i - 1) == '1'){
                if(name == null) throw new ParseException(view, objects, "Error in Bitset %s: invalid bitset flag: bit %d".formatted(thisName, i));
                selected.add(name);
            }
        }
        return new ParseTypeResult<>(data, view, new BitsetData(bitset, selected));
    }
}
