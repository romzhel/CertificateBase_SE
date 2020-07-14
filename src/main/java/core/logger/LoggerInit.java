package core.logger;

import core.Dialogs;
import javafx.application.Platform;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.*;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import utils.Utils;

import java.io.File;

public class LoggerInit {

    public LoggerInit init() {
        if (!(new File(System.getProperty("user.dir") + "\\_lib").exists())) {
            Dialogs.showMessageTS("Ошибка инициализации", "Программа не может быть запущена, так как не " +
                    "обнаружена папка с библиотеками '_lib'");
            Platform.exit();
        }

        ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();

        builder.addProperty("basePath", System.getProperty("user.home") + "/AppData/Roaming/CertificateBase/");

        AppenderComponentBuilder fileAppender = builder.newAppender("FILE", "File");
        fileAppender.addAttribute("fileName", "${basePath}/logs/certBase_" + Utils.getDateTimeForFileName() + ".log");
        fileAppender.addAttribute("append", "true");
        LayoutComponentBuilder fileLayout = builder.newLayout("PatternLayout");
        fileLayout.addAttribute("pattern", "%d{yyyy-MM-dd HH:mm:ss.SSS} [%-10.-10t] %-5level %logger{36} - %msg%n");
        fileAppender.add(fileLayout);
        builder.add(fileAppender);

        AppenderComponentBuilder consoleAppender = builder.newAppender("STDOUT", "Console");
        consoleAppender.addAttribute("target", "SYSTEM_OUT");
        consoleAppender.add(builder.newLayout("PatternLayout")
                .addAttribute("pattern", "%d{HH:mm:ss.SSS} [%-10.-10t] %-5level %logger{36} - %msg%n"));
        consoleAppender.add(builder.newFilter("ThresholdFilter", Filter.Result.ACCEPT, Filter.Result.DENY)
                .addAttribute("level", Level.INFO));
        builder.add(consoleAppender);

        RootLoggerComponentBuilder rootLogger = builder.newRootLogger(Level.TRACE);
        rootLogger.add(builder.newAppenderRef("STDOUT"));
        rootLogger.add(builder.newAppenderRef("FILE"));
        builder.add(rootLogger);

        Configurator.initialize(builder.build());
        /*try {
            builder.writeXmlConfiguration(System.out);
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        return this;
    }
}