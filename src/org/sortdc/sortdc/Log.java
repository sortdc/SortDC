package org.sortdc.sortdc;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {

    private static Log instance;
    private boolean verbose = true;
    private String filepath = "sortdc.log";

    private Log() {
    }

    /**
     * Creates a unique instance of Log (Singleton)
     *
     * @return Instance of Log
     */
    public static synchronized Log getInstance() {
        if (instance == null) {
            instance = new Log();
        }
        return instance;
    }

    /**
     * Enables or disables the verbose mode
     *
     * @param verbose
     */
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    /**
     * Sets the log file path
     *
     * @param filepath
     */
    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    /**
     * Adds a line to the log file and displays it if verbose mode is enabled
     *
     * @param line
     */
    public void add(String line) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = sdf.format(new Date());
            if (this.verbose) {
                System.out.println("[" + date + "] " + line);
            }
            FileWriter f = new FileWriter(this.filepath, true);
            BufferedWriter bf = new BufferedWriter(f);
            bf.write("[" + date + "] " + line + "\n");
            bf.close();
            f.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Adds an Exception (message + stacktrace) to the log file and displays it if verbose mode is enabled
     *
     * @param line
     */
    public void add(Exception e) {
        String message = e.getMessage();
        try {
            StackTraceElement[] elements = e.getStackTrace();
            for (StackTraceElement element : elements) {
                message += "\n    " + element;
            }
        } catch (Exception ex) {
        }
        this.add(message);
    }
}
