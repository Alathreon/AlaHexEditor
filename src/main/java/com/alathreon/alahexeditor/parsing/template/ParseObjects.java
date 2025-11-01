package com.alathreon.alahexeditor.parsing.template;

import com.alathreon.alahexeditor.parsing.object.ParseObject;

import java.util.*;

public class ParseObjects {

    private final Map<String, ParseObject> objects;
    private final List<Map<String, ParseObject>> stack;

    public ParseObjects(Map<String, ParseObject> result) {
        this.objects = result;
        this.stack = new ArrayList<>();
    }

    public ParseObject get(String id) {
        return stack.stream()
                .map(m -> m.get(id)).filter(Objects::nonNull)
                .findFirst()
                .orElse(objects.get(id));
    }
    public void add(String id, ParseObject object) {
        if(stack.isEmpty()) {
            objects.put(id, object);
        } else {
            stack.getFirst().put(id, object);
        }
    }

    public void startScope() {
        stack.addFirst(new HashMap<>());
    }
    public void endScope() {
        stack.removeFirst();
    }
}