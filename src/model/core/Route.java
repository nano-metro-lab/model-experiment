package model.core;

public record Route(
  Station next,
  Station last,
  int length,
  int transfer
) {
}
