package be.ttime.core.pebble.ext.function;

import be.ttime.core.util.CmsUtils;
import com.mitchellbosecke.pebble.extension.Function;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by fabricecipolla on 20/07/16.
 */
public class HasNotRoleFunction implements Function {

    @Override
    public List<String> getArgumentNames() {
        return null;
    }

    @Override
    public Object execute(Map<String, Object> args) {

        Object o = args.get("0");
        if(o == null || (!(o instanceof Collection) && !(o instanceof String)))
            throw new IllegalArgumentException();

        Collection<String> roles;
        if(o instanceof Collection) {
            roles = (Collection<String>) o;
        }
        else {
            roles = Collections.singleton((String)o);
        }

        return !CmsUtils.hasRoles(roles);
    }
}