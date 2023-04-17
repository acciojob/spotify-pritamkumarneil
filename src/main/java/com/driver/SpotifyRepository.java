package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;

    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;

    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public User createUser(String name, String mobile) {
        User user=new User();
        user.setName(name);
        user.setMobile(mobile);
        users.add(user);
        return user;
    }

    public Artist createArtist(String name) {
        Artist artist=new Artist();
        artist.setName(name);
        artist.setLikes(0);
        artists.add(artist);
        return artist;
    }

    public Album createAlbum(String title, String artistName) {
        //If the artist does not exist, first create an artist with given name
        //Create an album with given title and artist
        //1. find artist if not found create new artist
        Artist artist=null;
        boolean ifArtistPresent=false;
        for(Artist artist1:artists){
            if(artist1.getName().equals(artistName)){
                ifArtistPresent=true;
                artist=artist1;
                break;
            }
        }
        if(!ifArtistPresent){// creating new artist if not found
            artist=new Artist(artistName);
            artists.add(artist);
        }
        //2. if artist found or not found creating album // update albumDb , artistAlbumMap
        Album album=new Album();
        album.setTitle(title);
        album.setReleaseDate(new Date());
        albums.add(album);// added to albumDb
        if(!artistAlbumMap.containsKey(artist)){
            artistAlbumMap.put(artist,new ArrayList<>());
        }
        // updating artistAlbumMap database
        artistAlbumMap.get(artist).add(album);
        return album;
    }

    public Song createSong(String title, String albumName, int length) throws Exception{
        //If the album does not exist in database, throw "Album does not exist" exception
        //Create and add the song to respective album
    //1. find album if not found throw exception
        boolean albumExist=false;
        Album album=null;
        // checking if album is already available or not
        for(Album album1:albums){
            if(album1.getTitle().equals(albumName)){
                albumExist=true;
                album=album1;
                break;
            }
        }
        // if album not available the throw exception
        if(!albumExist) throw new Exception("Album does not exist");
    //2. create new Song // add it to songsDB,albumSongMap

        // creating song object
        Song song=new Song();
        song.setTitle(title);
        song.setLength(length);
        songs.add(song);// adding song to the song list

        // adding song in albumSongMap
        if(!albumSongMap.containsKey(album)){
            albumSongMap.put(album,new ArrayList<>());
        }
        albumSongMap.get(album).add(song);
        return song;

    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
        //Create a playlist with given title and add all songs having the given length in the database to that playlist
        //The creator of the playlist will be the given user and will also be the only listener at the time of playlist creation
        //If the user does not exist, throw "User does not exist" exception

    //1. create playlist with given title first
        Playlist playlist=new Playlist(title);

    //2. Find the user with mobile -if not found throw exception//
        User user=null;
        boolean userExist=false;
        for(User user1: users){
            if(user1.getMobile().equals(mobile)){
                user=user1;
                userExist=true;
                break;
            }
        }
        if(!userExist) throw new Exception("User does not exist");

    //3. Get the list of Song with  length==length
        List<Song> songList=new ArrayList<>();
        for(Song song:songs){
            if(song.getLength()==length){
                songList.add(song);
            }
        }
    //3. update creatorPlaylistMap with given user and playList
        creatorPlaylistMap.put(user,playlist);

    //4. update playListListenerMap with the playList and user
        if(!playlistListenerMap.containsKey(playlist)){
            playlistListenerMap.put(playlist,new ArrayList<>());
        }
        playlistListenerMap.get(playlist).add(user);

    //5. Update playListSongMap// as it is new playlist so just put the list of song
        //if(playlistSongMap.containsKey(playlist))
        playlistSongMap.put(playlist,songList);

        // if everything went well then we can add playlist in playlist DB
        playlists.add(playlist);


        return playlist;
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        //Create a playlist with given title and add all songs having the given titles in the database to that playlist
        //The creator of the playlist will be the given user and will also be the only listener at the time of playlist creation
        //If the user does not exist, throw "User does not exist" exception

        //1. find the user first with mobile; make it creator of the playlist
            User user=null;
            boolean userFound=false;
            for(User user1: users){
                if(user1.getMobile().equals(mobile)){
                    userFound=true;
                    user=user1;
                    break;
                }
            }
            if(!userFound)throw new Exception("User does not exist");

        //2. get the list of all song having all title from given list of songTitles
            List<Song> songList=new ArrayList<>();
            for (String songName : songTitles) {
                for(Song song: songs) {
                    if (song.getTitle().equals(songName)) {
                        songList.add(song);
                        break;
                    }
                }
            }

        //3. create PlayList with given title also update playListDb
            Playlist playlist=new Playlist(title);
            playlists.add(playlist);

        //4. update playListListenerMap for current user
            if(!playlistListenerMap.containsKey(playlist)){
                playlistListenerMap.put(playlist,new ArrayList<>());
            }
            playlistListenerMap.get(playlist).add(user);

        //5. update playListSongMap with songList
            if(!playlistSongMap.containsKey(playlist)){
                playlistSongMap.put(playlist,songList);
            }else{
                playlistSongMap.get(playlist).addAll(songList);
            }

        //6. update userPlaylistMap
            if(!userPlaylistMap.containsKey(user)){
                userPlaylistMap.put(user,new ArrayList<>());
            }
            userPlaylistMap.get(user).add(playlist);

        //7. update createPlaylistMap
            creatorPlaylistMap.put(user,playlist);
           return playlist;
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        //Find the playlist with given title and add user as listener of that playlist and update user accordingly
        //If the user is creator or already a listener, do nothing
        //If the user does not exist, throw "User does not exist" exception
        //If the playlist does not exist, throw "Playlist does not exist" exception
        // Return the playlist after updating

        //1. find user with mobileNo==mobile; // if user not found throw user doesn't exist
            User user=null;
            boolean userFound=false;
            for(User user1: users){
                if(user1.getMobile().equals(mobile)){
                    userFound=true;
                    user=user1;
                    break;
                }
            }
            if(!userFound)throw new Exception("User does not exist");
        //2. find the playList with given title; // if playlist not found throw exception
            Playlist playlist=null;
            boolean playlistFound=false;
            for(Playlist playlist1: playlists){
                if(playlist1.getTitle().equals(playlistTitle)){
                    playlist=playlist1;
                    playlistFound=true;
                    break;
                }
            }
            if(!playlistFound)throw new Exception("Playlist does not exist");
        //3. check if user is creator of playlist or a listener of playlist
            // checking is user is creator
            if(creatorPlaylistMap.get(user)==playlist){
                return playlist;
            }
            // checking if user is listener
            for(User listener:playlistListenerMap.get(playlist)){
                if(listener==user)return playlist;
            }
        //4. return playList
            return playlist;
    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        //The user likes the given song. The corresponding artist of the song gets auto-liked
        //A song can be liked by a user only once. If a user tried to like a song multiple times, do nothing
        //However, an artist can indirectly have multiple likes from a user, if the user has liked multiple songs of that artist.
        //If the user does not exist, throw "User does not exist" exception
        //If the song does not exist, throw "Song does not exist" exception
        //Return the song after updating

    //1. Find user with given mobile no; // if not found throw exception
        User user=null;
        boolean userFound=false;
        for(User user1:users){
            if(user1.getMobile().equals(mobile)){
                userFound=true;
                user=user1;
                break;
            }
        }
        if(!userFound) throw new Exception("User does not exist");

    //2. Find the song with given songTitle; // if not found throw exception
        Song song=null;
        boolean songFound=false;
        for(Song song1: songs){
            if(song1.getTitle().equals(songTitle)){
                songFound=true;
                song=song1;
                break;
            }
        }
        if(!songFound) throw new Exception("Song does not exist");

    //3. check/update database songLikeMap(song,list<user>) // also if already liked by user then return
        // if song is getting liked for the first time then it won't be there in the songLikeMap// creating new list for that
        if(!songLikeMap.containsKey(song)){
            song.setLikes(song.getLikes()+1);
            songLikeMap.put(song,new ArrayList<>());
            songLikeMap.get(song).add(user);
        }
        else{// if song is liked by other but not by this user then simply increase the number of likes of song and add user to songLikeMap
            for(User user1:songLikeMap.get(song)){
                if(user1==user)return song;
            }
            song.setLikes(song.getLikes()+1);
            songLikeMap.get(song).add(user);
        }

    //4a.Find album of that song(albumSongMap(album,list<song>) ->
        Album album=null;
        boolean albumFound=false;
        for(Album album1: albumSongMap.keySet()){
            for(Song song1:albumSongMap.get(album1)){
                if(song1==song){
                    album=album1;
                    albumFound=true;
                    break;
                }
            }
            if(albumFound)break;
        }
    //4b.Then find artist of that album(artistAlbumMap(artist,List<album>)-> increase like of artist also
        Artist artist=null;
        boolean artistFound=false;
        for(Artist artist1:artistAlbumMap.keySet()){
            for(Album album1:artistAlbumMap.get(artist1)){
                if(album1==album) {
                    artist = artist1;
                    artistFound = true;
                    break;
                }
            }
            if(artistFound)break;
        }
        try{
            int likes=artist.getLikes()+1;
            artist.setLikes(likes);
        }catch (Exception e){
            return song;
        }
    //5. Return song after updating;

        return song;
    }

    public String mostPopularArtist() {
        //Return the artist name with maximum likes
        String artistName="";
        int max=0;
        for(Artist artist: artists){
            if(max<artist.getLikes()){
                artistName=artist.getName();
                max=artist.getLikes();
            }
        }
        return artistName;
    }

    public String mostPopularSong() {
        //return the song title with maximum likes
        String songName="";
        int max=0;
        for(Song song: songs){
            int likes=song.getLikes();
            if(max<likes){
                songName=song.getTitle();
                max=likes;
            }
        }
        return songName;
    }
}
