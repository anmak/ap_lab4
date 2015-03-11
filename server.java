package lab4;

import java.io.*;
import java.net.*;
import java.util.*;

public class server extends Thread {
	
	

	static final String s = "<html>" +  "<body>";

	static final String e = "</body>" + "</html>";
	static Socket conn;
	Socket client1 = null;
	BufferedReader in = null;
	DataOutputStream out = null;

	public static void main(String args[]) throws Exception 
	{
		
		int port =1205;
		ServerSocket Server = new ServerSocket(port, 10, InetAddress.getByName("127.0.0.1"));
		

		while (true) 
		{
			 conn = Server.accept();
			(new server(conn)).start();
		}
	}
	
	

	public void run()
	{

		try {

			

			in = new BufferedReader(new InputStreamReader(client1.getInputStream()));
			out = new DataOutputStream(client1.getOutputStream());

			String request = in.readLine();
			String header = request;

			StringBuffer response = new StringBuffer();
			response.append("<b>Server Page.... </b><BR> Client request is ....<BR>");
			
			
			StringTokenizer tokenizer = new StringTokenizer(header);
			String method = tokenizer.nextToken();
			String query = tokenizer.nextToken();

			
			

			
			while (in.ready())
			{
				
				response.append(request + "<BR>");
				
				request = in.readLine();
			}

			
			if (method.equals("GET")) 
			{
				if (query.equals("/"))
				{
					int y=200;
					send_client(y, response.toString(), 0);
				} 
				else 
				
				{
					
					String name = query.replaceFirst("/", "");
					name = URLDecoder.decode(name);
					
					if(name.equals("redirect"))
					{
						
						send_client(302, name, 0);
					}
					
					if(name.equals("forbidden"))
					{
						
						send_client(403,"<b> 403 Error ...The Requested is Forbidden ....",0);
					}
					if (new File(name).isFile())
					{
						send_client(200, name, 1);
					}
					else 
					{
						send_client(404,"<b> 404 Error ...The Requested resource not found ....",0);
					}
				}
			}
			
			else if(method.equals("POST"))
			
			{
				
				if (query.equals("/")) {
					int y=200;					
					send_client(y, response.toString(), 0);
				} 
				else 
				{
					
					String name = query.replaceFirst("/", "");
					name = URLDecoder.decode(name);
					if (new File(name).isFile())
					{
						send_client(200, name, 1);
					}
					
					if (new File(name).isFile()) 
					{
						send_client(200, name, 1);
					} else 
					{
						send_client(404,"<b> 404 ERROR ...The Requested resource not found ....",0);
					}
				}
				
				
			}
			else
				send_client(404,"<b>404 ERROR ...The Requested resource not found ....",0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void send_client(int statusCode, String response, int a) throws Exception
	{  
		
		int j=0;

		String statusLine = null;
		String serverdetails = "Server: Java HTTPServer";
		String contentLengthLine = null;
		
		String contentTypeLine = "Content-Type: text/html" + "\r\n";
		FileInputStream fin = null;

		if (statusCode == 200)
			statusLine = "HTTP/1.1 200 OK \r\n";
		else if (statusCode == 302 )
		{
		    
			statusLine = "Found " + "lms.nust.edu.pk"  ;
			
		}
		else if (statusCode == 404 )
			{statusLine = "HTTP/1.1 404 Not Found\r\n" ;}
		
		else if (statusCode == 403 )
		{statusLine = "Forbidden Request \r\n" ;}
		
		String Name = null;
		
		if (a==1) 
		{
			Name = response;
			fin = new FileInputStream(Name);
			contentLengthLine = "Content-Length: "+ Integer.toString(fin.available()) + "\r\n";
			if (!Name.endsWith(".htm") && !Name.endsWith(".html"))
				contentTypeLine = "Content-Type: \r\n";
		}
		else 
		{
			response = server.s + response + server.e;
			contentLengthLine = "Content-Length: " + response.length() + "\r\n";
		}

	out.writeBytes(statusLine);
	out.writeBytes(serverdetails);
		out.writeBytes(contentTypeLine);
out.writeBytes(contentLengthLine);
	out.writeBytes("Connection: close\r\n");
		out.writeBytes("\r\n");

		if (a==1)
			send(fin, out);
		else
			out.writeBytes(response);

		out.close();
		conn.close(); 
	}

	
	
	public void send(FileInputStream fin1, DataOutputStream out)
			throws Exception {
		byte[] buf = new byte[1024];
		int bytesRead;

		while ((bytesRead = fin1.read(buf)) != -1)
{
			out.write(buf, 0, bytesRead);
		}
		fin1.close();
	}
	
	public server(Socket client) {
		client1 = client;
	}

}