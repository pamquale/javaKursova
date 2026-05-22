package ua.edu.university.lab10.exception;

/**
 * Исключение, выбрасываемое при попытке создать дубликат книги.
 */
public class CourseAlreadyExistsException extends RuntimeException {
    public CourseAlreadyExistsException(String message) {
        super(message);
    }
}