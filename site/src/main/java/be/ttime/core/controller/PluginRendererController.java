package be.ttime.core.controller;

import be.ttime.core.modules.CmsModule;
import be.ttime.core.modules.ModuleRenderingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/plugin")
public class PluginRendererController {

    @Autowired
    private Map<String, CmsModule> cmsModules;

    @RequestMapping(value = "/render/{moduleName}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String render(final @PathVariable("moduleName") String moduleName, final @RequestBody Map<String, String> data) {
        final CmsModule module = cmsModules.get(moduleName.concat(CmsModule.class.getSimpleName()));
        if (module == null) {
            throw new ModuleRenderingException(new IllegalArgumentException(moduleName));
        }
        return module.render(data);
    }

}
