//Brandon Selzer

package memorymanager.GUI;

import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

//Container holding the info about how to operate the application and what assumptions are made about the memory manager.
public class HelpFrame extends JFrame
{
    private JPanel helpPanel;
    private JEditorPane txtHelp; //An editor pane is used in order to get formatted text using html.
    private JScrollPane scrollPane;
    
    public HelpFrame()
    {
        initialize();
    }
    
    private void initialize()
    {
        setTitle("Memory Manager Help");
        setLayout(new FlowLayout(FlowLayout.CENTER));
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
        
        initializeComponents();
        
        pack(); //Set the size of the container to the necessary minimum.
        setLocationRelativeTo(null);
    }//end method
    
    private void initializeComponents()
    {
        txtHelp = new JEditorPane("text/html", "");
        txtHelp.setEditable(false);
        txtHelp.setBackground(null); //Editor pane's color is originally white but this makes it the same as the panel's default.
        txtHelp.setText(
            "Memory Manager by Brandon Selzer" + "<br><br>" +
            "<b><u>Program Operation:</b></u>" + "<br>" +
            "&#8226" + "Enter the number of processes." + "<br>" +
                "&emsp&emsp&#8226" + "Make sure to hit enter after typing in the number into the text field." + "<br>" +
                "&emsp&emsp&#8226" + "Enter any positive number of processes." + "<br>" +
                "&emsp&emsp&#8226" + "It is recommended to enter at most 2000 processes." + "<br>" +
                    "&emsp&emsp&emsp&emsp&#8226" + "However, you can still enter as many as you want. <b>BE CAREFUL!</b>" + "<br>" +
            "&#8226" + "Enter the total size of memory." + "<br>" +
                "&emsp&emsp&#8226" + "Make sure to hit enter after typing in the number into the text field." + "<br>" +
                "&emsp&emsp&#8226" + "The size must be a natural number." + "<br>" +    
            "&#8226" + "Select the allocation algorithm." + "<br>" +
                "&emsp&emsp&#8226" + "The default is first fit." + "<br>" +    
            "&#8226" + "Enter the request size for a process in the associated text field." + "<br>" +
                "&emsp&emsp&#8226" + "Sizes must be whole numbers." + "<br>" +
                    "&emsp&emsp&emsp&emsp&#8226" + "Entering a zero clears the process." + "<br>" +
                        "&emsp&emsp&emsp&emsp&emsp&emsp&#8226" + "You can also clear the process using the associated button instead." + "<br>" +
                        "&emsp&emsp&emsp&emsp&emsp&emsp&#8226" + "Once cleared, each waiting process attempts to get into memory." + "<br>" +
                "&emsp&emsp&#8226" + "You can also hit the random button associated with the process instead." + "<br>" + 
                "&emsp&emsp&#8226" + "If a process already has a size, it will be cleared and then readded with the new size." + "<br><br>" +
            
            "&#8226" + "\"Clear Memory and Process Fields\" button:" + "<br>" +
                "&emsp&emsp&#8226" + "Resets memory back to a single free block." + "<br>" + 
                "&emsp&emsp&#8226" + "Resets every process so that they all have a size of zero." + "<br>" +
                    "&emsp&emsp&emsp&emsp&#8226" + "The waiting queue gets cleared as well." + "<br>" + 
            "&#8226" + "\"Set Random Sizes\" button:" + "<br>" +
                "&emsp&emsp&#8226" + "One at a time, each process gets cleared and then readded with the new size." + "<br>" +
                    "&emsp&emsp&emsp&emsp&#8226" + "Waiting processes are included." + "<br>" +
                "&emsp&emsp&#8226" + "Repeatedly clicking this button with a large number of processes causes slow down." + "<br>" +
                    "&emsp&emsp&emsp&emsp&#8226" + "This is due to the waiting processes check after clearing each non-cleared process." + "<br>" +    
            "&#8226" + "\"Compact Memory\" button:" + "<br>" +
                "&emsp&emsp&#8226" + "Collects free blocks into a single free block toward the right side." + "<br>" +
                "&emsp&emsp&#8226" + "Afterwards, waiting processes will attempt to get into memory." + "<br><br>" +
            
            "<b><u>Assumptions:</b></u>" + "<br>" +
            "&#8226" + "The process/request limit depends on how long you are willing to wait for the answer." + "<br>" +
            "&#8226" + "The OS isn't considered, but it normally would be." + "<br>" +
            "&#8226" + "Burst times aren't considered, but they normally would be." + "<br>" +
                "&emsp&emsp&#8226" + "Burst time consideration is up to the user to personally track it." + "<br>" +
            "&#8226" + "Process/request order is defined by the user." + "<br><br>" +
               
            "<b><u>Design Choices:</b></u>" + "<br>" + 
            "&#8226" + "ArrayLists are consistently used." + "<br>" +
                "&emsp&emsp&#8226" + "ArrayLists expand automatically." + "<br>" + 
                "&emsp&emsp&#8226" + "ArrayLists allow access to every element." + "<br>" +
                    "&emsp&emsp&emsp&emsp&#8226" + "This is necessary for displaying components even though queue like methods are sometimes used." + "<br>" +
                "&emsp&emsp&#8226" + "ArrayLists have O(1) get and add (append version) methods." + "<br>" + 
            "&#8226" + "A ProcessList class was made to manage the ArrayList of processes." + "<br>" +
                "&emsp&emsp&#8226" + "The Process class consists of just the PID and size." + "<br>" +
            "&#8226" + "A Block class was made to deal with managing memory." + "<br>" +
                "&emsp&emsp&#8226" + "It isn't abstract since free blocks and non-free blocks are fundamentally the same." + "<br>" +
                "&emsp&emsp&#8226" + "Blocks consist of a base address, a size (limit), PID, and a boolean representing if it is free." + "<br>" +
                    "&emsp&emsp&emsp&emsp&#8226" + "The PID represents the connection between the requesting process and its block in memory." + "<br>" +
            "&#8226" + "A BlockList class was made to manage the ArrayList of blocks." + "<br>" + 
                "&emsp&emsp&#8226" + "Collections.sort() is used for sorting:" + "<br>" + 
                    "&emsp&emsp&emsp&emsp&#8226" + "It uses merge sort: O(n" + "&#8901" + "log" + "<sub>2</sub>" + "(n)) performance." + "<br>" +
                    "&emsp&emsp&emsp&emsp&#8226" + "The Block class implements Comparable, which is why this is used instead of creating a sort method." + "<br>" +
                        "&emsp&emsp&emsp&emsp&emsp&emsp&#8226" + "The natural ordering is by base address." + "<br>" +  
            "&#8226" + "The GUI is split into 4 sections:" + "<br>" +
                "&emsp&emsp&#8226" + "PropertiesPanel handles the properties of the processes/memory/algorithm and managing them." + "<br>" +
                "&emsp&emsp&#8226" + "ProcessPanel handles the processes: " + "<br>" +
                    "&emsp&emsp&emsp&emsp&#8226" + "What a processes's PID and size is." + "<br>" +
                    "&emsp&emsp&emsp&emsp&#8226" + "Handling a processes's associated clear/random button." + "<br>" +
                "&emsp&emsp&#8226" + "MemoryPanel handles drawing memory based on where the algorithm puts blocks." + "<br>" +
                "&emsp&emsp&#8226" + "WaitingPanel handles outputting what processes couldn't fit into memory and have to wait for an opening."    
        ); //end setText
        
        helpPanel = new JPanel();
        helpPanel.add(txtHelp);
        
        scrollPane = new JScrollPane(helpPanel);
        scrollPane.setPreferredSize(new Dimension(700, 500));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        add(scrollPane);
    }//end method
}//end class
