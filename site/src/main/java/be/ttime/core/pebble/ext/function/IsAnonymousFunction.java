package be.ttime.core.pebble.ext.function;

import be.ttime.core.util.CmsUtils;
import com.mitchellbosecke.pebble.extension.Function;

import java.util.List;
import java.util.Map;

public class IsAnonymousFunction implements Function {


    @Override
    public Object execute(Map<String, Object> args) {
        return !CmsUtils.isLogged();
    }

    @Override
    public List<String> getArgumentNames() {
        return null;
    }
}