package org.client;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import org.shared.Request;
import org.shared.Response;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        Client client = createClient();
        client.start();
        client.connect(5000, "127.0.0.1", 54555, 54777);

        Request request = new Request();
        request.text = "Here is the request";
        client.sendTCP(request);
        System.out.println("Sended request to server");

        while (true) {

        }

    }

    private static Client createClient() {

        Client client = new Client();
        Kryo kryo = client.getKryo();
        kryo.register(Request.class);
        kryo.register(Response.class);
        client.addListener(createListener());
        return client;
    }

    private static Listener createListener() {
        return new Listener() {
            public void received (Connection connection, Object object) {

                if (object instanceof Response) {
                    Response response = (Response)object;
                    System.out.println("Client received: " + response.text);
                }
            }
        };
    }
}
