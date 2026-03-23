package org.example.mentalhealthsystem.service;

import org.example.mentalhealthsystem.entity.AnonymousDict;
import org.example.mentalhealthsystem.repository.AnonymousDictRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnonymousService {

    @Autowired
    private AnonymousDictRepository anonymousDictRepository;

    public String generateAnonymousId() {
        AnonymousDict adj = anonymousDictRepository.findRandomAdj();
        AnonymousDict noun = anonymousDictRepository.findRandomNoun();
        if (adj == null || noun == null) {
            // 如果没有词库，生成默认ID
            return "匿名用户" + System.currentTimeMillis() % 10000;
        }
        return adj.getWord() + "的" + noun.getWord() + (int)(Math.random() * 1000);
    }
}