package org.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.time.LocalDate;

import org.Model.Album.Album;
import org.Model.Music.Music;
import org.Model.Music.MusicMultimedia;
import org.Model.Playlist.*;
import org.Model.User.User;
import org.Model.Plan.*;
import org.Exceptions.*;

/**
 * Classe principal do modelo do sistema SpotifUM.
 * Responsável por gerir músicas, playlists, utilizadores, álbuns e estatísticas.
 * Implementa Serializable para permitir persistência do estado.
 */
public class SpotifUM implements Serializable {

    // =================== ATRIBUTOS ===================

    /**
     * Mapa de músicas do sistema.
     * Key: Nome da música
     * Value: Objeto Music correspondente
     */
    private Map<String , Music> musics;
    /**
     * Mapa de playlists públicas do sistema.
     * Key: ID da playlist
     * Value: Objeto Playlist correspondente
     */
    private Map<Integer, Playlist> publicPlaylists;
    /**
     * Mapa de utilizadores do sistema.
     * Key: Nome do utilizador
     * Value: Objeto User correspondente
     */
    private Map<String, User> users;
    /**
     * Mapa de álbuns do sistema.
     * Key: Nome do álbum
     * Value: Objeto Album correspondente
     */
    private Map<String, Album> albums;
    /**
     * Utilizador atualmente autenticado no sistema.
     */
    private User currentUser;
    /**
     * Estatísticas de reproduções por artista.
     * Key: Nome do artista
     * Value: Número de reproduções
     */
    private Map<String, Integer> artistReproductions;
    /**
     * Estatísticas de reproduções por género musical.
     * Key: Nome do género
     * Value: Número de reproduções
     */
    private Map<String, Integer> genreReproductions;

    // =================== CONSTRUTORES ===================

    /**
     * Construtor vazio. Inicializa todos os mapas como vazios e o utilizador atual como null.
     */
    public SpotifUM(){
        this.musics = new HashMap<>();
        this.publicPlaylists = new HashMap<>();
        this.users = new HashMap<>();
        this.albums = new HashMap<>();
        this.artistReproductions = new TreeMap<>();
        this.genreReproductions = new TreeMap<>();
        this.currentUser = null;
        //populateDatabase();
    }

    /**
     * Construtor parametrizado. Inicializa os mapas e estatísticas com os valores fornecidos.
     * O utilizador atual é definido como null.
     * @param m Mapa de músicas
     * @param p Mapa de playlists públicas
     * @param u Mapa de utilizadores
     * @param a Mapa de álbuns
     * @param artistReproductions Estatísticas de reproduções por artista
     * @param genreReproductions Estatísticas de reproduções por género
     */
    public SpotifUM(Map<String, Music> m, Map<Integer, Playlist> p, Map<String, User> u, Map<String, Album> a, Map<String, Integer> artistReproductions, Map<String, Integer> genreReproductions){
        this.musics = new HashMap<>();
        m.forEach((k, v) -> this.musics.put(k, v.clone()));
        this.publicPlaylists = new HashMap<>();
        p.forEach((k, v) -> this.publicPlaylists.put(k, v.clone()));
        this.users = new HashMap<>();
        u.forEach((k, v) -> this.users.put(k, v.clone()));
        this.albums = new HashMap<>();
        a.forEach((k, v) -> this.albums.put(k, v.clone()));
        this.currentUser = null;
        this.artistReproductions = new TreeMap<>();
        artistReproductions.forEach((k, v) -> this.artistReproductions.put(k, v));
        this.genreReproductions = new TreeMap<>();
        genreReproductions.forEach((k, v) -> this.genreReproductions.put(k, v));
    }

    /**
     * Construtor de cópia. Cria uma nova instância com os mesmos dados do objeto fornecido.
     * @param s Objeto SpotifUM a copiar
     */
    public SpotifUM(SpotifUM s){
        this.musics = new HashMap<>();
        s.getMusics().forEach((k, v) -> this.musics.put(k, v.clone()));
        this.publicPlaylists = new HashMap<>();
        s.getPublicPlaylists().forEach((k, v) -> this.publicPlaylists.put(k, v.clone()));
        this.users = new HashMap<>();
        s.getUsers().forEach((k, v) -> this.users.put(k, v.clone()));
        this.albums = new HashMap<>();
        s.getAlbums().forEach((k, v) -> this.albums.put(k, v.clone()));
        this.artistReproductions = new TreeMap<>();
        s.getArtistReproductions().forEach((k, v) -> this.artistReproductions.put(k, v));
        this.genreReproductions = new TreeMap<>();
        s.getGenreReproductions().forEach((k, v) -> this.genreReproductions.put(k, v));
        this.currentUser = (s.getCurrentUser() == null ? null : s.getCurrentUser().clone());
    }

    // =================== GETTERS E SETTERS ===================

    /**
     * Retorna uma cópia do mapa de playlists públicas.
     * @return Mapa de playlists públicas
     */
    public Map<Integer, Playlist> getPublicPlaylists() {
        Map<Integer, Playlist> clone = new HashMap<>();
        clone.putAll(this.publicPlaylists);
        return clone;
    }

    /**
     * Define o mapa de playlists públicas do sistema.
     * @param playlists Mapa de playlists públicas
     */
    public void setPublicPlaylists(Map<Integer, Playlist> playlists) {
        this.publicPlaylists = new HashMap<>();
        this.publicPlaylists.putAll(playlists);
    }

    /**
     * Retorna uma cópia do mapa de utilizadores.
     * @return Mapa de utilizadores
     */
    public Map<String, User> getUsers() {
        Map<String, User> clone = new HashMap<>();
        clone.putAll(this.users);
        return clone;
    }

    /**
     * Define o mapa de utilizadores do sistema.
     * @param users Mapa de utilizadores
     */
    public void setUsers(Map<String, User> users) {
        this.users = new HashMap<>();
        this.users.putAll(users);
    }

    /**
     * Retorna uma cópia das estatísticas de reproduções por artista.
     * @return Mapa de reproduções por artista
     */
    public Map<String, Integer> getArtistReproductions() {
        Map<String, Integer> clone = new HashMap<>();
        clone.putAll(this.artistReproductions);
        return clone;
    }

    /**
     * Define as estatísticas de reproduções por artista.
     * @param artistReproductions Mapa de reproduções por artista
     */
    public void setArtistReproductions(Map<String, Integer> artistReproductions) {
        this.artistReproductions = new HashMap<>();
        this.artistReproductions.putAll(artistReproductions);
    }

    /**
     * Retorna uma cópia das estatísticas de reproduções por género.
     * @return Mapa de reproduções por género
     */
    public Map<String, Integer> getGenreReproductions() {
        Map<String, Integer> clone = new HashMap<>();
        clone.putAll(this.genreReproductions);
        return clone;
    }

    /**
     * Define as estatísticas de reproduções por género.
     * @param genreReproductions Mapa de reproduções por género
     */
    public void setGenreReproductions(Map<String, Integer> genreReproductions) {
        this.genreReproductions = new HashMap<>();
        this.genreReproductions.putAll(genreReproductions);
    }

    /**
     * Retorna uma cópia do mapa de músicas.
     * @return Mapa de músicas
     */
    public Map<String, Music> getMusics() {
        Map<String, Music> clone = new HashMap<>();
        clone.putAll(this.musics);
        return clone;
    }

    /**
     * Define o mapa de músicas do sistema.
     * @param musicas Mapa de músicas
     */
    public void setMusics(Map<String, Music> musicas) {
        this.musics = new HashMap<>();
        this.musics.putAll(musicas);
    }

    /**
     * Retorna o utilizador atualmente autenticado.
     * @return Utilizador atual
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Define o utilizador atualmente autenticado.
     * @param currentUser Utilizador a definir
     */
    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    /**
     * Retorna uma cópia do mapa de álbuns.
     * @return Mapa de álbuns
     */
    public Map<String, Album> getAlbums() {
        Map<String, Album> clone = new HashMap<>();
        clone.putAll(this.albums);
        return clone;
    }

    /**
     * Define o mapa de álbuns do sistema.
     * @param albums Mapa de álbuns
     */
    public void setAlbums(Map<String, Album> albums) {
        this.albums = new HashMap<>();
        this.albums.putAll(albums);
    }

    // =================== MÉTODOS DE AUTENTICAÇÃO E PERFIL ===================

    /**
     * Autentica um utilizador no sistema.
     * @param nome Nome do utilizador
     * @param password Password do utilizador
     * @throws UnsupportedOperationException Se a password estiver incorreta
     * @throws NotFoundException Se o utilizador não existir
     */
    public void authenticateUser(String nome, String password) throws UnsupportedOperationException, NotFoundException {
        User u = this.users.get(nome);
        if (u != null) {
            if(u.getPassword().equals(password)){
                this.currentUser = u;
            }
            else{
                throw new UnsupportedOperationException("Password incorreta.");  
            }
        }
        else{
            throw new NotFoundException(nome);
        }
    }

    /**
     * Verifica se a password do utilizador atual está correta.
     * @param password Password a verificar
     * @return true se estiver correta, false caso contrário
     */
    public boolean isPasswordCorrect(String password){
        return this.currentUser.getPassword().equals(password);
    }

    /**
     * Altera o email do utilizador atual.
     * @param email Novo email
     */
    public void setCurrentUserEmail(String email){
        this.currentUser.setEmail(email);
    }

    /**
     * Altera a password do utilizador atual.
     * @param password Nova password
     */
    public void setCurrentUserPassword(String password){
        this.currentUser.setPassword(password);
    }

    /**
     * Altera o nome de utilizador do utilizador atual.
     * @param username Novo nome de utilizador
     */
    public void setCurrentUserUsername(String username){
        this.currentUser.setUsername(username);
    }

    /**
     * Altera o nome do utilizador atual e atualiza o mapa de utilizadores.
     * @param username Novo nome de utilizador
     * @throws IllegalArgumentException Se o nome for igual ao atual ou já existir
     */
    public void changeCurrentUserName(String username) throws IllegalArgumentException{
        if (username.equals(this.currentUser.getUsername())){
            throw new IllegalArgumentException("Nome de utilizador igual ao atual.");
        }
        if (this.users.containsKey(username)){
            throw new IllegalArgumentException("Nome de utilizador já existe.");
        }
        this.users.remove(this.currentUser.getUsername());
        this.currentUser.changePlaylistAutor(username);
        this.currentUser.setUsername(username);
        this.users.put(this.currentUser.getUsername(), this.currentUser);
    }

    /**
     * Verifica se o utilizador atual tem acesso à biblioteca.
     * @return true se tiver acesso, false caso contrário
     */
    public boolean hasLibrary(){
        return this.currentUser.hasLibrary();
    }

    /**
     * Adiciona pontos ao utilizador atual, de acordo com o seu plano.
     */
    public void addPointsToCurrentUser(){
        this.currentUser.addPoints();
    }

    /**
     * Obtém o nome do plano do utilizador atual.
     * @return Nome do plano
     */
    public String getCurrentUserPlanName(){
        return this.currentUser.getPlan().getPlanName();
    }

    // =================== MÉTODOS DE UTILIZADOR ===================

    /**
     * Adiciona um novo utilizador ao sistema (plano free por defeito).
     * @param nome Nome do utilizador
     * @param email Email do utilizador
     * @param morada Morada do utilizador
     * @param password Password do utilizador
     */
    public void addNewUser(String nome, String email, String morada, String password) {
        User u = new User(nome, email, morada, password);
        this.users.put(u.getUsername(), u.clone());
    }

    /**
     * Verifica se um utilizador existe no sistema.
     * @param nome Nome do utilizador
     * @return true se o utilizador existe, false caso contrário
     */
    public boolean userExists(String nome) {
        return this.users.containsKey(nome);
    }

    // =================== MÉTODOS DE MÚSICAS ===================

    /**
     * Verifica se uma música existe no sistema.
     * @param musicName Nome da música
     * @return true se a música existe, false caso contrário
     */
    public boolean musicExists(String musicName){
        return this.musics.containsKey(musicName);
    }

    /**
     * Toca uma música, incrementando o número de reproduções.
     * @param musicName Nome da música a tocar
     * @return Letra da música
     * @throws NotFoundException Se a música não existir
     */
    public String playMusic(String musicName) throws NotFoundException{
        Music m = this.musics.get(musicName);
        if (m == null) {
            throw new NotFoundException(musicName);
        }
        return m.play();
    }

    /**
     * Obtém uma cópia da música pelo nome.
     * @param musicName Nome da música
     * @return Objeto Music clonado
     * @throws NotFoundException Se a música não existir
     */
    public Music getMusicByName(String musicName) throws NotFoundException{
        Music m = this.musics.get(musicName);
        if (m == null) {
            throw new NotFoundException(musicName);
        }
        return m.clone();
    }

    /**
     * Adiciona uma reprodução de música ao utilizador atual.
     * @param musicName Nome da música
     */
    public void addToCurrentUserMusicReproductions(String musicName){
        Music m = this.musics.get(musicName);
        this.currentUser.addMusicReproduction(m);
    }

    /**
     * Incrementa o número de reproduções de um género musical.
     * @param genre Nome do género
     */
    public void incrementGenreReproductions(String genre) {
        this.genreReproductions.put(genre, this.genreReproductions.getOrDefault(genre, 0) + 1);
    }

    /**
     * Incrementa o número de reproduções de um artista.
     * @param artistName Nome do artista
     */
    public void incrementArtistReproductions(String artistName) {
        this.artistReproductions.put(artistName, this.artistReproductions.getOrDefault(artistName, 0) + 1);
    }

    /**
     * Adiciona uma nova música ao sistema, podendo ser multimédia.
     * @param musicName Nome da música
     * @param artistName Nome do artista
     * @param publisher Editora
     * @param lyrics Letra da música
     * @param musicalFigures Figuras musicais
     * @param genre Género musical
     * @param albumName Nome do álbum
     * @param duration Duração em segundos
     * @param explicit true se for explícita, false caso contrário
     * @param url URL associado (pode ser null)
     * @throws AlreadyExistsException Se a música já existir
     */
    public void addNewMusic(String musicName, String artistName, String publisher, String lyrics, String musicalFigures, String genre, String albumName, int duration, boolean explicit, String url) throws AlreadyExistsException {
        if (this.musics.containsKey(musicName)) {
            throw new AlreadyExistsException(musicName);
        }
        if (url == null) {
            Music m = new Music(musicName, artistName, publisher, lyrics, musicalFigures, genre, albumName, duration, explicit);
            this.musics.put(musicName, m);
            Album a = this.albums.get(albumName);
            a.addMusic(m);
        } else {
            MusicMultimedia m = new MusicMultimedia(musicName, artistName, publisher, lyrics, musicalFigures, genre, albumName, duration, explicit, url);
            this.musics.put(musicName, m);
            Album a = this.albums.get(albumName);
            a.addMusic(m);
        }
    }

    /**
     * Lista todas as músicas do sistema com formatação aprimorada.
     * @return String formatada com nome e intérprete de cada música
     */
    public String listAllMusics() {
        StringBuilder sb = new StringBuilder();
        for (Music m : this.musics.values()) {
            sb.append("🎶 ").append(m.getName())
            .append(" — ").append(m.getInterpreter())
            .append("\n");
        }
        return sb.toString();
    }

    /**
     * Lista todas as músicas numa certa playlist da biblioteca do utilizador atual.
     * @param playlistId ID da playlist
     * @return String formatada com nome e intérprete de cada música
     * @throws NotFoundException Se a playlist não existir
     */
    public String listAllMusicsInPlaylist(int playlistId) throws NotFoundException{
        StringBuilder sb = new StringBuilder();
        try{
            Playlist p = this.currentUser.getPlaylistById(playlistId);
            for (Music m : p.getMusics()) {
                sb.append("🎶 ").append(m.getName())
                .append(" — ").append(m.getInterpreter())
                .append("\n");
            }
            return sb.toString();
        } catch (NotFoundException e) {
            return new NotFoundException("" + playlistId).getMessage();
        }
    }

    // =================== MÉTODOS DE ÁLBUNS ===================

    /**
     * Verifica se um álbum existe no sistema.
     * @param albumName Nome do álbum
     * @return true se o álbum existe, false caso contrário
     */
    public boolean albumExists(String albumName) {
        return this.albums.containsKey(albumName);
    }

    /**
     * Adiciona um novo álbum ao sistema.
     * @param albumName Nome do álbum
     * @param autor Nome do autor/artista
     */
    public void addNewAlbum(String albumName, String autor) {
        Album a = new Album(albumName, autor);
        this.albums.put(albumName, a);
    }

    /**
     * Obtém um álbum pelo nome.
     * @param albumName Nome do álbum
     * @return Objeto Album clonado
     * @throws NotFoundException Se o álbum não existir
     */
    public Album getAlbumByName(String albumName) throws NotFoundException{
        Album a = this.albums.get(albumName);
        if (a == null) {
            throw new NotFoundException(albumName);
        }
        return a.clone();
    }

    /**
     * Adiciona uma música a um álbum.
     * @param albumName Nome do álbum
     * @param music Objeto Music a adicionar
     * @throws NotFoundException Se o álbum não existir
     */
    public void addMusicToAlbum(String albumName, Music music) throws NotFoundException{
        Album a = this.albums.get(albumName);
        if (a == null) {
            throw new NotFoundException(albumName);
        }
        a.addMusic(music);
    }

    /**
     * Lista todos os álbuns do sistema com formatação aprimorada.
     * @return String formatada com nome e artista de cada álbum
     */
    public String listAllAlbums() {
        StringBuilder sb = new StringBuilder();
        for (Album a : this.albums.values()) {
            sb.append("📀 ").append(a.getName())
            .append(" — ").append(a.getArtist())
            .append("\n");
        }
        return sb.toString();
    }

    // =================== MÉTODOS DE PLAYLISTS ===================

    /**
     * Adiciona uma nova playlist pública ao sistema.
     * @param nome Nome da playlist
     * @param autor Autor da playlist
     */
    public void addPlaylist(String nome, String autor) {
        Playlist p = new Playlist(nome, autor);
        this.publicPlaylists.put(p.getId(), p);
    }

    /**
     * Remove uma música da playlist do utilizador atual.
     * @param musicName Nome da música
     * @param playlistId ID da playlist
     * @throws NotFoundException Se a música ou playlist não existir
     * @throws NoPremissionException Se o utilizador não tiver permissão
     */
    public void removeMusicFromPlaylist(String musicName, Integer playlistId) throws NotFoundException,NoPremissionException{
        if(!musicExists(musicName)){
            throw new NotFoundException(musicName);
        }
        this.currentUser.removeMusicFromPlaylist(this.musics.get(musicName), playlistId);
    }

    /**
     * Cria uma nova playlist de um género específico até uma duração máxima.
     * @param name Nome da playlist
     * @param genre Género musical
     * @param maxDuration Duração máxima em segundos
     * @throws AlreadyExistsException Se a playlist já existir
     * @throws EmptyPlaylistException Se não houver músicas do género
     */
    public void createGenrePlaylist(String name, String genre, int maxDuration) throws AlreadyExistsException, EmptyPlaylistException{
        List<Music> selectedMusics = PlaylistCreator.createGenrePlaylist(this.currentUser.getUsername(), name, genre, maxDuration, this.musics, this.publicPlaylists);
        Playlist genrePlaylist = new Playlist(name, this.currentUser.getUsername(), selectedMusics);
        try{
            this.currentUser.addPlaylistToLibrary(genrePlaylist);
        } catch (Exception e){
            throw new AlreadyExistsException(name);
        }
    }

    /**
     * Cria uma playlist de favoritos do utilizador atual.
     * @param maxDuration Duração máxima
     * @param explicit true se incluir explícitas
     * @return Lista de músicas favoritas
     * @throws IllegalArgumentException Se argumentos inválidos
     */
    public PlaylistFavorites createFavoritesPlaylist(int maxDuration, boolean explicit) throws IllegalArgumentException{
        List<Music> musicList;
        try {
            musicList = PlaylistCreator.createFavoritesPlaylist(maxDuration, explicit, this.currentUser.getMusicReproductions(), this.musics);
        } catch (IllegalArgumentException e) {
            // no favorites yet, return empty playlist
            musicList = new ArrayList<>();
        }
        return new PlaylistFavorites(musicList);
    }

    /**
     * Obtém uma playlist aleatória do sistema.
     * @return Objeto Playlist aleatória
     */
    public PlaylistRandom getRandomPlaylist(){
        if (this.musics.isEmpty()) {
            return new PlaylistRandom("Playlist Aleatória com 0 músicas.", new ArrayList<>());
        }
        List<Music> randomMusics = PlaylistCreator.createRandomPlaylist(this.musics);
        PlaylistRandom randomPlaylist = new PlaylistRandom("Playlist Aleatória com " + randomMusics.size() + " músicas.", randomMusics);
        return randomPlaylist;
    }

    /**
     * Torna uma playlist do utilizador atual pública.
     * @param playlistId ID da playlist
     * @throws NotFoundException Se a playlist não existir
     * @throws AlreadyExistsException Se a playlist já for pública
     */
    public void setPlaylistAsPublic(int playlistId) throws NotFoundException,AlreadyExistsException{
        Playlist p = this.currentUser.getPlaylistById(playlistId);
        if (p == null) {
            throw new NotFoundException("" + playlistId);
        }
        if (!this.publicPlaylists.containsKey(playlistId)){
            this.publicPlaylists.put(playlistId, p);
        }
        else{
            throw new AlreadyExistsException("" + playlistId);
        }
    }

    /**
     * Obtém uma playlist do utilizador atual pelo ID.
     * @param playlistId ID da playlist
     * @return Objeto Playlist clonado
     */
    public Playlist getUserPlaylistById(int playlistId){
        try {
            Playlist p = this.currentUser.getPlaylistById(playlistId);
            return p.clone();
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Obtém uma playlist pública pelo ID.
     * @param playlistId ID da playlist
     * @return Objeto Playlist clonado
     */
    public Playlist getPublicPlaylistById(int playlistId){
        Playlist p = this.publicPlaylists.get(playlistId);
        return p.clone();
    }

    /**
     * Adiciona uma nova playlist à biblioteca do utilizador atual.
     * @param playlistName Nome da playlist
     */
    public void addToCurrentUserPlaylist(String playlistName){
        this.currentUser.addPlaylist(playlistName);
    }

    /**
     * Adiciona uma música à playlist do utilizador atual.
     * @param playlistId ID da playlist
     * @param musicName Nome da música
     * @throws NotFoundException Se a música ou playlist não existir
     * @throws AlreadyExistsException Se a música já estiver na playlist
     * @throws NoPremissionException Se o utilizador não tiver permissão
     */
    public void addMusicToCurrentUserPlaylist(int playlistId, String musicName) throws NotFoundException, AlreadyExistsException, NoPremissionException {
        if (!this.musics.containsKey(musicName)) {
            throw new NotFoundException(musicName);
        }
        this.currentUser.addMusicPlaylist(playlistId, this.musics.get(musicName));
    }

    /**
     * Adiciona uma playlist pública à biblioteca do utilizador atual.
     * @param playlistId ID da playlist
     * @throws NotFoundException Se a playlist não existir
     * @throws AlreadyExistsException Se a playlist já existir na biblioteca
     */
    public void addPlaylistToCurrentUserLibrary(int playlistId) throws NotFoundException, AlreadyExistsException{
        Playlist p = this.publicPlaylists.get(playlistId);
        if (p == null) {
            throw new NotFoundException("" + playlistId);
        }
        try{
            this.currentUser.addPlaylistToLibrary(p);
        } catch (Exception e){
            throw new AlreadyExistsException("" + playlistId);
        }
    }

    /**
     * Lista todas as playlists do utilizador atual.
     * @return String com os nomes das playlists
     */
    public String listCurrentUserPlaylists() {
        return this.getCurrentUser().namePlaylists();
    }

    /**
     * Lista todas as playlists públicas do sistema.
     * @return String com os nomes das playlists públicas
     */
    public String listPublicPlaylists(){
        StringBuilder sb = new StringBuilder();
        for (Playlist p : this.publicPlaylists.values()) {
            sb.append(String.format("🎵 [#%03d] %-25s | Autor: %-15s\n", p.getId(), p.getName(), p.getAutor()));
        }
        return sb.toString();
    }

    /**
     * Obtém o número de playlists públicas.
     * @return Número de playlists públicas
     */
    public int getPublicPlaylistSize(){
        return this.publicPlaylists.size();
    }

    // =================== MÉTODOS DE GÉNEROS ===================

    /**
     * Lista todos os géneros musicais únicos presentes no sistema.
     * @return String formatada com a lista de géneros
     */
    public String listAllGenres() {
        Set<String> genres = new TreeSet<>();
        for (Music m : this.musics.values()) {
            genres.add(m.getGenre());
        }
        StringBuilder sb = new StringBuilder();
        for (String g : genres) {
            sb.append("• ").append(g).append("\n");
        }
        return sb.toString();
    }

    // =================== MÉTODOS DE ESTATÍSTICAS ===================

    /**
     * Obtém a música mais reproduzida do sistema.
     * @return Objeto Music mais reproduzido
     * @throws NoMusicsInDatabaseException Se não existirem músicas
     */
    public Music mostReproducedMusic() throws NoMusicsInDatabaseException{
        if (this.musics.isEmpty()){
            throw new NoMusicsInDatabaseException("Não existem músicas na base de dados.");
        }
        return this.musics.values().stream()
                .max((m1, m2) -> Integer.compare(m1.getReproductions(), m2.getReproductions()))
                .orElse(null).clone();
    }

    /**
     * Obtém os artistas mais tocados em ordem decrescente.
     * @return String com artistas e número de reproduções
     * @throws NoArtistsInDatabaseException Se não existirem artistas
     */
    public String getTopArtistName() throws NoArtistsInDatabaseException {
        if (this.artistReproductions.isEmpty()){
            throw new NoArtistsInDatabaseException("Não existem artistas na base de dados.");
        }
        List<Map.Entry<String, Integer>> sortedArtists = new ArrayList<>(this.artistReproductions.entrySet());
        sortedArtists.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> entry : sortedArtists) {
            sb.append(entry.getKey()).append(" - ").append(entry.getValue()).append("\n");
        }
        return sb.toString();
    }

    /**
     * Obtém o utilizador com mais pontos.
     * @return Objeto User com mais pontos
     * @throws NoUsersInDatabaseException Se não existirem utilizadores
     */
    public User getUserWithMostPoints() throws NoUsersInDatabaseException{
        if (this.users.isEmpty()){
            throw new NoUsersInDatabaseException("Não existem utilizadores na base de dados.");
        }
        return this.users.values().stream()
                .max((u1, u2) -> Integer.compare(u1.getPlan().getPoints(), u2.getPlan().getPoints()))
                .orElse(null).clone();
    }

    /**
     * Obtém o género musical com mais reproduções.
     * @return Nome do género
     * @throws NoReproductionsInDatabaseException Se não existirem reproduções
     */
    public String getGenreWithMostReproductions() throws NoReproductionsInDatabaseException {
        if (this.genreReproductions.isEmpty()){
            throw new NoReproductionsInDatabaseException();
        }
        return this.genreReproductions.entrySet().stream()
                .max((g1, g2) -> Integer.compare(g1.getValue(), g2.getValue()))
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    /**
     * Obtém o utilizador com mais playlists.
     * @return Objeto User com mais playlists
     * @throws NoUsersInDatabaseException Se não existirem utilizadores
     */
    public User getUserWithMostPlaylists() throws NoUsersInDatabaseException {
        if (this.users.isEmpty()){
            throw new NoUsersInDatabaseException("Não existem utilizadores na base de dados.");
        }
        return this.users.values().stream()
                .max((u1, u2) -> Integer.compare(u1.getUserPlaylistCount(), u2.getUserPlaylistCount()))
                .orElse(null).clone();
    }

    /**
     * Obtém o utilizador com mais reproduções num intervalo de datas.
     * @param startDate Data de início
     * @param endDate Data de fim
     * @return Objeto User com mais reproduções
     * @throws NoUsersInDatabaseException Se não existirem utilizadores
     */
    public User getUserWithMostReproductions(LocalDate startDate, LocalDate endDate) throws NoUsersInDatabaseException{
        if (this.users.isEmpty()) {
            throw new NoUsersInDatabaseException("Não existem utilizadores na base de dados.");
        }
        // find user with max reproductions in range
        User top = this.users.values().stream()
                .max((u1, u2) -> Integer.compare(
                        u1.getMusicReproductionsCount(startDate, endDate),
                        u2.getMusicReproductionsCount(startDate, endDate)))
                .orElse(null);
        if (top == null || top.getMusicReproductionsCount(startDate, endDate) == 0) {
            throw new NoUsersInDatabaseException("Não existem utilizadores com reproduções no intervalo.");
        }
        return top.clone();
    }

    /**
     * Obtém o utilizador com mais reproduções no geral.
     * @return Objeto User com mais reproduções
     * @throws NoUsersInDatabaseException Se não existirem utilizadores
     */
    public User getUserWithMostReproductions() throws NoUsersInDatabaseException{
        if (this.users.isEmpty()) {
            throw new NoUsersInDatabaseException("Não existem utilizadores na base de dados.");
        }
        return this.users.values().stream()
                .max((u1, u2) -> Integer.compare(u1.getMusicReproductions().size(), 
                                                u2.getMusicReproductions().size()))
                .map(User::clone)
                .orElse(null);
    }

    // =================== MÉTODOS DE PLANO E PERMISSÕES ===================

    /**
     * Verifica se o utilizador atual pode fazer skip de música.
     * @return true se puder, false caso contrário
     */
    public boolean canCurrentUserSkip(){
        return this.currentUser.getPlan().canSkip();
    }

    /**
     * Verifica se o utilizador atual pode escolher o que ouvir.
     * @return true se puder, false caso contrário
     */
    public boolean canCurrentUserChooseWhatToPlay(){
        return this.currentUser.getPlan().canChooseWhatToPlay();
    }

    /**
     * Verifica se o utilizador atual tem acesso a músicas favoritas.
     * @return true se tiver, false caso contrário
     */
    public boolean currentUserAccessToFavorites(){
        return this.currentUser.getPlan().hasAccessToFavorites();
    }

    // =================== MÉTODOS AUXILIARES E OVERRIDES ===================

    /**
     * Compara se dois objetos SpotifUM são iguais.
     * @param o Objeto a comparar
     * @return true se forem iguais, false caso contrário
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SpotifUM)) return false;
        SpotifUM spotifUM = (SpotifUM) o;
        boolean usersEqual = this.users.equals(spotifUM.getUsers());
        boolean currentUserEqual;
        if (this.currentUser == null && spotifUM.getCurrentUser() == null) {
            currentUserEqual = true;
        } else if (this.currentUser != null) {
            currentUserEqual = this.currentUser.equals(spotifUM.getCurrentUser());
        } else {
            currentUserEqual = false;
        }
        return this.musics.equals(spotifUM.getMusics()) &&
                this.publicPlaylists.equals(spotifUM.getPublicPlaylists()) &&
                usersEqual &&
                this.albums.equals(spotifUM.getAlbums()) &&
                currentUserEqual;
    }

    /**
     * Retorna uma representação textual simplificada do objeto.
     * @return String representando o objeto
     */
    public String toString(){
        return "SpotifUM(...)";
    }

    // =================== POPULAÇÃO DE BASE DE DADOS ===================

    /**
     * Preenche a base de dados com dados de exemplo (músicas, álbuns, playlists, utilizadores).
     */
    public void populateDatabase() {
        // ================================== MÚSICAS ==================================
        
        // Pop
        Music shapeOfYou = createMusic("Shape of You", "Ed Sheeran", "Atlantic", 
            "The club isn't the best place to find a lover\n" +
            "So the bar is where I go\n" +
            "Me and my friends at the table doing shots", 
            "Guitar", "Pop", "Divide", 240, false);
            
        Music blindingLights = createMusic("Blinding Lights", "The Weeknd", "XO", 
            "Yeah\n" +
            "I've been tryna call\n" +
            "I've been on my own for long enough", 
            "Synth", "Pop", "After Hours", 200, false);
            
        Music levitating = createMusic("Levitating", "Dua Lipa", "Warner", 
            "If you wanna run away with me, I know a galaxy\n" +
            "And I can take you for a ride\n" +
            "I had a premonition that we fell into a rhythm", 
            "Synth", "Pop", "Future Nostalgia", 203, false);
            
        Music watermelonSugar = createMusic("Watermelon Sugar", "Harry Styles", "Columbia", 
            "Tastes like strawberries on a summer evening\n" +
            "And it sounds just like a song\n" +
            "I want more berries and that summer feeling", 
            "Guitar", "Pop", "Fine Line", 174, false);
            
        Music dontStartNow = createMusic("Don't Start Now", "Dua Lipa", "Warner", 
            "If you don't wanna see me\n" +
            "Did a full 180, crazy\n" +
            "Thinking 'bout the way I was", 
            "Bass", "Pop", "Future Nostalgia", 183, false);
    
        // Rock
        Music bohemian = createMusic("Bohemian Rhapsody", "Queen", "EMI", 
            "Is this the real life? Is this just fantasy?\n" +
            "Caught in a landslide, no escape from reality\n" +
            "Open your eyes, look up to the skies and see", 
            "Piano", "Rock", "A Night at the Opera", 355, false);
            
        Music stairway = createMusic("Stairway to Heaven", "Led Zeppelin", "Atlantic", 
            "There's a lady who's sure all that glitters is gold\n" +
            "And she's buying a stairway to heaven\n" +
            "When she gets there she knows", 
            "Guitar", "Rock", "Led Zeppelin IV", 482, false);
            
        Music sweetChildOMine = createMusic("Sweet Child O' Mine", "Guns N' Roses", "Geffen", 
            "She's got a smile that it seems to me\n" +
            "Reminds me of childhood memories\n" +
            "Where everything was as fresh as the bright blue sky", 
            "Guitar", "Rock", "Appetite for Destruction", 356, false);
            
        Music hotelCalifornia = createMusic("Hotel California", "Eagles", "Asylum", 
            "On a dark desert highway, cool wind in my hair\n" +
            "Warm smell of colitas, rising up through the air\n" +
            "Up ahead in the distance, I saw a shimmering light", 
            "Guitar", "Rock", "Hotel California", 390, false);
            
        Music smellsLikeTeenSpirit = createMusic("Smells Like Teen Spirit", "Nirvana", "DGC", 
            "Load up on guns, bring your friends\n" +
            "It's fun to lose and to pretend\n" +
            "She's over bored and self assured", 
            "Guitar", "Rock", "Nevermind", 301, true);
    
        // Hip-Hop
        Music sickoMode = createMusic("SICKO MODE", "Travis Scott", "Cactus Jack", 
            "Astro'\n" +
            "Yeah\n" +
            "Sun is down, freezing cold", 
            "Bass", "Hip-Hop", "ASTROWORLD", 312, true);
            
        Music godPlan = createMusic("God's Plan", "Drake", "OVO", 
            "Yeah, they wishin' and wishin' and wishin' and wishin'\n" +
            "They wishin' on me, yuh\n" +
            "I been movin' calm, don't start no trouble", 
            "Drums", "Hip-Hop", "Scorpion", 198, false);
            
        Music humble = createMusic("HUMBLE.", "Kendrick Lamar", "Top Dawg", 
            "Nobody pray for me\n" +
            "It's been that day for me\n" +
            "Way (yeah) yeah", 
            "Bass", "Hip-Hop", "DAMN.", 177, true);
            
        Music rapGod = createMusic("Rap God", "Eminem", "Aftermath", 
            "Look, I was gonna go easy on you not to hurt your feelings\n" +
            "But I'm only going to get this one chance\n" +
            "Something's wrong, I can feel it", 
            "Vocals", "Hip-Hop", "The Marshall Mathers LP 2", 363, true);
    
        // Eletrônica
        Music starboy = createMusic("Starboy", "The Weeknd", "Republic", 
            "I'm tryna put you in the worst mood, ah\n" +
            "P1 cleaner than your church shoes, ah\n" +
            "Milli point two just to hurt you, ah", 
            "Synth", "Electronic", "Starboy", 230, false);
            
        Music oneMoreTime = createMusic("One More Time", "Daft Punk", "Virgin", 
            "One more time\n" +
            "We're gonna celebrate\n" +
            "Oh yeah, all right", 
            "Vocoder", "Electronic", "Discovery", 320, false);
            
        Music titanium = createMusic("Titanium", "David Guetta", "EMI", 
            "You shout it out\n" +
            "But I can't hear a word you say\n" +
            "I'm talking loud not saying much", 
            "Synth", "Electronic", "Nothing but the Beat", 245, false);
    
        // Clássicos
        Music billieJean = createMusic("Billie Jean", "Michael Jackson", "Epic", 
            "She was more like a beauty queen from a movie scene\n" +
            "I said don't mind, but what do you mean\n" +
            "I am the one who will dance on the floor", 
            "Bass", "Pop", "Thriller", 294, false);
            
        Music beatIt = createMusic("Beat It", "Michael Jackson", "Epic", 
            "They told him don't you ever come around here\n" +
            "Don't wanna see your face, you better disappear\n" +
            "The fire's in their eyes and their words are really clear", 
            "Guitar", "Pop", "Thriller", 258, false);
            
        Music hinoPorto = createMusic("Hino do Futebol Clube do Porto", "Maria Amélia Canossa", "FC Porto", 
            "Hino do Futebol Clube do Porto\n" +
            "Cantado com orgulho e amor\n" +
            "Por todos os portistas no mundo", 
            "Voz", "Hino", "Tanto Porto!", 120, false);

        MusicMultimedia neverGonnaGiveYouUp = new MusicMultimedia("Never Gonna Give You Up", "Rick Astley", "RCA", 
            "We're no strangers to love\n" +
            "You know the rules and so do I\n" +
            "A full commitment's what I'm thinking of", 
            "Synth", "Pop", "Rick Roll", 215, false, "https://www.youtube.com/watch?v=dQw4w9WgXcQ");
    
        // Adiciona todas as músicas ao mapa
        Arrays.asList(shapeOfYou, blindingLights, levitating, watermelonSugar, dontStartNow,
                     bohemian, stairway, sweetChildOMine, hotelCalifornia, smellsLikeTeenSpirit,
                     sickoMode, godPlan, humble, rapGod, starboy, oneMoreTime, titanium,
                     billieJean, beatIt, hinoPorto,neverGonnaGiveYouUp).forEach(m -> musics.put(m.getName(), m));
    
        // ================================== ÁLBUNS ==================================
        Album divide = new Album("Divide", "Ed Sheeran", List.of(shapeOfYou));
        Album afterHours = new Album("After Hours", "The Weeknd", List.of(blindingLights));
        Album futureNostalgia = new Album("Future Nostalgia", "Dua Lipa", List.of(levitating, dontStartNow));
        Album fineLine = new Album("Fine Line", "Harry Styles", List.of(watermelonSugar));
        Album nightAtTheOpera = new Album("A Night at the Opera", "Queen", List.of(bohemian));
        Album ledZeppelinIV = new Album("Led Zeppelin IV", "Led Zeppelin", List.of(stairway));
        Album appetiteForDestruction = new Album("Appetite for Destruction", "Guns N' Roses", List.of(sweetChildOMine));
        Album hotelCaliforniaAlbum = new Album("Hotel California", "Eagles", List.of(hotelCalifornia));
        Album nevermind = new Album("Nevermind", "Nirvana", List.of(smellsLikeTeenSpirit));
        Album astroworld = new Album("ASTROWORLD", "Travis Scott", List.of(sickoMode));
        Album scorpion = new Album("Scorpion", "Drake", List.of(godPlan));
        Album damn = new Album("DAMN.", "Kendrick Lamar", List.of(humble));
        Album marshallMathersLP2 = new Album("The Marshall Mathers LP 2", "Eminem", List.of(rapGod));
        Album starboyAlbum = new Album("Starboy", "The Weeknd", List.of(starboy));
        Album discovery = new Album("Discovery", "Daft Punk", List.of(oneMoreTime));
        Album nothingButTheBeat = new Album("Nothing but the Beat", "David Guetta", List.of(titanium));
        Album thriller = new Album("Thriller", "Michael Jackson", List.of(billieJean, beatIt));
        Album tantoPorto = new Album("Tanto Porto!", "FC Porto", List.of(hinoPorto));
        Album rickRoll = new Album("Rick Roll", "Rick Astley", List.of(neverGonnaGiveYouUp));

        // Adiciona álbuns ao map
        Arrays.asList(divide, afterHours, futureNostalgia, fineLine, nightAtTheOpera,
                     ledZeppelinIV, appetiteForDestruction, hotelCaliforniaAlbum, nevermind,
                     astroworld, scorpion, damn, marshallMathersLP2, starboyAlbum, discovery,
                     nothingButTheBeat, thriller, tantoPorto,rickRoll).forEach(a -> albums.put(a.getName(), a));
    
        // ================================== PLAYLISTS PÚBLICAS ==================================
        Playlist topGlobal = new Playlist("Top Global", "Spotify", 
            List.of(shapeOfYou, blindingLights, levitating, godPlan, starboy));
            
        Playlist rockLegends = new Playlist("Rock Legends", "RockFM", 
            List.of(bohemian, stairway, sweetChildOMine, hotelCalifornia, smellsLikeTeenSpirit));
            
        Playlist hipHopNation = new Playlist("Hip-Hop Nation", "HipHopTV", 
            List.of(sickoMode, godPlan, humble, rapGod));
            
        Playlist electronicVibes = new Playlist("Electronic Vibes", "EDM.com", 
            List.of(starboy, oneMoreTime, titanium));
            
        Playlist throwbackHits = new Playlist("Throwback Hits", "OldiesFM", 
            List.of(billieJean, beatIt, hotelCalifornia, bohemian));
            
        Playlist portuguesePride = new Playlist("Portuguese Pride", "RTP", 
            List.of(hinoPorto));
    
        // Adiciona playlists públicas
        Arrays.asList(topGlobal, rockLegends, hipHopNation, electronicVibes, throwbackHits, portuguesePride)
              .forEach(p -> publicPlaylists.put(p.getId(), p));
    
        // ================================== UTILIZADORES ==================================
        // (Mantido igual ao original)
        User premiumUser = new User("simao", "love@music.com", "Avenida Central", 
            "root", new PlanPremiumBase(), Arrays.asList(
                new Playlist("My Mix", "simao", List.of(sickoMode, bohemian)),
                new Playlist("Tesouros Portugueses", "simao", List.of(hinoPorto))
            ), new ArrayList<>());
    
        User freeUser = new User("gabriel", "student@uni.com", "Campus Residence", 
            "root", new PlanFree(), Arrays.asList(
                new Playlist("Study Time", "gabriel", List.of(bohemian, stairway))
            ), new ArrayList<>());
    
        User artistAccount = new User("jose", "weeknd@xo.com", "Los Angeles", 
            "root", new PlanPremiumTop(), new ArrayList<>(), new ArrayList<>());
    
        // Adiciona usuários
        Arrays.asList(premiumUser, freeUser, artistAccount)
              .forEach(u -> users.put(u.getUsername(), u));
    }

    /**
     * Cria uma nova música com os parâmetros fornecidos.
     * @param name Nome da música
     * @param artist Nome do artista
     * @param label Editora
     * @param lyrics Letra da música
     * @param instrument Instrumento principal
     * @param genre Género musical
     * @param album Nome do álbum
     * @param duration Duração em segundos
     * @param explicit true se for explícita, false caso contrário
     * @return Objeto Music criado
     */
    private Music createMusic(String name, String artist, String label, String lyrics, 
                             String instrument, String genre, String album, int duration, boolean explicit) {
        return new Music(name, artist, label, lyrics, instrument, genre, album, duration, explicit);
    }
}

