#!/usr/bin/python
# -*- coding: UTF-8 -*-

# enable debugging
import sys
sys.path.append ('lib')
from mortgageHtmlElements import *
import sys, string,re
#from mchainHtmlElements import *
import mchainlog
logger = mchainlog.setup_custom_logger('root')

from mchainUtil import *
from mchainDB import *
from mchainLib import *
from bitcoinrpc.authproxy import AuthServiceProxy, JSONRPCException

import cgitb
cgitb.enable()

rpc_user = "multichainrpc"
rpc_password = "GA1WySdWGtyRz3VSS4ojy6hFVRmm1nocSC5UnaSPvq6f"
rpc_port = "2906"
rpc_connection = AuthServiceProxy("http://%s:%s@127.0.0.1:%s" %(rpc_user, rpc_password,rpc_port))



# ------------------------------------------------------------------------------------
def displayModalClass(keyDisplay,keyname):

	print " <div class='modal fade' id='%s' role='dialog' width = '50'>" % (keyDisplay)
        print "   <div class='modal-dialog modal-lg'>"
        print "      <div class='modal-content'>"
        print "          <div class='modal-body'>"
        print "             <div class='tableChainFull'>"
        print "                 <table class='table table-borderless table-condensed'>"
        print "                 <thead class='defaultHeading'>"
        print "                    <tr>"
        print "                    <td colspan='4'>Document :%s</td>" % (keyname)
        print "                    </tr>"
        print "                 </thead>"
        print "                 </table>"
        print "                 <div class='container-fluid'>"
        print "                    <object style='align:center;'><embed width='700px' height='800px' src='sample.pdf' type='application/pdf'></object>"
        print "                </div>"
        print "              </div>"
        print "          </div>"
        print "<div class='modal-footer'>"
        print "<button type='button' class='btn btn-default' data-dismiss='modal'>Close</button>"
        print "</div>"
        print "</div>"
        print "</div>"
        print "</div>"



	return
# ------------------------------------------------------------------------------------
def listStreamItems(stream):
	items=[]
	if ((type(streamItems).__name__) == "list"):
		return streamItems
	#if hasattr(streamItems,'__len__'):
	#	print "Returning streamItems"
	#	print "<br>~~~~ "
	#	print streamItems
	#	print " ~~~~~<br>"
	#	print type(streamItems).__name__
	#	return streamItems
	else:	
		return
# ------------------------------------------------------------------------------------
def getstreamNameswithPrefix (assetrefPrefix):
	sList=[]
    	streamsList = rpc_connection.liststreams()	
	for i in range(0,len(streamsList)):
	  #print streamsList[i]['name']
	  searchString = "_"+assetrefPrefix
	  if (searchString in streamsList[i]['name'] ):
	      sList.append(streamsList[i]['name'])
	return sList
# ------------------------------------------------------------------------------------
def getBalanceAsset(address,assetref):
    	assetList = rpc_connection.getaddressbalances(address)	
	for i in range(0,len(assetList)):
	  if (assetList[i]['assetref']==assetref):
		return str(float(assetList[i]['qty']))
	  else:
		#print "No Show"
		return "0"


def displayIcon(address,assetref):
    	assetList = rpc_connection.getaddressbalances(address)	
	found=0
	for i in range(0,len(assetList)):
	  if (assetList[i]['assetref']==assetref):
		found = 1
		continue
	  else:
		pass
	return found
def getSignatories(chainAddress,assetref):
        conn = sqlite3.connect('/opt/mchain/tf/tfData.db')
        c = conn.cursor()
        c.execute ("select status1,status2,status3 from multisigParties where multisigAddress=? and assetref=?", (chainAddress,assetref,))
	for row in c:
                return row
def fetchNamefromTFA(chainAddress):
        conn = sqlite3.connect('/opt/mchain/tf/tfData.db')
        c = conn.cursor()
        c.execute ("select name from tfAddresses where chainAddress=?", (chainAddress,))
	for row in c:
                return row[0]

#def sendassetfrom(address1,address2,assetname,qty):
#        address1 = "" 
#        address2 = "" 
#        assetname = ""  
#        qty = "" 
#        #send_assetfrom = rpc_connection.sendassetfrom(address1,address2,{assetname:int(qty)})
#        send_assetfrom = rpc_connection.sendassetfrom(address1,address2,assetname,int(qty))
#  	print send_assetfrom
def getAssetsInTransit():
	return
def displayTransitStatus():
	print "<div id='chain' class='tableChainFull'>"
	print "<table class='table table-bordered table-condensed'>"
        print "<thead class='defaultHeading'>"  
        print "<tr>"
        print "        <th>Sl No</th>"
        print "        <th>Goods:Quantity</th>"
        print "        <th>Goods Reference</th>"
        print "        <th>Exporter</th>"
        print "        <th><img src='images/delivery-truck2.png' class='downloadImage'/></th>"
        print "        <th>Port &nbsp;<img src='images/port-container2.png' class='downloadImage'/></th>"
        print "        <th><img src='images/ship2.png' class='downloadImage'/></th>"
        print "        <th>Port &nbsp;<img src='images/port-container2.png' class='downloadImage'/></th>"
        print "        <th><img src='images/delivery-truck2.png' class='downloadImage'/></th>"
        print "        <th>Importer</th>"
        print "</tr>"
        print "</thead>"
	print "<tbody>"
	getAssetsInTransit()
	#--------------------------
        conn = sqlite3.connect('/opt/mchain/tf/tfData.db')
        c = conn.cursor()
        c.execute ("select * from assetMovementStatus ")
	#rowid, assetname,assetqty,assetref,exporter,port1,port2,importer
        for row in c:
		rowid = row[0]
		asset = row[1]+":"+row[2]
		assetref = row[3]
		exporter = row[4]
		exportername = fetchNamefromTFA(exporter)
		port1MultiSig = row[5]
		pm1Parties = []
		pm1Parties = getSignatories(port1MultiSig,assetref)
		pm1Addr1, pm1Status1 = pm1Parties[0].split(':')
		pm1Name1 = fetchNamefromTFA(pm1Addr1)
		pm1Addr2, pm1Status2 = pm1Parties[1].split(':')
		pm1Name2 = fetchNamefromTFA(pm1Addr2)
		pm1Addr3, pm1Status3 = pm1Parties[2].split(':')
		pm1Name3 = fetchNamefromTFA(pm1Addr3)
		port2MultiSig = row[6]
		pm2Parties = getSignatories(port2MultiSig,assetref)
		pm2Addr1, pm2Status1 = pm2Parties[0].split(':')
		pm2Name1 = fetchNamefromTFA(pm2Addr1)
		pm2Addr2, pm2Status2 = pm2Parties[1].split(':')
		pm2Name2 = fetchNamefromTFA(pm2Addr2)
		pm2Addr3, pm2Status3 = pm2Parties[2].split(':')
		pm2Name3 = fetchNamefromTFA(pm2Addr3)
		importer = row[7]
		importername = fetchNamefromTFA(importer)

		#print "rowid="+str(rowid)+"exporter = "+exporter+"iporter = "+importer
        	print "		<tr>"
		print "			<td scope='row'>%d</td>" % rowid 
        	print "                  <td>%s</td>" % asset
	        print "                  <td>%s</td>" % assetref
		print "                  <td>"
		#print "<form action='sample.py' method='POST'>"
		#print "<form action='sample1.py' method='POST'>"
		print "<form action='sample1.cgi' method='POST'>"
		print " <label for='exporter'>Exporter</label>"
		#print "<input type='Exporter' name='Exporter' size=10>"
		print "<input type='Submit' value='send'>"
		print "</form>"
		print " </td>"
		print " <td>"
        #	print "                  <a title='%s'>%s" % (exporter,exportername)
	#	show=displayIcon(exporter,assetref)
	#	if (show):
	#		print "<img src='images/placeholder_24.png' class='downloadImage'/>"
	#		show=0
		print "                   </td>"
	#        print " <td>"
        #        print "<button type='button' onclick = 'alert('Hello World!')'>send</button>"
        #        print "</td>"
	        #print "                  <td class='middle'><img src='images/placeholder_24.png' class='downloadImage'/> </td>"
		print "                  <td>"
	        print "                  <a href='#'> %s </a>" % port1MultiSig
		print "			      <br> <b> Signatories</b>"
		if (pm1Status1 == "1"):
			print "			      <br><a title='%s' style='color:green'>%s</a>" % (pm1Addr1,pm1Name1)
		else:
			print "			      <br><a title='%s' style='color:red'>%s</a>" % (pm1Addr1,pm1Name1)
		if (pm1Status2 == "1"):
			print "			      <br><a title='%s' style='color:green'>%s</a>" % (pm1Addr2,pm1Name2)
		else:
			print "			      <br><a title='%s' style='color:red'>%s</a>" % (pm1Addr2,pm1Name2)
		if (pm1Status3 == "1"):
			print "			      <br><a title='%s' style='color:green'>%s</a>" % (pm1Addr3,pm1Name3)
		else:
			print "			      <br><a title='%s' style='color:red'>%s</a><br>" % (pm1Addr3,pm1Name3)
		show=displayIcon(port1MultiSig,assetref)
		if (show):
			print "<img src='images/placeholder_24.png' class='downloadImage'/>"
			show=0
		print "                  </td>"
	        print "                  <td></td>"
	        print "                  <td><a href='#'> %s </a>" % port2MultiSig
		print "			      <br> <b> Signatories</b>"
		if (pm2Status1 == "1"):
			print "			      <br><a title='%s' style='color:green'>%s</a>" % (pm2Addr1,pm2Name1)
		else:
			print "			      <br><a title='%s' style='color:red'>%s</a>" % (pm2Addr1,pm2Name1)
		if (pm2Status2 == "1"):
			print "			      <br><a title='%s' style='color:green'>%s</a>" % (pm2Addr2,pm2Name2)
		else:
			print "			      <br><a title='%s' style='color:red'>%s</a>" % (pm2Addr2,pm2Name2)
		if (pm2Status3 == "1"):
			print "			      <br><a title='%s' style='color:green'>%s</a>" % (pm2Addr3,pm2Name3)
		else:
			print "			      <br><a title='%s' style='color:red'>%s</a>" % (pm2Addr3,pm2Name3)
		show=displayIcon(port2MultiSig,assetref)
		if (show):
			print "<img src='images/placeholder_24.png' class='downloadImage'/>"
		print "                  </td>"
	        print "                  <td></td>"
        	print "                  <td><a title='%s'>%s" % (importer,importername)
		show=displayIcon(importer,assetref)
		if (show):
			print "<img src='images/placeholder_24.png' class='downloadImage'/>"
		print "                  </td>"
        	print "         </tr>"
                continue

	#--------------------------
	if (1==0):
         print "		<tr>"
  	 print "			<td scope='row'>1</td>"
         print "                  <td>Cotton:20000</td>"
         print "                  <td>234-23-22</td>"
         print "                  <td>ABC Exports</td>"
         print "                  <td class='middle'><img src='images/placeholder_24.png' class='downloadImage'/> </td>"
         print "                  <td></td>"
         print "                  <td></td>"
         print "                  <td></td>"
         print "                  <td></td>"
         print "                  <td>VNR imports</td>"
         print "         </tr>"
	#--------------------------

	print "</tbody>"
	print "</table>"
	print "</div>"


def displayBankTransfer():
	print "<div id='chain' class='tableChain'>"
	print "<table class='table table-bordered'>"
	print "  <thead class='defaultHeading'>"
        print "    <tr><td colspan='6'>Transfers</td></tr>"
        print "  </thead>"
        print "  </table>"
        conn = sqlite3.connect('/opt/mchain/tf/tfData.db')
        c = conn.cursor()
        c.execute ("select * from atomicTransfer")
	#rowid, assetref ,exchangeref,currencyref,address1 , address2 );
        for row in c:
		#print row
		rowid=row[0]
		assetref=row[1]	
		xferasset=row[2]
		xchangeCurrencyRef=row[3]
		ExporterBank=row[4]
		exporterBankName=fetchNamefromTFA(ExporterBank)
		exporterBalance=getBalanceAsset(ExporterBank,xchangeCurrencyRef)
		ImporterBank=row[5]
		importerBankName=fetchNamefromTFA(ImporterBank)
		importerBalance=getBalanceAsset(ImporterBank,xchangeCurrencyRef)

        	print "<div class='container' style='padding:10px; height: 90px; border: solid 1px #406f99; background-color:#406f99; text-align:center; font-size:12pt;font-family:calibri;color;#555555;'>"
		print "   <div class='container' style='height: 70px; width:5%; float:left;'>"
		print "   %s" % str(rowid)
		print "   </div>"
		print "   <div class='container' style='height: 70px; width:30%; float:left;'>"
		print "    <a title=%s >%s </a><br/><b>Balance: %s</b>" % (ExporterBank,exporterBankName,exporterBalance)
		print "   </div>"
	        print "   <img src='images/254054.png' class='downloadImage' style='height:70px; width:200px;'/>"
	        print "   <div class='container' style='height: 70px; width:30%; float:right; font-size:12pt;font-family:calibri;color;#555555;'>"
		print "    <a title=%s >%s </a><br/><b>Balance: %s</b>" % (ImporterBank,importerBankName,importerBalance)
		print "   </div>"
        	print "</div>"
        print "</div>"
	return

def displayDocumentStreams():
	global itr
	print "<div id='chain' class='tableChainFull'>"
	print "<table class='table table-bordered'>"
	print "  <thead class='defaultHeading'>"
        print "    <tr><td colspan='6'>Document Streams</td></tr>"
        print "  </thead>"
        print "  <tbody>"
        print "    <tr><td>"
        print "      <div class='container-fluid'><!--Accordion Start-->"
        print "      <div class='panel-group col-lg-12 col-md-12 well ' id='accordion'>"

        #print "      <div class='panel panel-default'><!--2003-->"  ---- uncomment if required
        conn = sqlite3.connect('/opt/mchain/tf/tfData.db')
        c = conn.cursor()
        c.execute ("select distinct assetref from streamSet")
	#rowid, assetref ,streamname
        for row in c:
		itr=0
		assetref=row[0]
	
        	print "         <div class='panel-heading'>"
	        print "            <h4 class='panel-title'>"
        	print "                <a data-toggle='collapse' data-parent='#accordion' href=\"#%s\">GoodsRef:%s</a>" %(assetref,assetref)
	        print "            </h4>"
       		print "         </div>"
	        print "         <div id='%s' class='panel-collapse collapse'>" % (assetref)
	        print "            <div class='panel-body'><!--inner acc start-->"
		##############################################
		print "              <table class='table table-bordered'>"
        	print "              <thead class='defaultHeading'>"
	        print "                 <tr>"
        	print "                 <th>Sl. No</th>"
	        print "                 <th>Document Names</th>"
	        print "                 <th>Time</th>"
        	print "                 <th>Publisher</th>"
	        print "                 <th>Txid</th>"
	        #print "                 <th>Permissions</th>"
	        print "                 </tr>"
	        print "              </thead>"
	        print "              <tbody>"
		# ---------------- fetch list of streams with same name suffix
		streams = getstreamNameswithPrefix (assetref)
		#print "STREAMS = "
		#print streams
	        for stream in streams:
			#print stream
			prefix=stream.split('_')[0]
			suffix=stream.split('_')[1]
			#print "<br>MY displayname = "+stream
			#print "<br>prefix = "+prefix
			#print "<br>suffix = "+suffix
			suffix = suffix.replace("-","_")
			keyList=listStreamItems(stream)
			#print "Keylist for "+stream
			#print keyList
			#print "<br>"
			if (keyList):
				#print keyList[0]['key']
			        for i in range(0,len(keyList)):
				  itr=itr+1
				  publishers=keyList[i]['publishers']
				  keyname=keyList[i]['key']
				  txid=keyList[i]['txid']
				  blocktime=keyList[i]['blocktime']
				  timeStamp = time.strftime('%Y-%m-%d %H:%M:%S', time.localtime(blocktime))
				  metadata=keyList[i]['data'] 
				  keyname=str(keyname)

	  			  print "                  <tr>"
				  print "                  <td scope='row'>%s</td>" % str(itr)
				  #print "                  <td>%s</td>" % keyname
				  print "                  <td>" 
				  keyDisplay=keyname.translate(None, string.punctuation + ' ') 
				  keyDisplay=keyDisplay+"_"+suffix
				  print "<a data-toggle='modal' data-target='#%s'>%s</a>"%(keyDisplay,keyname)
				  displayModalClass(keyDisplay,keyname)
				  print "                  </td>" 
				  #--------------------------------------------------
				  print "                  <td>%s</td>" % timeStamp
				  #print "                  <td>%s</td>" % str(publishers)
				  print "                  <td>"
				  for j in range(0,len(publishers)):
					name = fetchNamefromTFA(publishers[j])
				  	print "<a title='%s'>%s</a><br>" % (publishers[j],name)
				  print "</td>"
				  print "                  <td><a href='#' >%s</a></td>" % txid
				  #print "                  <td><a href='#'>Add</a></td>"
				  print "                  </tr>"
			          #for end
				#iflkeylistend
			#forend
		print "              </tbody>"
        	print "              </table>"
	        print "            </div>"
	        print "        </div>"


        print "      </div>"
        print "      </div>"
        print "      </div>"
        print "    </td></tr>"
	print "  </tbody></table>"
	return
	##############################################


def test():

	print "<div id='chain' class='tableChain'>"
	print "<div class='navHeading'>"
	print "<span class='alignLeft sideHeading'>Streams</span>"
	print "</div>"
	print "<table class='table table-bordered'>"
        print "  <thead class='defaultHeading'>"
        print "  <tr>"
        print "    <th>Sl. No</th>"
        print "    <th>Document Names</th>"
        print "    <th>Time</th>"
        print "    <th>Publisher</th>"
        print "    <th>Viewer</th>"
        print "    <th>Permissions</th>"
        print "  </tr>"
        print "</thead>"
        print "<tbody>"
        print "  <tr>"
        print "    <td scope='row'>1</td>"
        print "    <td>Sales Contract</td>"
        print "    <td>2016-05-05 03:39:11</td>"
        print "    <td>Exporter</td>"
        print "    <td>Importer</td>"
        print "    <td><a href='#'>Add</a></td>"
        print "  </tr>"
	print "</tbody>"
        print "</table>"
	print "</div>"
	return

# ------------------------------------------------------------------------------------
htmlContentType()
htmlHeader("Trade Finance Flow")
sessionTime="12:00:00"
uname="Guest"
divclassNavBar(uname,sessionTime)
divclassPageLocator("")
divclassOuterContainer()
#sendassetfrom(address1,address2,assetname,qty)
displayTransitStatus()
displayBankTransfer()
displayDocumentStreams()
divEnd("outerContainer")
htmlFooter()

# ------------------------------------------------------------------------------------
