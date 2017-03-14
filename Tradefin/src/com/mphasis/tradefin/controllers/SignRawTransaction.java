package com.mphasis.tradefin.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
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

import com.mphasis.tradefin.beans.CreateRawTransactionPojo;
import com.mphasis.tradefin.beans.SignRawTransactionPojo;
import com.mphasis.tradefin.dao.TradeActionsDAO;
import com.mphasis.tradefin.dao.TradeRawTxDAO;
import com.mphasis.tradefin.rpc.ChainConnector;
import com.mphasis.tradefin.util.Utils;

/**
 * Servlet implementation class SendAssetFrom
 */
/*@*WebServlet("/sendassetfrom")*/

public class SignRawTransaction extends HttpServlet {
	
	//private static final long serialVersionUID = 1L;
	private static final String COMMAND_DUMP_PRIV_KEY = "dumpprivkey";
	private static final String COMMAND_SIGN_RAW_TRANSACTION = "signrawtransaction";
	
	
	
    

    public JSONObject signRawTransaction(String strTxHex, String strTxId, Long objJsonN, String strScriptPubKeyHex, String strRedeemScript, String strPrivKey) {
        /*
         * multichain syntax: 
         * signrawtransaction tx-hex [{"txid":txid,"vout":n,"scriptPubKey":hex,"redeemScript":redeemScript}] ["private-key"]
         */
    	
    	//String str = "{\"txid\":"+strTxId+",\"vout\":"+objJsonN+",\"scriptPubKey\":"+strScriptPubKeyHex+",\"redeemScript\":"+strRedeemScript+"}";
    	
    	JSONObject param0 = new JSONObject();
    	param0.put("txid", strTxId);
    	param0.put("vout", objJsonN);
    	param0.put("scriptPubKey", strScriptPubKeyHex);
    	param0.put("redeemScript", strRedeemScript);
    	JSONArray param1 = new JSONArray();
    	param1.add(param0);
    	
    	//JSONArray param2 = new JSONArray();
    	//param2.add(strPrivKey);
    	String[] param2 = {strPrivKey};
    	
    	//Object[] params = { strTxHex, param1, param2 };
    	Object[] params = new Object[3];
    	params[0] = strTxHex;
	    params[1] = param1;
	    params[2] = Arrays.asList(param2); //new Object[2];
    			
        ChainConnector objChainConnector = new ChainConnector();
        JSONObject json = objChainConnector.invokeRPC(UUID.randomUUID().toString(), COMMAND_SIGN_RAW_TRANSACTION, Arrays.asList(params));
        return (JSONObject)json.get("result");
    }
    
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("text/html");//setting the content type  
	    
		
		String strAssetId = request.getParameter("assetid");
	    String strAssetHoldingEntityId = request.getParameter("assetholdingentityid");
	    String strMultisigId = request.getParameter("multisigid");
	    String strSigningEntityId = request.getParameter("signingentityid");
	    
	    if(strAssetId == null || strAssetHoldingEntityId == null || strMultisigId == null || strSigningEntityId == null){
	    	System.out.println("############## ERROR : INPUT PARAMETERS MISSING");
	    	response.sendRedirect("ConsolidatedDashboard?error=inputs for SignRawTransaction are null");
	    	return;
	    }
	    
	    
	    //-------------------------------------------------------- initial fetching params from DB
	    
	    
	    String strPrevSentTxId = null;
	    String strRawTxHex = null;
	    String strLatestSignTxHex = null;
	    String strSigneeAddress = null;
	    String strRedeemScriptOfMultisig = null;
	    
	    Long lVoutN = null;
		String strVoutHex = null;
				
		Boolean bReceiverSendAsMultisig = null;
		
		
		TradeActionsDAO trdActDao = new TradeActionsDAO();
		TradeRawTxDAO trdRawTxDAO = new TradeRawTxDAO();
		
		try {
			
			List<Map> rows = trdActDao.fetchAssetMovement(Integer.parseInt(strAssetId), Integer.parseInt(strAssetHoldingEntityId));
			
			/*for(Map row : rows){*/
			Map row = rows.get(0);
			Integer iFromAddressSeqNo = (Integer) row.get("SEQ_NO");
			strRawTxHex = (String) row.get("RAW_TX_HEX");
			strRedeemScriptOfMultisig = (String) row.get("MULTISIG_REDEEM_SCRIPT");
							
			int iPrevAddressSeqNo = iFromAddressSeqNo.intValue()-1;
			List<Map> rowsPrev = trdActDao.fetchAssetMovementForAstSeq(Integer.parseInt(strAssetId), iPrevAddressSeqNo);
			Map rowPrev = rowsPrev.get(0);
			strPrevSentTxId = (String) rowPrev.get("SENT_TX_ID");
			
			List<Map> rowsOfSigneeEntity = trdActDao.fetchAssetMovement(Integer.parseInt(strAssetId), Integer.parseInt(strSigningEntityId));
			Map rowOfSigneeEntity = rowsOfSigneeEntity.get(0);
			strSigneeAddress = (String) rowOfSigneeEntity.get("CHAIN_ADDRESS");
			
			List<Map> listColumnsOfMultisigEntitesSigned = trdRawTxDAO.fetchMultisigEntitiesSigned(Integer.parseInt(strMultisigId));
			if(listColumnsOfMultisigEntitesSigned != null && listColumnsOfMultisigEntitesSigned.size()>=1){
				Map columnOfMultisigEntitesSigned = listColumnsOfMultisigEntitesSigned.get(0);
				strLatestSignTxHex = (String)columnOfMultisigEntitesSigned.get("SIGN_OUTPUT_HEX");
			}
			/*}*/
	    } catch (Exception se) {
		      se.printStackTrace();
		}
		
		
		
	   /* SignRawTransactionPojo signRaTxPojo = new SignRawTransactionPojo();
	    String strTxId = signRaTxPojo.getStrTxId();
	    String strTxHex = signRaTxPojo.getStrTxHex();
	    String strRedeemScript = signRaTxPojo.getStrRedeemScript();
	    String strSigneeAddress = signRaTxPojo.getSigneeAddress();*/
	    
	    
	    
		//String strTxHex = request.getParameter("strTxHex");
		//String strTxId = request.getParameter("strTxId");
		//String strRedeemScript = request.getParameter("strRedeemScript");
		//String strSigneeAddress = request.getParameter("signeeAddress");

		
	   /* Long objJsonN = null;
	    String strScriptPubKeyHex= null;
	    
	    System.out.println("</br>###### strTxHex:"+strTxHex);
	    System.out.println("</br>###### strTxId:"+strTxId);
	    System.out.println("</br>###### strRedeemScript:"+strRedeemScript);
	    System.out.println("</br>###### strSigneeAddress:"+strSigneeAddress);*/
	    
		
		
	    //------------------------------------------------------------------------------------ multichain rel actions
	    
	    System.out.println("### testing rpc on multichain:getRawTransaction");
    	JSONObject objJsonRawTx = Utils.getRawTransaction(strPrevSentTxId);
	    JSONArray obJsonArrVout = (JSONArray) objJsonRawTx.get("vout");
	    JSONObject objJsonVoutZero = (JSONObject) obJsonArrVout.get(0);
	    lVoutN = (Long) objJsonVoutZero.get("n");
	    JSONObject objJsonScriptPubKey = (JSONObject) objJsonVoutZero.get("scriptPubKey");
	    strVoutHex = (String) objJsonScriptPubKey.get("hex");
	    
	    
	    System.out.println("### parameters of sign raw tx:");
	    System.out.println("### strPrevSentTxId:"+strPrevSentTxId);
	    System.out.println("### strRawTxHex:"+strRawTxHex);
	    System.out.println("### strLatestSignTxHex:"+strLatestSignTxHex);
	    System.out.println("### strSigneeAddress:"+strSigneeAddress);
	    System.out.println("### strRedeemScriptOfMultisig:"+strRedeemScriptOfMultisig);
	    System.out.println("### lVoutN:"+lVoutN);
	    System.out.println("### strVoutHex:"+strVoutHex);
	    
	    
	    
	    
	    System.out.println("### testing rpc on multichain:dumpprivkey");
    	String strPrivKeyOfSignee = /*objRpctest.*/Utils.dumpPrivKey(strSigneeAddress);
    	System.out.println(" size of output multichain:dumpprivkey:"+strPrivKeyOfSignee.length());
    	System.out.println(" strPrivKeyOfSignee of multichain:dumpprivkey:" + strPrivKeyOfSignee);
	       
    	
    	
    	//----
    	String strTxHex = null;
    	if(strLatestSignTxHex != null && !strLatestSignTxHex.isEmpty()) {
    		strTxHex = strLatestSignTxHex;
    	}else{
    		strTxHex = strRawTxHex;
    	}
    	//---
    	JSONObject objJsonSignRawOutput = null;
    	System.out.println("### testing rpc on multichain:signrawtransaction");
    	objJsonSignRawOutput = /*objRpctest.*/signRawTransaction(strTxHex, strPrevSentTxId, lVoutN, strVoutHex, strRedeemScriptOfMultisig, strPrivKeyOfSignee);
    	String strSignedTxOutputHex = (String)objJsonSignRawOutput.get("hex");
    	System.out.println("size of output multichain:signrawtransaction:"+objJsonSignRawOutput.size());
    	System.out.println("output of multichain:signrawtransaction:" + objJsonSignRawOutput);
    	System.out.println("output strSignedTxOutputHex of multichain:signrawtransaction:" + strSignedTxOutputHex);
    	   		    
    	
    	
    	//-------------------------------------------------------- last db updates
    	
		
		try {
			
			int status = trdRawTxDAO.updateAfterSignRawTx(Integer.parseInt(strMultisigId), Integer.parseInt(strSigningEntityId), strSignedTxOutputHex);
			//AfterCreateRawTx(Integer.parseInt(strAssetId), Integer.parseInt(strEntityId), objStrCreateRawOutput);
			
			System.out.println("~~~~~~~~ output signrawtransaction saved to DB : " + status);
			
    	} catch (Exception se) {
		      se.printStackTrace();
		}
    	
    	//--------------------------------------------------------
		
		System.out.println("\n=========================\n");
		response.sendRedirect("ConsolidatedDashboard");
	}

}
