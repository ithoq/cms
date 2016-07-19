package be.ttime.core.util;


import be.ttime.core.persistence.model.BlockEntity;
import be.ttime.core.persistence.service.IBlockService;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.i18n.LocaleContextHolder;
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
    @Autowired
    public IBlockService blockService;

    public String parseBlock(BlockEntity block, Map<String, Object> model) throws IOException, PebbleException {
        if (model == null) {
            model = new HashMap<>(10);
        }

        PebbleTemplate compiledTemplate = getCompiledTemplate(block.getName());
        Writer writer = new StringWriter();
        compiledTemplate.evaluate(writer, model, LocaleContextHolder.getLocale());
        return writer.toString();

    }

    @Cacheable(value = "blockCompiledTemplate", key = "#name")
    public PebbleTemplate getCompiledTemplate(String name) throws PebbleException {

        BlockEntity block = blockService.find(name);
        return pebbleStringEngine.getTemplate(block.getContent());
    }

}
