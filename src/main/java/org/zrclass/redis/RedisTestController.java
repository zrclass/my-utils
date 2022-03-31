package org.zrclass.redis;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zrclass.dto.Result;
import org.zrclass.dto.User;

@RestController
@RequestMapping("/redis")
public class RedisTestController {

    @GetMapping("/getUser")
    @SelfRedisCache(preKey = "USER")
    public Result getUser() {
        User user = new User();
        user.setAge("19");
        user.setUsername("zhangsan");
        user.setPosition("zongjingli");
        user.setSex("man");
        return Result.ok(user);
    }
}
