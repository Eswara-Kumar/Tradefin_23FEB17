package com.mphasis.tradefin.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mphasis.tradefin.beans.Assets;
import com.mphasis.tradefin.dao.TradeActionsDAO;
import com.mphasis.tradefin.dao.TradeDAO;
import com.mphasis.tradefin.dao.TradeRawTxDAO;
import com.mphasis.tradefin.rpc.ChainAddresses;

/**
 * Servlet implementation class ConsolidatedDashboard
 */
@WebServlet("/ConsolidatedDashboard")
public class ConsolidatedDashboard extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("text/html");//setting the content type  
	    
		
		PrintWriter pw = response.getWriter();//get the stream to write the data
		//pw.append("Served at: ").append(request.getContextPath());
		
		printHead(pw);
		printMovements(pw);
		printTransfers(pw);
		printStreams(pw);
		printFoot(pw);
		
		pw.close();//closing the stream 

		
	}
	
	private void printHead(PrintWriter pw) {
		
		StringBuffer sb = new StringBuffer();
		sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\"> ");
		sb.append("		<html>");
		sb.append("		<head>");
		sb.append("			<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
		sb.append("			<title>Admin Document List</title>");
		sb.append("			<!-- Latest compiled and minified CSS -->");
		sb.append("			<link rel=\"stylesheet\" href=\"css/bootstrap.min.css\">");
		sb.append("			<link rel=\"stylesheet\" href=\"css/blockchain.css\">");
		
		sb.append("<!-- pdf popup scripts : start -->");
		sb.append("<script type='text/javascript' src='js/jquery.min.js'></script>"); //http://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js
		sb.append("<script type='text/javascript' src='js/jquery-ui.js'></script>"); //http://ajax.aspnetcdn.com/ajax/jquery.ui/1.8.9/jquery-ui.js
		sb.append("<link type='text/css' rel='stylesheet' href='css/jquery-ui.css'/>"); //http://ajax.aspnetcdn.com/ajax/jquery.ui/1.8.9/themes/blitzer/jquery-ui.css
		sb.append("<!-- pdf popup scripts : end -->");
	    
		
		
		sb.append("		</head>");
		sb.append("");
		sb.append("");
		sb.append("				<body>");
		sb.append("");				
		sb.append("					<nav class=\"navbar navbar-inverse navbar-static-top borderBottom\">");
		sb.append("						<div class=\"navbar-header\">");
		sb.append("							<a class=\"navbar-brand\" href=\"#\">");
		sb.append("								<img src=\"images/SBI-Logo.jpg\" class=\"brandLogoImage1\" style=\"width: 290px; height: 75px;\"/> <!-- ref: http://www.northeastacademy.in/payment-gateway -->");
		//sb.append("								<img src=\"images/mphasis_logo.png\" class=\"brandLogoImage1\"/>");
		sb.append("							</a>");
		sb.append("						</div>");
		sb.append("						<div id=\"navbar\">");
		sb.append("							<ul class=\"nav navbar-nav\">");
		sb.append("								<li class=\"dropdown\">");
		sb.append("								  <a href=\"#\" class=\"dropdown-toggle\" data-toggle=\"dropdown\" role=\"button\" aria-haspopup=\"true\" aria-expanded=\"false\">&nbsp;&nbsp;&nbsp; John Doe <span class=\"caret\"></span></a>");
		sb.append("								  <ul class=\"dropdown-menu\">");
		sb.append("									<li><a href=\"#\">Link 1</a></li>");
		sb.append("								  </ul>");
		sb.append("								</li>");
		sb.append("");								
		sb.append("");								
		sb.append("							</ul>");
		sb.append("							<ul class=\"nav navbar-nav alignRight\">");
		sb.append("								<li><a href=\"#\">Logged in at 12:30pm</a></li>");
		sb.append("							</ul>");
		sb.append("						</div>");			
		sb.append("					</nav>");
		sb.append("");					
		sb.append("					<div class=\"pageLocator\">");
		sb.append("						<span>Home &nbsp; > &nbsp;</span>"); 
		sb.append("						<span class=\"active\">Export Details</span>");
		sb.append("					</div>");
		sb.append("					<div class=\"outerContainer\">");
		sb.append("					<div class=\"container\">");
		sb.append("						<div class=\"navHeading\">");
		sb.append("							<span class=\"alignLeft sideHeading\">Consignments</span>");
		sb.append("							<!--<span><button class=\"alignRight newDoc\" id=\"loginSubmitButton\">Add New Doc</button></span>-->");
		sb.append("						</div>");
		sb.append("						<!-- --------------------------------------------- -->");
		
		pw.println(sb.toString());
		
	}
	
	private void printMovements(PrintWriter pw) {
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("		<!-- --------------------------------------------- -->");
		sb.append("		<table class=\"table table-bordered\"> ");
		sb.append("			<thead class=\"defaultHeading\"> ");
		sb.append("				<tr> ");
		sb.append("					<th>Sl. No</th>");
		sb.append("					<th>Goods/<br>Qty/<br>Amount</th>");
		sb.append("					<th>Goods Blockchain Ref./<br>Created Time</th>");
		sb.append("					<th>Exporter</th>"); 
		//sb.append("					<th>Truck &nbsp;&nbsp;&nbsp;<img src=\"images/delivery-truck2.png\" class=\"downloadImage\"/></th>");
		sb.append("					<th>Exporter Port &nbsp;&nbsp;&nbsp;<img src=\"images/port-container2.png\" class=\"downloadImage\"/></th>");
		//sb.append("					<th>Ship &nbsp;&nbsp;&nbsp;<img src=\"images/ship2.png\" class=\"downloadImage\"/></th>");
		sb.append("					<th>Importer Port &nbsp;&nbsp;&nbsp;<img src=\"images/port-container2.png\" class=\"downloadImage\"/></th>");
		//sb.append("					<th>Truck &nbsp;&nbsp;&nbsp;<img src=\"images/delivery-truck2.png\" class=\"downloadImage\"/></th>");				
		sb.append("					<th>Importer</th>");
		sb.append("				</tr>"); 
		sb.append("			</thead>"); 
		sb.append("			<tbody> ");
		
		
		try {
			TradeActionsDAO trdActDao = new TradeActionsDAO();
			TradeRawTxDAO trdRawTxDAO = new TradeRawTxDAO();
			
			List<Assets> objListAssets = trdActDao.fetchAssets();
			
			int slNo = 1;
			
			for(Assets objAsset : objListAssets){
				sb.append("				<tr>"); 
				int id  = objAsset.getId();
				
	            List<Map> listColumnsOfUI = trdActDao.fetchAssetMovement(id);
	            
	            int iMaxSeqNo = trdActDao.fetchAssetMovementMaxSeqNo(id);
	            boolean isLastEntityInSupplychain = false;
	            
	            
	            boolean bFinalTransferOfAmountIsFinished = false;
	            List<Map> listAtomicTransfers = trdActDao.fetchAtomicTransfersForAsset(id);
	            Map atomicTransfers = listAtomicTransfers.get(0);
	            if(atomicTransfers != null){
					String strFinalTransferTxId = (String) atomicTransfers.get("FINAL_TRANSFER_TX_ID");
					if(strFinalTransferTxId != null && !strFinalTransferTxId.isEmpty()){
						bFinalTransferOfAmountIsFinished = true;
					}
	            }
	            
				//------------------------------
	            
	            sb.append("					<td scope=\"row\">"+ (slNo++) +"</td>\n");
	            sb.append("					<td scope=\"row\">"+ (objAsset.getStrName()) + "&nbsp;/<br><br>" + (objAsset.getDbQuantity()) + "&nbsp;/<br><br>USD 4000000.00</td>\n");
	            //java.util.Date uDt = new java.util.Date(objAsset.getDtCreatedDate().getYear(), objAsset.getDtCreatedDate().getMonth(), objAsset.getDtCreatedDate().getDay(),  objAsset.getDtCreatedDate().getHours(), objAsset.getDtCreatedDate().getMinutes(), objAsset.getDtCreatedDate().getSeconds());
	            java.util.Date uDt = new java.util.Date(objAsset.getDtCreatedDate().getTime());
	            sb.append("					<td scope=\"row\">"+ (objAsset.getStrAssetRef()) /*+ "<br>" + objAsset.getDtCreatedDate().toString()*/ +"<br><br>"+ (new SimpleDateFormat ("yyyy.MM.dd HH:mm:ss")).format(uDt) + "</td>\n");
				
				for(Map columnOfUI : listColumnsOfUI){
					
					sb.append("					<td>");
					sb.append(columnOfUI.get("ENTITY_NAME"));
					
					
					//------- logic to print placeholder: start
					String strAddress = (String) columnOfUI.get("CHAIN_ADDRESS");
					Integer iEntityTypeId = (Integer) columnOfUI.get("ENTITY_TYPE_ID");
					Boolean bSendAsMultisig = (Boolean)columnOfUI.get("SEND_AS_MULTISIG");
					Integer iMultisigId = (Integer) columnOfUI.get("MULTISIG_ID");
					String strMultisigAddr = (String) columnOfUI.get("MULTISIG_ADDR");
					String strAssetRef = (String) columnOfUI.get("ASSET_REF");
					Integer iEntityId = (Integer) columnOfUI.get("ENTITY_ID");
					String strCreateRawTxHex = (String) columnOfUI.get("RAW_TX_HEX");
					Integer iSeqNo = (Integer) columnOfUI.get("SEQ_NO");
					
					if(iSeqNo == iMaxSeqNo){
						isLastEntityInSupplychain = true;
					}
					
					sb.append("</br><font size='1'><b>Blockchain Address:</b></font> </br><font size='1'>"+strAddress+"</font>");
					if(bSendAsMultisig != null && bSendAsMultisig.booleanValue()) {
						if(strMultisigAddr != null){
							sb.append("</br><font size='1'><b>Blockchain Multisig Address:</b></font> </br><font size='1'>"+strMultisigAddr+"</font>");
						}
					}
					
					
					
					
					ChainAddresses objChainAddresses = new ChainAddresses();
					boolean isSendAsMultisigWithAddress = false;
					boolean isAssetAvailableAtAddress = false;
					boolean isEligibleForCreateRawTxButton = false;
					boolean isHavingSignedEntities = false;
					boolean isHavingSignsPending = false;
					StringBuffer sbSigned = new StringBuffer();
					StringBuffer sbSigns = new StringBuffer();
					if(bSendAsMultisig != null && bSendAsMultisig.booleanValue()) {
						if(strMultisigAddr == null){
							//dont print location-pin or placeholder
							isAssetAvailableAtAddress = false;
						}else{
							isSendAsMultisigWithAddress = true;
							isAssetAvailableAtAddress = objChainAddresses.isAssetAvailableAtAddress(strMultisigAddr, strAssetRef);
							if(strCreateRawTxHex == null || strCreateRawTxHex.isEmpty()){
								isEligibleForCreateRawTxButton = true;
							}
							
							//---------
							sbSigned.append("</br></br><font size=2><b>Signed Entities:</b></font>");
							List<Map> listColumnsOfMultisigEntites = trdRawTxDAO.fetchMultisigEntities(iMultisigId.intValue());
							
							for(Map columnOfMultisigEntites : listColumnsOfMultisigEntites){
								Integer iEntityIdInMultisig = (Integer) columnOfMultisigEntites.get("ENTITY_ID");
								Boolean bSigned = (Boolean)columnOfMultisigEntites.get("SIGNED");
								String strEntityName = (String) columnOfMultisigEntites.get("NAME");
								
								if(bSigned != null && bSigned.booleanValue()){
									isHavingSignedEntities = true;
									sbSigned.append("</br><font size=2 color=green>"+strEntityName+"</font>");
								}
								
								if(bSigned == null || !bSigned.booleanValue()){
									isHavingSignsPending = true;
									sbSigns.append("</br><button type='button' onclick=\"location.href='SignRawTransaction?assetid="+id+"&assetholdingentityid="+iEntityId.intValue()+"&multisigid="+iMultisigId.intValue()+"&signingentityid="+iEntityIdInMultisig.intValue()+"'\">Sign as "+strEntityName+"</button>");
								}
							}
							sbSigned.append(isHavingSignedEntities ? "</br>" : "</br>None</br>");
							//---------
				        }
					} else {
						isAssetAvailableAtAddress = objChainAddresses.isAssetAvailableAtAddress(strAddress, strAssetRef);
					}
					
					sb.append(sbSigned.toString());
					
					if(isAssetAvailableAtAddress){
						sb.append("</br><img src=\"images/placeholder_24.png\" style=\"height:32px; width:30px;\" class=\"downloadImage\"/>");
						
						//TODO
						//we are hard-coding the numbers below for now. Need to follow standard way in future.
						if(iEntityTypeId==1/*exporter*/) {
							sb.append("</br></br><button type='button' onclick=\"location.href='PublishDocToStream?assetid="+id+"&entityid="+iEntityId.intValue()+"'\">Validate Goods<br>(upload docs:<br>1.CommercialInvoice<br>2.PackingList<br>3.Insurance)</button>");
						}else if(iEntityTypeId==2/*importer*/) {
							sb.append("</br></br><button type='button' onclick=\"location.href='PublishDocToStream?assetid="+id+"&entityid="+iEntityId.intValue()+"'\">Validate Goods<br>(upload doc:<br>GoodsReceived)</button>");
						}else if(iEntityTypeId==3/*port*/ && iSeqNo==2/*first port*/) {
							sb.append("</br></br><button type='button' onclick=\"location.href='PublishDocToStream?assetid="+id+"&entityid="+iEntityId.intValue()+"'\">Validate Goods<br>(upload doc:<br>BillOfLading)</button>");
						}
						
						
						if(isEligibleForCreateRawTxButton){
							sb.append("</br></br><button type='button' onclick=\"location.href='CreateRawTransaction?assetid="+id+"&entityid="+iEntityId.intValue()+"'\">Initiate Signing (CreateRawTx)</button>");
						}else if(isHavingSignsPending){
							sb.append(sbSigns.toString());
						}else if(isSendAsMultisigWithAddress){
							//TODO
							//we are hard-coding the numbers below for now. Need to follow standard way in future.
							String strSendRawTxPopupMsg = "";
							if(iEntityTypeId==3/*port*/ && iSeqNo==2/*first port*/) {
								strSendRawTxPopupMsg = "alert('Please note that 50% of Good\\'s total amount will be transferred to Exporter Bank');";
							}
							sb.append("</br></br><button type='button' onclick=\""+strSendRawTxPopupMsg+" location.href='SendRawTransaction?assetid="+id+"&assetholdingentityid="+iEntityId.intValue()+"&multisigid="+iMultisigId.intValue()+"'\">Send (sendrawtx) </button>");
						}else{
							if(!isLastEntityInSupplychain){
								sb.append("</br></br><button type='button' onclick=\"location.href='SendAssetFrom?assetid="+id+"&entityid="+iEntityId.intValue()+"'\">Send Asset</button>");
							}else if(!bFinalTransferOfAmountIsFinished){
								sb.append("</br></br><button type='button' onclick=\"location.href='InitiateFinalPayment?assetid="+id+"&entityid="+iEntityId.intValue()+"'\">Initiate Payment</button>");
							}
						}
							
						
						//for info: sb.append("</br><input TYPE='button' VALUE='Send Asset' onclick=\"window.location.href='http://www.wherever.com'\"> ");
						//wrong button syntax: sb.append("</br><button name='sendAsset' value='Send Asset'/>");
						
					}
					//------- ------- logic to print placeholder: end
					
					
					
					//------- ------- logic to print movement buttons: start
					
					/*
					for normal address: send button (entityid, assetid)
					for multisig address: 
						if no one signed and no create-tx-hex: create button(entityid, assetid)
						else : no one signed and present create-tx-hex: sign button (entityid, assetid)
						else : other signed: sign button (entityid, assetid)
					*/
					
					//------- ------- logic to print movement buttons: end
					
					
					sb.append("</td>\n"); 
			
					/*//sb.append("				<%");
					//sb.append("				ChainConnector objChainConnector = new ChainConnector();");
					//sb.append("	        	//JSONObject json = objChainConnector.invokeRPC(UUID.randomUUID().toString(), COMMAND_SIGN_RAW_TRANSACTION, Arrays.asList(params));");
					//sb.append("			    %>");
					sb.append("					<td class=\"middle\"><img src=\"images/placeholder_24.png\" class=\"downloadImage\"/>	</td>"); 
					sb.append("					<td></td>"); 
					sb.append("					<td></td>"); 
					sb.append("					<td></td>"); 
					sb.append("					<td></td>"); 
					sb.append("					<td>VNR imports</td>");*/
				}
				
				sb.append("				</tr>\n"); 
				
		
			 }
		} catch (Exception se) {
		      se.printStackTrace();
		}
		
		sb.append("			</tbody>\n");
		sb.append("		</table>\n");
		sb.append("\n");		
		sb.append("		<!-- --------------------------------------------- -->\n");

		pw.println(sb.toString());
	}

	private void printTransfers(PrintWriter pw) {
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("				<!-- --------------------------------------------- -->\n");
		sb.append("				\n");
		sb.append("\n");
		sb.append("\n");
		sb.append("				<div class=\"navHeading\">\n");
		sb.append("					<span class=\"alignLeft sideHeading\">Bank Transfers</span>\n");
		sb.append("				</div>\n");
		
		try {
			TradeActionsDAO trdActDao = new TradeActionsDAO();
			//TradeRawTxDAO trdRawTxDAO = new TradeRawTxDAO();
			ChainAddresses objChainAddresses = new ChainAddresses();
			
			List<Assets> objListAssets = trdActDao.fetchAssets();
			
			int slNo = 1;
			
			for(Assets objAsset : objListAssets){
				int id  = objAsset.getId();
				
	            List<Map> listAtomicTransfers = trdActDao.fetchAtomicTransfersForAsset(id);
	            
	            for(Map atomicTransfers : listAtomicTransfers){
					
					String strFromBankName = (String) atomicTransfers.get("FROM_BANK_NAME");
					String strToBankName = (String) atomicTransfers.get("TO_BANK_NAME");
					String strFromBankAddr = (String) atomicTransfers.get("FROM_BANK_ADDRESS");
					String strToBankAddr = (String) atomicTransfers.get("TO_BANK_ADDRESS");
					
					
					String strAssetFromAddr = new String("No Balance");
					JSONObject objAssetAtFromAddress = objChainAddresses.getAssetAvailableAtAddress(strFromBankAddr);
					if(objAssetAtFromAddress != null){
						strAssetFromAddr = (String)objAssetAtFromAddress.get("name") + " " + BigDecimal.valueOf((double)objAssetAtFromAddress.get("qty")).toPlainString();
					}
					
					String strAssetToAddr = new String("No Balance");
					JSONObject objAssetAtToAddress = objChainAddresses.getAssetAvailableAtAddress(strToBankAddr);
					if(objAssetAtToAddress != null){
						strAssetToAddr = (String)objAssetAtToAddress.get("name") + " " + BigDecimal.valueOf((double)objAssetAtToAddress.get("qty")).toPlainString();
					}
					
					sb.append("				<div class=\"container\" style=\"padding:10px; height: 115px; border: solid 1px #406f99; background-color:#406f99; text-align:center; font-size:14pt;font-family:calibri;color;#555555;\">\n");
					sb.append("  				<div class=\"container\" style=\"height:93px; width:15%; float:left; margin-right:10px;\"> <font size='3px'>Goods Ref: " + objAsset.getStrAssetRef() + " <br> " + objAsset.getStrName() + ":" + objAsset.getDbQuantity() + "</font></div>");
		    		sb.append("					<div class=\"container\" style=\"height:93px; width:30%; float:left;\">\n");
		    		sb.append("						"+strToBankName+"(Exporter Bank)\n");
		    		sb.append("					</br><font size='1'><b>Blockchain Address:</b>"+strToBankAddr+"</font>");
		    		sb.append("						<br/>Balance => "+strAssetToAddr+"\n");
		    		sb.append("					</div>\n");
		    		
		    		sb.append("					<img src=\"images/254054.png\" class=\"downloadImage\" style=\"height:93px; width:200px;\"/>\n");
		    		
		    		sb.append("					<div class=\"container\" style=\"height: 93px; width:30%; float:right; font-size:14pt;font-family:calibri;color;#555555;\">\n");
		    		sb.append("						"+strFromBankName+"(Importer Bank)\n");
		    		sb.append("					</br><font size='1'><b>Blockchain Address:</b>"+strFromBankAddr+"</font>");
		    		sb.append("						<br/>Balance => "+strAssetFromAddr+"\n");
		    		sb.append("					</div>\n");
		    		sb.append("				</div>\n");
					
	            }
			}
		} catch (Exception se) {
		      se.printStackTrace();
		}
			
		
		sb.append("\n");				
		sb.append("				<!-- --------------------------------------------- -->\n");

		pw.println(sb.toString());
	}

	private void printStreams(PrintWriter pw) {
	
		StringBuffer sb = new StringBuffer();

		sb.append("				<!-- --------------------------------------------- -->\n");
		sb.append("\n");
		/*sb.append("				<div class='container-fluid'>");*/
		sb.append("				<div class=\"navHeading\">\n");
		sb.append("					<span class=\"alignLeft sideHeading\">Streams</span>\n");
		sb.append("				</div>\n");
		
		sb.append("				<div id='dialog' style='display: none'>     </div>");

		sb.append("				<table class=\"table table-bordered\"> \n");
		sb.append("					<thead class=\"defaultHeading\">\n"); 
		sb.append("						<tr>\n"); 
		sb.append("							<th>Sl. No</th>\n"); 
		sb.append("							<th>Good:Qty:Ref</th>\n");
		sb.append("							<th>Stream Name</th>\n");
		sb.append("							<th>Document Name</th> \n");
		sb.append("							<th>Date Time</th>\n");
		sb.append("							<th>Publisher</th> \n");
		//sb.append("							<th>Tx Id</th>\n");	
		//sb.append("							<th>Permissions</th>					\n");
		sb.append("						</tr>\n"); 
		sb.append("					</thead>\n"); 
		sb.append("					<tbody>\n"); 
		
		
		
		
		//----------------------
		
		try {
			TradeActionsDAO trdActDao = new TradeActionsDAO();
			//TradeRawTxDAO trdRawTxDAO = new TradeRawTxDAO();
			ChainAddresses objChainAddresses = new ChainAddresses();
			
			List<Assets> objListAssets = trdActDao.fetchAssets();
			
			int slNo = 1;
			
			for(Assets objAsset : objListAssets){
				int id  = objAsset.getId();
				
	            List<Map> listStreamsets = trdActDao.fetchStreamsetsForAsset(id);
	            
	            for(Map streamsets : listStreamsets){
	            	String strStreamsetName = (String) streamsets.get("NAME");
	            	
	            	
	            	JSONArray arrStreamItems = objChainAddresses.getStreamItemsForStream(strStreamsetName);
	            	int i = 1;
	            	for(Object objStreamItem : arrStreamItems.toArray()) {
	            		try {
	            			//some times blocktime will not generate immediately after publishing the doc.
	            			//so there is a chance of NullPointerException. 
	            			//so in that case it should still loop for other docs, and other assets. so need to have try-catch
	            			
	            			JSONObject objJson = (JSONObject) objStreamItem;
		            		System.out.println("### objStreamItem:"+objJson.toJSONString());
		            		String strKey = (String) objJson.get("key");
		            		
		            		Long lTime = (Long) objJson.get("blocktime");
		            		String dateText = "";
		            		if(lTime != null){
		            			Date date = new Date(lTime*1000);
			                    SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			                    dateText = df2.format(date);
		            		}
		            		
		            		JSONArray strPublisher = (JSONArray)objJson.get("publishers");
		            		StringBuffer sbPub =  new StringBuffer();
		            		for(Object obj1 : strPublisher.toArray()){
		            			String strPub = (String)obj1;
		            			Map<String, Object> row = trdActDao.fetchEntityForAddress(strPub);
		            			sbPub.append(row.get("NAME")+"<br>");
		            		}
		            		
		            		
		            		sb.append("						<tr> \n");
		            		sb.append("							<td scope=\"row\"> "+ (slNo++) +" </td> \n");
		            		sb.append("							<td scope=\"row\"> "+ objAsset.getStrName() +":"+ objAsset.getDbQuantity() +"<br>"+ objAsset.getStrAssetRef() +" </td> \n");
		            		sb.append("							<td scope=\"row\"> "+ (strStreamsetName) +" </td> \n");
		            		
		            		String srtKeyWithoutSpaces = strKey.replaceAll(" ", "");
		            		String strLinkName = srtKeyWithoutSpaces+(i++)+"_"+strStreamsetName+"_"+objAsset.getStrAssetRef();
		            		
		            		sb.append("<script type='text/javascript'>");
		            		sb.append("    $(function () {");
		            		//sb.append("        var fileName = \"3Commercial_Invoice.pdf\";");
		            		sb.append("        var fileName = \""+srtKeyWithoutSpaces+".pdf\";");
		            		sb.append("        $(\"#"+strLinkName+"\").click(function () {");
		            		sb.append("            $(\"#dialog\").dialog({");
		            		sb.append("                modal: true,");
		            		sb.append("                title: fileName,");
		            		sb.append("                width: 640,");
		            		sb.append("                height: 850,");
		            		sb.append("                buttons: {");
		            		sb.append("                    Close: function () {");
		            		sb.append("                        $(this).dialog(\"close\");");
		            		sb.append("                    }");
		            		sb.append("                },");
		            		sb.append("                open: function () {");
		            		sb.append("                    var object = \"<object data='{FileName}' type='application/pdf' width='600px' height='700px'>\";");
		            		sb.append("                    object += \"If you are unable to view file, you can download from <a href='{FileName}'>here</a>\";");
		            		sb.append("                    object += \" or download <a target = '_blank' href = 'http://get.adobe.com/reader/'>Adobe PDF Reader</a> to view the file.\";");
		            		sb.append("                    object += \"</object>\";");
		            		sb.append("                    object = object.replace(/{FileName}/g, \"docs/\" + fileName);");
		            		sb.append("                    $(\"#dialog\").html(object);");
		            		sb.append("                }");
		            		sb.append("            });");
		            		sb.append("        });");
		            		sb.append("    });");
		            		sb.append("</script>");
		            		
		            		sb.append("							<td scope=\"row\"> <a id=\""+strLinkName+"\" >"+ (strKey) +"</a> \n");
	
		            		
		            		sb.append("							</td> <td scope=\"row\"> "+ dateText +" </td> \n");
		            		sb.append("							<td scope=\"row\"> "+ (sbPub.toString()) +" </td> \n");
		            		sb.append("						</tr>\n");
		            	} catch (Exception se) {
		      		      se.printStackTrace();
		            	}
	            	}
	            }
	        }
		} catch (Exception se) {
		      se.printStackTrace();
		}
		
		//------------------------
		
		/*
		sb.append("						<tr> \n");
		sb.append("							<td scope=\"row\">1</td> \n");
		sb.append("							<td>Sales Contract</td>\n"); 
		sb.append("							<td>2016-05-05 03:39:11</td> \n");
		sb.append("							<td>Exporter</td>\n"); 
		sb.append("							<td>Importer</td>\n");
		sb.append("							<td><a href=\"#\">Add</a></td>\n");
		sb.append("						</tr>\n"); 
		
		
		
		sb.append("						<tr>\n"); 
		sb.append("							<td scope=\"row\">2</td>\n"); 
		sb.append("							<td>Sales Contract Ack</td>\n"); 
		sb.append("							<td>2016-05-05 03:39:11</td>\n"); 
		sb.append("							<td>Importer</td>\n"); 
		sb.append("							<td>Exporter</td>\n");
		sb.append("							<td><a href=\"#\">Add</a></td>\n");
		sb.append("						</tr>\n"); 
		sb.append("						<tr>\n"); 
		sb.append("							<td scope=\"row\">3</td> \n");
		sb.append("							<td>Invoice</td>\n"); 
		sb.append("							<td>2016-05-05 03:39:11</td> \n");
		sb.append("							<td>Exporter</td>\n"); 
		sb.append("							<td>Importer</td>\n");
		sb.append("							<td><a href=\"#\">Add</a></td>\n");
		sb.append("						</tr>\n"); 
		sb.append("						<tr>\n"); 
		sb.append("							<td scope=\"row\">4</td> \n");
		sb.append("							<td>Truck Invoice</td>\n"); 
		sb.append("							<td>2016-05-05 03:39:11</td> \n");
		sb.append("							<td>Truck</td>\n"); 
		sb.append("							<td>Exporter, Importer</td>\n");
		sb.append("							<td><a href=\"#\">Add</a></td>\n");
		sb.append("						</tr>\n");*/ 
		sb.append("					</tbody>\n");
		sb.append("				</table>\n");
		
		pw.println(sb.toString());
	}
	
	private void printFoot(PrintWriter pw) {
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("			</div>\n");
		sb.append("			</div>\n");
		sb.append("			<footer style=\"position:relative;\"> \n");
		sb.append("				Copyrights 2016. All Rights Reserved.\n");
		sb.append("			</footer>\n");
		sb.append("		</body>\n");	
		sb.append("\n");	
		sb.append("\n");
		sb.append("</html>\n");

		pw.println(sb.toString());
	}
}
