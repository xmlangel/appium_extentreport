package com.xmlangel.utils;

import com.xmlangel.base.InitialProcess;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.log4j.Log4j;

@Log4j
public class AppiumRunner extends InitialProcess {
    private static final String LOG_FILE_PATH = "/logs/appiumNode.log";

    /**
     * Appium을 시작해준다.
     * 만약 실행되고 있으면 종료시켜주고 실행한다.
     *
     * @throws IOException
     */
    public static void startAppiumService(int appiumPort) throws IOException, InterruptedException {
        stopAppiumService(appiumPort);

        File logFile = new File(System.getProperty("user.dir") + LOG_FILE_PATH);
        PrintStream fileOutputStream = new PrintStream(logFile);
        System.setOut(fileOutputStream);

        service = new AppiumServiceBuilder()
                .usingPort(appiumPort)
                .withArgument(GeneralServerFlag.LOG_LEVEL, "debug")
                .build();

        service.start();
        log.info("Appium Server started " + service.getUrl());
    }

    public static URL getServiceUrl() {
        return service.getUrl();
    }

    /**
     * Appium 을 종료시켜준다.
     * 실행 중인 Appium 서버 프로세스 ID를 찾아서 Appium 서버 프로세스를 종료합니다.
     *
     * @throws IOException
     * @throws InterruptedException
     */
    public static void stopAppiumService(int appiumPort) throws IOException, InterruptedException {
        log.info("stopAppiumService");
        String command = String.format("lsof -i :%d -a -c node -s TCP:LISTEN -t", appiumPort);
        Process process = Runtime.getRuntime().exec(command);
        log.info("command: " + command);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            List<String> pids = new ArrayList<>();
            String pid;
            while ((pid = reader.readLine()) != null) {
                pids.add(pid);
            }
            log.info("pids: " + pids);

            log.info("kill");
            for (String pidValue : pids) {
                String killCommand = String.format("kill -9 %s", pidValue);
                log.info("killCommand: " + killCommand);
                ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", killCommand);
                log.info("processBuilder: " + processBuilder);
                Process killProcess = processBuilder.start();
                int exitCode = killProcess.waitFor();
                log.info("Exit code: " + exitCode);
            }
        } finally {
            process.destroy();
        }
    }
}
