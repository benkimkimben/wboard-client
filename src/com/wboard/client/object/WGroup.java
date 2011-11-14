package com.wboard.client.object;

import com.wboard.WObject;

/**
 * Not in use
 * @author Ben Kim
 *
 */
public class WGroup extends WObject{
		
	private static final long serialVersionUID = -8160551506855644620L;
/*		
	private static int gidCounter = 1; // gid counter for setting incremental gid; gid=0 represents no group affiliation, therefore not used.
	private Map<Integer, WClientObject> members; // map contains object keyed by its oid
	
	private final int gid;
	
	public WGroup() {
		super();
		gid = gidCounter++;		
		members = new HashMap<Integer, WClientObject>();	// list »ý¼º
	}
	
	public WGroup(List<WClientObject> objects) {
		this();
		
		for(WClientObject member: objects){
			addMember(member);			
		}		
	}
	
	public void addMember(WClientObject wcObj){
		//TODO: need to remove wcObj from its original group
		
		wcObj.setGid(gid);
		members.put(wcObj.getOid(), wcObj);
	}
	public void removeMember(int oid){
		// remove member
		WClientObject wcObj= members.remove(oid);
		// set wcObject's gid back to 0
		wcObj.setGid(0);
		
		//TODO: if this group is empty after the removal then destroy itself from the grouplist
	}
	public WClientObject getMember(int oid){
		return members.get(oid);
	}

	public int getGid() {
		return gid;
	}
*/
}
