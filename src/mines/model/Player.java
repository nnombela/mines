package mines.model;

import java.net.InetSocketAddress;

/**
 * Player class
 * <p/>
 * This class is used to ...
 *
 * @author <a href="mailto:nnombela@germinus.com">Nicolas Nombela</a>
 * @since 11-jul-2006
 */
public class Player {
    private int id;
    private String username;
    private InetSocketAddress address;
    private int score;
    private int gamesWon;
    private boolean ready = false;
    private GameSession session;
    private long lastEventTime;

    public Player(InetSocketAddress address, String username) {
        this(address.hashCode() + username.hashCode(), address, username);
    }

    public Player(int id, InetSocketAddress address, String username) {
        this.id = id;
        this.address = address;
        this.username = username;
    }

    public void updateLastEventTime() {
        this.lastEventTime = System.currentTimeMillis();
    }

    public long getLastEventTime() {
        return lastEventTime;
    }
    public void setLastEventTime(long lastEventTime) {
        this.lastEventTime = lastEventTime;
    }

    public boolean isSessionCreator() {
        return equals(session.getCreator());
    }

    public boolean isSessionSelf() {
        return equals(session.getMyself());
    }

    public void setSession(GameSession session) {
        this.session = session;
    }

    public void  setUsername(String username) {
        this.username = username;
    }

    public int getId() {
        return this.id;
    }

    public GameSession getSession() {
        return this.session;
    }

    public int getGamesWon() {
        return gamesWon;
    }

    public void incrementGamesWon() {
        ++gamesWon;
    }

    public void setGamesWon(int gamesWon) {
        this.gamesWon = gamesWon;   
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public boolean isReady() {
        return ready;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    public void incrementScore() {
        ++score;
    }

    public void decrementScore() {
        --score;
    }

    public String toString() {
        return username + "@" + address + "(" + id + ")";
    }

    public String getUsername() {
        return username;
    }

    public InetSocketAddress getAddress() {
        return this.address;
    }

    public boolean equals(Object obj) {
        return obj instanceof Player && id == ((Player) obj).id;
    }

    public int hashCode() {
        return id;    
    }

}
