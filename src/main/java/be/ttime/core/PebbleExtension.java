package be.ttime.core;

import com.mitchellbosecke.pebble.extension.AbstractExtension;
import com.mitchellbosecke.pebble.spring4.context.Beans;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import java.util.HashMap;
import java.util.Map;

@Component
public class PebbleExtension extends AbstractExtension {

    @Autowired
    private ServletContext servletContext;

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public Map<String, Object> getGlobalVariables() {
        Map<String, Object> map = new HashMap<>();
        map.put("beans", new Beans(applicationContext));
        map.put("context", servletContext);
        return map;
    }
}
