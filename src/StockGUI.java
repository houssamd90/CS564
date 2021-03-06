import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JList;
import javax.swing.DropMode;
import javax.swing.AbstractListModel;
import javax.swing.ListSelectionModel;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.BorderLayout;

import java.awt.Font;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.text.DateFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.Vector;

import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.markers.SeriesMarkers;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;

import java.text.ParseException;

import javax.swing.JCheckBox;

public class StockGUI {

	private JFrame frmStockMartket;
	private JTextField txtTicker;
	private JTextField txtQuantity;
	private JTextField txtDate;
	private JTextField txtUserID;
	private JTextField txtTicker2;
	private JTextField txtQuantity2;
	private JTextField txtValue;
	private JTextField txtUser3;
	private JTextField txtDate3;
	private JTextField txtStockValue;
	private JTextField txtCashOnHand;

	//Postgres variables
	private PreparedStatement st = null;
	private JTextField txtUserIDnew;
	private JTextField txtName;
	private JTextField txtDOB;
	private JTextField txtCashOnHandNew;
	Connection con;
	private JTextField txtUSERID;
	
	//last date with stock data from database
	private final java.sql.Date FINAL_DATE = getSQLdate("2016-11-04");
	private JTextField txtUserIDplot;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					StockGUI window = new StockGUI();
					window.frmStockMartket.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public StockGUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		//Postgres stuff
		try{
			Class.forName("org.postgresql.Driver");
			con=DriverManager.getConnection("jdbc:postgresql://localhost:5432/classdb","auri","testdb");
			if(con!=null){
				System.out.println("Connected");
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		//End postgress stuff
		
		frmStockMartket = new JFrame();
		frmStockMartket.setTitle("Stock Market");
		frmStockMartket.setBounds(100, 100, 1291, 360);
		frmStockMartket.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmStockMartket.getContentPane().setLayout(null);
		
		txtTicker = new JTextField();
		txtTicker.setBounds(104, 112, 86, 20);
		frmStockMartket.getContentPane().add(txtTicker);
		txtTicker.setColumns(10);
		
		txtQuantity = new JTextField(); 
		txtQuantity.setBounds(104, 143, 86, 20);
		frmStockMartket.getContentPane().add(txtQuantity);
		txtQuantity.setColumns(10);
		
		txtDate = new JTextField();
		txtDate.setBounds(104, 174, 86, 20);
		frmStockMartket.getContentPane().add(txtDate);
		txtDate.setColumns(10);
		
		JCheckBox chckbxEndowed = new JCheckBox("Endowed");
		chckbxEndowed.setBounds(10, 52, 141, 23);
		frmStockMartket.getContentPane().add(chckbxEndowed);
		
		JComboBox cmbBuySell = new JComboBox();
		cmbBuySell.setModel(new DefaultComboBoxModel(new String[] {"Buy", "Sell"}));
		cmbBuySell.setBounds(104, 205, 86, 20);
		frmStockMartket.getContentPane().add(cmbBuySell);
		
		JLabel lblNewLabel = new JLabel("Ticker");
		lblNewLabel.setBounds(10, 115, 84, 14);
		frmStockMartket.getContentPane().add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Quantity");
		lblNewLabel_1.setBounds(11, 146, 83, 14);
		frmStockMartket.getContentPane().add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("Date");
		lblNewLabel_2.setBounds(10, 178, 84, 14);
		frmStockMartket.getContentPane().add(lblNewLabel_2);
		
		JLabel lblNewLabel_3 = new JLabel("Action");
		lblNewLabel_3.setBounds(10, 208, 84, 14);
		frmStockMartket.getContentPane().add(lblNewLabel_3);
		
		//buy/sell logic. 
		JButton btnBuySell = new JButton("Submit");
		btnBuySell.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				int userID = Integer.parseInt(txtUSERID.getText());
				java.sql.Date date = getSQLdate(txtDate.getText());
				String ticker = txtTicker.getText();
				int quantity = Integer.parseInt(txtQuantity.getText());
				
				//If trying to perform a transaction past the last date of available 
				//stock data, throw an error
				if(date.compareTo(FINAL_DATE)>0){
					JOptionPane.showMessageDialog(null,"No stock data available past 2016-11-04, please use an earlier date");
					return;
				}
				
				//If trying to perform transactions out of order, throw an error
				if(hasFutureTransactions(userID, date)){
					JOptionPane.showMessageDialog(null,"A future transaction exists. Cannot perform transactions out of order!");
					return;
				}
				
				//If a transaction for that user/day/ticker already exists in the
				//database, delete it so we can add the current transaction
				if (transactionExists(userID, date, ticker)){
					deleteTransaction();
				}
				
				if(cmbBuySell.getSelectedIndex() == 0){ //Buy

					Boolean endowed = chckbxEndowed.isSelected();
					
					if(!validDateForTransaction(date, ticker)){
						JOptionPane.showMessageDialog(null,"Company didn't exist back then, please change date !");
						return;
					}
					
					if(!endowed){
						if(!enoughCashForTransaction(userID, date, ticker, (double) quantity)){
							JOptionPane.showMessageDialog(null,"Not enough cash on hand to perform transaction");
							return;
						}
					} 
					
					//Looks like we have enough cash on hand (or its endowed) for 
					//the transaction so let's go ahead with it
					if(endowed){
						addFreeTransaction(userID, date, ticker, BigDecimal.valueOf(quantity));
					} else{
						addCurTransaction(userID, date, ticker, BigDecimal.valueOf(quantity));
					}
					
				} else{ //Sell
					if(!enoughStocksForTransaction(userID, date, ticker, quantity)){
						JOptionPane.showMessageDialog(null,"Not enough stocks to perform transaction");
						return;
					}
					
					//Looks like we have enough stocks for the transaction so
					//let's go ahead with it
					addCurTransaction(userID, date, ticker, BigDecimal.valueOf(quantity*(-1)));
				}
			}
		});
		btnBuySell.setBounds(29, 246, 122, 39);
		frmStockMartket.getContentPane().add(btnBuySell);
		
		JSeparator separator = new JSeparator();
		separator.setOrientation(SwingConstants.VERTICAL);
		separator.setBounds(207, 11, 2, 280);
		frmStockMartket.getContentPane().add(separator);
		
		txtUserID = new JTextField();
		txtUserID.setBounds(361, 79, 86, 20);
		frmStockMartket.getContentPane().add(txtUserID);
		txtUserID.setColumns(10);
		
		txtTicker2 = new JTextField();
		txtTicker2.setBounds(361, 120, 86, 20);
		frmStockMartket.getContentPane().add(txtTicker2);
		txtTicker2.setColumns(10);
		
		txtQuantity2 = new JTextField();
		txtQuantity2.setEditable(false);
		txtQuantity2.setEnabled(false);
		txtQuantity2.setBounds(361, 161, 86, 20);
		frmStockMartket.getContentPane().add(txtQuantity2);
		txtQuantity2.setColumns(10);
		
		txtValue = new JTextField();
		txtValue.setEnabled(false);
		txtValue.setEditable(false);
		txtValue.setBounds(361, 205, 86, 20);
		frmStockMartket.getContentPane().add(txtValue);
		txtValue.setColumns(10);
		
		JLabel lblNewLabel_4 = new JLabel("User ID");
		lblNewLabel_4.setBounds(238, 82, 89, 14);
		frmStockMartket.getContentPane().add(lblNewLabel_4);
		
		JLabel lblNewLabel_5 = new JLabel("Ticker");
		lblNewLabel_5.setBounds(238, 123, 89, 14);
		frmStockMartket.getContentPane().add(lblNewLabel_5);
		
		JLabel lblNewLabel_6 = new JLabel("Quantity");
		lblNewLabel_6.setBounds(238, 164, 89, 14);
		frmStockMartket.getContentPane().add(lblNewLabel_6);
		
		JLabel lblNewLabel_7 = new JLabel("Value ($)");
		lblNewLabel_7.setBounds(238, 208, 89, 14);
		frmStockMartket.getContentPane().add(lblNewLabel_7);
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setOrientation(SwingConstants.VERTICAL);
		separator_1.setBounds(492, 11, 2, 280);
		frmStockMartket.getContentPane().add(separator_1);
		
		txtUser3 = new JTextField();
		txtUser3.setBounds(655, 79, 86, 20);
		frmStockMartket.getContentPane().add(txtUser3);
		txtUser3.setColumns(10);
		
		txtDate3 = new JTextField();
		txtDate3.setBounds(655, 120, 86, 20);
		frmStockMartket.getContentPane().add(txtDate3);
		txtDate3.setColumns(10);
		
		txtStockValue = new JTextField();
		txtStockValue.setEnabled(false);
		txtStockValue.setEditable(false);
		txtStockValue.setBounds(655, 161, 86, 20);
		frmStockMartket.getContentPane().add(txtStockValue);
		txtStockValue.setColumns(10);
		
		txtCashOnHand = new JTextField();
		txtCashOnHand.setEnabled(false);
		txtCashOnHand.setEditable(false);
		txtCashOnHand.setBounds(655, 205, 86, 20);
		frmStockMartket.getContentPane().add(txtCashOnHand);
		txtCashOnHand.setColumns(10);
		
		JLabel lblNewLabel_8 = new JLabel("User ID");
		lblNewLabel_8.setBounds(504, 82, 127, 14);
		frmStockMartket.getContentPane().add(lblNewLabel_8);
		
		JLabel lblNewLabel_9 = new JLabel("Date");
		lblNewLabel_9.setBounds(504, 123, 141, 14);
		frmStockMartket.getContentPane().add(lblNewLabel_9);
		
		JLabel lblNewLabel_10 = new JLabel("Stock Value ($)");
		lblNewLabel_10.setBounds(504, 164, 152, 14);
		frmStockMartket.getContentPane().add(lblNewLabel_10);
		
		JLabel lblNewLabel_11 = new JLabel("Cash on Hand ($)");
		lblNewLabel_11.setBounds(504, 208, 152, 14);
		frmStockMartket.getContentPane().add(lblNewLabel_11);
		
		JLabel lblBuysell = new JLabel("Buy/Sell");
		lblBuysell.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblBuysell.setBounds(48, 26, 127, 20);
		frmStockMartket.getContentPane().add(lblBuysell);
		
		JLabel lblTickerStatus = new JLabel("Ticker Status");
		lblTickerStatus.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblTickerStatus.setBounds(278, 26, 167, 20);
		frmStockMartket.getContentPane().add(lblTickerStatus);
		
		JLabel lblPortofollio = new JLabel("Portofolio");
		lblPortofollio.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblPortofollio.setBounds(578, 26, 141, 20);
		frmStockMartket.getContentPane().add(lblPortofollio);
		
		//gets the quantity and stock value for a current ticker/user combo as of today
		//txtUserID, txtTicker2, txtQuantity2, txtValue
		JButton btnUpdate2 = new JButton("Update");
		btnUpdate2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				@SuppressWarnings("deprecation")
				
				int userID = Integer.parseInt(txtUserID.getText());
				String ticker = txtTicker2.getText();
				//get current date
				java.util.Calendar cal = java.util.Calendar.getInstance();
				java.util.Date utilDate = cal.getTime();
				java.sql.Date curDate = new Date(utilDate.getTime());
				
				String sql="select current_holdings(?,?,?)"; //userID, date, ticker
				try {
					//Stock Value SQL call
					st = con.prepareStatement(sql);
					
					st.setInt(1, userID );
					st.setDate(2, curDate);
					st.setString(3, ticker);
					
					//if SQL call successful, grab the returned result and set it
					//to the quantity txtbox and the calculate the Value textbox
					if(st.execute()){
						ResultSet result = st.getResultSet();
						result.next();
						//quantity
						Double quantity = result.getBigDecimal(1).doubleValue();
						txtQuantity2.setText(String.valueOf(quantity.intValue()));
						//value
						double value = quantity * tickerValue(ticker, curDate);
						txtValue.setText(String.valueOf(value));
					}
					
				} catch (SQLException er) {
					er.printStackTrace();
					return;
				} finally{
					if (st != null) { try {
						st.close();
					} catch (SQLException e1) {
						e1.printStackTrace();
					} }
				}
				System.out.println("SQL Command sent successfully");
			}
		});
		btnUpdate2.setBounds(232, 246, 119, 39);
		frmStockMartket.getContentPane().add(btnUpdate2);
		
		//Updates portofolio fields (Stock value and Cash on hand for a current date)
		// txtStockValue for stock value txtCashOnHand for cash on hand
		JButton btnUpdate3 = new JButton("Update");
		btnUpdate3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				@SuppressWarnings("deprecation")
				
				int userID = Integer.parseInt(txtUser3.getText());
				java.sql.Date curDate = getSQLdate(txtDate3.getText());
				
				String stockValueSql="select stock_worth(?,?)"; //userID, curDate
				String cashOnHandSql="select current_cash(?,?)"; //userID, curDate
				try {
					//Stock Value SQL call
					st = con.prepareStatement(stockValueSql);
					
					st.setInt(1, userID );
					st.setDate(2, curDate);
					
					//if SQL call successful, grab the returned result and set it
					//to the stockValue txtbox
					if(st.execute()){
						ResultSet stockResult = st.getResultSet();
						stockResult.next();
						BigDecimal stockValue = stockResult.getBigDecimal(1);
						txtStockValue.setText(stockValue.toString());
					}
					
					//Cash on hand SQL call
					st = con.prepareStatement(cashOnHandSql);
					
					st.setInt(1, userID );
					st.setDate(2, curDate);
					
					//if SQL call successful, grab the returned result and set it
					//to the cashOnHand txtbox
					if(st.execute()){
						ResultSet cashResult = st.getResultSet();
						cashResult.next();
						BigDecimal cashOnHand = cashResult.getBigDecimal(1);
						txtCashOnHand.setText(cashOnHand.toString());
					}
					
				} catch (SQLException er) {
					er.printStackTrace();
					return;
				} finally{
					if (st != null) { try {
						st.close();
					} catch (SQLException e1) {
						e1.printStackTrace();
					} }
				}
				
				System.out.println("SQL Command sent successfully");
			}
		});
		btnUpdate3.setBounds(577, 246, 119, 39);
		frmStockMartket.getContentPane().add(btnUpdate3);
		
		JButton btnGraph = new JButton("Trending");
		
		//Put graphing stuff here. Can get user id from txtUserID and ticker from txtTicker2
		btnGraph.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				@SuppressWarnings("deprecation")
				final String sql="select * from net_worth_trending(?)";
				try {
					st = con.prepareStatement(sql);
					int userID = Integer.parseInt(txtUserID.getText());
					st.setInt(1, userID);
					
					if (st.execute()) {
						// The plotting stuff expects a list of java.util.Dates, I believe. Since java.sql.Date is a subclass, I think this should work, but I don't know
						// enough about Java's type system to know for sure. Worst case scenario, we convert the sql.Dates to strings and then parse them, something like
						// 
						//       xData.add(sdf.parse(rs.getDate("curDate").toString()))
						//       
						// using the "sdf" date parser defined in the testing block.
						ResultSet rs = st.getResultSet();
						List<java.util.Date> xData = new ArrayList<java.util.Date>();
						List<Double> yData = new ArrayList<Double>();
						while (rs.next()) {
							xData.add(rs.getDate("curDate"));
							yData.add(rs.getBigDecimal("curNetWorth").doubleValue());
						}
					
						/*
						// uncomment this block to test, comment out the above if database isn't available
						List<java.util.Date> xData = new ArrayList<java.util.Date>();
						List<Double> yData = new ArrayList<Double>();
						DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
						xData.add(sdf.parse("2016-01-01"));
						xData.add(sdf.parse("2015-02-05"));
						xData.add(sdf.parse("2014-04-15"));
						yData.add(1000.0);
						yData.add(900.0);
						yData.add(1312.52);
						*/
					
						XYChart chart = new XYChartBuilder().width(800).height(600).title("Net worth trend").build();
						chart.getStyler().setLegendVisible(false);
						XYSeries series = chart.addSeries("blah", xData, yData);
						series.setMarker(SeriesMarkers.NONE);
					
						new SwingWrapper(chart).displayChart();
					}
				} catch (Exception er) {
					er.printStackTrace();
					return;
				} finally {
					if (st != null) {
						try {
							st.close();
						} catch (SQLException er) {
							er.printStackTrace();
							return;
						}
					}
				}
			}
		});
		btnGraph.setBounds(354, 246, 119, 39);
		frmStockMartket.getContentPane().add(btnGraph);
		
		JSeparator separator_2 = new JSeparator();
		separator_2.setOrientation(SwingConstants.VERTICAL);
		separator_2.setBounds(764, 11, 2, 280);
		frmStockMartket.getContentPane().add(separator_2);
		
		JLabel label = new JLabel("User ID");
		label.setBounds(776, 82, 141, 14);
		frmStockMartket.getContentPane().add(label);
		
		JLabel lblName = new JLabel("Name");
		lblName.setBounds(776, 123, 141, 14);
		frmStockMartket.getContentPane().add(lblName);
		
		JLabel lblDateOfBirth = new JLabel("Date of Birth");
		lblDateOfBirth.setBounds(776, 164, 141, 14);
		frmStockMartket.getContentPane().add(lblDateOfBirth);
		
		txtUserIDnew = new JTextField();
		txtUserIDnew.setColumns(10);
		txtUserIDnew.setBounds(920, 79, 86, 20);
		frmStockMartket.getContentPane().add(txtUserIDnew);
		
		txtName = new JTextField();
		txtName.setColumns(10);
		txtName.setBounds(920, 120, 86, 20);
		frmStockMartket.getContentPane().add(txtName);
		
		txtDOB = new JTextField();
		txtDOB.setColumns(10);
		txtDOB.setBounds(920, 161, 86, 20);
		frmStockMartket.getContentPane().add(txtDOB);
		
		JLabel lblNewUser = new JLabel("New User");
		lblNewUser.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblNewUser.setBounds(855, 26, 152, 20);
		frmStockMartket.getContentPane().add(lblNewUser);
		
		//Create new user
		JButton btnCreate = new JButton("Create");
		btnCreate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				int userID = Integer.parseInt(txtUserIDnew.getText());
				String name = txtName.getText();
				java.sql.Date dob = getSQLdate(txtDOB.getText());
				BigDecimal cash = BigDecimal.valueOf(Double.parseDouble(txtCashOnHandNew.getText()));
				
				@SuppressWarnings("deprecation")
				String sql="select add_new_user(?,?,?,?)"; //userID, name, dob, cash
			
				try {
					st = con.prepareStatement(sql);
					
					st.setInt(1, userID );
					st.setString(2, name);
					st.setDate(3, dob);
					st.setBigDecimal(4, cash);
					
					st.execute();
				} catch (SQLException er) {
					er.printStackTrace();
					return;
				} finally{
					if (st != null) { try {
						st.close();
					} catch (SQLException e1) {
						e1.printStackTrace();
					} }
				}
			}
		});
		
		btnCreate.setBounds(855, 249, 119, 33);
		frmStockMartket.getContentPane().add(btnCreate);
		
		JLabel label_1 = new JLabel("Cash on Hand ($)");
		label_1.setBounds(776, 208, 147, 14);
		frmStockMartket.getContentPane().add(label_1);
		
		txtCashOnHandNew = new JTextField();
		txtCashOnHandNew.setColumns(10);
		txtCashOnHandNew.setBounds(920, 205, 86, 20);
		frmStockMartket.getContentPane().add(txtCashOnHandNew);
		
		JLabel lblTxtuserid = new JLabel("User ID");
		lblTxtuserid.setBounds(10, 82, 86, 14);
		frmStockMartket.getContentPane().add(lblTxtuserid);
		
		txtUSERID = new JTextField();
		txtUSERID.setColumns(10);
		txtUSERID.setBounds(104, 82, 86, 20);
		frmStockMartket.getContentPane().add(txtUSERID);
		
		JSeparator separator_3 = new JSeparator();
		separator_3.setOrientation(SwingConstants.VERTICAL);
		separator_3.setBounds(1028, 11, 2, 280);
		frmStockMartket.getContentPane().add(separator_3);
		
		JLabel lblTransactions = new JLabel("Transactions");
		lblTransactions.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblTransactions.setBounds(1089, 26, 152, 20);
		frmStockMartket.getContentPane().add(lblTransactions);
		
		JLabel label_2 = new JLabel("User ID");
		label_2.setBounds(1040, 123, 141, 14);
		frmStockMartket.getContentPane().add(label_2);
		
		txtUserIDplot = new JTextField();
		txtUserIDplot.setColumns(10);
		txtUserIDplot.setBounds(1163, 120, 86, 20);
		frmStockMartket.getContentPane().add(txtUserIDplot);
		
		//Display a table of the user's transactions. txtUserIDplot stores the userID input
		JButton btnDisplay = new JButton("Display");
		btnDisplay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// TABLE
				final String sql="select * from all_transactions(?)";
				try {
					st = con.prepareStatement(sql);
					int userID = Integer.parseInt(txtUserIDplot.getText());
					st.setInt(1, userID);
					
					if (st.execute()) {
						ResultSet rs = st.getResultSet();

						Vector<String> columnNames = new Vector<String>();
						columnNames.add("Date");
						columnNames.add("Ticker");
						columnNames.add("Number of shares");
						
						Vector<Vector<String>> rowData = new Vector<Vector<String>>();
						while (rs.next()) {
							Vector<String> row = new Vector<String>();
							row.add(rs.getDate("xactDate").toString());
							row.add(rs.getString("xactTicker"));
							row.add(rs.getBigDecimal("xactCount").toString());
							rowData.add(row);
						}
						JTable table = new JTable(rowData, columnNames);
						JScrollPane scrollPane = new JScrollPane(table);
						JFrame frame = new JFrame();
					    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
						frame.add(scrollPane, BorderLayout.CENTER);
					    frame.setSize(450, 200);
					    frame.setTitle("Transactions for user " + txtUserIDplot.getText());
					    frame.setVisible(true);
					}
				}
				catch (Exception er) {
                    er.printStackTrace();
                    return;
				}
				finally {
					if (st != null) {
						try {
							st.close();
						}
						catch (SQLException er) {
							er.printStackTrace();
							return;
						}
					}
				}
			}
		});
		btnDisplay.setBounds(1089, 185, 119, 61);
		frmStockMartket.getContentPane().add(btnDisplay);
		
	}
	
	//we will call in to the stored procedure to check if a transaction exists for the given date/ticker
	//inputed by user. If it does, we will then execute a remove_transaction followed by an add_transaction
	//command to replace the old transaction with the new
	private boolean transactionExists(int userID, java.sql.Date date, String ticker){
	
		Boolean transactionExists = false;
		
		@SuppressWarnings("deprecation")
		String sql="select is_transaction(?,?,?)"; //userID, date, curTicker
		
		try {
			st = con.prepareStatement(sql);
			
			st.setInt(1, userID );
			st.setDate(2, date);
			st.setString(3, ticker);
			
			//if SQL call successful, grab the returned result and set it
			//to the return value
			if(st.execute()){
				ResultSet result = st.getResultSet();
				result.next();
				transactionExists = result.getBoolean(1);
			}
		} catch (SQLException er) {
			er.printStackTrace();
		} finally{
			if (st != null) { try {
				st.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			} }
		}
		
		return transactionExists;
	}
	
	//delete transaction for current date/ticker/user from database
	private void deleteTransaction(){
		int userID = Integer.parseInt(txtUSERID.getText());
		java.sql.Date date = getSQLdate(txtDate.getText());
		String ticker = txtTicker.getText();
		
		@SuppressWarnings("deprecation")
		String sql="select remove_transaction(?,?,?)"; //userID, date, curTicker
		
		try {
			st = con.prepareStatement(sql);
			
			st.setInt(1, userID );
			st.setDate(2, date);
			st.setString(3, ticker);
			
			st.execute();
		} catch (SQLException er) {
			er.printStackTrace();
		} finally{
			if (st != null) { try {
				st.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			} }
		}
	}
	
	//add transaction for current date/ticker/user to database
	private void addCurTransaction(int userID, java.sql.Date date, String ticker, BigDecimal quantity){
		
		@SuppressWarnings("deprecation")
		String sql="select add_transaction(?,?,?,?)"; //userID, date, curTicker, quantity
		
		try {
			st = con.prepareStatement(sql);
			
			st.setInt(1, userID );
			st.setDate(2, date);
			st.setString(3, ticker);
			st.setBigDecimal(4, quantity);
			
			st.execute();
		} catch (SQLException er) {
			er.printStackTrace();
		} finally{
			if (st != null) { try {
				st.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			} }
		}
	}
	
	//add free transaction for current date/ticker/user to database (for endowed case)
		private void addFreeTransaction(int userID, java.sql.Date date, String ticker, BigDecimal quantity){
			
			@SuppressWarnings("deprecation")
			String sql="select free_stock(?,?,?,?)"; //userID, date, curTicker, quantity
			
			try {
				st = con.prepareStatement(sql);
				
				st.setInt(1, userID );
				st.setDate(2, date);
				st.setString(3, ticker);
				st.setBigDecimal(4, quantity);
				
				st.execute();
			} catch (SQLException er) {
				er.printStackTrace();
			} finally{
				if (st != null) { try {
					st.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				} }
			}
		}
	
	private Boolean validDateForTransaction(java.sql.Date date, String ticker){
		
		@SuppressWarnings("deprecation")
		String sql="select oldest_observation(?)"; //ticker
		java.sql.Date oldestDate = null;
		
		try {
			st = con.prepareStatement(sql);
			
			st.setString(1, ticker);
			
			if(st.execute()){
				ResultSet result = st.getResultSet();
				result.next();
				oldestDate = result.getDate(1);
			}
		} catch (SQLException er) {
			er.printStackTrace();
		} finally{
			if (st != null) { try {
				st.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			} }
		}
		
		return date.compareTo(oldestDate)>=0;
		
	}
	
	//Check if we have enough cash to perform transaction
	private Boolean enoughCashForTransaction(int userID, java.sql.Date date, String ticker, Double quantity){
		Double curCash = 0.00;
		
		@SuppressWarnings("deprecation")
		String sql="select current_cash(?,?)"; //userID, date
		
		try {
			st = con.prepareStatement(sql);
			
			st.setInt(1, userID );
			st.setDate(2, date);
			
			if(st.execute()){
				ResultSet result = st.getResultSet();
				result.next();
				curCash = result.getBigDecimal(1).doubleValue();
			}
		} catch (SQLException er) {
			er.printStackTrace();
		} finally{
			if (st != null) { try {
				st.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			} }
		}
		
		return curCash > (quantity * tickerValue(ticker, date) );
		
	}
	
	private Boolean enoughStocksForTransaction(int userID, java.sql.Date date, String ticker, int quantity){
		int heldQuantity = 0;
		
		@SuppressWarnings("deprecation")
		String sql="select current_holdings(?,?,?)"; //userID, date, ticker
		
		try {
			st = con.prepareStatement(sql);
			
			st.setInt(1, userID );
			st.setDate(2, date);
			st.setString(3, ticker);
			
			if(st.execute()){
				ResultSet result = st.getResultSet();
				result.next();
				heldQuantity = result.getBigDecimal(1).intValue();
			}
		} catch (SQLException er) {
			er.printStackTrace();
		} finally{
			if (st != null) { try {
				st.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			} }
		}
		
		return heldQuantity > quantity;
		
	}
	
	//this is just a stub for now, it should return the ticker value for a given date.....
	private Double tickerValue(String ticker, java.sql.Date date){
		double value = 0.0;
		
		@SuppressWarnings("deprecation")
		String sql="select current_price(?,?)"; //ticker, date
		
		try {
			st = con.prepareStatement(sql);
			
			st.setString(1, ticker );
			st.setDate(2, date);
			
			if(st.execute()){
				ResultSet result = st.getResultSet();
				result.next();
				value = result.getBigDecimal(1).doubleValue();
			}
		} catch (SQLException er) {
			er.printStackTrace();
		} finally{
			if (st != null) { try {
				st.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			} }
		}
		
		return value;

	}
	
	//Checks if future transactions exist
	private Boolean hasFutureTransactions(int userID, java.sql.Date date){
		
		Boolean hasFutureTransactions = false;
		
		@SuppressWarnings("deprecation")
		String sql="select has_future_transactions(?,?)"; //userID, date
		
		try {
			st = con.prepareStatement(sql);
			
			st.setInt(1, userID);
			st.setDate(2, date);
			
			if(st.execute()){
				ResultSet result = st.getResultSet();
				result.next();
				hasFutureTransactions = result.getBoolean(1);
			}
		} catch (SQLException er) {
			er.printStackTrace();
		} finally{
			if (st != null) { try {
				st.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			} }
		}
		
		return hasFutureTransactions;
	}
	
	//Returns a java.sql.date given a date string in the format yyyy-MM-dd
	private java.sql.Date getSQLdate(String date){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date parsed = null;
		try {
			parsed = format.parse(date);
		} catch (ParseException e2) {
			e2.printStackTrace();
		}
		
		return new java.sql.Date(parsed.getTime());
	}
}
