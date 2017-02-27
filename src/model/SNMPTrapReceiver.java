package model;

import java.io.IOException;

import org.snmp4j.CommandResponder;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import view.MainBrowserWindow;

public class SNMPTrapReceiver extends Thread {

	private String ip;
	private int port = 162;
	private int snmpVer;
	private String community;
	private MainBrowserWindow mainWindow;

	public SNMPTrapReceiver(MainBrowserWindow mw, String ip, int snmpVer, String community) {
		this.ip = ip;
		this.snmpVer = snmpVer;
		this.community = community;
		this.mainWindow = mw;
	}

	@Override
	public void run() {
		UdpAddress udp = new UdpAddress(ip.replace("/", "") + "/" + port);
		try {
			TransportMapping<?> transport = new DefaultUdpTransportMapping(udp);
			Snmp snmp = new Snmp(transport);
			CommandResponder pduHandler = new CommandResponder() {
				public synchronized void processPdu(CommandResponderEvent e) {
					PDU pdu = e.getPDU();
					
					if (pdu != null) {
						// PDU JUZ ODEBRANY - wyciaganie z niego danych
						mainWindow.addTrapRow("" + pdu.getVariableBindings(), "" + e.getPeerAddress(), "" + e.getTmStateReference(), "" + pdu.getBERPayloadLength());
						System.out.println(pdu);
						System.out.println(e);
						System.out.println("Trap Type = " + pdu.getType());
						System.out.println("Variable Bindings = " + pdu.getVariableBindings());
					}
				}
			};
			snmp.addCommandResponder(pduHandler);
			transport.listen();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
}
