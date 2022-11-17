package it.polimi.ingsw.Controller;

/**
 * ENUM: this class enumeration indicates in which phase of the game the player is set
 *
 * */
public enum PlayerPhase {
    CHOOSE_NICKNAME,
    CHOOSE_TOWER_COLOR,
    CHOOSE_CHARACTER,
    IDLE,                /* the player is waiting for the game to start */
    PLAY_CARD,
    CARD_PLAYED,         /* the player is waiting for other players to play their card */
    WAITING_TURN,        /* the player is waiting for other players to perform their actions in their turn */
    ENTRANCE_MOVE,
    MOTHER_NATURE_MOVE,
    CLOUD_CHOICE,
    WAITING_NEXT_TURN,   /* the player is waiting for a new turn to begin, all players have performed their turn actions --> players will be  moved to
                             the 'PLAY_CARD' */
    ENDGAME
}
