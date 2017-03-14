package com.mphasis.tradefin.util;

import java.util.Arrays;
import java.util.UUID;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mphasis.tradefin.rpc.ChainConnector;

public class Utils {
	
	private static final String COMMAND_GET_RAW_TRANSACTION = "getrawtransaction";
	private static final String COMMAND_DUMP_PRIV_KEY = "dumpprivkey";
	
	public static JSONObject getRawTransaction(String address) {
        Object[] params = {address, new Integer(1)};
        ChainConnector objChainConnector = new ChainConnector();
        JSONObject json = objChainConnector.invokeRPC(UUID.randomUUID().toString(), COMMAND_GET_RAW_TRANSACTION, Arrays.asList(params));
        return (JSONObject)json.get("result");
    }
	
	public static String dumpPrivKey(String address) {
        Object[] params = {address };
        ChainConnector objChainConnector = new ChainConnector();
        JSONObject json = objChainConnector.invokeRPC(UUID.randomUUID().toString(), COMMAND_DUMP_PRIV_KEY, Arrays.asList(params));
        return (String)json.get("result");
    }
}
