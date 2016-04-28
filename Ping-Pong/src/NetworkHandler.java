import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class NetworkHandler
{
	
	private Board board;
	private boolean isHost;
	private int connectedPlayers;
	private int aiPlayers;
	private int totalPlayers;
	
	private DatagramSocket skt_out; 	// called by Timer to send state
	private DatagramSocket skt_in;		// to receive state of other players
	
	public  int			  	  myPlayerNo;
	private List<InetAddress> playerAddresses;	// addresses for UDP
	private List<Integer>         playerPorts;		// ports for UDP
	
	private Runnable   updateGameState;
	private long[]     lastReceived; 
	private boolean[]  hostingAI;
	private boolean[] isInGame;
	private Timer timer;					// for detecting drops during game
	private long  startTime;
	private boolean gameStart;
	private Thread updateThread;
				
	public NetworkHandler(Board board, int connectedPlayers, int aiPlayers)
	{	// for host
		this.board  = board;
		this.isHost = true;
		this.connectedPlayers = connectedPlayers;
		this.aiPlayers        = aiPlayers;
		this.totalPlayers     = aiPlayers + connectedPlayers + 1;	// +1 for myself
		this.playerAddresses  = new ArrayList<InetAddress>();
		this.playerPorts      = new ArrayList<Integer>();	
		this.myPlayerNo       = 0;
		this.timer			  = new Timer();
		this.gameStart        = false;
		
		this.board.setNWH(this);
			
		defineUpdateThread();	
		initHost();
	}
	
	public NetworkHandler(Board board, String address, int port)
	{	// for client
		this.board  = board;
		this.isHost = false;		
		this.timer			  = new Timer();
		this.gameStart        = false;
			
		this.board.setNWH(this);
		
		//this.connectedPlayers = connectedPlayers;
		//this.playerAddresses  = new InetAddress[connectedPlayers];
		//this.playerPorts      = new int[connectedPlayers];				
		
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
		updateThread = new Thread(updateGameState);
        //updateThread.start();
		
		// ready to roll => sending start signal to all players and starting my game
		for (int i = 0 ; i<connectedPlayers ; i++)
		{
			try
			{
				byte[] buf = new byte[256];
				DatagramPacket packet = new DatagramPacket(buf, buf.length, playerAddresses.get(i) , playerPorts.get(i));
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
					if (playerAddresses.get(i).equals(cur_addr) && playerPorts.get(i)==cur_port)
						newPlayer = false;
				
				if (newPlayer)
				{
					playerAddresses.add(cur_addr);
					playerPorts.add(cur_port);
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
			String to_send = Integer.toString(i+aiPlayers+1); 	// player number (0 for host, 1 -> if AI, 2... for others)
			to_send       += "," + Integer.toString(connectedPlayers);
			to_send       += "," + Integer.toString(totalPlayers);
			
			for (int j = 0 ; j<connectedPlayers ; j++)
				if (j!=i)
					{
						to_send += "," + playerAddresses.get(j).getHostName();
						to_send += "," + Integer.toString(playerPorts.get(j));
					}		
					
			byte[] buf = new byte[256];
			buf = to_send.getBytes();
			DatagramPacket packet = new DatagramPacket(buf, buf.length, playerAddresses.get(i) , playerPorts.get(i));
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
			skt_in.setSoTimeout(15000);
			
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
		catch(Exception e) {System.out.println("Network Error: Client");}
		
		// received data from other players, filling playerAddresses and playerPorts
		setNumPlayersPNoAddressPorts(ackdata, hostAddress, hostPort);
		System.out.println("got acknowledgement: " + ackdata);
		System.out.println("connected players (excluding me): " + Integer.toString(connectedPlayers));
		// initialising skt_out
		try	{skt_out = new DatagramSocket();}		
		catch(Exception e){System.out.print("Client: could not initialise out_port");}
		
		// starting network update thread
		updateThread = new Thread(updateGameState);
        //updateThread.start();
		
		// informing I'm ready to start
		sendReadySignal();	
		
		// start on receiving start signal
		try
		{
			byte[] buf = new byte[256];
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			skt_in.receive(packet);	
		}
		catch (Exception e) {System.err.println("Client: Couldn't receive start signal");System.exit(0);}
		
		startGame();
	}
	
	private void setNumPlayersPNoAddressPorts(String ackdata, String hostAddress, int hostPort)
	{
		try
		{				
			// parsing for other players
			int i = 0;
			int j = 1;		// to fill for other players
			for (String cur: ackdata.split(","))
			{
				if (i==0) myPlayerNo = Integer.parseInt(cur.trim());
				
				else if (i==1) 
				{	
					connectedPlayers = Integer.parseInt(cur.trim());
					this.playerAddresses  = new ArrayList<InetAddress>();
					this.playerPorts      = new ArrayList<Integer>();
					
					// for host
					playerAddresses.add(InetAddress.getByName(hostAddress));
					playerPorts.add(hostPort);	
				}
				
				else if (i==2)
				{
					totalPlayers = Integer.parseInt(cur.trim());
					aiPlayers    = totalPlayers - connectedPlayers - 1 ;
					board.setNumPlayers(totalPlayers);
					System.out.println("Connected Players : " + connectedPlayers);
					System.out.println("Total Players     : " + totalPlayers);
				}
				
				else if (i%2 == 1) playerAddresses.add(InetAddress.getByName(cur.trim()));
				else
				{
					playerPorts.add(Integer.parseInt(cur.trim()));
					j += 1;
				}
				
				i+=1;				
			}									
		}
		catch (Exception e) {System.out.println(e+"\n");}		
		
		//for (int j = 0 ; j<connectedPlayers ; j++)
		//{
		//	System.out.println(playerAddresses.get(j).getHostName());
		//	System.out.println(playerPorts.get(j));
		//}		
		
	}
	
	private void sendReadySignal()
	{
		// sending ready signal to host
		try
		{
			byte[] rdybuf = new byte[256];
			DatagramPacket rdypkt = new DatagramPacket(rdybuf, rdybuf.length, playerAddresses.get(0) , playerPorts.get(0));
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
			
			DatagramPacket packet = new DatagramPacket(buf, buf.length , playerAddresses.get(i) , playerPorts.get(i));
			
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
				
				// wait for game to start, else receives unnecessary messages!
				//System.out.println("LISTEN THREAD: waiting for game to start");
				//while (!gameStart) {System.out.println("still in loop");}
				//System.out.println("LISTEN THREAD: IGI");
				
				try
				{
					while (board.ingame)		// while game is not over
					{
						byte[] buf = new byte[256];
						DatagramPacket packet = new DatagramPacket(buf, buf.length);						
						
						try {skt_in.receive(packet);}
						catch (SocketTimeoutException e) {System.out.println("haven't received a packet in long :'("); continue;}
						
						String update = new String(packet.getData());
						
						//System.out.println("received a packet");
						//System.out.println("received: " + update);
						// call board update functions
						board.updateStateFromNetwork(update);
						
						updateTimeStamp(update);
						
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
	
	private void updateTimeStamp(String update)
	{
		// assumes , separated packets and the second number is the player number
		String data[] = update.split(",");
		//System.out.println(update);
		int curPlayerNo   = Integer.parseInt(data[1]);	// 0, 1, 2...
		
		lastReceived[curPlayerNo] = System.currentTimeMillis();
		
		//System.out.println(curPlayerNo);
	}
	
    private class DropCheckTimer extends TimerTask {

        @Override
        public void run() {
			long curTime = System.currentTimeMillis();
			// to call currentTimeMillis only once, using lastReceived[j]
			
			boolean startAIs = true;
			boolean[] dropped = new boolean[totalPlayers];
			Arrays.fill(dropped,false);
			//System.out.println("---------------------");
			//System.out.println("curTime : " + curTime);
			
			
			for (int i = 0 ; i < totalPlayers ; i++)
				if (i != myPlayerNo)
				{
					//System.out.println("i : " + i);
					//System.out.println("lastRec[i] : " + lastReceived[i]);
					
					//System.out.println(i + " is alive : " + board.players[board.getPlayerByNetworkID(i)].isAlive());
					if ((curTime - startTime > 5000) && board.players[board.getPlayerByNetworkID(i)].isAlive()  && isInGame[i] && (curTime - lastReceived[i] > 3000) && !hostingAI[i])
					{						
						//isInGame[i] = false;
						System.out.println("Player Dropped : " + Integer.toString(i));
						dropped[i] = true;
					}
					
					else if (i < myPlayerNo) startAIs = false;
						
				}
			//System.out.println("---------------------");	
			if (startAIs)
				for (int i = 0 ; i<totalPlayers ; i++)
					if (dropped[i])
					{
						board.startAIPlayer(i);
						hostingAI[i] = true;
					}			
			
		}
    }
	
	private void startGame()
	{
		System.out.println("starting my game");
		System.out.println("Player No. " + Integer.toString(myPlayerNo));
		timer.scheduleAtFixedRate(new DropCheckTimer(), 1000, 500);				
		startTime = System.currentTimeMillis();
		
		// maintaining last receieved stats
		lastReceived = new long[totalPlayers];
		isInGame     = new boolean[totalPlayers];
		hostingAI    = new boolean[totalPlayers];				
		Arrays.fill(lastReceived,System.currentTimeMillis());
		Arrays.fill(isInGame,true);	
		Arrays.fill(hostingAI,false);
		
		// which players am I hosting?
		if (isHost)
			for (int i = 1 ; i<totalPlayers ; i++)
				if (i <= aiPlayers)
					hostingAI[i] = true;	

		gameStart = true;
		updateThread.start();
		
		int aiPlayersOnBoard = (isHost) ? aiPlayers : 0;
		System.out.println("AIs on board : " + aiPlayersOnBoard);
        board.startGame(aiPlayersOnBoard);
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