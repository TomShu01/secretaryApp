
package secretaryapp;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Locale;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import javax.swing.DefaultListModel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class Secretary extends javax.swing.JFrame {
    
    private final File path1 = new File("TaskList.txt");
    private final File path2 = new File("report.txt");
    private LocalDateTime startTask;//keeps track of the start of a task. it doesn't belong to any specific task, it's updated as soon as you finish a task
    
    //datasets for stats
    private HistogramDataset TimeDistribution = new HistogramDataset();
    private XYSeriesCollection TimeEfficiency = new XYSeriesCollection();
    private HistogramDataset fatigueLevelDistribution = new HistogramDataset();
    private XYSeriesCollection fatigueLevelEfficiency = new XYSeriesCollection();
    private HistogramDataset timeBudgetDistribution = new HistogramDataset();
    private XYSeriesCollection timeBudgetEfficiency = new XYSeriesCollection();
    private DefaultListModel subtasklist = new DefaultListModel ();
    private DefaultListModel resource = new DefaultListModel ();
    
    //taskList needs to be in the same order all the time, because availableTasks and currentTasks store indexes on taskList    
    //I didn't create a separate class for tasks but used an ArrayList because I can print items using a for loop
    private ArrayList<ArrayList> taskList = new ArrayList();//row, column; 9 columns
    private ArrayList<ArrayList> finishedTask = new ArrayList();//row, column; 10 columns
    private ArrayList<Integer> availableTasks = new ArrayList();
    private ArrayList<Integer> currentTasks = new ArrayList();
    
    public Secretary() throws IOException, FileNotFoundException, ParseException {
        System.out.println ("initializing");
        initComponents();
        initComponents2();
        updateStats ();
        System.out.println ("initialization complete - welcome to secretaryApp");
    }
    
    //takes an ArrayList (currentTasks or availableTasks) of indexes on taskList and displays the task name of the indexes onto the TaskList jList or CurrentWorks jList
    private void changeList (ArrayList<Integer> a){
        DefaultListModel lm1 = new DefaultListModel();
        if (availableTasks == a) {
            TaskList.setModel(new DefaultListModel());
            lm1=(DefaultListModel) TaskList.getModel();
            TaskList.removeAll();
        } else {
            CurrentWorks.setModel(new DefaultListModel());
            lm1=(DefaultListModel) CurrentWorks.getModel();
            CurrentWorks.removeAll();
        }
        ArrayList<Integer> j = a;
        for (int k=0; k<j.size();k++){
            //System.out.println(taskList.size());
            //System.out.println(availableTasks);
            lm1.addElement((String)taskList.get(j.get(k)).get(0));
        }
    }
    
    //displays the task name of all the indexes in currentTasks onto the tree jTree
    private void displayExecutingTasks () {
        DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
        DefaultMutableTreeNode [] nodes = new DefaultMutableTreeNode [currentTasks.size()];
        ArrayList count;
        
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();

        root.removeAllChildren();
        for (int k = 0; k < currentTasks.size();k++) {
            count = (ArrayList)taskList.get(currentTasks.get(k));
            nodes [k] = new DefaultMutableTreeNode(count.get(0));
            root.add(nodes[k]);
            ArrayList<String> subtasks = (ArrayList<String>)count.get(1);
            for (int c = 0; c < subtasks.size();c++){
                DefaultMutableTreeNode subnode = new DefaultMutableTreeNode(subtasks.get(c));
                DefaultMutableTreeNode child = (DefaultMutableTreeNode) model.getChild(root,k);
                child.add(subnode);
            }
        }
        tree.updateUI();
    }
    
    //*takes the characteristics of a task on the first tab, summarizes them into an ArrayList - task.
    //tasks is then added to both taskList and availableTasks.
    //changes in availableTasks will be displayed by TaskList jList on second tab
    //achieves one of the four key functions of this project - take in the details of the task
    private void addTask () {
        String name = taskName.getText();
        ArrayList<String> subTasks = new ArrayList ();
        for (int k = 0; k < subTaskList.getModel().getSize(); k++) {
            subTasks.add("-" + subTaskList.getModel().getElementAt(k));
        }
        String type = String.valueOf(taskType.getSelectedItem());
        String subject = String.valueOf(fixedCost.getSelectedItem());
        double timeBudget = (int)hours.getValue()*60 + (int)mins.getValue();//in mins
        double utility = utilitySlider.getValue();
        double fatigueLevel = fatigueSlider.getValue();
        Boolean dl = deadline.isSelected();
        Date deadline2 = new Date();
        if (dl) {
            deadline2 = (Date)DeadlineSpinner.getValue();
        }
        ArrayList<String> resources = new ArrayList ();
        for (int k = 0; k < resourceList.getModel().getSize(); k++) {
            resources.add(">" + resourceList.getModel().getElementAt(k));//I used ">" because I assume that no url or file path will have > in there
        }//achieves one of the four key functions of this project - you find resources and put them into here. Next time, with just one click, you can open up all the things you need to keep working
        ArrayList<Object> obj = new ArrayList(List.of(name, subTasks, type, subject, timeBudget,utility, fatigueLevel, dl, deadline2, resources));
        setRecordTaskDefault();
        
        taskList.add (obj);
        availableTasks.add(taskList.size()-1);
        changeList (availableTasks);
    }
    
    //sets all the inputs in the first tab to default value
    private void setRecordTaskDefault() {
        taskName.setText("name");
        subtasklist.clear();
        taskType.setSelectedIndex(0);
        fixedCost.setSelectedIndex(0);
        hours.setValue(0);mins.setValue(0);
        utilitySlider.setValue(0);
        fatigueSlider.setValue(50);
        deadline.setSelected(false);
        DeadlineSpinner.setEnabled(false);
        resource.clear();
    }
    
    //transfers a task from a status to another (either available task status or current work status)
    private void taskTransfer (ArrayList from, ArrayList to, JList<String> SourceList) {
        int selected = SourceList.getSelectedIndex();//index of selected task on taskList
        if (!from.isEmpty() && selected != -1) {
            to.add(from.get(selected));
            from.remove (selected);
            changeList (from);
            changeList (to);
        }
    }
    
    //updates all the charts in the stats tab
    private void updateStats () {
        chartSet ("time spent per task", "utility gained vs actual time","time (mins)", TimeDistribution, TimeEfficiency, statsPanel1,statsPanel2);
        chartSet ("effort spent per task", "utility gained vs fatigue","fatigue level (0-100)", fatigueLevelDistribution, fatigueLevelEfficiency, statsPanel3,statsPanel4);
        chartSet ("time budget per task", "utility gained vs time budget","time budget (mins)", timeBudgetDistribution, timeBudgetEfficiency, statsPanel5,statsPanel6);
    }
    
    //initializes one row of charts by taking in the data displayed in each chart
    private void chartSet (String HistogramName, String ScattergraphName, String variable, HistogramDataset dis, XYSeriesCollection eff, javax.swing.JPanel panel1, javax.swing.JPanel panel2) {
        JFreeChart TimeDistributionChart = ChartFactory.createHistogram(HistogramName, variable, "tasks", dis, PlotOrientation.VERTICAL, true, true, false);//histogram keeps track of the average variable for all tasks
        JFreeChart TimeEfficiencyChart = ChartFactory.createScatterPlot(ScattergraphName, variable, "utility gained", eff, PlotOrientation.VERTICAL, true, true, false);
    
    ChartPanel stats1 = new ChartPanel(TimeDistributionChart);
    ChartPanel stats2 = new ChartPanel(TimeEfficiencyChart);

    panel1.removeAll();
    panel2.removeAll();
    panel1.add(stats1);
    panel1.updateUI();
    panel2.add(stats2);
    panel2.updateUI();
    }

    //updates the charts when the data has changed.
    private void updateDataset () {
        TimeDistribution = updateDis(10);
        TimeEfficiency = updateEff(10);
        fatigueLevelDistribution = updateDis(6);
        fatigueLevelEfficiency = updateEff(6);
        timeBudgetDistribution = updateDis(4);
        timeBudgetEfficiency = updateEff(4);
        updateStats();//double check later
    }
    
    //takes the type of the data in a histogramDataset, returns an updated HistogramDataset of that type
    private HistogramDataset updateDis (int input) {
        double values[] = new double [finishedTask.size()];
        for (int k = 0;k<values.length;k++) {
            values[k] = (double)finishedTask.get(k).get(input);
            //System.out.println(values[k]);
        }
        HistogramDataset temp = new HistogramDataset();
        temp.addSeries("today",values,5);
        return(temp);
    }
    
    //takes the type of the data in a XYSeriesCollection, returns an updated XYSeriesCollection of that type
    private XYSeriesCollection updateEff (int input) {
        XYSeriesCollection dataset = new XYSeriesCollection();  
        XYSeries series1 = new XYSeries("today");
        for (int k = 0;k<finishedTask.size();k++) {
            series1.add((double)finishedTask.get(k).get(input), (double)finishedTask.get(k).get(5));
        } 
        XYSeriesCollection temp2 = new XYSeriesCollection();
        temp2.addSeries(series1);
        return(temp2);
    }
    
    //reads the recorded tasks in TaskList.txt, loads the taskList and displays it onto taskList jList
    private void initComponents2() throws IOException, FileNotFoundException, ParseException { 
        finishTaskButton.setEnabled(false);
        DeadlineSpinner.setEnabled(!rootPaneCheckingEnabled);
        readTaskList (path1);
        readTaskList (path2);
        for (int k = 0; k < taskList.size();k++) {
            availableTasks.add(k);
        }
        changeList (availableTasks);
        for (int k = 0; k < finishedTask.size();k++) {
            updateDataset ();//initializes the datasets used for stats
        }
        
    }
    
    //reads either the taskList.txt or report.txt, and initializes the taskList and finishedTasks
    private void readTaskList (File path) throws FileNotFoundException, IOException, ParseException {
        BufferedReader reader = new BufferedReader (new InputStreamReader (new FileInputStream(path)));//read tasks into TaskList
        String name = new String();
        while ((name = reader.readLine()) != null) {
            ArrayList<String> subTasks = new ArrayList ();
            String line = reader.readLine();
            int count = 0;
            while( line.indexOf("-",count)!= -1) {
                   if (line.indexOf(",", line.indexOf("-",count)) != -1) {
                       subTasks.add(line.substring(line.indexOf("-",count),line.indexOf(",", line.indexOf("-",count)) ));
                   } else {
                       subTasks.add(line.substring(line.indexOf("-",count),line.indexOf("]", line.indexOf("-",count)) ));
                   } 
                   count = line.indexOf("-",count)+1;
                   //System.out.println(line.substring(line.indexOf("-",count),line.indexOf(" ", line.indexOf("-",count)) ));
            }
            String type = reader.readLine();
            String subject = reader.readLine();
            double timeBudget = Double.parseDouble(reader.readLine());//change later for localdatetime
            double utility = Double.parseDouble(reader.readLine());
            double fatigueLevel = Double.parseDouble(reader.readLine());
            Boolean dl = Boolean.parseBoolean(reader.readLine());
            String dl2 = reader.readLine();
            Date deadline2 = new Date();
            if (!dl2.equals("null")) {
                deadline2 =  new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy", Locale.US).parse(dl2);
            }
            
            ArrayList<String> resources = new ArrayList ();
            line = reader.readLine();
            count = 0;
            while( line.indexOf(">",count)!= -1) {
                   if (line.indexOf(",", line.indexOf(">",count)) != -1) {
                       resources.add(line.substring(line.indexOf(">",count),line.indexOf(",", line.indexOf(">",count)) ));
                   } else {
                       resources.add(line.substring(line.indexOf(">",count),line.indexOf("]", line.indexOf(">",count)) ));
                   } 
                   count = line.indexOf(">",count)+1;
            }
           
            ArrayList<Object> obj = new ArrayList(List.of(name, subTasks, type, subject, timeBudget,utility, fatigueLevel, dl, deadline2, resources));
            if (path == path1) {
                taskList.add (obj); 
            } else {
                double timeSpent = Double.parseDouble(reader.readLine());
                obj.add(timeSpent);//simplify later
                finishedTask.add(obj);
            }
        }
        reader.close();
    }

    //*basically does anything associated with writing in this program: takes an ArrayList, clears the txt file, and writes the ArrayList of tasks on the file (includes deletion, addition, in all tasklist files).
    private void writeTaskList (File path, ArrayList<ArrayList> list) {
        try {BufferedWriter writer = new BufferedWriter(new FileWriter(path,false));//clear txt file: the false will clear the txt file
        
        for (int k = 0; k<list.size();k++) {
            for (int c = 0;c<list.get(k).size();c++) {
            writer.write(String.valueOf(list.get(k).get(c)));
            if (!(k == list.size()-1 && c == list.get(k).size()-1)) {
                writer.newLine();
                }//simplify later on
            }
        }
        writer.close();//adds the tasks onto the file
            
        } catch (IOException ex) {
            Logger.getLogger(Secretary.class.getName()).log(Level.SEVERE, null, ex);
        }//sends error message if the file cannot be found at the path
       
    }
    
    //takes a text from a text field and adds it to a JList which is an element of a task (either subtasks or resources)
    private void addSubElement (JList<String> list, DefaultListModel model, JTextField field ) {
        if (field.getText().contains(",")||field.getText().contains("]")){
            JOptionPane.showMessageDialog(field, "The text field will not accept ' , ' nor ' ] '", "Wrong format", 0);
            return;
        }    
        list.setModel(model);
        model.addElement(field.getText());
        field.setText("");
    }
    
    //takes an element of a JList which is an element of a task (either subtasks or resources)
    private void deleteSubElement (JList list, DefaultListModel model) {
            int selectedIndex = list.getSelectedIndex();
            if (selectedIndex != -1) {
                model.remove(selectedIndex);
            }
        }
    
    //opens websites and files that are resources to a task. the website is opened in the default browser
    private void openResource (String taskname) {
        for (int k = 0; k < currentTasks.size();k++) {
            if (taskname == taskList.get(currentTasks.get(k)).get(0)){
                ArrayList resources = (ArrayList)taskList.get(currentTasks.get(k)).get(9);
                //System.out.println(resources.size());
                for (int c = 0; c < resources.size(); c++) {
                    String path = (String)resources.get(c);
                    Boolean isURI = true;
                    URI uri = null;
                    try {
                        uri = new URI(path.substring(1));
                    } catch (URISyntaxException ex) {
                        isURI = false;
                    }
                    if (isURI == true) {
                        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                            try {
                                Desktop.getDesktop().browse(uri);
                            } catch (IOException ex) {
                                Logger.getLogger(Secretary.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    } else {
                        Desktop dt = Desktop.getDesktop();
                        try {
                            File file = new File(path.substring(1));
                            dt.open((file));
                        } catch (IOException ex) {
                            Logger.getLogger(Secretary.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        }
    }
    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFileChooser1 = new javax.swing.JFileChooser();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        taskName = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        subTaskList = new javax.swing.JList<>();
        jLabel2 = new javax.swing.JLabel();
        newSubTask = new javax.swing.JTextField();
        addSubTask = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        taskType = new javax.swing.JComboBox<>();
        fixedCost = new javax.swing.JComboBox<>();
        deadline = new javax.swing.JCheckBox();
        utilitySlider = new javax.swing.JSlider();
        fatigueSlider = new javax.swing.JSlider();
        addTasks = new javax.swing.JButton();
        deleteSubTask = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        hours = new javax.swing.JSpinner();
        mins = new javax.swing.JSpinner();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        DeadlineSpinner = new javax.swing.JSpinner();
        chosenFile = new javax.swing.JTextField();
        jScrollPane6 = new javax.swing.JScrollPane();
        resourceList = new javax.swing.JList<>();
        addResource = new javax.swing.JButton();
        deleteResource = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        chooseFile = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        TaskList = new javax.swing.JList<>();
        planningAddButton = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        CurrentWorks = new javax.swing.JList<>();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        Choices = new javax.swing.JComboBox<>();
        jLabel13 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tree = new javax.swing.JTree();
        jLabel11 = new javax.swing.JLabel();
        finishTaskButton = new javax.swing.JButton();
        startSessionButton = new javax.swing.JButton();
        getResourceButton = new javax.swing.JButton();
        restartPlanningButton = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jPanel5 = new javax.swing.JPanel();
        statsPanel1 = new javax.swing.JPanel();
        statsPanel2 = new javax.swing.JPanel();
        statsPanel3 = new javax.swing.JPanel();
        statsPanel4 = new javax.swing.JPanel();
        statsPanel5 = new javax.swing.JPanel();
        statsPanel6 = new javax.swing.JPanel();
        printButton = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        helpButton = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Task name:");

        taskName.setText("name");

        jScrollPane1.setViewportView(subTaskList);

        jLabel2.setText("Sub tasks:");

        newSubTask.setText("sub tasks");

        addSubTask.setText("add");
        addSubTask.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addSubTaskActionPerformed(evt);
            }
        });

        jLabel3.setText("task type:");

        jLabel4.setText("Utility gained:");

        jLabel5.setText("Fatigue level:");

        taskType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "cognitive", "physical" }));

        fixedCost.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "English", "Math", "Physics", "Biology", "Computer Science", "Statistics", "Philosophy" }));

        deadline.setText("with deadline?");
        deadline.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deadlineActionPerformed(evt);
            }
        });

        utilitySlider.setMajorTickSpacing(20);
        utilitySlider.setMinimum(-100);
        utilitySlider.setPaintLabels(true);
        utilitySlider.setValue(0);

        fatigueSlider.setMajorTickSpacing(10);
        fatigueSlider.setPaintLabels(true);

        addTasks.setText("add task");
        addTasks.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addTasksActionPerformed(evt);
            }
        });

        deleteSubTask.setText("delete");
        deleteSubTask.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteSubTaskActionPerformed(evt);
            }
        });

        jLabel6.setText("time budget:");

        mins.setModel(new javax.swing.SpinnerNumberModel(0, 0, 60, 1));
        mins.setMaximumSize(new java.awt.Dimension(30000, 30000));

        jLabel7.setText("hours");

        jLabel8.setText("mins");

        DeadlineSpinner.setModel(new javax.swing.SpinnerDateModel());

        chosenFile.setText("resources");

        jScrollPane6.setViewportView(resourceList);

        addResource.setText("add");
        addResource.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addResourceActionPerformed(evt);
            }
        });

        deleteResource.setText("delete");
        deleteResource.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteResourceActionPerformed(evt);
            }
        });

        jLabel12.setText("Resources:");

        chooseFile.setText("choose file");
        chooseFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chooseFileActionPerformed(evt);
            }
        });

        jLabel15.setText("choose a file or type its absolute directory, or type a url");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(14, 14, 14)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel12)
                                        .addGap(35, 35, 35))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel1)
                                            .addComponent(jLabel2))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel3)
                                .addGap(5, 5, 5)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 357, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(newSubTask, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(addSubTask)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(deleteSubTask))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(taskType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(fixedCost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(24, 24, 24)
                                    .addComponent(deadline))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGap(12, 12, 12)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                            .addComponent(chosenFile, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(18, 18, 18)
                                            .addComponent(chooseFile))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(18, 18, 18)
                                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                .addComponent(deleteResource)
                                                .addComponent(addResource)))
                                        .addComponent(jLabel15))))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(taskName, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(145, 145, 145)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(addTasks)
                            .addComponent(DeadlineSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(14, 14, 14)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel5))
                                .addGap(31, 31, 31))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel6)
                                .addGap(40, 40, 40)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(hours, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel7)
                                .addGap(1, 1, 1)
                                .addComponent(mins, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel8))
                            .addComponent(utilitySlider, javax.swing.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE)
                            .addComponent(fatigueSlider, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(121, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(taskName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(27, 27, 27)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(newSubTask, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addSubTask)
                    .addComponent(deleteSubTask))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(taskType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(deadline)
                    .addComponent(fixedCost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DeadlineSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(hours, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mins, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(jLabel4))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(utilitySlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(fatigueSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(jLabel15)
                        .addGap(1, 1, 1)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(chooseFile)
                            .addComponent(chosenFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel12)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(addTasks)
                        .addGap(55, 55, 55))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(addResource)
                                .addGap(11, 11, 11)
                                .addComponent(deleteResource))
                            .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        jTabbedPane2.addTab("record task", jPanel1);

        TaskList.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        TaskList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TaskListMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(TaskList);

        planningAddButton.setText("Confirm");
        planningAddButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                planningAddButtonActionPerformed(evt);
            }
        });

        CurrentWorks.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        CurrentWorks.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                CurrentWorksMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(CurrentWorks);

        jLabel9.setText("Current works");

        jLabel10.setText("Tasks");

        Choices.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "recommended", "types", "subject", "timeBudget", "utility", "fatigueLevel", "deadline" }));
        Choices.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChoicesActionPerformed(evt);
            }
        });

        jLabel13.setText("Order of tasks:");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(67, 67, 67)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(49, 49, 49)
                        .addComponent(planningAddButton))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addGap(56, 56, 56)
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Choices, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(57, 57, 57)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(78, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(102, 102, 102)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel10)
                        .addComponent(jLabel13)
                        .addComponent(Choices, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jScrollPane4)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(64, 64, 64)
                        .addComponent(planningAddButton)))
                .addContainerGap(110, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("planning", jPanel2);

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("Tasks");
        tree.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        jScrollPane2.setViewportView(tree);

        jLabel11.setText("procedure:");

        finishTaskButton.setText("finish a task or subtask");
        finishTaskButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                finishTaskButtonActionPerformed(evt);
            }
        });

        startSessionButton.setText("start work session");
        startSessionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startSessionButtonActionPerformed(evt);
            }
        });

        getResourceButton.setText("get resources");
        getResourceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getResourceButtonActionPerformed(evt);
            }
        });

        restartPlanningButton.setText("restart planning/ finish session");
        restartPlanningButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                restartPlanningButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(182, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(62, 62, 62)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(startSessionButton)
                            .addComponent(finishTaskButton)
                            .addComponent(getResourceButton)
                            .addComponent(restartPlanningButton))))
                .addGap(69, 69, 69))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap(100, Short.MAX_VALUE)
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(137, 137, 137)
                        .addComponent(startSessionButton)
                        .addGap(18, 18, 18)
                        .addComponent(getResourceButton)
                        .addGap(18, 18, 18)
                        .addComponent(finishTaskButton)
                        .addGap(18, 18, 18)
                        .addComponent(restartPlanningButton)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(56, 56, 56))
        );

        jTabbedPane2.addTab("Execution", jPanel3);

        statsPanel1.setLayout(new javax.swing.BoxLayout(statsPanel1, javax.swing.BoxLayout.LINE_AXIS));

        statsPanel2.setLayout(new javax.swing.BoxLayout(statsPanel2, javax.swing.BoxLayout.LINE_AXIS));

        statsPanel3.setLayout(new javax.swing.BoxLayout(statsPanel3, javax.swing.BoxLayout.LINE_AXIS));

        statsPanel4.setLayout(new javax.swing.BoxLayout(statsPanel4, javax.swing.BoxLayout.LINE_AXIS));

        statsPanel5.setLayout(new javax.swing.BoxLayout(statsPanel5, javax.swing.BoxLayout.LINE_AXIS));

        statsPanel6.setLayout(new javax.swing.BoxLayout(statsPanel6, javax.swing.BoxLayout.LINE_AXIS));

        printButton.setText("print");
        printButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printButtonActionPerformed(evt);
            }
        });

        jLabel14.setText("Performance Data");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(285, 285, 285)
                        .addComponent(jLabel14))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(569, 569, 569)
                        .addComponent(printButton))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(statsPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 316, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(statsPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 316, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(statsPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 316, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(statsPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 316, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(statsPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 316, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(statsPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 316, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );

        jPanel5Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {statsPanel1, statsPanel2, statsPanel3, statsPanel4, statsPanel5, statsPanel6});

        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(26, Short.MAX_VALUE)
                .addComponent(jLabel14)
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(statsPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(statsPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(statsPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(statsPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(statsPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(statsPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addComponent(printButton))
        );

        jPanel5Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {statsPanel1, statsPanel2, statsPanel3, statsPanel4, statsPanel5, statsPanel6});

        jScrollPane5.setViewportView(jPanel5);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 683, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 481, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane2.addTab("Stats", jPanel4);

        helpButton.setText("help");
        helpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpButtonActionPerformed(evt);
            }
        });

        jLabel16.setFont(new java.awt.Font("Freestyle Script", 0, 18)); // NOI18N
        jLabel16.setText("seretaryApp™ ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane2)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(helpButton)
                .addGap(123, 123, 123))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(helpButton)
                    .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(6, 6, 6)
                .addComponent(jTabbedPane2)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    //takes the input on the first tab and constructs a task, then adds it to the taskList and writes it into taskList.txt
    private void addTasksActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addTasksActionPerformed
        addTask ();
        writeTaskList (path1, taskList);
    }//GEN-LAST:event_addTasksActionPerformed
    //belongs to input of characteristics of tasks: adds a subtask to subTaskList jList and clears the input field
    private void addSubTaskActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addSubTaskActionPerformed
        addSubElement (subTaskList, subtasklist, newSubTask );
    }//GEN-LAST:event_addSubTaskActionPerformed
    //belongs to input of characteristics of tasks: deletes a subtask in the subTaskList jList
    private void deleteSubTaskActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteSubTaskActionPerformed
        deleteSubElement (subTaskList, subtasklist);
    }//GEN-LAST:event_deleteSubTaskActionPerformed
    //enables deadline to be recorded (it was a useless feature I added early on in the project)
    private void deadlineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deadlineActionPerformed
        if (deadline.isSelected()) {
            DeadlineSpinner.setEnabled(rootPaneCheckingEnabled);
        } else {
            DeadlineSpinner.setEnabled(!rootPaneCheckingEnabled);
        }
    }//GEN-LAST:event_deadlineActionPerformed
    //sorts availableTasks into the specified order, then displays the new availableTasks
    private void ChoicesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChoicesActionPerformed
        availableTasks = Analysis.getOrder ((String)Choices.getSelectedItem(),finishedTask,availableTasks,taskList);//needs double check
        changeList (availableTasks);
    }//GEN-LAST:event_ChoicesActionPerformed
    //moves task from available status to current work status, meaning that it's a task in your current work session
    private void TaskListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TaskListMouseClicked
        if (planningAddButton.isEnabled()) {
            taskTransfer (availableTasks,currentTasks, TaskList);  
        }
    }//GEN-LAST:event_TaskListMouseClicked
    //moves task from current work status to available status, meaning that it's a task in a future work session
    private void CurrentWorksMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_CurrentWorksMouseClicked
        taskTransfer (currentTasks, availableTasks, CurrentWorks);
    }//GEN-LAST:event_CurrentWorksMouseClicked
    //displays the tasks of the current work session in the third tab
    private void planningAddButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_planningAddButtonActionPerformed
        if (currentTasks.isEmpty() ==false) {
            ArrayList a = new ArrayList ();
            changeList (a);//hides CurrentList
            planningAddButton.setEnabled(false);
            displayExecutingTasks();//basically, an alternative form of displaying currentTasks - displaying it in the third tab
        }
    }//GEN-LAST:event_planningAddButtonActionPerformed
    //adds a finishedTime characteristic to a task, removes the finished task in taskList and puts it into finishedTasks and writes it into report.txt
    //achieves one of the four key functions of this project - automatically records task data, gives analysis for the data and presents it in the stats tab
    private void finishTaskButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_finishTaskButtonActionPerformed
        restartPlanningButton.setEnabled(false);
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent();
        if (selectedNode.getParent() == tree.getModel().getRoot() && tree.getModel().getChildCount(selectedNode) == 0) {
            int index = selectedNode.getParent().getIndex(selectedNode);//index of selectedNode on the tree, same index as in the currentWorks
            LocalDateTime finishTime = LocalDateTime.now();
            ArrayList task = taskList.get(currentTasks.get(index));
            task.add(Math. abs((double)Duration.between(finishTime, startTask).toMinutes()));//!!!deleted tasks will add a time value at the bottom of the task
            startTask = finishTime;
            finishedTask.add(task);
            updateDataset ();

            taskList.remove((int)currentTasks.get(index));
            for (int c = 0; c < currentTasks.size();c++) {
                if (currentTasks.get(c)>currentTasks.get(index)) {
                    currentTasks.set(c, currentTasks.get(c)-1);
                }
            }
            for (int c = 0; c < availableTasks.size();c++) {
                if (availableTasks.get(c)>currentTasks.get(index)) {
                    availableTasks.set(c, availableTasks.get(c)-1);
                }
            }
            currentTasks.remove(index);
            writeTaskList (path1, taskList);
            writeTaskList (path2, finishedTask);
        }
        if (selectedNode.getParent()!= null && tree.getModel().getChildCount(selectedNode) == 0){
            DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
            model.removeNodeFromParent(selectedNode);
        }
        restartPlanningButton.setEnabled(true);
    }//GEN-LAST:event_finishTaskButtonActionPerformed
    //starts the work session, starts timing tasks
    private void startSessionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startSessionButtonActionPerformed
        startTask = LocalDateTime.now();
        finishTaskButton.setEnabled(true);
        System.out.println("work session started");
    }//GEN-LAST:event_startSessionButtonActionPerformed
    //belongs to input of characteristics of tasks: adds a resource to resourceList jList and clears the input field
    private void addResourceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addResourceActionPerformed
        addSubElement (resourceList, resource, chosenFile );
    }//GEN-LAST:event_addResourceActionPerformed
    //belongs to input of characteristics of tasks: deletes a resource in the resourcelist jList
    private void deleteResourceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteResourceActionPerformed
        deleteSubElement (resourceList, resource);
    }//GEN-LAST:event_deleteResourceActionPerformed
    //uses FileChooser to get the path of a resource (a file), records the path onto chosenFile text field
    private void chooseFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chooseFileActionPerformed
        jFileChooser1.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if( jFileChooser1.showOpenDialog(null) == JFileChooser.APPROVE_OPTION ){
            File selected = jFileChooser1.getSelectedFile();
            chosenFile.setText(selected.toString());
        }
    }//GEN-LAST:event_chooseFileActionPerformed
    //opens the external resources you need for a task, whether a file or a url
    private void getResourceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getResourceButtonActionPerformed
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent();
        if (selectedNode.getParent() == null) {
            return;
        }
        while (selectedNode.getParent() != tree.getModel().getRoot()) {
            selectedNode = (DefaultMutableTreeNode)selectedNode.getParent();
        }
        String taskname = selectedNode.getUserObject().toString();
        openResource (taskname);
    }//GEN-LAST:event_getResourceButtonActionPerformed
    //prints the stats/analysis page. this feature is not very polished, as the printed page often misses a part of the original page
    private void printButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printButtonActionPerformed
        PrintMultiPageUtil stats = new PrintMultiPageUtil(jPanel5); 
        stats.print();
    }//GEN-LAST:event_printButtonActionPerformed
    //clears currentTasks, and makes all unfinished tasks have availableTasks status, you will be able to restart planning process, tab two and three return to default
    private void restartPlanningButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_restartPlanningButtonActionPerformed
        for (int k = 0;k < currentTasks.size();k++) {
            availableTasks.add(currentTasks.get(k));
        }
        currentTasks.clear();
        displayExecutingTasks();
        changeList(currentTasks);
        changeList(availableTasks);
        planningAddButton.setEnabled(true);
        finishTaskButton.setEnabled(false);
    }//GEN-LAST:event_restartPlanningButtonActionPerformed
    //displays instructions on how to use the app for different tabs
    private void helpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpButtonActionPerformed
        if (jTabbedPane2.getSelectedComponent() == jPanel1) {
            JOptionPane.showMessageDialog(helpButton, "-add task: adds a task to taskList \n\n-other components: input task info \n\n-enter subTasks/ resources: do not enter text that \ncontains the symbols ' , ' and ' ] '. \nalso don't enter too many items \n\n-delete subTasks/ resources: select an item in \nthe text area then delete \n\n-add resource: you can either type a path/url \nin the text field or use file chooser \nto choose a file. But you MUST \nenter a valid file path or URL to avoid errors", "help", 1);
        } else if (jTabbedPane2.getSelectedComponent() == jPanel2) {
            JOptionPane.showMessageDialog(helpButton, "-order of tasks: adjusts the order in tasks \n\n-click on items in a box to \nsend them to the other box \n\n-confirm: updates the task \nprocedure in the third tab", "help", 1);
        } else if (jTabbedPane2.getSelectedComponent() == jPanel3) {
            JOptionPane.showMessageDialog(helpButton, "-start work session: starts timer \n\n-get resources: opens up files or websites for the task \n\n-finish (sub)task: select a (sub)task then click \nfinish to finish the task. You can only finish \na task when all its subtasks are finished \n\n-restart/finish session: current tasks are cleared \nand you can create a brand new task procedure \nin the second tab", "help", 1);
        } else if (jTabbedPane2.getSelectedComponent() == jPanel4) {
            JOptionPane.showMessageDialog(helpButton, "-shows your performance data for all tasks completed \n\n-click print at the bottom of page \nto print a copy of this analysis", "help", 1);
        }
    }//GEN-LAST:event_helpButtonActionPerformed

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
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Secretary.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        //</editor-fold>

       
        
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new Secretary().setVisible(true);
                } catch (IOException | ParseException ex) {
                    Logger.getLogger(Secretary.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> Choices;
    private javax.swing.JList<String> CurrentWorks;
    private javax.swing.JSpinner DeadlineSpinner;
    private javax.swing.JList<String> TaskList;
    private javax.swing.JButton addResource;
    private javax.swing.JButton addSubTask;
    private javax.swing.JButton addTasks;
    private javax.swing.JButton chooseFile;
    private javax.swing.JTextField chosenFile;
    private javax.swing.JCheckBox deadline;
    private javax.swing.JButton deleteResource;
    private javax.swing.JButton deleteSubTask;
    private javax.swing.JSlider fatigueSlider;
    private javax.swing.JButton finishTaskButton;
    private javax.swing.JComboBox<String> fixedCost;
    private javax.swing.JButton getResourceButton;
    private javax.swing.JButton helpButton;
    private javax.swing.JSpinner hours;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JSpinner mins;
    private javax.swing.JTextField newSubTask;
    private javax.swing.JButton planningAddButton;
    private javax.swing.JButton printButton;
    private javax.swing.JList<String> resourceList;
    private javax.swing.JButton restartPlanningButton;
    private javax.swing.JButton startSessionButton;
    private javax.swing.JPanel statsPanel1;
    private javax.swing.JPanel statsPanel2;
    private javax.swing.JPanel statsPanel3;
    private javax.swing.JPanel statsPanel4;
    private javax.swing.JPanel statsPanel5;
    private javax.swing.JPanel statsPanel6;
    private javax.swing.JList<String> subTaskList;
    private javax.swing.JTextField taskName;
    private javax.swing.JComboBox<String> taskType;
    private javax.swing.JTree tree;
    private javax.swing.JSlider utilitySlider;
    // End of variables declaration//GEN-END:variables
}
