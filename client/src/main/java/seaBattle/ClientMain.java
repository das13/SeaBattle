package seaBattle;

import seaBattle.controller.ClientController;

public class ClientMain {
    public static void main(String[] args) throws Exception {
        ClientController clientController = new ClientController();
        clientController.run();
    }
}
