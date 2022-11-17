package it.polimi.ingsw.Connection.Client.ClientMessage;

import it.polimi.ingsw.Connection.Message;
import it.polimi.ingsw.Connection.MessageType;
import it.polimi.ingsw.Model.TowerColor;
import it.polimi.ingsw.Model.Character;

/**
 * Message sent from client : client chooses his tower color
 *
 * */
public class TowerColorMessage extends Message {
    private static final long serialVersionUID = 135251340L;
    private String  towerColor;
    private String nickname;

    /**
     * Constructor
     *
     * @param nickname  client nickname
     * @param towerColor
     * */
    public TowerColorMessage(String nickname, String towerColor){
        super(nickname, MessageType.TOWER_SETUP);
        this.towerColor= towerColor;
    }

    public String getTowerColor() {
        return towerColor;
    }
}
