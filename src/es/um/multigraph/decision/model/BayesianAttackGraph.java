/**
 * MULTIGRAPH
 *
 * Project for Mattia Zago Master Thesis
 *
 * (C) 2015 - Mattia Zago
 *
 */
package es.um.multigraph.decision.model;

import es.um.multigraph.conf.DBManager;
import es.um.multigraph.conf.FeaturesEnum;
import es.um.multigraph.conf.RiskScale;
import es.um.multigraph.core.MainClass;
import es.um.multigraph.decision.DecisionInterface;
import es.um.multigraph.decision.DecisionInterfaceImpl;
import es.um.multigraph.decision.basegraph.Edge;
import es.um.multigraph.decision.basegraph.Node;
import es.um.multigraph.event.Event;
import es.um.multigraph.event.EventStream;
import es.um.multigraph.event.dummy.DummyEvent;
import es.um.multigraph.event.solution.Solution;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import org.junit.Test;

/**
 * <h1>Dynamic Security Risk Management Using Bayesian Attack Graph</h1>
 * <h2>IEEE TRANSICTION ON DEPENDABLE AND SECURE COMPUTING, VOL. 9, NO. 1,
 * JANUARY/FEBRAURY 2012</h2>
 * <h3>Abstract</h3>
 * <p>
 * Security risk assessment and mitigation are two vital processes that need to
 * be executed to maintain a productive IT infrastructure. On one hand, models
 * such as attack graphs and attack trees have been proposed to assess the
 * cause-consequence relationships between various network states, while on the
 * other hand, different decision problems have been explored to identify the
 * minimum-cost hardening measures. However, these risk models do not help
 * reason about the causal dependencies between network states. Further, the
 * optimization formulations ignore the issue of resource availability while
 * analyzing a risk model. In this paper, we propose a risk management framework
 * using Bayesian networks that enable a system administrator to quantify the
 * chances of network compromise at various levels. We show how to use this
 * information to develop a security mitigation and management plan. In contrast
 * to other similar models, this risk model lends itself to dynamic analysis
 * during the deployed phase of the network. A multiobjective optimization
 * platform provides the administrator with all trade-off information required
 * to make decisions in a resource constrained environment.</p>
 *
 * @author Mattia Zago <a href="mailto:dev@zagomattia.it">dev@zagomattia.it</a>
 * (Implementation)
 * @author Nayot Poolsappasit <a href="mailto:nayot@mst.edu">nayot@mst.edu</a>
 * (Paper author)
 * @author Rinku Dewri <a href="mailto:rdewri@cs.du.edu">rdewri@cs.du.edu</a>
 * (Paper author)
 * @author Indrajit Ray
 * <a href="mailto:indrajit@cs.solostate.edu">indrajit@cs.solostate.edu</a>
 * (Paper author)
 * @see
 * <a href="http://dx.doi.org/10.1109/TDSC.2011.34">http://dx.doi.org/10.1109/TDSC.2011.34</a><br>
 */
public class BayesianAttackGraph implements DecisionInterface {

    MainClass parent;
    DBManager DB;
    private boolean stop = false;

    public BayesianAttackGraph() {
        super();

        DB = new DBManager("BayesianModelDatabase.db", DBManager.DRIVER_SQLLITE);
        /*
        try {

         // TO BE REMOVED IN THE FINAL VERSION. THIS CONFIGURE THE MYSQL CONNECTION STRING.
         // BTW. SPECTRE IS THE HOSTNAME OF MY LAPTOP
            if (InetAddress.getLocalHost().getHostName().equalsIgnoreCase("SPECTRE")) {
               DB = new DBManager("localhost", "3306", "bayesian", "bayesianuser", "bayesianpassword", DBManager.DRIVER_MYSQL);
            } else {
               DB = new DBManager("155.54.95.128", "3306", "bayesian", "bayesianuser", "bayesianpassword", DBManager.DRIVER_MYSQL);
            }

         } catch (UnknownHostException ex) {
            Logger.getLogger(BayesianAttackGraph.class.getName()).log(Level.SEVERE, null, ex);
            DB = new DBManager("155.54.95.128", "3306", "bayesian", "bayesianuser", "bayesianpassword", DBManager.DRIVER_MYSQL);
         } finally {
            log("Connected to " + DB.getDatabase() + "@" + DB.getHostname() + ":" + DB.getPort() + "\n");
         }
         */
    }

    private void setupDB(boolean dropEverything) throws SQLException {
        DB.connect();
        Connection conn = DB.getConnection();

        String drop = "DROP TABLE lcpd;";

        String queryCreateLCPD = ""
                + "CREATE TABLE lcpd ("
                + "ID INT NOT NULL AUTO_INCREMENT, "
                + "prTrue FLOAT NOT NULL,"
                + "prFalse FLOAT NOT NULL,"
                + "UNIQUE INDEX ID_UNIQUE (ID ASC)"
                + ");";
        Statement st = conn.createStatement();

        if (dropEverything) { // Does not cause any problem in MYSQL
            try {
                st.execute(drop);
            } catch (SQLException ex) {
                if(!ex.getMessage().contains("no such table"))  // Throw exception in SQLite if the table does not exists
                    throw ex;
            }
        }

        st.execute(DBManager.translateFromMySQLtoSQLite(queryCreateLCPD));
        st.close();
        conn.close();
        DB.disconnect();
        log("Database correctly initialized (" + (dropEverything ? "Drop Enabled" : "Drop Disabled") + ")\n");
    }

    private void emptyDB() throws SQLException {
        DB.connect();
        try (Connection conn = DB.getConnection()) {
            String delete = "DELETE FROM lcpd;";
            try (Statement st = conn.createStatement()) {
                st.execute(delete);
            }
        }
        DB.disconnect();
        log("Database correctly cleared\n");
    }

    private void LCPD_SQL_addColumn(String unique_name, String sql_type, boolean notNull, String defaultValue) throws SQLException {
        String alterQuery = "ALTER TABLE lcpd ADD COLUMN " + unique_name + " " + sql_type + " " + (notNull ? "NOT NULL" : "") + " " + (defaultValue == null ? ";" : ("DEFAULT " + defaultValue));

        try (Connection conn = DB.getConnection(); Statement st = conn.createStatement()) {
            st.executeUpdate(alterQuery);
        }
        DB.disconnect();
    }

    private void LCPD_SQL_addRow(String[] nodesID, Boolean[] nodesState, String me, double prTrue, double prFalse) throws SQLException {
        try (Connection conn = DB.getConnection()) {
            if (nodesID == null) {
                nodesID = new String[0];
            }
            if (nodesState == null) {
                nodesState = new Boolean[0];
            }
            // search for duplicates
            String checkQuery = "SELECT prTrue as T, prFalse as F FROM lcpd WHERE " + me + "='M'";
            for (int i = 0; i < nodesID.length; i++) {
                checkQuery += " AND " + nodesID[i] + "=" + (nodesState[i] ? "1" : "0");
            }
            
            ResultSet rs = conn.createStatement().executeQuery(checkQuery);
            if (rs.next()) //throw new SQLException("Duplicate state array", "DUP", 1062);
            {
                return; // better if we throw an exception?
            }
            if (nodesID.length != nodesState.length) {
                throw new AssertionError("ID and states have different size");
            }
            
            String insertQuery = "INSERT INTO lcpd (prTrue, prFalse, " + me + "";
            String values = "VALUES (?, ?, ?";
            for (String n : nodesID) {
                insertQuery += ", " + n + "";
                values += ", ?";
            }
            values += ")";
            insertQuery += ") " + values;
            
            try ( //this.update(insertQuery);
                    PreparedStatement ps = conn.prepareStatement(insertQuery)) {
                ps.setDouble(1, prTrue);
                ps.setDouble(2, prFalse);
                ps.setString(3, "M");
                
                for (int i = 0; i < nodesID.length; i++) {
                    ps.setString(4 + i, nodesState[i] ? "1" : "0");
                }
                
                ps.executeUpdate();
            }
        }
        DB.disconnect();

    }

    public final void LCPD_SQL_updatePr(String[] nodesID, Boolean[] nodesState, String me, double prTrue, double prFalse) throws SQLException {
        try (Connection conn = DB.getConnection()) {
            if (nodesID == null) {
                nodesID = new String[0];
            }
            if (nodesState == null) {
                nodesState = new Boolean[0];
            }
            
            if (nodesID.length != nodesState.length) {
                throw new AssertionError("ID and states have different size");
            }
            
            String updQuery = "UPDATE lcpd SET prTrue='" + prTrue + "', prFalse='" + prFalse + "' WHERE " + me + "='M'";
            for (int i = 0; i < nodesID.length; i++) {
                updQuery += " AND " + nodesID[i] + "=" + (nodesState[i] ? "1" : "0");
            }
            
            try (PreparedStatement ps = conn.prepareStatement(updQuery)) {
                ps.executeUpdate();
            }
        }
        DB.disconnect();
    }

    private Double LCPD_SQL_searchRecord(String me, String[] nodesID, boolean[] nodesState, boolean prTrue) throws SQLException {
        Connection conn = DB.getConnection();
        Double result;

        String query = "SELECT " + (prTrue ? "prTrue" : "prFalse") + " FROM lcpd WHERE " + me + "='M'";

        for (String n : nodesID) {
            query += " AND " + n + "=?";
        }

        query += " LIMIT 1";

        //this.update("Looking for " + me + " (" + (prTrue ? "prTrue" : "prFalse") + ") in LCPD table: " + query + " -- " + Arrays.toString(nodesState));
        PreparedStatement ps = conn.prepareStatement(query);

        for (int i = 0; i < nodesState.length; i++) {
            ps.setString(i + 1, nodesState[i] ? "1" : "0");
        }

        ResultSet rs = ps.executeQuery();
        rs.next();
        result = rs.getDouble(1); // RESULT COLUMN ARE COUNTED FROM 1

        return result;
    }

    /**
     * Ref. Example in fig. 3 in the paper
     *
     * @param main
     */
    @Override
    public void init(MainClass main) {
        this.parent = main;
        log("Start default initialization\n");

        BayesianNode A = new BayesianNode("A", 0.6);
        BayesianNode B = new BayesianNode("B", Double.NaN);
        BayesianNode C = new BayesianNode("C", Double.NaN);
        BayesianNode D = new BayesianNode("D", Double.NaN);
        BayesianNode E = new BayesianNode("E", Double.NaN);
        
        A.setLabel("Remote Attacker");
        A.setExpectedGain(0.6);
        B.setLabel("Website Remote Login");
        B.setExpectedGain(0.2);
        C.setLabel("SSH Remote Login");
        C.setExpectedGain(0.4);
        D.setLabel("Data Breach");
        D.setExpectedGain(0.4);
        E.setLabel("Massive SPAM");
        E.setExpectedGain(0.2);
        
        BayesianEdge AB = new BayesianEdge("SQL Injection", A, B);
        BayesianEdge AC = new BayesianEdge("Weak Password", A, C);
        BayesianEdge BD = new BayesianEdge("Database Dump", B, D);
        BayesianEdge CD = new BayesianEdge("File Dump", C, D);
        BayesianEdge CE = new BayesianEdge("Mail Server Vulnerability", C, E);
        
        AB.setDecomposition(BayesianEdge.DECOMPOSITION_OR);
        AB.setOverridePrActivable(0.4);
        AC.setDecomposition(BayesianEdge.DECOMPOSITION_OR);
        AC.setOverridePrActivable(0.6);
        BD.setDecomposition(BayesianEdge.DECOMPOSITION_OR);
        BD.setOverridePrActivable(0.9);
        CD.setDecomposition(BayesianEdge.DECOMPOSITION_OR);
        CD.setOverridePrActivable(0.9);
        CE.setDecomposition(BayesianEdge.DECOMPOSITION_OR);
        CE.setOverridePrActivable(0.7);
        
        try {
            this.setupDB(true);
            this.DB.connect();
        } catch (SQLException ex) {
            Logger.getLogger(BayesianAttackGraph.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, ""
                    + "There was an error while performing SQL operation.\n"
                    + "The system will shutdown now."
                    + "\nSQL Error Code: " + ex.getSQLState() + "\n"
                    + "Exception:\n" + ex.toString() + "\n", "SQL ERROR", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }
        
        this.BAG = new HashSet<>();
        this.addNode(A);
        this.addNode(B);
        this.addNode(C);
        this.addNode(D);
        this.addNode(E);

        this.addEdge(AB);
        this.addEdge(AC);
        this.addEdge(BD);
        this.addEdge(CD);
        this.addEdge(CE);

        this.setExpectedGainWeight(0.5);
        this.setExpectedLossWeight(0.5);
        
        System.out.println(A.getFullRepresentationAsString(true));
        System.out.println(B.getFullRepresentationAsString(true));
        System.out.println(C.getFullRepresentationAsString(true));
        System.out.println(D.getFullRepresentationAsString(true));
        System.out.println(E.getFullRepresentationAsString(true));
        
        /*
        BayesianNode A = new BayesianNode("A", Double.NaN);
        A.setLabel("Access to root/FTP server");
        A.setExpectedGain(0.9);

        //reset DB
        try {
            this.setupDB(true);
        } catch (SQLException ex) {
            Logger.getLogger(BayesianAttackGraph.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, ""
                    + "There was an error while performing SQL operation.\n"
                    + "The system will shutdown now."
                    + "\nSQL Error Code: " + ex.getSQLState() + "\n"
                    + "Exception:\n" + ex.toString() + "\n", "SQL ERROR", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }

        BayesianNode B = new BayesianNode("B", Double.NaN);
        B.setLabel("Matu FTP BOF");
        B.setExpectedGain(0.5);

        BayesianNode C = new BayesianNode("C", Double.NaN);
        C.setLabel("Remote BOF on ssh daemon");
        B.setExpectedGain(0.2);

        BayesianNode D = new BayesianNode("D", 0.70);
        D.setLabel("Remote attacker");
        D.setExpectedGain(0.6);

        try {
            DB.connect();
        } catch (SQLException ex) {
            Logger.getLogger(BayesianAttackGraph.class.getName()).log(Level.SEVERE, null, ex);
        }

        BayesianEdge DB = new BayesianEdge("DB", D, B);
        DB.setDecomposition(BayesianEdge.DECOMPOSITION_AND);
        DB.setOverridePrActivable(0.85);

        BayesianEdge DC = new BayesianEdge("DC", D, C);
        DC.setDecomposition(BayesianEdge.DECOMPOSITION_AND);
        DC.setOverridePrActivable(0.70);

        BayesianEdge BA = new BayesianEdge("BA", B, A);
        BA.setDecomposition(BayesianEdge.DECOMPOSITION_OR);
        BA.setOverridePrActivable(0.65);

        BayesianEdge CA = new BayesianEdge("CA", C, A);
        CA.setDecomposition(BayesianEdge.DECOMPOSITION_OR);
        CA.setOverridePrActivable(1.00);

        this.BAG = new HashSet<>();
        this.addNode(A);
        this.addNode(B);
        this.addNode(C);
        this.addNode(D);

        this.addEdge(DB);
        this.addEdge(DC);
        this.addEdge(BA);
        this.addEdge(CA);

        this.setExpectedGainWeight(0.5);
        this.setExpectedLossWeight(0.5);

        /*
         this.compromisedNodes = new HashSet<>();
         this.addCompromisedNode(A);
         */
        /*
         try {
            
         this.computeLCPD();
         this.computeUnconditionalProbability();
         this.computePosterior();
         } catch (Exception ex) {
         Logger.getLogger(BayesianAttackGraph.class.getName()).log(Level.SEVERE, null, ex);
         }
         */
        /*
        //this.log("Adding CMs");
        BayesianCMNode<Solution> M0 = new BayesianCMNode<>("M0", AC_AccessControl.AC_01);
        M0.getCountermeasure().setCost(0.5);

        BayesianCMEdge M0A = new BayesianCMEdge("M0A", M0, A);
        M0A.setDecomposition(BayesianEdge.DECOMPOSITION_AND);

        this.addNode(M0);
        this.addEdge(M0A);
        this.enableCM(M0);
        /*
         BayesianCMNode<Solution> M1 = new BayesianCMNode<>("M1", AC_AccessControl.AC_02);
         M1.getCountermeasure().setCost(0.7);

         BayesianCMEdge M1A = new BayesianCMEdge("M1A", M1, A);
         M1A.setDecomposition(BayesianEdge.DECOMPOSITION_AND);

         this.addNode(M1);
         this.addEdge(M1A);
         this.enableCM(M1);

         BayesianCMNode<Solution> M2 = new BayesianCMNode<>("M2", AC_AccessControl.AC_03);
         M2.getCountermeasure().setCost(0.2);

         BayesianCMEdge M2A = new BayesianCMEdge("M2A", M2, A);
         M2A.setDecomposition(BayesianEdge.DECOMPOSITION_AND);

         this.addNode(M2);
         this.addEdge(M2A);
         this.enableCM(M2);
         */
        /*
        this.computeLCPD();

        String[] nodesID;
        Boolean[] nodesState;
        Double prTrue;
        Double prFalse;

        try {
            nodesID = new String[]{"B", "C", "M0"};
            nodesState = new Boolean[]{false, false, true};
            prTrue = 0.0;
            prFalse = 1.0;
            this.LCPD_SQL_updatePr(nodesID, nodesState, "A", prTrue, prFalse);

            nodesID = new String[]{"B", "C", "M0"};
            nodesState = new Boolean[]{true, false, true};
            prTrue = 0.65;
            prFalse = 0.35;
            this.LCPD_SQL_updatePr(nodesID, nodesState, "A", prTrue, prFalse);

            nodesID = new String[]{"B", "C", "M0"};
            nodesState = new Boolean[]{false, true, true};
            prTrue = 0.75;
            prFalse = 0.25;
            this.LCPD_SQL_updatePr(nodesID, nodesState, "A", prTrue, prFalse);

            nodesID = new String[]{"B", "C", "M0"};
            nodesState = new Boolean[]{true, true, true};
            prTrue = 0.5;
            prFalse = 0.5;
            this.LCPD_SQL_updatePr(nodesID, nodesState, "A", prTrue, prFalse);
        } catch (SQLException ex) {
            Logger.getLogger(BayesianAttackGraph.class.getName()).log(Level.SEVERE, null, ex);
        }
        /*
         try {
         nodesID = new String[]{"B", "C", "M1"};
         nodesState = new Boolean[]{false, false, true};
         prTrue = 0.0;
         prFalse = 1.0;
         this.LCPD_SQL_updatePr(nodesID, nodesState, "A", prTrue, prFalse);

         nodesID = new String[]{"B", "C", "M1"};
         nodesState = new Boolean[]{true, false, true};
         prTrue = 0.65;
         prFalse = 0.35;
         this.LCPD_SQL_updatePr(nodesID, nodesState, "A", prTrue, prFalse);

         nodesID = new String[]{"B", "C", "M1"};
         nodesState = new Boolean[]{false, true, true};
         prTrue = 0.75;
         prFalse = 0.25;
         this.LCPD_SQL_updatePr(nodesID, nodesState, "A", prTrue, prFalse);

         nodesID = new String[]{"B", "C", "M1"};
         nodesState = new Boolean[]{true, true, true};
         prTrue = 0.5;
         prFalse = 0.5;
         this.LCPD_SQL_updatePr(nodesID, nodesState, "A", prTrue, prFalse);
         } catch (SQLException ex) {
         Logger.getLogger(BayesianAttackGraph.class.getName()).log(Level.SEVERE, null, ex);
         }

         try {
         nodesID = new String[]{"B", "C", "M2"};
         nodesState = new Boolean[]{false, false, true};
         prTrue = 0.0;
         prFalse = 1.0;
         this.LCPD_SQL_updatePr(nodesID, nodesState, "A", prTrue, prFalse);

         nodesID = new String[]{"B", "C", "M2"};
         nodesState = new Boolean[]{true, false, true};
         prTrue = 0.65;
         prFalse = 0.35;
         this.LCPD_SQL_updatePr(nodesID, nodesState, "A", prTrue, prFalse);

         nodesID = new String[]{"B", "C", "M2"};
         nodesState = new Boolean[]{false, true, true};
         prTrue = 0.75;
         prFalse = 0.25;
         this.LCPD_SQL_updatePr(nodesID, nodesState, "A", prTrue, prFalse);

         nodesID = new String[]{"B", "C", "M2"};
         nodesState = new Boolean[]{true, true, true};
         prTrue = 0.5;
         prFalse = 0.5;
         this.LCPD_SQL_updatePr(nodesID, nodesState, "A", prTrue, prFalse);
         } catch (SQLException ex) {
         Logger.getLogger(BayesianAttackGraph.class.getName()).log(Level.SEVERE, null, ex);
         }

         try {
         this.computeUnconditionalProbability(true);
         } catch (SQLException ex) {
         Logger.getLogger(BayesianAttackGraph.class.getName()).log(Level.SEVERE, null, ex);
         }
         */
        log("End of initialization\n");
    }

    public static void main(String[] args) {
        BayesianAttackGraph b = new BayesianAttackGraph();
        b.init(null);
    }

    @Override
    public List<Class<? extends Event>> getRecognizedEvent() {
        List<Class<? extends Event>> result = new LinkedList<>();
        result.add(DummyEvent.class);

        return result;
    }

    @Override
    public List<FeaturesEnum> getEnabledFeatures() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Solution react(Event e) {
        if (!getRecognizedEvent().contains(e.getClass())) {
            throw new UnsupportedOperationException("Cannot react to Event " + e);
        }

        // TODO: create a switch with all the supported operation
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public RiskScale getOverallRisk() {
        // higher risk in target node
        Double maxRisk = Double.NEGATIVE_INFINITY;

        for (BayesianNode t : getTarget()) {
            if (t.getUnconditionalPr() > maxRisk) {
                maxRisk = t.getUnconditionalPr();
            }
        }

        if (maxRisk <= 0.33) {
            return RiskScale.LOW;
        } else if (maxRisk <= 0.66) {
            return RiskScale.MEDIUM;
        } else {
            return RiskScale.HIGH;
        }
    }

    @Override
    public void loadState(DBManager database) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public final void log(String txt) {
        if (this.parent != null) {
            this.parent.log(txt, this);
        } else {
            System.out.println(txt);
        }
    }

    @Override
    public String toString() {
        return "BayesianAG";
    }

    @Override
    public String getPaperName() {
        return "Dynamic Security Risk Management Using Bayesian Attack Graph";
    }

    @Override
    public String getPaperAuthors() {
        return "Poolsappasit, Dewri, Ray";
    }

    @Override
    public URI getPaperDOI() {
        try {
            return new URI("http://dx.doi.org/10.1109/TDSC.2011.34");
        } catch (URISyntaxException ex) {
            Logger.getLogger(DecisionInterfaceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public boolean isStopped() {
        return stop;
    }

    @Override
    public void stop() {
        this.log("Stop requested.\n");
        this.stop = true;
    }

    @Override
    public void run() {
        this.stop = false;
        this.log("Started.\n");
        if(parent!=null)
            parent.showGraph();
        try {
            while (!this.isStopped()) {
                Thread.sleep(1000);
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(EventStream.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.log("Stopped.\n");
    }

    /*
     * START PAPER IMPLEMENTATION 
     */
    //========================================================================
    // STRUCTURAL PROP.
    //========================================================================    
    private Set<BayesianNode> BAG = new HashSet<>();
    private Set<BayesianNode> cachedInternals = new HashSet<>();
    private Set<BayesianNode> cachedExternals = new HashSet<>();
    private Set<BayesianNode> cachedTerminals = new HashSet<>();

    @Test
    public void assertBAGsubStructure() {
        //FIXME: Add test: BAG = internals U externals U terminals
        //TestCase.assertEquals(BAG, getInternals());
    }

    /**
     * Return a set of states for which, for all state 's' exists two nodes 'a1'
     * and 'a2' and s=pre(a1) and s=post(a2);
     *
     * @return All states with both in and out edges.
     */
    private Set<BayesianNode> getInternals() {
        if (!this.cachedInternals.isEmpty()) {
            return this.cachedInternals;
        }
        Set<BayesianNode> internals = new HashSet<>();
        BAG.stream().filter((tmp) -> (tmp.isInternal())).forEach((tmp) -> {
            internals.add(tmp);
        });
        this.cachedInternals = internals;
        return internals;
    }

    /**
     * Return a set of states for which, for all state 's' exist one node 'a'
     * and s=post(a).
     *
     * @return All states without ingoing edges.
     */
    private Set<BayesianNode> getExternals() {
        if (!this.cachedExternals.isEmpty()) {
            return this.cachedExternals;
        }
        Set<BayesianNode> externals = new HashSet<>();
        BAG.stream().filter((tmp) -> (tmp.isExternal())).forEach((tmp) -> {
            externals.add(tmp);
        });
        this.cachedExternals = externals;
        return externals;
    }

    /**
     * Return a set of states for which, for all state 's' exist one node 'a'
     * and s=pre(a).
     *
     * @return All states without outgoing edges.
     */
    private Set<BayesianNode> getTerminals() {
        if (!this.cachedTerminals.isEmpty()) {
            return this.cachedTerminals;
        }
        Set<BayesianNode> terminals = new HashSet<>();
        BAG.stream().filter((tmp) -> (tmp.isTerminal())).forEach((tmp) -> {
            terminals.add(tmp);
        });
        this.cachedTerminals = terminals;
        return terminals;
    }

    /**
     * Simply rename the getExternals function to match the synonym used in the
     * paper.
     *
     * @See getExternals
     * @return getExternals()
     */
    public Set<BayesianNode> getThreatSources() {
        return getExternals();
    }

    public Set<BayesianNode> getTarget() {
        return null;
        // FIXME: how to define target node?
    }

    /**
     * Bayes theorem can only be applied to acyclic graphs. Without doubt, the
     * cycle constitutes a new attack scenario, but from the value gained
     * perspective, such cycles can be disregarded using the monotonicity
     * constraint. See 4.3
     *
     * @return
     */
    private boolean isAcyclic() {
        throw new UnsupportedOperationException("FIXME");
        // try to compute topological sort, if can't is cyclic
        /*
         L ← Empty list where we put the sorted elements
         Q ← Set of all nodes with no incoming edges
         while Q is non-empty do
         remove a node n from Q
         insert n into L
         for each node m with an edge e from n to m do
         remove edge e from the graph
         if m has no other incoming edges then
         insert m into Q
         if graph has edges then
         output error message (graph has a cycle)
         else 
         output message (proposed topologically sorted order: L)
         */
    }

    //========================================================================
    // PROBABILITIES
    //========================================================================    
    private Map<BayesianNode, Double> priorPrMap;

    public void setPriorPrMap(Map<BayesianNode, Double> map) {
        this.priorPrMap = map;
    }

    public Map<BayesianNode, Double> getPriorPrMap() {
        return this.priorPrMap;
    }

    //==== PRIOR =============================================================
    public void setPriorPr() {
        BAG.stream().filter((n) -> (priorPrMap.containsKey(n))).forEach((n) -> {
            n.setPriorPr(priorPrMap.get(n));
            log("Added PriorPr to " + n + "\n");
        });
    }

    //==== LCPD ==============================================================
    private boolean lcpdComputed = false;

    /**
     * See section 4 in the paper: This function compute the LCPD for each
     * state.
     */
    public void computeLCPD() {
        // The LCPD table at this stage require only INSERT
        // Each row does NOT depend on the others
        for (BayesianNode n : this.BAG) {
            Double pr;

            if (n.isExternal()) {
                pr = n.getPriorPr();
                try {
                    this.LCPD_SQL_addRow(new String[0], new Boolean[0], n.getID(), pr, 1 - pr);
                } catch (SQLException ex) {
                    Logger.getLogger(BayesianAttackGraph.class.getName()).log(Level.SEVERE, null, ex);
                }
                continue;
            }

            Edge[] parentsEdges = new Edge[n.getIn().size()];
            parentsEdges = n.getIn().toArray(parentsEdges);

            String[] parentID = new String[parentsEdges.length];
            for (int i = 0; i < parentsEdges.length; i++) {
                parentID[i] = parentsEdges[i].getFrom().getID();
            }

            Boolean[] parentStates = new Boolean[parentID.length];

            // Generate all possible combinations
            for (int i = 0; i < Math.pow(2, parentID.length); i++) {
                StringBuilder binary = new StringBuilder(Integer.toBinaryString(i));
                for (int j = binary.length(); j < parentID.length; j++) {
                    binary.insert(0, '0');
                }

                for (int pos = 0; pos < parentID.length; pos++) {
                    parentStates[pos] = binary.charAt(pos) == '1';
                }

                if (n.getParentDecomposition() == BayesianEdge.DECOMPOSITION_AND) {
                    pr = 1d; // mult. neutro
                    for (int p = 0; p < parentID.length; p++) {
                        if (parentStates[p]) {
                            pr *= ((BayesianEdge) parentsEdges[p]).getPrActivable();
                        } else {
                            pr = 0d;
                            break;
                        }
                    }
                } else {
                    pr = 0d; // sum neutro
                    for (int p = 0; p < parentID.length; p++) {
                        if (parentStates[p]) {
                            pr += ((BayesianEdge) parentsEdges[p]).getPrActivable();
                        }
                    }
                    if (pr > 1d) {
                        pr = 1d;
                    }
                }

                try {
                    this.LCPD_SQL_addRow(parentID, parentStates, n.getID(), pr, 1 - pr);
                } catch (SQLException ex) {
                    Logger.getLogger(BayesianAttackGraph.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
        this.log("All LCPD's tables computed\n");
        this.lcpdComputed = true;
    }

    public boolean isLCPDComputed() {
        return this.lcpdComputed;
    }

    public void resetLCPD() {
        try {
            this.emptyDB();
        } catch (SQLException ex) {
            Logger.getLogger(BayesianAttackGraph.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.lcpdComputed = false;
        resetUnconditionalPr();
        resetPosteriorPr();
        log("All LCPD's tables were cleared\n");
    }

    public TableModel getJTableModel(Node filter) {
        try {
            Connection conn = DB.getConnection();

            DefaultTableModel result = new DefaultTableModel();

            String query = "SELECT * FROM lcpd";

            if (filter != null) {
                query += " WHERE " + filter.getID() + " = 'M';";
            } else {
                query += ";";
            }

            ResultSet rs = conn.createStatement().executeQuery(query);
            ResultSetMetaData metaData = rs.getMetaData();

            // Names of columns
            Vector<String> columnNames = new Vector<>();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                if (metaData.getColumnName(i).equalsIgnoreCase("ID")) { // SKIP ID COLUMN
                    continue;
                }

                if (filter != null) {
                    boolean pr = metaData.getColumnName(i).equalsIgnoreCase("prTrue") || metaData.getColumnName(i).equalsIgnoreCase("prFalse");
                    boolean is_a_parent = filter.getParentsIDs().contains(metaData.getColumnName(i));
                    boolean is_me = metaData.getColumnName(i).equals(filter.getID());
                    if (pr || is_a_parent || is_me) {
                        columnNames.add(metaData.getColumnName(i));
                    }
                } else {
                    columnNames.add(metaData.getColumnName(i));
                }
            }

            // Data of the table
            Vector<Vector<Object>> data = new Vector<>();
            while (rs.next()) {
                Vector<Object> vector = new Vector<>();
                for (String colName : columnNames) {
                    Object tmp = rs.getObject(colName);
                    if (tmp != null) {
                        if (tmp.toString().equals("1")) {
                            tmp = "True";
                        } else if (tmp.toString().equals("0")) {
                            tmp = "False";
                        }
                    }
                    vector.add(tmp);
                }
                data.add(vector);
            }

            result.setDataVector(data, columnNames);

            return result;
        } catch (SQLException ex) {
            Logger.getLogger(BayesianAttackGraph.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, ""
                    + "There was an error while loading the table data\n"
                    + "\nSQL Error Code: " + ex.getSQLState() + "\n"
                    + "Exception:\n" + ex.toString() + "\n", "SQL ERROR", JOptionPane.ERROR_MESSAGE);
        }
        return new DefaultTableModel();
    }

    //==== UNCONDITIONAL ======================================================
    private boolean unconditionalComputed = false;

    /**
     * Compute posterior probability for the node target.<br>
     * Suppose a set of 4 nodes: {A,B,C,D}. Edges are: {D->C},{D->B},
     * {B->A},{C->A}.<br>
     * UncdProb(A,B,C,D) = Pr(A|Pa(A)) x Pr(B|Pa(B)) x Pr(C|Pa(C)) x Pr(D|Pa(D))
     *
     * @param forceRecompute
     * @return
     * @throws java.sql.SQLException
     * @see Section 4.3
     * @param target Node A in the example.
     */
    public Double computeUnconditionalProbability(BayesianNode target, boolean forceRecompute) throws SQLException {

        if (!target.getUnconditionalPr().equals(Double.NaN) && !forceRecompute) {
            this.log("PrUnconditional(" + target.getID() + ") = " + target.getUnconditionalPr() + " CACHED\n");
            return target.getUnconditionalPr();
        }

        String debug = "PrUnconditional(" + target.getID();

        if (target.isExternal()) {
            debug += ")";
            //this.log(debug+"\n");
            //this.log("| Pr(" + target.getID() + "=true) = " + String.format("%.2f", target.getPriorPr()) + " PRIOR\n"); //COMMENT FOR INLINE
            //this.log("| SUM += 0,0 -> " + String.format("%.2f", target.getPriorPr())+"\n"); //COMMENT FOR INLINE
            //this.log("|-------------------------\nResult\n"); //COMMENT FOR INLINE
            this.log(debug + " = " + String.format("%.2f", target.getPriorPr()) + "\n");
            target.setUnconditionalPr(target.getPriorPr());
            return target.getPriorPr();
        }

        //debug += "|"; //COMMENT FOR INLINE
        Set<BayesianNode> ancestor = target.getAllAncestor();
        List<String> ancestorID = new LinkedList<>();
        List<Boolean> ancestorStates = new LinkedList<>();

        for (Iterator<BayesianNode> it = ancestor.iterator(); it.hasNext();) {
            String tmp = it.next().getID();
            ancestorID.add(tmp);
            //debug += tmp + " "; //COMMENT FOR INLINE
        }
        //debug = debug.trim(); //COMMENT FOR INLINE
        debug += ")"
                //+ "\n" //COMMENT FOR INLINE
                + "";
        //this.log(debug+"\n");

        Double sum_result = 0d;

        // Generate all possible combinations of parents states
        for (int i = 0; i < Math.pow(2, ancestorID.size()); i++) {
            ancestorStates.clear();

            //this.log("|-------------------------\n");
            //debug = "| Pr(" + target.getID() + "|"; //COMMENT FOR INLINE
            StringBuilder binary = new StringBuilder(Integer.toBinaryString(i));
            for (int j = binary.length(); j < ancestorID.size(); j++) {
                binary.insert(0, '0');
            }

            for (int pos = 0; pos < ancestorID.size(); pos++) {
                ancestorStates.add(binary.charAt(pos) == '1');
                //debug += ancestorID.get(pos) + "=" + ancestorStates.get(pos) + " "; //COMMENT FOR INLINE
            }

            //debug = debug.trim() + ")"; //COMMENT FOR INLINE
            //this.log(debug+"\n"); //COMMENT FOR INLINE
            Double partialPR = 1d;

            // TARGET NODE
            List<String> parentIDs = new LinkedList<>(target.getParentsIDs());
            boolean[] parentStates = new boolean[parentIDs.size()];

            for (int kk = 0; kk < parentIDs.size(); kk++) {
                parentStates[kk] = ancestorStates.get(ancestorID.indexOf(parentIDs.get(kk)));
            }

            Double tmp_pr = LCPD_SQL_searchRecord(target.getID(), parentIDs.toArray(new String[parentIDs.size()]), parentStates, true);
            partialPR *= tmp_pr;

            // END TARGET NODE
            if (partialPR == 0) {
                //this.log(debug + " == 0 -> SKIP"); //COMMENT FOR INLINE
                //this.log("| SUM += 0,0 -> " + String.format("%.2f", sum_result)); //COMMENT FOR INLINE
                continue;
            } else {
                //this.log(debug + " = " + String.format("%.2f", tmp_pr)); //COMMENT FOR INLINE
            }

            // ALL ANCESTORS
            for (Iterator<BayesianNode> itt = target.getAllAncestor().iterator(); itt.hasNext();) {
                //debug = "|"; //COMMENT FOR INLINE
                BayesianNode p = itt.next();

                boolean pState = ancestorStates.get(ancestorID.indexOf(p.getID()));

                if (p.isExternal()) {
                    tmp_pr = pState ? p.getPriorPr() : (1 - p.getPriorPr());
                    partialPR *= tmp_pr;
                    //debug += " Pr(" + p.getID() + "=" + pState + ") = " + String.format("%.2f", tmp_pr) + " PRIOR"; //COMMENT FOR INLINE
                    //this.log(debug); //COMMENT FOR INLINE
                    continue;
                }

                //debug += " Pr(" + p.getID() + "=" + pState + "|"; //COMMENT FOR INLINE
                parentIDs = new LinkedList<>(p.getParentsIDs());
                parentStates = new boolean[parentIDs.size()];

                for (int kk = 0; kk < parentIDs.size(); kk++) {
                    parentStates[kk] = ancestorStates.get(ancestorID.indexOf(parentIDs.get(kk)));
                    //debug += parentIDs.get(kk) + "=" + parentStates[kk] + " "; //COMMENT FOR INLINE
                }

                tmp_pr = LCPD_SQL_searchRecord(p.getID(), parentIDs.toArray(new String[parentIDs.size()]), parentStates, pState);
                partialPR *= tmp_pr;

                if (partialPR == 0) {
                    //this.log(debug + ") == 0 -> SKIP"); //COMMENT FOR INLINE
                    partialPR = 0d;
                    break;
                } else {
                    //log(debug + ") = " + String.format("%.2f", tmp_pr)); //COMMENT FOR INLINE
                }
            }
            // END ALL ANCESTORS

            sum_result += partialPR;
            //this.log("| SUM += " + String.format("%.2f", partialPR) + " -> " + String.format("%.2f", sum_result)); //COMMENT FOR INLINE
        }

        target.setUnconditionalPr(sum_result);

        //this.log("|-------------------------"); // COMMENT FOR INLINE
        this.log(debug + ""
                //  "Result" //COMMENT FOR INLINE
                + " = " + String.format("%.2f", sum_result)
                + "\n");

        return sum_result;
    }

    public void computeUnconditionalProbability(boolean forceRecompute) throws SQLException {
        for (BayesianNode n : BAG) {
            computeUnconditionalProbability(n, forceRecompute);
        }
        unconditionalComputed = true;
    }

    public boolean isUnconditionalPrComputed() {
        return this.unconditionalComputed;
    }

    public void resetUnconditionalPr() {
        this.unconditionalComputed = false;
        for (Iterator<BayesianNode> it = BAG.iterator(); it.hasNext();) {
            it.next().setUnconditionalPr(Double.NaN);
        }
        log("All UnconditionalPr were cleared\n");
    }

    //==== POSTERIOR =========================================================
    private Set<BayesianNode> compromisedNodes = new HashSet<>();

    public Set<BayesianNode> getCompromisedNodes() {
        return compromisedNodes;
    }

    public void setCompromisedNodes(Set<BayesianNode> compromisedNodes) {
        this.compromisedNodes = compromisedNodes;
    }

    public void addCompromisedNode(BayesianNode n) {
        n.setState(true);
        n.setPosteriorPr(1d);

        if (this.compromisedNodes == null) {
            this.compromisedNodes = new HashSet<>();
        }

        this.compromisedNodes.add(n);
        this.parent.setGraphLabel(n);
        this.parent.getGraph().setColor(n, java.awt.Color.RED);
    }

    public boolean isCompromised(String ID) {
        try {
            for (Iterator<BayesianNode> it = compromisedNodes.iterator(); it.hasNext();) {
                if (it.next().getID().equals(ID)) {
                    return true;
                }
            }

            return false;
        } catch (NullPointerException e) {
            return false;
        }
    }

    public boolean isCompromised(BayesianNode n) {
        return compromisedNodes.contains(n);
    }

    public void computePosterior(boolean forceRecompute) throws SQLException {
        if (this.compromisedNodes == null || this.compromisedNodes.isEmpty()) {
            JOptionPane.showMessageDialog(null, "The evidence list is empty. Posterior probability cannot be computed", "Evidence List is Empty", JOptionPane.WARNING_MESSAGE);
            return;
        }
        for (BayesianNode n : BAG) {
            computePosterior(n, forceRecompute);
        }
    }

    public Double computePosterior(BayesianNode target, boolean forceRecompute) throws SQLException {

        if (this.compromisedNodes == null || this.compromisedNodes.isEmpty()) {
            JOptionPane.showMessageDialog(null, "The evidence list is empty. Posterior probability cannot be computed", "Evidence List is Empty", JOptionPane.WARNING_MESSAGE);
            return Double.NaN;
        }

        if (this.compromisedNodes.contains(target)) {
            this.log("PrPosterior(" + target.getID() + ") = 1,00 EXPLOIT EVIDENCE\n");
            return target.getPosteriorPr();
        }

        if (!target.getPosteriorPr().equals(Double.NaN) && !forceRecompute) {
            this.log("PrPosterior(" + target.getID() + ") = " + target.getPosteriorPr() + " CACHED\n");
            return target.getPosteriorPr();
        }

        String debug = "PrPosterior(" + target.getID();

        //if (!target.isExternal()) { //COMMENT FOR INLINE
        debug += "|";
        //} //COMMENT FOR INLINE

        Set<BayesianNode> ancestor = target.getAllAncestor();

        for (Iterator<BayesianNode> it = this.compromisedNodes.iterator(); it.hasNext();) {
            debug += it.next().getID() + " ";
        }
        debug = debug.trim() + ")";

        //this.log(debug+"\n"); //COMMENT FOR INLINE
        //debug = "";
        Double posteriorProb = 1d;

// PART 1 -> Pr(E | Sj)   
        //debug = "|Part One: Pr("; //COMMENT FOR INLINE
        //for (Iterator<BayesianNode> it = this.compromisedNodes.iterator(); it.hasNext();) { //COMMENT FOR INLINE
        //    debug += it.next().getID() + " "; //COMMENT FOR INLINE
        //}//COMMENT FOR INLINE
        //this.update(debug.trim() + " | " + target.getID() + ")");//COMMENT FOR INLINE
        for (Iterator<BayesianNode> it = this.compromisedNodes.iterator(); it.hasNext();) {
            Double sum_result = 0d;
            BayesianNode cNode = it.next();
            List<String> cNodeParentsID = new LinkedList<>(cNode.getParentsIDs());
            boolean[] cNodeStates = new boolean[cNodeParentsID.size()];

            //    debug = "|>Pr(" + cNode.getID() + " | " + target.getID() + ") = \n";//COMMENT FOR INLINE
            for (int i = 0; i < Math.pow(2, cNodeParentsID.size()); i++) {
                //cNodeStates.clear();

                //        debug += "|>  Pr(" + cNode.getID() + " | ";//COMMENT FOR INLINE
                StringBuilder binary = new StringBuilder(Integer.toBinaryString(i));
                for (int j = binary.length(); j < cNodeParentsID.size(); j++) {
                    binary.insert(0, '0');
                }

                for (int pos = 0; pos < cNodeParentsID.size(); pos++) {
                    cNodeStates[pos] = binary.charAt(pos) == '1';
                    //            debug += cNodeParentsID.get(pos) + "=" + cNodeStates[pos] + " "; //COMMENT FOR INLINE
                }

                if (cNodeParentsID.contains(target.getID()) && !cNodeStates[cNodeParentsID.indexOf(target.getID())]) {
                    //            debug = debug.trim() + ") SKIP. " + target.getID() + " must be true\n"; //COMMENT FOR INLINE
                    continue;
                }
                //        debug = debug.trim() + ")"; //COMMENT FOR INLINE
                Double cNodePrState = this.LCPD_SQL_searchRecord(cNode.getID(),
                        cNodeParentsID.toArray(new String[cNodeParentsID.size()]),
                        cNodeStates,
                        true);

                for (int p = 0; p < cNodeParentsID.size(); p++) {
                    if (cNodeParentsID.get(p).equals(target.getID())) {
                        continue;
                    }

                    BayesianNode tmp = searchNodeFromID(cNodeParentsID.get(p));
                    Double uncProb = cNodeStates[p] ? computeUnconditionalProbability(tmp, false) : 1 - computeUnconditionalProbability(tmp, false);
                    //            debug += " * PrUnconditional(" + tmp.getID() + ")";//COMMENT FOR INLINE
                    cNodePrState *= uncProb;
                }

                //        debug += " = " + String.format("%.2f", cNodePrState) + "\n";//COMMENT FOR INLINE
                sum_result += cNodePrState;

            }

            //    this.update(debug);//COMMENT FOR INLINE
            posteriorProb *= sum_result;
        }

        //this.update("|Part One = " + String.format("%.2f", posteriorProb)); //COMMENT FOR INLINE
// PART 2 -> PrUnconditional(Sj) / PrUnconditional(E)
        //debug = "|Part Two: Pr(" + target.getID() + ") / Pr("; //COMMENT FOR INLINE
        //for (Iterator<BayesianNode> it = this.compromisedNodes.iterator(); it.hasNext();) { //COMMENT FOR INLINE
        //    debug += it.next().getID() + " "; //COMMENT FOR INLINE
        //}//COMMENT FOR INLINE
        //debug = debug.trim() + ")\n"; //COMMENT FOR INLINE
        Double priorSj = computeUnconditionalProbability(target, false);
        //debug += "|>PrUnconditional(" + target.getID() + ") = " + String.format("%.2f", priorSj); //COMMENT FOR INLINE
        //this.update(debug); //COMMENT FOR INLINE

        //debug = ""; //COMMENT FOR INLINE
        Double priorE = 1d;

        for (Iterator<BayesianNode> cit = compromisedNodes.iterator(); cit.hasNext();) {
            BayesianNode tmp = cit.next();
            Double tmpProb = computeUnconditionalProbability(tmp, false);
            //    debug += "|>PrUnconditional(" + tmp.getID() + ") = " + String.format("%.2f", tmpProb) + "\n"; //COMMENT FOR INLINE
            priorE *= tmpProb;
        }
        //this.update(debug); //COMMENT FOR INLINE

        Double partTwo = priorSj / priorE;
        //debug += "|Part Two = " + String.format("%.2f", partTwo); //COMMENT FOR INLINE

        posteriorProb *= partTwo;

        if (posteriorProb >= 1d) {
            posteriorProb = 1d;
        }

        target.setPosteriorPr(posteriorProb);
        this.log(debug
                + ""
                //        + "Result" //COMMENT FOR INLINE
                + " = " + String.format("%.2f", posteriorProb)
                + "\n");
        return posteriorProb;
    }

    public void resetPosteriorPr() {
        for (Iterator<BayesianNode> it = BAG.iterator(); it.hasNext();) {
            it.next().setPosteriorPr(Double.NaN);
        }
        log("All PosteriorPr were cleared\n");
    }

    @Override
    public JFrame getModelConfigurationFrame() {
        return new BayesianModelConfiguration(parent, this);
    }

    @Override
    public Class<? extends Edge> getEdgesClass() {
        return BayesianEdge.class;
    }

    @Override
    public Class<? extends Node> getNodesClass() {
        return BayesianNode.class;
    }

    @Override
    public void addNode(Node node) {
        log("Add node (ID: " + node.getID() + ", L: " + node.getLabel() + ")\n");
        this.BAG.add((BayesianNode) node);
        try {
            if (node instanceof BayesianCMNode) {
                this.LCPD_SQL_addColumn(node.getID(), "CHAR(1)", false, "0");
                this.securityControls.add((BayesianCMNode) node);

            } else {
                this.LCPD_SQL_addColumn(node.getID(), "CHAR(1)", false, null);
            }
        } catch (SQLException ex) {
            Logger.getLogger(BayesianAttackGraph.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (this.parent != null) {
            this.parent.addNodeToGraph(node);
            if (node instanceof BayesianCMNode) {
                this.parent.getGraph().setColor(node, java.awt.Color.GREEN);
            }
        }
    }

    @Override
    public void addEdge(Edge edge) {
        log("Add edge (ID: " + edge.getID() + ")\n");
        ((BayesianEdge) edge).addThisToNodes();
        if (this.parent != null) {
            this.parent.addEdgeToGraph(edge);
        }
    }

    @Override
    public Collection<? extends Node> getNodes() {
        return this.BAG;
    }

    @Override
    public Collection<? extends Edge> getEdges() {
        Set<Edge> result = new HashSet();
        for (Iterator<BayesianNode> it = BAG.iterator(); it.hasNext();) {
            BayesianNode n = it.next();
            result.addAll(n.getIn());
            result.addAll(n.getOut());
        }

        return result;
    }

    public Collection<BayesianCMNode> getCMNodes() {
        return this.securityControls.getCMS();
    }

    public Collection<? extends Node> getNodesNotCM() {
        Set<? extends Node> result = new HashSet(this.BAG);
        result.removeIf(n -> n.getClass() == BayesianCMNode.class);
        return result;
    }

    public Collection<? extends Edge> getCMEdges() {
        Set<Edge> result = new HashSet();
        for (BayesianCMNode e : this.securityControls.getCMS()) {
            result.addAll(e.getIn());
            result.addAll(e.getOut());
        }
        return result;
    }

    public Collection<BayesianNode> getEvidenceNodes() {
        return this.compromisedNodes;
    }

    @Override
    public void delNode(Node n) {
        log("Delete node (ID: " + n.getID() + ", L: " + n.getLabel() + ")\n");
        if (this.parent != null) {
            this.parent.delNodeFromGraph(n);
        }

        if (n instanceof BayesianCMNode) {
            this.securityControls.del((BayesianCMNode) n);
        }

        for (Iterator<Edge> it = n.getOut().iterator(); it.hasNext();) {
            delEdge(it.next());
        }

        for (Iterator<Edge> it = n.getIn().iterator(); it.hasNext();) {
            delEdge(it.next());
        }

        this.BAG.remove((BayesianNode) n);
        this.compromisedNodes.remove((BayesianNode) n);

        if (this.parent != null) {
            parent.delNodeFromGraph(n);
        }

        // FIXME: deal with LCPD table
    }

    @Override
    public void delEdge(Edge e) {

        log("Delete edge (ID: " + e.getID() + ")\n");
        Node from = e.getFrom();
        Node to = e.getTo();

        from.getOut().remove(e);
        to.getIn().remove(e);

        if (to.isExternal()) {
            BayesianJDialogNode x = new BayesianJDialogNode(null, true);
            x.overrideData((BayesianNode) to);
            x.setDisableClose(true);
            x.setPriorMandatory(true);
            x.setVisible(true);
        }

        if (this.parent != null) {
            parent.delEdgeFromGraph(e);
            parent.repaintGraph();
        }
    }

    public BayesianNode searchNodeFromID(String id) {
        for (BayesianNode n : this.BAG) {
            if (n.getID().equals(id)) {
                return n;
            }
        }

        return null;
    }

    //========================================================================
    // COUNTERMEASURE
    //========================================================================    
    /**
     * Security controls map the "Security Mitigation Plan" defined by def 7.
     *
     */
    BayesianCountermeasureSet securityControls = new BayesianCountermeasureSet();

    public void enableCM(BayesianCMNode n) {
        securityControls.enable(n);

        try {
            LCPD_SQL_updatePr(null, null, n.getID(), 1d, 0d);
        } catch (SQLException ex) {
            Logger.getLogger(BayesianAttackGraph.class.getName()).log(Level.SEVERE, null, ex);
            log("Error while updating LCPD table during CM activation");
        }
    }

    public void disableCM(BayesianCMNode n) {
        securityControls.disable(n);

        try {
            LCPD_SQL_updatePr(null, null, n.getID(), 0d, 1d);
        } catch (SQLException ex) {
            Logger.getLogger(BayesianAttackGraph.class.getName()).log(Level.SEVERE, null, ex);
            log("Error while updating LCPD table during CM deactivation");
        }
    }

    /**
     * Total implementation cost, refer to paragraph 5.1 in the paper. Made by
     * the combination of all different costs and their effective application.
     *
     * @return Total cost of the implemented plan
     */
    public Double getOverallMitigationPlanCost() {
        Double result = 0d;
        //System.out.println("Overall Mitigation Plan Cost:");
        for (BayesianCMNode cm : this.securityControls.getCMS()) {
            result += cm.isEnabled() ? cm.getCountermeasure().getCost() : 0;
            //System.out.println("  (" + cm.getID() + ":" + cm.getState() + ")\t" + (cm.getState() ? String.format("%+.2f", cm.getCountermeasure().getCost()) : 0) + " = " + String.format("%+.2f", result) + "");
        }
        return result;
    }

    /**
     * The expected loss/gain w.r.t. the security plan is implemented as the
     * cumulative sum of all nodes expectedLossGain. A positive values signifies
     * a gain, while a negative signifies a loss.
     *
     * @return Sum of all nodes expectedLossGain.
     */
    public Double getCumulativeLossGain() {
        Double result = 0d;
        System.out.println("Cumulative LossGain:");
        for (BayesianNode n : this.BAG) {
            result += n.getExpectedLossGain();
            System.out.println("  (" + n.getID() + ")\t" + String.format("%+.2f", n.getExpectedLossGain()) + " = " + String.format("%+.2f", result) + "");
        }

        return result;
    }

    private Double expectedLossWeight = Double.NaN;
    private Double expectedGainWeight = Double.NaN;

    /**
     * Return the alpha-modifier for the LG function.
     *
     * @return the alpha-modifier for the LG function.
     */
    public Double getExpectedLossWeight() {
        return expectedLossWeight;
    }

    /**
     * This method will change both loss and gain params. GainWeight = 1 -
     * LossWeight;
     *
     * @param expectedLossWeight alpha-modifier for LG
     */
    public void setExpectedLossWeight(Double expectedLossWeight) {
        this.expectedLossWeight = expectedLossWeight;
        this.expectedGainWeight = 1 - expectedLossWeight;
    }

    /**
     * Return the beta-modifier for the SCC function.
     *
     * @return the beta-modifier for the SCC function.
     */
    public Double getExpectedGainWeight() {
        return expectedGainWeight;
    }

    /**
     * This method will change both loss and gain params. LossWeight = 1 -
     * GainWeight;
     *
     * @param expectedGainWeight beta-modifier for SCC
     */
    public void setExpectedGainWeight(Double expectedGainWeight) {
        this.expectedLossWeight = 1 - expectedGainWeight;
        this.expectedGainWeight = expectedGainWeight;
    }

    public Double objectiveFunction() throws NumberFormatException {

        if (this.getExpectedLossWeight().equals(Double.NaN)) {
            throw new NumberFormatException("You must set a weight for the Expected Loss parameter");
        }
        if (this.getExpectedGainWeight().equals(Double.NaN)) {
            throw new NumberFormatException("You must set a weight for the Expected Gain parameter");
        }

        Double p1 = this.getExpectedLossWeight();
        Double p2 = this.getCumulativeLossGain();
        Double p3 = this.getExpectedGainWeight();
        Double p4 = this.getOverallMitigationPlanCost();

        System.out.println("Objective Function:");
        System.out.println(" - CM: " + this.securityControls.toString());
        System.out.println(" - a:\t" + String.format("%+.2f", p1).replaceAll(",", ".") + "\t(Loss Multiplier)");
        System.out.println(" - LG:\t" + String.format("%+.2f", p2).replaceAll(",", ".") + "\t(Cumulative LossGain)");
        System.out.println(" - b:\t" + String.format("%+.2f", p3).replaceAll(",", ".") + "\t(Gain Multiplier)");
        System.out.println(" - SCC: " + String.format("%+.2f", p4).replaceAll(",", ".") + "\t(Overall Mitigation Cost)");
        System.out.println("a*LG - b*SCC = " + String.format("%+.2f", p1 * p2 - p3 * p4).replaceAll(",", "."));
        return p1 * p2 - p3 * p4;
    }

    public void SOOP(int poolSize, int crossoverIteration, int randomMutation, double poolDecreaseRatio) {
        log("Start SOOP analysis\n");
        List<Map<BayesianCMNode, Boolean>> pool = new LinkedList<>();
        Random randGen = new Random();

        if (securityControls.getCMS().isEmpty()) {
            log("Empty CMs list\n");
            return;
        }

        // CREATE POPULATION
        log("Step 0 - Create Population\n");
        while (pool.size() < poolSize) {
            try {
                Map<BayesianCMNode, Boolean> T1 = randomizeSecurityControlStatus(randGen);
                Map<BayesianCMNode, Boolean> T2;
                do {
                    T2 = randomizeSecurityControlStatus(randGen);
                } while (T1.equals(T2));

                applyMap(T1);
                Double T1_obj = objectiveFunction();
                applyMap(T2);
                Double T2_obj = objectiveFunction();

                if (T1_obj >= T2_obj) {
                    log("- Add " + T1 + "\n");
                    pool.add(T1);
                    System.out.println("ObjF: " + (String.format("%+.2f", T1_obj).replaceAll(",", ".")) + " " + this.securityControls.toString());
                } else {
                    log("- Add " + T2 + "\n");
                    pool.add(T2);
                    System.out.println("ObjF: " + (String.format("%+.2f", T2_obj).replaceAll(",", ".")) + " " + this.securityControls.toString());
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Exception while computing Objective Function", JOptionPane.ERROR_MESSAGE);
                Logger.getLogger(BayesianAttackGraph.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
        }

        log("Step 1 - Iteration\n");
        while (pool.size() > 1) {
            //this.update("\nNEW ROUND");
            //printPool(pool);
            poolSize = pool.size();

            // CROSSOVER
            log("\tStep 1.1 - Crossover (" + crossoverIteration + ", " + (this.securityControls.getCMS().size() * 2 / 3) + ")\n");
            crossOver(pool, crossoverIteration, this.securityControls.getCMS().size() * 2 / 3, randGen);
            //printPool(pool);

            // RANDOM MUTATION
            log("\tStep 1.2 - Mutation (" + randomMutation + ")\n");
            mutation(pool, randomMutation, randGen);
            //printPool(pool);

            log("\tStep 1.3 - New Population\n");

            while (pool.size() > (poolSize * poolDecreaseRatio) && pool.size() >= 2) {
                try {
                    Map<BayesianCMNode, Boolean> T1 = pool.remove(randGen.nextInt(pool.size()));
                    Map<BayesianCMNode, Boolean> T2 = pool.remove(randGen.nextInt(pool.size()));

                    applyMap(T1);
                    Double T1_obj = objectiveFunction();
                    applyMap(T2);
                    Double T2_obj = objectiveFunction();

                    if (T1_obj >= T2_obj) {
                        log("\t- Add " + T1 + "\n");
                        pool.add(T1);
                        System.out.println("ObjF: " + (String.format("%+.2f", T1_obj).replaceAll(",", ".")) + " " + this.securityControls.toString());
                    } else {
                        log("\t- Add " + T1 + "\n");
                        pool.add(T2);
                        System.out.println("ObjF: " + (String.format("%+.2f", T2_obj).replaceAll(",", ".")) + " " + this.securityControls.toString());
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Exception while computing Objective Function", JOptionPane.ERROR_MESSAGE);
                    Logger.getLogger(BayesianAttackGraph.class.getName()).log(Level.SEVERE, null, ex);
                    return;
                }
            }
        }

        //this.update("\nEND");
        //printPool(pool);
        applyMap(pool.get(0));

        log("Choosen: " + this.securityControls + "\n");
    }

    // croosoverStart -> random
    private void crossOver(List<Map<BayesianCMNode, Boolean>> pool, int crossoverIteration, int crossoverStart, Random randGen) {
        for (int i = 0; i < crossoverIteration; ++i) {

            int first = randGen.nextInt(pool.size());
            int second;
            do {
                second = randGen.nextInt(pool.size());
            } while (second == first);

            Map<BayesianCMNode, Boolean> T1 = pool.get(first);
            Map<BayesianCMNode, Boolean> T2 = pool.get(second);

            int counter = 0;
            for (Iterator<BayesianCMNode> it = T1.keySet().iterator(); it.hasNext();) {
                if (counter++ < crossoverStart) {
                    it.next();
                } else {
                    BayesianCMNode tmp = it.next();
                    Boolean st = T1.get(tmp);

                    T1.put(tmp, T2.get(tmp));
                    T2.put(tmp, st);
                }
            }

            pool.set(first, T1);
            pool.set(second, T2);
        }
    }

    // 
    private void mutation(List<Map<BayesianCMNode, Boolean>> pool, int randomMutation, Random randGen) {
        for (int i = 0; i < randomMutation; ++i) {

            int choosen = randGen.nextInt(pool.size());
            Map<BayesianCMNode, Boolean> T1 = pool.get(choosen);

            int toBeChangedBool = randGen.nextInt(T1.size());

            T1.put(T1.keySet().iterator().next(), randGen.nextBoolean());

            pool.set(choosen, T1);
        }
    }

    public Map<BayesianCMNode, Boolean> randomizeSecurityControlStatus(Random r) {
        Map<BayesianCMNode, Boolean> result = new HashMap<>(this.securityControls.getCMS().size());

        for (int i = 0; i < securityControls.getCMS().size(); ++i) {
            result.put(securityControls.getCMS().get(i), r.nextBoolean());
        }

        return result;
    }

    private void applyMap(Map<BayesianCMNode, Boolean> T) {
        for (BayesianCMNode n : this.securityControls.getCMS()) {
            if (T.get(n)) {
                enableCM(n);
            } else {
                disableCM(n);
            }
        }
    }

    private void printPool(List<Map<BayesianCMNode, Boolean>> pool) {
        String result = "";
        for (Map<BayesianCMNode, Boolean> map : pool) {
            result += "[";
            for (Iterator<BayesianCMNode> it = map.keySet().iterator(); it.hasNext();) {
                BayesianCMNode n = it.next();
                result += "(" + n.getID() + ": " + map.get(n) + ") ";
            }
            result = result.trim();
            result += "]\n";
        }
        System.out.println(result + "---------------------------------------");
    }

}
