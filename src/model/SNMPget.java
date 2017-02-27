package model;

import java.io.IOException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.SMIConstants;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

public class SNMPget {
	private static String ipAddress;
	private static int snmpVersion;
	private CommunityTarget comtarget;
	private TransportMapping<?> transport;
	private Snmp snmp;
	private Map<Integer, String> typeMap;

	public SNMPget(String ip, String port, int snmpVer, String community) throws IOException {
		ipAddress = ip;
		snmpVersion = snmpVer;
		transport = new DefaultUdpTransportMapping();
		transport.listen();

		comtarget = new CommunityTarget();
		comtarget.setCommunity(new OctetString(community));
		if (snmpVer == 2)
			snmpVersion = SnmpConstants.version2c;
		else
			snmpVersion = SnmpConstants.version1;
		comtarget.setVersion(snmpVersion);
		comtarget.setAddress(new UdpAddress(ipAddress.replace("/", "") + "/" + port));
		comtarget.setRetries(2);
		comtarget.setTimeout(1000);

		typeMap = new HashMap<>();
		typeMap.put(130, "END_OF_MIB");
		typeMap.put(129, "NO_SUCH_INSTANCE");
		typeMap.put(128, "NO_SUCH_OBJECT");
		typeMap.put(4, "BITS");
		typeMap.put(65, "COUNTER32");
		typeMap.put(70, "COUNTER64");
		typeMap.put(66, "GAUGE32");
		typeMap.put(2, "INTEGER32");
		typeMap.put(64, "IP_ADRESS");
		typeMap.put(5, "NULL");
		typeMap.put(6, "OBJECT_IDENTIFIER");
		typeMap.put(4, "OCTET_STRING");
		typeMap.put(68, "OPAQUE");
		typeMap.put(67, "TIMETICKS");
		typeMap.put(66, "UNSIGNED_INTEGER32");
	}

	public String[] snmpGet(String oidValue) throws IOException {
		String[] out = new String[4];
		VariableBinding vb = new VariableBinding(new OID(oidValue));
		PDU pdu = new PDU(PDU.GET, Arrays.asList(vb));
		snmp = new Snmp(transport);

		ResponseEvent response = snmp.get(pdu, comtarget);
		if (response != null) {
			PDU responsePDU = response.getResponse();

			if (responsePDU != null) {
				int errorStatus = responsePDU.getErrorStatus();
				int errorIndex = responsePDU.getErrorIndex();
				String errorStatusText = responsePDU.getErrorStatusText();

				if (errorStatus == PDU.noError) {

					out[0] = "" + responsePDU.getVariableBindings().get(0).getOid();
					out[1] = "" + responsePDU.getVariableBindings().get(0).getVariable();
					out[2] = typeMap.get(responsePDU.getVariableBindings().get(0).getSyntax());
					if (out[2] == null)
						out[2] = "NO_SUCH_ELEMENT_IN_MAP";
					out[3] = "" + response.getPeerAddress();
					System.out.println(pdu);
					System.out.println(responsePDU);
				} else {
					System.out.println("Error: Request Failed");
					System.out.println("Error Status = " + errorStatus);
					System.out.println("Error Index = " + errorIndex);
					System.out.println("Error Status Text = " + errorStatusText);
				}
			} else {
				System.out.println("Response PDU is null!");
			}
		} else {
			System.out.println("Error - timeout!");
		}
		snmp.close();
		transport = new DefaultUdpTransportMapping();
		transport.listen();
		return out;
	}

	public String[] snmpGetNext(String oidValue) throws IOException {
		String[] out = new String[4];
		VariableBinding vb = new VariableBinding(new OID(oidValue));
		PDU pdu = new PDU(PDU.GETNEXT, Arrays.asList(vb));
		snmp = new Snmp(transport);

		ResponseEvent response = snmp.getNext(pdu, comtarget);
		if (response != null) {
			PDU responsePDU = response.getResponse();

			if (responsePDU != null) {
				int errorStatus = responsePDU.getErrorStatus();
				int errorIndex = responsePDU.getErrorIndex();
				String errorStatusText = responsePDU.getErrorStatusText();

				if (errorStatus == PDU.noError) {
					out[0] = "" + responsePDU.getVariableBindings().get(0).getOid();
					out[1] = "" + responsePDU.getVariableBindings().get(0).getVariable();
					out[2] = typeMap.get(responsePDU.getVariableBindings().get(0).getSyntax());
					if (out[2] == null)
						out[2] = "NO_SUCH_ELEMENT_IN_MAP";
					out[3] = "" + response.getPeerAddress();
					System.out.println(pdu);
					System.out.println(responsePDU);
				} else {
					System.out.println("Error: Request Failed");
					System.out.println("Error Status = " + errorStatus);
					System.out.println("Error Index = " + errorIndex);
					System.out.println("Error Status Text = " + errorStatusText);
				}
			} else {
				System.out.println("Response PDU is null!");
			}
		} else {
			System.out.println("Error - timeout!");
		}
		snmp.close();
		transport = new DefaultUdpTransportMapping();
		transport.listen();
		return out;
	}
}
