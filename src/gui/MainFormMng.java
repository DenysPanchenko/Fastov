package gui;

import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import core.DBManager;
import core.DataBase;
import core.DataType;
import core.Table;

public class MainFormMng { 
	
	public static JTree createDB(DBManager dbManager, JTree tree) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
		assert(node != null);
		List<String> dbNames = new ArrayList<String>();
		List<DataBase> dataBases = dbManager.getDataBaseList();

		String dbName = JOptionPane.showInputDialog("Enter data base name");

		if(dataBases != null) {
			for(DataBase db: dataBases) {
				dbNames.add(db.getName());
			}
		}
		while(dbNames.contains(dbName) && dbName != null){
			dbName = JOptionPane.showInputDialog("DataBase is already exists. Enter another name");
		}
		if(dbName != null) 
		{	
			// ATTENTION BELOW YOU CAN SEE SIMPLE REFLECTION EXAMPLE
			try {
				Method m = DBManager.class.getMethod("createDB", String.class);
				m.invoke(dbManager, dbName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return tree;
	}
	
/*	private static JTree addNewNodeToTree(Object o, JTree tree) {
		DefaultMutableTreeNode root = getRoot(tree);
		if(o instanceof DataBase) {
			root.add(new DefaultMutableTreeNode(o));
		} else {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
			node.add(new DefaultMutableTreeNode(o));
		}
		
		tree.setModel(new DefaultTreeModel(root));
		return tree;
	}

	private static DefaultMutableTreeNode getRoot(JTree tree) {
		DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
		return (DefaultMutableTreeNode)model.getRoot();
	}
*/
	
	public static JTree removeTable(JTree tree, DBManager dbManager) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
		assert(node != null);
		Object userObject = node.getUserObject();
		if(userObject instanceof Table) {
			Table table = (Table)userObject;
			DefaultMutableTreeNode parent = (DefaultMutableTreeNode)node.getParent();
			DataBase dataBase = (DataBase)(parent.getUserObject());
			dbManager.deleteTable(dataBase.getName(), table.getTableName());
		}
		removeNodeFromTree(node);
		return tree;
	}
	
	public static void removeNodeFromTree(Object o) {
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(o);
		node.removeFromParent();
	}
	
	public static DefaultMutableTreeNode createTreeNodes(DBManager dbManager) {
		
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Available data bases");
		List<DataBase> dataBases = dbManager.getDataBaseList();
		DataBase curDB;
		DefaultMutableTreeNode dataBaseNode;
		DefaultMutableTreeNode tableNode;
		
		if(dataBases != null) {
			for(int i = 0; i < dataBases.size(); i++) {
				curDB = dataBases.get(i);
				dataBaseNode = new DefaultMutableTreeNode(curDB);
				if(curDB.getTableList() != null) {
					for(int j = 0; j < curDB.getTableList().size(); j++) {
						tableNode = new DefaultMutableTreeNode(curDB.getTableList().get(j));
						dataBaseNode.add(tableNode);
					}
				}
				root.add(dataBaseNode);
			}
		}
		return root;
	}

	public static JTree createTable(DBManager dbManager, JTree tree) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
		assert(node != null);
		Object userObject = node.getUserObject();
		if(userObject instanceof DataBase) {
			DataBase dataBase = (DataBase) userObject;
			List<Table> tables = dataBase.getTableList();
			List<String> tablesNames = new ArrayList<String>();
			String tableName = JOptionPane.showInputDialog("Enter table name");
			
			if(tables != null) {
				for(Table table: tables) {
					tablesNames.add(table.getTableName());
				}
			}
			while(tablesNames.contains(tableName) && tableName != null){
				tableName = JOptionPane.showInputDialog("Table is already exists. Enter another name");
			}
			if(tableName != null) {
				dbManager.createTable(dataBase.getName(), tableName);
				return tree;
			} else {
				return tree;
			}
			//Table table = dbManager.createTable(dataBase, columnsNames, columnTypes);
			//dbManager.createTable(((DataBase) userObject).getName(), columnsNames, columnTypes);
			//return addNewNodeToTree(table, tree);
		}
		return tree;
	}

	public static JTree removeDB(JTree tree, DBManager dbManager) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
		assert(node != null);
		Object o = node.getUserObject();
		if(o instanceof DataBase) {
			DataBase dataBase = (DataBase)o;
			
			// ATTENTION BELOW YOU CAN SEE SIMPLE REFLECTION EXAMPLE
			
			try 
			{
				Method deleteDB = DBManager.class.getMethod("deleteDB", String.class);
				deleteDB.invoke(dbManager, dataBase.getName());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			removeNodeFromTree(dataBase);
		}
		return tree;
	}

	public static JTable addColumnToTable(final DBManager dbManager, JTree tree) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
		assert(node != null);
		Object o = node.getUserObject();
		Object parent = ((DefaultMutableTreeNode)node.getParent()).getUserObject();
		if(o instanceof Table && parent instanceof DataBase) {
			final Table table = (Table)o;
			final DataBase dataBase = (DataBase)parent;
			final InputDialog dialog = new InputDialog();
			final JTextField columnName = new JTextField();
			final JComboBox types = new JComboBox(DataType.TYPE.values());
			
			columnName.setColumns(10);
			
			dialog.initialize(columnName, types, "Add column", "Column name:", "Type:", "Add");
			dialog.getAcceptButton().addActionListener(new ActionListener() {

			//	TODO Deny empty string as name for new column
				
				@Override
				public void actionPerformed(ActionEvent e) {
					dbManager.createColumn(dataBase.getName(), table.getTableName(), columnName.getText(), (DataType.TYPE)types.getSelectedItem());
					dialog.dispose();
				}
			});
			
			dialog.getBtnCancel().addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					dialog.dispose();
				}
			});
			dialog.setVisible(true);
			return convertTableToJTable(table);
		}
		return null;
	}
	
	public static JTable convertTableToJTable(Table table) {
		return TableConverter.convertTableToJTable(table);
	}

	// UniT method =D
	public static JTable unitTable(DBManager dbManager, JTree tree) {
		
		/*
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
		assert(node != null);
		Object o = node.getUserObject();
		if(o instanceof Table) {
			Table table = (Table)o;
			createUnionDialog(dbManager, table);
		}
		*/
		return null;
	}

	public static void createJoinDialog(final DBManager dbManager, final String dbName,
			final String selectedTable) {
		
		final JDialog dialog = new JDialog();
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setTitle("Table join");
		
		// TODO Smth wrong here because I can see list of all tables within DBManager =((
		
		DataBase database = null;
		Table table = null;
		for(int i = 0; i < dbManager.getDataBaseList().size(); i++)
			if(dbManager.getDataBaseList().get(i).getName().equals(dbName))
			{
				database = dbManager.getDataBaseList().get(i);
				break;
			}
		for(int i = 0; i < database.getTableList().size(); i++)
			if(database.getTableList().get(i).getTableName().equals(selectedTable))
			{
				table = database.getTableList().get(i);
				break;
			}
		if(database == null || table == null)
			return;
		
		final JComboBox tables = new JComboBox(database.getTableList().toArray());
		final JComboBox columnsOfSelectedTable = new JComboBox(table.getColumnNames().toArray());
		 
		JButton joinBtn = new JButton("Join");
		joinBtn.setActionCommand("JOIN");
		joinBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(e.getActionCommand().equals("JOIN"))
					dbManager.tableJoin(dbName, selectedTable,
							tables.getSelectedItem().toString(),
							columnsOfSelectedTable.getSelectedItem().toString());
					dialog.dispose();
			}
		});
		
		final JButton cancelBtn = new JButton("Cancel");
		cancelBtn.setActionCommand("EXIT");
		cancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getActionCommand().equals("EXIT")){
					dialog.dispose();
				}
			}
		});
		
		GroupLayout mainLayout = new GroupLayout(dialog.getContentPane());
		dialog.getContentPane().setLayout(mainLayout);
		mainLayout.setAutoCreateGaps(true);
		mainLayout.setAutoCreateContainerGaps(true);
		
		mainLayout.setHorizontalGroup(
				mainLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
					.addGroup(
							mainLayout.createSequentialGroup()
								.addGroup(mainLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
										.addComponent(tables)
								)
								.addGroup(mainLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
										.addComponent(columnsOfSelectedTable)
								)
					)
					.addGroup(mainLayout.createSequentialGroup()
							.addComponent(joinBtn)
							.addComponent(cancelBtn)
					)
				);
		
		mainLayout.setVerticalGroup(
				mainLayout.createSequentialGroup()
					.addGroup(mainLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
							.addComponent(tables)
							.addComponent(columnsOfSelectedTable)
					)
					.addGroup(mainLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
							.addComponent(joinBtn)
							.addComponent(cancelBtn)
					)
				);
		dialog.setModalityType(ModalityType.APPLICATION_MODAL);
		dialog.pack();
		dialog.setResizable(false);
		dialog.setLocationRelativeTo(null);
		dialog.setVisible(true);
	}

	public static JTable deleteRowFromTable(DBManager dbManager, JTable jtable, JTree tree) {
		assert(jtable != null);
		
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
		Object o = node.getUserObject();
		
		if(o instanceof Table) {
			Table table = (Table)o;
			DefaultMutableTreeNode parent = (DefaultMutableTreeNode)node.getParent();
			DataBase dataBase = (DataBase)(parent.getUserObject());
		
			int rowIndex = jtable.getSelectedRow();
			System.out.println(rowIndex);
			dbManager.deleteRow(dataBase.getName(), table.getTableName(), rowIndex);	
			return convertTableToJTable(table);
		}
		return jtable;
	}

	public static JTable addRowToTable(DBManager dbManager, JTable jtable, JTree tree) {
		assert(jtable != null);
		
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
		Object o = node.getUserObject();
		
		if(o instanceof Table) {
			Table table = (Table)o;
			DefaultMutableTreeNode parent = (DefaultMutableTreeNode)node.getParent();
			DataBase dataBase = (DataBase)(parent.getUserObject());
		
			int rowIndex = jtable.getSelectedRow();
			dbManager.createRow(dataBase.getName(), table.getTableName());	
			return convertTableToJTable(table);
		}
		return jtable;
	}

	public static JTable setNewCellToTable(JTree tree, JTable jtable, int x, int y, String newValue) {
		
		assert(jtable != null);
		
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
		Object o = node.getUserObject();
		
		if(o instanceof Table) {
			Table table = (Table)o;
			table.setCellValue(x, y, newValue);
			//return convertTableToJTable(table);
		}
		
		return jtable;
	}
}
