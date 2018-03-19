//Brandon Selzer

package memorymanager.Algorithms;

import memorymanager.Blocks.Block;
import memorymanager.Blocks.BlockList;
import memorymanager.Processes.ProcessList;
import memorymanager.Processes.Process;

public class BestFit extends Algorithm
{
    public BestFit(ProcessList processList, BlockList blockList)
    {
        super(processList, blockList);
    }

    @Override
    public boolean placeBlock(Process process) 
    {
        int blockSize = process.getSize();
        Block smallestBlock = new Block(0, Integer.MAX_VALUE, true);
        
        //Find the smallest hole that is big enough for the new block to fit in.
        for(Block freeBlock: super.getBlockList().getFreeBlocks())
        {
            if(freeBlock.getSize() < smallestBlock.getSize() && freeBlock.getSize() >= blockSize)
            {
                smallestBlock = freeBlock;
            }//end if
        }//end loop
           
        if(smallestBlock.getSize() == Integer.MAX_VALUE) //There wasn't a hole of sufficient size. Add new block to the waiting queue.
        {
            super.getProcessList().addWaitingProcess(process);
            return false;
        }//end if
        else //Found the smallest hole that the new block can fit in.
        {
            super.getBlockList().splitFreeBlock(smallestBlock, blockSize, process.getPID());
            return true;
        }//end else
    }//end method
}//end class
