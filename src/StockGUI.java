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
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.List;
import java.util.ArrayList;
import java.text.SimpleDateFormat; // can remove this after testing?
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.markers.SeriesMarkers;
import org.knowm.xchart.QuickChart;	
import org.knowm.xchart.SwingWrapper;

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
	private Statement st = null;
	
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
			Connection con=DriverManager.getConnection("jdbc:postgresql://localhost:5432/Expenses","postgres","PASSWORD HERE");
			if(con!=null){
				System.out.println("Connected");
			}
			st = con.createStatement();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		//End postgress stuff
		
		frmStockMartket = new JFrame();
		frmStockMartket.setTitle("Stock Market");
		frmStockMartket.setBounds(100, 100, 656, 352);
		frmStockMartket.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmStockMartket.getContentPane().setLayout(null);
		
		txtTicker = new JTextField();
		txtTicker.setText("YHOO");
		txtTicker.setBounds(67, 79, 86, 20);
		frmStockMartket.getContentPane().add(txtTicker);
		txtTicker.setColumns(10);
		
		txtQuantity = new JTextField();
		txtQuantity.setText("56");
		txtQuantity.setBounds(67, 120, 86, 20);
		frmStockMartket.getContentPane().add(txtQuantity);
		txtQuantity.setColumns(10);
		
		txtDate = new JTextField();
		txtDate.setText("11/21/2016");
		txtDate.setBounds(67, 161, 86, 20);
		frmStockMartket.getContentPane().add(txtDate);
		txtDate.setColumns(10);
		
		JComboBox cmbBuySell = new JComboBox();
		cmbBuySell.setModel(new DefaultComboBoxModel(new String[] {"Buy", "Sell"}));
		cmbBuySell.setBounds(67, 205, 86, 20);
		frmStockMartket.getContentPane().add(cmbBuySell);
		
		JLabel lblNewLabel = new JLabel("Ticker");
		lblNewLabel.setBounds(10, 82, 46, 14);
		frmStockMartket.getContentPane().add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Quantity");
		lblNewLabel_1.setBounds(10, 123, 46, 14);
		frmStockMartket.getContentPane().add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("Date");
		lblNewLabel_2.setBounds(10, 164, 46, 14);
		frmStockMartket.getContentPane().add(lblNewLabel_2);
		
		JLabel lblNewLabel_3 = new JLabel("Action");
		lblNewLabel_3.setBounds(10, 208, 46, 14);
		frmStockMartket.getContentPane().add(lblNewLabel_3);
		
		JButton btnBuySell = new JButton("Submit");
		btnBuySell.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (transactionExists()){
					JOptionPane.showMessageDialog(null, "Write error"); 
					return;
				}
				@SuppressWarnings("deprecation")
				String sql="PUT SQL STATEMENT HERE";
				try {
					ResultSet resultSet = st.executeQuery(sql);
				} catch (SQLException e) {
					e.printStackTrace();
					return;
				}
				System.out.println("SQL Command sent successfully");
			}
		});
		btnBuySell.setBounds(29, 246, 95, 39);
		frmStockMartket.getContentPane().add(btnBuySell);
		
		JSeparator separator = new JSeparator();
		separator.setOrientation(SwingConstants.VERTICAL);
		separator.setBounds(178, 11, 2, 280);
		frmStockMartket.getContentPane().add(separator);
		
		txtUserID = new JTextField();
		txtUserID.setText("John544");
		txtUserID.setBounds(270, 79, 86, 20);
		frmStockMartket.getContentPane().add(txtUserID);
		txtUserID.setColumns(10);
		
		txtTicker2 = new JTextField();
		txtTicker2.setText("YHOO");
		txtTicker2.setBounds(270, 120, 86, 20);
		frmStockMartket.getContentPane().add(txtTicker2);
		txtTicker2.setColumns(10);
		
		txtQuantity2 = new JTextField();
		txtQuantity2.setText("25");
		txtQuantity2.setEditable(false);
		txtQuantity2.setEnabled(false);
		txtQuantity2.setBounds(270, 161, 86, 20);
		frmStockMartket.getContentPane().add(txtQuantity2);
		txtQuantity2.setColumns(10);
		
		txtValue = new JTextField();
		txtValue.setText("502.5");
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
		txtUser3.setText("John544");
		txtUser3.setBounds(510, 79, 86, 20);
		frmStockMartket.getContentPane().add(txtUser3);
		txtUser3.setColumns(10);
		
		txtDate3 = new JTextField();
		txtDate3.setText("10/15/2015");
		txtDate3.setBounds(510, 120, 86, 20);
		frmStockMartket.getContentPane().add(txtDate3);
		txtDate3.setColumns(10);
		
		txtStockValue = new JTextField();
		txtStockValue.setText("3522");
		txtStockValue.setEnabled(false);
		txtStockValue.setEditable(false);
		txtStockValue.setBounds(510, 161, 86, 20);
		frmStockMartket.getContentPane().add(txtStockValue);
		txtStockValue.setColumns(10);
		
		txtCashOnHand = new JTextField();
		txtCashOnHand.setText("560");
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
		
		JButton btnUpdate2 = new JButton("Update");
		btnUpdate2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				@SuppressWarnings("deprecation")
				String sql="PUT SQL STATEMENT HERE";
				try {
					ResultSet resultSet = st.executeQuery(sql);
				} catch (SQLException er) {
					er.printStackTrace();
					return;
				} finally{
					if (st != null) { try {
						st.close();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} }
				}
				System.out.println("SQL Command sent successfully");
			}
		});
		btnUpdate2.setBounds(190, 246, 95, 39);
		frmStockMartket.getContentPane().add(btnUpdate2);
		
		JButton btnUpdate3 = new JButton("Update");
		btnUpdate3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				@SuppressWarnings("deprecation")
				String sql="PUT SQL STATEMENT HERE";
				try {
					st.execute(sql);
				} catch (SQLException er) {
					er.printStackTrace();
					return;
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
				String sql="net_worth_trending(" + txtUserID + ")";
				try {
					
					//ResultSet rs = st.executeQuery(sql);
					List<java.util.Date> xData = new ArrayList<java.util.Date>();
					List<Double> yData = new ArrayList<Double>();
					/*
					while (rs.next()) {
						xData.add(rs.getDate("curDate"));
						yData.add(rs.getFloat("curNetWorth"));						
					}
					*/

					DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					xData.add(sdf.parse("2016-01-01"));
					xData.add(sdf.parse("2016-02-02"));
					xData.add(sdf.parse("2016-03-15"));
					yData.add(1000.0);
					yData.add(1500.1);
					yData.add(900.5);
										
					//double[] xData = new double[] { 0.0, 1.0, 2.0 };
				    //double[] yData = new double[] { 2.0, 1.0, 0.0 };

				    // Create Chart
				    //XYChart chart = QuickChart.getChart("Net worth trend", "Date", "Net worth ($)", "whatevs", xData, yData);

					XYChart chart = new XYChartBuilder().width(800).height(600).title("Net worth trend").build();
					chart.getStyler().setLegendVisible(false);
					XYSeries series = chart.addSeries("blah", xData, yData);
					series.setMarker(SeriesMarkers.NONE);
					
				    // Show it
				    new SwingWrapper(chart).displayChart();

				} catch (Exception er) {
					er.printStackTrace();
					return;
				} finally {
					if (st != null) {
						try { 
							st.close();
						}
						catch (SQLException er) {
							return;
						}
					}
				}
				
			}
		});
		btnGraph.setBounds(283, 246, 95, 39);
		frmStockMartket.getContentPane().add(btnGraph);
	}
	
	//we will call in to the stored procedure to check if a transaction exists for the given date/ticker
	//inputed by user. If it does, we will then execute a remove_transaction followed by an add_transaction
	//command to replace the old transaction with the new
	private boolean transactionExists(){
		return true;
	}
}
