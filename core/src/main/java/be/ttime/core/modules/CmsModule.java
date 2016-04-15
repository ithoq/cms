package be.ttime.core.modules;

import java.util.Map;

public interface CmsModule {

    String render(final Map<String, ?> model) throws ModuleRenderingException;

}
