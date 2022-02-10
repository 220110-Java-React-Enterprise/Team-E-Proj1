package Repository;

import java.io.FileWriter;
import java.io.Writer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileLogger {

    private static FileLogger fileLogger;
    private static String filePath;
    private static boolean consoleOutput;
    private static int stackTraceSize;
    private static Integer limitLoggerFailsMax = 10;
    private Integer limitLoggerFails;

    //creates singleton
    private FileLogger() {
        filePath = "real_proj1/logs/";
        consoleOutput = false;
        stackTraceSize = 10;
        limitLoggerFails = limitLoggerFailsMax;
    }

    //returns the filelogger if exists, if not creates one
    public static FileLogger getFileLogger() {
        if (fileLogger == null) {
            fileLogger = new FileLogger();
        }
        return fileLogger;
    }

    //overloaded function that takes in a string that changes the path
    public static FileLogger getFileLogger(String path) {
        if (fileLogger == null) {
            fileLogger = new FileLogger();
        }
        filePath = path;
        return fileLogger;
    }

    //function that logs the exception
    public void log(Exception e) {
        StringBuilder sb = new StringBuilder();
        sb.append(fileLogger.getTimestamp())
                .append(" - ")
                .append(e.getMessage())
                .append("\n")
                .append(fileLogger.formatStackTrace(e))
                .append("\n");
        fileLogger.writeToLog(sb.toString());
    }

    //overloaded function that logs the string inserted
    public void log(String str) {
        StringBuilder sb = new StringBuilder();
        sb.append(fileLogger.getTimestamp())
                .append(" - ")
                .append(str)
                .append("\n");
        fileLogger.writeToLog(sb.toString());
    }

    //gets time stamp
    private String getTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("[yyyy-MM-dd HH:mm:ss]");
        return formatter.format(LocalDateTime.now());
    }

    //gets file name
    private String getFileName() {
        return filePath + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".log";
    }

    //formats the file
    private String formatStackTrace(Exception e) {
        StackTraceElement[] stackTrace = e.getStackTrace();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < stackTraceSize; i++) {
            if (i >= stackTrace.length) {
                break;
            }
            sb.append("\t");
            sb.append(stackTrace[i]);
            sb.append("\n");
        }
        return sb.toString();
    }

    //writes a string to a log
    private void writeToLog(String text) {
        String fileName = getFileName();
        try (Writer fileWriter = new FileWriter(fileName, true)) {
            fileWriter.write(text);
        } catch (Exception e) {
            if (limitLoggerFails > 0) {
                limitLoggerFails--;
                writeToLog(text);
            }
        }

    }
}
