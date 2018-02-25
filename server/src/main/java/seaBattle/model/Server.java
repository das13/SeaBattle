package seaBattle.model;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.HashSet;

public class Server {
    private static final int PORT = 9001;

    private static HashSet<String> names = new HashSet<String>();

    private static HashSet<PrintWriter> writers = new HashSet<PrintWriter>();

    private static int countOfThread = 0;
    private static File playerList = new File("playerList.xml");
    private static File ipBlackList = new File("ipBlackList.xml");
    private static File serverConf = new File ("serverConf.xml");

    public static int getCountOfThread() {
        return countOfThread;
    }

    public static HashSet<String> getNames() {
        return names;
    }

    public static HashSet<PrintWriter> getWriters() {
        return writers;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("The chat server is running.");
        ServerSocket listener = new ServerSocket(PORT);
        checkServerXMLfiles();

        try {
            while (true) {
                new PlayerThread(listener.accept()).start();
                countOfThread++;
            }
        } finally {
            listener.close();
        }
    }

    public static void checkServerXMLfiles() throws XMLStreamException {
        //проверка наличия playerList.xml и создание при негативном результате
        if (!playerList.exists()) try {
            //не работает трансформ
//            Transformer t = TransformerFactory.newInstance().newTransformer();
//            t.setOutputProperty(OutputKeys.INDENT, "yes");
//            t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            XMLOutputFactory factory = XMLOutputFactory.newFactory();
            XMLStreamWriter writer = factory.createXMLStreamWriter(new FileOutputStream("playerList.xml"));
            writer.writeStartDocument();
            writer.writeStartElement("playerList");
            writer.writeStartElement("player");
            writer.writeAttribute("id" , "1");
            writer.writeStartElement("nickname");
            writer.writeCharacters("admin");
            writer.writeEndElement();
            writer.writeStartElement("password");
            writer.writeCharacters("admin");
            writer.writeEndElement();
            writer.writeStartElement("status");
            writer.writeCharacters("offline");
            writer.writeEndElement();
            writer.writeStartElement("rating");
            writer.writeCharacters("100");
            writer.writeEndElement();
            writer.writeEndElement();
            writer.writeStartElement("player");
            writer.writeAttribute("id" , "2");
            writer.writeStartElement("nickname");
            writer.writeCharacters("hacker");
            writer.writeEndElement();
            writer.writeStartElement("password");
            writer.writeCharacters("lol");
            writer.writeEndElement();
            writer.writeStartElement("status");
            writer.writeCharacters("offline");
            writer.writeEndElement();
            writer.writeStartElement("rating");
            writer.writeCharacters("9999");
            writer.writeEndElement();
            writer.writeEndElement();
            writer.writeStartElement("player");
            writer.writeAttribute("id" , "3");
            writer.writeStartElement("nickname");
            writer.writeCharacters("unnamed");
            writer.writeEndElement();
            writer.writeStartElement("password");
            writer.writeCharacters("test");
            writer.writeEndElement();
            writer.writeStartElement("status");
            writer.writeCharacters("playing");
            writer.writeEndElement();
            writer.writeStartElement("rating");
            writer.writeCharacters("10");
            writer.writeEndElement();
            writer.writeEndElement();
            writer.writeEndElement();
            writer.writeEndDocument();
        } catch (IOException e) {
            //logger.error("Problem with creating \"playerList.xml\".");
        }

        //проверка наличия ipBlackList.xml и создание при негативном результате
        if (!ipBlackList.exists()) try {
            XMLOutputFactory factory = XMLOutputFactory.newFactory();
            XMLStreamWriter writer = factory.createXMLStreamWriter(new FileOutputStream("ipBlackList.xml"));
            writer.writeStartDocument();
            writer.writeStartElement("ipBlackList");
            writer.writeStartElement("ip");
            writer.writeCharacters("250.250.250.250");
            writer.writeEndElement();
            writer.writeEndElement();
            writer.writeEndDocument();
        } catch (IOException e) {
            //logger.error("Problem with creating \"ipBlackList.xml\".");
        }

        //проверка наличия serverConf.xml и создание при негативном результате
        if (!serverConf.exists()) try {
            XMLOutputFactory factory = XMLOutputFactory.newFactory();
            XMLStreamWriter writer = factory.createXMLStreamWriter(new FileOutputStream("serverConf.xml"));
            writer.writeStartDocument();
            writer.writeStartElement("serverConf");
            writer.writeStartElement("ip");
            writer.writeCharacters("localhost");
            writer.writeEndElement();
            writer.writeStartElement("port");
            writer.writeCharacters("2345");
            writer.writeEndElement();
            writer.writeEndElement();
            writer.writeEndDocument();
        } catch (IOException e) {
            //logger.error("Problem with creating \"serverConf.xml\".");
        }
    }

    public static File getPlayerList() {
        return playerList;
    }

    public static void setPlayerList(File playerList) {
        Server.playerList = playerList;
    }

    public static File getIpBlackList() {
        return ipBlackList;
    }

    public static void setIpBlackList(File ipBlackList) {
        Server.ipBlackList = ipBlackList;
    }

    public static File getServerConf() {
        return serverConf;
    }

    public static void setServerConf(File serverConf) {
        Server.serverConf = serverConf;
    }
}
