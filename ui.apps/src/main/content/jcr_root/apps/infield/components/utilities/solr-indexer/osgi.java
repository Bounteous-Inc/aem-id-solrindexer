package apps.infield.components.utilities.solr_indexer;

import com.adobe.cq.sightly.WCMUse;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.Configuration;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.api.scripting.SlingScriptHelper;
import java.util.Dictionary;

public class osgi extends WCMUse {

    private String protocol;
    private String host;
    private String port;
    private String updatePath;
    private final String SOLR_CONFIG_PID = "com.headwire.aemsolrsearch.services.SolrConfigurationService";
    private final String SOLR_CONFIG_PROTOCOL = "solr.protocol";
    private final String SOLR_CONFIG_HOST = "solr.server.name";
    private final String SOLR_CONFIG_PORT = "solr.server.port";
    private final String SOLR_CONFIG_UPDATE = "sling.servlet.paths";

    @Override
    public void activate() throws Exception {

        try {

            // get basic bindings and classes:
            SlingBindings bindings = (SlingBindings) getRequest().getAttribute(SlingBindings.class.getName());
            SlingScriptHelper sling = bindings.getSling();
            ConfigurationAdmin configAdmin = sling.getService(ConfigurationAdmin.class);

            // AEM Solr Search - Solr Configuration Service
            Configuration config = configAdmin.getConfiguration(SOLR_CONFIG_PID);
            Dictionary props = config.getProperties();

            if (props != null) {
                this.protocol = PropertiesUtil.toString(props.get(SOLR_CONFIG_PROTOCOL), "http");
                this.host = PropertiesUtil.toString(props.get(SOLR_CONFIG_HOST), "localhost");
                this.port = PropertiesUtil.toString(props.get(SOLR_CONFIG_PORT), "8080");
            } else {
                this.protocol = "http";
                this.host = "localhost";
                this.port = "8080";
            }

            // FIXME: this ideally shouldn't be hard coded
            this.updatePath = "/apps/infield/solr/updatehandler";

        } catch (Exception e) {

        }

    }

    public String getProtocol(){
        return this.protocol;
    }

    public String getHost(){
        return this.host;
    }

    public String getPort(){
        return this.port;
    }

    public String getUpdatePath(){
        return this.updatePath;
    }

}
