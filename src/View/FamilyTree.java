package View;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.sql.*;

public class FamilyTree extends JFrame {

    private JTree tree;
    private DefaultMutableTreeNode rootNode;

    public FamilyTree() {
        super("Family Tree");
        rootNode = new DefaultMutableTreeNode("Family");
        tree = new JTree(rootNode);
        JScrollPane treeView = new JScrollPane(tree);
        getContentPane().add(treeView);
        setSize(500, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
        setCustomIcon();
    }
    public Connection getConnection() {
        Connection con = null;

        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/familytree", "root", "");
            return con;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    // Create a custom cell renderer
    private void setCustomIcon() {
        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        ImageIcon parentIcon = new ImageIcon("C:\\Users\\FPTShop\\Downloads\\image\\2person.png");
        ImageIcon childIcon = new ImageIcon("C:\\Users\\FPTShop\\Downloads\\image\\1person.png");
        renderer.setClosedIcon(parentIcon);
        renderer.setOpenIcon(parentIcon);
        renderer.setLeafIcon(childIcon);
        tree.setCellRenderer(renderer);
    }


    public void buildFamilyTree() {
        try {
            Connection con = getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM relationship");
            while (rs.next()) {
                int re_id = rs.getInt("re_id");
                int father_id = rs.getInt("father_id");
                int mother_id = rs.getInt("mother_id");
                int child_id = rs.getInt("child_id");

                DefaultMutableTreeNode parentNode = null;
                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode();

                if (father_id != 0) {
                    parentNode = findNode(rootNode, father_id);
                    if (parentNode == null) {
                        parentNode = new DefaultMutableTreeNode(getPersonName(father_id));
                        rootNode.add(parentNode);
                    }
                    if (mother_id != 0) {
                        DefaultMutableTreeNode duplicateNode = findNode(parentNode, mother_id);
                        if (duplicateNode != null) {
                            parentNode = duplicateNode;
                        } else {
                            DefaultMutableTreeNode motherNode = new DefaultMutableTreeNode(getPersonName(mother_id));
                            parentNode.add(motherNode);
                            parentNode = motherNode;
                        }
                    }
                } else if (mother_id != 0) {
                    parentNode = findNode(rootNode, mother_id);
                    if (parentNode == null) {
                        parentNode = new DefaultMutableTreeNode(getPersonName(mother_id));
                        rootNode.add(parentNode);
                    }
                }

                if (child_id != 0) {
                    childNode = new DefaultMutableTreeNode(getPersonName(child_id));
                    if (parentNode != null) {
                        parentNode.add(childNode);
                    } else {
                        rootNode.add(childNode);
                    }
                }
            }
            rs.close();
            stmt.close();
            con.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private DefaultMutableTreeNode findNode(DefaultMutableTreeNode node, int id) {
        if (node.getUserObject().equals(getPersonName(id))) {
            return node;
        }
        for (int i=0; i<node.getChildCount(); i++) {
            DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) node.getChildAt(i);
            DefaultMutableTreeNode foundNode = findNode(childNode, id);
            if (foundNode != null) {
                return foundNode;
            }
        }
        return null;
    }

    private String getPersonName(int id) {
        String name = "";
        try {
            Connection con = getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT name FROM person WHERE person_id = " + id);
            if (rs.next()) {
                name = rs.getString("name");
            }
            rs.close();
            stmt.close();
            con.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return name;
    }

    public static void main(String[] args) {
        FamilyTree familyTree = new FamilyTree();
        familyTree.buildFamilyTree();
    }
}