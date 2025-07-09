package com.dpdocter.request;

import java.util.List;

import com.dpdocter.beans.Age;
import com.dpdocter.beans.TestimonialMedia;

public class AddEditTestimonialRequest {

	private String id;
	private String name;
	private String planId;
	private Age age;
	private List<TestimonialMedia> media;
	private String para;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPlanId() {
		return planId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
	}

	public Age getAge() {
		return age;
	}

	public void setAge(Age age) {
		this.age = age;
	}

	public List<TestimonialMedia> getMedia() {
		return media;
	}

	public void setMedia(List<TestimonialMedia> media) {
		this.media = media;
	}

	public String getPara() {
		return para;
	}

	public void setPara(String para) {
		this.para = para;
	}

}
