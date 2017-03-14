package com.mphasis.tradefin.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;

import com.mphasis.tradefin.dao.TradeActionsDAO;
import com.mphasis.tradefin.rpc.ChainConnector;
import com.mphasis.tradefin.util.BinToJsonHexa1;

/**
 * Servlet implementation class PublishDocToStream
 */
@WebServlet("/PublishDocToStream")
public class PublishDocToStream extends HttpServlet {	
	
	private static final String COMMAND_PUBLISH_FROM = "publishfrom";	
	
	public String publishFrom(String fromaddrs, String streamName, String keyName, String dataHex) {
        Object[] params = {fromaddrs, streamName, keyName, dataHex };
        ChainConnector objChainConnector = new ChainConnector();
        JSONObject json = objChainConnector.invokeRPC(UUID.randomUUID().toString(), COMMAND_PUBLISH_FROM, Arrays.asList(params));
        return (String)json.get("result");
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("text/html");//setting the content type  
		//response.setContentType("application/json");//for json
		PrintWriter pw=response.getWriter();//get the stream to write the data
		
		//@@@@
		HttpSession session = request.getSession();
		ServletContext sc = session.getServletContext();
		//String x = sc.getRealPath("/");
		String contextPath = sc.getContextPath();
		System.out.println("@@@@@ Context path: "+contextPath);  
		
		ServletContext sc1 = this.getServletContext();
		String pt = sc1.getRealPath("/docs/3Commercial_Invoice.pdf");
		System.out.println("### this.getServletContext().getRealPath(\"/docs/3Commercial_Invoice.pdf\"):"+pt);
        //@@@@		
		
		System.out.println("***PublishDocToStream***");
		
		String strAssetId = request.getParameter("assetid");
		String strEntityId = request.getParameter("entityid");
		
		if(strAssetId == null || strEntityId == null){
			System.out.println("############## ERROR : INPUT PARAMETERS MISSING");
			response.sendRedirect("ConsolidatedDashboard?error=inputs for publishfrom are null");
			return;
		}
		String strStreamName = null;
		String strfromAddress = null;
		String strKeyName = null;
		String dataHex = null;
		int iEntityTypeId;
		String objStr1 = null;
		
		try {
			TradeActionsDAO trdActDao = new TradeActionsDAO();
			List<Map> rows = trdActDao.fetchStreamsetsForAsset(Integer.parseInt(strAssetId));
			Map row = rows.get(0);
			strStreamName = (String) row.get("NAME");		    
			System.out.println("Stream Name: " + strStreamName);
			List<Map> row1 = trdActDao.fetchAssetMovement(Integer.parseInt(strAssetId), Integer.parseInt(strEntityId));
			Map nrow = row1.get(0);
			strfromAddress = (String) nrow.get("CHAIN_ADDRESS");
			iEntityTypeId = (Integer) nrow.get("ENTITY_TYPE_ID");
			System.out.println("fromAddress_chainRelated: " + strfromAddress);
			System.out.println("Entity Type ID: " + iEntityTypeId);
			
			System.out.println("###### rpc on multichain:publishfrom");
			
			if (iEntityTypeId == 1)//Exporter - 3 documents - 3Commercial_Invoice.pdf, 4Packaging_List.pdf & 5Insurance.pdf should be published to the stream
			{
				strKeyName = "CommercialInvoice";
				BinToJsonHexa1 hexa = new BinToJsonHexa1();
				String path = this.getServletContext().getRealPath("/docs/3Commercial_Invoice.pdf");
				//dataHex = hexa.finalCommercialInvoiceJsonHexa();
				dataHex = hexa.finalCommercialInvoiceJsonHexa(path);
					
				objStr1 = /*objRpctest.*/publishFrom(strfromAddress,strStreamName,strKeyName,dataHex);					
					
				strKeyName = "PackagingList";
				//BinToJsonHexa1 hexa = new BinToJsonHexa1();
				String path1 = this.getServletContext().getRealPath("/docs/4Packaging_List.pdf");
				dataHex = hexa.finalPackagingListJsonHexa(path1);
				objStr1 = /*objRpctest.*/publishFrom(strfromAddress,strStreamName,strKeyName,dataHex);
				
				strKeyName = "Insurance";
				//BinToJsonHexa1 hexa = new BinToJsonHexa1();
				String path2 = this.getServletContext().getRealPath("/docs/5Insurance.pdf");
				dataHex = hexa.finalInsuranceJsonHexa(path2);
				objStr1 = /*objRpctest.*/publishFrom(strfromAddress,strStreamName,strKeyName,dataHex);
				
			}else if(iEntityTypeId == 2){
				strKeyName = "GoodsReceived";
				BinToJsonHexa1 hexa = new BinToJsonHexa1();
				String path = this.getServletContext().getRealPath("/docs/7Goods_Received.pdf");
				dataHex = hexa.finalGoodsReceivedJsonHexa(path);
				objStr1 = /*objRpctest.*/publishFrom(strfromAddress,strStreamName,strKeyName,dataHex);
				
			}else if(iEntityTypeId == 3){
				strKeyName = "BillOfLading";
				BinToJsonHexa1 hexa = new BinToJsonHexa1();
				String path = this.getServletContext().getRealPath("/docs/6Bill_Of_Lading.pdf");
				dataHex = hexa.finalBillOfLadingJsonHexa(path);
				objStr1 = /*objRpctest.*/publishFrom(strfromAddress,strStreamName,strKeyName,dataHex);
				
			}
							    
			if(objStr1 == null){
				System.out.println("~~~~~~~~~~~~ output of publishfrom received from multichain is null");
				response.sendRedirect("ConsolidatedDashboard?error=output of publishfrom received from multichain is null");
				return;
			}
			
			//String objStr1 = /*objRpctest.*/publishfrom(strFromAddress,strToAddress,strAssetName,dblAssetQty.intValue());
			System.out.println( " size of output of publishfrom: "+(objStr1 == null ? "null" : objStr1.length()) );
			System.out.println(" output publishfrom: " + objStr1);
			//System.out.println("####" + objJson1.toString());		    	
			
		}catch(Exception se) {
			se.printStackTrace();
		}
				
			/*try {
				TradeActionsDAO trdActDao = new TradeActionsDAO();
				
				int status = trdActDao.updateSentTxOfAssetMovement(objStr1, Integer.parseInt(strAssetId), Integer.parseInt(strEntityId));
				System.out.println("~~~~~~~~ output publishfrom saved to DB : " + status);
				
	    	} catch (Exception se) {
			      se.printStackTrace();
			}*/
		    
		System.out.println("\n==============================\n"); 
		response.sendRedirect("ConsolidatedDashboard");		    
		
	}
}
