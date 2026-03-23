package org.example.mentalhealthsystem.repository;

import org.example.mentalhealthsystem.entity.AnonymousDict;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AnonymousDictRepository extends JpaRepository<AnonymousDict, Long> {
    @Query(value = "SELECT * FROM anonymous_dict WHERE type = 'ADJ' ORDER BY RAND() LIMIT 1", nativeQuery = true)
    AnonymousDict findRandomAdj();

    @Query(value = "SELECT * FROM anonymous_dict WHERE type = 'NOUN' ORDER BY RAND() LIMIT 1", nativeQuery = true)
    AnonymousDict findRandomNoun();
}