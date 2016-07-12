/**
 * MULTIGRAPH
 *
 * Project for Mattia Zago Master Thesis
 *
 * (C) 2015 - Mattia Zago
 *
 */
package es.um.multigraph.decision.model;

import es.um.multigraph.decision.basegraph.Node;
import java.awt.Color;
import java.awt.Component;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Mattia Zago <a href="mailto:dev@zagomattia.it">dev@zagomattia.it</a>
 */
public class BayesianLCPDTableViewer extends javax.swing.JFrame {

    private BayesianAttackGraph model;
    private Node filter;
    private Vector<Vector> backupModel;

    /**
     * Creates new form BayesianLCPDTableViewer
     *
     * @param model
     * @param filter
     */
    public BayesianLCPDTableViewer(BayesianAttackGraph model, Node filter) {
        this.model = model;
        this.filter = filter;
        initComponents();
        setLocationRelativeTo(null);

        this.backupModel = new Vector<>(((DefaultTableModel) model.getJTableModel(filter)).getDataVector());

        LCPD.getModel().addTableModelListener(new MyTableModelListener(this));

        DefaultListModel SEL_List_Rows = new DefaultListModel();
        for (int i = 0; i < LCPD.getRowCount(); i++) {
            SEL_List_Rows.addElement("Row " + i);
        }
        this.SEL_List.setModel(SEL_List_Rows);

        DefaultComboBoxModel SEL_Col_Rows = new DefaultComboBoxModel();
        for (int i = 0; i < LCPD.getColumnCount(); i++) {
            SEL_Col_Rows.addElement(LCPD.getColumnName(i));
        }
        SEL_COL.setModel(SEL_Col_Rows);

        SEL_COLActionPerformed(null);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ScrollPane_LCPD = new javax.swing.JScrollPane();
        LCPD = new javax.swing.JTable();
        ICON = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        BTN_EnableEdit = new javax.swing.JToggleButton();
        TAB = new javax.swing.JTabbedPane();
        ScrollPane_SEL = new javax.swing.JScrollPane();
        SEL_List = new javax.swing.JList();
        CUSTOM_FILTER = new javax.swing.JPanel();
        SEL_COL = new javax.swing.JComboBox();
        jScrollPane3 = new javax.swing.JScrollPane();
        SEL_VAL = new javax.swing.JList();
        BTN_ClearSelection = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Bayesian "+(filter!=null?filter.getID():"full")+" LCPD Table Viewer");

        LCPD.setModel(this.model.getJTableModel(filter));
        LCPD.setEnabled(BTN_EnableEdit.isSelected());
        LCPD.setRowSelectionAllowed(false);
        LCPD.setUpdateSelectionOnSort(false);
        LCPD.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                LCPDMouseClicked(evt);
            }
        });
        ScrollPane_LCPD.setViewportView(LCPD);

        ICON.setText("Editing is disabled");
        ICON.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        BTN_EnableEdit.setText("Enable Edit");
        BTN_EnableEdit.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        BTN_EnableEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BTN_EnableEditActionPerformed(evt);
            }
        });

        TAB.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                TABStateChanged(evt);
            }
        });

        ScrollPane_SEL.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        ScrollPane_SEL.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        SEL_List.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        SEL_List.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                SEL_ListValueChanged(evt);
            }
        });
        ScrollPane_SEL.setViewportView(SEL_List);

        TAB.addTab("Highlight rows", ScrollPane_SEL);

        SEL_COL.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        SEL_COL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SEL_COLActionPerformed(evt);
            }
        });

        jScrollPane3.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane3.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        SEL_VAL.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        SEL_VAL.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                SEL_VALValueChanged(evt);
            }
        });
        jScrollPane3.setViewportView(SEL_VAL);

        javax.swing.GroupLayout CUSTOM_FILTERLayout = new javax.swing.GroupLayout(CUSTOM_FILTER);
        CUSTOM_FILTER.setLayout(CUSTOM_FILTERLayout);
        CUSTOM_FILTERLayout.setHorizontalGroup(
            CUSTOM_FILTERLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CUSTOM_FILTERLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(CUSTOM_FILTERLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3)
                    .addComponent(SEL_COL, 0, 240, Short.MAX_VALUE))
                .addContainerGap())
        );
        CUSTOM_FILTERLayout.setVerticalGroup(
            CUSTOM_FILTERLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CUSTOM_FILTERLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(SEL_COL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        TAB.addTab("Custom Filter", CUSTOM_FILTER);

        BTN_ClearSelection.setText("Clear Selection");
        BTN_ClearSelection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BTN_ClearSelectionActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(BTN_EnableEdit, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
            .addComponent(BTN_ClearSelection, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addComponent(TAB, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(BTN_EnableEdit)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(BTN_ClearSelection)
                .addGap(0, 176, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                    .addGap(0, 60, Short.MAX_VALUE)
                    .addComponent(TAB, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(ScrollPane_LCPD, javax.swing.GroupLayout.PREFERRED_SIZE, 693, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(ICON, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ScrollPane_LCPD, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(ICON, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BTN_EnableEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BTN_EnableEditActionPerformed

        if (BTN_EnableEdit.isSelected()) {
            LCPD.setEnabled(true);
            BTN_EnableEdit.setText("Stop Edit");
            ICON.setIcon(new javax.swing.ImageIcon(getClass().getResource("/es/um/multigraph/resources/images/loader.gif")));
            ICON.setText("Editing is enabled");
            setDefaultCloseOperation(BayesianLCPDTableViewer.DO_NOTHING_ON_CLOSE);
        } else {
            LCPD.setEnabled(false);
            BTN_EnableEdit.setText("Enable Edit");
            ICON.setIcon(null);
            ICON.setText("Editing is disabled");
            setDefaultCloseOperation(BayesianLCPDTableViewer.DISPOSE_ON_CLOSE);
        }

    }//GEN-LAST:event_BTN_EnableEditActionPerformed

    private void SEL_ListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_SEL_ListValueChanged
        applyFilter();
    }//GEN-LAST:event_SEL_ListValueChanged

    private void TABStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_TABStateChanged
        applyFilter();
    }//GEN-LAST:event_TABStateChanged

    private void SEL_COLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SEL_COLActionPerformed
        DefaultListModel val = new DefaultListModel();

        String col = LCPD.getColumnName(SEL_COL.getSelectedIndex());

        if (col.equalsIgnoreCase("prTrue") || col.equalsIgnoreCase("prFalse")) {
            val.addElement("0.00 < x < 0.25");
            val.addElement("0.25 < x < 0.50");
            val.addElement("0.50 < x < 0.75");
            val.addElement("0.75 < x < 1.00");
        } else {
            val.addElement("M");
            val.addElement("NULL");
            val.addElement("True");
            val.addElement("False");
        }

        SEL_VAL.setModel(val);
    }//GEN-LAST:event_SEL_COLActionPerformed

    private void SEL_VALValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_SEL_VALValueChanged
        applyFilter();
    }//GEN-LAST:event_SEL_VALValueChanged

    private void LCPDMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_LCPDMouseClicked

        if(LCPD.getSelectedRows().length < 1)
            return;
        
        SEL_List.clearSelection();
        SEL_VAL.clearSelection();
        List<Integer> rows = new LinkedList<>();
        for (int y : LCPD.getSelectedRows()) {
            rows.add(y);
        }

        for (int i = 0; i < LCPD.getColumnCount(); i++) {
            LCPD.getColumnModel().getColumn(i).setCellRenderer(new StatusColumnCellRenderer(rows, null));
        }

        LCPD.repaint();

    }//GEN-LAST:event_LCPDMouseClicked

    private void BTN_ClearSelectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BTN_ClearSelectionActionPerformed
        SEL_List.clearSelection();
        SEL_VAL.clearSelection();
        noFilter = true;
        applyFilter();
        noFilter = false;
    }//GEN-LAST:event_BTN_ClearSelectionActionPerformed

    public void enableEdit() {
        this.BTN_EnableEdit.doClick();
    }
    
    private boolean noFilter = false;

    private void applyFilter() {

        List<Integer> rows = new LinkedList<>();
        Integer col = null;
        if (!noFilter) {
            if (TAB.getSelectedComponent().equals(ScrollPane_SEL)) {
                for (int y : SEL_List.getSelectedIndices()) {
                    rows.add(y);
                }
            } else if (TAB.getSelectedComponent().equals(CUSTOM_FILTER)) {
                for (int y : SEL_VAL.getSelectedIndices()) {
                    rows.add(y);
                }
                col = SEL_COL.getSelectedIndex();
            }
        }
        for (int i = 0; i < LCPD.getColumnCount(); i++) {
            LCPD.getColumnModel().getColumn(i).setCellRenderer(new StatusColumnCellRenderer(rows, col));
        }

        LCPD.repaint();

    }

    private boolean innerChange = false;

    public void tableChanged(TableModelEvent e) {
        if (e.getType() == TableModelEvent.UPDATE) {
            int col = LCPD.getSelectedColumn();
            int row = LCPD.getSelectedRow();
            if (!innerChange) {
                innerChange = true;
                if (LCPD.getColumnName(col).equalsIgnoreCase("prTrue")) {
                    String tmp = LCPD.getValueAt(row, col).toString();
                    tmp = tmp.replaceAll(",", ".");
                    Double tmpVal;
                    try {
                        tmpVal = Double.parseDouble(tmp);
                        if (tmpVal < 0 || tmpVal > 1) {
                            throw new NumberFormatException("Must between 0 and 1");
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(rootPane, "You must insert a number between 0 and 1", "NumberFormatException", JOptionPane.ERROR_MESSAGE);
                        LCPD.setValueAt(backupModel.get(row).get(col), row, col);
                        LCPD.setValueAt(backupModel.get(row).get(col + 1), row, col + 1);
                        innerChange = false;
                        return;
                    }
                    LCPD.setValueAt(String.format("%.2f", 1 - tmpVal).replaceAll(",", "."), row, col + 1);
                    ICON.setText("[" + row + ", prTrue]: "
                            + String.format("%.2f", tmpVal).replaceAll(",", ".")
                            + " - Force [" + row + ", prFalse]: " + String.format("%.2f", 1 - tmpVal).replaceAll(",", ".")
                    );

                    parseRow(row);

                } else if (LCPD.getColumnName(col).equalsIgnoreCase("prFalse")) {
                    String tmp = LCPD.getValueAt(row, col).toString();
                    tmp = tmp.replaceAll(",", ".");
                    Double tmpVal;
                    try {
                        tmpVal = Double.parseDouble(tmp);
                        if (tmpVal < 0 || tmpVal > 1) {
                            throw new NumberFormatException("Must between 0 and 1");
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(rootPane, "You must insert a number between 0 and 1", "NumberFormatException", JOptionPane.ERROR_MESSAGE);
                        LCPD.setValueAt(backupModel.get(row).get(col), row, col);
                        LCPD.setValueAt(backupModel.get(row).get(col - 1), row, col - 1);
                        innerChange = false;
                        return;
                    }
                    LCPD.setValueAt(String.format("%.2f", 1 - tmpVal).replaceAll(",", "."), row, col - 1);
                    ICON.setText("[" + row + ", prFalse]: "
                            + String.format("%.2f", tmpVal).replaceAll(",", ".")
                            + " - Force [" + row + ", prTrue]: " + String.format("%.2f", 1 - tmpVal).replaceAll(",", ".")
                    );

                    parseRow(row);

                } else {
                    JOptionPane.showMessageDialog(rootPane, "Change not allowed.\nYou can change only prTrue and prFalse values", "Change not allowed", JOptionPane.ERROR_MESSAGE);
                    LCPD.setValueAt(backupModel.get(row).get(col), row, col);
                }
                innerChange = false;
            }
        }
    }

    private void parseRow(int row) {
        List<String> parentID = new LinkedList();
        List<Boolean> parentState = new LinkedList<>();
        String me = "";
        Double prTrue = Double.NaN;
        Double prFalse = Double.NaN;

        for (int c = 0; c < LCPD.getColumnCount(); ++c) {
            Object tmp = LCPD.getValueAt(row, c);
            if (tmp != null) {
                //System.out.println("Col: " + LCPD.getColumnName(c) + " Val: " + tmp);
                if (tmp.toString().equalsIgnoreCase("M")) {
                    me = LCPD.getColumnName(c);
                } else if (LCPD.getColumnName(c).equalsIgnoreCase("prTrue")) {
                    prTrue = Double.parseDouble(LCPD.getValueAt(row, c).toString().replaceAll(",", "."));
                } else if (LCPD.getColumnName(c).equalsIgnoreCase("prFalse")) {
                    prFalse = Double.parseDouble(LCPD.getValueAt(row, c).toString().replaceAll(",", "."));
                } else {
                    parentID.add(LCPD.getColumnName(c));
                    parentState.add(Boolean.parseBoolean(tmp.toString()));
                }
            }
        }

        String log = "Update LCPD table: ["
                + me + "='M'"
                + ", T='" + String.format("%.2f", prTrue) + "'"
                + ", F='" + String.format("%.2f", prFalse) + "'";

        for (int i = 0; i < parentID.size(); ++i) {
            log += ", " + parentID.get(i) + "='" + parentState.get(i) + "'";
        }

        log += "]\n";
        model.log(log);

        try {
            model.LCPD_SQL_updatePr(parentID.toArray(new String[parentID.size()]), parentState.toArray(new Boolean[parentID.size()]), me, prTrue, prFalse);
            model.parent.repaintGraph();
            ICON.setText("Change saved!");
        } catch (SQLException ex) {
            Logger.getLogger(BayesianLCPDTableViewer.class.getName()).log(Level.SEVERE, null, ex);
            model.log("Error while updating LCPD table");
        }
    }

    public static void main(String[] args) {
        BayesianAttackGraph x = new BayesianAttackGraph();
        x.init(null);
        x.computeLCPD();
        new BayesianLCPDTableViewer(x, null).setVisible(true);

    }

    private static final Color wrong = new Color(255, 153, 153);
    private static final Color ok = new Color(153, 255, 153);
    private static final Color clear = new Color(255, 255, 255);

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BTN_ClearSelection;
    private javax.swing.JToggleButton BTN_EnableEdit;
    private javax.swing.JPanel CUSTOM_FILTER;
    private javax.swing.JLabel ICON;
    private javax.swing.JTable LCPD;
    private javax.swing.JComboBox SEL_COL;
    private javax.swing.JList SEL_List;
    private javax.swing.JList SEL_VAL;
    private javax.swing.JScrollPane ScrollPane_LCPD;
    private javax.swing.JScrollPane ScrollPane_SEL;
    private javax.swing.JTabbedPane TAB;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane3;
    // End of variables declaration//GEN-END:variables

    private class MyTableModelListener implements TableModelListener {

        BayesianLCPDTableViewer parent;

        public MyTableModelListener(BayesianLCPDTableViewer parent) {
            super();
            this.parent = parent;
        }

        @Override
        public void tableChanged(TableModelEvent e) {
            parent.tableChanged(e);
        }
    }

    private class StatusColumnCellRenderer extends DefaultTableCellRenderer {

        Collection<Integer> sel_rows;
        Integer sel_col;
        private final Color selected = new Color(255, 255, 204);
        private final Color selected_2 = new Color(204, 204, 153);
        private final Color selected_M = new Color(255, 204, 153);
        private final Color not_selected = new Color(255, 255, 255);

        public StatusColumnCellRenderer(Collection<Integer> highlight, Integer col) {
            super();
            this.sel_rows = highlight;
            this.sel_col = col;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {

            //Cells are by default rendered as a JLabel.
            JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

            if (sel_col == null) { // not a custom filter
                if (sel_rows.contains(row)) {
                    Object Mobj = LCPD.getValueAt(row, col);
                    String M = "";
                    if (Mobj != null) {
                        M = Mobj.toString();
                    }

                    if (M.equals("M")) {
                        l.setBackground(selected_M);
                        return l;
                    } else {
                        l.setBackground(selected);
                        return l;
                    }
                } else {
                    l.setBackground(not_selected);
                    return l;
                }
            }

            if (sel_col == 0 || sel_col == 1) { // custom filter on prTrue or prFalse
                try {
                    Double val = Double.parseDouble(LCPD.getValueAt(row, sel_col).toString().replaceAll(",", "."));
                    int cat;
                    if (val >= 0.00 && val <= 0.25) {
                        cat = 0;
                    } else if (val >= 0.25 && val <= 0.50) {
                        cat = 1;
                    } else if (val >= 0.50 && val <= 0.75) {
                        cat = 2;
                    } else if (val >= 0.75 && val <= 1.00) {
                        cat = 3;
                    } else {
                        l.setBackground(not_selected);
                        return l;
                    }

                    if (sel_rows.contains(cat)) {
                        if (sel_col == col) {
                            l.setBackground(selected_2);
                            return l;
                        } else {
                            Object Mobj = LCPD.getValueAt(row, col);
                            String M = "";
                            if (Mobj != null) {
                                M = Mobj.toString();
                            }
                            if (M.equals("M")) {
                                l.setBackground(selected_M);
                                return l;
                            } else {
                                l.setBackground(selected);
                                return l;
                            }
                        }

                    } else {
                        l.setBackground(not_selected);
                        return l;
                    }

                } catch (NumberFormatException ex) {
                    l.setBackground(not_selected);
                    return l;
                }
            }

            if (sel_col > 1) { // custom filter not on prTrue or prFalse
                Object tmp = LCPD.getValueAt(row, sel_col);

                //M
                //NULL
                //True
                //False
                if (tmp == null) {
                    if (sel_rows.contains(1)) {
                        if (sel_col == col) {
                            l.setBackground(selected_2);
                            return l;
                        } else {
                            Object Mobj = LCPD.getValueAt(row, col);
                            String M = "";
                            if (Mobj != null) {
                                M = Mobj.toString();
                            }
                            if (M.equals("M")) {
                                l.setBackground(selected_M);
                                return l;
                            } else {
                                l.setBackground(selected);
                                return l;
                            }
                        }
                    } else {
                        l.setBackground(not_selected);
                        return l;
                    }
                }

                String val = tmp.toString();

                if (val.equalsIgnoreCase("M") && sel_rows.contains(0)) {
                    if (sel_col == col) {
                        l.setBackground(selected_2);
                        return l;
                    } else {
                        Object Mobj = LCPD.getValueAt(row, col);
                        String M = "";
                        if (Mobj != null) {
                            M = Mobj.toString();
                        }
                        if (M.equals("M")) {
                            l.setBackground(selected_M);
                            return l;
                        } else {
                            l.setBackground(selected);
                            return l;
                        }
                    }
                }

                if (val.equalsIgnoreCase("True") && sel_rows.contains(2)) {
                    if (sel_col == col) {
                        l.setBackground(selected_2);
                        return l;
                    } else {
                        Object Mobj = LCPD.getValueAt(row, col);
                        String M = "";
                        if (Mobj != null) {
                            M = Mobj.toString();
                        }

                        if (M.equals("M")) {
                            l.setBackground(selected_M);
                            return l;
                        } else {
                            l.setBackground(selected);
                            return l;
                        }
                    }
                }

                if (val.equalsIgnoreCase("False") && sel_rows.contains(3)) {
                    if (sel_col == col) {
                        l.setBackground(selected_2);
                        return l;
                    } else {
                        Object Mobj = LCPD.getValueAt(row, col);
                        String M = "";
                        if (Mobj != null) {
                            M = Mobj.toString();
                        }
                        if (M.equals("M")) {
                            l.setBackground(selected_M);
                            return l;
                        } else {
                            l.setBackground(selected);
                            return l;
                        }
                    }
                }

                l.setBackground(not_selected);
                return l;
            }

            l.setBackground(not_selected);
            return l;
        }
    }
}
