package com.websocket.bitstamp.client;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.PongMessage;
import javax.websocket.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ClientEndpoint
public class WebsocketClientEndpoint {
	// Refer to https://www.oracle.com/technical-resources/articles/java/jsr356.html

	private static Logger LOGGER = LoggerFactory.getLogger(WebsocketClientEndpoint.class);

	private String channel;

	public WebsocketClientEndpoint(String channel) {
		this.channel = channel;
	}

	@OnOpen
	public void onOpen(Session session, EndpointConfig endpointConfig) {
		LOGGER.info("WebSocket session opened for: {}", session.getRequestURI());
		try {
			session.addMessageHandler(new BitstampMessageHandler());
			String pingMessage = "hi";
			LOGGER.info("pingMessage = {}", pingMessage);
			session.getBasicRemote().sendPing(ByteBuffer.wrap(pingMessage.getBytes()));
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	@OnMessage
	public void pongMessage(Session session, PongMessage msg) {
		String pongMsg = new String(msg.getApplicationData().array());
		LOGGER.info("pongMessage = {}", pongMsg);
		try {
			session.getBasicRemote()
					.sendText("{\"event\": \"bts:subscribe\", \"data\": {\"channel\": \"" + this.channel + "\"}}");
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	@OnClose
	public void onClose(Session session, CloseReason reason) {
		LOGGER.info("Closing the WebSocket due to the reason: {}", reason.getReasonPhrase());
	}

	@OnError
	public void onError(Throwable e) {
		LOGGER.error("Error while establishing websocket connection", e);
	}
	
	class BitstampMessageHandler implements MessageHandler.Partial<String> {

		@Override
		public void onMessage(String messagePart, boolean last) {
			LOGGER.info("Message= {}, last= {}", messagePart, last);
		}

	}

}

