package be.ttime.core.pebble.ext.function;

import com.mitchellbosecke.pebble.extension.Function;
import java.util.List;

import java.util.Map;

public class ToDoubleFunction implements Function {

    @Override
    public List<String> getArgumentNames() {
        return null;
    }

    @Override
    public Object execute(Map<String, Object> args) {
        Object o = args.get("0");
        if(o==null) return null;
        if(o instanceof String){
            throw new IllegalArgumentException("Argument must be a String");
        }
        Double result = null;
        try {
            Double.parseDouble((String) o);
        } catch(NumberFormatException e) {}

        return result;
    }
}

