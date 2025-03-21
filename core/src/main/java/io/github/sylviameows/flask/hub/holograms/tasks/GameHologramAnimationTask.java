package io.github.sylviameows.flask.hub.holograms.tasks;

import org.bukkit.Color;
import org.bukkit.entity.TextDisplay;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.joml.Vector3f;

abstract public class GameHologramAnimationTask extends BukkitRunnable {
    private final Plugin plugin;
    private final TextDisplay display;
    private final int tick;
    private final int end;
    private final float start;

    protected GameHologramAnimationTask(TextDisplay display, int tick, int end, float start, Plugin plugin) {
        this.plugin = plugin;
        this.display = display;
        this.tick = tick;
        this.end = end;
        this.start = start;
    }

    protected void updateHologram(double opacity, double alpha, float change) {
        display.setTextOpacity((byte)(int) opacity);
        display.setBackgroundColor(Color.fromARGB((int) alpha, 0, 0, 0));
        display.setTransformation(updateY(start+change));
    }

    protected Transformation updateY(float y) {
        var current = display.getTransformation();
        var x = current.getTranslation().x();
        var z = current.getTranslation().z();
        return new Transformation(
                new Vector3f(x,y,z),
                current.getLeftRotation(),
                current.getScale(),
                current.getRightRotation()
        );
    }

    protected double ease(double percent) {
        return 1 - Math.pow(1-percent, 4);
    }

    protected Plugin plugin() {
        return plugin;
    }

    protected TextDisplay display() {
        return display;
    }

    protected int tick() {
        return tick;
    }

    protected int end() {
        return end;
    }

    protected float start() {
        return start;
    }
}
