package com.mphasis.tradefin.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

public class BinToJsonHexa1
{
    private final static String[] hexSymbols = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

    public final static int BITS_PER_HEX_DIGIT = 4;

    public static String toHexFromByte(final byte b)
    {
        byte leftSymbol = (byte)((b >>> BITS_PER_HEX_DIGIT) & 0x0f);
        byte rightSymbol = (byte)(b & 0x0f);

        return (hexSymbols[leftSymbol] + hexSymbols[rightSymbol]);
    }

    public static String toHexFromBytes(final byte[] bytes)
    {
        if(bytes == null || bytes.length == 0)
        {
            return ("");
        }
              
        // there are 2 hex digits per byte
        StringBuilder hexBuffer = new StringBuilder(bytes.length * 2);

        // for each byte, convert it to hex and append it to the buffer
        for(int i = 0; i < bytes.length; i++)
        {
            hexBuffer.append(toHexFromByte(bytes[i]));
        }
        
        return (hexBuffer.toString());
    }

    public static String CommercialInvoiceJsonHexa(final byte[] bytes)
    {
        if(bytes == null || bytes.length == 0)
        {
            return ("");
        }
        String fileHexdata = toHexFromBytes(bytes);
        
    	//String fileJson = "{\n\"FileName\":\"Commerical_Invoice\",\"FileType\":\"pdf\",\"Data\":"+fileHexdata+"\"}";
    	String fileJson = "\n{\n\"FileName\":\"Commerical_Invoice\",\n\"FileType\":\"pdf\",\n\"Data\":"+fileHexdata+"\n}";
    	System.out.println("JSON File: "+fileJson);
    	// there are 2 hex digits per byte
        /*StringBuilder hexBuffer = new StringBuilder(bytes.length * 2);
        // for each byte, convert it to hex and append it to the buffer
        for(int i = 0; i < bytes.length; i++)
        {
            hexBuffer.append(toHexFromByte(bytes[i]));
        }*/
    	byte[] bytesJson = fileJson.getBytes();
    	String fileHexdata1 = toHexFromBytes(bytesJson);
        
        return fileHexdata1;
    }
    
    //convert file -> bytes[] -> hex => publish
    public String finalCommercialInvoiceJsonHexa(String path) throws IOException    
    {   
    	String finalHexa = null;
        try
        {
        	//File inputPdfFile = new File("3Commercial_Invoice.pdf");
        	//File inputPdfFile = new File("files/3Commerical_Invoice.pdf");
        	
        	File inputPdfFile = new File(path);
            FileInputStream fis = new FileInputStream(inputPdfFile);            
            File intermedHexFile = new File("/home/node4/CI_Hexa");
            BufferedWriter fos = new BufferedWriter(new FileWriter(intermedHexFile));            
            StringBuilder strBldr = new StringBuilder();            
            //BufferedWriter fos = new BufferedWriter(new FileWriter(new File("files/CI_Hexa")));

            byte[] bytes = new byte[1024];
            int value = 0;
            do
            {
                value = fis.read(bytes);
                fos.write(toHexFromBytes(bytes));
                strBldr.append(toHexFromBytes(bytes));
            }while(value != -1);
            
            finalHexa = strBldr.toString(); //CommercialInvoiceJsonHexa(strBldr.toString().getBytes());            
            fos.flush();
            fos.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return finalHexa;
    }
    
    public static String packagingListJsonHexa(final byte[] bytes)
    {
        if(bytes == null || bytes.length == 0)
        {
            return ("");
        }
        String fileHexdata = toHexFromBytes(bytes);
        
    	//String fileJson = "{\n\"FileName\":\"Commerical_Invoice\",\"FileType\":\"pdf\",\"Data\":"+fileHexdata+"\"}";
    	String fileJson = "\n{\n\"FileName\":\"Packaging_List\",\n\"FileType\":\"pdf\",\n\"Data\":"+fileHexdata+"\n}";
    	System.out.println("JSON File: "+fileJson);
    	
        // there are 2 hex digits per byte
        /*StringBuilder hexBuffer = new StringBuilder(bytes.length * 2);

        // for each byte, convert it to hex and append it to the buffer
        for(int i = 0; i < bytes.length; i++)
        {
            hexBuffer.append(toHexFromByte(bytes[i]));
        }
        
        return (hexBuffer.toString());*/
    	byte[] bytesJson = fileJson.getBytes();
    	String fileHexdata1 = toHexFromBytes(bytesJson);
        
        return fileHexdata1;
    }
    
    public String finalPackagingListJsonHexa(String path1) throws IOException    
    {   
    	String finalHexa = null;
        try
        {
        	//File inputPdfFile = new File("/home/node3/001/files/4Packaging_List.pdf");
        	//File inputPdfFile = new File("files/4Packaging_List.pdf");
        	File inputPdfFile = new File(path1);
            FileInputStream fis = new FileInputStream(inputPdfFile);
            
            BufferedWriter fos = new BufferedWriter(new FileWriter(new File("/home/node4/PkgList_Hexa")));
            
            StringBuilder strBldr = new StringBuilder();
            
            //BufferedWriter fos = new BufferedWriter(new FileWriter(new File("files/CI_Hexa")));

            byte[] bytes = new byte[1024];
            int value = 0;
            do
            {
                value = fis.read(bytes);
                fos.write(toHexFromBytes(bytes));
                strBldr.append(toHexFromBytes(bytes));
            }while(value != -1);
            
            finalHexa = strBldr.toString(); //packagingListJsonHexa(strBldr.toString().getBytes());
            
            fos.flush();
            fos.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return finalHexa;
    }

    public static String insuranceJsonHexa(final byte[] bytes)
    {
        if(bytes == null || bytes.length == 0)
        {
            return ("");
        }
        String fileHexdata = toHexFromBytes(bytes);
        
    	//String fileJson = "{\n\"FileName\":\"Commerical_Invoice\",\"FileType\":\"pdf\",\"Data\":"+fileHexdata+"\"}";
    	String fileJson = "\n{\n\"FileName\":\"Insurance\",\n\"FileType\":\"pdf\",\n\"Data\":"+fileHexdata+"\n}";
    	System.out.println("JSON File: "+fileJson);
    	
        // there are 2 hex digits per byte
        /*StringBuilder hexBuffer = new StringBuilder(bytes.length * 2);

        // for each byte, convert it to hex and append it to the buffer
        for(int i = 0; i < bytes.length; i++)
        {
            hexBuffer.append(toHexFromByte(bytes[i]));
        }
        
        return (hexBuffer.toString());*/
    	byte[] bytesJson = fileJson.getBytes();
    	String fileHexdata1 = toHexFromBytes(bytesJson);
        
        return fileHexdata1;
    }
    
    public String finalInsuranceJsonHexa(String path2) throws IOException    
    {   
    	String finalHexa = null;
        try
        {
        	//File inputPdfFile = new File("/home/node3/001/files/5Insurance.pdf");
        	//File inputPdfFile = new File("files/5Insurance.pdf");
        	File inputPdfFile = new File(path2);
            FileInputStream fis = new FileInputStream(inputPdfFile);
            
            BufferedWriter fos = new BufferedWriter(new FileWriter(new File("/home/node4/insurance_Hexa")));
            
            StringBuilder strBldr = new StringBuilder();
            
            //BufferedWriter fos = new BufferedWriter(new FileWriter(new File("files/CI_Hexa")));

            byte[] bytes = new byte[1024];
            int value = 0;
            do
            {
                value = fis.read(bytes);
                fos.write(toHexFromBytes(bytes));
                strBldr.append(toHexFromBytes(bytes));
            }while(value != -1);
            
            finalHexa = strBldr.toString(); //insuranceJsonHexa(strBldr.toString().getBytes());
            
            fos.flush();
            fos.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return finalHexa;
    }
    
    public static String goodsReceivedJsonHexa(final byte[] bytes)
    {
        if(bytes == null || bytes.length == 0)
        {
            return ("");
        }
        String fileHexdata = toHexFromBytes(bytes);
        
    	//String fileJson = "{\n\"FileName\":\"Commerical_Invoice\",\"FileType\":\"pdf\",\"Data\":"+fileHexdata+"\"}";
    	String fileJson = "\n{\n\"FileName\":\"Goods_Received\",\n\"FileType\":\"pdf\",\n\"Data\":"+fileHexdata+"\n}";
    	System.out.println("JSON File: "+fileJson);
    	
        // there are 2 hex digits per byte
        /*StringBuilder hexBuffer = new StringBuilder(bytes.length * 2);

        // for each byte, convert it to hex and append it to the buffer
        for(int i = 0; i < bytes.length; i++)
        {
            hexBuffer.append(toHexFromByte(bytes[i]));
        }
        
        return (hexBuffer.toString());*/
    	byte[] bytesJson = fileJson.getBytes();
    	String fileHexdata1 = toHexFromBytes(bytesJson);
        
        return fileHexdata1;
    }
    
    public String finalGoodsReceivedJsonHexa(String path) throws IOException    
    {   
    	String finalHexa = null;
        try
        {
        	System.out.println("*********CanonicalPath: " + new File(".").getCanonicalPath());

        	//working...
//        	URL url = getClass().getResource("7Goods_Received.pdf");
//        	File inputPdfFile = new File(url.getPath());
        	
        	//working...
        	//File inputPdfFile = new File("./TradefinNew/Tradefin/src/com/mphasis/tradefin/util/7Goods_Received.pdf");
        	
        	//File inputPdfFile = new File("files/7Goods_Received.pdf");
        	System.out.println("%%%%%%%%%%%%%%PATH from Servlet is: "+path);
        	File inputPdfFile = new File(path);
            FileInputStream fis = new FileInputStream(inputPdfFile);
            
            BufferedWriter fos = new BufferedWriter(new FileWriter(new File("/home/node4/goodsRecd_Hexa")));
            
            StringBuilder strBldr = new StringBuilder();
            
            //BufferedWriter fos = new BufferedWriter(new FileWriter(new File("files/CI_Hexa")));

            byte[] bytes = new byte[1024];
            int value = 0;
            do
            {
                value = fis.read(bytes);
                fos.write(toHexFromBytes(bytes));
                strBldr.append(toHexFromBytes(bytes));
            }while(value != -1);
            
            finalHexa = strBldr.toString(); //goodsReceivedJsonHexa(strBldr.toString().getBytes());
            
            fos.flush();
            fos.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return finalHexa;
    }
    
    public static String billOfLadingJsonHexa(final byte[] bytes)
    {
        if(bytes == null || bytes.length == 0)
        {
            return ("");
        }
        String fileHexdata = toHexFromBytes(bytes);
        
    	//String fileJson = "{\n\"FileName\":\"Commerical_Invoice\",\"FileType\":\"pdf\",\"Data\":"+fileHexdata+"\"}";
    	String fileJson = "\n{\n\"FileName\":\"Bill_of_Lading\",\n\"FileType\":\"pdf\",\n\"Data\":"+fileHexdata+"\n}";
    	System.out.println("JSON File: "+fileJson);
    	
        // there are 2 hex digits per byte
        /*StringBuilder hexBuffer = new StringBuilder(bytes.length * 2);

        // for each byte, convert it to hex and append it to the buffer
        for(int i = 0; i < bytes.length; i++)
        {
            hexBuffer.append(toHexFromByte(bytes[i]));
        }
        
        return (hexBuffer.toString());*/
    	byte[] bytesJson = fileJson.getBytes();
    	String fileHexdata1 = toHexFromBytes(bytesJson);
        
        return fileHexdata1;
    }
    
    public String finalBillOfLadingJsonHexa(String path) throws IOException    
    {   
    	String finalHexa = null;
        try
        {
        	//File inputPdfFile = new File("/home/node3/001/files/8Bill_of_Lading.pdf");
        	//File inputPdfFile = new File("files/8Bill_of_Lading.pdf");
        	File inputPdfFile = new File(path);
            FileInputStream fis = new FileInputStream(inputPdfFile);
            
            BufferedWriter fos = new BufferedWriter(new FileWriter(new File("/home/node4/billOfLading_Hexa")));
            
            StringBuilder strBldr = new StringBuilder();
            
            //BufferedWriter fos = new BufferedWriter(new FileWriter(new File("files/CI_Hexa")));

            byte[] bytes = new byte[1024];
            int value = 0;
            
            do
            {
             value = fis.read(bytes);
             fos.write(toHexFromBytes(bytes));
             strBldr.append(toHexFromBytes(bytes));
            }while(value != -1);
            
            finalHexa = strBldr.toString(); //billOfLadingJsonHexa(strBldr.toString().getBytes());            
            fos.flush();
            fos.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return finalHexa;
    }    
}



