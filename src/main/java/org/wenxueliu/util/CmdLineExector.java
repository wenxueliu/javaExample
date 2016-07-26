package org.wenxueliu.util;


import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.BufferedOutputStream;

import org.apache.commons.exec.Executor;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.ExecuteResultHandler;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ProcessDestroyer;
import org.apache.commons.exec.ShutdownHookProcessDestroyer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * default workDir : current directory
 * default ExitValue : 0
 * default Stream   : stdout, stdin
 *
 * TODO
 *
 * Usage :
 *
 * Sync style:
 *
 *  ExecuteResultHandler result = CmdLineExector.withCmdLine("ls /").withTimeout(5000).build().Async();
 *  while(!result.hasResult()) {
 *      result.waitFor(1000); //ms
 *  }
 *  int ret = result.getExitValue();
 *
 * ASync style:
 *
 *  int ret = CmdLineExector.withCmdLine("ls /").withTimeout(5000).build().Sync();
 *
 * 1. OutputStream : such as sudo is needed
 */
public class CmdLineExector {

    private static Logger LOG = LoggerFactory.getLogger(CmdLineExector.class);

    /** the default size of the internal buffer for copying the streams */
    private static final int DEFAULT_SIZE = 1024;

    private Executor executor = new DefaultExecutor();
    private PumpStreamHandler streamHandler;
    private ExecuteWatchdog watchdog;

    private ByteArrayOutputStream outputBuffer = new ByteArrayOutputStream(DEFAULT_SIZE);
    private ByteArrayOutputStream errBuffer = new ByteArrayOutputStream(DEFAULT_SIZE);
    private OutputStream outputStream = new BufferedOutputStream(outputBuffer); //read process output;
    private OutputStream errStream = new BufferedOutputStream(errBuffer);    //read process error;
    private InputStream inputStream; //write to process stdin

    private String cmdString;
    private long timeoutMs = ExecuteWatchdog.INFINITE_TIMEOUT;

    boolean sudo = false;
    String passwd = null;

    private CmdLineExector() {
    }

    /*
     * -1 : cmdLine is null or empty space
     *
     * TODO
     * inputStream : such as run cmd with sudo
     */
    public DefaultExecuteResultHandler Async() {

        CommandLine cmdLine = CommandLine.parse(this.cmdString);

        this.watchdog = new ExecuteWatchdog(this.timeoutMs);

        if (this.sudo) {
            this.streamHandler = new PumpStreamHandler(this.outputStream, this.errStream, this.inputStream);
        } else {
            this.streamHandler = new PumpStreamHandler(this.outputStream, this.errStream);
        }

        if (this.timeoutMs > 0) {
            this.streamHandler.setStopTimeout(this.timeoutMs);
        }

        DefaultExecuteResultHandler resultHandler = new LogExecutorResultHandler(this.watchdog);
        ProcessDestroyer processHandler = new ShutdownHookProcessDestroyer();

        executor.setWatchdog(watchdog);
        executor.setStreamHandler(streamHandler);
        executor.setProcessDestroyer(processHandler);

        try {
            this.executor.execute(cmdLine, resultHandler);
        } catch (ExecuteException e) {
            LOG.error("exec {} error : {}", this.cmdString, e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            LOG.error("exec {} error : {}", this.cmdString, e.getMessage());
            e.printStackTrace();
        }

        final Thread outputThread = new Thread(new LogOutStream(resultHandler), "cmdLine process output handler");
        final Thread errThread = new Thread(new LogOutStream(resultHandler), "cmdLine process error handler");
        outputThread.start();
        errThread.start();

        //TODO inputThread
        return resultHandler;
    }


    public int Sync() {

        CommandLine cmdLine = null;
        try {
            cmdLine = CommandLine.parse(cmdString);
        } catch (IllegalArgumentException e) {
            LOG.error("parse cmdLine error {}", e.getMessage());
            return -1;
        }

        this.watchdog = new ExecuteWatchdog(this.timeoutMs);

        if (this.sudo) {
            streamHandler = new PumpStreamHandler(this.outputStream, this.errStream, this.inputStream);
        } else {
            streamHandler = new PumpStreamHandler(this.outputStream, this.errStream);
        }

        if (this.timeoutMs > 0) {
            streamHandler.setStopTimeout(this.timeoutMs);
        }

        DefaultExecuteResultHandler resultHandler = new LogExecutorResultHandler(this.watchdog);
        ProcessDestroyer processHandler = new ShutdownHookProcessDestroyer();

        executor.setWatchdog(watchdog);
        executor.setStreamHandler(streamHandler);
        executor.setProcessDestroyer(processHandler);

        final Thread outputThread = new Thread(new LogOutStream(resultHandler), "cmdLine process output handler");
        final Thread errThread = new Thread(new LogOutStream(resultHandler), "cmdLine process error handler");
        outputThread.start();
        errThread.start();

        try {
            executor.execute(cmdLine, resultHandler);
        } catch (ExecuteException e) {
            LOG.error("exec {} error : {}", this.cmdString, e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            LOG.error("exec {} error : {}", this.cmdString, e.getMessage());
            e.printStackTrace();
        }

        try {
            if (this.timeoutMs != 0) {
                resultHandler.waitFor(this.timeoutMs);
            } else {
                resultHandler.waitFor();
            }
        } catch (InterruptedException e) {
            LOG.error("the wait for by interrupted");
        }

        int ret = resultHandler.getExitValue();
        if (ret != 0) {
            LOG.error("exec {} with error: {}", cmdString, resultHandler.getException().getMessage());
        }
        return ret;
    }

    private class LogExecutorResultHandler extends DefaultExecuteResultHandler {

        private ExecuteWatchdog watchdog;

        public LogExecutorResultHandler(ExecuteWatchdog watchdog) {
            this.watchdog = watchdog;
        }

        @Override
        public void onProcessComplete(final int exitValue) {
            super.onProcessComplete(exitValue);
            LOG.info("exec {} successfully", cmdString);
        }

        @Override
        public void onProcessFailed(final ExecuteException e) {
            super.onProcessFailed(e);
            if (watchdog != null && watchdog.killedProcess()) {
                LOG.error("exec {} timeout", cmdString);
            } else {
                LOG.error("exec {} with error: {} ", cmdString, e.getMessage());
            }
        }
    }

    private class LogOutStream implements Runnable {

        DefaultExecuteResultHandler resultHandler;

        public LogOutStream(DefaultExecuteResultHandler resultHandler) {
            this.resultHandler = resultHandler;
        }

        @Override
        public void run() {
            //StringBuilder outputString = new StringBuilder();
            //BufferedReader inputBuffer = new BufferedReader(new InputStreamReader(this.os));
            //String outLine = null;
            //while((outLine = os.readLine()) != null) {
            //    outputString.append(outLine).append("\n");
            //}
            long internal = 100;
            while (!this.resultHandler.hasResult()) {
                try {
                    this.resultHandler.waitFor(internal); //ms
                } catch (InterruptedException e) {
                    LOG.error("the wait for by interrupted");
                }
            }
            LOG.debug("the output of {} is {}", cmdString, outputBuffer.toString());
            LOG.debug("the error of {} is {}", cmdString, errBuffer.toString());
        }
    }

    public static class CmdLineExectorBuilder {

        private CmdLineExector executor;

        public CmdLineExectorBuilder() {
            this.executor = new CmdLineExector();
        }

        public CmdLineExectorBuilder withCmdStr(String cmdString) {
            this.executor.cmdString = cmdString;
            return this;
        }

        public CmdLineExectorBuilder withSudo(String passwd) {
            this.executor.sudo = true;
            this.executor.passwd = passwd;
            return this;
        }

        public CmdLineExectorBuilder withTimeout(long timeoutMs) {
            this.executor.timeoutMs = timeoutMs;
            return this;
        }

        /*
         * default : 0
         */
        public CmdLineExectorBuilder withExitValue(int successRet) {
            this.executor.executor.setExitValue(successRet);
            return this;
        }

        /*
         * default : current directory
         */
        public CmdLineExectorBuilder withWorkDir(String execPath) {
            if (execPath != null) {
                this.executor.executor.setWorkingDirectory(new File(execPath));
            }
            return this;
        }

        public CmdLineExectorBuilder withOutStream(OutputStream outputStream, OutputStream errStream) {
            this.executor.outputStream = outputStream;
            this.executor.errStream = errStream;
            return this;
        }

        public CmdLineExectorBuilder withInputStream(InputStream inputStream) {
            this.executor.inputStream = inputStream;
            return this;
        }

        public void virityBuilder() {
            if (this.executor.cmdString == null || this.executor.cmdString.trim().length() == 0) {
                throw new IllegalArgumentException("Command line can not be empty");
            }
            File workdir = this.executor.executor.getWorkingDirectory();
            if (workdir != null && (!workdir.exists() || !workdir.isDirectory())) {
                LOG.error("the workdirector is illegal, set as defautl current directory");
                this.executor.executor.setWorkingDirectory(new File("."));
            }
        }

        public CmdLineExector build() {
            virityBuilder();
            return this.executor;
        }
    }

    public static void test() {
        DefaultExecuteResultHandler result = new CmdLineExectorBuilder().withCmdStr("ls /").withTimeout(5000).build().Async();
        while(!result.hasResult()) {
            try {
                result.waitFor(500); //ms
            } catch (InterruptedException e) {
                LOG.error("the wait for by interrupted");
            }
        }
        int ret = result.getExitValue();
        LOG.info("exec ls / : the ret is {}", ret);


        ret = new CmdLineExectorBuilder().withCmdStr("ls /").withTimeout(5000).build().Sync();
        LOG.info("exec ls / : the ret is {}", ret);
    }
}
