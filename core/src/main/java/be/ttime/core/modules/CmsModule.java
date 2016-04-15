package be.ttime.core.modules;

import java.util.Map;

public interface CmsModule {

    String render(final Map<String, Object> model) throws ModuleRenderingException;

}
