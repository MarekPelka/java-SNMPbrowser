package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import model.SNMPConnectionConfiguration;
import model.SNMPTrapReceiver;
import model.SNMPget;

public class MainBrowserWindow extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private final String SYS_OIDS_FILE = "sysOids.properties";
	private final String TABLE_OIDS_FILE = "tableOids.properties";
	private final String OTHERS_OIDS_FILE = "otherOids.properties";
	private final String TABLE_ENTRTIES_OIDS_FILE = "tableEntries.properties";

	/** BROWSER OUTPUT TEXTFIELD */
	private JTextField OIDTextField;

	/**
	 * BROWSER OUTPUT - Table with Get/Get Next results - columns : Name/OID,
	 * Value, Type, IP:Port
	 */
	private JTable browserTable;
	/**MONITORING OUTPUT -  Table to writing whole table from MIB */
	private JTable getTableTable;
	/**TRAPS OUTPUT -  Table to show received traps */
	private JTable trapsTable;
	
	/** Table model hander to write row there */
	private DefaultTableModel tableModel;
	/** Table model hander to write row there */
	private DefaultTableModel trapModel;

	/** MONITORING INPUT TEXTFIELD */
	private JTextField OIDMonitoringTextField;

	/** MONITORING OUTPUT TEXTFIELD */
	private JTextField OIDResultMonitoringTextField;
	private JTextPane valueResultMonitoringTextPane;
	private JTextField typeResultMonitoringTextField;
	private JTextField IPPortResultMonitoringTextField;

	/** CONFIGURATION TEXTFIELDS */
	private JTextField machineIPTextField;
	private JTextField snmpPortTextField;
	private JTextField readCommunityTextField;

	/** SNMP hander instance */
	private SNMPget snmp;

	/** MONITORING THREAD */
	private Thread monitoringThread;
	private MonitoringInterface monitoringInterface;
	
	
	
	private JComboBox<String> systemOIDsComboBox;
	private JComboBox<String> tableOIDsComboBox;
	private JComboBox<String> otherOIDsComboBox;

	public MainBrowserWindow(SNMPConnectionConfiguration conf) {
		this();

		try {
			snmp = new SNMPget(conf.getMachineAddress(), "" + conf.getSnmpPort(), conf.getSnmpVersion(),
					conf.getReadCommunity());
			getSystemOIDNames();
			getTableOIDNames();
			getOtherOIDNames();
			initTableEntriesNames();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/** START TRAP RECEIVER */
		SNMPTrapReceiver snmpTrapReceiver = new SNMPTrapReceiver(this, conf.getMachineAddress(), conf.getSnmpVersion(), conf.getReadCommunity());
		Thread snmpTrapThread = new Thread(snmpTrapReceiver);
		snmpTrapThread.start();

		machineIPTextField.setText(conf.getMachineAddress());
		snmpPortTextField.setText(String.valueOf(conf.getSnmpPort()));
		readCommunityTextField.setText(conf.getReadCommunity());
		initSystemComboBox();
		initTableComboBox();
		initOthersComboBox();
	}


	/**
	 * Create the frame.
	 */
	public MainBrowserWindow() {
		setTitle("SNMP BROWSER");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1077, 599);

		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
	    int x = (int) ((dimension.getWidth() - getWidth()) / 2);
	    int y = (int) ((dimension.getHeight() - getHeight()) / 2);
	    setLocation(x, y);
	    
	    
		/** PANELS */
		JPanel contentPane = new JPanel();
		contentPane.setBackground(Color.DARK_GRAY);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		TitledBorder browserBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.ORANGE),
				"BROWSER");
		browserBorder.setTitleColor(Color.ORANGE);
		browserBorder.setTitleJustification(TitledBorder.CENTER);

		JPanel browserPanel = new JPanel();
		browserPanel.setBackground(Color.DARK_GRAY);
		browserPanel.setBounds(10, 11, 553, 538);
		browserPanel.setBorder(browserBorder);
		contentPane.add(browserPanel);
		browserPanel.setLayout(null);

		TitledBorder monitoringBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.ORANGE),
				"MONITORING");
		monitoringBorder.setTitleColor(Color.ORANGE);
		monitoringBorder.setTitleJustification(TitledBorder.CENTER);

		JPanel monitoringPanel = new JPanel();
		monitoringPanel.setBackground(Color.DARK_GRAY);
		monitoringPanel.setBounds(573, 11, 373, 315);
		monitoringPanel.setBorder(monitoringBorder);
		contentPane.add(monitoringPanel);
		monitoringPanel.setLayout(null);

		/** BUTTONS */
		JButton btnGetNext = new JButton("Get Next");
		btnGetNext.setBackground(Color.GRAY);
		btnGetNext.setBounds(300, 21, 89, 23);
		browserPanel.add(btnGetNext);

		JButton btnGet = new JButton("Get");
		btnGet.setBackground(Color.GRAY);
		btnGet.setBounds(410, 21, 89, 23);
		browserPanel.add(btnGet);

		JButton btnStartMonitoring = new JButton("Start");
		btnStartMonitoring.setBackground(Color.GRAY);
		btnStartMonitoring.setBounds(272, 38, 89, 23);
		monitoringPanel.add(btnStartMonitoring);

		JButton btnStopMonitoring = new JButton("Stop");
		btnStopMonitoring.setBackground(Color.GRAY);
		btnStopMonitoring.setBounds(272, 72, 89, 23);
		monitoringPanel.add(btnStopMonitoring);
		
		JButton btnGetTable = new JButton("Get Table");
		btnGetTable.setBackground(Color.GRAY);
		btnGetTable.setBounds(300, 52, 89, 23);
		browserPanel.add(btnGetTable);

		/** LABELS */
		JLabel lblOID = new JLabel("OID:");
		lblOID.setForeground(Color.ORANGE);
		lblOID.setHorizontalAlignment(SwingConstants.TRAILING);
		lblOID.setBounds(10, 22, 31, 20);
		browserPanel.add(lblOID);

		JLabel lblOIDMonitoring = new JLabel("OID:");
		lblOIDMonitoring.setHorizontalAlignment(SwingConstants.TRAILING);
		lblOIDMonitoring.setForeground(Color.ORANGE);
		lblOIDMonitoring.setBounds(10, 39, 31, 20);
		monitoringPanel.add(lblOIDMonitoring);

		JLabel lblNameOIDResult = new JLabel("Name/OID:");
		lblNameOIDResult.setHorizontalAlignment(SwingConstants.TRAILING);
		lblNameOIDResult.setForeground(Color.ORANGE);
		lblNameOIDResult.setBounds(10, 106, 73, 20);
		monitoringPanel.add(lblNameOIDResult);

		JLabel lblValueResult = new JLabel("Value:");
		lblValueResult.setHorizontalAlignment(SwingConstants.TRAILING);
		lblValueResult.setForeground(Color.ORANGE);
		lblValueResult.setBounds(10, 137, 73, 20);
		monitoringPanel.add(lblValueResult);

		JLabel lblTypeResult = new JLabel("Type:");
		lblTypeResult.setHorizontalAlignment(SwingConstants.TRAILING);
		lblTypeResult.setForeground(Color.ORANGE);
		lblTypeResult.setBounds(10, 241, 73, 20);
		monitoringPanel.add(lblTypeResult);

		JLabel lblPortResult = new JLabel("IP:Port:");
		lblPortResult.setHorizontalAlignment(SwingConstants.TRAILING);
		lblPortResult.setForeground(Color.ORANGE);
		lblPortResult.setBounds(10, 272, 73, 20);
		monitoringPanel.add(lblPortResult);

		JLabel lvlConf1 = new JLabel("machine IP:");
		lvlConf1.setFont(new Font("Tahoma", Font.PLAIN, 9));
		lvlConf1.setHorizontalAlignment(SwingConstants.TRAILING);
		lvlConf1.setForeground(Color.ORANGE);
		lvlConf1.setBounds(956, 11, 62, 20);
		contentPane.add(lvlConf1);

		JLabel lblConf2 = new JLabel("SNMP Port:");
		lblConf2.setFont(new Font("Tahoma", Font.PLAIN, 9));
		lblConf2.setHorizontalAlignment(SwingConstants.TRAILING);
		lblConf2.setForeground(Color.ORANGE);
		lblConf2.setBounds(956, 60, 62, 20);
		contentPane.add(lblConf2);

		JLabel lblConf3 = new JLabel("R Community:");
		lblConf3.setFont(new Font("Tahoma", Font.PLAIN, 9));
		lblConf3.setHorizontalAlignment(SwingConstants.TRAILING);
		lblConf3.setForeground(Color.ORANGE);
		lblConf3.setBounds(956, 121, 73, 20);
		contentPane.add(lblConf3);

		JLabel otherOIDSlbl = new JLabel("OTHERS OID's:");
		otherOIDSlbl.setHorizontalAlignment(SwingConstants.LEFT);
		otherOIDSlbl.setForeground(Color.ORANGE);
		otherOIDSlbl.setFont(new Font("Tahoma", Font.PLAIN, 9));
		otherOIDSlbl.setBounds(956, 269, 73, 20);
		contentPane.add(otherOIDSlbl);
		
		JLabel lblTraps = new JLabel("TRAPS");
		lblTraps.setHorizontalAlignment(SwingConstants.CENTER);
		lblTraps.setForeground(Color.ORANGE);
		lblTraps.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblTraps.setBounds(747, 337, 130, 25);
		contentPane.add(lblTraps);
		
		JLabel systemOIDSlbl = new JLabel("SYSTEM OID's:");
		systemOIDSlbl.setHorizontalAlignment(SwingConstants.LEFT);
		systemOIDSlbl.setForeground(Color.ORANGE);
		systemOIDSlbl.setFont(new Font("Tahoma", Font.PLAIN, 9));
		systemOIDSlbl.setBounds(956, 171, 73, 20);
		contentPane.add(systemOIDSlbl);
		
		JLabel tableOIDSlbl = new JLabel("TABLE OID's:");
		tableOIDSlbl.setHorizontalAlignment(SwingConstants.LEFT);
		tableOIDSlbl.setForeground(Color.ORANGE);
		tableOIDSlbl.setFont(new Font("Tahoma", Font.PLAIN, 9));
		tableOIDSlbl.setBounds(956, 220, 73, 20);
		contentPane.add(tableOIDSlbl);
		
		/** BROWSER INPUT TEXTFIELD */
		OIDTextField = new JTextField();
		OIDTextField.setBackground(Color.LIGHT_GRAY);
		OIDTextField.setBounds(51, 22, 225, 20);
		browserPanel.add(OIDTextField);
		OIDTextField.setColumns(10);

		/** BROWSER TABLE */
		browserTable = new JTable()
		{
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public boolean getScrollableTracksViewportWidth()
            {
                return getPreferredSize().width < getParent().getWidth();
            }
        };
        browserTable.setAutoResizeMode( JTable.AUTO_RESIZE_OFF);
		
		tableModel = new DefaultTableModel(new Object[][] {}, new String[] { "Name/OID", "Value", "Type", "IP/Port" });
		browserTable.setModel(tableModel);
		JScrollPane browserScrollPane = new JScrollPane(browserTable);	
		browserScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		browserScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		browserScrollPane.setBounds(10, 93, 533, 195);
		browserPanel.add(browserScrollPane);
		
		
		getTableTable = new JTable();
		getTableTable.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
		JScrollPane getTableScrollPane = new JScrollPane(getTableTable);
		getTableScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		getTableScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		getTableScrollPane.setBounds(10, 311, 533, 216);
		browserPanel.add(getTableScrollPane);
		
		


		/** MONITORING OUTPUT TEXTFIELD */
		OIDMonitoringTextField = new JTextField();
		OIDMonitoringTextField.setColumns(10);
		OIDMonitoringTextField.setBackground(Color.LIGHT_GRAY);
		OIDMonitoringTextField.setBounds(51, 39, 211, 20);
		monitoringPanel.add(OIDMonitoringTextField);

		/** MONITORING INPUT TEXTFIELD */
		OIDResultMonitoringTextField = new JTextField();
		OIDResultMonitoringTextField.setEditable(false);
		OIDResultMonitoringTextField.setColumns(10);
		OIDResultMonitoringTextField.setBackground(Color.LIGHT_GRAY);
		OIDResultMonitoringTextField.setBounds(93, 106, 270, 20);
		monitoringPanel.add(OIDResultMonitoringTextField);

		typeResultMonitoringTextField = new JTextField();
		typeResultMonitoringTextField.setColumns(10);
		typeResultMonitoringTextField.setBackground(Color.LIGHT_GRAY);
		typeResultMonitoringTextField.setBounds(93, 241, 270, 20);
		monitoringPanel.add(typeResultMonitoringTextField);

		IPPortResultMonitoringTextField = new JTextField();
		IPPortResultMonitoringTextField.setEditable(false);
		IPPortResultMonitoringTextField.setColumns(10);
		IPPortResultMonitoringTextField.setBackground(Color.LIGHT_GRAY);
		IPPortResultMonitoringTextField.setBounds(93, 272, 270, 20);
		monitoringPanel.add(IPPortResultMonitoringTextField);

		valueResultMonitoringTextPane = new JTextPane();
		valueResultMonitoringTextPane.setBounds(93, 137, 270, 93);
		monitoringPanel.add(valueResultMonitoringTextPane);

		/** CONFIGURATION TEXTFIELDS */
		machineIPTextField = new JTextField();
		machineIPTextField.setForeground(Color.ORANGE);
		machineIPTextField.setEditable(false);
		machineIPTextField.setEnabled(false);
		machineIPTextField.setColumns(10);
		machineIPTextField.setBackground(Color.DARK_GRAY);
		machineIPTextField.setBounds(955, 29, 96, 20);
		contentPane.add(machineIPTextField);

		snmpPortTextField = new JTextField();
		snmpPortTextField.setForeground(Color.ORANGE);
		snmpPortTextField.setEditable(false);
		snmpPortTextField.setEnabled(false);
		snmpPortTextField.setColumns(10);
		snmpPortTextField.setBackground(Color.DARK_GRAY);
		snmpPortTextField.setBounds(955, 79, 96, 20);
		contentPane.add(snmpPortTextField);

		readCommunityTextField = new JTextField();
		readCommunityTextField.setForeground(Color.ORANGE);
		readCommunityTextField.setEditable(false);
		readCommunityTextField.setEnabled(false);
		readCommunityTextField.setColumns(10);
		readCommunityTextField.setBackground(Color.DARK_GRAY);
		readCommunityTextField.setBounds(955, 140, 96, 20);
		contentPane.add(readCommunityTextField);



	
		
		trapsTable = new JTable()
		{
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public boolean getScrollableTracksViewportWidth()
            {
                return getPreferredSize().width < getParent().getWidth();
            }
        };
        trapsTable.setAutoResizeMode( JTable.AUTO_RESIZE_OFF);
		trapsTable.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
		trapModel = new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
					"Variable Bindings", "IP/Port", "State Reference", "Payload length"
			}
		);
		trapsTable.setModel(trapModel);
		JScrollPane trapsScrollPane = new JScrollPane(trapsTable);
		trapsScrollPane.setBounds(573, 373, 479, 164);
		contentPane.add(trapsScrollPane);
		
		
		/** COMBO BOXES */
		otherOIDsComboBox = new JComboBox<String>();
		otherOIDsComboBox.setBounds(956, 285, 96, 20);
		contentPane.add(otherOIDsComboBox);
		
		systemOIDsComboBox = new JComboBox<String>();
		systemOIDsComboBox.setBounds(955, 189, 96, 20);
		contentPane.add(systemOIDsComboBox);
		
	
		
		tableOIDsComboBox = new JComboBox<String>();
		tableOIDsComboBox.setBounds(955, 236, 96, 20);
		contentPane.add(tableOIDsComboBox);
		
	

		/** LISTENERS */
		btnGetNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				getNext();
			}
		});

		btnGet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				get();
			}
		});

		btnStartMonitoring.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				monitoringStart();
			}
		});

		btnStopMonitoring.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				monitoringStop();
			}
		});

		browserTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent event) {
				if (browserTable.getSelectedRow() > -1) {
					// print first column value from selected row
					OIDTextField.setText(browserTable.getValueAt(browserTable.getSelectedRow(), 0).toString());
					OIDMonitoringTextField
							.setText(browserTable.getValueAt(browserTable.getSelectedRow(), 0).toString());
				}
			}
		});
		systemOIDsComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				OIDTextField.setText(systemOIDsComboBox.getSelectedItem().toString());
				OIDMonitoringTextField.setText(systemOIDsComboBox.getSelectedItem().toString());
			}
		});
		
		tableOIDsComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				OIDTextField.setText(tableOIDsComboBox.getSelectedItem().toString());
			}
		});

		otherOIDsComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				OIDTextField.setText(otherOIDsComboBox.getSelectedItem().toString());
				OIDMonitoringTextField.setText(otherOIDsComboBox.getSelectedItem().toString());
			}
		});
		
		btnGetTable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				getTable();
			}
		});
	}

	private void addRowToBrowserTable(String name, String value, String type, String IpPort) {
		tableModel.addRow(new Object[] { name, value, type, IpPort });

	}

	private void get() {
		String[] response = null;
		try {
			String OID = OIDTextField.getText();
			OID = parseOID(OID);
			response = snmp.snmpGet(OID);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(MainBrowserWindow.this, "SNMP Get failed.", "ERROR",
					JOptionPane.ERROR_MESSAGE);
		}
		addRowToBrowserTable(response[0], response[1], response[2], response[3]);

	}

	private void getNext() {
		String[] response = null;
		try {
			String OID = OIDTextField.getText();
			OID = parseOID(OID);
			response = snmp.snmpGetNext(OID);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(MainBrowserWindow.this, "SNMP GetNext failed.", "ERROR",
					JOptionPane.ERROR_MESSAGE);
		}
		if(response[2].equals("END_OF_MIB"))
		{
			JOptionPane.showMessageDialog(MainBrowserWindow.this, "END OF MIB.", "MIB",
					JOptionPane.INFORMATION_MESSAGE);
		}
		addRowToBrowserTable(response[0], response[1], response[2], response[3]);
		browserTable.changeSelection(browserTable.getRowCount()-1, 0, false, false);
	}
	

	private void getTable() {
		String[] response;
		List<String> columnsList = new ArrayList<>();
		List<List<String>> rowsLists = new ArrayList<>();
		
		String tableMainOID;
		String tmpTableOIDChecker;
		try {
			String OID = OIDTextField.getText();
			OID = parseOID(OID);
			
			tableMainOID = OID;
			tmpTableOIDChecker = OID;
			response = snmp.snmpGetNext(OID);
			if(!response[0].substring(0,OID.length()).equals(OID))
			{
				JOptionPane.showMessageDialog(MainBrowserWindow.this, "Table is empty.", "END OF TABLE",
						JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			do
			{
				List<String> columnValue = new ArrayList<>();
				response = snmp.snmpGetNext(OID);
				String name = response[0];
				name = name.replace(tableMainOID, "");
				
				if(!name.isEmpty())
				{
					name = name.substring(name.indexOf('.')+1, name.length());
					name = name.substring(name.indexOf('.')+1, name.length()); 
					name = name.substring(0,name.indexOf('.'));
				}
					
				else
					name = "";
				columnsList.add(response[0].substring(0, tableMainOID.length() +3 + name.length()));
				
				String tmpName = name;
				columnValue.add(response[1]);
				while(true)
				{
					OID = response[0];
					response = snmp.snmpGetNext(OID);
					
					tmpName = response[0].replace(tableMainOID, "");
					tmpName = tmpName.substring(tmpName.indexOf('.')+1, tmpName.length());
					tmpName = tmpName.substring(tmpName.indexOf('.')+1, tmpName.length()); 
					tmpName = tmpName.substring(0,tmpName.indexOf('.'));
					if(tmpName.equals(name))
						columnValue.add(response[1]);
					else
						break;
				}
				
				rowsLists.add(columnValue);
				tmpTableOIDChecker = response[0].substring(0, tableMainOID.length());
			}while(tableMainOID.equals(tmpTableOIDChecker));
			
		} catch (IOException e) {
			JOptionPane.showMessageDialog(MainBrowserWindow.this, "SNMP GetTABLE failed.", "ERROR",
					JOptionPane.ERROR_MESSAGE);
		}


		
		String[][] tmpRows = new String[rowsLists.get(0).size()][rowsLists.size()];
		for(int i = 0 ;i < rowsLists.size();i++)
		{
			for(int k = 0 ; k< rowsLists.get(i).size();k++)
			{
				tmpRows[k][i] = rowsLists.get(i).get(k); 
			}
		}
		
		String[] tmpColumns = new String[columnsList.size()];
		for(int i =0; i< columnsList.size();i++)
		{
			String column = columnsList.get(i);
			column=parseEntryOID(column);
			tmpColumns[i] = column;
		}
		
		DefaultTableModel model = new DefaultTableModel(tmpRows, tmpColumns);
		getTableTable.setModel(model);
	}

	private void monitoringStart() {
		monitoringInterface = new MonitoringInterface();
		// Start new thread
		monitoringThread = new Thread(monitoringInterface);
		monitoringThread.start();
	}

	private void monitoringStop() {
		if (monitoringThread != null) {
			monitoringInterface.terminate();
			try {
				monitoringThread.join();
			} catch (InterruptedException e) {
				JOptionPane.showMessageDialog(MainBrowserWindow.this, "Stopping monitoring failed.", "ERROR",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private class MonitoringInterface implements Runnable {
		private volatile boolean running = true;
		private String OID;

		public MonitoringInterface() {
			OID = OIDMonitoringTextField.getText();
			OID = parseOID(OID);
		}

		public void terminate() {
			running = false;
		}

		public void run() {
			while (running) {
				String[] response = null;
				try {
					response = snmp.snmpGet(OID);
				} catch (IOException e) {
					JOptionPane.showMessageDialog(MainBrowserWindow.this, "SNMP Get In monitoring failed.", "ERROR",
							JOptionPane.ERROR_MESSAGE);
				}
				OIDResultMonitoringTextField.setText(response[0]);
				valueResultMonitoringTextPane.setText(response[1]);
				typeResultMonitoringTextField.setText(response[2]);
				IPPortResultMonitoringTextField.setText(response[3]);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					JOptionPane.showMessageDialog(MainBrowserWindow.this, "Monitoring thread interrupted.", "WARNING",
							JOptionPane.WARNING_MESSAGE);
				}
			}

		}
	}
	
	public void addTrapRow(String des, String source, String time, String sev)
	{
		trapModel.addRow(new Object[] { des, source, time, sev });
	}
	
	private void initSystemComboBox() {

		try (FileReader reader = new FileReader(SYS_OIDS_FILE)) {
			Properties prop = new Properties();
			prop.load(reader);
			Enumeration<?> e = prop.propertyNames();
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				systemOIDsComboBox.addItem(key);
			}
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(MainBrowserWindow.this, "sysOids.properties file not found.", "ERROR",
					JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(MainBrowserWindow.this, "reading sysOids.properties failed.", "ERROR",
					JOptionPane.ERROR_MESSAGE);
		}

	}
	
	private void initTableComboBox() {

		try (FileReader reader = new FileReader(TABLE_OIDS_FILE)) {
			Properties prop = new Properties();
			prop.load(reader);
			Enumeration<?> e = prop.propertyNames();
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				tableOIDsComboBox.addItem(key);
			}
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(MainBrowserWindow.this, "tableOids.properties file not found.", "ERROR",
					JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(MainBrowserWindow.this, "reading tableOids.properties failed.", "ERROR",
					JOptionPane.ERROR_MESSAGE);
		}

	}
	

	private void initOthersComboBox() {

		try (FileReader reader = new FileReader(OTHERS_OIDS_FILE)) {
			Properties prop = new Properties();
			prop.load(reader);
			Enumeration<?> e = prop.propertyNames();
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				otherOIDsComboBox.addItem(key);
			}
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(MainBrowserWindow.this, "otherOids.properties file not found.", "ERROR",
					JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(MainBrowserWindow.this, "reading otherOids.properties failed.", "ERROR",
					JOptionPane.ERROR_MESSAGE);
		}

	}
	
	private void getSystemOIDNames() throws IOException {
		File file = new File(SYS_OIDS_FILE);
		if (!file.exists()) {
			file.createNewFile();
			String defaultData = "sysDescr=1.3.6.1.2.1.1.1.0\n" + "sysObjectID=1.3.6.1.2.1.1.2.0\n"
					+ "sysUpTime=1.3.6.1.2.1.1.3.0\n" + "sysContact=1.3.6.1.2.1.1.4.0\n" + "sysName=1.3.6.1.2.1.1.5.0\n"
					+ "sysLocation=1.3.6.1.2.1.1.6.0\n" + "sysServices=1.3.6.1.2.1.1.7.0\n";
			FileWriter fileWritter = new FileWriter(file.getName(), true);
			BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
			bufferWritter.write(defaultData);
			bufferWritter.close();
		}

	}
	
	private void getTableOIDNames() throws IOException {
		File file = new File(TABLE_OIDS_FILE);
		if (!file.exists()) {
			file.createNewFile();
			String defaultData = "sysORTable=1.3.6.1.2.1.1.9\n" + "ifTable=1.3.6.1.2.1.2.2\n"
					+ "ipAddrTable=1.3.6.1.2.1.4.20\n" + "ipRouteTable=1.3.6.1.2.1.4.21\n" + "ipNetToMediaTable=1.3.6.1.2.1.4.22\n"
					+ "tcpConnTable=1.3.6.1.2.1.6.13\n" + "udpTable=1.3.6.1.2.1.7.5\n";
			FileWriter fileWritter = new FileWriter(file.getName(), true);
			BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
			bufferWritter.write(defaultData);
			bufferWritter.close();
		}
	}

	private void getOtherOIDNames() throws IOException {
		File file = new File(OTHERS_OIDS_FILE);
		if (!file.exists()) {
			file.createNewFile();
			String defaultData =  "ifNumber=1.3.6.1.2.1.2.1\n"
					+ "ifIndex=1.3.6.1.2.1.2.2.1.1\n" + "ifDescr=1.3.6.1.2.1.2.2.1.2\n" + "ifType=1.3.6.1.2.1.2.2.1.3\n"
					+ "ifMtu=1.3.6.1.2.1.2.2.1.4\n" + "ifSpeed=1.3.6.1.2.1.2.2.1.5\n"
					+ "ifPhysAddress=1.3.6.1.2.1.2.2.1.6\n" + "ifAdminStatus=1.3.6.1.2.1.2.2.1.7\n"
					+ "ifOperStatus=1.3.6.1.2.1.2.2.1.8\n" + "ifLastChange=1.3.6.1.2.1.2.2.1.9\n"
					+ "ifInOctets=1.3.6.1.2.1.2.2.1.10\n" + "ifInUcastPkts=1.3.6.1.2.1.2.2.1.11\n"
					+ "ifInNUcastPkts=1.3.6.1.2.1.2.2.1.12\n" + "ifInDiscards=1.3.6.1.2.1.2.2.1.13\n"
					+ "ifInErrors=1.3.6.1.2.1.2.2.1.14\n" + "ifInUnknownProtos=1.3.6.1.2.1.2.2.1.15\n"
					+ "ifOutOctets=1.3.6.1.2.1.2.2.1.16\n" + "ifOutUcastPkts=1.3.6.1.2.1.2.2.1.17\n"
					+ "ifOutNUcastPkts=1.3.6.1.2.1.2.2.1.18\n" + "ifOutDiscards=1.3.6.1.2.1.2.2.1.19\n"
					+ "ifOutErrors=1.3.6.1.2.1.2.2.1.20\n" + "ifOutQLen=1.3.6.1.2.1.2.2.1.21\n"
					+ "ifSpecific=1.3.6.1.2.1.2.2.1.22\n" + "atIfIndex=1.3.6.1.2.1.3.1.1.1\n"
					+ "atPhysAddress=1.3.6.1.2.1.3.1.1.2\n" + "atNetAddress=1.3.6.1.2.1.3.1.1.3\n";
			FileWriter fileWritter = new FileWriter(file.getName(), true);
			BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
			bufferWritter.write(defaultData);
			bufferWritter.close();
		}

	}
	
	private String parseOID(String OID)
	{
		if (!OID.matches(".*\\d.*")) {
			String fileName = null;
			if(OID.contains("Table"))
				fileName = TABLE_OIDS_FILE;
			else if(OID.contains("sys"))
				fileName = SYS_OIDS_FILE;
			else
				fileName = OTHERS_OIDS_FILE;
			try (FileReader reader = new FileReader(fileName)) {
				Properties prop = new Properties();
				prop.load(reader);
				if(prop.getProperty(OID) != null)
					OID = prop.getProperty(OID);
			} catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(MainBrowserWindow.this, fileName+ " file not found.", "ERROR",
						JOptionPane.ERROR_MESSAGE);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(MainBrowserWindow.this, "reading "+ fileName +" failed.", "ERROR",
						JOptionPane.ERROR_MESSAGE);
			}
		}
		return OID;
	}
	
	private String parseEntryOID(String OID)
	{
		try (FileReader reader = new FileReader(TABLE_ENTRTIES_OIDS_FILE)) {
			Properties prop = new Properties();
			prop.load(reader);
			if(prop.getProperty(OID) != null)
				OID = prop.getProperty(OID);
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(MainBrowserWindow.this, TABLE_ENTRTIES_OIDS_FILE+ " file not found.", "ERROR",
					JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(MainBrowserWindow.this, "reading "+ TABLE_ENTRTIES_OIDS_FILE +" failed.", "ERROR",
					JOptionPane.ERROR_MESSAGE);
		}
		return OID;
	}
	
	private void initTableEntriesNames() throws IOException
	{
		File file = new File(TABLE_ENTRTIES_OIDS_FILE);
		if (!file.exists()) {
			file.createNewFile();
			String defaultData =  "1.3.6.1.2.1.4.20.1.1=ipAdEntAddr\n"
					+ "1.3.6.1.2.1.4.20.1.2=ipAdEntIfIndex\n" 
					+ "1.3.6.1.2.1.4.20.1.3=idAdEntNextMask\n" 
					+ "1.3.6.1.2.1.4.20.1.4=ipAdEntBcastAddr\n"
					+ "1.3.6.1.2.1.4.20.1.5=ipAdEntReasmMaxSize\n"
					+ "1.3.6.1.2.1.6.13.1.1=tcpConnState\n"
					+ "1.3.6.1.2.1.6.13.1.2=tcpConnLocalAddress\n" 
					+ "1.3.6.1.2.1.6.13.1.3=tcpConnLocalPort\n"
					+ "1.3.6.1.2.1.6.13.1.4=tcpConnRemAddress\n" 
					+ "1.3.6.1.2.1.6.13.1.5=tcpConnRemPort\n"
					+ "1.3.6.1.2.1.7.5.1.1=udpLocalAdress\n" 
					+ "1.3.6.1.2.1.7.5.1.2=udpLocalPort\n"
					+ "1.3.6.1.2.1.4.22.1.1=ipNetToMediaIfIndex\n" 
					+ "1.3.6.1.2.1.4.22.1.2=ipNetToMediaPhysAdress\n"
					+ "1.3.6.1.2.1.4.22.1.3=ipNetToMediaNetAddress\n" 
					+ "1.3.6.1.2.1.4.22.1.4=ipNetToMediaType\n"
					+ "1.3.6.1.2.1.4.21.1.1=ipRouteDest\n" 
					+ "1.3.6.1.2.1.4.21.1.2=ipRouteIfIndex\n"
					+ "1.3.6.1.2.1.4.21.1.3=ipRouteMetric1\n" 
					+ "1.3.6.1.2.1.4.21.1.4=ipRouteMetric2\n"
					+ "1.3.6.1.2.1.4.21.1.5=ipRouteMetric3\n" 
					+ "1.3.6.1.2.1.4.21.1.6=ipRouteMetric4\n"
					+ "1.3.6.1.2.1.4.21.1.7=ipNextHop\n" 
					+ "1.3.6.1.2.1.4.21.1.8=ipRouteType\n"
					+ "1.3.6.1.2.1.4.21.1.9=ipRouteProto\n"
					+ "1.3.6.1.2.1.4.21.1.10=ipRouteAge\n"
					+ "1.3.6.1.2.1.4.21.1.11=ipRouteMask\n"
					+ "1.3.6.1.2.1.4.21.1.12=ipRouteMetric5\n"
					+ "1.3.6.1.2.1.4.21.1.13=ipRouteInfo\n";
			FileWriter fileWritter = new FileWriter(file.getName(), true);
			BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
			bufferWritter.write(defaultData);
			bufferWritter.close();
		}

	}
	
}
