package org.zrclass.page;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zrclass.dto.StudentRequestVo;

@RestController
@RequestMapping("/page")
public class PageTestController {
    @GetMapping("/getPage")
    @PageQuery(pageArgName = "studentRequestVo")
    public PageResult getPage(StudentRequestVo studentRequestVo){
        return null;
    }
}
