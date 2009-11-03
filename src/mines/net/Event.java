/**
 * File Description -
 * Date: 04-nov-2006
 */
package mines.net;

/**
 * Event - This interface models...
 *
 * @author <a href="mailto:nnombela@gmail.com">Nicolas Nombela</a>
 * @version 1.0
 */
public interface Event {
    byte UNCOVER_CELL = 1;
    byte SET_FLAG = 2;
    byte PLAYER_LIST = 3;
    byte JOIN_SESSION = 4;
    byte PLAYER_READY = 5;
    byte TALK_MESSAGE = 6;
    byte RESTART_BOARD = 7;
    byte QUIT_SESSION = 8;
    byte FINISH_GAME = 9;
    byte CREATE_SESSION = 10;
    byte INIT_BOARD = 11;
    byte COUNTDOWN_GAME = 12;
    byte LOGIN_SERVER = 13;
    byte SESSION_LIST = 14;
    byte LOGOUT_SERVER = 15;
    byte GAME_OVER = 16;
    byte HEART_BEAT = 17;
    byte ABOUT = 18;

    byte ERROR_SERVER = 20;

    //---------  Error codes
    String NOT_LOGGED_ERROR_CODE = "Sorry, but you are not logged in";
    String SESSION_DOES_NOT_EXITS_ERROR_CODE = "Sorry, session does not exist";
    String CAN_NOT_QUIT_SESSION_ERROR_CODE = "Sorry, but you can quit session for player: ";
    String NOT_READY_ERROR_CODE = "Sorry, game has not started of player not ready: ";
    String GENERAL_ERROR_CODE = "Sorry, not specific error";
}
