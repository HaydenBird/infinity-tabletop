package com.mygdx.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.Protocol;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.mygdx.containers.Command;

import java.io.*;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
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
 * lightChange [token id] [light type] [light color] [light distance]
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
 *
 *
 *
 *
 *
 *
 *
 */
public class NetworkManager {

    private static ServerSocket serverSocket;
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
        //If yes then
        //Check to see if the hash is the same
        //If Yes, send response saying so
        //If no, overwrite
        //If no then continue
        //Receive the file
    }


    private static void recieveFile(String filename, Socket socket, int fileSize) throws IOException {
        File file = new File(filename);
        int bytesRead;
        int current;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        try {
            // receive file
            byte[] bytearray = new byte[fileSize];
            InputStream is = socket.getInputStream();
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bytesRead = is.read(bytearray, 0, bytearray.length);
            current = bytesRead;

            do {
                bytesRead =
                        is.read(bytearray, current, (bytearray.length - current));
                if (bytesRead >= 0) current += bytesRead;
            } while (bytesRead > -1);

            bos.write(bytearray, 0, current);
            bos.flush();

        } finally {
            if (fos != null) fos.close();
            if (bos != null) bos.close();
        }
    }

    private static void sendFile(Socket sock, String filepath) throws IOException {
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        OutputStream os = null;
        try {
            while (true) {

                try {
                    // send file
                    File myFile = new File(filepath);
                    byte[] mybytearray = new byte[(int) myFile.length()];
                    fis = new FileInputStream(myFile);
                    bis = new BufferedInputStream(fis);
                    bis.read(mybytearray, 0, mybytearray.length);
                    os = sock.getOutputStream();
                    os.write(mybytearray, 0, mybytearray.length);
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
