/**
 * MULTIGRAPH
 *
 * Project for Mattia Zago Master Thesis
 *
 * (C) 2015 - Mattia Zago
 *
 */
package es.um.multigraph.decision.model;

import es.um.multigraph.decision.basegraph.*;
import es.um.multigraph.decision.DecisionInterface;
import it.zagomattia.cvss.CSVVBaseJDialogUserInput;
import it.zagomattia.cvss.base.CVSSBase;
import java.awt.Color;
import java.util.Collection;
import java.util.Set;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

/**
 *
 * @author Mattia Zago <a href="mailto:dev@zagomattia.it">dev@zagomattia.it</a>
 */
public class BayesianJDialogEdge extends javax.swing.JDialog {

    DecisionInterface model;

    /**
     * Creates new form NewJDialog
     *
     * @param parent
     * @param modal
     * @param model
     */
    public BayesianJDialogEdge(BayesianModelConfiguration parent, boolean modal, DecisionInterface model) {
        super(parent, modal);
        this.model = model;
        initComponents();
        this.setLocationRelativeTo(parent);
        this.setModal(modal);
    }

    public void overrideData(BayesianEdge e) {
        this.override = e;

        FROM_List.setEnabled(false);
        TO_List.setEnabled(false);

        from = (BayesianNode) e.getFrom();
        to = (BayesianNode) e.getTo();

        DefaultListModel fromList = new DefaultListModel();
        fromList.addElement(from);
        FROM_List.setModel(fromList);
        FROM_List.setSelectedValue(from, true);

        DefaultListModel toList = new DefaultListModel();
        toList.addElement(to);
        TO_List.setModel(toList);
        TO_List.setSelectedValue(to, true);

        pr = e.getPrActivable();
        PR_Value.setText(String.format("%.2f", pr).replaceAll(",", "."));
        PR_Value.setBackground(ok);

    }

    public BayesianNode from;
    public BayesianNode to;
    public Double pr = Double.NaN;
    public boolean fromCVSS = false;
    public CVSSBase cvssBaseScore;
    BayesianEdge override;

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane4 = new javax.swing.JScrollPane();
        TO_List = new javax.swing.JList();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        BTN_SAVE = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        FROM_List = new javax.swing.JList();
        BTN_CANCEL = new javax.swing.JButton();
        PR_Value = new javax.swing.JTextField();
        PR_Label = new javax.swing.JLabel();
        BTN_CVSSCalculator = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Add new Edge");

        TO_List.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        TO_List.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                TO_ListValueChanged(evt);
            }
        });
        jScrollPane4.setViewportView(TO_List);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setText("From Node");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setText("To Node");

        BTN_SAVE.setText("SAVE");
        BTN_SAVE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BTN_SAVEActionPerformed(evt);
            }
        });

        FROM_List.setModel(new ListModelNodes(((BayesianAttackGraph)model).getNodesNotCM()));
        FROM_List.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        FROM_List.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                FROM_ListValueChanged(evt);
            }
        });
        jScrollPane3.setViewportView(FROM_List);

        BTN_CANCEL.setText("CANCEL");
        BTN_CANCEL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BTN_CANCELActionPerformed(evt);
            }
        });

        PR_Value.setBackground(wrong);
        PR_Value.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                PR_ValuesomeKeyAction(evt);
            }
        });

        PR_Label.setText("Edge Probability");

        BTN_CVSSCalculator.setText("CVSS Calculator");
        BTN_CVSSCalculator.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BTN_CVSSCalculatorActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jScrollPane3)
                    .addComponent(jScrollPane4)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(BTN_SAVE, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(BTN_CANCEL, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(PR_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(PR_Value, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(BTN_CVSSCalculator, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(PR_Value, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(PR_Label, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BTN_CVSSCalculator))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BTN_SAVE)
                    .addComponent(BTN_CANCEL))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void TO_ListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_TO_ListValueChanged
        this.to = (BayesianNode) TO_List.getSelectedValue();
        if (to != null && from != null) {
            if (!from.canReach(to)) {
                this.TO_List.setBackground(ok);
            }
        }
    }//GEN-LAST:event_TO_ListValueChanged

    private void BTN_SAVEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BTN_SAVEActionPerformed
        if (this.to == null) {
            JOptionPane.showMessageDialog(rootPane, "You need to choose a destination node", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (this.from == null) {
            JOptionPane.showMessageDialog(rootPane, "You need to choose a source node", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (PR_Value.getBackground().equals(wrong)) {
            JOptionPane.showMessageDialog(rootPane, "The edge probability must be a number between 0 and 1", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        } else {
            this.pr = Double.parseDouble(PR_Value.getText());
        }

        if (override != null) {
            if (fromCVSS) {
                override.setPrActivable(cvssBaseScore);
            } else {
                override.setOverridePrActivable(pr);
            }
        }

        dispose();
    }//GEN-LAST:event_BTN_SAVEActionPerformed

    private void FROM_ListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_FROM_ListValueChanged
        this.from = (BayesianNode) FROM_List.getSelectedValue();
        if (this.from != null) {
            Collection<? extends Node> nlist = ((BayesianAttackGraph) model).getNodesNotCM();
            nlist.removeIf(n -> n.canReach(from)); // remove all reachable nodes
            from.getOut().stream().forEach(e -> nlist.remove(e.getTo()));
            TO_List.setModel(new ListModelNodes(nlist));
            TO_List.clearSelection();
            TO_List.setBackground(clear);

            if (nlist.isEmpty()) {
                TO_List.setBackground(wrong);
            }
        }
        this.repaint();
    }//GEN-LAST:event_FROM_ListValueChanged

    private void BTN_CANCELActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BTN_CANCELActionPerformed
        this.from = null;
        this.to = null;
        this.pr = Double.NaN;
        dispose();
    }//GEN-LAST:event_BTN_CANCELActionPerformed

    private void PR_ValuesomeKeyAction(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_PR_ValuesomeKeyAction
        try {
            this.pr = Double.parseDouble(PR_Value.getText());
            PR_Value.setBackground(ok);

        } catch (Exception e) {
            PR_Value.setBackground(wrong);
        }

    }//GEN-LAST:event_PR_ValuesomeKeyAction

    private void BTN_CVSSCalculatorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BTN_CVSSCalculatorActionPerformed
        CSVVBaseJDialogUserInput cvssJDialog = new CSVVBaseJDialogUserInput(null, true);
        cvssJDialog.setVisible(true);
        if (cvssJDialog.result != null) {
            this.cvssBaseScore = cvssJDialog.result;

            this.pr = 2
                    * cvssBaseScore.getAccessVector().getValue()
                    * cvssBaseScore.getAccessComplexity().getValue()
                    * cvssBaseScore.getAuthentication().getValue();

            this.PR_Value.setText(String.format("%.2f", pr).replaceAll(",", "."));
            this.PR_Value.setBackground(ok);

            this.fromCVSS = true;
        }
    }//GEN-LAST:event_BTN_CVSSCalculatorActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BTN_CANCEL;
    private javax.swing.JButton BTN_CVSSCalculator;
    private javax.swing.JButton BTN_SAVE;
    private javax.swing.JList FROM_List;
    private javax.swing.JLabel PR_Label;
    private javax.swing.JTextField PR_Value;
    private javax.swing.JList TO_List;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    // End of variables declaration//GEN-END:variables

    private static final Color wrong = new Color(255, 153, 153);
    private static final Color ok = new Color(153, 255, 153);
    private static final Color clear = new Color(255, 255, 255);
}