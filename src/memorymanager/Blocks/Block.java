//Brandon Selzer

package memorymanager.Blocks;

public class Block implements Comparable<Block>
{
    private int baseAddress;
    private int size; 
    private int associatedPID = 0; //PIDs start at 1 so 0 is the default.
    private boolean free; 
    
    //Blocks have a base address, size. Blocks are either non-free and thus have an associated process (connected by PID) or are free.
    public Block(int baseAddress, int size, boolean free)
    {
        this.baseAddress = baseAddress;
        this.size = size;
        this.free = free;
    }//end constructor
    
    public Block(int baseAddress, int size, int associatedPID, boolean free)
    {
        this.baseAddress = baseAddress;
        this.size = size;
        this.associatedPID = associatedPID;
        this.free = free;
    }//end constructor
    
    public int getBaseAddress()
    {
        return baseAddress;
    }
    
    public int getSize()
    {
        return size;
    }
    
    public int getAssociatedPID()
    {
        return associatedPID;
    }
    
    public boolean getFree()
    {
        return free;
    }
    
    public void setBaseAddress(int baseAddress)
    {
        this.baseAddress = baseAddress;
    }
    
    public void setSize(int size)
    {
        this.size = size;
    }
    
    public void setFree(boolean free)
    {
        this.free = free;
    }

    //The natural ordering of blocks is by base address.
    //This is used in the binary search for findAdjacentBlocks in BlockList.
    //This is used to call Collections.sort() in BlockList and use its merge sort as opposed to coding a sort.
    @Override
    public int compareTo(Block block) 
    {
        if(this.baseAddress < block.getBaseAddress())
        {
            return -1;
        }//end if
        else if(this.baseAddress == block.getBaseAddress())
        {
            return 0;
        }//end if
        else
        {
            return 1;
        }//end else
    }//end method
}//end class
