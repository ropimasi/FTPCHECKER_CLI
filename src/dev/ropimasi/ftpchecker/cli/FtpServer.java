package dev.ropimasi.ftpchecker.cli;

public class FtpServer {
	private String name;
	private String host;
	private int port;
	private String user;
	private String password;



	public FtpServer(String name, String host, int port, String user, String password) {
		this.name = name;
		this.host = host;
		this.port = port;
		this.user = user;
		this.password = password;
	}



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public String getHost() {
		return host;
	}



	public void setHost(String host) {
		this.host = host;
	}



	public int getPort() {
		return port;
	}



	public void setPort(int port) {
		this.port = port;
	}



	public String getUser() {
		return user;
	}



	public void setUser(String user) {
		this.user = user;
	}



	public String getPassword() {
		return password;
	}



	public void setPassword(String password) {
		this.password = password;
	}
}