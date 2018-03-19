//Brandon Selzer

package memorymanager.GUI;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JPanel;
import memorymanager.Processes.ProcessList;

//Container holding the components for the waiting queue.
public class WaitingPanel extends JPanel
{
    private final GridBagConstraints gbc = new GridBagConstraints();
    private final ProcessList processList; 
    private ArrayList<JLabel> lblTitles, lblWaitingQueue, lblSizes;
    
    //Needs the ProcessPanel's processList in order to get the waiting queue.
    public WaitingPanel(ProcessList processList)
    {
        this.processList = processList;
        initialize();
    }//end constructor
    
    private void initialize()
    {
        setLayout(new GridBagLayout());
        initializeComponents();
    }//end method
    
    private void initializeComponents()
    {
        lblTitles = new ArrayList();
        lblTitles.add(new JLabel("Waiting Queue:"));
        lblTitles.add(new JLabel("Sizes:"));
        
        lblWaitingQueue = new ArrayList();
        lblSizes = new ArrayList();
        
        validateComponents(); //Shows initial waiting queue (nothing).
    }//end method
    
    public void validateComponents()
    {
        //TESTING
        //long startTime = System.currentTimeMillis();
        
        removeAll();
        setComponents();
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0, 0, 0, 7);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        
        for(JLabel lbl: lblTitles)
        {
            add(lbl, gbc);
            gbc.gridy++;
        }//end loop
        
        gbc.anchor = GridBagConstraints.CENTER;
        
        if(processList.getNumProcessesWaiting() == 0)
        {
            gbc.gridx = 1;
            gbc.gridy = 0;
            add(new JLabel("None"), gbc);
            
            revalidate();
            repaint();
            return;
        }//end if
        
        for(int i = 0; i < processList.getNumProcessesWaiting(); i++)
        {
            gbc.gridx = i + 1;
            gbc.gridy = 0;
            add(lblWaitingQueue.get(i), gbc);
            
            gbc.gridx = i + 1;
            gbc.gridy = 1;
            add(lblSizes.get(i), gbc);
        }//end for
        
        revalidate();
        repaint();
        
        //TESTING
        //long endTime = System.currentTimeMillis();
        //System.out.println("Waiting Panel Validation: " + (endTime - startTime));
    }//end method
    
    private void setComponents()
    {
        lblWaitingQueue = new ArrayList(); //Reset lblWaitingQueue
        
        for(int i = 0; i < processList.getNumProcessesWaiting(); i++)
        {
            lblWaitingQueue.add(new JLabel());
            lblWaitingQueue.get(i).setText("P" + Integer.toString(processList.getWaitingQueue().get(i).getPID()));
            
            lblSizes.add(new JLabel());
            lblSizes.get(i).setText(Integer.toString(processList.getWaitingQueue().get(i).getSize()));
        }//end loop
    }//end method
}//end class
