/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package View;

import Model.Person;
import Controller.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

import java.text.ParseException;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.NumberFormatter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import  javax.swing.ImageIcon;

/**
 *
 * @author FPTShop
 */
public class Home extends javax.swing.JFrame {

    /**
     * Creates new form Home
     */
    /*
        width 210
        height 523
     */
    int width = 175;
    int height = 645;

    File SelectedFile = null;

    private ImageIcon format = null;
    String ImgPath = null;
    
    DefaultTreeModel model;

    public Home() {
        initComponents();
        this.setTitle("Family Tree");
        this.setLocationRelativeTo(null);
        show_people();
        updateCbxFather();
        updateCbxMother();
        updateCbxChild();
    }

    public ArrayList<Person> people(){
        ArrayList<Person> people = new ArrayList<>();
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/familytree","root","");
            String sql = "SELECT * FROM `person`";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            Person person;
            while(rs.next()){
                person = new Person(rs.getInt("person_id"), rs.getString("name"),rs.getInt("age"), rs.getString("address"), rs.getString("gender"),rs.getString("isAlive"), rs.getString("note"), rs.getBytes("image"));
                people.add(person);
            }


        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return people;
    }

    public Connection getConnection() {
        Connection con = null;

        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/familytree", "root", "");
            return con;
        } catch (SQLException ex) {
            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public boolean checkIDInputs(){
        if(txtPersonID == null || txtPersonName == null || txtAddress == null || txtAge == null){
            return false;
        }
        else{
            try{
                Integer.parseInt(txtPersonID.getText());
                return true;
            }catch (Exception ex){
                return false;
            }
        }
    }

    public boolean checkAgeInputs(){
        if(txtPersonID == null || txtPersonName == null || txtAddress == null || txtAge == null){
            return false;
        }
        else{
            try{
                Integer.parseInt(txtAge.getText());
                return true;
            }catch (Exception ex){
                return false;
            }
        }
    }

    public boolean checkReIdInputs(){
        if(txtReId == null  ){
            return false;
        }
        else{
            try{
                Integer.parseInt(txtReId.getText());
                return true;
            }catch (Exception ex){
                return false;
            }
        }
    }

    public ImageIcon ResizeImage(String imagePath, byte[] pic)
    {
        ImageIcon myImage = null;

        if(imagePath != null)
        {
            myImage = new ImageIcon(imagePath);
        }else{
            myImage = new ImageIcon(pic);
        }

        Image img = myImage.getImage();
        Image img2 = img.getScaledInstance(lbImage.getWidth(), lbImage.getHeight(), Image.SCALE_SMOOTH);
        ImageIcon image = new ImageIcon(img2);
        return image;

    }

    public void show_people(){
        ArrayList<Person> list = people();
        DefaultTableModel model = (DefaultTableModel) tbPerson.getModel();
        model.setRowCount(0);
        Object[] row = new Object[7];
        for (int i = 0; i<list.size(); i++){

            row[0] = list.get(i).getId();
            row[1] = list.get(i).getName();
            row[2] = list.get(i).getAge();
            row[3] = list.get(i).getAddress();
            row[4] = list.get(i).getGender();
            row[5] = list.get(i).isAlive();
            row[6] = list.get(i).getNote();
            model.addRow(row);
        }


    }

    public void ShowItem(int index){
        txtPersonID.setText(String.valueOf(people().get(index).getId()));
        txtPersonName.setText(people().get(index).getName());
        txtAge.setText(String.valueOf(people().get(index).getAge()));
        txtAddress.setText(people().get(index).getAddress());
        txtaNote.setText(people().get(index).getNote());

        if(people().get(index).getGender().equals("Male")){
            rbtMale.setSelected(true);

        } else{
            rbtFemale.setSelected(true);

        }

        if(people().get(index).isAlive().equals("Yes")){
            cbxAlive.setSelected(true);
        }else {
            cbxAlive.setSelected(false);
        }
        lbImage.setIcon(ResizeImage(null,people().get(index).getImage()));

    }

    public void Print() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
        try {
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            String sql = "SELECT `re_id`,`father_id`, `mother_id`, `child_id` FROM `relationship`";
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                int fatherId = rs.getInt("father_id");
                int motherId = rs.getInt("mother_id");
                int childId = rs.getInt("child_id");
                int reId = rs.getInt("re_id");

                DefaultMutableTreeNode fatherNode = new DefaultMutableTreeNode("Father #" + fatherId + " - Family Tree #" + reId);
                DefaultMutableTreeNode motherNode = new DefaultMutableTreeNode("Mother #" + motherId + " - Family Tree #" + reId);
                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode("Child #" + childId + " - Family Tree #" + reId);

                fatherNode.add(childNode);
                motherNode.add(childNode);
                root.add(fatherNode);
                root.add(motherNode);
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        JTree tree = new JTree(new DefaultMutableTreeNode(root));
        pnTree.add(tree);

    }

    public void updateCbxFather(){
        try{
            Connection conn = getConnection();
            String sql = "SELECT * FROM `person` ";
            PreparedStatement psmt = conn.prepareStatement(sql);
            ResultSet rs = psmt.executeQuery();
            while(rs.next()){
                cbxFatherId.addItem(String.valueOf(rs.getInt("person_id")));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void updateCbxMother(){
        try{
            Connection conn = getConnection();
            String sql = "SELECT * FROM `person` ";
            PreparedStatement psmt = conn.prepareStatement(sql);
            ResultSet rs = psmt.executeQuery();
            while(rs.next()){
                cbxMotherId.addItem(String.valueOf(rs.getInt("person_id")));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void updateCbxChild(){
        try{
            Connection conn = getConnection();
            String sql = "SELECT * FROM `person` ";
            PreparedStatement psmt = conn.prepareStatement(sql);
            ResultSet rs = psmt.executeQuery();
            while(rs.next()){
                cbxChildId.addItem(String.valueOf(rs.getInt("person_id")));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    // phuong thuc mo menu
    public void openMenuBar() {
        // tao luồng chạy song song với luồng chính
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < width; i++) {
                    pnMenu.setSize(i, height);
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

        }).start();
    }

    public void closeMenuBar() {
        // tao luồng chạy song song với luồng chính
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = width; i > 0; i--) {
                    pnMenu.setSize(i, height);
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

        }).start();
    }

    public void changePanel(JPanel pn) {
        pnParent.removeAll();
        pnParent.add(pn);
        pnParent.repaint();
        pnParent.revalidate();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        pnMenu = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        lbHome = new javax.swing.JLabel();
        lbAddPerson = new javax.swing.JLabel();
        lbTree = new javax.swing.JLabel();
        lbHelp = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        lbAbout = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        lbRelationship = new javax.swing.JLabel();
        pnParent = new javax.swing.JPanel();
        pnHome = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        jLabel11 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        pnAbout = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jSeparator6 = new javax.swing.JSeparator();
        pnAddPerson = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jSeparator4 = new javax.swing.JSeparator();
        jLabel5 = new javax.swing.JLabel();
        txtPersonName = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtAddress = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        rbtMale = new javax.swing.JRadioButton();
        rbtFemale = new javax.swing.JRadioButton();
        jLabel8 = new javax.swing.JLabel();
        cbxAlive = new javax.swing.JCheckBox();
        jLabel17 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtaNote = new javax.swing.JTextArea();
        jSeparator5 = new javax.swing.JSeparator();
        btnAdd = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnReset = new javax.swing.JButton();
        jLabel18 = new javax.swing.JLabel();
        txtAge = new javax.swing.JTextField();
        btnFind = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        tbPerson = new javax.swing.JTable();
        jLabel32 = new javax.swing.JLabel();
        txtPersonID = new javax.swing.JTextField();
        btnBrowse = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        lbImage = new javax.swing.JLabel();
        pnHelp = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        jSeparator7 = new javax.swing.JSeparator();
        jLabel22 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        lbCheck = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        pnTree = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        jSeparator8 = new javax.swing.JSeparator();
        pnPrint = new javax.swing.JPanel();
        btnPrint = new javax.swing.JButton();
        pnRelationship = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        jSeparator9 = new javax.swing.JSeparator();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        btnCreate = new javax.swing.JButton();
        jSeparator10 = new javax.swing.JSeparator();
        jLabel31 = new javax.swing.JLabel();
        txtReId = new javax.swing.JTextField();
        cbxFatherId = new javax.swing.JComboBox<>();
        cbxMotherId = new javax.swing.JComboBox<>();
        cbxChildId = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setForeground(new java.awt.Color(255, 255, 255));

        pnMenu.setBackground(new java.awt.Color(255, 255, 255));
        pnMenu.setForeground(new java.awt.Color(255, 255, 255));
        pnMenu.setPreferredSize(new java.awt.Dimension(210, 645));

        jLabel2.setFont(new java.awt.Font("Showcard Gothic", 0, 14)); // NOI18N
        jLabel2.setText("Family Tree");

        lbHome.setIcon(new javax.swing.ImageIcon("C:\\Users\\FPTShop\\Downloads\\image\\homeicon.png")); // NOI18N
        lbHome.setText("Home");
        lbHome.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbHomeMouseClicked(evt);
            }
        });

        lbAddPerson.setIcon(new javax.swing.ImageIcon("C:\\Users\\FPTShop\\Downloads\\image\\personIcon.png")); // NOI18N
        lbAddPerson.setText("Person");
        lbAddPerson.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbAddPersonMouseClicked(evt);
            }
        });

        lbTree.setIcon(new javax.swing.ImageIcon("C:\\Users\\FPTShop\\Downloads\\image\\treeIcon.png")); // NOI18N
        lbTree.setText("Tree");
        lbTree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbTreeMouseClicked(evt);
            }
        });

        lbHelp.setIcon(new javax.swing.ImageIcon("C:\\Users\\FPTShop\\Downloads\\image\\help.png")); // NOI18N
        lbHelp.setText("Help");
        lbHelp.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbHelpMouseClicked(evt);
            }
        });

        lbAbout.setIcon(new javax.swing.ImageIcon("C:\\Users\\FPTShop\\Downloads\\image\\aboutIcon.png")); // NOI18N
        lbAbout.setText("About");
        lbAbout.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbAboutMouseClicked(evt);
            }
        });

        jLabel13.setIcon(new javax.swing.ImageIcon("C:\\Users\\FPTShop\\Downloads\\image\\familytree.png")); // NOI18N
        jLabel13.setText("jLabel13");

        jLabel14.setIcon(new javax.swing.ImageIcon("C:\\Users\\FPTShop\\Downloads\\image\\exit.png")); // NOI18N
        jLabel14.setText("jLabel14");
        jLabel14.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel14MouseClicked(evt);
            }
        });

        lbRelationship.setIcon(new javax.swing.ImageIcon("C:\\Users\\FPTShop\\Downloads\\image\\relationship.png")); // NOI18N
        lbRelationship.setText("Relationship");
        lbRelationship.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbRelationshipMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout pnMenuLayout = new javax.swing.GroupLayout(pnMenu);
        pnMenu.setLayout(pnMenuLayout);
        pnMenuLayout.setHorizontalGroup(
            pnMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1)
            .addComponent(jSeparator2)
            .addGroup(pnMenuLayout.createSequentialGroup()
                .addGroup(pnMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnMenuLayout.createSequentialGroup()
                        .addGap(151, 151, 151)
                        .addComponent(jLabel1))
                    .addGroup(pnMenuLayout.createSequentialGroup()
                        .addGap(41, 41, 41)
                        .addGroup(pnMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnMenuLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(pnMenuLayout.createSequentialGroup()
                .addGroup(pnMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnMenuLayout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addGroup(pnMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbHelp, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbAbout, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(pnMenuLayout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addGroup(pnMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(lbAddPerson, javax.swing.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE)
                                .addComponent(lbHome, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lbTree, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(lbRelationship))))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        pnMenuLayout.setVerticalGroup(
            pnMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnMenuLayout.createSequentialGroup()
                .addComponent(jLabel14)
                .addGap(35, 35, 35)
                .addComponent(jLabel13)
                .addGap(8, 8, 8)
                .addComponent(jLabel1)
                .addGap(1, 1, 1)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lbHome)
                .addGap(18, 18, 18)
                .addComponent(lbAddPerson)
                .addGap(18, 18, 18)
                .addComponent(lbTree)
                .addGap(18, 18, 18)
                .addComponent(lbRelationship)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lbHelp)
                .addGap(18, 18, 18)
                .addComponent(lbAbout)
                .addGap(91, 91, 91))
        );

        pnParent.setLayout(new java.awt.CardLayout());

        pnHome.setBackground(new java.awt.Color(255, 255, 255));

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel12.setText("Home");

        jLabel11.setIcon(new javax.swing.ImageIcon("C:\\Users\\FPTShop\\Downloads\\image\\familytree.png")); // NOI18N
        jLabel11.setText("jLabel11");

        jLabel20.setFont(new java.awt.Font("Showcard Gothic", 0, 14)); // NOI18N
        jLabel20.setIcon(new javax.swing.ImageIcon("C:\\Users\\FPTShop\\Downloads\\image\\homeicons.png")); // NOI18N

        jLabel25.setFont(new java.awt.Font("Showcard Gothic", 0, 14)); // NOI18N
        jLabel25.setText("Grow your family tree");

        javax.swing.GroupLayout pnHomeLayout = new javax.swing.GroupLayout(pnHome);
        pnHome.setLayout(pnHomeLayout);
        pnHomeLayout.setHorizontalGroup(
            pnHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnHomeLayout.createSequentialGroup()
                .addGroup(pnHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnHomeLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jSeparator3))
                    .addGroup(pnHomeLayout.createSequentialGroup()
                        .addGroup(pnHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnHomeLayout.createSequentialGroup()
                                .addGap(372, 372, 372)
                                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnHomeLayout.createSequentialGroup()
                                .addGap(303, 303, 303)
                                .addComponent(jLabel19))
                            .addGroup(pnHomeLayout.createSequentialGroup()
                                .addGap(395, 395, 395)
                                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnHomeLayout.createSequentialGroup()
                                .addGap(314, 314, 314)
                                .addComponent(jLabel20)))
                        .addGap(0, 342, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(pnHomeLayout.createSequentialGroup()
                .addGap(338, 338, 338)
                .addComponent(jLabel25)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnHomeLayout.setVerticalGroup(
            pnHomeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnHomeLayout.createSequentialGroup()
                .addGap(59, 59, 59)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel19)
                .addGap(18, 18, 18)
                .addComponent(jLabel20)
                .addGap(18, 18, 18)
                .addComponent(jLabel25)
                .addContainerGap(155, Short.MAX_VALUE))
        );

        pnParent.add(pnHome, "card4");

        pnAbout.setBackground(new java.awt.Color(255, 255, 255));

        jLabel15.setIcon(new javax.swing.ImageIcon("C:\\Users\\FPTShop\\Downloads\\image\\familytree.png")); // NOI18N
        jLabel15.setText("jLabel15");

        jLabel16.setText("Cây sơ đồ gia phả. <33 ");

        jLabel3.setText("Version 1.0.0 ");
        jLabel3.setToolTipText("");
        jLabel3.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout pnAboutLayout = new javax.swing.GroupLayout(pnAbout);
        pnAbout.setLayout(pnAboutLayout);
        pnAboutLayout.setHorizontalGroup(
            pnAboutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnAboutLayout.createSequentialGroup()
                .addGroup(pnAboutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnAboutLayout.createSequentialGroup()
                        .addGroup(pnAboutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnAboutLayout.createSequentialGroup()
                                .addGap(121, 121, 121)
                                .addGroup(pnAboutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 615, Short.MAX_VALUE)))
                            .addGroup(pnAboutLayout.createSequentialGroup()
                                .addGap(373, 373, 373)
                                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 145, Short.MAX_VALUE))
                    .addGroup(pnAboutLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jSeparator6)))
                .addContainerGap())
        );
        pnAboutLayout.setVerticalGroup(
            pnAboutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnAboutLayout.createSequentialGroup()
                .addGap(58, 58, 58)
                .addComponent(jLabel15)
                .addGap(18, 18, 18)
                .addComponent(jSeparator6, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36)
                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(138, Short.MAX_VALUE))
        );

        pnParent.add(pnAbout, "card3");

        pnAddPerson.setBackground(new java.awt.Color(255, 255, 255));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel4.setText("Manage Person");

        jLabel5.setText("Full name:");

        jLabel6.setText("Address:");

        jLabel7.setText("Gender:");

        rbtMale.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroup1.add(rbtMale);
        rbtMale.setSelected(true);
        rbtMale.setText("Male");
        rbtMale.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtMaleActionPerformed(evt);
            }
        });

        rbtFemale.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroup1.add(rbtFemale);
        rbtFemale.setText("Female");

        jLabel8.setText("Alive:");

        cbxAlive.setBackground(new java.awt.Color(255, 255, 255));
        cbxAlive.setSelected(true);
        cbxAlive.setText("Yes");
        cbxAlive.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxAliveActionPerformed(evt);
            }
        });

        jLabel17.setText("Note: ");

        txtaNote.setColumns(20);
        txtaNote.setRows(5);
        jScrollPane2.setViewportView(txtaNote);

        btnAdd.setBackground(new java.awt.Color(255, 255, 255));
        btnAdd.setText("Add");
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        btnUpdate.setBackground(new java.awt.Color(255, 255, 255));
        btnUpdate.setText("Update");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        btnReset.setBackground(new java.awt.Color(255, 255, 255));
        btnReset.setText("Reset");
        btnReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetActionPerformed(evt);
            }
        });

        jLabel18.setText("Age:");

        btnFind.setBackground(new java.awt.Color(255, 255, 255));
        btnFind.setText("Find");
        btnFind.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFindActionPerformed(evt);
            }
        });

        tbPerson.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Full Name", "Age", "Address", "Gender", "Alive", "Note"
            }
        ));
        tbPerson.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbPersonMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(tbPerson);

        jLabel32.setText("Person id:");

        btnBrowse.setText("Browse");
        btnBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseActionPerformed(evt);
            }
        });

        jLabel9.setText("Avatar:");

        lbImage.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        lbImage.setPreferredSize(new java.awt.Dimension(227, 166));

        javax.swing.GroupLayout pnAddPersonLayout = new javax.swing.GroupLayout(pnAddPerson);
        pnAddPerson.setLayout(pnAddPersonLayout);
        pnAddPersonLayout.setHorizontalGroup(
            pnAddPersonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnAddPersonLayout.createSequentialGroup()
                .addGroup(pnAddPersonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnAddPersonLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(pnAddPersonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator4)
                            .addComponent(jSeparator5)))
                    .addGroup(pnAddPersonLayout.createSequentialGroup()
                        .addGroup(pnAddPersonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnAddPersonLayout.createSequentialGroup()
                                .addGap(258, 258, 258)
                                .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnUpdate)
                                .addGap(18, 18, 18)
                                .addComponent(btnFind, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnReset, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnAddPersonLayout.createSequentialGroup()
                                .addGap(355, 355, 355)
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnAddPersonLayout.createSequentialGroup()
                                .addGap(103, 103, 103)
                                .addGroup(pnAddPersonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnAddPersonLayout.createSequentialGroup()
                                        .addGroup(pnAddPersonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel32)
                                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(pnAddPersonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(txtAddress)
                                            .addGroup(pnAddPersonLayout.createSequentialGroup()
                                                .addComponent(txtPersonName, javax.swing.GroupLayout.PREFERRED_SIZE, 379, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(txtAge, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addComponent(txtPersonID)
                                            .addGroup(pnAddPersonLayout.createSequentialGroup()
                                                .addGroup(pnAddPersonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addGroup(pnAddPersonLayout.createSequentialGroup()
                                                        .addGroup(pnAddPersonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                            .addGroup(pnAddPersonLayout.createSequentialGroup()
                                                                .addGap(2, 2, 2)
                                                                .addComponent(rbtMale, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(rbtFemale, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                            .addComponent(cbxAlive))
                                                        .addGap(0, 0, Short.MAX_VALUE))
                                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                                                .addGap(18, 18, 18)
                                                .addGroup(pnAddPersonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(btnBrowse))
                                                .addGap(44, 44, 44)
                                                .addComponent(lbImage, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jScrollPane4))))
                        .addGap(0, 108, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnAddPersonLayout.setVerticalGroup(
            pnAddPersonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnAddPersonLayout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnAddPersonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnAddPersonLayout.createSequentialGroup()
                        .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(pnAddPersonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel32)
                            .addComponent(txtPersonID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(16, 16, 16)
                        .addGroup(pnAddPersonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(txtPersonName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtAge, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel18))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(pnAddPersonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(txtAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnAddPersonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(rbtMale)
                            .addComponent(rbtFemale)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(pnAddPersonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addGroup(pnAddPersonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(cbxAlive)
                                .addComponent(btnBrowse)))
                        .addGap(11, 11, 11)
                        .addGroup(pnAddPersonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE)
                            .addGroup(pnAddPersonLayout.createSequentialGroup()
                                .addComponent(jLabel17)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addComponent(lbImage, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(39, 39, 39)
                .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnAddPersonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAdd)
                    .addComponent(btnReset)
                    .addComponent(btnUpdate)
                    .addComponent(btnFind))
                .addContainerGap())
        );

        pnParent.add(pnAddPerson, "card5");

        pnHelp.setBackground(new java.awt.Color(255, 255, 255));

        jLabel21.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel21.setText("Help");

        jLabel22.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel22.setText("May i help you ?");

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Our information"));

        lbCheck.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lbCheck.setText("Check for about me");
        lbCheck.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbCheckMouseClicked(evt);
            }
        });

        jLabel23.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel23.setText("Online help phone number : 0964819465 <33");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbCheck)
                    .addComponent(jLabel23))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbCheck)
                .addGap(26, 26, 26)
                .addComponent(jLabel23)
                .addContainerGap(26, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnHelpLayout = new javax.swing.GroupLayout(pnHelp);
        pnHelp.setLayout(pnHelpLayout);
        pnHelpLayout.setHorizontalGroup(
            pnHelpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnHelpLayout.createSequentialGroup()
                .addGroup(pnHelpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnHelpLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jSeparator7))
                    .addGroup(pnHelpLayout.createSequentialGroup()
                        .addGroup(pnHelpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnHelpLayout.createSequentialGroup()
                                .addGap(270, 270, 270)
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnHelpLayout.createSequentialGroup()
                                .addGap(391, 391, 391)
                                .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 276, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(pnHelpLayout.createSequentialGroup()
                .addGap(358, 358, 358)
                .addComponent(jLabel22)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnHelpLayout.setVerticalGroup(
            pnHelpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnHelpLayout.createSequentialGroup()
                .addGap(52, 52, 52)
                .addComponent(jLabel21)
                .addGap(18, 18, 18)
                .addComponent(jSeparator7, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel22)
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(325, Short.MAX_VALUE))
        );

        pnParent.add(pnHelp, "card5");

        pnTree.setBackground(new java.awt.Color(255, 255, 255));

        jLabel24.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel24.setText("Your family tree");

        pnPrint.setBackground(new java.awt.Color(255, 255, 255));
        pnPrint.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout pnPrintLayout = new javax.swing.GroupLayout(pnPrint);
        pnPrint.setLayout(pnPrintLayout);
        pnPrintLayout.setHorizontalGroup(
            pnPrintLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        pnPrintLayout.setVerticalGroup(
            pnPrintLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 465, Short.MAX_VALUE)
        );

        btnPrint.setText("Print");
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrintActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnTreeLayout = new javax.swing.GroupLayout(pnTree);
        pnTree.setLayout(pnTreeLayout);
        pnTreeLayout.setHorizontalGroup(
            pnTreeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnTreeLayout.createSequentialGroup()
                .addGroup(pnTreeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnTreeLayout.createSequentialGroup()
                        .addGap(374, 374, 374)
                        .addComponent(jLabel24)
                        .addGap(0, 369, Short.MAX_VALUE))
                    .addGroup(pnTreeLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(pnTreeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pnPrint, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jSeparator8))))
                .addContainerGap())
            .addGroup(pnTreeLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnPrint)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnTreeLayout.setVerticalGroup(
            pnTreeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnTreeLayout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addComponent(jLabel24)
                .addGap(18, 18, 18)
                .addComponent(jSeparator8, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                .addComponent(btnPrint)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnPrint, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pnParent.add(pnTree, "card6");

        pnRelationship.setBackground(new java.awt.Color(255, 255, 255));

        jLabel27.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel27.setText("Add Relationship");

        jLabel28.setText("Father id:");

        jLabel29.setText("Mother id:");

        jLabel30.setText("Child id:");

        btnCreate.setBackground(new java.awt.Color(255, 255, 255));
        btnCreate.setText("Create");
        btnCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateActionPerformed(evt);
            }
        });

        jLabel31.setText("Relationship id:");

        javax.swing.GroupLayout pnRelationshipLayout = new javax.swing.GroupLayout(pnRelationship);
        pnRelationship.setLayout(pnRelationshipLayout);
        pnRelationshipLayout.setHorizontalGroup(
            pnRelationshipLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnRelationshipLayout.createSequentialGroup()
                .addGroup(pnRelationshipLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnRelationshipLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(pnRelationshipLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator10)
                            .addComponent(jSeparator9)))
                    .addGroup(pnRelationshipLayout.createSequentialGroup()
                        .addGroup(pnRelationshipLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnRelationshipLayout.createSequentialGroup()
                                .addGap(359, 359, 359)
                                .addComponent(jLabel27))
                            .addGroup(pnRelationshipLayout.createSequentialGroup()
                                .addGap(400, 400, 400)
                                .addComponent(btnCreate)))
                        .addGap(0, 374, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnRelationshipLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(pnRelationshipLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel28, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel31, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel30))
                        .addGap(18, 18, 18)
                        .addGroup(pnRelationshipLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtReId, javax.swing.GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE)
                            .addComponent(cbxFatherId, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cbxMotherId, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cbxChildId, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(222, 222, 222)))
                .addContainerGap())
        );
        pnRelationshipLayout.setVerticalGroup(
            pnRelationshipLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnRelationshipLayout.createSequentialGroup()
                .addGroup(pnRelationshipLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnRelationshipLayout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addGroup(pnRelationshipLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(pnRelationshipLayout.createSequentialGroup()
                                .addComponent(jLabel27)
                                .addGap(23, 23, 23)
                                .addComponent(jSeparator9, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(177, 177, 177))
                            .addGroup(pnRelationshipLayout.createSequentialGroup()
                                .addGroup(pnRelationshipLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtReId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel31))
                                .addGap(58, 58, 58))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnRelationshipLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(pnRelationshipLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel28)
                            .addComponent(cbxFatherId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)))
                .addGroup(pnRelationshipLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel29)
                    .addComponent(cbxMotherId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnRelationshipLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbxChildId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel30))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 223, Short.MAX_VALUE)
                .addComponent(jSeparator10, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCreate)
                .addGap(25, 25, 25))
        );

        pnParent.add(pnRelationship, "card7");

        jLabel10.setIcon(new javax.swing.ImageIcon("C:\\Users\\FPTShop\\Downloads\\image\\menu-icon.jpg")); // NOI18N
        jLabel10.setText("a");
        jLabel10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel10MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(pnMenu, javax.swing.GroupLayout.PREFERRED_SIZE, 0, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(pnParent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addComponent(jLabel10)
                .addGap(18, 18, 18)
                .addComponent(pnParent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(pnMenu, javax.swing.GroupLayout.DEFAULT_SIZE, 711, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel10MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel10MouseClicked
        openMenuBar();
    }//GEN-LAST:event_jLabel10MouseClicked

    private void jLabel14MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel14MouseClicked
        closeMenuBar();
    }//GEN-LAST:event_jLabel14MouseClicked

    private void lbAboutMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbAboutMouseClicked
        // TODO add your handling code here:
        changePanel(pnAbout);
    }//GEN-LAST:event_lbAboutMouseClicked

    private void lbHomeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbHomeMouseClicked
        // TODO add your handling code here:
        changePanel(pnHome);
    }//GEN-LAST:event_lbHomeMouseClicked

    private void lbAddPersonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbAddPersonMouseClicked
        // TODO add your handling code here:
        changePanel(pnAddPerson);
    }//GEN-LAST:event_lbAddPersonMouseClicked

    private void cbxAliveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbxAliveActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbxAliveActionPerformed


    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {                                       


       // File f = new File(path);
        if(checkIDInputs() && checkAgeInputs() && ImgPath != null){
            try {

                Class.forName("com.mysql.jdbc.Driver");
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/familytree", "root", "");

                String sql = "INSERT INTO `person`(`person_id`, `name`, `age`, `address`, `gender`, `isAlive`, `note`, `image`) VALUES (?,?,?,?,?,?,?,?)";
                PreparedStatement psmt = conn.prepareStatement(sql);
                psmt.setInt(1, Integer.parseInt(txtPersonID.getText()));
                psmt.setString(2, txtPersonName.getText());
                psmt.setInt(3, Integer.parseInt(txtAge.getText()));
                psmt.setString(4, txtAddress.getText());
                String gender = "";
                if (rbtMale.isSelected()) {
                    gender = rbtMale.getText();
                }
                if (rbtFemale.isSelected()) {
                    gender = rbtFemale.getText();
                }
                psmt.setString(5, gender);
                String alive;
                if (cbxAlive.isSelected()) {
                    alive = "Yes";
                } else {
                    alive = "No";
                }
                psmt.setString(6, alive);

                psmt.setString(7, txtaNote.getText());
                InputStream is = new FileInputStream(new File(ImgPath));
                psmt.setBlob(8, is);
                psmt.executeUpdate();
                show_people();
                JOptionPane.showMessageDialog(null, "Successfully added");

            } catch (FileNotFoundException ex){
                Logger.getLogger(Home.class.getName()).log(Level.SEVERE,null,ex);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(null,"ID or Age entered incorrectly, or Empty");
        }


    }



    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        // TODO add your handling code here:
        if(checkIDInputs() && checkAgeInputs() && txtPersonID.getText() != null){
            String sql = null;
            PreparedStatement psmt = null;
            Connection conn = getConnection();
            if(ImgPath == null){
                try {

                    sql = "UPDATE `person` SET`name`=?,`age`=?,`address`=?,`gender`=?,`isAlive`=?,`note`=? WHERE person_id = ?";
                    psmt = conn.prepareStatement(sql);

                    psmt.setString(1,txtPersonName.getText());
                    psmt.setInt(2, Integer.parseInt(txtAge.getText()));
                    psmt.setString(3,txtAddress.getText());
                    String gender = "";
                    if (rbtMale.isSelected()) {
                        gender = rbtMale.getText();
                    }
                    if (rbtFemale.isSelected()) {
                        gender = rbtFemale.getText();
                    }
                    psmt.setString(4, gender);
                    String alive = "";
                    if (cbxAlive.isSelected()) {
                        alive = cbxAlive.getText();
                    } else {
                        alive = "No";
                    }
                    psmt.setString(5,alive);
                    psmt.setString(6,txtaNote.getText());
                    psmt.setInt(7, Integer.parseInt(txtPersonID.getText()));

                    psmt.executeUpdate();
                    show_people();
                    JOptionPane.showMessageDialog(null, "Update successful");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else{
                try{
                    InputStream img = new FileInputStream(new File(ImgPath));

                    sql = "UPDATE `person` SET`name`=?,`age`=?,`address`=?,`gender`=?,`isAlive`=?,`note`=?,`image`=? WHERE person_id = ?";
                    psmt = conn.prepareStatement(sql);

                    psmt.setString(1,txtPersonName.getText());
                    psmt.setInt(2, Integer.parseInt(txtAge.getText()));
                    psmt.setString(3,txtAddress.getText());
                    String gender = "";
                    if (rbtMale.isSelected()) {
                        gender = rbtMale.getText();
                    }
                    if (rbtFemale.isSelected()) {
                        gender = rbtFemale.getText();
                    }
                    psmt.setString(4, gender);
                    String alive = "";
                    if (cbxAlive.isSelected()) {
                        alive = cbxAlive.getText();
                    } else {
                        alive = "No";
                    }
                    psmt.setString(5,alive);
                    psmt.setString(6,txtaNote.getText());
                    psmt.setBlob(7,img);
                    psmt.setInt(8, Integer.parseInt(txtPersonID.getText()));

                    psmt.executeUpdate();
                    show_people();
                    JOptionPane.showMessageDialog(null, "Update successful");


                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }else{
            JOptionPane.showMessageDialog(null,"ID or Age entered incorrectly, or Empty");
        }



    }//GEN-LAST:event_btnUpdateActionPerformed
    private void resetForm() {
        txtPersonID.setText("");
        txtPersonName.setText("");
        txtAge.setText("");
        txtAddress.setText("");
        txtaNote.setText("");
        rbtMale.setSelected(true);
        cbxAlive.setSelected(true);
        lbImage.setIcon(null);
    }

    private void btnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetActionPerformed
        // TODO add your handling code here:
        resetForm();
    }//GEN-LAST:event_btnResetActionPerformed


    private void btnFindActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFindActionPerformed
        // TODO add your handling code here:
        if(checkIDInputs() == true){
            try {
                Connection conn = getConnection();

                String sql = "SELECT * FROM `person` WHERE person_id = ?";
                PreparedStatement psmt = conn.prepareStatement(sql);

                psmt.setInt(1, Integer.parseInt(txtPersonID.getText()));
                ResultSet rs = psmt.executeQuery();
                if(rs.next()){
                    txtPersonName.setText(rs.getString("name"));
                    txtAge.setText(String.valueOf(rs.getInt("age")));
                    txtAddress.setText(rs.getString("address"));
                    if(rs.getString("gender").equals("Male")){
                        rbtMale.setSelected(true);
                        rbtFemale.setSelected(false);
                    }
                    if(rs.getString("gender").equals("Female")){
                        rbtMale.setSelected(false);
                        rbtFemale.setSelected(true);
                    }

                    if(rs.getString("isAlive").equals("Yes")){
                        cbxAlive.setSelected(true);
                    }  else {
                        cbxAlive.setSelected(false);
                    }

                    txtaNote.setText(rs.getString("note"));
                    lbImage.setIcon(ResizeImage(null, rs.getBytes("image")));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(null,"Id entered is incorrect");
        }



    }//GEN-LAST:event_btnFindActionPerformed

    private void rbtMaleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtMaleActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rbtMaleActionPerformed

    private void tbPersonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbPersonMouseClicked
        // TODO add your handling code here:
            int index = tbPerson.getSelectedRow();
            ShowItem(index);

    }//GEN-LAST:event_tbPersonMouseClicked

    private void btnCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateActionPerformed
        // TODO add your handling code here:
        if(checkReIdInputs()  == true){
            try {
                Connection conn = getConnection();
                String sql = "INSERT INTO `relationship`(`re_id`, `father_id`, `mother_id`, `child_id`) VALUES (?,?,?,?)";
                PreparedStatement psmt = conn.prepareStatement(sql);
                psmt.setInt(1, Integer.parseInt(txtReId.getText()));
                psmt.setInt(2, Integer.parseInt(cbxFatherId.getSelectedItem().toString()));
                psmt.setInt(3, Integer.parseInt(cbxMotherId.getSelectedItem().toString()));
                psmt.setInt(4, Integer.parseInt(cbxChildId.getSelectedItem().toString()));

                psmt.executeUpdate();
                JOptionPane.showMessageDialog(null,"Created successfully");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Information entered incorrectly, or Empty");
        }


    }//GEN-LAST:event_btnCreateActionPerformed

    private Exception NoInformationException() {

        return null;
    }

    private void lbCheckMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbCheckMouseClicked
        // TODO add your handling code here:
        changePanel(pnAbout);
    }//GEN-LAST:event_lbCheckMouseClicked

    private void lbHelpMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbHelpMouseClicked
        // TODO add your handling code here:
        changePanel(pnHelp);
    }//GEN-LAST:event_lbHelpMouseClicked

    private void lbTreeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbTreeMouseClicked
        // TODO add your handling code here:
        FamilyTree familyTree = new FamilyTree();
        familyTree.buildFamilyTree();
        familyTree.setVisible(true);
    }//GEN-LAST:event_lbTreeMouseClicked

    private void lbRelationshipMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbRelationshipMouseClicked
        // TODO add your handling code here:
        Relationship relationship = new Relationship();
        relationship.setVisible(true);
    }//GEN-LAST:event_lbRelationshipMouseClicked

    private void btnBrowseActionPerformed(ActionEvent evt) {//GEN-FIRST:event_btnBrowseActionPerformed
        // TODO add your handling code here:
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter fnwf = new FileNameExtensionFilter("PNG JPG AND JPEG","png","jpeg","jpg");
        fileChooser.addChoosableFileFilter(fnwf);
        int load = fileChooser.showOpenDialog(null);

        if(load == fileChooser.APPROVE_OPTION){
            SelectedFile = fileChooser.getSelectedFile();

            String path = SelectedFile.getAbsolutePath();
            ImageIcon ii = new ImageIcon(path);
            lbImage.setIcon(ResizeImage(path,null));
            ImgPath = path;
            
        }
        else{
            JOptionPane.showMessageDialog(null,"No file selected");
        }


    }//GEN-LAST:event_btnBrowseActionPerformed

    private void btnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrintActionPerformed
        // TODO add your handling code here:
        Print();
        
    }//GEN-LAST:event_btnPrintActionPerformed


    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Home().setVisible(true);

            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnBrowse;
    private javax.swing.JButton btnCreate;
    private javax.swing.JButton btnFind;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnReset;
    private javax.swing.JButton btnUpdate;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox cbxAlive;
    private javax.swing.JComboBox<String> cbxChildId;
    private javax.swing.JComboBox<String> cbxFatherId;
    private javax.swing.JComboBox<String> cbxMotherId;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator10;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JLabel lbAbout;
    private javax.swing.JLabel lbAddPerson;
    private javax.swing.JLabel lbCheck;
    private javax.swing.JLabel lbHelp;
    private javax.swing.JLabel lbHome;
    private javax.swing.JLabel lbImage;
    private javax.swing.JLabel lbRelationship;
    private javax.swing.JLabel lbTree;
    private javax.swing.JPanel pnAbout;
    private javax.swing.JPanel pnAddPerson;
    private javax.swing.JPanel pnHelp;
    private javax.swing.JPanel pnHome;
    private javax.swing.JPanel pnMenu;
    private javax.swing.JPanel pnParent;
    private javax.swing.JPanel pnPrint;
    private javax.swing.JPanel pnRelationship;
    private javax.swing.JPanel pnTree;
    private javax.swing.JRadioButton rbtFemale;
    private javax.swing.JRadioButton rbtMale;
    private javax.swing.JTable tbPerson;
    private javax.swing.JTextField txtAddress;
    private javax.swing.JTextField txtAge;
    private javax.swing.JTextField txtPersonID;
    private javax.swing.JTextField txtPersonName;
    private javax.swing.JTextField txtReId;
    private javax.swing.JTextArea txtaNote;
    // End of variables declaration//GEN-END:variables
}
