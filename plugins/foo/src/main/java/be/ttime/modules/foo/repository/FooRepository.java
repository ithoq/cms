package be.ttime.modules.foo.repository;

import be.ttime.core.persistence.model.Foo;
import org.springframework.data.repository.CrudRepository;

public interface FooRepository extends CrudRepository<Foo, Long> {
}
