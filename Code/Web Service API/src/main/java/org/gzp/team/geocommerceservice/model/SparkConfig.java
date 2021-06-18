package org.gzp.team.geocommerceservice.model;

public class SparkConfig {

    private String dataSize;
    private String maxSpatialDistance;
    private String maxTemporalDistance;
    private String spatialStep;
    private String temporalStep;
    private String numExecutors;
    private String executorCores;
    private String executorMemory;

    public SparkConfig() {
    }

    public String getDataSize() {
        return dataSize;
    }

    public void setDataSize(String dataSize) {
        this.dataSize = dataSize;
    }

    public String getMaxSpatialDistance() {
        return maxSpatialDistance;
    }

    public void setMaxSpatialDistance(String maxSpatialDistance) {
        this.maxSpatialDistance = maxSpatialDistance;
    }

    public String getMaxTemporalDistance() {
        return maxTemporalDistance;
    }

    public void setMaxTemporalDistance(String maxTemporalDistance) {
        this.maxTemporalDistance = maxTemporalDistance;
    }

    public String getSpatialStep() {
        return spatialStep;
    }

    public void setSpatialStep(String spatialStep) {
        this.spatialStep = spatialStep;
    }

    public String getTemporalStep() {
        return temporalStep;
    }

    public void setTemporalStep(String temporalStep) {
        this.temporalStep = temporalStep;
    }

    public String getNumExecutors() {
        return numExecutors;
    }

    public void setNumExecutors(String numExecutors) {
        this.numExecutors = numExecutors;
    }

    public String getExecutorCores() {
        return executorCores;
    }

    public void setExecutorCores(String executorCores) {
        this.executorCores = executorCores;
    }

    public String getExecutorMemory() {
        return executorMemory;
    }

    public void setExecutorMemory(String executorMemory) {
        this.executorMemory = executorMemory;
    }

    @Override
    public String toString() {
        return "SparkConfig{" +
                "dataSize='" + dataSize + '\'' +
                ", maxSpatialDistance='" + maxSpatialDistance + '\'' +
                ", maxTemporalDistance='" + maxTemporalDistance + '\'' +
                ", spatialStep='" + spatialStep + '\'' +
                ", temporalStep='" + temporalStep + '\'' +
                ", numExecutors='" + numExecutors + '\'' +
                ", executorCores='" + executorCores + '\'' +
                ", executorMemory='" + executorMemory + '\'' +
                '}';
    }
}
