//Brandon Selzer

package memorymanager.GUI;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;

//Container holding all of the components that deal with user interaction and displaying memory/waiting queue.
public class GUI extends JFrame
{
    private final GridBagConstraints gbc = new GridBagConstraints();
    private PropertiesPanel propertiesPanel;
    private ProcessPanel processPanel;
    private MemoryPanel memoryPanel;
    private WaitingPanel waitingPanel;
    private JSeparator separator;
    private JScrollPane scrollProcess, scrollMemory, scrollWaiting;
    
    private final int width = 500;
    private final int height = 160;
    private final int initialProcessNumber = 5;
    private final int initialTotalSize = 16384; //2^14
    
    public GUI()
    {
        initialize();
    }
    
    private void initialize()
    {
        setTitle("Memory Manager by Brandon Selzer");
        setLayout(new GridBagLayout());
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        
        initializeComponents();
        
        pack(); //Set size of this frame to the minimum required size to display all components.
        setLocationRelativeTo(null);
    }//end method
    
    private void initializeComponents()
    {
        gbc.anchor = GridBagConstraints.NORTHWEST;
        
        setJMenuBar(new MenuBar());
        
        memoryPanel = new MemoryPanel(initialTotalSize);
        processPanel = new ProcessPanel(initialProcessNumber);
        propertiesPanel = new PropertiesPanel(processPanel, memoryPanel, initialProcessNumber, initialTotalSize);
        waitingPanel = new WaitingPanel(processPanel.getProcessList());
        processPanel.setPropertiesPanel(propertiesPanel);
        processPanel.setMemoryPanel(memoryPanel);
        processPanel.setWaitingPanel(waitingPanel);
        propertiesPanel.setWaitingPanel(waitingPanel);
        memoryPanel.setProcessPanel(processPanel);
        memoryPanel.setWaitingPanel(waitingPanel);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 10, 0);
        add(propertiesPanel, gbc);
        
        separator = new JSeparator(JSeparator.HORIZONTAL);
        separator.setPreferredSize(new Dimension(width, 10));
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(separator, gbc);

        scrollProcess = new JScrollPane(processPanel);
        scrollProcess.setPreferredSize(new Dimension(width, height));
        scrollProcess.getVerticalScrollBar().setUnitIncrement(16);
        scrollProcess.getHorizontalScrollBar().setUnitIncrement(16);
        gbc.gridx = 0;
        gbc.gridy = 2; 
        add(scrollProcess, gbc);
        
        scrollMemory = new JScrollPane(memoryPanel);
        scrollMemory.setPreferredSize(new Dimension(width, height / 2));
        scrollMemory.getVerticalScrollBar().setUnitIncrement(16);
        scrollMemory.getHorizontalScrollBar().setUnitIncrement(16);
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(scrollMemory, gbc);
        
        scrollWaiting = new JScrollPane(waitingPanel);
        scrollWaiting.setPreferredSize(new Dimension(width, height / 3));
        scrollWaiting.getVerticalScrollBar().setUnitIncrement(16);
        scrollWaiting.getHorizontalScrollBar().setUnitIncrement(16);
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(scrollWaiting, gbc);
        
        revalidate();
    }//end method
}//end class
