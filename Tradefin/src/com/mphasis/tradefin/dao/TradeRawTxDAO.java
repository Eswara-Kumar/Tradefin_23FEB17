package com.mphasis.tradefin.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.mphasis.tradefin.beans.CreateRawTransactionPojo;
import com.mphasis.tradefin.beans.SignRawTransactionPojo;

public class TradeRawTxDAO extends TradeDAO {
	
	
	private static String selMultisigsEntities = " SELECT EM.ENTITY_ID, EM.MULTISIG_ID, EM.SIGNED, EM.SIGN_OUTPUT_HEX, EM.SIGNED_DATE, EM.CREATED_DATE, EM.LAST_UPDATED_DATE, E.NAME " +
			" ";
	
	private static String fromMultisigsEntities = " 	FROM ENTITIES_MULTISIGS EM,"
			+ " ENTITIES E " +
			" ";
	
	private static String whereMultisigsEntities = " WHERE EM.ENTITY_ID = E.ID "
			+ " AND EM.MULTISIG_ID = ? " + 
			" ";
	
	private static String whereMultisigsEntitiesSigned = " WHERE EM.ENTITY_ID = E.ID "
			+ " AND EM.MULTISIG_ID = ? "
			+ " AND EM.SIGNED = 1 " +
			" ";
	
	private static String orderMultisigsEntities = " ORDER BY MULTISIG_ID " +
			" ";
	
	private static String orderMultisigsEntitiesSigned = " ORDER BY MULTISIG_ID, SIGNED_DATE DESC " +
			" ";
	
	private static String qryMultisigsEntities = selMultisigsEntities + fromMultisigsEntities + whereMultisigsEntities + orderMultisigsEntities;
	
	private static String qryMultisigsEntitiesSigned = selMultisigsEntities + fromMultisigsEntities + whereMultisigsEntitiesSigned + orderMultisigsEntitiesSigned;

	
	public CreateRawTransactionPojo selectForCreateRawTx(int assetId, int entityId) {
		Connection con = null;
		PreparedStatement preparedStmt = null;
		ResultSet rs = null;
		CreateRawTransactionPojo createRaTxPojo = null;
		
		try {
			con = getConnection();
			
			String selectQuery = "SELECT SENT_TX_ID FROM trade1.ASSET_MOVEMENT_ENTITIES WHERE SEQ_NO IN (SELECT SEQ_NO-1 FROM trade1.ASSET_MOVEMENT_ENTITIES WHERE ASSET_ID = ? AND ENTITY_ID = ?) UNION SELECT MULTISIG_ADDR FROM trade1.MULTISIGS WHERE ID IN (SELECT MULTISIG_ID FROM trade1.ASSET_MOVEMENT_ENTITIES WHERE SEQ_NO IN (SELECT SEQ_NO+1 FROM trade1.ASSET_MOVEMENT_ENTITIES WHERE ASSET_ID = ? AND ENTITY_ID = ?))";
			preparedStmt = con.prepareStatement(selectQuery);
			preparedStmt.setInt(1, assetId);
			preparedStmt.setInt(2, entityId);
			preparedStmt.setInt(3, assetId);
			preparedStmt.setInt(4, entityId);
			//preparedStmt.getParameterMetaData();
			//preparedStmt.executeUpdate();

			System.out.println("##### Database Query : " + preparedStmt.toString());
			rs = preparedStmt.executeQuery();
			int i = 1;
			while (rs.next()) {
				// Retrieve by column name
				createRaTxPojo = new CreateRawTransactionPojo();
				String strValue = rs.getString(1);
				
				if(i==1){
					createRaTxPojo.setStrTxId(strValue);
				}
				
				if(i==2){
					createRaTxPojo.setStrMultisigAddr(strValue);
				}

				//String SENT_TX_ID = rs.getString("SENT_TX_ID");
				//String strMultisigAddr = rs.getString("MULTISIG_ADDR");
				i++;
			}
			// Display values
			System.out.print("##### In selectForCreateRawTx : SENT_TX_ID is: " + createRaTxPojo.getStrTxId());
			System.out.print("##### In selectForCreateRawTx : MULTISIG_ADDR is: " + createRaTxPojo.getStrMultisigAddr());
			
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
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
		return createRaTxPojo;
	}
	
	public List<Map> fetchMultisigEntities(int iMultisigId) {
		Connection con = null;
		PreparedStatement preparedStmt = null;
		ResultSet rs = null;
		List<Map> rows = new ArrayList<Map>();
		
		try {
			con = getConnection();

			String querySel = qryMultisigsEntities;
			preparedStmt = con.prepareStatement(querySel);
			
			preparedStmt.setInt(1, iMultisigId);
			
			System.out.println("##### Database Query : " + preparedStmt.toString());
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
	
	
	public List<Map> fetchMultisigEntitiesSigned(int iMultisigId) {
		Connection con = null;
		PreparedStatement preparedStmt = null;
		ResultSet rs = null;
		List<Map> rows = new ArrayList<Map>();
		
		try {
			con = getConnection();

			String querySel = qryMultisigsEntitiesSigned;
			preparedStmt = con.prepareStatement(querySel);
			
			preparedStmt.setInt(1, iMultisigId);
			
			System.out.println("##### Database Query : " + preparedStmt.toString());
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

	public ResultSet selectForSignRawTx(int assetId, int entityId) {
		Connection con = null;
		PreparedStatement preparedStmt = null;
		ResultSet rs = null;
		try {
			con = getConnection();
			
			// String insQuery = "insert into test_table(id,name) values(?,?)";
			String selectQuery1 = "SELECT SENT_TX_ID FROM trade1.ASSET_MOVEMENT_ENTITIES WHERE SEQ_NO IN (SELECT SEQ_NO-1 FROM trade1.ASSET_MOVEMENT_ENTITIES WHERE ASSET_ID = ? AND ENTITY_ID = ?);"
					+ "SELECT RAW_TX_HEX FROM trade1.ASSET_MOVEMENT_ENTITIES WHERE ASSET_ID = ? AND ENTITY_ID = ? UNION SELECT REDEEM_SCRIPT FROM trade1.MULTISIGS WHERE ID = 1 UNION SELECT CHAIN_ADDRESS FROM trade1.ENTITIES WHERE ID IN (SELECT ENTITY_ID FROM trade1.ENTITIES_MULTISIGS WHERE MULTISIG_ID = 1 AND SIGNED = 1 ORDER BY SIGNED_DATE DESC);"
					+ "SELECT SIGNED FROM tarde1.ENTITIES_MULTISIGS WHERE MULTISIG_ID = 1 ORDER BY SIGNED_DATE	DESC;";

			preparedStmt = con.prepareStatement(selectQuery1);
			preparedStmt.setInt(1, assetId);
			preparedStmt.setInt(2, entityId);
			// preparedStmt.getParameterMetaData();
			System.out.println("##### Database Query : " + preparedStmt.toString());
			preparedStmt.executeUpdate();

			//rs = preparedStmt.executeQuery(selectQuery1);
			while (rs.next()) {
				// Retrieve by column name
				SignRawTransactionPojo signRaTxPojo = new SignRawTransactionPojo();
				String strSentTxId = rs.getString("SENT_TX_ID");
				signRaTxPojo.setStrTxId(strSentTxId);

				String strRawTxHex = rs.getString("RAW_TX_HEX");
				signRaTxPojo.setStrTxHex(strRawTxHex);

				String strReedeemScript = rs.getString("REDEEM_SCRIPT");
				signRaTxPojo.setStrRedeemScript(strReedeemScript);

				String strSigneeAddress = rs.getString("CHAIN_ADDRESS");
				signRaTxPojo.setStrRedeemScript(strSigneeAddress);

				// boolean blSigned = rs.getString("SIGNED");
				boolean blSigned = rs.getBoolean("SIGNED");
				// signRaTxPojo.setStrSigned(blSigned);
				signRaTxPojo.setStrSigned(blSigned);

				if (blSigned == true) {
					String selectQuery2 = "SELECT SIGN_OUTPUT_HEX FROM trade1.ENTITIES_MULTISIGS WHERE MULTISIG_ID = 1 AND SIGNED = 1 ORDER BY SIGNED_DATE DESC)";
				} else {
					String selectQuery3 = "SELECT RAW_TX_HEX FROM trade1.ASSET_MOVEMENT_ENTITIES WHERE ASSET_ID = ? AND ENTITY_ID = ?";
				}

				// Display values
				System.out.print("SENT_TX_ID is: " + strSentTxId);
				System.out.print(", RAW_TX_HEX is: " + strRawTxHex);
				System.out.print(", REDEEM_SCRIPT is: " + strReedeemScript);
				System.out.print(", CHAIN_ADDRESS is: " + strSigneeAddress);
				System.out.print(", SIGNED is: " + blSigned);
			}
			
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
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
		
		return rs;
	}

	public int updateAfterCreateRawTx(int assetId, int entityId, String rawTxHex) {
		Connection con = null;
		PreparedStatement preparedStmt = null;
		ResultSet rs = null;
		int status = -1;
		// String query = "update test_table set name = ? where id = ?";
		try {
			con = getConnection();
			con.setAutoCommit(false);
			
			// CreateRawTransaction cRT = new CreateRawTransaction();
			// String raw_tx_hex = cRT.createRawTransaction(strTxId, objJsonN,
			// recAddr, assetName, quantity);

			String upQuery = "update ASSET_MOVEMENT_ENTITIES set RAW_TX_HEX = ? WHERE ASSET_ID = ? AND ENTITY_ID = ?";
			preparedStmt = con.prepareStatement(upQuery);
			preparedStmt.setString(1, rawTxHex);
			preparedStmt.setInt(2, assetId);
			preparedStmt.setInt(3, entityId);
			// execute the java preparedstatement
			System.out.println("##### Database Query : " + preparedStmt.toString());
			status = preparedStmt.executeUpdate();
			
			System.out.println("Commiting data here: update()....");
			con.commit();
			System.out.println("New record Updated!");
			
		} catch (SQLException e) {
			System.out.println("Rolling back data from update()....");
			try {
				if (con != null)
					con.rollback();
			} catch (SQLException se2) {
				se2.printStackTrace();
			}
			e.printStackTrace();
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

	public int updateAfterSignRawTx(int iMultisigId, int iSigningEntityId, String strSignedTxHex) {
		Connection con = null;
		PreparedStatement preparedStmt = null;
		ResultSet rs = null;
		int status = -1;
		// String query = "update test_table set name = ? where id = ?";
		try {
			con = getConnection();
			con.setAutoCommit(false);
			
			String upQuery = " UPDATE ENTITIES_MULTISIGS "
					+ " SET SIGN_OUTPUT_HEX = ?, SIGNED=1, SIGNED_DATE=NOW(), LAST_UPDATED_DATE=NOW() "
					+ " WHERE MULTISIG_ID = ? AND ENTITY_ID = ? ";
			
			preparedStmt = con.prepareStatement(upQuery);
			preparedStmt.setString(1, strSignedTxHex);
			preparedStmt.setInt(2, iMultisigId);
			preparedStmt.setInt(3, iSigningEntityId);
			
			// execute the java preparedstatement
			System.out.println("##### Database Query : " + preparedStmt.toString());
			status = preparedStmt.executeUpdate();
			
			System.out.println("Commiting data here: update()....");
			con.commit();
			System.out.println("New record Updated!");
			
		} catch (SQLException e) {
			// If there is an error then rollback the changes.
			System.out.println("Rolling back data from update()....");
			try {
				if (con != null)
					con.rollback();
			} catch (SQLException se2) {
				se2.printStackTrace();
			}
				// System.out.println("Exception occured while updating database...");
			e.printStackTrace();
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


}