# AlaHexEditor

A JavaFX hex editor with declarative binary file parsing using JSON templates.

## Features

- **Hex Viewer**: View binary files in hexadecimal with offset, hex bytes, and ASCII representation
- **Template-Based Parsing**: Define binary file structures using JSON templates
- **Structured Data View**: Visualize parsed binary data as a hierarchical tree
- **Rich Type System**: Integers, floats, strings, arrays, structs, enums, bitsets, and unions
- **Expression Support**: Computed fields and dynamic sizes using postfix notation
- **Persistence**: Recently opened files and templates are remembered

## Quick Start

### Prerequisites

- Java 25

### Build and Run

```shell
# Build the project
./mvnw install

# Run directly
./mvnw exec:java

# Or run as standalone JAR
java -jar target/AlaHexEditor-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## Template Format

Templates are JSON files that define binary file structures. Example:

```json
{
  "endianness": "BIG",
  "types": {
    "Header": {
      "@type": "StructType",
      "fields": {
        "magic": { "@type": "IntElement", "size": 4 },
        "version": { "@type": "IntElement", "size": 2 }
      }
    }
  },
  "schema": {
    "header": { "@type": "TypedElement", "typeName": "Header" },
    "dataLength": { "@type": "IntElement", "size": 4 },
    "data": { "@type": "VariableRefSizeBlobElement", "sizeRef": "dataLength" }
  }
}
```

## Template Schema

Full JSON schema: `src/main/resources/alahex.schema.json`
