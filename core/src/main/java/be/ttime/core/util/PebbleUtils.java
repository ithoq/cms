package be.ttime.core.util;


import be.ttime.core.persistence.model.BlockEntity;
import be.ttime.core.persistence.service.IBlockService;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
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
    @Qualifier("pebbleStringEngine")
    private PebbleEngine pebbleStringEngine;
    @Autowired
    private IBlockService blockService;
    @Autowired
    ApplicationContext applicationContext;

    public String parseString(String data, Map<String, Object> model)  throws IOException, PebbleException{
        PebbleTemplate template = pebbleStringEngine.getTemplate(data);
        Writer writer = new StringWriter();
        template.evaluate(writer, model, LocaleContextHolder.getLocale());
        return writer.toString();
    }

    public String parseBlock(BlockEntity block, Map<String, Object> model) throws IOException, PebbleException {
        if (model == null) {
            model = new HashMap<>(10);
        }

        // force to pass in the cache
        PebbleUtils utils = applicationContext.getBean(this.getClass());
        PebbleTemplate compiledTemplate = utils.getCompiledTemplate(block.getName());
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
