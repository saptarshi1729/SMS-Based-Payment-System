package com.example.spayserver;

public class Transaction {
    private long tr_id;
    private long from;
    private long to;
    private long amount;
    private String date;

    Transaction(long tr, long f, long t, long a, String s)
    {
        tr_id = tr;
        from = f;
        to = t;
        amount = a;
        date  = s;
    }

    public long getid()
    {
        return tr_id;
    }

    public long getTo()
    {
        return to;
    }

    public long getFrom()
    {
        return from;
    }

    public long getAmount()
    {
        return amount;
    }

    public String getDate()
    {
        return date;
    }

}

