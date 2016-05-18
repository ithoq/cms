package be.ttime.modules.foo.service;

import be.ttime.core.persistence.model.Foo;
import be.ttime.modules.foo.repository.FooRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FooService {

    @Autowired
    private FooRepository fooRepository;

    @Transactional
    public Foo save(final Foo foo) {
        return fooRepository.save(foo);
    }

}
