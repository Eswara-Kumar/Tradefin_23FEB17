package com.mphasis.tradefin.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TestTableDAO extends TradeDAO {

	//static PreparedStatement preparedStmt = null;

	public void insertTestTable(int id, String name) {
		Connection con = null;
		PreparedStatement preparedStmt = null;
		try {
			con = getConnection();
			con.setAutoCommit(false);
			String insQuery = "insert into test_table(id,name) values(?,?)";
			preparedStmt = con.prepareStatement(insQuery);
			
			preparedStmt.setInt(1, id);
			preparedStmt.setString(2, name);
			preparedStmt.getParameterMetaData();
			preparedStmt.executeUpdate();
			
			System.out.println("Commiting data here: insert()....");
		    con.commit();
		    
			//System.out.println("INSERT Query Result: " + rsI);
			System.out.println("New record Inserted!");
			con.close();
		} catch (SQLException se) {//Handle errors for JDBC
		      
		      // If there is an error then rollback the changes.
		      System.out.println("Rolling back data from insert()....");
			  try{
				 if(con!=null)
		            con.rollback();
		      }catch(SQLException se2){
		         se2.printStackTrace();
		      }//end try
			  se.printStackTrace();
		} finally {
			//finally block used to close resources
		      try{
		         if(preparedStmt!=null)
		        	 preparedStmt.close();
		      }catch(SQLException se2){
		      }// nothing we can do
		      try{
		         if(con!=null)
		            con.close();
		      }catch(SQLException se){
		         se.printStackTrace();
		      }//end finally try
		      System.out.println("***Database Connection Terminated: insert()***");
		   }//end try
		
		}

	public void updateTestTable(int id, String name) {
		Connection con = null;
		PreparedStatement preparedStmt = null;
		// String query = "update test_table set name = ? where id = ?";
		try {
			con = getConnection();
			con.setAutoCommit(false);
			String upQuery = "update test_table set name = ? where id = ?";
			preparedStmt = con.prepareStatement(upQuery);
			preparedStmt.setString(1, name);
			preparedStmt.setInt(2, id);
			// execute the java preparedstatement
			preparedStmt.executeUpdate();
			System.out.println("Commiting data here: update()....");
		    con.commit();
			System.out.println("New record Updated!");
			con.close();
		} catch (SQLException e) {
			 // If there is an error then rollback the changes.
		      System.out.println("Rolling back data from update()....");
			  try{
				 if(con!=null)
		            con.rollback();
		      }catch(SQLException se2){
		         se2.printStackTrace();
		      }//end try
			//System.out.println("Exception occured while updating database...");
			e.printStackTrace();
		} finally {
			
			//finally block used to close resources
		      try{
		         if(preparedStmt!=null)
		        	 preparedStmt.close();
		      }catch(SQLException se2){
		      }// nothing we can do
		      try{
		         if(con!=null)
		            con.close();
		      }catch(SQLException se){
		         se.printStackTrace();
		      }//end finally try
		      System.out.println("***Database Connection Terminated: Update()***");			
		}
	}
	
	public void listTestTable() {
		Connection con = null;
		PreparedStatement preparedStmt = null;
		try {
			con = getConnection();
			//con.setAutoCommit(false);
			String insQuery = "SELECT id, name FROM test_table";
			preparedStmt = con.prepareStatement(insQuery);
			
			//preparedStmt.setInt(1, id);
			//preparedStmt.setString(2, name);
			//preparedStmt.getParameterMetaData();
			//preparedStmt.executeUpdate();
			ResultSet rs = preparedStmt.executeQuery();
			
			//System.out.println("Commiting data here: insert()....");
		    //con.commit();
			
			// Extract data from result set
			while(rs.next()){
				//Retrieve by column name
	            int id  = rs.getInt("id");
	            String name = rs.getString("name");
	            /*String first = rs.getString("first");
	            String last = rs.getString("last");*/

	            //Display values
	            System.out.println("ID: " + id );
	            System.out.println(", NAME: " + name );
	            /*out.println(", First: " + first + "<br>");
	            out.println(", Last: " + last + "<br>");*/
	        }
			
			con.close();
		} catch (SQLException se) {//Handle errors for JDBC
		      
		      // If there is an error then rollback the changes.
		      /*System.out.println("Rolling back data from insert()....");*/
			  /*try{
				 if(con!=null)
		            con.rollback();
		      }catch(SQLException se2){
		         se2.printStackTrace();
		      }*///end try
			  se.printStackTrace();
		} finally {
			//finally block used to close resources
		      try{
		         if(preparedStmt!=null)
		        	 preparedStmt.close();
		      }catch(SQLException se2){
		      }// nothing we can do
		      try{
		         if(con!=null)
		            con.close();
		      }catch(SQLException se){
		         se.printStackTrace();
		      }//end finally try
		      System.out.println("***Database Connection Terminated: insert()***");
		   }//end try
		
		}

	/*public static void main(String[] args) {
		// TODO Auto-generated method stub
		TestTableDAO testDao = new TestTableDAO();
		testDao.insertTestTable(25, "Durga1");
		testDao.updateTestTable(14, "SURYA3");
		testDao.listTestTable();

	}*/

}
