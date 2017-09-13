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
package se.kth.id1212.rmi.server.controller;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import se.kth.id1212.rmi.common.ChatClient;
import se.kth.id1212.rmi.common.ChatServer;
import se.kth.id1212.rmi.common.Credentials;
import se.kth.id1212.rmi.server.model.ParticipantManager;

/**
 * The chat server controller, which is also the remote object called by participants on remote
 * nodes. Note that there can only be one chat conversation at a time. All participants participate
 * in that same conversation.
 */
public class Controller extends UnicastRemoteObject implements ChatServer {
    private final ParticipantManager participantManager = new ParticipantManager();

    public Controller() throws RemoteException {
    }

    @Override
    public long login(ChatClient remoteNode, Credentials credentials) {
        System.out.println("User " + credentials.getUsername() + " logged in.");
        long participantId = participantManager.createParticipant(remoteNode);
        participantManager.sendConvToParticipant(participantId);
        return participantId;
    }

    @Override
    public void broadcastMsg(long id, String msg) {
        participantManager.findParticipant(id).broadcast(msg);
    }

    @Override
    public void leaveConversation(long id) {
        participantManager.findParticipant(id).leaveConversation();
        participantManager.removeParticipant(id);
    }

    @Override
    public void changeNickname(long id, String username) throws RemoteException {
        participantManager.findParticipant(id).changeUsername(username);
    }
}
