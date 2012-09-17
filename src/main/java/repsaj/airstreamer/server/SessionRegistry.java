/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package repsaj.airstreamer.server;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import repsaj.airstreamer.server.model.Session;
import repsaj.airstreamer.server.model.Video;

/**
 *
 * @author jasper
 */
public class SessionRegistry {

    private static final Logger LOGGER = Logger.getLogger(SessionRegistry.class);
    private Map<String, Session> sessions = new HashMap<String, Session>();
    private static SessionRegistry INSTANCE = null;

    private SessionRegistry() {
    }

    public static SessionRegistry getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SessionRegistry();
        }
        return INSTANCE;
    }

    public void addSession(Session session) {
        LOGGER.info("addSession " + session.getSessionId());
        sessions.put(session.getSessionId(), session);
    }

    public void removeSession(String id) {
        LOGGER.info("removeSession" + id);
        sessions.remove(id);
    }

    public Session getSession(String id) {
        return sessions.get(id);
    }

    public Collection<Session> getSessions() {
        return sessions.values();
    }
}
