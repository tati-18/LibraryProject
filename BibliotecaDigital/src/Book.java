public class Book {
    private String title;
    private String author;
    private String code;
    private int quantityAvailable;

    public Book(String title, String author, String code, int quantityAvailable) {
        this.title = title;
        this.author = author;
        this.code = code;
        this.quantityAvailable = quantityAvailable;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getCode() {
        return code;
    }

    public int getQuantityAvailable() {
        return quantityAvailable;
    }

    public int incrementQuantityAvailable(int quantity) {
        this.quantityAvailable += quantity;
        return quantityAvailable;
    }


    public int decrementQuantityAvailable(int quantity) {
        this.quantityAvailable -= quantity;
        return quantityAvailable;
    }

    
    public void setQuantityAvailable(int quantityAvailable) {
        this.quantityAvailable = quantityAvailable;
    }

  
}
