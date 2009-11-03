/**
 * File Description -
 * Date: 03-nov-2006
 */
package mines.net;

import mines.model.Player;

/**
 * EventReceiver - This interface models...
 *
 * @author <a href="mailto:nnombela@gmail.com">Nicolas Nombela</a>
 * @version 1.0
 */
public interface EventReceiver {

    void receiveLoginServer(Player from);

    void receiveLogoutServer(Player from);

    void receiveJoinSession(Player from);

    void receiveCreateSession(Player from);

    void receiveFinishGame(Player from);

    void receiveGameOver(Player from);

    void receivePlayerReady(Player from);

    void receiveCountdownGame(Player from);

    void receiveQuitSession(Player from);

    void receiveRestartBoard(Player from);

    void receiveInitBoard(Player from);

    void receiveTalkMessage(Player from);

    void receiveUncoverCell(Player from);

    void receiveSetFlag(Player from);

    void receiveSessionList(Player from);

    void receivePlayerList(Player from);

    void receiveHeartbeat(Player from);

    void receiveErrorServer(Player from);

    void receiveAbout(Player from);
}
