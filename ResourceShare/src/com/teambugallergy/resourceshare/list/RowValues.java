package com.teambugallergy.resourceshare.list;

/**
 * This Class has <b>String[] data</b> to store all the values of views in a row item.
 * This class has methods to set and get each values of a row.
 * The order of String values should be same as the order of the views inside a Row.
 * 
 * @since 04-04-2014
 * @author Adiga@TeamBugAllergy
 * 
 */
public class RowValues {
	
	/**
	 * Array of Texts to be displayed in a row.
	 */
	private String[] data;
		
	public RowValues(int size) {
	
		data = new String[size];
	}
    public RowValues(String[] d){
    	
    	data = d;
    }
    
    /**
     * Sets the Value of (index)th View in the row with the value <b>d</b>
     * @param index Position of the row, starting with 0.
     * @param d String value to be set to the View.
     */
    public void setRowValues(int index, String d){
        
    	data[index] = d;
    }
    
    /**
     * Returns the String value associated with the (index)th View
     * @param index Index of the View in the row.
     * @return Text associated with the View.
     */
    public String getRowValues(int index){
    
    	return data[index];
    }
	
    /**
     * 
     * @return Number of String values in the data[]. <b>This must be same as the number of Views in the row<b/>.
     */
    public int getNumberOfValues(){
    
    	return data.length;
    }       


}
