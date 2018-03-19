//Brandon Selzer

package memorymanager.Algorithms;

import memorymanager.Blocks.BlockList;
import memorymanager.Processes.ProcessList;
import memorymanager.Processes.Process;

public abstract class Algorithm 
{
    private final ProcessList processList;
    private final BlockList blockList;
    
    //Need to know the processList in order to add waiting processes.
    //Need to know the blockList in order to add new blocks to memory.
    public Algorithm(ProcessList processList, BlockList blockList)
    {
        this.processList = processList;
        this.blockList = blockList;
    }
    
    public ProcessList getProcessList()
    {
        return processList;
    }
    
    public BlockList getBlockList()
    {
        return blockList;
    }
    
    //Will either place the block into memory or onto the waiting queue.
    //Requires knowing the process in order to know what sufficient size is needed and to give the new block its PID/size.
    //The return of true means it was placed in memory. 
    //The return of false means that it was placed on the waiting queue.
    public abstract boolean placeBlock(Process process);
}//end class
