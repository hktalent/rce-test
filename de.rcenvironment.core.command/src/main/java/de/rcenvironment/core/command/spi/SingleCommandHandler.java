/*
 * Copyright (C) 2006-2014 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.core.command.spi;

import de.rcenvironment.core.command.common.CommandException;
import de.rcenvironment.core.utils.common.textstream.TextOutputReceiver;

/**
 * A minimal interface for handling a single command.
 * 
 * @author Robert Mischke
 */
public interface SingleCommandHandler {

    /**
     * Synchronously executes a single command.
     * 
     * @param commandContext the {@link CommandContext} containing the list of tokens and a
     *        {@link TextOutputReceiver}
     * @throws CommandException on syntax or execution errors
     */
    void execute(CommandContext commandContext) throws CommandException;
}
