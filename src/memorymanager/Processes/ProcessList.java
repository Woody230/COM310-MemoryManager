//Brandon Selzer

package memorymanager.Processes;

import java.util.ArrayList;
import java.util.Random;

//This class was created in order to have an ArrayList of processes with methods that manage it.
public class ProcessList 
{
    private int numProcesses = 0;
    private int numProcessesWaiting = 0;
    private final ArrayList<Process> processes;
    private final ArrayList<Process> waitingQueue;
    
    //ArrayLists are used since they expand automatically.
    //Some of the methods below create queue like properties but every element needs to accessed so ArrayLists are used.
    public ProcessList()
    {
        processes = new ArrayList();
        waitingQueue = new ArrayList();
    }
    
    public ArrayList<Process> getListOfProcesses()
    {
        return processes;
    }
    
    public ArrayList<Process> getWaitingQueue()
    {
        return waitingQueue;
    }
    
    public int getNumProcesses()
    {
        return numProcesses;
    } 
    
    public int getNumProcessesWaiting()
    {
        return numProcessesWaiting;
    }
    
    public Process getFirstProcessWaiting()
    {
        return waitingQueue.get(0);
    }
 
    public void addProcess(Process process)
    {
        processes.add(process);
        numProcesses++;
    }
    
    public void addWaitingProcess(Process process)
    {
        waitingQueue.add(process);
        numProcessesWaiting++;
    }
    
    //Used by the ProcessPanel to continuously remove the last process in order to remove processes.
    public void removeLastProcess()
    {
        processes.remove(numProcesses - 1);
        numProcesses--;
    }

    public void removeFirstWaitingProcess()
    {
        waitingQueue.remove(0);
        numProcessesWaiting--;
    }
    
    public void removeWaitingProcessByPID(int PID)
    {
        Process process = processes.get(PID - 1);
        
        //Need this check in order to keep numProcessesWaiting accurate
        if(waitingQueue.contains(process))
        {
            waitingQueue.remove(process);
            numProcessesWaiting--;
        }//end if
    }//end method
    
    public void clearWaitingQueue()
    {
        waitingQueue.clear();
        numProcessesWaiting = 0;
    }//end method
    
    //PIDs stay the same. Only size needs to be reset.
    public void resetListOfProcesses()
    {
        for(int i = 0; i < numProcesses; i++)
        {
            processes.get(i).setSize(0);
        }//end loop
    }//end method
    
    //Invoker will have to set the size of the process using the return of this method.
    //Range from 1 to totalSize / 4.
    public int getRandomSize(int totalSize)
    {
        Random rng = new Random();
        return rng.nextInt(totalSize / 4) + 1;
    }//end method
}//end class
