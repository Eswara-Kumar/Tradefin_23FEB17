package com.mphasis.tradefin.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.mphasis.tradefin.rpc.ChainAddresses;
import com.mphasis.tradefin.rpc.ChainConnector;
import com.mphasis.tradefin.util.Utils;
import com.mphasis.tradefin.beans.CreateRawTransactionPojo;
import com.mphasis.tradefin.dao.TestTableDAO;
import com.mphasis.tradefin.dao.TradeActionsDAO;
import com.mphasis.tradefin.dao.TradeDAO;
import com.mphasis.tradefin.dao.TradeRawTxDAO;
/**

 * Servlet implementation class CreateRawTransaction
 */
@WebServlet("/CreateRawTransaction")
public class CreateRawTransaction extends HttpServlet {
	private static final String COMMAND_CREATE_RAW_TRANSACTION = "createrawtransaction";
	
	
	
	public String createRawTransaction(String strTxId, String recAddr, Long lN, String assetName, Double quantity){
	
		 
		    JSONObject idsInfos = new JSONObject();
		    idsInfos.put("txid", strTxId);
	        idsInfos.put("vout", lN);
	        
	        JSONArray idsInfosObj = new JSONArray();
	        idsInfosObj.add(idsInfos);
	        
	        JSONObject addr = new JSONObject();
	        
	        JSONObject assetInfo = new JSONObject();
	        //assetInfo.put("asset", assetName);
	        assetInfo.put(""+assetName, quantity);
	        
	        addr.put(""+recAddr, assetInfo);
		 
		 Object[] params = new Object[2];
		 params[0] = idsInfosObj;
		 params[1] = addr;
				  
		 ChainConnector objChainConnector = new ChainConnector();
	     JSONObject json = objChainConnector.invokeRPC(UUID.randomUUID().toString(), COMMAND_CREATE_RAW_TRANSACTION, Arrays.asList(params));
	     return (String)json.get("result");
	}	
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
							
			response.setContentType("text/html");//setting the content type  
		    
		    
			String strAssetId = request.getParameter("assetid");
		    String strEntityId = request.getParameter("entityid");
		    
		    if(strAssetId == null || strEntityId == null){
		    	System.out.println("############## ERROR : INPUT PARAMETERS MISSING");
		    	response.sendRedirect("ConsolidatedDashboard?error=inputs for sendassetfrom are null");
		    	return;
		    }
		    
		    
		    //-------------------------------------------------------- initial fetching params from DB
			
		    String strPrevSentTxId = null;
		    String strToAddress = null;
		    String strToMultisigaddr = null;
		    
		    Long lN = null;
			String strAssetName = null;
			Double dblAssetQty = null;
			
			Boolean bReceiverSendAsMultisig = null;
			
			try {
				TradeActionsDAO trdActDao = new TradeActionsDAO();
				
				List<Map> rows = trdActDao.fetchAssetMovement(Integer.parseInt(strAssetId), Integer.parseInt(strEntityId));
				
				/*for(Map row : rows){*/
				Map row = rows.get(0);
				Integer iFromAddressSeqNo = (Integer) row.get("SEQ_NO");
								
				int iPrevAddressSeqNo = iFromAddressSeqNo.intValue()-1;
				List<Map> rowsPrev = trdActDao.fetchAssetMovementForAstSeq(Integer.parseInt(strAssetId), iPrevAddressSeqNo);
				Map rowPrev = rowsPrev.get(0);
				strPrevSentTxId = (String) rowPrev.get("SENT_TX_ID");
				
				
				int iToAddressSeqNo = iFromAddressSeqNo.intValue()+1;
				List<Map> rowsDest = trdActDao.fetchAssetMovementForAstSeq(Integer.parseInt(strAssetId), iToAddressSeqNo);
				Map rowDest = rowsDest.get(0);
				strToAddress = (String) rowDest.get("CHAIN_ADDRESS");
				bReceiverSendAsMultisig = (Boolean)rowDest.get("SEND_AS_MULTISIG");
				strToMultisigaddr = (String) rowDest.get("MULTISIG_ADDR");
				 
				/*}*/
		    } catch (Exception se) {
			      se.printStackTrace();
			}
			
			/*CreateRawTransactionPojo objCreateRawTransaction = null;
			try {
				TradeRawTxDAO trdRawTxDAO = new TradeRawTxDAO();
				trdRawTxDAO.selectForCreateRawTx(Integer.parseInt(strAssetId), Integer.parseInt(strEntityId));
				
			} catch (Exception se) {
			      se.printStackTrace();
			}
			
		    //ResultSet rs1 =  testD.selectDB(1, 3);
		    strPrevSentTxId = objCreateRawTransaction.getStrTxId();
		    strToAddrOrMultisigaddr = objCreateRawTransaction.getStrMultisigAddr();*/
		    		    
		    /*String assetName= null;
		    Double quantity;*/
		    
		    //System.out.println("###### output of createrawtransactions : initial db : strPrevSentTxId:"+strPrevSentTxId+", strToAddress:"+strToAddress+", strToMultisigaddr:"+strToMultisigaddr);
			
		    //writing html in the stream  
		    /*pw.println("<html><body>");  
		    pw.println("Welcome to servlet1111");  */
		    
		 	//--------------------------------------------------------  initial fetching params from multichain if required
		    
		    System.out.println("### testing rpc on multichain:getRawTransaction");
		    ChainAddresses objChainAddresses = new ChainAddresses();
    	    JSONObject objJsonRawTx = objChainAddresses.getRawTransaction(strPrevSentTxId);
    	    JSONArray obJsonArrVout = (JSONArray) objJsonRawTx.get("vout");
    	    JSONObject objJsonVoutZero = (JSONObject) obJsonArrVout.get(0);
    	    lN = (Long) objJsonVoutZero.get("n");
    	    JSONArray assets = (JSONArray) objJsonVoutZero.get("assets");
    	    JSONObject objJsonAssetZero = (JSONObject) assets.get(0);
    	    strAssetName = (String) objJsonAssetZero.get("name");
    	    dblAssetQty = (Double) objJsonAssetZero.get("qty");
    	    
    	    
    	    //--------------------------------------------------------  multichain rel action

    	    String objStrCreateRawOutput = null;
    	    System.out.println("### testing rpc on multichain:createrawtransactions");
		    if(bReceiverSendAsMultisig != null && bReceiverSendAsMultisig.booleanValue()) {
				if(strToMultisigaddr == null){
					//dont do
				}else{
					objStrCreateRawOutput = createRawTransaction(strPrevSentTxId, strToMultisigaddr, lN, strAssetName, dblAssetQty);
				}
			} else {
				objStrCreateRawOutput = createRawTransaction(strPrevSentTxId, strToAddress, lN, strAssetName, dblAssetQty);
			}
		    
		    if(objStrCreateRawOutput == null){
		    	System.out.println("~~~~~~~~~~~~ output of createrawtransactions received from multichain is null");
		    	response.sendRedirect("ConsolidatedDashboard?error=output of createrawtransactions received from multichain is null");
		    	return;
		    }
		    
	    	System.out.println( " size of output of createrawtransactions: "+(objStrCreateRawOutput == null ? "null" : objStrCreateRawOutput.length()) );
	    	System.out.println(" output createrawtransactions: " + objStrCreateRawOutput);
    	    
			/*String objStrCreateRawOutput = createRawTransaction(strPrevSentTxId, strToAddrOrMultisigaddr, lN, strAssetName, dblAssetQty);
			System.out.println(" size of output of createrawtransactions:"+objStrCreateRawOutput.length());
			System.out.println(" output of createrawtransactions:" + objStrCreateRawOutput);*/
	    	
	    	
	    	//-------------------------------------------------------- last db updates
	    	
			
			try {
				
				TradeRawTxDAO trdRawTxDAO = new TradeRawTxDAO();
				int status = trdRawTxDAO.updateAfterCreateRawTx(Integer.parseInt(strAssetId), Integer.parseInt(strEntityId), objStrCreateRawOutput);
				
				System.out.println("~~~~~~~~ output createRawTransaction saved to DB : " + status);
				
	    	} catch (Exception se) {
			      se.printStackTrace();
			}
	    	
	    	//--------------------------------------------------------
			
	    	/*pw.println("</body></html>");  
		    pw.close();*///closing the stream 
				
	    	//String objJson1 = /*objRpctest.*/createRawTransaction(idsInfoObj, assetInfo);
	    	//pw.println("</br></br>#### size of output:"+objJson1.length());
	    	//pw.println("</br></br>####" + objJson1);
	    	//pw.println("</br></br>####" + objJson1.toJSONString());
	    	//pw.println("####" + objJson1.toString());
			
			System.out.println("\n=========================\n");
			response.sendRedirect("ConsolidatedDashboard");
			
	}

}
