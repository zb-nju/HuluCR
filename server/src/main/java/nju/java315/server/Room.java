package nju.java315.server;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import nju.java315.server.msg.GameMsgProtocol;



public class Room implements Runnable{
    static private final Logger LOGGER = LoggerFactory.getLogger(Room.class);
    Player[] players;

    public enum ROOM_STATE{
        ONE_PLAYER(0),TWO_PLAYER(1),PLAYING(2);
        public static final int ONT_PLAYER_VALUE = 0;
        public static final int TWO_PLAYER_VALUE = 1;
        public static final int PLAYING_VALUE = 2;

        private final int value;
        private ROOM_STATE(int value){
            this.value = value;
        }

        public int getValue(){
            return this.value;
        }
    };
    private ROOM_STATE roomState;
    int msgID = -1;
    Queue<GameMsgProtocol.PlayerPutResult.StepInfo> msgQueue = new LinkedList<>();
    private ScheduledFuture<?> future;

    public Room(Player player){
        players = new Player[2];
        players[0] = player;
        roomState = ROOM_STATE.ONE_PLAYER;
    }

    public ROOM_STATE getRoomState(){
        return roomState;
    }

    public Player getEnemy(Player player){
        if(players[0] != player)
            return players[0];
        else
            return players[1];
    }

    public void join(Player player){
        if(players[0] == null)
            players[0] = player;
        else
            players[1] = player;
        roomState = ROOM_STATE.TWO_PLAYER;
    }

    public void leave(Player player){
        if(player == players[0])
            players[0] = null;
        else
            players[1] = null;
        roomState = ROOM_STATE.ONE_PLAYER;
    }

    public void start(ScheduledFuture<?> future){
        this.future = future;
        roomState = ROOM_STATE.PLAYING;
    }

    public void stop(){
        future.cancel(true);
        future = null;
        roomState = ROOM_STATE.TWO_PLAYER;
    }

    public void addMsg(GameMsgProtocol.PlayerPutResult.StepInfo.Builder builder){
        //builder.setMsgID(++msgID);
        msgQueue.add(builder.build());
    }

    @Override
    public void run(){
        GameMsgProtocol.PlayerPutResult.Builder resultBuilder = GameMsgProtocol.PlayerPutResult.newBuilder();

        while(msgQueue.peek() != null)
            resultBuilder.addStepInfo(msgQueue.remove());

        GameMsgProtocol.PlayerPutResult result = resultBuilder.build();
        players[0].getChannel().writeAndFlush(result);
        players[1].getChannel().writeAndFlush(result);
    }
}