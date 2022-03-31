package org.zrclass.excel;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zrclass.dto.User;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/excel")
public class ExcelTestController {

    @GetMapping("export")
    public String export(HttpServletResponse response) {
        User user = new User();
        user.setUsername("张三");
        user.setAge("19");
        user.setSex("男");
        user.setPosition("董事长");
        User user2 = new User();
        user2.setUsername("李四");
        user2.setAge("19");
        user2.setSex("女");
        user2.setPosition("董秘");

        User user3 = new User();
        user3.setUsername("王五");
        user3.setAge("19");
        user3.setSex("男");
        user3.setPosition("总经理");

        List<User> list = Arrays.asList(user, user2, user3);
        ExcelExportUtil.exportExcel("test", list, response, "test-", User.class);
        return "导出成功";
    }

    @GetMapping("import")
    public String importExcel() throws IOException {
        String path = "D:\\test.xls";
        List<User> test = ExcelImportUtil.getExcelToList("test", path, User.class);
        test.forEach(e->{
            System.out.println(e);
        });
        return "导入成功";
    }
}
