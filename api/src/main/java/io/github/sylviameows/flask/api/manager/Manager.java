package io.github.sylviameows.flask.api.manager;

import io.github.sylviameows.flask.api.map.FlaskMap;

public interface Manager<T> {
    T add(String key, T entry);
    T get(String key);
    T remove(String key);
}