package com.hfad.sportsapp.util;

import com.hfad.sportsapp.R;
import com.hfad.sportsapp.enums.ServerResponse;
import com.hfad.sportsapp.enums.SuccessType;
import com.hfad.sportsapp.utility.massages.MessageOnScreen;

public class ServerResponseToast {

    public static void show(String response, MessageOnScreen messageOnScreen) {
        if(response == null) {
            messageOnScreen.show(R.string.unexpectedError);
        } else {
            for(ServerResponse serverResponse: ServerResponse.values()) {
                if(response.equals(serverResponse.name())) {
                    messageOnScreen.show(serverResponse.getMessage());
                }
            }

            for(SuccessType successType: SuccessType.values()) {
                if(response.equals(successType.name())) {
                    messageOnScreen.show(successType.getMessage());
                }
            }
        }

    }

}
