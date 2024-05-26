package com.Groupe5.user.Repository;


import com.Groupe5.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}