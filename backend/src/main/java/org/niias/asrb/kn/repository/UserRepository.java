package org.niias.asrb.kn.repository;

import org.niias.asrb.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface   UserRepository extends CrudRepository<User, Integer> {
    User findFirstByLogin(@Param("login") String login);
    Optional<User> findById(@Param("id") int id);
}
