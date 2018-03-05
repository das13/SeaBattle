package seaBattle.model.serverFileService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "adminsList")
@XmlAccessorType(XmlAccessType.FIELD)
public class AdminsList
{
    @XmlElement(name = "login")
    private List<String> adminList = null;

    public List<String> getAdminList() {
        return adminList;
    }

    public void setAdminList(List<String> adminList) {
        this.adminList = adminList;
    }
}
