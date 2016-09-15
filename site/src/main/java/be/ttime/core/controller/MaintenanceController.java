package be.ttime.core.controller;

import be.ttime.core.persistence.model.BlockEntity;
import be.ttime.core.persistence.service.IApplicationService;
import be.ttime.core.persistence.service.IBlockService;
import be.ttime.core.util.CmsUtils;
import be.ttime.core.util.PebbleUtils;
import com.mitchellbosecke.pebble.error.PebbleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class MaintenanceController {

    @Autowired
    private IBlockService blockService;
    @Autowired
    private IApplicationService applicationService;
    @Autowired
    private PebbleUtils pebbleUtils;

    @RequestMapping(value = "/{lang:[a-z]{2}(?:_[A-Z]{2})?}/r/maintenance", method = RequestMethod.GET)
    @ResponseBody
    public String installForm(ModelMap model, HttpServletRequest request, HttpServletResponse response) throws IOException, PebbleException {

        BlockEntity master = blockService.find(CmsUtils.BLOCK_PAGE_MASTER);
        BlockEntity maintenance = blockService.find(CmsUtils.BLOCK_PAGE_MAINTENANCE);

        CmsUtils.fillModelMap(model,request, applicationService);

        model.put("title", "Maintenance");
        model.put("main", pebbleUtils.parseBlock(maintenance, model));
        response.setContentType("text/html");
        response.setStatus(503);
        String result = pebbleUtils.parseBlock(master, model);
        return result;

    }
}
