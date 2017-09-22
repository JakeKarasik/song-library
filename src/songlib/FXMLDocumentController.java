package songlib;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URL;
import java.util.Comparator;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

public class FXMLDocumentController implements Initializable {
    
    @FXML
    private GridPane background;
    
    @FXML
    private Pane top_right, bot_left, bot_right;
    
    @FXML
    private ListView top_left;
    
    @FXML
    private Label name_detail, artist_detail, year_detail, album_detail;
    
    //Currently selected song
    Song selected_song = null;
    
    //Create list to store song data
    ObservableList<Song> songs = FXCollections.observableArrayList();
    
    //Used for sorting songs
    Comparator<Song> compareSongByName = (Song o1, Song o2) -> {
        int cp = o1.name.compareToIgnoreCase(o2.name);
        if (cp == 0) {
            return o1.artist.compareToIgnoreCase(o2.artist);
        }
        return cp;
    };

    //To actually add a song... must create as song object, test if already exists, then add or send error to UI
//   Song test = new Song("Best I ever had", "Drake", "2005", "ALBUMMM");
//   if (!songExists(test)) {
//      addSong(test);
//   } else {
//      System.out.println("Song already exists");
//   }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        //Load songs from file into ObservableList
        try(BufferedReader br = new BufferedReader(new FileReader("library.txt"))) {
            for(String line; (line = br.readLine()) != null; ) {
                String song[] =  line.split("~", -1);
                songs.add(new Song(song[0], song[1], song[2], song[3]));
            }
        } catch (Exception ex) {
            System.err.println("Failed to read song library");
        }
        
        //If no songs in file, do not attempt to load/sort
        if (songs.isEmpty()) {
            return;
        }
       
        //Sort and update list
        sortSongs();
        
        //Select first item as default
        top_left.getSelectionModel().select(0);
        selectSong();
    }
    
    public void addSong(Song s) {
        //Add song to ObservableList
        songs.add(s);
        
        //Sort and update list
        sortSongs();
    }
    
    public void editSong(String name) {
        
    }
    
    public void deleteSong(String name) {
        
    }
    
    public boolean songExists(Song s) {
        return songs.contains(s);
    }
    
    public void sortSongs() {
        if (songs.isEmpty()) {
            return;
        }
        //Sort ObservableList
        FXCollections.sort(songs, compareSongByName);
        //Load songs into ListView
        top_left.setItems(songs);
    }
    
    @FXML
    public void selectSong() {
        selected_song = (Song)top_left.getSelectionModel().getSelectedItem();
        
        if (selected_song == null) {
            return;
        }
        
        name_detail.setText(selected_song.name);
        artist_detail.setText(selected_song.artist);
        year_detail.setText(selected_song.year);
        album_detail.setText(selected_song.album);
    }
    
    public class Song {
        String name = "";
        String artist = "";
        String year = "";
        String album = "";
        
        public Song(String name, String artist, String year, String album) {
            this.name = name;
            this.artist = artist;
            this.year = year == null ? "" : year;
            this.album = album == null ? "" : album;
        }
        
        @Override
        public String toString() {
            return this.name + " - " + this.artist;
        }
        
        @Override
        public boolean equals(Object s) {
            return (s != null && s instanceof Song) ? (this.name.equalsIgnoreCase(((Song)s).name) && this.artist.equalsIgnoreCase(((Song)s).artist)) : false;
        }
    }
}
