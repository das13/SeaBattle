package seaBattle.xmlservice;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.Socket;

/**
 * <code>OutServerXML</code>
 * provides sending keys/values with XMLStreamWriter
 */

public class InServerXML {
    private XMLStreamReader reader;
    private XMLInputFactory factory;
    private Reader fileReader;
    private Socket socket;

    public InServerXML(Socket socket){
        this.socket = socket;
        this.factory = XMLInputFactory.newInstance();
        try {
            this.fileReader = new InputStreamReader(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
