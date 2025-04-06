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

        //2. Hämta alla böcker av en specifik författare
        TypedQuery<Author> q1 = em.createQuery("select a from Author a left join fetch a.books where a.name = :authorName", Author.class);
        q1.setParameter("authorName", "J.K. Rowling");
        List<Author> authors = q1.getResultList();

        if (!authors.isEmpty()) {
            Author author = authors.get(0);
            System.out.println(author);
        } else {
            System.out.println("Ingen författare hittades.");
        }


        //3. Hämta alla läsare( readers) som har läst en viss bok (member of)
        //4. Hämta författare vars böcker har lästs av minst en läsare (join)
        //5. Räkna antalet böcker per författare (Aggregation Query)
        //6. Named Query - Hämta böcker efter genre

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
        reader3.getBooks().add(book4);
        reader3.getBooks().add(book5);

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