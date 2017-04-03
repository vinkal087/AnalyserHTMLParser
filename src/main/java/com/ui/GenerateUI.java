package com.ui;

import com.htmlutils.HTMLParser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ResourceBundle;

public class GenerateUI {

	private static final Color FFFF99 = null;
	private static String fileName =null;
	private static JTextField userText =null;
	private static JTextField userText1 = null;
	private static JRadioButton databaseInsert = null;
	private static JRadioButton spool = null;
	private static JRadioButton diagnosticRadioButton = null;
	private static JRadioButton analysersRadioButton = null;
	private static JRadioButton analysersRadioButtonNewFormat = null;
	private static JTextArea fileNameText = null;
	private static String[] fileNamesAnalyzer = {"TransactionAnalyzer", "AdjustmentAnalyzer", "ReceiptAnalyzer" };
	private static String[] fileNamesDiagnostics= {"TransactionDiagnostics", "AdjustmentDiagnostics", "ReceiptDiagnostics"};
	private static JComboBox dropDown = null;
	private static JLabel fileProcessingLabel = null;
	private static ResourceBundle bundle = ResourceBundle.getBundle("general");
	public static void main(String[] args) {
		final JFrame frame = new JFrame("HTML Parser");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setMinimumSize(new Dimension(1150,100));

		JPanel controlPanel = new JPanel();
		frame.add(controlPanel);
		GridLayout layout = new GridLayout(0,2);
		layout.setHgap(5);
		layout.setVgap(5);
		controlPanel.setLayout(layout);

		textPanels(controlPanel);

		radioButtonPanels(controlPanel,frame);
		browseButton(controlPanel);
		createDropDown(controlPanel);
		generateAndCancelButton(controlPanel, frame);

		frame.pack();
		frame.show();

	}

	private static void textPanels(JPanel controlPanel){
		Panel text_panel1 = new Panel();
		Panel text_panel2 = new Panel();
		JLabel  namelabel= new JLabel("Unique ID", JLabel.RIGHT);
		namelabel.setPreferredSize(new Dimension(70,30));
		JLabel  namelabel1= new JLabel("SR", JLabel.RIGHT);
		namelabel1.setPreferredSize(new Dimension(30,30));
		userText = new JTextField(15);
		userText1 = new JTextField(15);
		userText.setPreferredSize(new Dimension(70,30));
		userText1.setPreferredSize(new Dimension(70,30));
		text_panel1.add(namelabel);
		text_panel2.add(namelabel1);
		text_panel1.add(userText);
		text_panel2.add(userText1);
		controlPanel.add(text_panel2);
		controlPanel.add(text_panel1);

	}

	private static void radioButtonPanels(JPanel controlPanel,JFrame frame ){
		Panel panel3 = new Panel();
		//--------------------------------------------------------------------------------------------------------
		/// Radio Button 1
		diagnosticRadioButton = new JRadioButton("Diagnostics");
		diagnosticRadioButton.setSelected(true);
		analysersRadioButton = new JRadioButton("Analyzers");
		analysersRadioButtonNewFormat = new JRadioButton("AnalyzersNewFormat");
		ButtonGroup group = new ButtonGroup();
		group.add(diagnosticRadioButton);
		group.add(analysersRadioButton);
		group.add(analysersRadioButtonNewFormat);

		JPanel radioPanel = new JPanel(new GridLayout(0, 1));
		radioPanel.add(diagnosticRadioButton);
		radioPanel.add(analysersRadioButton);
		radioPanel.add(analysersRadioButtonNewFormat);

		// Radio Button 2
		Panel panel4 = new Panel();
		databaseInsert = new JRadioButton("Insert To Database");
		spool = new JRadioButton("Spool");
		databaseInsert.setSelected(true);
		ButtonGroup group1 = new ButtonGroup();
		group1.add(databaseInsert);
		group1.add(spool);

		JPanel radioPanel1 = new JPanel(new GridLayout(0, 1));
		radioPanel1.add(databaseInsert);
		radioPanel1.add(spool);

		panel3.add(radioPanel);
		panel4.add(radioPanel1);
		controlPanel.add(panel3);
		controlPanel.add(panel4);

		ActionListener radioListener = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				try {
					String ac = actionEvent.getActionCommand();
					if(ac.contains("NONANALYZERS")){
						dropDown.removeAllItems();
						for(int i=0;i<fileNamesDiagnostics.length;i++){
							dropDown.addItem(fileNamesDiagnostics[i]);
						}
					}
					else{
						dropDown.removeAllItems();
						for(int i=0;i<fileNamesAnalyzer.length;i++){
							dropDown.addItem(fileNamesAnalyzer[i]);
						}
					}
					frame.update(frame.getGraphics());
				}
				catch(Exception e){
					e.printStackTrace();

				}
			}
		};
		diagnosticRadioButton.setActionCommand("NONANALYZERS");
		analysersRadioButton.setActionCommand("ANALYZERS");
		analysersRadioButtonNewFormat.setActionCommand("ANALYZERS");
		diagnosticRadioButton.addActionListener(radioListener);
		analysersRadioButton.addActionListener(radioListener);
		analysersRadioButtonNewFormat.addActionListener(radioListener);
	}

	private static void browseButton(JPanel controlPanel){
		//--------------------------------------------------------------------------------------------------------
		JPanel panel5 = new JPanel();

		fileNameText = new JTextArea();
		fileNameText.setLineWrap(true);
		fileNameText.setEditable(false);
		fileNameText.setOpaque(false);

		//fileNameText.setVisible(true);
		//fileNameText.setBounds(10,30,300,100);

		Button browseButton = new Button("Select File");
		browseButton.setForeground(new Color(0,0,255));
		browseButton.setSize(100, 8);
		panel5.setLayout(new FlowLayout());

		//controlPanel.add(browseButton);
		panel5.add(browseButton, BorderLayout.WEST);
		panel5.add(fileNameText, BorderLayout.WEST);
		controlPanel.add(panel5);

		//controlPanel.add(panel6);
		ActionListener listenerBrowseButton = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				try {
					//UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					File workingDirectory = new File(bundle.getString("BROWSE_FILES_PATH"));
					JFileChooser jfc = new JFileChooser();
					jfc.setCurrentDirectory(workingDirectory);
					int returnValue = jfc.showOpenDialog(null);
					final File finalSelectedFile = jfc.getSelectedFile();
					//System.out.print(finalSelectedFile.getAbsolutePath());
					fileName = finalSelectedFile.getAbsolutePath();
					fileNameText.setText(fileName);
					fileProcessingLabel.setText("");
				}
				catch(Exception e){
					e.printStackTrace();

				}
			}
		};
		browseButton.addActionListener(listenerBrowseButton);


		controlPanel.add(fileNameText);
		fileNameText.setText(fileName);
	}

	private static void generateAndCancelButton(JPanel controlPanel,JFrame frame ){
		JButton generateReportButton = new JButton("Generate Report");
		JButton cancelButton = new JButton("Cancel");
		ActionListener cancelListener = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {

				frame.dispose();
			}
		};
		cancelButton.addActionListener(cancelListener);
		ActionListener listener = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				try {
					String uniqueString = userText.getText().trim();
					String srString = userText1.getText().trim();
					if(uniqueString.equals("") && srString.equals("")){
						JOptionPane.showMessageDialog(new JFrame(), "Please Fill UniqueID and SR", "Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					if(fileName==null || fileName.trim().equals("") || fileNameText.getText().toString().contains("Sucessfully Processed")){
						JOptionPane.showMessageDialog(new JFrame(), "Please select file", "Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					boolean insertToDB = databaseInsert.isSelected();
					cancelButton.setText("Processing...");
					cancelButton.removeActionListener(cancelListener);
					frame.update(frame.getGraphics());
					try {
						String keyy="";
						if(srString==null || srString.equalsIgnoreCase("")){
							keyy=uniqueString;
						}
						else if(uniqueString==null || uniqueString.equalsIgnoreCase("")){
							keyy=srString;
						}
						else{
							keyy=srString  + uniqueString;
						}
						boolean success = HTMLParser.parseHTML(fileName.trim(), keyy, insertToDB, getSelectedRadio(), dropDown.getSelectedItem().toString());
						if(success){
							fileProcessingLabel.setText("File Sucessfully Processed");
							//JOptionPane.showMessageDialog(new JFrame(), "File succesfully processed", "SUCESS",
							//		JOptionPane.INFORMATION_MESSAGE);
						}
					}
					catch(Exception e){
						fileProcessingLabel.setText(e.getMessage());
						JOptionPane.showMessageDialog(new JFrame(), e.getMessage(), "Error",
								JOptionPane.ERROR_MESSAGE);
					}
					cancelButton.setText("Cancel");
					frame.update(frame.getGraphics());
					cancelButton.addActionListener(cancelListener);
				}
				catch(Exception e){
					e.printStackTrace();

				}
			}
		};
		generateReportButton.addActionListener(listener);

		controlPanel.add(generateReportButton, BorderLayout.NORTH);

		controlPanel.add(cancelButton, BorderLayout.SOUTH);
	}

	private static void createDropDown(JPanel controlPanel){
		JPanel panel = new JPanel();
		JLabel selectPropertyFileLabel = new JLabel();
		selectPropertyFileLabel.setText("Select property File");
		panel.add(selectPropertyFileLabel);
		dropDown = new JComboBox();
		for(int i=0;i< fileNamesDiagnostics.length;i++)
			dropDown.addItem(fileNamesDiagnostics[i]);
		panel.add(dropDown);
		panel.setLayout(new FlowLayout());

		fileProcessingLabel = new JLabel();
		panel.add(fileProcessingLabel);

		controlPanel.add(panel);
		controlPanel.add(fileProcessingLabel);

	}

	private static String getSelectedRadio(){
		if(diagnosticRadioButton.isSelected())return "DIAGNOSTICS";
		else if(analysersRadioButton.isSelected())return "ANALYZERS";
		else if(analysersRadioButtonNewFormat.isSelected())return "ANALYZERS_NEW_FORMAT";
		return null;

	}

}