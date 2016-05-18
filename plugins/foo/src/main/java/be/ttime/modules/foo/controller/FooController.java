package be.ttime.modules.foo.controller;

import be.ttime.modules.foo.model.Foo;
import be.ttime.modules.foo.service.FooService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/modules/foo")

public class FooController {

    @Autowired
    private FooService fooService;

    @RequestMapping("/config")
    @ResponseBody
    public String config() {

        final Foo foo = new Foo();
        foo.setFoo("New foo");

        final Foo saved = fooService.save(foo);

        return String.format("Here we'll configure the module. " +
                "For now we just save a new foo with ID : [%s]", saved.getId());
    }

}
