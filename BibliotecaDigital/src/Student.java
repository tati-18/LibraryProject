
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class Student extends User {

    public static List<Student> students = new ArrayList<>();
    public List<Book> borrowedBooks;
    public List<Book> availableBooks;
    private List<Book> librosReservadosTemp = new ArrayList<>();
    private Timer timer;
    private static final int TIEMPO_CONFIRMACION = 60000;

    public Student(String nameUser, String password) {
        super(nameUser, password);
        this.borrowedBooks = new ArrayList<>();
        this.availableBooks = new ArrayList<>();

    }

    public void showAvailableBooks() {
        loadAvailableBookFromFile();

        if (availableBooks.isEmpty()) {
            System.out.println("No hay libros disponibles en este momento.");
        } else {
            System.out.println("Libros disponibles:");
            for (Book book : availableBooks) {
                System.out.println(
                        "Código: " + book.getCode() + ", Título: " + book.getTitle() + ", Autor: " + book.getAuthor() +
                                ", Cantidad Disponible: " + book.getQuantityAvailable());
            }
        }
    }

    public void borrowBookByCode(String code,Scanner scan) {

        loadAvailableBookFromFile();
        Book selectedBook = null;

        for (Book book : availableBooks) {
            if (book.getCode().equals(code)) {
                selectedBook = book;
                break;
            }
        }

        if (selectedBook != null) {
            if (hasBookWithTitle(selectedBook.getCode())) {
                System.out.println("Ya tienes un libro " + selectedBook.getTitle() + " prestado.");
                
            } else {
                reservarLibroTemp(selectedBook);

                System.out.println("¿Desea confirmar el préstamo? (si/no): ");
                String respuesta = scan.next();
                if (respuesta.toLowerCase().equals("si")) {
                    if (timer != null) {
                        borrowBook(selectedBook);
                        saveBorrowedBooksToFile(selectedBook);
                        Administrator.decrementBookQuantity(code);
                        System.out.println("Préstamo exitoso. Libro prestado: " + selectedBook.getTitle());
                        loadAvailableBookFromFile();

                    } else {
                        System.out.println("Tiempo de confirmación expirado. Préstamo cancelado.");
                        cancelarReservaTemp(selectedBook);

                    }

                } else {
                  
                    System.out.println("Prestamo cancelado");
                    cancelarReservaTemp(selectedBook);

                }
                
            }

        } else {
            System.out.println("Libro no encontrado o no disponible para préstamo.");
        }
        
    }

    public List<Book> getAvailableBooks() {
        loadAvailableBookFromFile();
        loadBorrowedBooksFromFile();

        List<Book> allBorrowedBooks = borrowedBooks;

        for (Book book : Administrator.getBooks()) {
            if (!allBorrowedBooks.contains(book) && book.getQuantityAvailable() > 0) {
                availableBooks.add(book);
            }
        }

        return availableBooks;
    }

    public void showBorrowedBooks() {

        loadBorrowedBooksFromFile();

        if (borrowedBooks.isEmpty()) {
            System.out.println("No tienes libros prestados en este momento.");
        } else {
            System.out.println("Libros prestados por " + getNameUser() + ":");
            for (Book book : borrowedBooks) {
                System.out.println("Título: " + book.getTitle() + ", Autor: " + book.getAuthor() +
                        ", Fecha de préstamo: " + getFechaPrestamo(book) +
                        ", Fecha de devolución: " + getFechaDevolucion(book) +
                        ", ¿Vencido?: " + isBookOverdue(book));
            }
        }
    }

    public static void addStudent(Student student) {
        students.add(student);
    }

    public void saveBorrowedBooksToFile(Book book) {
        Date fechaPrestamo = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String fechaPrestamoStr = sdf.format(fechaPrestamo);

        Calendar initCalen = Calendar.getInstance();
        initCalen.setTime(fechaPrestamo);
        initCalen.add(Calendar.DAY_OF_MONTH, 30);
        Date fechaDevolucion = initCalen.getTime();
        String fechaDevolucionStr = sdf.format(fechaDevolucion);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("borrowed_books.txt", true))) {
            writer.write(getNameUser() + "," + book.getCode() + "," + fechaPrestamoStr + "," + fechaDevolucionStr);
            writer.newLine();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadBorrowedBooksFromFile() {
        borrowedBooks.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader("borrowed_books.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2 && parts[0].equals(getNameUser())) {
                    String bookCode = parts[1];
                    Book book = findBookByCode(bookCode);
                    if (book != null) {
                        borrowedBooks.add(book);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Se creo un archivo para documentar los libros prestados.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Book findBookByCode(String code) {
        for (Book book : Administrator.getBooks()) {
            if (book.getCode().equals(code)) {
                return book;
            }
        }
        return null;
    }

    private void loadAvailableBookFromFile() {

        availableBooks.clear();

        try (BufferedReader reader = new BufferedReader(new FileReader("books.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String title = parts[0];
                String author = parts[1];
                String code = parts[2];
                int quantity = Integer.parseInt(parts[3]);

                Book book = new Book(title, author, code, quantity);
                if (quantity > 0) {
                    availableBooks.add(book);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getFechaPrestamo(Book book) {
        try (BufferedReader reader = new BufferedReader(new FileReader("borrowed_books.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4 && parts[0].equals(getNameUser()) && parts[1].equals(book.getCode())) {
                    return parts[2];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Fecha no disponible";
    }

    private String getFechaDevolucion(Book book) {
        try (BufferedReader reader = new BufferedReader(new FileReader("borrowed_books.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4 && parts[0].equals(getNameUser()) && parts[1].equals(book.getCode())) {
                    return parts[3];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Fecha no disponible";
    }

    public boolean hasBookWithTitle(String code) {
        for (Book book : borrowedBooks) {
            if (book.getCode().equals(code)) {
                return true;
            }
        }
        return false;
    }

    private boolean isBookOverdue(Book book) {
        String fechaDevolucionStr = getFechaDevolucion(book);

        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date fechaDevolucion;
        try {
            fechaDevolucion = sdf.parse(fechaDevolucionStr);

            return today.after(fechaDevolucion);
        } catch (ParseException e) {

            e.printStackTrace();
        }

        return false;
    }

    public void reservarLibroTemp(Book book) {
        if (timer == null) {
            timer = new Timer();
        }

        if (!librosReservadosTemp.contains(book)) {

            librosReservadosTemp.add(book);

            int total = book.getQuantityAvailable() - 1;
            book.setQuantityAvailable(total);

            Administrator.saveBooksToFile(availableBooks);
            loadAvailableBookFromFile();

            System.out.println("Libro " + book.getTitle() + " reservado temporalmente.");

            iniciarTemporizador(book);

        } else {
            System.out.println("El libro ya está reservado temporalmente.");
        }
    }

    private void iniciarTemporizador(Book book) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (librosReservadosTemp.contains(book)) {
                    System.out.println("Tiempo de confirmación expirado para el libro " + book.getTitle());
                    cancelarReservaTemp(book);
                }
            }
        },
                TIEMPO_CONFIRMACION);
    }

    public void cancelarReservaTemp(Book book) {

        if (librosReservadosTemp.contains(book)) {
            librosReservadosTemp.remove(book);
            Book selectedoldBook = null;

            for (Book bookOld : availableBooks) {
                if (book.getCode().equals(bookOld.getCode())) {
                    selectedoldBook = bookOld;
                    break;
                }
            }

            int total = selectedoldBook.getQuantityAvailable() + 1;
            selectedoldBook.setQuantityAvailable(total);
            Administrator.saveBooksToFile(availableBooks);
            loadAvailableBookFromFile();

            System.out.println("Reserva cancelada. Libro " + book.getTitle() + " devuelto al inventario.");

        }

        if (timer != null) {
            timer.cancel();
        }
    }

}
