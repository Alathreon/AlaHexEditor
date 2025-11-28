package com.alathreon.alahexeditor.parsing.template.type.union;

import com.alathreon.alahexeditor.parsing.ParseException;
import com.alathreon.alahexeditor.parsing.object.IntData;
import com.alathreon.alahexeditor.parsing.object.IntRangeBindingData;
import com.alathreon.alahexeditor.parsing.template.ParseObjects;
import com.alathreon.alahexeditor.parsing.template.Template;
import com.alathreon.alahexeditor.util.ByteView;

import java.util.*;

import static com.alathreon.alahexeditor.parsing.template.TemplateUtil.*;

public record IntRangeClassifier(int size, List<IntRangeBindingData> rangeBindings) implements Classifier {
    public IntRangeClassifier {
        ensureMaxByteSize(size, 4);
        Objects.requireNonNull(rangeBindings, "rangeBindings cannot be null");
        rangeBindings.forEach(binding -> Objects.requireNonNull(binding, "binding name cannot be null"));
        rangeBindings = rangeBindings.stream().sorted(Comparator.comparingLong(IntRangeBindingData::from)).toList();
        for(int i = 0; i < rangeBindings.size()-1; i++) {
            IntRangeBindingData binding = rangeBindings.get(i);
            IntRangeBindingData next = rangeBindings.get(i + 1);
            if(binding.to() > next.from()) throw new IllegalArgumentException("binding %s starts before binding %s ends".formatted(next.name(), binding.name()));
        }
    }

    @Override
    public ClassifierResult find(String thisName, ByteView data, Template template, ParseObjects objects) throws ParseException {
        ByteView view = safeSubView(data, size);
        long value = view.parseInt(template.endianness());
        IntRangeBindingData binding = rangeBindings.stream().filter(b -> b.from() <= value && value < b.to()).findFirst().orElseThrow(() -> new ParseException(view, "Error in Union %s: No binding found for value %d".formatted(thisName, value)));
        return new ClassifierResult(binding.name(), binding, new IntData(value, false, size), view, data.leftover(view));
    }
}
