package com.websocket.bitstamp.client;

import java.net.URI;
import java.util.InputMismatchException;
import java.util.Optional;
import java.util.Scanner;

import javax.websocket.ContainerProvider;
import javax.websocket.WebSocketContainer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BitstampWebsocketClientApplication implements CommandLineRunner {

	private static Logger LOGGER = LoggerFactory.getLogger(BitstampWebsocketClientApplication.class);

	private String bitstampURI = "wss://ws.bitstamp.net";

	public static void main(String[] args) {
		SpringApplication.run(BitstampWebsocketClientApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		try (Scanner scanner = new Scanner(System.in)) {
			Optional<String> channelName = getChannelFromUser(scanner);
			if (!channelName.isPresent()) {
				System.out.println("Channel Name could not be mapped for unknown reason");
				System.exit(0);
			}
			System.out.println("Selected Channel Name:" + channelName.get());

			Optional<String> currencyPair = getCurrencyPairFromUser(scanner);
			if (!currencyPair.isPresent()) {
				System.out.println("CurrencyPair could not be mapped for unknown reason");
				System.exit(0);
			}
			System.out.println("Selected CurrencyPair Name:" + currencyPair.get());
			String channel = channelName.get() + "_" + currencyPair.get();
			
			WebSocketContainer container = ContainerProvider.getWebSocketContainer();
			WebsocketClientEndpoint endpoint = new WebsocketClientEndpoint(channel);
			container.connectToServer(endpoint, new URI(bitstampURI));
		} catch (Exception e) {
			LOGGER.error("Exception while connecting to websocket URI, " + bitstampURI, e);
		}
	}

	private Optional<String> getChannelFromUser(Scanner scanner) {
		System.out.println("Select the channel number (type 0 to exit)");

		Channel[] channels = Channel.values();
		for (int i = 0; i < channels.length; i++) {
			System.out.println(i + 1 + ":" + channels[i].name());
		}
		Optional<String> userinput = readUserInput(scanner, "(q|[1-5])");

		if (!userinput.isPresent() || userinput.get().equals("q")) {
			System.exit(0);
		}

		int channel = Integer.parseInt(userinput.get());
		return Optional.of(channels[channel - 1].name());
	}

	private Optional<String> getCurrencyPairFromUser(Scanner scanner) {
		System.out.println("Select the curency pair (type q to exit)");

		CurrencyPair[] currencypairs = CurrencyPair.values();
		for (int i = 0; i < currencypairs.length; i++) {
			System.out.println(i + 1 + ":" + currencypairs[i].name());
		}
		Optional<String> userinput = readUserInput(scanner, "(q|[1-9]|1[0-5])");

		if (!userinput.isPresent() || userinput.get().equals("q")) {
			System.exit(0);
		}
		int currencypair = Integer.parseInt(userinput.get());
		return Optional.of(currencypairs[currencypair - 1].name());
	}

	private Optional<String> readUserInput(Scanner scanner, String regex) {
		try {
			return Optional.of(scanner.next(regex));
		} catch (InputMismatchException e) {
			System.out.println("Invalid input");
		}

		return Optional.empty();

	}

}
