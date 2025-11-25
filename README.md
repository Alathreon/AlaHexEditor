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

### Supported Elements

- **Primitives**: IntElement, FloatElement, BoolElement
- **Strings**: NullEndStringElement, FixedSizeStringElement, VariableSizeStringElement
- **Arrays**: Fixed/variable/dynamic size arrays with flexible element types
- **Blobs**: Raw byte sequences with fixed or variable sizes
- **Computed**: ComputedIntElement, ComputedStringFormatElement (derived values)
- **References**: ArrayReferenceElement (index into parsed arrays)

### Supported Types

- **StructType**: Named fields parsed sequentially
- **EnumType**: Integer to string mappings
- **BitsetType**: Bit flags with named constants
- **UnionType**: Discriminated unions with classifiers

### Integer Formats

Integers in templates support multiple formats:

- Decimal: `42`
- Hex: `"0x2A"`
- Binary: `"0b101010"`
- ASCII char: `"'*'"`

## Template Schema

Full JSON schema: `src/main/resources/alahex.schema.json`

## Architecture

- `HexEditorController`: Main UI controller managing TableView (hex display) and TreeView (parsed data)
- `Template`: Schema definition loaded from JSON
- `Parser`: Converts binary data into ParseObject hierarchy using templates
- `SchemaElement`/`SchemaType`: Interfaces for parseable elements and type definitions
- `ByteView`: Wrapper for byte arrays with offset tracking and subview support
