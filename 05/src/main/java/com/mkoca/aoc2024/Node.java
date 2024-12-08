package com.mkoca.aoc2024;

import java.util.ArrayList;
import java.util.List;

public class Node implements Cloneable {
    private String value;
    private List<Node> dependencies;
    private boolean visited;
    private boolean partOfReport;

    public Node(String value) {
        this.value = value;
        this.dependencies = new ArrayList<>();
        this.visited = false;
        this.partOfReport = false;
    }

    //region Getters and Setters

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<Node> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<Node> dependencies) {
        this.dependencies = dependencies;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public boolean isPartOfReport() {
        return partOfReport;
    }

    public void setPartOfReport(boolean partOfReport) {
        this.partOfReport = partOfReport;
    }

    //endregion

    @Override
    public String toString() {
        return this.value;
    }
}
