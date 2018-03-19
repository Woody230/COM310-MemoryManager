//Brandon Selzer

package memorymanager.Algorithms;

import memorymanager.Blocks.Block;
import memorymanager.Blocks.BlockList;
import memorymanager.Processes.ProcessList;
import memorymanager.Processes.Process;

public class FirstFit extends Algorithm
{
    public FirstFit(ProcessList processList, BlockList blockList)
    {
        super(processList, blockList);
    }

    @Override
    public boolean placeBlock(Process process) 
    {
        int blockSize = process.getSize();

        //Find the first hole that is big enough for the new block to fit in.
        for(Block freeBlock: super.getBlockList().getFreeBlocks())
        {
            if(freeBlock.getSize() >= blockSize) 
            {
                super.getBlockList().splitFreeBlock(freeBlock, blockSize, process.getPID());
                return true;
            }//end if
        }//end loop

        //New block can't fit, so add it to waiting queue.
        super.getProcessList().addWaitingProcess(process);
        return false;
    }//end method
}//end class
