package com.mygdx.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.Protocol;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import com.mygdx.containers.ChatMessage;
import com.mygdx.containers.Command;
import com.mygdx.containers.DicePool;
import com.mygdx.containers.RollContainer;
import com.mygdx.game.TableTopMap;
import com.mygdx.game.TableTopToken;
import com.mygdx.tabletop.Player;
import sun.security.ssl.Debug;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * This class will handle all the communication between instances of the program
 *
 *
 * Messages that we can send:
 *
 * response [message id] [response code]
 *
 * New asset
 *  newasset [file path] [filelength] [file hash] [message id]
 *
 * Player connect message:
 *  connect [display name] [id] [password hash] [message id]
 *
 *  Check player is still connected
 *  checkin [message id]
 *
 * Token created message:
 *  token [parent map id] [token id] [token X] [token Y] [layer] [image asset name] [id of the image to transfer] [message id]
 *
 * Token moved:
 * move [token id] [new X] [new Y] [new layer] [new width] [new height] [new rotation] [message id]
 *
 * Token image changed:
 * changeimage [token id] [new asset name] [id of the message transfer] [message id]
 *
 * Token light changed:
 * lightchange [token id] [light type] [light color] [light distance]
 *
 * Token owner added
 * addowner [token id] [new player id] [message id]
 *
 * Token owner removed:
 * removeowner [token id] [player id to remove] [message id]
 *
 * Associate token with entry
 * associate [token id] [entry id] [message id]
 *
 * Create entry:
 * newentry [entry id] [entry name] [entry type] [message id]
 *
 * Link entry:
 * linkentry [entry id] [URL] [message id]
 *
 * New Map Created:
 * newmap [map id] [map name] [map width] [map height] [ambient light r] [ambient light g] [ambient light b] [ambient light a] [move players true/false] [message id]
 *
 * Move players to map:
 * movetomap [map id] [player id 1] ... [player id n] [message id]
 * movealltomap [map id] [message id]
 *
 * Chat message sent
 * chat [message 1] [rolls 1] ... [message n] [rolls n] [style] [from] [gm only true/false] [message id]
 *
 */
public class NetworkManager {

    private static ServerSocket serverSocket;
    private static ConcurrentHashMap<Player, Socket> sockets;
    private static ConcurrentHashMap<Socket, PrintWriter> writers;
    private static Queue<Command> commandQueue; //This is the only place in which the networking threads will communicate with the main thread
    private static NetworkManager instance;
    private static boolean amHost;
    private ServerSocketHints serverHints;
    private Socket clientSocket;
    private Socket serverConnectTo;

    public NetworkManager() {
        sockets = new ConcurrentHashMap<>();
        writers = new ConcurrentHashMap<>();
        commandQueue = new ConcurrentLinkedQueue<>();
        amHost = false;
    }

    public static NetworkManager getInstance() {
        if (instance == null) {
            instance = new NetworkManager();
        }
        return instance;
    }

    public static void sendCommand(Command command) {
        Debug.println("Sending message to host", command.toString());
        if (command.getSocket() == null) return;
        if (writers.get(command.getSocket()) == null) {
            writers.put(command.getSocket(), new PrintWriter(command.getSocket().getOutputStream()));
        }
        writers.get(command.getSocket()).println(command.toString());
        writers.get(command.getSocket()).flush();
    }

    public static void sendCommand(Command command, List<Player> recipients) {
        if (!isHost()) { //If we arent the host send to the host
            return;
        }
        for (Player recipient : recipients) {
            Debug.println("Sending message to", recipient.getDisplayName());
            Socket playerSocket = sockets.get(recipient);

            if (writers.get(playerSocket) == null) {
                writers.put(playerSocket, new PrintWriter(playerSocket.getOutputStream()));
            }

            writers.get(playerSocket).println(command.toString());
            writers.get(playerSocket).flush();
        }
    }

    private static void newFile(Command currentCommand, String filePath, int filesize) {
        //Check to see if we have a file with that name
        File file = new File(filePath);
        if (file.exists()) {

            //If yes then
            //Check to see if the hash is the same
            //If Yes, send response saying so and return
            //If no, overwrite
        }
        //If no then continue
        //Receive the file
        return;/*
        try {
            receiveFile(file, currentCommand.getSocket(), filesize);
        } catch (IOException e) {
            //Sends response
        }*/

    }

    private static void receiveFile(File file, Socket socket, int fileSize) throws IOException {
        int bytesRead;
        int current;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        try {
            // receive file
            byte[] byteArray = new byte[fileSize];
            InputStream is = socket.getInputStream();
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bytesRead = is.read(byteArray, 0, byteArray.length);
            current = bytesRead;
            do {
                bytesRead = is.read(byteArray, current, (byteArray.length - current));
                if (bytesRead >= 0) current += bytesRead;
            } while (bytesRead > -1);
            bos.write(byteArray, 0, current);
            bos.flush();
        } finally {
            if (fos != null) fos.close();
            if (bos != null) bos.close();
        }
    }

    public static void clearCommandQueue() {
        //TODO: clear command queue
    }

    public static Command getNextCommand() {
        return commandQueue.poll();
    }

    public static void handleNextCommand() {
        Command currentCommand = getNextCommand();
        if (currentCommand == null) return;
        Debug.println("Handle Command", currentCommand.toString());
        switch (currentCommand.getType()) {
            case RESPONSE:
                break;
            case CONNECT:
                handleNewConnect(currentCommand);
                break;
            case CHECK_IN:
                break;
            case TOKEN:
                newToken(currentCommand);
                break;
            case MOVE:
                moveToken(currentCommand);
                break;
            case CHANGE_IMAGE:
                changeTexture(currentCommand);
                break;
            case LIGHT_CHANGE:
                changeLight(currentCommand);
                break;
            case ASSOCIATE:
                break;
            case NEW_ENTRY:
                //TODO: Entries
                break;
            case LINK_ENTRY:
                break;
            case NEW_MAP:
                newMap(currentCommand);
                break;
            case MOVE_TO_MAP:
                moveToMap(currentCommand);
                break;
            case MOVE_ALL_TO_MAP:
                moveAllMap(currentCommand);
                break;
            case CHAT:
                sendChatMessage(currentCommand);
                break;
            case NEW_FILE:
                break;
            case NEW_ASSET:
                break;
            case ADD_OWNER:
                break;
            case REMOVE_OWNER:
                break;
            case REMOVE_TOKEN:
                break;
            case REMOVE_MAP:
                break;
            case REMOVE_ENTRY:
                break;
            case ERROR:
                break;
        }
    }

    private static void sendChatMessage(Command command) {
        //chat [number of messages] [number of rolls] [message 1] ... [message n] [roll 1] ... [rolls n] [style] [from] [number of recipients] [recipient id 1] .. [recipient id 2] [message id]
        Debug.println("Handle chat", "Start");
        int messageCount = Integer.parseInt(command.get(0));
        Debug.println("Handle chat", "Got msg count");
        int rollCount = Integer.parseInt(command.get(1));
        Debug.println("Handle chat", "Got roll count");
        List<String> messages = new LinkedList<>();
        List<DicePool> rolls = new LinkedList<>();
        int index = 2;
        Debug.println("Handle chat", "Starting to get msgs");
        for (int i = 0; i < messageCount; i++) {
            messages.add(command.get(index++));
            Debug.println("Handle chat", "Got a message");
        }
        Debug.println("Handle chat", "Finished messages, starting rolls");
        for (int i = 0; i < rollCount; i++) {
            rolls.add(DicePool.createFromString(command.get(index++)));
            Debug.println("Handle chat", "Got roll");
        }
        Debug.println("Handle chat", "Finish rolls");
        String style = command.get(index++);
        Debug.println("Handle chat", "Got Style");
        Player from = getPlayerFromId(command.get(index++));
        int recipientCount = Integer.parseInt(command.get(index++));
        Debug.println("Handle chat", "Got recipient count, it is " + recipientCount);
        Debug.println("Handle chat", "Starting to get recipients");
        List<Player> recipients = new LinkedList<>();
        for (int i = 0; i < recipientCount; i++) {
            recipients.add(getPlayerFromId(command.get(index++)));
            Debug.println("Handle chat", "Got recipient number " + i);
        }
        Debug.println("Handle chat", "Got recipients");
        RollContainer rollContainer = new RollContainer(rolls);
        ChatMessage chatMessage = new ChatMessage(messages, from, recipients, rollContainer, EngineManager.getSkin());
        UIManager.addChat(chatMessage);

        //if we are the host, send it to all recipients
        if (isHost()) {
            List<Player> propagateRecipients = new LinkedList<>(recipients);
            propagateRecipients.remove(EngineManager.getCurrentPlayer());
            sendCommand(command, propagateRecipients);
        }

    }

    private static Player getPlayerFromId(String s) {
        for (Player p : getPlayers()) {
            if (s.equals(p.getUserId())) {
                return p;
            }
        }
        return null;
    }

    private static void moveAllMap(Command currentCommand) {
    }

    private static void moveToMap(Command currentCommand) {
    }

    private static void newMap(Command currentCommand) {
        //TODO: Map controls
    }

    private static void changeLight(Command currentCommand) {
        //TODO: Change light
    }

    private static void changeTexture(Command currentCommand) {
        //TODO: Change texture
    }

    private static void handleNewConnect(Command command) {
        //connect [display name] [id] [password hash] [message id]
        //Get the player assigned to the socket
        Socket playerSocket = command.getSocket();
        Player curPlayer = getPlayerFromSocket(playerSocket);
        if (curPlayer == null) return;
        String displayName = command.get(0);
        String id = command.get(1);
        String passwordHash = command.get(2);
        if (checkPassword(id, passwordHash)) {
            curPlayer.setDisplayName(displayName);
            curPlayer.setId(id);
        }
        //Create a new game state for that player
        buildMap(curPlayer, MapManager.getPlayerCurrentMap(curPlayer));
        //Update their chat log
        sendChatLog(curPlayer);
        //Make sure to create all the entries they have access to
        sendEntries(curPlayer);

    }

    private static Player getPlayerFromSocket(Socket sock) {
        for (Map.Entry<Player, Socket> e : sockets.entrySet()) {
            if (e.getValue().equals(sock)) return e.getKey();
        }
        return null;
    }

    private static void sendEntries(Player curPlayer) {
        //TODO: Send the entries
    }

    private static void sendChatLog(Player curPlayer) {
        //TODO: Send the last 100 chat entries
    }

    private static void buildMap(Player curPlayer, TableTopMap map) {
        //TODO: map sending
        //Give the clean
        //Create and put them on the current map
        //Create all the tokens for them that are on the current map
        //Send needed files
        //Create tokens
        //Enable lights
    }

    private static boolean checkPassword(String id, String passwordHash) {
        return true; //TODO: passwords
    }

    //  token [parent map id] [token id] [token X] [token Y] [layer] [image asset name] [file size] [message id]
    // token test test 4 4 2 assets/badlogic.jpg 100 test
    private static void newToken(Command currentCommand) {
        Debug.println("Got message", currentCommand.toString());
        TableTopMap parentMap = MapManager.getCurrentMap();//TableTopMap.getMapMap().get(currentCommand.get(0));
        TableTopToken newToken = TableTopToken.getTokenMap().get(currentCommand.get(1));
        //Check if map or token exists
        if (parentMap == null || newToken != null) {
            Debug.println("Cant create token", "");
            //TODO: Send error message
            return;
        }
        String tokenId = currentCommand.get(1);
        float x = Float.parseFloat(currentCommand.get(2));
        float y = Float.parseFloat(currentCommand.get(3));
        int layer = Integer.parseInt(currentCommand.get(4));
        String filePath = currentCommand.get(5);
        Debug.println("Got Filepath", filePath);
        newFile(currentCommand, filePath, Integer.parseInt(currentCommand.get(6)));
        newToken = new TableTopToken(x, y, filePath, parentMap, layer, EngineManager.getCurrentPlayer(), tokenId);
    }

    private static void moveToken(Command command) {
        TableTopToken token = TableTopToken.getTokenMap().get(command.get(0));
        float x = Float.parseFloat(command.get(1));
        float y = Float.parseFloat(command.get(2));
        int layer = Integer.parseInt(command.get(3));
        float width = Float.parseFloat(command.get(4));
        float height = Float.parseFloat(command.get(5));
        float rotation = Float.parseFloat(command.get(6));
        token.setPosition(x, y);
        token.setSize(width, height);
        token.setLayer(layer);
        token.updateLightPositions();
        //TODO: handle rotation

        //Now if you are the host, send the message to all players, the senders token is already in place but that's ok
        if (isHost()) sendCommand(command, getPlayers());
    }

    private static void sendMessage(Socket socket, Command command) {

    }

    public static List<Player> getPlayers() {
        return Collections.list(sockets.keys());
    }

    public static boolean isHost() {
        Debug.println("Host status", amHost + "");
        return amHost;
    }

    public Socket getServer() {
        return serverConnectTo;
    }

    public void startServer(int port) {
        amHost = true;
        serverHints = new ServerSocketHints();
        serverHints.acceptTimeout = 0;
        while (true) {
            Debug.println("Try to bind to port number", port + "");
            try {
                serverSocket = Gdx.net.newServerSocket(Protocol.TCP, port, serverHints);
            } catch (Exception e) {
                port++;
                continue;
            } finally {
                ListenForPlayers listen = new ListenForPlayers();
                listen.start();
                break;
            }

        }
    }

    private static void sendFile(Socket sock, String filepath) throws IOException {
        FileInputStream fis;
        BufferedInputStream bis = null;
        OutputStream os = null;
        try {
            while (true) {

                try {
                    // send file
                    File myFile = new File(filepath);
                    byte[] byteArray = new byte[(int) myFile.length()];
                    fis = new FileInputStream(myFile);
                    bis = new BufferedInputStream(fis);
                    bis.read(byteArray, 0, byteArray.length);
                    os = sock.getOutputStream();
                    os.write(byteArray, 0, byteArray.length);
                    os.flush();
                } finally {
                    if (bis != null) bis.close();
                    if (os != null) os.close();
                }
            }
        } finally {

        }
    }

    private Command parseMessage(String message, Socket originSocket) {
        String[] messagecomponents = message.split("‗");
        LinkedList<String> messageList = new LinkedList<>();
        for (String component : messagecomponents) {
            messageList.add(component);
        }
        Command.CommandType type = Command.getType(messageList.removeFirst());
        Command newCommand = new Command(type, messageList, originSocket);
        return newCommand;
    }

    public void connectToServer(String host, int port) {
        amHost = false;
        clientSocket = Gdx.net.newClientSocket(Protocol.TCP, host, port, null);
        serverConnectTo = clientSocket;
        ListenForCommand commandListener = new ListenForCommand(clientSocket);
        commandListener.start(true);
    }

    private class ListenForPlayers implements Runnable {
        @Override
        public void run() {
            SocketHints hints = new SocketHints();
            while (true) {
                Debug.println("Server", "Waiting for connection");
                Socket playerSocket = serverSocket.accept(hints);
                sockets.put(new Player("New name", "1feop3j3"), playerSocket);
                ListenForCommand newCommandListener = new ListenForCommand(playerSocket);
                newCommandListener.start(false);
            }

        }

        public void start() {
            Thread listeningThread = new Thread(this, "ListeningThread");
            listeningThread.start();
        }
    }

    private class ListenForCommand implements Runnable {
        private Socket socket;

        public ListenForCommand(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (true) {
                String message = "";
                Debug.println("Command", "Listening for message");
                try {
                    message = reader.readLine();
                    Command newCommand = parseMessage(message, socket);
                    commandQueue.add(newCommand);
                } catch (IOException e) {
                    //TODO: send error response
                    e.printStackTrace();
                }

            }
        }


        public void start(boolean b) {
            Thread commandListener = new Thread(this, "CommandThread" + Math.random());
            commandListener.start();
            if (b) {
                List<String> arguments = new LinkedList<>();
                arguments.add(EngineManager.getCurrentPlayer().getDisplayName());
                arguments.add(EngineManager.getCurrentPlayer().getUserId());
                arguments.add(EngineManager.getCurrentPlayer().promptPassword());
                Command connectMessage = new Command(Command.CommandType.CONNECT, arguments, socket);
                sendCommand(connectMessage);
            }
        }
    }

}
