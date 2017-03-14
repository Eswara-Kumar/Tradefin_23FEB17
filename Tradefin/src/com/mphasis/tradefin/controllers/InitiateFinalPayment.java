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
 * Servlet implementation class InitiateFinalPayment
 */

@WebServlet("/InitiateFinalPayment")

public class InitiateFinalPayment extends HttpServlet {
	
	//private static final long serialVersionUID = 1L;
	private static final String COMMAND_SEND_ASSET_FROM = "sendassetfrom";
	
	
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
	    String strEntityId = request.getParameter("entityid");
	    
	    if(strAssetId == null || strEntityId == null){
	    	System.out.println("############## ERROR : INPUT PARAMETERS MISSING");
	    	response.sendRedirect("ConsolidatedDashboard?error=inputs for InitiateFinalPayment/sendassetfrom are null");
	    	return;
	    }
	    
	    //-------------------------------------------------------- initial fetching params from DB
	    
	    String strFromBankAddr = null;
		String strToBankAddr = null;
		String strAssetName = null;
	    Double dblAssetQty = null;
	    int iAssetQtyForTransfer = 0;
		
	    TradeActionsDAO trdActDao = new TradeActionsDAO();
		TradeRawTxDAO trdRawTxDAO = new TradeRawTxDAO();
		ChainAddresses objChainAddresses = new ChainAddresses();
		
		
		//~~~~
		try {
			
            List<Map> listAtomicTransfers = trdActDao.fetchAtomicTransfersForAsset(Integer.parseInt(strAssetId));
            
            Map atomicTransfers = listAtomicTransfers.get(0);
				
			strFromBankAddr = (String) atomicTransfers.get("FROM_BANK_ADDRESS");
			strToBankAddr = (String) atomicTransfers.get("TO_BANK_ADDRESS");
			//strAssetName = (String) atomicTransfers.get("ASSET_NAME");
		    //dblAssetQty = (BigDecimal) atomicTransfers.get("QUANTITY");
		    //iAssetQtyForInitialTransfer = dblAssetQty.intValue() / 10;
        } catch (Exception se) {
  		      se.printStackTrace();
  		}
		//~~~~
		
		
		
		
		//------------------------------------------------------------------------------------ multichain rel actions
		
		String objStr2 = null;
		 
	    //~~~~ Do transfer of remaining (ie.50% was transferred previously) currency of Asset from Bank1 to Bank2 
    	try{
    		JSONObject objAssetAtFromAddress = objChainAddresses.getAssetAvailableAtAddress(strFromBankAddr);
    		if(objAssetAtFromAddress != null){
    			strAssetName = (String)objAssetAtFromAddress.get("name");
    			dblAssetQty = (double)objAssetAtFromAddress.get("qty");
    			iAssetQtyForTransfer = 2000000;//dblAssetQty.intValue() / 2; //TODO: hard-coding amount for now. need to fetch from DB later.
    		}
    		
    	   
    	    System.out.println("### rpc on multichain:InitiateFinalPayment:sendassetfrom");
    	    objStr2 = sendAssetFrom(strFromBankAddr,strToBankAddr,strAssetName,iAssetQtyForTransfer);
    		if(objStr2 == null){
    	    	System.out.println("~~~~~~~~~~~~ output of multichain:InitiateFinalPayment:sendassetfrom received from multichain is null");
    	    	response.sendRedirect("ConsolidatedDashboard?error=output of multichain:InitiateFinalPayment:sendassetfrom received from multichain is null");
    	    	return;
    	    }
    	    System.out.println("### size of output of multichain:InitiateFinalPayment:sendassetfrom: "+(objStr2 == null ? "null" : objStr2.length()) );
        	System.out.println("### output multichain:InitiateFinalPayment:sendassetfrom: " + objStr2);
        	/* Note : no need to save the output-tx-id (ie, objStr2) of sendassetfrom to the DB, because we are not using it anywhere as of now */ 
    	}catch(Exception e){
    		System.out.println("### ERROR while executing multichain:InitiateFinalPayment:sendassetfrom ");
    		e.printStackTrace();
    	}
    	//~~~~
    	

    	
    	

    	//-------------------------------------------------------- last db updates
    	
		try {
			int status = trdActDao.updateFinalTransferTxOfAtomicTransfers(objStr2, Integer.parseInt(strAssetId));
			
			System.out.println("### output InitiateFinalPayment saved to DB : " + status);
			
    	} catch (Exception se) {
		      se.printStackTrace();
		}
    	
    	//--------------------------------------------------------
		
    	System.out.println("\n=========================\n");
		response.sendRedirect("ConsolidatedDashboard");
	    
	    
	}

}
