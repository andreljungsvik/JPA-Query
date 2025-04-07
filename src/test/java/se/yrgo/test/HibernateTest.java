package se.yrgo.test;

import jakarta.persistence.*;

import se.yrgo.domain.Author;
import se.yrgo.domain.Reader;
import se.yrgo.domain.Book;

import java.util.List;

public class HibernateTest {
    public static EntityManagerFactory emf = Persistence.createEntityManagerFactory("databaseConfig");

    public static void main(String[] args) {
        setUpData();
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();


        System.out.println("1. Hämta alla böcker av en specifik författare:");
        TypedQuery<Author> q1 = em.createQuery("select a from Author a left join fetch a.books where a.name = :authorName", Author.class);
        q1.setParameter("authorName", "J.K. Rowling");
        List<Author> authors = q1.getResultList();

        if (!authors.isEmpty()) {
            Author author = authors.get(0);
            System.out.println("Författare: " + author.getName());
            System.out.println("Böcker: " + author.getBooks());
        } else {
            System.out.println("Ingen författare hittades.");
        }
        System.out.println();

//---------------------------------------------------------------------------------------------------------------------------------------

        System.out.println("2. Hämta alla läsare( readers) som har läst en viss bok (member of):");
        String bookTitle = "1984";

        TypedQuery<Book> bookQuery = em.createQuery("SELECT b FROM Book b WHERE b.title = :title", Book.class);
        bookQuery.setParameter("title", bookTitle);
        List<Book> books = bookQuery.getResultList();

        if (books.isEmpty()) {
            System.out.println("Ingen bok med titeln '" + bookTitle + "' hittades.");
            return;
        }

        Book book = books.get(0);

        TypedQuery<Reader> q2 = em.createQuery("select r from Reader r where :book member of r.books", Reader.class);
        q2.setParameter("book", book);
        List<Reader> readers = q2.getResultList();
        if (!readers.isEmpty()) {
            for (Reader reader : readers) {
                System.out.println("Läsare för boken, " + bookTitle + ": " + reader.getName());
            }
        } else {
            System.out.println("Ingen läsare för boken");
        }
        System.out.println();

//---------------------------------------------------------------------------------------------------------------------------------------

        System.out.println("3. Hämta författare vars böcker har lästs av minst en läsare (join):");

        TypedQuery q3 = em.createQuery("select distinct a.name from Author a join a.books b join b.readers r", String.class);
        List<String> authorNames = q3.getResultList();
        if (!authorNames.isEmpty()) {
            System.out.println("Författare: ");
            for (String name : authorNames) {
                System.out.println(name);
            }
        } else {
            System.out.println("Inga författare hittades.");
        }
        System.out.println();

//---------------------------------------------------------------------------------------------------------------------------------------

        System.out.println("4. Räkna antalet böcker per författare (Aggregation Query):");

        TypedQuery<Object[]> q4 = em.createQuery("select a.name, count(distinct b) from Author a join a.books b group by a.name", Object[].class);
        List<Object[]> results2 = q4.getResultList();

        if (!results2.isEmpty()) {
            for (Object[] result : results2) {
                String authorName = (String) result[0];
                Long bookCount = (Long) result[1];
                System.out.println(authorName + " har " + bookCount + " bok/böcker");
            }
        }
        System.out.println();

//---------------------------------------------------------------------------------------------------------------------------------------

        System.out.println("5. Named Query - Hämta böcker efter genre");
        String genre = "Fantasy";
        TypedQuery<Book> q5 = em.createNamedQuery("findBooksByGenre", Book.class);
        q5.setParameter("genre", genre);

        List<Book> books2 = q5.getResultList();
        if (!books2.isEmpty()) {
            System.out.println("Böcker i genren: " + genre);
            for (Book b : books2) {
                System.out.println(b.getTitle());
            }
        } else {
            System.out.println("Inga böcker i genren");
        }

        tx.commit();
        em.close();
    }

    public static void setUpData() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        Author author1 = new Author("J.K. Rowling", "British");
        Author author2 = new Author("George Orwell", "British");
        Author author3 = new Author("Haruki Murakami", "Japanese");

        Book book1 = new Book("Harry Potter and the Philosopher's Stone", "Fantasy", 1997);
        Book book2 = new Book("1984", "Dystopia", 1949);
        Book book3 = new Book("Animal Farm", "Dystopia", 1945);
        Book book4 = new Book("Kafka on the Shore", "Fiction", 2002);
        Book book5 = new Book("Norwegian Wood", "Fiction", 1987);

        author1.addBook(book1);
        author2.addBook(book2);
        author2.addBook(book3);
        author3.addBook(book4);
        author3.addBook(book5);

        Reader reader1 = new Reader("John Doe", "john@example.com");
        Reader reader2 = new Reader("Jane Smith", "jane@example.com");
        Reader reader3 = new Reader("Alice Johnson", "alice@example.com");

        reader1.getBooks().add(book1);
        reader2.getBooks().add(book2);
        reader2.getBooks().add(book3);

        em.persist(author1);
        em.persist(author2);
        em.persist(author3);
        em.persist(reader1);
        em.persist(reader2);
        em.persist(reader3);

        tx.commit();
        em.close();
    }
}