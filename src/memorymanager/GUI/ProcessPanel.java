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
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import memorymanager.Processes.ProcessList;
import memorymanager.Processes.Process;

//Container that displays processes. Each process displays its PID and size and has an associated clear/random button.
public class ProcessPanel extends JPanel
{
    private final GridBagConstraints gbc = new GridBagConstraints();
    private MemoryPanel memoryPanel;
    private WaitingPanel waitingPanel;
    private PropertiesPanel propertiesPanel;
    private final ProcessList processList; //This is final because ProcessPanel is the official holder.
    private ArrayList<JLabel> lblTitles, lblProcesses; //lblProcesses are the PIDs for the processes
    private ArrayList<JTextField> txtSizes;
    private ArrayList<JButton> btnClear, btnRandom; //Each process gets a clear and random button.

    public ProcessPanel(int initialProcessNumber)
    {
        processList = new ProcessList();
        
        initialize(initialProcessNumber);
    }//end constructor
    
    //MemoryPanel is needed in order to revalidate, activate blockList methods such as removing a block the user wants to specifically clear through 
    //the associated PID, and to give the random method in processList which determines the range of values based on the total size of memory.
    public void setMemoryPanel(MemoryPanel memoryPanel)
    {
        this.memoryPanel = memoryPanel;
    }
    
    //WaitingPanel is needed in order revalidate.
    public void setWaitingPanel(WaitingPanel waitingPanel)
    {
        this.waitingPanel = waitingPanel;
    }
    
    //PropertiesPanel is needed in order to activate the placeBlock method of the algorithm selected.
    public void setPropertiesPanel(PropertiesPanel propertiesPanel)
    {
        this.propertiesPanel = propertiesPanel;
    }
    
    private void initialize(int initialProcessNumber)
    {
        setLayout(new GridBagLayout());
        initializeComponents(initialProcessNumber);
    }
    
    private void initializeComponents(int initialProcessNumber)
    {
        lblTitles = new ArrayList();
        lblTitles.add(new JLabel("PIDs:"));
        lblTitles.add(new JLabel("Sizes:"));
        
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 10, 10);
        
        for(JLabel lbl: lblTitles)
        {
            add(lbl, gbc);
            gbc.gridy++;
        }//end loop
        
        lblProcesses = new ArrayList();
        txtSizes = new ArrayList();
        btnClear = new ArrayList();
        btnRandom = new ArrayList();
        
        addProcesses(initialProcessNumber);
    }//end method
    
    public void validateComponents()
    {
        gbc.anchor = GridBagConstraints.CENTER;
        
        for(int i = 0; i < processList.getNumProcesses(); i++)
        {
            gbc.insets = new Insets(0, 0, 10, 5);
            gbc.gridx = i + 1;
            
            gbc.gridy = 0;
            add(lblProcesses.get(i), gbc);
            
            gbc.gridy = 1;
            txtSizes.get(i).setText(Integer.toString(processList.getListOfProcesses().get(i).getSize()));           
            add(txtSizes.get(i), gbc);
            
            gbc.gridy = 2;          
            add(btnClear.get(i), gbc);
            
            gbc.gridy = 3;
            add(btnRandom.get(i), gbc);
        }//end loop
        
        revalidate();
        repaint();
    }//end method
    
    //Clear every single process (the JTextField text and the sizes)
    public void clearComponents()
    {
        for(int i = 0; i < processList.getNumProcesses(); i++)
        {
            txtSizes.get(i).setText("0");
        }//end loop
        
        processList.resetListOfProcesses();
        processList.clearWaitingQueue();
    }//end method
    
    //Clear an individual process. It is the job of the invoker to determine the index. (PID - 1)
    public void clearComponent(int index)
    {
        txtSizes.get(index).setText("0");
        processList.getListOfProcesses().get(index).setSize(0);
    }//end method
    
    //Creates the attributes associated with the container's components.
    public void addProcesses(int add)
    {
        for(int i = 1; i <= add; i++)
        {
            Process process = new Process(processList.getNumProcesses() + 1, 0);
            processList.addProcess(process);
            lblProcesses.add(new JLabel(Integer.toString(process.getPID())));
            
            txtSizes.add(new JTextField("0"));
            txtSizes.get(txtSizes.size() - 1).setPreferredSize(new Dimension(80, 20));
            txtSizes.get(txtSizes.size() - 1).addFocusListener(new TextFieldFocusAdapter());
            txtSizes.get(txtSizes.size() - 1).addActionListener(new txtSizeActionListener());
            
            btnClear.add(new JButton("Clear"));
            btnClear.get(btnClear.size() - 1).addActionListener(new btnClearActionListener());
            
            btnRandom.add(new JButton("Random"));
            btnRandom.get(btnRandom.size() - 1).addActionListener(new btnRandomActionListener());
        }//end loop
        
        validateComponents();
    }//end method
    
    //Removes the components from the container and also removes the process from the processList.
    public void removeProcesses(int remove)
    {
        for(int i = 1; i <= remove; i++)
        {
            int numProcesses = processList.getNumProcesses();
            remove(lblProcesses.get(numProcesses - 1));
            remove(txtSizes.get(numProcesses - 1));
            remove(btnClear.get(numProcesses - 1));
            remove(btnRandom.get(numProcesses - 1));
            
            lblProcesses.remove(numProcesses - 1);
            txtSizes.remove(numProcesses - 1);
            btnClear.remove(numProcesses - 1);
            btnRandom.remove(numProcesses - 1);
            memoryPanel.getBlockList().removeBlockByAssociatedPID(numProcesses);
            processList.removeWaitingProcessByPID(numProcesses); //Need to remove waitingProcess first since it finds it by comparing itself to processes in processList.
            processList.removeLastProcess();
        }//end loop
        
        validateComponents();
        validateMemoryPanel();
        validateWaitingPanel();
    }//end method
    
    public ProcessList getProcessList()
    {
        return processList;
    }
    
    //Activates the algorithm on each process in the waiting queue. 
    public void tryToAddWaitingProcessesToMemory()
    {
        //processList's numProcessesWaiting will change when waiting processes are removed so this variable is necessary
        int numProcessesWaiting = processList.getNumProcessesWaiting();
        
        //Waiting processes may still end up back on the queue, but the loop only makes a pass through each waiting process once.
        for(int i = 1; i <= numProcessesWaiting; i++)
        {
            activateAlgorithm(processList.getFirstProcessWaiting()); 
            processList.removeFirstWaitingProcess();
        }//end loop
    }//end method
    
    //Activates the algorithm for each process (including waiting queue) with a new randomized size.
    public void setRandomSizesForAllProcesses()
    {
        for(Process process: processList.getListOfProcesses())
        {
            if(process.getSize() > 0)
            {
                clearProcess(process);
            }//end if

            int size = processList.getRandomSize(memoryPanel.getTotalSize());
            txtSizes.get(process.getPID() - 1).setText(Integer.toString(size));
            process.setSize(size);
            activateAlgorithm(process);
        }//end loop
        
        validateMemoryPanel();
        validateWaitingPanel();
    }//end method
    
    //Used to determine which process is specifically being handled by the ActionListener.
    private Process findProcessByJTextField(JTextField txt)
    {
        for(int i = 0; i < processList.getNumProcesses(); i++)
        {
            if(txt.equals(txtSizes.get(i)))
            {
                return processList.getListOfProcesses().get(i);
            }//end if
        }//end loop
        
        return null;
    }//end method
    
    //Used to determine which process is specifically being handled by the ActionListener.
    private Process findProcessByClearJButton(JButton btn)
    {
        for(int i = 0; i < processList.getNumProcesses(); i++)
        {
            if(btn.equals(btnClear.get(i)))
            {
                return processList.getListOfProcesses().get(i);
            }//end if
        }//end loop
        
        return null;
    }//end method
    
    //Used to determine which process is specifically being handled by the ActionListener.
    private Process findProcessByRandomJButton(JButton btn)
    {
        for(int i = 0; i < processList.getNumProcesses(); i++)
        {
            if(btn.equals(btnRandom.get(i)))
            {
                return processList.getListOfProcesses().get(i);
            }//end if
        }//end loop
        
        return null;
    }//end method
    
    //Erases the process from everything (reset size to 0, remove from MemoryPanel and WaitingPanel).
    //Since the block associated with the process is removed, there is free memory so tryToAddWaitingProcessesToMemory() is called.
    private void clearProcess(Process process)
    {
        if(process.getSize() == 0) //Process is already cleared, so do nothing.
        {
            return;
        }//end if

        process.setSize(0);
        memoryPanel.getBlockList().removeBlockByAssociatedPID(process.getPID());
        processList.removeWaitingProcessByPID(process.getPID());
        tryToAddWaitingProcessesToMemory();
    }//end method
    
    private void activateAlgorithm(Process process)
    {
        propertiesPanel.getAlgorithm().placeBlock(process);
    }//end method
    
    //Needed to keep validation separate from the methods that require validation since the Random button would 
    //take multiple seconds to revalidate but it only needed to occur after every process was cleared and added.
    //There was a significant time loss because of that.
    private void validateMemoryPanel()
    {
        memoryPanel.validateComponents();
    }//end method
    
    private void validateWaitingPanel()
    {
        waitingPanel.validateComponents();
    }//end method
     
    private class txtSizeActionListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent ae)
        {
            JTextField source = (JTextField)ae.getSource();
            Process process = findProcessByJTextField(source);
            int input; 

            try
            {
                input = Integer.parseInt(source.getText());
            }//end try
            catch(NumberFormatException ex)
            {
                JOptionPane.showMessageDialog(null, "Invalid input for process " + process.getPID() + "'s size.");
                return;
            }//end catch

            if(input < 0)
            {
                JOptionPane.showMessageDialog(null, "Process " + process.getPID() + "'s size can't be negative.");
                return;
            }//end if
            else if(process.getSize() > 0 && input != 0) //User isn't trying to manually remove the process.
            {
                clearProcess(process);
                process.setSize(input);
                activateAlgorithm(process);
            }//end if
            else if(input == 0) //User is manually removing the process.
            {  
                clearProcess(process);
            }//end if
            else //Process is already cleared and the user is setting the size.
            {
                process.setSize(input);
                activateAlgorithm(process);          
            }//end else
            
            validateMemoryPanel();
            validateWaitingPanel();
        }//end method
    }//end AL class
    
    private class btnClearActionListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent ae)
        {
            JButton source = (JButton)ae.getSource();
            Process process = findProcessByClearJButton(source);
            clearProcess(process);
            txtSizes.get(process.getPID() - 1).setText("0");
            validateMemoryPanel();
            validateWaitingPanel();
        }//end method
    }//end AL class
    
    private class btnRandomActionListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent ae)
        {
            JButton source = (JButton)ae.getSource();
            Process process = findProcessByRandomJButton(source);

            if(process.getSize() > 0)
            {
                clearProcess(process);
            }//end if

            int size = processList.getRandomSize(memoryPanel.getTotalSize());
            txtSizes.get(process.getPID() - 1).setText(Integer.toString(size));
            process.setSize(size);
            activateAlgorithm(process);
            validateMemoryPanel();
            validateWaitingPanel();
        }//end method
    }//end AL class
    
    //Used by JTextFields so that when it gains focus, the user can just start typing in order to remove the text.
    private class TextFieldFocusAdapter extends FocusAdapter
    {
        @Override
        public void focusGained(FocusEvent e)
        {
            JTextField source = (JTextField)e.getSource();
            source.selectAll(); //Highlights all of the text in the JTextField
        }//end method
    }//end FA class
}//end class

