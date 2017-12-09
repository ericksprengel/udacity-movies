package br.com.ericksprengel.android.movies.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieVideoListResponse {

	@SerializedName("id")
	private int id;

	@SerializedName("results")
	private List<MovieVideo> results;

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setResults(List<MovieVideo> results){
		this.results = results;
	}

	public List<MovieVideo> getResults(){
		return results;
	}

	@Override
 	public String toString(){
		return 
			"MovieVideoListResponse{" +
			"id = '" + id + '\'' + 
			",results = '" + results + '\'' + 
			"}";
		}
}