package client.player.commands;

import constants.CommandConstants;

/**
 *
 * @author wl
 */
public abstract class AdminCommand implements ICommand {
    public AdminCommand() {
    }
    
    public static char getPrefix() {
        return CommandConstants.ADMIN_PREFIX;
    }
}
