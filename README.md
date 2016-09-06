Variator For Live

This Repository contains the frozen Max for Live device (ie .amxd), which contains all relevent files.  

Also included, is "VariatorForLive", the Java source code that runs the powers the device.  This code is accessed within the M4L device through the use of the Max Object "VariatorObject".  

If the source code is to be edited and the device refrozen with new source code for the mxj external:

    - MaxForLive must know where to find the class files that contain VariatorObject.java (This is done on Macs by editing the MaxForLive configuration file located in the application files).

    - The M4L object "VariatorObject" must be reloaded once the referenced classes are saved (preferably by retyping "mxj VariatorObject" within the M4L object box.

