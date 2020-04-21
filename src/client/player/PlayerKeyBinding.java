package client.player;

public class PlayerKeyBinding {

    private final int type;
    private final int action;

    public PlayerKeyBinding(int type, int action) {
        super();
        this.type = type;
        this.action = action;
    }

    public int getType() {
        return type;
    }

    public int getAction() {
        return action;
    }
}
