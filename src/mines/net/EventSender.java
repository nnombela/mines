/**
 * File Description -
 * Date: 03-nov-2006
 */
package mines.net;

import mines.model.Player;
import mines.model.Cell;
import mines.model.Board;


/**
 * EventSender - This interface models...
 *
 * @author <a href="mailto:nnombela@gmail.com">Nicolas Nombela</a>
 * @version 1.0
 */
public interface EventSender {

    void sendLoginServer(Player to, String username);

    void sendLogoutServer(Player to);

    void sendSessionList(Player to);

    void sendPlayerList(Player to);

    void sendJoinSession(Player to, Player player);

    void sendTalkMessage(Player to, Player from, String message);

    void sendCreateSession(Player to, Player creator, String sessionName, int gameMode, boolean autoFlags);

    void sendQuitSession(Player to, Player player);

    void sendPlayerReady(Player to, Player player);

    void sendInitBoard(Player to, Board board);

    void sendUncoverCell(Player to, Cell cell);

    void sendSetFlag(Player to, Cell cell);

    public void sendGameOver(Player to, Player winner);

    public void sendFinishGame(Player to, Player looser);
    
    void sendCountdownGame(Player to);

    void sendRestartBoard(Player to, Board board);

    void sendError(Player to, String message);

    void sendHeartbeat(Player to);

    void sendAbout(Player to);
}
