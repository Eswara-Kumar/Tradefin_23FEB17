package com.mphasis.tradefin.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.mphasis.tradefin.beans.Assets;
import com.mphasis.tradefin.dao.TradeActionsDAO;
import com.mphasis.tradefin.rpc.ChainConnector;

/**
 * Servlet implementation class SendAssetFrom
 */
/*@*WebServlet("/sendassetfrom")*/

public class SendAssetFrom extends HttpServlet {
	
	//private static final long serialVersionUID = 1L;
	private static final String COMMAND_SEND_ASSET_FROM = "sendassetfrom";
	private static final String COMMAND_GET_ADDRESS_BALANCES = "getaddressbalances";
	
	public String sendAssetFrom(String fromaddrs,String toaddrs, String assetname,Integer astqty) {
        Object[] params = {fromaddrs,toaddrs,assetname,astqty };
        ChainConnector objChainConnector = new ChainConnector();
        JSONObject json = objChainConnector.invokeRPC(UUID.randomUUID().toString(), COMMAND_SEND_ASSET_FROM, Arrays.asList(params));
        return (String)json.get("result");
    }

    public JSONArray getAddressBalances(String account) {
        String[] params = { account };
        ChainConnector objChainConnector = new ChainConnector();
        JSONObject json = objChainConnector.invokeRPC(UUID.randomUUID().toString(), COMMAND_GET_ADDRESS_BALANCES, Arrays.asList(params));
        return (JSONArray)json.get("result");
    }
       
    /**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		    response.setContentType("text/html");//setting the content type  
		    //PrintWriter pw=response.getWriter();//get the stream to write the data
		    
		    
		    String strAssetId = request.getParameter("assetid");
		    String strEntityId = request.getParameter("entityid");
		    
		    if(strAssetId == null || strEntityId == null){
		    	System.out.println("############## ERROR : INPUT PARAMETERS MISSING");
		    	response.sendRedirect("ConsolidatedDashboard?error=inputs for sendassetfrom are null");
		    	return;
		    }
		    
		    String strFromAddress = null;
		    String strAssetName = null;
		    BigDecimal dblAssetQty = null;
			String strToAddress = null;
			Boolean bReceiverSendAsMultisig = null;
			String strReceiverMultisigAddr = null;
			
			//--------------------------------------------------------  initial fetching params
			
		    try {
				TradeActionsDAO trdActDao = new TradeActionsDAO();
				
				List<Map> rows = trdActDao.fetchAssetMovement(Integer.parseInt(strAssetId), Integer.parseInt(strEntityId));
				
				/*for(Map row : rows){*/
				Map row = rows.get(0);
				Integer iFromAddressSeqNo = (Integer) row.get("SEQ_NO");
				strFromAddress = (String) row.get("CHAIN_ADDRESS");
				strAssetName = (String) row.get("ASSET_NAME");
				dblAssetQty = (BigDecimal) row.get("QUANTITY");
				
				int iToAddressSeqNo = iFromAddressSeqNo.intValue()+1;
				List<Map> rowsDest = trdActDao.fetchAssetMovementForAstSeq(Integer.parseInt(strAssetId), iToAddressSeqNo);
				Map rowDest = rowsDest.get(0);
				strToAddress = (String) rowDest.get("CHAIN_ADDRESS");
				bReceiverSendAsMultisig = (Boolean)rowDest.get("SEND_AS_MULTISIG");
				strReceiverMultisigAddr = (String) rowDest.get("MULTISIG_ADDR");
				/*}*/
		    } catch (Exception se) {
			      se.printStackTrace();
			}
		    
		    //String chainname = request.getParameter("chainname");
		    /*String fromaddrs = request.getParameter("fromaddrs");
		    String toaddrs = request.getParameter("toaddrs");
		    String assetname = request.getParameter("assetname");
		    String astqty = request.getParameter("astqty");*/ 
		    
		    
		    //--------------------------------------------------------  multichain rel
		    
		    //writing html in the stream  
		    /*pw.println("<html><body>");  
		    pw.println("Welcome to servlet111122");  */
		    
		    String objStr1 = null;
		    System.out.println("### rpc on multichain:sendassetfrom");
		    if(bReceiverSendAsMultisig != null && bReceiverSendAsMultisig.booleanValue()) {
				if(strReceiverMultisigAddr == null){
					//dont do
				}else{
					 objStr1 = /*objRpctest.*/sendAssetFrom(strFromAddress,strReceiverMultisigAddr,strAssetName,dblAssetQty.intValue());
				}
			} else {
				objStr1 = /*objRpctest.*/sendAssetFrom(strFromAddress,strToAddress,strAssetName,dblAssetQty.intValue());
			}
		    
		    if(objStr1 == null){
		    	System.out.println("~~~~~~~~~~~~ output of sendassetfrom received from multichain is null");
		    	response.sendRedirect("ConsolidatedDashboard?error=output of sendassetfrom received from multichain is null");
		    	return;
		    }
		    
	    	//String objStr1 = /*objRpctest.*/sendAssetFrom(strFromAddress,strToAddress,strAssetName,dblAssetQty.intValue());
	    	System.out.println(" size of output of sendassetfrom: "+(objStr1 == null ? "null" : objStr1.length()) );
	    	System.out.println(" output sendassetfrom: " + objStr1);
	    	//pw.println("####" + objJson1.toString());
	    	
	    	
	    	//-------------------------------------------------------- last db updates
	    	
	    	try {
				TradeActionsDAO trdActDao = new TradeActionsDAO();
				
				int status = trdActDao.updateSentTxOfAssetMovement(objStr1, Integer.parseInt(strAssetId), Integer.parseInt(strEntityId));
				System.out.println("~~~~~~~~ output sendassetfrom saved to DB : " + status);
				
	    	} catch (Exception se) {
			      se.printStackTrace();
			}
	    	
	    	//--------------------------------------------------------
	    	
	    	
	    	/*pw.println("</br></br>###### testing rpc on multichain:getaddressbalances");
	    	JSONArray objJson2 = objRpctest.getAddressBalances(toaddrs);
	    	pw.println("</br> size of output:"+objJson2.size());
	    	pw.println("</br> " + objJson2.toJSONString());*/
	    	
	    		    
	    	/*pw.println("</body></html>");  
		    pw.close();*///closing the stream  
	    	System.out.println("\n=========================\n");
	    	response.sendRedirect("ConsolidatedDashboard");
	    }
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	//protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	//}

//}
