package be.ttime.core.pebble.ext.function;

import be.ttime.core.util.CmsUtils;
import com.mitchellbosecke.pebble.extension.Test;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by fabricecipolla on 20/07/16.
 */
public class HasRoleFunctionTest implements Test {

    @Override
    public List<String> getArgumentNames() {
        return null;
    }

    @Override
    public boolean apply(Object input, Map<String, Object> args) {
        boolean isEmpty = input == null;
        if (isEmpty || !(input instanceof Collection))
            throw new IllegalArgumentException();


        Collection<String> roles = (Collection<String>) input;
        return CmsUtils.hasRoles(roles);
    }
}
