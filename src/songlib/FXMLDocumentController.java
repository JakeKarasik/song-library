//Benjamin Ker - bk375
//Jake Karasik - jak451

package songlib;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.URL;
import java.util.Comparator;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import java.util.ListIterator;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;


public class FXMLDocumentController implements Initializable {

    @FXML
    private GridPane background;

    @FXML
    private Pane top_right, bot_left, bot_right;

    @FXML
    private ListView song_list;

    @FXML
    private TextField name_detail, artist_detail, year_detail, album_detail, add_name_detail, add_artist_detail,
            add_year_detail, add_album_detail;

    @FXML
    private Button add_button, reset_button, delete_button, edit_button, save_button, cancel_button;

    @FXML
    private Label error_label;

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

        name_detail.setDisable(true);
        artist_detail.setDisable(true);
        year_detail.setDisable(true);
        album_detail.setDisable(true);
        name_detail.setOpacity(0.8);
        artist_detail.setOpacity(0.8);
        year_detail.setOpacity(0.8);
        album_detail.setOpacity(0.8);
        save_button.setDisable(true);
        cancel_button.setDisable(true);

        //If no songs in file, do not attempt to load/sort
        if (songs.isEmpty()) {
            edit_button.setDisable(true);
            delete_button.setDisable(true);
            return;
        }

        //Sort and update list
        sortSongs();

        //Select first item as default
        song_list.getSelectionModel().select(0);
        selectSong();
    }

    //Called by add_button - COMPLETE
    public void addSong() {
        //Get our input text
        Song foo = new Song(add_name_detail.getText(), add_artist_detail.getText(), add_year_detail.getText(),
                add_album_detail.getText());

        //Check if song or artist is blank - Check if duplicate
        if(add_name_detail.getText().trim().isEmpty() || add_artist_detail.getText().trim().isEmpty()) {
            error_label.setText("ERROR: MUST ENTER SONG/ARTIST");
            error_label.setTextFill(Color.RED);
        }else if(songExists(foo)){
            error_label.setText("ERROR: DUPLICATE SONG");
            error_label.setTextFill(Color.RED);
        }else{
            //Add our new song to ObservableList
            songs.add(foo);
            //Sort and update list
            sortSongs();
            saveSongList();
            //Set error status to green and reset fields
            edit_button.setDisable(false);
            delete_button.setDisable(false);
            error_label.setText("SUCCESS: SONG ADDED");
            error_label.setTextFill(Color.LIME);
            resetAdd();
            //Set selection to new edited song
            song_list.getSelectionModel().select(foo);
            selectSong();
        }
    }

    //Called by reset_button - COMPLETE
    public void resetAdd() {
        //Clear error label
        error_label.setText("");
        
        add_name_detail.setText("");
        add_artist_detail.setText("");
        add_year_detail.setText("");
        add_album_detail.setText("");
    }

    //Called by edit_button - COMPLETE
    public void initEditSong(){
        //Change button states
        save_button.setDisable(false);
        cancel_button.setDisable(false);
        edit_button.setDisable(true);
        delete_button.setDisable(true);

        name_detail.setStyle("-fx-control-inner-background: white; -fx-text-inner-color: black;");
        artist_detail.setStyle("-fx-control-inner-background: white; -fx-text-inner-color: black;");
        album_detail.setStyle("-fx-control-inner-background: white; -fx-text-inner-color: black;");
        year_detail.setStyle("-fx-control-inner-background: white; -fx-text-inner-color: black;");

        //Disable Listview to prevent selection of another song
        song_list.setDisable(true);

        //Allow text fields to be modified
        name_detail.setDisable(false);
        artist_detail.setDisable(false);
        year_detail.setDisable(false);
        album_detail.setDisable(false);

        //Disable add new song feature
        add_name_detail.setDisable(true);
        add_artist_detail.setDisable(true);
        add_year_detail.setDisable(true);
        add_album_detail.setDisable(true);
        add_button.setDisable(true);
        reset_button.setDisable(true);
    }


    //Called by cancel_button - COMPLETE
    public void cancelEditSong(){
        //Clear error messages
        error_label.setText("");
        
        //Reset all fields to stored values
        name_detail.setText(selected_song.name);
        artist_detail.setText(selected_song.artist);
        year_detail.setText(selected_song.year);
        album_detail.setText(selected_song.album);

        //Set edit buttons back to default state
        save_button.setDisable(true);
        cancel_button.setDisable(true);
        edit_button.setDisable(false);
        delete_button.setDisable(false);

        //Disable text fields to prevent modification
        name_detail.setDisable(true);
        artist_detail.setDisable(true);
        year_detail.setDisable(true);
        album_detail.setDisable(true);

        //Re-enable add new song feature
        add_name_detail.setDisable(false);
        add_artist_detail.setDisable(false);
        add_year_detail.setDisable(false);
        add_album_detail.setDisable(false);
        resetAdd();
        add_button.setDisable(false);
        reset_button.setDisable(false);

        //Re-enable listview
        name_detail.setStyle("-fx-control-inner-background: lightgray; -fx-text-inner-color: black;");
        artist_detail.setStyle("-fx-control-inner-background: lightgray; -fx-text-inner-color: black;");
        album_detail.setStyle("-fx-control-inner-background: lightgray; -fx-text-inner-color: black;");
        year_detail.setStyle("-fx-control-inner-background: lightgray; -fx-text-inner-color: black;");
        song_list.setDisable(false);
    }

    //Called by save_button - COMPLETE
    public void editSong() {
        //Get our input text
        Song foo = new Song(name_detail.getText(), artist_detail.getText(), year_detail.getText(), album_detail.getText());

        //Check if song or artist is blank - Check if duplicate
        if(name_detail.getText().trim().isEmpty() || artist_detail.getText().trim().isEmpty()) {
            error_label.setText("ERROR: MUST ENTER SONG/ARTIST");
            error_label.setTextFill(Color.RED);
        }else if(songExists(foo, song_list.getSelectionModel().getSelectedIndex())) {
            error_label.setText("ERROR: DUPLICATE SONG");
            error_label.setTextFill(Color.RED);
        }else{
            //Remove the selected song so that it can be replaced with edited version
            songs.remove(selected_song);

            //Add our edited song to ObservableList
            songs.add(foo);

            //Sort and update list
            sortSongs();
            saveSongList();

            //Select edited song and reset
            selected_song = foo;
            song_list.getSelectionModel().select(selected_song);
            cancelEditSong();
            error_label.setText("SUCCESS: SONG EDITED");
            error_label.setTextFill(Color.LIME);
            song_list.setDisable(false);
            name_detail.setStyle("-fx-control-inner-background: lightgray; -fx-text-inner-color: black;");
            artist_detail.setStyle("-fx-control-inner-background: lightgray; -fx-text-inner-color: black;");
            album_detail.setStyle("-fx-control-inner-background: lightgray; -fx-text-inner-color: black;");
            year_detail.setStyle("-fx-control-inner-background: lightgray; -fx-text-inner-color: black;");

        }
    }

    //Called by delete_button - COMPLETE
    public void deleteSong() {
        //Confirm deletion
        Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure you want to delete "+selected_song);
        alert.setHeaderText("Please confirm deletion or cancel");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() != ButtonType.OK) {
            return;
        }
 
        //Get song index before removal
        int index = song_list.getSelectionModel().getSelectedIndex();

        //Remove selected song from ObservableList
        songs.remove(selected_song);

        //Sort and update list
        sortSongs();
        saveSongList();

        //Select according to parameters
        song_list.getSelectionModel().select(index);
        selectSong();

        //Set error status to green
        error_label.setText("SUCCESS: SONG REMOVED");
        error_label.setTextFill(Color.LIME);

        //Disable edit/delete functionality if empty
        if (songs.isEmpty()) {
            edit_button.setDisable(true);
            delete_button.setDisable(true);
        }
    }

    public boolean songExists(Song s) {
        return songs.contains(s);
    }
    
    //Check if song exists, excluding passed index
    public boolean songExists(Song s, int index) {
        int pos = songs.indexOf(s);
        return pos >= 0 && pos != index;
    }

    public void sortSongs() {
        if (songs.isEmpty()) {
            return;
        }
        //Sort ObservableList
        FXCollections.sort(songs, compareSongByName);
        //Load songs into ListView
        song_list.setItems(songs);
    }

    @FXML
    public void selectSong() {
        //Clear error label
        error_label.setText("");
        
        selected_song = (Song)song_list.getSelectionModel().getSelectedItem();
        if (selected_song == null) {
            name_detail.setText("");
            artist_detail.setText("");
            year_detail.setText("");
            album_detail.setText("");
            return;
        }

        //Load data into song details UI
        name_detail.setText(selected_song.name);
        artist_detail.setText(selected_song.artist);
        year_detail.setText(selected_song.year);
        album_detail.setText(selected_song.album);
    }

    public void saveSongList(){
        //Load songs from file into ObservableList
        Song cur;
        try(BufferedWriter bw = new BufferedWriter(new FileWriter("library.txt"))) {
            ListIterator <Song> songIter = songs.listIterator();
            while(songIter.hasNext()){
                cur = songIter.next();
                bw.write(cur.name + "~" + cur.artist + "~" + cur.year + "~" + cur.album);
                bw.newLine();
            }
        } catch (Exception ex) {
            System.err.println("Failed to save song library");
        }
    }


    public class Song {
        String name = "";
        String artist = "";
        String year = "";
        String album = "";

        public Song(String name, String artist, String year, String album) {
            this.name = name;
            this.artist = artist;
            this.year = year;
            this.album = album;
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