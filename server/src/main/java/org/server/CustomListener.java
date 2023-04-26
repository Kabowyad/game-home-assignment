package org.server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import org.example.Request;
import org.example.Response;
import org.springframework.stereotype.Component;

@Component
public class CustomListener extends Listener {

    @Override
    public void received(Connection connection, Object object) {

        if (object instanceof Request) {

            Request request = (Request)object;
            System.out.println("Request from client: " + request.text);

            Response response = new Response();
            response.text = "Thanks";
            connection.sendTCP(response);
            System.out.println("Sending response from server : " + response.text + "-" + connection.getID());
        }
    }
}
