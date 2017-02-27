package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import model.SNMPConnectionConfiguration;

public class StartUpWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	/**
	 * Variables needed to property application start
	 */
	private JTextField machineAddress;
	private JTextField snmpPort;
	private JTextField readCommunity;
	private JTextField snmpVersion;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					StartUpWindow frame = new StartUpWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public StartUpWindow() {
		setTitle("SNMP BROWSER");
		setType(Type.UTILITY);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 223, 154);
		
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
	    int x = (int) ((dimension.getWidth() - getWidth()) / 2);
	    int y = (int) ((dimension.getHeight() - getHeight()) / 2);
	    setLocation(x, y);
	    
	    /** PANEL */
		JPanel contentPane;		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setBackground(Color.LIGHT_GRAY);
		setContentPane(contentPane);
		contentPane.setLayout(null);

		/** INPUT TEXT FIELDS */
		machineAddress = new JTextField();
		machineAddress.setFont(new Font("Tahoma", Font.BOLD, 11));
		machineAddress.setForeground(Color.WHITE);
		machineAddress.setBackground(Color.LIGHT_GRAY);
		machineAddress.setBounds(101, 0, 106, 20);
		machineAddress.setColumns(10);
		contentPane.add(machineAddress);

		snmpPort = new JTextField();
		snmpPort.setForeground(Color.WHITE);
		snmpPort.setFont(new Font("Tahoma", Font.BOLD, 11));
		snmpPort.setColumns(10);
		snmpPort.setBackground(Color.LIGHT_GRAY);
		snmpPort.setBounds(101, 21, 106, 20);
		contentPane.add(snmpPort);

		readCommunity = new JTextField();
		readCommunity.setForeground(Color.WHITE);
		readCommunity.setFont(new Font("Tahoma", Font.BOLD, 11));
		readCommunity.setColumns(10);
		readCommunity.setBackground(Color.LIGHT_GRAY);
		readCommunity.setBounds(101, 42, 106, 20);
		contentPane.add(readCommunity);

		snmpVersion = new JTextField();
		snmpVersion.setForeground(Color.WHITE);
		snmpVersion.setFont(new Font("Tahoma", Font.BOLD, 11));
		snmpVersion.setColumns(10);
		snmpVersion.setBackground(Color.LIGHT_GRAY);
		snmpVersion.setBounds(101, 63, 106, 20);
		contentPane.add(snmpVersion);

		/** INFO TEXT FIELDS */
		JTextField txtMachineAddress = new JTextField();
		txtMachineAddress.setHorizontalAlignment(SwingConstants.RIGHT);
		txtMachineAddress.setEnabled(false);
		txtMachineAddress.setEditable(false);
		txtMachineAddress.setText("Address");
		txtMachineAddress.setForeground(Color.WHITE);
		txtMachineAddress.setFont(new Font("Tahoma", Font.BOLD, 11));
		txtMachineAddress.setColumns(10);
		txtMachineAddress.setBackground(Color.GRAY);
		txtMachineAddress.setBounds(0, 0, 102, 20);
		contentPane.add(txtMachineAddress);

		JTextField txtSnmpPort = new JTextField();
		txtSnmpPort.setText("Port");
		txtSnmpPort.setHorizontalAlignment(SwingConstants.RIGHT);
		txtSnmpPort.setForeground(Color.WHITE);
		txtSnmpPort.setFont(new Font("Tahoma", Font.BOLD, 10));
		txtSnmpPort.setEnabled(false);
		txtSnmpPort.setEditable(false);
		txtSnmpPort.setColumns(10);
		txtSnmpPort.setBackground(Color.GRAY);
		txtSnmpPort.setBounds(0, 21, 102, 20);
		contentPane.add(txtSnmpPort);

		JTextField txtReadCommunity = new JTextField();
		txtReadCommunity.setText("Read Community");
		txtReadCommunity.setHorizontalAlignment(SwingConstants.RIGHT);
		txtReadCommunity.setForeground(Color.WHITE);
		txtReadCommunity.setFont(new Font("Tahoma", Font.BOLD, 10));
		txtReadCommunity.setEnabled(false);
		txtReadCommunity.setEditable(false);
		txtReadCommunity.setColumns(10);
		txtReadCommunity.setBackground(Color.GRAY);
		txtReadCommunity.setBounds(0, 42, 102, 20);
		contentPane.add(txtReadCommunity);

		JTextField txtSnmpVersion = new JTextField();
		txtSnmpVersion.setText("SNMP Version");
		txtSnmpVersion.setHorizontalAlignment(SwingConstants.RIGHT);
		txtSnmpVersion.setForeground(Color.WHITE);
		txtSnmpVersion.setFont(new Font("Tahoma", Font.BOLD, 10));
		txtSnmpVersion.setEnabled(false);
		txtSnmpVersion.setEditable(false);
		txtSnmpVersion.setColumns(10);
		txtSnmpVersion.setBackground(Color.GRAY);
		txtSnmpVersion.setBounds(0, 63, 102, 20);
		contentPane.add(txtSnmpVersion);

		/** BUTTONS */
		JButton okBtn = new JButton("OK");
		okBtn.setForeground(Color.LIGHT_GRAY);
		okBtn.setBackground(Color.DARK_GRAY);
		okBtn.setBounds(0, 84, 105, 31);
		contentPane.add(okBtn);

		JButton closeBtn = new JButton("CLOSE");
		closeBtn.setForeground(Color.LIGHT_GRAY);
		closeBtn.setBackground(Color.DARK_GRAY);
		closeBtn.setBounds(106, 84, 105, 31);
		contentPane.add(closeBtn);
		
		/** LISTENERS */
		okBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				okActionPerformed();
			}
		});
		closeBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelActionPerformed();
			}
		});

		try {
			getServerIP();
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(StartUpWindow.this, "connection.properties file writing/reading failed.",
					"ERROR", JOptionPane.ERROR_MESSAGE);
		}
	}

	protected void getServerIP() throws IOException {
		File file = new File("snmp.properties");
		if (!file.exists()) {
			file.createNewFile();
			String defaultData = "machineIp=127.0.0.1\n" + "snmpPort=161\n" + "readCommunity=community_rd\n" + "snmpVersion=2\n";
			FileWriter fileWritter = new FileWriter(file.getName(), true);
			BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
			bufferWritter.write(defaultData);
			bufferWritter.close();
		}
		try (FileReader reader = new FileReader("snmp.properties")) {
			Properties prop = new Properties();
			prop.load(reader);

			machineAddress.setText(prop.getProperty("machineIp"));
			snmpPort.setText(prop.getProperty("snmpPort"));
			readCommunity.setText(prop.getProperty("readCommunity"));
			snmpVersion.setText(prop.getProperty("snmpVersion"));
		}
	}

	private void okActionPerformed() {
		if (machineAddress.getText().isEmpty()) {
			JOptionPane.showMessageDialog(StartUpWindow.this, "Machine identifier needed.", "NEEDED",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		SNMPConnectionConfiguration conf = new SNMPConnectionConfiguration(machineAddress.getText(),
				Integer.valueOf(snmpPort.getText()), readCommunity.getText(), Integer.valueOf(snmpVersion.getText()));
		MainBrowserWindow frame = new MainBrowserWindow(conf);
		frame.setVisible(true);

		this.setVisible(false);
		this.dispose();
	}

	private void cancelActionPerformed() {
		this.dispose();
	}
}
