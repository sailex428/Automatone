/*
 * This file is part of Baritone.
 *
 * Baritone is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Baritone is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Baritone.  If not, see <https://www.gnu.org/licenses/>.
 */

package automatone.command.defaults;

import automatone.api.IBaritone;
import automatone.api.command.Command;
import automatone.api.command.exception.CommandException;
import automatone.api.command.argument.IArgConsumer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class SaveAllCommand extends Command {

    public SaveAllCommand(IBaritone baritone) {
        super(baritone, "saveall");
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        args.requireMax(0);
        throw new UnsupportedOperationException();
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) {
        return Stream.empty();
    }

    @Override
    public String getShortDesc() {
        return "Saves Baritone's cache for this world";
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList(
                "The saveall command saves Baritone's world cache.",
                "",
                "Usage:",
                "> saveall"
        );
    }
}