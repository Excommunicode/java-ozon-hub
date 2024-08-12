package kz.ozon.javaozonhub.repository;

import kz.ozon.javaozonhub.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    @Query("SELECT r " +
            "FROM RefreshToken r " +
            "JOIN FETCH r.user u " +
            "WHERE u.login = :login")
    Optional<RefreshToken> findByUserLogin(String login);

    @Modifying
    @Query("DELETE FROM RefreshToken r WHERE r.token = :token")
    int deleteByToken(String token);

    @Modifying
    @Query("DELETE FROM RefreshToken r WHERE r.expiryDate < :now")
    int deleteAllByExpiryDateIsBefore(LocalDateTime now);
}