package com.alathreon.alahexeditor.parsing.object;

import com.alathreon.alahexeditor.parsing.template.TemplateUtil;
import com.alathreon.alahexeditor.util.Pair;

import java.util.List;

public record BitsetData(String bits, List<String> names) implements Data {

    public BitsetData {
        TemplateUtil.ensureMaxByteSize(bits.length(), 8);
        for(int i = 0; i < bits.length(); i++) {
            if(bits.charAt(i) != '1' && bits.charAt(i) != '0') throw new IllegalArgumentException("Bits must be 1 or 0");
        }
    }

    @Override
    public List<Pair<String, String>> displayFields() {
        return List.of(
                new Pair<>("Bits", bits),
                new Pair<>("Names", String.join(", ", names))
        );
    }

    @Override
    public List<Pair<String, ParseObject>> children() {
        return List.of();
    }
}
