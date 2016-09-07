package be.ttime.core.pebble.ext;

import be.ttime.core.pebble.ext.function.*;
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
        functions.put("hasNotRole", new HasNotRoleFunction());
        functions.put("hasAnyRole", new HasAnyRoleFunction());
        functions.put("isAnonymous", new IsAnonymousFunction());
        functions.put("isLogged", new IsLoggedFunction());
        functions.put("getType", new GetTypeFunction());
        functions.put("ToDouble", new ToDoubleFunction());
        functions.put("ToLong", new ToLongFunction());
        functions.put("ToString", new ToStringFunction());
        return functions;
    }
}