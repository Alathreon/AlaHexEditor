package com.alathreon.alahexeditor.persistence;

import com.alathreon.alahexeditor.parsing.template.Template;
import com.alathreon.alahexeditor.util.IOUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.alathreon.alahexeditor.util.IOUtil.TEMPLATE_EXTENSION;

/**
 * Formats:
 * .extension.alahex.template.json
 * 0xMAGICNUMBER.alahex.template.json
 */
public class AutoTemplateModule {
    private static final String FOLDER_NAME = "templates";
    private static final Pattern FILE_PATTERN = Pattern.compile("^(\\.[\\w.]+|0x[0-9A-F]{2}+)\\.alahex\\.template\\.json$");
    private static final Pattern EXTENSION_PATTERN = Pattern.compile("^\\.[\\w.]+$");
    private static final Pattern HEX_PATTERN = Pattern.compile("^[0-9A-F]{2}+$");
    private final PersistenceAPI api;
    private final ObjectMapper mapper;

    public AutoTemplateModule(PersistenceAPI api, ObjectMapper mapper) {
        this.api = api;
        this.mapper = mapper;
    }

    private Template readTemplate(String name) {
        String json = api.readString(FOLDER_NAME, name);
        return IOUtil.parseTemplate(mapper, json);
    }
    public Template findTemplate(Path file) {
        String fileString = file.getFileName().toString();
        byte[] startBytes = new byte[0];
        String extension = fileString.substring(fileString.indexOf('.'));
        List<String> templates = api.list(FOLDER_NAME);
        for (String template : templates) {
            Matcher matcher = FILE_PATTERN.matcher(template);
            boolean found = matcher.find();
            if(!found) continue;
            String discriminator = matcher.group(1);
            if(discriminator.startsWith(".")) {
                if(extension.equals(discriminator)) {
                    return readTemplate(template);
                }
            } else {
                String hex = discriminator.substring(2); // Skip 0x
                int bytesCount = hex.length() / 2;
                startBytes = readMoreBytesIfNeeded(startBytes, bytesCount, template);
                if(matchBytes(bytesCount, startBytes, hex)) {
                    return readTemplate(template);
                }
            }
        }
        return null;
    }
    private byte[] readMoreBytesIfNeeded(byte[] bytes, int count, String template) {
        if(bytes.length < count) {
            try(var in = Files.newInputStream(api.path(FOLDER_NAME, template))) {
                return in.readNBytes(count);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        } else {
            return bytes;
        }
    }
    private boolean matchBytes(int count, byte[] bytes, String hex) {
        for(int i = 0; i < count; i++) {
            if(bytes[i] != Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16)) {
                return false;
            }
        }
        return true;
    }

    public void writeExtensionTemplate(String extension, Template template) {
        if(!EXTENSION_PATTERN.matcher(extension).matches()) throw new IllegalArgumentException("Invalid extension: " + extension);
        String json;
        try {
            json = mapper.writeValueAsString(template);
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException(e);
        }
        api.writeString(FOLDER_NAME, extension + TEMPLATE_EXTENSION, json);
    }

    public void writeMagicNumberTemplate(String magicNumberHex, Template template) {
        if(!HEX_PATTERN.matcher(magicNumberHex).matches()) throw new IllegalArgumentException("Invalid hex: " + magicNumberHex);
        String json = IOUtil.writeTemplate(mapper, template);
        api.writeString(FOLDER_NAME, "0x" + magicNumberHex + TEMPLATE_EXTENSION, json);
    }
}
