package com.cpalmer.projects.flashcards.repository;

import com.cpalmer.projects.flashcards.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUserId(int userId);
    User findByUserNameAndUserPasswordHash(String userName, String userPasswordHash);
}