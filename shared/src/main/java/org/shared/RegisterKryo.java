package org.shared;

import com.esotericsoftware.kryo.Kryo;
import org.shared.enums.GameStep;
import org.shared.enums.Move;
import org.shared.request.InitializeGameRequest;
import org.shared.request.MoveRequest;
import org.shared.request.SigninRequest;
import org.shared.request.SignupRequest;
import org.shared.request.TimeLeftResponse;
import org.shared.response.FailedSignInResponse;
import org.shared.response.GameEndedResponse;
import org.shared.response.MoveResponse;
import org.shared.response.SwitchToGameResponse;
import org.shared.response.SwitchToMenuResponse;

public class RegisterKryo {

    public static void registerClasses(Kryo kryo) {
        kryo.register(FailedSignInResponse.class);
        kryo.register(MoveRequest.class);
        kryo.register(SigninRequest.class);
        kryo.register(SignupRequest.class);
        kryo.register(SwitchToGameResponse.class);
        kryo.register(SwitchToMenuResponse.class);
        kryo.register(InitializeGameRequest.class);
        kryo.register(MoveResponse.class);
        kryo.register(GameEndedResponse.class);
        kryo.register(Move.class);
        kryo.register(GameStep.class);
        kryo.register(TimeLeftResponse.class);
    }

}
