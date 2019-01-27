package cn.hans.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.util.*;

public class JVMUtil {


    private static final int CPUTIME = 5000;
    /**
     * 网管进程信息采集周期(注意：PERIOD_TIME 一定要大于 SLEEP_TIME )
     */
    private static final int PERIOD_TIME = 1000 * 60 * 15;
    /**
     * 此类中Thread.sleep()里的线程睡眠时间
     */
    private static final int SLEEP_TIME = 1000 * 60 * 9;
    private static final int PERCENT = 100;
    private static final int FAULTLENGTH = 10;
    private String isWindowsOrLinux = isWindowsOrLinux();
    private String pid = "";
    private Timer sysInfoGetTimer = new Timer("sysInfoGet");


    public Logger log = LoggerFactory.getLogger(this.getClass());


    /**
     * 初始化bean的时候就立即获取JVM进程的PID及执行任务
     *  
     *
     * @return
     */
    @PostConstruct
    public void init() {
        getJvmPIDOnLinux();
    }


    /**
     * 判断是服务器的系统类型是Windows 还是 Linux
     *  
     *
     * @return
     */
    public String isWindowsOrLinux() {
        String osName = System.getProperty("os.name");
        String sysName = "";
        if (osName.toLowerCase().startsWith("windows")) {
            sysName = "windows";
        } else if (osName.toLowerCase().startsWith("linux")) {
            sysName = "linux";
        }
        return sysName;
    }


    /**
     * 获取JVM 的CPU占用率（%）
     *  
     *
     * @return
     */
    public String getCPURate() {
        return getCPURateForLinux();
    }



    /**
     * linux环境下获取JVM的PID
     */
    public String getJvmPIDOnLinux() {
        String pid = ManagementFactory.getRuntimeMXBean().getName();
        System.out.println("当前JVM Process ID: " + pid);

        int indexOf = pid.indexOf('@');
        if (indexOf > 0) {
            pid = pid.substring(0, indexOf);
        }

        System.out.println("当前JVM Process ID: " + pid);
        return pid;
    }


    /**
     * 获取JVM的内存占用率（%）
     *  
     *
     * @return
     */
    public String getMemoryRate() {
        return getMemoryRateForLinux();
    }


    /**
     * 获取JVM 线程数
     *  
     *
     * @return
     */
    public int getThreadCount() {
        return getThreadCountForLinux();
    }




    /**
     * 获取Linux环境下网口的上下行速率
     *  
     *
     * @return
     */
    public String getNetworkThroughputForLinux() {
        Process pro1 = null;
        Process pro2 = null;
        Runtime r = Runtime.getRuntime();
        BufferedReader input = null;
        String rxPercent = "";
        String txPercent = "";
        JSONObject jsonObject = new JSONObject();
        try {
            String command = "watch ifconfig";
            pro1 = r.exec(command);
            input = new BufferedReader(new InputStreamReader(pro1.getInputStream()));


            String result1[] = readInLine(input, "linux");
            Thread.sleep(SLEEP_TIME);
            pro2 = r.exec(command);
            input = new BufferedReader(new InputStreamReader(pro2.getInputStream()));
            String result2[] = readInLine(input, "linux");
            rxPercent = formatNumber((Long.parseLong(result2[0]) - Long.parseLong(result1[0]))
                    / (float) (1024 * 1024 * (SLEEP_TIME / 1000))); // 下行速率(MB/s)
            txPercent = formatNumber((Long.parseLong(result2[1]) - Long.parseLong(result1[1]))
                    / (float) (1024 * 1024 * (SLEEP_TIME / 1000))); // 上行速率(MB/s)
            input.close();
            pro1.destroy();
            pro2.destroy();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        jsonObject.put("rxPercent", rxPercent); // 下行速率
        jsonObject.put("txPercent", txPercent); // 上行速率
        return JSON.toJSONStringWithDateFormat(jsonObject, "yyyy-MM-dd HH:mm:ss", SerializerFeature.WriteMapNullValue);
    }



    /**
     * 获取linux环境下JVM的cpu占用率
     *  
     *
     * @return
     */
    public String getCPURateForLinux() {
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader brStat = null;
        StringTokenizer tokenStat = null;
        String user = "";
        String linuxVersion = System.getProperty("os.version");
        try {
            System.out.println("Linux版本: " + linuxVersion);

            String pid = ManagementFactory.getRuntimeMXBean().getName();
            Process process = Runtime.getRuntime().exec(new String[]{"sh", "-c", "top -b -p " + pid});
            try {
// top命令默认3秒动态更新结果信息，让线程睡眠5秒以便获取最新结果
                Thread.sleep(CPUTIME);
                is = process.getInputStream();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            isr = new InputStreamReader(is);
            brStat = new BufferedReader(isr);


            brStat.readLine();
            brStat.readLine();
            brStat.readLine();
            brStat.readLine();
            brStat.readLine();
            brStat.readLine();
            brStat.readLine();
            brStat.readLine();
            brStat.readLine();
            brStat.readLine();
            brStat.readLine();
            brStat.readLine();
            tokenStat = new StringTokenizer(brStat.readLine());
            tokenStat.nextToken();
            String userUsage = tokenStat.nextToken(); // 用户空间占用CPU百分比
            user = userUsage.substring(0, userUsage.indexOf("%"));
            process.destroy();


        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
            freeResource(is, isr, brStat);
            return "100";
        } finally {
            freeResource(is, isr, brStat);
        }
        return user; // jvm cpu占用率
    }


    /**
     * 获取Linux环境下JVM的内存占用率
     *  
     *
     * @return
     */
    public String getMemoryRateForLinux() {
        Process pro = null;
        Runtime r = Runtime.getRuntime();
        String remCount = "";
        try {
            String command = "top -b  -n 1 -H -p" + pid;
            pro = r.exec(command);
            BufferedReader in = new BufferedReader(new InputStreamReader(pro.getInputStream()));
            in.readLine();
            in.readLine();
            in.readLine();
            in.readLine();
            in.readLine();
            in.readLine();
            in.readLine();
            StringTokenizer ts = new StringTokenizer(in.readLine());
            int i = 1;
            while (ts.hasMoreTokens()) {
                i++;
                ts.nextToken();
                if (i == 10) {
                    remCount = ts.nextToken();
                }
            }
            in.close();
            pro.destroy();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return remCount;
    }


/**
 * 获取linux磁盘读写速率
 * 
 * @return
 */
// public String getDiskAccessForLinux() {
// Process pro = null;
// Runtime r = Runtime.getRuntime();
// String command = "time dd if=/dev/zero of=/temp.log bs=1024k count=1000";
// // 读取和写入1G数据
// BufferedReader in = null;
// float result = 0.0f;
// try {
// pro = r.exec(new String[] { "sh", "-c", command });
// // pro.getInputStream()取不到结果内容，反而是getErrorStream()可以取到值
// in = new BufferedReader(new InputStreamReader(pro.getErrorStream()));
// in.readLine();
// in.readLine();
// if (getLocale().indexOf("zh") != -1) { // 中文语言环境
// StringTokenizer ts = new StringTokenizer(in.readLine(), "，");
// ts.nextToken();
// ts.nextToken();
// result = Float.parseFloat(ts.nextToken().split(" ")[0]);
// System.out.println("中文环境");
// } else { // 英文语言环境
// StringTokenizer ts = new StringTokenizer(in.readLine());
// ts.nextToken();
// ts.nextToken();
// ts.nextToken();
// ts.nextToken();
// ts.nextToken();
// ts.nextToken();
// ts.nextToken();
// result = Float.parseFloat(ts.nextToken().split(" ")[0]);
// System.out.println("英文环境");
// }
// r.exec("rm -f /temp.log"); // 将生成的文件删掉
// in.close();
// pro.destroy();
// } catch (IOException e) {
// System.out.println(e.getMessage());
// }
// return String.valueOf(result);
// }


    /**
     * 获取Linux服务器的语言环境
     *  
     *
     * @return
     */
    public String getLocale() {
        Process pro = null;
        Runtime r = Runtime.getRuntime();
        String command = "locale";
        BufferedReader in = null;
        StringTokenizer ts = null;
        try {
            pro = r.exec(command);
            in = new BufferedReader(new InputStreamReader(pro.getInputStream()));
            ts = new StringTokenizer(in.readLine());
            in.close();
            pro.destroy();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return ts.nextToken();
    }


    /**
     * 获取Linux环境下JVM的线程数
     *  
     *
     * @return
     */
    public int getThreadCountForLinux() {
        Process pro = null;
        Runtime r = Runtime.getRuntime();
        String command = "top -b -n 1 -H -p " + pid;
        BufferedReader in = null;
        int result = 0;
        try {
            pro = r.exec(new String[]{"sh", "-c", command});
            in = new BufferedReader(new InputStreamReader(pro.getInputStream()));
            in.readLine();
            StringTokenizer ts = new StringTokenizer(in.readLine());
            ts.nextToken();
            result = Integer.parseInt(ts.nextToken());
            in.close();
            pro.destroy();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;


    }



    private void freeResource(InputStream is, InputStreamReader isr, BufferedReader br) {
        try {
            if (is != null) {
                is.close();
            }
            if (isr != null) {
                isr.close();
            }
            if (br != null) {
                br.close();
            }
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }


    /**
     *  
     * 读取CPU信息
     *  
     *
     * @param proc
     * @return  
     */
    private long[] readCpu(final Process proc) {
        long[] retn = new long[2];
        try {
            proc.getOutputStream().close();
            InputStreamReader ir = new InputStreamReader(proc.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            String line = input.readLine();
            if (line == null || line.length() < FAULTLENGTH) {
                return null;
            }
            int capidx = line.indexOf("Caption");
            int cmdidx = line.indexOf("CommandLine");
            int rocidx = line.indexOf("ReadOperationCount");
            int umtidx = line.indexOf("UserModeTime");
            int kmtidx = line.indexOf("KernelModeTime");
            int wocidx = line.indexOf("WriteOperationCount");
// Caption,CommandLine,KernelModeTime,ReadOperationCount,ThreadCount,UserModeTime,WriteOperationCount
            long idletime = 0;
            long kneltime = 0;
            long usertime = 0;
            while ((line = input.readLine()) != null) {
                if (line.length() < wocidx) {
                    continue;
                }
// 字段出现顺序：Caption,CommandLine,KernelModeTime,ReadOperationCount,
// ThreadCount,UserModeTime,WriteOperation
                String caption = this.substring(line, capidx, cmdidx - 1).trim();
                String cmd = this.substring(line, cmdidx, kmtidx - 1).trim();
                if (cmd.indexOf("javaw.exe") >= 0) {
                    continue;
                }
// log.info("line="+line);
                if (caption.equals("System Idle Process") || caption.equals("System")) {
                    idletime += Long.valueOf(this.substring(line, kmtidx, rocidx - 1).trim()).longValue();
                    idletime += Long.valueOf(this.substring(line, umtidx, wocidx - 1).trim()).longValue();
                    continue;
                }


                kneltime += Long.valueOf(this.substring(line, kmtidx, rocidx - 1).trim()).longValue();
                usertime += Long.valueOf(this.substring(line, umtidx, wocidx - 1).trim()).longValue();
            }
            retn[0] = idletime;
            retn[1] = kneltime + usertime;
            return retn;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                proc.getInputStream().close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    /**
     * 获取网口上下行速率
     *  
     *
     * @param input
     * @return
     */
    public String[] readInLine(BufferedReader input, String osType) {
        String rxResult = "";
        String txResult = "";
        StringTokenizer tokenStat = null;
        try {
            if (osType.equals("linux")) { // 获取linux环境下的网口上下行速率
                String result[] = input.readLine().split(" ");
                int j = 0, k = 0;
                for (int i = 0; i < result.length; i++) {
                    if (result[i].indexOf("RX") != -1) {
                        j++;
                        if (j == 2) {
                            rxResult = result[i + 1].split(":")[1];
                        }
                    }
                    if (result[i].indexOf("TX") != -1) {
                        k++;
                        if (k == 2) {
                            txResult = result[i + 1].split(":")[1];
                            break;
                        }
                    }
                }


            } else { // 获取windows环境下的网口上下行速率
                input.readLine();
                input.readLine();
                input.readLine();
                input.readLine();
                tokenStat = new StringTokenizer(input.readLine());
                tokenStat.nextToken();
                rxResult = tokenStat.nextToken();
                txResult = tokenStat.nextToken();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        String arr[] = {rxResult, txResult};
        return arr;
    }


    /**
     * 由于String.subString对汉字处理存在问题（把一个汉字视为一个字节)，因此在 包含汉字的字符串时存在隐患，现调整如下：
     *  
     *
     * @param src                  要截取的字符串
     * @param start_idx            开始坐标（包括该坐标)
     * @param end_idx              截止坐标（包括该坐标）
     * @return
     */
    private String substring(String src, int start_idx, int end_idx) {
        byte[] b = src.getBytes();
        String tgt = "";
        for (int i = start_idx; i <= end_idx; i++) {
            tgt += (char) b[i];
        }
        return tgt;
    }


    /**
     * 格式化浮点数(float 和 double)，保留两位小数
     *  
     *
     * @param obj
     * @return
     */
    private String formatNumber(Object obj) {
        String result = "";
        if (obj.getClass().getSimpleName().equals("Float")) {
            result = new Formatter().format("%.2f", (float) obj).toString();
        } else if (obj.getClass().getSimpleName().equals("Double")) {
            result = new Formatter().format("%.2f", (double) obj).toString();
        }
        return result;
    }


    /**
     * 测试方法 ：监测java执行相关命令后是否能获取到结果集(注：此方法执行后会中断程序的执行，测试完后请注释掉)
     *  
     */
    public void testGetInput(BufferedReader in) {
        int y = 0;
        try {
            while ((y = in.read()) != -1) {
                System.out.print((char) y);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }


    class SysInfoAcquirerTimerTask extends TimerTask {


        @Override
        public void run() {
            try {
                System.out.println("任务开始：");
                long startTime = System.currentTimeMillis();
                int threadCount = getThreadCount();
                String cpuRate = getCPURate(); // CPU使用率
                String memoryRate = getMemoryRate(); // 内存占用率
//                JSONObject jsonObj = JSON.parseObject(getNetworkThroughput());
//                String upSpeed = jsonObj.getString("txPercent");// 上行速度
//                String downSpeed = jsonObj.getString("rxPercent"); // 下行速度
                System.out.println("JVM  PID：" + pid);
                System.out.println("JVM 线程数：" + threadCount);
                System.out.println("内存占用率：" + memoryRate + "%");
                System.out.println("CPU使用率：" + cpuRate + "%");
//                System.out.println("上行速度：" + upSpeed + "MB/s 下行速度：" + downSpeed + "MB/s");
                //后续操作为将采集数据存放到数据库中，可自行设计
            /*SysPerformInfo sysPerformInfo = new SysPerformInfo();
            sysPerformInfo.setId(UuidUtil.getUuid());
            sysPerformInfo.setCpuRate(Float.parseFloat(cpuRate));
            sysPerformInfo.setMemoryRate(Float.parseFloat(memoryRate));
            sysPerformInfo.setThreadCount(threadCount);
            sysPerformInfo.setUpSpeed(Float.parseFloat(upSpeed));
            sysPerformInfo.setDownSpeed(Float.parseFloat(downSpeed));
            sysPerformInfoService.insertPerformInfo(sysPerformInfo); // 将采集到的数据插入数据库*/
                long endTime = System.currentTimeMillis();
                System.out.println("任务总耗时：" + (endTime - startTime) / (1000 * 60) + "分钟");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    public static void main(String[] args) {

        System.out.println("当前JVM Process ID: " + new JVMUtil().getJvmPIDOnLinux());

        while (true){
            System.out.println(new Date().getTime());
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }


}
