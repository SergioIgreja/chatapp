package com.example.chat;

import java.util.HashMap;
import java.util.Map;

public class Config {

    private static Config instance = null;

    // variable of type String
    public Map<String, String> accounts;

    // private constructor restricted to this class itself
    private Config()
    {
        accounts = new HashMap<>();
        accounts.put("root", "root");
        accounts.put("username", "password");
        accounts.put("fyi","fyi");
    }

    // static method to create instance of Singleton class
    public static Config getInstance()
    {
        if (instance == null)
            instance = new Config();

        return instance;
    }
}

