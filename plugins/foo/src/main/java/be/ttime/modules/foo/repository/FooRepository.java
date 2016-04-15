package be.ttime.modules.foo.repository;

import be.ttime.modules.foo.model.Foo;
import org.springframework.data.repository.CrudRepository;

public interface FooRepository extends CrudRepository<Foo, Long> {
}
