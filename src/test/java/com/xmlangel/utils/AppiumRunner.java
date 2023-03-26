package com.xmlangel.utils;

import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;

import java.io.*;
import java.net.URL;

import lombok.extern.log4j.Log4j;

@Log4j
public class AppiumRunner {
    private static AppiumDriverLocalService service;
    private static final String LOG_FILE_PATH = "/logs/appiumNode.log";

    /**
     * Appium을 시작해준다.
     * 만약 실행되고 있으면 종료시켜주고 실행한다.
     *
     * @throws IOException
     */
    public static void startAppiumService(int appiumPort) throws IOException {
        stopAppiumService(appiumPort);

        File logFile = new File(System.getProperty("user.dir") + LOG_FILE_PATH);
        PrintStream fileOutputStream = new PrintStream(logFile);
        System.setOut(fileOutputStream);

        service = new AppiumServiceBuilder()
                .usingPort(appiumPort)
                .withArgument(GeneralServerFlag.LOG_LEVEL, "debug")
                .build();

        service.start();
        log.info("Appium Server started");
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
    public static void stopAppiumService(int appiumPort) throws IOException {
        String command = String.format("ps -ef | grep appium | grep %d | awk '{print $2}'", appiumPort);
        Process process = Runtime.getRuntime().exec(command);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String pid = reader.readLine();
            if (pid != null) {
                log.info("kill");

                String killCommand = String.format("kill -9 %s", pid);
                Runtime.getRuntime().exec(killCommand).waitFor();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            process.destroy();
        }
    }
}
