package com.mygdx.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.Protocol;
import com.badlogic.gdx.graphics.Color;
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
import java.util.concurrent.CopyOnWriteArrayList;

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
 *  //newasset [host] [port][file size] [file path] [file hash] [message id]
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

    private static final String PLACEHOLDER_FILEPATH = "assets/badlogic.jpg";
    private static ServerSocket serverSocket;
    private static ConcurrentHashMap<Player, Socket> sockets;
    private static ConcurrentHashMap<Socket, PrintWriter> writers;
    private static Queue<Command> commandQueue; //This is the only place in which the networking threads will communicate with the main thread
    private static Queue<Command> pendingQueue;
    private static NetworkManager instance;
    private static boolean amHost;
    private static int fileServerPort;
    private ServerSocketHints serverHints;
    private Socket clientSocket;
    private Socket serverConnectTo;
    private List<String> isDownloading;

    public NetworkManager() {
        sockets = new ConcurrentHashMap<>();
        writers = new ConcurrentHashMap<>();
        commandQueue = new ConcurrentLinkedQueue<>();
        pendingQueue = new ConcurrentLinkedQueue<>();
        isDownloading = new CopyOnWriteArrayList<>();
        amHost = false;
    }

    public static NetworkManager getInstance() {
        if (instance == null) {
            instance = new NetworkManager();
        }
        return instance;
    }

    private static Player getPlayerFromSocket(Socket sock) {
        for (Map.Entry<Player, Socket> e : sockets.entrySet()) {
            if (e.getValue().equals(sock)) return e.getKey();
        }
        return null;
    }

    private static Player getPlayerFromId(String s) {
        for (Player p : getPlayers()) {
            if (s.equals(p.getUserId())) {
                return p;
            }
        }
        return null;
    }

    private static int getFileServerPort() {
        return fileServerPort;
    }

    public static void setFileServerPort(int port) {
        fileServerPort = port;
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

    public static void clearCommandQueue() {
        commandQueue.clear();
    }

    public static Command getNextCommand() {
        if (!commandQueue.isEmpty()) {
            return commandQueue.poll();
        } else if (!pendingQueue.isEmpty()) {
            return pendingQueue.poll();
        } else {
            return null;
        }
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
                NetworkManager.getInstance().getFile(currentCommand);
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
            case FILE_SERVER:
                setFileServerPort(Integer.parseInt(currentCommand.get(0)));
                break;
            case PLAYER:
                break;
            case BEGIN_SYNC:
                break;
            case END_SYNC:
                break;
        }
    }

    private static void changeTexture(Command currentCommand) {
        //changeimage [token id] [new asset name]
        File f = new File(currentCommand.get(1));
        if (f.exists()) {
            TableTopToken t = TableTopToken.getTokenMap().get(currentCommand.get(0));
            if (t != null) {
                t.changeTexture(currentCommand.get(1));
            }
        } else {
            pendingQueue.add(currentCommand);
        }

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

        List<String> args = new LinkedList<>();
        args.add(getFileServerPort() + "");
        Command responseCommand = new Command(Command.CommandType.FILE_SERVER, args, command.getSocket());
        sendCommand(responseCommand);
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
        //Check if the file exists
        //Yes then use it for the image
        //No, use the placeholder image, and place a change image command on the pending stack
        filePath = NetworkManager.getInstance().newFile(currentCommand, filePath, Integer.parseInt(currentCommand.get(6)));
        newToken = new TableTopToken(x, y, filePath, parentMap, layer, EngineManager.getCurrentPlayer(), tokenId);
    }

    public static List<Player> getPlayers() {
        return Collections.list(sockets.keys());
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

    private static void moveAllMap(Command currentCommand) {
    }

    private static void moveToMap(Command currentCommand) {
    }

    private static void newMap(Command currentCommand) {
        //TODO: Map controls
    }

    private static void changeLight(Command currentCommand) {
        //TODO: Change light
        //lightchange [token id] [light type] [light color] [light distance] [angle] [rotation] [active]
        TableTopToken token = TableTopToken.getTokenMap().get(currentCommand.get(0));
        Color lightColor = new Color(Integer.parseInt(currentCommand.get(2)));
        float distance = Float.parseFloat(currentCommand.get(3));
        float angle = Float.parseFloat(currentCommand.get(4));
        float rotation = Float.parseFloat(currentCommand.get(5));
        boolean active = Boolean.parseBoolean(currentCommand.get(6));
        switch (currentCommand.get(1)) {
            case "point":
                token.enableOmniLight(lightColor, distance);
                if (!active) token.disableOmniLight();
                break;
            case "cone":
                token.enableConeLight(lightColor, distance, angle, rotation);
                if (!active) token.disableConeLight();
                break;
        }
    }

    public static boolean isHost() {
        Debug.println("Host status", amHost + "");
        return amHost;
    }

    public void startServer(int port) {
        amHost = true;
        serverHints = new ServerSocketHints();
        serverHints.acceptTimeout = 0;
        startFileServer();
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

    private void startFileServer() {
        Debug.println("File Server", "Starting");
        StartFileServer startFileServer = new StartFileServer();
        startFileServer.start();
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

    private String newFile(Command currentCommand, String filePath, int filesize) {
        //  token [parent map id] [token id] [token X] [token Y] [layer] [image asset name] [file size] [message id]
        //Check to see if we have a file with that name
        File file = new File(filePath);
        if (file.exists()) {
            return filePath;
        } else {
            //Connect to the file server, and send the filepath
            if (!isDownloading.contains(filePath)) {
                String address = getServer().getRemoteAddress();
                address = address.substring(1);
                address = address.split(":")[0];
                Socket downloadSocket = Gdx.net.newClientSocket(Protocol.TCP, address, NetworkManager.getFileServerPort(), null);
                GetFile getFile = new GetFile(downloadSocket, Integer.parseInt(currentCommand.get(6)), currentCommand.get(5));
                getFile.run();
                isDownloading.add(currentCommand.get(5));
            }
            //Create pending change image command
            List<String> args = new LinkedList<>();
            //changeimage [token id] [new asset name]
            args.add(currentCommand.get(1));
            args.add(filePath);
            Command cmd = new Command(Command.CommandType.CHANGE_IMAGE, args, null);
            pendingQueue.add(cmd);
            return PLACEHOLDER_FILEPATH;
        }

    }

    private void getFile(Command currentCommand) {
        //newasset [host] [port][file size] [file path] [file hash] [message id]

        //We want to check if the file exists with that hash TODO
        //If it does, cancel and send a cancel transfer
        //If it doesn't connect to the host and download the file

        Socket downloadSocket = Gdx.net.newClientSocket(Protocol.TCP, currentCommand.get(3), Integer.parseInt(currentCommand.get(4)), null);
        GetFile getFile = new GetFile(downloadSocket, Integer.parseInt(currentCommand.get(2)), currentCommand.get(0));
        getFile.start();

    }

    public Socket getServer() {
        return serverConnectTo;
    }


    /**********************************************************************************************************************************************************************/
    /**********************************************************************************************************************************************************************/
    /**********************************************************************************************************************************************************************/
    /**********************************************************************************************************************************************************************/
    /**********************************************************************************************************************************************************************/


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


    /**
     * This thread listens for commands over the socket
     */
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

    /**
     * This thread acts as a file server.
     */
    private class StartFileServer implements Runnable {
        ServerSocket fileServerSocket;

        @Override
        public void run() {
            SocketHints hints = new SocketHints();
            while (true) {
                Debug.println("File Server", "Waiting for connection");
                Socket playerSocket = fileServerSocket.accept(hints);
                SendFile sendFile = new SendFile(playerSocket);
                Debug.println("File Server", "Got connection, send file");
                sendFile.start();
            }

        }

        public void start() {
            int port = 201;
            while (true) {
                Debug.println("File Server", "Try to bind to port number " + port + "");
                try {
                    fileServerSocket = Gdx.net.newServerSocket(Protocol.TCP, port, serverHints);
                } catch (Exception e) {
                    port++;
                    continue;
                } finally {
                    Debug.println("File Server", "Bound");
                    NetworkManager.setFileServerPort(port);
                    Thread fileServerThread = new Thread(this, "FileServerThread");
                    fileServerThread.start();
                    break;
                }

            }
        }
    }

    /**
     * This thread sends files that are requested over the socket given.
     */
    private class SendFile implements Runnable {

        private Socket sock;
        private String filepath;

        public SendFile(Socket sock) {

            Debug.println("Upload", "Created upload object");
            this.sock = sock;
        }

        private void sendFile() throws IOException {
            try {
                Debug.println("Uploading", "Starting");
                File file = new File(filepath);
                Debug.println("Uploading", "File has size of" + file.length());
                byte[] byteArray = new byte[16 * 1024];

                Debug.println("Uploading", "Make file stream");
                InputStream in = new FileInputStream(file);
                Debug.println("Uploading", "Make socket stream");
                OutputStream out = sock.getOutputStream();
                int count;
                Debug.println("Uploading", "Begin sending");
                while ((count = in.read(byteArray)) > 0) {
                    out.write(byteArray, 0, count);
                    Debug.println("Uploading", "Wrote line");
                }
                in.close();
                out.close();
                Debug.println("Uploading", "Closed streams");
            } finally {
                Debug.println("Uploading", "Finished");
            }
        }


        @Override
        public void run() {
            try {
                getPath();
                if (validatePath()) sendFile();
                else Debug.println("Uploading", "No valid filepath");
            } catch (IOException e) {
                Debug.println("Uploading", "Couldnt Upload");
            }
        }

        private boolean validatePath() {
            return true;
        }

        private void getPath() throws IOException {
            Debug.println("Upload", "Waiting for path");
            BufferedReader reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            filepath = reader.readLine();
            Debug.println("Upload", "Got file path, it is " + filepath);
        }

        public void start() {
            Debug.println("Upload", "Open new thread");
            Thread sendThread = new Thread(this, "SendFileThread" + UUID.randomUUID().toString());
            sendThread.start();
            Debug.println("Upload", "Thread started");
        }
    }


    /**
     * This class runs in a separate thread and downloads a file
     */
    private class GetFile implements Runnable {

        private final Socket socket;
        private final int fileSize;
        private final File file;
        private String path;

        public GetFile(Socket downloadSocket, int fileSize, String filePath) {
            this.socket = downloadSocket;
            this.fileSize = fileSize;
            this.file = new File(filePath);
            try {
                Debug.println("Download", "Try to make new file with name " + filePath);
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.path = filePath;

        }

        private void receiveFile() throws IOException {
            int bytesRead;
            int current;
            FileOutputStream fos = null;
            BufferedOutputStream bos = null;
            try {
                // receive file
                Debug.println("Download", "Starting");
                Debug.println("Download", "Size of file: " + fileSize + " bytes.");
                byte[] byteArray = new byte[16 * 1024];
                InputStream is = socket.getInputStream();
                fos = new FileOutputStream(file);
                bos = new BufferedOutputStream(fos);
                //bytesRead = is.read(byteArray, 0, byteArray.length);
                Debug.println("Download", "Got first bytes");
                current = 0;
                do {
                    bytesRead = is.read(byteArray, current, (byteArray.length - current));
                    Debug.println("Download", "Downloaded bytes");
                    if (bytesRead >= 0) current += bytesRead;
                } while (bytesRead > -1);
                Debug.println("Download", "Download finished");
                bos.write(byteArray, 0, current);
                bos.flush();
            } finally {
                if (fos != null) fos.close();
                if (bos != null) bos.close();
            }
        }


        @Override
        public void run() {
            PrintWriter pn = new PrintWriter(socket.getOutputStream());
            try {
                Debug.println("Download", "Send path");
                pn.println(path);
                pn.flush();
                Debug.println("Download", "Path sent");
                path += "(1)";
                receiveFile();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                pn.close();
            }
        }

        public void start() {
            Thread getThread = new Thread(this, "GetFileThread" + UUID.randomUUID().toString());
            getThread.start();
        }
    }
}
