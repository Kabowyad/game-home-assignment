package org.shared;

import com.esotericsoftware.kryo.Kryo;

public class RegisterKryo {

    public static void registerClasses(Kryo kryo) {
        kryo.register(FailedSignIn.class);
        kryo.register(MoveRequest.class);
        kryo.register(SigninRequest.class);
        kryo.register(SignupRequest.class);
        kryo.register(SwitchToGameResponse.class);
        kryo.register(SwitchToMenuResponse.class);
        kryo.register(SignupResponse.class);
        kryo.register(InitializeGameRequest.class);
    }

}
