# Summary

- **[Using the app](#Using-the-app)**
    - **[File actions](#File-actions)**
        - **[Create a file](#Create-a-file)**
        - **[Load a file](#Load-a-file)**
        - **[Save in a file](#Save-in-a-file)**
        - **[Quit](#Quit)**
        - **[The view](#The-view)**
    - **[Editor Actions](#Editor-actions)**
        - **[Edition](#Edit)**
        - **[Selection](#Selection)**
        - **[Clipboard](#Clipboard)**

- **[The parser](#The-parser)**
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
        - **[IntElement](#intelement)**
        - **[FloatElement](#floatelement)**
        - **[NullEndStringElement](#nullendstringelement)**
        - **[FixedSizeStringElement](#fixedsizestringelement)**
        - **[VariableSizeStringElement](#variablesizestringelement)**
        - **[FixedSizeArrayElement](#fixedsizearrayelement)**
        - **[VariableSizeArrayElement](#variablesizearrayelement)**
        - **[VariableRefSizeArrayElement](#variablerefsizearrayelement)**
        - **[DynamicSizeArrayElement](#dynamicsizearrayelement)**
        - **[FixedSizeBlobElement](#fixedsizeblobelement)**
        - **[VariableSizeBlobElement](#variablesizeblobelement)**
        - **[VariableRefSizeBlobElement](#variablerefsizeblobelement)**
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
* File -> Open Recent -> select file will open a recently opened file
* File -> Open template... -> select a file will open a template
* File -> Open Recent Template -> select a file will open a recently opened template
* By default, when the app opens, it will automatically load the first file and the first template in the recent list

### Save in a file
* File -> Save
* File -> Save as...

### Quit
* File -> Quit

### The view
#### Middle view
* Left and up -> the byte index
* Middle -> the data
* Right -> data as ASCII string, any character whose ascii is equal or less than 32 is visually replaced by a dot

#### Right view
Displays the parsed data in a tree like structure

## Editor actions

### Edit
* Double-click a cell -> start editing
* When a third character is entered, it will be automatically be written in the next cell
* Using the wheel to increase or decrease a value by 1
* using the wheel + control to increase or decrease a value by 16

#### End of file
At the end of the file, there will be blank spaces.
It is possible to fill them by writing something in them,
which would have the effect of lengthening the file.
If a non-adjacent blank is written, all the blanks between the previous end of file and the written blank will be filed with 0

### Selection
* Selection can be done either via arrow keys or via left click.
* Multiple selection can be done by holding control
* Selecting a range of values can be done by holding shift
* Edit -> Select All to select everything
* Edit -> Unselect All to unselect everything

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

- $schema: the schema to verify the JSON, unused.
- references: useful references, unused.
- types: see **[Types](#types)**, default to [].
- schema: see **[Elements](#elements)**, mandatory.

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
    "types": {
        "Person": {
            "@type": "StructType",
            "members": {
                "name": {
                    "@type": "NullEndStringElement"
                },
                "age": {
                    "@type": "IntElement",
                    "size": 1
                }
            }
        }
    },
    "schema": {
        "person": {
            "@type": "TypeElement",
            "name": "Person"
        }
    }
}
```
```
0x416E61000F
↓
person = {
    name = 0x416E6100 = Ana
    age = 0x0F = 15
}
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
    "types": {
        "Color": {
            "@type": "EnumType",
            "size": 1,
            "constants": [
                { "key":  0, "value":  "Black" },
                { "key":  1, "value":  "White" }
            ]
        }
    },
    "schema": {
        "color": {
            "@type": "TypeElement",
            "name": "Color"
        }
    }
}
```
```
Ox00 -> Black
0x01 -> White
```

### BitsetType

Define a bitset type, where each bit correspond to a constant.

| Name  | Type             | Default value if optional | Description                                                                                      |
|-------|------------------|---------------------------|--------------------------------------------------------------------------------------------------|
| names | Array of strings |                           | An array of names, where each index is the index of the bit corresponding to the constant name.  |

#### Example
```json
{
    "types": {
        "Color": {
            "@type": "BitsetType",
            "size": 1,
            "names": [
                "Black", "White"
            ]
        }
    },
    "schema": {
        "color": {
            "@type": "TypeElement",
            "name": "Color"
        }
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
        "Entry": {
            "@type": "UnionType",
            "classifier": {
                "@type": "SelfEnumClassifier",
                "size": 1,
                "constants": [
                    { "key":  0, "value":  "IntegerEntry" },
                    { "key":  1, "value":  "StringEntry" }
                ]
            }
        },
        "IntegerEntry": {
            "@type": "StructType",
            "members": {
                "value": {
                    "@type": "IntElement",
                    "size": 1
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
0x0005
↓
0x00 = IntegerEntry
0x05 = 5

0x01416E6100
↓
0x01 = StringEntry
0x416E6100 = Ana
```

##### EnumClassifier
Classify by using an enum, the name of the constant is the name of type to use.

| Name                 | Type                                                    | Default value if optional | Description                                                                                          |
|----------------------|---------------------------------------------------------|---------------------------|------------------------------------------------------------------------------------------------------|
| enumClassifierName   | String                                                  |                           | The name of the enum to use.                                                                         |
| overrideBindings     | Array of {"key": IntCode, "value": "StringName" }       | []                        | Override the names of the enum, to allow to represent types that don't match the enum constant names |

###### Example
```json
{
    "types": {
        "Entry": {
            "@type": "UnionType",
            "classifier": {
                "@type": "EnumClassifier",
                "enumClassifierName": "EntryKind"
            }
        },
        "EntryKind": {
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
                    "size": 1
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
0x0005
↓
0x00 = IntegerEntry
0x05 = 5

0x01416E6100
↓
0x01 = StringEntry
0x416E6100 = Ana
```
##### IntRangeClassifier
Classify by using a range of integers. Ranges must be from smallest to largest with no intersection.

| Name          | Type                                                             | Default value if optional | Description                                                                                                                                           |
|---------------|------------------------------------------------------------------|---------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------|
| size          | 1, 2, 4                                                          |                           | Size in bytes of the integer constant.                                                                                                                |
| rangeBindings | Array of {"from": IntCode, "to": IntCode, "name": "StringName" } |                           | Array of ranges, where each range represents a constant, from is inclusive, to is exclusive, no intersection is allowed, the ranges must be in order. |

###### Example
```json
{
    "types": {
        "Entry": {
            "@type": "UnionType",
            "classifier": {
                "@type": "IntRangeClassifier",
                "size": 1,
                "rangeBindings": [
                    { "from": 0, "to":  10, "name": "IntegerEntry" },
                    { "from": 10, "to":  20, "name": "StringEntry" }
                ]
            }
        },
        "IntegerEntry": {
            "@type": "StructType",
            "members": {
                "value": {
                    "@type": "IntElement",
                    "size": 1
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
0x0505
↓
0x05 = 5 = IntegerEntry
0x05 = 5

0x0A416E6100
↓
0x0A = 10 = StringEntry
0x416E6100 = Ana

0x0F416E6100
↓
0x0F = 15 = StringEntry
0x416E6100 = Ana
```

##### ArrayStringRefClassifier
Classify by using an array of strings representing types.
Will parse an index, which will then reference a string in an array, and this string will represent the type to use.

| Name       | Type        | Default value if optional | Description                                                                                                                                                                                     |
|------------|-------------|---------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| size       | 1, 2, 4     |                           | Size in bytes of the integer index.                                                                                                                                                             |
| variable   | String      |                           | Variable containing the array to use.                                                                                                                                                           |
| zeroIsNull | Boolean     | false                     | If false, nothing special. If true, 0 means null and will not correspond to any type, while any other index will be reduced by 1 to correspond to the array. So 0 -> null, 1 -> 0, 2 -> 1, etc. |

###### Example
Structless
```json
{
    "types": {
        "Entry": {
            "@type": "UnionType",
            "classifier": {
                "@type": "ArrayStringRefClassifier",
                "size": 1,
                "variable": "list"
            }
        },
        "IntegerEntry": {
            "@type": "StructType",
            "members": {
                "value": {
                    "@type": "IntElement",
                    "size": 1
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
        }
    },
    "schema": {
        "list": {
            "@type": "VariableSizeArrayElement",
            "fieldSize": 1,
            "schema": {
                "@type": "NullEndStringElement"
            }
        },
        "entry1": {
            "@type": "TypeElement",
            "name": "Entry"
        },
        "entry2": {
            "@type": "TypeElement",
            "name": "Entry"
        }
    }
}
```
With struct
```json
{
    "types": {
        "Data": {
            "@type": "StructType",
            "members": {
                "list": {
                    "@type": "VariableSizeArrayElement",
                    "fieldSize": 1,
                    "schema": {
                        "@type": "NullEndStringElement"
                    }
                },
                "entry1": {
                    "@type": "TypeElement",
                    "name": "Entry"
                },
                "entry2": {
                    "@type": "TypeElement",
                    "name": "Entry"
                }
            }
        },
        "Entry": {
            "@type": "UnionType",
            "classifier": {
                "@type": "ArrayStringRefClassifier",
                "size": 1,
                "variable": "list"
            }
        },
        "IntegerEntry": {
            "@type": "StructType",
            "members": {
            "value": {
                "@type": "IntElement",
                "size": 1
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
        }
    },
    "schema": {
        "data": {
            "@type": "TypeElement",
            "name": "Data"
        }
    }
}
```
Both above are equivalent an can parse the following the same way:
```
0x02496e7465676572456e74727900537472696e67456e74727900000501416E6100
↓
0x02 = length 2
0x496e7465676572456e74727900 = "IntegerEntry" (the string value)
0x537472696e67456e74727900 = "StringEntry" (the string value)
list = [IntegerEntry, StringEntry]
entry1 = 0x00 -> IntegerEntry, 0x05 -> value = 5
entry2 = 0x01 -> StringEntry, 0x416E6100 -> value = "Ana"
```

###### Example zero is null
```json
{
    "types": {
        "Entry": {
            "@type": "UnionType",
            "classifier": {
                "@type": "ArrayStringRefClassifier",
                "size": 1,
                "variable": "list",
                "zeroIsNull": true
            }
        },
        "IntegerEntry": {
            "@type": "StructType",
            "members": {
                "value": {
                    "@type": "IntElement",
                    "size": 1
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
        }
    },
    "schema": {
        "list": {
            "@type": "VariableSizeArrayElement",
            "fieldSize": 1,
            "schema": {
                "@type": "NullEndStringElement"
            }
        },
        "entry1": {
            "@type": "TypeElement",
            "name": "Entry"
        },
        "entry2": {
            "@type": "TypeElement",
            "name": "Entry"
        },
        "entry3": {
            "@type": "TypeElement",
            "name": "Entry"
        }
    }
}
```
```
0x02496e7465676572456e74727900537472696e67456e747279000001416E6100
↓
0x01 = length 1
0x496e7465676572456e74727900 = "IntegerEntry" (the string value)
0x537472696e67456e74727900 = "StringEntry" (the string value)
list = [IntegerEntry, StringEntry]
entry1 = 0x00 -> null
entry2 = 0x01 -> IntegerEntry, 0x05 -> value = 5
```


## Elements

```json
{
    "schema": {
        "name": {
            "@type": "NullEndStringElement"
        },
        "age": {
            "@type": "IntElement",
            "size": 2
        }
    }
}
```
Elements are used to dictate how to parse data, the parser will read which element in order in the schema element (or in a struct members)
and will parse them, and assign them to corresponding variables.
Each element is in the form "variable": { specification }.
The specification must include at least a "@type" tag used to determine which element to use.


### IntElement

An integer.

| Name       | Type       | Default value if optional | Description                                                                                    |
|------------|------------|---------------------------|------------------------------------------------------------------------------------------------|
| size       | 1, 2, 4, 8 |                           | The size of the integer in bytes.                                                              |
| signed     | Boolean    | false                     | True if signed, false if unsigned.                                                             |
| expression | String     | "x"                       | The expression to calculate in postfix notation, with the referenced integer being called "x". |

#### Example simple
```json
{
    "schema": {
        "int_element": {
            "@type": "IntElement",
            "size": 1
        }
    }
}
```
```
0x05
↓
int_element = 5
```

#### Example signed
```json
{
    "schema": {
        "int_element": {
            "@type": "IntElement",
            "size": 1,
            "signed": true
        }
    }
}
```
```
0xFF
↓
int_element = -1
```

#### Example expression
```json
{
    "schema": {
        "int_element": {
            "@type": "IntElement",
            "size": 1,
            "expression": "x 3 *"
        }
    }
}
```
```
0x05
↓
int_element = 5 * 3 = 15
```


### BoolElement

A boolean.

| Name       | Type       | Default value if optional | Description                                                                                    |
|------------|------------|---------------------------|------------------------------------------------------------------------------------------------|

#### Example simple
```json
{
    "schema": {
        "bool_element": {
            "@type": "BoolElement"
        }
    }
}
```
```
0x00
↓
bool_element = false

0x01
↓
bool_element = true

0x02
↓
bool_element = true
```


### FloatElement

A float, following those specifications:

| Size | Exponent | Mantissa | Notes                     |
|------|----------|----------|---------------------------|
| 1    | 4        | 3        | Minifloat                 |
| 2    | 5        | 10       | IEEE 754 half-precision   |
| 4    | 8        | 23       | IEEE 754 single-precision |
| 8    | 11       | 52       | IEEE 754 double-precision |

| Name       | Type       | Default value if optional | Description                     |
|------------|------------|---------------------------|---------------------------------|
| size       | 1, 2, 4, 8 |                           | The size of the float in bytes. |

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
0x42280000
↓
float_element = 42
```


### NullEndStringElement

A string ended by the null character (`\\0`).

| Name        | Type    | Default value if optional | Description                             |
|-------------|---------|---------------------------|-----------------------------------------|
| charset     | Charset | "UTF-8"                   | The charset to use.                     |

#### Example
```json
{
    "schema": {
        "string": {
            "@type": "NullEndStringElement"
        }
    }
}
```
```
0x416E6100
↓
string = "Ana"
```

#### Example SJIS charset
```json
{
    "schema": {
        "string": {
            "@type": "NullEndStringElement",
            "charset": "SJIS"
        }
    }
}
```
```
0x82B182F182C982BF82CD00
↓
string = "こんにちは"
```


### FixedSizeStringElement

A string whose size is fixed.

| Name       | Type    | Default value if optional | Description                                                                                           |
|------------|---------|---------------------------|-------------------------------------------------------------------------------------------------------|
| size       | Integer |                           | The size of the string in bytes                                                                       |
| charset    | Charset | "UTF-8"                   | The charset to use.                                                                                   |
| stopAtNull | Boolean | false                     | If true, will read the "size" characters but will only form a string until a null character is found. |

#### Example
```json
{
    "schema": {
        "string": {
            "@type": "FixedSizeStringElement",
            "size": 3
        }
    }
}
```
```
0x416E61
↓
string = "Ana"
```

#### Example
```json
{
    "schema": {
        "string": {
            "@type": "FixedSizeStringElement",
            "size": 5,
            "stopAtNull": true
        }
    }
}
```
```
0x416E610000
↓
parsed = 416E610000
actual string = 416E61
string = "Ana"
```


### VariableSizeStringElement

A string whose size depends on the size parsed just before it.

| Name         | Type    | Default value if optional | Description                                                                                           |
|--------------|---------|---------------------------|-------------------------------------------------------------------------------------------------------|
| fieldSize    | 1, 2, 4 |                           | The size of the size attribute in bytes                                                               |
| charset      | Charset | "UTF-8"                   | The charset to use.                                                                                   |
| stopAtNull   | Boolean | false                     | If true, will read the "size" characters but will only form a string until a null character is found. |

#### Example
```json
{
    "schema": {
        "string": {
            "@type": "VariableSizeStringElement",
            "fieldSize": 1
        }
    }
}
```
```
0x03416E61
↓
size = 3
string = "Ana"
```


### VariableRefSizeStringElement

String whose size depends on the size in another variable.

| Name        | Type     | Default value if optional | Description                                                                                           |
|-------------|----------|---------------------------|-------------------------------------------------------------------------------------------------------|
| sizeVarName | String   |                           | The name of the variable which stores the size of this string. Cannot be a forward reference.         |
| charset     | Charset  | "UTF-8"                   | The charset to use.                                                                                   |
| stopAtNull  | Boolean  | false                     | If true, will read the "size" characters but will only form a string until a null character is found. |

#### Example
```json
{
    "schema": {
        "string_size": {
            "@type": "IntElement",
            "size": 1
        },
        "string": {
            "@type": "VariableRefSizeStringElement",
            "sizeVarName": "string_size"
        }
    }
}
```
```
0x03416E61
↓
string_size = 3
string = "Ana"
```


### FixedSizeArrayElement

An array whose size is fixed.

| Name     | Type            | Default value if optional | Description                             |
|----------|-----------------|---------------------------|-----------------------------------------|
| size     | Integer         |                           | The size of array in number of elements |
| schema   | Schema elements |                           | The schema to parse at each index.      |

#### Example
```json
{
    "schema": {
        "array": {
            "@type": "FixedSizeArrayElement",
            "size": 4,
            "schema": {
                "@type": "IntElement",
                "size": 1
            }
        }
    }
}
```
```
0x08060902
↓
array = [8, 6, 9, 2]
```

#### Example leftover
```json
{
    "schema": {
        "array": {
            "@type": "FixedSizeArrayElement",
            "size": 4,
            "schema": {
                "@type": "IntElement",
                "size": 1
            }
        },
        "int_element": {
            "@type": "IntElement",
            "size": 1
        }
    }
}
```
```
0x0806090206
↓
array = [8, 6, 9, 2]
int_element = 6
```

### VariableSizeArrayElement

Array whose size depends on the size parsed just before it.

| Name        | Type            | Default value if optional | Description                             |
|-------------|-----------------|---------------------------|-----------------------------------------|
| fieldSize   | 1, 2, 4         |                           | The size of the size attribute in bytes |
| schema      | Schema elements |                           | The schema to parse at each index.      |

#### Example
```json
{
    "schema": {
        "array": {
            "@type": "VariableSizeArrayElement",
            "fieldSize": 1,
            "schema": {
                "@type": "IntElement",
                "size": 1
            }
        }
    }
}
```
```
0x0408060902
↓
size = 4
array = [8, 6, 9, 2]
```


### VariableRefSizeArrayElement

Array whose size depends on the size in another variable.

| Name        | Type            | Default value if optional | Description                                                                                  |
|-------------|-----------------|---------------------------|----------------------------------------------------------------------------------------------|
| sizeVarName | String          |                           | The name of the variable which stores the size of this array. Cannot be a forward reference. |
| schema      | Schema elements |                           | The schema to parse at each index.                                                           |

#### Example
```json
{
    "schema": {
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
}
```
```
0x0408060902
↓
array_size = 4
array = [8, 6, 9, 2]
```


### DynamicSizeArrayElement

An array generated by looping on a schema as long as the condition is met. Enums and unions will automatically be converted into an integer, while structs need structField attribute to be set.

| Name        | Type                | Default value if optional | Description                                                                                                            |
|-------------|---------------------|---------------------------|------------------------------------------------------------------------------------------------------------------------|
| against     | Integer             |                           | The integer value to compare against.                                                                                  |
| operator    | Comparison operator |                           | The operator to use among EQUALS, NOT\_EQUALS, LESS\_THAN, LESS\_THAN\_OR\_EQUALS, MORE\_THAN, MORE\_THAN\_OR\_EQUALS. |
| schema      | Schema elements     |                           | The schema to parse at each index.                                                                                     |
| structField | Schema elements     | null                      | If the schema is a struct, the field to use for the comparison.                                                        |

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
array = [Pair(a=5, b=2), Pair(a=3, b=4), Pair(a=1, b=0)]
```


### FixedSizeBlobElement

Element that can be used by default to take the bytes as is.

| Name    | Type    | Default value if optional | Description                   |
|---------|---------|---------------------------|-------------------------------|
| size    | Integer |                           | The size of the blob in bytes |

#### Example
```json
{
    "schema": {
        "blob": {
            "@type": "FixedSizeBlobElement",
            "size": 4
        }
    }
}
```
```
0x416E61AF
↓
blob = 41 6E 61 AF
```

#### Example leftover
```json
{
    "schema": {
        "blob": {
            "@type": "FixedSizeBlobElement",
            "size": 4
        },
        "int_element": {
            "@type": "IntElement",
            "size": 1
        }
    }
}
```
```
0x416E61AF06
↓
blob = 41 6E 61 AF
int_element = 6
```


### VariableSizeBlobElement

A blob whose size depends on the size parsed just before it.

| Name        | Type    | Default value if optional | Description                             |
|-------------|---------|---------------------------|-----------------------------------------|
| fieldSize   | 1, 2, 4 |                           | The size of the size attribute in bytes |

#### Example
```json
{
    "schema": {
        "blob": {
            "@type": "VariableSizeBlobElement",
            "fieldSize": 1
        }
    }
}
```
```
0x04416E61AF
↓
size = 4
blob = 41 6E 61 AF
```


### VariableRefSizeBlobElement

Blob whose size depends on the size in another variable.

| Name        | Type            | Default value if optional | Description                                                                                 |
|-------------|-----------------|---------------------------|---------------------------------------------------------------------------------------------|
| sizeVarName | String          |                           | The name of the variable which stores the size of this blob. Cannot be a forward reference. |

#### Example
```json
{
    "schema": {
        "blob_size": {
            "@type": "IntElement",
            "size": 1
        },
        "blob": {
            "@type": "VariableRefSizeBlobElement",
            "sizeVarName": "blob_size"
        }
    }
}
```
```
0x04416E61AF
↓
blob_size = 4
blob = 41 6E 61 AF
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
    "schema": {
        "array": {
            "@type": "VariableSizeArrayElement",
            "fieldSize": 1,
            "schema": {
                "@type": "NullEndStringElement"
            }
        },
        "reference": {
            "@type": "ArrayReferenceElement",
            "size": 1,
            "variable": "array"
        }
    }
}
```
```
0x02496e7465676572456e74727900537472696e67456e7472790001
↓
0x01 = length 1
0x496e7465676572456e74727900 = "IntegerEntry" (the string value)
0x537472696e67456e74727900 = "StringEntry" (the string value)
array = [IntegerEntry, StringEntry]
reference = 0x01 -> "StringEntry"
```

#### Example zero is null
```json
{
    "schema": {
        "array": {
            "@type": "VariableSizeArrayElement",
            "fieldSize": 1,
            "schema": {
                "@type": "NullEndStringElement"
            }
        },
        "reference": {
            "@type": "ArrayReferenceElement",
            "size": 1,
            "variable": "array",
            "zeroIsNull": true
        }
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
    "schema": {
        "int_element": {
            "@type": "IntElement",
            "size": 1
        },
        "display": {
            "@type": "ComputedIntElement",
            "variables": [ "int_element" ]
        }
    }
}
```
```
0x05
↓
int_element = 5
display = "5"
```

#### Example expression
```json
{
    "schema": {
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

| Name      | Type            | Default value if optional | Description                                                                                                                                       |
|-----------|-----------------|---------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------|
| format    | String          |                           | The format string, in the same syntax as [Java Formatter](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/Formatter.html). |
| variables | Array of String |                           | Array of variables to inject into the format.                                                                                                     |

#### Example variables
```json
{
    "schema": {
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
            "variables": [ [ "name" ], [ "age" ] ]
        }
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

#### Example struct
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
                    "size": 1
                }
            }
        }
    },
    "schema": {
        "person": {
            "@type": "TypeElement",
            "name": "Person"
        },
        "display": {
            "@type": "ComputedStringFormatElement",
            "format": "Name: %s, age: %d.",
            "variables": [ [ "person", "name" ], [ "person", "age" ] ]
        }
    }
}
```
```
0x416E61000F
↓
person = {
    name = "Ana"
    age = 15
}
display = "Name: Ana, age: 15."
```