package cn.hans.tool.helper;

import cn.hans.common.utils.FileUtil;
import cn.hans.common.utils.JVMUtil;
import cn.hans.common.utils.mail.SystemUtil;
import org.springframework.stereotype.Component;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;

@Component
public class SysHelper {



    public void execCommandAndExportFile(String commmand, HttpServletResponse httpServletResponse){

        Process process = SystemUtil.execShell(commmand);
        if (process == null)return;

        FileUtil.wrapperFileDownloadResponse(httpServletResponse,"command.txt");

        try (ServletOutputStream outputStream = httpServletResponse.getOutputStream()){

            SystemUtil.commandResultToOut(process,outputStream);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public  Process execShell(String command) {

        String[] commandArray = command.split(" ");
        try {
            Process exec = Runtime.getRuntime().exec(commandArray);

            exec.waitFor();

            if (exec.exitValue() != 0){
                System.out.println("执行命令错误");
            }
            return exec;

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 执行指定的命令
     *
     * @param command       命令字符串
     * @return              {@link Process}
     */
    public  String execShellToStr(String command) {
        return new JVMUtil().getCPURateForLinux();

//        return readDataFromExec(execShell(command));
    }

    /**]
     *
     * @param process
     * @return              命令结果
     */
    public  String readDataFromExec(Process process){

        if (process == null){return null;}

        try (InputStream inputStream = process.getInputStream();
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             BufferedReader br = new BufferedReader(inputStreamReader)){
            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getJavaPid(){
        return ManagementFactory.getRuntimeMXBean().getName();
    }

}
