package jpl.mipl.mdms.FileService.komodo.ui.savannah.logging;

import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

import java.io.Serializable;
import java.nio.charset.Charset;

/**
 * @author wphyo
 * Created on 4/14/22.
 */
@Plugin(
        name = "GuiLogAppender",
        category = Core.CATEGORY_NAME,
        elementType = Appender.ELEMENT_TYPE)
public class GuiLogAppender extends AbstractAppender {
    protected LogMessagePublisherSingleton _publisher;

    protected GuiLogAppender(String name, Filter filter, Layout<? extends Serializable> layout) {
        super(name, filter, layout, true, null);
        _publisher = LogMessagePublisherSingleton.instance();
    }

    @PluginFactory
    public static GuiLogAppender createAppender(
            @PluginAttribute("name") String name,
            @PluginElement("Layout") Layout<? extends Serializable> layout,
            @PluginElement("Filter") Filter filter) {
        return new GuiLogAppender(name, filter, layout);
    }

    @Override
    public void append(LogEvent event) {
        String message = event.getMessage().getFormattedMessage();
        long timestamp = event.getTimeMillis();
//        String lfjLevelStr = event.getLevel().name();
        int l4jLevel = event.getLevel().intLevel();
        int level = LevelTranslation.translate(l4jLevel);
        LogEntry entry = new LogEntry(message, timestamp, level);
        entry.setLocation(event.getSource().toString());
        if (event.getThrown() != null) {
            entry.setThrowable(event.getThrown());
        }
        this._publisher.publish(entry);  // add entry to queue
    }
}
