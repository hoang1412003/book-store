package com.tvh.bookstore.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tvh.bookstore.entity.User;

@Repository
public interface IUserRepository extends JpaRepository<User,Integer> {

    Optional<User> findByIdUser(int idUser);

    Optional<User> findByEmail(String email);

}