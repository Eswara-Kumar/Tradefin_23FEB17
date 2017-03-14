package com.mphasis.tradefin.rpc;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mphasis.tradefin.rpc.ChainConnector;


public class ChainAddresses {
	
	//private static final long serialVersionUID = 1L;
	private static final String COMMAND_GET_ADDRESS_BALANCES = "getaddressbalances";
	private static final String COMMAND_GET_RAW_TRANSACTION = "getrawtransaction";
	private static final String COMMAND_DUMP_PRIV_KEY = "dumpprivkey";
	private static final String COMMAND_LIST_STREAM_ITEMS = "liststreamitems";
	
	public JSONObject getRawTransaction(String strTxId) {
        Object[] params = {strTxId, new Integer(1)};
        ChainConnector objChainConnector = new ChainConnector();
        JSONObject json = objChainConnector.invokeRPC(UUID.randomUUID().toString(), COMMAND_GET_RAW_TRANSACTION, Arrays.asList(params));
        return (JSONObject)json.get("result");
    }
	
	public String dumpPrivKey(String address) {
        Object[] params = {address };
        ChainConnector objChainConnector = new ChainConnector();
        JSONObject json = objChainConnector.invokeRPC(UUID.randomUUID().toString(), COMMAND_DUMP_PRIV_KEY, Arrays.asList(params));
        return (String)json.get("result");
    }
	
	public JSONArray getAddressBalances(String address) {
        String[] params = { address };
        ChainConnector objChainConnector = new ChainConnector();
        JSONObject json = objChainConnector.invokeRPC(UUID.randomUUID().toString(), COMMAND_GET_ADDRESS_BALANCES, Arrays.asList(params));
        return (JSONArray)json.get("result");
        
    }
	
	public JSONArray listStreamItems(String strStreamName) {
        String[] params = { strStreamName };
        ChainConnector objChainConnector = new ChainConnector();
        JSONObject json = objChainConnector.invokeRPC(UUID.randomUUID().toString(), COMMAND_LIST_STREAM_ITEMS, Arrays.asList(params));
        return (JSONArray)json.get("result");
        
    }
	
	public boolean isAssetAvailableAtAddress(String strAddress, String strAssetRef) {
		/*response.setContentType("text/html");//setting the content type  
	    PrintWriter pw=response.getWriter();//get the stream to write the data
		
	    String address= request.getParameter("address");
	    String assetRef= request.getParameter("assetRef");
	  
	    pw.println("<html><body>");  
	    pw.println("Welcome to getaddressbalances servlet");*/
	    
	    
	    System.out.println("### testing rpc on multichain:getaddressbalances");
    	JSONArray objJson1 = getAddressBalances(strAddress);
    	System.out.println("### size of output getaddressbalances:"+objJson1.size());
    	System.out.println("### getaddressbalances:" + objJson1.toJSONString()); //[{"name":"silk","assetref":"12345-123-12345","qty":50}]
    	
    	for(Object obj : objJson1.toArray()) {
    		
    		JSONObject objJson2 = (JSONObject) obj;
        	String strAssetRefOfAddr= (String) objJson2.get("assetref");
        	System.out.println("### size of output assetref:"+strAssetRefOfAddr.length());
        	System.out.println("### assetref:" + strAssetRefOfAddr);
    		
        	if(strAssetRef.equalsIgnoreCase(strAssetRefOfAddr)) {
        		System.out.println("### true");
        		return true;
        	}
        }
    	
    	System.out.println("### false");
    	return false;
    	
    	/*System.out.println("</body></html>");  
    	System.out.close();//closing the stream */
	}
	
	public JSONObject getAssetAvailableAtAddress(String strAddress) {
		/*response.setContentType("text/html");//setting the content type  
	    PrintWriter pw=response.getWriter();//get the stream to write the data
		
	    String address= request.getParameter("address");
	    String assetRef= request.getParameter("assetRef");
	  
	    pw.println("<html><body>");  
	    pw.println("Welcome to getaddressbalances servlet");*/
	    
		JSONObject objJson = null;
	    
	    System.out.println("### testing rpc on multichain:getaddressbalances");
    	JSONArray objJson1 = getAddressBalances(strAddress);
    	System.out.println("### size of output getaddressbalances:"+objJson1.size());
    	System.out.println("### getaddressbalances:" + objJson1.toJSONString()); //[{"name":"silk","assetref":"12345-123-12345","qty":50}]
    	
    	for(Object obj : objJson1.toArray()) {
    		
    		objJson = (JSONObject) obj;
        	
        	System.out.println("### size of output assetref:"+ ((String) objJson.get("assetref")).length());
        	System.out.println("### assetref:" + ((String) objJson.get("assetref")));
        	System.out.println("### asset name:" + ((String) objJson.get("name")));
        	System.out.println("### asset qty:" + ((double) objJson.get("qty")));
        	
        	break;  //return for first object itself. we need to think on it to send only the currency asset (note: can be done using the Asset inserted in BANK_ASSETS table)
        }
    	
    	
    	return objJson;
    	
    	/*System.out.println("</body></html>");  
    	System.out.close();//closing the stream */
	}
	
	public JSONArray getStreamItemsForStream(String strStreamName) {
		/*response.setContentType("text/html");//setting the content type  
	    PrintWriter pw=response.getWriter();//get the stream to write the data
		
	    String address= request.getParameter("address");
	    String assetRef= request.getParameter("assetRef");
	  
	    pw.println("<html><body>");  
	    pw.println("Welcome to getaddressbalances servlet");*/
	    
		System.out.println("### testing rpc on multichain:liststreamitems");
    	JSONArray objJson1 = listStreamItems(strStreamName);
    	System.out.println("### size of output liststreamitems:"+objJson1.size());
    	System.out.println("### liststreamitems:" + objJson1.toJSONString()); //[{"name":"silk","assetref":"12345-123-12345","qty":50}]
    	
    	
    	return objJson1;
    	
    	/*System.out.println("</body></html>");  
    	System.out.close();//closing the stream */
	}

}
