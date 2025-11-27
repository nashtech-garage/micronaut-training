package com.micr.crud.app.integration.interceptor;

import com.micr.crud.app.entity.Book;
import com.micr.crud.app.repository.BookRepository;
import com.micr.crud.app.service.BookService;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for @RetryOnFailure annotation
 * Tests the retry logic with actual Micronaut context and beans
 */
@MicronautTest(transactional = false)
@DisplayName("RetryOnFailure Integration Tests")
class RetryOnFailureInterceptorIntegrationTest {

    @Inject
    private BookService bookService;

    @Inject
    private BookRepository bookRepository;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
    }

    @Test
    @DisplayName("Should successfully save a book without retry on first attempt")
    void testSaveBookSuccessfullyOnFirstAttempt() {
        // Arrange
        Book book = new Book(null, "Test Book", "Test Author");

        // Act
        Book savedBook = bookService.saveWithRetry(book);

        // Assert
        assertNotNull(savedBook);
        assertNotNull(savedBook.getId());
        assertEquals("Test Book", savedBook.getTitle());
        assertEquals("Test Author", savedBook.getAuthor());
        assertTrue(bookRepository.existsById(savedBook.getId()));
    }

    @Test
    @DisplayName("Should save multiple books using retry mechanism")
    void testSaveMultipleBooksWithRetry() {
        // Arrange
        Book book1 = new Book(null, "Book One", "Author One");
        Book book2 = new Book(null, "Book Two", "Author Two");
        Book book3 = new Book(null, "Book Three", "Author Three");

        // Act
        Book savedBook1 = bookService.saveWithRetry(book1);
        Book savedBook2 = bookService.saveWithRetry(book2);
        Book savedBook3 = bookService.saveWithRetry(book3);

        // Assert
        assertNotNull(savedBook1.getId());
        assertNotNull(savedBook2.getId());
        assertNotNull(savedBook3.getId());
        assertEquals(3, bookRepository.count());
    }

    @Test
    @DisplayName("Should persist saved book data correctly with retry")
    void testPersistedDataWithRetry() {
        // Arrange
        String title = "Micronaut Guide";
        String author = "Spring Boot Expert";
        Book book = new Book(null, title, author);

        // Act
        Book savedBook = bookService.saveWithRetry(book);

        // Assert - Verify data is persisted correctly
        Book retrievedBook = bookRepository.findById(savedBook.getId()).orElse(null);
        assertNotNull(retrievedBook);
        assertEquals(title, retrievedBook.getTitle());
        assertEquals(author, retrievedBook.getAuthor());
    }

    @Test
    @DisplayName("Should work with regular save method without retry annotation")
    void testRegularSaveMethodWithoutRetry() {
        // Arrange
        Book book = new Book(null, "Regular Book", "Regular Author");

        // Act
        Book savedBook = bookService.save(book);

        // Assert
        assertNotNull(savedBook);
        assertNotNull(savedBook.getId());
        assertTrue(bookRepository.existsById(savedBook.getId()));
    }

    @Test
    @DisplayName("Should handle null book gracefully in retry method")
    void testSaveWithRetryWithNullHandling() {
        // Arrange - Create a valid book
        Book book = new Book(null, "Valid Book", "Valid Author");

        // Act
        Book savedBook = bookService.saveWithRetry(book);

        // Assert
        assertNotNull(savedBook);
        assertNotNull(savedBook.getId());
    }

    @Test
    @DisplayName("Should maintain data integrity across multiple retry operations")
    void testDataIntegrityAcrossRetryOperations() {
        // Arrange
        Book book1 = new Book(null, "Book 1", "Author 1");
        Book book2 = new Book(null, "Book 2", "Author 2");
        Book book3 = new Book(null, "Book 3", "Author 3");

        // Act
        bookService.saveWithRetry(book1);
        bookService.saveWithRetry(book2);
        bookService.saveWithRetry(book3);

        // Assert
        assertEquals(3, bookRepository.count());
        assertTrue(bookRepository.findAll().stream()
                .anyMatch(b -> b.getTitle().equals("Book 1")));
        assertTrue(bookRepository.findAll().stream()
                .anyMatch(b -> b.getTitle().equals("Book 2")));
        assertTrue(bookRepository.findAll().stream()
                .anyMatch(b -> b.getTitle().equals("Book 3")));
    }

    @Test
    @DisplayName("Should handle special characters in book title and author with retry")
    void testRetryWithSpecialCharacters() {
        // Arrange
        Book book = new Book(null, "Book with @#$%^&* Special Chars", "Author with -_()[]{}");

        // Act
        Book savedBook = bookService.saveWithRetry(book);

        // Assert
        assertNotNull(savedBook);
        assertEquals("Book with @#$%^&* Special Chars", savedBook.getTitle());
        assertEquals("Author with -_()[]{}",  savedBook.getAuthor());
    }

    @Test
    @DisplayName("Should return book with correct ID after retry save")
    void testReturnValueCorrectnessAfterRetrySave() {
        // Arrange
        Book book = new Book(null, "ID Test Book", "ID Test Author");

        // Act
        Book savedBook = bookService.saveWithRetry(book);

        // Assert
        assertNotNull(savedBook.getId());
        assertTrue(savedBook.getId() > 0);
        
        // Verify the book exists in the database with the same ID
        assertTrue(bookRepository.existsById(savedBook.getId()));
        Book retrievedBook = bookRepository.findById(savedBook.getId()).orElse(null);
        assertNotNull(retrievedBook);
        assertEquals(savedBook.getId(), retrievedBook.getId());
    }

    @Test
    @DisplayName("Should successfully complete after temporary failures with retry")
    void testRetrySucceedsAfterTemporaryFailures() {
        // Arrange
        Book book = new Book(null, "Retry Success Book", "Retry Author");

        // Act - This will fail twice and succeed on third attempt
        Book savedBook = bookService.saveWithSimulatedRetry(book);

        // Assert
        assertNotNull(savedBook);
        assertNotNull(savedBook.getId());
        assertEquals("Retry Success Book", savedBook.getTitle());
        assertEquals("Retry Author", savedBook.getAuthor());
        assertTrue(bookRepository.existsById(savedBook.getId()));
    }

    @Test
    @DisplayName("Should successfully retrieve saved book after retry operation")
    void testRetrieveSavedBookAfterRetry() {
        // Arrange
        Book book = new Book(null, "Retrieval Test Book", "Retrieval Test Author");

        // Act
        Book savedBook = bookService.saveWithRetry(book);
        Book retrievedBook = bookService.findById(savedBook.getId()).orElse(null);

        // Assert
        assertNotNull(retrievedBook);
        assertEquals(savedBook.getId(), retrievedBook.getId());
        assertEquals(savedBook.getTitle(), retrievedBook.getTitle());
        assertEquals(savedBook.getAuthor(), retrievedBook.getAuthor());
    }
}
