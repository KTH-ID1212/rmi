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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import se.kth.id1212.rmi.common.ChatClient;
import se.kth.id1212.rmi.common.Credentials;

/**
 * Keeps track of all active participants in the conversation, and is also responsible for sending
 * messages to participants.
 */
public class ParticipantManager {
    private final Random idGenerator = new Random();
    private final Conversation conversation = new Conversation();
    private final Map<Long, Participant> participants = Collections.synchronizedMap(new HashMap<>());

    public long createParticipant(ChatClient remoteNode, Credentials credentials) {
        long participantId = idGenerator.nextLong();
        Participant newParticipant = new Participant(participantId, credentials.getUsername(),
                                                     remoteNode, this);
        participants.put(participantId, newParticipant);
        return participantId;
    }

    /**
     * Sends the entire conversation to the specified participant.
     *
     * @param id The id of the participant that shall receive the conversation.
     */
    public void sendConvToParticipant(long id) {
        Participant participant = participants.get(id);
        for (String entry : conversation.getConversation()) {
            participant.send(entry);
        }
    }

    /**
     * Searches for a participant with the specified id.
     *
     * @param id The id of the searched participant.
     * @return The participant with the specified id, or <code>null</code> if there is no such
     *         participant.
     */
    public Participant findParticipant(long id) {
        return participants.get(id);
    }

    /**
     * Removes the specified participant from the conversation. No more messages will be sent to
     * that participant.
     *
     * @param id The id of the participant that shall be removed.
     */
    public void removeParticipant(long id) {
        participants.remove(id);
    }

    /**
     * Send the specified message to all participants in the conversation.
     *
     * @param msg The message to send.
     */
    void broadcast(String msg) {
        conversation.appendEntry(msg);
        synchronized (participants) {
            for (Participant participant : participants.values()) {
                participant.send(msg);
            }
        }
    }

}
