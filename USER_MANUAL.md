# Summary

- **[Using the app](#using-the-app)**
  - **[File actions](#file-actions)**
    - **[Create a file](#create-a-file)**
    - **[Load a file](#load-a-file)**
    - **[Save in a file](#save-in-a-file)**
    - **[Quit](#quit)**
    - **[The view](#the-view)**
  - **[Editor Actions](#editor-actions)**
    - **[Edition](#edit)**
    - **[Selection](#selection)**
    - **[Clipboard](#clipboard)**

- **[The parser](#the-parser)**
  - **[Idea](#idea)**
  - **[Format](#format)**
    - **[Integer](#integer)**
  - **[Types](#types)**
    - **[StructType](#structtype)**
    - **[EnumType](#enumtype)**
    - **[BitsetType](#bitsettype)**
    - **[UnionType](#uniontype)**
      - **[SelfEnumClassifier](#selfenumclassifier)**
      - **[EnumClassifier](#enumclassifier)**
      - **[IntRangeClassifier](#intrangeclassifier)**
      - **[ArrayStringRefClassifier](#arraystringrefclassifier)**
  - **[Elements](#elements)**
    - **[FixedSizeBlobElement](#fixedsizeblobelement)**
    - **[IntElement](#intelement)**
    - **[FloatElement](#floatelement)**
    - **[NullEndStringElement](#nullendstringelement)**
    - **[FixedSizeStringElement](#fixedsizestringelement)**
    - **[VariableSizeStringElement](#variablesizestringelement)**
    - **[FixedSizeArrayElement](#fixedsizearrayelement)**
    - **[VariableSizeArrayElement](#variablesizearrayelement)**
    - **[VariableRefArrayElement](#variablerefarrayelement)**
    - **[DynamicSizeArrayElement](#dynamicsizearrayelement)**
    - **[ArrayReferenceElement](#arrayreferenceelement)**
    - **[ComputedIntElement](#computedintelement)**
    - **[ComputedStringFormatElement](#computedstringformatelement)**

---

# Using the app

Note that shortcuts to buttons are displayed on the buttons themselves

## File actions

### Create a file

1. File -> New
2. Edit it
3. Then save it

### Load a file

* File -> Open... -> select file will open the corresponding file
- File -> Open Recent -> select file will open a recently opened file
- File -> Open template... -> select a file will open a template
- File -> Open Recent Template -> select a file will open a recently opened template
- By default, when the app opens, it will automatically load the first file and the first template in the recent list

### Save in a file

* File -> Save
- File -> Save as...

### Quit

* File -> Quit

### The view

#### Middle view

* Left and up -> the byte index
- Middle -> the data
- Right -> data as ASCII string, any character whose ascii is equal or less than 32 is visually replaced by a dot

#### Right view

Displays the parsed data in a tree like structure

## Editor actions

### Edit

* Double-click a cell -> start editing
- When a third character is entered, it will be automatically be written in the next cell
- Using the wheel to increase or decrease a value by 1
- using the wheel + control to increase or decrease a value by 16

#### End of file

At the end of the file, there will be blank spaces.
It is possible to fill them by writing something in them,
which would have the effect of lengthening the file.
If a non-adjacent blank is written, all the blanks between the previous end of file and the written blank will be filed with 0

### Selection

* Selection can be done either via arrow keys or via left click.
- Multiple selection can be done by holding control
- Selecting a range of values can be done by holding shift
- Edit -> Select All to select everything
- Edit -> Unselect All to unselect everything

### Clipboard

When copying bytes, it will copy them in this format:

```hex
00000000  70 65 20 69 6E 74 65 62  65 72 2C 0D 0A 20 20 20    |pe.inteber,.....|
00000010  20 69 74 65 6D 5F 69 64  20 69 6E 74 65 67 65 72    |.item_id.integer|
00000020  2C 0D 0A 20 20 20 20 71  75 61 6E 74 69 74 79 20    |,......quantity.|
00000030  69 6E 74 65 67 65 72 2C  0D 0A 20 20 20 20 66 70    |integer,......fp|
```

When pasting, it will expect this format too, if it can't find this format, it will paste the raw bytes.

- Edit -> Cut to copy and delete selected bytes (it doesn't set to 0, it deletes)
- Edit -> Copy to copy selected bytes
- Edit -> Paste to paste in front of the first selected cell
- Edit -> Delete to delete a selection (it doesn't set to 0, it deletes)

---

# The parser

## Idea

It uses a declarative format using json to parse a file, you will have to declare types and elements.
There is a schema file available locally [here](src/main/resources/alahex.schema.json) or [in the web](https://github.com/Alathreon/AlaHexEditor/blob/master/src/main/resources/alahex.schema.json)

```json
{
    "$schema": "https://github.com/Alathreon/AlaHexEditor/blob/master/src/main/resources/alahex.schema.json",
    "references": [
        "https://myreference.com" // Or any other reference
    ],
    "endianness": "BIG", // Or "LITTLE"
    "types": // See types
    "schema": // See elements
}
```

## Format

Some attributes can have special format.

### Integer

Any attribute with the type Integer (not the set 1, 2, 4, etc) can be written in the following ways:

- Integer: 5, 0, -9, etc
- Hexadecimal: "0x05", "0xA5", "0xFF", etc
- Binary: "0b01", "0b11", "0b1011", etc
- ASCI Character: "'a'", "'B'", "' '", "'\n'", etc

## Types

```json
{
    "types": {
        "Person": {
            "@type": "StructType",
            "members": {
                "name": {
                    "@type": "NullEndStringElement"
                },
                "age": {
                    "@type": "IntElement",
                    "size": 2
                }
            }
        }
    }
}
```

A list of types can be declared in the format "name of type": { specification }.
The specification must include a "@type" element which will determine which kind of type to declare.

### StructType

Defines a struct type, fields are described in the members element in the same way as defined in the Schema element part.

| Name    | Type            | Default value if optional | Description                                                        |
|---------|-----------------|---------------------------|--------------------------------------------------------------------|
| members | Schema elements |                           | Fields of the struct, in the same format as in schema element.     |

#### Example

```json
{
    "Person": {
        "@type": "StructType",
        "members": {
            "name": {
                "@type": "NullEndStringElement"
            },
            "age": {
                "@type": "IntElement",
                "size": 2
            }
        }
    }
}
```

```
0x416E61000009
↓
name = 0x416E6100 = Ana
age = 0x0009 = 9
```

### EnumType

Defines an enum type.

| Name      | Type                                              | Default value if optional | Description                                                                                                      |
|-----------|---------------------------------------------------|---------------------------|------------------------------------------------------------------------------------------------------------------|
| size      | 1, 2, 4                                           |                           | Size in bytes of the integer constant.                                                                           |
| constants | Array of {"key": IntCode, "value": "StringName" } |                           | Array of {"key": IntCode, "value": "StringName" } representing the bindings int constant -> name. Order is kept. |

#### Example

```json
{
    "Color": {
        "@type": "EnumType",
        "size": 1,
        "constants": [
            { "key":  0, "value":  "Black" },
            { "key":  1, "value":  "White" }
        ]
    }
}
```

```
Ox01 -> Black
0x02 -> White
```

### BitsetType

Define a bitset type, where each bit correspond to a constant.

| Name  | Type             | Default value if optional | Description                                                                                      |
|-------|------------------|---------------------------|--------------------------------------------------------------------------------------------------|
| names | Array of strings |                           | An array of names, where each index is the index of the bit corresponding to the constant name.  |

#### Example

```json
{
    "Color": {
        "@type": "BitsetType",
        "size": 1,
        "names": [
            "Black", "White"
        ]
    }
}
```

```
Ob01 -> Black
0b10 -> White
0b11 -> Black | White
```

### UnionType

Define a union type, with a classifier which determines which struct to use for each case.

| Name        | Type                        | Default value if optional | Description           |
|-------------|-----------------------------|---------------------------|-----------------------|
| classifier  | A classifier, see next part |                           | Defines a classifier. |

#### Classifier

The classifier determines how to select the type to use.
The classifier must have a "@type" tag which indicates which classifier to use.

##### SelfEnumClassifier

Classify by using a self-contained enum, the name of the constant is the name of type to use.

| Name      | Type                                               | Default value if optional | Description                                                                                                      |
|-----------|----------------------------------------------------|---------------------------|------------------------------------------------------------------------------------------------------------------|
| size      | 1, 2, 4                                            |                           | Size in bytes of the integer constant.                                                                           |
| constants | Array of {"key": IntCode, "value": "StringName" }  |                           | Array of {"key": IntCode, "value": "StringName" } representing the bindings int constant -> name. Order is kept. |

###### Example

```json
{
    "types": {
        "IntegerEntry": {
            "@type": "StructType",
            "members": {
                "value": {
                    "@type": "IntElement",
                    "size": 4
                }
            }
        },
        "StringEntry": {
            "@type": "StructType",
            "members": {
                "value": {
                    "@type": "NullEndStringElement"
                }
            }
        },
        "Entry": {
            "@type": "UnionType",
            "classifier": {
                "@type": "SelfEnumClassifier",
                "size": 1,
                "constants": [
                    { "key": 0, "value": "IntegerEntry" },
                    { "key": 1, "value": "StringEntry" }
                ]
            }
        }
    },
    "schema": {
        "entry": {
            "@type": "TypeElement",
            "name": "Entry"
        }
    }
}
```

```
0x0000000009
↓
entry = Entry (
    tag = 0 = IntegerEntry
    data = IntegerEntry (
        value = 0x00000009 = 9
    )
)
```

```
0x01416E6100
↓
entry = Entry (
    tag = 1 = StringEntry
    data = StringEntry (
        value = 0x416E6100 = Ana
    )
)
```

##### EnumClassifier

Classify according to an enum like classifier which uses an already defined enum.

| Name        | Type    | Default value if optional | Description                                                       |
|-------------|---------|---------------------------|-------------------------------------------------------------------|
| enumVarName | String  |                           | The name of the variable that contains the enum, must be defined. |

###### Example

```json
{
    "types": {
        "TagKind": {
            "@type": "EnumType",
            "size": 1,
            "constants": [
                { "key":  0, "value":  "IntegerEntry" },
                { "key":  1, "value":  "StringEntry" }
            ]
        },
        "IntegerEntry": {
            "@type": "StructType",
            "members": {
                "value": {
                    "@type": "IntElement",
                    "size": 4
                }
            }
        },
        "StringEntry": {
            "@type": "StructType",
            "members": {
                "value": {
                    "@type": "NullEndStringElement"
                }
            }
        },
        "Entry": {
            "@type": "UnionType",
            "classifier": {
                "@type": "EnumClassifier",
                "enumVarName": "tag"
            }
        }
    },
    "schema": {
        "tag": {
            "@type": "TypeElement",
            "name": "TagKind"
        },
        "entry": {
            "@type": "TypeElement",
            "name": "Entry"
        }
    }
}
```

```
0x0000000009
↓
tag = TagKind(IntegerEntry)
entry = IntegerEntry (
    value = 0x00000009 = 9
)
```

```
0x01416E6100
↓
tag = TagKind(StringEntry)
entry = StringEntry (
    value = 0x416E6100 = Ana
)
```

##### IntRangeClassifier

Classify according to an integer range, that will select type based on multiple conditions.

| Name     | Type                                                 | Default value if optional | Description                                                                                                                      |
|----------|------------------------------------------------------|---------------------------|----------------------------------------------------------------------------------------------------------------------------------|
| size     | 1, 2, 4                                              |                           | Size in bytes of the integer.                                                                                                    |
| bindings | Array of {"rangeStart": Int, "rangeEnd": Int, "type" : String } |                           | Array of {"rangeStart": Int, "rangeEnd": Int, "type" : String } representing the bindings of range to type. Upper bound is included. |

###### Example

```json
{
    "types": {
        "IntegerEntry": {
            "@type": "StructType",
            "members": {
                "value": {
                    "@type": "IntElement",
                    "size": 4
                }
            }
        },
        "StringEntry": {
            "@type": "StructType",
            "members": {
                "value": {
                    "@type": "NullEndStringElement"
                }
            }
        },
        "Entry": {
            "@type": "UnionType",
            "classifier": {
                "@type": "IntRangeClassifier",
                "size": 1,
                "bindings": [
                    { "rangeStart": 0, "rangeEnd": 10, "type": "IntegerEntry" },
                    { "rangeStart": 11, "rangeEnd": 20, "type": "StringEntry" }
                ]
            }
        }
    },
    "schema": {
        "entry": {
            "@type": "TypeElement",
            "name": "Entry"
        }
    }
}
```

```
0x0500000009
↓
entry = Entry (
    tag = 5
    data = IntegerEntry (
        value = 0x00000009 = 9
    )
)
```

```
0x0F416E6100
↓
entry = Entry (
    tag = 15
    data = StringEntry (
        value = 0x416E6100 = Ana
    )
)
```

##### ArrayStringRefClassifier

Classify according to an array of string, where each string is the name of a type.

| Name        | Type   | Default value if optional | Description                                                                                                      |
|-------------|--------|---------------------------|------------------------------------------------------------------------------------------------------------------|
| size        | 1, 2, 4 |                           | Size in bytes of the integer tag used as an index.                                                               |
| arrayVarName | String |                           | Array of string that stores the name of each type that can be used, must be defined before. |
| zeroIsNull  | Boolean | false                     | If false, nothing special. If true, 0 means null and will not correspond to any type, while any other index will be reduced by 1 to correspond to the array. So 0 -> null, 1 -> 0, 2 -> 1, etc. |

###### Example

```json
{
    "types": {
        "IntegerEntry": {
            "@type": "StructType",
            "members": {
                "value": {
                    "@type": "IntElement",
                    "size": 4
                }
            }
        },
        "StringEntry": {
            "@type": "StructType",
            "members": {
                "value": {
                    "@type": "NullEndStringElement"
                }
            }
        },
        "Entry": {
            "@type": "UnionType",
            "classifier": {
                "@type": "ArrayStringRefClassifier",
                "size": 1,
                "arrayVarName": "list"
            }
        }
    },
    "schema": {
        "list": {
            "@type": "FixedSizeArrayElement",
            "size": 2,
            "schema": {
                "@type": "NullEndStringElement"
            }
        },
        "entry": {
            "@type": "TypeElement",
            "name": "Entry"
        }
    }
}
```

```
0x496e7465676572456e74727900537472696e67456e7472790000000009
↓
0x496e7465676572456e74727900 = "IntegerEntry" (the string value)
0x537472696e67456e74727900 = "StringEntry" (the string value)
list = [IntegerEntry, StringEntry]
0x00 = tag 0 = IntegerEntry
entry = IntegerEntry (
    value = 0x00000009 = 9
)
```

```
0x496e7465676572456e74727900537472696e67456e74727900416E6100
↓
0x496e7465676572456e74727900 = "IntegerEntry" (the string value)
0x537472696e67456e74727900 = "StringEntry" (the string value)
list = [IntegerEntry, StringEntry]
0x01 = tag 1 = StringEntry
entry = StringEntry (
    value = 0x416E6100 = Ana
)
```

###### Example zero is null

```json
{
    "types": {
        "IntegerEntry": {
            "@type": "StructType",
            "members": {
                "value": {
                    "@type": "IntElement",
                    "size": 4
                }
            }
        },
        "StringEntry": {
            "@type": "StructType",
            "members": {
                "value": {
                    "@type": "NullEndStringElement"
                }
            }
        },
        "Entry": {
            "@type": "UnionType",
            "classifier": {
                "@type": "ArrayStringRefClassifier",
                "size": 1,
                "arrayVarName": "list",
                "zeroIsNull": true
            }
        }
    },
    "schema": {
        "list": {
            "@type": "FixedSizeArrayElement",
            "size": 2,
            "schema": {
                "@type": "NullEndStringElement"
            }
        },
        "entry": {
            "@type": "TypeElement",
            "name": "Entry"
        }
    }
}
```

```
0x496e7465676572456e74727900537472696e67456e74727900
↓
0x496e7465676572456e74727900 = "IntegerEntry" (the string value)
0x537472696e67456e74727900 = "StringEntry" (the string value)
list = [IntegerEntry, StringEntry]
0x00 = tag 0 = null
entry = null
```

```
0x496e7465676572456e74727900537472696e67456e7472790001416E6100
↓
0x496e7465676572456e74727900 = "IntegerEntry" (the string value)
0x537472696e67456e74727900 = "StringEntry" (the string value)
list = [IntegerEntry, StringEntry]
0x01 = tag 1 - 1 = 0 = IntegerEntry
entry = IntegerEntry (
    value = 0x00000009 = 9
)
```

```
0x496e7465676572456e74727900537472696e67456e7472790002416E6100
↓
0x496e7465676572456e74727900 = "IntegerEntry" (the string value)
0x537472696e67456e74727900 = "StringEntry" (the string value)
list = [IntegerEntry, StringEntry]
0x02 = tag 2 - 1 = 1 = StringEntry
entry = StringEntry (
    value = 0x416E6100 = Ana
)
```

## Elements

Every schema element is identified by a "@type" tag which indicates which parser element to use.

### IntElement

Element that represents an integer.

| Name       | Type                                              | Default value if optional | Description                                                                                                               |
|------------|---------------------------------------------------|---------------------------|---------------------------------------------------------------------------------------------------------------------------|
| size       | 1, 2, 4, 8                                        |                           | Size in bytes of the integer.                                                                                             |
| signed     | boolean                                           | true                      | Whether the integer is signed.                                                                                            |
| constant   | Integer                                           | null                      | If provided, will check to see if the parsed value matches the constant, and will throw an error if not.                  |
| expression | String                                            | null                      | If provided, will parse the value in postfix notation, which can be used to perform integer operations on the parsed int. |

#### Example simple

```json
{
    "int_element": {
        "@type": "IntElement",
        "size": 2
    }
}
```

```
0x0009
↓
value = 9
```

#### Example constant

```json
{
    "int_element": {
        "@type": "IntElement",
        "size": 2,
        "constant": 9
    }
}
```

```
0x0009
↓
value = 9
```

```
0x0008
↓
throws exception
```

#### Example expression

```json
{
    "int_element": {
        "@type": "IntElement",
        "size": 1,
        "expression": "x 2 +"
    }
}
```

```
0x05
↓
value = 5 + 2 = 7
```

#### Example unsigned

```json
{
    "int_element": {
        "@type": "IntElement",
        "size": 1,
        "signed": false
    }
}
```

```
0xFF
↓
value = 255
```

### FloatElement

Element that represents a float.

| Name     | Type    | Default value if optional | Description                                                                   |
|----------|---------|---------------------------|-------------------------------------------------------------------------------|
| size     | 4, 8    |                           | Size in bytes of the float. 4 for float, 8 for double.                        |
| constant | Float   | null                      | If provided, check if parsed value matches, throws error if not.              |

#### Example

```json
{
    "float_element": {
        "@type": "FloatElement",
        "size": 4
    }
}
```

```
0x40490FDB
↓
value = 3.14159274
```

### BoolElement

Element that represents a boolean.

| Name          | Type     | Default value if optional | Description                                                                                |
|---------------|----------|---------------------------|--------------------------------------------------------------------------------------------|
| size          | 1, 2, 4  |                           | Size in bytes of the boolean.                                                              |
| trueConstant  | Integer  | 1                         | The value considered to be true.                                                           |
| falseConstant | Integer  | 0                         | The value considered to be false.                                                          |

#### Example

```json
{
    "bool_element": {
        "@type": "BoolElement",
        "size": 1
    }
}
```

```
0x01
↓
value = true
```

```
0x00
↓
value = false
```

### NullEndStringElement

A null-terminated string element.

| Name    | Type    | Default value if optional | Description                                                              |
|---------|---------|---------------------------|--------------------------------------------------------------------------|
| charset | String  | "ASCII"                   | The character set encoding of the string (e.g. "UTF-8", "ASCII", etc).   |

#### Example

```json
{
    "string_element": {
        "@type": "NullEndStringElement"
    }
}
```

```
0x416E6100
↓
value = 0x416E6100 = "Ana"
```

### FixedSizeStringElement

String with a fixed size in bytes.

| Name    | Type    | Default value if optional | Description                                                              |
|---------|---------|---------------------------|--------------------------------------------------------------------------|
| size    | Integer |                           | The size of the string in bytes                                          |
| charset | String  | "ASCII"                   | The character set encoding of the string (e.g. "UTF-8", "ASCII", etc).   |

#### Example

```json
{
    "string_element": {
        "@type": "FixedSizeStringElement",
        "size": 3
    }
}
```

```
0x416E61
↓
value = 0x416E61 = "Ana"
```

### VariableSizeStringElement

A string whose size depends on a size parsed just before it.

| Name      | Type    | Default value if optional | Description                                                              |
|-----------|---------|---------------------------|--------------------------------------------------------------------------|
| fieldSize | 1, 2, 4 |                           | The size of the size attribute in bytes                                  |
| charset   | String  | "ASCII"                   | The character set encoding of the string (e.g. "UTF-8", "ASCII", etc).   |

#### Example

```json
{
    "string_element": {
        "@type": "VariableSizeStringElement",
        "fieldSize": 1
    }
}
```

```
0x03416E61
↓
size = 3
value = 0x416E61 = "Ana"
```

### VariableRefSizeStringElement

String whose size depends on the size in another variable.

| Name        | Type    | Default value if optional | Description                                                                   |
|-------------|---------|---------------------------|-------------------------------------------------------------------------------|
| sizeVarName | String  |                           | The name of the variable which stores the size of this string. Cannot be forward reference. |
| charset     | String  | "ASCII"                   | The character set encoding of the string (e.g. "UTF-8", "ASCII", etc).        |

#### Example

```json
{
    "string_size": {
        "@type": "IntElement",
        "size": 1
    },
    "string_element": {
        "@type": "VariableRefSizeStringElement",
        "sizeVarName": "string_size"
    }
}
```

```
0x03416E61
↓
string_size = 3
value = 0x416E61 = "Ana"
```

### FixedSizeArrayElement

Array with a fixed amount of elements.

| Name   | Type            | Default value if optional | Description                        |
|--------|-----------------|---------------------------|------------------------------------|
| size   | Integer         |                           | The number of elements in the array |
| schema | Schema elements |                           | The schema to parse at each index. |

#### Example

```json
{
    "array": {
        "@type": "FixedSizeArrayElement",
        "size": 3,
        "schema": {
            "@type": "IntElement",
            "size": 1
        }
    }
}
```

```
0x050203
↓
[5, 2, 3]
```

### VariableSizeArrayElement

Array whose size depends on a size parsed just before it.

| Name      | Type            | Default value if optional | Description                             |
|-----------|-----------------|---------------------------|-----------------------------------------|
| fieldSize | 1, 2, 4         |                           | The size of the size attribute in bytes |
| schema    | Schema elements |                           | The schema to parse at each index.      |

#### Example

```json
{
    "array": {
        "@type": "VariableSizeArrayElement",
        "fieldSize": 1,
        "schema": {
            "@type": "IntElement",
            "size": 1
        }
    }
}
```

```
0x03050203
↓
size = 3
[5, 2, 3]
```

### VariableRefSizeArrayElement

Array whose size depends on the size in another variable.

| Name        | Type            | Default value if optional | Description                                                                    |
|-------------|-----------------|---------------------------|--------------------------------------------------------------------------------|
| sizeVarName | String          |                           | The name of the variable which stores the size of this array. Cannot be forward reference. |
| schema      | Schema elements |                           | The schema to parse at each index.                                             |

#### Example

```json
{
    "array_size": {
        "@type": "IntElement",
        "size": 1
    },
    "array": {
        "@type": "VariableRefSizeArrayElement",
        "sizeVarName": "array_size",
        "schema": {
            "@type": "IntElement",
            "size": 1
        }
    }
}
```

```
0x03050203
↓
array_size = 3
[5, 2, 3]
```

### DynamicSizeArrayElement

Array that keeps parsing elements until the given comparison is satisfied.

| Name        | Type                | Default value if optional | Description                                                                                                                        |
|-------------|---------------------|---------------------------|------------------------------------------------------------------------------------------------------------------------------------|
| against     | Integer             |                           | The integer value to compare against.                                                                                              |
| operator    | Comparison operator |                           | The operator to use among EQUALS, NOT_EQUALS, LESS_THAN, LESS_THAN_OR_EQUALS, MORE_THAN, MORE_THAN_OR_EQUALS.                     |
| schema      | Schema elements     |                           | The schema to parse at each index.                                                                                                 |
| structField | Schema elements     | null                      | If the schema is a struct, the field to use for the comparison.                                                                    |

#### Example simple int array

```json
{
    "array": {
        "@type": "DynamicSizeArrayElement",
        "against": 0,
        "operator": "NOT_EQUALS",
        "schema": {
            "@type": "IntElement",
            "size": 1
        }
    }
}
```

```
0x050203040100
↓
[5, 2, 3, 4, 1, 0]
```

#### Example struct array

```json
{
    "types": {
        "Pair": {
            "@type": "StructType",
            "members": {
                "a": {
                    "@type": "IntElement",
                    "size": 1
                },
                "b": {
                    "@type": "IntElement",
                    "size": 1
                }
            }
        }
    },
    "schema": {
        "array": {
            "@type": "DynamicSizeArrayElement",
            "against": 0,
            "operator": "NOT_EQUALS",
            "structField": "b",
            "schema": {
                "@type": "TypeElement",
                "name": "Pair"
            }
        }
    }
}
```

```
0x050203040100
↓
[Pair(a=5, b=2), Pair(a=3, b=4), Pair(a=1, b=0)]
```

### FixedSizeBlobElement

Element that can be used by default to take the bytes as is.

| Name    | Type    | Default value if optional | Description                   |
|---------|---------|---------------------------|-------------------------------|
| size    | Integer |                           | The size of the blob in bytes |

#### Example

```json
{
    "array": {
        "@type": "FixedSizeBlobElement",
        "size": 4
    }
}
```

```
0x416E61
↓
value = 0x416E61
```

### VariableSizeBlobElement

A blob whose size depends on the size parsed just before it.

| Name        | Type    | Default value if optional | Description                             |
|-------------|---------|---------------------------|-----------------------------------------|
| fieldSize   | 1, 2, 4 |                           | The size of the size attribute in bytes |

#### Example

```json
{
    "array": {
        "@type": "VariableSizeBlobElement",
        "fieldSize": 1
    }
}
```

```
0x03416E61
↓
size = 3
value = 0x416E61
```

### VariableRefSizeBlobElement

Blob whose size depends on the size in another variable.

| Name        | Type            | Default value if optional | Description                                                                                 |
|-------------|-----------------|---------------------------|---------------------------------------------------------------------------------------------|
| sizeVarName | String          |                           | The name of the variable which stores the size of this blob. Cannot be a forward reference. |

#### Example

```json
{
    "blob_size": {
        "@type": "IntElement",
        "size": 1
    },
    "array": {
        "@type": "VariableRefSizeBlobElement",
        "sizeVarName": "blob_size"
    }
}
```

```
0x0408060902
↓
blob_size = 4
value = 0x08060902
```

### ArrayReferenceElement

References an array by the given variable to the given index.

| Name       | Type        | Default value if optional | Description                                                                                                                                                                                     |
|------------|-------------|---------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| size       | 1, 2, 4     |                           | Size in bytes of the integer index.                                                                                                                                                             |
| variable   | String      |                           | Variable containing the array to use.                                                                                                                                                           |
| zeroIsNull | Boolean     | false                     | If false, nothing special. If true, 0 means null and will not correspond to any type, while any other index will be reduced by 1 to correspond to the array. So 0 -> null, 1 -> 0, 2 -> 1, etc. |

#### Example

```json
{
    "list": {
        "@type": "FixedSizeArrayElement",
        "fieldSize": 1,
        "schema": {
            "@type": "NullEndStringElement"
        }
    },
    "reference": {
        "@type": "ArrayReferenceElement",
        "size": 1,
        "variable": "list"
    }
}
```

```
0x02496e7465676572456e74727900537472696e67456e7472790001
↓
0x01 = length 1
0x496e7465676572456e74727900 = "IntegerEntry" (the string value)
0x537472696e67456e74727900 = "StringEntry" (the string value)
list = [IntegerEntry, StringEntry]
reference = 0x01 -> "StringEntry"
```

#### Example zero is null

```json
{
    "list": {
        "@type": "FixedSizeArrayElement",
        "fieldSize": 1,
        "schema": {
            "@type": "NullEndStringElement"
        }
    },
    "reference": {
        "@type": "ArrayReferenceElement",
        "size": 1,
        "variable": "list",
        "zeroIsNull": true
    }
}
```

```
0x02496e7465676572456e74727900537472696e67456e7472790000
↓
0x01 = length 1
0x496e7465676572456e74727900 = "IntegerEntry" (the string value)
0x537472696e67456e74727900 = "StringEntry" (the string value)
list = [IntegerEntry, StringEntry]
reference = 0x00 -> null
```

```
0x02496e7465676572456e74727900537472696e67456e7472790001
↓
0x01 = length 1
0x496e7465676572456e74727900 = "IntegerEntry" (the string value)
0x537472696e67456e74727900 = "StringEntry" (the string value)
list = [IntegerEntry, StringEntry]
reference = 0x01 -> IntegerEntry
```

### ComputedIntElement

Compute integer operations in postfix notation.

| Name        | Type            | Default value if optional | Description                                                                                                                 |
|-------------|-----------------|---------------------------|-----------------------------------------------------------------------------------------------------------------------------|
| variables   | Array of String |                           | Array of variables used to find the integer, if more than one variable, the other should be struct fields like foo.bar.baz. |
| expression  | String          | "x"                       | The expression to calculate in postfix notation, with the referenced variable being called "x".                             |

#### Example simple

```json
{
    "int_element": {
        "@type": "IntElement",
        "size": 1
    },
    "display": {
        "@type": "ComputedIntElement",
        "variables": [ "int_element" ]
    }
}
```

```
0x05
↓
int_element = 5
display = 5
```

#### Example expression

```json
{
    "int_element": {
        "@type": "IntElement",
        "size": 1
    },
    "display": {
        "@type": "ComputedIntElement",
        "variables": [ "int_element" ],
        "expression": "x 3 *"
    }
}
```

```
0x05
↓
int_element = 5
display = int_element * 3 = 15
```

#### Example struct

```json
{
    "types": {
        "Pair": {
            "@type": "StructType",
            "members": {
                "a": {
                    "@type": "IntElement",
                    "size": 1
                },
                "b": {
                    "@type": "IntElement",
                    "size": 1
                }
            }
        }
    },
    "schema": {
        "pair": {
            "@type": "TypeElement",
            "name": "Pair"
        },
        "display": {
            "@type": "ComputedIntElement",
            "variables": [ "pair", "b" ]
        }
    }
}
```

```
0x0205
↓
Pair(a=2, b=5)
display = pair.b = 5
```

### ComputedStringFormatElement

Format a string.

| Name   | Type            | Default value if optional | Description                                                                                                                                       |
|--------|-----------------|---------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------|
| format | String          |                           | The format string, in the same syntax as [Java Formatter](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/Formatter.html). |
| items  | Array of String |                           | Array of variables to inject into the format.                                                                                                     |

#### Example

```json
{
    "name": {
        "@type": "NullEndStringElement"
    },
    "age": {
        "@type": "IntElement",
        "size": 1
    },
    "display": {
        "@type": "ComputedStringFormatElement",
        "format": "Name: %s, age: %d.",
        "items": [ "name", "age" ]
    }
}
```

```
0x416E61000F
↓
name = "Ana"
age = 15
display = "Name: Ana, age: 15."
```
