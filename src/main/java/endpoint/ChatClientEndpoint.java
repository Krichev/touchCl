package endpoint;

import lombok.extern.log4j.Log4j;

import javax.websocket.*;
import java.net.URI;

@Log4j
@ClientEndpoint
public class ChatClientEndpoint {
    private Session userSession = null;
    private MessageHandler messageHandler;

    public ChatClientEndpoint(final URI endpointURI) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, endpointURI);
            log.debug("connected to server created");

        } catch (Exception e) {
            log.error(e+" while creation Container Provider");
        }
    }

    @OnOpen
    public void onOpen(final Session userSession) {
        log.debug("smbd connected");
        this.userSession = userSession;
    }

    @OnClose
    public void onClose(/*final Session userSession, final CloseReason reason*/) {
        log.debug(userSession.getQueryString() + " disconnected");
        this.userSession = null;
    }

    @OnMessage
    public void onMessage(final String message) {
        if (messageHandler != null) {
            messageHandler.handleMessage(message);
        }
    }

    public void addMessageHandler(final MessageHandler msgHandler) {
        messageHandler = msgHandler;
    }


    public void sendMessage(final String message) {
        userSession.getAsyncRemote().sendText(message);
    }

    public static interface MessageHandler {
        public void handleMessage(String message);
    }
}