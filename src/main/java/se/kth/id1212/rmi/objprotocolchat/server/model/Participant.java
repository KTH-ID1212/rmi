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
package se.kth.id1212.rmi.objprotocolchat.server.model;

import se.kth.id1212.rmi.objprotocolchat.common.ChatClient;

/**
 * Represents someone participating in the chat conversation.
 */
public class Participant {
    private static final String DEFAULT_USERNAME = "anonymous";
    private String username;
    private ChatClient remoteNode;

    /**
     * Creates a new instance with the specified username and remote node.
     *
     * @param username   The username of the newly created instance.
     * @param remoteNode The remote endpoint of the newly created instance.
     */
    public Participant(String username, ChatClient remoteNode) {
        this.username = username;
        this.remoteNode = remoteNode;
    }

    /**
     * Creates a new instance with the specified remote node and the default username.
     *
     * @param remoteNode The remote endpoint of the newly created instance.
     */
    public Participant(ChatClient remoteNode) {
        this(DEFAULT_USERNAME, remoteNode);
    }

    /**
     * Send the specified message to the participant's remote node.
     *
     * @param msg The message to send.
     */
    public void send(String msg) {
        remoteNode.recvMsg(msg);
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
    public void setUsername(String username) {
        this.username = username;
    }

}
