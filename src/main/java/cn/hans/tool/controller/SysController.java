package cn.hans.tool.controller;/**
 * @author gaofan
 * @date 2019-01-20 14:10
 */

import cn.hans.tool.helper.SysHelper;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

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

    @Resource
    SysHelper sysHelper;

    @ResponseBody
    @RequestMapping(value = "sayHello",method = RequestMethod.GET)
    @ApiOperation("测试是否正常 sayHelloWorld")
    public String hello(){
        return "hello tool world!";
    }

    @RequestMapping(value = "execToFile",method = RequestMethod.GET)
    @ApiOperation(value = "将执行的命令输出结果为文件",produces = "application/octet-stream")
    public void commmandToFile(@RequestParam String command,
                                 HttpServletResponse httpServletResponse){
        sysHelper.execCommandAndExportFile(command,httpServletResponse);
    }

    @ResponseBody
    @RequestMapping(value = "execToStr",method = RequestMethod.GET)
    @ApiOperation("将执行的命令输出")
    public String commmandToStr(@RequestParam String command){
        return sysHelper.execShellToStr(command);
    }

}
