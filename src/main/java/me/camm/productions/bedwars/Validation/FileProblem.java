package me.camm.productions.bedwars.Validation;

public enum FileProblem
{
    FILE_NOT_FOUND("File not found."),
    CANNOT_ACCESS_FILE("Can't access file."),
    DUPLICATE_FILE_FOUND("Duplicate files."),
    OTHER_PROBLEM("Other problem/unknown.");

    private final String problem;

    FileProblem(String problem)
    {
        this.problem = problem;
    }

    public String getProblem()
    {
        return problem;
    }
}
