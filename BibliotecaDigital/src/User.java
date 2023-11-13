import java.util.ArrayList;
import java.util.List;

public class User {
    public  String nameUser;
    private String password;

    private List<Book> borrowedBook;

    public User(String nameUser, String password) {
        this.nameUser = nameUser;
        this.password = password;
        this.borrowedBook = new ArrayList<>();
    }

    public  String getNameUser() {
        return nameUser;
    }
    
    public String getPassword() {
        return password;
    }

    public void borrowBook(Book book) {
        borrowedBook.add(book);
    }
    public List<Book> getBorrowedBooks() {
        return new ArrayList<>(borrowedBook);
    }

    
}
