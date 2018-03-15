package seaBattle.xmlservice;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.Socket;

/**
 * <code>OutServerXML</code>
 * provides receive keys/values with XMLStreamReader
 * @author Oleksandr Symonenko
 */

public class InServerXML {
    private XMLStreamReader reader;
    private XMLInputFactory factory;
    private Reader fileReader;
    private Socket socket;

    /**
     * <code>InServerXML</code> initializes for every <code>PlayerController</code> thread
     * to receive XML data from specific Client linked to this <code>PlayerController</code>
     @param socket - specific Client connection socket
     */
    public InServerXML(Socket socket){
        this.socket = socket;
        this.factory = XMLInputFactory.newInstance();
        try {
            this.fileReader = new InputStreamReader(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * The <code>checkValue</code> method returns all text information from value
     * @param reader - XMLStreamReader
     * @return - full String from text inside value
     */
    public static String checkValue(XMLStreamReader reader){
        StringBuilder sb = new StringBuilder();
        String value = "";
        try {
            while (reader.getEventType() != 4){
                reader.next();
            }
            while (reader.getEventType() == 4) {
                sb.append(reader.getText());
                value = sb.toString();
                reader.next();
            }
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
        return value;
    }

    //getters & setters
    public XMLStreamReader getReader() {
        return reader;
    }
    public void setReader(XMLStreamReader reader) {
        this.reader = reader;
    }
    public XMLInputFactory getFactory() {
        return factory;
    }
    public void setFactory(XMLInputFactory factory) {
        this.factory = factory;
    }
    public Reader getFileReader() {
        return fileReader;
    }
    public void setFileReader(Reader fileReader) {
        this.fileReader = fileReader;
    }
}
