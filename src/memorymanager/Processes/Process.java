//Brandon Selzer

package memorymanager.Processes;

//A process has a PID and a size that it will need to take up in memory.
//Burst times are handled by the user themselves.
public class Process 
{
    private int PID;
    private int size = 0;
    
    public Process(int PID, int size)
    {
        this.PID = PID;
        this.size = size;
    }
    
    public int getPID()
    {
        return PID;
    }
    
    public int getSize()
    {
        return size;
    }
    
    public void setPID(int PID)
    {
        this.PID = PID;
    }
    
    public void setSize(int size)
    {
        this.size = size;
    }
}//end class
