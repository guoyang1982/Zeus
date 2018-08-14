package com.gy.sched.common.remoting;


import com.gy.sched.common.exception.RemotingCommandException;

public interface CommandCustomHeader {
    public void checkFields() throws RemotingCommandException;
}
