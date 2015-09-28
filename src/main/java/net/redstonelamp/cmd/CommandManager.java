/*
 * This file is part of RedstoneLamp.
 *
 * RedstoneLamp is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * RedstoneLamp is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with RedstoneLamp.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.redstonelamp.cmd;

import lombok.Getter;
import net.redstonelamp.cmd.defaults.HelpCommand;
import net.redstonelamp.cmd.defaults.StopCommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommandManager{
    @Getter
    private CommandExecutor commandExecutor = new CommandExecutor();

    @Getter
    private HashMap<String, String> commands = new HashMap<>();
    @Getter
    private List<CommandListener> listeners = new ArrayList<>();

    public CommandManager(){
        registerDefaultCommands();
    }

    public void registerCommand(String cmd, String description, CommandListener listener){
        commands.put(cmd, description);
        listeners.add(listener);
    }

    private void registerDefaultCommands(){
        registerCommand("help", "View a list of all commands", new HelpCommand());
        registerCommand("stop", "Stops the server", new StopCommand());
    }
}
