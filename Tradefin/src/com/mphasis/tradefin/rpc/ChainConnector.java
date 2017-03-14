package com.mphasis.tradefin.rpc;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Properties;

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

public class ChainConnector {
	
	public static String ENV_VARIABLE_NAME = "CATALINA_HOME";
	public static String PROP_FILE_NAME = "db.chain.properties";
	
	
	public JSONObject invokeRPC(String id, String method, List<Object> params) {
        DefaultHttpClient httpclient = new DefaultHttpClient();

        JSONObject json = new JSONObject();
        json.put("id", id);
        //json.put("chain_name", chainName);
        json.put("method", method);
        
        if (null != params) {
            JSONArray array = new JSONArray();
            array.addAll(params);
            json.put("params", params);
            
            System.out.println("-------------------request: ");
            System.out.println("#### params.size():"+params.size());
            System.out.println("#### params.toString():"+params.toString());
            //System.out.println("############## params.get(0):"+params.get(0).toString());
            //System.out.println("############## params.get(1):"+params.get(1).toString());
            //System.out.println("############## params.get(2):"+params.get(2).toString());
        }
        
        
        
        JSONObject responseJsonObj = null;
        
        try {
        	
        	//--------------
            Properties prop = new Properties();        
    		//InputStream in = getServletContext().getResourceAsStream("WEB-INF/mysql.properties");
    		
    		String cat = System.getenv(ENV_VARIABLE_NAME);
    		String catalinaConf = cat+"/conf/";
    		
    		System.out.println("#### CATALINA_HOME/conf/<prop_file> : " + catalinaConf + PROP_FILE_NAME);
    		
    		
    		InputStream in = new FileInputStream(catalinaConf + PROP_FILE_NAME);
    		
            if (in ==null){
            	System.out.println("#### Sorry, unable to find properties file");
            }
            
            prop.load(in);
            
            /*String drivers = prop.getProperty("db.drivers");
            String connectionURL = prop.getProperty("db.url");
            String databaseName = prop.getProperty("db.database");
            String username = prop.getProperty("db.userName");
            String password = prop.getProperty("db.password");*/
            
            String strChainIP = prop.getProperty("chain.ip");
            String strChainPort = prop.getProperty("chain.port");
            String strChainUsername = prop.getProperty("chain.username");
            String strChainPassword = prop.getProperty("chain.password");
            //chain.url=http://multichainrpc:A9eETUUgh6b3JbVmX5pFkAgcVYZZkH7GpzA5ucjkFaN3@172.21.80.232:9568
            int iChainPort = Integer.parseInt(strChainPort);
            String ChainURL = "http://"+strChainUsername+":"+strChainPassword+"@"+strChainIP+":"+strChainPort;
            System.out.println("#### ChainURL : " + ChainURL);
            //--------------
            
            //httpclient.getCredentialsProvider().setCredentials(new AuthScope("172.21.80.232", 2906/*tfChain*/), new UsernamePasswordCredentials("multichainrpc", "GA1WySdWGtyRz3VSS4ojy6hFVRmm1nocSC5UnaSPvq6f"));
            //httpclient.getCredentialsProvider().setCredentials(new AuthScope("172.21.80.234", 4780/*trade1*/), new UsernamePasswordCredentials("multichainrpc", "9XCz7GQm2ESaVSshzCL1LHAtvHVSGXVPvYhRnhjtAP5N"));
        	//httpclient.getCredentialsProvider().setCredentials(new AuthScope("172.21.80.232", 9568/*tradefinance*/), new UsernamePasswordCredentials("multichainrpc", "A9eETUUgh6b3JbVmX5pFkAgcVYZZkH7GpzA5ucjkFaN3"));
            httpclient.getCredentialsProvider().setCredentials(new AuthScope(strChainIP, iChainPort), new UsernamePasswordCredentials(strChainUsername, strChainPassword));
            
            StringEntity myEntity = new StringEntity(json.toJSONString());
            System.out.println("#### Request json:" + json.toJSONString());
            //HttpPost httppost = new HttpPost("http://multichainrpc:GA1WySdWGtyRz3VSS4ojy6hFVRmm1nocSC5UnaSPvq6f@172.21.80.232:2906");
            //HttpPost httppost = new HttpPost("http://multichainrpc:9XCz7GQm2ESaVSshzCL1LHAtvHVSGXVPvYhRnhjtAP5N@172.21.80.234:4780");
            HttpPost httppost = new HttpPost(ChainURL);	
            
            httppost.setEntity(myEntity);

            System.out.println("#### Request line: " + httppost.getRequestLine());
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();

            System.out.println("---------response:");
            System.out.println(response.getStatusLine());
            if (entity != null) {
                System.out.println("#### Response content length: " + entity.getContentLength());
                // System.out.println(EntityUtils.toString(entity));
            }
            JSONParser parser = new JSONParser();
            
            //note: dont uncomment below print statement of EntityUtils.toString(entity). bcoz it will throw err java.io.IOException: Attempted read from closed stream
            //System.out.println("EntityUtils.toString(entity): "+EntityUtils.toString(entity));
            
            responseJsonObj = (JSONObject) parser.parse(EntityUtils.toString(entity));
            System.out.println("#### responseJsonObj.toJSONString(): "+responseJsonObj.toJSONString()+"\n-------------------");
            
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (org.json.simple.parser.ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpclient.getConnectionManager().shutdown();
        }
        return responseJsonObj;
    }

}
