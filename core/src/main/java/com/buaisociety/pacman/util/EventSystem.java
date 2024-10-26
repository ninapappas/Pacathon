package com.buaisociety.pacman.util;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents an event system that can be used to listen to events.
 */
public class EventSystem {

    private final Map<Class<? extends Event>, Set<EventListener<?>>> listeners = new ConcurrentHashMap<>();

    /**
     * Adds a listener to be called when the <code>eventClass</code> event is fired.
     *
     * @param eventClass the class of the event to listen to.
     * @param listener the listener to call when the event is fired.
     * @param <T> the type of event to listen to.
     */
    public <T extends Event> void registerListener(@NotNull Class<T> eventClass, @NotNull EventListener<T> listener) {
        listeners.computeIfAbsent(eventClass, k -> ConcurrentHashMap.newKeySet()).add(listener);
    }

    /**
     * Removes the listener, so it will no longer be called when the <code>eventClass</code> event is fired. If the listener
     * was not registered, this method does nothing.
     *
     * @param eventClass the class of the event to stop listening to.
     * @param listener the listener to remove.
     * @param <T> the type of event to stop listening to.
     */
    public <T extends Event> void unregisterListener(@NotNull Class<T> eventClass, @NotNull EventListener<T> listener) {
        Set<EventListener<?>> listeners = this.listeners.get(eventClass);
        if (listeners != null) {
            listeners.remove(listener);
        }
    }

    /**
     * Removes all listeners from the event system.
     */
    public void unregisterAllListeners() {
        this.listeners.clear();
    }

    /**
     * Used internally to fire an event.
     *
     * @param event the event to fire.
     * @return the event that was fired.
     * @param <T> the type of event to fire.
     */
    public <T extends Event> @NotNull T fireEvent(@NotNull T event) {
        Set<EventListener<?>> listeners = this.listeners.get(event.getClass());
        if (listeners == null)
            return event;

        for (EventListener<?> listener : listeners) {
            try {
                //noinspection unchecked
                ((EventListener<T>) listener).onEvent(event);
            } catch (Exception e) {
                System.err.println("An error occurred while calling an event listener: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return event;
    }
}
