package com.example.spayserver;

public class User {
    private long mob;
    private String username;
    private String password;
    private String key;
    private long amount;

    User(long mob, String username, String password, String key, long amount)
    {
        this.mob=mob;
        this.username=username;
        this.password=password;
        this.key=key;
        this.amount=amount;
    }

    public long getMob()
    {
        return mob;
    }

    public String getUserName()
    {
        return username;
    }

    public String getPassword()
    {
        return password;
    }

    public String getKey()
    {
        return key;
    }

    public long getAmount()
    {
        return amount;
    }

    public void setAmount(long amount){
        this.amount=amount;
    }

}
