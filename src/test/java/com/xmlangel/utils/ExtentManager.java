package com.xmlangel.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentReporter;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import lombok.extern.log4j.Log4j;
import org.testng.annotations.Test;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import static com.xmlangel.base.InitialProcess.getPlatformName;

@Log4j
public class ExtentManager {
    private static ExtentReports extent = new ExtentReports();
    private static String path;

    /**
     * Extents Report 생성
     *
     * @return
     */
    public static ExtentReports createInstance() {
        String fileName = getReportName();
        String directory = getClassPathRootDir() + "/reports/";
        new File(directory).mkdirs();
        setPath(directory, fileName);
        log.info("fimeName = " + getPath());

        ExtentSparkReporter reporter = new ExtentSparkReporter(getPath());
        reporter.config().setEncoding("utf-8");
        reporter.config().setTheme(Theme.STANDARD);
        reporter.config().thumbnailForBase64(true);
        reporter.config().setDocumentTitle("Automation");
        log.info("Report platformName = " + getPlatformName());
        reporter.config().setReportName(getPlatformName() + " Test Results");

        extent.attachReporter(reporter);

        return extent;
    }

    private static String getClassPathRootDir() {
        return System.getProperty("user.dir");
    }

    private static String getReportName() {
        Date d = new Date();
        String fimeName = "AutomationReport" + "_" + d.toString().replace(":", "_").replace(" ", "_") + ".html";
        log.info(fimeName);
        return fimeName;

    }

//    public void deleteFile() {
//        // Calendar 객체 생성
//        Calendar cal = Calendar.getInstance();
//        long todayMil = cal.getTimeInMillis();        // 현재 시간(밀리 세컨드)
//        long oneDayMil = 24 * 60 * 60 * 1000;            // 일 단위
//
//        Calendar fileCal = Calendar.getInstance();
//        Date fileDate = null;
//
//
//        File path = new File(getClassPathRootDir() + "/reports");
//        File[] list = path.listFiles();            // 파일 리스트 가져오기
//
//        for (File listvalue : list){
//            log.info(listvalue);
//            fileDate = new Date(listvalue.lastModified());
//
//        }
//        for (int j = 0; j < list.length; j++) {
//
//            // 파일의 마지막 수정시간 가져오기
//            fileDate = new Date(list[j].lastModified());
//
//            // 현재시간과 파일 수정시간 시간차 계산(단위 : 밀리 세컨드)
//            fileCal.setTime(fileDate);
//            long diffMil = todayMil - fileCal.getTimeInMillis();
//
//            //날짜로 계산
//            int diffDay = (int) (diffMil / oneDayMil);
//
//            // 3일이 지난 파일 삭제
//            if (diffDay > 3 && list[j].exists()) {
//                list[j].delete();
//                log.info(list[j].getName() + " 파일을 삭제했습니다.");
//            }

//        }
//    }
//    @Test
//    public void getFilelist(){
//        deleteFile();
//    }
    public static String getPath() {
        return path;
    }

    public static void setPath(String directory, String filename) {
        path = directory + filename;
    }

}
