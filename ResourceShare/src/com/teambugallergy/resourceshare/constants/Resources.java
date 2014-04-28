package com.teambugallergy.resourceshare.constants;

/**
 * This Interface contains all the Constants related to Resources. These
 * constants are used by both Provider and Seeker end modules.
 * 
 * <i> <br/>
 * -------------------------------------- <br/>
 * Constants of this class starts with 700 <br/>
 * -------------------------------------- <br/>
 * </i>
 * 
 * @author Adiga 22-04-2014
 */
public interface Resources {

	//------------------------------------------------------------------------------------------------
	/**
	 * Status of a given resource.
	 */
	public static final int RESOURCE_STATUS = 700;
	
	//---------------------------------RESOURCE AVAILABILITY CONSTANTS---------------------------------
	/**
	 *  Resource is present in the device and free.
	 */
	public static final int RESOURCE_AVAILABLE = 701;
	
	/**
	 * Resource is absent in the device.
	 */
	public static final int RESOURCE_UNAVAILABLE = 702;
	
	/**
	 * Resource is present in the device but it is not free.
	 */
	public static final int RESOURCE_BUSY = 703;
	
	//------------------------------------------------------------------------------------------------
	/**
	 * Request status that the provider has selected.
	 */
	public static final int REQUEST_STATUS = 704;
	
	/**
	 * User at Provider end has ACCEPTED to share the resource.
	 */
	public static final int REQUEST_ACCEPTED = 705;
	
	/**
	 * User at Provider end has REJECTED to share the resource.
	 */
	public static final int REQUEST_REJECTED = 706;
	
	//----------------------------------RESOURCE CONSTANTS--------------------------------
	/**
	 * 'what' of a message read from a seeker device containing a Resource Id
	 * that has been requested by the seeker.
	 */
	public static final int REQUESTING_RESOURCE_ID = 707;
	
	//----------------------------------STARTS WITH 710--------------------------------------------------
	public static final int FLASH = 710;
	
	public static final int WIFI = 711;
	
	public static final int GPS = 712;
	
	public static final int CAMERA = 713;
	
	public static final int SPEAKER = 714;
	

	//------------------------------------------------------------------------------------------------
}
