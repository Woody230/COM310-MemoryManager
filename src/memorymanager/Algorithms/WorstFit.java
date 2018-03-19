//Brandon Selzer

package memorymanager.Algorithms;

import memorymanager.Blocks.Block;
import memorymanager.Blocks.BlockList;
import memorymanager.Processes.ProcessList;
import memorymanager.Processes.Process;

public class WorstFit extends Algorithm
{
    public WorstFit(ProcessList processList, BlockList blockList)
    {
        super(processList, blockList);
    }

    @Override
    public boolean placeBlock(Process process) 
    {
        int blockSize = process.getSize();
        Block biggestBlock = new Block(0, Integer.MIN_VALUE, true);
        
        //Find the largest hole that is big enough for the new block to fit in.
        for(Block freeBlock: super.getBlockList().getFreeBlocks())
        {
            if(freeBlock.getSize() > biggestBlock.getSize() && freeBlock.getSize() >= blockSize)
            {
                biggestBlock = freeBlock;
            }//end if
        }//end loop
        
        if(biggestBlock.getSize() == Integer.MIN_VALUE) //There wasn't a hole of sufficient size. Add it to the waiting queue.
        {
            super.getProcessList().addWaitingProcess(process);
            return false;
        }//end if
        else //Found the largest hole that the new block can fit in.
        {
            super.getBlockList().splitFreeBlock(biggestBlock, blockSize, process.getPID());
            return true;
        }//end else
    }//end method
}//end class
