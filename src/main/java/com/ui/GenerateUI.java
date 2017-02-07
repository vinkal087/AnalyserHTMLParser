package com.ui;

import com.htmlutils.HTMLParser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class GenerateUI {

	private static final Color FFFF99 = null;
	private static String fileName =null;
	public static void main(String[] args) {
		final JFrame frame = new JFrame("HTML Parser");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel controlPanel = new JPanel();
		frame.add(controlPanel);
		GridLayout layout = new GridLayout(0,2);
		layout.setHgap(5);
		layout.setVgap(5);
		controlPanel.setLayout(layout);


		Panel text_panel1 = new Panel();
		Panel text_panel2 = new Panel();
		JLabel  namelabel= new JLabel("Unique ID", JLabel.RIGHT);
		namelabel.setPreferredSize(new Dimension(70,30));
		JLabel  namelabel1= new JLabel("SR", JLabel.RIGHT);
		namelabel1.setPreferredSize(new Dimension(30,30));
		final JTextField userText = new JTextField(15);
		final JTextField userText1 = new JTextField(15);
		userText.setPreferredSize(new Dimension(70,30));
		userText1.setPreferredSize(new Dimension(70,30));
		text_panel1.add(namelabel);
		text_panel2.add(namelabel1);
		text_panel1.add(userText);
		text_panel2.add(userText1);
		controlPanel.add(text_panel1);
		controlPanel.add(text_panel2);


		Panel panel3 = new Panel();
		//--------------------------------------------------------------------------------------------------------
		/// Radio Button 1
		JRadioButton diagnosticRadioButton = new JRadioButton("Diagnostics");
		diagnosticRadioButton.setSelected(true);
		JRadioButton analysersRadioButton = new JRadioButton("Analyzers");

		ButtonGroup group = new ButtonGroup();
		group.add(diagnosticRadioButton);
		group.add(analysersRadioButton);

		JPanel radioPanel = new JPanel(new GridLayout(0, 1));
		radioPanel.add(diagnosticRadioButton);
		radioPanel.add(analysersRadioButton);

		// Radio Button 2
		Panel panel4 = new Panel();
		JRadioButton databaseInsert = new JRadioButton("Insert To Database");
		JRadioButton spool = new JRadioButton("Spool");
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

		//--------------------------------------------------------------------------------------------------------
		JPanel panel5 = new JPanel();

		final JLabel fileNameText = new JLabel();
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
					JFileChooser jfc = new JFileChooser();
					int returnValue = jfc.showOpenDialog(null);
					final File finalSelectedFile = jfc.getSelectedFile();
					//System.out.print(finalSelectedFile.getAbsolutePath());
					fileName = finalSelectedFile.getAbsolutePath();
					fileNameText.setText(fileName);
				}
				catch(Exception e){
					e.printStackTrace();

				}
			}
		};
		browseButton.addActionListener(listenerBrowseButton);


		controlPanel.add(fileNameText);
		fileNameText.setText(fileName);

		JButton generateReportButton = new JButton("Generate Report");
		ActionListener listener = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				try {
					String uniqueString = userText.getText().trim();
					String srString = userText1.getText().trim();
					if(uniqueString.equals("") || srString.equals("")){
						JOptionPane.showMessageDialog(new JFrame(), "Please Fill UniqueID and SR", "Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					if(fileName==null || fileName.trim().equals("")){
						JOptionPane.showMessageDialog(new JFrame(), "Please select file", "Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					boolean insertToDB = databaseInsert.isSelected();
					try {
						boolean success = HTMLParser.parseHTML(fileName.trim(), srString + "-" + uniqueString, insertToDB, analysersRadioButton.isSelected());
						if(success){
							JOptionPane.showMessageDialog(new JFrame(), "File succesfully processed", "SUCESS",
									JOptionPane.INFORMATION_MESSAGE);
						}
					}
					catch(Exception e){
						JOptionPane.showMessageDialog(new JFrame(), e.getMessage(), "Error",
								JOptionPane.ERROR_MESSAGE);
					}
				}
				catch(Exception e){
					e.printStackTrace();

				}
			}
		};
		generateReportButton.addActionListener(listener);

		controlPanel.add(generateReportButton, BorderLayout.NORTH);

		JButton cancelButton = new JButton("Cancel");
		listener = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {

				frame.dispose();
			}
		};
		cancelButton.addActionListener(listener);

		controlPanel.add(cancelButton, BorderLayout.SOUTH);
		frame.pack();
		frame.show();

	}

}