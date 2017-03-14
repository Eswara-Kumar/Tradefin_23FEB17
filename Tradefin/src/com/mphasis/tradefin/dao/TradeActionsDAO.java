package com.mphasis.tradefin.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.mphasis.tradefin.beans.Assets;

public class TradeActionsDAO extends TradeDAO {

	private static String selAssetMovement = " SELECT AME.ID, AME.ASSET_ID, AME.ENTITY_ID, AME.SEQ_NO, AME.SEND_AS_MULTISIG, AME.MULTISIG_ID, AME.RAW_TX_HEX, AME.SENT_TX_ID, " +
			" E.NAME ENTITY_NAME, E.CHAIN_ADDRESS, E.PUBLIC_KEY, E.ENTITY_TYPE_ID, " +
			" A.NAME ASSET_NAME, A.QUANTITY, A.ASSET_REF, " +
			" (SELECT M1.MULTISIG_ADDR FROM MULTISIGS M1 WHERE AME.MULTISIG_ID=M1.ID) MULTISIG_ADDR, " +
			" (SELECT M1.REDEEM_SCRIPT FROM MULTISIGS M1 WHERE AME.MULTISIG_ID=M1.ID) MULTISIG_REDEEM_SCRIPT, " +
			" (SELECT EM.SIGNED FROM ENTITIES_MULTISIGS EM WHERE EM.MULTISIG_ID = AME.MULTISIG_ID AND EM.ENTITY_ID = AME.ENTITY_ID) SIGNED, " +
			" (SELECT EM.SIGN_OUTPUT_HEX FROM ENTITIES_MULTISIGS EM WHERE EM.MULTISIG_ID = AME.MULTISIG_ID AND EM.ENTITY_ID = AME.ENTITY_ID) SIGN_OUTPUT_HEX, " +
			" (SELECT EM.SIGNED_DATE FROM ENTITIES_MULTISIGS EM WHERE EM.MULTISIG_ID = AME.MULTISIG_ID AND EM.ENTITY_ID = AME.ENTITY_ID) SIGNED_DATE, " +
			" (SELECT EM.LAST_UPDATED_DATE FROM ENTITIES_MULTISIGS EM WHERE EM.MULTISIG_ID = AME.MULTISIG_ID AND EM.ENTITY_ID = AME.ENTITY_ID) LAST_UPDATED_DATE " +
			" ";
	
	private static String fromAssetMovement = " FROM " +
			"   ASSET_MOVEMENT_ENTITIES AME, " +
			"   ENTITIES E, " +
			"   ASSETS A " +
			" ";
	
	private static String whereAssetMovement = " WHERE " + 
			"   A.ID = AME.ASSET_ID " +
			"   AND E.ID = AME.ENTITY_ID " +
			" 	AND AME.ASSET_ID = ? " + 
			" ";
	
	private static String whereAssetMovementForAsstEntt = " WHERE " + 
			"   A.ID = AME.ASSET_ID " +
			"   AND E.ID = AME.ENTITY_ID " +
			" 	AND AME.ASSET_ID = ? " + 
			" 	AND AME.ENTITY_ID = ? " + 
			" ";
	
	private static String whereAssetMovementForAsstSeq = " WHERE " + 
			"   A.ID = AME.ASSET_ID " +
			"   AND E.ID = AME.ENTITY_ID " +
			" 	AND AME.ASSET_ID = ? " + 
			" 	AND AME.SEQ_NO = ? " + 
			" ";
	
	private static String orderAssetMovement = " ORDER BY AME.ASSET_ID, AME.SEQ_NO " +
			" ";
	
	
	
	private static String qryAssetMovementForAssetId = selAssetMovement+fromAssetMovement+whereAssetMovement+orderAssetMovement;
	
	private static String qryAssetMovementForAssetIdAndEntityId = selAssetMovement+fromAssetMovement+whereAssetMovementForAsstEntt+orderAssetMovement;
	
	private static String qryAssetMovementForAssetIdAndSeqno = selAssetMovement+fromAssetMovement+whereAssetMovementForAsstSeq+orderAssetMovement;
	
	private static String qryAssetMovementMaxSeqNoForAssetId = "SELECT MAX(AME.SEQ_NO) FROM ASSET_MOVEMENT_ENTITIES AME WHERE AME.ASSET_ID = ?";
	
	private static String qryAtomicTransfersForAssetId = " SELECT ATE.ID, ATE.ASSET_ID, ATE.EXCHANGE_REF, ATE.CURRENCY_REF, ATE.ENTITY_ID_FROM, ATE.ENTITY_ID_TO, ATE.PARTIAL_TRANSFER_TX_ID, ATE.FINAL_TRANSFER_TX_ID, ATE.CREATED_DATE, ATE.LAST_UPDATED_DATE "
			+ ", A.NAME ASSET_NAME, A.QUANTITY, EFROM.NAME FROM_BANK_NAME, EFROM.CHAIN_ADDRESS FROM_BANK_ADDRESS, ETO.NAME TO_BANK_NAME, ETO.CHAIN_ADDRESS TO_BANK_ADDRESS "
			+ " FROM ATOMIC_TRANSFER_ENTITIES ATE, ASSETS A, ENTITIES EFROM, ENTITIES ETO "
			+ " WHERE  "
			+ " ATE.ASSET_ID = A.ID "
			+ " AND ATE.ENTITY_ID_FROM = EFROM.ID "
			+ " AND ATE.ENTITY_ID_TO = ETO.ID "
			+ " AND ATE.ASSET_ID = ?"
			+ " ";
	
	private static String qryStreamsetsForAssetId = " SELECT SS.ID, SS.NAME, SS.ASSET_ID, SS.CREATED_DATE, SS.LAST_UPDATED_DATE FROM STREAM_SETS SS WHERE SS.ASSET_ID = ? "
			+ " ";
	
	private static String qryEntityForAddress = " SELECT E.ID, E.NAME, E.CHAIN_ADDRESS, E.PUBLIC_KEY, E.ENTITY_TYPE_ID, E.CREATED_DATE, E.LAST_UPDATED_DATE FROM ENTITIES E WHERE E.CHAIN_ADDRESS = ? "
			+ " ";
	
	private static String qryUpdateSentTxOfAssetMovement = "UPDATE ASSET_MOVEMENT_ENTITIES SET SENT_TX_ID = ? WHERE ASSET_ID = ? AND ENTITY_ID = ?";
	
	private static String qryUpdateFinalTransferTxOfAtomicTransfers = "UPDATE ATOMIC_TRANSFER_ENTITIES SET FINAL_TRANSFER_TX_ID = ? WHERE ASSET_ID = ?";
	
	private static String qryUpdatePartialTransferTxOfAtomicTransfers = "UPDATE ATOMIC_TRANSFER_ENTITIES SET PARTIAL_TRANSFER_TX_ID = ? WHERE ASSET_ID = ?";
	
	
	
	
	
	public int updateSentTxOfAssetMovement(String strSentTxId/*SENT_TX_ID*/, int iAssetId, int iEntityId) {
		Connection con = null;
		PreparedStatement preparedStmt = null;
		ResultSet rs = null;
		int status = -1;
		
		try {
			
			System.out.println("#### updateSentTxOfAssetMovement-strSentTxId: "+strSentTxId);
			System.out.println("#### updateSentTxOfAssetMovement-iAssetId: "+iAssetId);
			System.out.println("#### updateSentTxOfAssetMovement-iEntityId: "+iEntityId);
			
			con = getConnection();
			con.setAutoCommit(false);
			
			String querySel = qryUpdateSentTxOfAssetMovement;
			preparedStmt = con.prepareStatement(querySel);
			
			preparedStmt.setString(1, strSentTxId);
			preparedStmt.setInt(2, iAssetId);
			preparedStmt.setInt(3, iEntityId);
			//preparedStmt.setString(2, name);
			//preparedStmt.getParameterMetaData();
			//preparedStmt.executeUpdate();
			System.out.println("#### Database Query : " + preparedStmt.toString());
			status = preparedStmt.executeUpdate();
			
			System.out.println("#### updateSentTxOfAssetMovement-status: "+status);
			con.commit();
			
		} catch (SQLException se) {
			  //Handle errors for JDBC
		      se.printStackTrace();
		      System.out.println("Rolling back data from insert()....");
		      try {
		    	  if (con != null)
		    		  con.rollback();
		      } catch (SQLException se2) {
		    	  se2.printStackTrace();
		      }
		} finally {
			  //finally block used to close resources
		      try {
		         if(rs!=null) {
		        	 rs.close();
		        	 System.out.println("***Database Resultset Terminated***");
		         }
		      } catch(SQLException se2) {
		    	  se2.printStackTrace();
		      } 
		      
		      try {
			         if(preparedStmt!=null) {
			        	 preparedStmt.close();
			        	 System.out.println("***Database PreparedStmt Terminated***");
			         }
		      } catch(SQLException se1) {
			  	  se1.printStackTrace();
			  }
		      
		      try {
		         if(con!=null) {
		        	 con.close();
		        	 System.out.println("***Database Connection Terminated***");
		         }
		      } catch(SQLException se) {
		         se.printStackTrace();
		      } 
		
		}
		
		return status;
		
	}
	
	public List<Map> fetchAssetMovement(int iAssetId) {
		Connection con = null;
		PreparedStatement preparedStmt = null;
		ResultSet rs = null;
		List<Map> rows = new ArrayList<Map>();
		
		try {
			con = getConnection();

			String querySel = qryAssetMovementForAssetId;
			preparedStmt = con.prepareStatement(querySel);
			
			preparedStmt.setInt(1, iAssetId);
			//preparedStmt.setString(2, name);
			//preparedStmt.getParameterMetaData();
			//preparedStmt.executeUpdate();
			System.out.println("#### Database Query : " + preparedStmt.toString());
			rs = preparedStmt.executeQuery();
			
			ResultSetMetaData rsmd = rs.getMetaData();
			int numColumns = rsmd.getColumnCount();
			
			while (rs.next())
	        {
	            Map<String, Object> row = new LinkedHashMap<String, Object>();
	            for (int i = 0; i < numColumns; ++i)
	            {
	                String column = rsmd.getColumnLabel(i+1);
	                Object value = rs.getObject(i+1);
	                //System.out.println("############"+column+":"+value);
	                row.put(column, value);
	            }
	            rows.add(row);
	        }
			
		} catch (SQLException se) {
			  //Handle errors for JDBC
		      se.printStackTrace();
		} finally {
			  //finally block used to close resources
		      try {
		         if(rs!=null) {
		        	 rs.close();
		        	 System.out.println("***Database Resultset Terminated***");
		         }
		      } catch(SQLException se2) {
		    	  se2.printStackTrace();
		      } 
		      
		      try {
			         if(preparedStmt!=null) {
			        	 preparedStmt.close();
			        	 System.out.println("***Database PreparedStmt Terminated***");
			         }
		      } catch(SQLException se1) {
			  	  se1.printStackTrace();
			  }
		      
		      try {
		         if(con!=null) {
		        	 con.close();
		        	 System.out.println("***Database Connection Terminated***");
		         }
		      } catch(SQLException se) {
		         se.printStackTrace();
		      } 
		
		}
		
		return rows;
		
	}
	
	public List<Map> fetchAssetMovement(int iAssetId, int iEntityId) {
		Connection con = null;
		PreparedStatement preparedStmt = null;
		ResultSet rs = null;
		List<Map> rows = new ArrayList<Map>();
		
		try {
			con = getConnection();

			String querySel = qryAssetMovementForAssetIdAndEntityId;
			preparedStmt = con.prepareStatement(querySel);
			
			preparedStmt.setInt(1, iAssetId);
			preparedStmt.setInt(2, iEntityId);
			//preparedStmt.setString(2, name);
			//preparedStmt.getParameterMetaData();
			//preparedStmt.executeUpdate();
			System.out.println("#### Database Query : " + preparedStmt.toString());
			rs = preparedStmt.executeQuery();
			
			ResultSetMetaData rsmd = rs.getMetaData();
			int numColumns = rsmd.getColumnCount();
			
			while (rs.next())
	        {
	            Map<String, Object> row = new LinkedHashMap<String, Object>();
	            for (int i = 0; i < numColumns; ++i)
	            {
	                String column = rsmd.getColumnLabel(i+1);
	                Object value = rs.getObject(i+1);
	                //System.out.println("############"+column+":"+value);
	                row.put(column, value);
	            }
	            rows.add(row);
	        }
			
		} catch (SQLException se) {
			  //Handle errors for JDBC
		      se.printStackTrace();
		} finally {
			  //finally block used to close resources
		      try {
		         if(rs!=null) {
		        	 rs.close();
		        	 System.out.println("***Database Resultset Terminated***");
		         }
		      } catch(SQLException se2) {
		    	  se2.printStackTrace();
		      } 
		      
		      try {
			         if(preparedStmt!=null) {
			        	 preparedStmt.close();
			        	 System.out.println("***Database PreparedStmt Terminated***");
			         }
		      } catch(SQLException se1) {
			  	  se1.printStackTrace();
			  }
		      
		      try {
		         if(con!=null) {
		        	 con.close();
		        	 System.out.println("***Database Connection Terminated***");
		         }
		      } catch(SQLException se) {
		         se.printStackTrace();
		      } 
		
		}
		
		return rows;
		
	}
	
	public List<Map> fetchAssetMovementForAstSeq(int iAssetId, int iSeqNo) {
		Connection con = null;
		PreparedStatement preparedStmt = null;
		ResultSet rs = null;
		List<Map> rows = new ArrayList<Map>();
		
		try {
			con = getConnection();

			String querySel = qryAssetMovementForAssetIdAndSeqno;
			preparedStmt = con.prepareStatement(querySel);
			
			preparedStmt.setInt(1, iAssetId);
			preparedStmt.setInt(2, iSeqNo);
			//preparedStmt.setString(2, name);
			//preparedStmt.getParameterMetaData();
			//preparedStmt.executeUpdate();
			System.out.println("#### Database Query : " + preparedStmt.toString());
			rs = preparedStmt.executeQuery();
			
			ResultSetMetaData rsmd = rs.getMetaData();
			int numColumns = rsmd.getColumnCount();
			
			while (rs.next())
	        {
	            Map<String, Object> row = new LinkedHashMap<String, Object>();
	            for (int i = 0; i < numColumns; ++i)
	            {
	                String column = rsmd.getColumnLabel(i+1);
	                Object value = rs.getObject(i+1);
	                //System.out.println("############"+column+":"+value);
	                row.put(column, value);
	            }
	            rows.add(row);
	        }
			
		} catch (SQLException se) {
			  //Handle errors for JDBC
		      se.printStackTrace();
		} finally {
			  //finally block used to close resources
		      try {
		         if(rs!=null) {
		        	 rs.close();
		        	 System.out.println("***Database Resultset Terminated***");
		         }
		      } catch(SQLException se2) {
		    	  se2.printStackTrace();
		      } 
		      
		      try {
			         if(preparedStmt!=null) {
			        	 preparedStmt.close();
			        	 System.out.println("***Database PreparedStmt Terminated***");
			         }
		      } catch(SQLException se1) {
			  	  se1.printStackTrace();
			  }
		      
		      try {
		         if(con!=null) {
		        	 con.close();
		        	 System.out.println("***Database Connection Terminated***");
		         }
		      } catch(SQLException se) {
		         se.printStackTrace();
		      } 
		
		}
		
		return rows;
		
	}
	
	public int fetchAssetMovementMaxSeqNo(int iAssetId) {
		Connection con = null;
		PreparedStatement preparedStmt = null;
		ResultSet rs = null;
		int value = -1;
		
		try {
			con = getConnection();

			String querySel = qryAssetMovementMaxSeqNoForAssetId;
			preparedStmt = con.prepareStatement(querySel);
			
			preparedStmt.setInt(1, iAssetId);
			
			System.out.println("#### Database Query : " + preparedStmt.toString());
			rs = preparedStmt.executeQuery();
			
			if(rs.first()) {
				value = rs.getInt(1);
			}
			
		} catch (SQLException se) {
			  //Handle errors for JDBC
		      se.printStackTrace();
		} finally {
			  //finally block used to close resources
		      try {
		         if(rs!=null) {
		        	 rs.close();
		        	 System.out.println("***Database Resultset Terminated***");
		         }
		      } catch(SQLException se2) {
		    	  se2.printStackTrace();
		      } 
		      
		      try {
			         if(preparedStmt!=null) {
			        	 preparedStmt.close();
			        	 System.out.println("***Database PreparedStmt Terminated***");
			         }
		      } catch(SQLException se1) {
			  	  se1.printStackTrace();
			  }
		      
		      try {
		         if(con!=null) {
		        	 con.close();
		        	 System.out.println("***Database Connection Terminated***");
		         }
		      } catch(SQLException se) {
		         se.printStackTrace();
		      } 
		
		}
		
		return value;
		
	}
	
	public List<Map> fetchAtomicTransfersForAsset(int iAssetId) {
		Connection con = null;
		PreparedStatement preparedStmt = null;
		ResultSet rs = null;
		List<Map> rows = new ArrayList<Map>();
		
		try {
			con = getConnection();

			String querySel = qryAtomicTransfersForAssetId;
			preparedStmt = con.prepareStatement(querySel);
			
			preparedStmt.setInt(1, iAssetId);
			//preparedStmt.setString(2, name);
			//preparedStmt.getParameterMetaData();
			//preparedStmt.executeUpdate();
			System.out.println("#### Database Query : " + preparedStmt.toString());
			rs = preparedStmt.executeQuery();
			
			ResultSetMetaData rsmd = rs.getMetaData();
			int numColumns = rsmd.getColumnCount();
			
			while (rs.next())
	        {
	            Map<String, Object> row = new LinkedHashMap<String, Object>();
	            for (int i = 0; i < numColumns; ++i)
	            {
	                String column = rsmd.getColumnLabel(i+1);
	                Object value = rs.getObject(i+1);
	                //System.out.println("############"+column+":"+value);
	                row.put(column, value);
	            }
	            rows.add(row);
	        }
			
		} catch (SQLException se) {
			  //Handle errors for JDBC
		      se.printStackTrace();
		} finally {
			  //finally block used to close resources
		      try {
		         if(rs!=null) {
		        	 rs.close();
		        	 System.out.println("***Database Resultset Terminated***");
		         }
		      } catch(SQLException se2) {
		    	  se2.printStackTrace();
		      } 
		      
		      try {
			         if(preparedStmt!=null) {
			        	 preparedStmt.close();
			        	 System.out.println("***Database PreparedStmt Terminated***");
			         }
		      } catch(SQLException se1) {
			  	  se1.printStackTrace();
			  }
		      
		      try {
		         if(con!=null) {
		        	 con.close();
		        	 System.out.println("***Database Connection Terminated***");
		         }
		      } catch(SQLException se) {
		         se.printStackTrace();
		      } 
		
		}
		
		return rows;
		
	}
	
	public int updateFinalTransferTxOfAtomicTransfers(String strFinalTransferTxId/*FINAL_TRANSFER_TX_ID*/, int iAssetId) {
		Connection con = null;
		PreparedStatement preparedStmt = null;
		ResultSet rs = null;
		int status = -1;
		
		try {
			
			System.out.println("#### updateFinalTransferTxOfAtomicTransfers-strFinalTransferTxId: "+strFinalTransferTxId);
			System.out.println("#### updateFinalTransferTxOfAtomicTransfers-iAssetId: "+iAssetId);
			
			
			con = getConnection();
			con.setAutoCommit(false);
			
			String querySel = qryUpdateFinalTransferTxOfAtomicTransfers;
			preparedStmt = con.prepareStatement(querySel);
			
			preparedStmt.setString(1, strFinalTransferTxId);
			preparedStmt.setInt(2, iAssetId);
			//preparedStmt.setString(2, name);
			//preparedStmt.getParameterMetaData();
			//preparedStmt.executeUpdate();
			System.out.println("#### Database Query : " + preparedStmt.toString());
			status = preparedStmt.executeUpdate();
			
			System.out.println("#### updateFinalTransferTxOfAtomicTransfers-status: "+status);
			con.commit();
			
		} catch (SQLException se) {
			  //Handle errors for JDBC
		      se.printStackTrace();
		      System.out.println("Rolling back data from insert()....");
		      try {
		    	  if (con != null)
		    		  con.rollback();
		      } catch (SQLException se2) {
		    	  se2.printStackTrace();
		      }
		} finally {
			  //finally block used to close resources
		      try {
		         if(rs!=null) {
		        	 rs.close();
		        	 System.out.println("***Database Resultset Terminated***");
		         }
		      } catch(SQLException se2) {
		    	  se2.printStackTrace();
		      } 
		      
		      try {
			         if(preparedStmt!=null) {
			        	 preparedStmt.close();
			        	 System.out.println("***Database PreparedStmt Terminated***");
			         }
		      } catch(SQLException se1) {
			  	  se1.printStackTrace();
			  }
		      
		      try {
		         if(con!=null) {
		        	 con.close();
		        	 System.out.println("***Database Connection Terminated***");
		         }
		      } catch(SQLException se) {
		         se.printStackTrace();
		      } 
		
		}
		
		return status;
		
	}
	
	public int updatePartialTransferTxOfAtomicTransfers(String strPartialTransferTxId/*PARTIAL_TRANSFER_TX_ID*/, int iAssetId) {
		Connection con = null;
		PreparedStatement preparedStmt = null;
		ResultSet rs = null;
		int status = -1;
		
		try {
			
			System.out.println("#### updateFinalTransferTxOfAtomicTransfers-strPartialTransferTxId: "+strPartialTransferTxId);
			System.out.println("#### updateFinalTransferTxOfAtomicTransfers-iAssetId: "+iAssetId);
			
			
			con = getConnection();
			con.setAutoCommit(false);
			
			String querySel = qryUpdatePartialTransferTxOfAtomicTransfers;
			preparedStmt = con.prepareStatement(querySel);
			
			preparedStmt.setString(1, strPartialTransferTxId);
			preparedStmt.setInt(2, iAssetId);
			//preparedStmt.setString(2, name);
			//preparedStmt.getParameterMetaData();
			//preparedStmt.executeUpdate();
			System.out.println("#### Database Query : " + preparedStmt.toString());
			status = preparedStmt.executeUpdate();
			
			System.out.println("#### updatePartialTransferTxOfAtomicTransfers-status: "+status);
			con.commit();
			
		} catch (SQLException se) {
			  //Handle errors for JDBC
		      se.printStackTrace();
		      System.out.println("Rolling back data from insert()....");
		      try {
		    	  if (con != null)
		    		  con.rollback();
		      } catch (SQLException se2) {
		    	  se2.printStackTrace();
		      }
		} finally {
			  //finally block used to close resources
		      try {
		         if(rs!=null) {
		        	 rs.close();
		        	 System.out.println("***Database Resultset Terminated***");
		         }
		      } catch(SQLException se2) {
		    	  se2.printStackTrace();
		      } 
		      
		      try {
			         if(preparedStmt!=null) {
			        	 preparedStmt.close();
			        	 System.out.println("***Database PreparedStmt Terminated***");
			         }
		      } catch(SQLException se1) {
			  	  se1.printStackTrace();
			  }
		      
		      try {
		         if(con!=null) {
		        	 con.close();
		        	 System.out.println("***Database Connection Terminated***");
		         }
		      } catch(SQLException se) {
		         se.printStackTrace();
		      } 
		
		}
		
		return status;
		
	}
	
	public List<Assets> fetchAssets() {
		Connection con = null;
		PreparedStatement preparedStmt = null;
		ResultSet rs = null;
		List<Assets> objListAssets = new ArrayList<Assets>();
		
		try {
			con = getConnection();

			String querySel = "SELECT ID, NAME, QUANTITY, ASSET_REF, ISSUE_TX_ID, ISSUE_RECEIVER_ENTITY_ID, CREATED_DATE, LAST_UPDATED_DATE FROM ASSETS ORDER BY ID ";
			
			preparedStmt = con.prepareStatement(querySel);
			
			//preparedStmt.setInt(1, id);
			//preparedStmt.setString(2, name);
			//preparedStmt.getParameterMetaData();
			//preparedStmt.executeUpdate();
			System.out.println("#### Database Query : " + preparedStmt.toString());
			rs = preparedStmt.executeQuery();
			
			while(rs.next()){
				//Retrieve by column name
				Assets objAssets = new Assets();
	            objAssets.setId(rs.getInt("ID"));
	            objAssets.setStrName(rs.getString("NAME"));
	            objAssets.setDbQuantity(rs.getDouble("QUANTITY"));
	            objAssets.setStrAssetRef(rs.getString("ASSET_REF"));
	            objAssets.setStrIssueTxId(rs.getString("ISSUE_TX_ID"));
	            objAssets.setIntIssueReceiverEntityId(rs.getInt("ISSUE_RECEIVER_ENTITY_ID"));
	            objAssets.setDtCreatedDate(rs.getTimestamp("CREATED_DATE"));
	            objAssets.setDtLastUpdatedDate(rs.getTimestamp("LAST_UPDATED_DATE"));
	            objListAssets.add(objAssets);
	        }
		
		} catch (SQLException se) {
			  //Handle errors for JDBC
		      se.printStackTrace();
		} finally {
			  //finally block used to close resources
		      try {
		         if(rs!=null) {
		        	 rs.close();
		        	 System.out.println("***Database Resultset Terminated***");
		         }
		      } catch(SQLException se2) {
		    	  se2.printStackTrace();
		      } 
		      
		      try {
			         if(preparedStmt!=null) {
			        	 preparedStmt.close();
			        	 System.out.println("***Database PreparedStmt Terminated***");
			         }
		      } catch(SQLException se1) {
			  	  se1.printStackTrace();
			  }
		      
		      try {
		         if(con!=null) {
		        	 con.close();
		        	 System.out.println("***Database Connection Terminated***");
		         }
		      } catch(SQLException se) {
		         se.printStackTrace();
		      } 
		
		}
		
		return objListAssets;
		
	}
	
	
	public List<Map> fetchStreamsetsForAsset(int iAssetId) {
		Connection con = null;
		PreparedStatement preparedStmt = null;
		ResultSet rs = null;
		List<Map> rows = new ArrayList<Map>();
		
		try {
			con = getConnection();

			String querySel = qryStreamsetsForAssetId;
			preparedStmt = con.prepareStatement(querySel);
			
			preparedStmt.setInt(1, iAssetId);
			//preparedStmt.setString(2, name);
			//preparedStmt.getParameterMetaData();
			//preparedStmt.executeUpdate();
			System.out.println("#### Database Query : " + preparedStmt.toString());
			rs = preparedStmt.executeQuery();
			
			ResultSetMetaData rsmd = rs.getMetaData();
			int numColumns = rsmd.getColumnCount();
			
			while (rs.next())
	        {
	            Map<String, Object> row = new LinkedHashMap<String, Object>();
	            for (int i = 0; i < numColumns; ++i)
	            {
	                String column = rsmd.getColumnLabel(i+1);
	                Object value = rs.getObject(i+1);
	                //System.out.println("############"+column+":"+value);
	                row.put(column, value);
	            }
	            rows.add(row);
	        }
			
		} catch (SQLException se) {
			  //Handle errors for JDBC
		      se.printStackTrace();
		} finally {
			  //finally block used to close resources
		      try {
		         if(rs!=null) {
		        	 rs.close();
		        	 System.out.println("***Database Resultset Terminated***");
		         }
		      } catch(SQLException se2) {
		    	  se2.printStackTrace();
		      } 
		      
		      try {
			         if(preparedStmt!=null) {
			        	 preparedStmt.close();
			        	 System.out.println("***Database PreparedStmt Terminated***");
			         }
		      } catch(SQLException se1) {
			  	  se1.printStackTrace();
			  }
		      
		      try {
		         if(con!=null) {
		        	 con.close();
		        	 System.out.println("***Database Connection Terminated***");
		         }
		      } catch(SQLException se) {
		         se.printStackTrace();
		      } 
		
		}
		
		return rows;
		
	}

	
	public Map fetchEntityForAddress(String strAddress) {
		Connection con = null;
		PreparedStatement preparedStmt = null;
		ResultSet rs = null;
		//List<Map> rows = new ArrayList<Map>();
		Map<String, Object> row = null;
		
		try {
			con = getConnection();

			String querySel = qryEntityForAddress;
			preparedStmt = con.prepareStatement(querySel);
			
			preparedStmt.setString(1, strAddress);
			//preparedStmt.setString(2, name);
			//preparedStmt.getParameterMetaData();
			//preparedStmt.executeUpdate();
			System.out.println("#### Database Query : " + preparedStmt.toString());
			rs = preparedStmt.executeQuery();
			
			ResultSetMetaData rsmd = rs.getMetaData();
			int numColumns = rsmd.getColumnCount();
			
			if(rs.first()) {
				row = new LinkedHashMap<String, Object>();
	            for (int i = 0; i < numColumns; ++i)
	            {
	                String column = rsmd.getColumnLabel(i+1);
	                Object value = rs.getObject(i+1);
	                //System.out.println("############"+column+":"+value);
	                row.put(column, value);
	            }
	            //rows.add(row);
			}
			
			/*while (rs.next())
	        {
	            Map<String, Object> row = new LinkedHashMap<String, Object>();
	            for (int i = 0; i < numColumns; ++i)
	            {
	                String column = rsmd.getColumnLabel(i+1);
	                Object value = rs.getObject(i+1);
	                //System.out.println("############"+column+":"+value);
	                row.put(column, value);
	            }
	            rows.add(row);
	        }*/
			
		} catch (SQLException se) {
			  //Handle errors for JDBC
		      se.printStackTrace();
		} finally {
			  //finally block used to close resources
		      try {
		         if(rs!=null) {
		        	 rs.close();
		        	 System.out.println("***Database Resultset Terminated***");
		         }
		      } catch(SQLException se2) {
		    	  se2.printStackTrace();
		      } 
		      
		      try {
			         if(preparedStmt!=null) {
			        	 preparedStmt.close();
			        	 System.out.println("***Database PreparedStmt Terminated***");
			         }
		      } catch(SQLException se1) {
			  	  se1.printStackTrace();
			  }
		      
		      try {
		         if(con!=null) {
		        	 con.close();
		        	 System.out.println("***Database Connection Terminated***");
		         }
		      } catch(SQLException se) {
		         se.printStackTrace();
		      } 
		}
		
		return row;
		
	}
	
}
