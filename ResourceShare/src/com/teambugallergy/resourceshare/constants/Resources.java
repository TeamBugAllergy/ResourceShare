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
	
	//RESOURCE AVAILABILITY CONSTANTS
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
	
	//Possible statuses 
	/**
	 * User at Provider end has ACCEPTED to share the resource.
	 */
	public static final int REQUEST_ACCEPTED = 705;
	
	/**
	 * User at Provider end has REJECTED to share the resource.
	 */
	public static final int REQUEST_REJECTED = 706;
	
	//------------------------------------------------------------------------------------------------
	/**
	 * 'what' of a message read from a seeker device containing a Resource Id
	 * that has been requested by the seeker.
	 */
	public static final int REQUESTING_RESOURCE_ID = 707;
	
	//RESOURCE CONSTANTS
	//------------------------------------------------------------------------------------------------
	
	//FLASH: STARTS WITH 710
	public static final int FLASH = 710;
	public static final int FLASH_CONTROL = 711;
	public static final int FLASH_SWITCH_ON = 712;
	public static final int FLASH_SWITCH_OFF = 713;
	//------------------------------------------------------------------------------------------------
	
	//WIFI: STARTS WITH 720
	public static final int WIFI = 720;
	//------------------------------------------------------------------------------------------------
	
	//GPS: STARTS WITH 730
	public static final int GPS = 730;
	//------------------------------------------------------------------------------------------------
	
	//CAMERA: STARTS WITH 740
	public static final int CAMERA = 740;
	//------------------------------------------------------------------------------------------------
	
	//SPEAKER: STARTS WITH 750
	public static final int SPEAKER = 750;
	//------------------------------------------------------------------------------------------------
	
	//ACCESSING RESOURCES
	//STARTS WITH 800
	
	/**
	 * 'what' of a message that tells that message is about 'Requesting Access to a Resource'. 
	 */
	public static final int RESOURCE_ACCESS_REQUEST = 800;
	
	/** 
	 * The Potential provider has Accepted to access the resource.
	 */
	public static final int RESOURCE_ACCESS_GRANTED = 801;
	
	/**
	 * The Potential provider has Denied to access the resource.
	 */
	public static final int RESOURCE_ACCESS_DENIED = 802;
	
	//-------------------
	/**
	 * The Control message to tell to switch on/off the flash.
	 */
	public static final int SHARING_CONTROL = 803;
	
	/**
	 * Message to start sharing the resource.
	 */
	public static final int START_SHARING = 805;

	/**
	 * Message to stop sharing the resource.
	 */
	public static final int STOP_SHARING = 804;
	//--------------------
	/**
	 * The State of the sharing process Stopped/Started. 
	 */
	public static final int SHARING_STATUS = 806;
	
	/**
	 * Message to telling the Resource is NOT SHARED.
	 */
	public static final int SHARING_STOPPED = 807;
	
	/**
	 * Message to telling the Resource is SHARED.
	 */
	public static final int SHARING_STARTED = 808;
	//------------------------------------------------------------------------------------------------
}
