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
package se.kth.id1212.rmi.objprotocolchat.server.controller;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import se.kth.id1212.rmi.objprotocolchat.common.ChatClient;
import se.kth.id1212.rmi.objprotocolchat.common.ChatServer;
import se.kth.id1212.rmi.objprotocolchat.server.model.Conversation;
import se.kth.id1212.rmi.objprotocolchat.server.model.Participant;

/**
 * The chat server controller, which is also the remote object called by participants on remote
 * nodes. Note that there can only be one chat conversation at a time. All participants participate
 * in that same conversation.
 */
public class Controller extends UnicastRemoteObject implements ChatServer {
    private final Conversation conversation = new Conversation();
    private final List<Participant> participants = new ArrayList<>();
    
    public Controller() throws RemoteException {
    }

    @Override
    public void joinConversation(ChatClient remoteNode) {
        Participant joiningParticipant = new Participant(remoteNode);
        participants.add(joiningParticipant);
        for (String msg : conversation.getConversation()) {
            joiningParticipant.send(msg);
        }
    }

    @Override
    public void broadcastMsg(String msg) {
        for (Participant participant : participants) {
            participant.send(msg);
        }
    }

    @Override
    public void disconnect(ChatClient remoteNode) {
        participants.remove(findParticipantWithRemObj(remoteNode));
    }

    @Override
    public void setUsername(ChatClient remoteNode, String username) throws RemoteException {
        findParticipantWithRemObj(remoteNode).setUsername(username);
    }
    
    private Participant findParticipantWithRemObj(ChatClient remoteNode) {
        for (Participant participant : participants) {
            if (participant.hasRemoteNode(remoteNode)) {
                return participant;
            }
        }
        return null;
    }
}
