//Brandon Selzer
package memorymanager.GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import memorymanager.Blocks.Block;
import memorymanager.Blocks.BlockList;

//Container that displays memory, which consists of blocks (non-free) and holes (free). 
public class MemoryPanel extends JPanel
{
    private final GridBagConstraints gbc = new GridBagConstraints();
    private ProcessPanel processPanel;
    private WaitingPanel waitingPanel;
    private final BlockList blockList; //This is final because MemoryPanel is the official holder.
    private ArrayList<JLabel> lblTitles, lblOrder, lblAddresses, lblSizes; //lblOrder is the process order, lblAddresses are the base addresses and last block's end address
    private int totalSize; //Total size of memory.
    
    private final int width = 65;
    private final int height = 20;
    
    //Requires knowing what the initial size of memory is in order to add a free block with that size into memory (the default state).
    public MemoryPanel(int initialTotalSize)
    {
        this.totalSize = initialTotalSize;
        blockList = new BlockList();
        blockList.addBlock(new Block(0, initialTotalSize, true));
        initialize();
    }//end constructor
    
    //ProcessPanel is needed in order to clear components when decreasing the size of memory and try to 
    //add waiting processes to memory after compacting or increasing the size of memory.
    public void setProcessPanel(ProcessPanel processPanel)
    {
        this.processPanel = processPanel;
    }//end method
    
    //WaitingPanel is needed in order to revalidate it after compacting or increasing the size of memory.
    public void setWaitingPanel(WaitingPanel waitingPanel)
    {
        this.waitingPanel = waitingPanel;
    }//end method
    
    private void initialize()
    {
        setLayout(new GridBagLayout());
        initializeComponents();
    }//end method
    
    private void initializeComponents()
    {
        lblTitles = new ArrayList();
        lblTitles.add(new JLabel("Blocks:"));
        lblTitles.add(new JLabel("Addresses:"));
        lblTitles.add(new JLabel("Sizes:"));
        
        lblOrder = new ArrayList();
        lblAddresses = new ArrayList();
        lblSizes = new ArrayList();
        validateComponents(); //Shows initial memory (an entire free block).
    }//end method
    
    public void validateComponents()
    {
        //TESTING
        //long startTime = System.currentTimeMillis();
        
        removeAll();
        setComponents();
        
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 0, 10);
        
        for(JLabel label: lblTitles)
        {
            add(label, gbc);
            gbc.gridy++;
        }//end loop
        
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        
        for(int i = 0; i < blockList.getNumBlocks(); i++)
        {
            gbc.gridx = i + 2;
            gbc.gridy = 0;
            add(lblOrder.get(i), gbc); 
            
            gbc.gridx = i + 1;
            gbc.gridy = 1;
            add(lblAddresses.get(i), gbc); 
            
            gbc.gridx = i + 2;
            gbc.gridy = 2;
            add(lblSizes.get(i), gbc);
        }//end loop
        
        gbc.gridx = blockList.getNumBlocks() + 1;
        gbc.gridy = 1;
        add(lblAddresses.get(blockList.getNumBlocks()), gbc); //endAddress for last block.
        
        revalidate();
        repaint();
        
        //TESTING
        //blockList.outputBlockList();
        //long endTime = System.currentTimeMillis();
        //System.out.println("Memory Panel Validation: " + (endTime - startTime));
    }//end method
    
    //Resets the components and recreates the attributes associated with the container's components.
    private void setComponents()
    {
        //Reset lblOrder and lblAddresses 
        lblOrder = new ArrayList();
        lblAddresses = new ArrayList();
        lblSizes = new ArrayList();
        
        for(int i = 0; i < blockList.getNumBlocks(); i++)
        {
            lblOrder.add(new JLabel());
            
            if(!blockList.getBlocks().get(i).getFree())
            {
                lblOrder.get(i).setText("P" + Integer.toString(blockList.getBlocks().get(i).getAssociatedPID()));
                lblOrder.get(i).setBackground(Color.ORANGE);
            }//end if
            else
            {
                lblOrder.get(i).setText("Free");
                lblOrder.get(i).setBackground(Color.YELLOW);
            }//end else
            
            lblOrder.get(i).setPreferredSize(new Dimension(width, height));
            lblOrder.get(i).setBorder(BorderFactory.createLineBorder(Color.BLACK));
            lblOrder.get(i).setHorizontalAlignment(JLabel.CENTER);
            lblOrder.get(i).setOpaque(true);
            
            lblAddresses.add(new JLabel());
            lblAddresses.get(i).setText(Integer.toString(blockList.getBlocks().get(i).getBaseAddress()));
            lblAddresses.get(i).setHorizontalAlignment(JLabel.RIGHT);
            
            if(i == 0) //1st address is alone so minimize its width to just what is necessary to display "0"
            {
                lblAddresses.get(i).setPreferredSize(new Dimension(7, height)); 
            }//end if
            else
            {
                lblAddresses.get(i).setPreferredSize(new Dimension(width, height));
            }//end else
            
            lblSizes.add(new JLabel());
            lblSizes.get(i).setPreferredSize(new Dimension(width, height));
            lblSizes.get(i).setText(Integer.toString(blockList.getBlocks().get(i).getSize()));
            lblSizes.get(i).setHorizontalAlignment(JLabel.CENTER);
        }//end loop
        
        //Adding another label for endAddress of last block.
        int numBlocks = blockList.getNumBlocks();
        int lastAddress = blockList.getBlocks().get(numBlocks - 1).getBaseAddress();
        int lastSize = blockList.getBlocks().get(numBlocks - 1).getSize();
        lblAddresses.add(new JLabel(Integer.toString(lastAddress + lastSize)));
        lblAddresses.get(numBlocks).setPreferredSize(new Dimension(width, height));
        lblAddresses.get(numBlocks).setHorizontalAlignment(JLabel.RIGHT);
    }//end method
    
    //Resets memory by clearing every block from the block list and adding the free block with the size of memory.
    public void resetMemory()
    {
        blockList.removeAllBlocks();
        blockList.addBlock(new Block(0, totalSize, true));
        validateComponents();
    }//end method
    
    //Compacts memory and then tries to add every process in the waiting queue since memory has been freed.
    public void compactMemory()
    {
        blockList.compactBlocks(totalSize);
        processPanel.tryToAddWaitingProcessesToMemory();
        validateComponents();
        validateWaitingPanel();
    }//end method
    
    //Removes the last block if it is free in order to increase its size. 
    //If it isn't free, it adds a free block with a size of the newly created space.
    public void increaseTotalSize(int newTotalSize)
    {
        Block lastBlock = blockList.getLastBlock();

        if(lastBlock.getFree())
        {
            blockList.removeBlock(lastBlock);
            blockList.addBlock(new Block(lastBlock.getBaseAddress(), lastBlock.getSize() + newTotalSize - totalSize, true));
        }//end if
        else
        {
            blockList.addBlock(new Block(lastBlock.getBaseAddress() + lastBlock.getSize(), newTotalSize - totalSize, true));
        }//end if
        
        this.totalSize = newTotalSize;
        processPanel.tryToAddWaitingProcessesToMemory(); //Free space has been created so try to add processes in waiting queue.
        validateComponents();
        validateWaitingPanel();
    }//end method
    
    //Continuously removes the last block until this last block doesn't get cut off by the new size.
    //Readds the last cut off block if it was cut off in the middle.
    public void decreaseTotalSize(int newTotalSize)
    {
        Block lastBlock;
        int endAddress; 
        
        //LBD used to indicate variables for last block that is deleted.
        int baseAddressLBD = 0; 
        int remainingSizeLBD = 0;
        
        for(int i = blockList.getNumBlocks() - 1; i >= 0; i--)
        {
            lastBlock = blockList.getLastBlock();
            endAddress = lastBlock.getBaseAddress() + lastBlock.getSize();
            
            if(endAddress >= newTotalSize) //Block gets completely cut off or partially.
            {
                //Need to delete non-free blocks without replacement, otherwise it can cause issues in the list of blocks.
                blockList.deleteBlockWithoutReplacement(lastBlock);
                baseAddressLBD = lastBlock.getBaseAddress();
                remainingSizeLBD = newTotalSize - baseAddressLBD;
                
                if(!lastBlock.getFree()) //Non-free blocks have an associated process. Since it was removed, the process needs to be cleared too.
                {
                    processPanel.clearComponent(lastBlock.getAssociatedPID() - 1);
                }//end if
            }//end if
            else //Block isn't cut off.
            {
                break;
            }//end else
        }//end loop
         
        //End address of last block that needed to be deleted wasn't equal to newTotalSize so free up the remaining space.
        if(remainingSizeLBD != 0) 
        {
            blockList.addBlock(new Block(baseAddressLBD, remainingSizeLBD, true));
        }//end if
        
        this.totalSize = newTotalSize;
        validateComponents();
    }//end method
    
    public BlockList getBlockList()
    {
        return blockList;
    }//end method
    
    public int getTotalSize()
    {
        return totalSize;
    }
    
    public void setTotalSize(int totalSize)
    {
        this.totalSize = totalSize;
    }//end method
    
    //Needed to keep validation separate from tryToAddWaitingProcessesToMemory due to the Random button,
    private void validateWaitingPanel()
    {
        waitingPanel.validateComponents();
    }//end method
}//end class
