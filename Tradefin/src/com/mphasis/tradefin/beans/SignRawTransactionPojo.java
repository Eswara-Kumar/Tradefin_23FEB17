package com.mphasis.tradefin.beans;

public class SignRawTransactionPojo {
	
	String strTxHex;
	String strTxId;
	String strRedeemScript;
	String signeeAddress;
	boolean signed;
	

	public boolean isStrSigned() {
		return signed;
	}
	public void setStrSigned(boolean strSigned) {
		this.signed = strSigned;
	}
	public String getStrTxHex() {
		return strTxHex;
	}
	public void setStrTxHex(String strTxHex) {
		this.strTxHex = strTxHex;
	}
	public String getStrTxId() {
		return strTxId;
	}
	public void setStrTxId(String strTxId) {
		this.strTxId = strTxId;
	}
	public String getStrRedeemScript() {
		return strRedeemScript;
	}
	public void setStrRedeemScript(String strRedeemScript) {
		this.strRedeemScript = strRedeemScript;
	}
	public String getSigneeAddress() {
		return signeeAddress;
	}
	public void setSigneeAddress(String signeeAddress) {
		this.signeeAddress = signeeAddress;
	}

}
