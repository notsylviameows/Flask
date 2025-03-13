package io.github.sylviameows.duels.basic.modules;

import io.github.sylviameows.flask.api.game.Lobby;
import io.github.sylviameows.flask.api.game.module.FlaskModule;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;

public class MovementModule extends FlaskModule {

    @Override
    public void init(Lobby<?> lobby) {
        super.init(lobby);

        getOwner().getPlayers().forEach(player -> {
            AttributeInstance walk = player.getAttribute(Attribute.MOVEMENT_SPEED);
            if (walk != null) {
                walk.setBaseValue(0.0);
            }
        });
    }

    @Override
    public void purge() {
        super.purge();

        getOwner().getPlayers().forEach(player -> {
            AttributeInstance walk = player.getAttribute(Attribute.MOVEMENT_SPEED);
            if (walk != null) {
                walk.setBaseValue(walk.getDefaultValue());
            }
        });
    }

}
