package com.codepole.demo.spock;

import java.util.ArrayList;
import java.util.List;

public class Repository {

    List<String> items = new ArrayList<>();

    public void save(String item) {
        items.add(item);
    }
}
