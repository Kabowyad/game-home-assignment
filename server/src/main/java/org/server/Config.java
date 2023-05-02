package org.server;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import org.shared.RegisterKryo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {
    @Bean
    public Server server(final Listener customListener) {

        Server server = new Server();
        Kryo kryo = server.getKryo();
        RegisterKryo.registerClasses(kryo);
        server.addListener(customListener);
        return server;
    }

}
