//Brandon Selzer

package memorymanager.Blocks;

import java.util.ArrayList;
import java.util.Collections;

//This class was created in order to have an ArrayList of blocks/free blocks with methods that manage it.
public class BlockList 
{
    private int numBlocks = 0;
    private int numFreeBlocks = 0;
    private final ArrayList<Block> blocks;
    private final ArrayList<Block> freeBlocks;
    
    //ArrayLists are used since they expand automatically, unlike arrays.
    //Certain methods below create queue like properties but overall every element needs to get accessed in order to 
    //be displayed in the memory panel. Therefore ArrayLists are preferred over everything else.
    public BlockList()
    {
        blocks = new ArrayList();
        freeBlocks = new ArrayList();
    }
    
    public ArrayList<Block> getBlocks()
    {
        return blocks; 
    }
    
    public ArrayList<Block> getFreeBlocks()
    {
        return freeBlocks;
    }
    
    public int getNumBlocks()
    {
        return numBlocks;
    }
    
    public int getNumFreeBlocks()
    {
        return numFreeBlocks;
    }
    
    //Used when changing the total memory in order to work backwards by looking if the last block is affected.
    public Block getLastBlock()
    {
        return blocks.get(numBlocks - 1);
    }//end method
    
    //Returns the adjacent blocks of a given block. [0] = left block, [1] = right block
    //Binary search is used to find the position since the blocks ArrayList is sorted by baseAddress.
    private Block[] findAdjacentBlocks(Block block)
    {
        Block[] adjBlocks = new Block[2];
        int left = 0;
        int right = blocks.size() - 1;
        int mid;
        
        while(left <= right)
        {
            mid = (left + right) / 2;
            
            if(block.compareTo(blocks.get(mid)) < 0)
            {
                right = mid - 1; //In lower half
            }//end if
            else if(block.compareTo(blocks.get(mid)) > 0)
            {
                left = mid + 1; //In upper half
            }//end if
            else //Found
            {
                if(mid > 0) //There exists a left block.
                {
                    adjBlocks[0] = blocks.get(mid - 1);
                }//end if
                
                if(mid < blocks.size() - 1) //There exists a right block.
                {
                    adjBlocks[1] = blocks.get(mid + 1);
                }//end if
                
                return adjBlocks;
            }//end else
        }//end while
        
        return adjBlocks;
    }//end method
    
    //Blocks are connected to processes by the associated PID.
    //Blocks are found sequentially since they are sorted by baseAddress not PID.
    private Block findBlockByAssociatedPID(int PID)
    {
        for(Block block: blocks)
        {
            if(block.getAssociatedPID() == PID)
            {
                return block;
            }//end if
        }//end loop
        
        return null; //Didn't find the block. Shouldn't ever get to this point since non-free blocks should have valid associated PIDs.
    }//end method
    
    //Add the block created from placeBlock in Algorithms or splitFreeBlock or readd a removed block due to size changes.
    public void addBlock(Block block)
    {
        blocks.add(block);
        numBlocks++;
        
        if(block.getFree())
        {
            freeBlocks.add(block);
            numFreeBlocks++;
        }//end if
        
        //Need to keep blocks and freeBlocks sorted by base address.
        sortBlocks();
        sortFreeBlocks();
    }//end method
    
    public void removeBlock(Block block)
    {
        if(block != null)
        {
            blocks.remove(block);
            numBlocks--;

            if(block.getFree()) //Need this check in order to keep numFreeBlocks accurate.
            {
                freeBlocks.remove(block);
                numFreeBlocks--;
            }//end if
            else //Non-free block replaced with free block.
            {
                Block newBlock = new Block(block.getBaseAddress(), block.getSize(), true);
                addBlock(newBlock);
                consolidateFreeBlocks();
            }//end else
        }//end if
    }//end method
    
    public void removeBlockByAssociatedPID(int PID)
    {
        removeBlock(findBlockByAssociatedPID(PID));
    }//end method
    
    public void removeAllBlocks() 
    {
        blocks.clear();
        freeBlocks.clear();
        numBlocks = 0;
        numFreeBlocks = 0;
    }//end method
    
    //decreasingTotalSize in MemoryPanel needs to delete non-free blocks without replacement, otherwise it can 
    //cause issues in the list of blocks.
    public void deleteBlockWithoutReplacement(Block block)
    {
        if(block != null)
        {
            blocks.remove(block);
            numBlocks--;

            if(block.getFree()) //Need this check in order to keep numFreeBlocks accurate.
            {
                freeBlocks.remove(block);
                numFreeBlocks--;
            }//end if
        }//end if
    }//end method
    
    //Moves non-free blocks closer to baseAddress 0 and creates a large free block as the last block (closest to totalSize).
    public void compactBlocks(int totalSize)
    {
        ArrayList<Block> removeList = new ArrayList(); //Can't remove blocks until the end since an enhanced for loop is used.
        int baseAddress = 0; //Where the next non-free block needs to be placed after intermediary free blocks get removed.
        int freedSize = 0; //Combined sizes of the free blocks that is used to create the large free block.
        boolean lastBlockFree = false;

        for(Block block: blocks)
        {
            //If a free block is found, it is removed so that the next non-free block can be put into its place.
            if(block.getFree()) 
            {
                if(block == blocks.get(numBlocks - 1)) //Need to know if the last block is free so that it can be readded.
                {
                    lastBlockFree = true;
                }//end if
                
                removeList.add(block);
                freedSize += block.getSize();
            }//end if
            else 
            {
                block.setBaseAddress(baseAddress);
                baseAddress = block.getBaseAddress() + block.getSize();
            }//end else
        }//end loop

        for(Block block: removeList)
        {
            removeBlock(block);
        }//end loop
        
        if(lastBlockFree) //The last block was free and got deleted. Therefore it needs to be readded.
        {
            addBlock(new Block(baseAddress, totalSize - baseAddress, true));
        }//end if
        else 
        {
            if(freedSize != 0) //There isn't a free block at the end but there were other free blocks so consolidate them into the right side of memory.
            {
                addBlock(new Block(baseAddress, freedSize, true));
            }//end if
        }//end else
    }//end method
    
    //When a non-free block gets removed, it needs to be replaced with a free block.
    //This algorithm checks every block's left block in order to consolidate adjacent free blocks into a single free block.
    private void consolidateFreeBlocks()
    {
        Block left, current;
        Block[] adjBlocks;
        
        //Check every free block's left block in freeBlocks to see if they are actually adjacent. 
        //If there are multiple free blocks in a row, they will end up consolidated.
        for(int i = 1; i < numFreeBlocks; i++)
        {    
            left = freeBlocks.get(i - 1);
            current = freeBlocks.get(i);
            adjBlocks = findAdjacentBlocks(current);
            
            if(adjBlocks[0] == left) 
            {
                consolidateTwoFreeBlocks(left, current);
                i--; //Retest same position (needed for when a non-free block changes to free and is already between 2 free blocks)
            }//end if
        }//end loop
    }//end method
    
    //Consolidate the left and current block found from consolidateFreeBlocks.
    private void consolidateTwoFreeBlocks(Block left, Block right)
    {
        int baseAddress = left.getBaseAddress();
        int size = left.getSize() + right.getSize();
        
        removeBlock(left);
        removeBlock(right);
        addBlock(new Block(baseAddress, size, true));
    }//end method
    
    //Splits free block into a non-free block and smaller free block.
    public void splitFreeBlock(Block freeBlock, int newBlockSize, int newBlockPID)
    {
        int newFreeBlockBaseAddress = freeBlock.getBaseAddress() + newBlockSize;
        int newFreeBlockSize = freeBlock.getSize() - newBlockSize;
        int newBlockBaseAddress = freeBlock.getBaseAddress();
        Block newBlock;
        
        if(newFreeBlockSize == 0) //New block will take up the whole free block so remove the free block.
        {
            removeBlock(freeBlock);
        }//end if
        else //Decrease the free block's size to the new value.
        {
            freeBlock.setBaseAddress(newFreeBlockBaseAddress);
            freeBlock.setSize(newFreeBlockSize);
        }//end else
        
        newBlock = new Block(newBlockBaseAddress, newBlockSize, newBlockPID, false);
        addBlock(newBlock);
        
        sortBlocks();
        sortFreeBlocks();
    }//end method
    
    //Since the object Block implements Comparable, Collections.sort() can do the work
    //using merge sort in order to keep the natural ordering by base address.
    public void sortBlocks()
    {
        Collections.sort(blocks);
    }//end method
    
    public void sortFreeBlocks()
    {
        Collections.sort(freeBlocks);
    }//end method
    
    //For testing purposes.
    public void outputBlockList()
    {
        for(Block block: blocks)
        {
            System.out.println(block.getBaseAddress() + " " + block.getSize() + " " + block.getAssociatedPID() + " " + block.getFree());
        }//end loop
        
        System.out.println();
    }//end method
}//end class
