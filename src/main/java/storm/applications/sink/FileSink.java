package storm.applications.sink;

import backtype.storm.tuple.Tuple;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storm.applications.constants.BaseConstants.BaseConf;
import storm.applications.util.ConfigUtility;
import storm.applications.util.StringUtil;

public class FileSink extends BaseSink {
    private static final Logger LOG = LoggerFactory.getLogger(FileSink.class);
            
    private Writer writer = null;
    private String file;
    
    private String pathKey = BaseConf.SINK_PATH;
    
    @Override
    public void initialize() {
        super.initialize();
        
        file = ConfigUtility.getString(config, pathKey);
        
        Map<String, Object> map = new HashMap<>(3);
        map.put("taskid", context.getThisTaskId());
        map.put("taskindex", context.getThisTaskIndex());
        map.put("componentid", context.getThisComponentId());
        
        file = StringUtil.dictFormat(file, map);

        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                  new FileOutputStream(file), "utf-8"));
        } catch (IOException ex) {
            LOG.error("Error while creating file " + file, ex);
        }
    }

    @Override
    public void execute(Tuple tuple) {
        try {
            writer.write(formatter.format(tuple));
        } catch (IOException ex) {
            LOG.error("Error while writing to file " + file, ex);
        }
    }

    @Override
    public void cleanup() {
        super.cleanup();
        
        try {
            writer.close();
        } catch (IOException ex) {
            LOG.error("Error while closing the file " + file, ex);
        }
    }

    @Override
    protected Logger getLogger() {
        return LOG;
    }

    public void setPathKey(String pathKey) {
        this.pathKey = pathKey;
    }
    
}
