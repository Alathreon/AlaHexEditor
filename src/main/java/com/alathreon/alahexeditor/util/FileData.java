package com.alathreon.alahexeditor.util;

import java.nio.file.Path;

public record FileData(Path path, ByteView data) {
}
