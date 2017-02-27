package model;

public class SNMPConnectionConfiguration {
	/** SNMP NEEDED FIELDS FROM STARTUPWINDOW GIVEN TO MAINBROWSERWINDOW */
	private String machineAddress;
	private int snmpPort;
	private String readCommunity;
	private int snmpVersion;
	
	public SNMPConnectionConfiguration(String machineAddress, int snmpPort, String readCommunity, int snmpVersion) {
		this.machineAddress = machineAddress;
		this.snmpPort = snmpPort;
		this.readCommunity = readCommunity;
		this.snmpVersion = snmpVersion;
	}
	
	public String getMachineAddress() {
		return machineAddress;
	}
	public int getSnmpPort() {
		return snmpPort;
	}
	public String getReadCommunity() {
		return readCommunity;
	}
	public int getSnmpVersion() {
		return snmpVersion;
	}
}
