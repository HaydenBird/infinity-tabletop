package com.mygdx.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.Protocol;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.mygdx.containers.Command;

import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
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
    private static List<Socket> sockets;
    private static Queue<Command> commandQueue; //This is the only place in which the networking threads will communicate with the main thread

    public static void startServer(String host, int port) {
        commandQueue = new ConcurrentLinkedQueue<>();
        ServerSocketHints serverHints = new ServerSocketHints();
        serverSocket = Gdx.net.newServerSocket(Protocol.TCP, host, port, serverHints);

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
        switch (currentCommand.getType()) {
            case RESPONSE:
                break;
            case CONNECT:
                break;
            case CHECK_IN:
                break;
            case TOKEN:
                break;
            case MOVE:
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
                newFile(currentCommand);
                break;
        }
    }

    private static void newFile(Command currentCommand) {
        //Check to see if we have a file with that name
        File file = new File(currentCommand.get(0));
        if (file.exists()) {
            //If yes then
            //Check to see if the hash is the same
            //If Yes, send response saying so and return
            //If no, overwrite
        }
        //If no then continue
        //Receive the file
        try {
            recieveFile(file, currentCommand.getSocket(), Integer.parseInt(currentCommand.get(1)));
        } catch (IOException e) {
            //Sends response
        }

    }


    private static void recieveFile(File file, Socket socket, int fileSize) throws IOException {
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
                    if (sock != null) sock.close();
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


    private class listenForPlayers implements Runnable {
        @Override
        public void run() {
            while (true) {
                //TODO: create new socket to listen to commands on
            }
        }
    }

    private class listenForCommands implements Runnable {
        @Override
        public void run() {
            //TODO: parse command and add it to the queue
        }
    }

}
