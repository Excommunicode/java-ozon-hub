package kz.ozon.javaozonhub.repository;

import kz.ozon.javaozonhub.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT DISTINCT u, r " +
            "FROM User u " +
            "JOIN FETCH u.roles r " +
            "WHERE u.login = :name " )
    Optional<User> findByLogin(String name);

    @Query("SELECT COUNT(u) > 0 " +
            "FROM User u " +
            "WHERE u.login = :login")
    boolean existsByLogin(String login);
}