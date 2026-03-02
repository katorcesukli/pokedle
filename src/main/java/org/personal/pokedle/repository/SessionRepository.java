package org.personal.pokedle.repository;

import org.personal.pokedle.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findByUserIpAndCompletedFalse(String userIp);
}
