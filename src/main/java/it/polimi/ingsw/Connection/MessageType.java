package it.polimi.ingsw.Connection;

/**
 * The messageType that allows to identify immediately the class extension implemented by a message
 *
 * */
public enum MessageType {       //MESSAGE NAME     :  description
    NICK_NAME_INSERTED,         //NickNameMessage  : client sends his unique nickname
    LOBBIES_RESPONSE,           //LobbiesResponseMessage  : server sends list of lobbies
    SHOW_LOBBIES,               //SimpleRequest : client asks for an updated list of the available lobbies
    CREATE_LOBBY,               //CreateLobbyMessage : client creates a new game specifying game mode and players
    JOIN_LOBBY,                 //JoinLobbyMessage : client asks to enter lobby by passing lobbyId
    PLAYER_ID,                  //PlayerIdMessage : server sends the player id to the client
    LOGIN_REPLY,                //LoginResponseMessage : server sends the content of the joined lobby (playersNicknames, max num of players,game mode)
    INIT_TOWER,                 //InitGameMessage : server message that broadcasts the possibility to choose TowerColor
    INIT_TOWER_TEAMS,           //InitGameMessage : server message that broadcasts the possibility to choose TowerColor, indicating the players that have already chosen
    SHOW_TOWERS,                //SimpleRequest : client requests the see the list of Available Towers or another player has set his tower and the other clients will see the list
    TOWER_SETUP,                //TowerColorMessage: client sends his chosen tower color
    INIT_CHARACTER,             //InitGameMessage : server message that broadcasts the possibility to choose Character
    SHOW_CHARACTERS,            //SimpleRequest : client requests to see the list of Available Characters or another player has set his character and the other clients will see the list
    CHARACTER_SETUP,            //CharacterMessage  : client sends his chosen character
    START_GAME,                 //StartGameMessage : server sends the game presentation at the beginning
    PLAY_CARD,                  //PlayCardMessage : client specifies the index of the played card (cloud be character or normal card)
    DECK,                       //UpdatedDeckMessage : server sends back the updated deck to client after a card has been chosen
    MOVE_STUDENT_DINING_ROOM,   //MoveStudentOnDiningRoomMessage : client specifies the student to move in dining room
    BOARDS,                     //UpdatedBoardsMessage : server sends all the players boards
    MOVE_STUDENT_ISLAND,        //MoveStudentOnIslandMessage : client specifies the student to move on island
    ISLANDS,                    //UpdatedIslandsMessage : server sends the islands list
    MOVE_MOTHER_NATURE,         //MoveMotherNatureMessage : client indicated the number of movements for mother nature
    CHOOSE_CLOUD,               //ChooseCloudMessage : client sends the chosen cloud to retriever students from
    CLOUDS,                     //UpdatedCloudsMessage : server sends the updated clouds list
    EXPERT_PARAMETERS,          //UpdatedExpertParams : server sends coins and Character Cards
    ERROR,                      //ErrorMessage : server sends error message
    WIN,                        //EndGameMessage : server notifies the players that have won
    WIN_TEAMS,
    LOSS,                       //EndGameMessage : server notifies the players that have lost
    LOSS_TEAMS,
    EXIT,                       //SimpleRequest : client asks to exit lobby
    DISCONNECTED,               //DisconnectionMessage : server notifies the disconnection of a client from a lobby or match to all the
                                                     //  to players in that game/lobby and also indicates that they have been removed from the game
    GAME_TURN,                  //SimpleMessage : server sends a string to the client updating about the current player's turn
}

// path terminal :
// java -javaagent":/Applications/IntelliJ IDEA.app/Contents/lib/idea_rt.jar=60033:/Applications/IntelliJ IDEA.app/Contents/bin" -Dfile.encoding=UTF-8 -classpath /Users/francescaricter/IdeaProjects/Eriantys/ing-sw-2022-sulpizi-ricter-vucaj/target/classes:/Users/francescaricter/.m2/repository/org/apache/xbean/xbean-reflect/3.4/xbean-reflect-3.4.jar:/Users/francescaricter/.m2/repository/log4j/log4j/1.2.12/log4j-1.2.12.jar:/Users/francescaricter/.m2/repository/commons-logging/commons-logging-api/1.1/commons-logging-api-1.1.jar:/Users/francescaricter/.m2/repository/org/openjfx/javafx-controls/18-ea+6/javafx-controls-18-ea+6.jar:/Users/francescaricter/.m2/repository/org/openjfx/javafx-controls/18-ea+6/javafx-controls-18-ea+6-mac.jar:/Users/francescaricter/.m2/repository/org/openjfx/javafx-graphics/18-ea+6/javafx-graphics-18-ea+6.jar:/Users/francescaricter/.m2/repository/org/openjfx/javafx-graphics/18-ea+6/javafx-graphics-18-ea+6-mac.jar:/Users/francescaricter/.m2/repository/org/openjfx/javafx-base/18-ea+6/javafx-base-18-ea+6.jar:/Users/francescaricter/.m2/repository/org/openjfx/javafx-base/18-ea+6/javafx-base-18-ea+6-mac.jar:/Users/francescaricter/.m2/repository/org/openjfx/javafx-fxml/18-ea+6/javafx-fxml-18-ea+6.jar:/Users/francescaricter/.m2/repository/org/openjfx/javafx-fxml/18-ea+6/javafx-fxml-18-ea+6-mac.jar it.polimi.ingsw.App

