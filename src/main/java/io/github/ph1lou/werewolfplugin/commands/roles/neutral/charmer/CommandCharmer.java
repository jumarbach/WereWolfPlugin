package io.github.ph1lou.werewolfplugin.commands.roles.neutral.charmer;

import io.github.ph1lou.werewolfapi.Formatter;
import io.github.ph1lou.werewolfapi.ICommand;
import io.github.ph1lou.werewolfapi.IPlayerWW;
import io.github.ph1lou.werewolfapi.WereWolfAPI;
import io.github.ph1lou.werewolfapi.enums.Prefix;
import io.github.ph1lou.werewolfapi.enums.StatePlayer;
import io.github.ph1lou.werewolfapi.events.roles.charmer.CharmerEvent;
import io.github.ph1lou.werewolfapi.rolesattributs.IAffectedPlayers;
import io.github.ph1lou.werewolfapi.rolesattributs.IPower;
import io.github.ph1lou.werewolfapi.rolesattributs.IRole;
import io.github.ph1lou.werewolfplugin.roles.lovers.FakeLoverCharmer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class CommandCharmer implements ICommand {

    @Override
    public void execute(WereWolfAPI game, Player player, String[] args) {

        UUID uuid = player.getUniqueId();
        IPlayerWW playerWW = game.getPlayerWW(uuid).orElse(null);

        if (playerWW == null) return;

        IRole charmer = playerWW.getRole();

        Player playerArg = Bukkit.getPlayer(args[0]);

        if (playerArg == null) {
            playerWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.check.offline_player");
            return;
        }
        UUID argUUID = playerArg.getUniqueId();
        IPlayerWW playerWW1 = game.getPlayerWW(argUUID).orElse(null);

        if (argUUID.equals(uuid)) {
            playerWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.check.not_yourself");
            return;
        }

        if (playerWW1 == null || !playerWW1.isState(StatePlayer.ALIVE)) {
            playerWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.check.player_not_found");
            return;
        }

        ((IPower) charmer).setPower(false);

        CharmerEvent charmerEvent = new CharmerEvent(playerWW, playerWW1);
        Bukkit.getPluginManager().callEvent(charmerEvent);

        if (charmerEvent.isCancelled()) {
            playerWW.sendMessageWithKey(Prefix.RED.getKey() , "werewolf.check.cancel");
            return;
        }

        ((IAffectedPlayers) charmer).clearAffectedPlayer();
        ((IAffectedPlayers) charmer).addAffectedPlayer(playerWW1);

        playerWW.sendMessageWithKey(Prefix.YELLOW.getKey() , "werewolf.role.charmer.perform",
                Formatter.player(playerArg.getName()));

        game.getLoversManager().addLover(new FakeLoverCharmer(game,new ArrayList<>(Arrays.asList(playerWW,playerWW1)),playerWW));
    }
}