package io.github.greetlib.greet.event

import groovy.util.logging.Log4j2
import io.github.greetlib.greet.event.irc.Event

import java.lang.reflect.Method

@Log4j2
class EventManager {
    final Map<Class<Event>, LinkedHashMap<EventListener, ArrayList<Method>>> methodMap = new LinkedHashMap<>()

    synchronized addListener(EventListener listener) {
        listener.class.getDeclaredMethods().findAll({
            it.getAnnotationsByType(EventHandler.class).length != 0 &&
            it.getParameterTypes().length == 1 &&
            Event.class.isAssignableFrom(it.getParameterTypes()[0])
        }).each {
            registerMethod((Class<Event>)it.parameterTypes[0], it, listener)
        }
    }

    synchronized void removeListener(EventListener listener) {
        listener.class.getDeclaredMethods().findAll({
            it.getAnnotationsByType(EventHandler.class).length != 0 &&
            methodMap.containsKey(it.getParameterTypes()[0]) &&
            methodMap.get(it.getParameterTypes()[0]).containsKey(listener)
        }).each {
            methodMap.get(it.parameterTypes[0]).remove(listener)
            if(methodMap.get(it.parameterTypes[0]).isEmpty()) methodMap.remove(it.parameterTypes[0])
        }
    }

    synchronized private registerMethod(Class<Event> clazz, Method method, EventListener listener) {
        Map<EventListener, ArrayList<Method>> listenerMap = methodMap.getOrDefault clazz, new LinkedHashMap<>()
        ArrayList<Method> methods = listenerMap.getOrDefault listener, new ArrayList<>()
        methods.add method
        listenerMap.put listener, methods
        methodMap.put clazz, listenerMap
        log.debug "Registered event handler ${listener.class}.${method.name} for ${method.getParameterTypes()[0].simpleName}"
    }

    void fireEvent(Event event) {
        log.trace "Firing event ${event.class.simpleName}"
        LinkedHashMap<EventListener, ArrayList<Method>> listenerMap = new LinkedHashMap<>()
        methodMap.each {
            if(it.key.isAssignableFrom(event.class)) {
                it.value.each {
                    ArrayList<Method> methods = listenerMap.get(it.key, new ArrayList())
                    methods.addAll(it.value)
                    listenerMap.put(it.key, methods)
                }
            }
        }
        listenerMap.each {
            for(Method method : it.value) {
                method.invoke(it.key, event)
            }
        }
    }
}
