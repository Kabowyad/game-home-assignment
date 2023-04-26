package org.server;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class EventListeners implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private Server server;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        server.start();
        try {
            server.bind(54555, 54777);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
