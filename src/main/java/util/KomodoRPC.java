package util;

import com.google.gson.*;
import model.Bet;
import model.Table;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;

public class KomodoRPC {

    public KomodoRPC() {

    }


    // Returns 1 on 1 responses from komodo-cli.
    // Returns error json when it didn't work out.
    public static JsonElement POST(String payload) {
        StringBuilder response = new StringBuilder();
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("komodo-cli -ac_name=KMDICE " + payload).getInputStream()));
            String output;

            while ((output = in.readLine()) != null) {
                response.append("\n").append(output);
            }
            in.close();

            return new Gson().fromJson(response.toString(), JsonElement.class);

            // if not json, make it json:
        } catch (JsonSyntaxException jse) {
            JsonObject object = new JsonObject();
            object.addProperty("response", response.toString());

            return object;
        } catch (NullPointerException npe) {
            System.err.println("npe, payload:" + payload);

            JsonObject object = new JsonObject();
            object.addProperty("error", "Nullpointerexception");
            object.addProperty("payload", payload);

            return object;
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("error while talking to daemon");
            JsonObject object = new JsonObject();
            object.addProperty("error", "IOexception");

            return object;
        }
    }
}

// RLQoiCfB9nxuVwj2TULj1MwKAt1SCnstWy