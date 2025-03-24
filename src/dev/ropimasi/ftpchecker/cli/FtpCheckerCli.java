package dev.ropimasi.ftpchecker.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;




public class FtpCheckerCli {

	private static List<FtpServer> servers = new ArrayList<>();
	private static final String API_URL = "http://sua-api.com/servidores";
	private static final String USAGE_MSG_ADDSERVER = "Uso: adicionar <nome> <host> <porta> <usuario> <senha>";
	private static final String USAGE_MSG_UPDATESERVER = "Uso: atualizar <nome_atual> <novo_nome> <host> <porta> <usuario> <senha>";
	private static final String USAGE_MSG_DELETESERVER = "Uso: excluir <nome>";



	public static void main(String[] args) {
		clearScreen();
		System.out.println("Bem-vindo ao FtpChecker!");
		displayPrompt();

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
			String command;
			while ((command = reader.readLine()) != null) {
				processCommand(command);
				displayPrompt();
			}
		} catch (IOException e) {
			System.err.println("Erro ao ler entrada: " + e.getMessage());
		}
	}



	private static void processCommand(String command) {
		command = command.trim();
		String[] parts = command.split("\\s+");

		switch (parts[0].toLowerCase()) {
		case "sair":
			System.out.println("Saindo do FtpChecker...");
			System.exit(0);
			break;
		case "ajuda":
			displayHelp();
			break;
		case "limpar":
			clearScreen();
			break;
		case "adicionar":
			addServer(parts);
			break;
		case "listar":
			listServers();
			break;
		case "atualizar":
			updateServer(parts);
			break;
		case "excluir":
			deleteServer(parts);
			break;
		default:
			System.out.println("Comando desconhecido: " + command);
		}
	}



	private static void clearScreen() {
		System.out.print("\033[H\033[2J");
		System.out.flush();
	}



	private static void displayPrompt() {
		System.out.println();
		System.out.print("FtpChecker: ");
	}



	private static void displayHelp() {
		System.out.println("Comandos disponíveis:");
		System.out.println("\tajuda - Exibe esta mensagem de ajuda.");
		System.out.println("\tsair - Sai do FtpChecker.");
		System.out.println("\tlimpar - Limpa a tela.");
		System.out.println("\tadicionar <nome> <host> <porta> <usuario> <senha> - Adiciona um servidor.");
		System.out.println("\tlistar - Lista todos os servidores.");
		System.out.println(
				"\tatualizar <nome_atual> <novo_nome> <host> <porta> <usuario> <senha> - Atualiza um servidor.");
		System.out.println("\texcluir <nome> - Exclui um servidor.");
	}



	private static void addServer(String[] parts) {
		if (parts.length < 6) {
			System.out.println(USAGE_MSG_ADDSERVER);
			return;
		}

		String name = parts[1];
		String host = parts[2];
		int port = Integer.parseInt(parts[3]);
		String user = parts[4];
		String password = parts[5];

		FtpServer server = new FtpServer(name, host, port, user, password);
		sendToServerAPI(server); // Envia o servidor para a API
	}



	private static void sendToServerAPI(FtpServer server) {
		try {
			URL url = new URL(API_URL);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setDoOutput(true);

			Gson gson = new Gson();
			String jsonInputString = gson.toJson(server);

			try (OutputStream outputStream = connection.getOutputStream()) {
				byte[] input = jsonInputString.getBytes("utf-8");
				outputStream.write(input, 0, input.length);
			}

			int responseCode = connection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_CREATED) {
				System.out.println("Servidor adicionado com sucesso na API.");
			} else {
				System.out.println("Falha ao adicionar servidor na API. Código de resposta: " + responseCode);
			}

			connection.disconnect();
		} catch (IOException e) {
			System.err.println("Erro ao enviar servidor para a API: " + e.getMessage());
		}
	}



	private static void listServers() {
		if (servers.isEmpty()) {
			System.out.println("Nenhum servidor cadastrado.");
			return;
		}

		System.out.println("Servidores cadastrados:");
		for (FtpServer server : servers) {
			System.out.println("\t" + server.getName() + " - " + server.getHost() + ":" + server.getPort() + " ("
					+ server.getUser() + ")");
		}
	}



	private static void updateServer(String[] parts) {
		if (parts.length < 7) {
			System.out.println(USAGE_MSG_UPDATESERVER);
			return;
		}

		String currentName = parts[1];
		String newName = parts[2];
		String host = parts[3];
		int port = Integer.parseInt(parts[4]);
		String user = parts[5];
		String password = parts[6];

		for (FtpServer server : servers) {
			if (server.getName().equalsIgnoreCase(currentName)) {
				server.setName(newName);
				server.setHost(host);
				server.setPort(port);
				server.setUser(user);
				server.setPassword(password);
				System.out.println("Servidor '" + currentName + "' atualizado. Novo nome: " + newName + ".");
				return;
			}
		}

		System.out.println("Servidor '" + currentName + "' não encontrado.");
	}



	private static void deleteServer(String[] parts) {
		if (parts.length < 2) {
			System.out.println(USAGE_MSG_DELETESERVER);
			return;
		}

		String name = parts[1];

		for (int i = 0; i < servers.size(); i++) {
			if (servers.get(i).getName().equalsIgnoreCase(name)) {
				servers.remove(i);
				System.out.println("Servidor '" + name + "' excluído.");
				return;
			}
		}

		System.out.println("Servidor '" + name + "' não encontrado.");
	}
}