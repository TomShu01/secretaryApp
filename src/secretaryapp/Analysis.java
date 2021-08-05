
package secretaryapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import org.jfree.data.statistics.Statistics;


public class Analysis {
    
    public Analysis() {
    
    }
    
    //part of quicksort
    static public int Partition (Object[][] A, int start, int end) {
        Object pivot = A[end][1];
        int partitionIndex = start;
        if (Double.class == A[0][1].getClass()) {
            for (int i = start;i<end;i++) {
                if ((Double)A[i][1] >= (Double)pivot) {
                //Collections.swap(A, i, partitionIndex);
                swap (A, i, partitionIndex);
                partitionIndex++;
                }//this sorts the numbers into decreasing order: you want to do the longest (timeBudget), most exhausting (fatigueLevel) and most rewarding (utility, utilityPerCapita) tasks first
            }
        } else if (String.class == A[0][1].getClass()) {
            for (int i = start;i<end;i++)    {
                if (((String)A[i][1]).compareTo((String)pivot)<=0) {
                swap (A, i, partitionIndex);
                partitionIndex++;
                }
            }//this places the tasks with the same subjects or names together. Although this sounds lazy, doing the same type of work together is indeed more efficient than other arrangements
        } else {
            for (int i = start;i<end;i++)    {
                if (((Date)A[i][1]).compareTo((Date)pivot)<=0) {
                swap (A, i, partitionIndex);
                partitionIndex++;
                }
            }//this sorts the dates into increasing order: you want to do the earliest task first
        }
        //Collections.swap(A, partitionIndex, end);
        swap (A, partitionIndex, end);
        return partitionIndex;
    }
    
    //sorts an array really fast
    static public void QuickSort (Object[][] A, int start, int end) {
        if (start < end) {
            int partitionIndex = Partition (A, start, end);
            QuickSort (A, start, partitionIndex-1);
            QuickSort (A, partitionIndex + 1, end);
        }
    }
    
    //swaps items
    static public void swap (Object[][] A, int index, int index2) {
        Object temp[] =A[index];
        A[index] = A[index2];
        A[index2] = temp;
    }
    
    //cancelled method
    public ArrayList SearchTask () {
        return (null);
    }
    
    //calculates the utilityPerCapita for each task in availableTasks
    //achieves one of the four key functions of this project - machine learns from the data stored in journal.txt and automatically adjusts my recommended schedule
    //the recommended order adjusts following this formula: actualUtility = expectedUtility * fatigueThreshold * attentionThreshold * expectationThreshold, whereas fatigueThreshold = fatigueLevel/averagefatigue, attentionThreshold = averagetime/timeBudget, expectationThreshold = averagebudget/timeBudget
    //*utility: a term in economics that describes the amount of satisfaction you get
    static public Object [][] getUtilityPerCapita (ArrayList<ArrayList> finishedTasks, ArrayList availableTasks, ArrayList<ArrayList> taskList) {
        Double timespent[] = new Double[finishedTasks.size()];
        Double fatiguelevel [] = new Double[finishedTasks.size()];
        Double timebudget[] = new Double[finishedTasks.size()];
        for (int k = 0; k<finishedTasks.size();k++) {
            timespent[k] = (Double)finishedTasks.get(k).get(10);
            fatiguelevel[k] = (Double)finishedTasks.get(k).get(6);
            timebudget[k] = (Double)finishedTasks.get(k).get(4);
        }
        double averagetime = Statistics.calculateMean(timespent);
        double averagefatigue = Statistics.calculateMean(fatiguelevel);
        double averagebudget = Statistics.calculateMean(timebudget);
        Object utilityPerCapita [][] = new Object[availableTasks.size()][2];
        for (int k = 0; k < availableTasks.size(); k++) {
            double timeBudget = (double)taskList.get((int)availableTasks.get(k)).get(4);
            double fatigueLevel = (double)taskList.get((int)availableTasks.get(k)).get(6);
            double fatigueThreshold = fatigueLevel/averagefatigue;
            double attentionThreshold = averagetime/timeBudget;
            double expectationThreshold = averagebudget/timeBudget;
            double expectedUtility = (double)taskList.get((int)availableTasks.get(k)).get(5);
            double actualUtility = expectedUtility * fatigueThreshold * attentionThreshold * expectationThreshold;
            utilityPerCapita [k][1] = actualUtility/(timeBudget + fatigueLevel);
            utilityPerCapita [k][0] = (int)availableTasks.get(k);
        }
        return (utilityPerCapita);
    }

    //this method will adjust the thresholds according to the record in finishedTasks, thus learning the best order of tasks over time
    //calculates utilityPerCapita for each available task, sorts them from greatest to smallest, then returns the sorted ArrayList
    static public ArrayList getRecommended (ArrayList<ArrayList> finishedTasks, ArrayList availableTasks, ArrayList<ArrayList> taskList) {
        Object[][] utilityPerCapita = getUtilityPerCapita (finishedTasks, availableTasks, taskList);
        QuickSort (utilityPerCapita,0,availableTasks.size()-1);
        ArrayList<Integer> temp = availableTasks;
        for (int k = 0; k < availableTasks.size(); k++) {
            temp.set(k,(Integer)utilityPerCapita[k][0]);
        }
        return (temp);//ranks utility/input from high to low
    }
    
    //returns currentTasks in alternative order
    static public ArrayList getOrder (String type1, ArrayList<ArrayList> finishedTasks, ArrayList availableTasks, ArrayList<ArrayList> taskList) {
        if (type1.equals("recommended")){
            return (getRecommended(finishedTasks,availableTasks, taskList));
        }
        ArrayList<String> taskTraits = new ArrayList<>(Arrays.asList("name", "subTasks", "types", "subject", "timeBudget","utility", "fatigueLevel", "dl", "deadline"));
        int type = taskTraits.indexOf (type1);
        Object traits[][] = new Object[availableTasks.size()][2];
        for (int k = 0; k < availableTasks.size(); k++) {
            traits[k][1] = taskList.get((int)availableTasks.get(k)).get(type);
            traits[k][0] = (int)availableTasks.get(k);
        }
        ArrayList<Integer> temp = availableTasks;
        QuickSort (traits,0,availableTasks.size()-1);
        for (int k = 0; k < availableTasks.size(); k++) {
            temp.set(k,(Integer)traits[k][0]);
        }
        return (temp);
    }
}
