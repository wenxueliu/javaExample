package org.wenxueliu.log;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * http://logback.qos.ch/xref/chapters/configuration/MyApp3.html
 * http://stackoverflow.com/questions/21885787/setting-logback-xml-path-programatically
 */
public class LogConfig {
    private static Logger logger = LoggerFactory.getLogger(LogConfig.class);

    public static void testReloadLog() {
        String logPath = System.getProperty("logback.configurationFile");
        modifyFile(logPath);

        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        try {
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(context);
            // Call context.reset() to clear any previous configuration, e.g. default
            // configuration. For multi-step configuration, omit calling context.reset().
            context.reset();
            configurator.doConfigure(logPath);
        } catch (JoranException je) {
            // StatusPrinter will handle this
        }
        StatusPrinter.printInCaseOfErrorsOrWarnings(context);
    }

    public static void modifyFile(String path) {
        logger.info("path: {}", Paths.get(path).toAbsolutePath());
        List<String> fileContent = new ArrayList<String>();
        try {
            fileContent = new ArrayList<String>(Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8));
        } catch (IOException e) {
            logger.error("read file {} error {}", path, e.getMessage());
        }

        String newLine;
        for (int i = 0; i < fileContent.size(); i++) {
            logger.info("line: {}", fileContent.get(i));
            if (fileContent.get(i).matches(".*root level=.*")) {
                logger.info("match log");
                newLine = "  <root level=\"DEBUG\">";
                fileContent.set(i, newLine);
                break;
            }
        }
        try {
            Files.write(Paths.get(path), fileContent, StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.error("write file {} error {}", path, e.getMessage());
        }
    }
}
