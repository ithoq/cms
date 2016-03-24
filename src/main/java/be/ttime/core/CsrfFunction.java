package be.ttime.core;

import com.mitchellbosecke.pebble.extension.Function;
import org.springframework.security.web.csrf.CsrfToken;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CsrfFunction implements Function {

    @Override
    public Object execute(Map<String, Object> args) {
        HttpServletRequest request = (HttpServletRequest) args.get("request");
        Object param = request.getAttribute("_csrf");
        CsrfToken csrf = (param instanceof CsrfToken ? (CsrfToken) param : null);
        String csrfName = csrf != null ? csrf.getParameterName() : "";
        String csrfValue = csrf != null ? csrf.getToken() : "";
        return "<input type=\"hidden\" name=\"" + csrfName + "\" value=\"" + csrfValue + "\" />";
    }

    @Override
    public List<String> getArgumentNames() {
        List<String> names = new ArrayList<>();
        names.add("request");
        return names;
    }
}
