package fr.ph1lou.werewolfplugin.roles.villagers;

import fr.ph1lou.werewolfapi.role.utils.DescriptionBuilder;
import fr.ph1lou.werewolfapi.player.interfaces.IPlayerWW;
import fr.ph1lou.werewolfapi.game.WereWolfAPI;
import fr.ph1lou.werewolfapi.enums.Aura;
import fr.ph1lou.werewolfapi.enums.Prefix;
import fr.ph1lou.werewolfapi.enums.StatePlayer;
import fr.ph1lou.werewolfapi.events.game.life_cycle.AnnouncementDeathEvent;
import fr.ph1lou.werewolfapi.events.game.life_cycle.FirstDeathEvent;
import fr.ph1lou.werewolfapi.role.interfaces.IAffectedPlayers;
import fr.ph1lou.werewolfapi.role.impl.RoleVillage;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Shaman extends RoleVillage implements IAffectedPlayers {

    public Shaman(WereWolfAPI api, IPlayerWW playerWW, String key) {
        super(api, playerWW, key);
    }

    private final List<IPlayerWW> affectedPlayers = new ArrayList<>();

    @Override
    public @NotNull String getDescription() {
        return new DescriptionBuilder(game, this)
                .setDescription(game.translate("werewolf.role.shaman.description"))
                .build();
    }

    @Override
    public void recoverPower() {

    }

    @Override
    public Aura getDefaultAura() {
        return Aura.NEUTRAL;
    }

    @EventHandler
    public void onFirstDeathEvent(FirstDeathEvent event) {

        if (!this.getPlayerWW().isState(StatePlayer.ALIVE)) {
            return;
        }

        if (!isAbilityEnabled()) return;

        IPlayerWW playerWW = event.getPlayerWW();
        int nTimesAffected = (int) affectedPlayers.stream().filter(player -> player.equals(playerWW)).count();

        TextComponent textComponent = new TextComponent(
                game.translate(Prefix.YELLOW.getKey() , "werewolf.role.shaman.choice_message"));
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/ww %s %s %s",
                game.translate("werewolf.role.shaman.command"), playerWW.getUUID(), nTimesAffected)));

        this.getPlayerWW().sendMessage(textComponent);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAnnounceDeath(AnnouncementDeathEvent event) {

        if (!event.getTargetPlayer().equals(getPlayerWW())) {
            return;
        }

        if (!isAbilityEnabled()) return;

        event.setFormat("werewolf.announcement.death_message_with_role");
        event.setRole(event.getPlayerWW().getRole().getKey());
        event.setPlayerName(event.getPlayerWW().getName());
    }

    @Override
    public void addAffectedPlayer(IPlayerWW iPlayerWW) {
        affectedPlayers.add(iPlayerWW);
    }

    @Override
    public void removeAffectedPlayer(IPlayerWW iPlayerWW) {
        affectedPlayers.remove(iPlayerWW);
    }

    @Override
    public void clearAffectedPlayer() {
        affectedPlayers.clear();
    }

    @Override
    public List<? extends IPlayerWW> getAffectedPlayers() {
        return affectedPlayers;
    }
}
