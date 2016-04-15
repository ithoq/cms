package be.ttime.modules.foo;

import be.ttime.core.modules.CmsModule;
import be.ttime.core.modules.ModuleLoadingException;
import be.ttime.core.modules.ModuleRenderingException;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

@Component
public class FooCmsModule implements CmsModule {

    private static final Logger LOG = LoggerFactory.getLogger(FooCmsModule.class);

    @Autowired
    @Qualifier("fooPebbleEngine")
    private PebbleEngine engine;

    private PebbleTemplate template;

    @Override
    public String render(final Map<String, Object> model) throws ModuleRenderingException {
        final Writer writer = new StringWriter();
        final Map<String, Object> context = new HashMap<>();

        context.put("title", model.get("foo-title"));
        context.put("description", model.get("foo-description"));

        final String html;
        try {
            template.evaluate(writer, context);
            html = writer.toString();
            writer.close();
        } catch (final PebbleException | IOException e) {
            throw new ModuleRenderingException(e);
        }

        return html;
    }

    @PostConstruct
    public void init() {
        template = loadTemplate("foo.peb");

        LOG.info("[+] Foo module initialized");
    }

    private PebbleTemplate loadTemplate(final String classPathResource) {
        try {
            return engine.getTemplate(classPathResource);
        } catch (final PebbleException e) {
            throw new ModuleLoadingException(e);
        }
    }

}
