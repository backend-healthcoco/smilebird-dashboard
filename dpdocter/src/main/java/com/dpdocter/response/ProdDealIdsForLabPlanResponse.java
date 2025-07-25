package com.dpdocter.response;



import com.dpdocter.collections.GenericCollection;

public class ProdDealIdsForLabPlanResponse extends GenericCollection{
	
    private String id;

     
    private String healthiens_dealId;
    
     
    private String thyrocare_dealId;

     
    private String healthiens_sellingPrice;

     
    private String healthiens_pickupCharge;

     
    private String packageName;

     
    private String healthcoco_sellingPrice;


	private Boolean isDiscarded ;


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getHealthiens_dealId() {
		return healthiens_dealId;
	}


	public void setHealthiens_dealId(String healthiens_dealId) {
		this.healthiens_dealId = healthiens_dealId;
	}


	public String getThyrocare_dealId() {
		return thyrocare_dealId;
	}


	public void setThyrocare_dealId(String thyrocare_dealId) {
		this.thyrocare_dealId = thyrocare_dealId;
	}


	public String getHealthiens_sellingPrice() {
		return healthiens_sellingPrice;
	}


	public void setHealthiens_sellingPrice(String healthiens_sellingPrice) {
		this.healthiens_sellingPrice = healthiens_sellingPrice;
	}


	public String getHealthiens_pickupCharge() {
		return healthiens_pickupCharge;
	}


	public void setHealthiens_pickupCharge(String healthiens_pickupCharge) {
		this.healthiens_pickupCharge = healthiens_pickupCharge;
	}


	public String getPackageName() {
		return packageName;
	}


	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}


	public String getHealthcoco_sellingPrice() {
		return healthcoco_sellingPrice;
	}


	public void setHealthcoco_sellingPrice(String healthcoco_sellingPrice) {
		this.healthcoco_sellingPrice = healthcoco_sellingPrice;
	}


	public Boolean getIsDiscarded() {
		return isDiscarded;
	}


	public void setIsDiscarded(Boolean isDiscarded) {
		this.isDiscarded = isDiscarded;
	}
    
	
    
}
