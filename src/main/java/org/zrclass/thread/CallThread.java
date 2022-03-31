package org.zrclass.thread;

import org.zrclass.dto.User;

import java.util.concurrent.Callable;

public class CallThread implements Callable<User> {
    @Override
    public User call() throws Exception {
        User user = new User();
        user.setUsername("张三");
        user.setSex("男");
        user.setAge("18");
        user.setPosition("总经理");
        return user;
    }
}
