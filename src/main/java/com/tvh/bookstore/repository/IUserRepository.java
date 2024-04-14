package com.tvh.bookstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.tvh.bookstore.entity.User;
import java.lang.Boolean;
import java.lang.String;
import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByIdUser(int idUser);

    Optional<User> findByEmail(String email);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.userName = ?1")
    Boolean existsByUsername(String userName);

    Boolean existsByEmail(String email);
}
