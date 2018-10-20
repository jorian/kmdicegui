package util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import model.Bet;
import model.DiceFund;
import model.DiceList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;

public class KomodoRPC {

    public KomodoRPC() {

    }

    public static BigDecimal getCurrentFunding(String fundingTx) {
        DiceResponse response = POST("diceinfo " + fundingTx);
        if (response.status == DiceResponse.Status.SUCCESS) {
            return new BigDecimal(new Gson().fromJson(response.rawResponse, JsonObject.class).get("funding").getAsString());
        }
        return new BigDecimal(0);
    }

    public static ArrayList<DiceFund> fetchDiceList() {
        DiceResponse response = POST("dicelist");

        if (response.status == DiceResponse.Status.SUCCESS) {
//            System.out.println(response.rawResponse);
            JsonArray array = response.getRawResponse().getAsJsonArray();
            ArrayList<DiceFund> toReturn = new ArrayList<>();

            for (JsonElement o: array) {
                DiceResponse diceInfoResponse = POST("diceinfo " + o.getAsString());
                if (diceInfoResponse.status == DiceResponse.Status.SUCCESS) {
                    DiceInfo diceInfo = new Gson().fromJson(diceInfoResponse.rawResponse, DiceInfo.class);
                    DiceFund diceFund = new DiceFund(
                            diceInfo.name,
                            diceInfo.fundingtxid,
                            Double.valueOf(diceInfo.minbet),
                            Double.valueOf(diceInfo.maxbet),
                            diceInfo.maxodds,
                            diceInfo.timeoutBlocks
                    );
                    toReturn.add(diceFund);
                }
            }
            return toReturn;
        } else {
            System.out.println("no active dicefunds!");
            return new ArrayList<>();
        }
    }

    public Bet placeBet(Bet bet) {
        return null;
    }

    public JsonArray getDiceList() {
        DiceResponse response = POST("dicelist");
        if (response.status == DiceResponse.Status.SUCCESS) {
            return response.getRawResponse().getAsJsonArray();
        } else {
            return null;
        }
    }

    private static DiceResponse POST(String payload) {
        try {
            BufferedReader in =  new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("komodo-cli -ac_name=KMDICE " + payload).getInputStream()));
            String output;
            StringBuilder response = new StringBuilder();

            while ((output = in.readLine()) != null) {
                response.append("\n").append(output);
            }

            in.close();

            JsonElement element = new Gson().fromJson(response.toString(), JsonElement.class);
//            System.out.println("response: "+element.toString());
//            System.out.println("array." + element.isJsonArray());
//            System.out.println("object." + element.isJsonObject());

            return new DiceResponse(DiceResponse.Status.SUCCESS, element);
        } catch (IOException e) {
            e.printStackTrace();
            return new DiceResponse(DiceResponse.Status.ERROR);
        } catch (NullPointerException npe) {
            System.out.println("npe, payload:" +payload);
            return new DiceResponse(DiceResponse.Status.ERROR);
        }
    }

    private static class DiceResponse {
        Status status;
        JsonElement rawResponse;

        DiceResponse(Status status) {
            this.status = Status.ERROR;
        }

        DiceResponse(Status status, JsonElement rawResponse) {
            this.status = Status.SUCCESS;
            this.rawResponse = rawResponse;
        }

        JsonElement getRawResponse() {
            return rawResponse;
        }

        private enum Status {
            SUCCESS,
            ERROR
        }
    }

    private class DiceInfo {
        String result;
        String fundingtxid;
        String name;
        String minbet;
        String maxbet;
        int maxodds;
        int timeoutBlocks;
        String funding;
    }
}

// RLQoiCfB9nxuVwj2TULj1MwKAt1SCnstWy