import endpoint.ChatClientEndpoint;
import lombok.extern.log4j.Log4j;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

@Log4j
public class ConsoleChatClient {
    static Scanner scanner = new Scanner(System.in, "utf-8");
    static String roomName;
    static String role;
    static String name;

    public static void main(final String[] args) throws InterruptedException, URISyntaxException {
        System.out.println("to start enter such command [ /create (client or agent) name]");
        one:
        while (true) {
            while (scanner.hasNext()) {
                String command = scanner.nextLine();
                if (command.matches("/create [a-z]{5,6} [a-z]*$")) {
                    role = command.split(" ")[1];
                    name = command.split(" ")[2];
                    roomName = name + "_" + role;
                    log.info(command);
                    break one;
                } else
                    System.out.println("we will be in infinite loop, until you enter correct command");

            }
        }
        System.out.println("connecting to the server " + roomName);

        final ChatClientEndpoint clientEndPoint = new ChatClientEndpoint(
                new URI("ws://localhost:8080/WebS_war/chat/" + roomName));
        log.debug("ChatClientEndpoint created");

        clientEndPoint.addMessageHandler(responseString -> {
            String response = jsonMessageToString(responseString);
            log.info(response + " arrived");
            System.out.println(response);
        });

        while (true) {

            String message = scanner.nextLine();
            if (message.endsWith("/leave")) {
                clientEndPoint.onClose();
                break;
            }
            System.out.println(message);
            clientEndPoint.sendMessage(stringToJsonMessage(roomName, message));
            log.info(roomName + " " + message);
        }
    }


    private static String stringToJsonMessage(final String name, final String message) {
        log.info(name + " " + message + " stringToJsonMessage ");
        return Json.createObjectBuilder().add("name", name).add("text", message).build().toString();
    }

    private static String jsonMessageToString(final String response) {
        log.info(response + " jsonMessageToString");
        JsonObject root = Json.createReader(new StringReader(response)).readObject();
        String message = root.getString("text");
        String sender = root.getString("name");
        String result = sender + "\n" + message;
        return result;
    }

}