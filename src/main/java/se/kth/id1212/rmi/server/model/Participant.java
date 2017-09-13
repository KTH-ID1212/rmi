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
package se.kth.id1212.rmi.server.model;

import java.rmi.RemoteException;
import se.kth.id1212.rmi.common.ChatClient;
import se.kth.id1212.rmi.common.MessageException;

/**
 * Represents someone participating in the chat conversation.
 */
public class Participant {
    private static final String JOIN_MESSAGE = " joined conversation.";
    private static final String LEAVE_MESSAGE = " left conversation.";
    private static final String USERNAME_DELIMETER = ": ";
    private static final String DEFAULT_USERNAME = "anonymous";
    private final long id;
    private final ChatClient remoteNode;
    private final ParticipantManager participantMgr;
    private String username;

    /**
     * Creates a new instance with the specified username and remote node.
     *
     * @param id         The unique identifier of this participant.
     * @param username   The username of the newly created instance.
     * @param remoteNode The remote endpoint of the newly created instance.
     * @param mgr        The only existing participant manager.
     */
    public Participant(long id, String username, ChatClient remoteNode, ParticipantManager mgr) {
        this.id = id;
        this.username = username;
        this.remoteNode = remoteNode;
        this.participantMgr = mgr;
    }

    /**
     * Creates a new instance with the specified remote node and the default username.
     *
     * @param id         The unique identifier of this participant.
     * @param remoteNode The remote endpoint of the newly created instance.
     * @param mgr        The only existing participant manager.
     */
    public Participant(long id, ChatClient remoteNode, ParticipantManager mgr) {
        this(id, DEFAULT_USERNAME, remoteNode, mgr);
    }

    /**
     * Send the specified message to the participant's remote node.
     *
     * @param msg The message to send.
     */
    public void send(String msg) {
        try {
            remoteNode.recvMsg(msg);
        } catch (RemoteException re) {
            throw new MessageException("Failed to deliver message to " + username + ".");
        }
    }

    /**
     * Send the specified message to all participants in the conversation, including myself.
     *
     * @param msg The message to send.
     */
    public void broadcast(String msg) {
        participantMgr.broadcast(username + USERNAME_DELIMETER + msg);
    }

    /**
     * Checks if the specified remote node is the remote endpoint of this participant.
     *
     * @param remoteNode The searched remote node.
     * @return <code>true</code> if the specified remote node is the remote endpoint of this
     *         participant, <code>false</code> if it is not.
     */
    public boolean hasRemoteNode(ChatClient remoteNode) {
        return remoteNode.equals(this.remoteNode);
    }

    /**
     * @param username The new username of this participant.
     */
    public void changeUsername(String username) {
        this.username = username;
        broadcast(username + JOIN_MESSAGE);
    }

    /**
     * Inform other participants that this participant is leaving the conversation.
     */
    public void leaveConversation() {
        broadcast(username + LEAVE_MESSAGE);
    }

}
