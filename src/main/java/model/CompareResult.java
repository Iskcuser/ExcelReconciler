package model;

public record CompareResult(Status status) {

    public enum Status {
        MATCH,
        SKIP
    }
}
