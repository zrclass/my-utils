package org.zrclass.dto;

import lombok.Data;
import org.zrclass.page.PageLimitVo;

@Data
public class StudentRequestVo extends PageLimitVo {
    private String name;
    private int age;
    private String sex;
    private String cardNum;
}
