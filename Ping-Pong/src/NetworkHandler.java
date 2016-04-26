import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class NetworkHandler
{
	
	private Board board;
	private boolean isHost;
	private int connectedPlayers;
	
	private DatagramSocket skt_out; 	// called by Timer to send state
	private DatagramSocket skt_in;		// to receive state of other players
	
	public  int			  myPlayerNo;
	private InetAddress[] playerAddresses;	// addresses for UDP
	private int[]	 	  playerPorts;		// ports for UDP
	
	private Runnable   updateGameState;
	private long[]     lastReceived; 
	private boolean[] isInGame;
	private Timer timer;					// for detecting drops during game
	private long  startTime;
				
	public NetworkHandler(Board board, int connectedPlayers)
	{
		this.board  = board;
		this.isHost = true;
		this.connectedPlayers = connectedPlayers;
		this.playerAddresses  = new InetAddress[connectedPlayers];
		this.playerPorts      = new int[connectedPlayers];	
		this.myPlayerNo       = 0;
		this.timer			  = new Timer();
		
		this.board.setNWH(this);
			
		defineUpdateThread();	
		initHost();
	}
	
	public NetworkHandler(Board board, int connectedPlayers, String address, int port)
	{
		this.board  = board;
		this.isHost = false;
		this.connectedPlayers = connectedPlayers;
		this.playerAddresses  = new InetAddress[connectedPlayers];
		this.playerPorts      = new int[connectedPlayers];				
		this.timer			  = new Timer();
		
		this.board.setNWH(this);
		
		defineUpdateThread();
		initClient(address, port);
	}
	
	private void initHost()
	{
		// start with TCP for initialisation
		// receive UDP addresses one by one from all other players
		// send UDP addesses of other players to each
		
		try	{skt_in = new DatagramSocket(1231) ; skt_in.setSoTimeout(10000);}		
		catch(Exception e){System.out.print("Host could not initialise in_port");}
		
		getClients();				// fill playerAddresses and playerPorts
		sendAddressesToClients(); 	// send details of all other players to all players
		
		// waiting for ready signal from clients
		for (int i = 0 ; i<connectedPlayers ; i++)
		{
			byte[] buf = new byte[256];
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			
			try {skt_in.receive(packet);}
			catch (Exception e) { System.err.println("Host: Did not receive all ready signals") ; System.exit(0); }
			
			System.out.println("Received " + Integer.toString(i+1) + " ready signal"); 
		}
		
		// initialising skt_out
		try	{skt_out = new DatagramSocket();}		
		catch(Exception e){System.out.print("Host: Could not initialise out_port");}
		
		// starting network update thread
		Thread updateThread = new Thread(updateGameState);
        updateThread.start();
		
		// ready to roll => sending start signal to all players and starting my game
		for (int i = 0 ; i<connectedPlayers ; i++)
		{
			try
			{
				byte[] buf = new byte[256];
				DatagramPacket packet = new DatagramPacket(buf, buf.length, playerAddresses[i] , playerPorts[i]);
				skt_in.send(packet);	
			}
			catch (Exception e) {System.err.println("Host: Couldn't send start signal");}
		}
		System.out.println("Sent all start signals");
		
		startGame();
	}
	
	private void getClients()
	{
		int curConnected = 0;
		
		try
		{
			while (curConnected != connectedPlayers)
			{	
				boolean newPlayer = true;
				
				byte[] buf = new byte[256];
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				skt_in.receive(packet);
				
				InetAddress cur_addr = packet.getAddress();
				int cur_port		 = packet.getPort();
				
				// check if already there => takes care of multiple connections
				for (int i = 0 ; i<curConnected ; i++)
					if (playerAddresses[i].equals(cur_addr) && playerPorts[i]==cur_port)
						newPlayer = false;
				
				if (newPlayer)
				{
					playerAddresses[curConnected] = cur_addr;
					playerPorts[curConnected]     = cur_port;
					System.out.println("got player " + Integer.toString(curConnected+1));
					curConnected += 1;
				}
			}
		}
		catch(Exception e){System.out.print(e + "\n");}
		
		return;
	}
	
	private void sendAddressesToClients()
	{
		// IP address and Port of host already known => don't send
		// byte array protocol => " your player no. , hostname of next player , port no of next player ... ] => for (total - 2) players (self and host)
		
		// for each player
		for (int i = 0 ; i<connectedPlayers ; i++)
		{
			String to_send = Integer.toString(i+1); 	// player number (0 for host, 1,2... for others)
			
			for (int j = 0 ; j<connectedPlayers ; j++)
				if (j!=i)
					{
						to_send += "," + playerAddresses[j].getHostName();
						to_send += "," + Integer.toString(playerPorts[j]);
					}		
					
			byte[] buf = new byte[256];
			buf = to_send.getBytes();
			DatagramPacket packet = new DatagramPacket(buf, buf.length, playerAddresses[i] , playerPorts[i]);
			try {skt_in.send(packet);}
			catch (Exception e) {System.out.println("Host couldn't send the data for all others to client " + Integer.toString(i+1));}
		}
		
		System.out.println("sent player data to other players");	
	}
	
	private void initClient(String hostAddress, int hostPort)
	{
		// send empty packet to host (to say hi)
		// receive addresses of other players
		boolean acknowledged = false;			// details of all other players are sent with acknowledgement
		String ackdata = "";					// this will contain the data of other players
		
		try
		{
			// preparing an empty packet to send to host
			skt_in = new DatagramSocket();
			skt_in.setSoTimeout(10000);
			
			// empty ('hello') packet that will be sent to the host first
			byte[] buf 			  = new byte[256];
			InetAddress address   = InetAddress.getByName(hostAddress);
			DatagramPacket packet = new DatagramPacket(buf, buf.length,address, hostPort);		
			
			// acknowledgement packet that will be received later from the server
			byte[] ackbuf 		  = new byte[256];	
			DatagramPacket ackpkt = new DatagramPacket(ackbuf, ackbuf.length);
			
			while(!acknowledged)
			{
				// sending first empty packet to host
				try {skt_in.send(packet);}
				catch (Exception e) {System.out.println("Client couldn't send its empty packet");}				
				
				try
				{
					// waiting to receive details of other players from host
					// if timeout, send empty packet above again
					skt_in.receive(ackpkt);
					
					// got it!
					ackdata = new String(ackpkt.getData());
					acknowledged = true;
				}
				catch (SocketTimeoutException e) { System.out.println("Client: waiting for acknowledgement");}
			}	
		}
		catch(Exception e) {System.out.print("Network Error: Client\n");}
		
		// received data from other players, filling playerAddresses and playerPorts
		setPNoAddressPorts(ackdata, hostAddress, hostPort);
		
		// initialising skt_out
		try	{skt_out = new DatagramSocket();}		
		catch(Exception e){System.out.print("Client: could not initialise out_port");}
		
		// starting network update thread
		Thread updateThread = new Thread(updateGameState);
        updateThread.start();
		
		// informing I'm ready to start
		sendReadySignal();	
		
		// start on receiving start signal
		try
		{
			byte[] buf = new byte[256];
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			skt_in.receive(packet);	
		}
		catch (Exception e) {System.err.println("Client: Couldn't receive start signal");}
		
		startGame();
	}
	
	private void setPNoAddressPorts(String ackdata, String hostAddress, int hostPort)
	{
		try
		{
			// for host
			playerAddresses[0] = InetAddress.getByName(hostAddress);
			playerPorts[0]     = hostPort;
			
			// parsing for other players
			int i = 0;
			int j = 1;		// to fill for other players
			for (String cur: ackdata.split(","))
			{
				if (i==0) myPlayerNo = Integer.parseInt(cur.trim());
				else if (i%2 == 1) playerAddresses[j] = InetAddress.getByName(cur.trim());
				else
				{
					playerPorts[j] = Integer.parseInt(cur.trim());
					j += 1;
				}
				
				i+=1;				
			}
		}
		catch (Exception e) {System.out.println(e+"\n");}
		
		//for (int j = 0 ; j<connectedPlayers ; j++)
		//{
		//	System.out.println(playerAddresses[j].getHostName());
		//	System.out.println(playerPorts[j]);
		//}		
		
	}
	
	private void sendReadySignal()
	{
		// sending ready signal to host
		try
		{
			byte[] rdybuf = new byte[256];
			DatagramPacket rdypkt = new DatagramPacket(rdybuf, rdybuf.length, playerAddresses[0] , playerPorts[0]);
			skt_in.send(rdypkt);	
			System.out.println("Sent ready signal");
		}
		catch (Exception e) {System.err.println("I couldn't send ready signal, I suck"); e.printStackTrace(System.out);}		
	}
	
	public void sendStateInfo(String stateData)
	{
		//System.out.println("sending: " + stateData);
		for (int i = 0 ; i < connectedPlayers ; i++)
		{
			byte[] buf = new byte[256];
			buf = stateData.getBytes();
			
			DatagramPacket packet = new DatagramPacket(buf, buf.length , playerAddresses[i] , playerPorts[i]);
			
			try {
				skt_out.send(packet); 
				//System.out.println("sent a packet");
			}
			catch (Exception e) {System.err.println("I couldn't send state info");}
		}
	}
	
	
	private void defineUpdateThread()
	{
		updateGameState = new Runnable()
		{
			@Override
			public void run()
			{	
				// maintaining last receieved stats
				lastReceived = new long[connectedPlayers];
				isInGame     = new boolean[connectedPlayers];				
				Arrays.fill(lastReceived,System.currentTimeMillis());
				Arrays.fill(isInGame,true);			
				timer.scheduleAtFixedRate(new DropCheckTimer(), 1000, 500);				
				
				try
				{
					while (board.ingame)		// change to while game is not over
					{
						byte[] buf = new byte[256];
						DatagramPacket packet = new DatagramPacket(buf, buf.length);
						skt_in.receive(packet);
						String update = new String(packet.getData());
						
						//System.out.println("received a packet");
						//System.out.println("received: " + update);
						// call board update functions
						board.updateStateFromNetwork(update);
						
						updateTimeStamp(packet.getAddress(),packet.getPort());
						
					}
				}
				
				catch (IOException e)
				{
					System.err.println("Error in state update");
					e.printStackTrace();
				}
			}
		};
	}
	
	private void updateTimeStamp(InetAddress address, int port)
	{
		int j = 0;
		for (int i = 0 ; i < connectedPlayers ; i++)
			if (playerAddresses[i]==address && playerPorts[i]==port)
				j = 1;
		
		lastReceived[j] = System.currentTimeMillis();
					
	}
	
    private class DropCheckTimer extends TimerTask {

        @Override
        public void run() {
			long curTime = System.currentTimeMillis();
			// to call currentTimeMillis only once, using lastReceived[j]
			for (int i = 0 ; i < connectedPlayers ; i++)
			{
				int curPlayerNo = (i>=myPlayerNo) ? i+1 : i;
				
				if ((curTime - startTime > 2000) && board.players[curPlayerNo].isAlive() && isInGame[i] && (curTime - lastReceived[i] > 1000))
				{
					isInGame[i] = false;
					System.out.println("Player Dropped : " + Integer.toString(curPlayerNo));
				}
			}
        }
    }
	
	private void startGame()
	{
		System.out.println("starting my game");
		startTime = System.currentTimeMillis();
        board.startGame();
	}
	
	//public static void main(String[] args) 
	//{
	//	if (args[0].equals("0") )
	//	{
	//		System.out.println("host");
	//		NetworkHandler nwh = new NetworkHandler(3);
	//	}
	//	
	//	else 
	//		{NetworkHandler nwh = new NetworkHandler(3,"127.0.0.1",1231);}
	//}
		
}