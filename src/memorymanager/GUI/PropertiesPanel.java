//Brandon Selzer

package memorymanager.GUI;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import memorymanager.Algorithms.Algorithm;
import memorymanager.Algorithms.BestFit;
import memorymanager.Algorithms.FirstFit;
import memorymanager.Algorithms.WorstFit;

//Container holding the components that manage memory and processes.
public class PropertiesPanel extends JPanel
{
    private final GridBagConstraints gbc = new GridBagConstraints();
    private final ProcessPanel processPanel;
    private final MemoryPanel memoryPanel;
    private WaitingPanel waitingPanel;
    private final int initialTotalSize;
    private Algorithm algorithm; //The algorithm is only held by the PropertiesPanel. 
    private JLabel lblAlgorithm, lblNumProcesses, lblTotalSize;
    private JComboBox cboAlgorithm;
    private JTextField txtNumProcesses, txtTotalSize;
    private JButton btnCompact, btnClear, btnRandomSizes;
    
    //Requires the ProcessPanel and MemoryPanel since the algorithm needs the processList and blockList that they hold.
    //ProcessPanel and MemoryPanel are also needed to manage the # of processes/total size of memory.
    //Requires the initialProcessNumber and initialTotalSize in order to set the appropriate text for the JTextFields.
    public PropertiesPanel(ProcessPanel processPanel, MemoryPanel memoryPanel, int initialProcessNumber, int initialTotalSize)
    {
        this.processPanel = processPanel;
        this.memoryPanel = memoryPanel;
        this.initialTotalSize = initialTotalSize;
        this.algorithm = new FirstFit(processPanel.getProcessList(), memoryPanel.getBlockList());
        
        initialize(initialProcessNumber);
    }//end constructor
    
    //WaitingPanel is needed to revalidate after clearing a process.
    public void setWaitingPanel(WaitingPanel waitingPanel)
    {
        this.waitingPanel = waitingPanel;
    }
    
    private void initialize(int initialProcessNumber)
    {
        setLayout(new GridBagLayout());
        initializeComponents(initialProcessNumber);
    }
    
    private void initializeComponents(int initialProcessNumber)
    {
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(0, 0, 5, 5);
        
        lblAlgorithm = new JLabel();
        lblAlgorithm.setText("Allocation algorithm:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(lblAlgorithm, gbc);
        
        cboAlgorithm = new JComboBox();
        cboAlgorithm.addItem("First Fit");
        cboAlgorithm.addItem("Best Fit");
        cboAlgorithm.addItem("Worst Fit");
        cboAlgorithm.addActionListener(new cboAlgorithmActionListener());
        gbc.gridx = 1;
        gbc.gridy = 0;
        add(cboAlgorithm, gbc);
        
        lblTotalSize = new JLabel();
        lblTotalSize.setText("Total size of memory:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(lblTotalSize, gbc);
        
        txtTotalSize = new JTextField();
        txtTotalSize.setPreferredSize(new Dimension(80, 20));
        txtTotalSize.setText(Integer.toString(initialTotalSize));
        txtTotalSize.addFocusListener(new SelectAllFocusAdapter());
        txtTotalSize.addActionListener(new txtTotalSizeActionListener());
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(txtTotalSize, gbc);
        
        lblNumProcesses = new JLabel();
        lblNumProcesses.setText("Number of processes:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(lblNumProcesses, gbc);
        
        txtNumProcesses = new JTextField();
        txtNumProcesses.setPreferredSize(new Dimension(80, 20));
        txtNumProcesses.setText(Integer.toString(initialProcessNumber));
        txtNumProcesses.addFocusListener(new SelectAllFocusAdapter());
        txtNumProcesses.addActionListener(new txtNumProcessesActionListener());
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(txtNumProcesses, gbc);

        btnClear = new JButton();
        btnClear.setText("Clear Memory and Process Fields");
        btnClear.addActionListener(new btnClearActionListener());
        gbc.gridx = 2;
        gbc.gridy = 0;
        add(btnClear, gbc);
        
        btnRandomSizes = new JButton();
        btnRandomSizes.setText("Set Random Sizes");
        btnRandomSizes.addActionListener(new btnRandomSizesActionListener());
        gbc.gridx = 2;
        gbc.gridy = 1;
        add(btnRandomSizes, gbc);
                
        btnCompact = new JButton();
        btnCompact.setText("Compact Memory");
        btnCompact.addActionListener(new btnCompactActionListener());
        gbc.gridx = 2;
        gbc.gridy = 2;
        add(btnCompact, gbc);
    }//end method
    
    public Algorithm getAlgorithm()
    {
        return algorithm;
    }
    
    private class cboAlgorithmActionListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent ae) 
        {
            switch (cboAlgorithm.getSelectedIndex()) 
            {
                case 0:
                    algorithm = new FirstFit(processPanel.getProcessList(), memoryPanel.getBlockList());
                    break;
                case 1:
                    algorithm = new BestFit(processPanel.getProcessList(), memoryPanel.getBlockList());
                    break;
                case 2:
                    algorithm = new WorstFit(processPanel.getProcessList(), memoryPanel.getBlockList());
                    break;
                default:
                    break;
            }//end switch
        }//end method
    }//end AL class
    
    private class txtTotalSizeActionListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent ae) 
        {
            int input;
            
            try
            {
                input = Integer.parseInt(txtTotalSize.getText());
            }//end try
            catch(NumberFormatException ex)
            {
                JOptionPane.showMessageDialog(null, "Invalid input for the total size of memory.");
                return;
            }//end catch
            
            if(input <= 0)
            {
                JOptionPane.showMessageDialog(null, "Total size of memory must be greater than 0.");
                return;
            }//end if
            
            int totalSize = memoryPanel.getTotalSize();
            
            if(input > totalSize)
            {
                memoryPanel.increaseTotalSize(input);
            }//end if
            else if(input < totalSize)
            {
                memoryPanel.decreaseTotalSize(input);
            }//end if
        }//end method
    }//end AL class
    
    private class btnClearActionListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent ae)
        {
            processPanel.clearComponents();
            memoryPanel.resetMemory();
            waitingPanel.validateComponents();
        }//end method
    }//end AL class
    
    private class btnRandomSizesActionListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent ae)
        {
            //TESTING
            //long startTime = System.currentTimeMillis();
            
            processPanel.setRandomSizesForAllProcesses();
            
            //TESTING
            //long endTime = System.currentTimeMillis();
            //System.out.println("Total Time: " + (endTime - startTime));
        }//end method
    }//end AL class
    
    private class btnCompactActionListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent ae)
        {
            memoryPanel.compactMemory();
        }//end method
    }//end AL class
    
    private class txtNumProcessesActionListener implements ActionListener
    {
        //Will add/remove processes in the process panel based on the input.
        @Override
        public void actionPerformed(ActionEvent ae)
        {
            int input;

            try
            {
                input = Integer.parseInt(txtNumProcesses.getText());
            }//end try
            catch(NumberFormatException ex)
            {
                JOptionPane.showMessageDialog(null, "Invalid input for the number of processes.");
                return;
            }//end catch

            if(input <= 0)
            {
                JOptionPane.showMessageDialog(null, "Number of processes must be greater than 0.");
                return;
            }//end if

            int numProcesses = processPanel.getProcessList().getNumProcesses();

            if(input > numProcesses)
            {
                processPanel.addProcesses(input - numProcesses);
            }//end if 
            else if(input < numProcesses)
            {
                processPanel.removeProcesses(numProcesses - input);
            }//end if 
        }//end method
    }//end AL class
    
    private class SelectAllFocusAdapter extends FocusAdapter
    {
        @Override
        public void focusGained(FocusEvent e)
        {
            JTextField source = (JTextField)e.getSource();
            source.selectAll(); //Highlights all of the text in the JTextField
        }//end method
    }//end class
}//end class
