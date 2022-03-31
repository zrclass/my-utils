package org.zrclass.thread;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.zrclass.dto.User;

import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

@Component
public class TestThread implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        // runnable 测试
        ThreadPoolExecutor taskPool = ThreadPoolManager.getInstance().getTaskPool();
        taskPool.execute(() -> {
            System.out.println("runnable 线程执行");
        });

        // callable测试
        Future<User> future = taskPool.submit(() ->{
            System.out.println("callable 线程执行");
            User user = new User();
            user.setUsername("张三");
            user.setSex("男");
            user.setAge("18");
            user.setPosition("总经理");
            return user;
        });
        System.out.println(future.get());

    }
}
