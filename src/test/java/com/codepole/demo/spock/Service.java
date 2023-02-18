package com.codepole.demo.spock;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Service {

    private final Repository repository;

    public void saveItem(String item) {
        repository.save(item);
    }
    
}
