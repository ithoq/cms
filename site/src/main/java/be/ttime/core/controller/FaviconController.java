package be.ttime.core.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Handles favicon.ico requests assuring no <code>404 Not Found</code> error is returned.
 */
@Controller
public class FaviconController {
    @RequestMapping("favicon.ico")
    public String favicon() {
        return "forward:/resources/images/favicon.ico";
    }
}
