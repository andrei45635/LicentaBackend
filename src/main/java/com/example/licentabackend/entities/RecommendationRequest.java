package com.example.licentabackend.entities;

import java.util.List;

public class RecommendationRequest {
    private List<TrackDetail> cosine;
    private List<TrackDetail> euclidean;
    private List<TrackDetail> pearson;
    private String access_token;

    // Getters and setters
    public List<TrackDetail> getCosine() {
        return cosine;
    }

    public void setCosine(List<TrackDetail> cosine) {
        this.cosine = cosine;
    }

    public List<TrackDetail> getEuclidean() {
        return euclidean;
    }

    public void setEuclidean(List<TrackDetail> euclidean) {
        this.euclidean = euclidean;
    }

    public List<TrackDetail> getPearson() {
        return pearson;
    }

    public void setPearson(List<TrackDetail> pearson) {
        this.pearson = pearson;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }
}

