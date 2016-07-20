package be.ttime.core.pebble.ext;

import be.ttime.core.pebble.ext.function.HasAnyRoleFunction;
import be.ttime.core.pebble.ext.function.HasRoleFunction;
import be.ttime.core.pebble.ext.function.IsAnonymousFunction;
import com.mitchellbosecke.pebble.extension.AbstractExtension;
import com.mitchellbosecke.pebble.extension.Function;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class PebbleExtension extends AbstractExtension {
    @Override
    public Map<String, Function> getFunctions() {
        Map<String, Function> functions = new HashMap<>();

        functions.put("hasRole", new HasRoleFunction());
        functions.put("hasAnyRole", new HasAnyRoleFunction());
        functions.put("isAnonymous", new IsAnonymousFunction());

        return functions;
    }
}