package ua.kpi.iotask5.model;

import java.io.Serializable;

public class Book implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int id;
    private final String title;
    private final String author;
    private final int year;

    public Book(int id, String title, String author, int year) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.year = year;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public int getYear() { return year; }

    @Override
    public String toString() {
        return "Book{id=" + id + ", title='" + title + "', author='" + author + "', year=" + year + "}";
    }
}
