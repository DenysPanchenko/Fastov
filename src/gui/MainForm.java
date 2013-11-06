package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.ScrollPane;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.JTable;
import javax.swing.JButton;

import core.DBManager;
import core.DataBase;
import core.Table;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainForm extends JFrame implements MouseListener{

	private JPanel contentPane;
	private JTable table;
	private JPanel panel;
	private static JButton saveBtn;
	private static JButton cancelBtn;
	private static JButton createDBBtn;
	private static JButton createTableBtn;
	private static JButton removeTableBtn;
	private static JButton projectionBtn;
	private static JButton unitTableBtn;
	private static JButton removeDB;
	private static JButton addColumnBtn;
	private static JButton deleteColumnBtn;
	private static JButton addRowBtn;
	private static DBManager dbManager;
	private JPanel panel_1;
	private JPanel panel_2;
	private JTree tree;

	private enum BUTTON_SET_LEVEL {MANAGER_LEVEL, DATABASE_LEVEL, TABLE_LEVEL};
	
	/**
	 * Create the frame.
	 */
	public MainForm() {
		dbManager = new DBManager();
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(50, 50, 950, 700);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		tree = new JTree(MainFormMng.createTreeNodes(dbManager));
		contentPane.add(tree, BorderLayout.WEST);
		tree.addMouseListener(this);
		
		panel_1 = new JPanel();
		contentPane.add(panel_1, BorderLayout.CENTER);
		
		table = new JTable();
		table.setFillsViewportHeight(true);
		panel_1.add(new JScrollPane(table));
		
		panel = new JPanel();
		contentPane.add(panel, BorderLayout.EAST);
		panel.setLayout(new GridLayout(1, 1, 0, 0));
		
		panel_2 = new JPanel();
		panel.add(panel_2);
		panel_2.setLayout(new GridLayout(0, 1, 10, 10));
		
		saveBtn = new JButton("Save");
		panel_2.add(saveBtn);
		saveBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dbManager.save();
				DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
				model.setRoot(MainFormMng.createTreeNodes(dbManager));
			}
		});
		
		cancelBtn = new JButton("Cancel");
		panel_2.add(cancelBtn);
		cancelBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dbManager.cancelAllActions();
			}
		});
		
		createDBBtn = new JButton("Create DB");
		panel_2.add(createDBBtn);
		createDBBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tree = MainFormMng.createDB(dbManager, tree);
			}
		});
		
		removeDB = new JButton("Remove DB");
		panel_2.add(removeDB);
		removeDB.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				tree = MainFormMng.removeDB(tree, dbManager);
			}
		});
		
		createTableBtn = new JButton("Create Table");
		panel_2.add(createTableBtn);
		createTableBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				tree = MainFormMng.createTable(dbManager, tree);
			}
		});
		
		removeTableBtn = new JButton("Remove Table");
		panel_2.add(removeTableBtn);
		removeTableBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tree = MainFormMng.removeTable(tree, dbManager);
			}
		});
		
		addColumnBtn = new JButton("Add column");
		panel_2.add(addColumnBtn);
		addColumnBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				table = MainFormMng.addColumnToTable(dbManager, tree);
			}
		});
		
		deleteColumnBtn = new JButton("Remove column");
		panel_2.add(deleteColumnBtn);
		deleteColumnBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				 
				
			}
		});
		
		addRowBtn = new JButton("Add row");
		panel_2.add(addRowBtn);
		addRowBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
			}
		});
		
		projectionBtn = new JButton("Tables Projection");
		panel_2.add(projectionBtn);
		
		unitTableBtn = new JButton("Tables Join");
		panel_2.add(unitTableBtn);
		unitTableBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				table = MainFormMng.unitTable(dbManager, tree);
			}
		});
		
		AutorizationWindow dialog = new AutorizationWindow(dbManager);
		
		dialog.setVisible(true);
		dialog.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosed(WindowEvent e){
				System.exit(NORMAL);
			}
		}
		);
		//dialog.setModal(true);
		setButtonsSetLevel(BUTTON_SET_LEVEL.MANAGER_LEVEL);
		setLocationRelativeTo(null);
	}
	
	public static void disableButtons() {
		createDBBtn.setEnabled(false);
		saveBtn.setEnabled(false);
		removeTableBtn.setEnabled(false);
		createTableBtn.setEnabled(false);
		removeDB.setEnabled(false);
		cancelBtn.setEnabled(false);
	}
	
	/*private JDialog getCreationTableDialog() {
		final JDialog createTableDialog = new JDialog();
		final JTextField tName = new JTextField();
		final JTextField cName = new JTextField();
		final List<String> columnsNames = new ArrayList<String>();
		final List<DataType> columnTypes = new ArrayList<DataType>();
		final JComboBox types = new JComboBox(DataType.TYPE.values());
		final JTable newTable = new JTable();
		JLabel tableNameL = new JLabel("Table Name:    ");
		JLabel columnNameL = new JLabel("Column Name: ");
		JLabel lblNewLabel = new JLabel("Choose type:   ");
		JPanel tablePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
		JPanel columnPanel = new JPanel();
		JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 50, 50));
		JPanel okPanel = new JPanel();
		JPanel cancelPanel = new JPanel();
		JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
		JButton addBtn = new JButton("Add");
		JButton okBtn = new JButton("   Ok   ");
		JButton cancelBtn = new JButton("Cancel");
		
		cName.setColumns(19);
		tName.setColumns(25);
		tablePanel.add(tableNameL);
		tablePanel.add(tName);
		columnPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		columnPanel.add(columnNameL);
		columnPanel.add(cName);
		columnPanel.add(addBtn);
		
		createTableDialog.setSize(400, 200);
		createTableDialog.getContentPane().setLayout(new GridLayout(4, 1, 10, 10));
		createTableDialog.getContentPane().add(tablePanel);
		createTableDialog.getContentPane().add(columnPanel);
		createTableDialog.getContentPane().add(typePanel);
		createTableDialog.getContentPane().add(buttonPanel);
	
		typePanel.add(lblNewLabel);
		typePanel.add(types);
		
		buttonPanel.add(okPanel);
		okBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(tName.getText() != null) {
					newTable.setName(tName.getText());
				}
				tree = MainFormMng.createTable(dbManager, tree, newTable.getName(), columnsNames, columnTypes);
				tree.repaint();
				createTableDialog.setVisible(false);
			}
		});		
		
		cancelBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				createTableDialog.setVisible(false);
			}
		});
		
		addBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String columnName = cName.getText(); 
				if(columnName != null) {
					TableColumn column = new TableColumn();
					column.setHeaderValue(columnName);
					newTable.addColumnBtn(column);
					columnsNames.add(columnName);
					columnTypes.add(new DataType((TYPE)types.getSelectedItem()));
					cName.setText("");
				}
			}
		});
		
		okPanel.add(okBtn);
		buttonPanel.add(cancelPanel);
		cancelPanel.add(cancelBtn);
		
		createTableDialog.setVisible(true);
		createTableDialog.setModalityType(ModalityType.APPLICATION_MODAL);
		
		return createTableDialog;
	}*/
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainForm dbBaseMainForm = new MainForm();
					dbBaseMainForm.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void mouseClicked(MouseEvent event) {
		// TODO Auto-generated method stub
		TreePath path = tree.getPathForLocation(event.getX(), event.getY());
		if(path != null)
		{
			if(path.getPathCount() == 3)
			{
				setButtonsSetLevel(BUTTON_SET_LEVEL.TABLE_LEVEL);
				String tableName = path.getLastPathComponent().toString();
				String dbName = path.getPathComponent(1).toString();
				DataBase db = null;
				for(int i = 0; i < dbManager.getDataBaseList().size(); i++)
					if(dbManager.getDataBaseList().get(i).getName().equals(dbName))
					{
						db = dbManager.getDataBaseList().get(i);
						break;
					}
				if(db == null)
					return;
				Table t = null;
				for(int j = 0; j < db.getTableList().size(); j++)
					if(db.getTableList().get(j).getTableName().equals(tableName))
					{
						t = db.getTableList().get(j);
						break;
					}
				if(t == null)
					return;
				table.setModel(TableConverter.convertTableToJTable(t).getModel());
				table.repaint();
			} else if(path.getPathCount() == 2) {
				setButtonsSetLevel(BUTTON_SET_LEVEL.DATABASE_LEVEL);
			} else {
				setButtonsSetLevel(BUTTON_SET_LEVEL.MANAGER_LEVEL);
			}
		}
	}

	public void setButtonsSetLevel(BUTTON_SET_LEVEL setLevel)
	{
		if(saveBtn.isEnabled())
			if(setLevel == BUTTON_SET_LEVEL.MANAGER_LEVEL)
			{
				createDBBtn.setEnabled(true);
				removeDB.setEnabled(false);
				createTableBtn.setEnabled(false);
				removeTableBtn.setEnabled(false);
				addColumnBtn.setEnabled(false);
				deleteColumnBtn.setEnabled(false);
				addRowBtn.setEnabled(false);
				unitTableBtn.setEnabled(false);
				projectionBtn.setEnabled(false);
			} else if(setLevel == BUTTON_SET_LEVEL.DATABASE_LEVEL) {
				createDBBtn.setEnabled(true);
				removeDB.setEnabled(true);
				createTableBtn.setEnabled(true);
				removeTableBtn.setEnabled(false);
				addColumnBtn.setEnabled(false);
				deleteColumnBtn.setEnabled(false);
				addRowBtn.setEnabled(false);
				unitTableBtn.setEnabled(false);
				projectionBtn.setEnabled(false);
			} else if(setLevel == BUTTON_SET_LEVEL.TABLE_LEVEL) {
				createDBBtn.setEnabled(true);
				removeDB.setEnabled(false);
				createTableBtn.setEnabled(true);
				removeTableBtn.setEnabled(true);
				addColumnBtn.setEnabled(true);
				deleteColumnBtn.setEnabled(true);
				addRowBtn.setEnabled(true);
				unitTableBtn.setEnabled(true);
				projectionBtn.setEnabled(true);
			}
		getContentPane().repaint();
	}
	
	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
