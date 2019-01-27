package cn.hans.common.utils.mail;


import org.apache.commons.lang3.StringUtils;

import java.io.*;

public class SystemUtil {

    /**
     * 执行指定的命令
     *
     * @param command       命令字符串
     * @return              {@link Process}
     */
    public static Process execShell(String command) {

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
    public static String execShellToStr(String command) {
        return readDataFromExec(execShell(command));
    }

    /**]
     *
     * @param process
     * @return              命令结果
     */
    public static String readDataFromExec(Process process){

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

    /**
     *  将命令结果输出
     * @param process           {@link Process}
     * @param outputStream      {@link OutputStream}
     */
    public static void commandResultToOut(Process process, OutputStream outputStream){

        if (process == null || outputStream == null) {
            return;
        }

        try {
            String fromExec = readDataFromExec(process);
            if (StringUtils.isNotBlank(fromExec)){
                outputStream.write(fromExec.getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static File execCommandPrintToFile(String command, String fileFullName) throws FileNotFoundException {

        if (StringUtils.isBlank(command) || StringUtils.isBlank(fileFullName)) {
            return null;
        }

        File file = new File(fileFullName);

        try (FileOutputStream fileOutputStream = new FileOutputStream(file)){

            Process process = execShell(command);
            if (process == null) {
                return file;
            }else {
                commandResultToOut(process,fileOutputStream);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }
}
