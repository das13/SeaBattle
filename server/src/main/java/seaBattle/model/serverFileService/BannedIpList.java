package seaBattle.model.serverFileService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * <code>BannedIpList</code> exist for creating bannedIpList.xml file
 * by marshalling object based on <code>BannedIpList</code> class into XML
 * @author Oleksandr Symonenko
 */

@XmlRootElement(name = "bannedIpList")
@XmlAccessorType(XmlAccessType.FIELD)
public class BannedIpList
{
    @XmlElement(name = "ip")
    private List<String> bannedIpList = null;

    public List<String> getBannedIpList() {
        return bannedIpList;
    }

    public void setBannedIpList(List<String> bannedIpList) {
        this.bannedIpList = bannedIpList;
    }
}
