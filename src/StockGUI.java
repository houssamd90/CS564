import java.awt.EventQueue;

import javax.swing.JFrame;
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

public class StockGUI {

	private JFrame frmStockMartket;
	private JTextField txtYhoo;
	private JTextField textField_1;
	private JTextField txtYhoo_1;
	private JTextField txtJohn;
	private JTextField txtYhoo_2;
	private JTextField textField_3;
	private JTextField textField_4;
	private JTextField txtJohn_1;
	private JTextField textField_6;
	private JTextField textField_7;
	private JTextField textField_8;

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
		frmStockMartket = new JFrame();
		frmStockMartket.setTitle("Stock Market");
		frmStockMartket.setBounds(100, 100, 656, 352);
		frmStockMartket.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmStockMartket.getContentPane().setLayout(null);
		
		txtYhoo = new JTextField();
		txtYhoo.setText("YHOO");
		txtYhoo.setBounds(67, 79, 86, 20);
		frmStockMartket.getContentPane().add(txtYhoo);
		txtYhoo.setColumns(10);
		
		textField_1 = new JTextField();
		textField_1.setText("56");
		textField_1.setBounds(67, 120, 86, 20);
		frmStockMartket.getContentPane().add(textField_1);
		textField_1.setColumns(10);
		
		txtYhoo_1 = new JTextField();
		txtYhoo_1.setText("11/21/2016");
		txtYhoo_1.setBounds(67, 161, 86, 20);
		frmStockMartket.getContentPane().add(txtYhoo_1);
		txtYhoo_1.setColumns(10);
		
		JComboBox comboBox = new JComboBox();
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"Buy", "Sell"}));
		comboBox.setBounds(67, 205, 86, 20);
		frmStockMartket.getContentPane().add(comboBox);
		
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
		
		JButton btnNewButton = new JButton("Submit");
		btnNewButton.setBounds(29, 246, 95, 39);
		frmStockMartket.getContentPane().add(btnNewButton);
		
		JSeparator separator = new JSeparator();
		separator.setOrientation(SwingConstants.VERTICAL);
		separator.setBounds(178, 11, 2, 280);
		frmStockMartket.getContentPane().add(separator);
		
		txtJohn = new JTextField();
		txtJohn.setText("John544");
		txtJohn.setBounds(270, 79, 86, 20);
		frmStockMartket.getContentPane().add(txtJohn);
		txtJohn.setColumns(10);
		
		txtYhoo_2 = new JTextField();
		txtYhoo_2.setText("YHOO");
		txtYhoo_2.setBounds(270, 120, 86, 20);
		frmStockMartket.getContentPane().add(txtYhoo_2);
		txtYhoo_2.setColumns(10);
		
		textField_3 = new JTextField();
		textField_3.setText("25");
		textField_3.setEditable(false);
		textField_3.setEnabled(false);
		textField_3.setBounds(270, 161, 86, 20);
		frmStockMartket.getContentPane().add(textField_3);
		textField_3.setColumns(10);
		
		textField_4 = new JTextField();
		textField_4.setText("502.5");
		textField_4.setEnabled(false);
		textField_4.setEditable(false);
		textField_4.setBounds(270, 205, 86, 20);
		frmStockMartket.getContentPane().add(textField_4);
		textField_4.setColumns(10);
		
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
		
		txtJohn_1 = new JTextField();
		txtJohn_1.setText("John544");
		txtJohn_1.setBounds(510, 79, 86, 20);
		frmStockMartket.getContentPane().add(txtJohn_1);
		txtJohn_1.setColumns(10);
		
		textField_6 = new JTextField();
		textField_6.setText("10/15/2015");
		textField_6.setBounds(510, 120, 86, 20);
		frmStockMartket.getContentPane().add(textField_6);
		textField_6.setColumns(10);
		
		textField_7 = new JTextField();
		textField_7.setText("3522");
		textField_7.setEnabled(false);
		textField_7.setEditable(false);
		textField_7.setBounds(510, 161, 86, 20);
		frmStockMartket.getContentPane().add(textField_7);
		textField_7.setColumns(10);
		
		textField_8 = new JTextField();
		textField_8.setText("560");
		textField_8.setEnabled(false);
		textField_8.setEditable(false);
		textField_8.setBounds(510, 205, 86, 20);
		frmStockMartket.getContentPane().add(textField_8);
		textField_8.setColumns(10);
		
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
	}
}
