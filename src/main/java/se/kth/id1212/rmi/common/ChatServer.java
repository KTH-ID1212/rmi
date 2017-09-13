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
package se.kth.id1212.rmi.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The remote methods of a chat server.
 */
public interface ChatServer extends Remote {
    public static final String SERVER_NAME_IN_REGISTRY = "CHAT_SERVER";

    /**
     * Makes a new participant join the conversation.
     *
     * @param remoteNode The remote endpoint of the joining participant. This is the remote object
     *                   that will be used to send messages to the participant.
     * @param credentials The credentials of the joining participant. 
     * @return The id of the joining participant. A participant must use this id for identification
     *         in all communication with the server.
     */
    long login(ChatClient remoteNode, Credentials credentials) throws RemoteException;

    /**
     * Sets a new username for the participant with the specified remote endpoint.
     *
     * @param id The id of the participant wishing to change username.
     * @param userName   The participant's new username.
     */
    void changeNickname(long id, String username) throws RemoteException;

    /**
     * Broadcasts the specified message to all participants in the conversation.
     *
     * @param id The id of the broadcasting participant.
     * @param msg        The message to broadcast.
     */
    void broadcastMsg(long id, String msg) throws RemoteException;

    /**
     * The specified participant is removed from the conversation, no more messages will be sent to
     * that node.
     *
     * @param id The id of the leaving participant.
     */
    void leaveConversation(long id) throws RemoteException;
}
