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



    public static Bet placeBet(Bet bet, String fundingTx) {
        DiceResponse response = POST("dicebet " + bet.tableName + " " + fundingTx + " " + bet.amount + " " + bet.odds);
        // response: {result, hex}

        String strResponse = sendRawTransaction("sendrawtransaction " + new Gson().fromJson(response.rawResponse, JsonObject.class).get("hex"));
        System.out.println("sendrawtx response." + strResponse);
        // response: txid as string = 2ebe609113c730012a7449e52154222e206f8e9295b6f48e357ad3cf33c8aa44
        // this string is needed in dicestatus call
        bet.betTx = strResponse;
        return bet;
    }

    public static BigDecimal getCurrentFunding(String fundingTx) {
        DiceResponse response = POST("diceinfo " + fundingTx);
        if (response.status == DiceResponse.Status.SUCCESS) {
            return new BigDecimal(new Gson().fromJson(response.rawResponse, JsonObject.class).get("funding").getAsString());
        }
        return new BigDecimal(0);
    }

    public static ArrayList<Table> fetchDiceList() {
        DiceResponse response = POST("dicelist");

        if (response.status == DiceResponse.Status.SUCCESS) {
//            System.out.println(response.rawResponse);
            JsonArray array = response.getRawResponse().getAsJsonArray();
            ArrayList<Table> toReturn = new ArrayList<>();

            for (JsonElement o: array) {
                DiceResponse diceInfoResponse = POST("diceinfo " + o.getAsString());
                if (diceInfoResponse.status == DiceResponse.Status.SUCCESS) {
                    DiceInfo diceInfo = new Gson().fromJson(diceInfoResponse.rawResponse, DiceInfo.class);
                    Table table = new Table(
                            diceInfo.name,
                            diceInfo.fundingtxid,
                            Double.valueOf(diceInfo.minbet),
                            Double.valueOf(diceInfo.maxbet),
                            diceInfo.maxodds,
                            diceInfo.timeoutBlocks
                    );
                    toReturn.add(table);
                }
            }
            return toReturn;
        } else {
            System.out.println("no active dicefunds!");
            return new ArrayList<>();
        }
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
        StringBuilder response = new StringBuilder();
        try {
            BufferedReader in =  new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("komodo-cli -ac_name=KMDICE " + payload).getInputStream()));
            String output;


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
        } catch (JsonSyntaxException jse) {
            return new DiceResponse(DiceResponse.Status.SUCCESS, response.toString());
        }
    }

    private static String sendRawTransaction(String payload) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("komodo-cli -ac_name=KMDICE " + payload).getInputStream()));
            String output;
            StringBuilder response = new StringBuilder();

            while ((output = in.readLine()) != null) {
                response.append("\n").append(output);
            }
            in.close();

            return response.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

        private static class DiceResponse {
        Status status;
        JsonElement rawResponse;
        String rawStringResponse;

        DiceResponse(Status status) {
            this.status = Status.ERROR;
        }

        DiceResponse(Status status, JsonElement rawResponse) {
            this.status = Status.SUCCESS;
            this.rawResponse = rawResponse;
        }

        DiceResponse(Status status, String rawStringResponse) {
            this.status = Status.SUCCESS;
            this.rawStringResponse = rawStringResponse;
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