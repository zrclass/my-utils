package org.zrclass.dto;

import lombok.Data;
import org.zrclass.excel.ExportKey;
import org.zrclass.excel.ImportKey;

@Data
public class User {

    @ExportKey(remark = "用户名")
    @ImportKey(sort = 0)
    private String username;

    @ImportKey(sort = 1)
    @ExportKey(remark = "年龄")
    private String age;

    @ImportKey(sort = 2)
    @ExportKey(remark = "性别")
    private String sex;

    @ImportKey(sort = 3)
    @ExportKey(remark = "职位")
    private String position;

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", age='" + age + '\'' +
                ", sex='" + sex + '\'' +
                ", position='" + position + '\'' +
                '}';
    }
}
