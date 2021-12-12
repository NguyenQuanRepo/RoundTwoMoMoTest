package vending_machine_simulator;
import java.awt.EventQueue;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.GridLayout;
import java.awt.Image;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.Component;

import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import java.awt.Insets;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.BevelBorder;
import java.awt.FlowLayout;
import javax.swing.border.TitledBorder;

import entity.InfoPromotion;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class VendingMachine {
	
	private Color orange = new Color(255, 142, 35);		
	private Item[] itemList;
	private int codeCnt;
	private ArrayList<Integer> code;
	private StringBuilder codeStr;
	private int cashInserted;
	private int change;
	private Item chosenItem;
	private int statusPromotion;	//1: prevLitmitBudget < 50000;
									//2: prevLitmitBudget == 50000;
	private int currentLimitedBudget;
	private ArrayList<Item> consecutiveItems = new ArrayList<Item>();	// Array contains items with 3 consecutive purchases
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VendingMachine machine = new VendingMachine();
					machine.frame.setVisible(true);
					machine.frame.setLocationRelativeTo(null); 
					machine.initPromotion();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private void initPromotion() {
		Calendar calendar = Calendar.getInstance();
        int currentDay = calendar.get(Calendar.DATE);
        int currentMonth  = calendar.get(Calendar.MONTH) + 1;
        int currentYear  = calendar.get(Calendar.YEAR);
        
        InfoPromotion infoPromote = Utils.readFile("InfoPromotion.txt");
        if(infoPromote != null) {
        	String prevDate[] = infoPromote.getDate().split("/");
        	int prevDay = Integer.valueOf(prevDate[0]);
        	
        	if(prevDay <= currentDay && infoPromote.getPrevLimitedBudget() < 50000) {
        		statusPromotion = 1; // 50% win rate
        	}
        	else if(prevDay <= currentDay && infoPromote.getPrevLimitedBudget() == 50000) {
        		statusPromotion = 2; // 10% win rate
        	}
        	
        }
        setPromtionMessage(statusPromotion);
	}
	
	public void setPromtionMessage(int status) {
		switch (status) {
		case 1:
			btnPromtionMess.setText("Today will have 50% chance to receive " + 
					"product for free!!!");
			break;
		case 2:
			btnPromtionMess.setText("Today will have 10% chance to receive " + 
					"product for free!!!");
			break;
		
		default:
			btnPromtionMess.setText("Promotion's today was end!!!");
			break;
		}
	}
	
	private void updateCode() {
		for(int i=0; i<code.size(); i++) {
			if(code.get(i)==-1)
				codeStr.setCharAt(i*2, '_');
			else
				codeStr.setCharAt(i*2, code.get(i).toString().charAt(0));
		}
		lbCode.setText(codeStr.toString());
	}
	
	private void enableCashPanel(boolean on) {
		Component[] comp = panel_9.getComponents();
		if (on) {
			for (int i=0; i<comp.length; i++) {
				if (comp[i] instanceof  JButton) {
					comp[i].setEnabled(true);
				}
			}
		}
		else {
			for (int i=0; i<comp.length; i++) {
				if (comp[i] instanceof  JButton) {
					comp[i].setEnabled(false);
				}
			}
		}
	}
	
	
	
	private void reset() {
		payPanel.setVisible(false);
		selectPanel.setVisible(true);
		cashInserted = 0;
		change = 0;
		codeCnt = 0;
		currentLimitedBudget = 0;
		code = new ArrayList<>(Arrays.asList(-1,-1,-1));	
		updateCode();
		label_17.setVisible(false);
		lbInserted_1.setVisible(false);
		btnCancel_1.setEnabled(true);
		btnNext.setEnabled(true);
		btnNext.setBorder(new LineBorder(Color.CYAN, 3, true));
		btnCancel_1.setBorder(new LineBorder(Color.RED, 3, true));
		btnCancel_1.setForeground(Color.RED);
		lbInserted_1.setText(String.valueOf(cashInserted));
		lbInserted_2.setText(String.valueOf(cashInserted));
		lbInserted_1.setVisible(true);
		lbCollect.setText("Collect Item Here");
		lbCollect.setForeground(Color.WHITE);
		panel_9.setVisible(false);
		lbInserted_1.setVisible(false);
		btnBuy.setEnabled(false);
		btnCancelPurchase.setEnabled(true);
		btnBack.setEnabled(true);
		btnBuy.setForeground(Color.CYAN);
		btnBuy.setBorder(new LineBorder(Color.GRAY, 3, true));
		btnCancelPurchase.setBorder(new LineBorder(Color.RED, 3, true));
		btnCancelPurchase.setForeground(Color.RED);
		btnBack.setBorder(new LineBorder(Color.ORANGE, 3, true));
		panel_10.setBackground(Color.ORANGE);
		panel_10.setBorder(new LineBorder(Color.ORANGE, 3, true));
		btnGetChange.setVisible(true);
		enableCashPanel(true);
	}
	

	/**
	 * Create the application.
	 */
	public VendingMachine() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		codeCnt = 0;
		code = new ArrayList<>(Arrays.asList(-1,-1,-1));
		codeStr = new StringBuilder("_ _ _");
		itemList = new Item[]
			{new Item("coca-cola", 001, 10000), new Item("pepsi", 002, 10000), new Item("soda", 003, 20000)};
		cashInserted = 0;
		change = 0;
		
		frame = new JFrame();
		frame.setTitle("VENDING MACHINE");
		frame.getContentPane().setBackground(Color.LIGHT_GRAY);
		frame.setBounds(0, 0, 562, 784);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.getContentPane().setLayout(null);
		
		JPanel tapPanel = new JPanel();
		tapPanel.setBounds(188, 25, 180, 727);
		tapPanel.setBackground(Color.ORANGE);
		frame.getContentPane().add(tapPanel);
		tapPanel.setLayout(null);
		URL imgURL = getClass().getResource("/image/backspace_orange.png");
		// System.out.println(imgURL);
		ImageIcon bkImage = new ImageIcon(new ImageIcon(imgURL).getImage().getScaledInstance(30, 35, Image.SCALE_DEFAULT));
		
		JPanel insertPanel = new JPanel();
		insertPanel.setBackground(Color.DARK_GRAY);
		insertPanel.setBounds(0, 541, 180, 109);
		tapPanel.add(insertPanel);
		insertPanel.setLayout(null);
		
		panel_9 = new JPanel();
		panel_9.setBounds(0, 0, 180, 109);
		insertPanel.add(panel_9);
		panel_9.setBackground(Color.DARK_GRAY);
		panel_9.setVisible(false);
		
		btnTen = new JButton("10.000");
		btnTen.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		btnTen.setBorder(new LineBorder(Color.WHITE, 2, true));
		btnTen.setContentAreaFilled(false);
		btnTen.setForeground(Color.WHITE);
		btnTen.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				cashInserted+=10000;
				lbInserted_1.setText(String.valueOf(cashInserted));
				lbInserted_2.setText(String.valueOf(cashInserted));
				if (cashInserted>=chosenItem.getPrice()) {
					btnBuy.setEnabled(true);
					btnBuy.setBorder(new LineBorder(Color.CYAN, 3, true));
					enableCashPanel(false);
				}
			}
		});
		panel_9.setLayout(new GridLayout(0, 2, 0, 0));
		panel_9.add(btnTen);
		
		btnTwenty = new JButton("20.000");
		btnTwenty.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		btnTwenty.setBorder(new LineBorder(Color.WHITE, 2, true));
		btnTwenty.setForeground(Color.WHITE);
		btnTwenty.setContentAreaFilled(false);
		btnTwenty.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (!btnTwenty.isEnabled()) {return;}
				cashInserted+=20000;
				lbInserted_1.setText(String.valueOf(cashInserted));
				lbInserted_2.setText(String.valueOf(cashInserted));
				if (cashInserted>=chosenItem.getPrice()) {
					btnBuy.setEnabled(true);
					btnBuy.setBorder(new LineBorder(Color.CYAN, 3, true));
					btnTwenty.setEnabled(false);
					enableCashPanel(false);
				}
			}
		});
		panel_9.add(btnTwenty);
		
		btnFifty = new JButton("50.000");
		btnFifty.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		btnFifty.setBorder(new LineBorder(Color.WHITE, 2, true));
		btnFifty.setForeground(Color.WHITE);
		btnFifty.setContentAreaFilled(false);
		btnFifty.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (!btnFifty.isEnabled()) {return;}
				cashInserted+=50000;
				lbInserted_1.setText(String.valueOf(cashInserted));
				lbInserted_2.setText(String.valueOf(cashInserted));
				if (cashInserted>=chosenItem.getPrice()) {
					btnBuy.setEnabled(true);
					btnBuy.setBorder(new LineBorder(Color.CYAN, 3, true));
					enableCashPanel(false);
				}
			}
		});
		panel_9.add(btnFifty);
		
		btnOneHunred = new JButton("100.000");
		btnOneHunred.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		btnOneHunred.setBorder(new LineBorder(Color.WHITE, 2, true));
		btnOneHunred.setForeground(Color.WHITE);
		btnOneHunred.setContentAreaFilled(false);
		btnOneHunred.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (!btnOneHunred.isEnabled()) {return;}
				cashInserted+=100000;
				lbInserted_1.setText(String.valueOf(cashInserted));
				lbInserted_2.setText(String.valueOf(cashInserted));
				if (cashInserted>=chosenItem.getPrice()) {
					btnBuy.setEnabled(true);
					btnBuy.setBorder(new LineBorder(Color.CYAN, 3, true));
					enableCashPanel(false);
				}
			}
		});
		panel_9.add(btnOneHunred);
		
		btnTwoHundred = new JButton("200.000");
		btnTwoHundred.setFont(new Font("Times New Roman", Font.PLAIN, 20));
		btnTwoHundred.setBorder(new LineBorder(Color.WHITE, 2, true));
		btnTwoHundred.setForeground(Color.WHITE);
		btnTwoHundred.setContentAreaFilled(false);
		btnTwoHundred.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (!btnTwoHundred.isEnabled()) {return;}
				cashInserted+=200000;
				lbInserted_1.setText(String.valueOf(cashInserted));
				lbInserted_2.setText(String.valueOf(cashInserted));
				if (cashInserted>=chosenItem.getPrice()) {
					btnBuy.setEnabled(true);
					btnBuy.setBorder(new LineBorder(Color.CYAN, 3, true));
					enableCashPanel(false);
				}
			}
		});
		panel_9.add(btnTwoHundred);
		
		selectPanel = new JPanel();
		selectPanel.setBounds(0, 0, 180, 513);
		tapPanel.add(selectPanel);
		selectPanel.setBackground(Color.DARK_GRAY);
		selectPanel.setLayout(null);
		
		JPanel codeDispalyPanel = new JPanel();
		codeDispalyPanel.setBackground(Color.DARK_GRAY);
		codeDispalyPanel.setBounds(14, 30, 152, 30);
		selectPanel.add(codeDispalyPanel);
		codeDispalyPanel.setLayout(null);
		
		lbCode = new JLabel(codeStr.toString());
		lbCode.setForeground(Color.WHITE);
		lbCode.setBounds(0, 0, 152, 30);
		codeDispalyPanel.add(lbCode);
		lbCode.setFont(new Font("Arial Black", Font.BOLD, 30));
		lbCode.setHorizontalAlignment(SwingConstants.CENTER);
		
		label_17 = new JLabel("Cash Inserted:");
		label_17.setForeground(Color.ORANGE);
		label_17.setHorizontalAlignment(SwingConstants.CENTER);
		label_17.setFont(new Font("Arial Black", Font.PLAIN, 20));
		label_17.setBounds(0, 302, 180, 18);
		label_17.setVisible(false);
		selectPanel.add(label_17);
		
				lbInserted_1 = new JLabel("0 VND");
				lbInserted_1.setForeground(Color.WHITE);
				lbInserted_1.setHorizontalAlignment(SwingConstants.CENTER);
				lbInserted_1.setFont(new Font("Arial Black", Font.PLAIN, 20));
				lbInserted_1.setBounds(6, 336, 160, 29);
				lbInserted_1.setText("0 VND");
				lbInserted_1.setVisible(false);
				selectPanel.add(lbInserted_1);
				
				btnNext = new JButton("NEXT");
				btnNext.setForeground(Color.CYAN);
				btnNext.setFocusPainted(false);
				btnNext.setContentAreaFilled(false);
				btnNext.setBorder(new LineBorder(Color.CYAN, 3, true));
				btnNext.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						try {
							int chosenCode = code.get(0)*100 + code.get(1)*10 + code.get(2);
							chosenItem = itemList[chosenCode-1];
							
							selectPanel.setVisible(false);
							payPanel.setVisible(true);
							lbPrice.setText(String.valueOf(chosenItem.getPrice()));
							btnItem.setText(chosenItem.getName().toUpperCase());
							panel_9.setVisible(true);
							if (cashInserted>=chosenItem.getPrice()) {
								btnBuy.setEnabled(true);
								btnBuy.setBorder(new LineBorder(Color.CYAN, 3, true));
								enableCashPanel(false);
							}
							if (cashInserted<chosenItem.getPrice()) {
								btnBuy.setEnabled(false);
								btnBuy.setBorder(new LineBorder(Color.GRAY, 3, true));
								enableCashPanel(true);
							}
						}
						catch(IndexOutOfBoundsException err) {
							lbCode.setFont(new Font("Arial Black", Font.BOLD, 20));
							lbCode.setForeground(Color.RED);
							lbCode.setText("Invalid Code");
								
							TimerTask restore = new TimerTask() {
								public void run() {
									lbCode.setFont(new Font("Arial Black", Font.BOLD, 30));
									lbCode.setForeground(Color.WHITE);
									code = new ArrayList<>(Arrays.asList(-1,-1,-1));
									codeStr = new StringBuilder("_ _ _");
									codeCnt = 0;
									updateCode();
								}
							};
							Timer timer = new Timer("Delay");
							timer.schedule(restore, 1000L);
							// System.out.println(code.get(0) + " " + code.get(1) + " " + code.get(2));
						}
					}
					@Override
					public void mouseEntered(MouseEvent e) {
						if (btnNext.isEnabled()) {
							btnNext.setBackground(Color.CYAN);
							btnNext.setContentAreaFilled(true);
							btnNext.setForeground(Color.WHITE);
						}
					}
					@Override
					public void mouseExited(MouseEvent e) {
						if (btnNext.isEnabled()) {
							btnNext.setContentAreaFilled(false);
							btnNext.setForeground(Color.CYAN);
						}
					}

				});
				
						btnNext.setFont(new Font("Arial Black", Font.PLAIN, 15));
						btnNext.setBounds(14, 378, 152, 30);
						selectPanel.add(btnNext);
						
						JPanel keyPanel = new JPanel();
						keyPanel.setBackground(Color.DARK_GRAY);
						keyPanel.setBounds(14, 80, 152, 211);
						selectPanel.add(keyPanel);
						keyPanel.setLayout(new GridLayout(4, 3, 2, 2));
						
						JButton button_1 = new JButton("1");
						button_1.setBorder(new LineBorder(Color.ORANGE, 2, true));
						button_1.setFocusPainted(false);
						button_1.setContentAreaFilled(false);
						button_1.setForeground(Color.ORANGE);
						button_1.setFont(new Font("Arial Black", Font.BOLD, 20));
						button_1.addMouseListener(new MouseAdapter() {
							@Override
							public void mousePressed(MouseEvent e) {
								if(codeCnt==3) {
									
								}
								else {
									code.set(codeCnt, 1);
									codeCnt++;
									updateCode();
								}
							}
							@Override
							public void mouseEntered(MouseEvent e) {
								button_1.setBackground(Color.ORANGE);
								button_1.setContentAreaFilled(true);
								button_1.setForeground(Color.WHITE);
							}
							@Override
							public void mouseExited(MouseEvent e) {
								button_1.setContentAreaFilled(false);
								button_1.setForeground(Color.ORANGE);
							}
						});
						keyPanel.add(button_1);
						
						JButton button_2 = new JButton("2");
						button_2.setBorder(new LineBorder(new Color(255, 200, 0), 2, true));
						button_2.setFocusPainted(false);
						button_2.setContentAreaFilled(false);
						button_2.setForeground(Color.ORANGE);
						button_2.setFont(new Font("Arial Black", Font.BOLD, 20));
						button_2.addMouseListener(new MouseAdapter() {
							@Override
							public void mousePressed(MouseEvent e) {
								if(codeCnt==3) {
									
								}
								else {
									code.set(codeCnt, 2);
									codeCnt++;
									updateCode();
								}
							}
							@Override
							public void mouseEntered(MouseEvent e) {
								button_2.setBackground(Color.ORANGE);
								button_2.setContentAreaFilled(true);
								button_2.setForeground(Color.WHITE);
							}
							@Override
							public void mouseExited(MouseEvent e) {
								button_2.setContentAreaFilled(false);
								button_2.setForeground(Color.ORANGE);
							}
						});
						keyPanel.add(button_2);
						
						JButton button_3 = new JButton("3");
						button_3.setForeground(Color.ORANGE);
						button_3.setFocusPainted(false);
						button_3.setBorder(new LineBorder(Color.ORANGE, 2, true));
						button_3.setFont(new Font("Arial Black", Font.BOLD, 20));
						button_3.setContentAreaFilled(false);
						button_3.addMouseListener(new MouseAdapter() {
							@Override
							public void mousePressed(MouseEvent e) {
								if(codeCnt==3) {
									
								}
								else {
									code.set(codeCnt, 3);
									codeCnt++;
									updateCode();
								}
							}
							@Override
							public void mouseEntered(MouseEvent e) {
								button_3.setBackground(Color.ORANGE);
								button_3.setContentAreaFilled(true);
								button_3.setForeground(Color.WHITE);
							}
							@Override
							public void mouseExited(MouseEvent e) {
								button_3.setContentAreaFilled(false);
								button_3.setForeground(Color.ORANGE);
							}
						});
						keyPanel.add(button_3);
						
						JButton button_4 = new JButton("4");
						button_4.setForeground(Color.ORANGE);
						button_4.setBorder(new LineBorder(Color.ORANGE, 2, true));
						button_4.setFocusPainted(false);
						button_4.setFont(new Font("Arial Black", Font.BOLD, 20));
						button_4.setContentAreaFilled(false);
						button_4.addMouseListener(new MouseAdapter() {
							@Override
							public void mousePressed(MouseEvent e) {
								if(codeCnt==3) {
									
								}
								else {
									code.set(codeCnt, 4);
									codeCnt++;
									updateCode();
								}
							}
							@Override
							public void mouseEntered(MouseEvent e) {
								button_4.setBackground(Color.ORANGE);
								button_4.setContentAreaFilled(true);
								button_4.setForeground(Color.WHITE);
							}
							@Override
							public void mouseExited(MouseEvent e) {
								button_4.setContentAreaFilled(false);
								button_4.setForeground(Color.ORANGE);
							}
						});
						keyPanel.add(button_4);
						
						JButton button_5 = new JButton("5");
						button_5.setForeground(Color.ORANGE);
						button_5.setBorder(new LineBorder(Color.ORANGE, 2, true));
						button_5.setFocusPainted(false);
						button_5.setFont(new Font("Arial Black", Font.BOLD, 20));
						button_5.setContentAreaFilled(false);
						button_5.addMouseListener(new MouseAdapter() {
							@Override
							public void mousePressed(MouseEvent e) {
								if(codeCnt==3) {
									
								}
								else {
									code.set(codeCnt, 5);
									codeCnt++;
									updateCode();
								}
							}
							@Override
							public void mouseEntered(MouseEvent e) {
								button_5.setBackground(Color.ORANGE);
								button_5.setContentAreaFilled(true);
								button_5.setForeground(Color.WHITE);
							}
							@Override
							public void mouseExited(MouseEvent e) {
								button_5.setContentAreaFilled(false);
								button_5.setForeground(Color.ORANGE);
							}
						});
						keyPanel.add(button_5);
						
						JButton button_6 = new JButton("6");
						button_6.setForeground(Color.ORANGE);
						button_6.setBorder(new LineBorder(Color.ORANGE, 2, true));
						button_6.setFocusPainted(false);
						button_6.setFont(new Font("Arial Black", Font.BOLD, 20));
						button_6.setContentAreaFilled(false);
						button_6.addMouseListener(new MouseAdapter() {
							@Override
							public void mousePressed(MouseEvent e) {
								if(codeCnt==3) {
									
								}
								else {
									code.set(codeCnt, 6);
									codeCnt++;
									updateCode();
								}
							}
							@Override
							public void mouseEntered(MouseEvent e) {
								button_6.setBackground(Color.ORANGE);
								button_6.setContentAreaFilled(true);
								button_6.setForeground(Color.WHITE);
							}
							@Override
							public void mouseExited(MouseEvent e) {
								button_6.setContentAreaFilled(false);
								button_6.setForeground(Color.ORANGE);
							}
						});
						keyPanel.add(button_6);
						
						JButton button_7 = new JButton("7");
						button_7.setForeground(Color.ORANGE);
						button_7.setBorder(new LineBorder(Color.ORANGE, 2, true));
						button_7.setFont(new Font("Arial Black", Font.BOLD, 20));
						button_7.setFocusPainted(false);
						button_7.setContentAreaFilled(false);
						button_7.addMouseListener(new MouseAdapter() {
							@Override
							public void mousePressed(MouseEvent e) {
								if(codeCnt==3) {
									
								}
								else {
									code.set(codeCnt, 7);
									codeCnt++;
									updateCode();
								}
							}
							@Override
							public void mouseEntered(MouseEvent e) {
								button_7.setBackground(Color.ORANGE);
								button_7.setContentAreaFilled(true);
								button_7.setForeground(Color.WHITE);
							}
							@Override
							public void mouseExited(MouseEvent e) {
								button_7.setContentAreaFilled(false);
								button_7.setForeground(Color.ORANGE);
							}
						});
						keyPanel.add(button_7);
						
						JButton button_8 = new JButton("8");
						button_8.setForeground(Color.ORANGE);
						button_8.setFocusPainted(false);
						button_8.setBorder(new LineBorder(Color.ORANGE, 2, true));
						button_8.setFont(new Font("Arial Black", Font.BOLD, 20));
						button_8.setContentAreaFilled(false);
						button_8.addMouseListener(new MouseAdapter() {
							@Override
							public void mousePressed(MouseEvent e) {
								if(codeCnt==3) {
									
								}
								else {
									code.set(codeCnt, 8);
									codeCnt++;
									updateCode();
								}
							}
							@Override
							public void mouseEntered(MouseEvent e) {
								button_8.setBackground(Color.ORANGE);
								button_8.setContentAreaFilled(true);
								button_8.setForeground(Color.WHITE);
							}
							@Override
							public void mouseExited(MouseEvent e) {
								button_8.setContentAreaFilled(false);
								button_8.setForeground(Color.ORANGE);
							}
						});
						keyPanel.add(button_8);
						
						JButton button_9 = new JButton("9");
						button_9.setForeground(Color.ORANGE);
						button_9.setFocusPainted(false);
						button_9.setBorder(new LineBorder(Color.ORANGE, 2, true));
						button_9.setFont(new Font("Arial Black", Font.BOLD, 20));
						button_9.setContentAreaFilled(false);
						button_9.addMouseListener(new MouseAdapter() {
							@Override
							public void mousePressed(MouseEvent e) {
								if(codeCnt==3) {
									
								}
								else {
									code.set(codeCnt, 9);
									codeCnt++;
									updateCode();
								}
							}
							@Override
							public void mouseEntered(MouseEvent e) {
								button_9.setBackground(Color.ORANGE);
								button_9.setContentAreaFilled(true);
								button_9.setForeground(Color.WHITE);
							}
							@Override
							public void mouseExited(MouseEvent e) {
								button_9.setContentAreaFilled(false);
								button_9.setForeground(Color.ORANGE);
							}
						});
						keyPanel.add(button_9);
						
						JButton button_null = new JButton("");
						keyPanel.add(button_null);
						button_null.setBorderPainted(false);
						button_null.setContentAreaFilled(false);
						button_null.setFocusPainted(false);
						
						JButton button_0 = new JButton("0");
						button_0.setForeground(Color.ORANGE);
						button_0.setFocusPainted(false);
						button_0.setBorder(new LineBorder(Color.ORANGE, 2, true));
						button_0.setFont(new Font("Arial Black", Font.BOLD, 20));
						button_0.setContentAreaFilled(false);
						button_0.addMouseListener(new MouseAdapter() {
							@Override
							public void mousePressed(MouseEvent e) {
								if(codeCnt==3) {
									
								}
								else {
									code.set(codeCnt, 0);
									codeCnt++;
									updateCode();
								}
							}
							@Override
							public void mouseEntered(MouseEvent e) {
								button_0.setBackground(Color.ORANGE);
								button_0.setContentAreaFilled(true);
								button_0.setForeground(Color.WHITE);
							}
							@Override
							public void mouseExited(MouseEvent e) {
								button_0.setContentAreaFilled(false);
								button_0.setForeground(Color.ORANGE);
							}
						});
						keyPanel.add(button_0);
						
						JButton button_bk = new JButton("");
						button_bk.setBorder(new LineBorder(Color.ORANGE, 2, true));
						button_bk.setBackground(Color.DARK_GRAY);
						button_bk.setFocusPainted(false);
						button_bk.setContentAreaFilled(false);
						button_bk.setIcon(bkImage);
						button_bk.addMouseListener(new MouseAdapter() {
							@Override
							public void mousePressed(MouseEvent e) {
								if (codeCnt==0) {
									
								}
								else {
									code.set(--codeCnt, -1);
									updateCode();
								}
							}
							@Override
							public void mouseEntered(MouseEvent e) {
								button_bk.setBackground(Color.ORANGE);
								button_bk.setContentAreaFilled(true);
								button_bk.setForeground(Color.WHITE);
							}
							@Override
							public void mouseExited(MouseEvent e) {
								button_bk.setContentAreaFilled(false);
								button_bk.setForeground(Color.ORANGE);
							}
						});
						keyPanel.add(button_bk);
						
						JLabel lblNewLabel_1 = new JLabel("Enter Item Code:");
						lblNewLabel_1.setForeground(Color.ORANGE);
						lblNewLabel_1.setFont(new Font("Arial Black", Font.PLAIN, 15));
						lblNewLabel_1.setBounds(14, 0, 152, 30);
						selectPanel.add(lblNewLabel_1);
						
						btnCancel_1 = new JButton("Cancel");
						btnCancel_1.setForeground(Color.RED);
						btnCancel_1.setContentAreaFilled(false);
						btnCancel_1.setFocusPainted(false);
						btnCancel_1.setBorder(new LineBorder(Color.RED, 3, true));
						btnCancel_1.addMouseListener(new MouseAdapter() {
							@Override
							public void mouseClicked(MouseEvent e) {
								if (cashInserted>0) {
									TimerTask resetFrame = new TimerTask() {
										public void run() {
											reset();
										}
									};
									Timer timer = new Timer("Delay");
									timer.schedule(resetFrame, 5000L);
									label_17.setVisible(false);
									lbInserted_1.setVisible(false);
									btnCancel_1.setEnabled(false);
									btnCancel_1.setContentAreaFilled(false);
									btnNext.setContentAreaFilled(false);
									btnNext.setBorder(new LineBorder(Color.GRAY, 3, true));
									btnCancel_1.setBorder(new LineBorder(Color.GRAY, 3, true));
									btnNext.setEnabled(false);
								}
								else {
									label_17.setVisible(false);
									lbInserted_1.setVisible(false);
									reset();
									btnCancel_1.setForeground(Color.WHITE);
								}
							}
							@Override
							public void mouseEntered(MouseEvent e) {
								if (btnCancel_1.isEnabled()) {
									btnCancel_1.setBackground(Color.RED);
									btnCancel_1.setContentAreaFilled(true);
									btnCancel_1.setForeground(Color.WHITE);
								}
							}
							@Override
							public void mouseExited(MouseEvent e) {
								if (btnCancel_1.isEnabled()) {
									btnCancel_1.setContentAreaFilled(false);
									btnCancel_1.setForeground(Color.RED);
								}
							}
						});
						btnCancel_1.setFont(new Font("Arial Black", Font.PLAIN, 15));
						btnCancel_1.setBounds(14, 421, 152, 30);
						selectPanel.add(btnCancel_1);
		
		JPanel delivPanel = new JPanel();
		delivPanel.setBounds(-214, 652, 400, 100);
		delivPanel.setBackground(Color.DARK_GRAY);
		frame.getContentPane().add(delivPanel);
					
		URL arrImgURL = getClass().getResource("/image/orange_arrow.png");
		delivPanel.setLayout(null);
		
		panel_10 = new JPanel();
		panel_10.setBackground(Color.ORANGE);
		panel_10.setBorder(new LineBorder(new Color(255, 200, 0), 2, true));
		panel_10.setBounds(216, 28, 184, 46);
		delivPanel.add(panel_10);
		panel_10.setLayout(null);
		
		
		lbCollect = new JLabel("Collect Item Here");
		lbCollect.setBounds(10, 11, 164, 24);
		panel_10.add(lbCollect);
		lbCollect.setBackground(Color.DARK_GRAY);
		lbCollect.setOpaque(true);
		lbCollect.setForeground(Color.WHITE);
		lbCollect.setHorizontalAlignment(SwingConstants.CENTER);
		lbCollect.setFont(new Font("Arial Black", Font.BOLD | Font.ITALIC, 15));
		
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBackground(Color.WHITE);
		panel.setBounds(-2, 25, 191, 625);
		frame.getContentPane().add(panel);
		
		JPanel panelCoca = new JPanel();
		panelCoca.setLayout(null);
		panelCoca.setBackground(Color.WHITE);
		panelCoca.setBounds(41, 57, 110, 127);
		panel.add(panelCoca);
		
		JLabel lblCoca = new JLabel("coca");
		lblCoca.setIcon(new ImageIcon(VendingMachine.class.getResource("/image/coca-cola.jpg")));
		lblCoca.setVerticalAlignment(SwingConstants.BOTTOM);
		lblCoca.setHorizontalAlignment(SwingConstants.CENTER);
		lblCoca.setBounds(10, 0, 100, 95);
		panelCoca.add(lblCoca);
		
		JLabel lblCocaPrice = new JLabel("10.000 VND");
		lblCocaPrice.setHorizontalAlignment(SwingConstants.CENTER);
		lblCocaPrice.setBounds(0, 101, 110, 14);
		panelCoca.add(lblCocaPrice);
		
		JPanel panelPepsi = new JPanel();
		panelPepsi.setLayout(null);
		panelPepsi.setBackground(Color.WHITE);
		panelPepsi.setBounds(41, 280, 110, 127);
		panel.add(panelPepsi);
		
		JLabel lblPepsi = new JLabel("pepsi");
		lblPepsi.setIcon(new ImageIcon(VendingMachine.class.getResource("/image/pepsi.png")));
		lblPepsi.setVerticalAlignment(SwingConstants.BOTTOM);
		lblPepsi.setHorizontalAlignment(SwingConstants.CENTER);
		lblPepsi.setBounds(20, 0, 90, 91);
		panelPepsi.add(lblPepsi);
		
		JLabel lblPepsiPrice = new JLabel("10.000 VND");
		lblPepsiPrice.setHorizontalAlignment(SwingConstants.CENTER);
		lblPepsiPrice.setBounds(0, 102, 110, 14);
		panelPepsi.add(lblPepsiPrice);
		
		JPanel panelSoda = new JPanel();
		panelSoda.setLayout(null);
		panelSoda.setBackground(Color.WHITE);
		panelSoda.setBounds(41, 478, 110, 126);
		panel.add(panelSoda);
		
		JLabel lblSoda = new JLabel("soda");
		lblSoda.setIcon(new ImageIcon(VendingMachine.class.getResource("/image/soda.jpg")));
		lblSoda.setVerticalAlignment(SwingConstants.BOTTOM);
		lblSoda.setHorizontalAlignment(SwingConstants.CENTER);
		lblSoda.setBounds(20, 0, 90, 93);
		panelSoda.add(lblSoda);
		
		JLabel lblSodaPrice = new JLabel("20.000 VND");
		lblSodaPrice.setHorizontalAlignment(SwingConstants.CENTER);
		lblSodaPrice.setBounds(0, 101, 110, 14);
		panelSoda.add(lblSodaPrice);
						
						JPanel panel_7_1 = new JPanel();
						panel_7_1.setBorder(new LineBorder(new Color(0, 0, 0), 3, true));
						panel_7_1.setBackground(new Color(255, 153, 0));
						panel_7_1.setBounds(-2, 0, 370, 24);
						frame.getContentPane().add(panel_7_1);
						panel_7_1.setLayout(null);
						
						btnPromtionMess = new JButton("Promotion today!!!");
						btnPromtionMess.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent arg0) {
							}
						});
						btnPromtionMess.setBounds(0, 0, 370, 21);
						btnPromtionMess.setAlignmentY(Component.BOTTOM_ALIGNMENT);
						btnPromtionMess.setRequestFocusEnabled(false);
						btnPromtionMess.setOpaque(false);
						btnPromtionMess.setMargin(new Insets(2, 0, 2, 0));
						btnPromtionMess.setForeground(Color.RED);
						btnPromtionMess.setFont(new Font("Arial Black", Font.BOLD | Font.ITALIC, 14));
						btnPromtionMess.setFocusPainted(false);
						btnPromtionMess.setContentAreaFilled(false);
						btnPromtionMess.setBorderPainted(false);
						btnPromtionMess.setBorder(null);
						panel_7_1.add(btnPromtionMess);
						
						
						payPanel = new JPanel();
						payPanel.setBounds(378, 25, 180, 727);
						frame.getContentPane().add(payPanel);
						payPanel.setBackground(Color.DARK_GRAY);
						payPanel.setLayout(null);
						payPanel.setVisible(false);
						
						JLabel lblNewLabel = new JLabel("Please Pay:");
						lblNewLabel.setForeground(Color.ORANGE);
						lblNewLabel.setBounds(10, 10, 160, 29);
						lblNewLabel.setFont(new Font("Arial Black", Font.PLAIN, 20));
						lblNewLabel.setAutoscrolls(true);
						payPanel.add(lblNewLabel);
						
						JPanel panel_8 = new JPanel();
						panel_8.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
						panel_8.setBounds(20, 52, 130, 40);
						payPanel.add(panel_8);
						panel_8.setLayout(null);
						
						lbPrice = new JLabel("0 VND");
						lbPrice.setBackground(Color.ORANGE);
						lbPrice.setHorizontalAlignment(SwingConstants.CENTER);
						lbPrice.setBounds(0, 3, 130, 35);
						panel_8.add(lbPrice);
						lbPrice.setFont(new Font("Arial Black", Font.BOLD, 30));
						
						JPanel panel_7 = new JPanel();
						panel_7.setBackground(new Color(255, 153, 0));
						panel_7.setBounds(0, 104, 180, 58);
						panel_7.setBorder(new LineBorder(new Color(0, 0, 0), 3, true));
						payPanel.add(panel_7);
						
						btnItem = new JButton("Item");
						btnItem.setBorder(null);
						btnItem.setForeground(Color.WHITE);
						btnItem.setRequestFocusEnabled(false);
						btnItem.setOpaque(false);
						btnItem.setMargin(new Insets(2, 0, 2, 0));
						btnItem.setFont(new Font("Arial Black", Font.BOLD | Font.ITALIC, 25));
						btnItem.setFocusPainted(false);
						btnItem.setContentAreaFilled(false);
						btnItem.setBorderPainted(false);
						panel_7.add(btnItem);
						
						lblCashInserted = new JLabel("Cash Inserted:");
						lblCashInserted.setForeground(Color.ORANGE);
						lblCashInserted.setHorizontalAlignment(SwingConstants.CENTER);
						lblCashInserted.setFont(new Font("Arial Black", Font.PLAIN, 20));
						lblCashInserted.setBounds(0, 173, 180, 36);
						payPanel.add(lblCashInserted);
						
						lbInserted_2 = new JLabel("0 VND");
						lbInserted_2.setForeground(Color.WHITE);
						lbInserted_2.setHorizontalAlignment(SwingConstants.CENTER);
						lbInserted_2.setFont(new Font("Arial Black", Font.PLAIN, 20));
						lbInserted_2.setBounds(10, 220, 160, 29);
						payPanel.add(lbInserted_2);
						
						btnCancelPurchase = new JButton("Cancel Purchase");
						btnCancelPurchase.setForeground(Color.RED);
						btnCancelPurchase.setContentAreaFilled(false);
						btnCancelPurchase.setBorder(new LineBorder(Color.RED, 3, true));
						btnCancelPurchase.setFont(new Font("Arial Black", Font.PLAIN, 14));
						btnCancelPurchase.addMouseListener(new MouseAdapter() {
							@Override
							public void mouseClicked(MouseEvent e) {
								if (!btnCancelPurchase.isEnabled()) {return;}
								btnBuy.setEnabled(false);
								btnCancelPurchase.setEnabled(false);
								btnBack.setEnabled(false);
								btnGetChange.setEnabled(false);
												
								TimerTask resetFrame = new TimerTask() {
									public void run() {
										reset();
									}
								};
								Timer timer = new Timer("Delay");
								timer.schedule(resetFrame, 5000L);
								btnBuy.setEnabled(false);
								btnBuy.setContentAreaFilled(false);
								btnCancelPurchase.setContentAreaFilled(false);
								btnBuy.setBorder(new LineBorder(Color.GRAY, 3, true));
								btnCancelPurchase.setBorder(new LineBorder(Color.GRAY, 3, true));
								btnBack.setBorder(new LineBorder(Color.GRAY, 3, true));
								btnBack.setEnabled(false);
								btnCancelPurchase.setEnabled(false);
							}
							@Override
							public void mouseEntered(MouseEvent e) {
								if (btnCancelPurchase.isEnabled()) {
									btnCancelPurchase.setBackground(Color.RED);
									btnCancelPurchase.setContentAreaFilled(true);
									btnCancelPurchase.setForeground(Color.WHITE);
								}
							}
							@Override
							public void mouseExited(MouseEvent e) {
								if (btnCancelPurchase.isEnabled()) {
									btnCancelPurchase.setContentAreaFilled(false);
									btnCancelPurchase.setForeground(Color.RED);
								}
							}
						});
						btnCancelPurchase.setBounds(10, 357, 160, 30);
						payPanel.add(btnCancelPurchase);
						
						btnBuy = new JButton("BUY");
						btnBuy.setForeground(Color.CYAN);
						btnBuy.setBorder(new LineBorder(Color.GRAY, 3, true));
						btnBuy.setContentAreaFilled(false);
						btnBuy.addMouseListener(new MouseAdapter() {
							@Override
							public void mouseClicked(MouseEvent e) {
								if (!btnBuy.isEnabled()) {return;}
								else if (cashInserted>=chosenItem.getPrice()) {
									// sufficient cash
									lbCollect.setText(chosenItem.getName().toUpperCase() + " Delievered");
									lbCollect.setForeground(Color.RED);
									panel_10.setBackground(Color.WHITE);
									panel_10.setBorder(new LineBorder(Color.WHITE, 3, true));
									btnBack.setEnabled(false);
									btnCancelPurchase.setEnabled(false);
									enableCashPanel(false);
									
									consecutiveItems.add(chosenItem);
									if(consecutiveItems.size() == 3 && currentLimitedBudget < 50000) {
										int divItem = 0;
										for(Item item : consecutiveItems) {
											switch (item.getName()) {
												case "coca-cola":
													divItem++;
													break;
												case "pepsi":
													divItem++;
													break;
												case "soda":
													divItem++;
													break;
											}
										}
										System.out.println(divItem);
										if(divItem % 3 == 0) {
											  int res = JOptionPane.showOptionDialog(new JFrame(), "You have chance to get free product\n"
													  + "Do you want to try?",
													  "Congratulation",
											         JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
											         new Object[] { "Yes", "No" }, JOptionPane.YES_OPTION);
										      if (res == JOptionPane.YES_OPTION) {
										         if(statusPromotion == 1) {
										        	 if(Utils.getPromotion(0.1)) {
										        		 JOptionPane.showMessageDialog(new JFrame(), "Congratulation!!!");
										        		 currentLimitedBudget += chosenItem.getPrice();
										        		 consecutiveItems.clear();
										        	 }
										        	 else {
										        		 JOptionPane.showMessageDialog(new JFrame(), "Thank you for your purchase !!!");
										        		 currentLimitedBudget += chosenItem.getPrice();
										        		 consecutiveItems.clear();
										        	 }
										         }
										         
										         if(statusPromotion == 2) {
										        	 if(Utils.getPromotion(0.5)) {
										        		 JOptionPane.showMessageDialog(new JFrame(), "Congratulation!!!");
										        		 consecutiveItems.clear();
										        	 }
										        	 else {
										        		 JOptionPane.showMessageDialog(new JFrame(), "Thank you for your purchase !!!");
										        		 consecutiveItems.clear();
										        	 }
										         }
										      } else if (res == JOptionPane.NO_OPTION) {
										    	  JOptionPane.showMessageDialog(new JFrame(), "Thank you for your purchase !!!");
										    	  consecutiveItems.clear();
										      } else if (res == JOptionPane.CLOSED_OPTION) {
										    	  JOptionPane.showMessageDialog(new JFrame(), "Thank you for your purchase !!!");
									        	  consecutiveItems.clear();
										      }
										}
									}
									TimerTask resetFrame = new TimerTask() {
										public void run() {
											reset();
										}
									};
									Timer timer = new Timer("Delay");
									timer.schedule(resetFrame, 5000L);
									btnBuy.setEnabled(false);
									btnBuy.setContentAreaFilled(false);
									btnBuy.setBorder(new LineBorder(Color.GRAY, 3, true));
									btnCancelPurchase.setBorder(new LineBorder(Color.GRAY, 3, true));
									btnBack.setBorder(new LineBorder(Color.GRAY, 3, true));
									
									change = cashInserted-chosenItem.getPrice();				
									if(change > 0) {
										btnGetChange.setVisible(true);
										btnGetChange.setEnabled(true);
									}
									
									
								}
								else {
									lbInserted_2.setText("INSUFFICIENT");
									lbInserted_2.setForeground(Color.RED);
									lbInserted_2.setFont(new Font("Arial Black", Font.PLAIN, 20));
									TimerTask insufficient = new TimerTask() {
										public void run() {
											lbInserted_2.setText(String.valueOf(cashInserted));
											lbInserted_2.setForeground(Color.WHITE);
											lbInserted_2.setFont(new Font("Arial Black", Font.PLAIN, 25));
										}
									};
									Timer timer = new Timer("Delay");
									timer.schedule(insufficient, 3000L);
								}
							}
							@Override
							public void mouseEntered(MouseEvent e) {
								if(cashInserted>=chosenItem.getPrice() && btnBuy.isEnabled()) {
									btnBuy.setBackground(Color.CYAN);
									btnBuy.setContentAreaFilled(true);
									btnBuy.setForeground(Color.WHITE);
								}
							}
							@Override
							public void mouseExited(MouseEvent e) {
								if (cashInserted>=chosenItem.getPrice() && btnBuy.isEnabled()) {
									btnBuy.setContentAreaFilled(false);
									btnBuy.setForeground(Color.CYAN);
								}
							}
						});
						btnBuy.setEnabled(false);
						btnBuy.setFont(new Font("Arial Black", Font.PLAIN, 15));
						btnBuy.setBounds(10, 269, 160, 30);
						payPanel.add(btnBuy);
						
						btnBack = new JButton("BACK");
						btnBack.setForeground(Color.ORANGE);
						btnBack.setBorder(new LineBorder(Color.ORANGE, 3, true));
						btnBack.setContentAreaFilled(false);
						btnBack.addMouseListener(new MouseAdapter() {
							@Override
							public void mouseClicked(MouseEvent e) {
								if (!btnBack.isEnabled()) {return;}
								selectPanel.setVisible(true);
								payPanel.setVisible(false);
								label_17.setVisible(true);
								lbInserted_1.setText(String.valueOf(cashInserted));
								lbInserted_1.setVisible(true);
								codeCnt = 0;
								code = new ArrayList<>(Arrays.asList(-1,-1,-1));	
								updateCode();
								panel_9.setVisible(false);
							}
							@Override
							public void mouseEntered(MouseEvent e) {
								if (btnBack.isEnabled()) {
									btnBack.setBackground(Color.ORANGE);
									btnBack.setContentAreaFilled(true);
									btnBack.setForeground(Color.WHITE);
								}
							}
							@Override
							public void mouseExited(MouseEvent e) {
								if (btnBack.isEnabled()) {
									btnBack.setContentAreaFilled(false);
									btnBack.setForeground(Color.ORANGE);
								}
							}
						});
						btnBack.setFont(new Font("Arial Black", Font.PLAIN, 15));
						btnBack.setBounds(10, 314, 160, 30);
						payPanel.add(btnBack);
						
						btnGetChange = new JButton("");
						btnGetChange.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								JOptionPane.showMessageDialog(new JFrame(), "Your change: " + change);
							}
						});
						btnGetChange.setBounds(27, 446, 123, 54);
						payPanel.add(btnGetChange);
						btnGetChange.setIcon(new ImageIcon(VendingMachine.class.getResource("/image/receive-dollar.png")));
						btnGetChange.setBackground(Color.LIGHT_GRAY);
						btnGetChange.setVisible(false);
	}
	// declare component
	private JFrame frame;
	private JPanel selectPanel;
	private JPanel payPanel;
	private JLabel lbCode;
	private JButton btnNext;
	private JButton btnCancel_1;
	private JLabel lbCollect;
	
	private JLabel lbPrice;
	private JLabel label_17;
	private JLabel lbInserted_1;
	private JLabel lbInserted_2;
	private JButton btnItem;
	private JLabel lblCashInserted;
	private JButton btnBuy;
	private JPanel panel_9;
	private JPanel panel_10;
	
	private JButton btnBack;
	private JButton btnCancelPurchase;
	
	private JButton btnTen;
	private JButton btnTwenty;
	private JButton btnFifty;
	private JButton btnOneHunred;
	private JButton btnTwoHundred;
	private JButton btnPromtionMess;
	private JButton btnGetChange;
}
