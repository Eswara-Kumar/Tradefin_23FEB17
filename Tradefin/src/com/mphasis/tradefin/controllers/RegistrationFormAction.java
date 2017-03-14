package com.mphasis.tradefin.controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.ProcessBuilder.Redirect;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class RegistrationForm
 */
@WebServlet("/RegistrationFormAction")
public class RegistrationFormAction extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public static String ENV_VARIABLE_NAME = "CATALINA_HOME";
	public static String PYTHON_DATAENTRY_SCRIPT_FILE_NAME = "tradefin1.py"; //"number.py";
	public static String PYTHON_DATAENTRY_SCRIPT_PROCESSLOG_FILE_NAME = "ProcessLog.log"; //"number.py";
	public static String PYTHON_DATAENTRY_SCRIPT_ERRORLOG_FILE_NAME = "ErrorLog.log"; //"number.py";
	
	
public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("text/html");//setting the content type  
	    
		
		PrintWriter pw = response.getWriter();//get the stream to write the data
		//pw.append("Served at: ").append(request.getContextPath());
		
		printHead(pw);
		printProcess(pw);
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
		sb.append("						<span class=\"active\">Registration of Entities, Assets, etc into Multichain, and Database.</span>");
		sb.append("					</div>");
		sb.append("					<div class=\"outerContainer\">");
		sb.append("					<div class=\"container\">");
		//sb.append("						<div class=\"navHeading\">");
		//sb.append("							<span class=\"alignLeft sideHeading\">Consignments</span>");
		//sb.append("						</div>");
		sb.append("						<!-- --------------------------------------------- -->");
		
		pw.println(sb.toString());
		
	}


	protected void printProcess(PrintWriter pw) throws ServletException, IOException {
	//public static void main(String[] args) {
		
		//response.setContentType("text/html");//setting the content type  
	    //PrintWriter pw = response.getWriter();
		
	    
		Process p1;
		
		StringBuilder strBldr1 = new StringBuilder();
		StringBuilder strBldr2 = new StringBuilder();
		
		String strOp1;
		String strOp2;
		
	    
	    try {
			
            String catPath = System.getenv(ENV_VARIABLE_NAME);
    		String strPythonDataentryScriptFile = catPath + "/conf/tradefin_scripts/" + PYTHON_DATAENTRY_SCRIPT_FILE_NAME;// + "?a=1&b=2"; //if you pass querystring for commandline execution, it will not execute.
    		
    		Random randomGenerator = new Random();
    	    int randomInt = randomGenerator.nextInt(1000);

    	    String strPdfFileLOCAppl = catPath + "/conf/tradefin_scripts/1Letter_Of_Credit_Application.pdf";
    		String strPdfFileLOC = catPath + "/conf/tradefin_scripts/2Letter_Of_Credit.pdf";
    		String strPythonDataentryScriptProcesslogFile = catPath + "/conf/tradefin_scripts/" + PYTHON_DATAENTRY_SCRIPT_PROCESSLOG_FILE_NAME;
    		String strPythonDataentryScriptErrorlogFile = catPath + "/conf/tradefin_scripts/" + PYTHON_DATAENTRY_SCRIPT_ERRORLOG_FILE_NAME;
    		
    		/*String strScriptCommand = "python "+strPythonDataentryScriptFile + " -e \"AgarwalSons\" -i \"PKPsons\" -p \"portofMali\" "
    				+ " -P \"PortofSS\" -b \"CitiBank\" -B \"UCOBANK\" -c 11000 -n tennisballs9 -q 10000 "
    				+ " -s tennisballs9_docs -f "+strPdfFileLOCAppl+" -F "+strPdfFileLOC+" -t pdf -N INR";*/
    		
    		String[] strScriptCommand = new String[]{
    				"python"
    				, strPythonDataentryScriptFile 
    				, "-e"
    				, "J.K. Exporter "+randomInt 
    				, "-i"
    				, "T.G. Importer "+randomInt
    				, "-p" 
    				, "Port of Chennai, India - "+randomInt //, "Cosco India Logistics, Chennai - "+randomInt 
    				, "-P"
    				, "Port of Haiphong, Vietnam - "+randomInt //, "Cosco India Logistics, Mumbai - "+randomInt 
    				, "-b" 
    				, "S.B.I., Chennai - "+randomInt 
    				, "-B"
    				, "Bank of Haiphong., Haiphong - "+randomInt //, "S.B.M., Mumbai - "+randomInt 
    				, "-c" 
    				, "12300000"
    				, "-C" 
    				, "14500000"
    				, "-n"
    				, "CHILLIES "+randomInt
    				, "-q"
    				, "45108"
    				, "-s"
    				, "Chillies"+randomInt+"_docs"
    				
    				/*, "-e"
    				, "Agarwal Sons "+randomInt 
    				, "-i"
    				, "PKP Sons "+randomInt
    				, "-p" 
    				, "Port of Mali "+randomInt 
    				, "-P"
    				, "Port of SS "+randomInt 
    				, "-b" 
    				, "Citi Bank "+randomInt 
    				, "-B"
    				, "UCO BANK "+randomInt 
    				, "-c" 
    				, "11000" 
    				, "-n"
    				, "Tennisballs"+randomInt
    				, "-q"
    				, "10000"
    				, "-s"
    				, "Tennisballs"+randomInt+"_docs"*/
    				
    				, "-f"
    				, strPdfFileLOCAppl
    				, "-F"
    				, strPdfFileLOC
    				, "-t"
    				, "pdf"
    				, "-N"
    				, "USD"
    				};
    		
    		List<String> lstScriptCommand = Arrays.asList(strScriptCommand);
    		
    		//System.out.println("<br>###### command as array: " + Arrays.toString(strScriptCommand));
    		//System.out.println("<br>###### command as list lstScriptCommand : " + lstScriptCommand);
    		pw.println("<h1> Registration of Entities, Assets, etc into Blockchain.</h1>");
    		
    		pw.println("<div class=''>");
    		pw.println("<h3> Status: &nbsp;&nbsp;&nbsp;Success. </h3>"
    				+ " <br><h4>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Exporter : Success </h4>"
    				+ " <br><h4>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Importer : Success </h4>"
    				+ " <br><h4>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Ports : Success </h4>"
    				+ " <br><h4>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Banks : Success </h4>"
    				+ " <br><h4>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Tri-party Agreements (Multisigs) : Success </h4>"
    				+ " <br><h4>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Assets : Success </h4>"
    				+ " <br><h4>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </h4>");
	        pw.println("<center>");
	        pw.println("<button class='fileButton' id='fileButton' onclick='document.location.href=\"ConsolidatedDashboard\"'>Go to Dashboard</button> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
	        pw.println("</center>");
	        pw.println("</div>");
	        pw.println("<br><br><br><br> ");
	        
	        pw.println("<script type=\"text/javascript\"> ");
	        pw.println("	function toggle_visibility(id) { ");
			pw.println("	var e = document.getElementById(id); ");
			pw.println("	if(e.style.display == 'block') ");
			pw.println("		e.style.display = 'none'; ");
			pw.println("	else ");
			pw.println("		e.style.display = 'block'; ");
			pw.println("	} ");
			pw.println("</script>");
			pw.println("<center>");
	        pw.println("<button class='fileButton' id='DataEntryScriptLogButton' onclick=\"javascript: toggle_visibility('DataEntryScriptLog');\">Show/Hide Log</button> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
	        pw.println("</center>");
	        pw.println("<br><br>");
	        
	        pw.println(" <div id='DataEntryScriptLog' style='display: none; border: 1px solid grey;'>");
	        pw.println("<br><br>~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Process details~~~~~~~~~~~~~~~~~~~~~~~~~<br><br>");
	        pw.println("<br>Process Executed: <br>" + Arrays.toString(strScriptCommand));
    		//pw.println("<br>###### command as list lstScriptCommand : " + lstScriptCommand);
    		
    		
    		
    		//----------------------------------
    		
    		
    		//p1 = Runtime.getRuntime().exec(strScriptCommand);
    		ProcessBuilder pb = new ProcessBuilder(lstScriptCommand);
    		
    		File output = new File(strPythonDataentryScriptProcesslogFile);
    		File errors = new File(strPythonDataentryScriptErrorlogFile);
    		//pb.redirectOutput(Redirect.INHERIT);
    		//pb.redirectError(Redirect.INHERIT); //or //pb.redirectErrorStream(true); // redirect error stream to output stream
    		pb.redirectOutput(output);
    		pb.redirectError(errors);
    		pw.println("<br><br>Process log file : <br>" + pb.redirectOutput());
    		pw.println("<br><br>Error log file: <br>" + pb.redirectError());
    		
    		
    		p1 = pb.start();
    		
    		p1.waitFor();//returns immediately if the subprocess has already terminated

    		
    		
    		//----------------------------------
    		
    		
	        /*BufferedReader br;
	        br = new BufferedReader(new InputStreamReader(p1.getInputStream()));
	        String s = br.readLine();
	        System.out.println("#### s is "+ (s==null?"null":"notnull"));
	    	while (s != null){
	    		System.out.println("#### s is empty? "+ (s.isEmpty()?"yes":"no"));
	        	pw.println("<br>output-line: "+s+"<br>");
	        	strBldr1.append("<br>output: "+s);
	        	System.out.println("line: "+s);
	        	s = br.readLine();
	        	System.out.println("#### s is"+ (s==null?"null":"notnull"));
	        }
	        strOp1 = strBldr1.toString();
	        	        
	        br = new BufferedReader(new InputStreamReader(p1.getErrorStream()));
	        while ((s = br.readLine()) != null){
	        	strBldr1.append(s+"<br>");
	        	System.out.println(s);
	        }
	        strOp2 = strBldr1.toString();
	        
	        pw.println("<br><br>--------------------<br>output1: " + strOp1);
	        System.out.println("\n\n---------------------\noutput1: " + strOp1);
	        
	        pw.println("<br><br>output err: " + strOp2);
	        System.out.println("output err: " + strOp2);*/
	        
	        
	        
	        
	        //----------------------------------
    		
	        
	        pw.println ("<br><br>exit: " + p1.exitValue());//value 0 indicates normal termination
	        System.out.println ("exit: " + p1.exitValue());
	        
	        p1.destroy();//Process object is forcibly terminated.

	        
	        
		
	        //------------------------------------
	        
	        
	        //System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
	        pw.println("<br><br>~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
	        pw.println("<br><br> Output Log:  <br><br>");
	        //pw.println("<br><br>output log: <br><br>");
	        BufferedReader br1 = new BufferedReader(new FileReader(strPythonDataentryScriptErrorlogFile));
	        for (String line; (line = br1.readLine()) != null;) {
	            //System.out.print(line);
	            pw.println(line+"<br>");
	            
	        }
	        
	        //System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
	        pw.println("<br><br>~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
	        pw.println("<br><br>output prints: <br><br>");
	        BufferedReader br2 = new BufferedReader(new FileReader(strPythonDataentryScriptProcesslogFile));
	        for (String line; (line = br2.readLine()) != null;) {
	            //System.out.print(line);
	            pw.println(line+"<br>");
	        }
	        pw.println("</div>");
	        
	    
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	    
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
