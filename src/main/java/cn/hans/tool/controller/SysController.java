package cn.hans.tool.controller;/**
 * @author gaofan
 * @date 2019-01-20 14:10
 */

import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author hans
 * @Date 2019-01-20
 * @description 系统
 *
 */
@Controller
@RequestMapping("sys")
public class SysController {

    @ResponseBody
    @RequestMapping(value = "sayHello",method = RequestMethod.GET)
    @ApiOperation("测试是否正常 sayHelloWorld")
    public String hello(){
        return "hello tool world!";
    }

}
