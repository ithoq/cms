package be.ttime.core.pebble.ext.function;

import be.ttime.core.util.CmsUtils;
import com.mitchellbosecke.pebble.extension.Function;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class HasAnyRoleFunction implements Function {


    @Override
    public List<String> getArgumentNames() {
        return null;
    }

    @Override
    public Object execute(Map<String, Object> args) {

        if(args.get("0") == null || !(args.get("0") instanceof Collection))
            throw new IllegalArgumentException();

        Collection<String> roles = (Collection<String>) args.get("0");
        return CmsUtils.hasAnyRole(roles);
    }
}