package seaBattle.model;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import seaBattle.xmlservice.InServerXML;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
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
    private static final String PLAYERLIST = "playerList.xml";
    private String str;


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
                System.out.println("waiting to receive xml data in thread #" + threadNumber);
                //for XML data receiving
                inServerXML.setXmlr(inServerXML.getXmlif().createXMLStreamReader(inServerXML.getFileReader()));
                XMLStreamReader xmlr = inServerXML.getXmlr();
                while (xmlr.hasNext()) {
                    if (xmlr.getEventType() == 1 && xmlr.getLocalName().equals("key")){
                        xmlr.next();
                        switch (xmlr.getText()){
                            case "AUTHORIZATION": {
                                System.out.println("xml message with key \"AUTHORIZATION\" detected");
                                xmlr.next();
                                xmlr.next();
                                xmlr.next();
                                System.out.println("client login = " + xmlr.getText());
                                String login = xmlr.getText();
                                xmlr.next();
                                xmlr.next();
                                xmlr.next();
                                System.out.println("client password = " + xmlr.getText());
                                String password = xmlr.getText();
                                authorizationPhase(login,password);
                                break;
                            }
                            case "REGISTRATION":
                                System.out.println("xml message with key \"REGISTRATION\" detected");
                                break;
                            case "MESSAGE":
                                System.out.println("xml message with key \"MESSAGE\" detected");
                                break;
                            case "ASK OUT":
                                System.out.println("xml message with key \"ASK OUT\" detected");
                                break;
                            case "ASK ANSWER":
                                System.out.println("xml message with key \"ASK ANSWER\" detected");
                                break;
                            case "SHIP LOCATION":
                                System.out.println("xml message with key \"SHIP LOCATION\" detected");
                                break;
                            case "READY":
                                System.out.println("xml message with key \"READY\" detected");
                                break;
                            case "SHOOT":
                                System.out.println("xml message with key \"SHOOT\" detected");
                                break;
                            case "SURRENDER":
                                System.out.println("xml message with key \"SURRENDER\" detected");
                                break;
                            case "GAME OVER":
                                System.out.println("xml message with key \"GAME OVER\" detected");
                        }
                    }


                    //inServerXML.printEvent(xmlr);
                    if (xmlr.isEndElement() && "root".equals(xmlr.getName().toString())) {
                        break;
                    }else{
                        xmlr.next();
                    }
                }
                System.out.println("finished to receive xml data in thread #" + threadNumber);
                xmlr.close();
            }
        } catch (XMLStreamException | SAXException | ParserConfigurationException | IOException | TransformerException e) {
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

    public void authorizationPhase(String login, String password) throws ParserConfigurationException, IOException, SAXException, TransformerException, XMLStreamException {

        // Строим объектную модель исходного XML файла
        final String filepath = System.getProperty("user.dir") + File.separator + PLAYERLIST;
        final File xmlFile = new File(filepath);
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = db.parse(xmlFile);
        doc.normalize();

        // бежим по каждому player в players (playerList.xml)
        NodeList players = doc.getElementsByTagName("player");
        System.out.println("player count in \"players.xml\" = " + players.getLength());
        for (int i = 0; i < players.getLength(); i++) {

            Node node = players.item(i);
            //если указанные имя и пароль не найдены в списке
            if (!node.getChildNodes().item(1).getTextContent().equals(login) && !node.getChildNodes().item(3).getTextContent().equals(password)){
                str = "***player with this login or password was not found. register first***";
                System.out.println("if#1");
            }

            //если совпадает имя или пароль
            if ((node.getChildNodes().item(1).getTextContent().equals(login) && !node.getChildNodes().item(3).getTextContent().equals(password)) ||
                    (!node.getChildNodes().item(1).getTextContent().equals(login) && node.getChildNodes().item(3).getTextContent().equals(password))){
                str = "***account name or password is incorrect***";
                System.out.println("if#2");
                break;
            }
            //если совпадает и имя и пароль
            if (node.getChildNodes().item(1).getTextContent().equals(login) && node.getChildNodes().item(3).getTextContent().equals(password)) {
                // изменение статуса на online
                System.out.println("пытаюсь поменять у " + node.getChildNodes().item(1).getTextContent() + " статус \"" + node.getChildNodes().item(5).getTextContent() + "\" на ОНЛАЙН");

                node.getChildNodes().item(5).setTextContent("online");

                // Записываем изменения в XML файл
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(new File(filepath));
                transformer.transform(source, result);

                System.out.println("THREAD #" + threadNumber + "." + login + " authorized, status changed to \"online\". moving to IDLE mode with chat");
                str = "***YER BOATS IZ READY, CAP'N!***";
                System.out.println("if#3");
                break;
            }
            System.out.println("конец цикла #" + (i+1));
        }
        // Сообщение клиенту о результате авторизации
        System.out.println("\n" + "THREAD #" + threadNumber + ". AUTHORIZATION RESULT\n" + str + "\n");
    }
}
