package com.mygdx.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.Protocol;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import com.mygdx.containers.Command;
import com.mygdx.game.TableTopMap;
import com.mygdx.game.TableTopToken;
import com.mygdx.tabletop.Player;
import sun.security.ssl.Debug;

import java.io.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
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
    private ServerSocketHints serverHints;
    private Socket clientSocket;

    public NetworkManager() {
        sockets = new ConcurrentHashMap<>();
        writers = new ConcurrentHashMap<>();

    }

    public static NetworkManager getInstance() {
        if (instance == null) {
            instance = new NetworkManager();
        }
        return instance;
    }

    public static void sendCommand(Command command, List<Player> recipients) {
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
                break;
            case LIGHT_CHANGE:
                break;
            case ASSOCIATE:
                break;
            case NEW_ENTRY:
                break;
            case LINK_ENTRY:
                break;
            case NEW_MAP:
                break;
            case MOVE_TO_MAP:
                break;
            case MOVE_ALL_TO_MAP:
                break;
            case CHAT:
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
        //TODO: handle rotation
    }

    private static void sendMessage(Socket socket, Command command) {

    }

    public static List<Player> getPlayers() {
        return Collections.list(sockets.keys());
    }

    public void startServer(int port) {
        commandQueue = new ConcurrentLinkedQueue<>();
        serverHints = new ServerSocketHints();
        serverHints.acceptTimeout = 0;
        serverSocket = Gdx.net.newServerSocket(Protocol.TCP, port, serverHints);
        ListenForPlayers listen = new ListenForPlayers();
        listen.start();

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
        String[] messagecomponents = message.split(" ");
        LinkedList<String> messageList = new LinkedList<>();
        for (String component : messagecomponents) {
            messageList.add(component);
        }
        Command.CommandType type = Command.getType(messageList.removeFirst());
        Command newCommand = new Command(type, messageList, originSocket);
        return newCommand;
    }

    public void connectToServer(String host, int port) {
        clientSocket = Gdx.net.newClientSocket(Protocol.TCP, host, port, null);
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
                NetworkManager.sendMessage(socket, connectMessage);
            }
        }
    }

}
