import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Administrator extends User {

    public static List<Administrator> administrators = new ArrayList<>();
    public static List<Book> books = new ArrayList<>();
    private static final String libraryFile = "books.txt";
    private static boolean flag = false;

    public Administrator(String nameUser, String password) {
        super(nameUser, password);
    }

    public void addNewBook(Book newBook) {
        boolean existingBook = false;
    
        for (Iterator<Book> iterator = books.iterator(); iterator.hasNext();) {
            Book bookInLibrary = iterator.next();
    
            if (bookInLibrary.getCode().equals(newBook.getCode())) {
                
                int total = bookInLibrary.incrementQuantityAvailable(newBook.getQuantityAvailable());
                bookInLibrary.setQuantityAvailable(total);
                existingBook = true;
                System.out.println("Libro existente " + bookInLibrary.getTitle()
                        + "'. Nueva cantidad disponible: " + total);
                flag = false;
                saveBooksToFile(books);
                iterator.remove();

            }
        }
    
        if (!existingBook) {
            books.add(newBook);
            System.out.println("Nuevo libro agregado a la lista: " + newBook.getTitle());
            saveBooksToFile(books);
        }

        
    }
    
    

    public void searchBookByCode(String code) {
        for (Book book : books) {
            if (book.getCode().equals(code)) {
                System.out.println("Información del libro con código " + code + ":");
                System.out.println("Título: " + book.getTitle());
                System.out.println("Autor: " + book.getAuthor());
                System.out.println("Cantidad disponible: " + book.getQuantityAvailable());
                return;
            }
        }

        System.out.println("Libro con código " + code + " no encontrado.");
    }

    public static List<Book> getBooks() {
        return books;
    }

    public static void addAdministrator(Administrator admin) {
        administrators.add(admin);

    }

    public static void saveBooksToFile(List<Book> bookList) {
        
        List<Book> copyOfBooks = new ArrayList<>(bookList);
    
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(libraryFile, flag))) {
            for (Book existingBook : copyOfBooks) {
                writer.write(existingBook.getTitle() + "," + existingBook.getAuthor() + ","
                        + existingBook.getCode() + "," + existingBook.getQuantityAvailable());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    

    public static void initializeBooks() {
        books.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(libraryFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) { 
                    String title = parts[0];
                    String author = parts[1];
                    String code = parts[2];
                    int quantity = Integer.parseInt(parts[3]);
                    Book book = new Book(title, author, code, quantity);
                    books.add(book);
                } else {
                    System.out.println("Error: Formato incorrecto en la línea - " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    public static void decrementBookQuantity(String code) {
        for (Book book : books) {
            if (book.getCode().equals(code)) {
                int total = book.incrementQuantityAvailable(-1);
                //System.out.println(total);
                book.setQuantityAvailable(total);
            }
        }
        saveBooksToFile(books);
    }

     public static void inrementBookQuantity(String code) {
        for (Book book : books) {
            if (book.getCode().equals(code)) {
                int total = book.incrementQuantityAvailable(+1);
                //System.out.println(total);
                book.setQuantityAvailable(total);
            }
        }
        saveBooksToFile(books);
    }
    



}
