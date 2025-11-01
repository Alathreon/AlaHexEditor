package com.alathreon.alahexeditor.util;

import com.alathreon.alahexeditor.parsing.template.Template;

import java.nio.file.Path;

public record FileTemplate(Path path, Template template) {
}
