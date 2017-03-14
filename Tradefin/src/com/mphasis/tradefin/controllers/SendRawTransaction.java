package com.mphasis.tradefin.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import com.mphasis.tradefin.beans.Assets;
import com.mphasis.tradefin.dao.TradeActionsDAO;
import com.mphasis.tradefin.dao.TradeRawTxDAO;
import com.mphasis.tradefin.rpc.ChainAddresses;
import com.mphasis.tradefin.rpc.ChainConnector;
import com.mphasis.tradefin.util.Utils;

/**
 * Servlet implementation class SendRawTransaction
 */

/*@WebServlet("/SendRawTransaction")*/

public class SendRawTransaction extends HttpServlet {
	//private static final long serialVersionUID = 1L;
	private static final String COMMAND_SEND_RAW_TRANSACTION = "sendrawtransaction";
	private static final String COMMAND_SEND_ASSET_FROM = "sendassetfrom";
	
	
	public String sendRawTransaction(String strFinalTxHex) {
        //String params = {strFinalTxHex};
        Object[] params = {strFinalTxHex};
        ChainConnector objChainConnector = new ChainConnector();
        JSONObject json = objChainConnector.invokeRPC(UUID.randomUUID().toString(), COMMAND_SEND_RAW_TRANSACTION, Arrays.asList(params));
        //return (JSONObject)json.get("result");
        return (String)json.get("result");
    }
	
	public String sendAssetFrom(String fromaddrs,String toaddrs, String assetname,Integer astqty) {
        Object[] params = {fromaddrs,toaddrs,assetname,astqty };
        ChainConnector objChainConnector = new ChainConnector();
        JSONObject json = objChainConnector.invokeRPC(UUID.randomUUID().toString(), COMMAND_SEND_ASSET_FROM, Arrays.asList(params));
        return (String)json.get("result");
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");//setting the content type  
	    
		String strAssetId = request.getParameter("assetid");
	    String strAssetHoldingEntityId = request.getParameter("assetholdingentityid");
	    String strMultisigId = request.getParameter("multisigid");
	    
	    
	    if(strAssetId == null || strAssetHoldingEntityId == null || strMultisigId == null){
	    	System.out.println("############## ERROR : INPUT PARAMETERS MISSING");
	    	response.sendRedirect("ConsolidatedDashboard?error=inputs for SendRawTransaction are null");
	    	return;
	    }
	    
	    
	    //-------------------------------------------------------- initial fetching params from DB
	    
	    String strLatestSignTxHex= null;
	    
	    String strFromBankAddr = null;
		String strToBankAddr = null;
		String strAssetName = null;
	    Double dblAssetQty = null;
	    int iAssetQtyForInitialTransfer = 0;
		
	    TradeActionsDAO trdActDao = new TradeActionsDAO();
		TradeRawTxDAO trdRawTxDAO = new TradeRawTxDAO();
		ChainAddresses objChainAddresses = new ChainAddresses();
		
		try {
			
			List<Map> listColumnsOfMultisigEntitesSigned = trdRawTxDAO.fetchMultisigEntitiesSigned(Integer.parseInt(strMultisigId));
			if(listColumnsOfMultisigEntitesSigned != null && listColumnsOfMultisigEntitesSigned.size()>=1){
				Map columnOfMultisigEntitesSigned = listColumnsOfMultisigEntitesSigned.get(0);
				strLatestSignTxHex = (String)columnOfMultisigEntitesSigned.get("SIGN_OUTPUT_HEX");
			}
			/*}*/
	    } catch (Exception se) {
		      se.printStackTrace();
		}
		
		
		//~~~~
		boolean bPartialTransferOfAmountIsFinished = false;
		try {
			
            List<Map> listAtomicTransfers = trdActDao.fetchAtomicTransfersForAsset(Integer.parseInt(strAssetId));
            
            Map atomicTransfers = listAtomicTransfers.get(0);
				
			strFromBankAddr = (String) atomicTransfers.get("FROM_BANK_ADDRESS");
			strToBankAddr = (String) atomicTransfers.get("TO_BANK_ADDRESS");
			String strPartialTransferTxId = (String) atomicTransfers.get("PARTIAL_TRANSFER_TX_ID");
			
			if(strPartialTransferTxId != null && !strPartialTransferTxId.isEmpty()){
				bPartialTransferOfAmountIsFinished = true;
			}
			
			//strAssetName = (String) atomicTransfers.get("ASSET_NAME");
		    //dblAssetQty = (BigDecimal) atomicTransfers.get("QUANTITY");
		    //iAssetQtyForInitialTransfer = dblAssetQty.intValue() / 10;
        } catch (Exception se) {
  		      se.printStackTrace();
  		}
		//~~~~
		
		
		
		
		//------------------------------------------------------------------------------------ multichain rel actions
		
		
	    //String strRecepientAddress = request.getParameter("strRecepientAddress");
	    /*String strFinalTxHex= request.getParameter("strFinalTxHex");*/
	    
	    
	    System.out.println("### testing rpc on multichain:sendrawtransaction");
    	String objStr1 = /*objRpctest.*/sendRawTransaction(strLatestSignTxHex);
    	if(objStr1 == null){
	    	System.out.println("~~~~~~~~~~~~ output of sendrawtransaction received from multichain is null");
	    	response.sendRedirect("ConsolidatedDashboard?error=output of sendrawtransaction received from multichain is null");
	    	return;
	    }
    	System.out.println(" size of output multichain:sendrawtransaction:"+objStr1.length());
    	//pw.println("</br> strRecepientAddress :" + strRecepientAddress);
    	System.out.println(" output objStr1 of multichain:sendrawtransaction:" + objStr1);
    	


	    //~~~~ Do transfer of 10% or 50% of currency from Bank1 to Bank2 
    	String objStr2 = null;
    	try{
    		if(!bPartialTransferOfAmountIsFinished) {
    			JSONObject objAssetAtFromAddress = objChainAddresses.getAssetAvailableAtAddress(strFromBankAddr);
        		if(objAssetAtFromAddress != null){
        			strAssetName = (String)objAssetAtFromAddress.get("name");
        			dblAssetQty = (double)objAssetAtFromAddress.get("qty");
        			iAssetQtyForInitialTransfer = 2000000;//dblAssetQty.intValue() / 2; //TODO: hard-coding amount for now. need to fetch from DB later.
        		}
        		
        	    
        	    System.out.println("### rpc on multichain:sendrawtransaction:sendassetfrom");
        	    objStr2 = sendAssetFrom(strFromBankAddr,strToBankAddr,strAssetName,iAssetQtyForInitialTransfer);
        		if(objStr2 == null){
        	    	System.out.println("~~~~~~~~~~~~ output of multichain:sendrawtransaction:sendassetfrom received from multichain is null");
        	    	response.sendRedirect("ConsolidatedDashboard?error=output of multichain:sendrawtransaction:sendassetfrom received from multichain is null");
        	    	return;
        	    }
        	    System.out.println("### size of output of multichain:sendrawtransaction:sendassetfrom: "+(objStr2 == null ? "null" : objStr2.length()) );
            	System.out.println("### output multichain:sendrawtransaction:sendassetfrom: " + objStr2);
            	/* Note : no need to save the output-tx-id (ie, objStr2) of sendassetfrom to the DB, because we are not using it anywhere as of now */
    		}
    	}catch(Exception e){
    		System.out.println("### ERROR while executing multichain:sendrawtransaction:sendassetfrom ");
    		e.printStackTrace();
    	}
    	//~~~~
    	

    	
    	

    	//-------------------------------------------------------- last db updates
    	
		
		try {
			int status = trdActDao.updateSentTxOfAssetMovement(objStr1, Integer.parseInt(strAssetId), Integer.parseInt(strAssetHoldingEntityId));
			
			//int status = trdRawTxDAO.updateAfterSignRawTx(Integer.parseInt(strMultisigId), Integer.parseInt(strSigningEntityId), strSignedTxOutputHex);
			//AfterCreateRawTx(Integer.parseInt(strAssetId), Integer.parseInt(strEntityId), objStrCreateRawOutput);
			
			System.out.println("### output sendrawtransaction saved to DB : " + status);
			
			if(!bPartialTransferOfAmountIsFinished) {
				int status1 = trdActDao.updatePartialTransferTxOfAtomicTransfers(objStr2, Integer.parseInt(strAssetId));
				System.out.println("### output sendrawtransaction:updatePartialTransferTxOfAtomicTransfers saved to DB : " + status1);
			}
    	} catch (Exception se) {
		      se.printStackTrace();
		}
    	
    	//--------------------------------------------------------
		
    	System.out.println("\n=========================\n");
		response.sendRedirect("ConsolidatedDashboard");
	    
	    
	}

}
