package seaBattle.model.serverFileService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * <code>ServerConf</code> exist for creating serverConf.xml file
 * by marshalling object based on <code>ServerConf</code> class into XML
 * @author Oleksandr Symonenko
 */

@XmlRootElement(name = "serverConf")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServerConf{

    String host;
    int port;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
