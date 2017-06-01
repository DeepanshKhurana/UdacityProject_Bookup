package in.thepolymath.bookup;

/**
 * This class will define a Book object
 */

public class Book {

    private String bookTitle, bookURL, bookDesc, bookImage, bookDate, bookCost, bookAuthor, bookCategory;

    /**
     * Constructor for the class Book
     *
     */
    public Book(String title, String author, String url, String desc, String image, String date, String cost, String category) {
        bookTitle = title;
        bookAuthor = author;
        bookURL = url;
        bookDesc = desc;
        bookImage = image;
        bookDate = date;
        bookCost = cost;
        bookCategory = category;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }


    public String getBookCategory() {
        return bookCategory;
    }

    public String getBookURL() {
        return bookURL;
    }

    public String getBookDesc() {
        return bookDesc;
    }

    public String getBookImageLink() {
        return bookImage;
    }

    public String getBookDate() {
        return bookDate;
    }

    public String getBookCost() {
        return bookCost;
    }
}
