package com.alathreon.alahexeditor.parsing.template;

import com.alathreon.alahexeditor.parsing.Endianness;
import com.alathreon.alahexeditor.parsing.object.Data;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public record Template(
                        String $schema,
                        List<URL> references,
                        Endianness endianness,
                        Map<String, SchemaType<?>> types,
                        Map<String, SchemaElement> schema) {

    public Template {
        if(endianness == null) endianness = Endianness.BIG;
        if(references == null) references = Collections.emptyList();
        if(types == null) types = Collections.emptyMap();
        Objects.requireNonNull(schema, "Schema cannot be null");
    }

    public <T extends SchemaType<Data>> T findType(String name, Class<T> clazz) {
        return findType(name, clazz, () -> new IllegalArgumentException("Type " + name + " doesn't exist"));
    }
    public <T extends SchemaType<?>, E extends Exception> T findType(String name, Class<T> clazz, Supplier<E> ifNotFound) throws E {
        SchemaType<?> schemaType = types().get(name);
        if(schemaType == null) {
            throw ifNotFound.get();
        }
        if(clazz.isInstance(schemaType)) {
            return clazz.cast(schemaType);
        } else {
            throw new IllegalArgumentException(name + " is not a " + clazz.getSimpleName());
        }
    }
    public <E extends Exception> SchemaType<?> findType(String name, Supplier<E> ifNotFound) throws E {
        SchemaType<?> schemaType = types().get(name);
        if(schemaType == null) {
            throw ifNotFound.get();
        }
        return schemaType;
    }
}
