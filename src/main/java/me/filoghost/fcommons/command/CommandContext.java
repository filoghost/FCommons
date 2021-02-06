/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.command;

import org.bukkit.command.CommandSender;

public class CommandContext {

    private final CommandSender sender;
    private final String rootLabel;
    private final String[] args;

    public CommandContext(CommandSender sender, String rootLabel, String[] args) {
        this.sender = sender;
        this.rootLabel = rootLabel;
        this.args = args;
    }

    public CommandSender getSender() {
        return sender;
    }

    public String getRootLabel() {
        return rootLabel;
    }

    public String[] getArgs() {
        return args;
    }
    
}
