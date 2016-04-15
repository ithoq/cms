package be.ttime.core.util;


import be.ttime.core.persistence.model.PageBlockEntity;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

@Component
public class PebbleUtils {

    @Autowired
    public PebbleEngine pebbleStringEngine;

    public String parseString(String content, Map<String, Object> model) throws IOException, PebbleException {
        PebbleTemplate compiledTemplate = pebbleStringEngine.getTemplate(content);
        Writer writer = new StringWriter();
        compiledTemplate.evaluate(writer, model);
        return writer.toString();
    }

    public String parseString(PebbleTemplate compiledTemplate, Map<String, Object> model) throws IOException, PebbleException {
        Writer writer = new StringWriter();
        compiledTemplate.evaluate(writer, model);
        return writer.toString();
    }

    public void parseString(String content, Map<String, Object> model, Writer writer) throws IOException, PebbleException {
        PebbleTemplate compiledTemplate = pebbleStringEngine.getTemplate(content);
        compiledTemplate.evaluate(writer, model);
    }

    public String parseBlock(PageBlockEntity block, Map<String, Object> model) throws IOException, PebbleException {
        if (model == null) {
            model = new HashMap<>(10);
        }
        
        /* We add object related to the blockType
        switch (block.getBlockType()) {
            case FieldSet : break;
            case Content:;
            case

        }
        */

        return parseString(block.getContent(), model);
    }

    public PebbleTemplate getCompiledTemplate(String content) throws PebbleException {
        return pebbleStringEngine.getTemplate(content);
    }

}
