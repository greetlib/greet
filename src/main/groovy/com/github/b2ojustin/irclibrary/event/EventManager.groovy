package com.github.b2ojustin.irclibrary.event

import com.github.b2ojustin.irclibrary.event.irc.Event
import groovy.util.logging.Log4j2

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
        Map<EventListener, ArrayList<Method>> listenerMap = methodMap.getOrDefault clazz, new HashMap<>()
        ArrayList<Method> methods = listenerMap.getOrDefault listener, new ArrayList<>()
        methods.add method
        listenerMap.put listener, methods
        methodMap.put clazz, listenerMap
        log.debug "Registered event handler ${listener.class}.${method.name} for ${method.getParameterTypes()[0].simpleName}"
    }

    void fireEvent(Event event) {
        log.trace "Firing event ${event.class.simpleName}"
        def classMap = methodMap.findAll {
            it.key.isAssignableFrom(event.class)
        }
        classMap.each {
            def listenerMap = it.value
            listenerMap.each {
                for(Method method : it.value) {
                    it.key.invokeMethod(method.name, event)
                }
            }
        }

    }
}
