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

import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.markers.SeriesMarkers;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;

import java.text.ParseException;

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
	private String sql = null;
	private PreparedStatement st = null;
	private JTextField txtUserIDnew;
	private JTextField txtName;
	private JTextField txtDOB;
	private JTextField txtCashOnHandNew;
	Connection con;
	private JTextField txtUSERID;
	
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
		frmStockMartket.setBounds(100, 100, 897, 356);
		frmStockMartket.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmStockMartket.getContentPane().setLayout(null);
		
		txtTicker = new JTextField();
		txtTicker.setBounds(67, 112, 86, 20);
		frmStockMartket.getContentPane().add(txtTicker);
		txtTicker.setColumns(10);
		
		txtQuantity = new JTextField(); 
		txtQuantity.setBounds(67, 143, 86, 20);
		frmStockMartket.getContentPane().add(txtQuantity);
		txtQuantity.setColumns(10);
		
		txtDate = new JTextField();
		txtDate.setBounds(67, 174, 86, 20);
		frmStockMartket.getContentPane().add(txtDate);
		txtDate.setColumns(10);
		
		JComboBox cmbBuySell = new JComboBox();
		cmbBuySell.setModel(new DefaultComboBoxModel(new String[] {"Buy", "Sell"}));
		cmbBuySell.setBounds(67, 205, 86, 20);
		frmStockMartket.getContentPane().add(cmbBuySell);
		
		JLabel lblNewLabel = new JLabel("Ticker");
		lblNewLabel.setBounds(10, 115, 46, 14);
		frmStockMartket.getContentPane().add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Quantity");
		lblNewLabel_1.setBounds(11, 146, 46, 14);
		frmStockMartket.getContentPane().add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("Date");
		lblNewLabel_2.setBounds(10, 178, 46, 14);
		frmStockMartket.getContentPane().add(lblNewLabel_2);
		
		JLabel lblNewLabel_3 = new JLabel("Action");
		lblNewLabel_3.setBounds(10, 208, 46, 14);
		frmStockMartket.getContentPane().add(lblNewLabel_3);
		
		//buy/sell logic. 
		JButton btnBuySell = new JButton("Submit");
		btnBuySell.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				int userID = Integer.parseInt(txtUSERID.getText());
				java.sql.Date date = getSQLdate(txtDate.getText());
				String ticker = txtTicker.getText();
				int quantity = Integer.parseInt(txtQuantity.getText());
				
				//If a transaction for that user/day/ticker already exists in the
				//database, delete it so we can add the current transaction
				if (transactionExists(userID, date, ticker)){
					deleteTransaction();
				}
				
				if(cmbBuySell.getSelectedIndex() == 0){ //Buy

					if(!validDateForTransaction(date, ticker)){
						JOptionPane.showMessageDialog(null,"Company didn't exist back then, please change date !");
						return;
					}
					
					if(!enoughCashForTransaction(userID, date, ticker, (double) quantity)){
						JOptionPane.showMessageDialog(null,"Not enough cash on hand to perform transaction");
						return;
					}
					
					//Looks like we have enough cash on hand for the transaction so
					//let's go ahead with it
					addCurTransaction(userID, date, ticker, BigDecimal.valueOf(quantity));
					
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
		btnBuySell.setBounds(29, 246, 95, 39);
		frmStockMartket.getContentPane().add(btnBuySell);
		
		JSeparator separator = new JSeparator();
		separator.setOrientation(SwingConstants.VERTICAL);
		separator.setBounds(178, 11, 2, 280);
		frmStockMartket.getContentPane().add(separator);
		
		txtUserID = new JTextField();
		txtUserID.setBounds(270, 79, 86, 20);
		frmStockMartket.getContentPane().add(txtUserID);
		txtUserID.setColumns(10);
		
		txtTicker2 = new JTextField();
		txtTicker2.setBounds(270, 120, 86, 20);
		frmStockMartket.getContentPane().add(txtTicker2);
		txtTicker2.setColumns(10);
		
		txtQuantity2 = new JTextField();
		txtQuantity2.setEditable(false);
		txtQuantity2.setEnabled(false);
		txtQuantity2.setBounds(270, 161, 86, 20);
		frmStockMartket.getContentPane().add(txtQuantity2);
		txtQuantity2.setColumns(10);
		
		txtValue = new JTextField();
		txtValue.setEnabled(false);
		txtValue.setEditable(false);
		txtValue.setBounds(270, 205, 86, 20);
		frmStockMartket.getContentPane().add(txtValue);
		txtValue.setColumns(10);
		
		JLabel lblNewLabel_4 = new JLabel("User ID");
		lblNewLabel_4.setBounds(190, 82, 46, 14);
		frmStockMartket.getContentPane().add(lblNewLabel_4);
		
		JLabel lblNewLabel_5 = new JLabel("Ticker");
		lblNewLabel_5.setBounds(190, 123, 46, 14);
		frmStockMartket.getContentPane().add(lblNewLabel_5);
		
		JLabel lblNewLabel_6 = new JLabel("Quantity");
		lblNewLabel_6.setBounds(190, 164, 46, 14);
		frmStockMartket.getContentPane().add(lblNewLabel_6);
		
		JLabel lblNewLabel_7 = new JLabel("Value ($)");
		lblNewLabel_7.setBounds(190, 208, 46, 14);
		frmStockMartket.getContentPane().add(lblNewLabel_7);
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setOrientation(SwingConstants.VERTICAL);
		separator_1.setBounds(388, 11, 2, 280);
		frmStockMartket.getContentPane().add(separator_1);
		
		txtUser3 = new JTextField();
		txtUser3.setBounds(510, 79, 86, 20);
		frmStockMartket.getContentPane().add(txtUser3);
		txtUser3.setColumns(10);
		
		txtDate3 = new JTextField();
		txtDate3.setBounds(510, 120, 86, 20);
		frmStockMartket.getContentPane().add(txtDate3);
		txtDate3.setColumns(10);
		
		txtStockValue = new JTextField();
		txtStockValue.setEnabled(false);
		txtStockValue.setEditable(false);
		txtStockValue.setBounds(510, 161, 86, 20);
		frmStockMartket.getContentPane().add(txtStockValue);
		txtStockValue.setColumns(10);
		
		txtCashOnHand = new JTextField();
		txtCashOnHand.setEnabled(false);
		txtCashOnHand.setEditable(false);
		txtCashOnHand.setBounds(510, 205, 86, 20);
		frmStockMartket.getContentPane().add(txtCashOnHand);
		txtCashOnHand.setColumns(10);
		
		JLabel lblNewLabel_8 = new JLabel("User ID");
		lblNewLabel_8.setBounds(400, 82, 46, 14);
		frmStockMartket.getContentPane().add(lblNewLabel_8);
		
		JLabel lblNewLabel_9 = new JLabel("Date");
		lblNewLabel_9.setBounds(400, 123, 46, 14);
		frmStockMartket.getContentPane().add(lblNewLabel_9);
		
		JLabel lblNewLabel_10 = new JLabel("Stock Value ($)");
		lblNewLabel_10.setBounds(400, 164, 72, 14);
		frmStockMartket.getContentPane().add(lblNewLabel_10);
		
		JLabel lblNewLabel_11 = new JLabel("Cash on Hand ($)");
		lblNewLabel_11.setBounds(400, 208, 100, 14);
		frmStockMartket.getContentPane().add(lblNewLabel_11);
		
		JLabel lblBuysell = new JLabel("Buy/Sell");
		lblBuysell.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblBuysell.setBounds(48, 26, 76, 20);
		frmStockMartket.getContentPane().add(lblBuysell);
		
		JLabel lblTickerStatus = new JLabel("Ticker Status");
		lblTickerStatus.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblTickerStatus.setBounds(225, 26, 119, 20);
		frmStockMartket.getContentPane().add(lblTickerStatus);
		
		JLabel lblPortofollio = new JLabel("Portofolio");
		lblPortofollio.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblPortofollio.setBounds(454, 26, 95, 20);
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
		btnUpdate2.setBounds(190, 246, 95, 39);
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
		btnUpdate3.setBounds(454, 246, 95, 39);
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
		btnGraph.setBounds(283, 246, 95, 39);
		frmStockMartket.getContentPane().add(btnGraph);
		
		JSeparator separator_2 = new JSeparator();
		separator_2.setOrientation(SwingConstants.VERTICAL);
		separator_2.setBounds(634, 11, 2, 280);
		frmStockMartket.getContentPane().add(separator_2);
		
		JLabel label = new JLabel("User ID");
		label.setBounds(646, 82, 46, 14);
		frmStockMartket.getContentPane().add(label);
		
		JLabel lblName = new JLabel("Name");
		lblName.setBounds(646, 123, 46, 14);
		frmStockMartket.getContentPane().add(lblName);
		
		JLabel lblDateOfBirth = new JLabel("Date of Birth");
		lblDateOfBirth.setBounds(646, 164, 72, 14);
		frmStockMartket.getContentPane().add(lblDateOfBirth);
		
		txtUserIDnew = new JTextField();
		txtUserIDnew.setColumns(10);
		txtUserIDnew.setBounds(756, 79, 86, 20);
		frmStockMartket.getContentPane().add(txtUserIDnew);
		
		txtName = new JTextField();
		txtName.setColumns(10);
		txtName.setBounds(756, 120, 86, 20);
		frmStockMartket.getContentPane().add(txtName);
		
		txtDOB = new JTextField();
		txtDOB.setColumns(10);
		txtDOB.setBounds(756, 161, 86, 20);
		frmStockMartket.getContentPane().add(txtDOB);
		
		JLabel lblNewUser = new JLabel("New User");
		lblNewUser.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblNewUser.setBounds(720, 26, 95, 20);
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
		
		btnCreate.setBounds(731, 249, 95, 33);
		frmStockMartket.getContentPane().add(btnCreate);
		
		JLabel label_1 = new JLabel("Cash on Hand ($)");
		label_1.setBounds(646, 208, 100, 14);
		frmStockMartket.getContentPane().add(label_1);
		
		txtCashOnHandNew = new JTextField();
		txtCashOnHandNew.setColumns(10);
		txtCashOnHandNew.setBounds(756, 205, 86, 20);
		frmStockMartket.getContentPane().add(txtCashOnHandNew);
		
		JLabel lblTxtuserid = new JLabel("User ID");
		lblTxtuserid.setBounds(10, 82, 46, 14);
		frmStockMartket.getContentPane().add(lblTxtuserid);
		
		txtUSERID = new JTextField();
		txtUSERID.setColumns(10);
		txtUSERID.setBounds(67, 81, 86, 20);
		frmStockMartket.getContentPane().add(txtUSERID);
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
