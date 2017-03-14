package com.mphasis.tradefin.beans;

import java.sql.Timestamp;

public class Assets {

	int id;
	String strName;
	double dbQuantity;
	String strAssetRef;
	String strIssueTxId;
	int intIssueReceiverEntityId;
	Timestamp dtCreatedDate;
	Timestamp dtLastUpdatedDate;
	
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the strName
	 */
	public String getStrName() {
		return strName;
	}
	/**
	 * @param strName the strName to set
	 */
	public void setStrName(String strName) {
		this.strName = strName;
	}
	/**
	 * @return the dbQuantity
	 */
	public double getDbQuantity() {
		return dbQuantity;
	}
	/**
	 * @param dbQuantity the dbQuantity to set
	 */
	public void setDbQuantity(double dbQuantity) {
		this.dbQuantity = dbQuantity;
	}
	/**
	 * @return the strAssetRef
	 */
	public String getStrAssetRef() {
		return strAssetRef;
	}
	/**
	 * @param strAssetRef the strAssetRef to set
	 */
	public void setStrAssetRef(String strAssetRef) {
		this.strAssetRef = strAssetRef;
	}
	/**
	 * @return the strIssueTxId
	 */
	public String getStrIssueTxId() {
		return strIssueTxId;
	}
	/**
	 * @param strIssueTxId the strIssueTxId to set
	 */
	public void setStrIssueTxId(String strIssueTxId) {
		this.strIssueTxId = strIssueTxId;
	}
	/**
	 * @return the intIssueReceiverEntityId
	 */
	public int getIntIssueReceiverEntityId() {
		return intIssueReceiverEntityId;
	}
	/**
	 * @param intIssueReceiverEntityId the intIssueReceiverEntityId to set
	 */
	public void setIntIssueReceiverEntityId(int intIssueReceiverEntityId) {
		this.intIssueReceiverEntityId = intIssueReceiverEntityId;
	}
	/**
	 * @return the dtCreatedDate
	 */
	public Timestamp getDtCreatedDate() {
		return dtCreatedDate;
	}
	/**
	 * @param dtCreatedDate the dtCreatedDate to set
	 */
	public void setDtCreatedDate(Timestamp dtCreatedDate) {
		this.dtCreatedDate = dtCreatedDate;
	}
	/**
	 * @return the dtLastUpdatedDate
	 */
	public Timestamp getDtLastUpdatedDate() {
		return dtLastUpdatedDate;
	}
	/**
	 * @param dtLastUpdatedDate the dtLastUpdatedDate to set
	 */
	public void setDtLastUpdatedDate(Timestamp dtLastUpdatedDate) {
		this.dtLastUpdatedDate = dtLastUpdatedDate;
	}
		
}
