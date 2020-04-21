package client.player.commands;

import constants.CommandConstants;

/**
 *
 * @author wl
 */
public abstract class PlayerCommand implements ICommand {
    public PlayerCommand() {
    }
    
    public static char getPrefix() {
        return CommandConstants.PLAYER_PREFIX;
    }
}
