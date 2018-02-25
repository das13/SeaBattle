package seaBattle.model;

import seaBattle.xmlservice.InServerXML;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.net.Socket;
//все очень плохоо , это надо как то связать с контроллером
public class PlayerThread extends Thread {
    private String login;
    private String password;
    private String ip;
    private int rank;
    private Status status;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private int threadNumber;

    private InServerXML inServerXML;

    public PlayerThread(Socket socket) throws IOException {
        this.socket = socket;
        inServerXML = new InServerXML();
        inServerXML.setXmlif(XMLInputFactory.newInstance());
        inServerXML.setFileReader(new InputStreamReader(socket.getInputStream()));
    }

    public void run() {
        threadNumber = Server.getCountOfThread();
        try {


            while (!socket.isClosed()){
                System.out.println("START LOOP " + threadNumber);
                inServerXML.setXmlr(inServerXML.getXmlif().createXMLStreamReader(inServerXML.getFileReader()));
                XMLStreamReader xmlr = inServerXML.getXmlr();
                while (xmlr.hasNext()) {
                    inServerXML.printEvent(xmlr);
                    if (xmlr.isEndElement() && "root".equals(xmlr.getName().toString())) {
                        break;
                    }else{
                        xmlr.next();
                    }
                }
                System.out.println("FINISH LOOP " + threadNumber);
                xmlr.close();
            }
        } catch (XMLStreamException e) {
            e.printStackTrace();
        } finally {

            if (login != null) {
                Server.getNames().remove(login);
            }
            if (out != null) {
                Server.getWriters().remove(out);
            }
            try {
                socket.close();
            } catch (IOException e) {
            }
        }
    }
}
