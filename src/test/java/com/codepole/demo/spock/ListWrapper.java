package com.codepole.demo.spock;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ListWrapper {

    private List<String> value;

    public void add(String item) {
        value.add(item);
    }
}
