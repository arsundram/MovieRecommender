package com.example.movierecommender;

public class Movie {
    String title, genres;
    boolean poster;
    int movieId;

    public Movie(String title, String genres, boolean poster, int movieId) {
        this.title = title;
        this.genres = genres;
        this.poster = poster;
        this.movieId = movieId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public boolean isPoster() {
        return poster;
    }

    public void setPoster(boolean poster) {
        this.poster = poster;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }
}
