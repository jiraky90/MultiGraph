/**
 * MULTIGRAPH
 *
 * Project for Mattia Zago Master Thesis
 *
 * (C) 2015 - Mattia Zago
 *
 */
package es.um.multigraph.decision.basegraph;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 *
 * @author Mattia Zago <a href="mailto:dev@zagomattia.it">dev@zagomattia.it</a>
 */
public class JDialogNode extends javax.swing.JDialog {

    private Map<Field,JTextField> reflectedFields;
    private Class<? extends Node> nodeClass;
    
    /**
     * Creates new form JDialogNode
     * @param parent
     * @param modal
     * @param node
     * @param fields
     */
    public JDialogNode(java.awt.Frame parent, boolean modal, Class<? extends Node> node, Field[] fields) {
        super(parent, modal);
        
        if(node != null && fields != null) {
            this.nodeClass = node;
            this.reflectedFields = new HashMap<>();
            initComponents();

            for(Field f : fields) {
                // check because I can manage only string field here

                if(String.class.isAssignableFrom(f.getType())) {
                    this.reflectedFields.put(f, new JTextField());

                    this.PANEL.add(new JLabel("Field: "+f.getName()));
                    this.PANEL.add(this.reflectedFields.get(f));
                }
            }
        } else {
            //initComponents();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        BTN_SAVE = new javax.swing.JButton();
        BTN_CANCEL = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        PANEL = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Node Information");
        setAlwaysOnTop(true);

        BTN_SAVE.setText("SAVE");
        BTN_SAVE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BTN_SAVEActionPerformed(evt);
            }
        });

        BTN_CANCEL.setText("CANCEL");
        BTN_CANCEL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BTN_CANCELActionPerformed(evt);
            }
        });

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        PANEL.setLayout(new javax.swing.BoxLayout(PANEL, javax.swing.BoxLayout.PAGE_AXIS));
        jScrollPane1.setViewportView(PANEL);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(BTN_SAVE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(BTN_CANCEL, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(BTN_SAVE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(BTN_CANCEL)
                .addGap(0, 11, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private Node result;
    
    public Node getResult() {
        return result;
    }
    
    private void BTN_SAVEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BTN_SAVEActionPerformed
        try {
            result = this.nodeClass.newInstance();
            for(Field f : this.reflectedFields.keySet()) {
                f.set(result, this.reflectedFields.get(f).getText());
            }
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(JDialogNode.class.getName()).log(Level.SEVERE, null, ex);
        }
        dispose();
    }//GEN-LAST:event_BTN_SAVEActionPerformed

    private void BTN_CANCELActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BTN_CANCELActionPerformed
        dispose();
    }//GEN-LAST:event_BTN_CANCELActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BTN_CANCEL;
    private javax.swing.JButton BTN_SAVE;
    public javax.swing.JPanel PANEL;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
