package core.logger;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.*;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import utils.Utils;

public class LoggerInit {

    public LoggerInit() {
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
    }
}
