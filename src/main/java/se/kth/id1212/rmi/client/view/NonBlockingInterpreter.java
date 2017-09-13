/*
 * The MIT License
 *
 * Copyright 2017 Leif Lindbäck <leifl@kth.se>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package se.kth.id1212.rmi.client.view;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;
import se.kth.id1212.rmi.common.ChatClient;
import se.kth.id1212.rmi.common.ChatServer;
import se.kth.id1212.rmi.common.Credentials;

/**
 * Reads and interprets user commands. The command interpreter will run in a separate thread, which
 * is started by calling the <code>start</code> method. Commands are executed in a thread pool, a
 * new prompt will be displayed as soon as a command is submitted to the pool, without waiting for
 * command execution to complete.
 */
public class NonBlockingInterpreter implements Runnable {
    private static final String PROMPT = "> ";
    private final Scanner console = new Scanner(System.in);
    private final ThreadSafeStdOut outMgr = new ThreadSafeStdOut();
    private final ChatClient myRemoteObj;
    private ChatServer server;
    private long myIdAtServer;
    private boolean receivingCmds = false;

    public NonBlockingInterpreter() throws RemoteException {
        myRemoteObj = new ConsoleOutput();
    }

    /**
     * Starts the interpreter. The interpreter will be waiting for user input when this method
     * returns. Calling <code>start</code> on an interpreter that is already started has no effect.
     *
     * @param server The server with which this chat client will communicate.
     */
    public void start(ChatServer server) {
        this.server = server;
        if (receivingCmds) {
            return;
        }
        receivingCmds = true;
        new Thread(this).start();
    }

    /**
     * Interprets and performs user commands.
     */
    @Override
    public void run() {
        while (receivingCmds) {
            try {
                CmdLine cmdLine = new CmdLine(readNextLine());
                switch (cmdLine.getCmd()) {
                    case QUIT:
                        receivingCmds = false;
                        server.leaveConversation(myIdAtServer);
                        boolean forceUnexport = false;
                        UnicastRemoteObject.unexportObject(myRemoteObj, forceUnexport);
                        break;
                    case CONNECT:
                        myIdAtServer
                                = server.login(myRemoteObj,
                                                          new Credentials(cmdLine.getParameter(0),
                                                                          cmdLine.getParameter(1)));
                        break;
                    case USER:
                        server.changeNickname(myIdAtServer, cmdLine.getParameter(0));
                        break;
                    default:
                        server.broadcastMsg(myIdAtServer, cmdLine.getUserInput());
                }
            } catch (Exception e) {
                outMgr.println("Operation failed");
                e.printStackTrace();
            }
        }
    }

    private String readNextLine() {
        outMgr.print(PROMPT);
        return console.nextLine();
    }

    private class ConsoleOutput extends UnicastRemoteObject implements ChatClient {

        public ConsoleOutput() throws RemoteException {
        }

        @Override
        public void recvMsg(String msg) {
            outMgr.println((String) msg);
        }
    }
}
