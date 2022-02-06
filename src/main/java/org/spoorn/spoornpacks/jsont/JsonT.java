package org.spoorn.spoornpacks.jsont;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Json template substitution library.  Takes a .jsonT template and replaces substitution variables to produce a
 * custom output.
 * 
 * TODO: optimize
 */
public class JsonT {
    
    private final ObjectMapper objectMapper;
    
    public JsonT() {
        this.objectMapper = new ObjectMapper();
    }
    
    public JsonT(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    public <T> T substitute(String resourcePath, Class<T> clazz, String... args) throws IOException {
        try (InputStream input = getClass().getResourceAsStream(resourcePath)) {
            if (input == null) {
                throw new IllegalArgumentException("Class resource at " + resourcePath + " is not available");
            }
            
            Map<String, String> mapper = new HashMap<>();
            for (int i = 0; i < args.length; i++) {
                mapper.put(Integer.toString(i + 1), args[i]);
            }

            String template = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            String res = substitute(template, mapper);
            return this.objectMapper.readValue(res, clazz);
        }
    }
    
    public <T> T substitute(Path template, Class<T> clazz, String... args) throws IOException {        
        String res = substitute(template, args);
        return this.objectMapper.readValue(res, clazz);
    }
    
    public String substitute(Path template, String... args) throws IOException {        
        Map<String, String> mapper = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            mapper.put(Integer.toString(i + 1), args[i]);
        }
        
        return substitute(template, mapper);
    }
    
    public String substitute(Path template, Map<String, String> mapper) throws IOException {
        validateTemplate(template);
        String templateStr = Files.readString(template, StandardCharsets.UTF_8);
        return substitute(templateStr, mapper);
    }

    public String substitute(String template, Map<String, String> mapper) {
        for (Entry<String, String> entry : mapper.entrySet()) {
            template = template.replace("${string:" + entry.getKey() + "}", "\"" + entry.getValue() + "\"");
        }

        return template;
    }
    
    private void validateTemplate(Path template) {
        if (template == null || Files.notExists(template)) {
            throw new IllegalArgumentException("Template file at [" + template + "] does not exist");
        }
    }
}
