package com.dpdocter.beans;

import com.dpdocter.enums.AppType;
import com.dpdocter.enums.DeviceType;

/**
 * 
 * @author parag
 *
 */

public class VersionControl {

	private String id;
	private AppType appType;
	private DeviceType deviceType;
	private Integer majorVersion;
	private Integer minorVersion;
	private Integer patchVersion;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public AppType getAppType() {
		return appType;
	}

	public void setAppType(AppType appType) {
		this.appType = appType;
	}

	public DeviceType getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
	}

	public Integer getMajorVersion() {
		return majorVersion;
	}

	public void setMajorVersion(Integer majorVersion) {
		this.majorVersion = majorVersion;
	}

	public Integer getMinorVersion() {
		return minorVersion;
	}

	public void setMinorVersion(Integer minorVersion) {
		this.minorVersion = minorVersion;
	}

	public Integer getPatchVersion() {
		return patchVersion;
	}

	public void setPatchVersion(Integer patchVersion) {
		this.patchVersion = patchVersion;
	}

	@Override
	public String toString() {
		return "VersionControl [id=" + id + ", appType=" + appType + ", deviceType=" + deviceType + ", majorVersion="
				+ majorVersion + ", minorVersion=" + minorVersion + ", patchVersion=" + patchVersion + "]";
	}


}
