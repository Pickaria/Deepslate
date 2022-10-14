package fr.pickaria.job;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class JobLevelUpEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private boolean isCancelled = false;

    public final Player player;
    public final LevelUpType type;
    public final JobConfig.Configuration job;
    public final int level;

    JobLevelUpEvent(Player player, LevelUpType type, JobConfig.Configuration job, int level) {
        this.player = player;
        this.type = type;
        this.job = job;
        this.level = level;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }
}
